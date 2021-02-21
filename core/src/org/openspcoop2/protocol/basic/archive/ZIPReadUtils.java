/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.basic.archive;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.FileDataSource;

import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.utils.ConfigurazionePdDUtils;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.protocol.abstraction.Erogazione;
import org.openspcoop2.protocol.abstraction.Fruizione;
import org.openspcoop2.protocol.abstraction.constants.CostantiAbstraction;
import org.openspcoop2.protocol.abstraction.csv.Deserializer;
import org.openspcoop2.protocol.abstraction.template.TemplateErogazione;
import org.openspcoop2.protocol.abstraction.template.TemplateFruizione;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.archive.abstraction.ErogazioneConverter;
import org.openspcoop2.protocol.basic.archive.abstraction.FruizioneConverter;
import org.openspcoop2.protocol.information_missing.Openspcoop2;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveAllarme;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveGruppo;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginArchivio;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginClasse;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveScope;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveTokenPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveUrlInvocazioneRegola;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.constants.ArchiveVersion;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.slf4j.Logger;


/**
 * Classe utilizzata per lavorare sui package
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ZIPReadUtils  {

	
	
	protected Logger log = null;
	
	protected IRegistryReader registryReader;
	protected IConfigIntegrationReader configIntegrationReader;
	
	protected org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer jaxbRegistryDeserializer = null;
	protected org.openspcoop2.core.config.utils.serializer.JaxbDeserializer jaxbConfigDeserializer = null;
	protected org.openspcoop2.core.plugins.utils.serializer.JaxbDeserializer jaxbPluginDeserializer = null;
	protected org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer jaxbControlloTrafficoDeserializer = null;
	protected org.openspcoop2.core.allarmi.utils.serializer.JaxbDeserializer jaxbAllarmeDeserializer = null;
	protected org.openspcoop2.protocol.information_missing.utils.serializer.JaxbDeserializer jaxbInformationMissingDeserializer = null;
	
	private org.openspcoop2.protocol.abstraction.utils.serializer.JaxbDeserializer jaxbAbstractionDeserializer = null;
	private ErogazioneConverter _erogazioneConverter = null;
	private FruizioneConverter _fruizioneConverter = null;
	private ErogazioneConverter getErogazioneConverter() throws ProtocolException {
		if(this._erogazioneConverter==null) {
			_initializeConverter();
		}
		return this._erogazioneConverter;
	}
	private FruizioneConverter getFruizioneConverter() throws ProtocolException {
		if(this._fruizioneConverter==null) {
			_initializeConverter();
		}
		return this._fruizioneConverter;
	}
	private synchronized void _initializeConverter() throws ProtocolException {
		if(this._erogazioneConverter==null) {
			this._erogazioneConverter = new ErogazioneConverter(this.log,this);
		}
		if(this._fruizioneConverter==null) {
			this._fruizioneConverter = new FruizioneConverter(this.log,this);
		}
	}
	
	private TemplateErogazione templateErogazione;
	private List<Erogazione> erogazioni;
	private List<byte[]> csvErogazioni;
	private byte[]csvFormatErogazione;
	private byte[]csvMappingErogazione;
	private byte[]csvTemplateErogazione;
	private synchronized void initializeErogazioni() throws ProtocolException{
		if(this.templateErogazione==null){
			this.templateErogazione = new TemplateErogazione();
			this.erogazioni = new ArrayList<Erogazione>();
			this.csvErogazioni = new ArrayList<byte[]>();
		}
	}
	
	private TemplateFruizione templateFruizione;
	private List<Fruizione> fruizioni;
	private List<byte[]> csvFruizioni;
	private byte[]csvFormatFruizione;
	private byte[]csvMappingFruizione;
	private byte[]csvTemplateFruizione;
	private synchronized void initializeFruizioni() throws ProtocolException{
		if(this.templateFruizione==null){
			this.templateFruizione = new TemplateFruizione();
			this.fruizioni = new ArrayList<Fruizione>();
			this.csvFruizioni = new ArrayList<byte[]>();
		}
	}
	
	public ZIPReadUtils(Logger log,IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader) throws ProtocolException{
		this.log = log;
		
		this.registryReader = registryReader;
		this.configIntegrationReader = configIntegrationReader;
		
		this.jaxbRegistryDeserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
		this.jaxbConfigDeserializer = new org.openspcoop2.core.config.utils.serializer.JaxbDeserializer();
		this.jaxbPluginDeserializer = new org.openspcoop2.core.plugins.utils.serializer.JaxbDeserializer();
		this.jaxbControlloTrafficoDeserializer = new org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer();
		this.jaxbAllarmeDeserializer = new org.openspcoop2.core.allarmi.utils.serializer.JaxbDeserializer();
		this.jaxbInformationMissingDeserializer = new org.openspcoop2.protocol.information_missing.utils.serializer.JaxbDeserializer();
		
		// abstract
		this.jaxbAbstractionDeserializer = new org.openspcoop2.protocol.abstraction.utils.serializer.JaxbDeserializer();
	}
	
	
	
	
	
	
	/* ----- Unmarshall  ----- */
	
	/**
	 * Ritorna la rappresentazione java di un archivio
	 * 
	 * @param zip byte[]
	 * @return Archive
	 * @throws ProtocolException
	 */
	public Archive getArchive(byte[] zip,MapPlaceholder placeholder,boolean validationDocuments) throws ProtocolException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile("openspcoop", "."+org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_EXT);
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();
			
			return getArchive(tmp,placeholder,validationDocuments);
			
		}catch(Exception e){
//			if(tmp!=null)
//				throw new ProtocolException("["+tmp.getAbsolutePath()+"] "+ e.getMessage(),e);
//			else
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
				if(tmp!=null)
					tmp.delete();
			}catch(Exception eClose){}
		}
		
	}
	
	/**
	 * Ritorna la rappresentazione java di un archivio
	 * 
	 * @param fileName File
	 * @return Archive
	 * @throws ProtocolException
	 */
	public Archive getArchive(String fileName,MapPlaceholder placeholder,boolean validationDocuments) throws ProtocolException{
		return getArchive(new File(fileName),placeholder,validationDocuments);
	}
	
	/**
	 * Ritorna la rappresentazione java di un archivio
	 * 
	 * @param zip File
	 * @return Archive
	 * @throws ProtocolException
	 */
	public Archive getArchive(File zip,MapPlaceholder placeholder,boolean validationDocuments) throws ProtocolException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			return getArchive(zipFile,placeholder,validationDocuments);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
		
	/**
	 * Ritorna la rappresentazione java di un archivio
	 * 
	 * @param m InputStream
	 * @return Archive
	 * @throws ProtocolException
	 */
	public Archive getArchive(InputStream m,MapPlaceholder placeholder,boolean validationDocuments) throws ProtocolException{
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream();
			byte[]read = new byte[1024];
			int letti = 0;
			while( (letti=m.read(read))>=0 ){
				bout.write(read, 0, letti);
			}
			bout.flush();
			bout.close();
			m.close();
			
			return getArchive(bout.toByteArray(),placeholder,validationDocuments);
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	

	/**
	 * Ritorna la rappresentazione java di un archivio
	 * 
	 * @param zip File
	 * @return Archive
	 * @throws ProtocolException
	 */
	public Archive getArchive(ZipFile zip,MapPlaceholder placeholder,boolean validationDocuments) throws ProtocolException{
		try{
			Archive archivio = new Archive();
			
			if(placeholder==null){
				placeholder = new MapPlaceholder();
			}
			
			ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione(ZIPUtils.ID_CORRELAZIONE_DEFAULT);
			idCorrelazione.setDescrizione(zip.getName()); // come descrizione viene usato il nome dell'archivio zip
			
			// ArchiveVersion
			ArchiveVersion archiveVersion = ArchiveVersion.V_UNDEFINED;
			String openspcoopVersion = null;
						
			// ExtendedInfoManager
			ExtendedInfoManager extendedInfoManager = ExtendedInfoManager.getInstance();
			Hashtable<String, PortaDelegata> mapKeyForExtendedInfo_portaDelegata = new Hashtable<String, PortaDelegata>();
			boolean existsExtendsConfigForPortaDelegata = extendedInfoManager.newInstanceExtendedInfoPortaDelegata()!=null;
			Hashtable<String, PortaApplicativa> mapKeyForExtendedInfo_portaApplicativa = new Hashtable<String, PortaApplicativa>();
			boolean existsExtendsConfigForPortaApplicativa = extendedInfoManager.newInstanceExtendedInfoPortaApplicativa()!=null;
			
			// Map per identificativi accordi
			Hashtable<String, IdentificativoAccordo> mapKeyAccordi = new Hashtable<String, IdentificativoAccordo>();
			
			// Map per identificativi documenti
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti = new Hashtable<String, IdentificativoDocumento>();
			
			// Map per identificativi protocol properties
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties = new Hashtable<String, IdentificativoProprietaProtocollo>();
			
			// Map per nomi-file e nomi servizi applicativi
			Hashtable<String, IdentificativoServizioApplicativo> mapKeyServiziApplicativi = new Hashtable<String, IdentificativoServizioApplicativo>();
			
			String rootDir = null;
			
			Iterator<ZipEntry> it = ZipUtilities.entries(zip, true);
			while (it.hasNext()) {
				ZipEntry zipEntry = (ZipEntry) it.next();
				String entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
				
				//System.out.println("FILE NAME:  "+entryName);
				//System.out.println("SIZE:  "+entry.getSize());

				// Il codice dopo fissa il problema di inserire una directory nel package.
				// Commentare la riga sotto per ripristinare il vecchio comportamento.
				if(rootDir==null){
					// Calcolo ROOT DIR
					rootDir=ZipUtilities.getRootDir(entryName);
				}
				
				if(zipEntry.isDirectory()) {
					continue; // directory
				}
				else {
					FileDataSource fds = new FileDataSource(entryName);
					String nome = fds.getName();
					String tipo = nome.substring(nome.lastIndexOf(".")+1,nome.length()); 
					tipo = tipo.toUpperCase();
					//System.out.println("VERIFICARE NAME["+nome+"] TIPO["+tipo+"]");
					
					InputStream inputStream = zip.getInputStream(zipEntry);
					byte[]content = Utilities.getAsByteArray(inputStream);
					// NOTA: non deve essere effettuato qua poiche il contenuto pu0 non essere 'xml'.
					//       vedi csv ....
					//xml = placeholder.replace(xml);
					//ByteArrayInputStream bin = new ByteArrayInputStream(xml);
					ByteArrayInputStream bin = null;
					try{
						
						// ********** archiveVersion ****************
						if(entryName.equals((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_VERSION_FILE_NAME)) ){
							archiveVersion = ArchiveVersion.toArchiveVersion(content);
							openspcoopVersion = ArchiveVersion.toProductVersion(content);
							this.log.debug("Version ["+archiveVersion+"] product["+openspcoopVersion+"]");
						}
												
						// ********** configurazione ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar)){
								this.readConfigurazioneExtended(archivio, bin, xml, entryName, validationDocuments, extendedInfoManager);
							}
							else{
								bin = new ByteArrayInputStream(xml);
								this.readConfigurazione(archivio, bin, xml, entryName, validationDocuments);
							}
						}
						
						// ********** configurazione - url di invocazione ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_URL_INVOCAZIONE_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_URL_INVOCAZIONE_REGOLE_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readUrlInvocazioneRegola(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else {
								bin = new ByteArrayInputStream(xml);
								this.readUrlInvocazione(archivio, bin, xml, entryName, validationDocuments);
							}
						}
						
						// ********** controllo traffico ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_CONFIG_POLICY_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readControlloTraffico_configurazionePolicy(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_ACTIVE_POLICY_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readControlloTraffico_attivazionePolicy(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else {
								bin = new ByteArrayInputStream(xml);
								this.readControlloTraffico_configurazione(archivio, bin, xml, entryName, validationDocuments);
							}
						}
						
						// ********** allarmi ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_ALLARMI_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readAllarme(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** token policies ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_VALIDATION_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readTokenValidationPolicy(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_RETRIEVE_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readTokenRetrievePolicy(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else {
								throw new ProtocolException("Elemento ["+entryName+"] non atteso");
							}
						}
						
						// ********** plugins ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_PLUGINS_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_PLUGINS_CLASSI_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readPluginClasse(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_PLUGINS_ARCHIVI_DIR+File.separatorChar)){
								bin = new ByteArrayInputStream(xml);
								this.readPluginArchivio(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
							}
							else {
								throw new ProtocolException("Elemento ["+entryName+"] non atteso");
							}
						}
						
						// ********** pdd ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DOMINIO_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readPortaDominio(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** gruppi ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_GRUPPI_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readGruppo(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** ruoli ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_RUOLI_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readRuolo(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** scope ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_SCOPE_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readScope(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** informationMissing ********************
						else if(entryName.equals((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_INFORMATION_MISSING)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readInformationMissing(archivio, bin, xml, entryName, validationDocuments);
						}
						
						// ********** soggetti/* ********************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+File.separatorChar)) ){
							
							byte[] xml = null;
							if(entryName.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT)) {
								xml = content; // System.out.println("NO PLACEHOLDER PER '"+entryName+"'");
							}
							else {
								xml = placeholder.replace(content);
							}
							bin = new ByteArrayInputStream(xml);
														
							String name = entryName.substring((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+File.separatorChar).length());
							
							if(name.contains((File.separatorChar+""))==false){
								
								throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
										"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto e ulteriori file e/o directory che definiscono gli elementi del soggetto");
								
							}
							else{
								
								// comprendo tipo e nome soggetto
								String tipoNomeSoggetto = name.substring(0,name.indexOf(File.separatorChar));
								if(tipoNomeSoggetto==null || "".equals(tipoNomeSoggetto) || !tipoNomeSoggetto.contains("_")){
									throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
											"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>");
								}
								tipoNomeSoggetto = tipoNomeSoggetto.trim();
								String tipoSoggetto = null;
								String nomeSoggetto = null;
								if(tipoNomeSoggetto.equals("_")){
									// caso eccezionale senza ne tipo ne nome
								}
								else if(tipoNomeSoggetto.startsWith("_")){
									// caso eccezionale con solo il nome
									nomeSoggetto = tipoNomeSoggetto.substring(1);
								}
								else if(tipoNomeSoggetto.endsWith("_")){
									// caso eccezionale con solo il tipo
									tipoSoggetto = tipoNomeSoggetto.substring(0,tipoNomeSoggetto.length()-1);
								}
								else{
									// caso normale
									tipoSoggetto = tipoNomeSoggetto.split("_")[0];
									nomeSoggetto = tipoNomeSoggetto.split("_")[1];
									if(tipoSoggetto==null || "".equals(tipoSoggetto)){
										throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
												"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: tipo non identificato");
									}
									if(nomeSoggetto==null || "".equals(nomeSoggetto)){
										throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
												"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: nome non identificato");
									}
								}
								
								// comprendo parte restante
								String nomeFile = name.substring((tipoNomeSoggetto.length()+1),name.length());
								if(nomeFile==null){
									throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
											"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto e ulteriori file e/o directory che definiscono gli elementi del soggetto: non sono stati trovati ulteriori file");
								}
								
								if(nomeFile.contains((File.separatorChar+""))==false){
									
									// ------------ soggetto --------------------
									this.readSoggetto(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
									
								}
								else{

									// ------------ protocolProperties del soggetto ------------
									if(nomeFile.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar)){

										org.openspcoop2.core.registry.Soggetto soggettoRegistroServizi = null;
										String key = ArchiveSoggetto.buildKey(tipoSoggetto, nomeSoggetto);
										if(archivio.getSoggetti().containsKey(key)){
											soggettoRegistroServizi = archivio.getSoggetti().get(key).getSoggettoRegistro();
										}
										else {
											throw new ProtocolException("Elemento ["+entryName+"] errato. Non risulta la definizione del soggetto ["+tipoSoggetto+"/"+nomeSoggetto+"]");
										}
										
										List<ProtocolProperty> listPP = new ArrayList<>();
										if(soggettoRegistroServizi.sizeProtocolPropertyList()>0) {
											for (ProtocolProperty protocolProperty : soggettoRegistroServizi.getProtocolPropertyList()) {
												if(protocolProperty.getTipoProprietario()==null) {
													protocolProperty.setTipoProprietario(ProprietariProtocolProperty.SOGGETTO.name());
												}
												listPP.add(protocolProperty);
											}
										}
										processProtocolProperty(nomeFile, archiveVersion, entryName, xml, 
												ProprietarioProprietaProtocollo.SOGGETTO, 
												tipoSoggetto, nomeSoggetto, null, null, 
												null, null, 
												mapKeyProtocolProperties, listPP, null);
										
									}
									
									// ------------ servizio applicativo --------------------
									else if(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR+File.separatorChar)) ){
										
										String nomeFileServizioApplicativo = nomeFile.substring((Costanti.OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR+File.separatorChar).length());
										//System.out.println("nomeFileServizioApplicativo: "+nomeFileServizioApplicativo);
																				
										if(nomeFileServizioApplicativo.contains((File.separatorChar+""))==false){
											ServizioApplicativo sa = this.readServizioApplicativo(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
											
											IdentificativoServizioApplicativo identificativoServizioApplicativo = new IdentificativoServizioApplicativo();
											identificativoServizioApplicativo.nome = sa.getNome();
											identificativoServizioApplicativo.tipoSoggetto = tipoSoggetto;
											identificativoServizioApplicativo.nomeSoggetto = nomeSoggetto;
											String nomeFileServizioApplicativoSenzaEstensioni = nomeFileServizioApplicativo.substring(0, nomeFileServizioApplicativo.indexOf(".xml"));
											//System.out.println("ADD KEY["+nomeFileServizioApplicativoSenzaEstensioni+"] nome["+identificativoServizioApplicativo.nome+"]");
											mapKeyServiziApplicativi.put(nomeFileServizioApplicativoSenzaEstensioni, identificativoServizioApplicativo);
										}
										else {
											String nomeFileServizioApplicativoSenzaEstensioni = nomeFileServizioApplicativo.substring(0, nomeFileServizioApplicativo.indexOf(File.separatorChar+""));
											//System.out.println("nomeFileServizioApplicativoSenzaEstensioni: "+nomeFileServizioApplicativoSenzaEstensioni);
																						
											//String nomeServizioApplicativo = nomeFileServizioApplicativo.substring(0, nomeFileServizioApplicativo.indexOf(File.separatorChar));
											String nomeServizioApplicativo = null;
											if(mapKeyServiziApplicativi.containsKey(nomeFileServizioApplicativoSenzaEstensioni)){
												nomeServizioApplicativo = mapKeyServiziApplicativi.get(nomeFileServizioApplicativoSenzaEstensioni).nome;
											}
											else {
												throw new ProtocolException("Elemento ["+entryName+"] errato. Non risulta la definizione dell'applicativo con nome file '"+nomeFileServizioApplicativoSenzaEstensioni+"' (soggetto "+tipoSoggetto+"/"+nomeSoggetto+")");
											}
											
											String nomeFileSenzaServizioApplicativo = nomeFileServizioApplicativo.substring(nomeFileServizioApplicativo.indexOf(File.separatorChar)+1);
											this.readProprietaServizioApplicativo(archivio, content, entryName, tipoSoggetto, nomeSoggetto, nomeServizioApplicativo, nomeFileSenzaServizioApplicativo, 
													archiveVersion, mapKeyProtocolProperties);
										}
									}
									
									// ------------ porta delegata --------------------
									else if(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DELEGATE_DIR+File.separatorChar)) ){
										if(nomeFile.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar)){
											String key = this.getKeyPortaForExtendedInfo(tipoNomeSoggetto, nomeFile);
											PortaDelegata pd = mapKeyForExtendedInfo_portaDelegata.get(key);
											this.readPortaDelegataExtended(pd, bin, xml, entryName, validationDocuments, extendedInfoManager);
										}
										else{
											PortaDelegata pd = this.readPortaDelegata(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
											if(existsExtendsConfigForPortaDelegata){
												String key = this.getKeyPortaForExtendedInfo(tipoNomeSoggetto, nomeFile);
												mapKeyForExtendedInfo_portaDelegata.put(key, pd);
											}
										}
									}
									
									// ------------ porta applicativa --------------------
									else if(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_PORTE_APPLICATIVE_DIR+File.separatorChar)) ){
										if(nomeFile.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar)){
											String key = this.getKeyPortaForExtendedInfo(tipoNomeSoggetto, nomeFile);
											PortaApplicativa pa = mapKeyForExtendedInfo_portaApplicativa.get(key);
											this.readPortaApplicativaExtended(pa, bin, xml, entryName, validationDocuments, extendedInfoManager);
										}
										else{
											PortaApplicativa pa = this.readPortaApplicativa(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
											if(existsExtendsConfigForPortaApplicativa){
												String key = this.getKeyPortaForExtendedInfo(tipoNomeSoggetto, nomeFile);
												mapKeyForExtendedInfo_portaApplicativa.put(key, pa);
											}
										}
									}
									
									// ------------ accordi -------------------
									else if( (nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar))) 
											|| 
											(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)))
											|| 
											(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar)))
											||
											(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar))) ){
										
										String nomeFileAccordo = null;
										String directoryAccordo = null;
										if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar)) ){
											directoryAccordo = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR;
										}
										else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)) ){
											directoryAccordo = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR;
										}
										else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar)) ){
											directoryAccordo = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR;
										}
										else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar)) ){
											directoryAccordo = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR;
										}
										nomeFileAccordo = nomeFile.substring((directoryAccordo+File.separatorChar).length());
										
										if(nomeFileAccordo.contains((File.separatorChar+""))==false){
											
											throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
													"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione>] che descrive l'accordo e ulteriori file e/o directory che definiscono gli elementi dell'accordo");
											
										}
										else{
											
											// nome file contenente nome accordo e versione (codificati in maniera compatibile al file system)
											String nomeVersioneAccordo = nomeFileAccordo.substring(0,nomeFileAccordo.indexOf(File.separatorChar));
											if(nomeVersioneAccordo==null || "".equals(nomeVersioneAccordo) || "_".equals(nomeVersioneAccordo) || 
													nomeVersioneAccordo.startsWith("_") || nomeVersioneAccordo.endsWith("_")){
												throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
														"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione>] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa <nome>[_<versione>]");
											}
											nomeVersioneAccordo = nomeVersioneAccordo.trim();
											
											// comprendo parte restante
											String nomeFileSenzaAccordo = nomeFileAccordo.substring((nomeVersioneAccordo.length()+1),nomeFileAccordo.length());
											if(nomeFileSenzaAccordo==null){
												throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
														"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione> che descrive l'accordo e ulteriori file e/o directory che definiscono gli elementi dell'accordo: non sono stati trovati ulteriori file");
											}
											
											// ------------ accordo servizio parte comune -------------------
											// ------------ accordo servizio composto -------------------
											// ------------ accordo cooperazione -------------------
											if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar)) ||
													nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)) ||
													nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar))
													){
												
												// comprendo nome ed eventuale versione dell'accordo
												String versioneAccordo = null;
												String nomeAccordo = null;
												boolean findIdAccordo = false;
												if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
													if(nomeVersioneAccordo.contains("_")){
														nomeAccordo = nomeVersioneAccordo.split("_")[0];
														versioneAccordo = nomeVersioneAccordo.split("_")[1];
														if(versioneAccordo==null || "".equals(versioneAccordo)){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																	"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione>] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa <nome>[_<versione>]: versione non identificata");
														}
													}
													else{
														nomeAccordo = nomeVersioneAccordo;
													}
													if(nomeAccordo==null || "".equals(nomeAccordo)){
														throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione>] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa <nome>[_<versione>]: nome non identificato");
													}
												}
												else{
													IdentificativoAccordo idAccordo = null;
													String keyAccordo = getKeyAccordo(tipoSoggetto, nomeSoggetto, nomeVersioneAccordo, nomeFile);
													if( nomeFileSenzaAccordo.equals(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME) ){
														String identificativo = this.readLineId(xml);
														if(identificativo.contains(" ")){
															nomeAccordo = identificativo.split(" ")[0];
															versioneAccordo = identificativo.split(" ")[1];
															if(versioneAccordo==null || "".equals(versioneAccordo)){
																throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																		"] non è corretto, deve essere presente una struttura '<nome> <versione>' che descrive l'identificativo dell'accordo: versione non identificata");
															}
														}
														else{
															nomeAccordo = identificativo;
														}
														if(nomeAccordo==null || "".equals(nomeAccordo)){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																	"] non è corretto, deve essere presenta una struttura '<nome>[ <versione>]' che descrive identificativo dell'accordo: nome non identificato");
														}
														idAccordo = new IdentificativoAccordo();
														idAccordo.nome = nomeAccordo;
														idAccordo.versione = versioneAccordo;
														mapKeyAccordi.put(keyAccordo, idAccordo);
														findIdAccordo = true;
													}
													else{
														idAccordo = mapKeyAccordi.get(keyAccordo);
														if(idAccordo==null){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Non è stato trovato precedentemente il file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"] contenente l'identificativo dell'accordo");
														}
														nomeAccordo = idAccordo.nome;
														versioneAccordo = idAccordo.versione;
													}
												}	
												
												if(findIdAccordo==false){
												
													// ------------ accordo servizio parte comune -------------------
													if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar)) ){
														this.readAccordoServizioParteComune(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
																nomeFileSenzaAccordo,nomeAccordo,versioneAccordo,false, validationDocuments, idCorrelazione,
																archiveVersion, mapKeyDocumenti, mapKeyProtocolProperties);
													}
													// ------------ accordo servizio composto -------------------
													else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)) ){
														this.readAccordoServizioParteComune(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
																nomeFileSenzaAccordo,nomeAccordo,versioneAccordo,true, validationDocuments, idCorrelazione,
																archiveVersion, mapKeyDocumenti, mapKeyProtocolProperties);
													}
													// ------------ accordo cooperazione -------------------
													else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar)) ){
														this.readAccordoCooperazione(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
																nomeFileSenzaAccordo,nomeAccordo,versioneAccordo, validationDocuments, idCorrelazione,
																archiveVersion, mapKeyDocumenti, mapKeyProtocolProperties);
													}
													
												}
											}
											
											// ------------ accordo servizio parte specifica -------------------
											else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar)) ){
												
												// comprendo tipo, nome ed eventuale versione dell'accordo
												String versioneServizio = null;
												String nomeServizio = null;
												String tipoServizio = null;
												boolean findIdAccordo = false;
												if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
													// parte specifica tipo_nome[_versione]
													if(nomeVersioneAccordo.contains("_")==false){
														throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]");
													}
													int indexType = nomeVersioneAccordo.indexOf("_");
													if(indexType<=0){
														throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: tipo non trovato (index:"+indexType+")");
													}
													tipoServizio = nomeVersioneAccordo.substring(0, indexType);
													
													if(indexType==nomeVersioneAccordo.length()){
														throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: nome e versione non trovati");
													}
													String _nomeVersione = nomeVersioneAccordo.substring(indexType, nomeVersioneAccordo.length());
													if(_nomeVersione==null){
														throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: nome e versione non trovati");
													}
													if(_nomeVersione.contains("_")==false){
														nomeServizio = _nomeVersione;
													}
													else{
														int indexVersione = _nomeVersione.lastIndexOf("_");
														if(indexVersione<=0){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																	"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: versione non trovata (index:"+indexVersione+")");
														}
														nomeServizio = _nomeVersione.substring(0, indexVersione);
													
														if(indexVersione==_nomeVersione.length()){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																	"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: versione non trovata dopo aver localizzato il nome");
														}
														versioneServizio = _nomeVersione.substring(indexVersione, _nomeVersione.length());
														if(versioneServizio==null){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
																	"] deve essere presenta una ulteriore directory contenente la struttura tipo_nome[_versione] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa tipo_nome[_versione]: versione non trovata");
														}
													}
												}
												else{
													IdentificativoAccordo idAccordo = null;
													String keyAccordo = getKeyAccordo(tipoSoggetto, nomeSoggetto, nomeVersioneAccordo, nomeFile);
													if( nomeFileSenzaAccordo.equals(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME) ){
														String identificativo = this.readLineId(xml);
														if(!identificativo.contains(" ")){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																	"] non è corretto, deve essere presente una struttura '<tipo> <nome> <versione>' che descrive l'identificativo dell'accordo: ' ' non trovato");
														}
														String [] tmp = identificativo.split(" ");
														if(tmp.length>2){
															tipoServizio = identificativo.split(" ")[0];
															nomeServizio = identificativo.split(" ")[1];
															versioneServizio = identificativo.split(" ")[2];
															if(versioneServizio==null || "".equals(versioneServizio)){
																throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																		"] non è corretto, deve essere presente una struttura '<tipo> <nome> <versione>' che descrive l'identificativo dell'accordo: versione non identificata");
															}
														}
														else{
															tipoServizio = identificativo.split(" ")[0];
															nomeServizio = identificativo.split(" ")[1];
														}
														if(tipoServizio==null || "".equals(tipoServizio)){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																	"] non è corretto, deve essere presenta una struttura '<tipo> <nome> [<versione>]' che descrive identificativo dell'accordo: tipo non identificato");
														}
														if(nomeServizio==null || "".equals(nomeServizio)){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Il Contenuto ["+identificativo+
																	"] non è corretto, deve essere presenta una struttura '<tipo> <nome> [<versione>]' che descrive identificativo dell'accordo: nome non identificato");
														}
														idAccordo = new IdentificativoAccordo();
														idAccordo.tipo = tipoServizio;
														idAccordo.nome = nomeServizio;
														idAccordo.versione = versioneServizio;
														mapKeyAccordi.put(keyAccordo, idAccordo);
														findIdAccordo = true;
													}
													else{
														idAccordo = mapKeyAccordi.get(keyAccordo);
														if(idAccordo==null){
															throw new ProtocolException("Elemento ["+entryName+"] errato. Non è stato trovato precedentemente il file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"] contenente l'identificativo dell'accordo");
														}
														tipoServizio = idAccordo.tipo;
														nomeServizio = idAccordo.nome;
														versioneServizio = idAccordo.versione;
													}
												}
												
												if(findIdAccordo==false){
													
													// ------------ accordo servizio parte specifica -------------------
													this.readAccordoServizioParteSpecifica(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
																nomeFileSenzaAccordo,tipoServizio,nomeServizio,versioneServizio, validationDocuments, idCorrelazione,
																archiveVersion, mapKeyDocumenti, mapKeyProtocolProperties);
												}
												
											}

										}
										
									}
									
									// others ?
									else{
										throw new ProtocolException("Elemento ["+entryName+"] non atteso");
									}
									
								}
							}
						}
												
						// others ?
						else{
							bin = new ByteArrayInputStream(content);
							this.readExternalArchive(rootDir, archivio, bin, content, entryName, validationDocuments);
						}
												
					}finally{
						try{
							if(inputStream!=null){
								inputStream.close();
							}
						}catch(Exception eClose){}
						try{
							if(bin!=null){
								bin.close();
							}
						}catch(Exception eClose){}
					}
				}
			}
				
			// finalize
			this.finalize(archivio,validationDocuments);
			
			return archivio;

		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private String getKeyAccordo(String tipoSoggetto, String nomeSoggetto, String nomeVersioneAccordo, String nomeFile){
		StringBuilder bf = new StringBuilder();
		bf.append(tipoSoggetto==null?"":tipoSoggetto);
		bf.append("_");
		bf.append(nomeSoggetto==null?"":nomeSoggetto);
		bf.append("_");
		bf.append(nomeVersioneAccordo==null?"":nomeVersioneAccordo);
		bf.append("_");
		if( nomeFile.contains((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar)) ){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR);
		}
		else if( nomeFile.contains((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)) ){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR);
		}
		else if( nomeFile.contains((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar)) ){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR);
		}
		else if( nomeFile.contains((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar)) ){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR);
		}
		return bf.toString();
	}
	
	private String getKeyDocumento(String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo, String nomeFile, String nomeDocumento){
		StringBuilder bf = new StringBuilder();
		bf.append(this.getKeyAccordo(tipoSoggetto, nomeSoggetto, 
				(nomeAccordo==null?"":nomeAccordo) + (versioneAccordo==null?"":versioneAccordo), 
				nomeFile));
		bf.append("_");
		if(nomeFile.contains(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar)){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI);
		}
		else if(nomeFile.contains(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI);
		}
		else if(nomeFile.contains(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar)){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO);
		}
		else if(nomeFile.contains(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar)){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA);
		}
		else if(nomeFile.contains(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar)){
			bf.append(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO);
		}
		bf.append("_");
		bf.append(nomeDocumento);
		return bf.toString();
	}
	
	private String getKeyProtocolProperty(String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			ProprietarioProprietaProtocollo proprietario,
			String nomeDocumento){
		StringBuilder bf = new StringBuilder();
		bf.append(proprietario.name());
		bf.append("_");
		if(ProprietarioProprietaProtocollo.ACCORDO_COOPERAZIONE.equals(proprietario) ||
				ProprietarioProprietaProtocollo.ACCORDO_SERVIZIO_PARTE_COMUNE.equals(proprietario)  ||
				ProprietarioProprietaProtocollo.ACCORDO_SERVIZIO_PARTE_SPECIFICA.equals(proprietario) ||
				ProprietarioProprietaProtocollo.FRUITORE.equals(proprietario) ) {
			bf.append(tipoSoggetto==null?"":tipoSoggetto);
			bf.append("_");
			bf.append(nomeSoggetto==null?"":nomeSoggetto);
			bf.append("_");
			String nomeVersioneAccordo = (nomeAccordo==null?"":nomeAccordo) + (versioneAccordo==null?"":versioneAccordo);
			bf.append(nomeVersioneAccordo==null?"":nomeVersioneAccordo);
		}
		else if(ProprietarioProprietaProtocollo.SOGGETTO.equals(proprietario) ||
				ProprietarioProprietaProtocollo.SERVIZIO_APPLICATIVO.equals(proprietario)) {
			bf.append(tipoSoggetto==null?"":tipoSoggetto);
			bf.append("_");
			bf.append(nomeSoggetto==null?"":nomeSoggetto);
		}
		bf.append("_");
		if(ProprietarioProprietaProtocollo.FRUITORE.equals(proprietario)) {
			bf.append(tipoSoggetto==null?"":tipoSoggettoFruitore);
			bf.append("_");
			bf.append(nomeSoggetto==null?"":nomeSoggettoFruitore);
			bf.append("_");
		}
		if(ProprietarioProprietaProtocollo.SERVIZIO_APPLICATIVO.equals(proprietario)) {
			bf.append(nomeAccordo==null?"":nomeAccordo);
			bf.append("_");
		}
		
		bf.append("_");
		bf.append(nomeDocumento);
		return bf.toString();
	}
	
	private String getKeyPortaForExtendedInfo(String tipoNomeSoggetto, String nomeFile){
		String nomePortaSuFileSystem = nomeFile.substring(nomeFile.lastIndexOf(File.separatorChar)); 
		if(nomePortaSuFileSystem.endsWith(".xml")){
			nomePortaSuFileSystem = nomePortaSuFileSystem.substring(0,nomePortaSuFileSystem.length()-4);
		}
		else if(nomePortaSuFileSystem.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT)){
			nomePortaSuFileSystem = nomeFile.substring(0,nomeFile.lastIndexOf(File.separatorChar)); 
			if(nomePortaSuFileSystem.contains(File.separatorChar+"")){
				nomePortaSuFileSystem = nomePortaSuFileSystem.substring(nomePortaSuFileSystem.lastIndexOf(File.separatorChar)); 
			}
		}
		String key = tipoNomeSoggetto + nomePortaSuFileSystem;
		return key;
	}
	
	public void finalize(Archive archivio,boolean validationDocuments) throws ProtocolException{
		if(this.csvErogazioni!=null){
			Deserializer deserializer = new Deserializer(validationDocuments, this.log);
			for (byte[] erogazioneCsv : this.csvErogazioni) {
				this.erogazioni.addAll(deserializer.toErogazione(this.csvTemplateErogazione, this.csvFormatErogazione, this.csvMappingErogazione, erogazioneCsv));
			}
		}
		if(this.erogazioni!=null){
			for (Erogazione erogazione : this.erogazioni) {
				this.getErogazioneConverter().fillArchive(archivio, erogazione, this.templateErogazione, this.registryReader, this.configIntegrationReader, validationDocuments);
			}
		}

		if(this.csvFruizioni!=null){
			Deserializer deserializer = new Deserializer(validationDocuments, this.log);
			for (byte[] fruizioneCsv : this.csvFruizioni) {
				this.fruizioni.addAll(deserializer.toFruizione(this.csvTemplateFruizione, this.csvFormatFruizione, this.csvMappingFruizione, fruizioneCsv));
			}
		}
		if(this.fruizioni!=null){
			for (Fruizione fruizione : this.fruizioni) {
				this.getFruizioneConverter().fillArchive(archivio, fruizione, this.templateFruizione, this.registryReader, this.configIntegrationReader, validationDocuments);
			}
		
		}
	}
	
	public void readExternalArchive(String rootDir, Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		if(entryName.startsWith((rootDir+CostantiAbstraction.EROGAZIONI_DIR+File.separatorChar)) ){
			
			String name = entryName.substring((rootDir+CostantiAbstraction.EROGAZIONI_DIR+File.separatorChar).length());
			if(name.contains((File.separatorChar+""))){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+CostantiAbstraction.EROGAZIONI_DIR+
						"] non devono essere presenti ulteriori directory");
			}
			if(this.templateErogazione==null){
				this.initializeErogazioni();
			}
			if(name.equals(CostantiAbstraction.TEMPLATE_FILE) ){
				this.templateErogazione.updateTemplates(xml);
			}
			else if(name.equals(CostantiAbstraction.CSV_FORMAT_FILE) ){
				this.csvFormatErogazione = xml;
			}
			else if(name.equals(CostantiAbstraction.CSV_MAPPING_FILE) ){
				this.csvMappingErogazione = xml;
			}
			else if(name.equals(CostantiAbstraction.CSV_TEMPLATE_FILE) ){
				this.csvTemplateErogazione = xml;
			}
			else if(name.endsWith(CostantiAbstraction.XML_EXTENSION)){
				try{
					if(validationDocuments){
						org.openspcoop2.protocol.abstraction.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
					}
					Erogazione erogazione = this.jaxbAbstractionDeserializer.readErogazione(xml);
					this.erogazioni.add(erogazione);
				}catch(Exception eDeserializer){
					String xmlString = this.toStringXmlElementForErrorMessage(xml);
					throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (erogazione) non valida rispetto allo schema (Abstraction): "
							+eDeserializer.getMessage(),eDeserializer);
				}
			}
			else if(name.endsWith(CostantiAbstraction.CSV_EXTENSION)){
				this.csvErogazioni.add(xml);
			}
			else{
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Sono supportati solamente i formati 'xml' e 'csv' oltre a poter fornire i seguente files: '"+
						CostantiAbstraction.TEMPLATE_FILE+"','"+
						CostantiAbstraction.CSV_FORMAT_FILE+"','"+
						CostantiAbstraction.CSV_MAPPING_FILE+"','"+
						CostantiAbstraction.CSV_TEMPLATE_FILE+"'");
			}
			
		}
		else if(entryName.startsWith((rootDir+CostantiAbstraction.FRUIZIONI_DIR+File.separatorChar)) ){
			
			String name = entryName.substring((rootDir+CostantiAbstraction.FRUIZIONI_DIR+File.separatorChar).length());
			if(name.contains((File.separatorChar+""))){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+CostantiAbstraction.FRUIZIONI_DIR+
						"] non devono essere presenti ulteriori directory");
			}
			if(this.templateFruizione==null){
				this.initializeFruizioni();
			}
			if(name.equals(CostantiAbstraction.TEMPLATE_FILE) ){
				this.templateFruizione.updateTemplates(xml);
			}
			else if(name.equals(CostantiAbstraction.CSV_FORMAT_FILE) ){
				this.csvFormatFruizione = xml;
			}
			else if(name.equals(CostantiAbstraction.CSV_MAPPING_FILE) ){
				this.csvMappingFruizione = xml;
			}
			else if(name.equals(CostantiAbstraction.CSV_TEMPLATE_FILE) ){
				this.csvTemplateFruizione = xml;
			}
			else if(name.endsWith(CostantiAbstraction.XML_EXTENSION)){
				try{
					if(validationDocuments){
						org.openspcoop2.protocol.abstraction.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
					}
					Fruizione fruizione = this.jaxbAbstractionDeserializer.readFruizione(xml);
					this.fruizioni.add(fruizione);
				}catch(Exception eDeserializer){
					String xmlString = this.toStringXmlElementForErrorMessage(xml);
					throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (fruizione) non valida rispetto allo schema (Abstraction): "
							+eDeserializer.getMessage(),eDeserializer);
				}
			}
			else if(name.endsWith(CostantiAbstraction.CSV_EXTENSION)){
				this.csvFruizioni.add(xml);
			}
			else{
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Sono supportati solamente i formati 'xml' e 'csv' oltre a poter fornire i seguenti files: '"+
						CostantiAbstraction.TEMPLATE_FILE+"','"+
						CostantiAbstraction.CSV_FORMAT_FILE+"','"+
						CostantiAbstraction.CSV_MAPPING_FILE+"','"+
						CostantiAbstraction.CSV_TEMPLATE_FILE+"'");
			}
						
		}
		else{
			throw new ProtocolException("Elemento ["+entryName+"] non atteso");
		}
	}
	
	public void readConfigurazione(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			Configurazione configurazione = this.jaxbConfigDeserializer.readConfigurazione(xml);
			if(archivio.getConfigurazionePdD()!=null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una configurazione della pdd");
			}
			archivio.setConfigurazionePdD(configurazione);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (configurazione) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readConfigurazioneExtended(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments,ExtendedInfoManager extendedInfoManager) throws ProtocolException{
		try{
			if(archivio.getConfigurazionePdD()==null){
				//throw new Exception("Non è possibile indicare una configurazione estesa senza indicare anche la configurazione generale");
				// Caso speciale dove è presente solo l'extended info
				archivio.setConfigurazionePdD(new Configurazione());
			}
			Object o = extendedInfoManager.newInstanceExtendedInfoConfigurazione().deserialize(this.log, archivio.getConfigurazionePdD(), xml);
			archivio.getConfigurazionePdD().addExtendedInfo(o);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (configurazione-extended) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readControlloTraffico_configurazione(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.controllo_traffico.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazione = this.jaxbControlloTrafficoDeserializer.readConfigurazioneGenerale(xml);
			if(archivio.getControlloTraffico_configurazione()!=null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una configurazione di controllo del traffico");
			}
			archivio.setControlloTraffico_configurazione(configurazione);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (configurazione) non valida rispetto allo schema (ControlloTraffico): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readControlloTraffico_configurazionePolicy(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.controllo_traffico.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy policy = this.jaxbControlloTrafficoDeserializer.readConfigurazionePolicy(xml);
			String key = ArchiveConfigurationPolicy.buildKey(policy.getIdPolicy());
			if(archivio.getControlloTraffico_configurationPolicies().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una configurazione di policy con key ["+key+"]");
			}
			archivio.getControlloTraffico_configurationPolicies().add(key,new ArchiveConfigurationPolicy(policy,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (configurazione-policy) non valida rispetto allo schema (ControlloTraffico): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readControlloTraffico_attivazionePolicy(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.controllo_traffico.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy = this.jaxbControlloTrafficoDeserializer.readAttivazionePolicy(xml);
			RuoloPolicy ruoloPorta = null;
			String nomePorta = null;
			if(policy!=null && policy.getFiltro()!=null) {
				ruoloPorta = policy.getFiltro().getRuoloPorta();
				nomePorta = policy.getFiltro().getNomePorta();
			}
			String key = ArchiveActivePolicy.buildKey(ruoloPorta, nomePorta, policy.getAlias());
			if(archivio.getControlloTraffico_activePolicies().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere più di un'attivazione di policy con key ["+key+"]");
			}
			archivio.getControlloTraffico_activePolicies().add(key,new ArchiveActivePolicy(policy,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (attivazione-policy) non valida rispetto allo schema (ControlloTraffico): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readAllarme(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.allarmi.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.allarmi.Allarme allarme = this.jaxbAllarmeDeserializer.readAllarme(xml);
			RuoloPorta ruoloPorta = null;
			String nomePorta = null;
			if(allarme!=null && allarme.getFiltro()!=null) {
				ruoloPorta = allarme.getFiltro().getRuoloPorta();
				nomePorta = allarme.getFiltro().getNomePorta();
			}
			String key = ArchiveAllarme.buildKey(ruoloPorta, nomePorta, allarme.getAlias());
			if(archivio.getAllarmi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere più di un'allarme con key ["+key+"]");
			}
			archivio.getAllarmi().add(key,new ArchiveAllarme(allarme,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (allarme) non valida rispetto allo schema (Allarmi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readTokenValidationPolicy(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.config.GenericProperties policy = this.jaxbConfigDeserializer.readGenericProperties(xml);
			String key = ArchiveTokenPolicy.buildKey(policy.getTipo(), policy.getNome());
			if(archivio.getToken_validation_policies().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una policy di token validation con key ["+key+"]");
			}
			archivio.getToken_validation_policies().add(key,new ArchiveTokenPolicy(policy,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (token-policy) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readTokenRetrievePolicy(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.config.GenericProperties policy = this.jaxbConfigDeserializer.readGenericProperties(xml);
			String key = ArchiveTokenPolicy.buildKey(policy.getTipo(), policy.getNome());
			if(archivio.getToken_retrieve_policies().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una policy di token retrieve con key ["+key+"]");
			}
			archivio.getToken_retrieve_policies().add(key,new ArchiveTokenPolicy(policy,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (token-policy) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readPluginClasse(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.plugins.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.plugins.Plugin plugin = this.jaxbPluginDeserializer.readPlugin(xml);
			String key = ArchivePluginClasse.buildKey(plugin.getTipoPlugin(), plugin.getTipo());
			if(archivio.getPlugin_classi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un plugin con key ["+key+"]");
			}
			archivio.getPlugin_classi().add(key,new ArchivePluginClasse(plugin,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (plugin-classe) non valida rispetto allo schema (Plugins): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readPluginArchivio(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.config.RegistroPlugin plugin = this.jaxbConfigDeserializer.readRegistroPlugin(xml);
			String key = ArchivePluginArchivio.buildKey(plugin.getNome());
			if(archivio.getPlugin_archivi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un archivio plugin con key ["+key+"]");
			}
			archivio.getPlugin_archivi().add(key,new ArchivePluginArchivio(plugin,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (plugin-archivio) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readUrlInvocazioneRegola(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola regola = this.jaxbConfigDeserializer.readConfigurazioneUrlInvocazioneRegola(xml);
			String key = ArchiveUrlInvocazioneRegola.buildKey(regola.getNome());
			if(archivio.getConfigurazionePdD_urlInvocazione_regole().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una regola proxy pass con key ["+key+"]");
			}
			archivio.getConfigurazionePdD_urlInvocazione_regole().add(key,new ArchiveUrlInvocazioneRegola(regola,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (regola proxy-pass) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readUrlInvocazione(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			org.openspcoop2.core.config.ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = this.jaxbConfigDeserializer.readConfigurazioneUrlInvocazione(xml);
			if(archivio.getConfigurazionePdD_urlInvocazione()!=null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una configurazione della url di invocazione");
			}
			archivio.setConfigurazionePdD_urlInvocazione(configurazioneUrlInvocazione);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (configurazione url invocazione) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	
	public void readPortaDominio(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			PortaDominio pdd = this.jaxbRegistryDeserializer.readPortaDominio(xml);
			String key = ArchivePdd.buildKey(pdd.getNome());
			if(archivio.getPdd().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una pdd con key ["+key+"]");
			}
			archivio.getPdd().add(key,new ArchivePdd(pdd,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (porta-dominio) non valida rispetto allo schema (RegistroServizi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readGruppo(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			Gruppo gruppo = this.jaxbRegistryDeserializer.readGruppo(xml);
			String key = ArchiveGruppo.buildKey(gruppo.getNome());
			if(archivio.getGruppi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un gruppo con key ["+key+"]");
			}
			archivio.getGruppi().add(key,new ArchiveGruppo(gruppo,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (gruppo) non valida rispetto allo schema (RegistroServizi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readRuolo(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			Ruolo ruolo = this.jaxbRegistryDeserializer.readRuolo(xml);
			String key = ArchiveRuolo.buildKey(ruolo.getNome());
			if(archivio.getRuoli().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un ruolo con key ["+key+"]");
			}
			archivio.getRuoli().add(key,new ArchiveRuolo(ruolo,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (ruolo) non valida rispetto allo schema (RegistroServizi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readScope(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			Scope scope = this.jaxbRegistryDeserializer.readScope(xml);
			String key = ArchiveScope.buildKey(scope.getNome());
			if(archivio.getScope().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di uno scope con key ["+key+"]");
			}
			archivio.getScope().add(key,new ArchiveScope(scope,idCorrelazione));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (scope) non valida rispetto allo schema (RegistroServizi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readInformationMissing(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		try{
			
			// verifica
			Openspcoop2 informationMissingOp2 = null;
			if(archivio.getInformationMissing()!=null){
				informationMissingOp2 = archivio.getInformationMissing();
			}
			
			// conversione
			if(informationMissingOp2!=null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Una definizione xml di informationMissing e' presente piu' di una volta");
			}
			if(validationDocuments){
				org.openspcoop2.protocol.information_missing.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			informationMissingOp2 = this.jaxbInformationMissingDeserializer.readOpenspcoop2(xml);

			// add
			archivio.setInformationMissing(informationMissingOp2);
			
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (informationMissing) non valida rispetto allo schema informationMissing.xsd: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readSoggetto(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		boolean registro = false;
		String schema = null;
		if(RegistroServiziUtils.isRegistroServizi(xml, CostantiRegistroServizi.LOCAL_NAME_SOGGETTO)){
			registro = true;
			schema = "RegistroServizi";
		}
		else if(ConfigurazionePdDUtils.isConfigurazionePdD(xml, CostantiConfigurazione.LOCAL_NAME_SOGGETTO)){
			registro = false;
			schema = "ConfigurazionePdD";
		}
		else{
			throw new ProtocolException("Elemento ["+entryName+"] errato. Attesa una definizione del soggetto che contenga informazioni di registro (namespace: "+
						CostantiRegistroServizi.TARGET_NAMESPACE+") o di configurazione ("+CostantiConfigurazione.TARGET_NAMESPACE+")"); 
		}
		
		try{
			
			// verifica
			if(tipoSoggetto==null || nomeSoggetto==null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Non e' possibile fornire una definizione di soggetto all'interna di una directory che non definisce il tipo o il nome del soggetto");
			}
			org.openspcoop2.core.registry.Soggetto soggettoRegistroServizi = null;
			org.openspcoop2.core.config.Soggetto soggettoConfigurazione = null;
			String key = ArchiveSoggetto.buildKey(tipoSoggetto, nomeSoggetto);
			if(archivio.getSoggetti().containsKey(key)){
				ArchiveSoggetto archiveSoggetto = archivio.getSoggetti().remove(key);
				soggettoRegistroServizi = archiveSoggetto.getSoggettoRegistro();
				soggettoConfigurazione = archiveSoggetto.getSoggettoConfigurazione();
			}
			
			
			// conversione
			if(registro){
				if(soggettoRegistroServizi!=null){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Una definizione xml del soggetto con tipologia ("+schema+") e' presente piu' di una volta");
				}
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				soggettoRegistroServizi = this.jaxbRegistryDeserializer.readSoggetto(xml);
				if(tipoSoggetto.equals(soggettoRegistroServizi.getTipo())==false){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml del soggetto ("+schema+") contiene un tipo ["+
							soggettoRegistroServizi.getTipo()+"] differente da quello indicato ["+tipoSoggetto+"] nella directory che contiene la definizione");
				}
				if(nomeSoggetto.equals(soggettoRegistroServizi.getNome())==false){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml del soggetto ("+schema+") contiene un tipo ["+
							soggettoRegistroServizi.getNome()+"] differente da quello indicato ["+nomeSoggetto+"] nella directory che contiene la definizione");
				}
			}
			else if(!registro){
				if(soggettoConfigurazione!=null){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Una definizione xml del soggetto con tipologia ("+schema+") e' presente piu' di una volta");
				}
				if(validationDocuments){
					org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				soggettoConfigurazione = this.jaxbConfigDeserializer.readSoggetto(xml);
				if(tipoSoggetto.equals(soggettoConfigurazione.getTipo())==false){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml del soggetto ("+schema+") contiene un tipo ["+
							soggettoConfigurazione.getTipo()+"] differente da quello indicato ["+tipoSoggetto+"] nella directory che contiene la definizione");
				}
				if(nomeSoggetto.equals(soggettoConfigurazione.getNome())==false){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml del soggetto ("+schema+") contiene un tipo ["+
							soggettoConfigurazione.getNome()+"] differente da quello indicato ["+nomeSoggetto+"] nella directory che contiene la definizione");
				}
			}
			else{
				throw new ProtocolException("Elemento ["+entryName+"] errato. Attesa una definizione del soggetto che contenga informazioni di registro (namespace: "+
							CostantiRegistroServizi.TARGET_NAMESPACE+") o di configurazione ("+CostantiConfigurazione.TARGET_NAMESPACE+")"); 
			}
			
			// add
			if(soggettoConfigurazione!=null && soggettoRegistroServizi!=null)
				archivio.getSoggetti().add(key,new ArchiveSoggetto(soggettoConfigurazione, soggettoRegistroServizi, idCorrelazione));
			else if(soggettoConfigurazione!=null){
				archivio.getSoggetti().add(key,new ArchiveSoggetto(soggettoConfigurazione, idCorrelazione));
			}
			else{
				archivio.getSoggetti().add(key,new ArchiveSoggetto(soggettoRegistroServizi, idCorrelazione));
			}
			
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (soggetto) non valida rispetto allo schema ("+schema+"): "
					+eDeserializer.getMessage(),eDeserializer);
		}
		
	}
	
	public ServizioApplicativo readServizioApplicativo(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, 
			ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			ServizioApplicativo sa = this.jaxbConfigDeserializer.readServizioApplicativo(xml);
			sa.setTipoSoggettoProprietario(tipoSoggetto);
			sa.setNomeSoggettoProprietario(nomeSoggetto);
			String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
			String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
			String key = ArchiveServizioApplicativo.buildKey(tipoSoggettoKey, nomeSoggettoKey, sa.getNome());
			if(archivio.getServiziApplicativi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un servizio applicativo con key ["+key+"]");
			}
			archivio.getServiziApplicativi().add(key,new ArchiveServizioApplicativo(sa,idCorrelazione,true));
			return sa;
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (servizio-applicativo) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	public void readProprietaServizioApplicativo(Archive archivio,byte[]content,String entryName, String tipoSoggetto, String nomeSoggetto, String nomeSA,
			String nomeFileSenzaServizioApplicativo, ArchiveVersion archiveVersion,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties) throws ProtocolException {
		org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo = null;
		String key = ArchiveServizioApplicativo.buildKey(tipoSoggetto, nomeSoggetto, nomeSA);
		if(archivio.getServiziApplicativi().containsKey(key)){
			servizioApplicativo = archivio.getServiziApplicativi().get(key).getServizioApplicativo();
		}
		else {
			throw new ProtocolException("Elemento ["+entryName+"] errato. Non risulta la definizione dell'applicativo ["+tipoSoggetto+"/"+nomeSoggetto+" "+nomeSA+"]");
		}
		
		List<org.openspcoop2.core.config.ProtocolProperty> listPP = new ArrayList<>();
		if(servizioApplicativo.sizeProtocolPropertyList()>0) {
			for (org.openspcoop2.core.config.ProtocolProperty protocolProperty : servizioApplicativo.getProtocolPropertyList()) {
				if(protocolProperty.getTipoProprietario()==null) {
					protocolProperty.setTipoProprietario(ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.name());
				}
				listPP.add(protocolProperty);
			}
		}
		
		processProtocolPropertyConfig(nomeFileSenzaServizioApplicativo, archiveVersion, entryName, content, 
				ProprietarioProprietaProtocollo.SERVIZIO_APPLICATIVO, 
				tipoSoggetto, nomeSoggetto, nomeSA, 
				mapKeyProtocolProperties, listPP, null);
		
	}
	
	public PortaDelegata readPortaDelegata(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			PortaDelegata pd = this.jaxbConfigDeserializer.readPortaDelegata(xml);
			
			// backward compatibility
			if("openspcoop".equals(pd.getAutorizzazione())){
				pd.setAutorizzazione(TipoAutorizzazione.AUTHENTICATED.getValue());
			}
			
			pd.setTipoSoggettoProprietario(tipoSoggetto);
			pd.setNomeSoggettoProprietario(nomeSoggetto);
			String nome = pd.getNome();
			String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
			String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
			String key = ArchivePortaDelegata.buildKey(tipoSoggettoKey, nomeSoggettoKey, nome);
			if(archivio.getPorteDelegate().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una porta con key ["+key+"]");
			}
			archivio.getPorteDelegate().add(key,new ArchivePortaDelegata(pd,idCorrelazione,true));
			return pd;
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (porta-delegata) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readPortaDelegataExtended(PortaDelegata pd,InputStream bin,byte[]xml,String entryName,boolean validationDocuments,ExtendedInfoManager extendedInfoManager) throws ProtocolException{
		try{
			if(pd==null){
				throw new Exception("Non è possibile indicare una configurazione estesa senza indicare anche la definizione della porta delegata");
			}
			Object o = extendedInfoManager.newInstanceExtendedInfoPortaDelegata().deserialize(this.log, pd, xml);
			pd.addExtendedInfo(o);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (porta-delegata-extended) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public PortaApplicativa readPortaApplicativa(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			PortaApplicativa pa = this.jaxbConfigDeserializer.readPortaApplicativa(xml);
			pa.setTipoSoggettoProprietario(tipoSoggetto);
			pa.setNomeSoggettoProprietario(nomeSoggetto);
			String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
			String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
			String key = ArchivePortaApplicativa.buildKey(tipoSoggettoKey, nomeSoggettoKey, pa.getNome());
			if(archivio.getPorteApplicative().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di una porta con key ["+key+"]");
			}
			archivio.getPorteApplicative().add(key,new ArchivePortaApplicativa(pa,idCorrelazione,true));
			return pa;
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (porta-applicativa) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readPortaApplicativaExtended(PortaApplicativa pa,InputStream bin,byte[]xml,String entryName,boolean validationDocuments,ExtendedInfoManager extendedInfoManager) throws ProtocolException{
		try{
			if(pa==null){
				throw new Exception("Non è possibile indicare una configurazione estesa senza indicare anche la definizione della porta applicativa");
			}
			Object o = extendedInfoManager.newInstanceExtendedInfoPortaApplicativa().deserialize(this.log, pa, xml);
			pa.addExtendedInfo(o);
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (porta-applicativa-extended) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public final static String USE_VERSION_XML_BEAN = "USE_VERSION_XML_BEAN";
	
	public void readAccordoServizioParteComune(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,
			String nomeFileSenzaAccordo,String nomeAccordo,String versioneAccordo, boolean servizioComposto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione,
			ArchiveVersion archiveVersion,
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties) throws ProtocolException{
		
		Integer versioneAccordoInt = null;
		try{
			if(versioneAccordo!=null && USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
				versioneAccordoInt = Integer.parseInt(versioneAccordo);
			}
		}catch(Exception e){
			throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) nel path contiene una versione errata: "+e.getMessage());
		}
		
		String key = null;
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		Integer versioneKey = (versioneAccordoInt!=null ? versioneAccordoInt : -1 );
		if(servizioComposto){
			key = ArchiveAccordoServizioComposto.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
		}else{
			key = ArchiveAccordoServizioParteComune.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
		}
		
		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				AccordoServizioParteComune aspc = this.jaxbRegistryDeserializer.readAccordoServizioParteComune(xml);
				
				// soggetto referente
				if(aspc.getSoggettoReferente()==null){
					aspc.setSoggettoReferente(new IdSoggetto());
				}
				if(tipoSoggetto!=null){
					if(aspc.getSoggettoReferente().getTipo()!=null && !aspc.getSoggettoReferente().getTipo().equals(tipoSoggetto)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un soggetto referente con tipo ["+
								aspc.getSoggettoReferente().getTipo()+"] differente da quello indicato ["+tipoSoggetto+"] nella directory che contiene la definizione");
					}
				}
				if(nomeSoggetto!=null){
					if(aspc.getSoggettoReferente().getNome()!=null && !aspc.getSoggettoReferente().getNome().equals(nomeSoggetto)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un soggetto referente con nome ["+
								aspc.getSoggettoReferente().getNome()+"] differente da quello indicato ["+nomeSoggetto+"] nella directory che contiene la definizione");
					}
				}
				aspc.getSoggettoReferente().setTipo(tipoSoggetto);
				aspc.getSoggettoReferente().setNome(nomeSoggetto);

				// nome e versione check quello indicato nell'accordo rispetto a quello indicato 
				// - nome della directory (old version)
				// - file idAccordo (new version)
				if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
					String convertName = ZIPUtils.oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(aspc.getNome(),false);
					if(!convertName.equals(nomeAccordo)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								aspc.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeAccordo+"] nella directory che contiene la definizione");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
						if(versioneAccordoInt!=null){
							if(aspc.getVersione()!=null){
								if(aspc.getVersione()!=null && aspc.getVersione().intValue()!= versioneAccordoInt.intValue()){
									throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
											aspc.getVersione()+"] (fileSystemName:"+versioneAccordo+") differente da quella indicato ["+versioneAccordoInt+"] nella directory che contiene la definizione");
								}
							}
							aspc.setVersione(versioneAccordoInt);
						}
					}
				}
				else{
					if(!aspc.getNome().equals(nomeAccordo)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								aspc.getNome()+"] differente da quello indicato ["+nomeAccordo+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
						if(versioneAccordoInt!=null){
							if(aspc.getVersione()!=null && aspc.getVersione().intValue()!= versioneAccordoInt.intValue()){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
											aspc.getVersione()+"] differente da quella indicata ["+versioneAccordoInt+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
							}
						}
						aspc.setVersione(versioneAccordoInt);
					}
				}
				
				// servizio composto
				if(servizioComposto){
					if(aspc.getServizioComposto()==null){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) non contiene le informazioni obbligatorie in un servizio composto");
					}
					if(archivio.getAccordiServizioComposto().containsKey(key)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un accordo con key ["+key+"]");
					}
					archivio.getAccordiServizioComposto().add(key,new ArchiveAccordoServizioComposto(aspc,idCorrelazione,true));
				}
				else{
					if(aspc.getServizioComposto()!=null){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene le informazioni per un servizio composto, ma l'accordo e' stato definito tra gli accordi di servizio parte comune");
					}
					if(archivio.getAccordiServizioParteComune().containsKey(key)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un accordo con key ["+key+"]");
					}
					archivio.getAccordiServizioParteComune().add(key,new ArchiveAccordoServizioParteComune(aspc,idCorrelazione,true));
				}			

			}catch(Exception eDeserializer){
				String xmlString = this.toStringXmlElementForErrorMessage(xml);
				throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (accordo-servizio-parte-comune) non valida rispetto allo schema (RegistroServizi): "
						+eDeserializer.getMessage(),eDeserializer);
			}
			
		}
		else{
			
			// recupero archivio precedentemente letto
			AccordoServizioParteComune as = null;
			if(servizioComposto){
				if(archivio.getAccordiServizioComposto().containsKey(key)==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di un accordo senza fornire la definizione xml dell'accordo");
				}
				as = archivio.getAccordiServizioComposto().get(key).getAccordoServizioParteComune();
			}else{
				if(archivio.getAccordiServizioParteComune().containsKey(key)==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di un accordo senza fornire la definizione xml dell'accordo");
				}
				as = archivio.getAccordiServizioParteComune().get(key).getAccordoServizioParteComune();
			}
			
			// wsdl
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar)){
				
				if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA)){
					as.setByteWsdlDefinitorio(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL)){
					as.setByteWsdlConcettuale(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL)){
					as.setByteWsdlLogicoErogatore(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL)){
					as.setByteWsdlLogicoFruitore(xml);
				}
				
			}
			
			// specifica conversazioni
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI+File.separatorChar)){
				
				if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_CONCETTUALE)){
					as.setByteSpecificaConversazioneConcettuale(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_EROGATORE)){
					as.setByteSpecificaConversazioneErogatore(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_FRUITORE)){
					as.setByteSpecificaConversazioneFruitore(xml);
				}
				
			}
			
			// allegati
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, mapKeyDocumenti, as.getAllegatoList());
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, mapKeyDocumenti, as.getSpecificaSemiformaleList());
				
			}
			
			// specificheCoordinamento
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar)){
				
				if(servizioComposto==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di specifica di coordinamento per un accordo non registrato come servizio composto");
				}
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, mapKeyDocumenti, as.getServizioComposto().getSpecificaCoordinamentoList());
				
			}
			
			// protocolProperties
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar)){

				List<ProtocolProperty> listPP = new ArrayList<>();
				HashMap<String,Long> mapIdToLong = new HashMap<>();			
				_updateListProtocolProperties(as, listPP, mapIdToLong);
				processProtocolProperty(nomeFileSenzaAccordo, archiveVersion, entryName, xml, 
						ProprietarioProprietaProtocollo.ACCORDO_SERVIZIO_PARTE_COMUNE, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, 
						null, null, 
						mapKeyProtocolProperties, listPP, mapIdToLong);
				
			}
		}
		
	}
	
	private void _updateListProtocolProperties(AccordoServizioParteComune as,
			List<ProtocolProperty> listPP, HashMap<String,Long> mapIdToLong) {
		long idLongForMap = 1;
		if(as.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty protocolProperty : as.getProtocolPropertyList()) {
				if(protocolProperty.getTipoProprietario()==null) {
					protocolProperty.setTipoProprietario(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE.name());
				}
				listPP.add(protocolProperty);
			}
		}
		if(as.sizeAzioneList()>0) {
			for (Azione az : as.getAzioneList()) {
				if(az.sizeProtocolPropertyList()>0) {
					String key = buildKeyPPAccordoParteComuneItem(ProprietariProtocolProperty.AZIONE_ACCORDO, az.getNome());
					Long idLong = idLongForMap++;
					mapIdToLong.put(key,idLong);
					for (ProtocolProperty protocolProperty : az.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.AZIONE_ACCORDO.name());
						}
						protocolProperty.setIdProprietario(idLong);
						listPP.add(protocolProperty);
					}
				}
			}
		}
		if(as.sizePortTypeList()>0) {
			for (PortType pt : as.getPortTypeList()) {
				if(pt.sizeProtocolPropertyList()>0) {
					String key = buildKeyPPAccordoParteComuneItem(ProprietariProtocolProperty.PORT_TYPE, pt.getNome());
					Long idLong = idLongForMap++;
					mapIdToLong.put(key,idLong);
					for (ProtocolProperty protocolProperty : pt.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.PORT_TYPE.name());
						}
						protocolProperty.setIdProprietario(idLong);
						listPP.add(protocolProperty);
					}
				}
				if(pt.sizeAzioneList()>0) {
					for (Operation op : pt.getAzioneList()) {
						if(op.sizeProtocolPropertyList()>0) {
							String key = buildKeyPPAccordoParteComuneItem(ProprietariProtocolProperty.OPERATION, op.getNome());
							Long idLong = idLongForMap++;
							mapIdToLong.put(key,idLong);
							for (ProtocolProperty protocolProperty : op.getProtocolPropertyList()) {
								if(protocolProperty.getTipoProprietario()==null) {
									protocolProperty.setTipoProprietario(ProprietariProtocolProperty.OPERATION.name());
								}
								protocolProperty.setIdProprietario(idLong);
								listPP.add(protocolProperty);
							}
						}
					}
				}
			}
		}
		if(as.sizeResourceList()>0) {
			for (Resource resource : as.getResourceList()) {
				if(resource.sizeProtocolPropertyList()>0) {
					String key = buildKeyPPAccordoParteComuneItem(ProprietariProtocolProperty.RESOURCE, resource.getNome());
					Long idLong = idLongForMap++;
					mapIdToLong.put(key,idLong);
					for (ProtocolProperty protocolProperty : resource.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.RESOURCE.name());
						}
						protocolProperty.setIdProprietario(idLong);
						listPP.add(protocolProperty);
					}
				}
			}
		}

	}

	public void readAccordoServizioParteSpecifica(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,
			String nomeFileSenzaAccordo,String tipoServizio,String nomeServizio,String versioneServizio,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione,
			ArchiveVersion archiveVersion,
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties) throws ProtocolException{
		
		Integer versioneServizioInt = null;
		try{
			if(versioneServizio!=null && USE_VERSION_XML_BEAN.equals(versioneServizio)==false){
				versioneServizioInt = Integer.parseInt(versioneServizio);
			}
		}catch(Exception e){
			throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) nel path contiene una versione errata: "+e.getMessage());
		}
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		Integer versioneKey = (versioneServizioInt!=null ? versioneServizioInt : -1 );
		String key = ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggettoKey, nomeSoggettoKey, tipoServizio, nomeServizio, versioneKey);
		
		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				AccordoServizioParteSpecifica asps = this.jaxbRegistryDeserializer.readAccordoServizioParteSpecifica(xml);
				
				// soggetto erogatore
				asps.setTipoSoggettoErogatore(tipoSoggetto);
				asps.setNomeSoggettoErogatore(nomeSoggetto);
				
				// nome e versione check quello indicato nell'accordo rispetto a quello indicato 
				// - nome della directory (old version)
				// - file idAccordo (new version)
				if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){				
					String convertName = ZIPUtils.oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(asps.getNome(),false);
					if(!convertName.equals(nomeServizio)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								asps.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeServizio+"] nella directory che contiene la definizione");
					}
					String convertTipo = ZIPUtils.oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(asps.getTipo(),false);
					if(!convertTipo.equals(tipoServizio)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un tipo ["+
								asps.getTipo()+"] (fileSystemName:"+convertTipo+") differente da quello indicato ["+tipoServizio+"] nella directory che contiene la definizione");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneServizio)==false){
						if(versioneServizioInt!=null){
							if(asps.getVersione()!=null){
								if(asps.getVersione()!=null && asps.getVersione().intValue()!= versioneServizioInt.intValue()){
									throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
											asps.getVersione()+"] (fileSystemName:"+versioneServizio+") differente da quella indicato ["+versioneServizioInt+"] nella directory che contiene la definizione");
								}
							}
							asps.setVersione(versioneServizioInt);
						}
					}
				}
				else{
					if(!asps.getNome().equals(nomeServizio)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								asps.getNome()+"] differente da quello indicato ["+nomeServizio+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
					}
					if(!asps.getTipo().equals(tipoServizio)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un tipo ["+
								asps.getNome()+"] differente da quello indicato ["+tipoServizio+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneServizio)==false){
						if(versioneServizioInt!=null){
							if(asps.getVersione()!=null && asps.getVersione().intValue()!= versioneServizioInt.intValue()){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
										asps.getVersione()+"] differente da quella indicata ["+versioneServizio+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
							}
						}
						asps.setVersione(versioneServizioInt);
					}
				}
					
				// check fruizioni
				if(asps.sizeFruitoreList()>0){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) non deve contenere fruizioni (eventuali fruizioni devono essere configurate nella struttura apposita)");
				}
				
				// add
				if(archivio.getAccordiServizioParteSpecifica().containsKey(key)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un accordo con key ["+key+"]");
				}
				archivio.getAccordiServizioParteSpecifica().add(key,new ArchiveAccordoServizioParteSpecifica(asps,idCorrelazione,true));
	
			}catch(Exception eDeserializer){
				String xmlString = this.toStringXmlElementForErrorMessage(xml);
				throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (accordo-servizio-parte-specifica) non valida rispetto allo schema (RegistroServizi): "
						+eDeserializer.getMessage(),eDeserializer);
			}
			
		}
		else{
			
			// recupero archivio precedentemente letto
			ArchiveAccordoServizioParteSpecifica archiveASPS = null;
			AccordoServizioParteSpecifica as = null;
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar)==false){
				if(archivio.getAccordiServizioParteSpecifica().containsKey(key)==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di un accordo senza fornire la definizione xml dell'accordo");
				}
				archiveASPS = archivio.getAccordiServizioParteSpecifica().get(key);
				as = archiveASPS.getAccordoServizioParteSpecifica();
			}
			
			// mapping PA
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_MAPPING+File.separatorChar) &&
					nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_MAPPING_PA_SUFFIX)){
				readAccordoServizioParteSpecifica_PortaApplicativaAssociata(archiveASPS, bin, xml, entryName);
			}
			
			// wsdl
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar)){
				
				if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL)){
					as.setByteWsdlImplementativoErogatore(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL)){
					as.setByteWsdlImplementativoFruitore(xml);
				}
				
			}
			
			// allegati
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, mapKeyDocumenti, as.getAllegatoList());
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, mapKeyDocumenti, as.getSpecificaSemiformaleList());
				
			}
			
			// specificheLivelliServizio
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, mapKeyDocumenti, as.getSpecificaLivelloServizioList());
				
			}
			
			// specificheSicurezza
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, mapKeyDocumenti, as.getSpecificaSicurezzaList());
				
			}
			
			// protocolProperties
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar)){

				List<ProtocolProperty> listPP = new ArrayList<>();
				if(as.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty protocolProperty : as.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name());
						}
						listPP.add(protocolProperty);
					}
				}
				processProtocolProperty(nomeFileSenzaAccordo, archiveVersion, entryName, xml, 
						ProprietarioProprietaProtocollo.ACCORDO_SERVIZIO_PARTE_SPECIFICA, 
						tipoSoggetto, nomeSoggetto, tipoServizio+"/"+nomeServizio, versioneServizio, 
						null, null, 
						mapKeyProtocolProperties, listPP, null);
				
			}
			
			// fruitori (politiche di sicurezza)
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar) &&
					nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI)){
				
				// NOP per backward compatibility
			}
			
			// fruitori (Mapping Fruitore - PortaDelegata)
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar) &&
					nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_MAPPING_PD_SUFFIX)){
				
				this.readAccordoServizioParteSpecifica_Fruitore_PortaDelegataAssociata(archivio, bin, xml, entryName, 
						nomeFileSenzaAccordo,
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, 
						validationDocuments, idCorrelazione);
			}
			
			// fruitori
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar)){
				this.readAccordoServizioParteSpecifica_Fruitore(archivio, bin, xml, entryName, 
						nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar).length()),
						tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, 
						validationDocuments, idCorrelazione,
						archiveVersion,
						mapKeyProtocolProperties);
			}
			
		}
		
	}
	
	public void readAccordoServizioParteSpecifica_PortaApplicativaAssociata(ArchiveAccordoServizioParteSpecifica archiveASPS,InputStream bin,byte[]xml,String entryName) throws ProtocolException{
		
		// *** leggo porta applicativa associata ****
		
		try{
			if(archiveASPS==null){
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire il mapping con la PA senza fornire la definizione xml dell'accordo di servizio parte specifica");
			}
			
			String idLine = this.readLineId(xml);
			if(idLine==null || "".equals(idLine)){
				throw new Exception("id non contiene valori");
			}
			if(idLine.endsWith("\n")) {
				idLine = idLine.substring(0, idLine.length()-1);
			}
			String [] tmp = idLine.split(" ");
			if(tmp.length<3) {
				throw new Exception("Attesi almeno tre valori separati da spazio (nomeRegola nomePorta isDefault [descrizione])");
			}
			String nomeRegola = tmp[0];
			String nomePorta = tmp[1];
			boolean isDefault = false;
			try {
				isDefault = Boolean.parseBoolean(tmp[2]);
			}catch(Exception e) {
				throw new Exception("Attesi tre valori separati da spazio (nomeRegola nomePorta isDefault) in cui l'ultimo valore di tipo booleano: "+e.getMessage(),e);
			}
			String descrizione = null;
			if(tmp.length>3) {
				String primeTreInfo = nomeRegola +" "+nomePorta+" "+isDefault+" ";
				descrizione = idLine.substring(primeTreInfo.length());
			}
			
			MappingErogazionePortaApplicativa mapping = new MappingErogazionePortaApplicativa();
			mapping.setNome(nomeRegola);
			mapping.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromAccordo(archiveASPS.getAccordoServizioParteSpecifica()));
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			mapping.setIdPortaApplicativa(idPA);
			mapping.setDefault(isDefault);
			mapping.setDescrizione(descrizione);
			
			if(archiveASPS.getMappingPorteApplicativeAssociate()==null) {
				archiveASPS.setMappingPorteApplicativeAssociate(new ArrayList<>());
			}
			archiveASPS.getMappingPorteApplicativeAssociate().add(mapping);
			
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura id (mapping asps-pa) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readAccordoServizioParteSpecifica_Fruitore(Archive archivio,InputStream bin,byte[]xml,String entryName,String nomeFileSenzaAccordo,
			String tipoSoggetto, String nomeSoggetto, String tipoServizio, String nomeServizio, String versioneServizio,
			boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione,
			ArchiveVersion archiveVersion,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties) throws ProtocolException{
		
		Integer versioneServizioInt = null;
		try{
			if(versioneServizio!=null && USE_VERSION_XML_BEAN.equals(versioneServizio)==false){
				versioneServizioInt = Integer.parseInt(versioneServizio);
			}
		}catch(Exception e){
			throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) nel path contiene una versione errata: "+e.getMessage());
		}
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		Integer versioneKey = (versioneServizioInt!=null ? versioneServizioInt : -1 );

		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				
				if(USE_VERSION_XML_BEAN.equals(versioneServizio)){
					// devo recuperare la versione dell'accordo
					String keyAccordo = ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeServizio, tipoServizio, versioneKey);
					versioneKey = archivio.getAccordiServizioParteSpecifica().get(keyAccordo).getAccordoServizioParteSpecifica().getVersione();
					versioneServizioInt = versioneKey;
				}
				
				Fruitore fruitore = this.jaxbRegistryDeserializer.readFruitore(xml);
				String keyFruitore = ArchiveFruitore.buildKey(fruitore.getTipo(), fruitore.getNome(), tipoSoggettoKey, nomeSoggettoKey, tipoServizio, nomeServizio, versioneServizioInt);
				if(archivio.getAccordiFruitori().containsKey(keyFruitore)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un fruitore con key ["+keyFruitore+"]");
				}
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, tipoSoggetto, nomeSoggetto, versioneServizioInt);
				archivio.getAccordiFruitori().add(keyFruitore,new ArchiveFruitore(idServizio,fruitore,idCorrelazione,true));
			}catch(Exception eDeserializer){
				String xmlString = this.toStringXmlElementForErrorMessage(xml);
				throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (fruitore) non valida rispetto allo schema (RegistroServizi): "
						+eDeserializer.getMessage(),eDeserializer);
			}
			
		}
		
		else {
			
			// comprendo tipo e nome soggetto fruitore
			String tipoNomeSoggettoFruitore = nomeFileSenzaAccordo.substring(0,nomeFileSenzaAccordo.indexOf(File.separatorChar));
			if(tipoNomeSoggettoFruitore==null || "".equals(tipoNomeSoggettoFruitore) || !tipoNomeSoggettoFruitore.contains("_")){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+
						"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>");
			}
			tipoNomeSoggettoFruitore = tipoNomeSoggettoFruitore.trim();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(tipoNomeSoggettoFruitore.equals("_")){
				// caso eccezionale senza ne tipo ne nome
			}
			else if(tipoNomeSoggettoFruitore.startsWith("_")){
				// caso eccezionale con solo il nome
				nomeSoggettoFruitore = tipoNomeSoggettoFruitore.substring(1);
			}
			else if(tipoNomeSoggettoFruitore.endsWith("_")){
				// caso eccezionale con solo il tipo
				tipoSoggettoFruitore = tipoNomeSoggettoFruitore.substring(0,tipoNomeSoggettoFruitore.length()-1);
			}
			else{
				// caso normale
				tipoSoggettoFruitore = tipoNomeSoggettoFruitore.split("_")[0];
				nomeSoggettoFruitore = tipoNomeSoggettoFruitore.split("_")[1];
				if(tipoSoggettoFruitore==null || "".equals(tipoSoggettoFruitore)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+
							"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: tipo non identificato");
				}
				if(nomeSoggettoFruitore==null || "".equals(nomeSoggettoFruitore)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+
							"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il soggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: nome non identificato");
				}
			}
			
			// recupero archivio precedentemente letto
			String keyFruitore = ArchiveFruitore.buildKey(tipoSoggettoFruitore, nomeSoggettoFruitore, tipoSoggettoKey, nomeSoggettoKey, tipoServizio, nomeServizio, versioneServizioInt);
			ArchiveFruitore archiveFruitore = null;
			Fruitore fruitore = null;
			if(archivio.getAccordiFruitori().containsKey(keyFruitore)==false){
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire delle proprietà di protocollo di un fruitore senza fornire la definizione xml del fruitore");
			}
			archiveFruitore = archivio.getAccordiFruitori().get(keyFruitore);
			fruitore = archiveFruitore.getFruitore();
			
			nomeFileSenzaAccordo = nomeFileSenzaAccordo.substring((tipoNomeSoggettoFruitore+File.separatorChar).length());
			
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar)){

				List<ProtocolProperty> listPP = new ArrayList<>();
				if(fruitore.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty protocolProperty : fruitore.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.FRUITORE.name());
						}
						listPP.add(protocolProperty);
					}
				}
				processProtocolProperty(nomeFileSenzaAccordo, archiveVersion, entryName, xml, 
						ProprietarioProprietaProtocollo.FRUITORE, 
						tipoSoggetto, nomeSoggetto, tipoServizio+"/"+nomeServizio, versioneServizio, 
						tipoSoggettoFruitore, nomeSoggettoFruitore, 
						mapKeyProtocolProperties, listPP, null);
				
			}
			
		}
	}
	
	public void readAccordoServizioParteSpecifica_Fruitore_PortaDelegataAssociata(Archive archivio,InputStream bin,byte[]xml,String entryName,
			String nomeFileSenzaAccordo,
			String tipoSoggetto, String nomeSoggetto, String tipoServizio, String nomeServizio, String versioneServizio,
			boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		// *** comprendo tipo e nome fruitore ****
		
		String prefixError = "Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+
				"] il file che termina con ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_MAPPING_PD_SUFFIX+
				"] deve essere contenuto in una directory definita tramite la struttura <tipo>_<nome> che descrive il soggetto fruitore.";
		
		// comprendo tipo e nome soggetto
		String nomeServizioMappingPD = nomeFileSenzaAccordo.substring(nomeFileSenzaAccordo.lastIndexOf(File.separatorChar),nomeFileSenzaAccordo.length());
		String tipoNomeSoggettoFruitore = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar).length(), 
				nomeFileSenzaAccordo.length()-
				//(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_MAPPING_PD).length());
				nomeServizioMappingPD.length());
		if(tipoNomeSoggettoFruitore==null || "".equals(tipoNomeSoggettoFruitore) || !tipoNomeSoggettoFruitore.contains("_")){
			throw new ProtocolException(prefixError+" Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>");
		}
		tipoNomeSoggettoFruitore = tipoNomeSoggettoFruitore.trim();
		String tipoSoggettoFruitore = null;
		String nomeSoggettoFruitore = null;
		if(tipoNomeSoggettoFruitore.equals("_")){
			// caso eccezionale senza ne tipo ne nome
		}
		else if(tipoNomeSoggettoFruitore.startsWith("_")){
			// caso eccezionale con solo il nome
			nomeSoggettoFruitore = tipoNomeSoggettoFruitore.substring(1);
		}
		else if(tipoNomeSoggettoFruitore.endsWith("_")){
			// caso eccezionale con solo il tipo
			tipoSoggettoFruitore = tipoNomeSoggettoFruitore.substring(0,tipoNomeSoggettoFruitore.length()-1);
		}
		else{
			// caso normale
			tipoSoggettoFruitore = tipoNomeSoggettoFruitore.split("_")[0];
			nomeSoggettoFruitore = tipoNomeSoggettoFruitore.split("_")[1];
			if(tipoSoggettoFruitore==null || "".equals(tipoSoggettoFruitore)){
				throw new ProtocolException(prefixError+" Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: tipo non identificato");
			}
			if(nomeSoggettoFruitore==null || "".equals(nomeSoggettoFruitore)){
				throw new ProtocolException(prefixError+" Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: nome non identificato");
			}
		}
		
		
		// *** leggo porta delegata associata ****
		
		Integer versioneServizioInt = null;
		try{
			if(versioneServizio!=null && USE_VERSION_XML_BEAN.equals(versioneServizio)==false){
				versioneServizioInt = Integer.parseInt(versioneServizio);
			}
		}catch(Exception e){
			throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) nel path contiene una versione errata: "+e.getMessage());
		}
		
		String tipoSoggettoFruitoreKey = (tipoSoggettoFruitore!=null ? tipoSoggettoFruitore : "" );
		String nomeSoggettoFruitoreKey = (nomeSoggettoFruitore!=null ? nomeSoggettoFruitore : "" );
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		Integer versioneKey = (versioneServizioInt!=null ? versioneServizioInt : -1 );
		
		try{
			String idLine = this.readLineId(xml);
			if(idLine==null || "".equals(idLine)){
				throw new Exception("id non contiene valori");
			}
			if(idLine.endsWith("\n")) {
				idLine = idLine.substring(0, idLine.length()-1);
			}
			String [] tmp = idLine.split(" ");
			if(tmp.length<3) {
				throw new Exception("Attesi almeno tre valori separati da spazio (nomeRegola nomePorta isDefault [descrizione])");
			}
			String nomeRegola = tmp[0];
			String nomePorta = tmp[1];
			boolean isDefault = false;
			try {
				isDefault = Boolean.parseBoolean(tmp[2]);
			}catch(Exception e) {
				throw new Exception("Attesi tre valori separati da spazio (nomeRegola nomePorta isDefault) in cui l'ultimo valore di tipo booleano: "+e.getMessage(),e);
			}
			String descrizione = null;
			if(tmp.length>3) {
				String primeTreInfo = nomeRegola +" "+nomePorta+" "+isDefault+" ";
				descrizione = idLine.substring(primeTreInfo.length());
			}
			
			String keyFruitore = ArchiveFruitore.buildKey(tipoSoggettoFruitoreKey, nomeSoggettoFruitoreKey, tipoSoggettoKey, nomeSoggettoKey, tipoServizio, nomeServizio, versioneKey);
			if(archivio.getAccordiFruitori().containsKey(keyFruitore)==false){
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire un mapping con la porta delegata senza fornire la definizione xml della fruizione");
			}
			
			ArchiveFruitore archiveFruitore = archivio.getAccordiFruitori().get(keyFruitore);
			MappingFruizionePortaDelegata mapping = new MappingFruizionePortaDelegata();
			mapping.setNome(nomeRegola);
			mapping.setIdServizio(archiveFruitore.getIdAccordoServizioParteSpecifica());
			mapping.setIdFruitore(archiveFruitore.getIdSoggettoFruitore());
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			mapping.setIdPortaDelegata(idPD);
			mapping.setDefault(isDefault);
			mapping.setDescrizione(descrizione);
			
			if(archiveFruitore.getMappingPorteDelegateAssociate()==null) {
				archiveFruitore.setMappingPorteDelegateAssociate(new ArrayList<>());
			}
			archiveFruitore.getMappingPorteDelegateAssociate().add(mapping);
			
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura csv (mapping fruizione-pd) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	
	public void readAccordoCooperazione(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,
			String nomeFileSenzaAccordo,String nomeAccordo,String versioneAccordo,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione,
			ArchiveVersion archiveVersion,
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties) throws ProtocolException{
		
		Integer versioneAccordoInt = null;
		try{
			if(versioneAccordo!=null && USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
				versioneAccordoInt = Integer.parseInt(versioneAccordo);
			}
		}catch(Exception e){
			throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) nel path contiene una versione errata: "+e.getMessage());
		}
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		Integer versioneKey = (versioneAccordoInt!=null ? versioneAccordoInt : -1 );
		String key = ArchiveAccordoCooperazione.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
		
		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				AccordoCooperazione ac = this.jaxbRegistryDeserializer.readAccordoCooperazione(xml);
				
				// soggetto referente
				if(ac.getSoggettoReferente()==null){
					ac.setSoggettoReferente(new IdSoggetto());
				}
				if(tipoSoggetto!=null){
					if(ac.getSoggettoReferente().getTipo()!=null && !ac.getSoggettoReferente().getTipo().equals(tipoSoggetto)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un soggetto referente con tipo ["+
								ac.getSoggettoReferente().getTipo()+"] differente da quello indicato ["+tipoSoggetto+"] nella directory che contiene la definizione");
					}
				}
				if(nomeSoggetto!=null){
					if(ac.getSoggettoReferente().getNome()!=null && !ac.getSoggettoReferente().getNome().equals(nomeSoggetto)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un soggetto referente con nome ["+
								ac.getSoggettoReferente().getNome()+"] differente da quello indicato ["+nomeSoggetto+"] nella directory che contiene la definizione");
					}
				}
				ac.getSoggettoReferente().setTipo(tipoSoggetto);
				ac.getSoggettoReferente().setNome(nomeSoggetto);
				
				// nome e versione check quello indicato nell'accordo rispetto a quello indicato 
				// - nome della directory (old version)
				// - file idAccordo (new version)
				if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
					String convertName = ZIPUtils.oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(ac.getNome(),false);
					if(!convertName.equals(nomeAccordo)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								ac.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeAccordo+"] nella directory che contiene la definizione");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
						if(versioneAccordoInt!=null){
							if(ac.getVersione()!=null){
								if(ac.getVersione()!=null && ac.getVersione().intValue()!= versioneAccordoInt.intValue()){
									throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
											ac.getVersione()+"] (fileSystemName:"+versioneAccordo+") differente da quella indicato ["+versioneAccordoInt+"] nella directory che contiene la definizione");
								}
							}
							ac.setVersione(versioneAccordoInt);
						}
					}
				}
				else{
					if(!ac.getNome().equals(nomeAccordo)){
						throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
								ac.getNome()+"] differente da quello indicato ["+nomeAccordo+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
					}
					if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
						if(versioneAccordoInt!=null){
							if(ac.getVersione()!=null && ac.getVersione().intValue()!= versioneAccordoInt.intValue()){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
										ac.getVersione()+"] differente da quella indicata ["+versioneAccordo+"] nel file ["+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME+"]");
							}
						}
						ac.setVersione(versioneAccordoInt);
					}
				}
				
				// add
				if(archivio.getAccordiCooperazione().containsKey(key)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un accordo con key ["+key+"]");
				}
				archivio.getAccordiCooperazione().add(key,new ArchiveAccordoCooperazione(ac,idCorrelazione,true));		

			}catch(Exception eDeserializer){
				String xmlString = this.toStringXmlElementForErrorMessage(xml);
				throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (accordo-cooperazione) non valida rispetto allo schema (RegistroServizi): "
						+eDeserializer.getMessage(),eDeserializer);
			}
			
		}
		else{
			
			// recupero archivio precedentemente letto
			AccordoCooperazione ac = null;
			if(archivio.getAccordiCooperazione().containsKey(key)==false){
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di un accordo senza fornire la definizione xml dell'accordo");
			}
			ac = archivio.getAccordiCooperazione().get(key).getAccordoCooperazione();
			
			// allegati
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, mapKeyDocumenti, ac.getAllegatoList());
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				processDocument(nomeFileSenzaAccordo, Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI, archiveVersion, entryName, xml, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, mapKeyDocumenti, ac.getSpecificaSemiformaleList());
				
			}
			
			
			// protocolProperties
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar)){

				List<ProtocolProperty> listPP = new ArrayList<>();
				if(ac.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty protocolProperty : ac.getProtocolPropertyList()) {
						if(protocolProperty.getTipoProprietario()==null) {
							protocolProperty.setTipoProprietario(ProprietariProtocolProperty.ACCORDO_COOPERAZIONE.name());
						}
						listPP.add(protocolProperty);
					}
				}
				processProtocolProperty(nomeFileSenzaAccordo, archiveVersion, entryName, xml, 
						ProprietarioProprietaProtocollo.ACCORDO_COOPERAZIONE, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, 
						null, null, 
						mapKeyProtocolProperties, listPP, null);
				
			}
			
		}
		
	}
	
	private void processDocument(String nomeFileSenzaAccordo,String tipoDir, ArchiveVersion archiveVersion, String entryName, byte[] xml,
			String tipoSoggetto, String nomeSoggetto, String tipoServizio, String nomeServizio, String versioneServizio,
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti, List<Documento> documenti) throws ProtocolException{
		this.processDocument(nomeFileSenzaAccordo, tipoDir, archiveVersion, entryName, xml, tipoSoggetto, nomeSoggetto, 
				tipoServizio+nomeServizio, versioneServizio, mapKeyDocumenti, documenti);
	}
	private void processDocument(String nomeFileSenzaAccordo,String tipoDir, ArchiveVersion archiveVersion, String entryName, byte[] xml,
			String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo,
			Hashtable<String, IdentificativoDocumento> mapKeyDocumenti, List<Documento> documenti) throws ProtocolException{
		String nomeDocumento = nomeFileSenzaAccordo.substring((tipoDir+File.separatorChar).length());
		if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
			
			this.getDocument(documenti, nomeDocumento, null, entryName, archiveVersion).setByteContenuto(xml);
			
		}else{
			String nomeDocumentoSenzaEstensione = nomeDocumento;
			if(nomeDocumento.contains(".")==false){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Per i documenti è attesa una estensione");
			}
			nomeDocumentoSenzaEstensione = nomeDocumento.substring(0,nomeDocumento.lastIndexOf("."));
			String keyDocumento = getKeyDocumento(tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, entryName, nomeDocumentoSenzaEstensione);
			if(nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID)){
				String tmp = new String(xml);
				IdentificativoDocumento identificativoDocumento = new IdentificativoDocumento();
				identificativoDocumento.tipo = tmp.split(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR)[0]; 
				identificativoDocumento.nome = tmp.substring(identificativoDocumento.tipo.length()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR.length());
				// fix file editati a mano
				identificativoDocumento.tipo = identificativoDocumento.tipo.trim();
				identificativoDocumento.nome = identificativoDocumento.nome.trim();
				if(identificativoDocumento.nome.endsWith("\n") && identificativoDocumento.nome.length()>1) {
					identificativoDocumento.nome = identificativoDocumento.nome.substring(0, identificativoDocumento.nome.length()-1);
				}
				mapKeyDocumenti.put(keyDocumento, identificativoDocumento);
			}
			else if(nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT)){
				IdentificativoDocumento identificativoDocumento = mapKeyDocumenti.get(keyDocumento);
				if(identificativoDocumento==null){
					throw new ProtocolException("Elemento ["+entryName+"] errato. Non è stato rilevato precedentemente il corrispettivo file contenente l'identificativo (con estensione '"+
								Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID+"')");
				}
				this.getDocument(documenti, identificativoDocumento.nome, identificativoDocumento.tipo, entryName, archiveVersion).setByteContenuto(xml);
			}
			else {
				throw new ProtocolException("Elemento ["+entryName+"] non atteso.");
			}
		}
	}
	private Documento getDocument(List<Documento> documenti, String nome, String tipo, String entryName, ArchiveVersion archiveVersion) throws ProtocolException{
		
		List<Character> permitPoint = new ArrayList<Character>();
		permitPoint.add('.');
				
		for (Documento documento : documenti) {
			
			String fileName = documento.getFile();
			if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
				fileName = ZIPUtils.oldMethod_convertCharNonPermessiQualsiasiSistemaOperativo(fileName,true,permitPoint);
			}
				
			if(nome.equals(fileName)){
				if(ArchiveVersion.V_UNDEFINED.equals(archiveVersion)){
					return documento;
				}
				else{
					if(tipo.equals(documento.getTipo())){
						return documento;
					}
				}
			}
		}
		throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire un documento di un accordo senza definirlo anche all'interno della definizione xml dell'accordo");
	}
	
	
	
	private void processProtocolProperty(String nomeFileSenzaAccordo,ArchiveVersion archiveVersion, String entryName, byte[] xml,
			ProprietarioProprietaProtocollo proprietario,
			String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties, 
			List<ProtocolProperty> protocolProperties,
			HashMap<String,Long> mapIdToLong) throws ProtocolException{
		String nomePP = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar).length());
		
		String nomePPSenzaEstensione = nomePP;
		if(nomePP.contains(".")==false){
			throw new ProtocolException("Elemento ["+entryName+"] errato. Per le protocol properties è attesa una estensione");
		}
		nomePPSenzaEstensione = nomePP.substring(0,nomePP.lastIndexOf("."));
		String keyProtocolProperty = getKeyProtocolProperty(tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, 
				tipoSoggettoFruitore, nomeSoggettoFruitore, proprietario, nomePPSenzaEstensione);
		if(nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID)){
			String tmp = new String(xml);
			IdentificativoProprietaProtocollo identificativoPP = new IdentificativoProprietaProtocollo();
			identificativoPP.tipo = ProprietariProtocolProperty.valueOf(tmp.split(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR)[0]); 
			int prefixLength = identificativoPP.tipo.name().length()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR.length();
			if(ProprietariProtocolProperty.AZIONE_ACCORDO.equals(identificativoPP.tipo) ||
					ProprietariProtocolProperty.PORT_TYPE.equals(identificativoPP.tipo) ||
					ProprietariProtocolProperty.OPERATION.equals(identificativoPP.tipo) ||
					ProprietariProtocolProperty.RESOURCE.equals(identificativoPP.tipo)) {
				identificativoPP.idProprietario = tmp.split(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR)[1]; 
				prefixLength+=identificativoPP.idProprietario.length()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR.length();
			}
			identificativoPP.nome = tmp.substring(prefixLength);
			mapKeyProtocolProperties.put(keyProtocolProperty, identificativoPP);
		}
		else if(nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT)){
			IdentificativoProprietaProtocollo identificativoPP = mapKeyProtocolProperties.get(keyProtocolProperty);
			if(identificativoPP==null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Non è stato rilevato precedentemente il corrispettivo file contenente l'identificativo (con estensione '"+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID+"')");
			}
			this.getProtocolProperty(proprietario, protocolProperties, 
					identificativoPP.nome, identificativoPP.tipo,
					identificativoPP.idProprietario, mapIdToLong,
					entryName).setByteFile(xml);
		}
		else {
			throw new ProtocolException("Elemento ["+entryName+"] non atteso.");
		}

	}
	private String buildKeyPPAccordoParteComuneItem(ProprietariProtocolProperty tipo,String idProprietario) {
		return tipo.name()+"_"+idProprietario;
	}
	private ProtocolProperty getProtocolProperty(ProprietarioProprietaProtocollo proprietario,
			List<ProtocolProperty> protocolProperties, String nome, ProprietariProtocolProperty tipo, 
			String idProprietario, HashMap<String,Long> mapIdToLong, 
			String entryName) throws ProtocolException{
			
		for (ProtocolProperty pp : protocolProperties) {
			if(pp.getTipoProprietario().equals(tipo.name()) && pp.getName().equals(nome)) {
				if(mapIdToLong==null || ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE.equals(tipo)) {
					return pp;
				}
				else {
					String key = buildKeyPPAccordoParteComuneItem(tipo, idProprietario);
					Long l = mapIdToLong.get(key);
					if(l.longValue() == pp.getIdProprietario().longValue()) {
						return pp;
					}
				}
			}
		}
		throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire una proprietà di protocollo senza definirlo anche all'interno della definizione xml ("+proprietario.name()+")");
	}
	
	
	
	private void processProtocolPropertyConfig(String nomeFileSenzaId,ArchiveVersion archiveVersion, String entryName, byte[] xml,
			ProprietarioProprietaProtocollo proprietario,
			String tipoSoggetto, String nomeSoggetto, String nome,
			Hashtable<String, IdentificativoProprietaProtocollo> mapKeyProtocolProperties, 
			List<org.openspcoop2.core.config.ProtocolProperty> protocolProperties,
			HashMap<String,Long> mapIdToLong) throws ProtocolException{
		String nomePP = nomeFileSenzaId.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES+File.separatorChar).length());
		
		String nomePPSenzaEstensione = nomePP;
		if(nomePP.contains(".")==false){
			throw new ProtocolException("Elemento ["+entryName+"] errato. Per le protocol properties è attesa una estensione");
		}
		nomePPSenzaEstensione = nomePP.substring(0,nomePP.lastIndexOf("."));
		String keyProtocolProperty = getKeyProtocolProperty(tipoSoggetto, nomeSoggetto, nome, null, null, null, proprietario, nomePPSenzaEstensione);
		if(nomeFileSenzaId.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID)){
			String tmp = new String(xml);
			IdentificativoProprietaProtocollo identificativoPP = new IdentificativoProprietaProtocollo();
			identificativoPP.tipo = ProprietariProtocolProperty.valueOf(tmp.split(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR)[0]); 
			int prefixLength = identificativoPP.tipo.name().length()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR.length();
			identificativoPP.nome = tmp.substring(prefixLength);
			mapKeyProtocolProperties.put(keyProtocolProperty, identificativoPP);
		}
		else if(nomeFileSenzaId.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT)){
			IdentificativoProprietaProtocollo identificativoPP = mapKeyProtocolProperties.get(keyProtocolProperty);
			if(identificativoPP==null){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Non è stato rilevato precedentemente il corrispettivo file contenente l'identificativo (con estensione '"+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID+"')");
			}
			this.getProtocolPropertyConfig(proprietario, protocolProperties, 
					identificativoPP.nome, identificativoPP.tipo,
					identificativoPP.idProprietario, mapIdToLong,
					entryName).setByteFile(xml);
		}
		else {
			throw new ProtocolException("Elemento ["+entryName+"] non atteso.");
		}

	}
	private org.openspcoop2.core.config.ProtocolProperty getProtocolPropertyConfig(ProprietarioProprietaProtocollo proprietario,
			List<org.openspcoop2.core.config.ProtocolProperty> protocolProperties, String nome, ProprietariProtocolProperty tipo, 
			String idProprietario, HashMap<String,Long> mapIdToLong, 
			String entryName) throws ProtocolException{
			
		for (org.openspcoop2.core.config.ProtocolProperty pp : protocolProperties) {
			if(pp.getTipoProprietario().equals(tipo.name()) && pp.getName().equals(nome)) {
				return pp;
			}
		}
		throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire una proprietà di protocollo senza definirlo anche all'interno della definizione xml ("+proprietario.name()+")");
	}
	
	
	
	protected String toStringXmlElementForErrorMessage(byte[]xml){
		return xml!=null ? "Xml: ["+new String(xml)+"] \n" : "Xml Undefined. \n";
	}
	
	private String readLineId(byte[] xml) throws IOException {
		// Elimino eventuali \n
		StringReader sr = new StringReader(new String(xml));
		BufferedReader br = new BufferedReader(sr);
		String identificativo = br.readLine();
		br.close();
		sr.close();
		return identificativo;
	}
}

class IdentificativoAccordo{
	
	protected String tipo; // solo per aps
	protected String nome;
	protected String versione;
	
}

class IdentificativoDocumento{
	
	protected String nome;
	protected String tipo;
	
}

class IdentificativoProprietaProtocollo{
	
	protected String nome;
	protected String idProprietario;
	protected ProprietariProtocolProperty tipo;
	
}

class IdentificativoServizioApplicativo{
	
	protected String tipoSoggetto;
	protected String nomeSoggetto;
	protected String nome;
	
}

enum ProprietarioProprietaProtocollo{
	
	SOGGETTO, 
	ACCORDO_COOPERAZIONE,
	ACCORDO_SERVIZIO_PARTE_COMUNE,
	ACCORDO_SERVIZIO_PARTE_SPECIFICA,
	FRUITORE,
	SERVIZIO_APPLICATIVO
	
}
