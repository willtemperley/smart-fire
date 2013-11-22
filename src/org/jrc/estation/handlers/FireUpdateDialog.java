package org.jrc.estation.handlers;

import it.jrc.estation.FireDAO;
import it.jrc.estation.domain.ActiveFire;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.hibernate.SmartHibernateManager;
import org.wcs.smart.intelligence.model.Intelligence;
import org.wcs.smart.intelligence.model.IntelligencePoint;
import org.wcs.smart.intelligence.model.IntelligenceSource;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FireUpdateDialog extends TrayDialog {

	private static final String SELECTION_DIALOG = "Fire data manager";

	public FireUpdateDialog(Shell parentShell) {
		super(parentShell);
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//no-op
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
						
		
		Button button = new Button(container, SWT.PUSH);
	    button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1));


		button.setText("Get fires");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Session session = SmartHibernateManager.openSession();

				// Get CA and area
				ConservationArea ca = SmartDB.getCurrentConservationArea();
				Query q = session
						.createQuery("from Area where type = :areaType and conservationArea.uuid = :caUuid");
				q.setParameter("areaType", AreaType.BA);
				q.setParameter("caUuid", ca.getUuid());
				
				System.out.println("Clicked");

				@SuppressWarnings("unchecked")
				List<Area> areas = q.list();
				for (Area area : areas) {

					System.out.println("area" + area.getUuid());

					Geometry env = area.getGeometry().getEnvelope();

					Envelope x = env.getEnvelopeInternal();

					Date fromDate = getDateFromDateTime(fromDatePicker);
					Date toDate = getDateFromDateTime(toDatePicker);

					List<Date> dateList;

					if (fromDate.after(toDate)) {
					    dateList = FireDAO.getDatesBetweenTheseDates(toDate, fromDate);
					} else {
						dateList = FireDAO.getDatesBetweenTheseDates(fromDate, toDate);
					}


					/*
					 * Making lots of web service calls just for simplified programming.
					 */
					int fireCount = 0;
					int dateCount = 0;

					for (Date date : dateList) {
						System.out.println(date);

						Collection<ActiveFire> someFires = FireDAO.getFiresByDate(x.getMinX(), x.getMinY(), x.getMaxX(), x.getMaxY(), date, date);
						
						if(!someFires.isEmpty()) {
							fireCount += someFires.size();
							saveFires(session, ca, someFires, date, date);
							dateCount++;
						}
						
						status.setText("Imported " + fireCount + " fires over " + dateCount + " days in which fires occurred.");
						//Todo: check dates

					}
				}
			}
		});

		return container;
	}

	// Why DateTime can't do this I don't know!
	private static Date getDateFromDateTime(DateTime dateTimeDOB) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dateTimeDOB.getDay());
		cal.set(Calendar.MONTH, dateTimeDOB.getMonth());
		cal.set(Calendar.YEAR, dateTimeDOB.getYear());
		return cal.getTime();
	}

	public void saveFires(Session session, ConservationArea ca,
			Collection<ActiveFire> someFires, Date fromDate, Date toDate) {

		Intelligence intelligence = new Intelligence();

		Date now = new Date();

		intelligence.setReceivedDate(fromDate);

		intelligence.setFromDate(fromDate);
		intelligence.setToDate(toDate);

//		IntelligenceSource source = new IntelligenceSource();
//		intelligence.setSource(source);

		intelligence.setDescription("Fire");
		intelligence.setName("Fire");
		intelligence.setConservationArea(ca);

		List<IntelligencePoint> ips = new ArrayList<IntelligencePoint>();
		for (ActiveFire activeFire : someFires) {

			IntelligencePoint ip = new IntelligencePoint();
			System.out.println(ip);
			ip.setX(activeFire.getX());
			ip.setY(activeFire.getY());
			ip.setIntelligence(intelligence);
			ips.add(ip);
		}
		intelligence.setPoints(ips);

		session.getTransaction().begin();
		session.saveOrUpdate(intelligence);
		System.out.println("Saved!");
		session.getTransaction().commit();

	}

	// overriding this methods allows you to set the
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
