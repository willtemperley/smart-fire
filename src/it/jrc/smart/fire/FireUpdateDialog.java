package it.jrc.smart.fire;

import it.jrc.smart.fire.job.DeleteFireJob;
import it.jrc.smart.fire.job.UpdateFireJob;
import it.jrc.smart.fire.model.ActiveFire;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.hibernate.SmartHibernateManager;
import org.wcs.smart.observation.model.Waypoint;

public class FireUpdateDialog extends TrayDialog {

	private static final String SELECTION_DIALOG = "Fire data manager";

	Session session = SmartHibernateManager.openSession();

	public FireUpdateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// no-op
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Label fromDateLabel = new Label(container, SWT.NONE);
		fromDateLabel.setText("From date:");

		final DateTime fromDatePicker = new DateTime(container, SWT.BORDER
				| SWT.DATE | SWT.DROP_DOWN);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		fromDatePicker.setLayoutData(gridData);

		Label toDateLabel = new Label(container, SWT.NONE);
		toDateLabel.setText("To date:");

		final DateTime toDatePicker = new DateTime(container, SWT.BORDER
				| SWT.DATE | SWT.DROP_DOWN);
		toDatePicker.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));

		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		toDatePicker.setLayoutData(gridData);

		final Label status = new Label(container, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		status.setLayoutData(gridData);
		
		Button deleteButton = new Button(container, SWT.PUSH);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1,
				1));
		deleteButton.setText("Delete fires");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				DeleteFireJob dfj = new DeleteFireJob(session);
				dfj.schedule();

			}
		});


		Button button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1,
				1));

		button.setText("Get fires");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				UpdateFireJob ufj = new UpdateFireJob(SmartDB.getCurrentConservationArea(), session);
				ufj.schedule();

			}
		});

		return container;
	}
	
	public void getLastDate() {
		Query q = session.createQuery("select max(dateTime) from Waypoint where source = 'MODIS'");
		Object x = q.uniqueResult();
		System.out.println(x);
	}

	// Why DateTime can't do this I don't know!
	private static Date getDateFromDateTime(DateTime dateTimeDOB) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dateTimeDOB.getDay());
		cal.set(Calendar.MONTH, dateTimeDOB.getMonth());
		cal.set(Calendar.YEAR, dateTimeDOB.getYear());
		return cal.getTime();
	}

	private void saveFires(Session session, ConservationArea ca,
			Collection<ActiveFire> someFires, Date fromDate, Date toDate) {

		session.getTransaction().begin();

		for (ActiveFire activeFire : someFires) {
			Waypoint waypoint = new Waypoint();
			waypoint.setDateTime(activeFire.getStamp());
			waypoint.setConservationArea(ca);
			waypoint.setX(activeFire.getX());
			waypoint.setY(activeFire.getY());
			session.saveOrUpdate(waypoint);
		}

		session.getTransaction().commit();
		System.out.println("Saved!");

	}

	// overriding this method allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(SELECTION_DIALOG);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
