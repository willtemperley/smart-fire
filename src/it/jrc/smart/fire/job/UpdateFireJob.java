package it.jrc.smart.fire.job;

import it.jrc.smart.fire.ConservationAreaDAO;
import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.model.ActiveFire;
import it.jrc.smart.fire.model.SmartModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.CategoryAttribute;
import org.wcs.smart.ca.datamodel.DataModel;
import org.wcs.smart.hibernate.HibernateManager;
import org.wcs.smart.observation.model.Waypoint;
import org.wcs.smart.observation.model.WaypointObservation;
import org.wcs.smart.observation.model.WaypointObservationAttribute;

import com.vividsolutions.jts.geom.Envelope;

public abstract class UpdateFireJob extends Job {

	private ConservationArea ca;
	private Session session;
	private Date mostRecent;

	public UpdateFireJob(ConservationArea ca, Date fromDate, Session session) {
		super("Fire update");
		this.ca = ca;
		this.session = session;
		this.mostRecent = fromDate;
	}

	protected String doUpdate(final IProgressMonitor monitor) {

		List<String> messages = new ArrayList<String>();
		Integer nFires = 0;

		// Monitor
		monitor.beginTask("Update active fire archive", 100);

		try {
			ConservationAreaDAO caDao = new ConservationAreaDAO(ca);
			messages.add("CA: " +  ca.getId());

			if (mostRecent == null) {
				// Set to 1970
				mostRecent = new Date(0);
			}

			messages.add("Date: " +  mostRecent.toString());

			/*
			 * Access data model
			 */
			Category activeFireCat = null;
			DataModel dm = HibernateManager.loadDataModel(ca, session);
			List<Category> cats = dm.getCategories();
			for (Category category : cats) {
				if (category.getKeyId().equals(SmartModel.activefire)) {
					activeFireCat = category;
				}
			}

			if (activeFireCat == null) {
				return "Data model not configured: can't find activefire category.";
			}
			monitor.worked(50);

			/*
			 * Get fires from service
			 */
			Area area = caDao.getArea(session, AreaType.BA);

			if (area == null) {
				return "Cannot find buffered management area.";
			}

			Envelope envelopeInternal = area.getGeometry()
					.getEnvelopeInternal();
			if (envelopeInternal == null) {
				return "Can't find the extent of the BA.";
			}

			List<ActiveFire> fires = FireDAO.getFires(envelopeInternal,
					mostRecent);


			try {
				
				if (session == null) {
					messages.add("Session is null!");
				}

				session.getTransaction().begin();

				for (ActiveFire activeFire : fires) {

					Waypoint waypoint = new Waypoint();
					Date stamp = activeFire.getStamp();
					waypoint.setDateTime(stamp);
					waypoint.setConservationArea(ca);
					waypoint.setX(activeFire.getX());
					waypoint.setY(activeFire.getY());
					waypoint.setSourceId("MODIS-" + activeFire.getVersion());
					session.saveOrUpdate(waypoint);
					nFires++;

					/*
					 * Set up the WP observation, one per fire
					 */
					List<WaypointObservationAttribute> attributes = new ArrayList<WaypointObservationAttribute>();
					WaypointObservation wo = new WaypointObservation();

					/*
					 * Create wp obs att for each attribute
					 */
					List<CategoryAttribute> fireAtts = activeFireCat
							.getAttributes();

					for (CategoryAttribute categoryAttribute : fireAtts) {

						WaypointObservationAttribute waypointObsAttr = new WaypointObservationAttribute();
						if (categoryAttribute.getAttribute() == null) {
							messages.add("Null category attribute!");
						}
						waypointObsAttr.setAttribute(categoryAttribute.getAttribute());

						String keyID = categoryAttribute.getAttribute()
								.getKeyId();
						if (keyID.equals(SmartModel.frp)) {
							waypointObsAttr.setNumberValue(activeFire.getFrp());
						} else if (keyID.equals(SmartModel.confidence)) {
							waypointObsAttr.setNumberValue(Double
									.valueOf(activeFire.getConfidence()));
						}

						waypointObsAttr.setObservation(wo);
						attributes.add(waypointObsAttr);

					}

					/*
					 * A waypoint obs needs atts and a waypoint and a category?
					 */
					wo.setWaypoint(waypoint);
					wo.setCategory(activeFireCat);
					wo.setAttributes(attributes);

					session.saveOrUpdate(wo);

				}
				session.getTransaction().commit();

			} catch (Exception e) {
				e.printStackTrace();
				session.getTransaction().rollback();
				nFires = null;
			}
			monitor.worked(100);

			return "Fire archive updated. " + nFires
					+ " active fire pixels imported.";
		} catch (Exception e) {
			
			messages.add("nFires worked: " + nFires);
			return join(messages, ";");

		}

	}
	
	public static String join(Collection<?> col, String delim) {
	    StringBuilder sb = new StringBuilder();
	    Iterator<?> iter = col.iterator();
	    if (iter.hasNext())
	        sb.append(iter.next().toString());
	    while (iter.hasNext()) {
	        sb.append(delim);
	        sb.append(iter.next().toString());
	    }
	    return sb.toString();
	}
}
