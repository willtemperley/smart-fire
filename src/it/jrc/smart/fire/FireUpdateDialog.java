package it.jrc.smart.fire;

import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.job.UpdateBAJob;
import it.jrc.smart.fire.job.UpdateFireJob;
import it.jrc.smart.fire.model.ActiveFire;
import it.jrc.smart.fire.model.BurnedArea;
import it.jrc.smart.fire.model.ICategory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.CategoryAttribute;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.hibernate.SmartHibernateManager;

/**
 * Dialog for management of the fire archive
 * 
 * @author willtemperley@gmail.com
 *
 */
public class FireUpdateDialog extends TrayDialog {

	private static ConservationArea CURRENT_CONSERVATION_AREA = SmartDB
			.getCurrentConservationArea();

	private final Date mostRecentAF = new Date(0);

	private final Date mostRecentBA = new Date(0);

	private Label status;

	private Button button;

	private Button activeFire;

	private Button burnedArea;

	private Label activeFireStatus;

	private Label burnedAreaStatus;

	public FireUpdateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// no-op stops the button bar being created
	}
	
	protected Button addCheckBox(Composite container) {
		
		Button checkBox = new Button(container, SWT.CHECK);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		checkBox.setLayoutData(gridData);
		return checkBox;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		

		boolean hasDM = ensureDataModel(CURRENT_CONSERVATION_AREA,
				ActiveFire.model)
				&& ensureDataModel(CURRENT_CONSERVATION_AREA,
						BurnedArea.model);

		if (!hasDM) {
			status = createLabel(container);
			status.setText(Messages.FIRE_DATA_MODEL_NOT_CONFIGURED);
			return container;
		}

		activeFire = addCheckBox(container);
		activeFire.setText(Messages.ACTIVE_FIRE_CHECKBOX_LABEL);
		activeFireStatus = createLabel(container);
		activeFireStatus.setText(Messages.DETERMINING_ARCHIVE_STATUS);

		burnedArea = addCheckBox(container);
		burnedArea.setText(Messages.BURNED_AREA);
		burnedAreaStatus = createLabel(container);
		burnedAreaStatus.setText(Messages.DETERMINING_ARCHIVE_STATUS);

		determineArchiveStatus(CURRENT_CONSERVATION_AREA, ActiveFire.model, activeFireStatus, mostRecentAF);
		determineArchiveStatus(CURRENT_CONSERVATION_AREA, BurnedArea.model, burnedAreaStatus, mostRecentBA);


		// addDeleteFireButton(container);

		button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1,
				1));

		button.setText(Messages.UPDATE_BUTTON_TEXT);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(activeFire.getSelection()) {
					doFireUpdate(mostRecentAF, activeFireStatus);
				}

				if(burnedArea.getSelection()) {
					doBAUpdate(mostRecentBA, burnedAreaStatus);
				}

			}
		});

		return container;
	}

	private Label createLabel(Composite container) {
		Label label = new Label(container, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		return label;
	}

	private boolean ensureDataModel(ConservationArea ca, ICategory iCategory) {

		Session session = SmartHibernateManager.openSession();

		Query q = session
				.createQuery("from Category where conservationArea.uuid = :caUuid and keyId = :keyId");
		q.setParameter("caUuid", ca.getUuid());
		q.setParameter("keyId", iCategory.getName());
		Category fireCat = (Category) q.uniqueResult();

		Set<String> reqAtts = iCategory.getRequiredAttributes();
		Set<String> attNames = new HashSet<String>();

		if (fireCat != null) {
			List<CategoryAttribute> atts = fireCat.getAttributes();
			for (CategoryAttribute categoryAttribute : atts) {
				if (categoryAttribute.getAttribute() != null) {
					attNames.add(categoryAttribute.getAttribute().getName());
				}
			}
		} else {
			return false;
		}
		
		if (attNames.equals(reqAtts)) return true;

		for (String a: reqAtts) {
			if (!attNames.contains(a)) {
				return false;
			}
		}

		
//		for (String att: reqAtts) {
//			System.err.println("Req: " + att);
//		}
//		for (String att: attNames) {
//			System.err.println("Att: " + att);
//		}

		return true;

		// session.getTransaction().begin();
		//
		// Category category = new Category();
		// category.updateName(ca.getDefaultLanguage(), "ActiveFire");
		// category.setKeyId(SmartModel.activefire);
		// category.setConservationArea(ca);
		// category.setHkey("");
		// category.setIsActive(true);
		//
		// session.save(category);
		//
		// String[] attNames = new String[]{SmartModel.frp,
		// SmartModel.confidence};
		//
		// List<CategoryAttribute> catts = new ArrayList<CategoryAttribute>();
		// for (int i = 0; i < attNames.length; i++) {
		//
		// Attribute att = new Attribute();
		// att.setType(AttributeType.NUMERIC);
		// att.updateName(ca.getDefaultLanguage(), attNames[i]);
		//
		// att.setKeyId(SmartModel.activefire + attNames[i]);
		// att.setConservationArea(ca);
		// session.save(att);
		//
		// CategoryAttribute catt = new CategoryAttribute(category, att);
		// catt.setIsActive(true);
		// catts.add(catt);
		// session.save(catt);
		//
		// }
		//
		// category.setAttributes(catts);
		//
		// session.getTransaction().commit();
		// System.out.println("Saved DM");
	}
	

	/**
	 * Given a category, determine the latest date data is available
	 * 
	 * @param iCat
	 */
	private void determineArchiveStatus(final ConservationArea ca, final ICategory iCat, final Label statusLabel, final Date latestDate) {
		new Thread(new Runnable() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						Session session = SmartHibernateManager.openSession();
						Query q = session
								.createQuery("select max(dateTime) from Waypoint where source in (:sourceList) and conservationArea = :ca");
						q.setParameterList("sourceList", iCat.getSources());
						q.setParameter("ca", ca);
						Date mostRecent = (Date) q.uniqueResult();
						if (mostRecent == null) {
							statusLabel.setText(Messages.NO_INFORMATION_YET_AVAILABLE);
						} else {
							latestDate.setTime(mostRecent.getTime());
							statusLabel.setText(Messages.DATA_AVAILABLE_UP_TO
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
//				DeleteFireJob dfj = new DeleteFireJob(session);
//				dfj.schedule();

			}
		});
	}

	private void doBAUpdate(Date mostRecent, final Label statusLabel) {

		Session session = SmartHibernateManager.openSession();
		UpdateBAJob ufj = new UpdateBAJob(CURRENT_CONSERVATION_AREA,
				mostRecent, session) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				final String nFires = doUpdate(monitor);

				/*
				 * A new thread, to update the UI
				 */
				new Thread(new Runnable() {
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								statusLabel.setText(nFires);
							}
						});
					}
				}).start();

				return Status.OK_STATUS;
			}

		};
		ufj.schedule();
		
	}

	private void doFireUpdate(Date mostRecent, final Label statusLabel) {

		Session session = SmartHibernateManager.openSession();
		UpdateFireJob ufj = new UpdateFireJob(CURRENT_CONSERVATION_AREA,
				mostRecent, session) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				final String nFires = doUpdate(monitor);

				/*
				 * A new thread, to update the UI
				 */
				new Thread(new Runnable() {
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								statusLabel.setText(nFires);
							}
						});
					}
				}).start();

				return Status.OK_STATUS;
			}

		};
		ufj.schedule();
	}
	


	// overriding this method allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.FIRE_DIALOG_TITLE);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
