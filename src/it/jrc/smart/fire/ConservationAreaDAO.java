package it.jrc.smart.fire;

import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;

public class ConservationAreaDAO {

	private ConservationArea ca;

	/**
	 * This class exists simply to make CA data access testable. 
	 */
	public ConservationAreaDAO(ConservationArea ca) {
		this.ca = ca;
	}
	
	public Area getArea(Session session, AreaType areaType) {

		Query q = session.createQuery("from Area where type = :areaType and conservationArea.uuid = :caUuid");
		q.setParameter("areaType", areaType);
		q.setParameter("caUuid", ca.getUuid());

		return (Area) q.uniqueResult();
	}
}
