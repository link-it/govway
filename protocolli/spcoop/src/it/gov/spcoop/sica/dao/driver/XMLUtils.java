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



package it.gov.spcoop.sica.dao.driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.activation.FileDataSource;

import org.slf4j.Logger;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;
import org.openspcoop2.utils.io.ZipUtilities;

import it.gov.spcoop.sica.dao.AccordoCooperazione;
import it.gov.spcoop.sica.dao.AccordoServizioComposto;
import it.gov.spcoop.sica.dao.AccordoServizioParteComune;
import it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.dao.Costanti;
import it.gov.spcoop.sica.dao.Documento;
import it.gov.spcoop.sica.firma.Firma;
import it.gov.spcoop.sica.manifest.AccordoServizio;
import it.gov.spcoop.sica.manifest.ServizioComposto;
import it.gov.spcoop.sica.manifest.SpecificaConversazione;
import it.gov.spcoop.sica.manifest.SpecificaInterfaccia;
import it.gov.spcoop.sica.manifest.SpecificaPortiAccesso;
import it.gov.spcoop.sica.manifest.driver.XMLUtilsException;



/**
 * Classe utilizzata per lavorare sui package di un accordo di servizio o di cooperazione 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLUtils  {

	
	
	private SICAtoOpenSPCoopContext sicaToOpenSPCoopContext = null;
	private Logger log = null;
	public XMLUtils(SICAtoOpenSPCoopContext sContext,Logger log){
		this.sicaToOpenSPCoopContext = sContext;
		this.log = log;
	}
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di servizio parte comune ----- */
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte comune
	 * 
	 * @param zip byte[]
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(byte[] zip) throws XMLUtilsException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile("sica", Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE);
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();
			
			return getAccordoServizioParteComune(tmp);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
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
	 * Ritorna la rappresentazione java di un accordo di servizio parte comune
	 * 
	 * @param fileName File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(String fileName) throws XMLUtilsException{
		return getAccordoServizioParteComune(new File(fileName));
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte comune
	 * 
	 * @param zip File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(File zip) throws XMLUtilsException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			return getAccordoServizioParteComune(zipFile);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
		
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte comune
	 * 
	 * @param m InputStream
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(InputStream m) throws XMLUtilsException{
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
			
			return getAccordoServizioParteComune(bout.toByteArray());
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte comune
	 * 
	 * @param zip File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(ZipFile zip) throws XMLUtilsException{
		try{
			AccordoServizioParteComune archivio = new AccordoServizioParteComune();
			
			String rootDir = null;
			
			/* Primo giro per catturare il Manifest */
			it.gov.spcoop.sica.manifest.AccordoServizioParteComune parteComune = null;
			Enumeration<?> e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
				String entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
				
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
					
					//System.out.println("ENTRY ["+rootDir+Costanti.MANIFESTO_XML+"]==["+entryName+"]");
					if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO!");
						InputStream inputStream = zip.getInputStream(zipEntry);
						AccordoServizio manifesto = it.gov.spcoop.sica.manifest.driver.XMLUtils.getManifestoAS(this.log,inputStream);
						parteComune = manifesto.getParteComune();
						if(parteComune==null){
							throw new Exception("Manifest Parte Comune non presente");
						}
						inputStream.close();
						archivio.setManifesto(manifesto);
						break;
					}
				}
			}
			if(parteComune==null){
				throw new Exception("Manifest Parte Comune non presente");
			}
			
			
			/* Lettura informazioni dal manifest */
			String interfacciaConcettuale = Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL;
			String interfaccialogicoErogatore = Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL;
			String interfaccialogicoFruitore = Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL;
			String conversazioneConcettuale = Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL;
			String conversazionelogicoErogatore = Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL;
			String conversazionelogicoFruitore = Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL;
			if(parteComune.getSpecificaInterfaccia()!=null){
				SpecificaInterfaccia interfaccia = parteComune.getSpecificaInterfaccia();
				if(interfaccia.getInterfacciaConcettuale()!=null){
					interfacciaConcettuale = ZipUtilities.getBaseName(interfaccia.getInterfacciaConcettuale().getBase());
				}
				if(interfaccia.getInterfacciaLogicaLatoErogatore()!=null){
					interfaccialogicoErogatore = ZipUtilities.getBaseName(interfaccia.getInterfacciaLogicaLatoErogatore().getBase());
				}
				if(interfaccia.getInterfacciaLogicaLatoFruitore()!=null){
					interfaccialogicoFruitore = ZipUtilities.getBaseName(interfaccia.getInterfacciaLogicaLatoFruitore().getBase());
				}
			}
			if(parteComune.getSpecificaConversazione()!=null){
				SpecificaConversazione conversazione = parteComune.getSpecificaConversazione();
				if(conversazione.getConversazioneConcettuale()!=null){
					conversazioneConcettuale = ZipUtilities.getBaseName(conversazione.getConversazioneConcettuale().getBase());
				}
				if(conversazione.getConversazioneLogicaLatoErogatore()!=null){
					conversazionelogicoErogatore = ZipUtilities.getBaseName(conversazione.getConversazioneLogicaLatoErogatore().getBase());
				}
				if(conversazione.getConversazioneLogicaLatoFruitore()!=null){
					conversazionelogicoFruitore = ZipUtilities.getBaseName(conversazione.getConversazioneLogicaLatoFruitore().getBase());
				}
			}
			
			
			/* Comprensione altri campi */
			e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.PROJECT_CLIENT_SICA).equals(entryName)){
						//System.out.println(".PROJECT: NIENTE DA GESTIRE");
					}	
					else if((rootDir+Costanti.FIRMA_XML).equals(entryName)){
						//System.out.println("FIRMA!");
						Firma firma = new Firma();
						archivio.setFirma(firma);
					}	
					else if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO! SCARTO");
					}		
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR)) ){
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						String dir = rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar;
						if((dir+conversazioneConcettuale).equals(entryName)){
							//System.out.println("CONVERSAZIONE CONCETTUALE WSBL!");
							archivio.setConversazioneConcettuale(d);
						}
						else if((dir+conversazionelogicoErogatore).equals(entryName)){
							//System.out.println("CONVERSAZIONE LOGICA EROGATORE WSBL!");
							archivio.setConversazioneLogicaErogatore(d);
						}
						else if((dir+conversazionelogicoFruitore).equals(entryName)){
							//System.out.println("CONVERSAZIONE LOGICA FRUITORE WSBL!");
							archivio.setConversazioneLogicaFruitore(d);
						}

					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR)) ){
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						String dir = rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar;
						if((dir+interfacciaConcettuale).equals(entryName)){
							//System.out.println("INTERFACCIA CONCETTUALE WSDL!");
							archivio.setInterfacciaConcettuale(d);
						}
						else if((dir+interfaccialogicoErogatore).equals(entryName)){
							//System.out.println("INTERFACCIA LOGICA EROGATORE WSDL!");
							archivio.setInterfacciaLogicaLatoErogatore(d);
						}
						else if((dir+interfaccialogicoFruitore).equals(entryName)){
							//System.out.println("INTERFACCIA LOGICA FRUITORE WSDL!");
							archivio.setInterfacciaLogicaLatoFruitore(d);
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.ALLEGATI_DIR)) ){
						//System.out.println("ALLEGATO");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addAllegato(d);
						
						// Validazione XSD delle informazioni egov
						if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.cnipa.collprofiles.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpDisabled_childUnqualified());
						}else if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.gov.spcoop.sica.wscp.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpEnabled_childUnqualified());
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR)) ){
						//System.out.println("SPECIFICASEMIFORMALE");
						InputStream inputStream = zip.getInputStream(zipEntry);						
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaSemiformale(d);
						
						// Validazione XSD delle informazioni egov
						if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.cnipa.collprofiles.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpDisabled_childUnqualified());
						}else if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.gov.spcoop.sica.wscp.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpEnabled_childUnqualified());
						}
					}
				}
				
	
			}
			
			// check
			if(archivio.getManifesto()==null){
				throw new Exception("Manifesto non presente");
			}
			
			// Metto a posto i tipi (le estensioni dei files possono essere diverse)
			if(archivio.getManifesto().getSpecificaSemiformale()!=null){
				for(int i=0; i<archivio.getManifesto().getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
					String fileName = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getBase();
					String tipo = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheSemiformali(); j++){
						if(fileName.equals(archivio.getSpecificaSemiformale(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaSemiformale(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaSemiformale(j).setTipo(tipo);
						}
					}
				}
			}
			
			return archivio;

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Marshall dell'accordo di servizio parte comune----- */
	public void generateAccordoServizioParteComune(AccordoServizioParteComune manifesto,File file) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			generateAccordoServizioParteComune(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void generateAccordoServizioParteComune(AccordoServizioParteComune manifesto,String fileName) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			generateAccordoServizioParteComune(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] generateAccordoServizioParteComune(AccordoServizioParteComune manifesto) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			generateAccordoServizioParteComune(manifesto,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public void generateAccordoServizioParteComune(AccordoServizioParteComune manifesto,OutputStream out) throws XMLUtilsException{
		
		boolean generaSICAClientProjectFile = this.sicaToOpenSPCoopContext.isSICAClient_generaProject();
		boolean includiInfoRegistroGenerale = this.sicaToOpenSPCoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean lunghezzaNomeAccordoLimitata = this.sicaToOpenSPCoopContext.isSICAClient_nomeAccordo_32CaratteriMax();
		
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootDir = manifesto.getManifesto().getNome()+File.separatorChar;
			
			// .project
			if(generaSICAClientProjectFile){
				String nomeProgettoEclipse =  manifesto.getManifesto().getNome() + "_" + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE;
				String projectContenuto = Costanti.PROJECT_CLIENT_SICA_CONTENUTO.replace(Costanti.PROJECT_CLIENT_SICA_KEY_NOME, nomeProgettoEclipse);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.PROJECT_CLIENT_SICA));
				zipOut.write(projectContenuto.getBytes());
			}
			
			// firma
			if(manifesto.getFirma()!=null){
				byte[] firma = manifesto.getFirma().getBytes();
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.FIRMA_XML));
				zipOut.write(firma);
			}
			
			// manifest
			byte[] manifestoBytes = it.gov.spcoop.sica.manifest.driver.XMLUtils.generateManifestoAS(manifesto.getManifesto(),includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata);
			zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.MANIFESTO_XML));
			zipOut.write(manifestoBytes);
			
			// Specifica Interfaccia
			boolean specificaInterfaccia = false;
			if(manifesto.getInterfacciaConcettuale()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL));
				zipOut.write(manifesto.getInterfacciaConcettuale().getContenuto());
				specificaInterfaccia = true;
			}
			if(manifesto.getInterfacciaLogicaLatoErogatore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL));
				zipOut.write(manifesto.getInterfacciaLogicaLatoErogatore().getContenuto());
				specificaInterfaccia = true;
			}
			if(manifesto.getInterfacciaLogicaLatoFruitore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL));
				zipOut.write(manifesto.getInterfacciaLogicaLatoFruitore().getContenuto());
				specificaInterfaccia = true;
			}
			if(!specificaInterfaccia){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar));
			}
			
			// Specifica Conversazioni
			boolean specificaConversazione = false;
			if(manifesto.getConversazioneConcettuale()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL));
				zipOut.write(manifesto.getConversazioneConcettuale().getContenuto());
				specificaConversazione = true;
			}
			if(manifesto.getConversazioneLogicaErogatore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL));
				zipOut.write(manifesto.getConversazioneLogicaErogatore().getContenuto());
				specificaConversazione = true;
			}
			if(manifesto.getConversazioneLogicaFruitore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL));
				zipOut.write(manifesto.getConversazioneLogicaFruitore().getContenuto());
				specificaConversazione = true;
			}
			if(!specificaConversazione){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar));
			}
			
			// Allegati
			boolean allegati = manifesto.sizeAllegati()>0;
			for(int i=0; i<manifesto.sizeAllegati(); i++){
				Documento allegato = manifesto.getAllegato(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar+allegato.getNome()));
				zipOut.write(allegato.getContenuto());
			}
			if(!allegati){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar));
			}
			
			// Specifiche semiformali
			boolean specificheSemiformali = manifesto.sizeSpecificheSemiformali()>0;
			for(int i=0; i<manifesto.sizeSpecificheSemiformali(); i++){
				Documento doc = manifesto.getSpecificaSemiformale(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheSemiformali){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar));
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di servizio parte specifica----- */
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte specifica
	 * 
	 * @param zip byte[]
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(byte[] zip) throws XMLUtilsException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile("sica", Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();
			
			return getAccordoServizioParteSpecifica(tmp);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
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
	 * Ritorna la rappresentazione java di un accordo di servizio parte specifica
	 * 
	 * @param fileName File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(String fileName) throws XMLUtilsException{
		return getAccordoServizioParteSpecifica(new File(fileName));
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte specifica
	 * 
	 * @param zip File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(File zip) throws XMLUtilsException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			return getAccordoServizioParteSpecifica(zipFile);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
		
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte specifica
	 * 
	 * @param m InputStream
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(InputStream m) throws XMLUtilsException{
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
			
			return getAccordoServizioParteSpecifica(bout.toByteArray());
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio parte specifica
	 * 
	 * @param zip File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(ZipFile zip) throws XMLUtilsException{
		try{
			AccordoServizioParteSpecifica archivio = new AccordoServizioParteSpecifica();
			
			String rootDir = null;
			
			/* Primo giro per catturare il Manifest */
			it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica parteSpecifica = null;
			Enumeration<?> e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					//System.out.println("NAME["+nome+"] TIPO["+tipo+"]");
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO!");
						InputStream inputStream = zip.getInputStream(zipEntry);
						AccordoServizio manifesto = it.gov.spcoop.sica.manifest.driver.XMLUtils.getManifestoAS(this.log,inputStream);
						parteSpecifica = manifesto.getParteSpecifica();
						if(parteSpecifica==null){
							throw new Exception("Manifest Parte Specifica non presente");
						}
						inputStream.close();
						archivio.setManifesto(manifesto);
						break;
					}
				}
			}
			if(parteSpecifica==null){
				throw new Exception("Manifest Parte Specifica non presente");
			}
			
			
			/* Lettura informazioni dal manifest */
			String interfacciaPortiAccessoErogatore = Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL;
			String interfacciaPortiAccessoFruitore = Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL;
			if(parteSpecifica.getSpecificaPortiAccesso()!=null){
				SpecificaPortiAccesso portiAccesso = parteSpecifica.getSpecificaPortiAccesso();
				if(portiAccesso.getPortiAccessoErogatore()!=null){
					interfacciaPortiAccessoErogatore = ZipUtilities.getBaseName(portiAccesso.getPortiAccessoErogatore().getBase());
				}
				if(portiAccesso.getPortiAccessoFruitore()!=null){
					interfacciaPortiAccessoFruitore = ZipUtilities.getBaseName(portiAccesso.getPortiAccessoFruitore().getBase());
				}
			}
			
			
			/* Comprensione altri campi */
			e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					//System.out.println("NAME["+nome+"] TIPO["+tipo+"]");
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.PROJECT_CLIENT_SICA).equals(entryName)){
						//System.out.println(".PROJECT: NIENTE DA GESTIRE");
					}	
					else if((rootDir+Costanti.FIRMA_XML).equals(entryName)){
						//System.out.println("FIRMA!");
						Firma firma = new Firma();
						archivio.setFirma(firma);
					}			
					else if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO SCARTO!");
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_PORTI_ACCESSO_DIR)) ){
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						String dir = rootDir+Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar;
						if((dir+interfacciaPortiAccessoErogatore).equals(entryName)){
							//System.out.println("PORTI ACCESSO EROGATORE WSBL!");
							archivio.setPortiAccessoErogatore(d);
						}
						else if((dir+interfacciaPortiAccessoFruitore).equals(entryName)){
							//System.out.println("PORTI ACCESSO FRUITORE WSBL!");
							archivio.setPortiAccessoFruitore(d);
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.ALLEGATI_DIR)) ){
						//System.out.println("ALLEGATO");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addAllegato(d);
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR)) ){
						//System.out.println("SPECIFICA SEMIFORMALE");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaSemiformale(d);
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_LIVELLI_SERVIZIO_DIR)) ){
						//System.out.println("SPECIFICA LIVELLO SERVIZIO");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaLivelloServizio(d);
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_SICUREZZA_DIR)) ){
						//System.out.println("SPECIFICA SICUREZZA");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaSicurezza(d);
					}
				}
				
	
			}
			
			// check
			if(archivio.getManifesto()==null){
				throw new Exception("Manifesto non presente");
			}
			
			// Metto a posto i tipi (le estensioni dei files possono essere diverse)
			if(archivio.getManifesto().getSpecificaSemiformale()!=null){
				for(int i=0; i<archivio.getManifesto().getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
					String fileName = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getBase();
					String tipo = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheSemiformali(); j++){
						if(fileName.equals(archivio.getSpecificaSemiformale(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaSemiformale(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaSemiformale(j).setTipo(tipo);
						}
					}
				}
			}
			if(archivio.getManifesto().getParteSpecifica().getSpecificaLivelliServizio()!=null){
				for(int i=0; i<archivio.getManifesto().getParteSpecifica().getSpecificaLivelliServizio().sizeDocumentoLivelloServizioList(); i++){
					String fileName = archivio.getManifesto().getParteSpecifica().getSpecificaLivelliServizio().getDocumentoLivelloServizio(i).getBase();
					String tipo = archivio.getManifesto().getParteSpecifica().getSpecificaLivelliServizio().getDocumentoLivelloServizio(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheLivelliServizio(); j++){
						if(fileName.equals(archivio.getSpecificaLivelloServizio(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaLivelloServizio(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaLivelloServizio(j).setTipo(tipo);
						}
					}
				}
			}
			if(archivio.getManifesto().getParteSpecifica().getSpecificaSicurezza()!=null){
				for(int i=0; i<archivio.getManifesto().getParteSpecifica().getSpecificaSicurezza().sizeDocumentoSicurezzaList(); i++){
					String fileName = archivio.getManifesto().getParteSpecifica().getSpecificaSicurezza().getDocumentoSicurezza(i).getBase();
					String tipo = archivio.getManifesto().getParteSpecifica().getSpecificaSicurezza().getDocumentoSicurezza(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheSicurezza(); j++){
						if(fileName.equals(archivio.getSpecificaSicurezza(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaSicurezza(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaSicurezza(j).setTipo(tipo);
						}
					}
				}
			}
			
			return archivio;

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Marshall dell'accordo di servizio parte specifica----- */
	public void generateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica manifesto,File file) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			generateAccordoServizioParteSpecifica(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void generateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica manifesto,String fileName) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			generateAccordoServizioParteSpecifica(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] generateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica manifesto) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			generateAccordoServizioParteSpecifica(manifesto,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public void generateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica manifesto,OutputStream out) throws XMLUtilsException{
		
		boolean generaSICAClientProjectFile = this.sicaToOpenSPCoopContext.isSICAClient_generaProject();
		boolean includiInfoRegistroGenerale = this.sicaToOpenSPCoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean lunghezzaNomeAccordoLimitata = this.sicaToOpenSPCoopContext.isSICAClient_nomeAccordo_32CaratteriMax();
		
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootDir = manifesto.getManifesto().getNome()+File.separatorChar;
			
			// .project
			if(generaSICAClientProjectFile){
				String nomeProgettoEclipse =  manifesto.getManifesto().getNome() + "_" + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA;
				String projectContenuto = Costanti.PROJECT_CLIENT_SICA_CONTENUTO.replace(Costanti.PROJECT_CLIENT_SICA_KEY_NOME, nomeProgettoEclipse);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.PROJECT_CLIENT_SICA));
				zipOut.write(projectContenuto.getBytes());
			}
			
			// firma
			if(manifesto.getFirma()!=null){
				byte[] firma = manifesto.getFirma().getBytes();
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.FIRMA_XML));
				zipOut.write(firma);
			}
			
			// manifest
			byte[] manifestoBytes = it.gov.spcoop.sica.manifest.driver.XMLUtils.generateManifestoAS(manifesto.getManifesto(),includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata);
			zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.MANIFESTO_XML));
			zipOut.write(manifestoBytes);
			
			// Specifica Porti Accesso
			boolean specificaPortiAccesso = false;
			if(manifesto.getPortiAccessoErogatore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar+Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL));
				zipOut.write(manifesto.getPortiAccessoErogatore().getContenuto());
				specificaPortiAccesso = true;
			}
			if(manifesto.getPortiAccessoFruitore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar+Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL));
				zipOut.write(manifesto.getPortiAccessoFruitore().getContenuto());
				specificaPortiAccesso = true;
			}
			if(!specificaPortiAccesso){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar));
			}
			
			// Allegati
			boolean allegati = manifesto.sizeAllegati()>0;
			for(int i=0; i<manifesto.sizeAllegati(); i++){
				Documento allegato = manifesto.getAllegato(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar+allegato.getNome()));
				zipOut.write(allegato.getContenuto());
			}
			if(!allegati){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar));
			}
			
			// Specifiche Livelli Servizio
			boolean specificheLivelliServizio = manifesto.sizeSpecificheLivelliServizio()>0;
			for(int i=0; i<manifesto.sizeSpecificheLivelliServizio(); i++){
				Documento doc = manifesto.getSpecificaLivelloServizio(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_LIVELLI_SERVIZIO_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheLivelliServizio){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_LIVELLI_SERVIZIO_DIR+File.separatorChar));
			}
			
			// Specifiche semiformali
			boolean specificheSemiformali = manifesto.sizeSpecificheSemiformali()>0;
			for(int i=0; i<manifesto.sizeSpecificheSemiformali(); i++){
				Documento doc = manifesto.getSpecificaSemiformale(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheSemiformali){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar));
			}
			
			// Specifiche sicurezza
			boolean specificheSicurezza = manifesto.sizeSpecificheSicurezza()>0;
			for(int i=0; i<manifesto.sizeSpecificheSicurezza(); i++){
				Documento doc = manifesto.getSpecificaSicurezza(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SICUREZZA_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheSicurezza){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SICUREZZA_DIR+File.separatorChar));
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di cooperazione ----- */
	
	/**
	 * Ritorna la rappresentazione java di un accordo di cooperazione
	 * 
	 * @param zip byte[]
	 * @return ManifestoAC
	 * @throws XMLUtilsException
	 */
	public AccordoCooperazione getAccordoCooperazione(byte[] zip) throws XMLUtilsException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile("sica", Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE);
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();
			
			return getAccordoCooperazione(tmp);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
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
	 * Ritorna la rappresentazione java di un accordo di cooperazione
	 * 
	 * @param fileName File
	 * @return ManifestoAC
	 * @throws XMLUtilsException
	 */
	public AccordoCooperazione getAccordoCooperazione(String fileName) throws XMLUtilsException{
		return getAccordoCooperazione(new File(fileName));
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di cooperazione
	 * 
	 * @param zip File
	 * @return ManifestoAC
	 * @throws XMLUtilsException
	 */
	public AccordoCooperazione getAccordoCooperazione(File zip) throws XMLUtilsException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			return getAccordoCooperazione(zipFile);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
		
	/**
	 * Ritorna la rappresentazione java di un accordo di cooperazione
	 * 
	 * @param m InputStream
	 * @return ManifestoAC
	 * @throws XMLUtilsException
	 */
	public AccordoCooperazione getAccordoCooperazione(InputStream m) throws XMLUtilsException{
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
			
			return getAccordoCooperazione(bout.toByteArray());
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di cooperazione
	 * 
	 * @param zip File
	 * @return ManifestoAC
	 * @throws XMLUtilsException
	 */
	public AccordoCooperazione getAccordoCooperazione(ZipFile zip) throws XMLUtilsException{
		try{
			AccordoCooperazione archivio = new AccordoCooperazione();
			
			String rootDir = null;
			
			Enumeration<?> e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					//System.out.println("NAME["+nome+"] TIPO["+tipo+"]");
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.PROJECT_CLIENT_SICA).equals(entryName)){
						//System.out.println(".PROJECT: NIENTE DA GESTIRE");
					}	
					else if((rootDir+Costanti.FIRMA_XML).equals(entryName)){
						//System.out.println("FIRMA!");
						Firma firma = new Firma();
						archivio.setFirma(firma);
					}			
					else if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO!");
						InputStream inputStream = zip.getInputStream(zipEntry);
						it.gov.spcoop.sica.manifest.AccordoCooperazione manifesto = it.gov.spcoop.sica.manifest.driver.XMLUtils.getManifestoAC(this.log,inputStream);
						inputStream.close();
						archivio.setManifesto(manifesto);
					}
					else if(entryName.startsWith((rootDir+Costanti.ALLEGATI_DIR)) ){
						//System.out.println("ALLEGATO");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addAllegato(d);
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR)) ){
						//System.out.println("SPECIFICA SEMIFORMALE");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaSemiformale(d);
					}
				}
				
	
			}
			
			// check
			if(archivio.getManifesto()==null){
				throw new Exception("Manifesto non presente");
			}
			
			// Metto a posto i tipi (le estensioni dei files possono essere diverse)
			if(archivio.getManifesto().getSpecificaSemiformale()!=null){
				for(int i=0; i<archivio.getManifesto().getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
					String fileName = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getBase();
					String tipo = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheSemiformali(); j++){
						if(fileName.equals(archivio.getSpecificaSemiformale(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaSemiformale(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaSemiformale(j).setTipo(tipo);
						}
					}
				}
			}
			
			return archivio;

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Marshall dell'accordo di cooperazione ----- */
	public void generateAccordoCooperazione(AccordoCooperazione manifesto,File file) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			generateAccordoCooperazione(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void generateAccordoCooperazione(AccordoCooperazione manifesto,String fileName) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			generateAccordoCooperazione(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] generateAccordoCooperazione(AccordoCooperazione manifesto) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			generateAccordoCooperazione(manifesto,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public void generateAccordoCooperazione(AccordoCooperazione manifesto,OutputStream out) throws XMLUtilsException{
		
		boolean generaSICAClientProjectFile = this.sicaToOpenSPCoopContext.isSICAClient_generaProject();
		boolean includiInfoRegistroGenerale = this.sicaToOpenSPCoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean lunghezzaNomeAccordoLimitata = this.sicaToOpenSPCoopContext.isSICAClient_nomeAccordo_32CaratteriMax();
		
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootDir = manifesto.getManifesto().getNome()+File.separatorChar;
			
			// .project
			if(generaSICAClientProjectFile){
				String nomeProgettoEclipse =  manifesto.getManifesto().getNome() + "_" + Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE;
				String projectContenuto = Costanti.PROJECT_CLIENT_SICA_CONTENUTO.replace(Costanti.PROJECT_CLIENT_SICA_KEY_NOME, nomeProgettoEclipse);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.PROJECT_CLIENT_SICA));
				zipOut.write(projectContenuto.getBytes());
			}
			
			// firma
			if(manifesto.getFirma()!=null){
				byte[] firma = manifesto.getFirma().getBytes();
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.FIRMA_XML));
				zipOut.write(firma);
			}
			
			// manifest
			byte[] manifestoBytes = it.gov.spcoop.sica.manifest.driver.XMLUtils.generateManifestoAC(manifesto.getManifesto(),includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata);
			zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.MANIFESTO_XML));
			zipOut.write(manifestoBytes);
			
			// Allegati
			boolean allegati = manifesto.sizeAllegati()>0;
			for(int i=0; i<manifesto.sizeAllegati(); i++){
				Documento allegato = manifesto.getAllegato(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar+allegato.getNome()));
				zipOut.write(allegato.getContenuto());
			}
			if(!allegati){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar));
			}
			
			// Specifiche semiformali
			boolean specificheSemiformali = manifesto.sizeSpecificheSemiformali()>0;
			for(int i=0; i<manifesto.sizeSpecificheSemiformali(); i++){
				Documento doc = manifesto.getSpecificaSemiformale(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheSemiformali){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar));
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di servizio composto ----- */
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio composto
	 * 
	 * @param zip byte[]
	 * @return ManifestoSC
	 * @throws XMLUtilsException
	 */
	public  AccordoServizioComposto getAccordoServizioComposto(byte[] zip) throws XMLUtilsException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile("sica", Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO);
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();
			
			return getAccordoServizioComposto(tmp);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
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
	 * Ritorna la rappresentazione java di un accordo di servizio composto
	 * 
	 * @param fileName File
	 * @return ManifestoSC
	 * @throws XMLUtilsException
	 */
	public AccordoServizioComposto getAccordoServizioComposto(String fileName) throws XMLUtilsException{
		return getAccordoServizioComposto(new File(fileName));
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio composto
	 * 
	 * @param zip File
	 * @return ManifestoSC
	 * @throws XMLUtilsException
	 */
	public AccordoServizioComposto getAccordoServizioComposto(File zip) throws XMLUtilsException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			return getAccordoServizioComposto(zipFile);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
		
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio composto
	 * 
	 * @param m InputStream
	 * @return ManifestoSC
	 * @throws XMLUtilsException
	 */
	public AccordoServizioComposto getAccordoServizioComposto(InputStream m) throws XMLUtilsException{
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
			
			return getAccordoServizioComposto(bout.toByteArray());
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un accordo di servizio composto
	 * 
	 * @param zip File
	 * @return ManifestoSC
	 * @throws XMLUtilsException
	 */
	public AccordoServizioComposto getAccordoServizioComposto(ZipFile zip) throws XMLUtilsException{
		try{
			AccordoServizioComposto archivio = new AccordoServizioComposto();
			
			String rootDir = null;
			
			/* Primo giro per catturare il Manifest */
			ServizioComposto manifesto = null;
			Enumeration<?> e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					//System.out.println("NAME["+nome+"] TIPO["+tipo+"]");
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO!");
						InputStream inputStream = zip.getInputStream(zipEntry);
						manifesto = it.gov.spcoop.sica.manifest.driver.XMLUtils.getManifestoSC(this.log,inputStream);
						inputStream.close();
						archivio.setManifesto(manifesto);
						break;
					}
				}
			}
			if(manifesto==null){
				throw new Exception("Manifest ServizioComposto non presente");
			}
			
			
			/* Lettura informazioni dal manifest */
			String interfacciaConcettuale = Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL;
			String interfaccialogicoErogatore = Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL;
			String interfaccialogicoFruitore = Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL;
			String conversazioneConcettuale = Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL;
			String conversazionelogicoErogatore = Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL;
			String conversazionelogicoFruitore = Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL;
			if(manifesto.getSpecificaInterfaccia()!=null){
				SpecificaInterfaccia interfaccia = manifesto.getSpecificaInterfaccia();
				if(interfaccia.getInterfacciaConcettuale()!=null){
					interfacciaConcettuale = ZipUtilities.getBaseName(interfaccia.getInterfacciaConcettuale().getBase());
				}
				if(interfaccia.getInterfacciaLogicaLatoErogatore()!=null){
					interfaccialogicoErogatore = ZipUtilities.getBaseName(interfaccia.getInterfacciaLogicaLatoErogatore().getBase());
				}
				if(interfaccia.getInterfacciaLogicaLatoFruitore()!=null){
					interfaccialogicoFruitore = ZipUtilities.getBaseName(interfaccia.getInterfacciaLogicaLatoFruitore().getBase());
				}
			}
			if(manifesto.getSpecificaConversazione()!=null){
				SpecificaConversazione conversazione = manifesto.getSpecificaConversazione();
				if(conversazione.getConversazioneConcettuale()!=null){
					conversazioneConcettuale = ZipUtilities.getBaseName(conversazione.getConversazioneConcettuale().getBase());
				}
				if(conversazione.getConversazioneLogicaLatoErogatore()!=null){
					conversazionelogicoErogatore = ZipUtilities.getBaseName(conversazione.getConversazioneLogicaLatoErogatore().getBase());
				}
				if(conversazione.getConversazioneLogicaLatoFruitore()!=null){
					conversazionelogicoFruitore = ZipUtilities.getBaseName(conversazione.getConversazioneLogicaLatoFruitore().getBase());
				}
			}
			
			
			/* Comprensione altri campi */
			e = zip.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
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
					//System.out.println("NAME["+nome+"] TIPO["+tipo+"]");
					
					//System.out.println("ENTRY ["+entryName+"]");
					if((rootDir+Costanti.PROJECT_CLIENT_SICA).equals(entryName)){
						//System.out.println(".PROJECT: NIENTE DA GESTIRE");
					}	
					else if((rootDir+Costanti.FIRMA_XML).equals(entryName)){
						//System.out.println("FIRMA!");
						Firma firma = new Firma();
						archivio.setFirma(firma);
					}			
					else if((rootDir+Costanti.MANIFESTO_XML).equals(entryName)){
						//System.out.println("MANIFESTO SCARTO!");
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR)) ){
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						String dir = rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar;
						if((dir+conversazioneConcettuale).equals(entryName)){
							//System.out.println("CONVERSAZIONE CONCETTUALE WSBL!");
							archivio.setConversazioneConcettuale(d);
						}
						else if((dir+conversazionelogicoErogatore).equals(entryName)){
							//System.out.println("CONVERSAZIONE LOGICA EROGATORE WSBL!");
							archivio.setConversazioneLogicaErogatore(d);
						}
						else if((dir+conversazionelogicoFruitore).equals(entryName)){
							//System.out.println("CONVERSAZIONE LOGICA FRUITORE WSBL!");
							archivio.setConversazioneLogicaFruitore(d);
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR)) ){
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						String dir = rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar;
						if((dir+interfacciaConcettuale).equals(entryName)){
							//System.out.println("INTERFACCIA CONCETTUALE WSDL!");
							archivio.setInterfacciaConcettuale(d);
						}
						else if((dir+interfaccialogicoErogatore).equals(entryName)){
							//System.out.println("INTERFACCIA LOGICA EROGATORE WSDL!");
							archivio.setInterfacciaLogicaLatoErogatore(d);
						}
						else if((dir+interfaccialogicoFruitore).equals(entryName)){
							//System.out.println("INTERFACCIA LOGICA FRUITORE WSDL!");
							archivio.setInterfacciaLogicaLatoFruitore(d);
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_COORDINAMENTO_DIR)) ){
						//System.out.println("COORDINAMENTO!");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaCoordinamento(d);
					}
					else if(entryName.startsWith((rootDir+Costanti.ALLEGATI_DIR)) ){
						//System.out.println("ALLEGATO");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addAllegato(d);
						
						// Validazione XSD delle informazioni egov
						if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.cnipa.collprofiles.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpDisabled_childUnqualified());
						}else if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.gov.spcoop.sica.wscp.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpEnabled_childUnqualified());
						}
					}
					else if(entryName.startsWith((rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR)) ){
						//System.out.println("SPECIFICA SEMIFORMALE");
						InputStream inputStream = zip.getInputStream(zipEntry);
						Documento d = new Documento(nome,tipo,inputStream);
						inputStream.close();
						archivio.addSpecificaSemiformale(d);
						
						// Validazione XSD delle informazioni egov
						if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.cnipa.collprofiles.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpDisabled_childUnqualified());
						}else if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(d.getContenuto())){
							it.gov.spcoop.sica.wscp.driver.XMLUtils.getDichiarazioneEGov(this.log,d.getContenuto(),
									this.sicaToOpenSPCoopContext.isInformazioniEGov_wscpEnabled_childUnqualified());
						}
					}
				}
				
	
			}
			
			// check
			if(archivio.getManifesto()==null){
				throw new Exception("Manifesto non presente");
			}
			
			// Metto a posto i tipi (le estensioni dei files possono essere diverse)
			if(archivio.getManifesto().getSpecificaSemiformale()!=null){
				for(int i=0; i<archivio.getManifesto().getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
					String fileName = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getBase();
					String tipo = archivio.getManifesto().getSpecificaSemiformale().getDocumentoSemiformale(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheSemiformali(); j++){
						if(fileName.equals(archivio.getSpecificaSemiformale(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaSemiformale(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaSemiformale(j).setTipo(tipo);
						}
					}
				}
			}
			if(archivio.getManifesto().getSpecificaCoordinamento()!=null){
				for(int i=0; i<archivio.getManifesto().getSpecificaCoordinamento().sizeDocumentoCoordinamentoList(); i++){
					String fileName = archivio.getManifesto().getSpecificaCoordinamento().getDocumentoCoordinamento(i).getBase();
					String tipo = archivio.getManifesto().getSpecificaCoordinamento().getDocumentoCoordinamento(i).getTipo();
					for(int j=0; j<archivio.sizeSpecificheCoordinamento(); j++){
						if(fileName.equals(archivio.getSpecificaCoordinamento(j).getNome())){
							//System.out.println("Metto a posto da ["+archivio.getSpecificaCoordinamento(j).getTipo()+"] a ["+tipo+"]");
							archivio.getSpecificaCoordinamento(j).setTipo(tipo);
						}
					}
				}
			}
			
			return archivio;

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Marshall dell'accordo di servizio composto----- */
	public void generateAccordoServizioComposto(AccordoServizioComposto manifesto,File file) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			generateAccordoServizioComposto(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void generateAccordoServizioComposto(AccordoServizioComposto manifesto,String fileName) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			generateAccordoServizioComposto(manifesto,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] generateAccordoServizioComposto(AccordoServizioComposto manifesto) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			generateAccordoServizioComposto(manifesto,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public void generateAccordoServizioComposto(AccordoServizioComposto manifesto,OutputStream out) throws XMLUtilsException{
		
		boolean generaSICAClientProjectFile = this.sicaToOpenSPCoopContext.isSICAClient_generaProject();
		boolean includiInfoRegistroGenerale = this.sicaToOpenSPCoopContext.isSICAClient_includiInfoRegistroGenerale();
		boolean lunghezzaNomeAccordoLimitata = this.sicaToOpenSPCoopContext.isSICAClient_nomeAccordo_32CaratteriMax();
		
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootDir = manifesto.getManifesto().getNome()+File.separatorChar;
			
			// .project
			if(generaSICAClientProjectFile){
				String nomeProgettoEclipse =  manifesto.getManifesto().getNome() + "_" + Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO;
				String projectContenuto = Costanti.PROJECT_CLIENT_SICA_CONTENUTO.replace(Costanti.PROJECT_CLIENT_SICA_KEY_NOME, nomeProgettoEclipse);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.PROJECT_CLIENT_SICA));
				zipOut.write(projectContenuto.getBytes());
			}
			
			// firma
			if(manifesto.getFirma()!=null){
				byte[] firma = manifesto.getFirma().getBytes();
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.FIRMA_XML));
				zipOut.write(firma);
			}
			
			// manifest
			byte[] manifestoBytes = it.gov.spcoop.sica.manifest.driver.XMLUtils.generateManifestoSC(manifesto.getManifesto(),includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata);
			zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.MANIFESTO_XML));
			zipOut.write(manifestoBytes);
			
			// Specifica Interfaccia
			boolean specificaInterfaccia = false;
			if(manifesto.getInterfacciaConcettuale()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL));
				zipOut.write(manifesto.getInterfacciaConcettuale().getContenuto());
				specificaInterfaccia = true;
			}
			if(manifesto.getInterfacciaLogicaLatoErogatore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL));
				zipOut.write(manifesto.getInterfacciaLogicaLatoErogatore().getContenuto());
				specificaInterfaccia = true;
			}
			if(manifesto.getInterfacciaLogicaLatoFruitore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL));
				zipOut.write(manifesto.getInterfacciaLogicaLatoFruitore().getContenuto());
				specificaInterfaccia = true;
			}
			if(!specificaInterfaccia){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar));
			}
			
			// Specifica Conversazioni
			boolean specificaConversazioni = false;
			if(manifesto.getConversazioneConcettuale()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL));
				zipOut.write(manifesto.getConversazioneConcettuale().getContenuto());
				specificaConversazioni = true;
			}
			if(manifesto.getConversazioneLogicaErogatore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL));
				zipOut.write(manifesto.getConversazioneLogicaErogatore().getContenuto());
				specificaConversazioni = true;
			}
			if(manifesto.getConversazioneLogicaFruitore()!=null){
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar+Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL));
				zipOut.write(manifesto.getConversazioneLogicaFruitore().getContenuto());
				specificaConversazioni = true;
			}
			if(!specificaConversazioni){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_CONVERSAZIONE_DIR+File.separatorChar));
			}
			
			// SpecificaCoordinamento
			boolean specificheCoordinameno = manifesto.sizeSpecificheCoordinamento()>0;
			for(int i=0; i<manifesto.sizeSpecificheCoordinamento(); i++){
				Documento doc = manifesto.getSpecificaCoordinamento(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_COORDINAMENTO_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheCoordinameno){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_COORDINAMENTO_DIR+File.separatorChar));
			}
			
			// Allegati
			boolean allegati = manifesto.sizeAllegati()>0;
			for(int i=0; i<manifesto.sizeAllegati(); i++){
				Documento allegato = manifesto.getAllegato(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar+allegato.getNome()));
				zipOut.write(allegato.getContenuto());
			}
			if(!allegati){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.ALLEGATI_DIR+File.separatorChar));
			}
			
			// Specifiche semiformali
			boolean specificheSemiformali = manifesto.sizeSpecificheSemiformali()>0;
			for(int i=0; i<manifesto.sizeSpecificheSemiformali(); i++){
				Documento doc = manifesto.getSpecificaSemiformale(i);
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+doc.getNome()));
				zipOut.write(doc.getContenuto());
			}
			if(!specificheSemiformali){
				// La directory deve cmq esistere, al massimo sara' vuota
				zipOut.putNextEntry(new ZipEntry(rootDir+Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar));
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
}