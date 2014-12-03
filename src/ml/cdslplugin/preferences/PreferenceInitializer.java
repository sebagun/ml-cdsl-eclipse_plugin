package ml.cdslplugin.preferences;

import ml.cdslplugin.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 * 
 * @author Sebastián Gun <sebastian.gun@mercadolibre.com>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_VM_USER, "root");
		store.setDefault(PreferenceConstants.P_VM_IP, "");
		store.setDefault(PreferenceConstants.P_ALERT_RSYNC, false);
		store.setDefault(PreferenceConstants.P_ALERT_RESTART_RESIN, false);
		store.setDefault(PreferenceConstants.P_ALERT_RESTART_APACHE, false);
	}

}
