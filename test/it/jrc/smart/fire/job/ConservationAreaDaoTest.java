package it.jrc.smart.fire.job;

import it.jrc.smart.fire.ConservationAreaDAO;

import java.util.List;

import javax.persistence.Temporal;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.hibernate.SmartDB;

public class ConservationAreaDaoTest {

	@Test
	public void getManagementArea() {
		
		Session session = TestHibernateSessionManager.openSession();
		
		Query q = session.createQuery("from ConservationArea");
		List l = q.list();
		for (Object object : l) {

			ConservationArea ca = (ConservationArea) object;
			System.out.println(ca.getName());
			if (ca.getName().equals("W National Park of Niger")) {
				
				ConservationAreaDAO dao = new ConservationAreaDAO(ca);
				Area a = dao.getArea(session, AreaType.BA);
				System.out.println(a.getUuid());
			}

		}
	}
}
