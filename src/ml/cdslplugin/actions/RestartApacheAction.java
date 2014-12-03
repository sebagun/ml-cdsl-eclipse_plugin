package ml.cdslplugin.actions;

import ml.cdslplugin.Activator;
import ml.cdslplugin.preferences.PreferenceConstants;
import ml.cdslplugin.process.RestartApacheProcess;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be
 * delegated to it.
 * 
 * <br><br>Action que reinicia Apache
 * 
 * @see IWorkbenchWindowActionDelegate
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */
public class RestartApacheAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	// Paths a archivos necesarios
	private static String cdslKey = null;
	private static String restartApache = null;
	
	private static final String DIALOG_TITLE = "CDSL Eclipse Plug-in";
	
	/**
	 * The constructor.
	 */
	public RestartApacheAction() {
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
		boolean dontShowAlert = store.getBoolean(PreferenceConstants.P_ALERT_RESTART_APACHE);
		
		// Verifico que la configuración sea correcta
		if (vmUser.equals("") || vmIP.equals("")) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Debe ingresar el usuario e IP de la VM en Window -> Preferences -> CDSL");
			return;
		}
		
		if (cdslKey == null || cdslKey.equals("") ||
				restartApache == null || restartApache.equals("")) {
			// Pruebo volver a seteralos por si se actualizaron con Eclipse abierto
			Activator.getDefault().setPaths();
		}
		if (cdslKey == null || cdslKey.equals("") ||
				restartApache == null || restartApache.equals("")) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Configure los paths cdslkey y restartApache\n" +
						"en el archivo Activator.properties");
			return;
		}
		
		/*
		 * Esto lo comento porque a través de la VPN no anda.
		 * Si no hay conexión a la VM, el error saltará al ejecutar
		 * el comando y se informará algo más feo al usuario.
		 */
//		// Verifico que exista comunicación con la VM
//		try {
//			InetAddress inet = InetAddress.getByName(vmIP);
//			if (!inet.isReachable(10000)) {
//				throw new Exception();
//			}
//		}
//		catch (Exception e) {
//			MessageDialog.openError(
//					window.getShell(), DIALOG_TITLE,
//					"No es posible la comunicación con " + vmIP);
//			return;
//		}
		
		boolean restart = dontShowAlert ? true : MessageDialog.openConfirm(
				window.getShell(), DIALOG_TITLE,
				"¿Seguro que desea reiniciar Apache?\n\nUsuario: " + vmUser + "\nIP: " + vmIP);
		
		if (restart) {
			RestartApacheProcess rrp = null;
			try {
				rrp = new RestartApacheProcess(vmUser, vmIP, cdslKey, restartApache);
				//Progress bar chiquita abajo
				window.run(true, false, rrp);
			}
			catch (Exception e) {
				MessageDialog.openError(
						window.getShell(), DIALOG_TITLE,
						"Error al reiniciar Apache: " + e);
				e.printStackTrace();
				return;
			}
			
			if (rrp != null && rrp.getStatus() == RestartApacheProcess.STATUS_DONE) {
				MessageDialog.openInformation(
						window.getShell(), DIALOG_TITLE,
						"OK\n\nApache ha reiniciado");
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
		RestartApacheAction.cdslKey = cdslKey;
	}

	public static void setRestartApache(String restartApache) {
		RestartApacheAction.restartApache = restartApache;
	}

	public static String getRestartApache() {
		return restartApache;
	}
}