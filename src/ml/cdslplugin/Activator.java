package ml.cdslplugin;

import java.util.ArrayList;
import java.util.ResourceBundle;

import ml.cdslplugin.actions.ResinConsoleAction;
import ml.cdslplugin.actions.RsyncResinAction;
import ml.cdslplugin.actions.RunJobAction;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ml.cdslplugin";

	// The shared instance
	private static Activator plugin;
	
	private ArrayList<String> previousJobs;
	private ArrayList<String> previousJobParams;
	
	/**
	 * The constructor
	 */
	public Activator() {
		previousJobs = new ArrayList<String>();
		previousJobParams = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		setPaths();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbenchWindow window= plugin.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			final WindowRef windowRef= new WindowRef();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					setActiveWorkbenchWindow(windowRef);
				}
			});
			return windowRef.window;
		}
		return window;
	}

	private static class WindowRef {
		public IWorkbenchWindow window;
	}

	private static void setActiveWorkbenchWindow(WindowRef windowRef) {
		windowRef.window= null;
		Display display= Display.getCurrent();
		if (display == null)
			return;
		Control shell= display.getActiveShell();
		while (shell != null) {
			Object data= shell.getData();
			if (data instanceof IWorkbenchWindow) {
				windowRef.window= (IWorkbenchWindow)data;
				return;
			}
			shell= shell.getParent();
		}
		Shell shells[]= display.getShells();
		for (int i= 0; i < shells.length; i++) {
			Object data= shells[i].getData();
			if (data instanceof IWorkbenchWindow) {
				windowRef.window= (IWorkbenchWindow)data;
				return;
			}
		}
	}

	/**
	 * @return Returns the shell of the active workbench window.
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window= getActiveWorkbenchWindow();
		if (window != null)
			return window.getShell();
		return null;
	}

	/**
	 * Beeps using the display of the active workbench window.
	 */
	public static void beep() {
		getActiveWorkbenchShell().getDisplay().beep();
	}

	/**
	 * @return  Returns the active workbench window's currrent page.
	 */
	public static IWorkbenchPage getActivePage() {
		return getActiveWorkbenchWindow().getActivePage();
	} 

	/**
	 * @return Returns the workbench from which this plugin has been loaded.
	 */	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/**
	 * Reads properties from the configuration file, and sets usefull paths
	 */
	public void setPaths() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("ml.cdslplugin.Activator");
			String cdslKey = rb.getString("cdslkey");
			RsyncResinAction.setCdslKey(cdslKey);
			RsyncResinAction.setSyncList(rb.getString("synclist"));
			RsyncResinAction.setRunRsync(rb.getString("runrsync"));
			RunJobAction.setCdslKey(cdslKey);
			RunJobAction.setRunJob(rb.getString("runjob"));
			ResinConsoleAction.setCdslKey(cdslKey);
		}
		catch (Exception e) {
			System.err.println("No se puede leer el archivo Activator.properties");
		}
	}
	
	public void addJob(String value){
		if(previousJobs.indexOf(value) < 0)
			previousJobs.add(value);
	}
	
	public String[] getPreviousJobs(){
		return previousJobs.toArray(new String[previousJobs.size()]);
	}
	
	public void addJobParam(String value){
		if(previousJobParams.indexOf(value) < 0)
			previousJobParams.add(value);
	}
	
	public String[] getPreviousJobParams(){
		return previousJobParams.toArray(new String[previousJobParams.size()]);
	}
}
