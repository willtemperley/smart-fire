package it.jrc.smart.fire.job;

import org.hibernate.Session;

public class DeleteFireTest {
	
//	@Test
	public void deleteFires() {
		Session session = TestHibernateSessionManager.openSession();
		DeleteFireJob dfj = new DeleteFireJob(session);
		
		dfj.run(new MockProgressMonitor());
	}

}
