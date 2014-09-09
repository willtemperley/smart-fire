package it.jrc.smart.fire.job;

import it.jrc.smart.fire.ConservationAreaDAO;
import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.model.ActiveFire;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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


//	@Override
//	protected IStatus run(final IProgressMonitor monitor) {
//
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				
//				doUpdate(monitor);
//
//			}
//		});
//
//		return Status.OK_STATUS;
//	}

	protected Integer doUpdate(final IProgressMonitor monitor) {

		//Monitor
		monitor.beginTask("Update active fire archive", 100);


		ConservationAreaDAO caDao = new ConservationAreaDAO(ca);
		
		if (mostRecent == null) {
			try {
				mostRecent = FireDAO.df.parse("2014-01-01 00:00:00");
			} catch (ParseException e) {
				e.printStackTrace();
				monitor.setCanceled(true);
				return null;
			}
		}

		Area area = caDao.getArea(session, AreaType.BA);
		List<ActiveFire> fires = FireDAO.getRecentFires(area.getGeometry().getEnvelopeInternal(), mostRecent);
		
//		System.out.println("N RETREIVED: " + fires.size());

		monitor.worked(50);

		/*
		 * Access data model
		 */
		Category activeFireCat = null;
		DataModel dm = HibernateManager.loadDataModel(ca, session);
		List<Category> cats = dm.getCategories();
		for (Category category : cats) {
			if (category.getDefaultName().equals("ActiveFire")) {
				activeFireCat = category;
			}
//			List<CategoryAttribute> atts = category.getAttributes();
//			System.out.println(category.getDefaultName());
//			for (CategoryAttribute categoryAttribute : atts) {
//				System.out.print("--->");
//				Attribute attribute = categoryAttribute.getAttribute();
//				System.out.println(attribute.getDefaultName());
//			}
		}

		if (activeFireCat == null) {
			return null;
		}

		Integer nFires = 0;

		try {

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
					waypointObsAttr.setAttribute(categoryAttribute.getAttribute());

					String keyID = categoryAttribute.getAttribute().getKeyId();
					if (keyID.equals("activefire.frp")) {
						waypointObsAttr.setNumberValue(activeFire.getFrp());
					} else if (keyID.equals("activefire.confidence")) {
						waypointObsAttr.setNumberValue(Double.valueOf(activeFire.getConfidence()));
					}

					waypointObsAttr.setObservation(wo);
					attributes.add(waypointObsAttr);

				}

				/*
				 * A waypoint obs needs atts and a waypoint and a
				 * category?
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
		return nFires;
	}

}
