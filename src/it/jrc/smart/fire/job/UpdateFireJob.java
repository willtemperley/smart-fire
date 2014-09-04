package it.jrc.smart.fire.job;

import it.jrc.smart.fire.ConservationAreaDAO;
import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.model.ActiveFire;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.datamodel.Attribute;
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.CategoryAttribute;
import org.wcs.smart.ca.datamodel.DataModel;
import org.wcs.smart.hibernate.HibernateManager;
import org.wcs.smart.observation.model.Waypoint;
import org.wcs.smart.observation.model.WaypointObservation;
import org.wcs.smart.observation.model.WaypointObservationAttribute;

public class UpdateFireJob extends Job {

	private ConservationArea ca;
	private Session session;

	public UpdateFireJob(ConservationArea ca, Session session) {
		super("Fire update");
		this.ca = ca;
		this.session = session;
	}

//	public void debug() {
//		Collection<IWaypointSource> z = WaypointSourceEngine.getInstance()
//				.getSupportedSources();
//		for (IWaypointSource ws : z) {
//			System.out.println(ws.getName());
//			System.out.println(ws.getKey());
//		}
//	}
	

	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				
				doUpdate(monitor);

			}
		});

		return Status.OK_STATUS;
	}

	protected void doUpdate(final IProgressMonitor monitor) {

		//Monitor
		monitor.beginTask("Update fires", 100);


		Query q = session.createQuery("select max(dateTime) from Waypoint where source = 'MODIS'");
		Date mostRecent  = (Date) q.uniqueResult();
		ConservationAreaDAO caDao = new ConservationAreaDAO(ca);
		
		if (mostRecent == null) {
			try {
				mostRecent = FireDAO.df.parse("2000-01-01 00:00:00");
			} catch (ParseException e) {
				e.printStackTrace();
				monitor.setCanceled(true);
				return;
			}
		}

		Area area = caDao.getArea(session, AreaType.BA);
		List<ActiveFire> fires = FireDAO.getRecentFires(area.getGeometry().getEnvelopeInternal(), mostRecent);

		monitor.worked(50);

		/*
		 * Access data model
		 */
		Category activeFireCat = null;
		DataModel dm = HibernateManager.loadDataModel(ca, session);
		List<Category> cats = dm.getCategories();
		for (Category category : cats) {
			List<CategoryAttribute> atts = category.getAttributes();
			System.out.println(category.getName());
			if (category.getName().equals("ActiveFire")) {
				activeFireCat = category;
			}
			for (CategoryAttribute categoryAttribute : atts) {
				System.out.print("--->");
				Attribute attribute = categoryAttribute.getAttribute();
				System.out.println(attribute.getName());
			}
		}

		if (activeFireCat == null) {
			return;
		}

		System.out.println("found: " + activeFireCat);

		try {

			session.getTransaction().begin();

			/*
			 * 
			 */
			for (ActiveFire activeFire : fires) {

				Waypoint waypoint = new Waypoint();
				Date stamp = activeFire.getStamp();
				System.out.println("stamp: " + stamp);
				waypoint.setDateTime(stamp);
				waypoint.setConservationArea(ca);
				waypoint.setX(activeFire.getX());
				waypoint.setY(activeFire.getY());
				waypoint.setSourceId("MODIS-" + activeFire.getVersion());
				session.saveOrUpdate(waypoint);

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

					WaypointObservationAttribute attr = new WaypointObservationAttribute();
					attr.setAttribute(categoryAttribute.getAttribute());

					String name = categoryAttribute.getAttribute().getName();
					if (name.equals("frp")) {
						attr.setNumberValue(activeFire.getFrp());
					} else if (name.equals("confidence")) {
						attr.setNumberValue(Double.valueOf(activeFire.getConfidence()));
					}

					attr.setObservation(wo);
					attributes.add(attr);

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
		}
		monitor.worked(100);
	}

}
