package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;

public class ProtocolPropertiesFactory {

	public static BaseConsoleItem newTitleItem(String id, String label) throws ProtocolException{
		return new BaseConsoleItem(id, label, ConsoleItemType.TITLE);
	}
	public static BaseConsoleItem newNoteItem(String id, String label) throws ProtocolException{
		return new BaseConsoleItem(id, label, ConsoleItemType.NOTE);
	}
	
	public static AbstractConsoleItem<?> newConsoleItem(ConsoleItemType itemType, String id, String label) throws ProtocolException{
		return newConsoleItem(ConsoleItemValueType.STRING, itemType, id, label);
	}
	public static AbstractConsoleItem<?> newConsoleItem(ConsoleItemValueType type, ConsoleItemType itemType, String id, String label) throws ProtocolException{
		switch (type) {
			case STRING:
				return new StringConsoleItem(id, label, itemType);
			case NUMBER:
				return new NumberConsoleItem(id, label, itemType);
			case BINARY:
				return new BinaryConsoleItem(id, label, itemType);
		}
		throw new ProtocolException("Type ["+type+"] unsupported");
	}
	
	public static BinaryProperty newProperty(String id, byte[] value){
		return new BinaryProperty(id, value);
	}
	public static NumberProperty newProperty(String id, long value){
		return new NumberProperty(id, value);
	}
	public static NumberProperty newProperty(String id, int value){
		return new NumberProperty(id, (long) value);
	}
	public static StringProperty newProperty(String id, String value){
		return new StringProperty(id, value);
	}
}
