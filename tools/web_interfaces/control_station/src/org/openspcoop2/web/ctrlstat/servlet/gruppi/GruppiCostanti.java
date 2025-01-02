/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.gruppi;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * GruppiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GruppiCostanti {
	
	private GruppiCostanti() {}

	public static final String OBJECT_NAME_GRUPPI = "gruppi";

	public static final String SERVLET_NAME_GRUPPI_ADD = OBJECT_NAME_GRUPPI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_GRUPPI_CHANGE = OBJECT_NAME_GRUPPI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_GRUPPI_DELETE = OBJECT_NAME_GRUPPI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_GRUPPI_LIST = OBJECT_NAME_GRUPPI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_GRUPPI = new ArrayList<>();
	public static List<String> getServletGruppi() {
		return SERVLET_GRUPPI;
	}
	static{
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_ADD);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_CHANGE);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_DELETE);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_LIST);
	}
	
	/* NOME VISTA CUSTOM */
	public static final String GRUPPI_NOME_VISTA_CUSTOM_LISTA = "gruppi";
	
	
	public static final String LABEL_GRUPPO = "Tag";
	public static final String LABEL_GRUPPI = "Tags";
	
	
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_GRUPPO_ID = "gruppoId";
	public static final String PARAMETRO_GRUPPO_NOME = "gruppoNome";
	public static final String PARAMETRO_GRUPPO_DESCRIZIONE = "gruppoDescrizione";
	public static final String PARAMETRO_GRUPPO_SERVICE_BINDING = "gruppoServiceBinding";
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_GRUPPO_NOME = "Nome";
	public static final String LABEL_PARAMETRO_GRUPPO_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING = "Tipo";
	public static final String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public static final String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
	public static final String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
	
	public static final String LABEL_GRUPPI_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_GRUPPI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.GRUPPO.name()+"')";
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public static final String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
	public static final String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
	public static final String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
	
	private static final String[] VALUES_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING = {  DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI, DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP, DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_REST };
	public static String[] getValuesSelectParametroGruppoServiceBinding() {
		return VALUES_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING;
	}
	private static final String[] LABELS_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING = {  LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI, LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP, LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST  };
	public static String[] getLabelsSelectParametroGruppoServiceBinding() {
		return LABELS_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING;
	}

	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "Il tag non risulta utilizzato in alcuna configurazione";	
	
	public static final String MESSAGE_METADATI_GRUPPO_TIPO = GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING +": {0}";
}
