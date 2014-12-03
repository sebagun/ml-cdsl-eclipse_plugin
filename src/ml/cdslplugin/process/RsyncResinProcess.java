package ml.cdslplugin.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Proceso de sincronización de recursos y reinicio de Resin
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */
public class RsyncResinProcess implements IRunnableWithProgress{
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_DONE = 1;
	public final static int STATUS_IDDLE = 2;
	
	private String vmUser;
	private String vmIP;
	private String cdslKey;
	private String syncList;
	private String runRsync;
	
	private int status;
	ArrayList<String> log;
	
	public RsyncResinProcess(String vmUser, String vmIP,
			String cdslKey, String syncList, String runRsync){
		this.vmUser = vmUser;
		this.vmIP = vmIP;
		this.cdslKey = cdslKey;
		this.syncList = syncList;
		this.runRsync = runRsync;
		this.status = STATUS_IDDLE;
		log = new ArrayList<String>();
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
		
		String cmd = "cmd.exe /C " + runRsync + " " + vmUser + " " + vmIP + " " + cdslKey + " " + syncList;
		/*
		 * Lista de parámetros al runrsync.bat:
		 * %1 usuario de la VM
		 * %2 IP de la VM
		 * %3 Llave privada para el login
		 * %4 Lista de archivos a rsyncronizar
		 */
		
		monitor.beginTask("Sincronizando Resin", 12);
		
		boolean progress = true;
		status = STATUS_RUNNING;
		
		try {
			System.out.println("CMD: " + cmd);
			log.add(cmd);
			Process proc = Runtime.getRuntime().exec(cmd);
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			String str = null;
			while ((str = stdOut.readLine()) != null) {
				if (!str.equals("")) {
					System.out.println(str);
					log.add(str);
					
					// luego de la transferencia de archivos continua el aumento de la barra de progreso
					if (!progress && str.indexOf("Sleep for") > -1) progress = true;
					if (progress) monitor.worked(1); // aumento la barra de progreso si corresponde
					// durante la transferencia de archivos no aumenta la barra (es toda una misma unidad de trabajo)
					if (str.indexOf("rsync -avz --del") > -1) progress = false;
				}
			}
			stdOut.close();
			str = null;
			while ((str = stdErr.readLine()) != null) {
				if (!str.equals("")) {
					System.err.println(str);
					log.add("ERROR: " + str);
				}
			}
			stdErr.close();
		}
		catch (IOException e) {
			System.err.println("Error al sincronizar: " + e);
		}
		
		monitor.done();
		status = STATUS_DONE;
	}
	
	public int getStatus() {
		return status;
	}

	public List<String> getListInput() {
		return log;
	}
}
