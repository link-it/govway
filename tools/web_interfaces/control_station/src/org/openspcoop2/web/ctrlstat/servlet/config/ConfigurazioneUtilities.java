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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.utils.ConfigurazionePdDUtils;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
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

	private static String _getAttivazionePolicyTipo(AttivazionePolicy attivazionePolicy) {
		String tipo = "";
		if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getRuoloPorta()!=null && 
				StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getNomePorta())) {
			tipo = attivazionePolicy.getFiltro().getRuoloPorta().getValue()+"_"+attivazionePolicy.getFiltro().getNomePorta();
		}
		else {
			tipo = "globale";
		}
		return tipo;
	}
	private static String _buildLabelApi(Allarme allarme, ConfigurazioneHelper confHelper, ConfigurazioneCore confCore) {
		if(allarme.getFiltro()!=null && allarme.getFiltro().isEnabled() && 
				allarme.getFiltro().getRuoloPorta()!=null && 
				allarme.getFiltro().getNomePorta()!=null && StringUtils.isNotEmpty(allarme.getFiltro().getNomePorta())) {
			switch (allarme.getFiltro().getRuoloPorta()) {
			case APPLICATIVA:
				try {
					PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(confCore);
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(allarme.getFiltro().getNomePorta());
					PortaApplicativa paFound = porteApplicativeCore.getPortaApplicativa(idPA);
					MappingErogazionePortaApplicativa mappingPA = porteApplicativeCore.getMappingErogazionePortaApplicativa(paFound);
					String labelErogazione = confHelper.getLabelIdServizio(mappingPA.getIdServizio());
					if(!mappingPA.isDefault()) {
						labelErogazione = labelErogazione+" (gruppo:"+mappingPA.getDescrizione()+")";
					}
					return labelErogazione;
				}catch(Throwable t) {
					ControlStationCore.getLog().error("Errore durante l'identificazione dell'erogazione: "+t.getMessage(),t);
				}
				break;
			case DELEGATA:
				try {
					PorteDelegateCore porteDelegateCore = new PorteDelegateCore(confCore);
					SoggettiCore soggettiCore = new SoggettiCore(confCore);
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(allarme.getFiltro().getNomePorta());
					PortaDelegata pdFound = porteDelegateCore.getPortaDelegata(idPD);
					MappingFruizionePortaDelegata mappingPD = porteDelegateCore.getMappingFruizionePortaDelegata(pdFound);
					String labelFruizione = confHelper.getLabelServizioFruizione(soggettiCore.getProtocolloAssociatoTipoSoggetto(mappingPD.getIdFruitore().getTipo()),
							mappingPD.getIdFruitore(), mappingPD.getIdServizio());
					if(!mappingPD.isDefault()) {
						labelFruizione = labelFruizione+" (gruppo:"+mappingPD.getDescrizione()+")";
					}
					return labelFruizione;
				}catch(Throwable t) {
					ControlStationCore.getLog().error("Errore durante l'identificazione della fruizione: "+t.getMessage(),t);
				}
				break;
			default:
				break;
			}
		}
		return null;
	}
	public static void deleteAttivazionePolicy(List<AttivazionePolicy> policies , ConfigurazioneHelper confHelper, ConfigurazioneCore confCore, String userLogin,
			StringBuilder inUsoMessage, String newLine, List<AttivazionePolicy> policiesRimosse) throws DriverControlStationException, DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException, ControlStationCoreException, Exception {
		
		StringBuilder deleteMessage = new StringBuilder();
		
		boolean oneOnlyConfig = false;
		if(policies.size()==1) {
			oneOnlyConfig=true;
		}
		else {
			oneOnlyConfig = true;
			String tipo = _getAttivazionePolicyTipo(policies.get(0));
			for (int i = 1; i < policies.size(); i++) {
				AttivazionePolicy attivazionePolicy_i = policies.get(i);
				String tipo_i = _getAttivazionePolicyTipo(attivazionePolicy_i);
				if(!tipo_i.equals(tipo)) {
					oneOnlyConfig = false;
					break;
				}
			}
		}
		
		for (AttivazionePolicy attivazionePolicy : policies) {
			
			boolean delete = true;
			
			boolean policyApi = attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().isEnabled() &&
					attivazionePolicy.getFiltro().getRuoloPorta()!=null && 
					attivazionePolicy.getFiltro().getNomePorta()!=null && StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getNomePorta());
			if("DimensioneMassimaMessaggi".equals(attivazionePolicy.getAlias()) && !policyApi) {
				if(deleteMessage.length()>0){
					deleteMessage.append(newLine);
				}
				StringBuilder bf = new StringBuilder();
				bf.append(newLine);
				bf.append("La policy '"+attivazionePolicy.getAlias()+"' non è eliminabile; è consentito modificarne i valori di soglia o disabilitarla");
				deleteMessage.append(bf.toString());
				delete = false;
			}
			
			if(delete && confCore.isConfigurazioneAllarmiEnabled()){
				
				RuoloPorta ruoloPorta = null;
				String nomePorta = null;
				if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getRuoloPorta()!=null && 
						StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getNomePorta())) {
					switch (attivazionePolicy.getFiltro().getRuoloPorta()) {
					case DELEGATA:
						ruoloPorta = RuoloPorta.DELEGATA;
						break;
					case APPLICATIVA:
						ruoloPorta = RuoloPorta.APPLICATIVA;
						break;
					default:
						break;
					}
					nomePorta = attivazionePolicy.getFiltro().getNomePorta();
				}
				
				// throw new NotImplementedException("Da implementare quando verranno aggiunti gli allarmi."); 
				List<Allarme> allarmiObjectUtilizzanoPolicy = confCore.allarmiForPolicyRateLimiting(attivazionePolicy.getIdActivePolicy(),ruoloPorta,nomePorta);
				List<String> allarmiUtilizzanoPolicy = null;
				if(allarmiObjectUtilizzanoPolicy!=null && !allarmiObjectUtilizzanoPolicy.isEmpty()) {
					allarmiUtilizzanoPolicy = new ArrayList<String>();
					for (Allarme allarme : allarmiObjectUtilizzanoPolicy) {
						String nomeAllarme = allarme.getAlias();
						if(!oneOnlyConfig) {
							String labelApi = _buildLabelApi(allarme, confHelper, confCore);
							if(labelApi!=null) {
								nomeAllarme = nomeAllarme + " - "+ labelApi;
							}
						}
						allarmiUtilizzanoPolicy.add(nomeAllarme);				
					}
				}
				
				if(allarmiUtilizzanoPolicy!=null && allarmiUtilizzanoPolicy.size()>0){
					StringBuilder bf = new StringBuilder();
					bf.append(newLine);
					bf.append("La policy '"+attivazionePolicy.getAlias()+"' risulta utilizzata da ");
					bf.append(allarmiUtilizzanoPolicy.size());
					if(allarmiUtilizzanoPolicy.size()<2){
						bf.append(" allarme");
					}else{
						bf.append(" allarmi");
					}
					if(oneOnlyConfig) {
						boolean globale = !(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getRuoloPorta()!=null && 
								StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getNomePorta()));
						if(!globale) {
							bf.append(RuoloPolicy.APPLICATIVA.equals(attivazionePolicy.getFiltro().getRuoloPorta())?" configurati sull'erogazione":" configurati sulla fruizione");
						}
					}
					bf.append(": ");
					for (int j = 0; j < allarmiUtilizzanoPolicy.size(); j++) {
						//if(j>0){
						bf.append(newLine);
						//}
						bf.append("- ").append(allarmiUtilizzanoPolicy.get(j));
					}
					
					if(deleteMessage.length()>0){
						deleteMessage.append(newLine);
					}
					deleteMessage.append(bf.toString());
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
	
	public static void deleteAllarmi(List<Allarme> allarmiToRemove , ConfigurazioneHelper confHelper, ConfigurazioneCore confCore, String userLogin,
			StringBuilder inUsoMessage, String newLine, List<Allarme> allarmiRimossi) throws DriverControlStationException, DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException, ControlStationCoreException, Exception {
		
		StringBuilder deleteMessage = new StringBuilder();
		
		for (Allarme allarmeDaEliminare : allarmiToRemove) {
			
			boolean delete = true;
			
			List<ConfigurazionePolicy> policyObjectUtilizzanoAllarme = confCore.configurazioneControlloTrafficoConfigurazionePolicyList_conApplicabilitaAllarme(allarmeDaEliminare.getNome());
			List<String> policyUtilizzanoAllarme = null;
			if(policyObjectUtilizzanoAllarme!=null && !policyObjectUtilizzanoAllarme.isEmpty()) {
				policyUtilizzanoAllarme = new ArrayList<String>();
				for (ConfigurazionePolicy policy : policyObjectUtilizzanoAllarme) {
					String nomePolicy = policy.getIdPolicy();
					policyUtilizzanoAllarme.add(nomePolicy);				
				}
			}
			
			if(policyUtilizzanoAllarme!=null && policyUtilizzanoAllarme.size()>0){
				StringBuilder bf = new StringBuilder();
				bf.append(newLine);
				bf.append("L'allarme '"+allarmeDaEliminare.getAlias()+"' risulta utilizzata da ");
				bf.append(policyUtilizzanoAllarme.size());
				bf.append(" policy del Controllo del Traffico (criterio di applicabilità)");
				bf.append(": ");
				for (int j = 0; j < policyUtilizzanoAllarme.size(); j++) {
					//if(j>0){
					bf.append(newLine);
					//}
					bf.append("- ").append(policyUtilizzanoAllarme.get(j));
				}
				
				if(deleteMessage.length()>0){
					deleteMessage.append(newLine);
				}
				deleteMessage.append(bf.toString());
				delete = false;
			}
		
			if(delete) {
				// aggiungo elemento alla lista di quelli da cancellare
				allarmiRimossi.add(allarmeDaEliminare);
			}
		}
		
		if(deleteMessage.length()>0){
			if(allarmiRimossi.size()>0){
				inUsoMessage.append("Non è stato possibile completare l'eliminazione di tutti gli elementi selezionati:"+newLine+deleteMessage.toString());
			}
			else{
				inUsoMessage.append("Non è stato possibile eliminare gli elementi selezionati:"+newLine+deleteMessage.toString());
			}
		}
		
	}
}
