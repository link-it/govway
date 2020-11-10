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
package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.Vector;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AccordiServizioParteSpecificaCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteSpecificaCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_APS = "accordiServizioParteSpecifica";
	
	public final static String OBJECT_NAME_APS_FRUITORI = "accordiServizioParteSpecificaFruitori";
	
	public final static ForwardParams TIPO_OPERAZIONE_WSDL_CHANGE = ForwardParams.OTHER("WSDLChange");
		
	public final static String OBJECT_NAME_APS_ALLEGATI = "accordiServizioParteSpecificaAllegati";
	public final static ForwardParams TIPO_OPERAZIONE_VIEW = ForwardParams.OTHER("View");
	
	public final static String OBJECT_NAME_APS_PORTE_APPLICATIVE = "accordiServizioParteSpecificaPorteApplicative";
	
	public final static ForwardParams TIPO_OPERAZIONE_CONFIGURAZIONE = ForwardParams.OTHER("Configurazione");
	
	
	public final static String OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE = "accordiServizioParteSpecificaFruitoriPorteDelegate";
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_APS_ADD = OBJECT_NAME_APS+"Add.do";
	public final static String SERVLET_NAME_APS_CHANGE = OBJECT_NAME_APS+"Change.do";
	public final static String SERVLET_NAME_APS_DELETE = OBJECT_NAME_APS+"Del.do";
	public final static String SERVLET_NAME_APS_LIST = OBJECT_NAME_APS+"List.do";
	public final static String SERVLET_NAME_APS_WSDL_CHANGE = OBJECT_NAME_APS+"WSDLChange.do";
	public final static Vector<String> SERVLET_APS = new Vector<String>();
	static{
		SERVLET_APS.add(SERVLET_NAME_APS_ADD);
		SERVLET_APS.add(SERVLET_NAME_APS_CHANGE);
		SERVLET_APS.add(SERVLET_NAME_APS_DELETE);
		SERVLET_APS.add(SERVLET_NAME_APS_LIST);
		SERVLET_APS.add(SERVLET_NAME_APS_WSDL_CHANGE);
	}
	
	public final static String SERVLET_NAME_APS_FRUITORI_ADD = OBJECT_NAME_APS_FRUITORI+"Add.do";
	public final static String SERVLET_NAME_APS_FRUITORI_CHANGE = OBJECT_NAME_APS_FRUITORI+"Change.do";
	public final static String SERVLET_NAME_APS_FRUITORI_DELETE = OBJECT_NAME_APS_FRUITORI+"Del.do";
	public final static String SERVLET_NAME_APS_FRUITORI_LIST = OBJECT_NAME_APS_FRUITORI+"List.do";
	public final static String SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE = OBJECT_NAME_APS_FRUITORI+"WSDLChange.do";
	public final static Vector<String> SERVLET_APS_FRUITORI = new Vector<String>();
	static{
		SERVLET_APS.add(SERVLET_NAME_APS_FRUITORI_ADD);
		SERVLET_APS.add(SERVLET_NAME_APS_FRUITORI_CHANGE);
		SERVLET_APS.add(SERVLET_NAME_APS_FRUITORI_DELETE);
		SERVLET_APS.add(SERVLET_NAME_APS_FRUITORI_LIST);
		SERVLET_APS.add(SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE);
	}
		
	public final static String SERVLET_NAME_APS_ALLEGATI_ADD = OBJECT_NAME_APS_ALLEGATI+"Add.do";
	public final static String SERVLET_NAME_APS_ALLEGATI_CHANGE = OBJECT_NAME_APS_ALLEGATI+"Change.do";
	public final static String SERVLET_NAME_APS_ALLEGATI_DELETE = OBJECT_NAME_APS_ALLEGATI+"Del.do";
	public final static String SERVLET_NAME_APS_ALLEGATI_LIST = OBJECT_NAME_APS_ALLEGATI+"List.do";
	public final static String SERVLET_NAME_APS_ALLEGATI_VIEW = OBJECT_NAME_APS_ALLEGATI+"View.do";
	public final static Vector<String> SERVLET_APS_ALLEGATI = new Vector<String>();
	static{
		SERVLET_APS_ALLEGATI.add(SERVLET_NAME_APS_ALLEGATI_ADD);
		SERVLET_APS_ALLEGATI.add(SERVLET_NAME_APS_ALLEGATI_CHANGE);
		SERVLET_APS_ALLEGATI.add(SERVLET_NAME_APS_ALLEGATI_DELETE);
		SERVLET_APS_ALLEGATI.add(SERVLET_NAME_APS_ALLEGATI_LIST);
		SERVLET_APS_ALLEGATI.add(SERVLET_NAME_APS_ALLEGATI_VIEW);
	}

	public final static String SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD = OBJECT_NAME_APS_PORTE_APPLICATIVE+"Add.do";
	public final static String SERVLET_NAME_APS_PORTE_APPLICATIVE_DELETE = OBJECT_NAME_APS_PORTE_APPLICATIVE+"Del.do";
	public final static String SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST = OBJECT_NAME_APS_PORTE_APPLICATIVE+"List.do";
	public final static Vector<String> SERVLET_APS_PORTE_APPLICATIVE = new Vector<String>();
	static{
		SERVLET_APS_PORTE_APPLICATIVE.add(SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD);
		SERVLET_APS_PORTE_APPLICATIVE.add(SERVLET_NAME_APS_PORTE_APPLICATIVE_DELETE);
		SERVLET_APS_PORTE_APPLICATIVE.add(SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST);
	}
	
	public final static String SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_ADD = OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE+"Add.do";
	public final static String SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_DELETE = OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE+"Del.do";
	public final static String SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST = OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE+"List.do";
	public final static Vector<String> SERVLET_APS_FRUITORI_PORTE_DELEGATE = new Vector<String>();
	static{
		SERVLET_APS_FRUITORI_PORTE_DELEGATE.add(SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_ADD);
		SERVLET_APS_FRUITORI_PORTE_DELEGATE.add(SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_DELETE);
		SERVLET_APS_FRUITORI_PORTE_DELEGATE.add(SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST);
	}
	
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_APS = "Erogazioni";
	public final static String LABEL_APS_SINGOLO = "Erogazione";
	public final static String LABEL_APC_COMPOSTO = "API (ParteComune/Composto)";
	public final static String LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE = "API";
	public final static String LABEL_APC_COMPOSTO_SOLO_COMPOSTO = "API (Servizio Composto)";
	public final static String LABEL_APS_SERVIZI = "Servizi";
	public final static String LABEL_APS_FRUITORI = "Fruizioni";
	public final static String LABEL_APS_FRUITORE = "Fruizione";
	public final static String LABEL_PARAMETRO_VISUALIZZA_DATI_FRUITORE = "Visualizza Dati Soggetto";
	public final static String LABEL_APS_CORRELATO = "Correlato";
	public final static String LABEL_APS_FUITORI_DI = "Fruizioni di ";
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI = "WSDL Implementativo Erogatore di "; 
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI = "WSDL Implementativo Fruitore di ";
	public final static String LABEL_APS_ALLEGATI_DI = "Allegati di ";
	public final static String LABEL_APS_DOWNLOAD = "Download";
	public final static String LABEL_APS_INFO_GENERALI = "Informazioni Generali";
	public final static String LABEL_APS_SERVIZIO = "Servizio";
	public final static String LABEL_APS_SERVIZIO_SOAP = "Servizio ("+CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP+")";
	public final static String LABEL_APS_RICERCA_SERVIZIO_SOGGETTO = "Servizio / Soggetto";
	public final static String LABEL_APS_RICERCA_API_SOGGETTO = "API / Soggetto Erogatore";
	public final static String LABEL_APS_ALLEGATI = "Allegati";
	public final static String LABEL_APS_ALLEGATO = "Allegato";
	public final static String LABEL_APS_CONFIGURAZIONI = "Configurazioni";
	//public final static String LABEL_APS_CONFIGURAZIONI_DI = "Configurazioni di ";
	public final static String LABEL_APS_CONFIGURAZIONI_DI = "";
	public final static String LABEL_APS_PORTE_APPLICATIVE = "Configurazione";
	public final static String LABEL_APS_PORTE_DELEGATE = "Configurazione";
	public final static String LABEL_APS_DATI_INVOCAZIONE = "URL Invocazione";
	//public final static String LABEL_APS_DATI_INVOCAZIONE_DI = "URL Invocazione di ";
	public final static String LABEL_APS_DATI_INVOCAZIONE_DI = "";
	public final static String LABEL_APS_STATO = "Stato";
	public final static String LABEL_APS_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_APS_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name()+"')";
	public final static String LABEL_EROGAZIONI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.EROGAZIONE.name()+"')";
	public final static String LABEL_FRUIZIONI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.FRUIZIONE.name()+"')";
	public final static String LABEL_APS_USA_VERSIONE_EROGATORE = "usa versione erogatore";
	public final static String LABEL_APS_SPECIFICA_PORTI_ACCESSO = "Specifica dei Porti di Accesso";
	public final static String LABEL_APS_SPECIFICA_PORTA_APPLICATIVA = "Configurazione";
	public final static String LABEL_APS_SPECIFICA_PORTA_DELEGATA = "Porta Delegata";
	public final static String LABEL_APS_ALTRE_INFORMAZIONI = "Altre informazioni";
	public final static String LABEL_APS_SERVIZIO_APPLICATIVO_EROGATORE = "Servizio Applicativo Erogatore";
	public final static String LABEL_APS_SERVIZIO_APPLICATIVO_FRUITORE = "Servizio Applicativo Fruitore";
	public final static String LABEL_APS_SOGGETTO_EROGATORE = "Soggetto Erogatore";
	public final static String LABEL_APS_SOGGETTO_FRUITORE = "Soggetto Fruitore";
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_DI = "WSDL Implementativo di ";
	public final static String LABEL_WSDL_CHANGE_CLEAR_WARNING = "Attenzione";
	public final static String LABEL_WSDL_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Invi&agrave; senza selezionare alcun file"; //fornirne un'altra versione";
	public final static String LABEL_WSDL_AGGIORNAMENTO = "Aggiornamento WSDL";
	public final static String LABEL_WSDL_NOT_FOUND = "non fornito";
	public final static String LABEL_WSDL_ATTUALE = "Attuale";
	public final static String LABEL_WSDL_NUOVO = "Nuovo WSDL";
	public final static String LABEL_AGGIUNTA_FRUITORI_COMPLETATA = "Tutti i soggetti sono già stati aggiunti come fruitori";
	public final static String LABEL_APS_VERSIONE_APS = "Versione Accordo Servizio Parte Specifica";
	public static final String LABEL_APS_SOGGETTO = "Soggetto";
	public static final String LABEL_APS_TIPO_SERVIZIO = "Tipo Servizio";
	public static final String LABEL_APS_NOME_SERVIZIO = "Nome Servizio";
	public final static String LABEL_PARAMETRO_APS_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public final static String LABEL_PARAMETRO_APS_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_APS_APPLICATIVO_INTERNO_PREFIX = "Dominio Interno - ";
	public final static String LABEL_APS_APPLICATIVO_ESTERNO_PREFIX = "Dominio Esterno - ";
	
	public final static String LABEL_APS_MENU_VISUALE_AGGREGATA = "Erogazioni";
	public final static String LABEL_APS_FRUIZIONI_MENU_VISUALE_AGGREGATA = "Fruizioni";
	public static final String LABEL_N_D = "N.D.";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_APS_TIPO_EROGAZIONE = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE;
	public final static String PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE;
	public final static String PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE;
	public final static String PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_COMPLETA = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_COMPLETA;
	
	public final static String PARAMETRO_APS_ID = CostantiControlStation.PARAMETRO_ID;
	public final static String PARAMETRO_APS_MY_ID = CostantiControlStation.PARAMETRO_ID_FRUIZIONE;
	public final static String PARAMETRO_APS_MY_TIPO = "myTipo";
	public final static String PARAMETRO_APS_MY_NOME = "myNome";
	public final static String PARAMETRO_APS_ID_SOGGETTO = CostantiControlStation.PARAMETRO_ID_SOGGETTO;
	public final static String PARAMETRO_APS_ID_SOGGETTO_EROGATORE = "idSoggErogatore";
	public final static String PARAMETRO_APS_SERVIZIO_APPLICATIVO = "servizioApplicativo";
	public final static String PARAMETRO_APS_WSDL_EROGATORE = "wsdlimpler";
	public final static String PARAMETRO_APS_WSDL_FRUITORE = "wsdlimplfru";
	public final static String PARAMETRO_APS_NOME_SERVIZIO = "nomeservizio";
	public final static String PARAMETRO_APS_TIPO_SERVIZIO = "tiposervizio";	
	public final static String PARAMETRO_APS_TIPO = "tipo";
	public final static String PARAMETRO_APS_NOME = "nome";
	public final static String PARAMETRO_APS_WSDL = "wsdl";
	public final static String PARAMETRO_APS_WSDL_WARN = "wsdlWarn";
	public final static String PARAMETRO_APS_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public final static String PARAMETRO_APS_ACCORDO = "accordo";
	public final static String PARAMETRO_APS_SERVIZIO_CORRELATO = "servcorr";
	public final static String PARAMETRO_APS_PORT_TYPE = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_APS_PORT_TYPE;
	public final static String PARAMETRO_APS_PORT_TYPE_OLD = "port_type_old";
	public final static String PARAMETRO_APS_DESCRIZIONE = "descrizione";
	public final static String PARAMETRO_APS_PRIVATO = "privato";
	public final static String PARAMETRO_APS_VERSIONE = "versione";
	public final static String PARAMETRO_APS_RUOLO = "ruolo";
	public final static String PARAMETRO_APS_TIPO_FILE = "tipoFile";
	public final static String PARAMETRO_APS_THE_FILE = "theFile";
	public final static String PARAMETRO_APS_NOME_DOCUMENTO = "nomeDoc";
	public final static String PARAMETRO_APS_TIPO_DOCUMENTO = "tipoDocumento";
	public final static String PARAMETRO_APS_ID_ALLEGATO = "idAllegato";
	public final static String PARAMETRO_APS_ID_ACCORDO= "idAccordo";
	public final static String PARAMETRO_APS_DOCUMENTO= "documento";
	public final static String PARAMETRO_APS_ID_FRUITORE = "idFruitore";
	public final static String PARAMETRO_APS_ID_PORTA = "idPorta";
	public final static String PARAMETRO_APS_NOME_PORTA = "nomePorta";
	public final static String PARAMETRO_APS_ACCORDO_LABEL = "accordoLabel";
	public final static String PARAMETRO_APS_PORT_TYPE_LABEL =  "port_type_label";
	public final static String PARAMETRO_APS_PROVIDER_EROGATORE =  "providerErogatore";
	public final static String PARAMETRO_APS_PROVIDER_FRUITORE =  "providerFruitore";
	public final static String PARAMETRO_APS_PROVIDER_FRUITORE_AS_TEXT =  "providerFruitoreAsText";
	public final static String PARAMETRO_APS_PROVIDER_TEXT =  "providerText";
	public final static String PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE =  "fruitoreViewConnettoreAzione";
	public final static String PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA =  "fruitoreViewConnettoreAzioneIdPorta";
	public final static String PARAMETRO_APS_SERVIZIO_CORRELATO_LABEL = "servcorrLabel";
	public final static String PARAMETRO_APS_PRIVATO_LABEL = "privatoLabel";
	public final static String PARAMETRO_APS_PROFILO = "profilo";
	public final static String PARAMETRO_APS_NOME_SOGGETTO = "nomeSogg";
	public final static String PARAMETRO_APS_TIPO_SOGGETTO = "tipoSogg";
	public final static String PARAMETRO_APS_NOME_SOGGETTO_FRUITORE = "nomeSoggFru";
	public final static String PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE = "tipoSoggFru";
	public final static String PARAMETRO_APS_CORRELATO = "correlato";	
	public final static String PARAMETRO_APS_ID_SERVIZIO = "idServ";
	public final static String PARAMETRO_APS_STATO = "stato";
	public final static String PARAMETRO_APS_NOME_PA = "nomePA";
	public final static String PARAMETRO_APS_NOME_SA = "nomeServizioApplicativo";
	public final static String PARAMETRO_APS_FRUIZIONE_NOME_SA = CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO;
	public final static String PARAMETRO_APS_NOME_RUOLO = CostantiControlStation.PARAMETRO_RUOLO;
	public final static String PARAMETRO_APS_AUTENTICAZIONE = CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE;
	public final static String PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE = CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE = CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE = CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE_RUOLI = CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA = CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH = CostantiControlStation.PARAMETRO_RUOLO_MATCH;
	public final static String PARAMETRO_APS_AUTORIZZAZIONE_SOGGETTO_AUTENTICATO = CostantiControlStation.PARAMETRO_SOGGETTO;
	public final static String PARAMETRO_APS_RIPRISTINA_STATO = "backToStato";
	public final static String PARAMETRO_APS_SERVICE_BINDING = CostantiControlStation.PARAMETRO_SERVICE_BINDING;
	public final static String PARAMETRO_APS_PROTOCOLLO = CostantiControlStation.PARAMETRO_PROTOCOLLO;
	public final static String PARAMETRO_APS_GESTIONE_GRUPPI = "gestioneGruppi";
	public final static String PARAMETRO_APS_GESTIONE_CONFIGURAZIONI = "gestioneConfigurazioni";
	public final static String PARAMETRO_APS_MODIFICA_API = "modificaAPI";
	public final static String PARAMETRO_APS_MODIFICA_PROFILO = "modificaProfilo";
	public final static String PARAMETRO_APS_CAMBIA_API = "cambiaAPI";
	public final static String PARAMETRO_APS_CONFERMA_MODIFICA_DATI_SERVIZIO = "backToConfermaModificaDatiServizio";
	public final static String PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER;
	public final static String PARAMETRO_APS_ID_APPLICATIVO_SERVER = CostantiControlStation.PARAMETRO_ID_APPLICATIVO_SERVER;
	
	/* ATTRIBUTI SESSIONE */
	
	
	public final static String SESSION_ATTRIBUTE_APS_SERVIZI_AGGIUNTI = "serviziAggiunti";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_APS_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_APS_WSDL_NUOVO = "Nuovo WSDL";
	public final static String LABEL_PARAMETRO_APS_WSDL_ATTUALE  ="WSDL attuale";
	public final static String LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI = "Validazione";
	public final static String LABEL_PARAMETRO_APS_ID_SOGGETTO_EROGATORE = "idErogatore";
	public final static String LABEL_PARAMETRO_APS_TIPO_FILE = "Tipo";
	public final static String LABEL_PARAMETRO_APS_NOME_FILE = "Nome";
	public final static String LABEL_PARAMETRO_APS_NOME_EROGAZIONE = "Nome Erogazione";
	public final static String LABEL_PARAMETRO_APS_NOME_FRUIZIONE = "Nome Fruizione";
	public final static String LABEL_PARAMETRO_APS_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_APS_SCOPE = "Scope";
	public final static String LABEL_PARAMETRO_APS_THE_FILE = "Documento";
	public final static String LABEL_PARAMETRO_APS_ACCORDO = LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE;//"API";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME_ATTUALE = "Attuale";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME_NUOVO = "Nuovo";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE = "Soggetto Referente";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APS_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APS_PROVIDER_EROGATORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_PROVIDER_FRUITORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APS_STATO = "Stato";
	public final static String LABEL_PARAMETRO_APS_SERVIZIO_CORRELATO = "Correlato";
	public final static String LABEL_PARAMETRO_APS_TIPOLOGIA_SERVIZIO = "Tipologia Servizio";
	public final static String LABEL_PARAMETRO_APS_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_APS_VISIBILITA_SERVIZIO = "Visibilit&agrave; Servizio";
	public final static String LABEL_PARAMETRO_APS_VERSIONE_PROTOCOLLO = "Versione Protocollo";
	public final static String LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA = "Validazione Documenti";
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_COMPATTO = "WSDL Impl. Erogatore"; 
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_COMPATTO = "WSDL Impl. Fruitore";
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_ESTESO = "WSDL Implementativo Erogatore"; 
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_ESTESO = "WSDL Implementativo Fruitore";
	public final static String LABEL_PARAMETRO_APS_NOME_PA = "Nome";
	public final static String LABEL_PARAMETRO_APS_NOME_SA = "ServizioApplicativo";
	public final static String LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_EROGATORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_FRUITORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO = "WSDL Implementativo";
	public final static String LABEL_PARAMETRO_APS_RIPRISTINA_STATO_OPERATIVO = "Ripristina Stato Operativo";
	public final static String LABEL_PARAMETRO_APS_SERVICE_BINDING = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING;
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE = "wsdlimplfru";
	public final static String DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE = "wsdlimpler";
	
	public final static String DEFAULT_VALUE_PARAMETRO_APS_SERVICE_BINDING_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
	public final static String DEFAULT_VALUE_PARAMETRO_APS_SERVICE_BINDING_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
	
	public final static String DEFAULT_VALUE_ABILITATO = "abilitato"; 
	public final static String DEFAULT_VALUE_DISABILITATO = "disabilitato";
	public final static String DEFAULT_VALUE_DEFAULT = "default";
	public final static String DEFAULT_VALUE_CORRELATO = "correlato";
	public final static String DEFAULT_VALUE_NORMALE = "normale";
	public final static String DEFAULT_VALUE_PRIVATA = "privata";
	public final static String DEFAULT_VALUE_PUBBLICA = "pubblica";	
	
	/* DEFAULT VALUE ATTRIBUTI SESSIONE */
	

	/* MESSAGGI ERRORE */
	public static final String MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTORIZZAZIONE_XX = "Indicare un nome per l''autorizzazione ''{0}''";
	public static final String MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTENTICAZIONE_XX = "Indicare un nome per l''autenticazione ''{0}''";
	public static final String MESSAGGIO_ERRORE_LA_SPECIFICA_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO = "La {0} con nome {1} (tipo: {2}) &egrave; gi&agrave; presente nel servizio.";
	public static final String MESSAGGIO_ERRORE_LA_SPECIFICA_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_SENZA_TIPO = "La {0} con nome {1} &egrave; gi&agrave; presente nel servizio.";
	public static final String MESSAGGIO_ERRORE_ALLEGATO_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_CON_PARAMETRI = "L''allegato con nome {0} (tipo: {1}) &egrave; gi&agrave; presente nel servizio.";
	public static final String MESSAGGIO_ERRORE_ALLEGATO_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_CON_PARAMETRI_SENZA_TIPO = "L''allegato con nome {0} &egrave; gi&agrave; presente nel servizio.";
	public static final String MESSAGGIO_ERRORE_ESTENSIONE_DEL_DOCUMENTO_NON_VALIDA_DIMENSIONE_ESTENSIONE_TROPPO_LUNGA = "L'estensione del documento non &egrave; valida. La dimensione dell'estensione &egrave; troppo lunga.";
	public static final String MESSAGGIO_ERRORE_ESTENSIONE_DEL_DOCUMENTO_NON_VALIDA = "L'estensione del documento non &egrave; valida.";
	public static final String MESSAGGIO_ERRORE_DOCUMENTO_SELEZIONATO_NON_PUO_ESSERE_VUOTO = "Il documento selezionato non pu&ograve; essere vuoto.";
	public static final String MESSAGGIO_ERRORE_DOCUMENTO_OBBLIGATORIO = "&Egrave; necessario selezionare un documento.";
	public static final String MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_IL_TIPO_DI_DOCUMENTO = "Dati incompleti. &Egrave; necessario indicare il Tipo di documento";
	public static final String MESSAGGIO_ERRORE_ESISTE_GI_AGRAVE_UN_FRUITORE_DEL_SERVIZIO_CON_LO_STESSO_SOGGETTO = "Esiste gi&agrave; un fruitore del Servizio con lo stesso Soggetto";
	public static final String MESSAGGIO_ERRORE_PER_POTER_AGGIUNGERE_IL_FRUITORE_DEVE_ESSERE_DEFINITO_IL_CONNETTORE_BR_IN_ALTERNATIVA_È_POSSIBILE_CONFIGURARE_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE_PRIMA_DI_PROCEDERE_CON_LA_CREAZIONE_DEL_FRUITORE = "Per poter aggiungere il fruitore deve essere definito il connettore.<br/>In alternativa è possibile configurare un connettore sul servizio o sul soggetto erogatore prima di procedere con la creazione del fruitore.";
	public static final String MESSAGGIO_ERRORE_PER_POTER_AGGIUNGERE_IL_FRUITORE_DEVE_PRIMA_ESSERE_DEFINITO_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE = "Per poter aggiungere il fruitore deve prima essere definito un connettore sul servizio o sul soggetto erogatore.";
	public static final String MESSAGGIO_ERRORE_PER_POTER_DISABILITARE_IL_CONNETTORE_DEVE_PRIMA_ESSERE_DEFINITO_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE = "Per poter disabilitare il connettore deve prima essere definito un connettore sul servizio o sul soggetto erogatore";
	public static final String MESSAGGIO_ERRORE_VALIDAZIONE_PROTOCOLLO_CON_PARAMETRI = "[validazione-{0}] {1}";
	public static final String MESSAGGIO_ERRORE_API_SELEZIONATA_NON_ESISTENTE_CON_PARAMETRI = "API selezionata ({0}) non esistente: {1}";
	public static final String MESSAGGIO_ERRORE_ESISTE_GIA_UNA_PORTA_APPLICATIVA_CON_PARAMETRO = "Esiste gi&agrave; una porta applicativa con nome {0}";
	public static final String MESSAGGIO_ERRORE_ESISTE_GIA_UN_ACCORDO_DI_SERVIZIO_PARTE_SPECIFICA_CON_TIPO_NOME_VERSIONE_E_SOGGETTO_INDICATO = "Esiste gi&agrave; un accordo di servizio parte specifica con tipo, nome, versione e soggetto indicato.";
	public static final String MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI = 
			"Esiste gi&agrave; una erogazione del servizio {0} erogato dal Soggetto {1}";
	public static final String MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI_FRUIZIONE = 
			"Esiste gi&agrave; una fruizione, da parte del Soggetto {0}, del servizio {1} erogato dal Soggetto {2}";
	public static final String MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_API_DIFFERENTE = 
			"Esiste gi&agrave; una fruizione o erogazione del servizio {0} erogato dal Soggetto {1} che implementa una API differente: {2}";
	public static final String MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_VERSIONE_PROTOCOLLO_DIFFERENTE = 
			"Esiste gi&agrave; una fruizione o erogazione del servizio {0} erogato dal Soggetto {1} configurata con una versione di protocollo differente: {2}";
	public static final String MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PORT_TYPE_DIFFERENTE = 
			"Esiste gi&agrave; una fruizione o erogazione del servizio {0} erogato dal Soggetto {1} che implementa un servizio differente: {2}";
	public static final String MESSAGGIO_ERRORE_ID_ACCORDO_SERVIZIO_NON_DEFINITO = "id Accordo Servizio non definito";
	public static final String MESSAGGIO_ERRORE_ID_SOGGETTO_EROGATORE_NON_DEFINITO = "id Soggetto erogatore non definito";
	public static final String MESSAGGIO_ERRORE_IL_CONNETTORE_SUL_SERVIZIO_NON_PUO_ESSERE_DISABILITATO_POICHE_NON_E_STATO_DEFINITO_UN_CONNETTORE_SUL_SOGGETTO_EROGATORE_ED_ESISTONO_FRUIZIONI_DEL_SERVIZIO_DA_PARTE_DI_SOGGETTI_OPERATIVI_CHE_NON_HANNO_UN_CONNETTORE_DEFINITO = "Il connettore sul servizio non può essere disabilitato poichè non è stato definito un connettore sul soggetto erogatore ed esistono fruizioni del servizio, da parte di soggetti operativi, che non hanno un connettore definito";
	public static final String MESSAGGIO_ERRORE_IL_CONNETTORE_DEL_SERVIZIO_DEVE_ESSERE_SPECIFICATO_SE_NON_EGRAVE_STATO_DEFINITO_UN_CONNETTORE_PER_IL_SOGGETTO_EROGATORE = "Il connettore del servizio deve essere specificato se non &egrave; stato definito un connettore per il soggetto erogatore";
	public static final String MESSAGGIO_ERRORE_IL_CONNETTORE_SUL_SERVIZIO_NON_PUO_ESSERE_DISABILITATO_POICHE_NON_E_STATO_DEFINITO_UN_CONNETTORE_EROGAZIONE = "Deve essere definito un connettore per l'erogazione";
	public static final String MESSAGGIO_ERRORE_USO_SOGGETTO_EROGATORE_CON_VISIBILITA_PRIVATA_IN_UN_SERVIZIO_CON_VISIBILITA_PUBBLICA = "Non &egrave; possibile utilizzare un soggetto erogatore con visibilit&agrave; privata, in un servizio con visibilit&agrave; pubblica.";
	public static final String MESSAGGIO_ERRORE_ACCORDO_SERVIZIO_DEV_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_ACCORDI_SERVIZIO = "L'accordo servizio dev'essere scelto tra quelli definiti nel pannello Accordi servizio";
	public static final String MESSAGGIO_ERRORE_IL_SOGGETTO_DEV_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_SOGGETTI = "Il soggetto dev'essere scelto tra quelli definiti nel pannello Soggetti";
	public static final String MESSAGGIO_ERRORE_IL_SOGGETTO_INDICATO_NON_AUTORIZZATO_A_EROGARE = "Il soggetto indicato non è autorizzato ad erogare una API";
	public static final String MESSAGGIO_ERRORE_SERVIZIO_CORRELATO_DEV_ESSERE_SELEZIONATO_O_DESELEZIONATO = "Servizio correlato dev'essere selezionato o deselezionato";
	public static final String MESSAGGIO_ERRORE_DATI_INCOMPLETI_CON_PARAMETRO = "Dati incompleti. &Egrave; necessario indicare: {0}";
	public static final String MESSAGGIO_ERRORE_DATI_INCOMPLETI_SOGGETTO_MANCANTE = "Dati incompleti. &Egrave; necessario indicare un Soggetto";
	public static final String MESSAGGIO_ERRORE_SPAZI_BIANCHI_NON_CONSENTITI = "Non inserire spazi nei campi di testo";
	public static final String MESSAGGIO_ERRORE_SERVIZIO_OBBLIGATORIO = "&Egrave; necessario indicare un Servizio.";
	public static final String MESSAGGIO_ERRORE_SERVIZIO_OBBLIGATORIO_PORT_TYPE_NON_PRESENTI = "&Egrave; necessario indicare un Servizio, definire almeno un Port-Type per completare l'operazione.";
	public static final String MESSAGGIO_ERRORE_USO_ACCORDO_SERVIZIO_CON_VISIBILITA_PRIVATA_IN_UN_SERVIZIO_CON_VISIBILITA_PUBBLICA = "Non &egrave; possibile utilizzare un accordo di servizio con visibilit&agrave; privata, in un servizio con visibilit&agrave; pubblica.";
	public static final String MESSAGGIO_ERRORE_PRIMA_DI_POTER_DEFINIRE_UN_ACCORDO_PARTE_SPECIFICA_DEVE_ESSERE_CREATO_UN_SERVIZIO_APPLICATIVO_EROGATO_DAL_SOGGETTO_X_Y = "Prima di poter definire un accordo parte specifica deve essere creato un servizio applicativo erogato dal soggetto {0}/{1}";
	public static final String MESSAGGIO_ERRORE_NON_E_POSSIBILE_CREARE_L_ACCORDO_PARTE_SPECIFICA_SENZA_SELEZIONARE_UN_SERVIZIO_APPLICATIVO_EROGATORE = "Non &egrave; possibile creare l'accordo parte specifica senza selezionare un servizio applicativo erogatore";
	public static final String MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT_EROGAZIONE = "Non è possibile eliminare il gruppo '"+Costanti.MAPPING_EROGAZIONE_PA_DESCRIZIONE_DEFAULT+"'";
	public static final String MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT_FRUIZIONE = "Non è possibile eliminare il gruppo '"+Costanti.MAPPING_FRUIZIONE_PD_DESCRIZIONE_DEFAULT+"'";
	public static final String MESSAGGIO_ERRORE_ABILITARE_AUTENTICAZIONE_PER_AUTORIZZAZIONE_PUNTUALE = "Per poter abilitare l'autorizzazione per richiedente, devi abilitare l'autenticazione";
}
