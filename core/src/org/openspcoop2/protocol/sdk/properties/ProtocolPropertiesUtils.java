package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;

public class ProtocolPropertiesUtils {

	public static void setDefaultValue(List<BaseConsoleItem> consoleItems, AbstractProperty<?> property) throws ProtocolException{
		if(property ==null)
			return;

		for (BaseConsoleItem consoleItem : consoleItems) {
			if(consoleItem.getId().equals(property.getId()))
				setDefaultValue(consoleItem, property.getValue());
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

	public static void setDefaultValue(BaseConsoleItem item, Object defaultValue) throws ProtocolException{
		try{
			if(item instanceof AbstractConsoleItem<?> && defaultValue != null){
				if(item instanceof StringConsoleItem){
					((StringConsoleItem) item).setDefaultValue((String) defaultValue);
				}
				else if(item instanceof NumberConsoleItem){
					((NumberConsoleItem) item).setDefaultValue((Long) defaultValue);
				}
				else if(item instanceof BinaryConsoleItem){
					((BinaryConsoleItem) item).setDefaultValue((byte[]) defaultValue);
				}else if(item instanceof BooleanConsoleItem){
					((BooleanConsoleItem) item).setDefaultValue((Boolean) defaultValue);
				}
			}
		}catch(Exception e){
			throw new ProtocolException("Impossibile assegnare un valore ci tipo ["+defaultValue.getClass().getName()+"] all'item ["+item.getId()+"]");
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
							prop.setFile(bp.getFileName());
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
