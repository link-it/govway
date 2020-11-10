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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.Vector;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
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

	
	/* NOME VISTA CUSTOM */
	public final static String SERVIZI_APPLICATIVI_NOME_VISTA_CUSTOM_LISTA = "applicativi";
	
	/* LABEL GENERALI */
	
	public final static String LABEL_TIPO ="Tipo";
	public final static String LABEL_TIPOLOGIA ="Tipologia";
	public final static String LABEL_FRUITORE ="Fruitore";
	public final static String LABEL_EROGATORE ="Erogatore";
	public final static String LABEL_APPLICATIVI = "Applicativi";
	public final static String LABEL_APPLICATIVO = "Applicativo";
	public final static String LABEL_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public final static String LABEL_SERVIZIO_APPLICATIVO = "Servizio Applicativo";
	public final static String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO ="Autenticazione";
	public final static String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP ="Autenticazione Http";
	public final static String LABEL_CREDENZIALI_ACCESSO_PORTA ="Modalit&agrave; di Accesso"; // alla Porta";
	public final static String LABEL_CREDENZIALE_ACCESSO ="Modalit&agrave; di Accesso";
	public final static String LABEL_TIPO_CREDENZIALE ="Tipo";
	public final static String LABEL_ERRORE_APPLICATIVO = "Errore Applicativo generato della Porta";
	public final static String LABEL_TRATTAMENTO_MESSAGGIO = "Trattamento Messaggio";
	public final static String LABEL_SERVIZIO_MESSAGE_BOX = "Servizio IntegrationManager/MessageBox";
	public final static String LABEL_INFO_INTEGRAZIONE = "Informazioni di Integrazione";
	public final static String LABEL_INVOCAZIONE_SERVIZIO = "Invocazione Servizio";
	public final static String LABEL_RISPOSTA_ASINCRONA = "Risposta Asincrona";
	public final static String LABEL_CONNETTORE_ABILITATO_SOLO_IM =  CostantiConfigurazione.ABILITATO + " (MessageBox)";
	public final static String LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SERVIZIO_APPLICATIVO.name()+"')";
	public final static String LABEL_APPLICATIVI_MENU_VISUALE_AGGREGATA = "Applicativi";
	public final static String LABEL_SA_MENU_VISUALE_AGGREGATA = "Servizi Applicativi";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID = "id";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO = "idsil";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME = "nome";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS = CostantiControlStation.PARAMETRO_ID_ASPS;
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA = CostantiControlStation.PARAMETRO_ID_PORTA;
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SOGGETTO = "tipoprov";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SOGGETTO = "nomeprov";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = CostantiControlStation.PARAMETRO_PROVIDER;
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_ID_PROVIDER_SERVIZIO_APPLICATIVO = "idprovidersa";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP = "sbustamento";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX = "getmsg";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE = CostantiControlStation.PARAMETRO_ACCESSO_DA_CHANGE;
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA = "sbustamentoInformazioniProtocolloRichiesta";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA = "sbustamentoInformazioniProtocolloRisposta";
		
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
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA = Costanti.CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA;
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI = "ruoli";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO = "ruolo";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION = "action";
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "correlato";
	
	public final static String PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT = "useAsClient";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME = "Nome";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI = "Soggetti";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = "Soggetto";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_VISUALIZZA_DATI_PROVIDER = "Visualizza Dati Soggetto";
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
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT = "Utilizzabile come Client";
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "Servizio Correlato";
	
	public final static String LABEL_PARAMETRO_APPLICATIVI_DI = "Applicativi di ";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI = "Servizi Applicativi di ";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI = "Ruoli di ";
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVOCAZIONE_SERVIZIO_DI = "Invocazione Servizio di ";	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_ASINCRONA_DI = "Risposta Asincrona di ";
	
	public final static String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_TIPO = "Tipo";
	
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
		
	public final static String LABEL_PARAMETRO_FILTRO_RUOLO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String SERVIZI_APPLICATIVI_RUOLO_NON_CONFIGURATO = "Non Configurato";
	public final static String SERVIZI_APPLICATIVI_RUOLO_FRUITORE = Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE;
	public final static String SERVIZI_APPLICATIVI_RUOLO_EROGATORE = Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE;
	public final static String[] SERVIZI_APPLICATIVI_RUOLO = { SERVIZI_APPLICATIVI_RUOLO_FRUITORE, 
		SERVIZI_APPLICATIVI_RUOLO_EROGATORE };
	
	public final static String LABEL_PARAMETRO_FILTRO_TIPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER = CostantiConfigurazione.SERVER;
	public final static String VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT = CostantiConfigurazione.CLIENT;
	public final static String VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI = CostantiConfigurazione.CLIENT_OR_SERVER;
	public final static String SERVIZI_APPLICATIVI_TIPO_NON_CONFIGURATO = "Non Configurato";
	public final static String LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT = "Client";
	public final static String LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER = "Server";
	public final static String[] LABELS_SERVIZI_APPLICATIVI_TIPO = { LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT, LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER };
	public final static String[] VALUES_SERVIZI_APPLICATIVI_TIPO = { VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT, VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER };
	
	/* ATTRIBUTI */
	
	public final static String ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT;
	public final static int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_NONE;
	public final static int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_SOGGETTO;
	public final static int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_CONFIGURAZIONE;
	
	public static String getLabelSbustamentoProtocollo(String nomeProtocollo) {
		if(Costanti.SPCOOP_PROTOCOL_NAME.equals(nomeProtocollo)) {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " SPCoop";
		}
		else if(Costanti.MODIPA_PROTOCOL_NAME.equals(nomeProtocollo)) {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " ModI PA";
		}
		else {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " " +nomeProtocollo.toUpperCase();
		}
	}
	
	public final static String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_VUOTI = "";
	public final static String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SOLO_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public final static String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_CON_PROFILO = "Profilo Interoperabilit&agrave;: {0}, Tipo: {1}";
	public final static String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SENZA_PROFILO = "Tipo: {0}";
		
	public final static String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "L'applicativo non risulta utilizzato in alcuna configurazione";
	
}
