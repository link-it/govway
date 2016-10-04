/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
		this.VALIDAZIONE_BUSTE = new org.openspcoop2.core.config.model.ValidazioneBusteModel(new Field("validazione-buste",org.openspcoop2.core.config.ValidazioneBuste.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new Field("validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"configurazione",Configurazione.class));
		this.INDIRIZZO_RISPOSTA = new org.openspcoop2.core.config.model.IndirizzoRispostaModel(new Field("indirizzo-risposta",org.openspcoop2.core.config.IndirizzoRisposta.class,"configurazione",Configurazione.class));
		this.ATTACHMENTS = new org.openspcoop2.core.config.model.AttachmentsModel(new Field("attachments",org.openspcoop2.core.config.Attachments.class,"configurazione",Configurazione.class));
		this.RISPOSTE = new org.openspcoop2.core.config.model.RisposteModel(new Field("risposte",org.openspcoop2.core.config.Risposte.class,"configurazione",Configurazione.class));
		this.INOLTRO_BUSTE_NON_RISCONTRATE = new org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel(new Field("inoltro-buste-non-riscontrate",org.openspcoop2.core.config.InoltroBusteNonRiscontrate.class,"configurazione",Configurazione.class));
		this.MESSAGGI_DIAGNOSTICI = new org.openspcoop2.core.config.model.MessaggiDiagnosticiModel(new Field("messaggi-diagnostici",org.openspcoop2.core.config.MessaggiDiagnostici.class,"configurazione",Configurazione.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.TracciamentoModel(new Field("tracciamento",org.openspcoop2.core.config.Tracciamento.class,"configurazione",Configurazione.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel(new Field("gestione-errore",org.openspcoop2.core.config.ConfigurazioneGestioneErrore.class,"configurazione",Configurazione.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.IntegrationManagerModel(new Field("integration-manager",org.openspcoop2.core.config.IntegrationManager.class,"configurazione",Configurazione.class));
		this.STATO_SERVIZI_PDD = new org.openspcoop2.core.config.model.StatoServiziPddModel(new Field("stato-servizi-pdd",org.openspcoop2.core.config.StatoServiziPdd.class,"configurazione",Configurazione.class));
		this.SYSTEM_PROPERTIES = new org.openspcoop2.core.config.model.SystemPropertiesModel(new Field("system-properties",org.openspcoop2.core.config.SystemProperties.class,"configurazione",Configurazione.class));
	
	}
	
	public ConfigurazioneModel(IField father){
	
		super(father);
	
		this.ROUTING_TABLE = new org.openspcoop2.core.config.model.RoutingTableModel(new ComplexField(father,"routing-table",org.openspcoop2.core.config.RoutingTable.class,"configurazione",Configurazione.class));
		this.ACCESSO_REGISTRO = new org.openspcoop2.core.config.model.AccessoRegistroModel(new ComplexField(father,"accesso-registro",org.openspcoop2.core.config.AccessoRegistro.class,"configurazione",Configurazione.class));
		this.ACCESSO_CONFIGURAZIONE = new org.openspcoop2.core.config.model.AccessoConfigurazioneModel(new ComplexField(father,"accesso-configurazione",org.openspcoop2.core.config.AccessoConfigurazione.class,"configurazione",Configurazione.class));
		this.ACCESSO_DATI_AUTORIZZAZIONE = new org.openspcoop2.core.config.model.AccessoDatiAutorizzazioneModel(new ComplexField(father,"accesso-dati-autorizzazione",org.openspcoop2.core.config.AccessoDatiAutorizzazione.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_BUSTE = new org.openspcoop2.core.config.model.ValidazioneBusteModel(new ComplexField(father,"validazione-buste",org.openspcoop2.core.config.ValidazioneBuste.class,"configurazione",Configurazione.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new ComplexField(father,"validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"configurazione",Configurazione.class));
		this.INDIRIZZO_RISPOSTA = new org.openspcoop2.core.config.model.IndirizzoRispostaModel(new ComplexField(father,"indirizzo-risposta",org.openspcoop2.core.config.IndirizzoRisposta.class,"configurazione",Configurazione.class));
		this.ATTACHMENTS = new org.openspcoop2.core.config.model.AttachmentsModel(new ComplexField(father,"attachments",org.openspcoop2.core.config.Attachments.class,"configurazione",Configurazione.class));
		this.RISPOSTE = new org.openspcoop2.core.config.model.RisposteModel(new ComplexField(father,"risposte",org.openspcoop2.core.config.Risposte.class,"configurazione",Configurazione.class));
		this.INOLTRO_BUSTE_NON_RISCONTRATE = new org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel(new ComplexField(father,"inoltro-buste-non-riscontrate",org.openspcoop2.core.config.InoltroBusteNonRiscontrate.class,"configurazione",Configurazione.class));
		this.MESSAGGI_DIAGNOSTICI = new org.openspcoop2.core.config.model.MessaggiDiagnosticiModel(new ComplexField(father,"messaggi-diagnostici",org.openspcoop2.core.config.MessaggiDiagnostici.class,"configurazione",Configurazione.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.TracciamentoModel(new ComplexField(father,"tracciamento",org.openspcoop2.core.config.Tracciamento.class,"configurazione",Configurazione.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel(new ComplexField(father,"gestione-errore",org.openspcoop2.core.config.ConfigurazioneGestioneErrore.class,"configurazione",Configurazione.class));
		this.INTEGRATION_MANAGER = new org.openspcoop2.core.config.model.IntegrationManagerModel(new ComplexField(father,"integration-manager",org.openspcoop2.core.config.IntegrationManager.class,"configurazione",Configurazione.class));
		this.STATO_SERVIZI_PDD = new org.openspcoop2.core.config.model.StatoServiziPddModel(new ComplexField(father,"stato-servizi-pdd",org.openspcoop2.core.config.StatoServiziPdd.class,"configurazione",Configurazione.class));
		this.SYSTEM_PROPERTIES = new org.openspcoop2.core.config.model.SystemPropertiesModel(new ComplexField(father,"system-properties",org.openspcoop2.core.config.SystemProperties.class,"configurazione",Configurazione.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.RoutingTableModel ROUTING_TABLE = null;
	 
	public org.openspcoop2.core.config.model.AccessoRegistroModel ACCESSO_REGISTRO = null;
	 
	public org.openspcoop2.core.config.model.AccessoConfigurazioneModel ACCESSO_CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.AccessoDatiAutorizzazioneModel ACCESSO_DATI_AUTORIZZAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneBusteModel VALIDAZIONE_BUSTE = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel VALIDAZIONE_CONTENUTI_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.IndirizzoRispostaModel INDIRIZZO_RISPOSTA = null;
	 
	public org.openspcoop2.core.config.model.AttachmentsModel ATTACHMENTS = null;
	 
	public org.openspcoop2.core.config.model.RisposteModel RISPOSTE = null;
	 
	public org.openspcoop2.core.config.model.InoltroBusteNonRiscontrateModel INOLTRO_BUSTE_NON_RISCONTRATE = null;
	 
	public org.openspcoop2.core.config.model.MessaggiDiagnosticiModel MESSAGGI_DIAGNOSTICI = null;
	 
	public org.openspcoop2.core.config.model.TracciamentoModel TRACCIAMENTO = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneGestioneErroreModel GESTIONE_ERRORE = null;
	 
	public org.openspcoop2.core.config.model.IntegrationManagerModel INTEGRATION_MANAGER = null;
	 
	public org.openspcoop2.core.config.model.StatoServiziPddModel STATO_SERVIZI_PDD = null;
	 
	public org.openspcoop2.core.config.model.SystemPropertiesModel SYSTEM_PROPERTIES = null;
	 

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