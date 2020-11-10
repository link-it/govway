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
package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * ProtocolPropertiesCostanti
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesCostanti {

	public final static String OBJECT_NAME_PP = "protocolProperty";
	
	public final static ForwardParams TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE = ForwardParams.OTHER("BinaryPropertyChange");
	public static final String SERVLET_NAME_BINARY_PROPERTY_CHANGE = OBJECT_NAME_PP+"BinaryPropertyChange.do";
	
	public final static Vector<String> SERVLET_PP = new Vector<String>();
	static{
		SERVLET_PP.add(SERVLET_NAME_BINARY_PROPERTY_CHANGE);
	}
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_PP_ID = "id";
	public static final String PARAMETRO_PP_NOME = "nome";
	public static final String PARAMETRO_PP_ID_PROPRIETARIO = "idProprietario";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO = "tipoProprietario";
	public static final String PARAMETRO_PP_NOME_PROPRIETARIO = "nomeProprietario";
	public static final String PARAMETRO_PP_NOME_PARENT_PROPRIETARIO = "nomeParentProprietario";
	public static final String PARAMETRO_PP_URL_ORIGINALE_CHANGE = "urlOrigChange";
	public static final String PARAMETRO_PP_SET = "ppSet";
	public final static String PARAMETRO_PP_CONTENUTO_DOCUMENTO = "contenutoDocumento";
	public final static String PARAMETRO_PP_CONTENUTO_DOCUMENTO_WARN = "contenutoDocumentoWarn";
	public final static String PARAMETRO_PP_PROTOCOLLO = "protocollo";
	public static final String PARAMETRO_PP_TIPO_ACCORDO = "tipoAccordo";
	public final static String PARAMETRO_PP_ID_ALLEGATO = "idAllegato";
	public static final String PARAMETRO_PP_CHANGE_BINARY = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_PP_CHANGE_BINARY;
	public static final String PARAMETRO_PP_CHANGE_BINARY_VALUE_TRUE = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_PP_CHANGE_BINARY_VALUE_TRUE;
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_PP_ID = "Id Property";
	public final static String LABEL_PARAMETRO_PP_ID_PROPRIETARIO = "Id Proprietario";
	public final static String LABEL_PARAMETRO_PP_TIPO_PROPRIETARIO = "Tipo Proprietario";
	public final static String LABEL_GESTIONE_DOCUMENTO = "Gestione Documento";
	public final static String LABEL_GESTIONE = "Gestione ";

	public final static String LABEL_SOGGETTO = "Soggetto";
	public final static String LABEL_SERVIZIO_APPLICATIVO = "Applicativo";
	public final static String LABEL_APC = "Accordo Parte Comune";
	public final static String LABEL_ASC = "Servizio Composto";
	public final static String LABEL_APS = "Accordo Parte Specifica";
	public final static String LABEL_PORT_TYPE= "Port Type";
	public final static String LABEL_AZIONE = "Azione";
	public final static String LABEL_AZIONE_ACCORDO = "Azione Accordo";
	public final static String LABEL_SERVIZIO = "Servizio";
	public final static String LABEL_RESOURCE = "Risorsa";
	public final static String LABEL_AC = "Accordo Cooperazione";
	public final static String LABEL_OPERATION = "Operazione";
	public final static String LABEL_FRUITORE = "Fruitore";
	public final static String LABEL_DOCUMENTO_ATTUALE = "Attuale";
	public final static String LABEL_NOME = "Nome";
	public final static String LABEL_DOWNLOAD = "Download";
	public final static String LABEL_DOCUMENTO_AGGIORNAMENTO = "Aggiorna Documento";
	public final static String LABEL_AGGIORNAMENTO = "Aggiorna ";
	public final static String LABEL_DOCUMENTO_NOT_FOUND = "non fornito";
	public final static String LABEL_DOCUMENTO_NUOVO = "Nuovo Documento";
	public final static String LABEL_CONTENUTO_NUOVO = "Nuovo Contenuto";
	public final static String LABEL_DOCUMENTO_CHANGE_CLEAR_WARNING = "Warning: ";
	public final static String LABEL_DOCUMENTO_CHANGE_CLEAR = "Se si desidera eliminare un contenuto precedentemente caricato cliccare su 'Salva' senza selezionare alcun file"; //fornirne un'altra versione";

	/* VALORI PARAMETRI */
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SERVIZIO_APPLICATIVO = "SERVIZIO_APPLICATIVO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SOGGETTO = "SOGGETTO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_COOPERAZIONE = "ACCORDO_COOPERAZIONE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE = "ACCORDO_SERVIZIO_PARTE_COMUNE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_COMPOSTO = "ACCORDO_SERVIZIO_COMPOSTO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_PORT_TYPE = "PORT_TYPE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_OPERATION = "OPERATION";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_AZIONE_ACCORDO = "AZIONE_ACCORDO";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "ACCORDO_SERVIZIO_PARTE_SPECIFICA";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_FRUITORE = "FRUITORE";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_RESOURCE = "RESOURCE";
	
	public final static String PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE = "apc";
	public final static String PARAMETRO_VALORE_PP_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "asc";

	public static final String PARAMETER_FILENAME_PREFIX = Costanti.PARAMETER_FILENAME_PREFIX;  
	public static final String PARAMETER_FILEID_PREFIX = Costanti.PARAMETER_FILEID_PREFIX;
	public static final String PARAMETER_FILENAME_RECHANGE_PREFIX = "__fnReChange__";
 
}