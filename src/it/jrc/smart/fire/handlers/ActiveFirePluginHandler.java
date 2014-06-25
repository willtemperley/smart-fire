package it.jrc.smart.fire.handlers;


import it.jrc.smart.fire.FireUpdateDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class ActiveFirePluginHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		FireUpdateDialog md = new FireUpdateDialog(Display.getDefault()
				.getActiveShell());
		md.open();

		return null;
	}

}
