/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.controllo_traffico.utils;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStato;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;

/**
 * PolicyUtilities 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyUtilities {

	public static String toString(Integer statoAllarme){
		
		StringBuilder bf = new StringBuilder();
		
		if(statoAllarme==null){
			bf.append("N.C.");
		}
		else if(statoAllarme==0){
			bf.append("OK");
		}
		else if(statoAllarme==1){
			bf.append("Warning");
		}
		else if(statoAllarme==2){
			bf.append("Error");
		}
		else{
			bf.append("N.C.");
		}
		
		return bf.toString();
	}
	
	public static String toString(RisultatoStato statoAllarme){
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		
		StringBuilder bf = new StringBuilder(toString(statoAllarme.getStato()));
		
		bf.append(", ultimo aggiornamento:").
		append(dateFormat.format(statoAllarme.getDateCheck())).
		append(")");
		
		return bf.toString();
	}



	public static String toStringFilter(AttivazionePolicyFiltro filtro) {
		StringBuilder bf = new StringBuilder("");
		if(filtro.isEnabled()){

			if( (filtro.getRuoloPorta()!=null && !RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
					bf.append("Ruolo:Fruitore");
				}
				else if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta())){
					bf.append("Ruolo:Erogatore");
				}
			}
			
			if( !(filtro.getNomePorta()==null || "".equals(filtro.getNomePorta())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Porta:");
				bf.append(filtro.getNomePorta());
			}

			if( !(filtro.getProtocollo()==null || "".equals(filtro.getProtocollo())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Protocollo:");
				bf.append(filtro.getProtocollo());
			}
			
			if(filtro.getRuoloErogatore()!=null && !"".equals(filtro.getRuoloErogatore())){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("RuoloErogatore:");
				bf.append(filtro.getRuoloErogatore());
			}
						
			if( !( (filtro.getTipoErogatore()==null || "".equals(filtro.getTipoErogatore())) 
					||
					(filtro.getNomeErogatore()==null || "".equals(filtro.getNomeErogatore())) ) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Erogatore:");
				bf.append(filtro.getTipoErogatore()+"/"+filtro.getNomeErogatore());
			}

			if( !(filtro.getTag()==null || "".equals(filtro.getTag())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Tag:");
				bf.append(filtro.getTag());
			}
			
			if( !( (filtro.getTipoServizio()==null || "".equals(filtro.getTipoServizio())) 
					||
					(filtro.getNomeServizio()==null || "".equals(filtro.getNomeServizio())) ) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Servizio:");
				bf.append(filtro.getTipoServizio()+"/"+filtro.getNomeServizio());
				if(filtro.getVersioneServizio()!=null && filtro.getVersioneServizio()>0) {
					bf.append("/v").append(filtro.getVersioneServizio());
				}
			}
			
			if( !(filtro.getAzione()==null || "".equals(filtro.getAzione())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Azione:");
				bf.append(filtro.getAzione());
			}
			
			if( !(filtro.getServizioApplicativoErogatore()==null || "".equals(filtro.getServizioApplicativoErogatore())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAErogatore:");
				bf.append(filtro.getServizioApplicativoErogatore());
			}
			
			if(filtro.getRuoloFruitore()!=null && !"".equals(filtro.getRuoloFruitore())){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("RuoloFruitore:");
				bf.append(filtro.getRuoloFruitore());
			}
			
			if( !( (filtro.getTipoFruitore()==null || "".equals(filtro.getTipoFruitore())) 
					||
					(filtro.getNomeFruitore()==null || "".equals(filtro.getNomeFruitore())) ) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Fruitore:");
				bf.append(filtro.getTipoFruitore()+"/"+filtro.getNomeFruitore());
			}

			if( !(filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAFruitore:");
				bf.append(filtro.getServizioApplicativoFruitore());
			}
			
			if(filtro.getTokenClaims()!=null){
				Properties properties = PropertiesUtilities.convertTextToProperties(filtro.getTokenClaims());
				if(properties!=null && properties.size()>0) {
					for (Object o : properties.keySet()) {
						if(o!=null && o instanceof String) {
							String key = (String) o;
							String value = properties.getProperty(key);
							if(bf.length()>0){
								bf.append(", ");
							}
							bf.append("Token-").append(key).append(":");
							bf.append(value);			
						}
					}
				}
			}
			
			if(filtro.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave:");
				bf.append(filtro.getInformazioneApplicativaTipo());
			}

		}
		else{
			bf.append("Disabilitato");
		}

		return bf.toString();
	}
	
	
	public static String toStringGroupBy(AttivazionePolicyRaggruppamento groupBy,IDUnivocoGroupByPolicy datiGroupBy, boolean printDati) {
		if(printDati) {
			return _toStringGroupBy(groupBy, datiGroupBy);
		}
		StringBuilder bf = new StringBuilder("");
		if(groupBy.isEnabled()){

			if(groupBy.isRuoloPorta()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Tipologia");
			}
			
			if(groupBy.isProtocollo()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Modalità");
			}
			
			if(groupBy.isErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Erogatore");
			}
			
			if(groupBy.isServizio()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Servizio");
			}
			
			if(groupBy.isAzione()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Azione");
			}
			
			if(groupBy.isServizioApplicativoErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAErogatore");
			}
			
			if(groupBy.isFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Fruitore");
			}
			
			if(groupBy.isServizioApplicativoFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAFruitore");
			}
			
			if(groupBy.getToken()!=null && StringUtils.isNotEmpty(groupBy.getToken())){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Token: ").append(groupBy.getToken());
			}
			
			if(groupBy.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave-Tipo: ");
				bf.append(groupBy.getInformazioneApplicativaTipo());
				if(groupBy.getInformazioneApplicativaNome()!=null) {
					bf.append(", Chiave-Criterio: ");
					bf.append(groupBy.getInformazioneApplicativaNome());
				}
				if(datiGroupBy!=null) {
					bf.append(", Chiave-Valore: ");
					bf.append(datiGroupBy.getValoreKey());
				}
			}

		}
		else{
			bf.append("Disabilitato");
		}

		return bf.toString();
	}
	
	
	private static String _toStringGroupBy(AttivazionePolicyRaggruppamento groupBy,IDUnivocoGroupByPolicy datiGroupBy) {
		StringBuilder bf = new StringBuilder("");
		if(groupBy.isEnabled()){

			if(groupBy.isRuoloPorta()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Tipologia: ");
				bf.append(datiGroupBy.getRuoloPorta());
			}
			
			if(groupBy.isProtocollo()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Protocollo: ");
				bf.append(datiGroupBy.getProtocollo());
			}
			
			if(groupBy.isErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Erogatore: ");
				bf.append(datiGroupBy.getErogatore());
			}
			
			if(groupBy.isServizio()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Servizio: ");
				bf.append(datiGroupBy.getServizio());
			}
			
			if(groupBy.isAzione()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Azione: ");
				bf.append(datiGroupBy.getAzione());
			}
			
			if(groupBy.isServizioApplicativoErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAErogatore: ");
				bf.append(datiGroupBy.getServizioApplicativoErogatore());
			}
			
			if(groupBy.isFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Fruitore: ");
				bf.append(datiGroupBy.getFruitore());
			}
			
			if(groupBy.isServizioApplicativoFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAFruitore: ");
				bf.append(datiGroupBy.getServizioApplicativoFruitore());
				
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("ApplicativoToken: ");
				bf.append(datiGroupBy.getServizioApplicativoToken());
			}
			
			if(groupBy.getToken()!=null && StringUtils.isNotEmpty(groupBy.getToken())){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Token: ").append(groupBy.getToken());
			}
			
			if(groupBy.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave-Tipo: ");
				bf.append(groupBy.getInformazioneApplicativaTipo());
				if(groupBy.getInformazioneApplicativaNome()!=null) {
					bf.append(", Chiave-Criterio: ");
					bf.append(groupBy.getInformazioneApplicativaNome());
				}
				bf.append(", Chiave-Valore: ");
				bf.append(datiGroupBy.getValoreKey());
			}

		}
		else{
			bf.append("Disabilitato");
		}

		return bf.toString();
	}
	
	/*
	public static IDUnivocoGroupByPolicy toIDUnivocoGroupByPolicy(String stringGroupBy) {
		IDUnivocoGroupByPolicy id = new IDUnivocoGroupByPolicy();
		if(stringGroupBy==null || stringGroupBy.equals("") || stringGroupBy.equals("Disabilitato")) {
			return id;
		}
		
		String [] split = stringGroupBy.split(",");
		for (int i = 0; i < split.length; i++) {
			String tmp = split[i].trim();
			
			if(tmp.startsWith("Tipologia: ")) {
				id.setRuoloPorta(tmp.substring("Tipologia: ".length()));
			}
			else if(tmp.startsWith("Protocollo: ")) {
				id.setProtocollo(tmp.substring("Protocollo: ".length()));
			}
			else if(tmp.startsWith("Erogatore: ")) {
				id.setErogatore(tmp.substring("Erogatore: ".length()));
			}
			else if(tmp.startsWith("Servizio: ")) {
				id.setServizio(tmp.substring("Servizio: ".length()));
			}
			else if(tmp.startsWith("Azione: ")) {
				id.setAzione(tmp.substring("Azione: ".length()));
			}
			else if(tmp.startsWith("SAErogatore: ")) {
				id.setServizioApplicativoErogatore(tmp.substring("SAErogatore: ".length()));
			}
			else if(tmp.startsWith("Fruitore: ")) {
				id.setFruitore(tmp.substring("Fruitore: ".length()));
			}
			else if(tmp.startsWith("SAFruitore: ")) {
				id.setServizioApplicativoFruitore(tmp.substring("SAFruitore: ".length()));
			}
			else if(tmp.startsWith("ApplicativoToken: ")) {
				id.setServizioApplicativoToken(tmp.substring("ApplicativoToken: ".length()));
			}
			else if(tmp.startsWith("TokenSubject: ")) {
				id.setTokenSubject(tmp.substring("TokenSubject: ".length()));
			}
			else if(tmp.startsWith("TokenIssuer: ")) {
				id.setTokenIssuer(tmp.substring("TokenIssuer: ".length()));
			}
			else if(tmp.startsWith("TokenClientId: ")) {
				id.setTokenClientId(tmp.substring("TokenClientId: ".length()));
			}
			else if(tmp.startsWith("TokenUsername: ")) {
				id.setTokenUsername(tmp.substring("TokenUsername: ".length()));
			}
			else if(tmp.startsWith("TokenEMail: ")) {
				id.setTokenEMail(tmp.substring("TokenEMail: ".length()));
			}
			else if(tmp.startsWith("Chiave-Tipo: ")) {
				id.setTipoKey(tmp.substring("Chiave-Tipo: ".length()));
			}
			else if(tmp.startsWith("Chiave-Criterio: ")) {
				id.setNomeKey(tmp.substring("Chiave-Criterio: ".length()));
			}
			else if(tmp.startsWith("Chiave-Valore: ")) {
				id.setValoreKey(tmp.substring("Chiave-Valore: ".length()));
			}
		}
		
		return id;
	}
	*/
	
	public static String buildIdConfigurazioneEventoPerPolicy(ActivePolicy activePolicy, IDUnivocoGroupByPolicy datiGroupBy, String API) {
		AttivazionePolicy attivazionePolicy = activePolicy.getInstanceConfiguration();
		return buildIdConfigurazioneEventoPerPolicy(attivazionePolicy, datiGroupBy, API);
	}
	public static String buildIdConfigurazioneEventoPerPolicy(AttivazionePolicy attivazionePolicy, IDUnivocoGroupByPolicy datiGroupBy, String API) {
		// L'obiettivo è di generare un evento differente per ogni raggruppamento violato di una stessa policy
		// All'interno di una stessa policy ci possono essere gruppi che non sono violati ed altri che lo sono
		// Ad esempio se si raggruppa per soggetto fruitore, ci potranno essere soggetti che la violano, altri no.
		// Si vuole un evento per ogni soggetto che viola la policy
		boolean printDati = true;
		String idPolicyConGruppo = PolicyUtilities.getNomeActivePolicy(attivazionePolicy.getAlias(), attivazionePolicy.getIdActivePolicy()); 
		if(API!=null && !"".equals(API)) {
			idPolicyConGruppo = idPolicyConGruppo + " - API: "+API;
		}
		if(attivazionePolicy.getGroupBy().isEnabled()){
			String toStringRaggruppamentoConDatiIstanza = PolicyUtilities.toStringGroupBy(attivazionePolicy.getGroupBy(), datiGroupBy, printDati);
			idPolicyConGruppo = idPolicyConGruppo +
					org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO+
					toStringRaggruppamentoConDatiIstanza;
		}
		return idPolicyConGruppo;
	}
	
	public static final String GLOBALE_PROPERTY = "globale:";
	public static final String ID_ACTIVE_POLICY_PROPERTY = "idActivePolicy:";
	public static final String RUOLO_PORTA_PROPERTY = "ruoloPorta:";
	public static final String NOME_PORTA_PROPERTY = "nomePorta:";
	
	public static String buildConfigurazioneEventoPerPolicy(ActivePolicy activePolicy, boolean policyGlobale) {
		AttivazionePolicy attivazionePolicy = activePolicy.getInstanceConfiguration();
		return buildConfigurazioneEventoPerPolicy(attivazionePolicy, policyGlobale);
	}
	public static String buildConfigurazioneEventoPerPolicy(AttivazionePolicy attivazionePolicy, boolean policyGlobale) {
		StringBuilder sb = new StringBuilder();
		sb.append(GLOBALE_PROPERTY).append(policyGlobale);
		sb.append("\n").append(ID_ACTIVE_POLICY_PROPERTY).append(attivazionePolicy.getIdActivePolicy());
		if(!policyGlobale) {
			if(attivazionePolicy.getFiltro()!=null &&
					attivazionePolicy.getFiltro().getNomePorta()!=null && 
					StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getNomePorta()) && 
					attivazionePolicy.getFiltro().getRuoloPorta()!=null) {
				String nomePorta = attivazionePolicy.getFiltro().getNomePorta();
				String ruoloPorta = attivazionePolicy.getFiltro().getRuoloPorta().getValue();
				sb.append("\n").append(RUOLO_PORTA_PROPERTY).append(ruoloPorta);
				sb.append("\n").append(NOME_PORTA_PROPERTY).append(nomePorta);
			}
		}
		return sb.toString();
	}
	
	public static boolean isConfigurazioneEventoPerPolicy(String configurazione) {
		return configurazione!=null && configurazione.startsWith(GLOBALE_PROPERTY);
	}
	public static boolean isConfigurazioneEventoPerPolicy_policyGlobale(String configurazione) {
		if(isConfigurazioneEventoPerPolicy(configurazione)) {
			String [] tmp = configurazione.split("\n");
			if(tmp!=null && tmp.length>0) {
				String firstLine = tmp[0];
				if(firstLine!=null && firstLine.startsWith(GLOBALE_PROPERTY) && firstLine.length()>GLOBALE_PROPERTY.length()) {
					String s = firstLine.substring(GLOBALE_PROPERTY.length());
					return Boolean.valueOf(s);
				}
			}
		}
		return false;
	}
	public static String getConfigurazioneEventoPerPolicy_idActivePolicy(String configurazione) {
		if(isConfigurazioneEventoPerPolicy(configurazione)) {
			String [] tmp = configurazione.split("\n");
			if(tmp!=null && tmp.length>1) {
				String firstLine = tmp[1];
				if(firstLine!=null && firstLine.startsWith(ID_ACTIVE_POLICY_PROPERTY) && firstLine.length()>ID_ACTIVE_POLICY_PROPERTY.length()) {
					String s = firstLine.substring(ID_ACTIVE_POLICY_PROPERTY.length());
					return s;
				}
			}
		}
		return null;
	}
	public static String getConfigurazioneEventoPerPolicy_ruoloPorta(String configurazione) {
		if(isConfigurazioneEventoPerPolicy(configurazione)) {
			String [] tmp = configurazione.split("\n");
			if(tmp!=null && tmp.length>2) {
				String firstLine = tmp[2];
				if(firstLine!=null && firstLine.startsWith(RUOLO_PORTA_PROPERTY) && firstLine.length()>RUOLO_PORTA_PROPERTY.length()) {
					String s = firstLine.substring(RUOLO_PORTA_PROPERTY.length());
					return s;
				}
			}
		}
		return null;
	}
	public static String getConfigurazioneEventoPerPolicy_nomePorta(String configurazione) {
		if(isConfigurazioneEventoPerPolicy(configurazione)) {
			String [] tmp = configurazione.split("\n");
			if(tmp!=null && tmp.length>3) {
				String firstLine = tmp[3];
				if(firstLine!=null && firstLine.startsWith(NOME_PORTA_PROPERTY) && firstLine.length()>NOME_PORTA_PROPERTY.length()) {
					String s = firstLine.substring(NOME_PORTA_PROPERTY.length());
					return s;
				}
			}
		}
		return null;
	}
	
	
	public static String extractIdPolicyFromIdConfigurazioneEvento(String idConfigurazioneEvento) {
		if(idConfigurazioneEvento.contains(org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO)) {
			return idConfigurazioneEvento.split(org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO)[0];
		}
		else {
			return idConfigurazioneEvento;
		}
	}
	
	public static String extractIDUnivocoGroupByPolicyFromIdConfigurazioneEventoAsString(String idConfigurazioneEvento) {
		String datiGruppo = null;
		if(idConfigurazioneEvento.contains(org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO)) {
			int indexOf = idConfigurazioneEvento.indexOf(org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO);
			datiGruppo = idConfigurazioneEvento.substring(indexOf+org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO.length());
		}
		return datiGruppo;
	}
	
	/*
	public static IDUnivocoGroupByPolicy extractIDUnivocoGroupByPolicyFromIdConfigurazioneEvento(String idConfigurazioneEvento) {
		return toIDUnivocoGroupByPolicy(extractIDUnivocoGroupByPolicyFromIdConfigurazioneEventoAsString(idConfigurazioneEvento));
	}
	*/
	
	public static String getNomeActivePolicy(String alias, String id) {
		if(alias==null || "".equals(alias)) {
			return id.replace(":", "_");
		}
		else {
			return alias;
		}
	}
}
