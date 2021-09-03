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
package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;

/**
 * ProtocolPropertiesFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesFactory {

	public static BaseConsoleItem newTitleItem(String id, String label) throws ProtocolException{
		return new TitleConsoleItem(id, label);
	}
	public static BaseConsoleItem newSubTitleItem(String id, String label) throws ProtocolException{
		return new SubtitleConsoleItem(id, label);
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
			case BOOLEAN:
				return new BooleanConsoleItem(id, label, itemType);
			case BINARY:
				return new BinaryConsoleItem(id, label, itemType);
		}
		throw new ProtocolException("Type ["+type+"] unsupported");
	}
	public static AbstractConsoleItem<?> newBinaryConsoleItem(ConsoleItemType itemType, String id, String label, String fileName, String fileId) throws ProtocolException{
		return new BinaryConsoleItem(id, label, itemType, fileName, fileId);
	}
	public static BinaryProperty newProperty(String id, byte[] value, String fileName, String fileId){
		return new BinaryProperty(id, value, fileName, fileId);
	}
	public static NumberProperty newProperty(String id, Long value){
		return new NumberProperty(id, value);
	}
	public static NumberProperty newProperty(String id, Integer value){
		return new NumberProperty(id, (value != null ? value.longValue() : null)); 
	}
	public static BooleanProperty newProperty(String id, Boolean value){
		return new BooleanProperty(id, value);
	}
	public static StringProperty newProperty(String id, String value){
		return new StringProperty(id, value);
	}
}
