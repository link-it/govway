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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.NumberConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.Parameter;


/**
 * ProtocolPropertiesUtilities
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesUtilities {


	public static ConsoleInterfaceType getTipoInterfaccia(ConsoleHelper consoleHelper){
		if(consoleHelper.isModalitaStandard()) {
			return ConsoleInterfaceType.STANDARD;
		}
		if(consoleHelper.isModalitaCompleta()) {
			return ConsoleInterfaceType.COMPLETA;
		}
		return ConsoleInterfaceType.AVANZATA;
	}

	public static Vector<DataElement> itemToDataElement(Vector<DataElement> dati ,
			ConsoleHelper consoleHelper,
			BaseConsoleItem item, Object defaultItemValue,
			ConsoleOperationType consoleOperationType, 
			Properties binaryChangeProperties, ProtocolProperty protocolProperty, int size) throws Exception {
		return _itemToDataElement(dati, consoleHelper,
				item, defaultItemValue, 
				consoleOperationType, 
				binaryChangeProperties, 
				protocolProperty!=null ? protocolProperty.getId() : null, 
				protocolProperty!=null ? protocolProperty.getFile() : null, 
				size,
				protocolProperty!=null ? (protocolProperty.getByteFile()!=null) : false);
	}
	public static Vector<DataElement> itemToDataElement(Vector<DataElement> dati ,
			ConsoleHelper consoleHelper,
			BaseConsoleItem item, Object defaultItemValue,
			ConsoleOperationType consoleOperationType, 
			Properties binaryChangeProperties, org.openspcoop2.core.config.ProtocolProperty protocolProperty, int size) throws Exception {
		return _itemToDataElement(dati, consoleHelper,
				item, defaultItemValue, 
				consoleOperationType, 
				binaryChangeProperties, 
				protocolProperty!=null ? protocolProperty.getId() : null, 
				protocolProperty!=null ? protocolProperty.getFile() : null, 
				size,
				protocolProperty!=null ? (protocolProperty.getByteFile()!=null) : false);
	}
	private static Vector<DataElement> _itemToDataElement(Vector<DataElement> dati ,
			ConsoleHelper consoleHelper,
			BaseConsoleItem item, Object defaultItemValue,
			ConsoleOperationType consoleOperationType, 
			Properties binaryChangeProperties, 
			Long protocolPropertyId, String protocolPropertyFile, int size,
			boolean contentSaved) throws Exception {
		if(item == null)
			return dati;

		// tipi con valore
		if(item instanceof AbstractConsoleItem<?>){
			AbstractConsoleItem<?> abItem = (AbstractConsoleItem<?>) item;
			switch (abItem.getType()) {
			case CHECKBOX:
				dati = getCheckbox(dati,abItem, defaultItemValue, size);
				break;
			case CRYPT:
				dati = getText(dati,abItem, size, DataElementType.CRYPT);
				break;
			case FILE:
				dati = getFile(dati,consoleHelper, abItem, size, consoleOperationType,binaryChangeProperties, protocolPropertyId, protocolPropertyFile, contentSaved); 
				break;
			case HIDDEN:
				dati = getHidden(dati,abItem, size);
				break;
			case SELECT:
				dati = getSelect(dati,abItem, defaultItemValue, size);
				break;
			case MULTI_SELECT:
				dati = getMultiSelect(dati,abItem, defaultItemValue, size);
				break;
			case TEXT:
				dati = getText(dati,abItem, size, DataElementType.TEXT);
				break;
			case TEXT_AREA:
				dati = getText(dati,abItem, size, DataElementType.TEXT_AREA);
				break;
			case TEXT_AREA_NO_EDIT:
				dati = getText(dati,abItem, size, DataElementType.TEXT_AREA_NO_EDIT);
				break;
			case TEXT_EDIT:
				dati = getText(dati,abItem, size, DataElementType.TEXT_EDIT);
				break;
			case TAGS:
				dati = getText(dati,abItem, size, DataElementType.TEXT_EDIT);
				dati.get(dati.size()-1).enableTags();
				break;
			case NUMBER:
				dati = getText(dati,abItem, size, DataElementType.NUMBER);
				break;
			default:
				throw new ProtocolException("Item con classe ["+abItem.getClass()+"] identificato come tipo AbstractConsoleItem ma con Type: ["+abItem.getType()+"] di tipo titolo o note");
			}
		} else {
			// titoli e note
			switch (item.getType()) {
			case NOTE:
				dati = getTitle(dati,item, size,DataElementType.NOTE);
				break;
			case SUBTITLE:
				dati = getTitle(dati,item, size,DataElementType.SUBTITLE);
				break;
			case TITLE:
				dati = getTitle(dati,item, size,DataElementType.TITLE);
				break;
			case HIDDEN:
				dati = getHidden(dati,item, size);
				break;
			default:
				throw new ProtocolException("Item con classe ["+item.getClass()+"] non identificato come tipo AbstractConsoleItem ma con Type: ["+item.getType()+"] non di tipo titolo o note");
			}
		}

		return dati;
	}
	
	public static Vector<DataElement> itemToDataElementAsHidden(Vector<DataElement> dati ,BaseConsoleItem item, Object defaultItemValue,
			ConsoleOperationType consoleOperationType, 
			Properties binaryChangeProperties, ProtocolProperty protocolProperty, int size) throws Exception {
		if(item == null)
			return dati;

		// tipi con valore
		if(item instanceof AbstractConsoleItem<?>){
			AbstractConsoleItem<?> abItem = (AbstractConsoleItem<?>) item;
			switch (abItem.getType()) {
			case CHECKBOX:
			case CRYPT:
			case FILE:
			case HIDDEN:
			case SELECT:
			case MULTI_SELECT:
			case TEXT:
			case TEXT_AREA:
			case TEXT_AREA_NO_EDIT:
			case TEXT_EDIT:
			case TAGS:
			case NUMBER:
				dati = getHidden(dati,abItem, size);
				break;
			default:
				throw new ProtocolException("Item con classe ["+abItem.getClass()+"] identificato come tipo AbstractConsoleItem ma con Type: ["+abItem.getType()+"] di tipo titolo o note");
			}
		} else {
			// titoli e note non vengono aggiunti lascio il controllo dei tipi per sicurezza
			switch (item.getType()) {
			case NOTE:
			case SUBTITLE:
			case TITLE:
			case HIDDEN:
				break;
			default:
				throw new ProtocolException("Item con classe ["+item.getClass()+"] non identificato come tipo AbstractConsoleItem ma con Type: ["+item.getType()+"] non di tipo titolo o note");
			}
		}

		return dati;
	}

	public static Vector<DataElement> getTitle(Vector<DataElement> dati ,BaseConsoleItem item, int size, DataElementType type) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(type);
		de.setLabel(item.getLabel());
		de.setSize(size);
		de.setValue("");

		dati.addElement(de);

		return dati;
	}

	public static Vector<DataElement> getText(Vector<DataElement> dati ,AbstractConsoleItem<?> item, int size, DataElementType type) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(type);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setLabelRight(item.getLabelRight());
		de.setPostBack(item.isReloadOnChange()); 
		de.setNote(item.getNote());
		de.setSize(size);
		addDataElementInfo(item, de);
		
		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setValue(booleanItem.getDefaultValue() != null ? booleanItem.getDefaultValue() + "" : "");
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setValue(numberItem.getDefaultValue() != null ? numberItem.getDefaultValue() + "" : "");
			if(DataElementType.NUMBER.equals(type)) {
				de.setMinValue((int)numberItem.getMin());
				de.setMaxValue((int)numberItem.getMax());
			}
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setValue(stringItem.getDefaultValue() != null ? stringItem.getDefaultValue() : "");
			if(stringItem.getRows()!=null) {
				de.setRows(stringItem.getRows());
			}
			break;
		case BINARY: // [TODO] da decidere 
			de.setValue("Prova Binary");
			break;
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come un "+type);
		}

		dati.addElement(de);

		return dati;
	}

	public static Vector<DataElement> getFile(Vector<DataElement> dati ,
			ConsoleHelper consoleHelper,
			AbstractConsoleItem<?> item, int size, ConsoleOperationType consoleOperationType, 
			Properties binaryChangeProperties,
			Long protocolPropertyId, String protocolPropertyFile,
			boolean contentSaved) throws Exception{
		DataElement de = new DataElement();
		DataElement de2 =null ;
		List<DataElement> df = null;
		de.setName(item.getId());

//		String nameRechange = ProtocolPropertiesCostanti.PARAMETER_FILENAME_RECHANGE_PREFIX + item.getId();
//		String nameRechangeParamTmp = consoleHelper.getParameter(nameRechange);
//		boolean isNameRechange = "true".equals(nameRechangeParamTmp);
//		
//		org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem bci = null;
//		String fileName = null;
//		if(item!=null && item instanceof org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem) {
//			bci = (org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem) item;
//			fileName = bci.getFileName();
//		}
//		if(!consoleOperationType.equals(ConsoleOperationType.ADD) && fileName==null) {
//			// aggiungo riferimento per dopo
//			DataElement deChange = new DataElement();
//			deChange.setName(nameRechange);
//			deChange.setValue("true");
//			deChange.setType(DataElementType.HIDDEN);
//			dati.addElement(deChange);
//		}
		
		boolean addDE = true;
		
		if(consoleOperationType.equals(ConsoleOperationType.ADD) || !contentSaved){
			BinaryConsoleItem binaryItem = (BinaryConsoleItem) item;

			BinaryParameter bp = new BinaryParameter();
			bp.setName(binaryItem.getId());
			bp.setFilename(binaryItem.getFileName());
			bp.setId(binaryItem.getFileId());

			de = bp.getFileDataElement(item.getLabel(), "", size);
			de.setRequired(item.isRequired());
			//			de.setPostBack(item.isReloadOnChange()); 
			df = bp.getFileNameDataElement();
			de2 = bp.getFileIdDataElement();
		} else {
			
//			if(fileName==null || isNameRechange) {
//				addDE = false;
//			}
			
			de.setType(DataElementType.LINK);
			String idItem = protocolPropertyId != null ? protocolPropertyId + "" : ""; 
			String idProprietario = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO);
			String urlChange= binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE);
			String tipoProprietario = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO);
			String nomeProprietario = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO);
			String parentProprietario = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO);
			if(parentProprietario == null)
				parentProprietario = "";
			String protocollo = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO);
			String tipoAccordo = binaryChangeProperties.getProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO);
			if(tipoAccordo == null)
				tipoAccordo = "";

			de.setUrl(ProtocolPropertiesCostanti.SERVLET_NAME_BINARY_PROPERTY_CHANGE, 
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID,idItem),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME,item.getId()),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO,idProprietario),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO,tipoProprietario),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO,nomeProprietario),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO,parentProprietario),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO,protocollo),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, tipoAccordo),
					new Parameter(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,urlChange));
			// CHANGE Label del link
			de.setValue(item.getLabel());

			if(protocolPropertyFile != null){
				de2 = new DataElement();
				de2.setName(ProtocolPropertiesCostanti.PARAMETER_FILENAME_PREFIX + item.getId());
				de2.setValue(protocolPropertyFile);
				de2.setType(DataElementType.HIDDEN);

			}
		}

		if(addDE) {
			dati.addElement(de);
		}
		
		if(df != null)
			dati.addAll(df);

		if(de2 != null)
			dati.addElement(de2);

		return dati;
	}

	private static void addDataElementInfo(AbstractConsoleItem<?> item, DataElement de) {
		if(item.getInfo()!=null) {
			DataElementInfo dInfo = new DataElementInfo(item.getInfo().getHeaderFinestraModale());
			dInfo.setHeaderBody(item.getInfo().getHeaderBody());
			if(item.getInfo().getListBody()!=null && !item.getInfo().getListBody().isEmpty()) {
				dInfo.setListBody(item.getInfo().getListBody());
			}
			de.setInfo(dInfo);
		}
	}
	
	private static Boolean getSelectedValue(BooleanConsoleItem booleanItem, Object defaultItemValue) {
		Boolean selectedBooleanValue = null;
		if(booleanItem.getDefaultValue() != null) {
			selectedBooleanValue = booleanItem.getDefaultValue();
		}
		else if(defaultItemValue!=null && defaultItemValue instanceof Boolean) {
			selectedBooleanValue = (Boolean) defaultItemValue;
		}
		else {
			selectedBooleanValue = false;
		}
		return selectedBooleanValue;
	}
	private static String getSelectedValue(NumberConsoleItem numberItem, Object defaultItemValue) {
		String selectedNumberValue = null;
		if(numberItem.getDefaultValue()!=null) {
			selectedNumberValue = numberItem.getDefaultValue() + "";
		}
		else if(defaultItemValue!=null && defaultItemValue instanceof Long) {
			selectedNumberValue = ((Long) defaultItemValue) + "";
		}
		else if(defaultItemValue!=null && defaultItemValue instanceof Integer) {
			selectedNumberValue = ((Integer) defaultItemValue) + "";
		}
		else {
			selectedNumberValue = null;
		}
		return selectedNumberValue;
	}
	private static String getSelectedValue(StringConsoleItem stringItem, Object defaultItemValue) {
		String selectedStringValue = null;
		if(stringItem.getDefaultValue()!=null) {
			selectedStringValue = stringItem.getDefaultValue();
		}
		else if(defaultItemValue!=null && defaultItemValue instanceof String) {
			selectedStringValue = ((String) defaultItemValue);
		}
		else {
			selectedStringValue = null;
		}
		return selectedStringValue;
	}
	
	public static Vector<DataElement> getCheckbox(Vector<DataElement> dati ,AbstractConsoleItem<?> item, Object defaultItemValue, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.CHECKBOX);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setLabelRight(item.getLabelRight());
		de.setPostBack(item.isReloadOnChange()); 
		de.setNote(item.getNote());
		de.setSize(size);
		addDataElementInfo(item, de);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			Boolean selectedBooleanValue = getSelectedValue(booleanItem, defaultItemValue);
			de.setSelected(selectedBooleanValue);
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			String selectedNumberValue = getSelectedValue(numberItem, defaultItemValue);
			de.setSelected(selectedNumberValue);
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			String selectedStringValue = getSelectedValue(stringItem, defaultItemValue);
			de.setSelected(selectedStringValue);
			break;
		case BINARY: // non supportato 
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una CheckBox");
		}

		dati.addElement(de);

		return dati;
	}

	public static Vector<DataElement> getHidden(Vector<DataElement> dati ,AbstractConsoleItem<?> item, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.HIDDEN);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setLabelRight(item.getLabelRight());
		de.setPostBack(item.isReloadOnChange()); 
		de.setNote(item.getNote());
		de.setSize(size);
		//addDataElementInfo(item, de);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setValue(booleanItem.getDefaultValue() != null ? booleanItem.getDefaultValue() + "" : "");
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setValue(numberItem.getDefaultValue() != null ? numberItem.getDefaultValue() + "" : "");
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setValue(stringItem.getDefaultValue() != null ? stringItem.getDefaultValue() + "" : "");
			break;
		case BINARY: // [TODO] da decidere 
			de.setValue("");
			break;
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una Hidden");
		}

		dati.addElement(de);

		return dati;
	}
	
	public static Vector<DataElement> getHidden(Vector<DataElement> dati ,BaseConsoleItem item, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.HIDDEN);
		de.setLabel(item.getLabel());
		de.setSize(size);
		dati.addElement(de);

		return dati;
	}

	public static Vector<DataElement> getSelect(Vector<DataElement> dati ,AbstractConsoleItem<?> item, Object defaultItemValue, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.SELECT);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setLabelRight(item.getLabelRight());
		de.setPostBack(item.isReloadOnChange()); 
		de.setNote(item.getNote());
		de.setSize(size);
		addDataElementInfo(item, de);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		List<String> values = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			Boolean selectedBooleanValue = getSelectedValue(booleanItem, defaultItemValue);
			de.setSelected(selectedBooleanValue);

			Map<String, Boolean> booleanMapLabelValues = booleanItem.getMapLabelValues();
			for (String key : booleanMapLabelValues.keySet()) {
				labels.add(key);
				values.add(booleanMapLabelValues.get(key)+ "");
			}
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			String selectedNumberValue = getSelectedValue(numberItem, defaultItemValue);
			de.setSelected(selectedNumberValue);

			Map<String, Long> numberMapLabelValues = numberItem.getMapLabelValues();
			for (String key : numberMapLabelValues.keySet()) {
				labels.add(key);
				values.add(numberMapLabelValues.get(key)+ "");
			}
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			String selectedStringValue = getSelectedValue(stringItem, defaultItemValue);
			de.setSelected(selectedStringValue);

			Map<String, String> stringMapLabelValues = stringItem.getMapLabelValues();
			for (String key : stringMapLabelValues.keySet()) {
				labels.add(key);
				values.add(stringMapLabelValues.get(key)+ "");
			}
			break;
		case BINARY: // non supportato 
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una Select List");
		}

		de.setValues(values);
		de.setLabels(labels); 

		dati.addElement(de);

		return dati;
	}
	
	public static Vector<DataElement> getMultiSelect(Vector<DataElement> dati ,AbstractConsoleItem<?> item, Object defaultItemValue, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.MULTI_SELECT);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setLabelRight(item.getLabelRight());
		de.setPostBack(item.isReloadOnChange());
		de.setNote(item.getNote());
		de.setSize(size);
		addDataElementInfo(item, de);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		List<String> values = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		switch(consoleItemValueType){
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			String selectedStringValue = getSelectedValue(stringItem, defaultItemValue);
			if(selectedStringValue!=null) {
				if(selectedStringValue.contains(",")) {
					de.setSelezionati(selectedStringValue.split(","));
				}
				else {
					de.setSelezionati(new String[] {selectedStringValue});
				}
			}
			if(stringItem.getRows()!=null) {
				de.setRows(stringItem.getRows());
			}
			
			Map<String, String> stringMapLabelValues = stringItem.getMapLabelValues();
			for (String key : stringMapLabelValues.keySet()) {
				labels.add(key);
				values.add(stringMapLabelValues.get(key)+ "");
			}
			break;
		
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una Multi-Select List");
		}

		de.setValues(values);
		de.setLabels(labels); 

		dati.addElement(de);

		return dati;
	}

	public static String getLabelTipoProprietario(ProprietariProtocolProperty tipoProprietario, String tipoAccordo) {
		if(tipoProprietario != null){
			switch (tipoProprietario) {
			case ACCORDO_COOPERAZIONE:
				return ProtocolPropertiesCostanti.LABEL_AC;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				return AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo);
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				return ProtocolPropertiesCostanti.LABEL_APS;
			case AZIONE_ACCORDO:
				return ProtocolPropertiesCostanti.LABEL_AZIONE_ACCORDO;
			case FRUITORE:
				return ProtocolPropertiesCostanti.LABEL_FRUITORE;
			case OPERATION:
				return ProtocolPropertiesCostanti.LABEL_OPERATION;
			case PORT_TYPE:
				return ProtocolPropertiesCostanti.LABEL_PORT_TYPE;
			case RESOURCE:
				return ProtocolPropertiesCostanti.LABEL_RESOURCE;
			case SOGGETTO:
				return ProtocolPropertiesCostanti.LABEL_SOGGETTO;
			case SERVIZIO_APPLICATIVO:
				return ProtocolPropertiesCostanti.LABEL_SERVIZIO_APPLICATIVO;
			}
		}
		return null;
	}

	public static String getValueParametroTipoProprietarioAccordoServizio(String tipo){
		if(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE.equals(tipo)){
			return ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE;
		}
		else{
			return ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_COMPOSTO;
		}
	}
}
