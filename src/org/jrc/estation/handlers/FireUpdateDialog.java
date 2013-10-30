package org.jrc.estation.handlers;

import it.jrc.estation.FireDAO;
import it.jrc.estation.FireHibernateManager;
import it.jrc.estation.domain.ActiveFire;

import java.util.Collection;
import java.util.List;

import org.apache.derby.tools.sysinfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jaitools.jiffle.parser.RuntimeSourceGenerator.foreachLoop_return;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.hibernate.SmartHibernateManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FireUpdateDialog extends TitleAreaDialog {

	  public FireUpdateDialog(Shell parentShell) {
	    super(parentShell);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite container = (Composite) super.createDialogArea(parent);
	    Button button = new Button(container, SWT.PUSH);
	    button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
	        false));

	    button.setText("5 day fires");
	    button.addSelectionListener(new SelectionAdapter() {
	      @Override
	      public void widgetSelected(SelectionEvent e) {
	    	  
			Session session = SmartHibernateManager.openSession();
			Query q = session.createQuery("from Area where type = :areaType");
			q.setParameter("areaType", AreaType.BA);

			@SuppressWarnings("unchecked")
			List<Area> areas = q.list();
			for (Area area : areas) {
				System.out.println(area.getName());
				Geometry env = area.getGeometry().getEnvelope();
				
				Envelope x = env.getEnvelopeInternal();
				
				Collection<ActiveFire> someFires = FireDAO.getSomeFires(x.getMinX(),x.getMinY(), x.getMaxX(), x.getMaxY());
				System.out.println(FireHibernateManager.getDatabaseLocation());
				for (ActiveFire activeFire : someFires) {
					boolean success = FireHibernateManager.saveActiveFire(activeFire, FireHibernateManager.openSession());
					if (success) {
						System.out.println("Saved: " + activeFire.getUuid());
					}
				}
				

			}
			
	      }
	    });

	    return container;
	  }

	  // overriding this methods allows you to set the
	  // title of the custom dialog
	  @Override
	  protected void configureShell(Shell newShell) {
	    super.configureShell(newShell);
	    newShell.setText("Selection dialog");
	  }

	  @Override
	  protected Point getInitialSize() {
	    return new Point(450, 300);
	  }

	} 
