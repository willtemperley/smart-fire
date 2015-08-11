package it.jrc.smart.fire.job;

import it.jrc.smart.fire.ConservationAreaDAO;
import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.model.BurnedArea;
import it.jrc.smart.fire.model.SmartModel;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.DataModel;
import org.wcs.smart.hibernate.HibernateManager;
import org.wcs.smart.observation.model.Waypoint;
import org.wcs.smart.observation.model.WaypointObservation;

public abstract class UpdateBAJob extends Job {

	private ConservationArea ca;
	private Session session;
	private Date mostRecent;

	public UpdateBAJob(ConservationArea ca, Date fromDate, Session session) {
		super(Messages.BURNED_AREA_UPDATE);
		this.ca = ca;
		this.session = session;
		this.mostRecent = fromDate;
	}

	protected String doUpdate(final IProgressMonitor monitor) {

		//Monitor
		monitor.beginTask(Messages.UPDATE_BURNED_AREA_ARCHIVE, 100);

		ConservationAreaDAO caDao = new ConservationAreaDAO(ca);
		
		if (mostRecent == null) {
			//Set to 1970
			mostRecent = new Date(0);
		}

		/*
		 * Access data model
		 */
		Category BurnedAreaCat = null;
		DataModel dm = HibernateManager.loadDataModel(ca, session);
		List<Category> cats = dm.getCategories();
		for (Category category : cats) {
			if (category.getKeyId().equals(SmartModel.burnedarea)) {
				BurnedAreaCat = category;
			}
		}

		if (BurnedAreaCat == null) {
			return String.format(Messages.DATA_MODEL_NOT_CONFIGURED_CATEGORY, "BurnedArea");
		}
		monitor.worked(50);

		/*
		 * Get fires from service
		 */
		Area area = caDao.getArea(session, AreaType.BA);
		List<BurnedArea> fires = FireDAO.getBurnedAreas(area.getGeometry().getEnvelopeInternal(), mostRecent);

		Integer nFires = 0;

		try {

			session.getTransaction().begin();

			for (BurnedArea BurnedArea : fires) {

				Waypoint waypoint = new Waypoint();
				Date stamp = BurnedArea.getStamp();
				waypoint.setDateTime(stamp);
				waypoint.setConservationArea(ca);
				waypoint.setX(BurnedArea.getX());
				waypoint.setY(BurnedArea.getY());
				waypoint.setSourceId(Messages.MODIS_BA);
				session.saveOrUpdate(waypoint);
				nFires++;

				/*
				 * Set up the WP observation, one per fire
				 */
				WaypointObservation wo = new WaypointObservation();
				

				/*
				 * A waypoint obs needs atts and a waypoint and a
				 * category?
				 */
				wo.setWaypoint(waypoint);
				wo.setCategory(BurnedAreaCat);

				session.saveOrUpdate(wo);

			}
			session.getTransaction().commit();


		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			nFires = null;
		}
		monitor.worked(100);

		return String.format(Messages.FIRE_ARCHIVE_UPDATED_FIRE_RECORDS_IMPORTED, nFires);
	}

}
