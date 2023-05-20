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

package org.openspcoop2.pdd.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.utils.CleanerOpenSPCoop2Extensions;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegExpNotValidException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 *  ZipConfigurationReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreLoadingConfig  {

	private static final String SOGGETTO_LOCALE = "@SOGGETTO_LOCALE@";
	private static final String GOVWAY_ENTITY_NAME = "GOVWAY_DEFAULT_ENTITY_NAME";
	private static final String VARIABLE_GOVWAY_ENTITY_NAME = "${"+GOVWAY_ENTITY_NAME+"}";
	
	private Logger log;
	private Logger logDriver;
	private String protocolloDefault;
	private IDSoggetto dominio;
	public PreLoadingConfig(Logger log, Logger logDriver, String protocolloDefault, IDSoggetto dominio) {
		this.log = log;
		this.logDriver = logDriver;
		this.protocolloDefault = protocolloDefault;
		this.dominio = dominio;
	}

	public Logger getLogDriver() {
		return this.logDriver;
	}
	private void logInfo(String msg) {
		if(this.log!=null) {
			this.log.info(msg);
		}
	}
	private void logError(String msg, Exception e) {
		if(this.log!=null) {
			this.log.error(msg,e);
		}
	}
	
	public void loadConfig(List<byte[]> zipContent) throws UtilsException, IOException, DriverConfigurazioneException, ServiceException, NotImplementedException {
		if(zipContent!=null && !zipContent.isEmpty()) {
			for (byte[] bs : zipContent) {
				this.loadConfig(bs);
			}
		}
	}
	public void loadConfig(byte[] zipContent) throws UtilsException, IOException, DriverConfigurazioneException, ServiceException, NotImplementedException {
		
		List<String> configNameList = new ArrayList<>();
		List<byte[]> configList = new ArrayList<>();
		
		List<String> registryNameList = new ArrayList<>();
		List<byte[]> registryList = new ArrayList<>();
		
		List<String> controllaTrafficoConfigPolicyNameList = new ArrayList<>();
		List<byte[]> controllaTrafficoConfigPolicyList = new ArrayList<>();
		
		List<String> controllaTrafficoActivePolicyNameList = new ArrayList<>();
		List<byte[]> controllaTrafficoActivePolicyList = new ArrayList<>();
		
		HashMap<String, Document> contentList = new HashMap<>();
		
		File fTmp = FileSystemUtilities.createTempFile("preLoadConfiguration", ".zip");
		/**System.out.println("TMP: "+fTmp.getAbsolutePath());*/
		try {
			FileSystemUtilities.writeFile(fTmp, zipContent);
			ZipFile zip = new ZipFile(fTmp);
			Iterator<ZipEntry> itZip = ZipUtilities.entries(zip, true);
			while (itZip.hasNext()) {
				ZipEntry zipEntry = itZip.next();
				if(!zipEntry.isDirectory()) {
					InputStream isZip = zip.getInputStream(zipEntry);
					try {
						/**System.out.println("LEGGO ["+zipEntry.getName()+"]");*/
						byte [] bytes = Utilities.getAsByteArray(isZip);
						String sBytes = new String(bytes);
						if(sBytes.contains(SOGGETTO_LOCALE)) {
							while(sBytes.contains(SOGGETTO_LOCALE)) {
								sBytes = sBytes.replace(SOGGETTO_LOCALE, this.dominio.getNome());
							}
							bytes = sBytes.getBytes();
						}
						if(sBytes.contains(VARIABLE_GOVWAY_ENTITY_NAME)) {
							String valueSystemProperty = convertVariabile(GOVWAY_ENTITY_NAME);
							if(valueSystemProperty==null || StringUtils.isEmpty(valueSystemProperty)) {
								throw new ServiceException("Una configurazione creata con la variabile '"+VARIABLE_GOVWAY_ENTITY_NAME+"' richiede la sua definizione come variabile di sistema o java");
							}
						}
						String afterVariable = null;
						try {
							afterVariable = convertVariable(sBytes);
						}catch(Exception e) {
							throw new UtilsException(e.getMessage(),e);
						}
						if(afterVariable!=null && !afterVariable.equals(sBytes)) {
							bytes = afterVariable.getBytes();
						}
						try {
							
							String entryName = zipEntry.getName();
							int indexOfWsdl = -1;
							String name = null;
							if(entryName.endsWith(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL) ) {
								indexOfWsdl = entryName.indexOf(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
								name = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL;
							}
							else if(entryName.endsWith(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL) ) {
								indexOfWsdl = entryName.indexOf(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
								name = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL;
							}
							else if(entryName.endsWith(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL) ) {
								indexOfWsdl = entryName.indexOf(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
								name = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL;
							}
							if(indexOfWsdl>0) {
								String prefixEntryName = entryName.substring(0, indexOfWsdl-1);
								int posizione = 0; // default
								if(prefixEntryName.contains(".") && !prefixEntryName.endsWith(".")) {
									String posizioneEntry = prefixEntryName.substring(prefixEntryName.lastIndexOf(".")+1, prefixEntryName.length());
									posizione = converPosizione(posizioneEntry);
									prefixEntryName = prefixEntryName.substring(0, prefixEntryName.lastIndexOf("."));
								}
								Document d = new Document();
								d.content = bytes;
								d.index = posizione;
								d.name = name;
								contentList.put(prefixEntryName, d);
							}
							else {
							
								Element element = MessageXMLUtils.DEFAULT.newElement(bytes);
								if(org.openspcoop2.core.config.utils.XMLUtils.isConfigurazione(element)) {
									configNameList.add(entryName);
									configList.add(bytes);
								}
								else if(org.openspcoop2.core.registry.utils.XMLUtils.isRegistroServizi(element)) {
									registryNameList.add(entryName);
									registryList.add(bytes);
								}
								else if(org.openspcoop2.core.controllo_traffico.utils.XMLUtils.isConfigurazionePolicy(element)) {
									controllaTrafficoConfigPolicyNameList.add(entryName);
									controllaTrafficoConfigPolicyList.add(bytes);
								}
								else if(org.openspcoop2.core.controllo_traffico.utils.XMLUtils.isAttivazionePolicy(element)) {
									controllaTrafficoActivePolicyNameList.add(entryName);
									controllaTrafficoActivePolicyList.add(bytes);
								}
								else {
									throw new UtilsException("unknown type");
								}
								
							}
						}catch(Exception e) {
							this.logError("PreLoading entry ["+zipEntry.getName()+"] error: "+e.getMessage(),e);
						}
					}finally {
						try {
							if(isZip!=null) {
								isZip.close();
							}
						}catch(Exception e) {
							// close
						}
					}
				}
			}
		}finally {
			try {
				java.nio.file.Files.delete(fTmp.toPath());
			}catch(Exception e) {
				// ignore
			}
		}

		if(!registryNameList.isEmpty()) {
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializerRegistry = new  org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer (); 
			java.util.Map<String, IDriverRegistroServiziGet> mapRegistri = RegistroServiziReader.getDriverRegistroServizi();
			DriverRegistroServiziDB driverRegistroServizi = (DriverRegistroServiziDB) mapRegistri.values().iterator().next();
			
			for (int i = 0; i < registryNameList.size(); i++) {
				String name = registryNameList.get(i);
				byte [] content = registryList.get(i);
				
				convert(contentList, content, name, deserializerRegistry, driverRegistroServizi);
			}
		}
		
		if(!configNameList.isEmpty()) {
			org.openspcoop2.core.config.utils.serializer.JaxbDeserializer deserializerConfig = new  org.openspcoop2.core.config.utils.serializer.JaxbDeserializer (); 
			DriverConfigurazioneDB driverConfigurazione = (DriverConfigurazioneDB) ConfigurazionePdDReader.getDriverConfigurazionePdD();
			
			for (int i = 0; i < configNameList.size(); i++) {
				String name = configNameList.get(i);
				byte [] content = configList.get(i);
				
				try {
					this.logInfo(getMessageInCorso(ID_CONFIG, name));
					Openspcoop2 op2 = deserializerConfig.readOpenspcoop2(content);
					
					Configurazione config = op2.getConfigurazione();
					if(config!=null &&
						config.getGenericPropertiesList()!=null && !config.getGenericPropertiesList().isEmpty()) {
						for (GenericProperties genericProperties : config.getGenericPropertiesList()) {
							boolean exists = existsGenericProperties(driverConfigurazione, genericProperties);
							if(!exists) {
								driverConfigurazione.createGenericProperties(genericProperties);
							}
						}
					}
					
					if(op2.sizeSoggettoList()>0) {
						convert(driverConfigurazione, content, config, op2);
					}
					
					this.logInfo(getMessageEffettuataConSuccesso(ID_CONFIG, name));
	
				}catch(Exception e) {
					this.logError(getMessageError(ID_CONFIG, name, e),e);
				}
			}
		}
		
		
		
		if(!controllaTrafficoConfigPolicyNameList.isEmpty()) {
			org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer deserializerCT = new  org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer (); 
			DriverConfigurazioneDB driverConfigurazione = (DriverConfigurazioneDB) ConfigurazionePdDReader.getDriverConfigurazionePdD();
			Connection con = null;
			try {
				con = driverConfigurazione.getConnection("preloading controllaTrafficoConfigPolicies");
				ServiceManagerProperties jdbcProperties = new ServiceManagerProperties();
				jdbcProperties.setDatabaseType(driverConfigurazione.getTipoDB());
				jdbcProperties.setShowSql(true);
				org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = 
						new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, jdbcProperties, this.log);
				org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyService service = serviceManager.getConfigurazionePolicyService();
				
				org.openspcoop2.core.controllo_traffico.utils.CleanerOpenSPCoop2Extensions cleaner = new org.openspcoop2.core.controllo_traffico.utils.CleanerOpenSPCoop2Extensions();
				
				for (int i = 0; i < controllaTrafficoConfigPolicyNameList.size(); i++) {
					String name = controllaTrafficoConfigPolicyNameList.get(i);
					byte [] content = controllaTrafficoConfigPolicyList.get(i);
					
					try {
						this.logInfo(getMessageInCorso(ID_CONTROLLO_TRAFFICO_CONFIG_POLICY, name));
						org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy policy = deserializerCT.readConfigurazionePolicy(content);
						IdPolicy id = new IdPolicy();
						id.setNome(policy.getIdPolicy());
						if(!service.exists(id)) {
							cleaner.clean(policy);
							service.create(policy);
						}
						this.logInfo(getMessageEffettuataConSuccesso(ID_CONTROLLO_TRAFFICO_CONFIG_POLICY, name));
		
					}catch(Exception e) {
						this.logError(getMessageError(ID_CONTROLLO_TRAFFICO_CONFIG_POLICY, name, e),e);
					}
				}
			}finally {
				driverConfigurazione.releaseConnection(con);
			}
		}
		
		if(!controllaTrafficoActivePolicyNameList.isEmpty()) {
			org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer deserializerCT = new  org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer (); 
			DriverConfigurazioneDB driverConfigurazione = (DriverConfigurazioneDB) ConfigurazionePdDReader.getDriverConfigurazionePdD();
			Connection con = null;
			try {
				con = driverConfigurazione.getConnection("preloading controllaTrafficoActivePolicies");
				ServiceManagerProperties jdbcProperties = new ServiceManagerProperties();
				jdbcProperties.setDatabaseType(driverConfigurazione.getTipoDB());
				jdbcProperties.setShowSql(true);
				org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = 
						new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, jdbcProperties, this.log);
				org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyService service = serviceManager.getAttivazionePolicyService();
				
				org.openspcoop2.core.controllo_traffico.utils.CleanerOpenSPCoop2Extensions cleaner = new org.openspcoop2.core.controllo_traffico.utils.CleanerOpenSPCoop2Extensions();
				
				for (int i = 0; i < controllaTrafficoActivePolicyNameList.size(); i++) {
					String name = controllaTrafficoActivePolicyNameList.get(i);
					byte [] content = controllaTrafficoActivePolicyList.get(i);
					
					try {
						this.logInfo(getMessageInCorso(ID_CONTROLLO_TRAFFICO_ACTIVE_POLICY, name));
						org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy = deserializerCT.readAttivazionePolicy(content);
						IdActivePolicy id = new IdActivePolicy();
						id.setNome(policy.getIdActivePolicy());
						if(!service.exists(id)) {
							cleaner.clean(policy);
							service.create(policy);
						}
						this.logInfo(getMessageEffettuataConSuccesso(ID_CONTROLLO_TRAFFICO_ACTIVE_POLICY, name));
		
					}catch(Exception e) {
						this.logError(getMessageError(ID_CONTROLLO_TRAFFICO_ACTIVE_POLICY, name, e),e);
					}
				}
			}finally {
				driverConfigurazione.releaseConnection(con);
			}
		}

	}
	
	private int converPosizione(String posizioneEntry) throws UtilsException {
		try {
			return Integer.valueOf(posizioneEntry);
		}catch(Exception e) {
			throw new UtilsException("Nome file 'wsdl' con posizione non intera '"+posizioneEntry+"': "+e.getMessage());
		}
	}
	
	private String convertVariable(String tmp) throws RegExpException, RegExpNotValidException {
		String pattern = "\\$\\{([A-Za-z0-9_]+)\\}";
        List<String> l = null;
        try {
        	l = RegularExpressionEngine.getAllStringFindPattern(tmp, pattern);
        }catch(RegExpNotFoundException notFound) {
        	// ignore
        }
        return convertVariable(l, tmp);
	}
	private String convertVariable(List<String> l, String tmp) {
        if(l!=null && !l.isEmpty()) {
        	for (String varName : l) {
        		String valueSystemProperty = convertVariabile(varName);
    			if(valueSystemProperty!=null) {
        			String expr = "${"+varName+"}";
        			int i = 0;
        			int max = 1000;
        			while(tmp.contains(expr) && i<max) {
        				tmp = tmp.replace(expr, valueSystemProperty);
        				i++;
        			}
        		}
			}
        }
        return tmp;
	}
	private String convertVariabile(String varName) {
		String valueSystemProperty = System.getenv(varName); // sistema
		if(valueSystemProperty==null) {
			valueSystemProperty = System.getProperty(varName); // java
		}
		return valueSystemProperty;
	}
		
	private static final String PRELOADING_PREFIX = "PreLoading ";
	private String getMessageInCorso(String type, String name) {
		return PRELOADING_PREFIX+type+" ["+name+"] ...";
	}
	private String getMessageEffettuataConSuccesso(String type, String name) {
		return PRELOADING_PREFIX+type+" ["+name+"] effettuato con successo";
	}
	private String getMessageError(String type, String name, Exception e) {
		return PRELOADING_PREFIX+type+" ["+name+"] failed: "+e.getMessage();
	}
	private static final String ID_REGISTRY = "Registry";
	private static final String ID_CONFIG = "Config";
	private static final String ID_CONTROLLO_TRAFFICO_CONFIG_POLICY = "ControlloTraffico ConfigPolicy";
	private static final String ID_CONTROLLO_TRAFFICO_ACTIVE_POLICY = "ControlloTraffico ActivePolicy";
	
	private UtilsException getUtilsExceptionDocumentoNonAssociabileApi(Document d, RegistroServizi registry) {
		return new UtilsException("Documento '"+d.name+"' non associabile all'Api; la posizione indicata '"+d.index+"' è maggiore delle api disponibili '"+registry.sizeAccordoServizioParteComuneList()+"'");
	}
	
	private void convert(HashMap<String, Document> contentList, byte[] content, String name, org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializerRegistry,
			DriverRegistroServiziDB driverRegistroServizi) {
		try {
			this.logInfo(getMessageInCorso(ID_REGISTRY, name));
			RegistroServizi registry = deserializerRegistry.readRegistroServizi(content); // verifico file
			
			setContent(contentList, name, registry);
			
			convert(registry, driverRegistroServizi);
			
			this.logInfo(getMessageEffettuataConSuccesso(ID_REGISTRY, name));

		}catch(Exception e) {
			this.logError(getMessageError(ID_REGISTRY, name, e),e);
		}
	}
	private void setContent(HashMap<String, Document> contentList, String name, RegistroServizi registry) throws UtilsException {
		if(contentList!=null && contentList.containsKey(name)) {
			Document d = contentList.get(name);
			if(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL.equals(d.name)) {
				if(registry.sizeAccordoServizioParteComuneList()<=d.index) {
					throw getUtilsExceptionDocumentoNonAssociabileApi(d,  registry);
				}
				registry.getAccordoServizioParteComune(d.index).setByteWsdlConcettuale(d.content);
			}
			else if(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL.equals(d.name)) {
				if(registry.sizeAccordoServizioParteComuneList()<=d.index) {
					throw getUtilsExceptionDocumentoNonAssociabileApi(d,  registry);
				}
				registry.getAccordoServizioParteComune(d.index).setByteWsdlLogicoErogatore(d.content);
			}
			else if(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL.equals(d.name)) {
				if(registry.sizeAccordoServizioParteComuneList()<=d.index) {
					throw getUtilsExceptionDocumentoNonAssociabileApi(d,  registry);
				}
				registry.getAccordoServizioParteComune(d.index).setByteWsdlLogicoFruitore(d.content);
			}
		}
	}
	private void convert(RegistroServizi registry, DriverRegistroServiziDB driverRegistroServizi) throws DriverRegistroServiziException {
		try {
			String superUser = null;
			
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter xmlDataConverter = 
					new org.openspcoop2.core.registry.driver.utils.XMLDataConverter(registry,driverRegistroServizi,CostantiConfigurazione.REGISTRO_DB.getValue(),
							superUser,this.protocolloDefault,
							this.log,this.log);
			boolean reset = false;
			boolean mantieniFruitoriEsistenti = true;
			boolean aggiornamentoSoggetti = false;
			boolean updateEnabled = false; // per non modificare eventuali configurazioni gia' caricate, che possono essere state modificate
			xmlDataConverter.convertXML(reset, mantieniFruitoriEsistenti,aggiornamentoSoggetti,StatiAccordo.operativo, updateEnabled);
		}
		finally {
			// nop
		}
	}
	
	private boolean existsGenericProperties(DriverConfigurazioneDB driverConfigurazione, GenericProperties genericProperties) throws DriverConfigurazioneException {
		try {
			return driverConfigurazione.getGenericProperties(genericProperties.getTipologia(), genericProperties.getNome()) != null;
		}catch(DriverConfigurazioneNotFound notFound) {
			// ignore
		}
		return false;
	}
	
	private void convert(DriverConfigurazioneDB driverConfigurazione, byte[] content, Configurazione config, Openspcoop2 op2) throws UtilsException, DriverConfigurazioneException {
		byte [] contentXml = content;

		if(config==null) {
			op2.setConfigurazione(new Configurazione());
			// ne carico una dummy, tanto non la uso
			op2.getConfigurazione().setAccessoRegistro(new AccessoRegistro());
			AccessoRegistroRegistro registro = new AccessoRegistroRegistro();
			registro.setTipo(RegistroTipo.DB);
			registro.setNome("dummy");
			registro.setLocation("dummy");
			op2.getConfigurazione().getAccessoRegistro().addRegistro(registro);
			op2.getConfigurazione().setInoltroBusteNonRiscontrate(new InoltroBusteNonRiscontrate());
			op2.getConfigurazione().getInoltroBusteNonRiscontrate().setCadenza("1");
			op2.getConfigurazione().setMessaggiDiagnostici(new MessaggiDiagnostici());
			op2.getConfigurazione().getMessaggiDiagnostici().setSeverita(Severita.INFO_INTEGRATION);
			op2.getConfigurazione().getMessaggiDiagnostici().setSeveritaLog4j(Severita.INFO_INTEGRATION);
			
			CleanerOpenSPCoop2Extensions cleaner = new CleanerOpenSPCoop2Extensions();
			cleaner.clean(op2.getConfigurazione());
			for (Soggetto soggettoConfig : op2.getSoggettoList()) {
				cleaner.clean(soggettoConfig);
				
				for (PortaDelegata pdConfig : soggettoConfig.getPortaDelegataList()) {
					cleaner.clean(pdConfig);
				}
				for (PortaApplicativa paConfig : soggettoConfig.getPortaApplicativaList()) {
					cleaner.clean(paConfig);
				}
				for (ServizioApplicativo saConfig : soggettoConfig.getServizioApplicativoList()) {
					cleaner.clean(saConfig);
				}
			}
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			op2.writeTo(bout, WriteToSerializerType.XML_JAXB);
			contentXml = bout.toByteArray();
			
		}
		
		boolean udpateConfigurazioneDisabled = false;
		boolean tabellaSoggettiCondivisaPddRegserv = true;
		String superUser = null;
		org.openspcoop2.core.config.driver.utils.XMLDataConverter xmlDataConverter = 
				new org.openspcoop2.core.config.driver.utils.XMLDataConverter(contentXml,driverConfigurazione,CostantiConfigurazione.CONFIGURAZIONE_DB,
						udpateConfigurazioneDisabled,tabellaSoggettiCondivisaPddRegserv,superUser,this.protocolloDefault,
						this.log,this.log);
		boolean reset = false;
		boolean aggiornamentoSoggetti = false;
		boolean createMappingErogazioneFruizione = true; // serve per creare il mapping ad es. sulle fruizioni ed erogazioni
		boolean updateEnabled = false; // per non modificare eventuali configurazioni gia' caricate, che possono essere state modificate
		xmlDataConverter.convertXML(reset, aggiornamentoSoggetti, createMappingErogazioneFruizione, updateEnabled);
	}
}

class Document{
	
	byte[] content;
	int index;
	String name;
	
}

