/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.Vector;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreFileUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreFileUtils {

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		connettore.setCustom(true);
		
		org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
		prop.setValore(requestOutputFileName);
		connettore.addProperty(prop);
		
		if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
			prop.setValore(requestOutputFileNameHeaders);
			connettore.addProperty(prop);
		}

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		if(ServletUtils.isCheckBoxEnabled(requestOutputParentDirCreateIfNotExists)){
			prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
		}
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
		if(ServletUtils.isCheckBoxEnabled(requestOutputOverwriteIfExists)){
			prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
		}
		connettore.addProperty(prop);
		
		
		prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		prop.setValore(responseInputMode);
		connettore.addProperty(prop);
		
		
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
			prop.setValore(responseInputFileName);
			connettore.addProperty(prop);
			
			if(responseInputFileNameHeaders!=null && !"".equals(responseInputFileNameHeaders)){
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
				prop.setValore(responseInputFileNameHeaders);
				connettore.addProperty(prop);
			}
			
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
			if(ServletUtils.isCheckBoxEnabled(responseInputDeleteAfterRead)){
				prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
			}
			else{
				prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			}
			connettore.addProperty(prop);
			
			if(responseInputWaitTime!=null && !"".equals(responseInputWaitTime)){
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
				prop.setValore(responseInputWaitTime);
				connettore.addProperty(prop);
			}
		}
	}
	
	public static void fillConnettoreRegistry(org.openspcoop2.core.registry.Connettore connettore,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		connettore.setCustom(true);
	
		org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
		prop.setValore(requestOutputFileName);
		connettore.addProperty(prop);

		if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
			prop.setValore(requestOutputFileNameHeaders);
			connettore.addProperty(prop);
		}
		
		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		if(ServletUtils.isCheckBoxEnabled(requestOutputParentDirCreateIfNotExists)){
			prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
		}
		connettore.addProperty(prop);

		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
		if(ServletUtils.isCheckBoxEnabled(requestOutputOverwriteIfExists)){
			prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
		}
		connettore.addProperty(prop);
		
		
		prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		prop.setValore(responseInputMode);
		connettore.addProperty(prop);
		
		
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
			prop.setValore(responseInputFileName);
			connettore.addProperty(prop);
			
			if(responseInputFileNameHeaders!=null && !"".equals(responseInputFileNameHeaders)){
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
				prop.setValore(responseInputFileNameHeaders);
				connettore.addProperty(prop);
			}
			
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
			if(ServletUtils.isCheckBoxEnabled(responseInputDeleteAfterRead)){
				prop.setValore(CostantiConfigurazione.ABILITATO.getValue());
			}
			else{
				prop.setValore(CostantiConfigurazione.DISABILITATO.getValue());
			}
			connettore.addProperty(prop);
			
			if(responseInputWaitTime!=null && !"".equals(responseInputWaitTime)){
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
				prop.setValore(responseInputWaitTime);
				connettore.addProperty(prop);
			}
		}
	}
	
	public static void addFileDati(Vector<DataElement> dati,int pageSize,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){

		DataElementInfo dInfoPattern = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
		dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
		dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI);
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setValue(requestOutputFileName);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE);
		de.setRequired(true);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setSize(pageSize);
		de.setInfo(dInfoPattern);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setValue(requestOutputFileNameHeaders);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE);
		de.setRequired(false);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setSize(pageSize);
		de.setInfo(dInfoPattern);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setSelected(ServletUtils.isCheckBoxEnabled(requestOutputParentDirCreateIfNotExists));
		de.setValue(requestOutputParentDirCreateIfNotExists);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setSize(pageSize);
		dati.addElement(de);
			
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setSelected(ServletUtils.isCheckBoxEnabled(requestOutputOverwriteIfExists));
		de.setValue(requestOutputOverwriteIfExists);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setSize(pageSize);
		dati.addElement(de);
		
		
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPI_GESTIONE_RESPONSE_FILE);
		de.setValue(responseInputMode);
		de.setSelected(responseInputMode);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setPostBack(true);
		de.setSize(pageSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setValue(responseInputFileName);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_SIZE);
			de.setRequired(true);
			de.setInfo(dInfoPattern);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setSize(pageSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setValue(responseInputFileNameHeaders);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS_SIZE);
			de.setInfo(dInfoPattern);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setSize(pageSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		de.setValue(responseInputDeleteAfterRead);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setSelected(ServletUtils.isCheckBoxEnabled(responseInputDeleteAfterRead));
			de.setType(DataElementType.CHECKBOX);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		de.setSize(pageSize);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		de.setValue(responseInputWaitTime);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setType(DataElementType.TEXT_EDIT);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		de.setSize(pageSize);
		dati.addElement(de);
		
	}
	
	public static void addFileDatiAsHidden(Vector<DataElement> dati,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setValue(requestOutputFileName);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setValue(requestOutputFileNameHeaders);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setValue(requestOutputParentDirCreateIfNotExists);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		dati.addElement(de);
			
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setValue(requestOutputOverwriteIfExists);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		dati.addElement(de);
		
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(responseInputMode);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setValue(responseInputFileName);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setValue(responseInputFileNameHeaders);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		de.setValue(responseInputDeleteAfterRead);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		de.setValue(responseInputWaitTime);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		dati.addElement(de);

	}
	
}
