package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

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


	public static List<ProtocolProperty> toProtocolProperties (ProtocolProperties protocolProperties, ConsoleOperationType consoleOperationType){
		List<ProtocolProperty> lstProtocolProperty = new ArrayList<>();

		for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
			AbstractProperty<?> property = protocolProperties.getProperty(i);

			ProtocolProperty prop = new ProtocolProperty();

			prop.setName(property.getId()); 

			if(property instanceof StringProperty){
				StringProperty sp = (StringProperty) property;
				prop.setValue(sp.getValue());
			} else if(property instanceof NumberProperty){
				NumberProperty np = (NumberProperty) property;
				prop.setNumberValue(np.getValue());
			} else if(property instanceof BinaryProperty){
				BinaryProperty bp = (BinaryProperty) property;
				prop.setByteFile(bp.getValue()); 
			} else if(property instanceof BooleanProperty){
				BooleanProperty bp = (BooleanProperty) property;
				prop.setBooleanValue(bp.getValue() != null ? bp.getValue() : false); 
			}   

			lstProtocolProperty.add(prop);
		}

		return lstProtocolProperty;
	}


}
