package it.jrc.smart.fire;

import it.jrc.smart.fire.job.DeleteFireJob;
import it.jrc.smart.fire.job.UpdateFireJob;
import it.jrc.smart.fire.model.ActiveFire;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.datamodel.Attribute;
import org.wcs.smart.ca.datamodel.Attribute.AttributeType;
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.CategoryAttribute;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.hibernate.SmartHibernateManager;
import org.wcs.smart.observation.model.Waypoint;

public class FireUpdateDialog extends TrayDialog {

	private static final ConservationArea CURRENT_CONSERVATION_AREA = SmartDB.getCurrentConservationArea();

	private static final String SELECTION_DIALOG = "Fire data manager";

	Session session = SmartHibernateManager.openSession();

	private Date mostRecent;

	private Label status;

	private Button button;

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
		status = new Label(container, SWT.NONE);

		ensureDataModel(CURRENT_CONSERVATION_AREA);

		status.setText("Determining fire archive status ...");

		determineLatestFireDate();

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		status.setLayoutData(gridData);

		addDeleteFireButton(container);

		button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1,
				1));

		button.setText("Update");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				doFireUpdate();
			}
		});

		return container;
	}

	private void ensureDataModel(ConservationArea ca) {
		
		Query q = session.createQuery("from Category where conservationArea.uuid = :caUuid and keyId = :keyId");
		q.setParameter("caUuid", ca.getUuid());
		q.setParameter("keyId", "activefire");
		Category fireCat = (Category) q.uniqueResult();
		if (fireCat != null) {
			System.out.println("have fireCat " + fireCat);
			return;
		}

		session.getTransaction().begin();

		Category category = new Category();
		category.updateName(ca.getDefaultLanguage(), "ActiveFire");
		category.setKeyId("activefire");
		category.setConservationArea(ca);
		category.setHkey("");
		category.setIsActive(true);

		session.save(category);
		
		String[] attNames = new String[]{"frp", "confidence"};
		List<CategoryAttribute> catts = new ArrayList<CategoryAttribute>();
		for (int i = 0; i < attNames.length; i++) {
			
			Attribute att = new Attribute();
			att.setType(AttributeType.NUMERIC);
			att.updateName(ca.getDefaultLanguage(), attNames[i]);
			att.setKeyId("activefire." + attNames[i]);
			att.setConservationArea(ca);
			session.save(att);

			CategoryAttribute catt = new CategoryAttribute(category, att);
			catts.add(catt);
			session.save(catt);

		}

		category.setAttributes(catts);
		
		session.getTransaction().commit();
		System.out.println("Saved DM");
	}

	private void determineLatestFireDate() {
		new Thread(new Runnable() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						Query q = session
								.createQuery("select max(dateTime) from Waypoint where source = 'MODIS-5.0' or source = 'MODIS-5.1'");
						mostRecent = (Date) q.uniqueResult();
						if (mostRecent == null) {
							status.setText("No active fire information yet available.");
						} else {
							status.setText("Date of latest active fire information available: "
									+ mostRecent.toString());
						}

					}
				});
			}
		}).start();
	}

	private void addDeleteFireButton(Composite container) {
		Button deleteButton = new Button(container, SWT.PUSH);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true,
				true, 1, 1));
		deleteButton.setText("Delete fires");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				DeleteFireJob dfj = new DeleteFireJob(session);
				dfj.schedule();

			}
		});
	}

	private void doFireUpdate() {

		UpdateFireJob ufj = new UpdateFireJob(
				CURRENT_CONSERVATION_AREA, mostRecent, session) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Integer nFires = doUpdate(monitor);

				/*
				 * A new thread, to update the UI
				 */
				new Thread(new Runnable() {
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								status.setText("Fire archive updated. " + nFires + " active fire pixels imported.");
							}
						});
					}
				}).start();

				return Status.OK_STATUS;
			}

		};
		ufj.schedule();
		
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
