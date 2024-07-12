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

package org.openspcoop2.protocol.utils;

import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
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
	protected String securityMessageKeystorePathPublicKey = null;
	protected String securityMessageKeystoreKeyAlgorithm = null;
	protected String securityMessageKeystorePassword = null;
	protected String securityMessageKeystoreByokPolicy = null;

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
			if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeystorePathPublicKey = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_PATH_PUBLIC_KEY);
			}
			
			if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) || 
				CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeystoreKeyAlgorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_KEY_ALGORITHM);
			}
		}
		
		if(!this.securityMessageKeystoreHSM) {
			if(!CostantiDB.KEYSTORE_TYPE_JWK.equalsIgnoreCase(this.securityMessageKeystoreType) && 
					!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
					!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_PASSWORD);
			}
			else {
				this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_PASSWORD);
			}
		}
		else {
			this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		
		if(!this.securityMessageKeystoreHSM && !CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreByokPolicy = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEYSTORE_BYOK_POLICY);
		}
		else {
			this.securityMessageKeystoreByokPolicy = BYOKProvider.BYOK_POLICY_UNDEFINED;
		}
		
		if(!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
				!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
			this.securityMessageKeyAlias = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_ALIAS);
		}
		
		if(!this.securityMessageKeystoreHSM) {
			if(!CostantiDB.KEYSTORE_TYPE_JWK.equalsIgnoreCase(this.securityMessageKeystoreType) && 
					!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
					!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_PASSWORD);
			}
			else {
				this.securityMessageKeyPassword = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_PASSWORD);
			}
		}
		else if(HSMUtils.isHsmConfigurableKeyPassword()) {
			this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), CostantiDB.MODIPA_KEY_PASSWORD);
		}
		else {
			this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
		}
		
	}
	
	public ModIKeystoreUtils(boolean fruizione, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps, String securityMessageProfile,
			String modIpropertiesSecurityMessageKeystoreType,
			String modIpropertiesSecurityMessageKeystorePath,
			String modIpropertiesSecurityMessageKeystorePassword,
			String modIpropertiesSecurityMessageKeyAlias,
			String modIpropertiesSecurityMessageKeyPassword) throws ProtocolException, UtilsException {
		
		try {
			List<ProtocolProperty> listProtocolProperties = ProtocolPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, asps);
			
			String prefix = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
			
			String mode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE);
			boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(mode);
			boolean undefined = CostantiDB.MODIPA_PROFILO_UNDEFINED.equals(mode);
			
			if(ridefinisci) {
			
				this.securityMessageKeystoreMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_MODE);
				if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(this.securityMessageKeystoreMode)) {
					this.securityMessageKeystoreHSM = true;
				}
				else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
					this.securityMessageKeystoreArchive = ProtocolPropertiesUtils.getRequiredBinaryValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
				}
				else {
					this.securityMessageKeystorePath = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_PATH);
					
					try {
						HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
					}catch(Exception e) {
						throw new ProtocolException("["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
					}
				}
				
				this.securityMessageKeystoreType = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_TYPE);
				
				if(this.securityMessageKeystoreHSM) {
					this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
				}
				
				if(!this.securityMessageKeystoreHSM) {
					if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType)) {
						this.securityMessageKeystorePathPublicKey = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_PATH_PUBLIC_KEY);
					}
					
					if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) || 
						CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
						this.securityMessageKeystoreKeyAlgorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_KEY_ALGORITHM);
					}
				}
				
				if(!this.securityMessageKeystoreHSM) {
					if(!CostantiDB.KEYSTORE_TYPE_JWK.equalsIgnoreCase(this.securityMessageKeystoreType) && 
						!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
						!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
						this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_PASSWORD);
					}
					else {
						this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_PASSWORD);
					}
				}
				else {
					this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
				}
				
				if(!this.securityMessageKeystoreHSM && !CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
					this.securityMessageKeystoreByokPolicy = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEYSTORE_BYOK_POLICY);
				}
				else {
					this.securityMessageKeystoreByokPolicy = BYOKProvider.BYOK_POLICY_UNDEFINED;
				}
				
				if(!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
						!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
					this.securityMessageKeyAlias = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEY_ALIAS);
				}
				
				if(!this.securityMessageKeystoreHSM) {
					if(!CostantiDB.KEYSTORE_TYPE_JWK.equalsIgnoreCase(this.securityMessageKeystoreType) && 
							!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
							!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
						this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEY_PASSWORD);
					}
					else {
						this.securityMessageKeyPassword = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEY_PASSWORD);
					}
				}
				else if(HSMUtils.isHsmConfigurableKeyPassword()) {
					this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, CostantiDB.MODIPA_KEY_PASSWORD);
				}
				else {
					this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
				}
				
			}
			else if(undefined) {
				throw new ProtocolException(prefix+" non definito");
			}
			else {
				
				this.securityMessageKeystoreType = modIpropertiesSecurityMessageKeystoreType; 
				if(this.securityMessageKeystoreType!=null) {
					
					this.securityMessageKeystoreHSM = HSMUtils.isKeystoreHSM(this.securityMessageKeystoreType);
					
					if(this.securityMessageKeystoreHSM) {
						this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
					}
					else {
					
						this.securityMessageKeystorePath = modIpropertiesSecurityMessageKeystorePath;
						try {
							HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
						}catch(Exception e) {
							throw new ProtocolException(prefix+" ["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
						}
						
					}
					
					if(!this.securityMessageKeystoreHSM) {
						this.securityMessageKeystorePassword = modIpropertiesSecurityMessageKeystorePassword;
					}
					else {
						this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
					}
					
					this.securityMessageKeyAlias = modIpropertiesSecurityMessageKeyAlias;
					
					if(!this.securityMessageKeystoreHSM || HSMUtils.isHsmConfigurableKeyPassword()) {
						this.securityMessageKeyPassword = modIpropertiesSecurityMessageKeyPassword;
					}
					else {
						this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
					}
				}
				
			}
			
			if(this.securityMessageKeystoreArchive==null && this.securityMessageKeystorePath==null) {
				throw new ProtocolException(prefix+" non definito");
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
		
	}
	
	public ModIKeystoreUtils(KeystoreParams kp) throws ProtocolException, UtilsException {
		
		this.securityMessageKeystoreHSM = HSMUtils.isKeystoreHSM(kp.getType());
		
		if(!this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePath = kp.getPath();
			
			try {
				HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
			}catch(Exception e) {
				throw new ProtocolException("["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
			}
		}
		
		this.securityMessageKeystoreType = kp.getType();
	
		if(this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
		}
		
		if(!this.securityMessageKeystoreHSM) {
			if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeystorePathPublicKey = kp.getKeyPairPublicKeyPath();
			}
			
			if(CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) || 
				CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
				this.securityMessageKeystoreKeyAlgorithm = kp.getKeyPairAlgorithm();
			}
		}
		
		if(!this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePassword = kp.getPassword();
		}
		else {
			this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		
		if(!this.securityMessageKeystoreHSM && !CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreByokPolicy = kp.getByokPolicy();
		}
		else {
			this.securityMessageKeystoreByokPolicy = BYOKProvider.BYOK_POLICY_UNDEFINED;
		}
		
		if(!CostantiDB.KEYSTORE_TYPE_KEY_PAIR.equalsIgnoreCase(this.securityMessageKeystoreType) && 
				!CostantiDB.KEYSTORE_TYPE_PUBLIC_KEY.equalsIgnoreCase(this.securityMessageKeystoreType)) {
			this.securityMessageKeyAlias = kp.getKeyAlias();
		}
		
		if(!this.securityMessageKeystoreHSM) {
			this.securityMessageKeyPassword = kp.getKeyPassword();
		}
		else if(HSMUtils.isHsmConfigurableKeyPassword()) {
			this.securityMessageKeyPassword = kp.getKeyPassword();
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

	public String getSecurityMessageKeystoreByokPolicy() {
		return this.securityMessageKeystoreByokPolicy;
	}
	
	public String getSecurityMessageKeyPassword() {
		return this.securityMessageKeyPassword;
	}
	
	public String getSecurityMessageKeystorePathPublicKey() {
		return this.securityMessageKeystorePathPublicKey;
	}

	public String getSecurityMessageKeystoreKeyAlgorithm() {
		return this.securityMessageKeystoreKeyAlgorithm;
	}
}
