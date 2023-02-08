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
package org.openspcoop2.pdd.core.token;

import java.lang.reflect.Method;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.Loader;

/**
 * NegoziazioneTokenDynamicParametersModIUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NegoziazioneTokenDynamicParametersModIUtils {

	private static Class<?> modIPropertiesClass = null;
	private static Object modIProperties = null;
	private static synchronized void initModiProperties() throws ProtocolException {
		if(modIProperties==null) {
			try {
				Loader loader = new Loader();
				modIPropertiesClass = loader.forName("org.openspcoop2.protocol.modipa.config.ModIProperties");
				Method m = modIPropertiesClass.getMethod("getInstance");
				modIProperties = m.invoke(null);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
	}
	private static Object getModiProperties() throws ProtocolException {
		if(modIProperties==null) {
			initModiProperties();
		}
		return modIProperties;
	}
	public static String getSicurezzaMessaggio_certificati_keyStore_tipo() throws ProtocolException {
		try {
			Object o = getModiProperties();
			Method m = modIPropertiesClass.getMethod("getSicurezzaMessaggio_certificati_keyStore_tipo");
			return (String) m.invoke(o);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static String getSicurezzaMessaggio_certificati_keyStore_path() throws ProtocolException {
		try {
			if(getSicurezzaMessaggio_certificati_keyStore_tipo()!=null) {
				Object o = getModiProperties();
				Method m = modIPropertiesClass.getMethod("getSicurezzaMessaggio_certificati_keyStore_path");
				return (String) m.invoke(o);
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static String getSicurezzaMessaggio_certificati_keyStore_password() throws ProtocolException {
		try {
			if(getSicurezzaMessaggio_certificati_keyStore_tipo()!=null) {
				Object o = getModiProperties();
				Method m = modIPropertiesClass.getMethod("getSicurezzaMessaggio_certificati_keyStore_password");
				return (String) m.invoke(o);
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static String getSicurezzaMessaggio_certificati_key_alias() throws ProtocolException {
		try {
			if(getSicurezzaMessaggio_certificati_keyStore_tipo()!=null) {
				Object o = getModiProperties();
				Method m = modIPropertiesClass.getMethod("getSicurezzaMessaggio_certificati_key_alias");
				return (String) m.invoke(o);
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static String getSicurezzaMessaggio_certificati_key_password() throws ProtocolException {
		try {
			if(getSicurezzaMessaggio_certificati_keyStore_tipo()!=null) {
				Object o = getModiProperties();
				Method m = modIPropertiesClass.getMethod("getSicurezzaMessaggio_certificati_key_password");
				return (String) m.invoke(o);
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}
