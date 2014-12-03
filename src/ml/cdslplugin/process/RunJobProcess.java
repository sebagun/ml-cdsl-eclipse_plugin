package ml.cdslplugin.process;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class RunJobProcess implements IRunnableWithProgress{
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_DONE = 1;
	public final static int STATUS_IDDLE = 2;
	
	private String vmUser;
	private String vmIP;
	private String cdslKey;
	private String runJob;
	private String job;
	private String params;
	
	private int status;
	
	public RunJobProcess(String vmUser, String vmIP, 
			String cdslKey, String runJob, String job, String params){
		this.vmUser = vmUser;
		this.vmIP = vmIP;
		this.cdslKey = cdslKey;
		this.runJob = runJob;
		this.job = job;
		this.params = params;
		this.status = STATUS_IDDLE;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
		
		String cmd = "cmd.exe /C " + runJob + " " + vmUser + " " + vmIP + " " + cdslKey + " " + job + " \"" + params + "\"";
		/*
		 * Lista de parámetros al runjob.bat:
		 * %1 usuario de la VM
		 * %2 IP de la VM
		 * %3 Llave privada para el login
		 * %4 Job a ejecutar
		 * %5 Parámetros del job
		 */
		
		monitor.beginTask("Corriendo Job " + job, IProgressMonitor.UNKNOWN);
		status = STATUS_RUNNING;
		
		try {
			System.out.println("CMD: " + cmd);
			Runtime.getRuntime().exec(cmd);
			
			// Muestro la salida de consola del job
			try {
				String logCmd = "cmd.exe /C START \"Job " + job + "\" plink -i " + cdslKey + " -v -t -l " + 
						vmUser + " " + vmIP + " tail -f /data1/resin/log/" + job + ".log";
				System.out.println("CMD: " + logCmd);
				Runtime.getRuntime().exec(logCmd);
			}
			catch (IOException e) {
				System.err.println("Error al querer mostrar la consola del job " + job + ": " + e);
			}
		}
		catch (IOException e) {
			System.err.println("Error al correr el job " + job + 
					" con los parámetros [" + params + "]: " + e);
		}
		
		monitor.done();
		status = STATUS_DONE;
	}
	
	public int getStatus() {
		return status;
	}
}
