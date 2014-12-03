package ml.cdslplugin.actions;

import java.net.InetAddress;

import ml.cdslplugin.Activator;
import ml.cdslplugin.preferences.PreferenceConstants;
import ml.cdslplugin.views.RsyncResinView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class RsyncResinAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	// Paths a archivos necesarios
	private static String cdslKey = null;
	private static String syncList = null;
	private static String runRsync = null;
	
	private static final String DIALOG_TITLE = "CDSL Eclipse Plug-in";
	
	/**
	 * The constructor.
	 */
	public RsyncResinAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String vmUser = store.getString(PreferenceConstants.P_VM_USER);
		String vmIP = store.getString(PreferenceConstants.P_VM_IP);
		
		// Verifico que la configuración sea correcta
		if (vmUser.equals("") || vmIP.equals("")) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Debe ingresar el usuario e IP de la VM en Window -> Preferences -> CDSL");
			return;
		}
		
		if (cdslKey == null || cdslKey.equals("") ||
				syncList == null || syncList.equals("") ||
				runRsync == null || runRsync.equals("")) {
			// Pruebo volver a seteralos por si se actualizaron con Eclipse abierto
			Activator.getDefault().setPaths();
		}
		if (cdslKey == null || cdslKey.equals("") ||
				syncList == null || syncList.equals("") ||
				runRsync == null || runRsync.equals("")) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Configure los paths cdslkey, synclist y runrsync\n" +
						"en el archivo Activator.properties");
			return;
		}
		
		// Verifico que exista comunicación con la VM
		try {
			InetAddress inet = InetAddress.getByName(vmIP);
			if (!inet.isReachable(10000)) {
				throw new Exception();
			}
		}
		catch (Exception e) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"No es posible la comunicación con " + vmIP);
			return;
		}
		
		boolean doSync = MessageDialog.openConfirm(
				window.getShell(), DIALOG_TITLE,
				"¿Seguro que desea sincronizar Resin?\n\nUsuario: " + vmUser + "\nIP: " + vmIP);
		
		if (doSync) {
			boolean success = false;
			// Ejecuta el script de sincronización
			try {
				RsyncResinView view = (RsyncResinView) Activator.getActivePage().showView(RsyncResinView.ID);
				success = view.initScript(window, vmUser, vmIP, cdslKey, syncList, runRsync);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			
			if (success) {
				MessageDialog.openInformation(
						window.getShell(), DIALOG_TITLE,
						"OK\n\nResin ha reiniciado");
			}
			else {
				MessageDialog.openError(
						window.getShell(), DIALOG_TITLE,
						"ERROR\n\nVea la consola para más información");
			}
		}
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public static String getCdslKey() {
		return cdslKey;
	}

	public static void setCdslKey(String cdslKey) {
		RsyncResinAction.cdslKey = cdslKey;
	}

	public static String getSyncList() {
		return syncList;
	}

	public static void setSyncList(String syncList) {
		RsyncResinAction.syncList = syncList;
	}

	public static String getRunRsync() {
		return runRsync;
	}

	public static void setRunRsync(String runRsync) {
		RsyncResinAction.runRsync = runRsync;
	}
}