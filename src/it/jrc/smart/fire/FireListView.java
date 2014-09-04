package it.jrc.smart.fire;

import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.model.ActiveFire;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Session;
import org.wcs.smart.hibernate.SmartHibernateManager;

/**
 * 
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
		
		final DateTime fromDatePicker = new DateTime(main, SWT.BORDER
				| SWT.DATE | SWT.DROP_DOWN);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = false;
		fromDatePicker.setLayoutData(gridData);
		
//        final RangeSlider hRangeSlider = new RangeSlider(parent, SWT.HORIZONTAL);
//        final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 2);
//        gd.widthHint = 250;
//        hRangeSlider.setLayoutData(gd);
//        hRangeSlider.setMinimum(100);
//        hRangeSlider.setMaximum(1000);
//        hRangeSlider.setLowerValue(200);
//        hRangeSlider.setUpperValue(800);
//        hRangeSlider.setIncrement(100);
//        hRangeSlider.setPageIncrement(200);
	}

	/**
	 * Refreshes the intelligence list
	 */
	public void updateContent(){
		updateJob.cancel();
		updateJob.schedule();		
	}
	
	@Override
	public void setFocus() {
//		intelligenceListViewer.getControl().setFocus();

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
