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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ModIKeystoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIKeystoreUtils {

	protected String securityMessageKeystoreMode = null;
	protected byte[] securityMessageKeystoreArchive = null;
	protected String securityMessageKeystoreType = null;
	protected boolean securityMessageKeystoreHSM = false;
	protected String securityMessageKeystorePath = null;
	protected String securityMessageKeystorePassword = null;
	protected String securityMessageKeyAlias = null;
	protected String securityMessageKeyPassword = null;
	
	public ModIKeystoreUtils() throws ProtocolException, UtilsException {
		// nop per altri costruttori
	}
	
	public ModIKeystoreUtils(ServizioApplicativo sa, String securityMessageProfile) throws ProtocolException, UtilsException {
		
		boolean securityMessageProfileDefinedApplicativo = ProtocolPropertiesUtils.getBooleanValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_SICUREZZA_MESSAGGIO, false);
		if(!securityMessageProfileDefinedApplicativo) {
			ProtocolException pe = new ProtocolException("Il profilo di sicurezza richiesto '"+securityMessageProfile+"' non è applicabile poichè l'applicativo mittente "+sa.getNome()+" ("+sa.getNomeSoggettoProprietario()+") non possiede una configurazione dei parametri di sicurezza messaggio (Keystore)");
			pe.setInteroperabilityError(true);
			throw pe;
		}
		
		this.securityMessageKeystoreMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_MODE);
		if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreHSM = true;
		}
		else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreArchive = ProtocolPropertiesUtils.getRequiredBinaryValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		}
		else {
			this.securityMessageKeystorePath = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_PATH);
			
			try {
				HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
			}catch(Exception e) {
				throw new ProtocolException("["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
			}
		}
		
		this.securityMessageKeystoreType = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_TYPE);
	
		if(this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
		}
		
		if(!this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_PASSWORD);
		}
		else {
			this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		
		this.securityMessageKeyAlias = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_ALIAS);
		
		if(!this.securityMessageKeystoreHSM || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
			this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_PASSWORD);
		}
		else {
			this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
		}
		
	}
	
	
	public String getSecurityMessageKeyAlias() {
		return this.securityMessageKeyAlias;
	}

	public String getSecurityMessageKeystoreType() {
		return this.securityMessageKeystoreType;
	}

	public boolean isSecurityMessageKeystoreHSM() {
		return this.securityMessageKeystoreHSM;
	}
	
	public String getSecurityMessageKeystoreMode() {
		return this.securityMessageKeystoreMode;
	}

	public byte[] getSecurityMessageKeystoreArchive() {
		return this.securityMessageKeystoreArchive;
	}

	public String getSecurityMessageKeystorePath() {
		return this.securityMessageKeystorePath;
	}

	public String getSecurityMessageKeystorePassword() {
		return this.securityMessageKeystorePassword;
	}

	public String getSecurityMessageKeyPassword() {
		return this.securityMessageKeyPassword;
	}
}
