/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;


/**
 * PorteApplicativeUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeUtilities {

	public static void deletePortaApplicativaAzioni(PortaApplicativa pa, PorteApplicativeCore porteApplicativeCore, PorteApplicativeHelper porteApplicativeHelper, 
			StringBuffer inUsoMessage, List<String> azioni) throws Exception {
	
		ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);
		StringBuffer bfCT = new StringBuffer();
		
		for (int i = 0; i < azioni.size(); i++) {

			String azione = azioni.get(i);
			
			if(confCore.checkConfigurazioneControlloTrafficoAttivazionePolicyListUsedAction(RuoloPolicy.APPLICATIVA, pa.getNome(), azione)) {
				if(bfCT.length()>0) {
					bfCT.append(",");
				}
				bfCT.append(azione);
			}else {
				for (int j = 0; j < pa.getAzione().sizeAzioneDelegataList(); j++) {
					String azioneDelegata = pa.getAzione().getAzioneDelegata(j);
					if (azione.equals(azioneDelegata)) {
						pa.getAzione().removeAzioneDelegata(j);
						break;
					}
				}
			}
		}
		
		// non posso eliminare tutte le azioni
		if(pa.getAzione().sizeAzioneDelegataList() == 0) {
			inUsoMessage.append(PorteApplicativeCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_ELIMINARE_TUTTE_LE_AZIONI_ASSOCIATE_ALLA_CONFIGURAZIONE); 
		}
		else if(bfCT.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè le seguenti azioni risultano utilizzate in configurazione di Rate Limiting: "+bfCT.toString()); 
		}
		else {
			String userLogin = ServletUtils.getUserLoginFromSession(porteApplicativeHelper.getSession());
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
		}
		
	}
	
}
