package ml.cdslplugin.process;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Proceso que ejecuta el script de reinicio de Resin.
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */
public class RestartResinProcess implements IRunnableWithProgress{
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_DONE = 1;
	public final static int STATUS_IDDLE = 2;
	public final static int STATUS_ERROR = 3;
	
	private String vmUser;
	private String vmIP;
	private String cdslKey;
	private String restartResin;
	
	private int status;
	
	public RestartResinProcess(String vmUser, String vmIP, String cdslKey,
			String restartResin) {
		this.vmUser = vmUser;
		this.vmIP = vmIP;
		this.cdslKey = cdslKey;
		this.restartResin = restartResin;
		this.status = STATUS_IDDLE;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
		boolean error = false;
		String cmd = "cmd.exe /C " + restartResin + " " + vmUser + " " + vmIP + " " + cdslKey;
		/*
		 * Lista de parámetros al restartresin.bat:
		 * %1 usuario de la VM
		 * %2 IP de la VM
		 * %3 Llave privada para el login
		 */
		
		monitor.beginTask("Reiniciando Resin", IProgressMonitor.UNKNOWN);
		status = STATUS_RUNNING;
		
		try {
			System.out.println("CMD: " + cmd);
			Runtime.getRuntime().exec(cmd);
			
			/*
			 * Duermo durante toda la ejecución del script para
			 * dar tiempo a que los comandos lleguen a la VM
			 */
			Thread.sleep(10000);
		}
		catch (IOException e) {
			System.err.println("Error al reiniciar Resin: " + e);
			error = true;
		}
		
		monitor.done();
		status = error ? STATUS_ERROR : STATUS_DONE;
	}
	
	public int getStatus() {
		return status;
	}
}
