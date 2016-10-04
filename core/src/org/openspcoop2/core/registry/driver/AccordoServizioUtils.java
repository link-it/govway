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

package org.openspcoop2.core.registry.driver;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDSchemaCollection;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * AccordoServizioUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AccordoServizioUtils {

	private Logger logger = null;
	private AbstractXMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	public AccordoServizioUtils(Logger log){
		if(log!=null)
			this.logger = log;
		else
			this.logger = LoggerWrapperFactory.getLogger(AccordoServizioUtils.class);
		this.xmlUtils = XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
	}
	
	


	
	public Schema buildSchema(AccordoServizioParteComune as,boolean fromBytes) throws DriverRegistroServiziException {
		XSDSchemaCollection schemaCollections = this.buildSchemaCollection(as, fromBytes);
		try{
			return schemaCollections.buildSchema(this.logger);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public XSDSchemaCollection buildSchemaCollection(AccordoServizioParteComune as,boolean fromBytes) throws DriverRegistroServiziException {

		boolean definitorioPresente = false;
		if(fromBytes){
			definitorioPresente = as.getByteWsdlDefinitorio()!=null;
		}else{
			definitorioPresente = as.getWsdlDefinitorio()!=null;
		}
		
		List<Node> schemiInWsdl = new ArrayList<Node>();
		List<Node> schemiInWsdlFruitore = new ArrayList<Node>();
		WSDLUtilities wsdlUtilities = null;
		Document dWsdl = null;
		Document dWsdlFruitore = null;
		byte [] wsdl = null;
		byte [] wsdlFruitore = null;
		if(fromBytes){
			if(as.getByteWsdlConcettuale()!=null){
				wsdl = as.getByteWsdlConcettuale();
			}
			else{
				if(as.getByteWsdlLogicoErogatore()!=null){
					wsdl = as.getByteWsdlLogicoErogatore();
				}
				if(as.getByteWsdlLogicoFruitore()!=null){
					wsdlFruitore = as.getByteWsdlLogicoFruitore();
				}
			}
		}
		else{
			if(as.getWsdlConcettuale()!=null){
				try{
					String location = as.getWsdlConcettuale();
					if(location.startsWith("http://") || location.startsWith("file://")){
						URL url = new URL(location);
						wsdl = HttpUtilities.requestHTTPFile(url.toString());
					}
					else{
						File f = new File(location);
						wsdl = FileSystemUtilities.readBytesFromFile(f);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl concettuale corrotto: "+e.getMessage(),e);
				}
			}
			else{
				if(as.getWsdlLogicoErogatore()!=null){
					try{
						String location = as.getWsdlLogicoErogatore();
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							wsdl = HttpUtilities.requestHTTPFile(url.toString());
						}
						else{
							File f = new File(location);
							wsdl = FileSystemUtilities.readBytesFromFile(f);
						}
					}catch(Exception e){
						throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl logico erogatore corrotto: "+e.getMessage(),e);
					}
				}
				if(as.getWsdlLogicoFruitore()!=null){
					try{
						String location = as.getWsdlLogicoFruitore();
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							wsdlFruitore = HttpUtilities.requestHTTPFile(url.toString());
						}
						else{
							File f = new File(location);
							wsdlFruitore = FileSystemUtilities.readBytesFromFile(f);
						}
					}catch(Exception e){
						throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl logico fruitore corrotto: "+e.getMessage(),e);
					}
				}
			}
		}
		if(wsdl!=null){
			try{
				wsdlUtilities = new WSDLUtilities(this.xmlUtils);
				dWsdl = this.xmlUtils.newDocument(wsdl);
				List<Node> schemi = wsdlUtilities.getSchemiXSD(dWsdl);
				if(schemi!=null && schemi.size()>0){
					for (Node schema : schemi) {
						boolean schemaWithOnlyImport = this.xsdUtils.isSchemaWithOnlyImports(schema);
						if(schemaWithOnlyImport==false){
							schemiInWsdl.add(schema);		
						}
					}
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl corrotto: "+e.getMessage(),e);
			}
		}
		if(wsdlFruitore!=null){
			try{
				wsdlUtilities = new WSDLUtilities(this.xmlUtils);
				dWsdlFruitore = this.xmlUtils.newDocument(wsdlFruitore);
				List<Node> schemi = wsdlUtilities.getSchemiXSD(dWsdlFruitore);
				if(schemi!=null && schemi.size()>0){
					for (Node schema : schemi) {
						boolean schemaWithOnlyImport = this.xsdUtils.isSchemaWithOnlyImports(schema);
						if(schemaWithOnlyImport==false){
							schemiInWsdlFruitore.add(schema);		
						}
					}
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl fruitore corrotto: "+e.getMessage(),e);
			}
		}
		
		
		if( (schemiInWsdl.size()<=0) && (schemiInWsdlFruitore.size()<=0) && (!definitorioPresente) && as.sizeAllegatoList()==0 && as.sizeSpecificaSemiformaleList()==0){
			throw new DriverRegistroServiziException("L'accordo di servizio parte comune non contiene schemi XSD");
		}

		Hashtable<String, byte[]> resources = new Hashtable<String, byte[]>();
		Hashtable<String, String> mappingNamespaceLocations = new Hashtable<String, String>();

		
		
		// --------- WSDL CONCETTUALE/LOGICO EROGATORE ---------------------
		if(schemiInWsdl.size()>0){

			Hashtable<String, String> declarationNamespacesWSDL = null;
			try{
				declarationNamespacesWSDL = this.xmlUtils.getNamespaceDeclaration(dWsdl.getDocumentElement());
			}catch(Exception e){
				throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl corrotto (Analisi namespace declarations): "+e.getMessage(),e);
			}
			for (int i = 0; i < schemiInWsdl.size(); i++) {
				Node schema = schemiInWsdl.get(i);
				try{
					if(declarationNamespacesWSDL!=null && declarationNamespacesWSDL.size()>0){
						this.xmlUtils.addNamespaceDeclaration(declarationNamespacesWSDL, (Element) schema);
					}
					byte[] resource = this.xmlUtils.toByteArray(schema); 
					String systemId = "internalWsdlTypes_"+(i+1)+".xsd";
					
					// registro risorsa con systemid
					//System.out.println("ADD CONCETTUALE ["+systemId+"] ");
					resources.put(systemId, resource);

					// registro namespace
					this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl corrotto (Estrazione schema): "+e.getMessage(),e);
				}
			}
		}
		
		// --------- WSDL CONCETTUALE/LOGICO EROGATORE ---------------------
		if(schemiInWsdlFruitore.size()>0){

			Hashtable<String, String> declarationNamespacesWSDL = null;
			try{
				declarationNamespacesWSDL = this.xmlUtils.getNamespaceDeclaration(dWsdlFruitore.getDocumentElement());
			}catch(Exception e){
				throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl corrotto (Analisi namespace declarations): "+e.getMessage(),e);
			}
			for (int i = 0; i < schemiInWsdlFruitore.size(); i++) {
				Node schema = schemiInWsdlFruitore.get(i);
				try{
					if(declarationNamespacesWSDL!=null && declarationNamespacesWSDL.size()>0){
						this.xmlUtils.addNamespaceDeclaration(declarationNamespacesWSDL, (Element) schema);
					}
					byte[] resource = this.xmlUtils.toByteArray(schema); 
					String systemId = "internalWsdlFruitoreTypes_"+(i+1)+".xsd";
					
					// registro risorsa con systemid
					//System.out.println("ADD CONCETTUALE ["+systemId+"] ");
					resources.put(systemId, resource);

					// registro namespace
					this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'accordo di servizio parte comune contiene un wsdl corrotto (Estrazione schema): "+e.getMessage(),e);
				}
			}
		}

		// --------- WSDL DEFINITORIO ---------------------
		if( (fromBytes && as.getByteWsdlDefinitorio()!=null) ||
				(!fromBytes && as.getWsdlDefinitorio()!=null)
				){

			byte[] resource = null;
			String systemId = null;
			if(fromBytes){
				systemId = CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD;
				resource = as.getByteWsdlDefinitorio();
			}else{
				String location = as.getWsdlDefinitorio();
				try{
					if(location.startsWith("http://") || location.startsWith("file://")){
						URL url = new URL(location);
						resource = HttpUtilities.requestHTTPFile(url.toString());
						systemId = (new File(url.getFile())).getName();
					}
					else{
						File f = new File(location);
						resource = FileSystemUtilities.readBytesFromFile(f);
						systemId = f.getName();
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'accordo di servizio parte comune indirizza un wsdl definitorio ["+location+"] non esistente");
				}
			}

			try{
				if(this.xsdUtils.isXSDSchema(resource)){

					// registro risorsa con systemid
					//System.out.println("ADD DEFINITIORIO ["+systemId+"] ");
					resources.put(systemId, resource);

					// registro namespace
					this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("La lettura del wsdl definitorio ha causato un errore: "+e.getMessage(),e);
			}
		}

		// --------- ALLEGATI --------- 
		if(as.sizeAllegatoList()>0){
			for(int i=0; i<as.sizeAllegatoList();i++){

				Documento allegato = as.getAllegato(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = allegato.getFile();
					systemId = allegato.getFile();
					resource = allegato.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Allegato ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = allegato.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Allegato ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resources.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resources.put(systemId,resource); 

						// registro namespace
						this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura dell'allegato ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}


		// --------- SPECIFICHE SEMIFORMALI --------- 
		if(as.sizeSpecificaSemiformaleList()>0){
			for(int i=0; i<as.sizeSpecificaSemiformaleList();i++){

				Documento specificaSemiformale = as.getSpecificaSemiformale(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = specificaSemiformale.getFile();
					systemId = specificaSemiformale.getFile();
					resource = specificaSemiformale.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = specificaSemiformale.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resources.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resources.put(systemId,resource); 

						// registro namespace
						this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura della specifica semiformale ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}

		try{
			return this.xsdUtils.buildSchemaCollection(resources, mappingNamespaceLocations, this.logger);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
			
	}

	
}
