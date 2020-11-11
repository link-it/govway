/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;

/**
 * ConfigurazioneCanaliUtilities
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneCanaliUtilities {

	public static boolean deleteCanale(CanaleConfigurazione canale, String userLogin, 
			ConfigurazioneCore confCore, ConfigurazioneHelper confHelper, StringBuilder inUsoMessage, String newLine) throws Exception {

		boolean deleteCanale = false;
		// Controllo che il canale non sia in uso
		Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new Hashtable<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !confHelper.isModalitaCompleta();
		boolean canaleInUso  = confCore.isCanaleInUso(canale, whereIsInUso, normalizeObjectIds);

		if (canaleInUso) {

			inUsoMessage.append(DBOggettiInUsoUtils.toString(canale, whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			// il canale si puo' eliminare
			deleteCanale = true;
		}

		return deleteCanale;
	}
	
	public static boolean isCanaleInUsoRegistro(CanaleConfigurazione canale, 
			ConfigurazioneCore confCore, ConfigurazioneHelper confHelper, StringBuilder inUsoMessage, String newLine) throws Exception {

		// Controllo che il canale non sia in uso
		Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new Hashtable<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !confHelper.isModalitaCompleta();
		boolean canaleInUso  = confCore.isCanaleInUsoRegistro(canale, whereIsInUso, normalizeObjectIds);

		if (canaleInUso) {

			inUsoMessage.append(DBOggettiInUsoUtils.toString(canale, whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		}  

		return canaleInUso;
	}
}
