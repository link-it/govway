package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;

/**
 * ConfigurazionePluginsUtils
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazionePluginsUtils {

	public static boolean deletePlugin(Long idPlugin, String userLogin, ConfigurazioneCore confCore, ConfigurazioneHelper confHelper,
			StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !confHelper.isModalitaCompleta();
		
		// rileggo il plugin 
		Plugin plugin = confCore.getPlugin(idPlugin);
		
		boolean pluginInUso = confCore.isPluginInUso(plugin.getClassName(), 
				plugin.getLabel(), plugin.getTipoPlugin(), plugin.getTipo(),whereIsInUso,normalizeObjectIds);
		
		if (pluginInUso) {
			inUsoMessage.append(DBOggettiInUsoUtils.toString(plugin.getClassName(), 
					plugin.getLabel(), plugin.getTipoPlugin(), plugin.getTipo(), whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);
			return true;

		} else {
			confCore.performDeleteOperation(userLogin, confHelper.smista(), plugin);
		}
		
		return false;
	}
}
