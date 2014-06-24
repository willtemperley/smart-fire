package org.jrc.estation.handlers;

import it.jrc.estation.Activator;
import it.jrc.estation.FirePerspective;
import it.jrc.estation.messages.Messages;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFirePerspectiveHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		//Open Intelligence Perspective
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench()
					.showPerspective(FirePerspective.ID, HandlerUtil.getActiveWorkbenchWindow(event));
		} catch (WorkbenchException e) {
//			Activator.displayLog(Messages.ShowIntelligencePersepctiveHandler_Error, e);
			System.out.println(e);
		}
		return null;
	}

}
