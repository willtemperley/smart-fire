package it.jrc.smart.fire.handlers;

import java.io.IOException;
import java.util.List;

import it.jrc.smart.fire.FirePlugin;
import it.jrc.smart.fire.FirePerspective;
import it.jrc.smart.fire.SmartAccess;
import it.jrc.smart.fire.internal.messages.Messages;
import it.jrc.smart.fire.map.FireService;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.AddLayersCommand;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFirePerspectiveHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil
					.getActiveWorkbenchWindow(event)
					.getWorkbench()
					.showPerspective(FirePerspective.ID,
							HandlerUtil.getActiveWorkbenchWindow(event));


		} catch (WorkbenchException e) {
			// FirePlugin.displayLog(Messages.ShowIntelligencePersepctiveHandler_Error,
			// e);
			System.out.println(e);
		}
		return null;
	}

}
