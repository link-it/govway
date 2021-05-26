package org.openspcoop2.core.config.rs.server.api.impl;

import java.util.Map;

import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.StringProperty;

public class ProtocolPropertiesHelper {

	public static Boolean getBooleanProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof BooleanProperty) {
			return ((BooleanProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una Boolean:" + prop.getClass().getName());
		}
	}


	public static String getStringProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) return null;
		if(prop instanceof StringProperty) {
			return ((StringProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una StringProperty:" + prop.getClass().getName());
		}
	}

	public static byte[] getByteArrayProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) return null;
		if(prop instanceof BinaryProperty) {
			return ((BinaryProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una BinaryProperty:" + prop.getClass().getName());
		}
	}

	public static Integer getIntegerProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop == null) return null;
		if(prop instanceof NumberProperty) {
			return ((NumberProperty)prop).getValue().intValue();
		} else {
			throw new Exception("Property "+key+" non e' una NumberProperty:" + prop.getClass().getName());
		}
	}

	public static AbstractProperty<?> getProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		if(p.containsKey(key)) {
			return p.get(key);
		} else {
			if(required) {
				throw new Exception("Property "+key+" non trovata");
			} else {
				return null;
			}
		}
	}
}
