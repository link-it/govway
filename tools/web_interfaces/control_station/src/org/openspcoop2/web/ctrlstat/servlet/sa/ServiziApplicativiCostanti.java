/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.Vector;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * ServiziApplicativiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_SERVIZI_APPLICATIVI = "serviziApplicativi";
	public final static ForwardParams TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO = ForwardParams.OTHER("EndPointInvocazioneServizio");
	public final static ForwardParams TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA = ForwardParams.OTHER("EndPointRispostaAsincrona");
	
	public final static String OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI = "serviziApplicativiRuoli";
	
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI+"Add.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE = OBJECT_NAME_SERVIZI_APPLICATIVI+"Change.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI+"Del.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI+"List.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT = OBJECT_NAME_SERVIZI_APPLICATIVI+"EndPointInvocazioneServizio.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA = OBJECT_NAME_SERVIZI_APPLICATIVI+"EndPointRispostaAsincrona.do";
	public final static Vector<String> SERVLET_SERVIZI_APPLICATIVI = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ADD);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_DELETE);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_LIST);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA);
	}
	
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"Add.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"Del.do";
	public final static String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"List.do";
	public final static Vector<String> SERVLET_SERVIZI_APPLICATIVI_RUOLI = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_ADD);
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_DELETE);
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST);
	}
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_TIPOLOGIA ="Tipologia";
	public final static String LABEL_FRUITORE ="Fruitore";
	public final static String LABEL_EROGATORE ="Erogatore";
	public final static String LABEL_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public final static String LABEL_SERVIZIO_APPLICATIVO = "Servizio Applicativo";
	public final static String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO ="Autenticazione";
	public final static String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP ="Autenticazione Http";
	public final static String LABEL_CREDENZIALI_ACCESSO_PORTA ="Modalit&agrave; di Accesso alla Porta";
	public final static String LABEL_CREDENZIALE_ACCESSO ="Modalit&agrave; di Accesso";
	public final static String LABEL_TIPO_CREDENZIALE ="Tipo";
	public final static String LABEL_ERRORE_APPLICATIVO = "Errore Applicativo generato della Porta";
	public final static String LABEL_TRATTAMENTO_MESSAGGIO = "Trattamento Messaggio";
	public final static String LABEL_SERVIZIO_MESSAGE_BOX = "Servizio IntegrationManager/MessageBox";
	public final static String LABEL_INFO_INTEGRAZIONE = "Informazioni di Integrazione";
	public final static String LABEL_INVOCAZIONE_SERVIZIO = "Invocazione Servizio";
	public final static String LABEL_RISPOSTA_ASINCRONA = "Risposta Asincrona";
	public final static String LABEL_RUOLI = "Ruoli";
	public final static String LABEL_RUOLO = "Ruolo";
	public final static String LABEL_CONNETTORE_ABILITATO_SOLO_IM =  CostantiConfigurazione.ABILITATO + " (MessageBox)";
	
	public final static String LABEL_SA_MENU_VISUALE_AGGREGATA = "Servizi Applicativi";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID = "id";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO = "idsil";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME = "nome";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SOGGETTO = "tipoprov";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SOGGETTO = "nomeprov";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = "provider";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_PROVIDER_SERVIZIO_APPLICATIVO = "idprovidersa";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP = "sbustamento";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX = "getmsg";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA = "sbustamentoInformazioniProtocolloRichiesta";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA = "sbustamentoInformazioniProtocolloRisposta";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE = "tipoauth";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_CONNETTORE = "utente";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_CONNETTORE = "password";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_CONNETTORE = "confpw";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE = "subject";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA = "tipoauthSA";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA = "utenteSA";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA = "passwordSA";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA = "confpwSA";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA = "subjectSA";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_FAULT = "fault";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR = "faultactor";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = "genericfault";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX = "prefixfault";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA = "invrifRichiesta";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA = "invrifRisposta";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO = "risprif";
//	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_FRUITORE = "ruoloFruitore";
//	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_EROGATORE = "ruoloErogatore";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA = "ruoloSA";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI = "ruoli";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION = "action";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "correlato";
	
	public final static String ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO = CostantiControlStation.PARAMETRO_USAIDSOGG;
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME = "Nome";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI = "Soggetti";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = "Soggetto";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME = "Utente";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD = "Password";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT = "Subject";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP = "Sbustamento SOAP";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO = "Sbustamento";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO = "Sbustamento Protocollo";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX = "Attivazione MessageBox";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT = "Modalit&agrave; di fault";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR = "Fault Actor";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = "Generic Fault Code";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX = "Prefix Fault Code";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO = "Invio per Riferimento";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO = "Risposta per Riferimento";
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "Servizio Correlato";
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI = "Servizi Applicativi di ";
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String SERVIZI_APPLICATIVI_FAULT_SOAP = CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP.toString();
	public final static String SERVIZI_APPLICATIVI_FAULT_XML = CostantiConfigurazione.ERRORE_APPLICATIVO_XML.toString();
	public final static String[] SERVIZI_APPLICATIVI_FAULT = { SERVIZI_APPLICATIVI_FAULT_SOAP, SERVIZI_APPLICATIVI_FAULT_XML };
	
	public final static String SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public final static String SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public final static String[] SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = { SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_ABILITATO, 
		SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_DISABILITATO };
	
	public final static String SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public final static String SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public final static String[] SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO = { SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_ABILITATO, 
		SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_DISABILITATO };
	
	public final static String SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public final static String SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public final static String[] SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO = { SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_ABILITATO, 
		SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO };
	
	public final static String SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA = "nessuna";
	public final static String SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC = CostantiConfigurazione.CREDENZIALE_BASIC.toString();
	public final static String SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL = CostantiConfigurazione.CREDENZIALE_SSL.toString();
	public final static String LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC = "http-basic";
	public final static String LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL = "https";
	public final static String DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE = SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA;
	
	public final static String SERVIZI_APPLICATIVI_RUOLO_NON_CONFIGURATO = "Non Configurato";
	public final static String SERVIZI_APPLICATIVI_RUOLO_FRUITORE = "Fruitore";
	public final static String SERVIZI_APPLICATIVI_RUOLO_EROGATORE = "Erogatore";
	public final static String[] SERVIZI_APPLICATIVI_RUOLO = { SERVIZI_APPLICATIVI_RUOLO_FRUITORE, 
		SERVIZI_APPLICATIVI_RUOLO_EROGATORE };
	
	
}
