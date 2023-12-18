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
package org.openspcoop2.protocol.modipa.properties;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPProvider;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;

/**
 * ModIDynamicConfigurationKeystoreUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationKeystoreUtilities {
	
	private ModIDynamicConfigurationKeystoreUtilities() {}

	static void addKeystoreConfig(ConsoleConfiguration configuration, boolean checkRidefinisci, boolean addHiddenSubjectIssuer, boolean requiredValue) throws ProtocolException {
		
		if(checkRidefinisci) {
			BaseConsoleItem subTitleItem = 
					ProtocolPropertiesFactory.newSubTitleItem(
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ID, 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_LABEL);
			subTitleItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(subTitleItem);
		}
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID, 
				ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE);
		modeItem.setReloadOnChange(true);
		configuration.addConsoleItem(modeItem);
				
		StringConsoleItem typeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID, 
				ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL);
		typeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_DEFAULT_VALUE);
		typeItem.setReloadOnChange(true);
		configuration.addConsoleItem(typeItem);
		
		AbstractConsoleItem<?> archiveItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_LABEL);
		((BinaryConsoleItem)archiveItem).setShowContent(false);
		((BinaryConsoleItem)archiveItem).setReadOnly(false);
		((BinaryConsoleItem)archiveItem).setRequired(true);
		((BinaryConsoleItem)archiveItem).setNoteUpdate(ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_NOTE_UPDATE);
		configuration.addConsoleItem(archiveItem);
		
		AbstractConsoleItem<?> pathItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL);
		configuration.addConsoleItem(pathItem);
		
		AbstractConsoleItem<?> pathPublicKeyItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_LABEL);
		configuration.addConsoleItem(pathPublicKeyItem);
		
		StringConsoleItem pathKeyAlgorithmItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_LABEL);
		pathKeyAlgorithmItem.setDefaultValue(ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_DEFAULT_VALUE);
		configuration.addConsoleItem(pathKeyAlgorithmItem);
		
		AbstractConsoleItem<?> keystorePasswordItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_LABEL);
		keystorePasswordItem.setRequired(requiredValue);
		configuration.addConsoleItem(keystorePasswordItem);
		
		AbstractConsoleItem<?> keyAliasItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID, 
						ModIConsoleCostanti.MODIPA_KEY_ALIAS_LABEL);
		keyAliasItem.setRequired(requiredValue);
		configuration.addConsoleItem(keyAliasItem);
		
		AbstractConsoleItem<?> keyPasswordItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID, 
						ModIConsoleCostanti.MODIPA_KEY_PASSWORD_LABEL);
		keyPasswordItem.setRequired(requiredValue);
		configuration.addConsoleItem(keyPasswordItem);

		AbstractConsoleItem<?> certificateItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID, 
						ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_LABEL);
		((BinaryConsoleItem)certificateItem).setShowContent(false);
		((BinaryConsoleItem)certificateItem).setReadOnly(false);
		((BinaryConsoleItem)certificateItem).setRequired(false);
		configuration.addConsoleItem(certificateItem);
		
		if(addHiddenSubjectIssuer) {
			
			AbstractConsoleItem<?> cnSubjectItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.HIDDEN,
							ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID, 
							ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
			configuration.addConsoleItem(cnSubjectItem);
			
			AbstractConsoleItem<?> cnIssuerItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.HIDDEN,
							ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID, 
							ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
			configuration.addConsoleItem(cnIssuerItem);
			
		}
	}
	
	static boolean updateKeystoreConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean checkRidefinisci, 
			boolean hideSceltaArchivioFilePath, boolean addHiddenSubjectIssuer,
			boolean requiredValue, ConfigurazioneMultitenant configurazioneMultitenant,
			boolean rest) throws ProtocolException {
		return updateKeystoreConfig(consoleConfiguration, properties, checkRidefinisci, false, 
				hideSceltaArchivioFilePath, addHiddenSubjectIssuer,
				requiredValue, configurazioneMultitenant,
				rest);
	}
	static boolean updateKeystoreConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean checkRidefinisci, boolean checkRidefinisciOauth, 
			boolean hideSceltaArchivioFilePath, boolean addHiddenSubjectIssuer,
			boolean requiredValue, ConfigurazioneMultitenant configurazioneMultitenant,
			boolean rest) throws ProtocolException {
		
		boolean ridefinisci = true;
		if(checkRidefinisci) {
			
			if(checkRidefinisciOauth) {
				
				BooleanProperty booleanItemValue = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_ID);
				if(booleanItemValue==null || booleanItemValue.getValue()==null || (!booleanItemValue.getValue())) {
					ridefinisci = false;	
				}
				else {
					ridefinisci = true;
				}
				
			}
			else {
			
				StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID);
				if(selectModeItemValue==null) {
					ridefinisci = false;	
				}
				else {
					ridefinisci = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectModeItemValue.getValue());
				}
			
				BaseConsoleItem subTitleItem = 	
						ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ID);
				if(subTitleItem!=null) {
					if(ridefinisci) {
						subTitleItem.setType(ConsoleItemType.SUBTITLE);	
					}
					else {
						subTitleItem.setType(ConsoleItemType.HIDDEN);	
					}
				}
				
			}
		}
		
		AbstractConsoleItem<?> modeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		if(modeItem!=null) {
			if(ridefinisci && !hideSceltaArchivioFilePath) {
				modeItem.setType(ConsoleItemType.SELECT);
			}
			else {
				modeItem.setType(ConsoleItemType.HIDDEN);
			}
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_ARCHIVE,
					ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_PATH,
					ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH);
			if(HSMUtils.existsTipologieKeystoreHSM(false, false)) {
				((StringConsoleItem)modeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_LABEL_HSM,
						ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM);
			}
		}
		
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		AbstractConsoleItem<?> archiveItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
		AbstractConsoleItem<?> pathItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
		AbstractConsoleItem<?> certificateItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
				
		boolean permitCertificate = false;
		boolean hsm = false;
		String modalita = ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE;
		if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
			modalita = selectModeItemValue.getValue();
		}
		if(ridefinisci) {
			
			if(selectModeItemValue!=null && 
					(selectModeItemValue.getValue()==null || "".equals(selectModeItemValue.getValue()))) {
				selectModeItemValue.setValue(modalita);
			}
			
			if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(modalita)) {
				
				permitCertificate = true;
				
				hsm = true;
				
				archiveItem.setType(ConsoleItemType.HIDDEN);
				archiveItem.setRequired(false);
				BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
				if(archiveItemValue!=null) {
					archiveItemValue.setValue(null);
					archiveItemValue.setFileName(null);
					archiveItemValue.setClearContent(true);
				}
				
				pathItem.setType(ConsoleItemType.HIDDEN);
				pathItem.setRequired(false);
				StringProperty pathItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(pathItemItemValue!=null) {
					pathItemItemValue.setValue(null);
				}
				
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					modeItem.setNote(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_NOTE_PATH);
				}
				else {
					modeItem.setNote(null);
				}
				
				StringProperty keyPasswordMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
				if(keyPasswordMODIItemValue!=null) {
					keyPasswordMODIItemValue.setValue(null);
				}
				
				StringProperty keystorePasswordMODIItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
				if(keystorePasswordMODIItemValue!=null) {
					keystorePasswordMODIItemValue.setValue(null);
				}
			}
			else if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
				
				archiveItem.setType(ConsoleItemType.FILE);
				archiveItem.setRequired(requiredValue);
				if(addHiddenSubjectIssuer) {
					((BinaryConsoleItem)archiveItem).setReadOnly(true);
				}
				
				pathItem.setType(ConsoleItemType.HIDDEN);
				pathItem.setRequired(false);
				StringProperty pathItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
				if(pathItemItemValue!=null) {
					pathItemItemValue.setValue(null);
				}
				
				modeItem.setNote(null);
			}
			else {
			
				permitCertificate = true;
				StringProperty typeItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
				if(typeItemItemValue!=null && typeItemItemValue.getValue()!=null &&
					( ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JWK.equals(typeItemItemValue.getValue()) 
							||
							ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR.equals(typeItemItemValue.getValue()))
					){
					permitCertificate = false;
				}
				
				archiveItem.setType(ConsoleItemType.HIDDEN);
				archiveItem.setRequired(false);
				BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
				if(archiveItemValue!=null) {
					archiveItemValue.setValue(null);
					archiveItemValue.setFileName(null);
					archiveItemValue.setClearContent(true);
				}
				
				pathItem.setType(ConsoleItemType.TEXT_AREA);
				((StringConsoleItem)pathItem).setRows(3);
				pathItem.setRequired(requiredValue);
				
				if(configurazioneMultitenant!=null &&
						StatoFunzionalita.ABILITATO.equals(configurazioneMultitenant.getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazioneMultitenant.getErogazioneSceltaSoggettiFruitori())) {
					modeItem.setNote(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_NOTE_PATH);
				}
				else {
					modeItem.setNote(null);
				}
				
			}
			
			if(certificateItem!=null) {
				if(addHiddenSubjectIssuer // ha senso solo per l'applicativo per ovviare al problema di riconoscimento multi-tenant
						&& permitCertificate) {
					certificateItem.setType(ConsoleItemType.FILE);
					certificateItem.setRequired(false);
					((BinaryConsoleItem)archiveItem).setReadOnly(true);
					
					BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
					if(certificateItemValue!=null && certificateItemValue.getValue()!=null && certificateItemValue.getValue().length>0) {
						modeItem.setNote(null);
					}
				}
				else {
					certificateItem.setType(ConsoleItemType.HIDDEN);
					certificateItem.setRequired(false);
					BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
					if(certificateItemValue!=null) {
						certificateItemValue.setValue(null);
						certificateItemValue.setFileName(null);
						certificateItemValue.setClearContent(true);
					}
				}
			}
			
			if(addHiddenSubjectIssuer) {
				
				try {
					CertificateInfo cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(properties, permitCertificate);
					if(cert!=null && cert.getSubject()!=null) {
						StringProperty subjectItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_SUBJECT_ID);
						subjectItemValue.setValue(cert.getSubject().toString());
						
						StringProperty issuerItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_CN_ISSUER_ID);
						if(cert.getIssuer()!=null) {
							issuerItemValue.setValue(cert.getIssuer().toString());
						}
						else {
							if(issuerItemValue!=null) {
								issuerItemValue.setValue(null);
							}
						}
					}
				}catch(Exception e) {
					// errore sollevato in validazione
				}
				
			}
		}
		else {
			if(pathItem!=null) {
				pathItem.setType(ConsoleItemType.HIDDEN);
				pathItem.setRequired(false);
			}
			
			StringProperty pathItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
			if(pathItemItemValue!=null) {
				pathItemItemValue.setValue(null);
			}
			
			if(archiveItem!=null) {
				archiveItem.setType(ConsoleItemType.HIDDEN);
				archiveItem.setRequired(false);
			}
			
			BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
			if(archiveItemValue!=null) {
				archiveItemValue.setValue(null);
				archiveItemValue.setFileName(null);
				archiveItemValue.setClearContent(true);
			}
			
			if(certificateItem!=null) {
				certificateItem.setType(ConsoleItemType.HIDDEN);
				certificateItem.setRequired(false);
			}
			
			BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
			if(certificateItemValue!=null) {
				certificateItemValue.setValue(null);
				certificateItemValue.setFileName(null);
				certificateItemValue.setClearContent(true);
			}
						
		}
		
		AbstractConsoleItem<?> typeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
		List<String> lHsmTypes = null;
		if(typeItem!=null) {
			if(hsm) {
				lHsmTypes = new ArrayList<>();
				HSMUtils.fillTipologieKeystore(false, false, lHsmTypes);
				if(lHsmTypes!=null && !lHsmTypes.isEmpty()) {
					for (String hsmType : lHsmTypes) {
						((StringConsoleItem)typeItem).addLabelValue(hsmType, hsmType);
					}
				}
			}	
			else {
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_JKS,
						ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_PKCS12,
						ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12);
				if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH.equals(modalita) && rest) {
					((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_JWK,
							ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JWK);
					((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_LABEL_KEY_PAIR,
							ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR);
				}
			}
			if(ridefinisci) {
				typeItem.setType(ConsoleItemType.SELECT);
			}
			else {
				typeItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		
		StringProperty typeItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
		String keystoreType = null;
		boolean keystoreKeyPair = false;
		boolean keystoreJWK = false;
		if(typeItemItemValue!=null && typeItemItemValue.getValue()!=null) {
			keystoreType = typeItemItemValue.getValue();
			if(hsm && (lHsmTypes==null || !lHsmTypes.contains(keystoreType))) {
				keystoreType = null;
				typeItemItemValue.setValue(null);
			}
			 
			keystoreKeyPair = ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR.equals(keystoreType);
			keystoreJWK = ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JWK.equals(keystoreType);
			
			if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita) &&
				(keystoreKeyPair || keystoreJWK) 
				){
				keystoreKeyPair = false;
				keystoreJWK = false;
				keystoreType = ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_DEFAULT_VALUE;
				typeItemItemValue.setValue(keystoreType);
			}
		}
		
		AbstractConsoleItem<?> pathPublicKeyItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID);
		boolean pathPublicKeyRestoreDefaultValue = true;
		if(pathPublicKeyItem!=null && ridefinisci &&
			ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH.equals(modalita) &&
			keystoreKeyPair) {
			pathPublicKeyItem.setType(ConsoleItemType.TEXT_AREA);
			((StringConsoleItem)pathPublicKeyItem).setRows(3);
			pathPublicKeyItem.setRequired(true);
			pathPublicKeyRestoreDefaultValue = false;
			
			// Ridefinisco anche label Path
			if(pathItem!=null) {
				pathItem.setLabel(ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PRIVATE_KEY_LABEL);
			}
		}
		if(pathPublicKeyRestoreDefaultValue) {
			if(pathPublicKeyItem!=null) {
				pathPublicKeyItem.setType(ConsoleItemType.HIDDEN);
				pathPublicKeyItem.setRequired(false);
			}
			StringProperty pathPublicKeyItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID);
			if(pathPublicKeyItemItemValue!=null) {
				pathPublicKeyItemItemValue.setValue(null);
			}
			
			// Ripristino anche label Path
			if(pathItem!=null) {
				pathItem.setLabel(ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_LABEL);
			}
		}
		
		
		AbstractConsoleItem<?> keystorePasswordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
		if(keystorePasswordItem!=null) {
			if(ridefinisci && !hsm && !keystoreKeyPair && !keystoreJWK) {
				keystorePasswordItem.setType(ConsoleItemType.TEXT_EDIT);
			}
			else {
				keystorePasswordItem.setType(ConsoleItemType.HIDDEN);
				
				StringProperty keystorePasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
				if(keystorePasswordItemValue!=null) {
					keystorePasswordItemValue.setValue(null);
				}
			}
		}
		
		AbstractConsoleItem<?> keyAliasItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
		if(keyAliasItem!=null) {
			if(ridefinisci && !keystoreKeyPair) {
				keyAliasItem.setType(ConsoleItemType.TEXT_EDIT);
			}
			else {
				keyAliasItem.setType(ConsoleItemType.HIDDEN);
				
				StringProperty keyAliasItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
				if(keyAliasItemValue!=null) {
					keyAliasItemValue.setValue(null);
				}
			}
		}
		
		AbstractConsoleItem<?> keyPasswordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
		if(keyPasswordItem!=null) {
			if(ridefinisci && (!hsm || HSMUtils.isHsmConfigurableKeyPassword()) && !keystoreJWK) {
				keyPasswordItem.setType(ConsoleItemType.TEXT_EDIT);
				
				if(keystoreKeyPair) {
					keyPasswordItem.setRequired(false);
				}
			}
			else {
				keyPasswordItem.setType(ConsoleItemType.HIDDEN);
				
				StringProperty keyPasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
				if(keyPasswordItemValue!=null) {
					keyPasswordItemValue.setValue(null);
				}
				
				keyPasswordItem.setRequired(requiredValue);
			}
		}
		
		StringProperty keyPairAlgorithmItemItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_ID);
		if(keystoreKeyPair &&
			keyPairAlgorithmItemItemValue!=null && 
			(keyPairAlgorithmItemItemValue.getValue()==null || StringUtils.isEmpty(keyPairAlgorithmItemItemValue.getValue()))) {
			keyPairAlgorithmItemItemValue.setValue(ModIConsoleCostanti.MODIPA_KEYSTORE_KEY_ALGORITHM_DEFAULT_VALUE);
		}
		
		return ridefinisci;
	}
	
	public static CertificateInfo readKeystoreConfig(ProtocolProperties properties, boolean onlyCert) throws Exception {
		
		Certificate cert = null;
			
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_ID);
		String modalita = ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_DEFAULT_VALUE;
		if(selectModeItemValue!=null && selectModeItemValue.getValue()!=null && !"".equals(selectModeItemValue.getValue())) {
			modalita = selectModeItemValue.getValue();
		}
		
		if(onlyCert) {
			
			if(!ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
				
				BinaryProperty certificateItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_CERTIFICATO_ID);
				if(certificateItemValue!=null && certificateItemValue.getValue()!=null) {
					byte [] certificate = certificateItemValue.getValue();
					cert = ArchiveLoader.load(certificate);
				}
				
			}
			else {
				// deve essere gestito invocando questo metodo con onlyCert
			}
			
		}
		else {
					
			StringProperty keystoreTypeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_ID);
			StringProperty keystorePasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PASSWORD_ID);
			StringProperty keyAliasItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_ALIAS_ID);
			StringProperty keyPasswordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEY_PASSWORD_ID);
			if(keystoreTypeItemValue!=null && keystorePasswordItemValue!=null && keyAliasItemValue!=null && keyPasswordItemValue!=null) {
				String type = keystoreTypeItemValue.getValue();
				ArchiveType archiveType = null;
				if(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS.equals(type)) {
					archiveType = ArchiveType.JKS;
				}
				else {
					archiveType = ArchiveType.PKCS12;
				}
				
				byte [] archive = null;
				if(ModIConsoleCostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(modalita)) {
					BinaryProperty archiveItemValue = (BinaryProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_ARCHIVE_ID);
					if(archiveItemValue!=null && archiveItemValue.getValue()!=null) {
						archive = archiveItemValue.getValue();
						cert = ArchiveLoader.load(archiveType, archive, keyAliasItemValue.getValue(), keystorePasswordItemValue.getValue());
					}
				}
				else {
					// Il PATH o HSM indicato non e' disponibile nella macchina dove gira la console.
	/**				StringProperty pathItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_KEYSTORE_PATH_ID);
	//				if(pathItemValue!=null && pathItemValue.getValue()!=null && !"".equals(pathItemValue.getValue())) {
	//					archive = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(pathItemValue.getValue());
	//					cert = ArchiveLoader.load(archiveType, archive, keyAliasItemValue.getValue(), keystorePasswordItemValue.getValue());
	//				}*/
				}
				
				// Verifico chiave privata
				if(archive!=null) {
					KeyStore ks = KeyStore.getInstance(archiveType.name());
					ks.load(new ByteArrayInputStream(archive), keystorePasswordItemValue.getValue().toCharArray());
					ks.getKey(keyAliasItemValue.getValue(), keyPasswordItemValue.getValue().toCharArray());
				}
			}
		}
		
		if(cert!=null) {
			return cert.getCertificate();
		}
		return null;
	}
	
	
	static void addTrustStoreSSLConfigChoice(ConsoleConfiguration configuration, boolean x5u) throws ProtocolException {
		addTrustStoreConfigChoice(configuration, true, false, 
				false, 
				false, false, false, 
				x5u);
	}
	static void addTrustStoreCertificatiConfigChoice(ConsoleConfiguration configuration, boolean x5u) throws ProtocolException {
		addTrustStoreConfigChoice(configuration, false, true, 
				false, 
				false, false, false, 
				x5u);
	}
	static void addTrustStoreKeystoreErogazioneConfigChoice(ConsoleConfiguration configuration) throws ProtocolException {
		addTrustStoreConfigChoice(configuration, false, false, 
				true, 
				false, false, false, 
				false);
	}
	static void addTrustStoreKeystoreFruizioneConfigChoice(ConsoleConfiguration configuration, boolean tokenNonLocale) throws ProtocolException {
		addTrustStoreConfigChoice(configuration, false, false, 
				false, 
				true, tokenNonLocale, false, 
				false);
	}
	static void addTrustStoreKeystoreFruizioneOAuthConfigChoice(ConsoleConfiguration configuration) throws ProtocolException {
		addTrustStoreConfigChoice(configuration, false, false, 
				false, 
				false, false, true, 
				false);
	}
	private static void addTrustStoreConfigChoice(ConsoleConfiguration configuration, boolean ssl, boolean truststore, 
			boolean keystoreErogazione, 
			boolean keystoreFruizione, boolean tokenNonLocale, boolean keystoreFruizioneOauthNoSicurezzaMessaggio, 
			boolean x5u) throws ProtocolException {
		
		if(keystoreErogazione) {
			// nop
		}
		if(keystoreFruizione) {
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_ID, 
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_APPLICATIVO,
					ModIConsoleCostanti.MODIPA_KEYSTORE_FRUIZIONE_APPLICATIVO);
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE,
					ModIConsoleCostanti.MODIPA_KEYSTORE_FRUIZIONE);
			if(tokenNonLocale) {
				modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_TOKEN_POLICY,
						ModIConsoleCostanti.MODIPA_KEYSTORE_FRUIZIONE_TOKEN_POLICY);
			}
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_DEFAULT_VALUE);
			modeItem.setReloadOnChange(true);
			configuration.addConsoleItem(modeItem);
		}
		if(keystoreFruizioneOauthNoSicurezzaMessaggio) {
			// nop
		}
		
		String id = null;
		if(ssl) {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID; 
		}
		else if(truststore) {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_ID;
		}
		else {
			id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID;
		}
		
		String label = null;
		if(ssl) {
			label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL; 
		}
		else if(truststore) {
			label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
		}
		else {
			if(keystoreFruizione) {
				label = "";
			}
			else {
				label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
			}
		}
		
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				id, 
				label);
		if(keystoreFruizioneOauthNoSicurezzaMessaggio) {
			modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_UNDEFINED,
					ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_UNDEFINED);
		}
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI,
				ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
		if(keystoreFruizioneOauthNoSicurezzaMessaggio) {
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_OAUTH_MODE_DEFAULT_VALUE);
		}
		else {
			modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_DEFAULT_VALUE);
		}
		modeItem.setReloadOnChange(true);
		if( (ssl && !x5u) || keystoreFruizione) {
			modeItem.setType(ConsoleItemType.HIDDEN);
		}
		configuration.addConsoleItem(modeItem);
		
	}
	public static void addKeStoreConfigOAuthChoice(ConsoleConfiguration configuration) throws ProtocolException {
		
		String id = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_ID;
		
		String label = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_LABEL;
		
		BooleanConsoleItem booleanConsoleItem = 
				(BooleanConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN, ConsoleItemType.CHECKBOX,
						id, label);
		booleanConsoleItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_DEFAULT_VALUE);
		booleanConsoleItem.setReloadOnChange(true);
		configuration.addConsoleItem(booleanConsoleItem);
				
	}
	static void addTrustStoreConfigSubSection(ConsoleConfiguration configuration, boolean ssl, boolean x5u) throws ProtocolException {
		
		if(x5u) {
			// nop
		}
		
		BaseConsoleItem subTitleItem = 
				ProtocolPropertiesFactory.newSubTitleItem(
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_LABEL);
		if(subTitleItem!=null) {
			subTitleItem.setType(ConsoleItemType.HIDDEN);
			configuration.addConsoleItem(subTitleItem);
		}
				
		StringConsoleItem typeItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL);
		typeItem.setDefaultValue(ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS
									:
									ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS);
		configuration.addConsoleItem(typeItem);
		
		AbstractConsoleItem<?> pathItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_LABEL);
		configuration.addConsoleItem(pathItem);
		
		StringConsoleItem passwordItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_LABEL
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_LABEL);
		configuration.addConsoleItem(passwordItem);
				
		StringConsoleItem ocspPolicyItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_ID
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_LABEL
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_LABEL		);
		ocspPolicyItem.setRequired(false);
		configuration.addConsoleItem(ocspPolicyItem);
		
		StringConsoleItem crlsItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.HIDDEN,
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID, 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_LABEL
								:
							  ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_LABEL		);
		crlsItem.setRequired(false);
		crlsItem.setRows(2);
		crlsItem.setNote(ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_NOTE
					:
					ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_NOTE);
		configuration.addConsoleItem(crlsItem);
		
	}
	
	static void updateTrustConfig(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean ssl, boolean x5u, boolean requiredValue, 
			boolean addTrustStoreTypesChiaviPubbliche, List<RemoteStoreConfig> remoteStoreConfig) throws ProtocolException {

		
		if(ssl) {
			StringConsoleItem modeItem = (StringConsoleItem) 
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID);
			if(x5u) {
				modeItem.setType(ConsoleItemType.SELECT);
				modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT,
						ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT);
				modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI,
						ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI);
			}	
			else {
				modeItem.setType(ConsoleItemType.HIDDEN);
			}
		}
		
		
		
		StringProperty selectModeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID
						: 
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_ID);
		boolean ridefinisci = selectModeItemValue!=null && ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI.equals(selectModeItemValue.getValue());
		

		BaseConsoleItem subTitleItem = 	
				ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_ID
								: 
								ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_ID);
		if(subTitleItem!=null) {
			subTitleItem.setType(ridefinisci ? ConsoleItemType.SUBTITLE : ConsoleItemType.HIDDEN);
		}
		
				
		boolean hsm = false;
		boolean remoteStore = false;
		boolean jwk = false;
		AbstractConsoleItem<?> typeItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID);
		typeItem.setType(ridefinisci ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		if(ridefinisci) {
			boolean reloadOnChange = false;
			if(ssl) {
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL_JKS,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS);
			}
			else {
				((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL_JKS,
						ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS);
				
				if(addTrustStoreTypesChiaviPubbliche) {
					((StringConsoleItem)typeItem).addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL_JWK,
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JWK);
					reloadOnChange=true;
				}
				if(remoteStoreConfig!=null && !remoteStoreConfig.isEmpty()) {
					for (RemoteStoreConfig rsc : remoteStoreConfig) {
						((StringConsoleItem)typeItem).addLabelValue(rsc.getStoreLabel(),
								rsc.getStoreName());
						reloadOnChange=true;
					}
				}
			}
			if(HSMUtils.existsTipologieKeystoreHSM(true, false)) {
				List<String> l = new ArrayList<>();
				HSMUtils.fillTipologieKeystore(true, false, l);
				if(l!=null && !l.isEmpty()) {
					typeItem.setReloadOnChange(true);
					for (String hsmType : l) {
						((StringConsoleItem)typeItem).addLabelValue(hsmType, hsmType);
					}
				}
			}
			else {
				typeItem.setReloadOnChange(reloadOnChange);
			}
			
			StringProperty typeItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
					ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID
							: 
							ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID);
			if(typeItemValue!=null && typeItemValue.getValue()!=null) {
				hsm = HSMUtils.isKeystoreHSM(typeItemValue.getValue());
				if(!hsm) {
					jwk = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JWK.equals(typeItemValue.getValue());
					if(!jwk &&
						remoteStoreConfig!=null && !remoteStoreConfig.isEmpty()) {
						for (RemoteStoreConfig rsc : remoteStoreConfig) {
							if(typeItemValue.getValue().equals(rsc.getStoreName())) {
								remoteStore = true;	
							}
						}
					}
				}
			}
		}
		
		
		String pathId = ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID
				: 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID;
		
		boolean path = ridefinisci && !hsm && !remoteStore;
		AbstractConsoleItem<?> pathItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						pathId);
		pathItem.setType(path ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);
		((StringConsoleItem)pathItem).setRows(3);
		if(path)
			pathItem.setRequired(requiredValue);
		else 
			pathItem.setRequired(false);
		
		StringProperty pathItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				pathId);
		if(pathItemValue!=null && !path) {
			pathItemValue.setValue(null);
		}
		
		
		String passwordId = ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_ID
				: 
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_ID;
		
		boolean password = ridefinisci && !hsm && !jwk && !remoteStore;
		AbstractConsoleItem<?> passwordItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						passwordId);
		passwordItem.setType(password ? ConsoleItemType.TEXT_EDIT : ConsoleItemType.HIDDEN);
		if(password)
			passwordItem.setRequired(requiredValue);
		else 
			passwordItem.setRequired(false);
		
		StringProperty passwordItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				passwordId);
		if(passwordItemValue!=null && !password) {
			passwordItemValue.setValue(null);
		}
		
		
		String ocspId = ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_ID
				:
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_ID;
		
		AbstractConsoleItem<?> ocspItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						ocspId	);
		OCSPProvider ocspProvider = new OCSPProvider();
		boolean ocsp = (ridefinisci && !jwk && !remoteStore && ocspProvider.isOcspEnabled());
		ocspItem.setType(ocsp ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		if(ocsp) {
			List<String> ocspTypes = ocspProvider.getValues();
			List<String> ocspLabels = ocspProvider.getLabels();
			for (int i = 0; i < ocspTypes.size(); i++) {
				String type = ocspTypes.get(i);
				String label = ocspLabels.get(i);
				((StringConsoleItem)ocspItem).addLabelValue(label, type);
			}
		}
		
		StringProperty ocspItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				ocspId);
		if(ocspItemValue!=null && !ocsp) {
			ocspItemValue.setValue(null);
		}
		
		
		String crlId = ssl ? ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID
				:
				ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID;
		
		AbstractConsoleItem<?> crlsItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), 
						crlId	);
		boolean crl = ridefinisci && !jwk && !remoteStore;
		crlsItem.setType(crl ? ConsoleItemType.TEXT_AREA : ConsoleItemType.HIDDEN);

		StringProperty crlsItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, 
				crlId);
		if(crlsItemValue!=null && !crl) {
			crlsItemValue.setValue(null);
		}
	}
}
