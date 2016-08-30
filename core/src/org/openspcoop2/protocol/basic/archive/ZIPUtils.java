/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.FileDataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.utils.ConfigurazionePdDUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.information_missing.Openspcoop2;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;


/**
 * Classe utilizzata per lavorare sui package
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ZIPUtils  {

	
	
	protected Logger log = null;
	protected org.openspcoop2.core.registry.utils.serializer.JibxDeserializer jibxRegistryDeserializer = null;
	protected org.openspcoop2.core.config.utils.serializer.JibxDeserializer jibxConfigDeserializer = null;
	protected org.openspcoop2.protocol.information_missing.utils.serializer.JibxDeserializer jibxInformationMissingDeserializer = null;
	
	protected IRegistryReader registryReader;
	
	public ZIPUtils(Logger log,IRegistryReader registryReader){
		this.log = log;
		this.jibxRegistryDeserializer = new org.openspcoop2.core.registry.utils.serializer.JibxDeserializer();
		this.jibxConfigDeserializer = new org.openspcoop2.core.config.utils.serializer.JibxDeserializer();
		this.jibxInformationMissingDeserializer = new org.openspcoop2.protocol.information_missing.utils.serializer.JibxDeserializer();
		this.registryReader = registryReader;
	}
	
	
	
	
	/* ----- Utils  ----- */
	
	public String convertCharNonPermessiQualsiasiSistemaOperativo(String nome){
		return this.convertCharNonPermessiQualsiasiSistemaOperativo(nome, true, null);
	}
	public String convertCharNonPermessiQualsiasiSistemaOperativo(String nome,boolean permitUnderscore){
		return this.convertCharNonPermessiQualsiasiSistemaOperativo(nome, permitUnderscore, null);
	}
	public String convertCharNonPermessiQualsiasiSistemaOperativo(String nome,boolean permitUnderscore,List<Character> permit){
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < nome.length(); i++) {
			if(Character.isLetterOrDigit(nome.charAt(i))){
				bf.append(nome.charAt(i));
			}
			else {
				if(permit!=null){
					// check che sia nella lista dei caratteri permessi
					boolean found = false;
					for (char charPermit : permit) {
						if(charPermit == nome.charAt(i)){
							found = true;
							break;
						}
					}
					if(found){
						bf.append(nome.charAt(i));
						continue;
					}
				}
				
				// Se non e' nella lista dei caratteri permessi, se e' abilitato l'underscore mappo qualsiasi carattere in un '_',
				// altrimenti lo "brucio"
				if(permitUnderscore){
					// sostituisco tutto con _
					bf.append("_");
				}
			}
		}
		return bf.toString();
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
	
	public static final String ID_CORRELAZIONE_DEFAULT = "@PackageOpenSPCoop@";
	
