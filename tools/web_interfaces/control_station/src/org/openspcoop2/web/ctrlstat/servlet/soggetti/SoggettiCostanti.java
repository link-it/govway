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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * SoggettiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiCostanti {

	/* OBJECT NAME */

	public final static String OBJECT_NAME_SOGGETTI = "soggetti";

	public final static ForwardParams TIPO_OPERAZIONE_ENDPOINT = ForwardParams.OTHER("EndPoint");
	
	/* SERVLET NAME */

	public final static String SERVLET_NAME_SOGGETTI_ADD = OBJECT_NAME_SOGGETTI
			+ "Add.do";
	public final static String SERVLET_NAME_SOGGETTI_CHANGE = OBJECT_NAME_SOGGETTI
			+ "Change.do";
	public final static String SERVLET_NAME_SOGGETTI_DELETE = OBJECT_NAME_SOGGETTI
			+ "Del.do";
	public final static String SERVLET_NAME_SOGGETTI_LIST = OBJECT_NAME_SOGGETTI
			+ "List.do";
	public final static String SERVLET_NAME_SOGGETTI_ENDPOINT = OBJECT_NAME_SOGGETTI
			+ TIPO_OPERAZIONE_ENDPOINT.getOtherContext()+".do";
	public final static Vector<String> SERVLET_SOGGETTI = new Vector<String>();
	static {
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ADD);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_CHANGE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_DELETE);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_LIST);
		SERVLET_SOGGETTI.add(SERVLET_NAME_SOGGETTI_ENDPOINT);
	}

	/* LABEL GENERALI */

	public final static String LABEL_SOGGETTI = "Soggetti";
	public final static String LABEL_SOGGETTO = "Soggetto";
	public final static String LABEL_CLIENT = "Client";
	public final static String LABEL_SERVER = "Server";
	public final static String LABEL_SOGGETTI_ESPORTA_SELEZIONATI = "Esporta Selezionati";
	public final static String LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.SOGGETTO.name()+"')";
	public final static String LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA = "Soggetti";

	/* PARAMETRI */

	public final static String PARAMETRO_SOGGETTO_ID = "id";
	public final static String PARAMETRO_SOGGETTO_PROTOCOLLO = "protocollo";
	public final static String PARAMETRO_SOGGETTO_NOME = "nomeprov";
	public final static String PARAMETRO_SOGGETTO_TIPO = "tipoprov";
	public final static String PARAMETRO_SOGGETTO_CODICE_PORTA = "portadom";
	public final static String PARAMETRO_SOGGETTO_DESCRIZIONE = "descr";
	public final static String PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "profilo";
	public final static String PARAMETRO_SOGGETTO_PDD = "pdd";
	public final static String PARAMETRO_SOGGETTO_IS_ROUTER = "is_router";
	public final static String PARAMETRO_SOGGETTO_IS_PRIVATO = "privato";
	public final static String PARAMETRO_SOGGETTO_CODICE_IPA = "codice_ipa";
	public final static String PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "pd_url_prefix_rewriter";
	public final static String PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "pa_url_prefix_rewriter";
	
	/* LABEL PARAMETRI */

	public final static String LABEL_PARAMETRO_SOGGETTO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_SOGGETTO_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO = "Protocollo";
	public final static String LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA = "Identificativo Porta";
	public final static String LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO = "Versione Protocollo";
	public final static String LABEL_PARAMETRO_SOGGETTO_IS_ROUTER = "Router";
	public final static String LABEL_PARAMETRO_SOGGETTO_IS_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_SOGGETTO_CODICE_IPA = "Codice IPA";
	public final static String LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER = "UrlPrefix rewriter";
	public final static String LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER = "UrlPrefix rewriter";

	/* DEFAULT VALUE PARAMETRI */



}
