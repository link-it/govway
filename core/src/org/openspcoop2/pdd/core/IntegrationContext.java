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


package org.openspcoop2.pdd.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;

/**
 * IntegrationContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationContext {

	/** Identificativo di correlazione applicativa */
	private String idCorrelazioneApplicativa;
	/** Identificativo di correlazione applicativa risposta */
	private String idCorrelazioneApplicativaRisposta;
	
	/** Identita' del servizio applicativo fruitore */
	private String servizioApplicativoFruitore = null;
	/** Identita' dei servizi applicativi erogatori */
	private List<String> serviziApplicativiErogatori = new ArrayList<String>();
	
	/** Indicazione se la gestione e' stateless/stateful (Null se non ancora definita) */
	private Boolean gestioneStateless;
	
	/**
	 * Tipo di autenticazione utilizzato
	 */
	private String tipoAutenticazione;
	
	/**
	 * Tipo di gestione token utilizzato
	 */
	private String tipoGestioneToken;
		
	/**
	 * Autenticazione opzionale
	 */
	private boolean autenticazioneOpzionale = false;

	/**
	 * Tipo di autorizzazione utilizzato
	 */
	private String tipoAutorizzazione;
	
	/**
	 * Tipo di autorizzazione per contenuto utilizzato
	 */
	private String tipoAutorizzazioneContenuto;
	
	/**
	 * Tipo di validazione contenuti
	 */
	private String tipoValidazioneContenuti;
	
	/**
	 * Tipo di processamento mtom della richiesta
	 */
	private String tipoProcessamentoMtomXopRichiesta;
	
	/**
	 * Tipo di processamento mtom della risposta
	 */
	private String tipoProcessamentoMtomXopRisposta;
	
	/**
	 * Tipo di security message applicato alla richiesta
	 */
	private String tipoMessageSecurityRichiesta;
	
	/**
	 * Tipo di security message applicato alla risposta
	 */
	private String tipoMessageSecurityRisposta;
	
	/**
	 * Tipo di token policy;
	 */
	private String tokenPolicy;
	
	/**
	 * Azioni intraprese per validare il token;
	 */
	private String tokenPolicy_actions;
	
	/**
	 * Azioni intraprese per autenticare il token;
	 */
	private String tokenPolicy_authn;
	
	/**
	 * Lista di AttributeAuthority utilizzate
	 */
	private List<String> attributeAuthorities;
	
	/**
	 * Identificativo della Porta Applicativa
	 */
	private IDPortaApplicativa idPA;
	
	/**
	 * Identificativo della Porta Delegata
	 */
	private IDPortaDelegata idPD;
	
	public String getTipoAutenticazione() {
		return this.tipoAutenticazione;
	}
	public void setTipoAutenticazione(String tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}
		
	public boolean isAutenticazioneOpzionale() {
		return this.autenticazioneOpzionale;
	}
	public void setAutenticazioneOpzionale(boolean autenticazioneOpzionale) {
		this.autenticazioneOpzionale = autenticazioneOpzionale;
	}
	
	public String getTipoGestioneToken() {
		return this.tipoGestioneToken;
	}
	public void setTipoGestioneToken(String tipoGestioneToken) {
		this.tipoGestioneToken = tipoGestioneToken;
	}
	
	public String getTipoAutorizzazione() {
		return this.tipoAutorizzazione;
	}
	public void setTipoAutorizzazione(String tipoAutorizzazione) {
		this.tipoAutorizzazione = tipoAutorizzazione;
	}
	
	public String getTipoAutorizzazioneContenuto() {
		return this.tipoAutorizzazioneContenuto;
	}
	public void setTipoAutorizzazioneContenuto(String tipoAutorizzazioneContenuto) {
		this.tipoAutorizzazioneContenuto = tipoAutorizzazioneContenuto;
	}
	
	public String getTipoValidazioneContenuti() {
		return this.tipoValidazioneContenuti;
	}
	public void setTipoValidazioneContenuti(String tipoValidazioneContenuti) {
		this.tipoValidazioneContenuti = tipoValidazioneContenuti;
	}
	
	public String getTipoProcessamentoMtomXopRichiesta() {
		return this.tipoProcessamentoMtomXopRichiesta;
	}
	public void setTipoProcessamentoMtomXopRichiesta(
			String tipoProcessamentoMtomXopRichiesta) {
		this.tipoProcessamentoMtomXopRichiesta = tipoProcessamentoMtomXopRichiesta;
	}
	
	public String getTipoProcessamentoMtomXopRisposta() {
		return this.tipoProcessamentoMtomXopRisposta;
	}
	public void setTipoProcessamentoMtomXopRisposta(
			String tipoProcessamentoMtomXopRisposta) {
		this.tipoProcessamentoMtomXopRisposta = tipoProcessamentoMtomXopRisposta;
	}
	
	public String getTipoMessageSecurityRichiesta() {
		return this.tipoMessageSecurityRichiesta;
	}
	public void setTipoMessageSecurityRichiesta(String tipoMessageSecurityRichiesta) {
		this.tipoMessageSecurityRichiesta = tipoMessageSecurityRichiesta;
	}
	
	public String getTipoMessageSecurityRisposta() {
		return this.tipoMessageSecurityRisposta;
	}
	public void setTipoMessageSecurityRisposta(String tipoMessageSecurityRisposta) {
		this.tipoMessageSecurityRisposta = tipoMessageSecurityRisposta;
	}
	
	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}
	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}
	public String getIdCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}
	public void setIdCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}
	
	public void addServizioApplicativoErogatore(String servizioApplicativo){
		this.serviziApplicativiErogatori.add(servizioApplicativo);
	}
	public int sizeServiziApplicativiErogatori(){
		return this.serviziApplicativiErogatori.size();
	}
	public String getServizioApplicativoErogatore(int index){
		return this.serviziApplicativiErogatori.get(index);
	}
	public String removeServizioApplicativoErogatore(int index){
		return this.serviziApplicativiErogatori.remove(index);
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public Boolean isGestioneStateless() {
		return this.gestioneStateless;
	}
	public void setGestioneStateless(Boolean gestioneStateless) {
		this.gestioneStateless = gestioneStateless;
	}
	
	public IDPortaApplicativa getIdPA() {
		return this.idPA;
	}
	public void setIdPA(IDPortaApplicativa idPA) {
		this.idPA = idPA;
	}
	public IDPortaDelegata getIdPD() {
		return this.idPD;
	}
	public void setIdPD(IDPortaDelegata idPD) {
		this.idPD = idPD;
	}
	
	public String getTokenPolicy() {
		return this.tokenPolicy;
	}
	public void setTokenPolicy(String tokenPolicy) {
		this.tokenPolicy = tokenPolicy;
	}
	public String getTokenPolicy_actions() {
		return this.tokenPolicy_actions;
	}
	public void setTokenPolicy_actions(String tokenPolicy_actions) {
		this.tokenPolicy_actions = tokenPolicy_actions;
	}
	public String getTokenPolicy_authn() {
		return this.tokenPolicy_authn;
	}
	public void setTokenPolicy_authn(String tokenPolicy_authn) {
		this.tokenPolicy_authn = tokenPolicy_authn;
	}
	
	public List<String> getAttributeAuthorities() {
		return this.attributeAuthorities;
	}
	public void setAttributeAuthoritiesFromObjectList(List<AttributeAuthority> attributeAuthorities) {
		if(attributeAuthorities==null) {
			this.attributeAuthorities = null;
		}
		this.attributeAuthorities = new ArrayList<String>();
		if(!attributeAuthorities.isEmpty()) {
			for (AttributeAuthority attributeAuthority : attributeAuthorities) {
				this.attributeAuthorities.add(attributeAuthority.getNome());
			}
		}
	}
	public void setAttributeAuthorities(List<String> attributeAuthorities) {
		this.attributeAuthorities = attributeAuthorities;
	}
}
