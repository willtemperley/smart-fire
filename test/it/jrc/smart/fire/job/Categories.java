package it.jrc.smart.fire.job;

import org.hibernate.Query;
import org.hibernate.Session;

public class Categories {
	
//	@Test
	public void testFires() {
		
		Session session = TestHibernateSessionManager.openSession();
		
/*		Session session = 
		SmartHibernateManager.openSession();*/

		Query q = session.createQuery("select max(dateTime) from Waypoint where source = 'MODIS'");
		Object x = q.uniqueResult();
		System.out.println(x);
		
	}
	
}
