/*
 * Copyright (C) 2012 Wildlife Conservation Society
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package it.jrc.smart.fire;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.wcs.smart.ui.map.MapView;

/**
 * The intelligence perspective.
 * 
 * @author elitvin
 *
 */
public class FirePerspective implements IPerspectiveFactory {

	public static final String ID = "it.jrc.smart.fire.FirePerspective"; //$NON-NLS-1$
	
	@Override
	public void createInitialLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		layout.addView(FireListView.ID, IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
//		layout.getViewLayout(FireListView.ID).setCloseable(false);
		
		IFolderLayout folder1 = layout.createFolder("org.wcs.smart.fire.fireFolder", IPageLayout.BOTTOM, 0.6f, FireListView.ID); //$NON-NLS-1$
		folder1.addView("net.refractions.udig.project.ui.layerManager"); //$NON-NLS-1$
		
		layout.addView(MapView.ID, IPageLayout.RIGHT, 0.75f, IPageLayout.ID_EDITOR_AREA);
		

	}

}
