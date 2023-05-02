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

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ModIKeystoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModITruststoreConfig {

	private String securityMessageTruststoreType = null;
	private boolean securityMessageTruststoreHSM = false;
	private boolean securityMessageTruststoreJWK = false;
	private boolean securityMessageTruststoreRemote = false;
	private String securityMessageTruststorePath = null;
	private String securityMessageTruststorePassword = null;
	private String securityMessageTruststoreCRLs = null;
	private String securityMessageTruststoreOCSPPolicy = null;
	
	public ModITruststoreConfig(boolean fruizione, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps, boolean ssl) throws ProtocolException {
		
		try {
			List<ProtocolProperty> listProtocolProperties = ModIPropertiesUtils.getProtocolProperties(fruizione, soggettoFruitore, asps);
			
			String prefix = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
			if(ssl) {
				prefix = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL;
			}
			
			String mode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
					ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE);
			boolean ridefinisci =  ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(mode);
			
			ModIProperties modIproperties = ModIProperties.getInstance();
			
			if(ridefinisci) {
				
				this.securityMessageTruststoreType = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
						ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE);
				this.securityMessageTruststoreHSM = HSMUtils.isKeystoreHSM(this.securityMessageTruststoreType);
				
				if(!this.securityMessageTruststoreHSM) {
					this.securityMessageTruststoreJWK = CostantiDB.KEYSTORE_TYPE_JWK.equals(this.securityMessageTruststoreType);
					if(!this.securityMessageTruststoreJWK) {
						this.securityMessageTruststoreRemote = modIproperties.isRemoteStore(this.securityMessageTruststoreType);
					}
				}
				
				if(this.securityMessageTruststoreHSM) {
					
					this.securityMessageTruststorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageTruststoreType;
					
					this.securityMessageTruststorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
							
				}
				else {
					if(!this.securityMessageTruststoreRemote) {
						this.securityMessageTruststorePath = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
								ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH);
						try {
							HttpUtilities.validateUri(this.securityMessageTruststorePath, true);
						}catch(Exception e) {
							throw new ProtocolException(prefix+" ["+this.securityMessageTruststorePath+"] "+e.getMessage(),e);
						}
					}
					
					if(!this.securityMessageTruststoreJWK && !this.securityMessageTruststoreRemote) {
						this.securityMessageTruststorePassword = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(listProtocolProperties, 
								ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD);
					}
				}

				if(!this.securityMessageTruststoreJWK && !this.securityMessageTruststoreRemote) {
					this.securityMessageTruststoreCRLs = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, 
							ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS);
					
					this.securityMessageTruststoreOCSPPolicy = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(listProtocolProperties, 
							ssl ? ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY : ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY);
				}
				
			}
			else {
				
				this.securityMessageTruststoreType = ssl ? modIproperties.getSicurezzaMessaggioSslTrustStoreTipo() : modIproperties.getSicurezzaMessaggioCertificatiTrustStoreTipo();
				if(this.securityMessageTruststoreType!=null) {
					
					this.securityMessageTruststoreHSM = HSMUtils.isKeystoreHSM(this.securityMessageTruststoreType);
					
					if(this.securityMessageTruststoreHSM) {
						
						this.securityMessageTruststorePath = HSMUtils.KEYSTORE_HSM_PREFIX+this.securityMessageTruststoreType;
						
						this.securityMessageTruststorePassword = HSMUtils.KEYSTORE_HSM_STORE_PASSWORD_UNDEFINED;
						
					}
					else {
						
						this.securityMessageTruststorePath = ssl ? modIproperties.getSicurezzaMessaggioSslTrustStorePath() : modIproperties.getSicurezzaMessaggioCertificatiTrustStorePath();
						try {
							HttpUtilities.validateUri(this.securityMessageTruststorePath, true);
						}catch(Exception e) {
							throw new ProtocolException(prefix+" ["+this.securityMessageTruststorePath+"] "+e.getMessage(),e);
						}
						
						this.securityMessageTruststorePassword = ssl ? modIproperties.getSicurezzaMessaggioSslTrustStorePassword() : modIproperties.getSicurezzaMessaggioCertificatiTrustStorePassword();
					}
				
					this.securityMessageTruststoreCRLs = ssl ? modIproperties.getSicurezzaMessaggioSslTrustStoreCrls() : modIproperties.getSicurezzaMessaggioCertificatiTrustStoreCrls();
				
					this.securityMessageTruststoreOCSPPolicy = ssl ? modIproperties.getSicurezzaMessaggioSslTrustStoreOcspPolicy() : modIproperties.getSicurezzaMessaggioCertificatiTrustStoreOcspPolicy();
				}
				
			}
			
			if(!ssl) {
				if(this.securityMessageTruststoreType==null) {
					throw new ProtocolException(prefix+" non definito");
				}
				if(this.securityMessageTruststorePath==null && !this.securityMessageTruststoreRemote) {
					throw new ProtocolException(prefix+" (path) non definito");
				}
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e);
		}
		
	}
	
	
	public String getSecurityMessageTruststorePath() {
		return this.securityMessageTruststorePath;
	}

	public String getSecurityMessageTruststoreType() {
		return this.securityMessageTruststoreType;
	}
	
	public boolean isSecurityMessageTruststoreHSM() {
		return this.securityMessageTruststoreHSM;
	}
	
	public boolean isSecurityMessageTruststoreJWK() {
		return this.securityMessageTruststoreJWK;
	}

	public boolean isSecurityMessageTruststoreRemote() {
		return this.securityMessageTruststoreRemote;
	}

	public String getSecurityMessageTruststorePassword() {
		return this.securityMessageTruststorePassword;
	}

	public String getSecurityMessageTruststoreCRLs() {
		return this.securityMessageTruststoreCRLs;
	}
	
	public String getSecurityMessageTruststoreOCSPPolicy() {
		return this.securityMessageTruststoreOCSPPolicy;
	}
	
}
