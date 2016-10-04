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
package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
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
	
	public final static String OBJECT_NAME_APS_SERVIZI_APPLICATIVI = "accordiServizioParteSpecificaServizioApplicativo";
	
	public final static String OBJECT_NAME_APS_PORTE_APPLICATIVE = "accordiServizioParteSpecificaPorteApplicative";
	
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
	
	public final static String SERVLET_NAME_APS_SERVIZI_APPLICATIVI_ADD = OBJECT_NAME_APS_SERVIZI_APPLICATIVI+"Add.do";
	public final static String SERVLET_NAME_APS_SERVIZI_APPLICATIVI_DELETE = OBJECT_NAME_APS_SERVIZI_APPLICATIVI+"Del.do";
	public final static String SERVLET_NAME_APS_SERVIZI_APPLICATIVI_LIST = OBJECT_NAME_APS_SERVIZI_APPLICATIVI+"List.do";
	public final static Vector<String> SERVLET_APS_SERVIZI_APPLICATIVI = new Vector<String>();
	static{
		SERVLET_APS.add(SERVLET_NAME_APS_SERVIZI_APPLICATIVI_ADD);
		SERVLET_APS.add(SERVLET_NAME_APS_SERVIZI_APPLICATIVI_DELETE);
		SERVLET_APS.add(SERVLET_NAME_APS_SERVIZI_APPLICATIVI_LIST);
	}
	
	public final static String SERVLET_NAME_APS_PORTE_APPLICATIVE_DELETE = OBJECT_NAME_APS_PORTE_APPLICATIVE+"Del.do";
	public final static String SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST = OBJECT_NAME_APS_PORTE_APPLICATIVE+"List.do";
	public final static Vector<String> SERVLET_APS_PORTE_APPLICATIVE = new Vector<String>();
	static{
		SERVLET_APS.add(SERVLET_NAME_APS_PORTE_APPLICATIVE_DELETE);
		SERVLET_APS.add(SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST);
	}
	
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_APS = "Accordi Servizio Parte Specifica";
	public final static String LABEL_APS_SINGOLO = "Accordo Servizio Parte Specifica";
	public final static String LABEL_APC_COMPOSTO = "Accordo Servizio (ParteComune/Composto)";
	public final static String LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE = "Accordo Servizio Parte Comune";
	public final static String LABEL_APC_COMPOSTO_SOLO_COMPOSTO = "Accordo Servizio Composto";
	public final static String LABEL_APS_SERVIZI = "Servizi";
	public final static String LABEL_APS_FRUITORI = "Fruitori";
	public final static String LABEL_APS_FRUITORE = "Fruitore";
	public final static String LABEL_APS_CORRELATO = "Correlato";
	public final static String LABEL_APS_SERVIZI_APPLICATIVI_AUTORIZZATI_DI = "Servizi Applicativi Autorizzati di ";
	public final static String LABEL_APS_SERVIZI_APPLICATIVI_AUTORIZZATI = "Servizi Applicativi Autorizzati";
	public final static String LABEL_APS_FUITORI_DI = "Fruitori di ";
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI = "WSDL Implementativo Erogatore di "; 
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI = "WSDL Implementativo Fruitore di ";
	public final static String LABEL_APS_ALLEGATI_DI = "Allegati di ";
	public final static String LABEL_APS_DOWNLOAD = "Download";
	public final static String LABEL_APS_SERVIZIO = "Servizio";
	public final static String LABEL_APS_ALLEGATI = "Allegati";
	public final static String LABEL_APS_PORTE_APPLICATIVE = "Porte Applicative";
	public final static String LABEL_APS_PORTE_APPLICATIVE_DI = "Porte Applicative di ";
	public final static String LABEL_APS_STATO = "Stato";
	public final static String LABEL_APS_ESPORTA_SELEZIONATI = "Esporta Selezionati";
	public final static String LABEL_APS_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name()+"')";
	public final static String LABEL_APS_USA_VERSIONE_EROGATORE = "usa versione erogatore";
	public final static String LABEL_APS_USA_PROFILO_PDD_FRUITORE = "usa profilo della pdd del fruitore";
	public final static String LABEL_APS_SPECIFICA_PORTI_ACCESSO = "Specifica dei Porti di Accesso";
	public final static String LABEL_APS_SPECIFICA_PORTA_APPLICATIVA = "Porta Applicativa";
	public final static String LABEL_APS_ALTRE_INFORMAZIONI = "Altre informazioni";
	public final static String LABEL_APS_SERVIZIO_APPLICATIVO_EROGATORE = "Servizio Applicativo Erogatore";
	public final static String LABEL_APS_SERVIZIO_APPLICATIVO_FRUITORE = "Servizio Applicativo Fruitore";
	public final static String LABEL_APS_SOGGETTO_EROGATORE = "Soggetto Erogatore";
	public final static String LABEL_APS_WSDL_IMPLEMENTATIVO_DI = "WSDL Implementativo di ";
	public final static String LABEL_WSDL_CHANGE_CLEAR_WARNING = "Warning: ";
	public final static String LABEL_WSDL_CHANGE_CLEAR = "Se si desidera eliminare un documento precedentemente caricato cliccare su 'Invia' senza selezionare alcun file"; //fornirne un'altra versione";
	public final static String LABEL_WSDL_AGGIORNAMENTO = "Aggiornamento WSDL";
	public final static String LABEL_WSDL_NOT_FOUND = "non fornito";
	public final static String LABEL_WSDL_ATTUALE = "Attuale";
	public final static String LABEL_WSDL_NUOVO = "Nuovo WSDL";
	public final static String LABEL_AGGIUNTA_FRUITORI_COMPLETATA = "Tutti i soggetti sono già stati aggiunti come fruitori";
	
	
	
	public final static String LABEL_APS_MENU_VISUALE_AGGREGATA = "Accordi Parte Specifica";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_APS_ID = "id";
	public final static String PARAMETRO_APS_MY_ID = "myId";
	public final static String PARAMETRO_APS_ID_SOGGETTO = "idsogg";
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
	public final static String PARAMETRO_APS_PORT_TYPE = "port_type";
	public final static String PARAMETRO_APS_PORT_TYPE_OLD = "port_type_old";
	public final static String PARAMETRO_APS_DESCRIZIONE = "descrizione";
	public final static String PARAMETRO_APS_PRIVATO = "privato";
	public final static String PARAMETRO_APS_NOME_APS = "nome_aps";
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
	public final static String PARAMETRO_APS_PROVIDER =  "provider";
	public final static String PARAMETRO_APS_PROVIDER_TEXT =  "providerText";
	public final static String PARAMETRO_APS_SERVIZIO_CORRELATO_LABEL = "servcorrLabel";
	public final static String PARAMETRO_APS_PRIVATO_LABEL = "privatoLabel";
	public final static String PARAMETRO_APS_PROFILO = "profilo";
	public final static String PARAMETRO_APS_NOME_SOGGETTO = "nomeSogg";
	public final static String PARAMETRO_APS_TIPO_SOGGETTO = "tipoSogg";	
	public final static String PARAMETRO_APS_CORRELATO = "correlato";	
	public final static String PARAMETRO_APS_ID_SERVIZIO = "idServ";
	public final static String PARAMETRO_APS_STATO = "stato";
	public final static String PARAMETRO_APS_NOME_PA = "nomePA";
	public final static String PARAMETRO_APS_NOME_SA = "nomeServizioApplicativo";
	public final static String PARAMETRO_APS_RIPRISTINA_STATO = "backToStato";
	
	
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
	public final static String LABEL_PARAMETRO_APS_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_APS_THE_FILE = "Documento";
	public final static String LABEL_PARAMETRO_APS_ACCORDO = "Accordo";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME = "Nome";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE = "Soggetto Referente";
	public final static String LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APS_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_APS_PROVIDER = "Nome";
	public final static String LABEL_PARAMETRO_APS_PROVIDER_FRUITORE = "Soggetto Fruitore";
	public final static String LABEL_PARAMETRO_APS_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_APS_STATO = "Stato";
	public final static String LABEL_PARAMETRO_APS_SERVIZIO_CORRELATO = "Correlato";
	public final static String LABEL_PARAMETRO_APS_TIPOLOGIA_SERVIZIO = "Tipologia Servizio";
	public final static String LABEL_PARAMETRO_APS_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_APS_VISIBILITA_SERVIZIO = "Visibilit&agrave; Servizio";
	public final static String LABEL_PARAMETRO_APS_VERSIONE_PROTOCOLLO = "Versione Protocollo";
	public final static String LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA = "Validazione Documenti";
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE = "WSDL Implementativo Erogatore"; 
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE = "WSDL Implementativo Fruitore";
	public final static String LABEL_PARAMETRO_APS_NOME_PA = "Nome";
	public final static String LABEL_PARAMETRO_APS_NOME_SA = "ServizioApplicativo";
	public final static String LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_EROGATORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_FRUITORE = "Nome";
	public final static String LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO = "WSDL Implementativo";
	public final static String LABEL_PARAMETRO_APS_RIPRISTINA_STATO_OPERATIVO = "Ripristina Stato Operativo";
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE = "wsdlimplfru";
	public final static String DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE = "wsdlimpler";
	
	 
	
	public final static String DEFAULT_VALUE_ABILITATO = "abilitato"; 
	public final static String DEFAULT_VALUE_DISABILITATO = "disabilitato";
	public final static String DEFAULT_VALUE_DEFAULT = "default";
	public final static String DEFAULT_VALUE_CORRELATO = "correlato";
	public final static String DEFAULT_VALUE_NORMALE = "normale";
	public final static String DEFAULT_VALUE_PRIVATA = "privata";
	public final static String DEFAULT_VALUE_PUBBLICA = "pubblica";	

	
}
