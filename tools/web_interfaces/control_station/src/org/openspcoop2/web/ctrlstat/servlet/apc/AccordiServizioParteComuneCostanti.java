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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.protocol.engine.constants.Costanti;
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
	
	private AccordiServizioParteComuneCostanti() {}

	/* OBJECT NAME */

	public static final String OBJECT_NAME_APC = "accordiServizioParteComune";
	public static final String OBJECT_NAME_ASC = "accordiServiziComposto"; // usati per gli archivi
	public static final ForwardParams TIPO_OPERAZIONE_WSDL_CHANGE = ForwardParams.OTHER("WSDLChange");

	public static final String OBJECT_NAME_APC_AZIONI = "accordiServizioParteComuneAzioni";

	public static final String OBJECT_NAME_APC_PORT_TYPES = "accordiServizioParteComunePortTypes";

	public static final String OBJECT_NAME_APC_PORT_TYPE_OPERATIONS = "accordiServizioParteComunePortTypeOperations";

	public static final String OBJECT_NAME_APC_ALLEGATI = "accordiServizioParteComuneAllegati";
	public static final ForwardParams TIPO_OPERAZIONE_VIEW = ForwardParams.OTHER("View");

	public static final String OBJECT_NAME_APC_EROGATORI = "accordiServizioParteComuneErogatori";

	public static final String OBJECT_NAME_APC_COMPONENTI = "accordiServizioParteComuneComponenti";

	public static final String OBJECT_NAME_ACCORDI_COOPERAZIONE = "accordiCooperazione";
	
	public static final String OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE = "accordiServizioParteComunePortTypeOperationsMessage";
	
	public static final String OBJECT_NAME_APC_RESOURCES = "accordiServizioParteComuneResources";
	
	public static final String OBJECT_NAME_APC_RESOURCES_RISPOSTE = "accordiServizioParteComuneResourcesRisposte";
	public static final String OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS = "accordiServizioParteComuneResourcesRepresentation";
	public static final String OBJECT_NAME_APC_RESOURCES_PARAMETERS = "accordiServizioParteComuneResourcesParameters";
	
	/* SERVLET NAME */

	public static final String SERVLET_NAME_APC_ADD = OBJECT_NAME_APC+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_CHANGE = OBJECT_NAME_APC+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_DELETE = OBJECT_NAME_APC+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_LIST = OBJECT_NAME_APC+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_APC_WSDL_CHANGE = OBJECT_NAME_APC+"WSDLChange.do";
	public static final List<String> SERVLET_APC = new ArrayList<>();
	static{
		SERVLET_APC.add(SERVLET_NAME_APC_ADD);
		SERVLET_APC.add(SERVLET_NAME_APC_CHANGE);
		SERVLET_APC.add(SERVLET_NAME_APC_DELETE);
		SERVLET_APC.add(SERVLET_NAME_APC_LIST);
		SERVLET_APC.add(SERVLET_NAME_APC_WSDL_CHANGE);
	}

	public static final String SERVLET_NAME_APC_AZIONI_ADD = OBJECT_NAME_APC_AZIONI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_AZIONI_CHANGE = OBJECT_NAME_APC_AZIONI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_AZIONI_DELETE = OBJECT_NAME_APC_AZIONI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_AZIONI_LIST = OBJECT_NAME_APC_AZIONI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_AZIONI = new ArrayList<>();
	static{
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_ADD);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_CHANGE);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_DELETE);
		SERVLET_APC_AZIONI.add(SERVLET_NAME_APC_AZIONI_LIST);
	}

	public static final String SERVLET_NAME_APC_PORT_TYPES_ADD = OBJECT_NAME_APC_PORT_TYPES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_PORT_TYPES_CHANGE = OBJECT_NAME_APC_PORT_TYPES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_PORT_TYPES_DELETE = OBJECT_NAME_APC_PORT_TYPES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_PORT_TYPES_LIST = OBJECT_NAME_APC_PORT_TYPES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_PORT_TYPES = new ArrayList<>();
	static{
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_ADD);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_CHANGE);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_DELETE);
		SERVLET_APC_PORT_TYPES.add(SERVLET_NAME_APC_PORT_TYPES_LIST);
	}

	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_ADD = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_DELETE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_PORT_TYPE_OPERATIONS = new ArrayList<>();
	static{
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_ADD);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_DELETE);
		SERVLET_APC_PORT_TYPE_OPERATIONS.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST);
	}

	public static final String SERVLET_NAME_APC_ALLEGATI_ADD = OBJECT_NAME_APC_ALLEGATI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_ALLEGATI_CHANGE = OBJECT_NAME_APC_ALLEGATI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_ALLEGATI_DELETE = OBJECT_NAME_APC_ALLEGATI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_ALLEGATI_LIST = OBJECT_NAME_APC_ALLEGATI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_APC_ALLEGATI_VIEW = OBJECT_NAME_APC_ALLEGATI+"View.do";
	public static final List<String> SERVLET_APC_ALLEGATI = new ArrayList<>();
	static{
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_ADD);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_CHANGE);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_DELETE);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_LIST);
		SERVLET_APC_ALLEGATI.add(SERVLET_NAME_APC_ALLEGATI_VIEW);
	}

	public static final String SERVLET_NAME_APC_EROGATORI_LIST = OBJECT_NAME_APC_EROGATORI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_EROGATORI = new ArrayList<>();
	static{
		SERVLET_APC_EROGATORI.add(SERVLET_NAME_APC_EROGATORI_LIST);
	}

	public static final String SERVLET_NAME_APC_COMPONENTI_ADD = OBJECT_NAME_APC_COMPONENTI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_COMPONENTI_DELETE = OBJECT_NAME_APC_COMPONENTI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_COMPONENTI_LIST = OBJECT_NAME_APC_COMPONENTI+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_COMPONENTI = new ArrayList<>();
	static{
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_ADD);
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_DELETE);
		SERVLET_APC_COMPONENTI.add(SERVLET_NAME_APC_COMPONENTI_LIST);
	}

	public static final String SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE = OBJECT_NAME_ACCORDI_COOPERAZIONE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_ADD = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_CHANGE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_DELETE = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST = OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE = new ArrayList<>();
	static{
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_ADD);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_CHANGE);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_DELETE);
		SERVLET_APC_PORT_TYPE_OPERATIONS_MESSAGE.add(SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST);
	}

	public static final String SERVLET_NAME_APC_RESOURCES_ADD = OBJECT_NAME_APC_RESOURCES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_RESOURCES_CHANGE = OBJECT_NAME_APC_RESOURCES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_RESOURCES_DELETE = OBJECT_NAME_APC_RESOURCES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_RESOURCES_LIST = OBJECT_NAME_APC_RESOURCES+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_RESOURCES = new ArrayList<>();
	static{
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_ADD);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_CHANGE);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_DELETE);
		SERVLET_APC_RESOURCES.add(SERVLET_NAME_APC_RESOURCES_LIST);
	}
	
	public static final String SERVLET_NAME_APC_RESOURCES_RISPOSTE_ADD = OBJECT_NAME_APC_RESOURCES_RISPOSTE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_RESOURCES_RISPOSTE_CHANGE = OBJECT_NAME_APC_RESOURCES_RISPOSTE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_RESOURCES_RISPOSTE_DELETE = OBJECT_NAME_APC_RESOURCES_RISPOSTE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST = OBJECT_NAME_APC_RESOURCES_RISPOSTE+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_RESOURCES_RISPOSTE = new ArrayList<>();
	static{
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_ADD);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_CHANGE);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_DELETE);
		SERVLET_APC_RESOURCES_RISPOSTE.add(SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST);
	}
	
	public static final String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_ADD = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_CHANGE = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_DELETE = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_LIST = OBJECT_NAME_APC_RESOURCES_REPRESENTATIONS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_RESOURCES_REPRESENTATIONS = new ArrayList<>();
	static{
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_ADD);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_CHANGE);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_DELETE);
		SERVLET_APC_RESOURCES_REPRESENTATIONS.add(SERVLET_NAME_APC_RESOURCES_REPRESENTATIONS_LIST);
	}
	
	public static final String SERVLET_NAME_APC_RESOURCES_PARAMETERS_ADD = OBJECT_NAME_APC_RESOURCES_PARAMETERS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_RESOURCES_PARAMETERS_CHANGE = OBJECT_NAME_APC_RESOURCES_PARAMETERS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_RESOURCES_PARAMETERS_DELETE = OBJECT_NAME_APC_RESOURCES_PARAMETERS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_APC_RESOURCES_PARAMETERS_LIST = OBJECT_NAME_APC_RESOURCES_PARAMETERS+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final List<String> SERVLET_APC_RESOURCES_PARAMETERS = new ArrayList<>();
	static{
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_ADD);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_CHANGE);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_DELETE);
		SERVLET_APC_RESOURCES_PARAMETERS.add(SERVLET_NAME_APC_RESOURCES_PARAMETERS_LIST);
	}
	

	/* LABEL GENERALI */

	public static final String LABEL_APC = "API";
	public static final String LABEL_ASC = "API (Servizio Composto)";
	public static final String LABEL_ACCORDI_EROGATORI_DI = "Erogazioni di ";
	public static final String LABEL_ACCORDI_EROGATORI = "Erogazioni";
	public static final String LABEL_ACCORDI_FRUITORI_DI = "Fruizioni di ";
	public static final String LABEL_ACCORDI_FRUITORI = "Fruizioni";
	public static final String LABEL_AZIONI = "Azioni";
	public static final String LABEL_AZIONE = "Azione";
	public static final String LABEL_PORT_TYPES = "Servizi";
	public static final String LABEL_PORT_TYPE = "Servizio";
	public static final String LABEL_ALLEGATI = "Allegati";
	public static final String LABEL_ALLEGATO = "Allegato";
	public static final String LABEL_SERVIZI_COMPONENTI = "Servizi Componenti";
	public static final String LABEL_COMPONENTE = "Componente";
	public static final String LABEL_SPECIFICA_INTERFACCE = "Specifica delle interfacce";
	public static final String LABEL_SPECIFICA_CONVERSAZIONI = "Specifica delle conversazioni";
	public static final String LABEL_INFORMAZIONI = "Informazioni Protocollo";
	public static final String LABEL_INFORMAZIONI_WSDL = "Informazioni WSDL";
	public static final String LABEL_INFORMAZIONI_WSDL_PART = "Informazioni WSDL Part";
	public static final String LABEL_STYLE = "Style";
	public static final String LABEL_APC_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_APC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE.name()+"')";
	public static final String LABEL_ASC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_COMPOSTO.name()+"')";
	public static final String LABEL_AZIONE_CORRELATA = "Azione correlata";
	public static final String LABEL_PROFILO_PROTOCOLLO = "Profilo";
	public static final String LABEL_CORRELATA_AL_SERVIZIO = "Correlata al servizio";
	public static final String LABEL_CORRELATA_A_AZIONE = "Correlata all'azione";
	public static final String LABEL_CORRELAZIONE_ASINCRONA = "Correlazione asincrona";
	public static final String LABEL_DOWNLOAD = "Download";
	public static final String LABEL_XSD_SCHEMA_COLLECTION = "XSD Schema Collection";
	public static final String LABEL_SOGGETTO = "Soggetto Erogatore";
	public static final String LABEL_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "Servizio";
	public static final String LABEL_FRUITORI = "Fruitori";
	public static final String LABEL_FRUITORE = "Fruitore";
	public static final String LABEL_POLITICHE_SLA = "Politiche SLA";
	public static final String LABEL_NON_DISPONIBILE = "non disp.";
	public static final String LABEL_WSDL_CHANGE_CLEAR_WARNING = "Attenzione";
	public static final String LABEL_WSDL_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Salva' senza selezionare alcun file"; //fornirne un'altra versione";
	public static final String LABEL_COMPONENTI = "Componenti";
	public static final String LABEL_WSDL_AGGIORNAMENTO = "Aggiornamento";
	public static final String LABEL_WSDL_NOT_FOUND = "non fornito";
	public static final String LABEL_WSDL_ATTUALE = "Attuale";
	public static final String LABEL_WSDL_NUOVO = "Nuovo";
	public static final String LABEL_INTERFACCIA = "Interfaccia";

	public static final String LABEL_APC_MENU_VISUALE_AGGREGATA = "API";
	public static final String LABEL_ASC_MENU_VISUALE_AGGREGATA = "API (Servizio Composto)";
	public static final String LABEL_OPERATION_MESSAGE = "Message";
	public static final String LABEL_OPERATION_MESSAGE_INPUT = "Message Input";
	public static final String LABEL_OPERATION_MESSAGE_OUTPUT = "Message Output";
	
	public static final String LABEL_RISORSE = "Risorse";
	public static final String LABEL_RISORSA = "Risorsa";
	public static final String LABEL_APC_RESOURCES_RICHIESTA = "Richiesta";
	public static final String LABEL_APC_RESOURCES_RISPOSTA = "Risposta";
	public static final String LABEL_RISPOSTE = "Risposte";
	public static final String LABEL_RISPOSTA = "Risposta";
	public static final String LABEL_REPRESENTATION = "Rappresentazione";
	public static final String LABEL_REPRESENTATION_DEFINIZIONE = "Definizione";
	public static final String LABEL_PARAMETERS = "Parametri";
	public static final String LABEL_PARAMETER = "Parametro";
	
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE = "Completare la configurazione dell'API appena creata ";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE = "Completare la configurazione dell'API appena modificata ";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_REST_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno una risorsa";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_REST_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno una risorsa";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un servizio";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un servizio";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZIO_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un'azione per il servizio";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZIO_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un'azione per il servizio";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZI_CREATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_CREATE+"definendo almeno un'azione nei servizi che non la possiedono";
	public static final String LABEL_CONFIGURAZIONE_INCOMPLETA_SOAP_AZIONE_SERVIZI_UPDATE = LABEL_CONFIGURAZIONE_INCOMPLETA_PREFIX_UPDATE+"definendo almeno un'azione nei servizi che non la possiedono";
	

	/* PARAMETRI */

	public static final String PARAMETRO_APC_ID = "id";
	public static final String PARAMETRO_APC_NOME = Costanti.CONSOLE_PARAMETRO_APC_NOME;
	public static final String PARAMETRO_APC_TIPO_ACCORDO = "tipoAccordo";
	public static final String PARAMETRO_APC_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public static final String PARAMETRO_APC_DESCRIZIONE = "descr";
	public static final String PARAMETRO_APC_TIPO_WSDL = "tipo";
	public static final String PARAMETRO_APC_UPDATE_WSDL_AGGIORNA = "wsdlAggiorna";
	public static final String PARAMETRO_APC_UPDATE_WSDL_ELIMINA = "wsdlElimina";
	public static final String PARAMETRO_APC_WSDL = "wsdl";
	public static final String PARAMETRO_APC_WSDL_WARN = "wsdlWarn";
	public static final String PARAMETRO_APC_WSDL_DEFINITORIO = "wsdldef";
	public static final String PARAMETRO_APC_WSDL_CONCETTUALE = "wsdlconc";
	public static final String PARAMETRO_APC_WSDL_EROGATORE = "wsdlserv";
	public static final String PARAMETRO_APC_WSDL_FRUITORE = "wsdlservcorr";
	public static final String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "wsblconc";
	public static final String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE = "wsblserv";
	public static final String PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE = "wsblservcorr";
	public static final String PARAMETRO_APC_PROFILO_COLLABORAZIONE = "profcoll";
	public static final String PARAMETRO_APC_FILTRO_DUPLICATI = "filtrodup";
	public static final String PARAMETRO_APC_CONFERMA_RICEZIONE = "confric";
	public static final String PARAMETRO_APC_COLLABORAZIONE = "idcoll";
	public static final String PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA = "idRifReq";
	public static final String PARAMETRO_APC_CONSEGNA_ORDINE = "consord";
	public static final String PARAMETRO_APC_SCADENZA = "scadenza";
	public static final String PARAMETRO_APC_REFERENTE = "referente";
	public static final String PARAMETRO_APC_REFERENTE_1_2 = "referenteLabelAccordo1.2";
	public static final String PARAMETRO_APC_VERSIONE = Costanti.CONSOLE_PARAMETRO_APC_VERSIONE;
	public static final String PARAMETRO_APC_PRIVATO = "privato";
	public static final String PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA = "privatoLabel";
	public static final String PARAMETRO_APC_IS_SERVIZIO_COMPOSTO = "isServizioComposto";
	public static final String PARAMETRO_APC_ACCORDO_COOPERAZIONE = "accordoCooperazione";
	public static final String PARAMETRO_APC_STATO_PACKAGE = "stato";
	public static final String PARAMETRO_APC_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public static final String PARAMETRO_APC_UTILIZZO_SENZA_AZIONE = "utilizzoSenzaAzione";
	public static final String PARAMETRO_VERSIONE_PROTOCOLLO = "profilo";
	public static final String PARAMETRO_CLIENT_AUTH = "clientAuth";
	public static final String PARAMETRO_APC_RIPRISTINA_STATO = "backToStato";
	public static final String PARAMETRO_APC_SERVICE_BINDING = CostantiControlStation.PARAMETRO_SERVICE_BINDING;
	public static final String PARAMETRO_APC_MESSAGE_TYPE = CostantiControlStation.PARAMETRO_MESSAGE_TYPE;
	public static final String PARAMETRO_APC_INTERFACE_TYPE = CostantiControlStation.PARAMETRO_INTERFACE_TYPE;
	public static final String PARAMETRO_APC_SERVICE_BINDING_SEARCH = CostantiControlStation.PARAMETRO_SERVICE_BINDING_SEARCH;
	public static final String PARAMETRO_APC_GRUPPI = "gruppi";
	public static final String PARAMETRO_APC_GRUPPO = "gruppo";
	public static final String PARAMETRO_APC_GRUPPI_SUPPORTO_COLORI = "__gruppi_colors";
	public static final String PARAMETRO_APC_API_NUOVA_VERSIONE = "apiNewVersion";
	public static final String PARAMETRO_APC_API_NUOVA_VERSIONE_MIN = "apiNewVersionMin";
	public static final String PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA = "apiNewVersionRedefine";
	public static final String PARAMETRO_APC_API_NUOVA_VERSIONE_OLD_ID_APC = "apiNewVersionOldIdApc";
	public static final String PARAMETRO_APC_CANALE = CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE;
	public static final String PARAMETRO_APC_CANALE_STATO = CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO;

	public static final String PARAMETRO_APC_AZIONI_NOME = "nomeaz";
	public static final String PARAMETRO_APC_AZIONI_CORRELATA = "azicorr";
	public static final String PARAMETRO_APC_AZIONI_PROFILO_BUSTA = "profBusta";
	public static final String PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE = "profcollaz";
	public static final String PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI = "filtrodupaz";
	public static final String PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE = "confricaz";
	public static final String PARAMETRO_APC_AZIONI_COLLABORAZIONE = "idcollaz";
	public static final String PARAMETRO_APC_AZIONI_ID_RIFERIMENTO_RICHIESTA = "idRifReqAz";
	public static final String PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE = "consordaz";
	public static final String PARAMETRO_APC_AZIONI_SCADENZA = "scadenzaaz";

	public static final String PARAMETRO_APC_PORT_TYPES_NOME = "nomept";
	public static final String PARAMETRO_APC_PORT_TYPES_MESSAGE_TYPE = "messageTypePT";
	public static final String PARAMETRO_APC_PORT_TYPES_PROFILO_BUSTA = "profBusta";
	public static final String PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE = "profcollpt";
	public static final String PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI = "filtroduppt";
	public static final String PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE = "confricpt";
	public static final String PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE = "idcollpt";
	public static final String PARAMETRO_APC_PORT_TYPES_ID_RIFERIMENTO_RICHIESTA = "idRifReqPt";
	public static final String PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE = "consordpt";
	public static final String PARAMETRO_APC_PORT_TYPES_SCADENZA = "scadenzapt";
	public static final String PARAMETRO_APC_PORT_TYPES_STYLE = "stylept";

	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_NOME = "nomeop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA = "profBusta";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_CORRELATA = "opcorrelata";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = "servcorr";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA = "azicorr";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE = "profcollop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_FILTRO_DUPLICATI = "filtrodupop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_CONFERMA_RICEZIONE = "confricop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_COLLABORAZIONE = "idcollop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_ID_RIFERIMENTO_RICHIESTA = "idRifReqOp";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_CONSEGNA_ORDINE = "consordop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_SCADENZA = "scadenzaop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_USE = "useop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE = "typeop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL = "nswsdlop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE = "styleop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION = "soapactionop";
	
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE = "msgop";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = "msgparttype";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME = "msgpartname";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME = "msgpartlocalname";
	public static final String PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS = "msgpartns";

	public static final String PARAMETRO_APC_ALLEGATI_RUOLO = "ruolo";
	public static final String PARAMETRO_APC_ALLEGATI_TIPO_FILE = "tipoFile";
	public static final String PARAMETRO_APC_ALLEGATI_DOCUMENTO = "theFile";
	public static final String PARAMETRO_APC_ALLEGATI_DOCUMENTO_VIEW = "documento";
	public static final String PARAMETRO_APC_ALLEGATI_ID_ALLEGATO = ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO;
	public static final String PARAMETRO_APC_ALLEGATI_ID_ACCORDO = ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO;
	public static final String PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO = "nomeDoc";

	public static final String PARAMETRO_APC_COMPONENTI_TIPO_SICA = "tipoSICA" ;
	public static final String PARAMETRO_APC_COMPONENTI_ELEMENTO = "elemento" ;
	public static final String PARAMETRO_APC_COMPONENTI_COMPONENTE = "componente";
	public static final String PARAMETRO_APC_ACCETTA_MODIFICHE = "accMod";

	public static final String PARAMETRO_APC_WSDL_CHANGE_TMP = "apcWsdlTMP";
	
	public static final String PARAMETRO_APC_RESOURCES_ID = "idRs";
	public static final String PARAMETRO_APC_RESOURCES_NOME = "nomeRs";
	public static final String PARAMETRO_APC_RESOURCES_DESCRIZIONE = "descrizioneRs";
	public static final String PARAMETRO_APC_RESOURCES_PATH = "pathRs";
	public static final String PARAMETRO_APC_RESOURCES_HTTP_METHOD = "httpMethodRs";
	public static final String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE = PARAMETRO_APC_MESSAGE_TYPE;
	public static final String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_REQUEST = "messageTypeReq";
	public static final String PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_RESPONSE = "messageTypeRes";
	
	public static final String PARAMETRO_APC_RESOURCE_REQUEST = "req";
	
	public static final String PARAMETRO_APC_RESOURCES_RESPONSE_ID = "idResRisposta";
	public static final String PARAMETRO_APC_RESOURCES_RESPONSE_STATUS = "statusResRisposta";
	public static final String PARAMETRO_APC_RESOURCES_RESPONSE_DESCRIZIONE = "descrizioneResRisposta";
	
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_ID = "idResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_MEDIA_TYPE = "mediaTypeResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_MESSAGE_TYPE = "messageTypeResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_NOME = "nomeResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_DESCRIZIONE = "descrResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO = "tipoResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO = "xmlTipoResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAME = "xmlNameResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAMESPACE = "xmlNamespaceResRepres";
	public static final String PARAMETRO_APC_RESOURCES_REPRESENTATION_JSON_TYPE = "jsonTypeResRepres";
	
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_ID = "idResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_NOME = "nomeResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_DESCRIZIONE = "descrResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO = "tipoParametroResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_REQUIRED = "requiredResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_TIPO = "tipoResParam";
	public static final String PARAMETRO_APC_RESOURCES_PARAMETER_RESTRIZIONI = "restrResParam";
	
	/* LABEL PARAMETRI */
	public static final String LABEL_APC_STATO = "Stato";
	public static final String LABEL_PARAMETRO_APC_NOME = "Nome";
	public static final String LABEL_PARAMETRO_APC_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_APC_REFERENTE = "Soggetto Referente";
	public static final String LABEL_PARAMETRO_APC_VERSIONE = "Versione";
	public static final String LABEL_PARAMETRO_APC_PRIVATO = "Privato";
	public static final String LABEL_PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA = "Visibilità API";
	public static final String LABEL_PARAMETRO_APC_IS_SERVIZIO_COMPOSTO = "Servizio Composto";
	public static final String LABEL_PARAMETRO_APC_STATO_PACKAGE = CostantiControlStation.LABEL_PARAMETRO_STATO_PACKAGE;
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_AGGIORNA_LEFT = ""; // "Aggiornamento";
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_ELIMINA_LEFT = ""; //"Cancellazione";
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_AGGIORNA_REST = "Le risorse esistenti vengono aggiornate";
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_ELIMINA_REST = "Elimina le risorse non presenti nella nuova interfaccia";
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_AGGIORNA_SOAP = "Le azioni esistenti vengono aggiornate";
	public static final String LABEL_PARAMETRO_APC_UPDATE_WSDL_ELIMINA_SOAP = "Elimina i servizi e le azioni non presenti nella nuova interfaccia";
	public static final String LABEL_PARAMETRO_APC_VALIDAZIONE_SPECIFICA = "Validazione Specifica";
	public static final String LABEL_PARAMETRO_APC_VALIDAZIONE_SPECIFICHE = "Validazione Specifiche";
	public static final String LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE = "Accordo Cooperazione";
	public static final String LABEL_PARAMETRO_APC_WSDL_DEFINITORIO = "WSDL Definitorio";
	public static final String LABEL_PARAMETRO_APC_WSDL_CONCETTUALE = "WSDL Concettuale";
	public static final String LABEL_PARAMETRO_APC_WSDL_EROGATORE = "WSDL Logico Erogatore";
	public static final String LABEL_PARAMETRO_APC_WSDL_FRUITORE = "WSDL Logico Fruitore";
	public static final String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "Specifica Concettuale";
	public static final String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE = "Specifica Erogatore";
	public static final String LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE = "Specifica Fruitore";
	public static final String LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE = "Profilo di collaborazione";
	public static final String LABEL_PARAMETRO_APC_UTILIZZO_SENZA_AZIONE = "Utilizzo Senza Azione";
	public static final String LABEL_PARAMETRO_APC_FILTRO_DUPLICATI = "Filtro Duplicati";
	public static final String LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE = "Conferma Ricezione";
	public static final String LABEL_PARAMETRO_APC_COLLABORAZIONE = "ID Conversazione";
	public static final String LABEL_PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA = "Riferimento ID Richiesta";
	public static final String LABEL_PARAMETRO_APC_CONSEGNA_ORDINE = "Consegna in Ordine";
	public static final String LABEL_PARAMETRO_APC_SCADENZA = "Scadenza";
	public static final String LABEL_PARAMETRO_APC_RIPRISTINA_STATO_OPERATIVO = "Ripristina Stato Operativo";
	public static final String LABEL_PARAMETRO_APC_SERVICE_BINDING = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING;
	public static final String LABEL_PARAMETRO_APC_SERVICE_BINDING_SOAP = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
	public static final String LABEL_PARAMETRO_APC_SERVICE_BINDING_REST = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_DEFAULT;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_SOAP_11 = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_11;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_SOAP_12 = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_12;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_XML = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_XML;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_JSON = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_JSON;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_BINARY = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_BINARY;
	public static final String LABEL_PARAMETRO_APC_MESSAGE_TYPE_MIME_MULTIPART = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
	public static final String LABEL_PARAMETRO_APC_INTERFACE_TYPE = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE;
	public static final String LABEL_PARAMETRO_APC_INTERFACE_TYPE_WSDL_11 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11;
	public static final String LABEL_PARAMETRO_APC_INTERFACE_TYPE_SWAGGER_2 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
	public static final String LABEL_PARAMETRO_APC_INTERFACE_TYPE_OPEN_API_3 = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
	public static final String LABEL_PARAMETRO_APC_GRUPPI = GruppiCostanti.LABEL_GRUPPI;
	public static final String LABEL_PARAMETRO_APC_GRUPPO = GruppiCostanti.LABEL_GRUPPO;
	public static final String LABEL_PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA = "Ridefinisci Interfaccia";
	public static final String LABEL_PARAMETRO_APC_CANALE = "Canale";
	public static final String LABEL_PARAMETRO_APC_CANALE_STATO = "Canale";

	public static final String LABEL_PARAMETRO_APC_ALLEGATI_RUOLO = "Ruolo";
	public static final String LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE = "Tipo";
	public static final String LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO = "Documento";
	public static final String LABEL_PARAMETRO_APC_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public static final String LABEL_PARAMETRO_APC_PROTOCOLLO_COMPATC = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public static final String LABEL_PARAMETRO_APC_WSDL = "WSDL";
	public static final String LABEL_PARAMETRO_APC_WSDL_LOGICO = "WSDL Logico";
	
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT = "MessageInput";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT = "MessageOutput";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE = "OperationType";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL = "Namespace";
	public static final String LABEL_USE = "Use";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION = "SOAPAction";
	
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE = "Tipo Messaggio";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = "Type";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME = "Name";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME = "Local Name";
	public static final String LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS = "Namespace";
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_NOME = "Nome";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_NOME_NOTE = "Se non definito verr&agrave; automaticamente generato un identificativo univoco";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PATH = "Path";
	private static final String LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO = "Per associare al metodo HTTP selezionato un qualsiasi path è possibile lasciare il campo vuoto o utilizzare il carattere speciale 'CHAR'";
	private static final String LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO_NOME_OBBLIGATORIO = "; in questo caso è obbligatorio indicare il nome della Risorsa";
	public static String getLABEL_PARAMETRO_APC_RESOURCES_PATH_INFO(String charAll, boolean appendNomeObbligatorio) {
		return LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO.replace("CHAR", charAll) + (appendNomeObbligatorio ? LABEL_PARAMETRO_APC_RESOURCES_PATH_INFO_NOME_OBBLIGATORIO : "");
	}
	public static final String LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD = CostantiControlStation.LABEL_PARAMETRO_HTTP_METHOD;
	public static final String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE = "Tipo Messaggio";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_REQUEST = "Tipo Messaggio Richiesta";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_MESSAGE_TYPE_RESPONSE = "Tipo Messaggio Risposta";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_FORMATO_SPECIFICA = "Formato Specifica";
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PATH_QUALSIASI = "*";
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_ID = "ID";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS = "HTTP Status";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_NOTE = "Non fornire alcun valore per indicare uno stato di default";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_DEFAULT = "Default";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_DESCRIZIONE = "Descrizione";
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_ID = "ID";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_MEDIA_TYPE = "Media Type";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_MESSAGE_TYPE = "Message Type";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_NOME = "Nome";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO = "Schema";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_XML = "Xml";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_JSON = "Json";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_NON_DEFINITO = "Non Definito";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_ELEMENT = "Element";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_TYPE = "Type";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAME = "Nome";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_NAMESPACE = "Namespace";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_REPRESENTATION_JSON_TYPE = "Tipo";
	
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_ID = "ID";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_NOME = "Nome";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO = "Tipo Parametro";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_REQUIRED = "Obbligatorio";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_RESTRIZIONI = "Restrizioni";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_COOKIE = "Cookie";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_DYNAMIC_PATH = "Dynamic Path";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_FORM = "Form";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_HEADER = "Header";
	public static final String LABEL_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_QUERY = "Query";
	
	public static final String LABEL_DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO = CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_RIDEFINITO;
	public static final String LABEL_DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT = CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT;
	
	public static final String LABEL_SI = "Si";
	public static final String LABEL_NO = "No";
	
	/* DEFAULT VALUE PARAMETRI */

	public static final String TIPO_WSDL_CONCETTUALE = "Concettuale";
	public static final String TIPO_WSDL_EROGATORE = "LogicoErogatore";
	public static final String TIPO_WSDL_FRUITORE = "LogicoFruitore";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPES_STYLE = BindingStyle.DOCUMENT.getValue();
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE = "0";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE = BindingUse.LITERAL.getValue();
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN = "input";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT =  "inputOutput";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE = "type";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT = "element";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT = "i";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT = "o";
	
	public static final String PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE = "apc";
	public static final String PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "asc";

	public static final String INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO = CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO;
	public static final String INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT = CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT;
	public static final String[] INFORMAZIONI_PROTOCOLLO_MODALITA = { CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO };

	public static final String INFORMAZIONI_CLIENT_AUTH_DEFAULT = "default";

	public static final String LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO = "profilo API";
	public static final String[] LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO = { "usa profilo API", "ridefinisci" };

	public static final String LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_PORT_TYPE = "profilo servizio";
	public static final String[] LABEL_INFORMAZIONI_PROTOCOLLO_PORT_TYPE = { "usa profilo servizio", "ridefinisci" };

	public static final String INFORMATIVA_VISIBILITA_PRIVATA = "privata";
	public static final String INFORMATIVA_VISIBILITA_PUBBLICA = "pubblica";

	public static final String TIPO_PROFILO_COLLABORAZIONE_ONEWAY = CostantiRegistroServizi.ONEWAY.toString();
	public static final String TIPO_PROFILO_COLLABORAZIONE_SINCRONO = CostantiRegistroServizi.SINCRONO.toString();
	public static final String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = CostantiRegistroServizi.ASINCRONO_SIMMETRICO.toString();
	public static final String TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString();
	public static final String[] TIPI_PROFILI_COLLABORAZIONE = { TIPO_PROFILO_COLLABORAZIONE_ONEWAY, TIPO_PROFILO_COLLABORAZIONE_SINCRONO,
		TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO };

	public static final String[] PORT_TYPES_STYLE = { BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue()	 };
	public static final String[] LABEL_PORT_TYPES_STYLE = { BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue() };
	
	public static final String[] PORT_TYPES_OPERATION_STYLE = { "0", BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue()	 };
	public static final String[] LABEL_PORT_TYPES_OPERATION_STYLE = { "Usa style del servizio",BindingStyle.DOCUMENT.getValue(), BindingStyle.RPC.getValue() };
	
	public static final String[] PORT_TYPES_OPERATION_USE = {   BindingUse.LITERAL.getValue(), BindingUse.ENCODED.getValue()	 };
	public static final String[] LABEL_PORT_TYPES_OPERATION_USE = {  BindingUse.LITERAL.getValue(), BindingUse.ENCODED.getValue()};
	
	public static final String[] PORT_TYPE_OPERATION_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT };
	public static final String[] LABEL_PORT_TYPE_OPERATION_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT  };
	
	public static final String[] PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE };
	public static final String[] LABEL_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE = {  DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT, DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE  };
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_SERVICE_BINDING_QUALSIASI = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
		
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_DEFAULT;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_SOAP_11 = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_11;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_SOAP_12 = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_12;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_XML = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_XML;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_JSON = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_JSON;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_BINARY = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_BINARY;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_MIME_MULTIPART = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
	
	public static final String VALUE_PARAMETRO_APC_INTERFACE_TYPE_WSDL_11 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_WSDL_11;
	public static final String VALUE_PARAMETRO_APC_INTERFACE_TYPE_SWAGGER_2 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
	public static final String VALUE_PARAMETRO_APC_INTERFACE_TYPE_OPEN_API_3 = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
	public static final String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST;
	public static final String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP;
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI = CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE;
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_XML = "xml";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_JSON = "json";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO_NON_DEFINITO = "nd";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_COOKIE = "cookie";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_DYNAMIC_PATH = "dynamicPath";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_FORM = "form";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_HEADER = "header";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO_QUERY = "query";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_ELEMENT = "element";
	public static final String DEFAULT_VALUE_PARAMETRO_APC_RESOURCES_REPRESENTATION_XML_TIPO_TYPE = "type";
	
	public static final String DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO;
	public static final String DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT;
	
	public static final String[] VALUES_PARAMETRO_APC_CANALE_STATO = CostantiControlStation.VALUES_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO;
	
	public static final String LABEL_IN_USO_ACCORDO_RISORSA_BODY_HEADER_NESSUN_RISULTATO = "La risorsa non risulta utilizzata in alcuna configurazione";
	public static final String LABEL_IN_USO_ACCORDO_PORT_TYPE_BODY_HEADER_NESSUN_RISULTATO = "Il servizio non risulta utilizzato in alcuna configurazione";
	public static final String LABEL_IN_USO_ACCORDO_OPERAZIONE_BODY_HEADER_NESSUN_RISULTATO = "L'azione non risulta utilizzata in alcuna configurazione";
}

