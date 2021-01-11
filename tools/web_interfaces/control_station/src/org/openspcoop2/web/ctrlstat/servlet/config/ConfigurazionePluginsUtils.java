/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
