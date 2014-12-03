package ml.cdslplugin.process;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Proceso que ejecuta el script de reinicio de Apache.
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */
public class RestartApacheProcess implements IRunnableWithProgress{
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_DONE = 1;
	public final static int STATUS_IDDLE = 2;
	public final static int STATUS_ERROR = 3;
	
	private String vmUser;
	private String vmIP;
	private String cdslKey;
	private String restartApache;
	
	private int status;
	
	public RestartApacheProcess(String vmUser, String vmIP, String cdslKey,
			String restartApache) {
		this.vmUser = vmUser;
		this.vmIP = vmIP;
		this.cdslKey = cdslKey;
		this.restartApache = restartApache;
		this.status = STATUS_IDDLE;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
		boolean error = false;
		String cmd = "cmd.exe /C " + restartApache + " " + vmUser + " " + vmIP + " " + cdslKey;
		/*
		 * Lista de parámetros al restartapache.bat:
		 * %1 usuario de la VM
		 * %2 IP de la VM
		 * %3 Llave privada para el login
		 */
		
		monitor.beginTask("Reiniciando Apache", IProgressMonitor.UNKNOWN);
		status = STATUS_RUNNING;
		
		try {
			System.out.println("CMD: " + cmd);
			Runtime.getRuntime().exec(cmd);
			
			/*
			 * Duermo durante toda la ejecución del script para
			 * dar tiempo a que los comandos lleguen a la VM
			 */
			Thread.sleep(15000);
		}
		catch (IOException e) {
			System.err.println("Error al reiniciar Apache: " + e);
			error = true;
		}
		
		monitor.done();
		status = error ? STATUS_ERROR : STATUS_DONE;
	}
	
	public int getStatus() {
		return status;
	}
}