//	public static final List<Character> LIST_CHARACTER_PERMIT_IMPORT_PACKAGES = new ArrayList<Character>();
//	static{
//		LIST_CHARACTER_PERMIT_IMPORT_PACKAGES.add('-');
//		LIST_CHARACTER_PERMIT_IMPORT_PACKAGES.add('.');
//	}
	
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
			
			ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione(ID_CORRELAZIONE_DEFAULT);
			idCorrelazione.setDescrizione(zip.getName()); // come descrizione viene usato il nome dell'archivio zip
			
			// ExtendedInfoManager
			ExtendedInfoManager extendedInfoManager = ExtendedInfoManager.getInstance();
			Hashtable<String, PortaDelegata> mapKeyForExtendedInfo_portaDelegata = new Hashtable<String, PortaDelegata>();
			boolean existsExtendsConfigForPortaDelegata = extendedInfoManager.newInstanceExtendedInfoPortaDelegata()!=null;
			Hashtable<String, PortaApplicativa> mapKeyForExtendedInfo_portaApplicativa = new Hashtable<String, PortaApplicativa>();
			boolean existsExtendsConfigForPortaApplicativa = extendedInfoManager.newInstanceExtendedInfoPortaApplicativa()!=null;
			
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
						
						// ********** configurazione ****************
						if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							if(entryName.contains(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar)){
								this.readConfigurazioneExtended(archivio, bin, xml, entryName, validationDocuments, extendedInfoManager);
							}
							else{
								bin = new ByteArrayInputStream(xml);
								this.readConfigurazione(archivio, bin, xml, entryName, validationDocuments);
							}
						}
						
						// ********** pdd ****************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DOMINIO_DIR+File.separatorChar)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readPortaDominio(archivio, bin, xml, entryName, validationDocuments, idCorrelazione);
						}
						
						// ********** informationMissing ********************
						else if(entryName.equals((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_INFORMATION_MISSING)) ){
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
							this.readInformationMissing(archivio, bin, xml, entryName, validationDocuments);
						}
						
						// ********** soggetti/* ********************
						else if(entryName.startsWith((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+File.separatorChar)) ){
							
							byte[] xml = placeholder.replace(content);
							bin = new ByteArrayInputStream(xml);
														
							String name = entryName.substring((rootDir+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+File.separatorChar).length());
							
							if(name.contains((File.separatorChar+""))==false){
								
								throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
										"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il sogggetto e ulteriori file e/o directory che definiscono gli elementi del soggetto");
								
							}
							else{
								
								// comprendo tipo e nome soggetto
								String tipoNomeSoggetto = name.substring(0,name.indexOf(File.separatorChar));
								if(tipoNomeSoggetto==null || "".equals(tipoNomeSoggetto) || !tipoNomeSoggetto.contains("_")){
									throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
											"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il sogggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>");
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
												"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il sogggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: tipo non identificato");
									}
									if(nomeSoggetto==null || "".equals(nomeSoggetto)){
										throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
												"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il sogggetto. Il nome utilizzato per la directory non e' conforme alla struttura attesa <tipo>_<nome>: nome non identificato");
									}
								}
								
								// comprendo parte restante
								String nomeFile = name.substring((tipoNomeSoggetto.length()+1),name.length());
								if(nomeFile==null){
									throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+
											"] deve essere presenta una ulteriore directory contenente la struttura <tipo>_<nome> che descrive il sogggetto e ulteriori file e/o directory che definiscono gli elementi del soggetto: non sono stati trovati ulteriori file");
								}
								
								if(nomeFile.contains((File.separatorChar+""))==false){
									
									// ------------ soggetto --------------------
									this.readSoggetto(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
									
								}
								else{

									// ------------ servizio applicativo --------------------
									if(nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR+File.separatorChar)) ){
										this.readServizioApplicativo(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, validationDocuments, idCorrelazione);
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
											
											// comprendo nome ed eventuale versione dell'accordo
											String nomeVersioneAccordo = nomeFileAccordo.substring(0,nomeFileAccordo.indexOf(File.separatorChar));
											if(nomeVersioneAccordo==null || "".equals(nomeVersioneAccordo) || "_".equals(nomeVersioneAccordo) || 
													nomeVersioneAccordo.startsWith("_") || nomeVersioneAccordo.endsWith("_")){
												throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
														"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione>] che descrive l'accordo. Il nome utilizzato per la directory non e' conforme alla struttura attesa <nome>[_<versione>]");
											}
											nomeVersioneAccordo = nomeVersioneAccordo.trim();
											String versioneAccordo = null;
											String nomeAccordo = null;
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
											
											// comprendo parte restante
											String nomeFileSenzaAccordo = nomeFileAccordo.substring((nomeVersioneAccordo.length()+1),nomeFileAccordo.length());
											if(nomeFileSenzaAccordo==null){
												throw new ProtocolException("Elemento ["+entryName+"] errato. Dopo la directory ["+directoryAccordo+
														"] deve essere presenta una ulteriore directory contenente la struttura <nome>[_<versione> che descrive l'accordo e ulteriori file e/o directory che definiscono gli elementi dell'accordo: non sono stati trovati ulteriori file");
											}
											
											// ------------ accordo servizio parte comune -------------------
											if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR+File.separatorChar)) ){
												this.readAccordoServizioParteComune(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
														nomeFileSenzaAccordo,nomeAccordo,versioneAccordo,false, validationDocuments, idCorrelazione);
											}
											// ------------ accordo servizio composto -------------------
											else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR+File.separatorChar)) ){
												this.readAccordoServizioParteComune(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
														nomeFileSenzaAccordo,nomeAccordo,versioneAccordo,true, validationDocuments, idCorrelazione);
											}
											// ------------ accordo servizio parte specifica -------------------
											else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar)) ){
												this.readAccordoServizioParteSpecifica(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
														nomeFileSenzaAccordo,nomeAccordo,versioneAccordo, validationDocuments, idCorrelazione);
											}
											// ------------ accordo cooperazione -------------------
											else if( nomeFile.startsWith((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar)) ){
												this.readAccordoCooperazione(archivio, bin, xml, entryName, tipoSoggetto, nomeSoggetto, 
														nomeFileSenzaAccordo,nomeAccordo,versioneAccordo, validationDocuments, idCorrelazione);
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
		// nop
	}
	
	public void readExternalArchive(String rootDir, Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		throw new ProtocolException("Elemento ["+entryName+"] non atteso");
	}
	
	public void readConfigurazione(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			Configurazione configurazione = this.jibxConfigDeserializer.readConfigurazione(xml);
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
	
	public void readPortaDominio(Archive archivio,InputStream bin,byte[]xml,String entryName,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			PortaDominio pdd = this.jibxRegistryDeserializer.readPortaDominio(xml);
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
			informationMissingOp2 = this.jibxInformationMissingDeserializer.readOpenspcoop2(xml);

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
				soggettoRegistroServizi = this.jibxRegistryDeserializer.readSoggetto(xml);
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
				soggettoConfigurazione = this.jibxConfigDeserializer.readSoggetto(xml);
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
	
	public void readServizioApplicativo(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			ServizioApplicativo sa = this.jibxConfigDeserializer.readServizioApplicativo(xml);
			sa.setTipoSoggettoProprietario(tipoSoggetto);
			sa.setNomeSoggettoProprietario(nomeSoggetto);
			String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
			String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
			String key = ArchiveServizioApplicativo.buildKey(tipoSoggettoKey, nomeSoggettoKey, sa.getNome());
			if(archivio.getServiziApplicativi().containsKey(key)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un servizio applicativo con key ["+key+"]");
			}
			archivio.getServiziApplicativi().add(key,new ArchiveServizioApplicativo(sa,idCorrelazione,true));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (servizio-applicativo) non valida rispetto allo schema (ConfigurazionePdD): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public PortaDelegata readPortaDelegata(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		try{
			if(validationDocuments){
				org.openspcoop2.core.config.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			PortaDelegata pd = this.jibxConfigDeserializer.readPortaDelegata(xml);
			pd.setTipoSoggettoProprietario(tipoSoggetto);
			pd.setNomeSoggettoProprietario(nomeSoggetto);
			String nome = pd.getNome();
			if(pd.getLocation()!=null && !"".equals(pd.getLocation()))
				nome = pd.getLocation();
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
			PortaApplicativa pa = this.jibxConfigDeserializer.readPortaApplicativa(xml);
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
			String nomeFileSenzaAccordo,String nomeAccordo,String versioneAccordo, boolean servizioComposto,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		String key = null;
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		String versioneKey = (versioneAccordo!=null ? versioneAccordo : "" );
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
				AccordoServizioParteComune aspc = this.jibxRegistryDeserializer.readAccordoServizioParteComune(xml);
				
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
				
				// nome e versione
				//String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(aspc.getNome(),false,LIST_CHARACTER_PERMIT_IMPORT_PACKAGES);
				String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(aspc.getNome(),false);
				if(!convertName.equals(nomeAccordo)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
							aspc.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeAccordo+"] nella directory che contiene la definizione");
				}
				if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
					if(versioneAccordo!=null){
						if(aspc.getVersione()!=null && !"".equals(aspc.getVersione())){
							String convertVersion = convertCharNonPermessiQualsiasiSistemaOperativo(aspc.getVersione(),false);
							if(aspc.getVersione()!=null && !convertVersion.equals(versioneAccordo)){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
										aspc.getVersione()+"] (fileSystemName:"+convertVersion+") differente da quella indicato ["+versioneAccordo+"] nella directory che contiene la definizione");
							}
						}
					}
					aspc.setVersione(versioneAccordo);
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
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar).length());
				this.getDocument(as.getAllegatoList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar).length());
				this.getDocument(as.getSpecificaSemiformaleList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheCoordinamento
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar)){
				
				if(servizioComposto==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di specifica di coordinamento per un accordo non registrato come servizio composto");
				}
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar).length());
				this.getDocument(as.getServizioComposto().getSpecificaCoordinamentoList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
		}
		
	}
	
	public void readAccordoServizioParteSpecifica(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,
			String nomeFileSenzaAccordo,String nomeAccordo,String versioneAccordo,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		String versioneKey = (versioneAccordo!=null ? versioneAccordo : "" );
		String key = ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
		
		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				AccordoServizioParteSpecifica asps = this.jibxRegistryDeserializer.readAccordoServizioParteSpecifica(xml);
				
				// soggetto erogatore
				asps.getServizio().setTipoSoggettoErogatore(tipoSoggetto);
				asps.getServizio().setNomeSoggettoErogatore(nomeSoggetto);
				
				// nome e versione
				//String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(asps.getNome(),false,LIST_CHARACTER_PERMIT_IMPORT_PACKAGES);
				String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(asps.getNome(),false);
				if(!convertName.equals(nomeAccordo)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
							asps.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeAccordo+"] nella directory che contiene la definizione");
				}
				if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
					if(versioneAccordo!=null){
						if(asps.getVersione()!=null && !"".equals(asps.getVersione())){
							String convertVersion = convertCharNonPermessiQualsiasiSistemaOperativo(asps.getVersione(),false);
							if(asps.getVersione()!=null && !convertVersion.equals(versioneAccordo)){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
										asps.getVersione()+"] (fileSystemName:"+convertVersion+") differente da quella indicato ["+versioneAccordo+"] nella directory che contiene la definizione");
							}
						}
					}
					asps.setVersione(versioneAccordo);
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
			AccordoServizioParteSpecifica as = null;
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar)==false){
				if(archivio.getAccordiServizioParteSpecifica().containsKey(key)==false){
					throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire dei documenti di un accordo senza fornire la definizione xml dell'accordo");
				}
				as = archivio.getAccordiServizioParteSpecifica().get(key).getAccordoServizioParteSpecifica();
			}
			
			// wsdl
			if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar)){
				
				if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL)){
					as.setByteWsdlImplementativoErogatore(xml);
				}
				else if(nomeFileSenzaAccordo.equalsIgnoreCase(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL)){
					as.setByteWsdlImplementativoFruitore(xml);
				}
				
			}
			
			// allegati
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar).length());
				this.getDocument(as.getAllegatoList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar).length());
				this.getDocument(as.getSpecificaSemiformaleList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheLivelliServizio
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar).length());
				this.getDocument(as.getSpecificaLivelloServizioList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheSicurezza
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar).length());
				this.getDocument(as.getSpecificaSicurezzaList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// fruitori (politiche di sicurezza)
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar) &&
					nomeFileSenzaAccordo.endsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI)){
				
				this.readAccordoServizioParteSpecifica_Fruitore_ServiziApplicativiAutorizzati(archivio, bin, xml, entryName, 
						nomeFileSenzaAccordo,
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, 
						validationDocuments, idCorrelazione);
			}
			
			// fruitori
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar)){
				this.readAccordoServizioParteSpecifica_Fruitore(archivio, bin, xml, entryName, 
						tipoSoggetto, nomeSoggetto, nomeAccordo, versioneAccordo, 
						validationDocuments, idCorrelazione);
			}
			
		}
		
	}
	
	public void readAccordoServizioParteSpecifica_Fruitore(Archive archivio,InputStream bin,byte[]xml,String entryName,
			String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo,
			boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		String versioneKey = (versioneAccordo!=null ? versioneAccordo : "" );
		
		try{
			if(validationDocuments){
				org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
			}
			
			if(USE_VERSION_XML_BEAN.equals(versioneAccordo)){
				// devo recuperare la versione dell'accordo
				String keyAccordo = ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
				versioneKey = archivio.getAccordiServizioParteSpecifica().get(keyAccordo).getAccordoServizioParteSpecifica().getVersione();
				versioneAccordo = versioneKey;
			}
			
			Fruitore fruitore = this.jibxRegistryDeserializer.readFruitore(xml);
			String keyFruitore = ArchiveFruitore.buildKey(fruitore.getTipo(), fruitore.getNome(), tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
			if(archivio.getAccordiFruitori().containsKey(keyFruitore)){
				throw new ProtocolException("Elemento ["+entryName+"] errato. Risulta esistere piu' di un fruitore con key ["+keyFruitore+"]");
			}
			
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValuesWithoutCheck(nomeAccordo, tipoSoggetto, nomeSoggetto, versioneAccordo);
			archivio.getAccordiFruitori().add(keyFruitore,new ArchiveFruitore(idAccordo,fruitore,idCorrelazione,true));
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura xml (fruitore) non valida rispetto allo schema (RegistroServizi): "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	public void readAccordoServizioParteSpecifica_Fruitore_ServiziApplicativiAutorizzati(Archive archivio,InputStream bin,byte[]xml,String entryName,
			String nomeFileSenzaAccordo,
			String tipoSoggetto, String nomeSoggetto, String nomeAccordo, String versioneAccordo,
			boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		// *** comprendo tipo e nome fruitore ****
		
		String prefixError = "Elemento ["+entryName+"] errato. Dopo la directory ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+
				"] il file ["+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI+
				"] deve essere contenuto in una directory definita tramite la struttura <tipo>_<nome> che descrive il sogggetto fruitore.";
		
		// comprendo tipo e nome soggetto
		String tipoNomeSoggettoFruitore = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar).length(), 
				nomeFileSenzaAccordo.length()-(File.separatorChar+Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI).length());
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
		
		
		// *** leggo lista servizi applicativi autorizzati ****
		
		String tipoSoggettoFruitoreKey = (tipoSoggettoFruitore!=null ? tipoSoggettoFruitore : "" );
		String nomeSoggettoFruitoreKey = (nomeSoggettoFruitore!=null ? nomeSoggettoFruitore : "" );
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		String versioneKey = (versioneAccordo!=null ? versioneAccordo : "" );
		
		try{
			String csvLine = new String(xml);
			String [] line = csvLine.split(",");
			if(line==null || line.length<=0){
				throw new Exception("csv non contiene valori");
			}
			
			String keyFruitore = ArchiveFruitore.buildKey(tipoSoggettoFruitoreKey, nomeSoggettoFruitoreKey, tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
			if(archivio.getAccordiFruitori().containsKey(keyFruitore)==false){
				throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire la lista dei serviziApplicativiAutorizzati senza fornire la definizione xml della fruizione");
			}
			
			ArchiveFruitore archiveFruitore = archivio.getAccordiFruitori().get(keyFruitore);
			for (int i = 0; i < line.length; i++) {
				archiveFruitore.getServiziApplicativiAutorizzati().add(line[i].trim());
			}
			
		}catch(Exception eDeserializer){
			String xmlString = this.toStringXmlElementForErrorMessage(xml);
			throw new ProtocolException(xmlString+"Elemento ["+entryName+"] contiene una struttura csv (serviziApplicativiAutorizzati) non valida: "
					+eDeserializer.getMessage(),eDeserializer);
		}
	}
	
	
	public void readAccordoCooperazione(Archive archivio,InputStream bin,byte[]xml,String entryName,String tipoSoggetto,String nomeSoggetto,
			String nomeFileSenzaAccordo,String nomeAccordo,String versioneAccordo,boolean validationDocuments, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		String tipoSoggettoKey = (tipoSoggetto!=null ? tipoSoggetto : "" );
		String nomeSoggettoKey = (nomeSoggetto!=null ? nomeSoggetto : "" );
		String versioneKey = (versioneAccordo!=null ? versioneAccordo : "" );
		String key = ArchiveAccordoCooperazione.buildKey(tipoSoggettoKey, nomeSoggettoKey, nomeAccordo, versioneKey);
		
		if(nomeFileSenzaAccordo.contains((File.separatorChar+""))==false){
			
			// definizione dell'accordo
			try{
				if(validationDocuments){
					org.openspcoop2.core.registry.utils.XSDValidator.getXSDValidator(this.log).valida(bin);
				}
				AccordoCooperazione ac = this.jibxRegistryDeserializer.readAccordoCooperazione(xml);
				
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
				
				// nome e versione
				//String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(ac.getNome(),false,LIST_CHARACTER_PERMIT_IMPORT_PACKAGES);
				String convertName = convertCharNonPermessiQualsiasiSistemaOperativo(ac.getNome(),false);
				if(!convertName.equals(nomeAccordo)){
					throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene un nome ["+
							ac.getNome()+"] (fileSystemName:"+convertName+") differente da quello indicato ["+nomeAccordo+"] nella directory che contiene la definizione");
				}
				if(USE_VERSION_XML_BEAN.equals(versioneAccordo)==false){
					if(versioneAccordo!=null){
						if(ac.getVersione()!=null && !"".equals(ac.getVersione())){
							String convertVersion = convertCharNonPermessiQualsiasiSistemaOperativo(ac.getVersione(),false);
							if(ac.getVersione()!=null && !convertVersion.equals(versioneAccordo)){
								throw new ProtocolException("Elemento ["+entryName+"] errato. La definizione xml dell'accordo (RegistroServizi) contiene una versione ["+
										ac.getVersione()+"] (fileSystemName:"+convertVersion+") differente da quella indicato ["+versioneAccordo+"] nella directory che contiene la definizione");
							}
						}
					}
					ac.setVersione(versioneAccordo);
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
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar).length());
				this.getDocument(ac.getAllegatoList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
			// specificheSemiformali
			else if(nomeFileSenzaAccordo.startsWith(Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar)){
				
				String nomeAllegato = nomeFileSenzaAccordo.substring((Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar).length());
				this.getDocument(ac.getSpecificaSemiformaleList(), nomeAllegato, entryName).setByteContenuto(xml);
				
			}
			
		}
		
	}
	
	private Documento getDocument(List<Documento> documenti, String nome, String entryName) throws ProtocolException{
		
		List<Character> permitPoint = new ArrayList<Character>();
		permitPoint.add('.');
				
		for (Documento documento : documenti) {
			
			String fileName = documento.getFile();
			fileName = this.convertCharNonPermessiQualsiasiSistemaOperativo(fileName,true,permitPoint);
			//fileName = this.convertCharNonPermessiQualsiasiSistemaOperativo(fileName,true,LIST_CHARACTER_PERMIT_IMPORT_PACKAGES);
			
			if(nome.equals(fileName)){
				return documento;
			}
		}
		throw new ProtocolException("Elemento ["+entryName+"] non atteso. Non e' possibile fornire un documento di un accordo senza definirlo anche all'interno della definizione xml dell'accordo");
	}
	
	protected String toStringXmlElementForErrorMessage(byte[]xml){
		return xml!=null ? "Xml: ["+new String(xml)+"] \n" : "Xml Undefined. \n";
	}
}