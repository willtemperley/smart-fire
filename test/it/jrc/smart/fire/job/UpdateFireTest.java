package it.jrc.smart.fire.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.observation.model.Waypoint;

public class UpdateFireTest {
	
	private static final String MODIS_5_0 = "MODIS-5.0";
	private static final String MODIS_5_1 = "MODIS-5.1";

	Session session = TestHibernateSessionManager.openSession();

	public ConservationArea getConservationArea() {
		
		Query q = session.createQuery("from ConservationArea");
		@SuppressWarnings("unchecked")
		List<ConservationArea> l = q.list();
		for (ConservationArea ca : l) {
			System.out.println(ca.getName());
			if (ca.getName().equals("W National Park of Niger")) {
				return ca;
			}

		}
		return null;
	}
	
	
	@Test
	public void getRecentFiresFromDB() {
		Query q = session.createQuery("from Waypoint where source = 'MODIS-5.0' or source = 'MODIS-5.1' order by datetime desc");
		List l = q.list();
		for (Object object : l) {
			System.out.println(object);
		}
	}

	@Test
	public void updateFires() {

		UpdateFireJob dfj = new UpdateFireJob(getConservationArea(), null, session) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}};

		dfj.doUpdate(new MockProgressMonitor());

	}
	
	@Test
	public void getStats() {
		
		Query q = session.createQuery("from Waypoint");
		List<Waypoint> waypoints = q.list();
		
		Map<String, Integer> m = new HashMap<String, Integer>();
		
		for (Waypoint waypoint : waypoints) {
			String sourceId = waypoint.getSourceId();
			if(m.get(sourceId) == null) {
				m.put(sourceId, 1);
			} else {
				m.put(sourceId, m.get(sourceId) + 1);
			}
		}

		System.out.println("N waypoints: " + waypoints.size());
		for(String key: m.keySet()) {
			System.out.print(key + " ");
			System.out.println(m.get(key));
		}
		
	}

}
