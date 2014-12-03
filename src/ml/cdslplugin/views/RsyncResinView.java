package ml.cdslplugin.views;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ml.cdslplugin.process.RsyncResinProcess;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class RsyncResinView extends ViewPart {
	
	public static final String ID = "ml.cdslplugin.views.RsyncResinView";
	
	private ListViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		setContentDescription("Preparado para sincronizar");
		
		parent.setLayout(new FillLayout());
		viewer = new ListViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
	}

	public RsyncResinView() {
	}

	public boolean initScript(IWorkbenchWindow window, String vmUser, String vmIP, 
			String cdslKey, String syncList, String runRsync) {
		RsyncResinProcess sync = null;
		try {
			setContentDescription("Sincronizando Resin...");
			viewer.setInput(null); // limpia la vista
			sync = new RsyncResinProcess(vmUser, vmIP, cdslKey, syncList, runRsync);
			//Progress bar chiquita abajo
			window.run(true, false, sync);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		if (sync != null && sync.getStatus() == RsyncResinProcess.STATUS_DONE) {
			List<String> input = sync.getListInput();
			if (input != null && input.size()>0) {
				viewer.setInput(input);
				setContentDescription("Sincronización exitosa");
				return true;
			}
			else {
				setContentDescription("Falló la sincronización");
				return false;
			}
		}
		else {
			setContentDescription("Sincronización cancelada");
			return true;
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}