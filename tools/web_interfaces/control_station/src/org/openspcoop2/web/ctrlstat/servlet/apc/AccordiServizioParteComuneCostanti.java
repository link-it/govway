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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.Vector;

import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
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

	public final static String OBJECT_NAME_APC_SERVIZI_APPLICATIVI = "accordiServizioParteComuneServiziApplicativi";

	public final static String OBJECT_NAME_APC_EROGATORI = "accordiServizioParteComuneErogatori";

	public final static String OBJECT_NAME_APC_EROGATORI_FRUITORI = "accordiServizioParteComuneErogatoriFruitori";

	public final static String OBJECT_NAME_APC_COMPONENTI = "accordiServizioParteComuneComponenti";

	public final static String OBJECT_NAME_ACCORDI_COOPERAZIONE = "accordiCooperazione";
	
	public final static String OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE = "accordiServizioParteComunePortTypeOperationsMessage";

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

	public final static String SERVLET_NAME_APC_SERVIZI_APPLICATIVI_ADD = OBJECT_NAME_APC_SERVIZI_APPLICATIVI+"Add.do";
	public final static String SERVLET_NAME_APC_SERVIZI_APPLICATIVI_LIST = OBJECT_NAME_APC_SERVIZI_APPLICATIVI+"List.do";
	public final static String SERVLET_NAME_APC_SERVIZI_APPLICATIVI_DEL = OBJECT_NAME_APC_SERVIZI_APPLICATIVI+"Del.do";
	public final static Vector<String> SERVLET_APC_SERVIZI_APPLICATIVI = new Vector<String>();
	static{
		SERVLET_APC_EROGATORI.add(SERVLET_NAME_APC_SERVIZI_APPLICATIVI_ADD);
		SERVLET_APC_EROGATORI.add(SERVLET_NAME_APC_SERVIZI_APPLICATIVI_LIST);
		SERVLET_APC_EROGATORI.add(SERVLET_NAME_APC_SERVIZI_APPLICATIVI_DEL);
	}

	public final static String SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE = OBJECT_NAME_APC_EROGATORI_FRUITORI+"Change.do";
	public final static String SERVLET_NAME_APC_EROGATORI_FRUITORI_LIST = OBJECT_NAME_APC_EROGATORI_FRUITORI+"List.do";
	public final static Vector<String> SERVLET_APC_EROGATORI_FRUITORI = new Vector<String>();
	static{
		SERVLET_APC_EROGATORI_FRUITORI.add(SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE);
		SERVLET_APC_EROGATORI_FRUITORI.add(SERVLET_NAME_APC_EROGATORI_FRUITORI_LIST);
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


	/* LABEL GENERALI */

	public final static String LABEL_APC = "Accordi Servizio Parte Comune";
	public final static String LABEL_ASC = "Accordi Servizio Composto";
	public final static String LABEL_ACCORDI_EROGATORI_DI = "Erogatori di ";
	public final static String LABEL_ACCORDI_EROGATORI = "Erogatori";
	public final static String LABEL_ACCORDI_FRUITORI_DI = "Fruitori di ";
	public final static String LABEL_ACCORDI_FRUITORI = "Fruitori";
	public final static String LABEL_AZIONI = "Azioni";
	public final static String LABEL_PORT_TYPES = "Servizi";
	public final static String LABEL_ALLEGATI = "Allegati";
	public final static String LABEL_SERVIZI_COMPONENTI = "Servizi Componenti";
	public final static String LABEL_SPECIFICA_INTERFACCE = "Specifica delle interfacce";
	public final static String LABEL_SPECIFICA_CONVERSAZIONI = "Specifica delle conversazioni";
	public final static String LABEL_INFORMAZIONI = "Informazioni";
	public final static String LABEL_INFORMAZIONI_WSDL = "Informazioni WSDL";
	public final static String LABEL_INFORMAZIONI_WSDL_PART = "Informazioni WSDL Part";
	public final static String LABEL_STYLE = "Style";
	public final static String LABEL_APC_ESPORTA_SELEZIONATI = "Esporta Selezionati";
	public final static String LABEL_APC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE.name()+"')";
	public final static String LABEL_ASC_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_COMPOSTO.name()+"')";
	public final static String LABEL_AZIONE_CORRELATA = "Azione correlata";
	public final static String LABEL_PROFILO_PROTOCOLLO = "Profilo";
	public final static String LABEL_CORRELATA_AL_SERVIZIO = "Correlata al servizio";
	public final static String LABEL_CORRELATA_A_AZIONE = "Correlata all'azione";
	public final static String LABEL_CORRELAZIONE_ASINCRONA = "Correlazione asincrona";
	public final static String LABEL_DOWNLOAD = "Download";
	public final static String LABEL_XSD_SCHEMA_COLLECTION = "XSD Schema Collection";
	public final static String LABEL_SOGGETTO = "Soggetto";
	public final static String LABEL_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "Accordo Servizio Parte Specifica";
	public final static String LABEL_FRUITORI = "Fruitori";
	public final static String LABEL_FRUITORE = "Fruitore";
	public final static String LABEL_POLITICHE_SICUREZZA = "Politiche sicurezza";
	public final static String LABEL_POLITICHE_SLA = "Politiche SLA";
	public final static String LABEL_NON_DISPONIBILE = "non disp.";
	public final static String LABEL_WSDL_CHANGE_CLEAR_WARNING = "Warning: ";
	public final static String LABEL_WSDL_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Invia' senza selezionare alcun file"; //fornirne un'altra versione";
	public final static String LABEL_COMPONENTI = "Componenti";
	public final static String LABEL_WSDL_AGGIORNAMENTO = "Aggiornamento WSDL";
	public final static String LABEL_WSDL_NOT_FOUND = "non fornito";
	public final static String LABEL_WSDL_ATTUALE = "Attuale";
	public final static String LABEL_WSDL_NUOVO = "Nuovo WSDL";

	public final static String LABEL_APC_MENU_VISUALE_AGGREGATA = "Accordi Parte Comune";
	public final static String LABEL_ASC_MENU_VISUALE_AGGREGATA = "Servizi Composti";
	public final static String LABEL_OPERATION_MESSAGE = "Message";
	public final static String LABEL_OPERATION_MESSAGE_INPUT = "Message Input";
	public final static String LABEL_OPERATION_MESSAGE_OUTPUT = "Message Output";

	/* PARAMETRI */

	public final static String PARAMETRO_APC_ID = "id";
	public final static String PARAMETRO_APC_NOME = "nome";
	public final static String PARAMETRO_APC_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_APC_PROTOCOLLO = "tipoProt";
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


	public final static String PARAMETRO_APC_AZIONI_NOME = "nomeaz";
	public final static String PARAMETRO_APC_AZIONI_CORRELATA = "azicorr";
	public final static String PARAMETRO_APC_AZIONI_PROFILO_BUSTA = "profBusta";
	public final static String PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE = "profcollaz";
	public final static String PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI = "filtrodupaz";
	public final static String PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE = "confricaz";
	public final static String PARAMETRO_APC_AZIONI_COLLABORAZIONE = "idcollaz";
	public final static String PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE = "consordaz";
	public final static String PARAMETRO_APC_AZIONI_SCADENZA = "scadenzaaz";

	public final static String PARAMETRO_APC_PORT_TYPES_NOME = "nomept";
	public final static String PARAMETRO_APC_PORT_TYPES_PROFILO_BUSTA = "profBusta";
	public final static String PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE = "profcollpt";
	public final static String PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI = "filtroduppt";
	public final static String PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE = "confricpt";
	public final static String PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE = "idcollpt";
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
	public final static String PARAMETRO_APC_EROGATORI_ENDPOINT_TYPE = "endpointtype";
	public final static String PARAMETRO_APC_EROGATORI_NOME_ACCORDO = "nomeAccordo";
	public final static String PARAMETRO_APC_EROGATORI_CORRELATO = "correlato";
	public final static String PARAMETRO_APC_EROGATORI_ID_FRUITORE = "myId";
	public final static String PARAMETRO_APC_EROGATORI_EROGATORE = "provider";

	public final static String PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SOGGETTO = "idsogg";
	public final static String PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SERVIZIO = "idDelServizio";
	public final static String PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SOGGETTO_FRUITORE_SERVIZIO= "idSoggettoFruitoreDelServizio";

	public final static String PARAMETRO_APC_COMPONENTI_TIPO_SICA = "tipoSICA" ;
	public final static String PARAMETRO_APC_COMPONENTI_ELEMENTO = "elemento" ;
	public final static String PARAMETRO_APC_COMPONENTI_COMPONENTE = "componente";
	public final static String PARAMETRO_APC_ACCETTA_MODIFICHE = "accMod";

	public final static String PARAMETRO_APC_WSDL_CHANGE_TMP = "apcWsdlTMP";
	
	/* LABEL PARAMETRI */

	public final static String LABEL_PARAMETRO_APC_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APC_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APC_REFERENTE = "Soggetto referente";
	public final static String LABEL_PARAMETRO_APC_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APC_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA = "Visibilità accordo";
	public final static String LABEL_PARAMETRO_APC_IS_SERVIZIO_COMPOSTO = "Servizio Composto";
	public final static String LABEL_PARAMETRO_APC_STATO_PACKAGE = "Stato";
	public final static String LABEL_PARAMETRO_APC_VALIDAZIONE_DOCUMENTI = "Validazione Documenti";
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
	public final static String LABEL_PARAMETRO_APC_FILTRO_DUPLICATI = "Filtro duplicati";
	public final static String LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE = "Conferma ricezione";
	public final static String LABEL_PARAMETRO_APC_COLLABORAZIONE = "ID Collaborazione";
	public final static String LABEL_PARAMETRO_APC_CONSEGNA_ORDINE = "Consegna in ordine";
	public final static String LABEL_PARAMETRO_APC_SCADENZA = "Scadenza";
	public final static String LABEL_PARAMETRO_APC_RIPRISTINA_STATO_OPERATIVO = "Ripristina Stato Operativo";

	public final static String LABEL_PARAMETRO_APC_ALLEGATI_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE = "Tipo";
	public final static String LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO = "Documento";
	public final static String LABEL_PARAMETRO_APC_PROTOCOLLO = "Protocollo";
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

	public final static String LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO = "profilo accordo";
	public final static String[] LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO = { "usa profilo accordo", "ridefinisci" };

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
}
