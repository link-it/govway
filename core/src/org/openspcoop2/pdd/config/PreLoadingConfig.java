/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.utils.CleanerOpenSPCoop2Extensions;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 *  ZipConfigurationReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14183 $, $Date: 2018-06-24 12:22:25 +0200 (Sun, 24 Jun 2018) $
 */
public class PreLoadingConfig  {

	private static final String SOGGETTO_LOCALE = "@SOGGETTO_LOCALE@";
	
	private Logger log;
	private String protocolloDefault;
	private IDSoggetto dominio;
	public PreLoadingConfig(Logger log, Logger logDriver, String protocolloDefault, IDSoggetto dominio) {
		this.log = log;
		this.protocolloDefault = protocolloDefault;
		this.dominio = dominio;
	}
	
	
	public void loadConfig(byte[] zipContent) throws Exception {
		
		List<String> configNameList = new ArrayList<>();
		List<byte[]> configList = new ArrayList<>();
		
		List<String> registryNameList = new ArrayList<>();
		List<byte[]> registryList = new ArrayList<>();
		
		File fTmp = File.createTempFile("preLoadConfiguration", ".zip");
		//System.out.println("TMP: "+fTmp.getAbsolutePath());
		try {
			FileSystemUtilities.writeFile(fTmp, zipContent);
			ZipFile zip = new ZipFile(fTmp);
			Iterator<ZipEntry> itZip = ZipUtilities.entries(zip, true);
			while (itZip.hasNext()) {
				ZipEntry zipEntry = (ZipEntry) itZip.next();
				if(zipEntry.isDirectory() == false) {
					InputStream isZip = zip.getInputStream(zipEntry);
					try {
						//System.out.println("LEGGO ["+zipEntry.getName()+"]");
						byte [] bytes = Utilities.getAsByteArray(isZip);
						String sBytes = new String(bytes);
						if(sBytes.contains(SOGGETTO_LOCALE)) {
							while(sBytes.contains(SOGGETTO_LOCALE)) {
								sBytes = sBytes.replace(SOGGETTO_LOCALE, this.dominio.getNome());
							}
							bytes = sBytes.getBytes();
						}
						try {
							Element element = XMLUtils.getInstance().newElement(bytes);
							if(org.openspcoop2.core.config.utils.XMLUtils.isConfigurazione(element)) {
								configNameList.add(zipEntry.getName());
								configList.add(bytes);
							}
							else if(org.openspcoop2.core.registry.utils.XMLUtils.isRegistroServizi(element)) {
								registryNameList.add(zipEntry.getName());
								registryList.add(bytes);
							}
							else {
								throw new Exception("unknown type");
							}
						}catch(Exception e) {
							this.log.error("PreLoading entry ["+zipEntry.getName()+"] error: "+e.getMessage(),e);
						}
					}finally {
						try {
							if(isZip!=null) {
								isZip.close();
							}
						}catch(Exception e) {}
					}
				}
			}
		}finally {
			fTmp.delete();
		}

		if(registryNameList.size()>0) {
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializerRegistry = new  org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer (); 
			java.util.Hashtable<String, IDriverRegistroServiziGet> mapRegistri = RegistroServiziReader.getDriverRegistroServizi();
			DriverRegistroServiziDB driverRegistroServizi = (DriverRegistroServiziDB) mapRegistri.values().iterator().next();
			
			for (int i = 0; i < registryNameList.size(); i++) {
				String name = registryNameList.get(i);
				byte [] content = registryList.get(i);
				
				try {
					this.log.info("PreLoading Registry ["+name+"] ...");
					@SuppressWarnings("unused")
					RegistroServizi registry = deserializerRegistry.readRegistroServizi(content); // verifico file
					
					Connection con = null;
					try {
						String superUser = null;
						con = driverRegistroServizi.getConnection("preLoading["+name+"]");
						
						org.openspcoop2.core.registry.driver.utils.XMLDataConverter xmlDataConverter = 
								new org.openspcoop2.core.registry.driver.utils.XMLDataConverter(content,driverRegistroServizi,CostantiConfigurazione.REGISTRO_DB.getValue(),
										superUser,this.protocolloDefault,
										this.log,this.log);
						boolean reset = false;
						boolean mantieniFruitoriEsistenti = true;
						boolean aggiornamentoSoggetti = false;
						boolean updateEnabled = false; // per non modificare eventuali configurazioni gia' caricate, che possono essere state modificate
						xmlDataConverter.convertXML(reset, mantieniFruitoriEsistenti,aggiornamentoSoggetti,StatiAccordo.operativo, updateEnabled);
					}finally {
						try {
							driverRegistroServizi.releaseConnection(con);
						}catch(Exception eClose) {}
					}
					
					this.log.info("PreLoading Registry ["+name+"] effettuato con successo");
	
				}catch(Exception e) {
					this.log.error("PreLoad Registry ["+name+"] failed: "+e.getMessage(),e);
				}
			}
		}
		
		if(configNameList.size()>0) {
			org.openspcoop2.core.config.utils.serializer.JaxbDeserializer deserializerConfig = new  org.openspcoop2.core.config.utils.serializer.JaxbDeserializer (); 
			DriverConfigurazioneDB driverConfigurazione = (DriverConfigurazioneDB) ConfigurazionePdDReader.getDriverConfigurazionePdD();
			
			for (int i = 0; i < configNameList.size(); i++) {
				String name = configNameList.get(i);
				byte [] content = configList.get(i);
				
				try {
					this.log.info("PreLoading Config ["+name+"] ...");
					Openspcoop2 op2 = deserializerConfig.readOpenspcoop2(content);
					
					Configurazione config = op2.getConfigurazione();
					if(config!=null) {
						if(config.getGenericPropertiesList()!=null && config.getGenericPropertiesList().size()>0) {
							for (GenericProperties genericProperties : config.getGenericPropertiesList()) {
								boolean exists = false;
								try {
									exists = driverConfigurazione.getGenericProperties(genericProperties.getTipologia(), genericProperties.getNome()) != null;
								}catch(DriverConfigurazioneNotFound notFound) {}
								if(!exists) {
									driverConfigurazione.createGenericProperties(genericProperties);
		
								}
							}
						}
					}
					
					if(op2.sizeSoggettoList()>0) {
						Connection con = null;
						try {
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
							con = driverConfigurazione.getConnection("preLoading["+name+"]");
							org.openspcoop2.core.config.driver.utils.XMLDataConverter xmlDataConverter = 
									new org.openspcoop2.core.config.driver.utils.XMLDataConverter(contentXml,driverConfigurazione,CostantiConfigurazione.CONFIGURAZIONE_DB,
											udpateConfigurazioneDisabled,tabellaSoggettiCondivisaPddRegserv,superUser,this.protocolloDefault,
											this.log,this.log);
							boolean reset = false;
							boolean aggiornamentoSoggetti = false;
							boolean createMappingErogazioneFruizione = false;
							boolean updateEnabled = false; // per non modificare eventuali configurazioni gia' caricate, che possono essere state modificate
							xmlDataConverter.convertXML(reset, aggiornamentoSoggetti, createMappingErogazioneFruizione, updateEnabled);
						}finally {
							try {
								driverConfigurazione.releaseConnection(con);
							}catch(Exception eClose) {}
						}
					}
					
					this.log.info("PreLoading Config ["+name+"] effettuato con successo");
	
				}catch(Exception e) {
					this.log.error("PreLoad Config ["+name+"] failed: "+e.getMessage(),e);
				}
			}
		}

	}
	
}

