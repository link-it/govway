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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;


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
			StringBuilder inUsoMessage, List<String> azioni, String userLogin) throws Exception {
	
		ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);
		StringBuilder bfTrasformazioni = new StringBuilder();
		StringBuilder bfCT = new StringBuilder();
		
		for (int i = 0; i < azioni.size(); i++) {

			String azione = azioni.get(i);
			
			boolean usedInTrasformazioni = false;
			if(pa.getTrasformazioni()!=null && pa.getTrasformazioni().sizeRegolaList()>0) {
				for (TrasformazioneRegola trasformazioneRegola : pa.getTrasformazioni().getRegolaList()) {
					if(trasformazioneRegola.getApplicabilita()!=null && trasformazioneRegola.getApplicabilita().getAzioneList()!=null &&
							trasformazioneRegola.getApplicabilita().getAzioneList().contains(azione)) {
						usedInTrasformazioni = true;
						break;
					}
				}
			}
			
			if(confCore.usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy.APPLICATIVA, pa.getNome(), azione)) {
				if(bfCT.length()>0) {
					bfCT.append(",");
				}
				bfCT.append(azione);
			}
			else if(usedInTrasformazioni) {
				if(bfTrasformazioni.length()>0) {
					bfTrasformazioni.append(",");
				}
				bfTrasformazioni.append(azione);
			}
			else {
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
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè risultano utilizzate in configurazione di Rate Limiting: "+bfCT.toString()); 
		}
		else if(bfTrasformazioni.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè utilizzate in criteri di applicabilità di una Trasformazione: "+bfTrasformazioni.toString()); 
		}
		else {
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
		}
		
	}
	
}
