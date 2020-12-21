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

package org.openspcoop2.core.controllo_traffico.utils;

import java.text.SimpleDateFormat;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStato;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.utils.date.DateUtils;

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
			
			if(groupBy.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave-Tipo: ");
				bf.append(groupBy.getInformazioneApplicativaTipo());
				bf.append(", Chiave-Criterio: ");
				bf.append(groupBy.getInformazioneApplicativaNome());
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
			}
			
			if(groupBy.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave-Tipo: ");
				bf.append(groupBy.getInformazioneApplicativaTipo());
				bf.append(", Chiave-Criterio: ");
				bf.append(groupBy.getInformazioneApplicativaNome());
				bf.append(", Chiave-Valore: ");
				bf.append(datiGroupBy.getValoreKey());
			}

		}
		else{
			bf.append("Disabilitato");
		}

		return bf.toString();
	}
	
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
	
	
	public static String buildIdConfigurazioneEventoPerPolicy(ActivePolicy activePolicy, IDUnivocoGroupByPolicy datiGroupBy, String API) {
		// L'obiettivo è di generare un evento differente per ogni raggruppamento violato di una stessa policy
		// All'interno di una stessa policy ci possono essere gruppi che non sono violati ed altri che lo sono
		// Ad esempio se si raggruppa per soggetto fruitore, ci potranno essere soggetti che la violano, altri no.
		// Si vuole un evento per ogni soggetto che viola la policy
		boolean printDati = true;
		String idPolicyConGruppo = PolicyUtilities.getNomeActivePolicy(activePolicy.getInstanceConfiguration().getAlias(), activePolicy.getInstanceConfiguration().getIdActivePolicy()); 
		if(API!=null && !"".equals(API)) {
			idPolicyConGruppo = idPolicyConGruppo + " - API: "+API;
		}
		if(activePolicy.getInstanceConfiguration().getGroupBy().isEnabled()){
			String toStringRaggruppamentoConDatiIstanza = PolicyUtilities.toStringGroupBy(activePolicy.getInstanceConfiguration().getGroupBy(), datiGroupBy, printDati);
			idPolicyConGruppo = idPolicyConGruppo +
					org.openspcoop2.core.controllo_traffico.constants.Costanti.SEPARATORE_IDPOLICY_RAGGRUPPAMENTO+
					toStringRaggruppamentoConDatiIstanza;
		}
		return idPolicyConGruppo;
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
	
	public static IDUnivocoGroupByPolicy extractIDUnivocoGroupByPolicyFromIdConfigurazioneEvento(String idConfigurazioneEvento) {
		return toIDUnivocoGroupByPolicy(extractIDUnivocoGroupByPolicyFromIdConfigurazioneEventoAsString(idConfigurazioneEvento));
	}
	
	public static String getNomeActivePolicy(String alias, String id) {
		if(alias==null || "".equals(alias)) {
			return id.replace(":", "_");
		}
		else {
			return alias;
		}
	}
}
