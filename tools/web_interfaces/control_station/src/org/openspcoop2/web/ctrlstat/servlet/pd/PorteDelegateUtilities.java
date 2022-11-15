/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;

/**
 * PorteDelegateUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateUtilities {

	public static void deletePortaDelegataAzioni(PortaDelegata portaDelegata, AccordoServizioParteSpecifica asps,
			PorteDelegateCore porteDelegateCore, PorteDelegateHelper porteDelegateHelper, 
			StringBuilder inUsoMessage, List<String> azioni, 
			String userLogin) throws Exception {
		
		String azioneGiaEsistente = portaDelegata.getAzione().getAzioneDelegata(0); // prendo la prima
		
		ConfigurazioneServizioAzione configAzioni = null; 
		boolean updateASPS = false;
		
		Fruitore fruitore = null;
		for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
			if(fruitoreCheck.getTipo().equals(portaDelegata.getTipoSoggettoProprietario()) && fruitoreCheck.getNome().equals(portaDelegata.getNomeSoggettoProprietario())) {
				fruitore = fruitoreCheck;
				break;
			}
		}
		if(fruitore==null) {
			throw new Exception("Fruitore con id '"+portaDelegata.getTipoSoggettoProprietario()+"/"+portaDelegata.getNomeSoggettoProprietario()+"' non trovata");
		}
		for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
			ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
			if(config!=null) {
				if(config.getAzioneList().contains(azioneGiaEsistente)) {
					configAzioni = config;
					break;
				}
			}
		}
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
		StringBuilder bfTrasformazioni = new StringBuilder();
		StringBuilder bfCT = new StringBuilder();
		
		for (int i = 0; i < azioni.size(); i++) {

			String azione = azioni.get(i);
			
			boolean usedInTrasformazioni = false;
			if(portaDelegata.getTrasformazioni()!=null && portaDelegata.getTrasformazioni().sizeRegolaList()>0) {
				for (TrasformazioneRegola trasformazioneRegola : portaDelegata.getTrasformazioni().getRegolaList()) {
					if(trasformazioneRegola.getApplicabilita()!=null && trasformazioneRegola.getApplicabilita().getAzioneList()!=null &&
							trasformazioneRegola.getApplicabilita().getAzioneList().contains(azione)) {
						usedInTrasformazioni = true;
						break;
					}
				}
			}
			
			if(confCore.usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy.DELEGATA, portaDelegata.getNome(), azione)) {
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
				for (int j = 0; j < portaDelegata.getAzione().sizeAzioneDelegataList(); j++) {
					String azioneDelegata = portaDelegata.getAzione().getAzioneDelegata(j);
					if (azione.equals(azioneDelegata)) {
						portaDelegata.getAzione().removeAzioneDelegata(j);
						break;
					}
				}
				
				if(configAzioni!=null) {
					for (int j = 0; j < configAzioni.sizeAzioneList(); j++) {
						if(configAzioni.getAzione(j).equals(azione)) {
							configAzioni.removeAzione(j);
							updateASPS = true;
							break;
						}
					}
				}
			}

		}
		
		// non posso eliminare tutte le azioni
		if(portaDelegata.getAzione().sizeAzioneDelegataList() == 0) {
			inUsoMessage.append(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_ELIMINARE_TUTTE_LE_AZIONI_ASSOCIATE_ALLA_CONFIGURAZIONE); 
		} 
		else if(bfCT.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè utilizzate in configurazione di Rate Limiting: "+bfCT.toString()); 
		}
		else if(bfTrasformazioni.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè utilizzate in criteri di applicabilità di una Trasformazione: "+bfTrasformazioni.toString()); 
		}
		else {
			
			List<Object> listaOggettiDaModificare = new ArrayList<Object>();
			
			listaOggettiDaModificare.add(portaDelegata);
			
			if(updateASPS) {
				listaOggettiDaModificare.add(asps);
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());
			
		}
		
	}
	
}
