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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.Vector;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;


/**
 * AccordiServizioParteComuneCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneCostanti {

	/* OBJECT NAME */

	public final static String OBJECT_NAME_APC = "accordiServizioParteComune";
	public final static String OBJECT_NAME_ASC = "accordiServiziComposto"; // usati per gli archivi
	public final static ForwardParams TIPO_OPERAZIONE_WSDL_CHANGE = ForwardParams.OTHER("WSDLChange");

	public final static String OBJECT_NAME_APC_AZIONI = "accordiServizioParteComuneAzioni";

	public final static String OBJECT_NAME_APC_PORT_TYPES = "accordiServizioParteComunePortTypes";

	public final static String OBJECT_NAME_APC_PORT_TYPE_OPERATIONS = "accordiServizioParteComunePortTypeOperations";

	public final static String OBJECT_NAME_APC_ALLEGATI = "accordiServizioParteComuneAllegati";
	public final static ForwardParams TIPO_OPERAZIONE_VIEW = ForwardParams.OTHER("View");

	public final static String OBJECT_NAME_APC_EROGATORI = "accordiServizioParteComuneErogatori";

	public final static String OBJECT_NAME_APC_COMPONENTI = "accordiServizioParteComuneComponenti";

	public final static String OBJECT_NAME_ACCORDI_COOPERAZIONE = "accordiCooperazione";
	
	public final static String OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE = "accordiServizioParteComunePortTypeOperationsMessage";
	
	public final static String OBJECT_NAME_APC_RESOURCES = "accordiServizioParteComuneResources";
	
	public final static String OBJECT_NAME_APC_RESOURCES_RISPOSTE = "accordiServizioParteComuneResourcesRisposte";
	public final static String OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS = "accordiServizioParteComuneResourcesRepresentation";
	public final static String OBJECT_NAME_APC_RESOURCES_PARAMETERS = "accordiServizioParteComuneResourcesParameters";

	/* SERVLET NAME */

	public final static String SERVLET_NAME_APC_ADD = OBJECT_NAME_APC+"Add.do";
	public final static String SERVLET_NAME_APC_CHANGE = OBJECT_NAME_APC+"Change.do";
	public final static String SERVLET_NAME_APC_DELETE = OBJECT_NAME_APC+"Del.do";
	public final static String SERVLET_NAME_APC_LIST = OBJECT_NAME_APC+"List.do";
	public final static String SERVLET_NAME_APC_WSDL_CHANGE = OBJECT_NAME_APC+"WSDLChange.do";
	public final static Vector<String> SERVLET_APC = new Vector<String>();
	static{
		SERVLET_APC.add(SERVLET_NAME_APC_ADD);
		SERVLET_APC.add(SERVLET_NAME_APC_CHANGE);
		SERVLET_APC.add(SERVLET_NAME_APC_DELETE);
		SERVLET_APC.add(SERVLET_NAME_APC_LIST);
		SERVLET_APC.add(SERVLET_NAME_APC_WSDL_CHANGE);
	}

	public final static String SERVLET_NAME_APC_AZIONI_ADD = OBJECT_NAME_APC_AZIONI+"Add.do";
	public final static String SERVLET_NAME_APC_AZIONI_CHANGE = OBJECT_NAME_APC_AZIONI+"Change.do";
	public final static String SERVLET_NAME_APC_AZIONI_DELETE = OBJECT_NAME_APC_AZIONI+"Del.do";
	public final static String SERVLET_NAME_APC_AZIONI_LIST = OBJECT_NAME_APC_AZIONI+"List.do";
	public final static Vector<String> SERVLET_APC_AZIONI = new Vector<String>();
	static{
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_ADD);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_CHANGE);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_DELETE);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_LIST);
	}

	public final static String SERVLET_NAME_APC_PORT_TYPES_ADD = OBJECT_NAME_APC_PORT_TYPES+"Add.do";
	public final static String SERVLET_NAME_APC_PORT_TYPES_CHANGE = OBJECT_NAME_APC_PORT_TYPES+"Change.do";
	public final static String SERVLET_NAME_APC_PORT_TYPES_DELETE = OBJECT_NAME_APC_PORT_TYPES+"Del.do";
	public final static String SERVLET_NAME_APC_PORT_TYPES_LIST = OBJECT_NAME_APC_PORT_TYPES+"List.do";
	public final static Vector<String> SERVLET_APC_PORT_TYPES = new Vector<String>();
	static{
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_ADD);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_CHANGE);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_DELETE);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_LIST);
	}

	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_ADD = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+"Add.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+"Change.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_DELETE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+"Del.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+"List.do";
	public final static Vector<String> SERVLET_APC_PORT_TYPE_OPERATIONS = new Vector<String>();
	static{
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_ADD);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_DELETE);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST);
	}

	public final static String SERVLET_NAME_APC_ALLEGATI_ADD = OBJECT_NAME_APC_ALLEGATI+"Add.do";
	public final static String SERVLET_NAME_APC_ALLEGATI_CHANGE = OBJECT_NAME_APC_ALLEGATI+"Change.do";
	public final static String SERVLET_NAME_APC_ALLEGATI_DELETE = OBJECT_NAME_APC_ALLEGATI+"Del.do";
	public final static String SERVLET_NAME_APC_ALLEGATI_LIST = OBJECT_NAME_APC_ALLEGATI+"List.do";
	public final static String SERVLET_NAME_APC_ALLEGATI_VIEW = OBJECT_NAME_APC_ALLEGATI+"View.do";
	public final static Vector<String> SERVLET_APC_ALLEGATI = new Vector<String>();
	static{
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_ADD);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_CHANGE);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_DELETE);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_LIST);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_VIEW);
	}

	public final static String SERVLET_NAME_APC_EROGATORI_LIST = OBJECT_NAME_APC_EROGATORI+"List.do";
	public final static Vector<String> SERVLET_APC_EROGATORI = new Vector<String>();
	static{
		SERVLET_APC_EROGATORI.add(SERVLET_NAME_APC_EROGATORI_LIST);
	}

	public final static String SERVLET_NAME_APC_COMPONENTI_ADD = OBJECT_NAME_APC_COMPONENTI+"Add.do";
	public final static String SERVLET_NAME_APC_COMPONENTI_DELETE = OBJECT_NAME_APC_COMPONENTI+"Del.do";
	public final static String SERVLET_NAME_APC_COMPONENTI_LIST = OBJECT_NAME_APC_COMPONENTI+"List.do";
	public final static Vector<String> SERVLET_APC_COMPONENTI = new Vector<String>();
	static{
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_ADD);
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_DELETE);
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_LIST);
	}

	public final static String SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE = OBJECT_NAME_ACCORDI_COOPERAZIONE+"Change.do";
	
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_ADD = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+"Add.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_CHANGE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+"Change.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_DELETE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+"Del.do";
	public final static String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+"List.do";
	public final static Vector<String> SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE = new Vector<String>();
	static{
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_ADD);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_CHANGE);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_DELETE);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST);
	}

	public final static String SERVLET_NAME_APC_RESOURCES_ADD = OBJECT_NAME_APC_RESOURCES+"Add.do";
	public final static String SERVLET_NAME_APC_RESOURCES_CHANGE = OBJECT_NAME_APC_RESOURCES+"Change.do";
	public final static String SERVLET_NAME_APC_RESOURCES_DELETE = OBJECT_NAME_APC_RESOURCES+"Del.do";
	public final static String SERVLET_NAME_APC_RESOURCES_LIST = OBJECT_NAME_APC_RESOURCES+"List.do";
	public final static Vector<String> SERVLET_APC_RESOURCES = new Vector<String>();
	static{
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_ADD);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_CHANGE);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_DELETE);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_LIST);
	}
	
	public final static String SERVLET_NAME_APC_RESOURCES_RISPOSTE_ADD = OBJECT_NAME_APC_RESOURCES_RISPOSTE+"Add.do";
	public final static String SERVLET_NAME_APC_RESOURCES_RISPOSTE_CHANGE = OBJECT_NAME_APC_RESOURCES_RISPOSTE+"Change.do";
	public final static String SERVLET_NAME_APC_RESOURCES_RISPOSTE_DELETE = OBJECT_NAME_APC_RESOURCES_RISPOSTE+"Del.do";
	public final static String SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST = OBJECT_NAME_APC_RESOURCES_RISPOSTE+"List.do";
	public final static Vector<String> SERVLET_APC_RESOURCES_RISPOSTE = new Vector<String>();
	static{
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_ADD);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_CHANGE);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_DELETE);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST);
	}
	
	public final static String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_ADD = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+"Add.do";
	public final static String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_CHANGE = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+"Change.do";
	public final static String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_DELETE = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+"Del.do";
	public final static String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_LIST = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+"List.do";
	public final static Vector<String> SERVLET_APC_RESOURCES_REPRESENTATIONS = new Vector<String>();
	static{
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_ADD);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_CHANGE);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_DELETE);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_LIST);
	}
	
	public final static String SERVLET_NAME_APC_RESOURCES_PARAMETERS_ADD = OBJECT_NAME_APC_RESOURCES_PARAMETERS+"Add.do";
	public final static String SERVLET_NAME_APC_RESOURCES_PARAMETERS_CHANGE = OBJECT_NAME_APC_RESOURCES_PARAMETERS+"Change.do";
	public final static String SERVLET_NAME_APC_RESOURCES_PARAMETERS_DELETE = OBJECT_NAME_APC_RESOURCES_PARAMETERS+"Del.do";
	public final static String SERVLET_NAME_APC_RESOURCES_PARAMETERS_LIST = OBJECT_NAME_APC_RESOURCES_PARAMETERS+"List.do";
	public final static Vector<String> SERVLET_APC_RESOURCES_PARAMETERS = new Vector<String>();
	static{
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_ADD);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_CHANGE);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_DELETE);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_LIST);
	}
	

	/* LABEL GENERALI */

	public final static String LABEL_APC = "API";
	public final static String LABEL_ASC = "API (Servizio Composto)";
	public final static String LABEL_ACCORDI_EROGATORI_DI = "Erogazioni di ";
	public final static String LABEL_ACCORDI_EROGATORI = "Erogazioni";
	public final static String LABEL_ACCORDI_FRUITORI_DI = "Fruizioni di ";
	public final static String LABEL_ACCORDI_FRUITORI = "Fruizioni";
	public final static String LABEL_AZIONI = "Azioni";
	public final static String LABEL_AZIONE = "Azione";
	public final static String LABEL_PORT_TYPES = "Servizi";
	public final static String LABEL_PORT_TYPE = "Servizio";
	public final static String LABEL_ALLEGATI = "Allegati";
	public final static String LABEL_ALLEGATO = "Allegato";
	public final static String LABEL_SERVIZI_COMPONENTI = "Servizi Componenti";
	public final static String LABEL_COMPONENTE = "Componente";
	public final static String LABEL_SPECIFICA_INTERFACCE = "Specifica delle interfacce";
	public final static String LABEL_SPECIFICA_CONVERSAZIONI = "Specifica delle conversazioni";
	public final static String LABEL_INFORMAZIONI = "Informazioni Protocollo";
	public final static String LABEL_INFORMAZIONI_WSDL = "Informazioni WSDL";
	public final static String LABEL_INFORMAZIONI_WSDL_PART = "Informazioni WSDL Part";
	public final static String LABEL_STYLE = "Style";
	public final static String LABEL_APC_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_APC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE.name()+"')";
	public final static String LABEL_ASC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_COMPOSTO.name()+"')";
	public final static String LABEL_AZIONE_CORRELATA = "Azione correlata";
	public final static String LABEL_PROFILO_PROTOCOLLO = "Profilo";
	public final static String LABEL_CORRELATA_AL_SERVIZIO = "Correlata al servizio";
	public final static String LABEL_CORRELATA_A_AZIONE = "Correlata all'azione";
	public final static String LABEL_CORRELAZIONE_ASINCRONA = "Correlazione asincrona";
	public final static String LABEL_DOWNLOAD = "Download";
	public final static String LABEL_XSD_SCHEMA_COLLECTION = "XSD Schema Collection";
	public final static String LABEL_SOGGETTO = "Soggetto Erogatore";
	public final static String LABEL_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "Servizio";
	public final static String LABEL_FRUITORI = "Fruitori";
	public final static String LABEL_FRUITORE = "Fruitore";
	public final static String LABEL_POLITICHE_SLA = "Politiche SLA";
	public final static String LABEL_NON_DISPONIBILE = "non disp.";
	public final static String LABEL_WSDL_CHANGE_CLEAR_WARNING = "Attenzione";
	public final static String LABEL_WSDL_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Salva' senza selezionare alcun file"; //fornirne un'altra versione";
	public final static String LABEL_COMPONENTI = "Componenti";
	public final static String LABEL_WSDL_AGGIORNAMENTO = "Aggiornamento";
	public final static String LABEL_WSDL_NOT_FOUND = "non fornito";
	public final static String LABEL_WSDL_ATTUALE = "Attuale";
	public final static String LABEL_WSDL_NUOVO = "Nuovo";
	public final static String LABEL_INTERFACCIA = "Interfaccia";

	public final static String LABEL_APC_MENU_VISUALE_AGGREGATA = "API";
	public final static String LABEL_ASC_MENU_VISUALE_AGGREGATA = "API (Servizio Composto)";
	public final static String LABEL_OPERATION_MESSAGE = "Message";
	public final static String LABEL_OPERATION_MESSAGE_INPUT = "Message Input";
	public final static String LABEL_OPERATION_MESSAGE_OUTPUT = "Message Output";
	
	public final static String LABEL_RISORSE = "Risorse";
	public final static String LABEL_RISORSA = "Risorsa";
	public final static String LABEL_APC_RESOURCES_RICHIESTA = "Richiesta";
	public final static String LABEL_APC_RESOURCES_RISPOSTA = "Risposta";
	public final static String LABEL_RISPOSTE = "Risposte";
	public final static String LABEL_RISPOSTA = "Risposta";
	public final static String LABEL_REPRESENTATION = "Rappresentazione";
	public final static String LABEL_REPRESENTATION_DEFINIZIONE = "Definizione";
	public final static String LABEL_PARAMETERS = "Parametri";
	public final static String LABEL_PARAMETER = "Parametro";
	
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE = "Completare la configurazione dell'API appena creata ";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE = "Completare la configurazione dell'API appena modificata ";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_REST_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno una risorsa";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_REST_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno una risorsa";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un servizio";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un servizio";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZIO_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un'azione per il servizio";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZIO_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un'azione per il servizio";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZI_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un'azione nei servizi che non la possiedono";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZI_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un'azione nei servizi che non la possiedono";
	

	/* PARAMETRI */

	public final static String PARAMETRO_APC_ID = "id";
	public final static String PARAMETRO_APC_NOME = "nome";
	public final static String PARAMETRO_APC_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_APC_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public final static String PARAMETRO_APC_DESCRIZIONE = "descr";
	public final static String PARAMETRO_APC_TIPO_WSDL = "tipo";
	public final static String PARAMETRO_APC_WSDL = "wsdl";
	public final static String PARAMETRO_APC_WSDL_WARN = "wsdlWarn";
	public final static String PARAMETRO_APC_WSDL_DEFINITORIO = "wsdldef";
	public final static String PARAMETRO_APC_WSDL_CONCETTUALE = "wsdlconc";
	public final static String PARAMETRO_APC_WSDL_EROGATORE = "wsdlserv";
	public final static String PARAMETRO_APC_WSDL_FRUITORE = "wsdlservcorr";
	public final static String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "wsblconc";
	public final static String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE = "wsblserv";
	public final static String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE = "wsblservcorr";
	public final static String PARAMETRO_APC_PROFILO_COLLABORAZIONE = "profcoll";
	public final static String PARAMETRO_APC_FILTRO_DUPLICATI = "filtrodup";
	public final static String PARAMETRO_APC_CONFERMA_RICEZIONE = "confric";
	public final static String PARAMETRO_APC_COLLABORAZIONE = "idcoll";
	public final static String PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA = "idRifReq";
	public final static String PARAMETRO_APC_CONSEGNA_ORDINE = "consord";
	public final static String PARAMETRO_APC_SCADENZA = "scadenza";
	public final static String PARAMETRO_APC_REFERENTE = "referente";
	public final static String PARAMETRO_APC_REFERENTE_1_2 = "referenteLabelAccordo1.2";
	public final static String PARAMETRO_APC_VERSIONE = "versione";
	public final static String PARAMETRO_APC_PRIVATO = "privato";
	public final static String PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA = "privatoLabel";
	public final static String PARAMETRO_APC_IS_SERVIZIO_COMPOSTO = "isServizioComposto";
	public final static String PARAMETRO_APC_ACCORDO_COOPERAZIONE = "accordoCooperazione";
	public final static String PARAMETRO_APC_STATO_PACKAGE = "stato";
	public final static String PARAMETRO_APC_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public final static String PARAMETRO_APC_UTILIZZO_SENZA_AZIONE = "utilizzoSenzaAzione";
	public final static String PARAMETRO_VERSIONE_PROTOCOLLO = "profilo";
	public final static String PARAMETRO_CLIENT_AUTH = "clientAuth";
	public final static String PARAMETRO_APC_RIPRISTINA_STATO = "backToStato";
	public final static String PARAMETRO_APC_SERVICE_BINDING = CostantiControlStation.PARAMETRO_SERVICE_BINDING;
	public final static String PARAMETRO_APC_MESSAGE_TYPE = CostantiControlStation.PARAMETRO_MESSAGE_TYPE;
	public final static String PARAMETRO_APC_INTERFACE_TYPE = CostantiControlStation.PARAMETRO_INTERFACE_TYPE;
	public final static String PARAMETRO_APC_SERVICE_BINDING_SEARCH = CostantiControlStation.PARAMETRO_SERVICE_BINDING_SEARCH;
	public final static String PARAMETRO_APC_GRUPPI = "gruppi";
	public final static String PARAMETRO_APC_GRUPPO = "gruppo";
	public final static String PARAMETRO_APC_GRUPPI_SUPPORTO_COLORI = "__gruppi_colors";
	public final static String PARAMETRO_APC_API_NUOVA_VERSIONE = "apiNewVersion";
	public final static String PARAMETRO_APC_API_NUOVA_VERSIONE_MIN = "apiNewVersionMin";
	public final static String PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA = "apiNewVersionRedefine";
	public final static String PARAMETRO_APC_API_NUOVA_VERSIONE_OLD_ID_APC = "apiNewVersionOldIdApc";
	public final static String PARAMETRO_APC_CANALE = CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE;
	public final static String PARAMETRO_APC_CANALE_STATO = CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO;

	public final static String PARAMETRO_APC_AZIONI_NOME = "nomeaz";
	public final static String PARAMETRO_APC_AZIONI_CORRELATA = "azicorr";
	public final static String PARAMETRO_APC_AZIONI_PROFILO_BUSTA = "profBusta";
	public final static String PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE = "profcollaz";
	public final static String PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI = "filtrodupaz";
	public final static String PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE = "confricaz";
	public final static String PARAMETRO_APC_AZIONI_COLLABORAZIONE = "idcollaz";
	public final static String PARAMETRO_APC_AZIONI_ID_RIFERIMENTO_RICHIESTA = "idRifReqAz";
	public final static String PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE = "consordaz";
	public final static String PARAMETRO_APC_AZIONI_SCADENZA = "scadenzaaz";

	public final static String PARAMETRO_APC_PORT_TYPES_NOME = "nomept";
	public final static String PARAMETRO_APC_PORT_TYPES_MESSAGE_TYPE = "messageTypePT";
	public final static String PARAMETRO_APC_PORT_TYPES_PROFILO_BUSTA = "profBusta";
	public final static String PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE = "profcollpt";
	public final static String PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI = "filtroduppt";
	public final static String PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE = "confricpt";
	public final static String PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE = "idcollpt";
	public final static String PARAMETRO_APC_PORT_TYPES_ID_RIFERIMENTO_RICHIESTA = "idRifReqPt";
	public final static String PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE = "consordpt";
	public final static String PARAMETRO_APC_PORT_TYPES_SCADENZA = "scadenzapt";
	public final static String PARAMETRO_APC_PORT_TYPES_STYLE = "stylept";

	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_NOME = "nomeop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA = "profBusta";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_CORRELATA = "opcorrelata";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = "servcorr";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA = "azicorr";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE = "profcollop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_FILTRO_DUPLICATI = "filtrodupop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_CONFERMA_RICEZIONE = "confricop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_COLLABORAZIONE = "idcollop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_ID_RIFERIMENTO_RICHIESTA = "idRifReqOp";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_CONSEGNA_ORDINE = "consordop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_SCADENZA = "scadenzaop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_USE = "useop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE = "typeop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL = "nswsdlop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE = "styleop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION = "soapactionop";
	
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE = "msgop";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = "msgparttype";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME = "msgpartname";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME = "msgpartlocalname";
	public final static String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS = "msgpartns";

	public final static String PARAMETRO_APC_ALLEGATI_RUOLO = "ruolo";
	public final static String PARAMETRO_APC_ALLEGATI_TIPO_FILE = "tipoFile";
	public final static String PARAMETRO_APC_ALLEGATI_DOCUMENTO = "theFile";
	public final static String PARAMETRO_APC_ALLEGATI_DOCUMENTO_VIEW = "documento";
	public final static String PARAMETRO_APC_ALLEGATI_ID_ALLEGATO = ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO;
	public final static String PARAMETRO_APC_ALLEGATI_ID_ACCORDO = ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO;
	public final static String PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO = "nomeDoc";

	public final static String PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO = "tipoprov";
	public final static String PARAMETRO_APC_EROGATORI_NOME_SOGGETTO = "nomeprov";
	public final static String PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO = "tiposervizio";
	public final static String PARAMETRO_APC_EROGATORI_NOME_SERVIZIO = "nomeservizio";

	public final static String PARAMETRO_APC_EROGATORI_NOME_ACCORDO = "nomeAccordo";
	public final static String PARAMETRO_APC_EROGATORI_CORRELATO = "correlato";
	public final static String PARAMETRO_APC_EROGATORI_ID_FRUITORE = "myId";
	public final static String PARAMETRO_APC_EROGATORI_EROGATORE = "provider";

	public final static String PARAMETRO_APC_COMPONENTI_TIPO_SICA = "tipoSICA" ;
	public final static String PARAMETRO_APC_COMPONENTI_ELEMENTO = "elemento" ;
	public final static String PARAMETRO_APC_COMPONENTI_COMPONENTE = "componente";
	public final static String PARAMETRO_APC_ACCETTA_MODIFICHE = "accMod";

	public final static String PARAMETRO_APC_WSDL_CHANGE_TMP = "apcWsdlTMP";
	
	public final static String PARAMETRO_APC_RESOURCES_ID = "idRs";
	public final static String PARAMETRO_APC_RESOURCES_NOME = "nomeRs";
	public final static String PARAMETRO_APC_RESOURCES_DESCRIZIONE = "descrizioneRs";
	public final static String PARAMETRO_APC_RESOURCES_PATH = "pathRs";
	public final static String PARAMETRO_APC_RESOURCES_HTTP_METHOD = "httpMethodRs";
	public final static String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE = PARAMETRO_APC_MESSAGE_TYPE;
	public final static String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_REQUEST = "messageTypeReq";
	public final static String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_RESPONSE = "messageTypeRes";
	
	public final static String PARAMETRO_APC_RESOURCE_REQUEST = "req";
	
	public final static String PARAMETRO_APC_RESOURCES_RESPONSE_ID = "idResRisposta";
	public final static String PARAMETRO_APC_RESOURCES_RESPONSE_STATUS = "statusResRisposta";
	public final static String PARAMETRO_APC_RESOURCES_RESPONSE_DESCRIZIONE = "descrizioneResRisposta";
	
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_ID = "idResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_MEDIA_TYPE = "mediaTypeResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_MESSAGE_TYPE = "messageTypeResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_NOME = "nomeResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_DESCRIZIONE = "descrResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO = "tipoResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO = "xmlTipoResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAME = "xmlNameResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAMESPACE = "xmlNamespaceResRepres";
	public final static String PARAMETRO_APC_RESOURCES_REPRESENTATION_JSON_TYPE = "jsonTypeResRepres";
	
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_ID = "idResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_NOME = "nomeResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_DESCRIZIONE = "descrResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO = "tipoParametroResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_REQUIRED = "requiredResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_TIPO = "tipoResParam";
	public final static String PARAMETRO_APC_RESOURCES_PARAMETER_RESTRIZIONI = "restrResParam";
	
	/* LABEL PARAMETRI */
	public final static String LABEL_APC_STATO = "Stato";
	public final static String LABEL_PARAMETRO_APC_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APC_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APC_REFERENTE = "Soggetto Referente";
	public final static String LABEL_PARAMETRO_APC_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APC_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA = "Visibilità API";
	public final static String LABEL_PARAMETRO_APC_IS_SERVIZIO_COMPOSTO = "Servizio Composto";
	public final static String LABEL_PARAMETRO_APC_STATO_PACKAGE = CostantiControlStation.LABEL_PARAMETRO_STATO_PACKAGE;
	public final static String LABEL_PARAMETRO_APC_VALIDAZIONE_SPECIFICA = "Validazione Specifica";
	public final static String LABEL_PARAMETRO_APC_VALIDAZIONE_SPECIFICHE = "Validazione Specifiche";
	public final static String LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE = "Accordo Cooperazione";
	public final static String LABEL_PARAMETRO_APC_WSDL_DEFINITORIO = "WSDL Definitorio";
	public final static String LABEL_PARAMETRO_APC_WSDL_CONCETTUALE = "WSDL Concettuale";
	public final static String LABEL_PARAMETRO_APC_WSDL_EROGATORE = "WSDL Logico Erogatore";
	public final static String LABEL_PARAMETRO_APC_WSDL_FRUITORE = "WSDL Logico Fruitore";
	public final static String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "Specifica Concettuale";
	public final static String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE = "Specifica Erogatore";
	public final static String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE = "Specifica Fruitore";
	public final static String LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE = "Profilo di collaborazione";
	public final static String LABEL_PARAMETRO_APC_UTILIZZO_SENZA_AZIONE = "Utilizzo Senza Azione";
	public final static String LABEL_PARAMETRO_APC_FILTRO_DUPLICATI = "Filtro Duplicati";
	public final static String LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE = "Conferma Ricezione";
	public final static String LABEL_PARAMETRO_APC_COLLABORAZIONE = "ID Collaborazione";
	public final static String LABEL_PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA = "Riferimento ID Richiesta";
	public final static String LABEL_PARAMETRO_APC_CONSEGNA_ORDINE = "Consegna in Ordine";
	public final static String LABEL_PARAMETRO_APC_SCADENZA = "Scadenza";
	public final static String LABEL_PARAMETRO_APC_RIPRISTINA_STATO_OPERATIVO = "Ripristina Stato Operativo";
	public final static String LABEL_PARAMETRO_APC_SERVICE_BINDING = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING;
	public final static String LABEL_PARAMETRO_APC_SERVICE_BINDING_SOAP = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
	public final static String LABEL_PARAMETRO_APC_SERVICE_BINDING_REST = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_DEFAULT;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_SOAP_11 = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_11;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_SOAP_12 = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_12;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_XML = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_XML;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_JSON = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_JSON;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_BINARY = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_BINARY;
	public final static String LABEL_PARAMETRO_APC_MESSAGE_TYPE_MIME_MULTIPART = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
	public final static String LABEL_PARAMETRO_APC_INTERFACE_TYPE = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE;
	public final static String LABEL_PARAMETRO_APC_INTERFACE_TYPE_WSDL_11 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11;
	public final static String LABEL_PARAMETRO_APC_INTERFACE_TYPE_WADL = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WADL;
	public final static String LABEL_PARAMETRO_APC_INTERFACE_TYPE_SWAGGER_2 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
	public final static String LABEL_PARAMETRO_APC_INTERFACE_TYPE_OPEN_API_3 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
	public final static String LABEL_PARAMETRO_APC_GRUPPI = GruppiCostanti.LABEL_GRUPPI;
	public final static String LABEL_PARAMETRO_APC_GRUPPO = GruppiCostanti.LABEL_GRUPPO;
	public final static String LABEL_PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA = "Ridefinisci Interfaccia";
	public final static String LABEL_PARAMETRO_APC_CANALE = "Canale";
	public final static String LABEL_PARAMETRO_APC_CANALE_STATO = "Canale";

	public final static String LABEL_PARAMETRO_APC_ALLEGATI_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE = "Tipo";
	public final static String LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO = "Documento";
	public final static String LABEL_PARAMETRO_APC_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public final static String LABEL_PARAMETRO_APC_PROTOCOLLO_COMPATC = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_PARAMETRO_APC_WSDL = "WSDL";
	public final static String LABEL_PARAMETRO_APC_WSDL_LOGICO = "WSDL Logico";
	
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT = "MessageInput";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT = "MessageOutput";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE = "OperationType";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL = "Namespace";
	public final static String LABEL_USE = "Use";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION = "SOAPAction";
	
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE = "Tipo Messaggio";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = "Type";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME = "Name";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME = "Local Name";
	public final static String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS = "Namespace";
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_NOME_NOTE = "Se non definito verr&agrave; automaticamente generato un identificativo univoco";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PATH = "Path";
	private final static String LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO = "Per associare al metodo HTTP selezionato un qualsiasi path è possibile lasciare il campo vuoto o utilizzare il carattere speciale 'CHAR'";
	private final static String LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO_NOME_OBBLIGATORIO = "; in questo caso è obbligatorio indicare il nome della Risorsa";
	public static String getLABEL_PARAMETRO_APC_RESOURCES_PATH_INFO(String charAll, boolean appendNomeObbligatorio) {
		return LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO.replace("CHAR", charAll) + (appendNomeObbligatorio ? LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO_NOME_OBBLIGATORIO : "");
	}
	public final static String LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD = CostantiControlStation.LABEL_PARAMETRO_HTTP_METHOD;
	public final static String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE = "Tipo Messaggio";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_REQUEST = "Tipo Messaggio Richiesta";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_RESPONSE = "Tipo Messaggio Risposta";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_FORMATO_SPECIFICA = "Formato Specifica";
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PATH_QUALSIASI = "*";
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_ID = "ID";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS = "HTTP Status";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_NOTE = "Non fornire alcun valore per indicare uno stato di default";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_DEFAULT = "Default";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_DESCRIZIONE = "Descrizione";
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_ID = "ID";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_MEDIA_TYPE = "Media Type";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_MESSAGE_TYPE = "Message Type";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO = "Schema";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_XML = "Xml";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_JSON = "Json";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_NON_DEFINITO = "Non Definito";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_ELEMENT = "Element";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_TYPE = "Type";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAME = "Nome";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAMESPACE = "Namespace";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_JSON_TYPE = "Tipo";
	
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_ID = "ID";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO = "Tipo Parametro";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_REQUIRED = "Obbligatorio";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_RESTRIZIONI = "Restrizioni";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_COOKIE = "Cookie";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_DYNAMIC_PATH = "Dynamic Path";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_FORM = "Form";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_HEADER = "Header";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_QUERY = "Query";
	
	public final static String LABEL_DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO = CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_RIDEFINITO;
	public final static String LABEL_DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT = CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT;
	
	public final static String LABEL_SI = "Si";
	public final static String LABEL_NO = "No";
	
	/* DEFAULT VALUE PARAMETRI */

	public final static String TIPO_WSDL_CONCETTUALE = "Concettuale";
	public final static String TIPO_WSDL_EROGATORE = "LogicoErogatore";
	public final static String TIPO_WSDL_FRUITORE = "LogicoFruitore";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPES_STYLE = BindingStyle.DOCUMENT.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE = "0";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE = BindingUse.LITERAL.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN = "input";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT =  "inputOutput";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE = "type";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT = "element";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT = "i";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT = "o";
	
	public final static String PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE = "apc";
	public final static String PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "asc";

	public final static String INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO = CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO;
	public final static String INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT = CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT;
	public final static String[] INFORMAZIONI_PROTOCOLLO_MODALITA = { CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO };

	public final static String INFORMAZIONI_CLIENT_AUTH_DEFAULT = "default";

	public final static String LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO = "profilo API";
	public final static String[] LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO = { "usa profilo API", "ridefinisci" };

	public final static String LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_PORT_TYPE = "profilo servizio";
	public final static String[] LABEL_INFORMAZIONI_PROTOCOLLO_PORT_TYPE = { "usa profilo servizio", "ridefinisci" };

	public final static String INFORMATIVA_VISIBILITA_PRIVATA = "privata";
	public final static String INFORMATIVA_VISIBILITA_PUBBLICA = "pubblica";

	public final static String TIPO_PROFILO_COLLABORAZIONE_ONEWAY = CostantiRegistroServizi.ONEWAY.toString();
	public final static String TIPO_PROFILO_COLLABORAZIONE_SINCRONO = CostantiRegistroServizi.SINCRONO.toString();
	public final static String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = CostantiRegistroServizi.ASINCRONO_SIMMETRICO.toString();
	public final static String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString();
	public final static String[] TIPI_PROFILI_COLLABORAZIONE = { TIPO_PROFILO_COLLABORAZIONE_ONEWAY, TIPO_PROFILO_COLLABORAZIONE_SINCRONO,
		TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO };

	public final static String[] PORT_TYPES_STYLE = { BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue()	 };
	public final static String[] LABEL_PORT_TYPES_STYLE = { BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue() };
	
	public final static String[] PORT_TYPES_OPERATION_STYLE = { "0", BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue()	 };
	public final static String[] LABEL_PORT_TYPES_OPERATION_STYLE = { "Usa style del servizio",BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue() };
	
	public final static String[] PORT_TYPES_OPERATION_USE = {   BindingUse.LITERAL.getValue(), BindingUse.ENCODED.getValue()	 };
	public final static String[] LABEL_PORT_TYPES_OPERATION_USE = {  BindingUse.LITERAL.getValue(), BindingUse.ENCODED.getValue()};
	
	public final static String[] PORT_TYPE_OPERATION_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT };
	public final static String[] LABEL_PORT_TYPE_OPERATION_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT  };
	
	public final static String[] PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE };
	public final static String[] LABEL_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE  };
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_QUALSIASI = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
		
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_DEFAULT;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_SOAP_11 = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_11;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_SOAP_12 = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_12;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_XML = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_XML;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_JSON = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_JSON;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_BINARY = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_BINARY;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_MIME_MULTIPART = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
	
	public final static String VALUE_PARAMETRO_APC_INTERFACE_TYPE_WSDL_11 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_WSDL_11;
	public final static String VALUE_PARAMETRO_APC_INTERFACE_TYPE_WADL = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_WADL;
	public final static String VALUE_PARAMETRO_APC_INTERFACE_TYPE_SWAGGER_2 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
	public final static String VALUE_PARAMETRO_APC_INTERFACE_TYPE_OPEN_API_3 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST;
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP;
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI = CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE;
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_XML = "xml";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_JSON = "json";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_NON_DEFINITO = "nd";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_COOKIE = "cookie";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_DYNAMIC_PATH = "dynamicPath";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_FORM = "form";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_HEADER = "header";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_QUERY = "query";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_ELEMENT = "element";
	public final static String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_TYPE = "type";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO;
	public final static String DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT;
	
	public final static String[] VALUES_PARAMETRO_APC_CANALE_STATO = CostantiControlStation.VALUES_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO;
}

