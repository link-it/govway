/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ModIKeystoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIKeystoreConfig {

	private String securityMessageKeystoreMode = null;
	private byte[] securityMessageKeystoreArchive = null;
	private String securityMessageKeystoreType = null;
	private boolean securityMessageKeystoreHSM = false;
	private String securityMessageKeystorePath = null;
	private String securityMessageKeystorePassword = null;
	private String securityMessageKeyAlias = null;
	private String securityMessageKeyPassword = null;
	
	public ModIKeystoreConfig(ServizioApplicativo sa, String securityMessageProfile) throws ProtocolException, UtilsException {
		
		boolean securityMessageProfileDefinedApplicativo = ProtocolPropertiesUtils.getBooleanValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, false);
		if(!securityMessageProfileDefinedApplicativo) {
			ProtocolException pe = new ProtocolException("Il profilo di sicurezza richiesto '"+securityMessageProfile+"' non è applicabile poichè l'applicativo mittente "+sa.getNome()+" ("+sa.getNomeSoggettoProprietario()+") non possiede una configurazione dei parametri di sicurezza messaggio");
			pe.setInteroperabilityError(true);
			throw pe;
		}
		
		this.securityMessageKeystoreMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_MODE);
		if(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreHSM = true;
		}
		else if(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
			this.securityMessageKeystoreArchive = ProtocolPropertiesUtils.getRequiredBinaryValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_ARCHIVE);
		}
		else {
			this.securityMessageKeystorePath = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_PATH);
			
			try {
				HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
			}catch(Exception e) {
				throw new ProtocolException("["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
			}
		}
		
		this.securityMessageKeystoreType = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_TYPE);
	
		if(this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
		}
		
		if(!this.securityMessageKeystoreHSM) {
			this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_PASSWORD);
		}
		else {
			this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
		}
		
		this.securityMessageKeyAlias = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEY_ALIAS);
		
		if(!this.securityMessageKeystoreHSM || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
			this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEY_PASSWORD);
		}
		else {
			this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
		}
		
	}
	
	public ModIKeystoreConfig(boolean fruizione, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps, String securityMessageProfile) throws ProtocolException, UtilsException {
		
		try {
			List<ProtocolProperty> listProtocolProperties = ModIPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, asps);
			
			String prefix = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
			
			String mode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE);
			boolean ridefinisci = ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode);
			
			if(ridefinisci) {
			
				this.securityMessageKeystoreMode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEYSTORE_MODE);
				if(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(this.securityMessageKeystoreMode)) {
					this.securityMessageKeystoreHSM = true;
				}
				else if(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(this.securityMessageKeystoreMode)) {
					this.securityMessageKeystoreArchive = ProtocolPropertiesUtils.getRequiredBinaryValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEYSTORE_ARCHIVE);
				}
				else {
					this.securityMessageKeystorePath = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEYSTORE_PATH);
					
					try {
						HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
					}catch(Exception e) {
						throw new ProtocolException("["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
					}
				}
				
				this.securityMessageKeystoreType = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEYSTORE_TYPE);
				
				if(this.securityMessageKeystoreHSM) {
					this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
				}
				
				if(!this.securityMessageKeystoreHSM) {
					this.securityMessageKeystorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEYSTORE_PASSWORD);
				}
				else {
					this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
				}
				
				this.securityMessageKeyAlias = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEY_ALIAS);
				
				if(!this.securityMessageKeystoreHSM || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
					this.securityMessageKeyPassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, ModICostanti.MODIPA_KEY_PASSWORD);
				}
				else {
					this.securityMessageKeyPassword = HSMUtils.KEYSTORE_HSM_PRIVATE_KEY_PASSWORD_UNDEFINED;
				}
				
			}
			else {
				
				ModIProperties modIproperties = ModIProperties.getInstance();
				this.securityMessageKeystoreType = modIproperties.getSicurezzaMessaggio_certificati_keyStore_tipo();
				if(this.securityMessageKeystoreType!=null) {
					
					this.securityMessageKeystoreHSM = HSMUtils.isKeystoreHSM(this.securityMessageKeystoreType);
					
					if(this.securityMessageKeystoreHSM) {
						this.securityMessageKeystorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageKeystoreType;
					}
					else {
					
						this.securityMessageKeystorePath = modIproperties.getSicurezzaMessaggio_certificati_keyStore_path();
						try {
							HttpUtilities.validateUri(this.securityMessageKeystorePath, true);
						}catch(Exception e) {
							throw new ProtocolException(prefix+" ["+this.securityMessageKeystorePath+"] "+e.getMessage(),e);
						}
						
					}
					
					if(!this.securityMessageKeystoreHSM) {
						this.securityMessageKeystorePassword = modIproperties.getSicurezzaMessaggio_certificati_keyStore_password();
					}
					else {
						this.securityMessageKeystorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
					}
					
					this.securityMessageKeyAlias = modIproperties.getSicurezzaMessaggio_certificati_key_alias();
					
					if(!this.securityMessageKeystoreHSM || HSMUtils.HSM_CONFIGURABLE_KEY_PASSWORD) {
						this.securityMessageKeyPassword = modIproperties.getSicurezzaMessaggio_certificati_key_password();
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
