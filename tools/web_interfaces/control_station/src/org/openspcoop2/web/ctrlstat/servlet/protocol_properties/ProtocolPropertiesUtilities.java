package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.ProprietariProtocolProperty;
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
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.InterfaceType;


/***
 * 
 * ConsoleConfigurationUtils Classe Utilities per la gestione delle configurazione custom
 * 
 * @author pintori
 *
 */
public class ProtocolPropertiesUtilities {

	//	public static ProtocolProperties estraiPropertiesDaRequest(HttpServletRequest request, ConsoleConfiguration consoleConfiguration) throws Exception {
	//		ProtocolProperties properties = new ProtocolProperties();
	//
	//		List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();
	//
	//		for (BaseConsoleItem item : consoleItems) {
	//			// per ora prelevo solo i parametri che possono avere un valore non considero titoli e note
	//			if(item instanceof AbstractConsoleItem<?>){
	//				String parameterValue = request.getParameter(item.getId());
	//				if(parameterValue != null){
	//					ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);
	//					if(consoleItemValueType != null){
	//						switch (consoleItemValueType) {
	//						case BINARY:
	//							// [TODO] caso multipart
	//							break;
	//						case NUMBER:
	//							NumberProperty numberProperty = ProtocolPropertiesFactory.newProperty(item.getId(), Long.parseLong(parameterValue)); 
	//							properties.addProperty(numberProperty); 
	//							break;
	//						case BOOLEAN:
	//							BooleanProperty booleanProperty = ProtocolPropertiesFactory.newProperty(item.getId(), Boolean.parseBoolean(parameterValue)); 
	//							properties.addProperty(booleanProperty); 
	//							break;
	//						case STRING:
	//						default:
	//							StringProperty stringProperty = ProtocolPropertiesFactory.newProperty(item.getId(), parameterValue);
	//							properties.addProperty(stringProperty);
	//							break;
	//						}
	//					}
	//				}
	//			}
	//		}
	//
	//		return properties;
	//	}

	//	public static void estriPropertyMultipart(BufferedReader dis, String line, ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws Exception{
	//		List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();
	//
	//		for (BaseConsoleItem item : consoleItems) {
	//			// per ora prelevo solo i parametri che possono avere un valore non considero titoli e note
	//			if(item instanceof AbstractConsoleItem<?>){
	//				String parameterValue = null;
	//				if(line.indexOf("\""+item.getId()+"\"") != -1){
	//					ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);
	//					if(consoleItemValueType != null){
	//						switch (consoleItemValueType) {
	//						case BINARY:
	//							int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
	//							startId = startId + 10;
	//							line = dis.readLine();
	//							line = dis.readLine();
	//							parameterValue = "";
	//							while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
	//								if("".equals(parameterValue))
	//									parameterValue = line;
	//								else
	//									parameterValue = parameterValue + "\n" + line;
	//								line = dis.readLine();
	//							}
	//
	//							break;
	//						case NUMBER:
	//							line = dis.readLine();
	//							parameterValue = dis.readLine();
	//
	//							NumberProperty numberProperty = ProtocolPropertiesFactory.newProperty(item.getId(), Long.parseLong(parameterValue)); 
	//							properties.addProperty(numberProperty); 
	//							break;
	//						case STRING:
	//						default:
	//							line = dis.readLine();
	//							parameterValue = dis.readLine();
	//
	//							StringProperty stringProperty = ProtocolPropertiesFactory.newProperty(item.getId(), parameterValue);
	//							properties.addProperty(stringProperty);
	//							break;
	//						}
	//					}
	//				}
	//			}
	//		}
	//	}

