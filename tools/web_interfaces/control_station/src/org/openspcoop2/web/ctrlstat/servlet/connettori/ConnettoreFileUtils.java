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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.pdd.core.connettori.ConnettoreFILE;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
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
	
	private ConnettoreFileUtils() {}

	public static void fillConnettoreConfig(org.openspcoop2.core.config.Connettore connettore,
			String requestOutputFileName, String requestOutputFileNamePermissions, String requestOutputFileNameHeaders, String requestOutputFileNameHeadersPermissions,
			String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		connettore.setCustom(true);
		
		org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
		prop.setValore(requestOutputFileName);
		connettore.addProperty(prop);
		
		if(requestOutputFileNamePermissions!=null && !"".equals(requestOutputFileNamePermissions)){
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);
			prop.setValore(requestOutputFileNamePermissions);
			connettore.addProperty(prop);
		}
		
		if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
			prop.setValore(requestOutputFileNameHeaders);
			connettore.addProperty(prop);
		}
		
		if(requestOutputFileNameHeadersPermissions!=null && !"".equals(requestOutputFileNameHeadersPermissions)){
			prop = new org.openspcoop2.core.config.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);
			prop.setValore(requestOutputFileNameHeadersPermissions);
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
			String requestOutputFileName, String requestOutputFileNamePermissions, String requestOutputFileNameHeaders, String requestOutputFileNameHeadersPermissions,
			String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		connettore.setCustom(true);
	
		org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
		prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
		prop.setValore(requestOutputFileName);
		connettore.addProperty(prop);
		
		if(requestOutputFileNamePermissions!=null && !"".equals(requestOutputFileNamePermissions)){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);
			prop.setValore(requestOutputFileNamePermissions);
			connettore.addProperty(prop);
		}

		if(requestOutputFileNameHeaders!=null && !"".equals(requestOutputFileNameHeaders)){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
			prop.setValore(requestOutputFileNameHeaders);
			connettore.addProperty(prop);
		}
		
		if(requestOutputFileNameHeadersPermissions!=null && !"".equals(requestOutputFileNameHeadersPermissions)){
			prop = new org.openspcoop2.core.registry.Property();
			prop.setNome(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);
			prop.setValore(requestOutputFileNameHeadersPermissions);
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
	
	public static void addFileDati(List<DataElement> dati,int pageSize, ConsoleHelper consoleHelper,
			String requestOutputFileName, String requestOutputFileNamePermissions, String requestOutputFileNameHeaders, String requestOutputFileNameHeadersPermissions,
			String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean modi, boolean fruizione, boolean forceNoSec){

		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_REQUEST_OUTPUT);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setValue(requestOutputFileName);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE);
		de.setRequired(true);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setSize(pageSize);
		DataElementInfo dInfoPatternFileName = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		dInfoPatternFileName.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
		dInfoPatternFileName.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(modi, fruizione, forceNoSec));
		de.setInfo(dInfoPatternFileName);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
		de.setValue(requestOutputFileNamePermissions);
		if(!consoleHelper.isModalitaStandard() || StringUtils.isNotEmpty(requestOutputFileNamePermissions)) {
			de.setType(DataElementType.TEXT_AREA);
			int rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS;
			try {
				if(StringUtils.isNotEmpty(requestOutputFileNamePermissions)) {
					rows = ConnettoreFILE.getNumPermission(requestOutputFileNamePermissions);
				}
				if(rows<=0) {
					rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS;
				}
				else if(rows>3) {
					rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS_MAX;
				}
			}catch(Exception t) {
				rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_SIZE_PERMISSIONS;
			}
			de.setRows(rows);
			de.setInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS_INFO);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setRequired(false);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setValue(requestOutputFileNameHeaders);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE);
		de.setRequired(false);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setSize(pageSize);
		DataElementInfo dInfoPatternFileNameHdr = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		dInfoPatternFileNameHdr.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
		dInfoPatternFileNameHdr.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(modi, fruizione, forceNoSec));
		de.setInfo(dInfoPatternFileNameHdr);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
		de.setValue(requestOutputFileNameHeadersPermissions);
		if(!consoleHelper.isModalitaStandard() || StringUtils.isNotEmpty(requestOutputFileNameHeadersPermissions)) {
			de.setType(DataElementType.TEXT_AREA);
			int rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS;
			try {
				if(StringUtils.isNotEmpty(requestOutputFileNameHeadersPermissions)) {
					rows = ConnettoreFILE.getNumPermission(requestOutputFileNameHeadersPermissions);
				}
				if(rows<=0) {
					rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS;
				}
				else if(rows>3) {
					rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS_MAX;
				}
			}catch(Exception t) {
				rows = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_SIZE_PERMISSIONS;
			}
			de.setRows(rows);
			de.setInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS_INFO);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setRequired(false);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setSelected(ServletUtils.isCheckBoxEnabled(requestOutputParentDirCreateIfNotExists));
		de.setValue(requestOutputParentDirCreateIfNotExists);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setSize(pageSize);
		dati.add(de);
			
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setSelected(ServletUtils.isCheckBoxEnabled(requestOutputOverwriteIfExists));
		de.setValue(requestOutputOverwriteIfExists);
		de.setType(DataElementType.CHECKBOX);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setSize(pageSize);
		dati.add(de);
		
		
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_RESPONSE_INPUT);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setType(DataElementType.SELECT);
		de.setValues(ConnettoriCostanti.TIPI_GESTIONE_RESPONSE_FILE);
		de.setValue(responseInputMode);
		de.setSelected(responseInputMode);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setPostBack(true);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setValue(responseInputFileName);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_SIZE);
			de.setRequired(true);
			DataElementInfo dInfoPatternFileNameResponse = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			dInfoPatternFileNameResponse.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			dInfoPatternFileNameResponse.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(modi, fruizione, forceNoSec));
			de.setInfo(dInfoPatternFileNameResponse);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setSize(pageSize);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setValue(responseInputFileNameHeaders);
		if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS_SIZE);
			DataElementInfo dInfoPatternFileNameResponseHdr = new DataElementInfo(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			dInfoPatternFileNameResponseHdr.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			dInfoPatternFileNameResponseHdr.setListBody(DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI(modi, fruizione, forceNoSec));
			de.setInfo(dInfoPatternFileNameResponseHdr);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setSize(pageSize);
		dati.add(de);
		
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
		dati.add(de);
		
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
		dati.add(de);
		
	}
	
	public static void addFileDatiAsHidden(List<DataElement> dati,
			String requestOutputFileName, String requestOutputFileNamePermissions, String requestOutputFileNameHeaders, String requestOutputFileNameHeadersPermissions,
			String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime){
		
		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		de.setValue(requestOutputFileName);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
		de.setValue(requestOutputFileNamePermissions);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		de.setValue(requestOutputFileNameHeaders);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
		de.setValue(requestOutputFileNameHeadersPermissions);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		de.setValue(requestOutputParentDirCreateIfNotExists);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		dati.add(de);
			
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		de.setValue(requestOutputOverwriteIfExists);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
		dati.add(de);
		
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(responseInputMode);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		de.setValue(responseInputFileName);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		de.setValue(responseInputFileNameHeaders);
		de.setType(DataElementType.HIDDEN);	
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		de.setValue(responseInputDeleteAfterRead);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		de.setValue(responseInputWaitTime);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
		dati.add(de);

	}
	
}
