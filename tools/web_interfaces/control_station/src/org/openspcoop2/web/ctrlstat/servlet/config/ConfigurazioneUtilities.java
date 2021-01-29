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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.utils.ConfigurazionePdDUtils;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneUtilities {

	public static boolean alreadyExists(TipoOperazione tipoOperazione, ConfigurazioneCore confCore, ConfigurazioneHelper confHelper, 
			AttivazionePolicy policy, InfoPolicy infoPolicy, RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding,
			StringBuilder existsMessage, String newLine, String modalita) throws Exception {
		if(infoPolicy!=null){
			
			String perApi = "globale";
			if(ruoloPorta!=null && nomePorta!=null && !"".equals(nomePorta)) {
				perApi = "per l'API";
			}
			
			AttivazionePolicy p = null;
			try {
				p = confCore.getPolicy(policy.getIdPolicy(),policy.getFiltro(), policy.getGroupBy(),
						ruoloPorta, nomePorta);
			}catch(DriverControlStationNotFound e) {
				//ignore
			}
			if(p!=null){
				if(TipoOperazione.ADD.equals(tipoOperazione) ||	(p.getId()!=null &&	policy.getId()!=null &&	p.getId().longValue()!=policy.getId().longValue())){
					
					String prefisso = "Esiste già una attivazione "+perApi+" della policy '"+
							policy.getIdPolicy()+"' ";
					if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN.equals(modalita)) {
						prefisso = "Esiste già una policy "+perApi+" con i criteri indicati ";
					}
					
					String messaggio = prefisso+newLine+
							"e"+newLine+
							"Raggruppamento: "+ confHelper.toStringCompactGroupBy(policy.getGroupBy(),ruoloPorta,nomePorta,serviceBinding)+newLine+
							"e"+newLine+	
							"Filtro: "+ confHelper.toStringCompactFilter(policy.getFiltro(),ruoloPorta,nomePorta,serviceBinding);
					existsMessage.append(messaggio);
					return true; 
				}
			}
			
			AttivazionePolicy pAlias = null;
			if(policy.getAlias()!=null && !"".equals(policy.getAlias())) {
				try {
					pAlias = confCore.getPolicyByAlias(policy.getAlias(),
							ruoloPorta, nomePorta);
				}catch(DriverControlStationNotFound e) {
					//ignore
				}
				if(pAlias!=null){
					if(TipoOperazione.ADD.equals(tipoOperazione) || (pAlias.getId()!=null && policy.getId()!=null && pAlias.getId().longValue()!=policy.getId().longValue())){
						String messaggio = "Esiste già una policy "+perApi+" con "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS+" '"+policy.getAlias()+"'";
						existsMessage.append(messaggio);
						return true; 
					}
				}
			}
		}
		
		return false;
	}

	public static void deleteAttivazionePolicy(List<AttivazionePolicy> policies , ConfigurazioneHelper confHelper, ConfigurazioneCore confCore, String userLogin,
			StringBuilder inUsoMessage, String newLine, List<AttivazionePolicy> policiesRimosse) throws DriverControlStationException, DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException, ControlStationCoreException, Exception {
		
		StringBuilder deleteMessage = new StringBuilder();
		
		for (AttivazionePolicy attivazionePolicy : policies) {
			
			boolean delete = true;
			if(confHelper.isAllarmiModuleEnabled()){
				
				// throw new NotImplementedException("Da implementare quando verranno aggiunti gli allarmi."); 
				List<String> allarmiUtilizzanoPolicy = null;
//				List<String> allarmiUtilizzanoPolicy = confHelper.getAllIdAllarmiUseActivePolicy(policy.getIdActivePolicy());
				allarmiUtilizzanoPolicy = new ArrayList<String>();
				allarmiUtilizzanoPolicy.add("Allarme1");
				
				if(allarmiUtilizzanoPolicy!=null && allarmiUtilizzanoPolicy.size()>0){
					StringBuilder bf = new StringBuilder();
					bf.append("La policy '"+attivazionePolicy.getIdActivePolicy()+"' risulta utilizzata da ");
					bf.append(allarmiUtilizzanoPolicy.size());
					if(allarmiUtilizzanoPolicy.size()<2){
						bf.append(" allarme: ");
					}else{
						bf.append(" allarmi: ");
					}
					for (int j = 0; j < allarmiUtilizzanoPolicy.size(); j++) {
						if(j>0){
							bf.append(", ");
						}
						bf.append(allarmiUtilizzanoPolicy.get(j));
					}
					
					if(deleteMessage.length()>0){
						deleteMessage.append(newLine);
					}
					deleteMessage.append("- "+bf.toString());
					delete = false;
				}
			}

			if(delete) {
				// aggiungo elemento alla lista di quelli da cancellare
				policiesRimosse.add(attivazionePolicy);
			}
			
		}
		
		if(deleteMessage.length()>0){
			if(policiesRimosse.size()>0){
				inUsoMessage.append("Non è stato possibile completare l'eliminazione di tutti gli elementi selezionati:"+newLine+deleteMessage.toString());
			}
			else{
				inUsoMessage.append("Non è stato possibile eliminare gli elementi selezionati:"+newLine+deleteMessage.toString());
			}
		}
		
		if(policiesRimosse .size() > 0) {
//		 	eseguo delete
			confCore.performDeleteOperation(userLogin, confHelper.smista(), (Object[]) policiesRimosse.toArray(new AttivazionePolicy[1])); 
		}
			
	}
	
	public static void updatePosizioneAttivazionePolicy(ConfigurazioneCore confCore, InfoPolicy infoPolicy, AttivazionePolicy policy,
			RuoloPolicy ruoloPorta, String nomePorta) throws Exception {
		confCore.updatePosizioneAttivazionePolicy(infoPolicy, policy,
				ruoloPorta, nomePorta);
	}
	
	public static int getProssimaPosizioneUrlInvocazioneRegola(Configurazione config) {
		return ConfigurazionePdDUtils.getProssimaPosizioneUrlInvocazioneRegola(config);
	}
	
	public static AllarmeHistory createAllarmeHistory(Allarme allarme, String userLogin) {
		// registro la modifica
		AllarmeHistory history = new AllarmeHistory();
		history.setEnabled(allarme.getEnabled());
		history.setAcknowledged(allarme.getAcknowledged());
		history.setDettaglioStato(allarme.getDettaglioStato());
		IdAllarme idConfigurazioneAllarme = new IdAllarme();
		idConfigurazioneAllarme.setNome(allarme.getNome());
		history.setIdAllarme(idConfigurazioneAllarme);
		history.setStato(allarme.getStato());
		history.setTimestampUpdate(allarme.getLasttimestampUpdate());
		history.setUtente(userLogin);
		return history;
	}
}
