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


package org.openspcoop2.core.registry.wsdl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.wsdl.Import;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utilities per la gestione dei bean accordi di servizio di OpenSPCoop
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RegistroOpenSPCoopUtilities {

	private Logger logger = null;
	private org.openspcoop2.message.XMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	private WSDLUtilities wsdlUtilities = null;
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
	
	public RegistroOpenSPCoopUtilities(Logger log){
		if(log!=null)
			this.logger = log;
		else
			this.logger = LoggerWrapperFactory.getLogger(RegistroOpenSPCoopUtilities.class);
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
	}
	
	
	
	/**
	 * Costruisce un Definition a partire da un accordo di servizio parte comune e eventualmente dai bytes del wsdl implementativo
	 * 
	 * @param parteComune
	 * @param implementativoByte
	 * @param implementativoErogatore
	 * @return javax.wsdl.Definition
	 * @throws DriverRegistroServiziException
	 */
	public javax.wsdl.Definition buildWsdlFromObjects(AccordoServizioParteComune parteComune, byte[] implementativoByte, boolean implementativoErogatore) throws DriverRegistroServiziException{
		try{
			
			// Normalizzo l'accordo di servizio per avere i path relativi basati sul Registro dei Servizi di OpenSPCoop
			AccordoServizioParteComune parteComuneNormalizzata = (AccordoServizioParteComune) parteComune.clone();
			this.updateLocation(parteComuneNormalizzata, false, false);
			
			// Seleziona wsdl logico in base all'erogatore o fruitore
			byte[] logicoByte = null;
			if(implementativoErogatore){
				if(parteComuneNormalizzata.getByteWsdlLogicoErogatore()!=null){
					logicoByte = parteComuneNormalizzata.getByteWsdlLogicoErogatore();
				}else{
					throw new DriverRegistroServiziException("Byte del WSDL logico erogatore non presenti");
				}
			}else{
				if(parteComuneNormalizzata.getByteWsdlLogicoFruitore()!=null){
					logicoByte = parteComuneNormalizzata.getByteWsdlLogicoFruitore();
				}else{
					throw new DriverRegistroServiziException("Byte del WSDL logico fruitore non presenti");
				}
			}
			
			// Riporto in document il wsdl logico contenente gli import
			com.ibm.wsdl.xml.WSDLReaderImpl builderWSDL = new com.ibm.wsdl.xml.WSDLReaderImpl();
			javax.wsdl.Definition wsdl = null;
			if(logicoByte==null){
				throw new Exception("Wsdl logico (byte[]) non definito");
			}
			this.logger.debug("Leggo WSDL logico");
			Document documentLogico = this.xmlUtils.newDocument(logicoByte);

			
							
			// Gestione import presenti nel wsdl logico
			Element wsdlElement = documentLogico.getDocumentElement();
			String prefix = wsdlElement.getPrefix();
			ByteArrayOutputStream xsd = new ByteArrayOutputStream();
			HashMap<String,String> prefixForWSDL = new HashMap<String, String>();
						
			
			
			// NOTA: Avendo normalizzato il wsdl, tutti gli import/include sono dentro i types
			
			List<Node> importIntoWSDL = this.wsdlUtilities.readImportsSchemaIntoTypes(documentLogico);
			//System.out.println("IMPORTS ["+importIntoWSDL.size()+"]");
			for(int i=0; i<importIntoWSDL.size(); i++){
				Node n = importIntoWSDL.get(i);
				//System.out.println("************************************* IMPORT *******************************************************************");
				/*String targetNamespaceXSD =  null;
				Object o = n.getUserData("TargetNamespaceSchema"); 
				if(o!=null){
					targetNamespaceXSD = (String)o;
				}*/
				//NON DEVO PRENDERE QUELLO DELLO SCHEMA MA QUELLO DEL XSD
				String namespaceImport = null;
				try{
					namespaceImport = this.xsdUtils.getImportNamespace(n);
				}catch(Exception e){}
				//System.out.println("TargetNamespace schema xsd che contiene l'import: "+targetNamespaceXSD);
				String location = null;
				try{
					location = this.xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Import WSDL ["+namespaceImport+"] ["+location+"]");
				if(location!=null){
					//System.out.println("IMPORT XSD INTO WSDL!");
					if(CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD.equals(location)){
						if(parteComuneNormalizzata.getByteWsdlDefinitorio()!=null){
							//System.out.println("DA WSDL AGGIUNGO WSDL DEFINITORIO");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getByteWsdlDefinitorio(), 
									xsd, wsdlElement,prefixForWSDL, true, namespaceImport, 1);
						}
					}
					else if(location.startsWith(CostantiRegistroServizi.ALLEGATI_DIR)){
						for(int j=0; j<parteComuneNormalizzata.sizeAllegatoList(); j++){
							Documento allegato = parteComuneNormalizzata.getAllegato(j);
							String file = CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+allegato.getFile();
							//System.out.println("Check allegato.. ["+location+"]==["+file+"]");
							if(location.equals(file)){
								//System.out.println("DA WSDL AGGIUNGO ALLEGATO");
								xsd.write("\n".getBytes());
								risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getAllegato(j).getByteContenuto(), 
										xsd, wsdlElement,prefixForWSDL, true, namespaceImport, 1);
								break;
							}
						}
					}
					else if(location.startsWith(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR)){
						for(int j=0; j<parteComuneNormalizzata.sizeSpecificaSemiformaleList(); j++){
							Documento specificaSemiformale = parteComuneNormalizzata.getSpecificaSemiformale(j);
							String file = CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+specificaSemiformale.getFile();
							//System.out.println("Check specifica.. ["+location+"]==["+file+"]");
							if(location.equals(file)){
								//System.out.println("DA WSDL AGGIUNGO SPECIFICA SEMIFORMALE");
								xsd.write("\n".getBytes());
								risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getSpecificaSemiformale(j).getByteContenuto(), 
										xsd, wsdlElement,prefixForWSDL, true, namespaceImport, 1);
								break;
							}
						}
					}
				}
				//System.out.println("*******************************************  IMPORT  *************************************************************");
			}
			
			
			// NOTA: Avendo normalizzato il wsdl, tutti gli import/include sono dentro i types
			
			List<Node> includeIntoWSDL = this.wsdlUtilities.readIncludesSchemaIntoTypes(documentLogico);
			//System.out.println("INCLUDE ["+includeIntoWSDL.size()+"]");
			for(int i=0; i<includeIntoWSDL.size(); i++){
				Node n = includeIntoWSDL.get(i);
				//System.out.println("************************************* INCLUDE *******************************************************************");
				Object o = n.getUserData("TargetNamespaceSchema"); 
				String targetNamespaceXSD =  null;
				if(o!=null){
					targetNamespaceXSD = (String)o;
				}
				//System.out.println("TargetNamespace schema xsd che contiene l'include: "+targetNamespaceXSD);
				String location = null;
				try{
					location = this.xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Include WSDL ["+location+"]");
				if(location!=null){
					//System.out.println("IMPORT XSD INTO WSDL!");
					if(CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD.equals(location)){
						if(parteComuneNormalizzata.getByteWsdlDefinitorio()!=null){
							//System.out.println("DA WSDL AGGIUNGO WSDL DEFINITORIO");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getByteWsdlDefinitorio(), 
									xsd, wsdlElement,prefixForWSDL, false, targetNamespaceXSD, 1);
						}
					}
					else if(location.startsWith(CostantiRegistroServizi.ALLEGATI_DIR)){
						for(int j=0; j<parteComuneNormalizzata.sizeAllegatoList(); j++){
							Documento allegato = parteComuneNormalizzata.getAllegato(j);
							String file = CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+allegato.getFile();
							//System.out.println("Check allegato.. ["+location+"]==["+file+"]");
							if(location.equals(file)){
								//System.out.println("DA WSDL AGGIUNGO ALLEGATO");
								xsd.write("\n".getBytes());
								risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getAllegato(j).getByteContenuto(), 
										xsd, wsdlElement,prefixForWSDL, false, targetNamespaceXSD, 1);
								break;
							}
						}
					}
					else if(location.startsWith(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR)){
						for(int j=0; j<parteComuneNormalizzata.sizeSpecificaSemiformaleList(); j++){
							Documento specificaSemiformale = parteComuneNormalizzata.getSpecificaSemiformale(j);
							String file = CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+specificaSemiformale.getFile();
							//System.out.println("Check specifica.. ["+location+"]==["+file+"]");
							if(location.equals(file)){
								//System.out.println("DA WSDL AGGIUNGO SPECIFICA SEMIFORMALE");
								xsd.write("\n".getBytes());
								risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getSpecificaSemiformale(j).getByteContenuto(), 
										xsd, wsdlElement,prefixForWSDL, false, targetNamespaceXSD, 1);
								break;
							}
						}
					}
				}
				//System.out.println("*******************************************  INCLUDE  *************************************************************");
			}
						
			if(this.wsdlUtilities.existsTypes(documentLogico)){
				//System.out.println("Rimozione Schemi from types");
				
				// NOTA: Non è possibile lasciare gli schemi interni perche contengono include ed import
				// Inizialmente venivano rimossi solo gli schemi
				// Se però non è abilitata l'opzione dalla console che estra gli schemi da dentro il wsdl,
				// poi il wsdl non conteneva tutti gli schemi una volta effettuata questa normalizzazione
				// non venivano re-inseriti gli schemi definiti dentro il wsdl stesso
				//this.wsdlUtilities.removeSchemiIntoTypes(documentLogico);
				
				List<Node> schemi = this.wsdlUtilities.getSchemiXSD(documentLogico);
				if(schemi!=null && schemi.size()>0){
					// Gli import ed includes sono già stati gestiti precedentemente
					for (Node schema : schemi) {
						boolean schemaWithOnlyImportOrIncludes = this.xsdUtils.isSchemaWithOnlyImportsAndIncludes(schema);
						if(schemaWithOnlyImportOrIncludes){
							this.wsdlUtilities.getIfExistsTypesElementIntoWSDL(documentLogico).removeChild(schema);
						}
						else{
							// Elimino import e include
							List<Node> nl = this.xsdUtils.readImportsAndIncludes(schema);
							if(nl!=null){
								for (Node importInclude : nl) {
									schema.removeChild(importInclude);
								}
							}
						}
					}
				}
				
			}
			else{
				//System.out.println("Add Empty types");	
				this.wsdlUtilities.addEmptyTypesIfNotExists(documentLogico);
			}
								
			// Costruisco wsdl logico che non contiene piu' gli import 
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(this.xmlUtils.toByteArray(documentLogico));
			bout.flush();
			bout.close();
						
			// Calcolo keyword
			String typesStart = null;
			String typesClosed = null;
			String typesEnd = null;
			String definitionLine = null;
			if(prefix!=null){
				typesStart = "<"+prefix+":types>";
				typesClosed = "<"+prefix+":types/>";
				typesEnd = "</"+prefix+":types>";
				definitionLine = "<"+prefix+":definitions ";
			}else{
				typesStart = "<types>";
				typesClosed = "<types/>";
				typesEnd = "</types>";
				definitionLine = "<definitions ";
			}
			
			//System.out.println("KEYWORD typesLine1["+typesStart+"] typesLine2["+typesClosed+"] definitionLine["+definitionLine+"]");
			
			// Aggiungo dentro types gli schemi trovati nelle precedenti analisi degli import
			String wsdlTrasformato = bout.toString();
			if(wsdlTrasformato.contains(typesStart)){
				//System.out.println("REPLACE1");
				wsdlTrasformato = wsdlTrasformato.replace(typesStart, typesStart+"\n"+xsd.toString());
			}
			else{
				//System.out.println("REPLACE2 contains("+wsdlTrasformato.contains(typesClosed)+")");
				wsdlTrasformato = wsdlTrasformato.replace(typesClosed, typesStart+"\n"+xsd.toString()+"\n\t"+typesEnd);
			}
			
			// Elimino <?xml
			if(wsdlTrasformato.contains("<?xml") ){
				//System.out.println("XML INSTR");
				int indexOf = wsdlTrasformato.indexOf("<?xml");
				int endIndexOf = wsdlTrasformato.indexOf(">",indexOf+"<?xml".length());
				//System.out.println("XML INSTR indexOf["+indexOf+"] endIndexOf["+endIndexOf+"]");
				if(endIndexOf>0 && indexOf>=0){
					if(indexOf>0){
						//System.out.println("XML INSTR A");
						wsdlTrasformato = wsdlTrasformato.substring(0, indexOf)+wsdlTrasformato.substring(endIndexOf+1);
					}
					else{
						//System.out.println("XML INSTR B");
						wsdlTrasformato = wsdlTrasformato.substring(endIndexOf+1);
					}
				}
			}
			
			// Aggiunto prefissi al wsdl
			Iterator<String> keys = prefixForWSDL.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				//System.out.println("ADD ["+key+"] ["+prefixForWSDL.get(key)+"]");
				wsdlTrasformato = wsdlTrasformato.replaceFirst(definitionLine, definitionLine+key+"=\""+prefixForWSDL.get(key)+"\" ");
			}
			
