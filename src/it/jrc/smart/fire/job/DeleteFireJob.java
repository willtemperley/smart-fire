package it.jrc.smart.fire.job;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DeleteFireJob extends Job{

	private Session session;

	public DeleteFireJob(Session session) {
		super("Delete fire");
		this.session = session;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Transaction t = session.beginTransaction();
		try {
//			session.createQuery("delete from Waypoint where source = 'PATROL'").executeUpdate();
			session.createQuery("delete from Waypoint").executeUpdate();
			t.commit();
		} catch (Exception e) {
			t.rollback();
			return Status.CANCEL_STATUS;
		} 

		return Status.OK_STATUS;
	}
}
