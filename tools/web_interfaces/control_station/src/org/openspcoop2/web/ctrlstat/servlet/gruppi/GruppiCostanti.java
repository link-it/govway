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
package org.openspcoop2.web.ctrlstat.servlet.gruppi;

import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;

/**
 * GruppiCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GruppiCostanti {

	public final static String OBJECT_NAME_GRUPPI = "gruppi";

	public final static String SERVLET_NAME_GRUPPI_ADD = OBJECT_NAME_GRUPPI+"Add.do";
	public final static String SERVLET_NAME_GRUPPI_CHANGE = OBJECT_NAME_GRUPPI+"Change.do";
	public final static String SERVLET_NAME_GRUPPI_DELETE = OBJECT_NAME_GRUPPI+"Del.do";
	public final static String SERVLET_NAME_GRUPPI_LIST = OBJECT_NAME_GRUPPI+"List.do";
	public final static Vector<String> SERVLET_GRUPPI = new Vector<String>();
	static{
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_ADD);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_CHANGE);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_DELETE);
		SERVLET_GRUPPI.add(SERVLET_NAME_GRUPPI_LIST);
	}
	
	
	
	
	public final static String LABEL_GRUPPO = "Tag";
	public final static String LABEL_GRUPPI = "Tags";
	
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_GRUPPO_ID = "gruppoId";
	public final static String PARAMETRO_GRUPPO_NOME = "gruppoNome";
	public final static String PARAMETRO_GRUPPO_DESCRIZIONE = "gruppoDescrizione";
	public final static String PARAMETRO_GRUPPO_SERVICE_BINDING = "gruppoServiceBinding";
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_GRUPPO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_GRUPPO_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING = "Tipo";
	public final static String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
	public final static String LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
	
	public final static String LABEL_GRUPPI_ESPORTA_SELEZIONATI = "Esporta";
	public final static String LABEL_GRUPPI_ESPORTA_SELEZIONATI_ONCLICK = "Esporta('"+ArchiveType.GRUPPO.name()+"')";
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
	public final static String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
	public final static String DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_REST = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
	
	public final static String[] VALUES_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING = {  DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI, DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP, DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_REST };
	public final static String[] LABELS_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING = {  LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI, LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP, LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST  };
	
	
}
