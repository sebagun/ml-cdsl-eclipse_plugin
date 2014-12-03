package ml.cdslplugin.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class RunJobForm extends Dialog {
	private Combo cmbJob;
	private String[] previousJobs;
	private Combo cmbJobParams;
	private String[] previousJobParams;
	
	private String job;
	private String params;
	
	private static final Point initialSize = new Point(346,200);
	
	public RunJobForm(Shell parentShell, String[] previousJobs,
			String[] previousJobParams) {
		super(parentShell);
		this.previousJobs = previousJobs;
		this.previousJobParams = previousJobParams;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(null);
		
		final Label lblJob = new Label(container,SWT.LEFT);
		lblJob.setText("Job a ejecutar (nombre completo de la clase):");
		lblJob.setSize(330,18);
		lblJob.setLocation(5, 5);
		
		cmbJob = new Combo(container,SWT.DROP_DOWN);
		cmbJob.setSize(323, 22);
		cmbJob.setLocation(5, 25);
		
		final Label lblEjemploJob = new Label(container,SWT.LEFT);
		lblEjemploJob.setText("Ejemplo: com.mercadolibre.jobs.cws.DepurateCWSCasesJob");
		lblEjemploJob.setSize(330,18);
		lblEjemploJob.setLocation(5, 50);
		
		final Label lblFiltroArchivos = new Label(container,SWT.LEFT);
		lblFiltroArchivos.setText("Parámetros (opcional):");
		lblFiltroArchivos.setSize(330, 18);
		lblFiltroArchivos.setLocation(5, 80);
		
		cmbJobParams = new Combo(container,SWT.DROP_DOWN);
		cmbJobParams.setSize(323, 22);
		cmbJobParams.setLocation(5, 100);
		
		initCombos();
		
		return container;

	}

	private void initCombos() {
		// Jobs
		if(previousJobs != null && previousJobs.length > 0){
			for (int i = 0; i < previousJobs.length; i++) {
				if(valueIsNotInCombo(previousJobs[i], cmbJob))
					cmbJob.add(previousJobs[i]);
			}
		}
		
		// Job Params
		if(previousJobParams != null && previousJobParams.length > 0){
			for (int i = 0; i < previousJobParams.length; i++) {
				if(valueIsNotInCombo(previousJobParams[i], cmbJobParams))
					cmbJobParams.add(previousJobParams[i]);
			}
		}
	}

	private boolean valueIsNotInCombo(String value, Combo combo) {
		for (int i = 0; i < combo.getItems().length; i++) {
			if(value.equals(combo.getItems()[i]))
				return false;
		}
		return true;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Correr Job");
	}
	
	public String getJob(){
		return job;
	}
	
	public String getJobParams(){
		return params;
	}
	
	@Override
	protected Point getInitialSize() {
		return initialSize;
	}
	
	@Override
	protected void okPressed() {
		job = cmbJob.getText();
		params = cmbJobParams.getText();
		super.okPressed();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				"Correr Job", true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"Cancelar", false);
	}
}
