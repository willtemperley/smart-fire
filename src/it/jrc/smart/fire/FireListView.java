package it.jrc.smart.fire;

import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.model.ActiveFire;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.hibernate.SmartHibernateManager;

/**
 * A viewer where users can view all intelligence items.
 * 
 * @author elitvin
 * @since 1.0.0
 */
public class FireListView extends ViewPart  {

	public static final String ID = "it.jrc.smart.fire.FireListView"; //$NON-NLS-1$

	private TableViewer intelligenceListViewer;
	private Job updateJob = new UpdateIntelligenceListIdJob();

	private FireViewFilter filter = new FireViewFilter();
	
	public FireViewFilter getFilter() {
		return filter;
	}
	
	/**
	 * Default constructor
	 */
	public FireListView() {
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(partListener);
	}

	public void dispose() {		
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(partListener);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		main.setLayout(layout);
		
		intelligenceListViewer = new TableViewer(main, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table list = intelligenceListViewer.getTable();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		list.setBounds(0, 0, 88, 68);
		
		intelligenceListViewer.setContentProvider(ArrayContentProvider.getInstance());
		intelligenceListViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updateContent();

		
		intelligenceListViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
			}
		});
		
		/* add right click context menu */
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(intelligenceListViewer.getControl());
		intelligenceListViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager,  intelligenceListViewer);
		getSite().setSelectionProvider(intelligenceListViewer);
	}

	/**
	 * Refreshes the intelligence list
	 */
	public void updateContent(){
		updateJob.cancel();
		updateJob.schedule();		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		intelligenceListViewer.getControl().setFocus();

	}

    /**
     * Job is used to fill list viewer with data
     * 
     * @author elitvin
     *
     */
    private class UpdateIntelligenceListIdJob extends Job {

    	public UpdateIntelligenceListIdJob() {
			super(Messages.FireListView_UpdateJob_Title);
		}

    	
    	/*
    	 * USE AS AN EXAMPLE JOB
    	 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(Messages.FireDataUpdate, 1);
			
			List<?> result = loadIntelligences();
			monitor.internalWorked(0.7);
			
			//convert loaded data to List<IntelligenceEditorInput>
			final List<ActiveFire> inputData = new ArrayList<ActiveFire>();
			for (Object obj : result) {
				Object[] data = (Object[]) obj;
			}
			monitor.internalWorked(0.8);
			
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					intelligenceListViewer.setInput(inputData);
					intelligenceListViewer.refresh();
				}
			});
			return Status.OK_STATUS;
		}
		
		private List<?> loadIntelligences() {
			Session session = SmartHibernateManager.openSession();
			session.beginTransaction();
			try {
//				Query query = FireListView.this.getFilter().buildQuery(session);
				List<?> list = new ArrayList<Object>();
				return list;
			} finally {
				session.getTransaction().rollback();
				session.close();
			}
		}
   	
    }
    
    
}
