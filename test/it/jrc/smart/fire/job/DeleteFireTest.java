package it.jrc.smart.fire.job;

import it.jrc.smart.fire.job.DeleteFireJob;

import org.hibernate.Session;
import org.junit.Test;

public class DeleteFireTest {
	
	@Test
	public void deleteFires() {
		Session session = TestHibernateSessionManager.openSession();
		DeleteFireJob dfj = new DeleteFireJob(session);
		
		dfj.run(new MockProgressMonitor());
	}

}
