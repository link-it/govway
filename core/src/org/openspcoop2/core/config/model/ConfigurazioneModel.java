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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.Configurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Configurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneModel extends AbstractModel<Configurazione> {

	public ConfigurazioneModel(){
	
		super();
	
		this.ROUTING_TABLE = new org.openspcoop2.core.config.model.RoutingTableModel(new Field("routing-table",org.openspcoop2.core.config.RoutingTable.class,"configurazione",Configurazione.class));
		this.ACCESSO_REGISTRO = new org.openspcoop2.core.config.model.AccessoRegistroModel(new Field("accesso-registro",org.openspcoop2.core.config.AccessoRegistro.class,"configurazione",Configurazione.class));
		this.ACCESSO_CONFIGURAZIONE = new org.openspcoop2.core.config.model.AccessoConfigurazioneModel(new Field("accesso-configurazione",org.openspcoop2.core.config.AccessoConfigurazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_AUTORIZZAZIONE = new org.openspcoop2.core.config.model.AccessoDatiAutorizzazioneModel(new Field("accesso-dati-autorizzazione",org.openspcoop2.core.config.AccessoDatiAutorizzazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_AUTENTICAZIONE = new org.openspcoop2.core.config.model.AccessoDatiAutenticazioneModel(new Field("accesso-dati-autenticazione",org.openspcoop2.core.config.AccessoDatiAutenticazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_GESTIONE_TOKEN = new org.openspcoop2.core.config.model.AccessoDatiGestioneTokenModel(new Field("accesso-dati-gestione-token",org.openspcoop2.core.config.AccessoDatiGestioneToken.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_KEYSTORE = new org.openspcoop2.core.config.model.AccessoDatiKeystoreModel(new Field("accesso-dati-keystore",org.openspcoop2.core.config.AccessoDatiKeystore.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_CONSEGNA_APPLICATIVI = new org.openspcoop2.core.config.model.AccessoDatiConsegnaApplicativiModel(new Field("accesso-dati-consegna-applicativi",org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi.class,"configurazione",Configurazione.class));
		this.MULTITENANT = new org.openspcoop2.core.config.model.ConfigurazioneMultitenantModel(new Field("multitenant",org.openspcoop2.core.config.ConfigurazioneMultitenant.class,"configurazione",Configurazione.class));
		this.URL_INVOCAZIONE = new org.openspcoop2.core.config.model.ConfigurazioneUrlInvocazioneModel(new Field("url-invocazione",org.openspcoop2.core.config.ConfigurazioneUrlInvocazione.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_BUSTE = new org.openspcoop2.core.config.model.ValidazioneBusteModel(new Field("validazione-buste",org.openspcoop2.core.config.ValidazioneBuste.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new Field("validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"configurazione",Configurazione.class));
		this.INDIRIZZO_RISPOSTA = new org.openspcoop2.core.config.model.IndirizzoRispostaModel(new Field("indirizzo-risposta",org.openspcoop2.core.config.IndirizzoRisposta.class,"configurazione",Configurazione.class));
		this.ATTACHMENTS = new org.openspcoop2.core.config.model.AttachmentsModel(new Field("attachments",org.openspcoop2.core.config.Attachments.class,"configurazione",Configurazione.class));
		this.RISPOSTE = new org.openspcoop2.core.config.model.RisposteModel(new Field("risposte",org.openspcoop2.core.config.Risposte.class,"configurazione",Configurazione.class));
		this.INOLTRO_BUSTE_NON_RISCONTRATE = new org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel(new Field("inoltro-buste-non-riscontrate",org.openspcoop2.core.config.InoltroBusteNonRiscontrate.class,"configurazione",Configurazione.class));
		this.MESSAGGI_DIAGNOSTICI = new org.openspcoop2.core.config.model.MessaggiDiagnosticiModel(new Field("messaggi-diagnostici",org.openspcoop2.core.config.MessaggiDiagnostici.class,"configurazione",Configurazione.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.TracciamentoModel(new Field("tracciamento",org.openspcoop2.core.config.Tracciamento.class,"configurazione",Configurazione.class));
		this.DUMP = new org.openspcoop2.core.config.model.DumpModel(new Field("dump",org.openspcoop2.core.config.Dump.class,"configurazione",Configurazione.class));
		this.TRANSAZIONI = new org.openspcoop2.core.config.model.TransazioniModel(new Field("transazioni",org.openspcoop2.core.config.Transazioni.class,"configurazione",Configurazione.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel(new Field("gestione-errore",org.openspcoop2.core.config.ConfigurazioneGestioneErrore.class,"configurazione",Configurazione.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.IntegrationManagerModel(new Field("integration-manager",org.openspcoop2.core.config.IntegrationManager.class,"configurazione",Configurazione.class));
		this.STATO_SERVIZI_PDD = new org.openspcoop2.core.config.model.StatoServiziPddModel(new Field("stato-servizi-pdd",org.openspcoop2.core.config.StatoServiziPdd.class,"configurazione",Configurazione.class));
		this.SYSTEM_PROPERTIES = new org.openspcoop2.core.config.model.SystemPropertiesModel(new Field("system-properties",org.openspcoop2.core.config.SystemProperties.class,"configurazione",Configurazione.class));
		this.GENERIC_PROPERTIES = new org.openspcoop2.core.config.model.GenericPropertiesModel(new Field("generic-properties",org.openspcoop2.core.config.GenericProperties.class,"configurazione",Configurazione.class));
		this.GESTIONE_CORS = new org.openspcoop2.core.config.model.CorsConfigurazioneModel(new Field("gestione-cors",org.openspcoop2.core.config.CorsConfigurazione.class,"configurazione",Configurazione.class));
		this.RESPONSE_CACHING = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneGeneraleModel(new Field("response-caching",org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale.class,"configurazione",Configurazione.class));
		this.GESTIONE_CANALI = new org.openspcoop2.core.config.model.CanaliConfigurazioneModel(new Field("gestione-canali",org.openspcoop2.core.config.CanaliConfigurazione.class,"configurazione",Configurazione.class));
	
	}
	
	public ConfigurazioneModel(IField father){
	
		super(father);
	
		this.ROUTING_TABLE = new org.openspcoop2.core.config.model.RoutingTableModel(new ComplexField(father,"routing-table",org.openspcoop2.core.config.RoutingTable.class,"configurazione",Configurazione.class));
		this.ACCESSO_REGISTRO = new org.openspcoop2.core.config.model.AccessoRegistroModel(new ComplexField(father,"accesso-registro",org.openspcoop2.core.config.AccessoRegistro.class,"configurazione",Configurazione.class));
		this.ACCESSO_CONFIGURAZIONE = new org.openspcoop2.core.config.model.AccessoConfigurazioneModel(new ComplexField(father,"accesso-configurazione",org.openspcoop2.core.config.AccessoConfigurazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_AUTORIZZAZIONE = new org.openspcoop2.core.config.model.AccessoDatiAutorizzazioneModel(new ComplexField(father,"accesso-dati-autorizzazione",org.openspcoop2.core.config.AccessoDatiAutorizzazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_AUTENTICAZIONE = new org.openspcoop2.core.config.model.AccessoDatiAutenticazioneModel(new ComplexField(father,"accesso-dati-autenticazione",org.openspcoop2.core.config.AccessoDatiAutenticazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_GESTIONE_TOKEN = new org.openspcoop2.core.config.model.AccessoDatiGestioneTokenModel(new ComplexField(father,"accesso-dati-gestione-token",org.openspcoop2.core.config.AccessoDatiGestioneToken.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_KEYSTORE = new org.openspcoop2.core.config.model.AccessoDatiKeystoreModel(new ComplexField(father,"accesso-dati-keystore",org.openspcoop2.core.config.AccessoDatiKeystore.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_CONSEGNA_APPLICATIVI = new org.openspcoop2.core.config.model.AccessoDatiConsegnaApplicativiModel(new ComplexField(father,"accesso-dati-consegna-applicativi",org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi.class,"configurazione",Configurazione.class));
		this.MULTITENANT = new org.openspcoop2.core.config.model.ConfigurazioneMultitenantModel(new ComplexField(father,"multitenant",org.openspcoop2.core.config.ConfigurazioneMultitenant.class,"configurazione",Configurazione.class));
		this.URL_INVOCAZIONE = new org.openspcoop2.core.config.model.ConfigurazioneUrlInvocazioneModel(new ComplexField(father,"url-invocazione",org.openspcoop2.core.config.ConfigurazioneUrlInvocazione.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_BUSTE = new org.openspcoop2.core.config.model.ValidazioneBusteModel(new ComplexField(father,"validazione-buste",org.openspcoop2.core.config.ValidazioneBuste.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new ComplexField(father,"validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"configurazione",Configurazione.class));
		this.INDIRIZZO_RISPOSTA = new org.openspcoop2.core.config.model.IndirizzoRispostaModel(new ComplexField(father,"indirizzo-risposta",org.openspcoop2.core.config.IndirizzoRisposta.class,"configurazione",Configurazione.class));
		this.ATTACHMENTS = new org.openspcoop2.core.config.model.AttachmentsModel(new ComplexField(father,"attachments",org.openspcoop2.core.config.Attachments.class,"configurazione",Configurazione.class));
		this.RISPOSTE = new org.openspcoop2.core.config.model.RisposteModel(new ComplexField(father,"risposte",org.openspcoop2.core.config.Risposte.class,"configurazione",Configurazione.class));
		this.INOLTRO_BUSTE_NON_RISCONTRATE = new org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel(new ComplexField(father,"inoltro-buste-non-riscontrate",org.openspcoop2.core.config.InoltroBusteNonRiscontrate.class,"configurazione",Configurazione.class));
		this.MESSAGGI_DIAGNOSTICI = new org.openspcoop2.core.config.model.MessaggiDiagnosticiModel(new ComplexField(father,"messaggi-diagnostici",org.openspcoop2.core.config.MessaggiDiagnostici.class,"configurazione",Configurazione.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.TracciamentoModel(new ComplexField(father,"tracciamento",org.openspcoop2.core.config.Tracciamento.class,"configurazione",Configurazione.class));
		this.DUMP = new org.openspcoop2.core.config.model.DumpModel(new ComplexField(father,"dump",org.openspcoop2.core.config.Dump.class,"configurazione",Configurazione.class));
		this.TRANSAZIONI = new org.openspcoop2.core.config.model.TransazioniModel(new ComplexField(father,"transazioni",org.openspcoop2.core.config.Transazioni.class,"configurazione",Configurazione.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel(new ComplexField(father,"gestione-errore",org.openspcoop2.core.config.ConfigurazioneGestioneErrore.class,"configurazione",Configurazione.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.IntegrationManagerModel(new ComplexField(father,"integration-manager",org.openspcoop2.core.config.IntegrationManager.class,"configurazione",Configurazione.class));
		this.STATO_SERVIZI_PDD = new org.openspcoop2.core.config.model.StatoServiziPddModel(new ComplexField(father,"stato-servizi-pdd",org.openspcoop2.core.config.StatoServiziPdd.class,"configurazione",Configurazione.class));
		this.SYSTEM_PROPERTIES = new org.openspcoop2.core.config.model.SystemPropertiesModel(new ComplexField(father,"system-properties",org.openspcoop2.core.config.SystemProperties.class,"configurazione",Configurazione.class));
		this.GENERIC_PROPERTIES = new org.openspcoop2.core.config.model.GenericPropertiesModel(new ComplexField(father,"generic-properties",org.openspcoop2.core.config.GenericProperties.class,"configurazione",Configurazione.class));
		this.GESTIONE_CORS = new org.openspcoop2.core.config.model.CorsConfigurazioneModel(new ComplexField(father,"gestione-cors",org.openspcoop2.core.config.CorsConfigurazione.class,"configurazione",Configurazione.class));
		this.RESPONSE_CACHING = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneGeneraleModel(new ComplexField(father,"response-caching",org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale.class,"configurazione",Configurazione.class));
		this.GESTIONE_CANALI = new org.openspcoop2.core.config.model.CanaliConfigurazioneModel(new ComplexField(father,"gestione-canali",org.openspcoop2.core.config.CanaliConfigurazione.class,"configurazione",Configurazione.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.RoutingTableModel ROUTING_TABLE = null;
	 
	public org.openspcoop2.core.config.model.AccessoRegistroModel ACCESSO_REGISTRO = null;
	 
	public org.openspcoop2.core.config.model.AccessoConfigurazioneModel ACCESSO_CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiAutorizzazioneModel ACCESSO_DATI_AUTORIZZAZIONE = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiAutenticazioneModel ACCESSO_DATI_AUTENTICAZIONE = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiGestioneTokenModel ACCESSO_DATI_GESTIONE_TOKEN = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiKeystoreModel ACCESSO_DATI_KEYSTORE = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiConsegnaApplicativiModel ACCESSO_DATI_CONSEGNA_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneMultitenantModel MULTITENANT = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneUrlInvocazioneModel URL_INVOCAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneBusteModel VALIDAZIONE_BUSTE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel VALIDAZIONE_CONTENUTI_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.IndirizzoRispostaModel INDIRIZZO_RISPOSTA = null;
	 
	public org.openspcoop2.core.config.model.AttachmentsModel ATTACHMENTS = null;
	 
	public org.openspcoop2.core.config.model.RisposteModel RISPOSTE = null;
	 
	public org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel INOLTRO_BUSTE_NON_RISCONTRATE = null;
	 
	public org.openspcoop2.core.config.model.MessaggiDiagnosticiModel MESSAGGI_DIAGNOSTICI = null;
	 
	public org.openspcoop2.core.config.model.TracciamentoModel TRACCIAMENTO = null;
	 
	public org.openspcoop2.core.config.model.DumpModel DUMP = null;
	 
	public org.openspcoop2.core.config.model.TransazioniModel TRANSAZIONI = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel GESTIONE_ERRORE = null;
	 
	public org.openspcoop2.core.config.model.IntegrationManagerModel INTEGRATION_MANAGER = null;
	 
	public org.openspcoop2.core.config.model.StatoServiziPddModel STATO_SERVIZI_PDD = null;
	 
	public org.openspcoop2.core.config.model.SystemPropertiesModel SYSTEM_PROPERTIES = null;
	 
	public org.openspcoop2.core.config.model.GenericPropertiesModel GENERIC_PROPERTIES = null;
	 
	public org.openspcoop2.core.config.model.CorsConfigurazioneModel GESTIONE_CORS = null;
	 
	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneGeneraleModel RESPONSE_CACHING = null;
	 
	public org.openspcoop2.core.config.model.CanaliConfigurazioneModel GESTIONE_CANALI = null;
	 

	@Override
	public Class<Configurazione> getModeledClass(){
		return Configurazione.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}