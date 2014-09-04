package it.jrc.smart.fire.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jaitools.jiffle.parser.RuntimeSourceGenerator.foreachLoop_return;
import org.junit.Test;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.observation.model.Waypoint;

public class UpdateFireTest {
	
	private static final String MODIS_5_0 = "MODIS-5.0";
	private static final String MODIS_5_1 = "MODIS-5.1";

	Session session = TestHibernateSessionManager.openSession();

	public ConservationArea getConservationArea() {
		
		Query q = session.createQuery("from ConservationArea");
		List l = q.list();
		for (Object object : l) {

			ConservationArea ca = (ConservationArea) object;
			System.out.println(ca.getName());
			if (ca.getName().equals("W National Park of Niger")) {
				return ca;
			}

		}
		return null;
	}

	@Test
	public void updateFires() {

		UpdateFireJob dfj = new UpdateFireJob(getConservationArea(), session);
		
		dfj.doUpdate(new MockProgressMonitor());

	}
	
	@Test
	public void getStats() {
		
		Query q = session.createQuery("from Waypoint");
		List<Waypoint> waypoints = q.list();
		
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put(MODIS_5_0, 0);
		m.put(MODIS_5_1, 0);
		
		for (Waypoint waypoint : waypoints) {
			if (waypoint.getSource().equals(MODIS_5_0)) {
				m.put(MODIS_5_0, m.get(MODIS_5_0) + 1);
				m.put(MODIS_5_1, m.get(MODIS_5_1) + 1);
			}
		}

		System.out.println("N waypoints: " + waypoints.size());
		for(String key: m.keySet()) {
			System.out.print(key + " ");
			System.out.println(m.get(key));
		}
		
	}

}
