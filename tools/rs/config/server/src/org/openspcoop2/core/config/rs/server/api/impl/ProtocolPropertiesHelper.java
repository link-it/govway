/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.core.config.rs.server.api.impl;

import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.StringProperty;

/**
 * ProtocolPropertiesHelper
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertiesHelper {
	
	private ProtocolPropertiesHelper() {}

	private static String getPropertyPrefix(String key) {
		return "Property "+key+" ";
	}
	
	public static Boolean getBooleanProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws CoreException {
		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof BooleanProperty) {
			return ((BooleanProperty)prop).getValue();
		} else {
			throw new CoreException(getPropertyPrefix(key)+"non è una Boolean:" + (prop!=null ? prop.getClass().getName() : "null prop" ) );
		}
	}


	public static String getStringProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws CoreException {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) return null;
		if(prop instanceof StringProperty) {
			return ((StringProperty)prop).getValue();
		} else {
			throw new CoreException(getPropertyPrefix(key)+"non è una StringProperty:" + prop.getClass().getName());
		}
	}

	public static byte[] getByteArrayProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws CoreException {

		byte[] empty = null;
		
		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) 
			return empty;
		if(prop instanceof BinaryProperty) {
			return ((BinaryProperty)prop).getValue();
		} else {
			throw new CoreException(getPropertyPrefix(key)+"non è una BinaryProperty:" + prop.getClass().getName());
		}
	}

	public static Integer getIntegerProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws CoreException {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) return null;
		if(prop instanceof NumberProperty) {
			return ((NumberProperty)prop).getValue().intValue();
		} else {
			throw new CoreException(getPropertyPrefix(key)+"non è una NumberProperty:" + prop.getClass().getName());
		}
	}

	@SuppressWarnings("rawtypes")
	public static AbstractProperty getProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws CoreException {
		if(p.containsKey(key)) {
			return p.get(key);
		} else {
			if(required) {
				throw new CoreException(getPropertyPrefix(key)+"non trovata");
			} else {
				return null;
			}
		}
	}
}
