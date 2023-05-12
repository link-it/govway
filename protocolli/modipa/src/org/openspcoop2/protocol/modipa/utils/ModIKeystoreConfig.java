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

package org.openspcoop2.protocol.modipa.utils;

import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.utils.ModIKeystoreUtils;
import org.openspcoop2.utils.UtilsException;

/**
 * ModIKeystoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIKeystoreConfig extends ModIKeystoreUtils {

	public static boolean isKeystoreDefinitoInFruizione(IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps) throws ProtocolException, UtilsException {
		boolean fruizione = true;
		List<ProtocolProperty> listProtocolProperties = ModIPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, asps);
		String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE);
		if(mode!=null && ModICostanti.MODIPA_KEYSTORE_FRUIZIONE.equals(mode)) {
			return true;
		}
		return false;
	}
	
	public ModIKeystoreConfig(ServizioApplicativo sa, String securityMessageProfile) throws ProtocolException, UtilsException {
		super(sa, securityMessageProfile);
	}
	
	public ModIKeystoreConfig(boolean fruizione, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps, String securityMessageProfile) throws ProtocolException, UtilsException {
		super(fruizione, soggettoFruitore, asps, securityMessageProfile,
				getSicurezzaMessaggioCertificatiKeyStoreTipo(),
				getSicurezzaMessaggioCertificatiKeyStorePath(),
				getSicurezzaMessaggioCertificatiKeyStorePassword(),
				getSicurezzaMessaggioCertificatiKeyAlias(),
				getSicurezzaMessaggioCertificatiKeyPassword());
	}
	
	private static ModIProperties modIProperties = null;
	private static synchronized void initModiProperties() throws ProtocolException {
		if(modIProperties==null) {
			modIProperties = ModIProperties.getInstance();
		}
	}
	private static ModIProperties getModiProperties() throws ProtocolException {
		if(modIProperties==null) {
			initModiProperties();
		}
		return modIProperties;
	}
	private static String getSicurezzaMessaggioCertificatiKeyStoreTipo() throws ProtocolException {
		try {
			return getModiProperties().getSicurezzaMessaggioCertificatiKeyStoreTipo();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static String getSicurezzaMessaggioCertificatiKeyStorePath() throws ProtocolException {
		try {
			if(getSicurezzaMessaggioCertificatiKeyStoreTipo()!=null) {
				return getModiProperties().getSicurezzaMessaggioCertificatiKeyStorePath();
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static String getSicurezzaMessaggioCertificatiKeyStorePassword() throws ProtocolException {
		try {
			if(getSicurezzaMessaggioCertificatiKeyStoreTipo()!=null) {
				return getModiProperties().getSicurezzaMessaggioCertificatiKeyStorePassword();
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static String getSicurezzaMessaggioCertificatiKeyAlias() throws ProtocolException {
		try {
			if(getSicurezzaMessaggioCertificatiKeyStoreTipo()!=null) {
				return getModiProperties().getSicurezzaMessaggioCertificatiKeyAlias();
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static String getSicurezzaMessaggioCertificatiKeyPassword() throws ProtocolException {
		try {
			if(getSicurezzaMessaggioCertificatiKeyStoreTipo()!=null) {
				return getModiProperties().getSicurezzaMessaggioCertificatiKeyPassword();
			}
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}
