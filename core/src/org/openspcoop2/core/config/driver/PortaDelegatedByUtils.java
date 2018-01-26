/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.core.config.driver;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.id.IDServizio;

/**
 * PortaDelegatedByUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13507 $, $Date: 2017-12-13 16:55:45 +0100 (Wed, 13 Dec 2017) $
 */
public class PortaDelegatedByUtils {

	public static List<PortaApplicativa> filter(List<PortaApplicativa> list, IDServizio idServizio) throws DriverConfigurazioneNotFound {
		List<String> paDaEliminare = new ArrayList<String>();
		for (PortaApplicativa portaApplicativa : list) {
			if(portaApplicativa.getAzione()!=null && PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(portaApplicativa.getAzione().getIdentificazione())) {
				boolean actionPresente = false;
				for (String azione : portaApplicativa.getAzione().getAzioneDelegataList()) {
					if(azione.equals(idServizio.getAzione())) {
						actionPresente = true;
						break;
					}
				}
				if(actionPresente==false) {
					paDaEliminare.add(portaApplicativa.getNome());
				}
				else {
					paDaEliminare.add(portaApplicativa.getAzione().getNomePortaDelegante());
				}
			}
		}
		
		if(paDaEliminare.size()<=0) {
			return list;
		}
		List<PortaApplicativa> newList = new ArrayList<PortaApplicativa>();
		for (PortaApplicativa portaApplicativa : list) {
			if(paDaEliminare.contains(portaApplicativa.getNome())==false) {
				newList.add(portaApplicativa);
			}
		}
		if(newList.size()>0) {
			return newList;
		}
		else {
			throw new DriverConfigurazioneNotFound("Porte Applicative non trovate");
		}
	}
	
}
