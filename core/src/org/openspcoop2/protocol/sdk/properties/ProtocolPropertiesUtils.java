/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;

/**
 * ProtocolPropertiesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesUtils {

	public static void setDefaultValue(List<BaseConsoleItem> consoleItems, AbstractProperty<?> property) throws ProtocolException{
		if(property ==null)
			return;

		for (BaseConsoleItem consoleItem : consoleItems) {
			if(consoleItem.getId().equals(property.getId()))
				setDefaultValue(consoleItem, property);
		}
	}

	public static void setDefaultValue(List<BaseConsoleItem> consoleItems, ProtocolProperty property){
		if(property ==null)
			return;

		for (BaseConsoleItem consoleItem : consoleItems) {
			if(consoleItem.getId().equals(property.getName()) && consoleItem instanceof AbstractConsoleItem<?>){
				if(consoleItem instanceof StringConsoleItem){
					((StringConsoleItem) consoleItem).setDefaultValue(property.getValue());
				}
				else if(consoleItem instanceof NumberConsoleItem){
					Long numberValue = property.getNumberValue();
					((NumberConsoleItem) consoleItem).setDefaultValue(numberValue);
				}
				else if(consoleItem instanceof BinaryConsoleItem){
					byte[] byteFile = property.getByteFile();
					((BinaryConsoleItem) consoleItem).setFileName(property.getFile());
					((BinaryConsoleItem) consoleItem).setDefaultValue(byteFile);
				}else if(consoleItem instanceof BooleanConsoleItem){
					Boolean booleanValue = property.getBooleanValue();
					((BooleanConsoleItem) consoleItem).setDefaultValue(booleanValue);
				}

			}
		}
	}

	public static BaseConsoleItem getBaseConsoleItem(List<BaseConsoleItem> consoleItems, AbstractProperty<?> property){
		if(property ==null)
			return null;

		String propertyId = property.getId();
		return getBaseConsoleItem(consoleItems, propertyId);
	}

	public static BaseConsoleItem getBaseConsoleItem(List<BaseConsoleItem> consoleItems, String propertyId){
		if(propertyId ==null)
			return null;

		for (BaseConsoleItem consoleItem : consoleItems) {
			if(consoleItem.getId().equals(propertyId))
				return consoleItem;
		}
		return null;
	}

	public static AbstractConsoleItem<?> getAbstractConsoleItem(List<BaseConsoleItem> consoleItems, AbstractProperty<?> property){
		if(property ==null)
			return null;

		String propertyId = property.getId();
		return getAbstractConsoleItem(consoleItems, propertyId);
	}

	public static AbstractConsoleItem<?> getAbstractConsoleItem(List<BaseConsoleItem> consoleItems, ProtocolProperty property){
		if(property ==null)
			return null;

		String propertyId = property.getName();
		return getAbstractConsoleItem(consoleItems, propertyId);
	}

	public static AbstractConsoleItem<?> getAbstractConsoleItem(List<BaseConsoleItem> consoleItems, String propertyId){
		if(propertyId ==null)
			return null;

		for (BaseConsoleItem consoleItem : consoleItems) {
			if(consoleItem.getId().equals(propertyId) && consoleItem instanceof AbstractConsoleItem<?>)
				return (AbstractConsoleItem<?>) consoleItem;
		}
		return null;
	}

	public static AbstractProperty<?> getAbstractPropertyById(ProtocolProperties protocolProperties, String propertyId){
		if(propertyId ==null)
			return null;

		for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
			AbstractProperty<?> property = protocolProperties.getProperty(i);

			if(property.getId().equals(propertyId))
				return property;
		}

		return null;
	}

	public static void setDefaultValue(BaseConsoleItem item, AbstractProperty<?> property) throws ProtocolException{
		Object defaultValue = null;
		try{
			if(item instanceof AbstractConsoleItem<?> && property != null){
				defaultValue = property.getValue();
				if(item instanceof StringConsoleItem){
					StringProperty sp = (StringProperty) property;
					((StringConsoleItem) item).setDefaultValue(sp.getValue());
				}
				else if(item instanceof NumberConsoleItem){
					NumberProperty np = (NumberProperty) property;
					((NumberConsoleItem) item).setDefaultValue(np.getValue());
				}
				else if(item instanceof BinaryConsoleItem){
					BinaryProperty bp = (BinaryProperty) property;
					((BinaryConsoleItem) item).setFileId(bp.getFileId());
					((BinaryConsoleItem) item).setFileName(bp.getFileName());
					((BinaryConsoleItem) item).setDefaultValue(bp.getValue());
				}else if(item instanceof BooleanConsoleItem){
					BooleanProperty bolP = (BooleanProperty) property;
					((BooleanConsoleItem) item).setDefaultValue(bolP.getValue()); 
				}
			}
		}catch(Exception e){
			String defaultValueClassName = defaultValue != null ?  defaultValue.getClass().getName() : "Default Value Null";
			throw new ProtocolException("Impossibile assegnare un valore di tipo ["+defaultValueClassName+"] all'item ["+item.getId()+"]");
		}
	}
	public static ConsoleItemValueType getConsoleItemValueType(BaseConsoleItem item){
		if(item instanceof AbstractConsoleItem<?>){
			if(item instanceof StringConsoleItem){
				return ConsoleItemValueType.STRING;
			}
			else if(item instanceof NumberConsoleItem){
				return ConsoleItemValueType.NUMBER;
			}
			else if(item instanceof BooleanConsoleItem){ 
				return ConsoleItemValueType.BOOLEAN;
			}
			else if(item instanceof BinaryConsoleItem){
				return ConsoleItemValueType.BINARY;
			}
		}
		return null;
	}


	public static List<ProtocolProperty> toProtocolProperties (ProtocolProperties protocolProperties, ConsoleOperationType consoleOperationType, List<ProtocolProperty> oldProtocolPropertyList){
		List<ProtocolProperty> lstProtocolProperty = new ArrayList<>();


		for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
			AbstractProperty<?> property = protocolProperties.getProperty(i);

			ProtocolProperty prop = new ProtocolProperty();

			prop.setName(property.getId());
			boolean add = false;

			if(property instanceof StringProperty){
				StringProperty sp = (StringProperty) property;
				prop.setValue(sp.getValue());
				if(StringUtils.isNotEmpty(sp.getValue()))
					add = true;
			} else if(property instanceof NumberProperty){
				NumberProperty np = (NumberProperty) property;
				prop.setNumberValue(np.getValue());
				if(np.getValue() != null)
					add = true;

			} else if(property instanceof BinaryProperty){
				BinaryProperty bp = (BinaryProperty) property;
				if(consoleOperationType.equals(ConsoleOperationType.ADD)){
					prop.setByteFile(bp.getValue());
					prop.setFile(bp.getFileName());
					if(bp.getValue() != null && bp.getValue().length > 0)
						add = true;
				}else {
					// caso change non si puo' modificare un binary quindi riporto il valore che ho trovato sul db a inizio change
					for (ProtocolProperty protocolProperty : oldProtocolPropertyList) {
						if(property.getId().equals(protocolProperty.getName())){
							prop.setByteFile(protocolProperty.getByteFile());
							prop.setFile(protocolProperty.getFile());
							if(protocolProperty.getByteFile() != null && protocolProperty.getByteFile().length > 0){
								add = true;
							}
							break;
						}
					}
				}

			} else if(property instanceof BooleanProperty){
				BooleanProperty bp = (BooleanProperty) property;
				prop.setBooleanValue(bp.getValue() != null ? bp.getValue() : false);
				if(bp.getValue() != null)
					add = true;
			}   

			if(add)
				lstProtocolProperty.add(prop);
		}

		return lstProtocolProperty;
	}

	public static void mergeProtocolProperties (ProtocolProperties protocolProperties, List<ProtocolProperty> listaProtocolPropertiesDaDB, ConsoleOperationType consoleOperationType){
		for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
			AbstractProperty<?> property = protocolProperties.getProperty(i);

			for (ProtocolProperty protocolProperty : listaProtocolPropertiesDaDB) {
				if(property.getId().equals(protocolProperty.getName())){
					if(property instanceof StringProperty){
						StringProperty sp = (StringProperty) property;
						sp.setValue(protocolProperty.getValue());
					} else if(property instanceof NumberProperty){
						NumberProperty np = (NumberProperty) property;
						np.setValue(protocolProperty.getNumberValue());
					} else if(property instanceof BinaryProperty){
						BinaryProperty bp = (BinaryProperty) property;
						bp.setValue(protocolProperty.getByteFile());
						bp.setFileName(protocolProperty.getFile());
					} else if(property instanceof BooleanProperty){
						BooleanProperty bp = (BooleanProperty) property;
						bp.setValue(protocolProperty.getBooleanValue());
					}
					break;
				}
			}
		}
	}

	public static ProtocolProperty getProtocolProperty (String propertyId , List<ProtocolProperty> listaProtocolPropertiesDaDB){
		if(listaProtocolPropertiesDaDB == null || propertyId == null)
			return null;
		
		for (ProtocolProperty protocolProperty : listaProtocolPropertiesDaDB) {
			if(propertyId.equals(protocolProperty.getName())){
				return protocolProperty;
			}
		}
		
		return null;
	}

}