	public static ConsoleInterfaceType getTipoInterfaccia(HttpSession session){
		if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(session).getInterfaceType()))
			return ConsoleInterfaceType.AVANZATA;
		else 
			return ConsoleInterfaceType.STANDARD;
	}

	public static DataElement itemToDataElement(BaseConsoleItem item, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, Properties binaryChangeProperties, ProtocolProperty protocolProperty, int size) throws Exception {
		if(item == null)
			return null;

		DataElement de = null;

		// tipi con valore
		if(item instanceof AbstractConsoleItem<?>){
			AbstractConsoleItem<?> abItem = (AbstractConsoleItem<?>) item;
			switch (abItem.getType()) {
			case CHECKBOX:
				de = getCheckbox(abItem, size);
				break;
			case CRYPT:
				de = getText(abItem, size, DataElementType.CRYPT);
				break;
			case FILE:
				de = getFile(abItem, size, consoleOperationType,consoleInterfaceType,binaryChangeProperties, protocolProperty); 
				break;
			case HIDDEN:
				de = getHidden(abItem, size);
				break;
			case SELECT:
				de = getSelect(abItem,size);
				break;
			case TEXT:
				de = getText(abItem, size, DataElementType.TEXT);
				break;
			case TEXT_AREA:
				de = getText(abItem, size, DataElementType.TEXT_AREA);
				break;
			case TEXT_AREA_NO_EDIT:
				de = getText(abItem, size, DataElementType.TEXT_AREA_NO_EDIT);
				break;
			case TEXT_EDIT:
				de = getText(abItem, size, DataElementType.TEXT_EDIT);
				break;
			default:
				throw new ProtocolException("Item con classe ["+abItem.getClass()+"] identificato come tipo AbstractConsoleItem ma con Type: ["+abItem.getType()+"] di tipo titolo o note");
			}
		} else {
			// titoli e note
			switch (item.getType()) {
			case NOTE:
				de = getTitle(item, size,DataElementType.NOTE);
				break;
			case TITLE:
				de = getTitle(item, size,DataElementType.TITLE);
				break;
			default:
				throw new ProtocolException("Item con classe ["+item.getClass()+"] non identificato come tipo AbstractConsoleItem ma con Type: ["+item.getType()+"] non di tipo titolo o note");
			}
		}

		return de;
	}

	public static DataElement getTitle(BaseConsoleItem item, int size, DataElementType type) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(type);
		de.setLabel(item.getLabel());
		de.setSize(size);
		de.setValue("");
		return de;
	}

	public static DataElement getText(AbstractConsoleItem<?> item, int size, DataElementType type) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(type);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setPostBack(item.isReloadOnChange()); 
		de.setSize(size);

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
			de.setValue(stringItem.getDefaultValue() != null ? stringItem.getDefaultValue() : "");
			break;
		case BINARY: // [TODO] da decidere 
			de.setValue("Prova Binary");
			break;
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come un "+type);
		}

		return de;
	}

	public static DataElement getFile(AbstractConsoleItem<?> item, int size, ConsoleOperationType consoleOperationType, 
			ConsoleInterfaceType consoleInterfaceType, Properties binaryChangeProperties,ProtocolProperty protocolProperty) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		if(consoleOperationType.equals(ConsoleOperationType.ADD)){
			de.setType(DataElementType.FILE);
			de.setRequired(item.isRequired());
			de.setLabel(item.getLabel());
			de.setPostBack(item.isReloadOnChange()); 
			de.setSize(size);
		} else {
			de.setType(DataElementType.LINK);
			String idItem = protocolProperty != null ? protocolProperty.getId() + "" : ""; 
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
		}

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setValue(booleanItem.getDefaultValue() + "");
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setValue(numberItem.getDefaultValue() + "");
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setValue(stringItem.getDefaultValue());
			break;
		case BINARY:  
			BinaryConsoleItem binaryItem = (BinaryConsoleItem) item;
			if(consoleOperationType.equals(ConsoleOperationType.ADD)){
				// ADD valore vuoto
				de.setValue("");	
			} else {
				// CHANGE Label del link
				de.setValue(binaryItem.getLabel());
			}
			break;
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come un file");
		}

		return de;
	}

	public static DataElement getCheckbox(AbstractConsoleItem<?> item, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.CHECKBOX);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setPostBack(item.isReloadOnChange()); 
		de.setSize(size);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setSelected(booleanItem.getDefaultValue() != null ?  booleanItem.getDefaultValue() : false);
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setSelected(numberItem.getDefaultValue() + "");
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setSelected(stringItem.getDefaultValue());
			break;
		case BINARY: // non supportato 
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una CheckBox");
		}

		return de;
	}

	public static DataElement getHidden(AbstractConsoleItem<?> item, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.HIDDEN);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setPostBack(item.isReloadOnChange()); 
		de.setSize(size);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setValue(booleanItem.getDefaultValue() + "");
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setValue(numberItem.getDefaultValue() + "");
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setValue(stringItem.getDefaultValue());
			break;
		case BINARY: // [TODO] da decidere 
			de.setValue("");
			break;
		default:
			throw new ProtocolException("Item con consoleItemType ["+consoleItemValueType+"] non puo' essere visualizzato come una Hidden");
		}

		return de;
	}

	public static DataElement getSelect(AbstractConsoleItem<?> item, int size) throws Exception{
		DataElement de = new DataElement();
		de.setName(item.getId());
		de.setType(DataElementType.SELECT);
		de.setRequired(item.isRequired());
		de.setLabel(item.getLabel());
		de.setPostBack(item.isReloadOnChange()); 
		de.setSize(size);

		ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);

		List<String> values = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		switch(consoleItemValueType){
		// [TODO] controllare casi che ci possono essere
		case BOOLEAN:
			BooleanConsoleItem booleanItem = (BooleanConsoleItem) item;
			de.setSelected(booleanItem.getDefaultValue() + "");

			Map<String, Boolean> booleanMapLabelValues = booleanItem.getMapLabelValues();
			for (String key : booleanMapLabelValues.keySet()) {
				labels.add(key);
				values.add(booleanMapLabelValues.get(key)+ "");
			}
			break;
		case NUMBER:
			NumberConsoleItem numberItem = (NumberConsoleItem) item;
			de.setSelected(numberItem.getDefaultValue() + "");

			Map<String, Long> numberMapLabelValues = numberItem.getMapLabelValues();
			for (String key : numberMapLabelValues.keySet()) {
				labels.add(key);
				values.add(numberMapLabelValues.get(key)+ "");
			}
			break;
		case STRING:
			StringConsoleItem stringItem = (StringConsoleItem) item;
			de.setSelected(stringItem.getDefaultValue());

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

		return de;
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
			case SOGGETTO:
				return ProtocolPropertiesCostanti.LABEL_SOGGETTO;
			}
		}
		return null;
	}
}
