package ml.cdslplugin.actions;

import java.net.InetAddress;

import ml.cdslplugin.Activator;
import ml.cdslplugin.preferences.PreferenceConstants;
import ml.cdslplugin.process.RunJobProcess;
import ml.cdslplugin.views.RunJobForm;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
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
 * @see IWorkbenchWindowActionDelegate
 */
public class RunJobAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	// Paths a archivos necesarios
	private static String cdslKey = null;
	private static String runJob = null;
	
	private static final String DIALOG_TITLE = "CDSL Eclipse Plug-in";
	
	/**
	 * The constructor.
	 */
	public RunJobAction() {
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
				runJob == null || runJob.equals("")) {
			// Pruebo volver a seteralo por si se actualizó con Eclipse abierto
			Activator.getDefault().setPaths();
		}
		if (cdslKey == null || cdslKey.equals("") ||
				runJob == null || runJob.equals("")) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Configure los paths cdslkey y runjob\n" +
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
		
		RunJobForm form = new RunJobForm(window.getShell(), 
				Activator.getDefault().getPreviousJobs(),
				Activator.getDefault().getPreviousJobParams());
		
		if (form.open() != InputDialog.OK) {
			// El usuario canceló desde el formulario
			return;
		}
		
		String job = form.getJob();
		if (job != null && !job.equals("")) {
			Activator.getDefault().addJob(job);
		}
		else {
			// No ingresó job a ejecutar ?!?!
			return;
		}
		
		String params = form.getJobParams();
		if (params == null) {
			params = "";
		}
		if (!params.equals("")) {
			Activator.getDefault().addJobParam(params);
		}
		
		RunJobProcess rjp = null;
		try {
			rjp = new RunJobProcess(vmUser, vmIP, cdslKey, runJob, job, params);
			//Progress bar chiquita abajo
			window.run(true, false, rjp);
		}
		catch (Exception e) {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"Error al correr el job " + job + 
							" con los parámetros [" + params + "]: " + e);
			e.printStackTrace();
			return;
		}
		
		if (rjp != null && rjp.getStatus() == RunJobProcess.STATUS_DONE) {
			MessageDialog.openInformation(
					window.getShell(), DIALOG_TITLE,
					"OK\n\nEl job ha sido lanzado");
		}
		else {
			MessageDialog.openError(
					window.getShell(), DIALOG_TITLE,
					"ERROR\n\nVea la consola para más información");
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
		RunJobAction.cdslKey = cdslKey;
	}

	public static String getRunJob() {
		return runJob;
	}

	public static void setRunJob(String runJob) {
		RunJobAction.runJob = runJob;
	}
}