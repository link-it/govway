/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.SchemaXSD;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.wsdl.ImportImpl;
import com.ibm.wsdl.TypesImpl;
import com.ibm.wsdl.extensions.schema.SchemaImpl;


/**
 * Classe che gestisce la creazione dei wsdl standard a partire da WSDL suddivisi in schemi, parte comune e parte specifica
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */

public class StandardWSDL {
	
	
	/* ------- INPUT --------- */
	private DefinitionWrapper implementativoErogatore = null, implementativoFruitore = null;
	private DefinitionWrapper logicoErogatore = null, logicoFruitore = null;
	private StandardWSDLOutputMode outputMode = StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI;
	private String pathImplementativo, pathLogico;
	
	
	/* ------- OUTPUT --------- */
	private DefinitionWrapper wsdlErogatore = null, wsdlFruitore = null, wsdlUnificato = null;
	private List<SchemaXSDAccordoServizio> schemiWsdlErogatore = new ArrayList<SchemaXSDAccordoServizio>();
	private List<SchemaXSDAccordoServizio> schemiWsdlFruitore = new ArrayList<SchemaXSDAccordoServizio>();
	private List<SchemaXSDAccordoServizio> schemiWsdlUnificato = new ArrayList<SchemaXSDAccordoServizio>();
	
	/* ------- ALTRO ---------- */
	private org.openspcoop2.message.XMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	private WSDLUtilities wsdlUtilities = null;
	
	
	/* ------- Costruttori --------- */
	
	public StandardWSDL (String implementativoErogatorePath, String implementativoFruitorePath, StandardWSDLOutputMode outputMode) throws WSDLException, StandardWSDLException, IOException, ParserConfigurationException, SAXException, XMLException{
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		
		// Vincolo i due documenti (se specificati entrambi) ad essere nel solito folder
		this.pathImplementativo = getParentFile(implementativoErogatorePath);
		if(implementativoFruitorePath != null){
			String fileFruitoreParent = getParentFile(implementativoFruitorePath);
			if(this.pathImplementativo!=null){
				if(fileFruitoreParent==null){
					throw new StandardWSDLException("Fruitore ed Erogatore in folder differenti non sono supportati da questo tool");
				}
				if(this.pathImplementativo.compareTo(fileFruitoreParent) != 0){ 
					throw new StandardWSDLException("Fruitore ed Erogatore in folder differenti non sono supportati da questo tool");
				}
			}
		}
		
		this.implementativoErogatore = new DefinitionWrapper(implementativoErogatorePath,this.xmlUtils);
		if(implementativoFruitorePath != null){
			this.implementativoFruitore = new DefinitionWrapper(implementativoFruitorePath,this.xmlUtils);
		}
		
		this.outputMode = outputMode;
		
		setup(false,null, null);
	}
	
	
	public StandardWSDL (List<SchemaXSDAccordoServizio> schemi, byte[] logicoErogatore, byte[] implementativoErogatore, byte[] logicoFruitore, byte[] implementativoFruitore,
			StandardWSDLOutputMode outputMode) throws WSDLException, StandardWSDLException, IOException, ParserConfigurationException, SAXException, org.openspcoop2.utils.wsdl.WSDLException, XMLException{
		this(schemi, logicoErogatore, implementativoErogatore, schemi, logicoFruitore, implementativoFruitore,outputMode);
	}

	public StandardWSDL (List<SchemaXSDAccordoServizio> schemiErogatore, byte[] logicoErogatore, byte[] implementativoErogatore,
			List<SchemaXSDAccordoServizio> schemiFruitore, byte[] logicoFruitore, byte[] implementativoFruitore,
			StandardWSDLOutputMode outputMode) throws WSDLException, StandardWSDLException, IOException, ParserConfigurationException, SAXException, org.openspcoop2.utils.wsdl.WSDLException, XMLException{
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		
		if(StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL.equals(outputMode)==false &&
				StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI.equals(outputMode)==false){
			throw new StandardWSDLException("OutputMode non permesso quando in input vengono forniti i byte[]");
		}
		this.outputMode = outputMode;
				
		// EROGATORE
		if(logicoErogatore == null)
			throw new StandardWSDLException("Logico erogatore deve essere specificato");
		if(implementativoErogatore==null)
			throw new StandardWSDLException("Implementativo erogatore deve essere specificato");
					
		// implementativoErogatore
		Document implementativoErogatoreDoc =  this.xmlUtils.newDocument(implementativoErogatore);
		implementativoErogatoreDoc = cleanupWsdlImportsAndTypes(implementativoErogatoreDoc);
		this.implementativoErogatore = new DefinitionWrapper(implementativoErogatoreDoc,this.xmlUtils);
		
		// logicoErogatore
		Document logicoErogatoreDoc = this.xmlUtils.newDocument(logicoErogatore);
		// lettura import per eventuali riaggiunte in outMode=MANTIENI_IMPORT_INCLUDE_ORIGINALI
		List<Node> importPresentiWsdlLogicoErogatore = this.wsdlUtilities.readImports(logicoErogatoreDoc);
		List<Node> importTypesPresentiWsdlLogicoErogatore = this.wsdlUtilities.readImportsSchemaIntoTypes(logicoErogatoreDoc);
		List<Node> includeTypesPresentiWsdlLogicoErogatore = this.wsdlUtilities.readIncludesSchemaIntoTypes(logicoErogatoreDoc);
		// genero wsdl object
		logicoErogatoreDoc = cleanupWsdlImportsAndTypes(logicoErogatoreDoc);
		this.logicoErogatore = new DefinitionWrapper(logicoErogatoreDoc,this.xmlUtils);
		// riaggiungo imports/includes
		this.addImportsAndTypesToWsdl(this.logicoErogatore, importPresentiWsdlLogicoErogatore, 
				includeTypesPresentiWsdlLogicoErogatore, importTypesPresentiWsdlLogicoErogatore);

		
		// FRUITORE
		if(implementativoFruitore != null){
			
			// implementativoFruitore
			Document implementativoFruitoreDoc = this.xmlUtils.newDocument(implementativoFruitore);
			implementativoFruitoreDoc = cleanupWsdlImportsAndTypes(implementativoFruitoreDoc);
			this.implementativoFruitore = new DefinitionWrapper(implementativoFruitoreDoc,this.xmlUtils);
			
			// logicoFruitore
			Document logicoFruitoreDoc = this.xmlUtils.newDocument(logicoFruitore);
			// lettura import  per eventuali riaggiunte in outMode=MANTIENI_IMPORT_INCLUDE_ORIGINALI
			List<Node> importPresentiWsdlLogicoFruitore = this.wsdlUtilities.readImports(logicoFruitoreDoc);
			List<Node> importTypesPresentiWsdlLogicoFruitore = this.wsdlUtilities.readImportsSchemaIntoTypes(logicoFruitoreDoc);
			List<Node> includeTypesPresentiWsdlLogicoFruitore = this.wsdlUtilities.readIncludesSchemaIntoTypes(logicoFruitoreDoc);
			// genero wsdl object
			logicoFruitoreDoc = cleanupWsdlImportsAndTypes(logicoFruitoreDoc);
			this.logicoFruitore = new DefinitionWrapper(logicoFruitoreDoc,this.xmlUtils);
			// riaggiungo imports/includes
			this.addImportsAndTypesToWsdl(this.logicoFruitore, importPresentiWsdlLogicoFruitore, 
					includeTypesPresentiWsdlLogicoFruitore, importTypesPresentiWsdlLogicoFruitore);
			
			
			// ****** import/include per wsdl unificato *****
			if(importPresentiWsdlLogicoErogatore!=null){
				for(int i=0;i<importPresentiWsdlLogicoFruitore.size();i++){
					Node n = importPresentiWsdlLogicoFruitore.get(i);
					boolean trovato = false;
					for(int j=0;j<importPresentiWsdlLogicoErogatore.size();j++){
						Node giaPresente = importPresentiWsdlLogicoErogatore.get(j);
						try{
							if(this.xmlUtils.toString(giaPresente).equals(this.xmlUtils.toString(n))){
								trovato = true;
								break;
							}
						}catch(Exception e){
							throw new StandardWSDLException(e.getMessage(),e);
						}
					}
					if(!trovato){
						importPresentiWsdlLogicoErogatore.add(n);
					}
				}
			}else
				importPresentiWsdlLogicoErogatore = importPresentiWsdlLogicoFruitore;
			
			if(includeTypesPresentiWsdlLogicoErogatore!=null){
				for(int i=0;i<includeTypesPresentiWsdlLogicoFruitore.size();i++){
					Node n = includeTypesPresentiWsdlLogicoFruitore.get(i);
					boolean trovato = false;
					for(int j=0;j<includeTypesPresentiWsdlLogicoErogatore.size();j++){
						Node giaPresente = includeTypesPresentiWsdlLogicoErogatore.get(j);
						try{
							if(this.xmlUtils.toString(giaPresente).equals(this.xmlUtils.toString(n))){
								trovato = true;
								break;
							}
						}catch(Exception e){
							throw new StandardWSDLException(e.getMessage(),e);
						}
					}
					if(!trovato){
						includeTypesPresentiWsdlLogicoErogatore.add(n);
					}
				}
			}else
				includeTypesPresentiWsdlLogicoErogatore = includeTypesPresentiWsdlLogicoFruitore;
			
			if(importTypesPresentiWsdlLogicoErogatore!=null){
				for(int i=0;i<importTypesPresentiWsdlLogicoFruitore.size();i++){
					Node n = importTypesPresentiWsdlLogicoFruitore.get(i);
					boolean trovato = false;
					for(int j=0;j<importTypesPresentiWsdlLogicoErogatore.size();j++){
						Node giaPresente = importTypesPresentiWsdlLogicoErogatore.get(j);
						try{
							if(this.xmlUtils.toString(giaPresente).equals(this.xmlUtils.toString(n))){
								trovato = true;
								break;
							}
						}catch(Exception e){
							throw new StandardWSDLException(e.getMessage(),e);
						}
					}
					if(!trovato){
						importTypesPresentiWsdlLogicoErogatore.add(n);
					}
				}
			}else
				importTypesPresentiWsdlLogicoErogatore = importTypesPresentiWsdlLogicoFruitore;
			
		}
		
		
		// UNIFICATO
		this.wsdlUnificato = new DefinitionWrapper(logicoErogatoreDoc,this.xmlUtils);
		this.addImportsAndTypesToWsdl(this.wsdlUnificato, importPresentiWsdlLogicoErogatore, 
				includeTypesPresentiWsdlLogicoErogatore, importTypesPresentiWsdlLogicoErogatore);
		
		
		setup(true,schemiErogatore, schemiFruitore);
	}
	
