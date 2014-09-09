package it.jrc.smart.fire;

import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.job.UpdateFireJob;
import it.jrc.smart.fire.map.FireDataSource;
import it.jrc.smart.fire.map.FireService;
import it.jrc.smart.fire.model.ActiveFire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.commands.AddLayersCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.hibernate.SmartHibernateManager;

/**
 * 
 */
public class FireListView extends ViewPart  {

	public static final String ID = "it.jrc.smart.fire.FireListView"; //$NON-NLS-1$

	private Session session = SmartHibernateManager.openSession();

	private FireViewFilter filter = new FireViewFilter();

	private Label statusLabel;

	private Date mostRecent;

	private DateTime fromDateTime;

	private DateTime toDateTime;
	
	public FireViewFilter getFilter() {
		return filter;
	}
	
	public FireListView() {
	}

	public void dispose() {		
		super.dispose();
	}
	
	
	@Override
	public void createPartControl(Composite parent) {


		Composite main = new Composite(parent, SWT.NONE);

		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		main.setLayout(layout);
		
		fromDateTime = addDatePicker(main, "From date:");

		toDateTime = addDatePicker(main, "To date:");
		
		
		/*
		 * Update section
		 */
//		statusLabel = new Label(main, SWT.NONE);
//		statusLabel.setText(mostRecent.toString());

		Button button = new Button(main, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1,
				1));
		
		button.setText("Display date range");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				x();

			}
		});
	}

	public void x() {
		
			FireDataSource fds = new FireDataSource(session, getDateFromDateTime(fromDateTime), getDateFromDateTime(toDateTime));
			FireService s = new FireService(fds);
			List<IGeoResource> layers;
			try {
				layers = (List<IGeoResource>) s.resources(null);
				AddLayersCommand command = new AddLayersCommand(layers, 0);
				SmartAccess.getMapView().getMap().sendCommandASync(command);

			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	// Why DateTime can't do this I don't know!
	private static Date getDateFromDateTime(DateTime dateTimeDOB) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dateTimeDOB.getDay());
		cal.set(Calendar.MONTH, dateTimeDOB.getMonth());
		cal.set(Calendar.YEAR, dateTimeDOB.getYear());
		return cal.getTime();
	}

	private DateTime addDatePicker(Composite main, String caption) {
		Label toDateLabel = new Label(main, SWT.NONE);
		toDateLabel.setText(caption);

		final DateTime fromDatePicker = new DateTime(main, SWT.BORDER
				| SWT.DATE | SWT.DROP_DOWN);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		fromDatePicker.setLayoutData(gridData);
		return fromDatePicker;
	}

	
	@Override
	public void setFocus() {
//		intelligenceListViewer.getControl().setFocus();

	}

    
}
