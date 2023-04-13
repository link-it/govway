/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
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
	
	public static final String OBJECT_NAME_SERVIZI_APPLICATIVI = "serviziApplicativi";
	public static final ForwardParams TIPO_OPERAZIONE_ENDPOINT_INVOCAZIONE_SERVIZIO = ForwardParams.OTHER("EndPointInvocazioneServizio");
	public static final ForwardParams TIPO_OPERAZIONE_ENDPOINT_RISPOSTA_ASINCRONA = ForwardParams.OTHER("EndPointRispostaAsincrona");
	
	public static final String OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI = "serviziApplicativiRuoli";
	public static final String OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI = "serviziApplicativiCredenziali";
	public static final String OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA = "serviziApplicativiProprieta";
	
	public static final String OBJECT_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI = "serviziApplicativiVerificaCertificati";
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI+"Add.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE = OBJECT_NAME_SERVIZI_APPLICATIVI+"Change.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI+"Del.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI+"List.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT = OBJECT_NAME_SERVIZI_APPLICATIVI+"EndPointInvocazioneServizio.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA = OBJECT_NAME_SERVIZI_APPLICATIVI+"EndPointRispostaAsincrona.do";
	public static final Vector<String> SERVLET_SERVIZI_APPLICATIVI = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ADD);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_DELETE);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_LIST);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT);
		SERVLET_SERVIZI_APPLICATIVI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA);
	}
	
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"Add.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"Del.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI+"List.do";
	public static final Vector<String> SERVLET_SERVIZI_APPLICATIVI_RUOLI = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_ADD);
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_DELETE);
		SERVLET_SERVIZI_APPLICATIVI_RUOLI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST);
	}
	
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI+"Add.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_CHANGE = OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI+"Change.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI+"Del.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI+"List.do";
	public static final Vector<String> SERVLET_SERVIZI_APPLICATIVI_CREDENZIALI = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI_CREDENZIALI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_ADD);
		SERVLET_SERVIZI_APPLICATIVI_CREDENZIALI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_CHANGE);
		SERVLET_SERVIZI_APPLICATIVI_CREDENZIALI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_DELETE);
		SERVLET_SERVIZI_APPLICATIVI_CREDENZIALI.add(SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST);
	}
	
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_ADD = OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA+"Add.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_CHANGE = OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA+"Change.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_DELETE = OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA+"Del.do";
	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST = OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA+"List.do";
	public static final Vector<String> SERVLET_SERVIZI_APPLICATIVI_PROPRIETA = new Vector<String>();
	static{
		SERVLET_SERVIZI_APPLICATIVI_PROPRIETA.add(SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_ADD);
		SERVLET_SERVIZI_APPLICATIVI_PROPRIETA.add(SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_CHANGE);
		SERVLET_SERVIZI_APPLICATIVI_PROPRIETA.add(SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_DELETE);
		SERVLET_SERVIZI_APPLICATIVI_PROPRIETA.add(SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST);
	}

	public static final String SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI = OBJECT_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI+".do";
	
	/* NOME VISTA CUSTOM */
	public static final String SERVIZI_APPLICATIVI_NOME_VISTA_CUSTOM_LISTA = "applicativi";
	
	/* LABEL GENERALI */
	
	public static final String LABEL_TIPO ="Tipo";
	public static final String LABEL_TIPOLOGIA ="Tipologia";
	public static final String LABEL_FRUITORE ="Fruitore";
	public static final String LABEL_EROGATORE ="Erogatore";
	public static final String LABEL_APPLICATIVI = "Applicativi";
	public static final String LABEL_APPLICATIVO = "Applicativo";
	public static final String LABEL_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public static final String LABEL_SERVIZIO_APPLICATIVO = "Servizio Applicativo";
	public static final String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO ="Autenticazione";
	public static final String LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP ="Autenticazione Http";
	public static final String LABEL_CREDENZIALI_ACCESSO_PORTA ="Modalit&agrave; di Accesso"; // alla Porta";
	public static final String LABEL_CREDENZIALE_ACCESSO ="Modalit&agrave; di Accesso";
	public static final String LABEL_CREDENZIALE_ACCESSO_HTTPS ="X.509 Subject";
	public static final String LABEL_CREDENZIALE_ACCESSO_HTTPS_ISSUER ="X.509 Issuer";
	public static final String LABEL_CREDENZIALE_ACCESSO_USERNAME ="Basic Username";
	public static final String LABEL_CREDENZIALE_ACCESSO_PRINCIPAL ="Principal";
	public static final String LABEL_CREDENZIALE_ACCESSO_TOKEN_POLICY ="Token Policy";
	public static final String LABEL_CREDENZIALE_ACCESSO_TOKEN_CLIENT_ID = "Identificativo Client";
	public static final String LABEL_TIPO_CREDENZIALE ="Tipo";
	public static final String LABEL_ERRORE_APPLICATIVO = "Errore Applicativo generato della Porta";
	public static final String LABEL_TRATTAMENTO_MESSAGGIO = "Trattamento Messaggio";
	public static final String LABEL_SERVIZIO_MESSAGE_BOX = "Servizio IntegrationManager/MessageBox";
	public static final String LABEL_INFO_INTEGRAZIONE = "Informazioni di Integrazione";
	public static final String LABEL_INVOCAZIONE_SERVIZIO = "Invocazione Servizio";
	public static final String LABEL_RISPOSTA_ASINCRONA = "Risposta Asincrona";
	public static final String LABEL_CONNETTORE_ABILITATO_SOLO_IM =  CostantiConfigurazione.ABILITATO + " (MessageBox)";
	public static final String LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SERVIZIO_APPLICATIVO.name()+"')";
	public static final String LABEL_APPLICATIVI_MENU_VISUALE_AGGREGATA = "Applicativi";
	public static final String LABEL_SA_MENU_VISUALE_AGGREGATA = "Servizi Applicativi";
	public static final String LABEL_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI;
	public static final String LABEL_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI_DI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_DI;
	public static final String LABEL_SERVIZI_APPLICATIVI_VERIFICA_CONNETTIVITA = CostantiControlStation.LABEL_VERIFICA_CONNETTIVITA;
	public static final String LABEL_SERVIZI_APPLICATIVI_VERIFICA_CONNETTIVITA_DI = CostantiControlStation.LABEL_VERIFICA_CONNETTIVITA_DI;
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_ID = "id";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO = "idsil";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_NOME = "nome";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO = "nomeservizioApplicativo";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS = CostantiControlStation.PARAMETRO_ID_ASPS;
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA = CostantiControlStation.PARAMETRO_ID_PORTA;
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SOGGETTO = "tipoprov";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_NOME_SOGGETTO = "nomeprov";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = CostantiControlStation.PARAMETRO_PROVIDER;
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_ID_PROVIDER_SERVIZIO_APPLICATIVO = "idprovidersa";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP = "sbustamento";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX = "getmsg";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE = CostantiControlStation.PARAMETRO_ACCESSO_DA_CHANGE;
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA = "sbustamentoInformazioniProtocolloRichiesta";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA = "sbustamentoInformazioniProtocolloRisposta";
		
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_FAULT = "fault";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR = "faultactor";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = "genericfault";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX = "prefixfault";
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA = "invrifRichiesta";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA = "invrifRisposta";
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO = "risprif";
//	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_FRUITORE = "ruoloFruitore";
//	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_EROGATORE = "ruoloErogatore";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA = "ruoloSA";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA = Costanti.CONSOLE_PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA;
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI = "ruoli";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLO = "ruolo";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION = "action";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "correlato";
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT = "useAsClient";
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_CREDENZIALI_ID = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CREDENZIALI_ID;
	
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME = "propNome";
	public static final String PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE = "propValore";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME = "Nome";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI = "Soggetti";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER = "Soggetto";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_VISUALIZZA_DATI_PROVIDER = "Visualizza Dati Soggetto";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP = "Sbustamento SOAP";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO = "Sbustamento";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO = "Sbustamento Protocollo";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX = "Attivazione MessageBox";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT = "Modalit&agrave; di fault";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR = "Fault Actor";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = "Generic Fault Code";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX = "Prefix Fault Code";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO = "Invio per Riferimento";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO = "Risposta per Riferimento";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT = "Utilizzabile come Client";
	
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO = "Servizio Correlato";
	
	public static final String LABEL_PARAMETRO_APPLICATIVI_DI = "Applicativi di ";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI = "Servizi Applicativi di ";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI = "Ruoli di ";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVOCAZIONE_SERVIZIO_DI = "Invocazione Servizio di ";	
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_ASINCRONA_DI = "Risposta Asincrona di ";
	
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_TIPO = "Tipo";
	
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA = "Propriet&agrave;";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME = "Nome";
	public static final String LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE = CostantiControlStation.LABEL_PARAMETRO_VALORE;
	
	/* DEFAULT VALUE PARAMETRI */
	
	public static final String SERVIZI_APPLICATIVI_FAULT_SOAP = CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP.toString();
	public static final String SERVIZI_APPLICATIVI_FAULT_XML = CostantiConfigurazione.ERRORE_APPLICATIVO_XML.toString();
	public static final String[] SERVIZI_APPLICATIVI_FAULT = { SERVIZI_APPLICATIVI_FAULT_SOAP, SERVIZI_APPLICATIVI_FAULT_XML };
	
	public static final String SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public static final String SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public static final String[] SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE = { SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_ABILITATO, 
		SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE_DISABILITATO };
	
	public static final String SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public static final String SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public static final String[] SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO = { SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_ABILITATO, 
		SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO_DISABILITATO };
	
	public static final String SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_ABILITATO = CostantiConfigurazione.ABILITATO.toString();
	public static final String SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO = CostantiConfigurazione.DISABILITATO.toString();
	public static final String[] SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO = { SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_ABILITATO, 
		SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO };
		
	public static final String LABEL_PARAMETRO_FILTRO_RUOLO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public static final String SERVIZI_APPLICATIVI_RUOLO_NON_CONFIGURATO = "Non Configurato";
	public static final String SERVIZI_APPLICATIVI_RUOLO_FRUITORE = Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE;
	public static final String SERVIZI_APPLICATIVI_RUOLO_EROGATORE = Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE;
	public static final String[] SERVIZI_APPLICATIVI_RUOLO = { SERVIZI_APPLICATIVI_RUOLO_FRUITORE, 
		SERVIZI_APPLICATIVI_RUOLO_EROGATORE };
	
	public static final String LABEL_PARAMETRO_FILTRO_TIPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public static final String VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER = CostantiConfigurazione.SERVER;
	public static final String VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT = CostantiConfigurazione.CLIENT;
	public static final String VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI = CostantiConfigurazione.CLIENT_OR_SERVER;
	public static final String SERVIZI_APPLICATIVI_TIPO_NON_CONFIGURATO = "Non Configurato";
	public static final String LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT = "Client";
	public static final String LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER = "Server";
	public static final String[] LABELS_SERVIZI_APPLICATIVI_TIPO = { LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT, LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER };
	public static final String[] VALUES_SERVIZI_APPLICATIVI_TIPO = { VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT, VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER };
	
	/* ATTRIBUTI */
	
	public static final String ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT;
	public static final int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_NONE;
	public static final int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_SOGGETTO;
	public static final int ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE = CostantiControlStation.ATTRIBUTO_CONFIGURAZIONE_PARENT_CONFIGURAZIONE;
	
	public static String getLabelSbustamentoProtocollo(String nomeProtocollo) {
		if(Costanti.SPCOOP_PROTOCOL_NAME.equals(nomeProtocollo)) {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " "+ Costanti.SPCOOP_PROTOCOL_LABEL;
		}
		else if(Costanti.MODIPA_PROTOCOL_NAME.equals(nomeProtocollo)) {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " "+ Costanti.MODIPA_PROTOCOL_LABEL;
		}
		else {
			return ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " " +nomeProtocollo.toUpperCase();
		}
	}
	
	public static final String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_VUOTI = "";
	public static final String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SOLO_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public static final String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_CON_PROFILO = "Profilo Interoperabilit&agrave;: {0}, Tipo: {1}";
	public static final String MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SENZA_PROFILO = "Tipo: {0}";
		
	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "L'applicativo non risulta utilizzato in alcuna configurazione";
	
	public static final String MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX = "Dati incompleti. &Egrave; necessario indicare: {0}";
	public static final String MESSAGGIO_ERRORE_LA_PROPRIETA_XX_E_GIA_STATO_ASSOCIATA_AL_SA_YY = "La propriet&agrave; {0} &egrave; gi&agrave; stata associata all''applicativo {1}";
	
	
	
}