	private void setup(boolean setupFromByte,List<SchemaXSDAccordoServizio> schemiErogatore, List<SchemaXSDAccordoServizio> schemiFruitore) throws StandardWSDLException, WSDLException, ParserConfigurationException, SAXException, IOException, XMLException{
		
		List<byte[]> schemiInglobareWsdlErogatore = null;
		List<byte[]> schemiInglobareWsdlFruitore = null;
		
		HashMap<String,String> prefixForWSDLErogatore = new HashMap<String, String>();
		HashMap<String,String> prefixForWSDLFruitore = new HashMap<String, String>();
		String uniquePrefix = "_n"+IDUtilities.getUniqueSerialNumber()+"_";
		
		
		// ----- Generazione wsdl erogatore -----
		// Il logico e' null, in caso il setupFromByte==false
		this.wsdlErogatore = importLogico(this.implementativoErogatore, this.logicoErogatore, setupFromByte);	
		
		// ----- Gestione schemi wsdl erogatore -----
		if(setupFromByte) {
			// Schemi sono stati passati in input. Li devo solo salvare come output
			if(schemiErogatore!=null && schemiErogatore.size()>0){
				
				if(StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL.equals(this.outputMode)){
					// StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL
					try{
						schemiInglobareWsdlErogatore = gestioneSchemiInBaseOutputMode_inputByteArray(true, schemiErogatore, prefixForWSDLErogatore, uniquePrefix);
					}catch(Exception e){
						throw new StandardWSDLException(e.getMessage(),e);
					}
				}
				else{
					// StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI
					this.schemiWsdlErogatore.addAll(schemiErogatore);
				}
				
			}
		}
		else{
			// Schemi vengono letti dal wsdl
			try{
				schemiInglobareWsdlErogatore = gestioneSchemiInBaseOutputMode_inputWsdl(true);
			}catch(Exception e){
				throw new StandardWSDLException(e.getMessage(),e);
			}
		}
		
		if(this.implementativoFruitore!=null){
			
			// ----- Generazione wsdl fruitore -----
			// Il logico e' null, in caso il setupFromByte==false
			this.wsdlFruitore = importLogico(this.implementativoFruitore, this.logicoFruitore, setupFromByte);
			
			// ----- Gestione schemi wsdl fruitore -----
			if(setupFromByte) {
				// Schemi sono stati passati in input. Li devo solo salvare come output
				
				if(StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL.equals(this.outputMode)){
					// StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL
					try{
						schemiInglobareWsdlFruitore = gestioneSchemiInBaseOutputMode_inputByteArray(false, schemiFruitore, prefixForWSDLFruitore, uniquePrefix);
					}catch(Exception e){
						throw new StandardWSDLException(e.getMessage(),e);
					}
				}
				else{
					// StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI
					this.schemiWsdlFruitore.addAll(schemiFruitore);
				}

			}
			else{
				// Schemi vengono letti dal wsdl
				try{
					schemiInglobareWsdlFruitore = gestioneSchemiInBaseOutputMode_inputWsdl(false);
				}catch(Exception e){
					throw new StandardWSDLException(e.getMessage(),e);
				}
			}
		}
		
		// Non riuscendo a clonare un wsdl (se portato in byte e ricreato, non funzionano gli imports)
		// carico nell'unico il logicoErogatore e ci importo l'implementativo
		this.wsdlUnificato = importLogico(this.implementativoErogatore, this.wsdlUnificato, setupFromByte);
		if(this.wsdlFruitore!=null){
			this.wsdlUnificato = join(this.wsdlUnificato, this.wsdlFruitore);
		}
		
		
		
		// add schemi
		if(schemiInglobareWsdlErogatore!=null && schemiInglobareWsdlErogatore.size()>0){
			this.wsdlErogatore = addSchemiWsdlTypes(this.wsdlErogatore, schemiInglobareWsdlErogatore);
		}
		
		if(this.wsdlFruitore!=null){
			
			if(schemiInglobareWsdlFruitore!=null && schemiInglobareWsdlFruitore.size()>0)
				this.wsdlFruitore = addSchemiWsdlTypes(this.wsdlFruitore, schemiInglobareWsdlFruitore);
			
			if(schemiInglobareWsdlErogatore!=null && schemiInglobareWsdlErogatore.size()>0 &&
					schemiInglobareWsdlFruitore!=null && schemiInglobareWsdlFruitore.size()>0){
				this.wsdlUnificato = addSchemiWsdlTypes(this.wsdlUnificato, join(schemiInglobareWsdlErogatore, schemiInglobareWsdlFruitore));
			}
			else if(schemiInglobareWsdlErogatore!=null && schemiInglobareWsdlErogatore.size()>0){
				this.wsdlUnificato = addSchemiWsdlTypes(this.wsdlUnificato, schemiInglobareWsdlErogatore);
			}
			else if(schemiInglobareWsdlFruitore!=null && schemiInglobareWsdlFruitore.size()>0){
				this.wsdlUnificato = addSchemiWsdlTypes(this.wsdlUnificato, schemiInglobareWsdlFruitore);
			}
		}
		else{
			if(schemiInglobareWsdlErogatore!=null && schemiInglobareWsdlErogatore.size()>0){
				this.wsdlUnificato = addSchemiWsdlTypes(this.wsdlUnificato, schemiInglobareWsdlErogatore);
			}
		}
		
		if(this.schemiWsdlErogatore.size()>0)
			this.schemiWsdlUnificato.addAll(this.schemiWsdlErogatore);
		if(this.schemiWsdlFruitore.size()>0)
			this.schemiWsdlUnificato.addAll(this.schemiWsdlFruitore);
	}
	
	
	
	
	
	
	/* ------------------ Metodi pubblici ----------------------- */
	
