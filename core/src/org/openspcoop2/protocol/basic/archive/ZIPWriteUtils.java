/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.constants.ArchiveVersion;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

/**     
 * ZIPWriteUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZIPWriteUtils {

	protected Logger log = null;
	
	protected IRegistryReader registryReader;
	protected IConfigIntegrationReader configIntegrationReader;

	private org.openspcoop2.core.registry.utils.serializer.JibxSerializer jibxRegistrySerializer = null;
	private org.openspcoop2.core.config.utils.serializer.JibxSerializer jibxConfigSerializer = null;
	private org.openspcoop2.core.registry.utils.CleanerOpenSPCoop2Extensions cleanerOpenSPCoop2ExtensionsRegistry = null;
	private org.openspcoop2.core.config.utils.CleanerOpenSPCoop2Extensions cleanerOpenSPCoop2ExtensionsConfig = null;
	
	
	public ZIPWriteUtils(Logger log,IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader) throws ProtocolException{
		this.log = log;
		
		this.registryReader = registryReader;
		this.configIntegrationReader = configIntegrationReader;
		
		this.jibxRegistrySerializer = new org.openspcoop2.core.registry.utils.serializer.JibxSerializer();
		this.jibxConfigSerializer = new org.openspcoop2.core.config.utils.serializer.JibxSerializer();
		this.cleanerOpenSPCoop2ExtensionsRegistry = new org.openspcoop2.core.registry.utils.CleanerOpenSPCoop2Extensions();
		this.cleanerOpenSPCoop2ExtensionsConfig = new org.openspcoop2.core.config.utils.CleanerOpenSPCoop2Extensions();
		
	}
	
	
	

	
	
	
	// ---------- Marshall ---------------- 
	
	public void generateArchive(Archive archive,File file) throws ProtocolException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			generateArchive(archive,fout);
			fout.flush();
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void generateArchive(Archive archive,String fileName) throws ProtocolException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			generateArchive(archive,fout);
			fout.flush();
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] generateArchive(Archive archive) throws ProtocolException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			generateArchive(archive,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}


	
	private void write(ZipOutputStream zipOut,String elemento,Object idElemento,boolean registry,Object object) throws ProtocolException{
		try{
			Method method = null;
			byte[]bytes = null;
			if(registry){
				method = this.jibxRegistrySerializer.getClass().getMethod("toByteArray", object.getClass());
				bytes = (byte[]) method.invoke(this.jibxRegistrySerializer, object);
			}else{
				method = this.jibxConfigSerializer.getClass().getMethod("toByteArray", object.getClass());
				bytes = (byte[]) method.invoke(this.jibxConfigSerializer, object);
			}
			zipOut.write(bytes);
		}catch(Exception e){
			String xml = null;
			try{
				Method method = object.getClass().getMethod("toXml_Jaxb");
				xml = (String) method.invoke(object);
			}catch(Exception eDebug){
				this.log.error("Errore durante il recupero della struttura xml: "+eDebug,eDebug);
				throw new ProtocolException("["+elemento+"]["+idElemento+"]: "+e.getMessage(),e);
			}
			throw new ProtocolException("["+elemento+"]["+idElemento+"] ("+xml+"): "+e.getMessage(),e);
		}
	}
	
	public void generateArchive(Archive archive,OutputStream out) throws ProtocolException{
		
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootPackageDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			// ExtendedInfoManager
			ExtendedInfoManager extendedInfoManager = ExtendedInfoManager.getInstance();
			
			// version
			String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_VERSION_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
			zipOut.write(ArchiveVersion.getContentFileVersion(org.openspcoop2.utils.Costanti.OPENSPCOOP2_PRODUCT_VERSION).getBytes());
			
			// configurazione
			if(archive.getConfigurazionePdD()!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_FILE_NAME;
				zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
				Configurazione configurazionePdD = archive.getConfigurazionePdD();
				this.cleanerOpenSPCoop2ExtensionsConfig.clean(configurazionePdD);
				write(zipOut, "ConfigurazionePdD", "", false, configurazionePdD);
			}
			
			// extendedConfigurazione
			if(archive.getConfigurazionePdD()!=null && archive.getConfigurazionePdD().sizeExtendedInfoList()>0){
				String prefix = Costanti.OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar;
				for (int i = 0; i < archive.getConfigurazionePdD().sizeExtendedInfoList(); i++) {
					nomeFile = prefix+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_NAME+(i+1)+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT;
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					String className = null;
					try{
						Object o = archive.getConfigurazionePdD().getExtendedInfo(i);
						className = o.getClass().getName();
						zipOut.write(extendedInfoManager.newInstanceExtendedInfoConfigurazione().serialize(this.log,archive.getConfigurazionePdD(),o));
					}catch(Exception e){
						throw new ProtocolException("[ConfigurazionePdDExt][Posizione-"+i+"] ("+className+"): "+e.getMessage(),e);
					}
				}
			}
			
			// porteDominio
			if(archive.getPdd()!=null && archive.getPdd().size()>0){
				for (int i = 0; i < archive.getPdd().size(); i++) {
					ArchivePdd archivePdd = archive.getPdd().get(i);
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DOMINIO_DIR+File.separatorChar+
							ZIPUtils.convertNameToSistemaOperativoCompatible(archivePdd.getNomePdd())+".xml";
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					PortaDominio pdd = archivePdd.getPortaDominio();
					this.cleanerOpenSPCoop2ExtensionsRegistry.clean(pdd);
					write(zipOut, "PortaDominio", archivePdd.getNomePdd(), true, pdd);
				}
			}
			
			// ruoli
			if(archive.getRuoli()!=null && archive.getRuoli().size()>0){
				for (int i = 0; i < archive.getRuoli().size(); i++) {
					ArchiveRuolo archiveRuolo = archive.getRuoli().get(i);
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_RUOLI_DIR+File.separatorChar+
							ZIPUtils.convertNameToSistemaOperativoCompatible(archiveRuolo.getIdRuolo().getNome())+".xml";
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					Ruolo ruolo = archiveRuolo.getRuolo();
					this.cleanerOpenSPCoop2ExtensionsRegistry.clean(ruolo);
					write(zipOut, "Ruolo", archiveRuolo.getIdRuolo().getNome(), true, ruolo);
				}
			}
			
			// soggetti
			Hashtable<String, Archive> archiveMapIntoSoggetti = archiveMapIntoSoggetti(archive);
			Enumeration<String> keys = archiveMapIntoSoggetti.keys();
			List<String> listaSoggetti = new ArrayList<String>();
			while (keys.hasMoreElements()) {
				listaSoggetti.add(keys.nextElement());
			}
			java.util.Collections.sort(listaSoggetti);
			for (String idSoggettoAsString : listaSoggetti) {
				
				IDSoggetto idSoggetto = new IDSoggetto(idSoggettoAsString.split("/")[0], idSoggettoAsString.split("/")[1]);
				
				Archive archiveListaOggettiSoggetto = archiveMapIntoSoggetti.get(idSoggettoAsString);
				
				// il converName sui soggetti serve a poco, visto che devono essere simpleName (solo caratteri e numeri)
				String rootDir = rootPackageDir + Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR+File.separatorChar+
						ZIPUtils.convertNameToSistemaOperativoCompatible(idSoggetto.getTipo())+"_"+
						ZIPUtils.convertNameToSistemaOperativoCompatible(idSoggetto.getNome())+File.separatorChar;
				
				// definizione soggetto
				if(archiveListaOggettiSoggetto.getSoggetti()!=null && archiveListaOggettiSoggetto.getSoggetti().size()>0){
					
					// puo' esistere al massimo una definizione del soggetto, avendo fatto una group by logica.
					ArchiveSoggetto archiveSoggetto = archiveListaOggettiSoggetto.getSoggetti().get(0);
					
					if(archiveSoggetto.getSoggettoRegistro()!=null){
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_REGISTRO;
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						org.openspcoop2.core.registry.Soggetto soggettoRegistro = archiveSoggetto.getSoggettoRegistro();
						this.cleanerOpenSPCoop2ExtensionsRegistry.clean(soggettoRegistro);
						write(zipOut, "SoggettoRegistro", archiveSoggetto.getIdSoggetto(), true, soggettoRegistro);
					}
					if(archiveSoggetto.getSoggettoConfigurazione()!=null){
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_CONFIG;
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						org.openspcoop2.core.config.Soggetto soggettoConfigurazione = archiveSoggetto.getSoggettoConfigurazione();
						this.cleanerOpenSPCoop2ExtensionsConfig.clean(soggettoConfigurazione);
						write(zipOut, "SoggettoConfigurazione", archiveSoggetto.getIdSoggetto(), false, soggettoConfigurazione);
					}
				}
				
				// serviziApplicativi
				if(archiveListaOggettiSoggetto.getServiziApplicativi()!=null && archiveListaOggettiSoggetto.getServiziApplicativi().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getServiziApplicativi().size(); i++) {
						ArchiveServizioApplicativo archiveServizioApplicativo = archiveListaOggettiSoggetto.getServiziApplicativi().get(i);
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR+File.separatorChar+
								ZIPUtils.convertNameToSistemaOperativoCompatible(archiveServizioApplicativo.getIdServizioApplicativo().getNome())+".xml";
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						ServizioApplicativo servizioApplicativo = archiveServizioApplicativo.getServizioApplicativo();
						this.cleanerOpenSPCoop2ExtensionsConfig.clean(servizioApplicativo);
						write(zipOut, "ServizioApplicativo", archiveServizioApplicativo.getIdServizioApplicativo(), false, servizioApplicativo);
					}
				}
				
				// porteDelegate
				if(archiveListaOggettiSoggetto.getPorteDelegate()!=null && archiveListaOggettiSoggetto.getPorteDelegate().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getPorteDelegate().size(); i++) {
						
						// portaDelegata
						ArchivePortaDelegata archivePortaDelegata = archiveListaOggettiSoggetto.getPorteDelegate().get(i);
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DELEGATE_DIR+File.separatorChar+
								ZIPUtils.convertNameToSistemaOperativoCompatible(archivePortaDelegata.getIdPortaDelegata().getNome())+".xml";
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						PortaDelegata portaDelegata = archivePortaDelegata.getPortaDelegata();
						this.cleanerOpenSPCoop2ExtensionsConfig.clean(portaDelegata);
						write(zipOut, "PortaDelegata", archivePortaDelegata.getIdPortaDelegata(), false, portaDelegata);
						
						// extended porteDelegata
						if(archivePortaDelegata.getPortaDelegata()!=null && archivePortaDelegata.getPortaDelegata().sizeExtendedInfoList()>0){
							String prefix = Costanti.OPENSPCOOP2_ARCHIVE_PORTE_DELEGATE_DIR+File.separatorChar+
									Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar+
									ZIPUtils.convertNameToSistemaOperativoCompatible(archivePortaDelegata.getIdPortaDelegata().getNome())+
									File.separatorChar;
							for (int k = 0; k < archivePortaDelegata.getPortaDelegata().sizeExtendedInfoList(); k++) {
								nomeFile = prefix+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_NAME+(k+1)+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT;
								zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
								String className = null;
								try{
									Object o = archivePortaDelegata.getPortaDelegata().getExtendedInfo(k);
									className = o.getClass().getName();
									zipOut.write(extendedInfoManager.newInstanceExtendedInfoPortaDelegata().serialize(this.log,archivePortaDelegata.getPortaDelegata(),o));
								}catch(Exception e){
									throw new ProtocolException("[PortaDelegataExt]["+archivePortaDelegata.getIdPortaDelegata()+"][Posizione-"+i+"] ("+className+"): "+e.getMessage(),e);
								}
							}
						}
					}
				}
				
				// porteApplicative
				if(archiveListaOggettiSoggetto.getPorteApplicative()!=null && archiveListaOggettiSoggetto.getPorteApplicative().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getPorteApplicative().size(); i++) {
						
						// portaApplicativa
						ArchivePortaApplicativa archivePortaApplicativa = archiveListaOggettiSoggetto.getPorteApplicative().get(i);
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_PORTE_APPLICATIVE_DIR+File.separatorChar+
								ZIPUtils.convertNameToSistemaOperativoCompatible(archivePortaApplicativa.getIdPortaApplicativa().getNome())+".xml";
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						PortaApplicativa portaApplicativa = archivePortaApplicativa.getPortaApplicativa();
						this.cleanerOpenSPCoop2ExtensionsConfig.clean(portaApplicativa);
						write(zipOut, "PortaApplicativa", archivePortaApplicativa.getIdPortaApplicativa(), false, portaApplicativa);
						
						// extended portaApplicativa
						if(archivePortaApplicativa.getPortaApplicativa()!=null && archivePortaApplicativa.getPortaApplicativa().sizeExtendedInfoList()>0){
							String prefix = Costanti.OPENSPCOOP2_ARCHIVE_PORTE_APPLICATIVE_DIR+File.separatorChar+
									Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_DIR+File.separatorChar+
									ZIPUtils.convertNameToSistemaOperativoCompatible(archivePortaApplicativa.getIdPortaApplicativa().getNome())+
									File.separatorChar;
							for (int k = 0; k < archivePortaApplicativa.getPortaApplicativa().sizeExtendedInfoList(); k++) {
								nomeFile = prefix+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_NAME+(k+1)+Costanti.OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT;
								zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
								String className = null;
								try{
									Object o = archivePortaApplicativa.getPortaApplicativa().getExtendedInfo(k);
									className = o.getClass().getName();
									zipOut.write(extendedInfoManager.newInstanceExtendedInfoPortaApplicativa().serialize(this.log,archivePortaApplicativa.getPortaApplicativa(),o));
								}catch(Exception e){
									throw new ProtocolException("[PortaApplicativaExt]["+archivePortaApplicativa.getIdPortaApplicativa()+"][Posizione-"+i+"] ("+className+"): "+e.getMessage(),e);
								}
							}
						}
					}
				}
				
				// accordiServizioParteComune
				if(archiveListaOggettiSoggetto.getAccordiServizioParteComune()!=null && archiveListaOggettiSoggetto.getAccordiServizioParteComune().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getAccordiServizioParteComune().size(); i++) {
						ArchiveAccordoServizioParteComune archiveAccordo = archiveListaOggettiSoggetto.getAccordiServizioParteComune().get(i);
						AccordoServizioParteComune accordo = archiveAccordo.getAccordoServizioParteComune();
						IDAccordo idAccordo = archiveAccordo.getIdAccordoServizioParteComune();
						this.saveAccordo(accordo, idAccordo, false, rootDir, zipOut);
					}
				}
				
				// accordiServizioParteSpecifica
				Hashtable<String, String> map_IdApsScritti_nomeFileSystem = new Hashtable<>();
				if(archiveListaOggettiSoggetto.getAccordiServizioParteSpecifica()!=null && archiveListaOggettiSoggetto.getAccordiServizioParteSpecifica().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getAccordiServizioParteSpecifica().size(); i++) {
						ArchiveAccordoServizioParteSpecifica archiveAccordo = archiveListaOggettiSoggetto.getAccordiServizioParteSpecifica().get(i);
						AccordoServizioParteSpecifica accordo = archiveAccordo.getAccordoServizioParteSpecifica();
						IDServizio idAccordo = archiveAccordo.getIdAccordoServizioParteSpecifica();
						this.saveAccordo(accordo, idAccordo, archiveAccordo.getIdPorteApplicativeAssociate(), rootDir, zipOut, map_IdApsScritti_nomeFileSystem);
					}
				}
				
				// accordiCooperazione
				if(archiveListaOggettiSoggetto.getAccordiCooperazione()!=null && archiveListaOggettiSoggetto.getAccordiCooperazione().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getAccordiCooperazione().size(); i++) {
						ArchiveAccordoCooperazione archiveAccordo = archiveListaOggettiSoggetto.getAccordiCooperazione().get(i);
						AccordoCooperazione accordo = archiveAccordo.getAccordoCooperazione();
						IDAccordoCooperazione idAccordo = archiveAccordo.getIdAccordoCooperazione();
						this.saveAccordo(accordo, idAccordo, rootDir, zipOut);
					}
				}
				
				// accordiServizioComposto
				if(archiveListaOggettiSoggetto.getAccordiServizioComposto()!=null && archiveListaOggettiSoggetto.getAccordiServizioComposto().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getAccordiServizioComposto().size(); i++) {
						ArchiveAccordoServizioParteComune archiveAccordo = archiveListaOggettiSoggetto.getAccordiServizioComposto().get(i);
						AccordoServizioParteComune accordo = archiveAccordo.getAccordoServizioParteComune();
						IDAccordo idAccordo = archiveAccordo.getIdAccordoServizioParteComune();
						this.saveAccordo(accordo, idAccordo, true, rootDir, zipOut);
					}
				}

				// accordiFruitori
				if(archiveListaOggettiSoggetto.getAccordiFruitori()!=null && archiveListaOggettiSoggetto.getAccordiFruitori().size()>0){
					for (int i = 0; i < archiveListaOggettiSoggetto.getAccordiFruitori().size(); i++) {
						ArchiveFruitore archiveFruitore = archiveListaOggettiSoggetto.getAccordiFruitori().get(i);
						
						String dirAsps = null;
						
						String idAps = archiveFruitore.getIdAccordoServizioParteSpecifica().getNome()+" "+archiveFruitore.getIdAccordoServizioParteSpecifica().getVersione();
						if(map_IdApsScritti_nomeFileSystem.containsKey(idAps)==false){
							
							dirAsps=Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar+
									ZIPUtils.convertNameToSistemaOperativoCompatible(archiveFruitore.getIdAccordoServizioParteSpecifica().getNome())+"_"+
									archiveFruitore.getIdAccordoServizioParteSpecifica().getVersione()+
									File.separatorChar;
							
							// serializzo identificativo accordo servizio parte specifica (servira' per il fruitore)
							nomeFile = dirAsps + Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME;
							zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
							String identificativo = idAps;
							zipOut.write(identificativo.getBytes());
							
							map_IdApsScritti_nomeFileSystem.put(idAps,dirAsps);
						}
						else{
							dirAsps = map_IdApsScritti_nomeFileSystem.get(idAps);
						}
						
						// il converName sui soggetti serve a poco, visto che devono essere simpleName (solo caratteri e numeri)
						String nomeFruitore = dirAsps +
								Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar+
								ZIPUtils.convertNameToSistemaOperativoCompatible(archiveFruitore.getIdSoggettoFruitore().getTipo())+"_"+
								ZIPUtils.convertNameToSistemaOperativoCompatible(archiveFruitore.getIdSoggettoFruitore().getNome());
						nomeFile = nomeFruitore+".xml";
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						Fruitore fruitore = archiveFruitore.getFruitore();						
						this.cleanerOpenSPCoop2ExtensionsRegistry.clean(fruitore);
						write(zipOut, "Fruitore", archiveFruitore.getIdSoggettoFruitore().toString()+" di "+
								archiveFruitore.getIdAccordoServizioParteSpecifica().toString(), true, fruitore);
						
						// PortaDelegata Associata
						if(archiveFruitore.getIdPorteDelegateAssociate()!=null){
							for (int j = 0; j < archiveFruitore.getIdPorteDelegateAssociate().size(); j++) {
								String nomeFilePDAssociata = nomeFruitore+File.separatorChar+
										(j+1)+
										Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_MAPPING_PD_SUFFIX;
								zipOut.putNextEntry(new ZipEntry(rootDir+nomeFilePDAssociata));
								zipOut.write(archiveFruitore.getIdPorteDelegateAssociate().get(j).getNome().getBytes());	
							}
						}
					}
				}
				
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	private Hashtable<String, Archive> archiveMapIntoSoggetti(Archive archive) throws ProtocolException{
		
		Hashtable<String, Archive> archiveMapIntoSoggetti = new Hashtable<String, Archive>();
		
		// raccolgo soggetti
		if(archive.getSoggetti()!=null && archive.getSoggetti().size()>0){
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto archiveSoggetto = archive.getSoggetti().get(i);
				IDSoggetto idSoggetto = archiveSoggetto.getIdSoggetto();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getSoggetti().add(archiveSoggetto);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo serviziApplicativi
		if(archive.getServiziApplicativi()!=null && archive.getServiziApplicativi().size()>0){
			for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
				ArchiveServizioApplicativo archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				IDSoggetto idSoggetto = archiveServizioApplicativo.getIdSoggettoProprietario();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getServiziApplicativi().add(archiveServizioApplicativo);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo porteDelegate
		if(archive.getPorteDelegate()!=null && archive.getPorteDelegate().size()>0){
			for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
				ArchivePortaDelegata archivePortaDelegata = archive.getPorteDelegate().get(i);
				IDSoggetto idSoggetto = archivePortaDelegata.getIdSoggettoProprietario();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getPorteDelegate().add(archivePortaDelegata);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo porteApplicative
		if(archive.getPorteApplicative()!=null && archive.getPorteApplicative().size()>0){
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				ArchivePortaApplicativa archivePortaApplicativa = archive.getPorteApplicative().get(i);
				IDSoggetto idSoggetto = archivePortaApplicativa.getIdSoggettoProprietario();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getPorteApplicative().add(archivePortaApplicativa);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo accordiServizioParteComune
		if(archive.getAccordiServizioParteComune()!=null && archive.getAccordiServizioParteComune().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				ArchiveAccordoServizioParteComune archiveAccordo = archive.getAccordiServizioParteComune().get(i);
				IDSoggetto idSoggetto = archiveAccordo.getIdSoggettoReferente();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getAccordiServizioParteComune().add(archiveAccordo);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo accordiServizioComposto
		if(archive.getAccordiServizioComposto()!=null && archive.getAccordiServizioComposto().size()>0){
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				ArchiveAccordoServizioComposto archiveAccordo = archive.getAccordiServizioComposto().get(i);
				IDSoggetto idSoggetto = archiveAccordo.getIdSoggettoReferente();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getAccordiServizioComposto().add(archiveAccordo);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo accordiServizioParteSpecifica
		if(archive.getAccordiServizioParteSpecifica()!=null && archive.getAccordiServizioParteSpecifica().size()>0){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordo = archive.getAccordiServizioParteSpecifica().get(i);
				IDSoggetto idSoggetto = archiveAccordo.getIdSoggettoErogatore();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getAccordiServizioParteSpecifica().add(archiveAccordo);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo accordiCooperazione
		if(archive.getAccordiCooperazione()!=null && archive.getAccordiCooperazione().size()>0){
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				ArchiveAccordoCooperazione archiveAccordo = archive.getAccordiCooperazione().get(i);
				IDSoggetto idSoggetto = archiveAccordo.getIdSoggettoReferente();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getAccordiCooperazione().add(archiveAccordo);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		// raccolgo fruizioni
		if(archive.getAccordiFruitori()!=null && archive.getAccordiFruitori().size()>0){
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				ArchiveFruitore archiveFruitore = archive.getAccordiFruitori().get(i);
				IDSoggetto idSoggetto = archiveFruitore.getIdAccordoServizioParteSpecifica().getSoggettoErogatore();
				Archive archiveListaOggettiSoggetti = null;
				if(archiveMapIntoSoggetti.containsKey(idSoggetto.toString())){
					archiveListaOggettiSoggetti = archiveMapIntoSoggetti.remove(idSoggetto.toString());
				}
				else{
					archiveListaOggettiSoggetti = new Archive();
				}
				archiveListaOggettiSoggetti.getAccordiFruitori().add(archiveFruitore);
				archiveMapIntoSoggetti.put(idSoggetto.toString(), archiveListaOggettiSoggetti);
			}
		}
		
		return archiveMapIntoSoggetti;
	}
	
	private void saveAccordo(AccordoServizioParteComune accordo, IDAccordo idAccordo, boolean servizioComposto, String parentDir, ZipOutputStream zipOut) throws IOException, SerializerException, ProtocolException{
		
		String nomeAccordo = null;
		try{
			String nomeDefinizione = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_FILE_NAME;
			nomeAccordo =  Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR;
			if(servizioComposto){
				nomeDefinizione = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_FILE_NAME;
				nomeAccordo = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR;
			}
			
			String rootDir = parentDir + nomeAccordo+File.separatorChar+
					ZIPUtils.convertNameToSistemaOperativoCompatible(idAccordo.getNome())+"_"+
					idAccordo.getVersione()+File.separatorChar;
			
			// raccolta elementi
			
			byte[]wsdlInterfacciaDefinitoria = accordo.getByteWsdlDefinitorio();
			accordo.setByteWsdlDefinitorio(null);
			byte[]wsdlInterfacciaConcettuale = accordo.getByteWsdlConcettuale();
			accordo.setByteWsdlConcettuale(null);
			byte[]wsdlInterfacciaLogicaErogatore = accordo.getByteWsdlLogicoErogatore();
			accordo.setByteWsdlLogicoErogatore(null);
			byte[]wsdlInterfacciaLogicaFruitore = accordo.getByteWsdlLogicoFruitore();
			accordo.setByteWsdlLogicoFruitore(null);
			
			byte[]specificaConversazioneConcettuale = accordo.getByteSpecificaConversazioneConcettuale();
			accordo.setSpecificaConversazioneConcettuale(null);
			byte[]specificaConversazioneLogicaErogatore = accordo.getByteSpecificaConversazioneErogatore();
			accordo.setSpecificaConversazioneErogatore(null);
			byte[]specificaConversazioneLogicaFruitore = accordo.getByteSpecificaConversazioneFruitore();
			accordo.setSpecificaConversazioneFruitore(null);
			
			Hashtable<String, byte[]> allegatiList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getAllegatoList()) {
				if(documento.getFile()==null){
					throw new Exception("Allegato senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				allegatiList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaSemiformaleList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				if(documento.getFile()==null){
					throw new Exception("SpecificaSemiformale senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				specificaSemiformaleList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaCoordinamentoList = new Hashtable<String, byte[]>();
			if(servizioComposto){
				for (Documento documento : accordo.getServizioComposto().getSpecificaCoordinamentoList()) {
					if(documento.getFile()==null){
						throw new Exception("SpecificaCoordinamento senza nome file");
					}
					if(documento.getByteContenuto()==null){
						throw new Exception("SpecificaCoordinamento["+documento.getFile()+"] senza contenuto");
					}
					if(documento.getTipo()==null){
						throw new Exception("SpecificaCoordinamento["+documento.getFile()+"] senza tipo");
					}
					String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
					specificaCoordinamentoList.put(id, documento.getByteContenuto());
					documento.setByteContenuto(null);
				}
			}
			
			// id
			String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			String identificativo = accordo.getNome()+" "+accordo.getVersione();
			zipOut.write(identificativo.getBytes());
			
			// definizione
			nomeFile = nomeDefinizione;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			// write(zipOut, nomeDefinizione, idAccordo, true, accordo); viene effettuato il catch di tutto il metodo saveAccordo
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(accordo); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			zipOut.write(this.jibxRegistrySerializer.toByteArray(accordo));
			
			// wsdl
			if(wsdlInterfacciaDefinitoria!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaDefinitoria);
			}
			if(wsdlInterfacciaConcettuale!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaConcettuale);
			}
			if(wsdlInterfacciaLogicaErogatore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaLogicaErogatore);
			}
			if(wsdlInterfacciaLogicaFruitore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaLogicaFruitore);
			}
			
			// specificaConversazione
			if(specificaConversazioneConcettuale!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_CONCETTUALE;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(specificaConversazioneConcettuale);
			}
			if(specificaConversazioneLogicaErogatore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_EROGATORE;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(specificaConversazioneLogicaErogatore);
			}
			if(specificaConversazioneLogicaFruitore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_FRUITORE;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(specificaConversazioneLogicaFruitore);
			}
			
			// Permit '.'
			List<Character> listCharacterPermit = new ArrayList<Character>();
			listCharacterPermit.add('.');
			
			// allegati
			if(allegatiList.size()>0){
				int index = 1;
				Enumeration<String> enumAllegati = allegatiList.keys();
				while (enumAllegati.hasMoreElements()) {
					String id = (String) enumAllegati.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(allegatiList.get(id));
					
					index++;
				}
			}
			
			// specificaSemiformale
			if(specificaSemiformaleList.size()>0){
				int index = 1;
				Enumeration<String> enumSpecifiche = specificaSemiformaleList.keys();
				while (enumSpecifiche.hasMoreElements()) {
					String id = (String) enumSpecifiche.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(specificaSemiformaleList.get(id));
					
					index++;
				}
			}
			
			if(servizioComposto){
				if(specificaCoordinamentoList.size()>0){
					int index = 1;
					Enumeration<String> enumSpecifiche = specificaCoordinamentoList.keys();
					while (enumSpecifiche.hasMoreElements()) {
						String id = (String) enumSpecifiche.nextElement();
						
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar+
								Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
								Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						zipOut.write(id.getBytes());
						
						nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO+File.separatorChar+
								Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
								Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
						zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
						zipOut.write(specificaCoordinamentoList.get(id));
						
						index++;
					}
				}
			}
			
		}catch(Exception e){
			String xml = null;
			try{
				xml = accordo.toXml_Jaxb();
			}catch(Exception eDebug){
				this.log.error("Errore durante il recupero della struttura xml: "+eDebug,eDebug);
				throw new ProtocolException("["+nomeAccordo+"]["+idAccordo+"]: "+e.getMessage(),e);
			}
			throw new ProtocolException("["+nomeAccordo+"]["+idAccordo+"] ("+xml+"): "+e.getMessage(),e);
		}
	}
	
	private void saveAccordo(AccordoServizioParteSpecifica accordo, IDServizio idServizio, List<IDPortaApplicativa> idPorteApplicativeAssociate,
			String parentDir, ZipOutputStream zipOut,
			Hashtable<String, String> map_IdApsScritti_nomeFileSystem) throws IOException, SerializerException, ProtocolException{
		
		try{
			String aspsDir = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR+File.separatorChar+
					ZIPUtils.convertNameToSistemaOperativoCompatible(idServizio.getTipo())+"_"+
					ZIPUtils.convertNameToSistemaOperativoCompatible(idServizio.getNome())+"_"+
					idServizio.getVersione()+File.separatorChar;
			String rootDir = parentDir + aspsDir; 
			
			String keyMap = accordo.getNome()+" "+accordo.getVersione();
			map_IdApsScritti_nomeFileSystem.put(keyMap, aspsDir);
			
			// raccolta elementi
			
			byte[]wsdlInterfacciaImplementativaErogatore = accordo.getByteWsdlImplementativoErogatore();
			accordo.setByteWsdlImplementativoErogatore(null);
			byte[]wsdlInterfacciaImplementativaFruitore = accordo.getByteWsdlImplementativoFruitore();
			accordo.setByteWsdlImplementativoFruitore(null);
			
			Hashtable<String, byte[]> allegatiList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getAllegatoList()) {
				if(documento.getFile()==null){
					throw new Exception("Allegato senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				allegatiList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaSemiformaleList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				if(documento.getFile()==null){
					throw new Exception("SpecificaSemiformale senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				specificaSemiformaleList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaLivelloServizioList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getSpecificaLivelloServizioList()) {
				if(documento.getFile()==null){
					throw new Exception("SpecificaLivelloServizio senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("SpecificaLivelloServizio["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("SpecificaLivelloServizio["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				specificaLivelloServizioList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaSicurezzaList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getSpecificaSicurezzaList()) {
				if(documento.getFile()==null){
					throw new Exception("SpecificaSicurezza senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("SpecificaSicurezza["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("SpecificaSicurezza["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				specificaSicurezzaList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			List<Fruitore> listFruitore = new ArrayList<Fruitore>();
			while(accordo.sizeFruitoreList()>0){
				listFruitore.add(accordo.removeFruitore(0));
			}
			
			// id
			String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			String identificativo = accordo.getTipo()+" "+accordo.getNome()+" "+accordo.getVersione();
			zipOut.write(identificativo.getBytes());
			
			// definizione
			nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			//write(zipOut, "accordoServizioParteSpecifica", idAccordo, true, accordo); viene effettuato il catch di tutto il metodo saveAccordo
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(accordo); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			zipOut.write(this.jibxRegistrySerializer.toByteArray(accordo));
			
			// wsdl
			if(wsdlInterfacciaImplementativaErogatore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaImplementativaErogatore);
			}
			if(wsdlInterfacciaImplementativaFruitore!=null){
				nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL+File.separatorChar+
						Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL;
				zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
				zipOut.write(wsdlInterfacciaImplementativaFruitore);
			}
			
			// Permit '.'
			List<Character> listCharacterPermit = new ArrayList<Character>();
			listCharacterPermit.add('.');
			
			// allegati
			if(allegatiList.size()>0){
				int index = 1;
				Enumeration<String> enumAllegati = allegatiList.keys();
				while (enumAllegati.hasMoreElements()) {
					String id = (String) enumAllegati.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(allegatiList.get(id));
					
					index++;
				}
			}
			
			// specificaSemiformale
			if(specificaSemiformaleList.size()>0){
				int index = 1;
				Enumeration<String> enumSpecifiche = specificaSemiformaleList.keys();
				while (enumSpecifiche.hasMoreElements()) {
					String id = (String) enumSpecifiche.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(specificaSemiformaleList.get(id));
					
					index++;
				}
			}
			
			// specificaLivelloServizio
			if(specificaLivelloServizioList.size()>0){
				int index = 1;
				Enumeration<String> enumSpecifiche = specificaLivelloServizioList.keys();
				while (enumSpecifiche.hasMoreElements()) {
					String id = (String) enumSpecifiche.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(specificaLivelloServizioList.get(id));
					
					index++;
				}
			}
			
			// specificaSicurezza
			if(specificaSicurezzaList.size()>0){
				int index = 1;
				Enumeration<String> enumSpecifiche = specificaSicurezzaList.keys();
				while (enumSpecifiche.hasMoreElements()) {
					String id = (String) enumSpecifiche.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(specificaSicurezzaList.get(id));
					
					index++;
				}
			}
			
			// fruitori (questo caso non dovrebbe essere presente, ci dovrebbe invece essere l'archiveFruitore)
			if(listFruitore.size()>0){
				for (Fruitore fruitore : listFruitore) {
					// il converName sui soggetti serve a poco, visto che devono essere simpleName (solo caratteri e numeri)
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_FRUITORE_DIR+File.separatorChar+
							ZIPUtils.convertNameToSistemaOperativoCompatible(fruitore.getTipo())+"_"+
							ZIPUtils.convertNameToSistemaOperativoCompatible(fruitore.getNome())+".xml";
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					this.cleanerOpenSPCoop2ExtensionsRegistry.clean(fruitore);
					zipOut.write(this.jibxRegistrySerializer.toByteArray(fruitore));
				}
			}
			
			// PortaApplicativa Associata
			if(idPorteApplicativeAssociate!=null){
				for (int i = 0; i < idPorteApplicativeAssociate.size(); i++) {
					String nomeFilePAAssociata = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_MAPPING+File.separatorChar+
							(i+1)+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_MAPPING_PA_SUFFIX;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFilePAAssociata));
					zipOut.write(idPorteApplicativeAssociate.get(i).getNome().getBytes());	
				}
			}
						
		}catch(Exception e){
			String xml = null;
			try{
				xml = accordo.toXml_Jaxb();
			}catch(Exception eDebug){
				this.log.error("Errore durante il recupero della struttura xml: "+eDebug,eDebug);
				throw new ProtocolException("[AccordoServizioParteSpecifica]["+idServizio+"]: "+e.getMessage(),e);
			}
			throw new ProtocolException("[AccordoServizioParteSpecifica]["+idServizio+"] ("+xml+"): "+e.getMessage(),e);
		}

	}
	
	private void saveAccordo(AccordoCooperazione accordo, IDAccordoCooperazione idAccordo, String parentDir, ZipOutputStream zipOut) throws IOException, SerializerException, ProtocolException{
		
		try{
		
			String rootDir = parentDir + Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR+File.separatorChar+
					ZIPUtils.convertNameToSistemaOperativoCompatible(idAccordo.getNome())+"_"+
					idAccordo.getVersione()+File.separatorChar;
			
			// raccolta elementi
			
			Hashtable<String, byte[]> allegatiList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getAllegatoList()) {
				if(documento.getFile()==null){
					throw new Exception("Allegato senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("Allegato["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				allegatiList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			Hashtable<String, byte[]> specificaSemiformaleList = new Hashtable<String, byte[]>();
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				if(documento.getFile()==null){
					throw new Exception("SpecificaSemiformale senza nome file");
				}
				if(documento.getByteContenuto()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza contenuto");
				}
				if(documento.getTipo()==null){
					throw new Exception("SpecificaSemiformale["+documento.getFile()+"] senza tipo");
				}
				String id = documento.getTipo()+Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR+documento.getFile();
				specificaSemiformaleList.put(id, documento.getByteContenuto());
				documento.setByteContenuto(null);
			}
			
			// id
			String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			String identificativo = accordo.getNome()+" "+accordo.getVersione();
			zipOut.write(identificativo.getBytes());
			
			// definizione
			nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_FILE_NAME;
			zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
			//write(zipOut, "accordoCooperazione", idAccordo, true, accordo); viene effettuato il catch di tutto il metodo saveAccordo
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(accordo); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			zipOut.write(this.jibxRegistrySerializer.toByteArray(accordo));
			
			// Permit '.'
			List<Character> listCharacterPermit = new ArrayList<Character>();
			listCharacterPermit.add('.');
			
			// allegati
			if(allegatiList.size()>0){
				int index = 1;
				Enumeration<String> enumAllegati = allegatiList.keys();
				while (enumAllegati.hasMoreElements()) {
					String id = (String) enumAllegati.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(allegatiList.get(id));
					
					index++;
				}
			}
			
			// specificaSemiformale
			if(specificaSemiformaleList.size()>0){
				int index = 1;
				Enumeration<String> enumSpecifiche = specificaSemiformaleList.keys();
				while (enumSpecifiche.hasMoreElements()) {
					String id = (String) enumSpecifiche.nextElement();
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(id.getBytes());
					
					nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI+File.separatorChar+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX+index+
							Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT;
					zipOut.putNextEntry(new ZipEntry(rootDir+nomeFile));
					zipOut.write(specificaSemiformaleList.get(id));
					
					index++;
				}
			}
		
		}catch(Exception e){
			String xml = null;
			try{
				xml = accordo.toXml_Jaxb();
			}catch(Exception eDebug){
				this.log.error("Errore durante il recupero della struttura xml: "+eDebug,eDebug);
				throw new ProtocolException("[AccordoCooperazione]["+idAccordo+"]: "+e.getMessage(),e);
			}
			throw new ProtocolException("[AccordoCooperazione]["+idAccordo+"] ("+xml+"): "+e.getMessage(),e);
		}
	}
}