//			File f = File.createTempFile("aaaaaaaaaaa", ".tmp");
//			org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, wsdlTrasformato.getBytes());
//			System.out.println("TEST PERCHE SCHIANTAAAAAAAA ["+f.getAbsolutePath()+"] ["+wsdlTrasformato+"]");
			
			// Costruisco wsdl logico contenente gli schemi
			documentLogico = this.xmlUtils.newDocument(wsdlTrasformato.getBytes());
			this.logger.debug("Costruisco WSDL Logico per la seconda volta, stavolta con i types corretti");
			wsdl = builderWSDL.readWSDL(null,documentLogico);
			
			
			// Aggiungo parte Implementativa
			if(implementativoByte!=null){
				
				this.logger.debug("Leggo WSDL implementativo");
				Document documentImplementativo = this.xmlUtils.newDocument(implementativoByte);
				
				this.logger.debug("Rimuovo import da WSDL Loimplementativogico");
				this.wsdlUtilities.removeImports(documentImplementativo);
				this.wsdlUtilities.removeTypes(documentImplementativo);
				
				this.logger.debug("Costruisco WSDL Implementativo");
				javax.wsdl.Definition wsdlImplementativo = builderWSDL.readWSDL(null,documentImplementativo);
				
				this.addParteImplementativa(wsdl, wsdlImplementativo);
			}
			
			return wsdl;
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la costruzione del wsdl dai bytes: "+e.getMessage(),e);
		}
	}
	
	
	/**
	 * Imposta correttamente le location degli imports nell'accordo di servizio parte comune
	 * 
	 * @param accordoServizioOpenspcoop
	 * @param strutturaPackageCNIPA
	 * @param prettyDocument
	 * @throws DriverRegistroServiziException
	 */
	public void updateLocation(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizioOpenspcoop,boolean strutturaPackageCNIPA, boolean prettyDocument) throws DriverRegistroServiziException{
		try{
			// WSDL
			byte[]wsdlDefinitorio = accordoServizioOpenspcoop.getByteWsdlDefinitorio();
			byte[]wsdlConcettuale = accordoServizioOpenspcoop.getByteWsdlConcettuale();
			byte[]wsdlLogicoErogatore = accordoServizioOpenspcoop.getByteWsdlLogicoErogatore();
			byte[]wsdlLogicoFruitore = accordoServizioOpenspcoop.getByteWsdlLogicoFruitore();
			
			// Associazione TargetNamespace a path dei files.
			// Associazione path dei files per risoluzione include:
			// - nome del file singolo
			// - nome del file con il parent.
			HashMap<String, String> targetNamespacesXSD = new HashMap<String, String>();
			HashMap<String, String> includePath = new HashMap<String, String>();
			if(wsdlDefinitorio!=null){
				String file = CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD;
				if(strutturaPackageCNIPA){
					file = ".."+File.separatorChar+CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+file;
				}
				// Associazione TargetNamespace a path dei files.
				String targetNamespace = null;
				try{
					targetNamespace = this.xsdUtils.getTargetNamespace(wsdlDefinitorio);
				}catch(Exception e){}
				if(targetNamespace!=null){
					//System.out.println("TARGET["+targetNamespace+"] FILE["+file+"]");
					targetNamespacesXSD.put(targetNamespace,file);
				}else{
					this.logger.debug("Target namespace non trovato per il wsdl definitorio ["+CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD+"]");
				}
				// Associazione path dei files per risoluzione include
				includePath.put(CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD, file);
				includePath.put(CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD, file);
			}else{
				this.logger.debug("Bytes non presenti per il wsdl definitorio ["+CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD+"]");
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeAllegatoList();i++){
				Documento doc = accordoServizioOpenspcoop.getAllegato(i);
				File fLocalName = new File(doc.getFile());
				String file = CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+fLocalName.getName();
				if(strutturaPackageCNIPA){
					file = ".."+File.separatorChar+file;
				}
				
				//System.out.println("UPDATE LOCATION ALLEGATO["+file+"]");
				
				// Associazione TargetNamespace a path dei files.
				if(doc.getByteContenuto()!=null){
					String targetNamespace = null;
					try{
						targetNamespace = this.xsdUtils.getTargetNamespace(doc.getByteContenuto()); // verifico che sia un xsd
					}catch(Exception e){}
					if(targetNamespace==null){
						try{
							targetNamespace = this.wsdlUtilities.getTargetNamespace(doc.getByteContenuto()); // verifico che sia un wsdl
						}catch(Exception e){}
					}
					//System.out.println("TARGET NAMESPACE["+targetNamespace+"]");
					if(targetNamespace!=null){
						//System.out.println("TARGET["+targetNamespace+"] FILE["+file+"]");
						targetNamespacesXSD.put(targetNamespace,file);
					}else{
						this.logger.debug("Target namespace non trovato per l'allegato ["+doc.getFile()+"] (tipo:"+doc.getTipo()+")");
					}
				}else{
					this.logger.debug("Bytes non presenti per l'allegato ["+doc.getFile()+"] (tipo:"+doc.getTipo()+")");
				}
				// Associazione path dei files per risoluzione include
				includePath.put(fLocalName.getName(), file);
				includePath.put(CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+fLocalName.getName(), file);
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeSpecificaSemiformaleList();i++){
				Documento doc = accordoServizioOpenspcoop.getSpecificaSemiformale(i);
				File fLocalName = new File(doc.getFile());
				String file = CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fLocalName.getName();
				if(strutturaPackageCNIPA){
					file = ".."+File.separatorChar+file;
				}
				// Associazione TargetNamespace a path dei files.
				if(doc.getByteContenuto()!=null){
					String targetNamespace = null;
					try{
						targetNamespace = this.xsdUtils.getTargetNamespace(doc.getByteContenuto()); // verifico che sia un xsd
					}catch(Exception e){}
					if(targetNamespace==null){
						try{
							targetNamespace = this.wsdlUtilities.getTargetNamespace(doc.getByteContenuto()); // verifico che sia un wsdl
						}catch(Exception e){}
					}
					if(targetNamespace!=null){
						//System.out.println("TARGET["+targetNamespace+"] FILE["+file+"]");
						targetNamespacesXSD.put(targetNamespace,file);
					}else{
						this.logger.debug("Target namespace non trovato per la specifica semiformale ["+doc.getFile()+"] (tipo:"+doc.getTipo()+")");
					}
				}else{
					this.logger.debug("Bytes non presenti per la specifica semiformale ["+doc.getFile()+"] (tipo:"+doc.getTipo()+")");
				}
				// Associazione path dei files per risoluzione include
				includePath.put(fLocalName.getName(), file);
				includePath.put(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+fLocalName.getName(), file);
			}
			
			// WSDL
			if(wsdlConcettuale!=null){
				this.logger.debug("Update Wsdl Concettuale...");
				wsdlConcettuale = updateImportsAndIncludesWSDLLocation("WSDLConcettuale",wsdlConcettuale,targetNamespacesXSD,includePath,prettyDocument,true);
				accordoServizioOpenspcoop.setByteWsdlConcettuale(wsdlConcettuale);
				this.logger.debug("Update Wsdl Concettuale ok");
			}
			if(wsdlLogicoErogatore!=null){
				this.logger.debug("Update Wsdl Logico Erogatore...");
				wsdlLogicoErogatore = updateImportsAndIncludesWSDLLocation("WSDLLogicoErogatore",wsdlLogicoErogatore,targetNamespacesXSD,includePath,prettyDocument,true);
				accordoServizioOpenspcoop.setByteWsdlLogicoErogatore(wsdlLogicoErogatore);
				this.logger.debug("Update Wsdl Logico Erogatore ok");
			}
			if(wsdlLogicoFruitore!=null){
				this.logger.debug("Update Wsdl Logico Fruitore...");
				wsdlLogicoFruitore = updateImportsAndIncludesWSDLLocation("WSDLLogicoFruitore",wsdlLogicoFruitore,targetNamespacesXSD,includePath,prettyDocument,true);
				accordoServizioOpenspcoop.setByteWsdlLogicoFruitore(wsdlLogicoFruitore);
				this.logger.debug("Update Wsdl Logico Fruitore ok");
			}

			// XSD
			if(wsdlDefinitorio!=null){
				this.logger.debug("Update Wsdl Definitorio (import)...");
				wsdlDefinitorio = updateImportXSDLocation(wsdlDefinitorio,targetNamespacesXSD,prettyDocument);
				this.logger.debug("Update Wsdl Definitorio (include)...");
				wsdlDefinitorio = updateIncludeXSDLocation(wsdlDefinitorio,includePath,prettyDocument);
				this.logger.debug("Update Wsdl Definitorio ok");
				accordoServizioOpenspcoop.setByteWsdlDefinitorio(wsdlDefinitorio);
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeAllegatoList();i++){
				Documento doc = accordoServizioOpenspcoop.getAllegato(i);
				if(doc.getByteContenuto()!=null){
					this.logger.debug("Update Allegato di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] (import)...");
					byte [] allegato = updateImportXSDLocation(doc.getByteContenuto(),targetNamespacesXSD,prettyDocument);
					if(allegato!=null){
						this.logger.debug("Update Allegato di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] (include)...");
						allegato = updateIncludeXSDLocation(allegato,includePath,prettyDocument);
						if(allegato!=null){
							doc.setByteContenuto(allegato);
							this.logger.debug("Update Allegato di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] ok");
						}else{
							this.logger.debug("Update (include) Allegato di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] non effettuato, il documento non e' uno schema XSD");
						}
					}else{
						this.logger.debug("Update (import) Allegato di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] non effettuato, il documento non e' uno schema XSD");
					}
				}
			}
			for(int i=0; i<accordoServizioOpenspcoop.sizeSpecificaSemiformaleList();i++){
				Documento doc = accordoServizioOpenspcoop.getSpecificaSemiformale(i);
				if(doc.getByteContenuto()!=null){
					this.logger.debug("Update SpecificaSemiformale di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] (import)...");
					byte [] specificaSemiformale = updateImportXSDLocation(doc.getByteContenuto(),targetNamespacesXSD,prettyDocument);
					if(specificaSemiformale!=null){
						this.logger.debug("Update SpecificaSemiformale di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] (include)...");
						specificaSemiformale = updateIncludeXSDLocation(specificaSemiformale,includePath,prettyDocument);
						if(specificaSemiformale!=null){
							doc.setByteContenuto(specificaSemiformale);
							this.logger.debug("Update SpecificaSemiformale di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] ok");
						}else{
							this.logger.debug("Update (include) SpecificaSemiformale di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] non effettuato, il documento non e' uno schema XSD");
						}
					}else{
						this.logger.debug("Update (import)SpecificaSemiformale di tipo ["+doc.getTipo()+"] ["+doc.getFile()+"] non effettuato, il documento non e' uno schema XSD");
					}
				}
			}
			
			
		}catch(Exception e){
			this.logger.error("Correzione import non riuscita: "+e.getMessage(),e);
			
			throw new DriverRegistroServiziException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Imposta correttamente le location degli imports nell'accordo di servizio parte specifica
	 * 
	 * @param servizioOpenspcoop
	 * @param strutturaPackageCNIPA
	 * @param prettyDocument
	 * @throws DriverRegistroServiziException
	 */
	public void updateLocation(org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioOpenspcoop,boolean strutturaPackageCNIPA, boolean prettyDocument) throws DriverRegistroServiziException{
		try{
			// WSDL
			byte[]wsdlImplementativoErogatore = servizioOpenspcoop.getByteWsdlImplementativoErogatore();
			byte[]wsdlImplementativoFruitore = servizioOpenspcoop.getByteWsdlImplementativoFruitore();
			
			HashMap<String, String> targetNamespacesXSD = new HashMap<String, String>();
			IDAccordo idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromUri(servizioOpenspcoop.getAccordoServizioParteComune());			
			
			if(wsdlImplementativoErogatore!=null){
				String targetNamespace = this.wsdlUtilities.getTargetNamespace(wsdlImplementativoErogatore);
				if(targetNamespace!=null){
					//System.out.println("TARGET["+targetNamespace+"] FILE["+file+"]");
					String file = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL;
					if(strutturaPackageCNIPA){
						file = ".."+File.separatorChar+".."+File.separatorChar+idAccordoParteComune.getNome()+File.separatorChar+CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+file;
					}
					targetNamespacesXSD.put(targetNamespace,file);
				}else{
					this.logger.debug("Target namespace non trovato per il wsdl implementativo erogatore ["+CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL+"]");
				}
				
				this.logger.debug("Update Wsdl Implementativo Erogatore...");
				wsdlImplementativoErogatore = updateImportsAndIncludesWSDLLocation("WSDLImplementativoErogatore",wsdlImplementativoErogatore,targetNamespacesXSD,null,prettyDocument,false);
				servizioOpenspcoop.setByteWsdlImplementativoErogatore(wsdlImplementativoErogatore);
				this.logger.debug("Update Wsdl Implementativo Erogatore ok");
			}
			if(wsdlImplementativoFruitore!=null){
				String targetNamespace = this.wsdlUtilities.getTargetNamespace(wsdlImplementativoFruitore);
				if(targetNamespace!=null){
					//System.out.println("TARGET["+targetNamespace+"] FILE["+file+"]");
					String file = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL;
					if(strutturaPackageCNIPA){
						file = ".."+File.separatorChar+".."+File.separatorChar+idAccordoParteComune.getNome()+File.separatorChar+CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+file;
					}
					targetNamespacesXSD.put(targetNamespace,file);
				}else{
					this.logger.debug("Target namespace non trovato per il wsdl implementativo fruitore ["+CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL+"]");
				}
				
				this.logger.debug("Update Wsdl Implementativo Fruitore...");
				wsdlImplementativoFruitore = updateImportsAndIncludesWSDLLocation("WSDLImplementativoFruitore",wsdlImplementativoFruitore,targetNamespacesXSD,null,prettyDocument,false);
				servizioOpenspcoop.setByteWsdlImplementativoFruitore(wsdlImplementativoFruitore);
				this.logger.debug("Update Wsdl Implementativo Fruitore ok");
			}
			
			
		}catch(Exception e){
			this.logger.error("Correzione import non riuscita: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Riscontrato errore durante la lettura del wsdl: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ************ UTILITY **************** */
	
	public byte[] eliminaASParteComune(byte[] wsdlBytes,boolean implementativoErogatore) throws DriverRegistroServiziException{
		
		try{
		
			Document d = this.xmlUtils.newDocument(wsdlBytes);
			this.wsdlUtilities.removeTypes(d);
			this.wsdlUtilities.removeImports(d);
					
			DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils);
				
			wsdl.createTypes();
			Import importLogico = wsdl.createImport();
			if(implementativoErogatore){
				importLogico.setLocationURI(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			}
			else{
				importLogico.setLocationURI(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			}
			importLogico.setNamespaceURI(this.wsdlUtilities.getTargetNamespace(wsdlBytes));
			wsdl.addImport(importLogico);
			
			wsdl.removeAllMessages();
			wsdl.removeAllPortTypes();
			
			//System.out.println(wsdl.toString());
	
			// serializzo wsdl 
			byte[] wsdlSenzaParteComune = wsdl.toByteArray();			
			return wsdlSenzaParteComune;
			
		}catch(Exception e){
			this.logger.error("Eliminazione  ASParteComune non riuscita: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Riscontrato errore durante l'eliminazione dell'ASParteComune del wsdl: "+e.getMessage(),e);
		}
	}
	
	public byte[] aggiungiImportASParteComune(byte[] wsdlBytes,boolean implementativoErogatore) throws DriverRegistroServiziException{
		
		try{
		
			Document d = this.xmlUtils.newDocument(wsdlBytes);
			this.wsdlUtilities.removeTypes(d);
			this.wsdlUtilities.removeImports(d);
			
			DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils);
			
			wsdl.createTypes();
			Import importLogico = wsdl.createImport();
			if(implementativoErogatore){
				importLogico.setLocationURI(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			}
			else{
				importLogico.setLocationURI(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
			}
			importLogico.setNamespaceURI(this.wsdlUtilities.getTargetNamespace(wsdlBytes));
			wsdl.addImport(importLogico);
			
			return wsdl.toByteArray();
			
		}catch(Exception e){
			this.logger.error("aggiungiImportASParteComune non riuscito: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Riscontrato errore durante l'eliminazione dell'ASParteComune del wsdl: "+e.getMessage(),e);
		}
		
	}
	
	public byte[] eliminaImportASParteComune(byte[] wsdlBytes) throws DriverRegistroServiziException{
		
		try{
		
			Document d = this.xmlUtils.newDocument(wsdlBytes);
			this.wsdlUtilities.removeTypes(d);
			this.wsdlUtilities.removeImports(d);
			
			DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils);
			return wsdl.toByteArray();
			
		}catch(Exception e){
			this.logger.error("Eliminazione  ASParteComune non riuscita: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Riscontrato errore durante l'eliminazione dell'ASParteComune del wsdl: "+e.getMessage(),e);
		}
		
	}
	
	/**
	 * Aggiunge i bindings/services di un wsdl implementativo in un wsdl logico.
	 * 
	 * @param wsdlOriginale
	 * @param wsdlImplementativo
	 */
	public void addParteImplementativa(javax.wsdl.Definition wsdlOriginale,javax.wsdl.Definition wsdlImplementativo){
		this.logger.debug("Aggiungo wsdl-binding...");
		java.util.Map<?,?> bindings = wsdlImplementativo.getAllBindings();
		if(bindings!=null && bindings.size()>0){
			this.logger.debug("Aggiungo wsdl-binding ["+bindings.size()+"] a wsdl ritornato");
			java.util.Iterator<?> bindingsIterator = bindings.values().iterator();
			while(bindingsIterator.hasNext()) {
				javax.wsdl.Binding bindingWSDL = (javax.wsdl.Binding) bindingsIterator.next();
				wsdlOriginale.addBinding(bindingWSDL);
			}
		}
		
		this.logger.debug("Aggiungo wsdl-service...");
		java.util.Map<?,?> services = wsdlImplementativo.getAllServices();
		if(services!=null && services.size()>0){
			this.logger.debug("Aggiungo wsdl-service ["+services.size()+"] a wsdl ritornato");
			java.util.Iterator<?> servicesIterator = services.values().iterator();
			while(servicesIterator.hasNext()) {
				javax.wsdl.Service serviceWSDL = (javax.wsdl.Service) servicesIterator.next();
				wsdlOriginale.addService(serviceWSDL);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* ---------------- METODI PRIVATI ------------------------- */
	
	/** Risoluzione import include */
	private void risoluzioneImportIncludeInXSD(AccordoServizioParteComune parteComuneNormalizzata,byte[] documentXSD,ByteArrayOutputStream xsd,
			Element wsdlElement,HashMap<String,String> prefixForWSDL,boolean docImportato,String targetNamespaceParent,int profondita)throws DriverRegistroServiziException{
		try{
			
			Document doc = this.xmlUtils.newDocument(documentXSD);
			Element docElement = doc.getDocumentElement();
			String targetNamespace = null;
			
			
			
			
			
			// Risoluzione prefix da inserire nel wsdl ed eliminazione attributi negli schemi
			String uniquePrefix = "_p"+profondita+"_n"+IDUtilities.getUniqueSerialNumber()+"_";
			targetNamespace = this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(docElement, wsdlElement, prefixForWSDL, uniquePrefix, docImportato, targetNamespaceParent);
			
			
			

			
			
			// Risoluzione ricorsiva degli import presenti nel xsd
			List<Node> importIntoXSD = this.xsdUtils.readImports(doc);
			for(int i=0; i<importIntoXSD.size(); i++){
				Node n = importIntoXSD.get(i);
				//System.out.println("----------------------------------- IMPORT ("+profondita+")------------------------------------------------------------------------");
				String location = null;
				try{
					location = this.xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Import XSD ["+this.wsdlUtilities.getAttributeValue(n,"namespace")+"] ["+location+"]");
				//System.out.println("IMPORT ["+location+"]");
				if(location.startsWith(CostantiRegistroServizi.ALLEGATI_DIR)){
					for(int j=0; j<parteComuneNormalizzata.sizeAllegatoList(); j++){
						Documento allegato = parteComuneNormalizzata.getAllegato(j);
						String file = CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+allegato.getFile();
						//System.out.println("Check allegato.. ["+location+"]==["+file+"]");
						if(location.equals(file)){
							//System.out.println("AGGIUNGO ALLEGATO INTERNO");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getAllegato(j).getByteContenuto(), 
									xsd, wsdlElement,prefixForWSDL, true, targetNamespace, (profondita+1));
							break;
						}
					}
				}
				else if(location.startsWith(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR)){
					for(int j=0; j<parteComuneNormalizzata.sizeSpecificaSemiformaleList(); j++){
						Documento specificaSemiformale = parteComuneNormalizzata.getSpecificaSemiformale(j);
						String file = CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+specificaSemiformale.getFile();
						//System.out.println("Check specifica.. ["+location+"]==["+file+"]");
						if(location.equals(file)){
							//System.out.println("AGGIUNGO SPECIFICA SEMIFORMALE");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getSpecificaSemiformale(j).getByteContenuto(), 
									xsd, wsdlElement,prefixForWSDL, true, targetNamespace, (profondita+1));
							break;
						}
					}
				}
				//System.out.println("--------------------------------------------- FINE IMPORT ("+profondita+")--------------------------------------------------------------");
			}
				
			
			
			
			// Risoluzione ricorsiva degli include presenti nel xsd
			List<Node> includeIntoXSD = this.xsdUtils.readIncludes(doc);
			for(int i=0; i<includeIntoXSD.size(); i++){
				Node n = includeIntoXSD.get(i);
				//System.out.println("----------------------------------- INCLUDE ("+profondita+")------------------------------------------------------------------------");
				String location = null;
				try{
					location = this.xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Include XSD ["+this.wsdlUtilities.getAttributeValue(n,"namespace")+"] ["+location+"]");
				//System.out.println("INCLUDE ["+location+"]");
				if(location.startsWith(CostantiRegistroServizi.ALLEGATI_DIR)){
					for(int j=0; j<parteComuneNormalizzata.sizeAllegatoList(); j++){
						Documento allegato = parteComuneNormalizzata.getAllegato(j);
						String file = CostantiRegistroServizi.ALLEGATI_DIR+File.separatorChar+allegato.getFile();
						//System.out.println("Check allegato.. ["+location+"]==["+file+"]");
						if(location.equals(file)){
							//System.out.println("AGGIUNGO ALLEGATO INTERNO");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getAllegato(j).getByteContenuto(), 
									xsd, wsdlElement,prefixForWSDL, false, targetNamespace, (profondita+1));
							break;
						}
					}
				}
				else if(location.startsWith(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR)){
					for(int j=0; j<parteComuneNormalizzata.sizeSpecificaSemiformaleList(); j++){
						Documento specificaSemiformale = parteComuneNormalizzata.getSpecificaSemiformale(j);
						String file = CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+specificaSemiformale.getFile();
						//System.out.println("Check specifica.. ["+location+"]==["+file+"]");
						if(location.equals(file)){
							//System.out.println("AGGIUNGO SPECIFICA SEMIFORMALE");
							xsd.write("\n".getBytes());
							risoluzioneImportIncludeInXSD(parteComuneNormalizzata, parteComuneNormalizzata.getSpecificaSemiformale(j).getByteContenuto(), 
									xsd, wsdlElement,prefixForWSDL, false, targetNamespace, (profondita+1));
							break;
						}
					}
				}
				//System.out.println("----------------------------------- FINE INCLUDE ("+profondita+")------------------------------------------------------------------------");
				
			}
			
			
			
			// Rimuovo tutti gli import/include precedentemente aggiunti direttamente nel types del wsdl
			this.xsdUtils.removeImportsAndIncludes(doc);
			
			
			
			// Rigenerazione byte[] del xsd trasformato
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.xmlUtils.writeTo(doc, bout, true);
			bout.flush();
			bout.close();
			//if(docImportato){
			//	System.out.println("AGGIUNGO XSD IMPORTATO ["+bout.toString()+"]");
			//}else{
			//System.out.println("AGGIUNGO XSD INCLUSO ["+bout.toString()+"]");
			//}
			xsd.write(bout.toByteArray());
			
			
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la costruzione del xsd dai bytes: "+e.getMessage(),e);
		}
	}
	
	private byte [] updateImportsAndIncludesWSDLLocation(String file,byte [] wsdl,
			HashMap<String, String> targetNamespacesXSD,HashMap<String, String> includePath,
			boolean prettyDocument,boolean traduciImportIntoTypesImport) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
		
			//System.out.println("\n\n"+file);
			
			if(!this.xmlUtils.isDocument(wsdl)){
				throw new org.openspcoop2.utils.wsdl.WSDLException("WSDL["+file+"] non e' un documento valido");
			}
			Document doc = this.xmlUtils.newDocument(wsdl);
			
			// Read import into types.
			List<Node> importsIntoTypesIntoWSDL = this.wsdlUtilities.readImportsSchemaIntoTypes(doc);
			for(int i=0; i<importsIntoTypesIntoWSDL.size(); i++){
				Node n = importsIntoTypesIntoWSDL.get(i);
				//System.out.println("CLASS["+n.getClass().getName()+"]");
				String namespaceImport = null;
				try{
					namespaceImport = this.xsdUtils.getImportNamespace(n);
				}catch(Exception e){}
				//System.out.println("TargetNamespace schema xsd che contiene l'import: "+targetNamespaceXSD);
				String location = null;
				try{
					location = this.xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Import WSDL ["+namespaceImport+"] ["+location+"]");
				if(namespaceImport!=null){
					
					String path = targetNamespacesXSD.get(namespaceImport);
					if(path==null){
						this.logger.debug("Schema non trovato per il namespace "+namespaceImport);
					}
					else{
//						Node old = n.getAttributes().removeNamedItem("schemaLocation");
//						old.setNodeValue(path);	
//						n.getAttributes().setNamedItem(old);
						this.xsdUtils.updateSchemaLocation(n, path);
						this.logger.debug("Reimpostata location: "+path);
					}
					
				}else{
					this.logger.debug("Import presente nel WSDL["+file+"] ["+location+"] non possiede il target namespace");
				}
			}
			
			// Read include into types.
			if(includePath!=null){
				List<Node> includesIntoTypesIntoWSDL = this.wsdlUtilities.readIncludesSchemaIntoTypes(doc);
				for(int i=0; i<includesIntoTypesIntoWSDL.size(); i++){
					Node n = includesIntoTypesIntoWSDL.get(i);
					//System.out.println("CLASS["+n.getClass().getName()+"]");
					String location = null;
					try{
						location = this.xsdUtils.getIncludeSchemaLocation(n);
					}catch(Exception e){}
					//System.out.println("Include TYPES ["+this.wsdlUtilities.getAttributeValue(n,"namespace")+"] ["+location+"]");
					if(location!=null){
						
						File locationF = new File(location);
						String path = null;
						if(locationF.getParentFile()!=null){
							String key = locationF.getParentFile().getName()+File.separatorChar+locationF.getName();
							//System.out.println("CERCO CON CHIAVE: "+key);
							path = includePath.get(key);
						}
						if(path==null){
							String key = locationF.getName();
							//System.out.println("CERCO CON CHIAVE: "+key);
							path = includePath.get(key);
						}
						if(path==null){
							//System.out.println("NON TROVATO PER: "+location);
							this.logger.debug("Schema non trovato per la location "+location);
						}
						else{
							//System.out.println("TROVATO: "+path);
//							Node old = n.getAttributes().removeNamedItem("schemaLocation");
//							old.setNodeValue(path);	
//							n.getAttributes().setNamedItem(old);
							this.xsdUtils.updateSchemaLocation(n, path);
							this.logger.debug("Reimpostata location: "+path);
						}
						
					}else{
						this.logger.debug("Include presente nel XSD ["+location+"] non possiede la location?");
					}
				}
			}
			
			// Read import into wsdl.
			List<Node> importsIntoWSDL = this.wsdlUtilities.readImports(doc);
			Vector<Node> newSchemi = new Vector<Node>();		
			for(int i=0; i<importsIntoWSDL.size(); i++){
				Node n = importsIntoWSDL.get(i);
				String namespaceImport = null;
				try{
					namespaceImport = this.wsdlUtilities.getImportNamespace(n);
				}catch(Exception e){}
				//System.out.println("TargetNamespace schema xsd che contiene l'import: "+targetNamespaceXSD);
				String location = null;
				try{
					location = this.wsdlUtilities.getImportLocation(n);
				}catch(Exception e){}
				//System.out.println("Import NORMALE ["+namespaceImport+"] ["+location+"]");
				if(namespaceImport!=null){
					
					String path = targetNamespacesXSD.get(namespaceImport);
					if(path==null){
						this.logger.debug("Schema non trovato per il namespace "+namespaceImport);
					}
					else{
						if(traduciImportIntoTypesImport){
							//System.out.println("CREO SCHEMA!!!!");
							Element schemaElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","schema");
							schemaElement.setPrefix("xsd");
							schemaElement.setAttribute("targetNamespace",this.wsdlUtilities.getTargetNamespace(wsdl));
							//schemaElement.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema"); se si decommenta questo, viene aggiunto due volte l'import?
							Element importElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","import");
							importElement.setPrefix("xsd");
							importElement.setAttribute("schemaLocation",path);
							importElement.setAttribute("namespace",namespaceImport);
							schemaElement.appendChild(importElement);
							newSchemi.add(schemaElement);
						}
						else{
//							Node old = n.getAttributes().removeNamedItem("location");
//							old.setNodeValue(path);	
//							n.getAttributes().setNamedItem(old);
							this.xsdUtils.updateSchemaLocation(n, path);
							this.logger.debug("Reimpostata location: "+path);
						}
					}
					
				}else{
					this.logger.debug("Import presente nel WSDL["+file+"] ["+location+"] non possiede il target namespace");
				}
			}
			//System.out.println("RIMUOVO TUTTI GLI IMPORT DIRETTI SUL WSDL");
			if(traduciImportIntoTypesImport){
				this.wsdlUtilities.removeImports(doc);
			}
			
			if(newSchemi.size()>0){
				Node types = this.wsdlUtilities.getIfExistsTypesElementIntoWSDL(doc);
				if(types==null){
					//System.out.println("NULL??");
					Node definition = this.wsdlUtilities.getIfExistsDefinitionsElementIntoWSDL(doc);
					if(definition==null){
						this.logger.debug("Definition non esistente");
					}else{
						types = this.wsdlUtilities.addEmptyTypesIfNotExists(doc);
						for(int i=0; i<newSchemi.size(); i++){
							this.wsdlUtilities.addSchemaIntoTypes(doc, newSchemi.get(i));
							//System.out.println("APPENDO SCHEMA DOPO CREO");
						}
					}
				}else{
					for(int i=0; i<newSchemi.size(); i++){
						//System.out.println("APPENDO SCHEMA");
						this.wsdlUtilities.addSchemaIntoTypes(doc, newSchemi.get(i));
					}
				}
			}
			
	
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			if(prettyDocument){
				//XMLUtils.PrettyDocumentToStream(doc, bout);
				PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, bout);
			}else{
				this.xmlUtils.writeTo(doc, bout);
			}
			bout.flush();
			bout.close();
			return bout.toByteArray();
			
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
	
	private byte [] updateImportXSDLocation(byte [] xsd,HashMap<String, String> targetNamespacesXSD, boolean prettyDocument) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
		
			if(!this.xmlUtils.isDocument(xsd)){
				return null;
			}
			Document doc = this.xmlUtils.newDocument(xsd);
				
			List<Node> importsIntoXSD = this.xsdUtils.readImports(doc);
			for(int i=0; i<importsIntoXSD.size(); i++){
				Node n = importsIntoXSD.get(i);
				String namespaceImport = null;
				try{
					namespaceImport = this.xsdUtils.getImportNamespace(n);
				}catch(Exception e){}
				//System.out.println("TargetNamespace schema xsd che contiene l'import: "+targetNamespaceXSD);
				String location = null;
				try{
					location = this.xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Import ["+namespaceImport+"] ["+location+"]");
				if(namespaceImport!=null && location!=null){
					// Lo schema location non è presente ad esempio quando si costruisce un unico file wsdl+xsd, 
					// e tutti gli xsd sono all'interno del wsdl. E' presente l'import ma non lo schema location
			
					String path = targetNamespacesXSD.get(namespaceImport);
					if(path==null){
						this.logger.debug("Schema non trovato per il namespace "+namespaceImport);
					}
					else{
//						Node old = n.getAttributes().removeNamedItem("schemaLocation");
//						old.setNodeValue(path);	
//						n.getAttributes().setNamedItem(old);
						this.xsdUtils.updateSchemaLocation(n, path);
						this.logger.debug("Reimpostata location: "+path);
					}
					
				}else{
					this.logger.debug("Import presente nel XSD ["+location+"] non possiede il target namespace");
				}
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			if(prettyDocument){
				//XMLUtils.PrettyDocumentToStream(doc, bout);
				PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, bout);
			}else{
				this.xmlUtils.writeTo(doc, bout);
			}
			bout.flush();
			bout.close();
			return bout.toByteArray();
			
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}

	private byte [] updateIncludeXSDLocation(byte [] xsd,HashMap<String, String> includePath, boolean prettyDocument) throws org.openspcoop2.utils.wsdl.WSDLException{
		
		try{
		
			if(!this.xmlUtils.isDocument(xsd)){
				return null;
			}
			Document doc = this.xmlUtils.newDocument(xsd);
				
			List<Node> includeIntoXSD = this.xsdUtils.readIncludes(doc);
			for(int i=0; i<includeIntoXSD.size(); i++){
				Node n = includeIntoXSD.get(i);
				String location = null;
				try{
					location = this.xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				//System.out.println("Include ["+location+"]");
				if(location!=null){
					
					File locationF = new File(location);
					String path = null;
					if(locationF.getParentFile()!=null){
						String key = locationF.getParentFile().getName()+File.separatorChar+locationF.getName();
						//System.out.println("CERCO CON CHIAVE: "+key);
						path = includePath.get(key);
					}
					if(path==null){
						String key = locationF.getName();
						//System.out.println("CERCO CON CHIAVE: "+key);
						path = includePath.get(key);
					}
					if(path==null){
						this.logger.debug("Schema non trovato per la location "+location);
					}
					else{
						//System.out.println("TROVATO");
//						Node old = n.getAttributes().removeNamedItem("schemaLocation");
//						old.setNodeValue(path);	
//						n.getAttributes().setNamedItem(old);
						this.xsdUtils.updateSchemaLocation(n, path);
						this.logger.debug("Reimpostata location: "+path);
					}
					
				}else{
					this.logger.debug("Include presente nel XSD ["+location+"] non possiede la location?");
				}
			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			if(prettyDocument){
				//XMLUtils.PrettyDocumentToStream(doc, bout);
				PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, bout);
			}else{
				this.xmlUtils.writeTo(doc, bout);
			}
			bout.flush();
			bout.close();
			return bout.toByteArray();
			
		}catch(Exception e){
			throw new org.openspcoop2.utils.wsdl.WSDLException(e.getMessage(),e);
		}
	}
}
