package ml.cdslplugin.preferences;

import ml.cdslplugin.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * <br><br>Panel de configuración del plugin en Eclipse.
 * Aquí se agregan los controles necesarios para las
 * configuraciones que deban mostrarse.
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */

public class CDSLPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public CDSLPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuraciones específicas para el CDSL.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_VM_USER,
				"Usuario de la VM:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_VM_IP,
				"IP de la VM:", getFieldEditorParent()));
		
		// divisor
		createDescriptionLabel(getFieldEditorParent()).setText("");
		createDescriptionLabel(getFieldEditorParent()).setText("");
		
		createDescriptionLabel(getFieldEditorParent()).setText(
				"No mostrar alerta al:");
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALERT_RSYNC,
				"Sincronizar Resin", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALERT_RESTART_RESIN,
				"Reiniciar Resin", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALERT_RESTART_APACHE,
				"Reiniciar Apache", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}