	public void writeWsdlErogatoreTo(String dir,boolean prettyPrint) throws StandardWSDLException{
		this.writeWsdlErogatoreTo(new File(dir),prettyPrint);
	}
	public void writeWsdlErogatoreTo(File dir,boolean prettyPrint) throws StandardWSDLException{
		try{
			File wsdl = new File(dir,CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
			FileSystemUtilities.mkdirParentDirectory(wsdl);
			this.wsdlUtilities.writeWsdlTo(this.wsdlErogatore,wsdl,prettyPrint);
			if(this.schemiWsdlErogatore.size()>0){
				for(int i=0; i<this.schemiWsdlErogatore.size(); i++){
					this.schemiWsdlErogatore.get(i).writeTo(dir,prettyPrint);
				}
			}
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
	}
	
	public void writeWsdlFruitoreTo(String dir,boolean prettyPrint) throws StandardWSDLException{
		this.writeWsdlFruitoreTo(new File(dir),prettyPrint);
	}
	public void writeWsdlFruitoreTo(File dir,boolean prettyPrint) throws StandardWSDLException{
		try{
			File wsdl = new File(dir,CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
			FileSystemUtilities.mkdirParentDirectory(wsdl);
			this.wsdlUtilities.writeWsdlTo(this.wsdlFruitore,wsdl,prettyPrint);
			if(this.schemiWsdlFruitore.size()>0){
				for(int i=0; i<this.schemiWsdlFruitore.size(); i++){
					this.schemiWsdlFruitore.get(i).writeTo(dir,prettyPrint);
				}
			}
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
	}
	
	public void writeWsdlUnificatoTo(String dir,boolean prettyPrint) throws StandardWSDLException{
		this.writeWsdlUnificatoTo(new File(dir),prettyPrint);
	}
	public void writeWsdlUnificatoTo(File dir,boolean prettyPrint) throws StandardWSDLException{
		try{
			File wsdl = new File(dir,CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
			FileSystemUtilities.mkdirParentDirectory(wsdl);
			this.wsdlUtilities.writeWsdlTo(this.wsdlUnificato,wsdl,prettyPrint);
			if(this.schemiWsdlUnificato.size()>0){
				for(int i=0; i<this.schemiWsdlUnificato.size(); i++){
					this.schemiWsdlUnificato.get(i).writeTo(dir,prettyPrint);
				}
			}
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
	}
	
	public DefinitionWrapper getWsdlErogatore() {
		return this.wsdlErogatore;
	}
	public DefinitionWrapper getWsdlFruitore() {
		return this.wsdlFruitore;
	}
	public DefinitionWrapper getWsdlUnificato() {
		return this.wsdlUnificato;
	}
	
	public List<SchemaXSDAccordoServizio> getSchemiWsdlErogatore() {
		return this.schemiWsdlErogatore;
	}
	public List<SchemaXSDAccordoServizio> getSchemiWsdlFruitore() {
		return this.schemiWsdlFruitore;
	}
	public List<SchemaXSDAccordoServizio> getSchemiWsdlUnificato() {
		return this.schemiWsdlUnificato;
	}
	
	
	
	
	/* -------------------- SCHEMI ----------------------------*/
	
	private List<byte[]> gestioneSchemiInBaseOutputMode_inputWsdl(boolean erogatore) throws StandardWSDLException{
		
		try{
					
			// Trasformo definition in Document/Element
			DefinitionWrapper wsdl = null;
			if(erogatore){
				wsdl = this.wsdlErogatore;
			}
			else{
				wsdl = this.wsdlFruitore;
			}
			Document documentWSDL = this.xmlUtils.newDocument(wsdl.toByteArray());
			Element wsdlElement = documentWSDL.getDocumentElement();
			HashMap<String,String> prefixForWSDL = new HashMap<String, String>();
			
			List<Node> schemiImportatiDalWsdl = new ArrayList<Node>();
			List<Node> schemiInclusiDalWsdl = new ArrayList<Node>();
			
			// leggo dal wsdl
			List<SchemaXSDAccordoServizio> listaSchemiImportati = new ArrayList<SchemaXSDAccordoServizio>();
			List<String> namespaceSchemiImportati = new ArrayList<String>();
			readSchemi(listaSchemiImportati, namespaceSchemiImportati,schemiImportatiDalWsdl,schemiInclusiDalWsdl,wsdl,false,true);
			//System.out.println("LETTI IMP: "+listaSchemiImportati.size()+" NS:"+namespaceSchemiImportati.size());
			
			List<SchemaXSDAccordoServizio> listaSchemiInclusi = new ArrayList<SchemaXSDAccordoServizio>();
			List<String> namespaceSchemiInclusi = new ArrayList<String>();
			readSchemi(listaSchemiInclusi, namespaceSchemiInclusi,schemiImportatiDalWsdl,schemiInclusiDalWsdl,wsdl,true,false);
			//System.out.println("LETTI INCL: "+listaSchemiImportati.size()+" NS:"+namespaceSchemiImportati.size());
			
			List<byte[]> schemiDaInglobareInWsdl = new ArrayList<byte[]>();
			
			String uniquePrefix = "_n"+IDUtilities.getUniqueSerialNumber()+"_";
			
			// Schemi lasciati originali
			if(this.outputMode.equals(StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI)){
				if(erogatore){
					this.schemiWsdlErogatore.addAll(listaSchemiImportati);
					this.schemiWsdlErogatore.addAll(listaSchemiInclusi);
				}else{
					this.schemiWsdlFruitore.addAll(listaSchemiImportati);
					this.schemiWsdlErogatore.addAll(listaSchemiInclusi);
				}
			}
			
			// Schemi inglobati completamente nel wsdl
			else if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL) ){
				
				for(int i=0;i<listaSchemiImportati.size();i++){
					SchemaXSD xsd = listaSchemiImportati.get(i);
					Element xsdElement = xsd.getXml();
					this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, true, null);
					this.xsdUtils.removeImportsAndIncludes(xsdElement);
					schemiDaInglobareInWsdl.add(xsd.toByteArray());
				}
				
				for(int i=0;i<listaSchemiInclusi.size();i++){
					SchemaXSD xsd = listaSchemiInclusi.get(i);
					Element xsdElement = xsd.getXml();
					this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(xsdElement, wsdlElement, prefixForWSDL,uniquePrefix, false, namespaceSchemiInclusi.get(i));
					this.xsdUtils.removeImportsAndIncludes(xsdElement);
					schemiDaInglobareInWsdl.add(xsd.toByteArray());
				}
			}
			
			// Schemi inglobati nel wsdl sono solo quelli che vengono inclusi dal wsdl stesso.
			else if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL) ||
					this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL)  ){
			
				for(int i=0;i<listaSchemiImportati.size();i++){
					SchemaXSDAccordoServizio xsd = listaSchemiImportati.get(i);
					Element xsdElement = xsd.getXml();
					this.wsdlUtilities.readPrefixForWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, false);
					if(erogatore){
						this.schemiWsdlErogatore.add(xsd);
					}else{
						this.schemiWsdlFruitore.add(xsd);
					}
				}
											
				for(int i=0;i<listaSchemiInclusi.size();i++){
					SchemaXSDAccordoServizio xsd = listaSchemiInclusi.get(i);
					Element xsdElement = xsd.getXml();
					
					boolean schemaInclusoDalWsdl = false;
					for(int j=0; j<schemiInclusiDalWsdl.size();j++){
						Node n = schemiInclusiDalWsdl.get(j);
						String location = null;
						try{
							location = this.xsdUtils.getIncludeSchemaLocation(n);
						}catch(Exception e){}
						if(xsd.getSource().getAbsolutePath().endsWith(location)){
							
							if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL)){
								File locationF = new File(location);
								// check nome autogenerato
								if(SplitWSDL.DEFINITORIO_FILENAME.equals(locationF.getName())){
									schemaInclusoDalWsdl = true;
									break;
								}else if(locationF.getName().startsWith("InterfacciaDefinitoria_") && locationF.getName().endsWith(".xsd")){
									schemaInclusoDalWsdl = true;
									break;
								}
							}
							else{
								schemaInclusoDalWsdl = true;
								break;
							}
						}
					}
					
					if(schemaInclusoDalWsdl){
						this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, false, namespaceSchemiInclusi.get(i));
						schemiDaInglobareInWsdl.add(xsd.toByteArray());
					}else{
						this.wsdlUtilities.readPrefixForWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, false);
						if(erogatore){
							this.schemiWsdlErogatore.add(xsd);
						}else{
							this.schemiWsdlFruitore.add(xsd);
						}
					}
				}
			}
			
			// Schemi inglobati nel wsdl sono solo quelli che vengono importati dal wsdl stesso.
			else if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL) ){
			
				for(int i=0;i<listaSchemiInclusi.size();i++){
					SchemaXSDAccordoServizio xsd = listaSchemiInclusi.get(i);
					Element xsdElement = xsd.getXml();
					this.wsdlUtilities.readPrefixForWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, false);
					if(erogatore){
						this.schemiWsdlErogatore.add(xsd);
					}else{
						this.schemiWsdlFruitore.add(xsd);
					}
				}
								
				for(int i=0;i<listaSchemiImportati.size();i++){
					SchemaXSDAccordoServizio xsd = listaSchemiImportati.get(i);
					Element xsdElement = xsd.getXml();
					
					boolean schemaImportatoDalWsdl = false;
					for(int j=0; j<schemiImportatiDalWsdl.size();j++){
						Node n = schemiImportatiDalWsdl.get(j);
						String location = null;
						try{
							location = this.xsdUtils.getImportSchemaLocation(n);
						}catch(Exception e){}
						if(xsd.getSource().getAbsolutePath().endsWith(location)){
							schemaImportatoDalWsdl = true;
							break;
						}
					}
					
					if(schemaImportatoDalWsdl){
						this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, true, null);
						schemiDaInglobareInWsdl.add(xsd.toByteArray());
					}else{
						this.wsdlUtilities.readPrefixForWsdl(xsdElement, wsdlElement, prefixForWSDL, uniquePrefix, false);
						if(erogatore){
							this.schemiWsdlErogatore.add(xsd);
						}else{
							this.schemiWsdlFruitore.add(xsd);
						}
					}
				}
			}
			
			
			
			//System.out.println("SCHEMI INGLOBATI: "+schemiDaInglobareInWsdl.size());
			//System.out.println("SCHEMI EROGATORI: "+this.schemiErogatore.size());
			//System.out.println("SCHEMI FRUITORI: "+this.schemiFruitore.size());
			
			
			
			
			/* -------------  Elimino tutti gli schemi dal wsdl, e inoltre imposto i prefix corretti nel wsdl definition ----------------*/
			
			// Creo wsdl originale senza types
			this.wsdlUtilities.removeTypes(documentWSDL);
			DefinitionWrapper definitionTmp = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			Types types = new TypesImpl();
			definitionTmp.setTypes(types); // types è vuoto!
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			definitionTmp.writeTo(bout);
			bout.flush();
			bout.close();
			
			// Imposto nuovi prefix ottenuti dall'analisi degli schemi xsd
			String wsdlTrasformato = bout.toString();
			if(wsdlTrasformato.trim().startsWith("<?xml")){
				wsdlTrasformato = wsdlTrasformato.substring(wsdlTrasformato.indexOf(">")+1);
			}
			Iterator<String> keys = prefixForWSDL.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				//System.out.println("ADD ["+key+"] ["+prefixForWSDL.get(key)+"]");
				wsdlTrasformato = wsdlTrasformato.replaceFirst(">", " "+key+"=\""+prefixForWSDL.get(key)+"\">");
			}
			
			// Riottengo definition object
			documentWSDL = this.xmlUtils.newDocument(wsdlTrasformato.getBytes());
			if(erogatore){
				this.wsdlErogatore = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			}else{
				this.wsdlFruitore = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			}
			
			
			
			
			
			
			/* -------------  Riaggiungo import o include originali ----------------*/
			if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL) ||
					this.outputMode.equals(StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI) ||
					this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL) ){
				
				//System.out.println("ripristino import ("+schemiImportatiDalWsdl.size()+")");
				
				// ripristino import
				for(int i=0; i<schemiImportatiDalWsdl.size();i++){
					Node n = schemiImportatiDalWsdl.get(i);
					String namespaceImport = null;
					try{
						namespaceImport = this.xsdUtils.getImportNamespace(n);
					}catch(Exception e){}
					String location = null;
					try{
						location = this.xsdUtils.getImportSchemaLocation(n);
					}catch(Exception e){}
					//System.out.println("Import ["+namespaceImport+"] ["+location+"]");
					String targetNamespacePadre =  null;
					Object o = n.getUserData("TargetNamespaceSchema"); 
					if(o!=null){
						targetNamespacePadre = (String)o;
					}
					if(targetNamespacePadre==null){
						targetNamespacePadre = namespaceImport;
					}
					
					String importSchema = "<xsd:schema targetNamespace=\""+targetNamespacePadre+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
						"\t<xsd:import namespace=\""+namespaceImport+"\" schemaLocation=\""+location+"\"/>\n"+
						"</xsd:schema>\n";
					schemiDaInglobareInWsdl.add(importSchema.getBytes());
				}
								
			}
			
			if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL) ||
					this.outputMode.equals(StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI) ){
				
				//System.out.println("ripristino include ("+schemiInclusiDalWsdl.size()+")");
				
				// ripristino include
				for(int i=0; i<schemiInclusiDalWsdl.size();i++){
					Node n = schemiInclusiDalWsdl.get(i);
					String location = null;
					try{
						location = this.xsdUtils.getIncludeSchemaLocation(n);
					}catch(Exception e){}
					//System.out.println("Include ["+location+"]");
					String targetNamespacePadre =  null;
					Object o = n.getUserData("TargetNamespaceSchema"); 
					if(o!=null){
						targetNamespacePadre = (String)o;
					}
										
					String includeSchema = "<xsd:schema targetNamespace=\""+targetNamespacePadre+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
						"\t<xsd:include schemaLocation=\""+location+"\"/>\n"+
						"</xsd:schema>\n";
					schemiDaInglobareInWsdl.add(includeSchema.getBytes());
				}
				
			}
			
			if(this.outputMode.equals(StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL)){
				
				//System.out.println("ripristino include con nome non autogenerato ("+schemiInclusiDalWsdl.size()+")");
				
				// ripristino include
				for(int i=0; i<schemiInclusiDalWsdl.size();i++){
					Node n = schemiInclusiDalWsdl.get(i);
					String location = null;
					try{
						location = this.xsdUtils.getIncludeSchemaLocation(n);
					}catch(Exception e){}
					//System.out.println("Include ["+location+"]");
					File locationF = new File(location);
					
					// check nome autogenerato
					if(   !(   SplitWSDL.DEFINITORIO_FILENAME.equals(locationF.getName()) ||
							  (locationF.getName().startsWith("InterfacciaDefinitoria_") && locationF.getName().endsWith(".xsd")) 
						   )
						){
						
						String targetNamespacePadre =  null;
						Object o = n.getUserData("TargetNamespaceSchema"); 
						if(o!=null){
							targetNamespacePadre = (String)o;
						}
											
						String includeSchema = "<xsd:schema targetNamespace=\""+targetNamespacePadre+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
							"\t<xsd:include schemaLocation=\""+location+"\"/>\n"+
							"</xsd:schema>\n";
						schemiDaInglobareInWsdl.add(includeSchema.getBytes());
						
					}
				}
				
			}

			
			
			
			/* -------- ritorno schemi da includere nel wsdl -------------- */
			if(schemiDaInglobareInWsdl.size()>0){
				return schemiDaInglobareInWsdl;
			}else{
				return null;
			}
			
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
	}
	
	
	private void buildListaSchemiInclusi_inputByteArray(List<Node> inclusioni,List<SchemaXSDAccordoServizio> schemiInclusi,
			List<String> namespacesInclusioni,List<SchemaXSDAccordoServizio> schemiCompleti,String targetNamespace) 
	throws StandardWSDLException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException,IOException,ParserConfigurationException,SAXException,TransformerException,XMLException{
		if(inclusioni!=null && inclusioni.size()>0){
			for(int i=0; i<inclusioni.size();i++){
				Node nodo = inclusioni.get(i);
				buildListaSchemiInclusi_esaminaNodo_inputByteArray(inclusioni, schemiInclusi, namespacesInclusioni, schemiCompleti, nodo, true,targetNamespace);
			}
		}
	}
	private void buildListaSchemiInclusi_esaminaNodo_inputByteArray(List<Node> inclusioni,List<SchemaXSDAccordoServizio> schemiInclusi,
			List<String> namespacesInclusioni,List<SchemaXSDAccordoServizio> schemiCompleti,Node nodo,boolean include,String targetNamespace) 
		throws StandardWSDLException,WSDLException,org.openspcoop2.utils.wsdl.WSDLException,IOException,ParserConfigurationException,SAXException,TransformerException,XMLException{
		
		String location = null;
		if(include){
			try{
				location = this.xsdUtils.getIncludeSchemaLocation(nodo);
			}catch(Exception e){}
		}
		else{
			try{
				location = this.xsdUtils.getImportSchemaLocation(nodo);
			}catch(Exception e){}
		}
		File locationF = new File(location);
		String nome = locationF.getName();
		if(locationF.getParentFile()==null){
			throw new StandardWSDLException("Trovato include di uno schema che non indica la directory "+CostantiRegistroServizi.ALLEGATI_DIR+" o "+CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR);
		}
		String padre = locationF.getParentFile().getName();
		
		// cerco lo schema incluso 
		for(int j=0; j<schemiCompleti.size();j++){
			SchemaXSDAccordoServizio tmp = schemiCompleti.get(j);
			if(tmp.getFilename().equals(nome) && tmp.getTipoSchema().getDirectory().equals(padre)){
				// trovato schema
				
				SchemaXSDAccordoServizio schemaXSD = schemiCompleti.get(j);
				if(include){
					schemiCompleti.remove(j);
					
					Object o = nodo.getUserData("TargetNamespaceSchema"); 
					if(o!=null){
						targetNamespace = (String)o;
					}
					namespacesInclusioni.add(targetNamespace);
					schemiInclusi.add(schemaXSD);				
				}
				
				buildListaSchemiInclusi_inputByteArray(this.xsdUtils.readIncludes(targetNamespace,schemaXSD.getXml()),
						schemiInclusi,namespacesInclusioni,schemiCompleti,targetNamespace);
				
				break;

			}
		}
		
	}
	private List<byte[]> gestioneSchemiInBaseOutputMode_inputByteArray(boolean erogatore,List<SchemaXSDAccordoServizio> schemiXSD,
			HashMap<String,String> prefixForWSDL, String uniquePrefix) throws StandardWSDLException{
		
		try{
			
			@SuppressWarnings("unchecked")
			List<SchemaXSDAccordoServizio> schemi = (List<SchemaXSDAccordoServizio>) ((ArrayList<SchemaXSDAccordoServizio>) schemiXSD).clone();
			
			Document documentWSDL = null;
			if(erogatore){
				documentWSDL = this.xmlUtils.newDocument(this.wsdlErogatore.toByteArray());
			}
			else{
				documentWSDL = this.xmlUtils.newDocument(this.wsdlFruitore.toByteArray());
			}
			String targetNamespaceWSDL = this.wsdlUtilities.getTargetNamespace(documentWSDL);
			
			List<SchemaXSDAccordoServizio> schemiInclusi = new ArrayList<SchemaXSDAccordoServizio>();
			List<String> namespacesInclusioni = new ArrayList<String>();
			List<Node> inclusioni = this.wsdlUtilities.readIncludesSchemaIntoTypes(documentWSDL);
			//System.out.println("TROVATE INCLUSIONI NEL WSDL: "+inclusioni.size());
			buildListaSchemiInclusi_inputByteArray(inclusioni, schemiInclusi, namespacesInclusioni ,schemi,targetNamespaceWSDL);
			List<Node> imports = this.wsdlUtilities.readImportsSchemaIntoTypes(documentWSDL);
			//System.out.println("TROVATE IMPORT NEL WSDL: "+imports.size());
			for(int i=0; i<imports.size();i++){
				Node nodo = imports.get(i);
				buildListaSchemiInclusi_esaminaNodo_inputByteArray(inclusioni, schemiInclusi, namespacesInclusioni, schemi, nodo, false,targetNamespaceWSDL);
			}
			// In schemi sono rimasti solo gli imports
			//System.out.println("INCLUDE["+schemiInclusi.size()+"] IMPORTS["+schemi.size()+"]");
			
			Element wsdlElement = documentWSDL.getDocumentElement();
			List<byte[]> schemiInglobareWsdl = new ArrayList<byte[]>();
			
			// includes
			for(int i=0; i<schemiInclusi.size();i++){
				try{
					//System.out.println("GESTIONE INCLUDE: "+schemiInclusi.get(i).getFilename());
					Element schemaXML = (Element) schemiInclusi.get(i).getXml().cloneNode(true); // per non modificare l'originale
					this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(schemaXML, wsdlElement, prefixForWSDL, uniquePrefix, false, namespacesInclusioni.get(i));
					this.xsdUtils.removeImportsAndIncludes(schemaXML);
					schemiInglobareWsdl.add(this.xmlUtils.toByteArray(schemaXML));
				}catch(Exception e){
					throw new StandardWSDLException(e.getMessage(),e);
				}
			}
			
			// imports
			for(int i=0; i<schemi.size();i++){
				try{
					//System.out.println("GESTIONE IMPORT: "+schemi.get(i).getFilename());
					Element schemaXML = (Element) schemi.get(i).getXml().cloneNode(true); // per non modificare l'originale
					this.wsdlUtilities.normalizzazioneSchemaPerInserimentoInWsdl(schemaXML, wsdlElement, prefixForWSDL, uniquePrefix, true, null);
					this.xsdUtils.removeImportsAndIncludes(schemaXML);
					schemiInglobareWsdl.add(this.xmlUtils.toByteArray(schemaXML));
				}catch(Exception e){
					throw new StandardWSDLException(e.getMessage(),e);
				}
			}
			
			// Creo wsdl originale senza types
			this.wsdlUtilities.removeTypes(documentWSDL);
			DefinitionWrapper definitionTmp = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			Types types = new TypesImpl();
			definitionTmp.setTypes(types); // types è vuoto!
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			definitionTmp.writeTo(bout);
			bout.flush();
			bout.close();
			
			// Imposto nuovi prefix ottenuti dall'analisi degli schemi xsd
			String wsdlTrasformato = bout.toString();
			if(wsdlTrasformato.trim().startsWith("<?xml")){
				wsdlTrasformato = wsdlTrasformato.substring(wsdlTrasformato.indexOf(">")+1);
			}
			Iterator<String> keys = prefixForWSDL.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				//System.out.println("ADD ["+key+"] ["+prefixForWSDL.get(key)+"]");
				wsdlTrasformato = wsdlTrasformato.replaceFirst(">", " "+key+"=\""+prefixForWSDL.get(key)+"\">");
			}
			
			// Riottengo definition object
			documentWSDL = this.xmlUtils.newDocument(wsdlTrasformato.getBytes());
			if(erogatore)
				this.wsdlErogatore = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			else
				this.wsdlFruitore = new DefinitionWrapper(documentWSDL,this.xmlUtils,false,false);
			
			return schemiInglobareWsdl;
			
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
		
	}
	
	/**
	 * Recupera l'array dei documenti in xsd:include
	 * @param wsdl
	 * @return array dei documenti in xsd:include
	 * @throws IOException 
	 */
	private void readSchemi(List<SchemaXSDAccordoServizio> schemi, List<String> namespaceSchemi, 
			List<Node> schemiImportatiWSDL,List<Node> schemiInclusiWSDL,
			DefinitionWrapper wsdl,boolean readIncludes,boolean readImports) throws StandardWSDLException{
		try{
			Types types = wsdl.getTypes();
			if(types!=null){
				List<?> xsdTypes = types.getExtensibilityElements();
				for (int i = 0; i< xsdTypes.size(); i++){
					Schema schema = (Schema) xsdTypes.get(i);
					String targetNamespaceSchema = this.wsdlUtilities.getTargetNamespace(schema);
					readSchemi(schemi, namespaceSchemi, schemiImportatiWSDL, schemiInclusiWSDL,
							schema.getElement(), readIncludes, readImports, targetNamespaceSchema,0);
				}
			}			
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
	}
	private void readSchemi(List<SchemaXSDAccordoServizio> schemi,List<String> namespaceSchemi,
			List<Node> schemiImportatiWSDL,List<Node> schemiInclusiWSDL,
			Element schema,boolean readIncludes,boolean readImports, 
			String targetNamespacePadre, int profondita) throws StandardWSDLException{
	
		try{
			boolean wsdl = false;
			if(profondita==0){
				wsdl = true;
			}
			
			// Leggo gli includes
			List<Node> includes = this.xsdUtils.readIncludes(schema);
			for (int j = 0; j< includes.size(); j++){
				Node n = includes.get(j);
				String location = null;
				try{
					location = this.xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				if(location!=null){
					File fLocation = null;
					if(this.pathLogico!=null){
						fLocation = new File(this.pathLogico,location);
					}else{
						fLocation = new File(location);
					}
					byte [] schemaBytes = FileSystemUtilities.readBytesFromFile(fLocation);
					SchemaXSDAccordoServizio schemaXSD = SchemaXSDAccordoServizio.creaSchema(schemaBytes, fLocation);
					//System.out.println("INCLUDE LOCATION ["+location+"] NS["+targetNamespacePadre+"]");
					readSchemi(schemi, namespaceSchemi, schemiImportatiWSDL, schemiInclusiWSDL, schemaXSD.getXml(), readIncludes, readImports, targetNamespacePadre, ++profondita);
					if(readIncludes){
						schemi.add(schemaXSD);
						namespaceSchemi.add(targetNamespacePadre);
						if(wsdl){
							//System.out.println("SCHEMA INCLUSO DAL WSDL ("+targetNamespacePadre+"): "+location);
							n.setUserData("TargetNamespaceSchema", targetNamespacePadre, null);
							schemiInclusiWSDL.add(n);
						}
					}
				}
			}
					
			
			// Leggo gli imports
			List<Node> imports = this.xsdUtils.readImports(schema);
			for (int j = 0; j< imports.size(); j++){
				Node n = imports.get(j);
				String location = null;
				try{
					location = this.xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				if(location!=null){
					File fLocation = null;
					if(this.pathLogico!=null){
						fLocation = new File(this.pathLogico,location);
					}else{
						fLocation = new File(location);
					}
					byte [] schemaBytes = FileSystemUtilities.readBytesFromFile(fLocation);
					SchemaXSDAccordoServizio schemaXSD = SchemaXSDAccordoServizio.creaSchema(schemaBytes, fLocation);
					String targetNamespaceXSD = this.xsdUtils.getTargetNamespace(schemaXSD.getXml());
					//System.out.println("IMPORT LOCATION ["+location+"] NS["+targetNamespace+"]");
					readSchemi(schemi, namespaceSchemi, schemiImportatiWSDL, schemiInclusiWSDL, schemaXSD.getXml(), readIncludes, readImports, targetNamespaceXSD, ++profondita);
					if(readImports){
						schemi.add(schemaXSD);
						namespaceSchemi.add(targetNamespaceXSD);
						if(wsdl){
							//System.out.println("SCHEMA IMPORTATO DAL WSDL ("+targetNamespace+"): "+location);
							n.setUserData("TargetNamespaceSchema", targetNamespaceXSD, null);
							schemiImportatiWSDL.add(n);
						}
					}
				}
			}	
			
		}catch(Exception e){
			throw new StandardWSDLException(e.getMessage(),e);
		}
		
	}
	
	
	
	/**
	 * Svuota il wsdl:types e lo riempie con gli xsd:schema nel vettore degli schemi
	 * @param schemi
	 * @param wsdl
	 * @return DefinitionWrapper
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws WSDLException 
	 * @throws XMLException 
	 */
	private DefinitionWrapper addSchemiWsdlTypes(DefinitionWrapper wsdl, List<byte[]> schemi) throws ParserConfigurationException, SAXException, IOException, WSDLException, XMLException{
		// Se non mi sono stati passati gli schemi, ritorno il wsdl originale.
		DefinitionWrapper result = new DefinitionWrapper(wsdl,this.xmlUtils);
		if(schemi == null || schemi.size() == 0) return wsdl;
		
		Types types = result.getTypes();
		if(types != null){
			List<?> xsdTypes = types.getExtensibilityElements();
			while (xsdTypes.size()>0){
				types.removeExtensibilityElement((Schema) xsdTypes.get(0));
				xsdTypes = types.getExtensibilityElements();
			}
		} else {
			types = result.createTypes();
			result.setTypes(types);
		}
		
		for(int i=0; i<schemi.size(); i++){
			if(schemi.get(i) == null) continue;
			Document schemaDoc =  this.xmlUtils.newDocument(schemi.get(i));
			Schema schema = new SchemaImpl();
			schema.setElementType(new QName("http://www.w3.org/2001/XMLSchema","schema"));
			schema.setElement(schemaDoc.getDocumentElement());
			types.addExtensibilityElement(schema);
		}
		
		return result;
	}
	
	private void addImportsAndTypesToWsdl(DefinitionWrapper wsdl,List<Node> importsPresentiWsdl,
			List<Node> includesTypesPresentiWsdl,
			List<Node> importsTypesPresentiWsdl) throws WSDLException,IOException,SAXException,ParserConfigurationException, XMLException{
		
		// riaggiungo wsdl import
		//System.out.println("RIAGGIUNGO WSDL IMPORT ("+importsPresentiWsdl.size()+")");
		for(int i=0; i<importsPresentiWsdl.size(); i++){
			Node wsdlImport = importsPresentiWsdl.get(i);
			String namespaceImport = null;
			try{
				namespaceImport = this.wsdlUtilities.getImportNamespace(wsdlImport);
			}catch(Exception e){}
			String location = null;
			try{
				location = this.wsdlUtilities.getImportLocation(wsdlImport);
			}catch(Exception e){}
			Import importWsdl = new ImportImpl();
			importWsdl.setLocationURI(location);
			importWsdl.setNamespaceURI(namespaceImport);
			//System.out.println("ADD ["+namespaceImport+"] ["+location+"]");
			wsdl.addImport(importWsdl);
		}
		
		
		List<byte[]> listaWsdlTypes = new ArrayList<byte[]>();
		
		// riaggiungo types:includes
		//System.out.println("RIAGGIUNGO WSDL TYPES INCLUDES ("+includesTypesPresentiWsdl.size()+")");
		for(int i=0; i<includesTypesPresentiWsdl.size(); i++){
			Node xsdIncludes = includesTypesPresentiWsdl.get(i);
			String location = null;
			try{
				location = this.xsdUtils.getIncludeSchemaLocation(xsdIncludes);
			}catch(Exception e){}
			String targetNamespacePadre = (String) xsdIncludes.getUserData("TargetNamespaceSchema"); 
			String includeSchema = "<xsd:schema targetNamespace=\""+targetNamespacePadre+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
				"\t<xsd:include schemaLocation=\""+location+"\"/>\n"+
				"</xsd:schema>\n";
			//System.out.println("ADD P["+targetNamespacePadre+"] ["+location+"]");
			listaWsdlTypes.add(includeSchema.getBytes());
		}
		
		// riaggiungo types:imports
		//System.out.println("RIAGGIUNGO WSDL TYPES IMPORTS ("+importsTypesPresentiWsdl.size()+")");
		for(int i=0; i<importsTypesPresentiWsdl.size(); i++){
			Node xsdImport = importsTypesPresentiWsdl.get(i);
			String namespaceImport = null;
			try{
				namespaceImport = this.xsdUtils.getImportNamespace(xsdImport);
			}catch(Exception e){}
			//System.out.println("TargetNamespace schema xsd che contiene l'import: "+targetNamespaceXSD);
			String location = null;
			try{
				location = this.xsdUtils.getImportSchemaLocation(xsdImport);
			}catch(Exception e){}
			String targetNamespacePadre = null;
			Object o = xsdImport.getUserData("TargetNamespaceSchema"); 
			if(o!=null){
				targetNamespacePadre = (String)o;
			}
			if(targetNamespacePadre==null){
				targetNamespacePadre = namespaceImport;
			}
			String importSchema = "<xsd:schema targetNamespace=\""+targetNamespacePadre+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"+
				"\t<xsd:import namespace=\""+namespaceImport+"\" schemaLocation=\""+location+"\"/>\n"+
				"</xsd:schema>\n";
			//System.out.println("ADD P["+targetNamespacePadre+"] ["+namespaceImport+"] ["+location+"]");
			listaWsdlTypes.add(importSchema.getBytes());
		}
		
		this.addSchemiWsdlTypes(wsdl, listaWsdlTypes);
	}
	
	
	
	
	
	
	/* ------------------- IMPORT WSDL LOGICO ----------------------------- */
	
	/** 
	 * Risolve l'import del wsdlLogico all'interno dell'implementativo
	 * @param logico
	 * @param risultato
	 * @throws StandardWSDLException 
	 * @throws WSDLException 
	 */
	private DefinitionWrapper importLogico(DefinitionWrapper implementativo, DefinitionWrapper logico, boolean setupFromBytes) throws StandardWSDLException, WSDLException{
		if(implementativo == null){ 
			throw new StandardWSDLException("Implementativo non puo' essere null.");
		}
		DefinitionWrapper risultato = null;

		// Cerco l'import del wsdlLogico. 
		Map<?,?> importsMap = implementativo.getImports();
		
		// Se mi e' stato passato il wsdl logico, accetto che non ci sia l'import, altrimenti lancio un'eccezione.
		if(importsMap.size() == 0 && logico == null){
			throw new StandardWSDLException("Implementativi che non importano il Logico non sono supportati da questo tool.");
		}
		if(importsMap.size() > 1){
			throw new StandardWSDLException("Implementativi che importano altri wsdl oltre al Logico non sono supportati da questo tool.");
		}
		Import importLogico = null;
		if(importsMap.size() == 1){
			Iterator<?> it = importsMap.values().iterator();
			List<?> imports = (List<?>) it.next();
			if(imports.size() != 1){
				throw new StandardWSDLException("Implementativi che importano altri wsdl oltre al Logico non sono supportati da questo tool.");
			}	
			importLogico = (Import) imports.get(0);
		}
		
		
		if(setupFromBytes) { // vecchio logico!=null
			risultato = new DefinitionWrapper(logico,this.xmlUtils);
		} else {
			File importFile = null;
			if(this.pathImplementativo!=null){
				importFile = new File(new File(this.pathImplementativo), importLogico.getLocationURI());
			}else{
				importFile = new File(importLogico.getLocationURI());
			}
			this.pathLogico = importFile.getParent();
			if (importFile.getName().compareTo(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL) == 0 || importFile.getName().compareTo(CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL) == 0){
				// Carico il logico nel risultato
				risultato = new DefinitionWrapper(importFile.getAbsolutePath(),this.xmlUtils);
			} else {
				throw new StandardWSDLException("Implementativi che importano un wsdl Logico con nome diverso da " + CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL + " o " +CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL + " non sono supportati da questo tool.");
			}
		}
		
		// Controllo che i target siano gli stessi
		if(implementativo.getTargetNamespace().compareTo(risultato.getTargetNamespace()) != 0)
			throw new StandardWSDLException("Implementativo e Logico con targetNamespace diversi non sono supportati da questo tool.");
		
		
		// Prendo la definizione dei PortType e dei Services e li inserisco nel risultato.
		// Binding
		Map<?,?> bindings = implementativo.getBindings();
		Iterator<?> it = bindings.values().iterator();
		while(it.hasNext()){
			Binding bd = (Binding) it.next();
			risultato.addBinding(bd);
		}

		// Services
		Map<?,?> services = implementativo.getServices();
		it = services.values().iterator();
		while(it.hasNext()){
			Service s = (Service) it.next();
			risultato.addService(s);
		}
		
		return risultato;
	}
	
	
	
	
	
	
	
	
	/* ------------------- UTILITIES GENERALI ----------------------------- */
		
	private String getParentFile(String file){
		File f = new File(file);
		if(f.getParentFile()!=null){
			return f.getParentFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	
	private Document cleanupWsdlImportsAndTypes(Document doc) {
		Element description = doc.getDocumentElement();
		NodeList imports = description.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "import");
		for(int i=0; i<imports.getLength(); i++){
			description.removeChild(imports.item(i));
		}
		NodeList types = description.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");
		for(int i=0; i<types.getLength(); i++){
			description.removeChild(types.item(i));
		}
		return doc;
	}
	
	/**
	 * Esegue il merge dei due wsdl.
	 * @param a
	 * @param b
	 * @return DefinitionWrapper
	 * @throws StandardWSDLException
	 * @throws WSDLException
	 */
	private DefinitionWrapper join(DefinitionWrapper a, DefinitionWrapper b) throws StandardWSDLException, WSDLException{
		if(a == null) return b;
		if(b == null) return a;
		
		DefinitionWrapper result = new DefinitionWrapper(a,this.xmlUtils);
		
		
		// Controllo che i due abbiano il solito target
		
		if(a.getTargetNamespace().compareTo(b.getTargetNamespace()) != 0){
			throw new StandardWSDLException("Erogatore e Fruitore con targetNamespace diversi non sono supportati da questo tool.");
		}
		
		// Namespace
		// Cerco le dichiarazioni di namespace di B che non sono in A e li aggiungo in risultato. (quelli di A ci sono gia')
		
		Map<?,?> namespacesBMap = b.getNamespaces();
		Map<?,?> namespacesAMap = a.getNamespaces();
		java.util.Iterator<?> prefixesIt = namespacesBMap.keySet().iterator();
		while(prefixesIt.hasNext()){
			String prefix = (String) prefixesIt.next();
			// Controllo se questo prefisso e' gia usato.
			String namespace = (String) namespacesAMap.get(prefix);
			if(namespace == null){
				// Non c'e', lo aggiungo
				result.addNamespace(prefix, (String) namespacesBMap.get(prefix));
			} else {
				// Prefisso gia in uso. Se il namespace e' lo stesso, tutto ok, altrimenti lancio un'eccezione.
				if(namespace.compareTo((String) namespacesBMap.get(prefix)) != 0)
					throw new StandardWSDLException("Erogatore e Fruitore usano lo stesso prefisso per due namespace distinti. Impossibile fare il merge.");
			}
			
		}
		
		// Imports
		// Cerco gli import di B che non sono in A e li aggiungo in risultato (Quelli di A ci sono gia').
		
		Map<?,?> importBMap = b.getImports();
		Map<?,?> importAMap = a.getImports();
		java.util.Iterator<?> itb = importBMap.keySet().iterator();
		while(itb.hasNext()){
			String namespace = (String) itb.next();
			Vector<?> listb = (Vector<?>) importBMap.get(namespace);
			Vector<?> lista = (Vector<?>) importAMap.get(namespace);
			
			for(int i =0; i<listb.size(); i++){
				Import importB = (Import) listb.get(i);
				boolean found = false;
				for(int j=0; j<lista.size(); j++){
					Import importA = (Import) lista.get(j);
					if(importB.getLocationURI().compareTo(importA.getLocationURI()) == 0 && importB.getNamespaceURI().compareTo(importA.getNamespaceURI()) == 0){
						//System.out.println("Trovato: " + importA.getLocationURI());
						found = true;
					}
				}
				if(!found) {
					result.addImport(importB);
				}
			}
		}
		
		// Messaggi
		// Cerco la definizione dei messaggi di B che non sono in A e li aggiungo in risultato (Quelli di A ci sono gia').
		
		Map<?,?> messages = b.getMessages();
		Iterator<?> it = messages.values().iterator();
		while(it.hasNext()){
			Message msg = (Message) it.next();
			if(a.getMessage(msg.getQName()) == null){
				//System.out.println("Aggiunto " + msg.getQName());
				result.addMessage(msg);
			}
		}
		
		// PortTypes
		// Cerco la definizione dei PortTypes di B che non sono in A e li aggiungo in risultato (Quelli di A ci sono gia').
		
		Map<?,?> ports = b.getPortTypes();
		it = ports.values().iterator();
		while(it.hasNext()){
			PortType port = (PortType) it.next();
			if(a.getPortType(port.getQName()) == null){
				//System.out.println("Aggiunto " + port.getQName());
				result.addPortType(port);
			}
		}
		
		// Bindings
		// Cerco la definizione dei PortTypes di B che non sono in A e li aggiungo in risultato (Quelli di A ci sono gia').
		
		Map<?,?> bindings = b.getBindings();
		it = bindings.values().iterator();
		while(it.hasNext()){
			Binding binding = (Binding) it.next();
			if(a.getBinding(binding.getQName()) == null){
				//System.out.println("Aggiunto " + binding.getQName());
				result.addBinding(binding);
			}
		}
		
		// Services
		// Cerco la definizione dei Services di B che non sono in A e li aggiungo in risultato (Quelli di A ci sono gia').
		
		Map<?,?> services = b.getServices();
		it = services.values().iterator();
		while(it.hasNext()){
			Service service = (Service) it.next();
			if(a.getService(service.getQName()) == null){
				//System.out.println("Aggiunto " + service.getQName());
				result.addService(service);
			}
		}
		return result;
	}
		
	/**
	 * Fonde i due array evitando duplici entry
	 * @param a
	 * @param b
	 * @return Lista 
	 */
	private List<byte[]> join(List<byte[]> a, List<byte[]> b){
		List<byte[]> c = new ArrayList<byte[]>();
		
		// Inserisco gli elementi di a
		for(int i = 0; i<a.size(); i++){
			c.add(a.get(i));
		}
		
		// Inserisco gli elementi di b
		for(int i = 0; i<b.size(); i++){
			// Controllo che non ci sia gia'
			String bi = new String(b.get(i));
			boolean found = false;
			for(int j = 0; j<a.size(); j++){
				String aj = new String(a.get(j));
				if(bi.compareTo(aj) == 0) found = true;
			}
			if(!found){
				c.add(b.get(i));
			}
		}
		return c;
	}
	
	
	
}
