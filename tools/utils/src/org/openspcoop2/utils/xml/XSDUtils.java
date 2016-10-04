/*
f * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.utils.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe utilizzabile per ottenere informazioni sugli schemi
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XSDUtils {

	private AbstractXMLUtils xmlUtils;
	public XSDUtils(AbstractXMLUtils xmlUtils){
		this.xmlUtils = xmlUtils;
	}



	// IS SCHEMA
	public boolean isXSDSchema(byte[]xsd) throws XMLException {
		try{
			if(!this.xmlUtils.isDocument(xsd)){
				return false;
			}
			Document docXML = this.xmlUtils.newDocument(xsd);
			Element elemXML = docXML.getDocumentElement();
			return this.isXSDSchema(elemXML);
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	public boolean isXSDSchema(Document xsd) throws XMLException {
		Element elemXML = xsd.getDocumentElement();
		return this.isXSDSchema(elemXML);
	}

	public boolean isXSDSchema(Element xsd) throws XMLException {
		return this.isXSDSchema((Node)xsd);
	}
	public boolean isXSDSchema(Node xsd) throws XMLException {
		try{
			if(xsd == null){
				throw new Exception("Schema xsd da verificare non definito");
			}
			//System.out.println("LOCAL["+xsd.getLocalName()+"]  NAMESPACE["+xsd.getNamespaceURI()+"]");
			if(!"schema".equals(xsd.getLocalName())){
				return false;
			}
			if(!"http://www.w3.org/2001/XMLSchema".equals(xsd.getNamespaceURI())){
				return false;
			}
			return true;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}








	// TARGET NAMESPACE

	public String getTargetNamespace(byte[]xsd) throws XMLException {
		try{
			if(!this.xmlUtils.isDocument(xsd)){
				throw new Exception("Schema xsd non e' un documento valido");
			}
			Document docXML = this.xmlUtils.newDocument(xsd);
			Element elemXML = docXML.getDocumentElement();
			return this.getTargetNamespace(elemXML);
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

	public String getTargetNamespace(Document xsd) throws XMLException {
		Element elemXML = xsd.getDocumentElement();
		return this.getTargetNamespace(elemXML);
	}

	public String getTargetNamespace(Element xsd) throws XMLException {
		return this.getTargetNamespace((Node)xsd);
	}

	public String getTargetNamespace(Node xsd) throws XMLException {
		try{
			if(xsd == null){
				throw new Exception("Schema xsd non e' un documento valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"schema".equals(xsd.getLocalName())){
				throw new Exception("Root element non e' uno schema xsd ("+xsd.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(xsd, "targetNamespace");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}




	
	
	// SCHEMA LOCATION
	public void updateSchemaLocation(Node schemaImportInclude, String newLocation){
		
		//System.out.println("------------------------- updateSchemaLocation -------------------------------");
		
		if(schemaImportInclude!=null && schemaImportInclude.getAttributes()!=null && schemaImportInclude.getAttributes().getLength()>0){
			
//			try{
//				System.out.println(" PRIMA: "+this.xmlUtils.toString(schemaImportInclude));
//			}catch(Exception e){System.out.println("ERRORE PRIMA");}
			
			Attr oldSchemaLocation = (Attr) schemaImportInclude.getAttributes().getNamedItem("schemaLocation");
			this.xmlUtils.removeAttribute(oldSchemaLocation, (Element)schemaImportInclude);
			
//			try{
//				System.out.println(" REMOVE: "+this.xmlUtils.toString(schemaImportInclude));
//			}catch(Exception e){System.out.println("ERRORE REMOVE");}
			
			oldSchemaLocation.setValue(newLocation);
			this.xmlUtils.addAttribute(oldSchemaLocation, (Element)schemaImportInclude);
			
//			try{
//				System.out.println(" DOPO: "+this.xmlUtils.toString(schemaImportInclude));
//			}catch(Exception e){System.out.println("ERRORE DOPO");}
		}
		
	}
	
	
	
	
	
	// IMPORT
	public String getImportNamespace(Node xsd) throws XMLException {
		try{
			if(xsd == null){
				throw new Exception("Non e' un import valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"import".equals(xsd.getLocalName())){
				throw new Exception("Root element non e' un import di uno schema xsd ("+xsd.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(xsd, "namespace");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	public String getImportSchemaLocation(Node xsd) throws XMLException {
		try{
			if(xsd == null){
				throw new Exception("Non e' un import valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"import".equals(xsd.getLocalName())){
				throw new Exception("Root element non e' un import di uno schema xsd ("+xsd.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(xsd, "schemaLocation");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	public boolean isSchemaWithOnlyImports(Node xsd) throws XMLException {
		if(this.isXSDSchema(xsd)==false){
			throw new XMLException("Il parametro fornito non è uno schema"); // non è uno schmea
		}
		List<Node> listChild = this.xmlUtils.getNotEmptyChildNodes(xsd, false);
		if(listChild==null || listChild.size()<=0){
			return false; // schema vuoto
		}
		boolean schemaConSoloImports = true;
		for (int j = 0; j < listChild.size(); j++) {
			if( !( 
					"import".equals(listChild.get(j).getLocalName()) 
					&& 
					"http://www.w3.org/2001/XMLSchema".equals(listChild.get(j).getNamespaceURI())
					) 
				){
				schemaConSoloImports = false;
				break;
			}
		}
		return schemaConSoloImports;
	}


	// INCLUDE
	public String getIncludeSchemaLocation(Node xsd) throws XMLException {
		try{
			if(xsd == null){
				throw new Exception("Non e' un import valido");
			}
			//System.out.println("LOCAL["+elemXML.getLocalName()+"]  NAMESPACE["+elemXML.getNamespaceURI()+"]");
			if(!"include".equals(xsd.getLocalName())){
				throw new Exception("Root element non e' un include di uno schema xsd ("+xsd.getLocalName()+")");
			}
			String targetNamespace = this.xmlUtils.getAttributeValue(xsd, "schemaLocation");
			//System.out.println("TARGET["+targetNamespace+"]");
			return targetNamespace;
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	public boolean isSchemaWithOnlyIncludes(Node xsd) throws XMLException {
		if(this.isXSDSchema(xsd)==false){
			throw new XMLException("Il parametro fornito non è uno schema"); // non è uno schmea
		}
		List<Node> listChild = this.xmlUtils.getNotEmptyChildNodes(xsd, false);
		if(listChild==null || listChild.size()<=0){
			return false; // schema vuoto
		}
		boolean schemaConSoloIncludes = true;
		for (int j = 0; j < listChild.size(); j++) {
			if( !( 
					"include".equals(listChild.get(j).getLocalName()) 
					&& 
					"http://www.w3.org/2001/XMLSchema".equals(listChild.get(j).getNamespaceURI())
					) 
				){
				schemaConSoloIncludes = false;
				break;
			}
		}
		return schemaConSoloIncludes;
	}
	
	
	
	// IMPORT - INCLUDE
	
	public boolean isSchemaWithOnlyImportsAndIncludes(Node xsd) throws XMLException {
		if(this.isXSDSchema(xsd)==false){
			throw new XMLException("Il parametro fornito non è uno schema"); // non è uno schmea
		}
		List<Node> listChild = this.xmlUtils.getNotEmptyChildNodes(xsd, false);
		if(listChild==null || listChild.size()<=0){
			return false; // schema vuoto
		}
		boolean schemaConSoloImportsAndIncludes = true;
		for (int j = 0; j < listChild.size(); j++) {
			if( !( 
					( "import".equals(listChild.get(j).getLocalName()) || "include".equals(listChild.get(j).getLocalName()) ) 
					&& 
					"http://www.w3.org/2001/XMLSchema".equals(listChild.get(j).getNamespaceURI())
					) 
				){
				schemaConSoloImportsAndIncludes = false;
				break;
			}
		}
		return schemaConSoloImportsAndIncludes;
	}



	// READ

	public List<Node> readImportsAndIncludes(Document xsd) throws XMLException{
		return this.readImportsIncludes(null,xsd,null, true, true);
	}
	public List<Node> readImports(Document xsd) throws XMLException{
		return this.readImportsIncludes(null,xsd,null, true, false);
	}
	public List<Node> readIncludes(Document xsd) throws XMLException{
		return this.readImportsIncludes(null,xsd,null, false, true);
	}
	public List<Node> readImportsAndIncludes(Element xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, true, true);
	}
	public List<Node> readImports(Element xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, true, false);
	}
	public List<Node> readIncludes(Element xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, false, true);
	}
	public List<Node> readImportsAndIncludes(String targetNamespaceSchema,Element xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, true, true);
	}
	public List<Node> readImports(String targetNamespaceSchema,Element xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, true, false);
	}
	public List<Node> readIncludes(String targetNamespaceSchema,Element xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, false, true);
	}
	public List<Node> readImportsAndIncludes(Node xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, true, true);
	}
	public List<Node> readImports(Node xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, true, false);
	}
	public List<Node> readIncludes(Node xsd) throws XMLException{
		return this.readImportsIncludes(null,null,xsd, false, true);
	}
	public List<Node> readImportsAndIncludes(String targetNamespaceSchema,Node xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, true, true);
	}
	public List<Node> readImports(String targetNamespaceSchema,Node xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, true, false);
	}
	public List<Node> readIncludes(String targetNamespaceSchema,Node xsd) throws XMLException{
		return this.readImportsIncludes(targetNamespaceSchema,null,xsd, false, true);
	}
	private List<Node> readImportsIncludes(String targetNamespaceSchema,Document xsdD,Node xsdE,boolean imports,boolean includes) throws XMLException{

		try{
			Vector<Node> nodes = new Vector<Node>();
			NodeList list = null;
			if(xsdD!=null){
				list = xsdD.getChildNodes(); 
			}else{
				list = xsdE.getChildNodes(); 
			}
			if(list!=null){
				for(int i=0; i<list.getLength(); i++){
					Node child = list.item(i);
					if("schema".equals(child.getLocalName())){

						if(targetNamespaceSchema==null){
							try{
								targetNamespaceSchema = this.getTargetNamespace(child);
							}catch(Exception e){}
						}

						NodeList listDefinition = child.getChildNodes();
						if(listDefinition!=null){
							for(int j=0; j<listDefinition.getLength(); j++){
								Node childDefinition = listDefinition.item(j);
								if(imports){
									if("import".equals(childDefinition.getLocalName())){

										if(targetNamespaceSchema==null){
											try{
												targetNamespaceSchema = this.getImportNamespace(childDefinition);
											}catch(Exception e){}
										}		

										childDefinition.setUserData("TargetNamespaceSchema", targetNamespaceSchema, null);
										nodes.add(childDefinition);
									}
								}
								if(includes){
									if("include".equals(childDefinition.getLocalName())){
										childDefinition.setUserData("TargetNamespaceSchema", targetNamespaceSchema, null);
										nodes.add(childDefinition);
									}
								}
							}
						}
					}
					else if("import".equals(child.getLocalName())){
						if(imports){

							if(targetNamespaceSchema==null){
								try{
									targetNamespaceSchema = this.getImportNamespace(child);
								}catch(Exception e){}
							}	

							child.setUserData("TargetNamespaceSchema", targetNamespaceSchema, null);
							nodes.add(child);
						}
					}
					else if("include".equals(child.getLocalName())){
						if(includes){							
							if(targetNamespaceSchema == null){
								throw new XMLException("Lo schema esegue un include senza definire un targetNamespace.");
							}
							child.setUserData("TargetNamespaceSchema", targetNamespaceSchema, null);
							nodes.add(child);
						}
					}
				}
			}
			return nodes;
		}catch(Exception e){
			throw new XMLException("Riscontrato errore durante la lettura dello schema: "+e.getMessage(),e);
		}
	}







	// REMOVE

	public void removeImport(Document xsd,Node importNode){
		this.removeImports_engine(xsd,null,true,false,importNode);
	}
	public void removeInclude(Document xsd,Node includeNode){
		this.removeImports_engine(xsd,null,false,true,includeNode);
	}

	public void removeImport(Element xsd,Node importNode){
		this.removeImports_engine(null,xsd,true,false,importNode);
	}
	public void removeInclude(Element xsd,Node includeNode){
		this.removeImports_engine(null,xsd,false,true,includeNode);
	}

	public void removeImport(Node xsd,Node importNode){
		this.removeImports_engine(null,xsd,true,false,importNode);
	}
	public void removeInclude(Node xsd,Node includeNode){
		this.removeImports_engine(null,xsd,false,true,includeNode);
	}

	public void removeImports(Document xsd){
		this.removeImports_engine(xsd,null,true,false,null);
	}
	public void removeIncludes(Document xsd){
		this.removeImports_engine(xsd,null,false,true,null);
	}
	public void removeImportsAndIncludes(Document xsd){
		this.removeImports_engine(xsd,null,true,true,null);
	}

	public void removeImports(Element xsd){
		this.removeImports_engine(null,xsd,true,false,null);
	}
	public void removeIncludes(Element xsd){
		this.removeImports_engine(null,xsd,false,true,null);
	}
	public void removeImportsAndIncludes(Element xsd){
		this.removeImports_engine(null,xsd,true,true,null);
	}

	public void removeImports(Node xsd){
		this.removeImports_engine(null,xsd,true,false,null);
	}
	public void removeIncludes(Node xsd){
		this.removeImports_engine(null,xsd,false,true,null);
	}
	public void removeImportsAndIncludes(Node xsd){
		this.removeImports_engine(null,xsd,true,true,null);
	}

	private void removeImports_engine(Document xsdD,Node xsdE,boolean imports,boolean includes,Node importIncludeNode){

		NodeList list = null;
		if(xsdD!=null){
			list = xsdD.getChildNodes(); 
		}else{
			list = xsdE.getChildNodes(); 
		}
		if(list!=null){
			for(int i=0; i<list.getLength(); i++){
				Node child = list.item(i);
				if("schema".equals(child.getLocalName())){
					NodeList listDefinition = child.getChildNodes();
					if(listDefinition!=null){
						for(int j=0; j<listDefinition.getLength(); j++){
							Node childDefinition = listDefinition.item(j);
							if(imports){
								if("import".equals(childDefinition.getLocalName())){
									if(importIncludeNode==null){
										child.removeChild(childDefinition);
									}else{
										if(importIncludeNode.equals(childDefinition)){
											child.removeChild(childDefinition);
										}
									}
								}
							}
							if(includes){
								if("include".equals(childDefinition.getLocalName())){
									if(importIncludeNode==null){
										child.removeChild(childDefinition);
									}else{
										if(importIncludeNode.equals(childDefinition)){
											child.removeChild(childDefinition);
										}
									}
								}
							}
						}
					}
				}
				else if("import".equals(child.getLocalName())){
					if(imports){
						if(importIncludeNode==null){
							if(xsdD!=null){
								xsdD.removeChild(child);
							}else{
								xsdE.removeChild(child);
							}
						}else{
							if(importIncludeNode.equals(child)){
								if(xsdD!=null){
									xsdD.removeChild(child);
								}else{
									xsdE.removeChild(child);
								}
							}
						}
					}
				}
				else if("include".equals(child.getLocalName())){
					if(includes){
						if(importIncludeNode==null){
							if(xsdD!=null){
								xsdD.removeChild(child);
							}else{
								xsdE.removeChild(child);
							}
						}
						else{
							if(importIncludeNode.equals(child)){
								if(xsdD!=null){
									xsdD.removeChild(child);
								}else{
									xsdE.removeChild(child);
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	// BUILD SCHEMA COLLECTION
		
	public XSDSchemaCollection buildSchemaCollection(Hashtable<String, byte[]> resources,Hashtable<String, String> mappingNamespaceLocations,Logger logger) throws XMLException {

		// ---------  Check esistenza almeno 1 schema --------- 
		if(resources.size()==0){
			throw new XMLException("Schemi non presenti");
		}

		// ---------  Creo XSD di root da utilizzare per la validazione --------- 
		byte[] schemaPerValidazione = null;
		try{
			StringBuffer bf = new StringBuffer();
			bf.append("<xsd:schema targetNamespace=\"http://openspcoop2.org/validazione_engine\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");

			StringBuffer bfContenitori = new StringBuffer();
			StringBuffer bfImportNormali = new StringBuffer();

			Enumeration<String> targetNamespaces = mappingNamespaceLocations.keys();
			int indexSystemId = 1;
			while(targetNamespaces.hasMoreElements()){
				String targetNamespace = targetNamespaces.nextElement();
				String locations = mappingNamespaceLocations.get(targetNamespace);
				String [] splitLocations = locations.split(" ");

				/* 
				 * Motivo
				 *   Perche è stato fatto questo codice sotto ??? : perche' altrimenti la validazione risulta dipendente dall'ordine degli import con stesso namespace.
				 *   O Meglio il secondo import viene ignorato. Perche? La spiegazione e' in http://stackoverflow.com/questions/4998063/one-xml-namespace-equals-one-and-only-one-schema-file
				 *   Riassumendo:
				 * 
				 * b1.xsd:
				 *	<xs:schema targetNamespace="TNS_B" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *	  ....
				 *	</xs:schema>
				 *
				 * b2.xsd:
				 *  <xs:schema targetNamespace="TNS_B" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     .....
				 *  </xs:schema>
				 * 
				 * a.xsd:
				 * 	<xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     <xs:import namespace="TNS_B" schemaLocation="b1.xsd" />
				 *	   <xs:import namespace="TNS_B" schemaLocation="b2.xsd" />
				 *     .....
				 *  </xs:schema>
				 *  
				 *  1) When I ran xmllint --schema a.xsd data.xml, I was presented with this warning:
				 *  	  a.xsd:4: element import: Schemas parser warning : Element '{http://www.w3.org/2001/XMLSchema}import':
				 *  		 Skipping import of schema located at 'b2.xsd' for the namespace 'TNS_B', 
				 *  	     since this namespace was already imported with the schema located at 'b1.xsd'.
				 * 	   The fact that the import of b2.xsd was skipped obviously leads to this error:
				 *          a.xsd:9: element attribute: Schemas parser error : DETTAGLIO SULL'ERRORE DELLA VALIDAZIONE FALLITA

				 *  2) VisualStudio (XML Spy): work
				 *  
				 *  3) Xerces-J non produce errore in fase di creazione dello schema.
				 *          Produce pero' lo stesso errore di validazione: 
				 *          a.xsd:9: element attribute: Schemas parser error : DETTAGLIO SULL'ERRORE DELLA VALIDAZIONE FALLITA
				 *          
				 *  The crux of the problem here is what does it mean when you have two different <import> elements, when both of them refer to the same namespace.
				 *  The precise meaning of <import> is a bit fuzzy when you read the W3C spec, possibly deliberately so. As a result, interpretations vary.
				 *  Some XML processors tolerate multiple <import> for the same namespace, and essentially amalgamate all of the schemaLocation into a single target.
				 *  Other processors are stricter, and decide that only one <import> per target namespace is valid. 
				 *  I think this is more correct, when you consider that schemaLocation is optional.
				 *  In addition to the VS and xmllint examples you gave, Xerces-J is also super-strict, and ignores subsequent <import> for the same target namespace, 
				 *  giving much the same error as xmllint does. 
				 *  XML Spy, on the other hand, is much more permissive (but then, XML Spy's validation is notoriously flaky)
				 *  To be safe, you should not have these multiple imports. 
				 *  A given namespace should have a single "master" document, which in turn has an <include> for each sub-document. 
				 *  This master is often highly artificial, acting only as a container. for these sub-documents.
				 *  From what I've seen, this generally consists of "best practise" for XML Schema when it comes to maximum tool compatibility, 
				 *  but some will argue that it's a hack that takes away from elegant schema design.
				 *  
				 *  CONCLUSIONI:
				 *  	Viene quindi usato l'approccio a contenitore suggerito.
				 *  
				 * openspcoop_system.xsd:
				 * 	<xs:schema targetNamespace="TNS_B" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     <xs:include namespace="TNS_B" schemaLocation="b1.xsd" />
				 *	   <xs:include namespace="TNS_B" schemaLocation="b2.xsd" />
				 *  </xs:schema>
				 *  
				 * a.xsd:
				 * 	<xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     <xs:import namespace="TNS_B" schemaLocation="openspcoop_system.xsd" />
				 *     .....
				 *  </xs:schema>
				 *  
				 *  
				 *  
				 * Inoltre viene applicato un ulteriore accorgimento per superare il seguente problema:
				 *  
				 * types.xsd:
				 *	<xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *	  ....
				 *	</xs:schema>
				 *
				 * f1.xsd:
				 *  <xs:schema targetNamespace="TNS_B" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *  	<xs:import namespace="TNS_A" schemaLocation="types.xsd" />
				 *     .....
				 *  </xs:schema>
				 *
				 * f2.xsd:
				 *  <xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *  	<xs:include namespace="TNS_A" schemaLocation="types.xsd" />
				 *     .....
				 *  </xs:schema>
				 * 
				 * root.xsd:
				 * 	<xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     <xs:import namespace="TNS_B" schemaLocation="f1.xsd" />
				 *	   <xs:import namespace="TNS_A" schemaLocation="f2.xsd" />
				 *  </xs:schema>
				 *  
				 *  Nonostante sia stato seguito l'approccio a contenitore, durante l'utilizzo del root.xsd schema, si ottiene l'errore:
				 *  root.xsd:4: element import: Schemas parser warning : Element '{http://www.w3.org/2001/XMLSchema}import':
				 *  		 Skipping import of schema located at 'f2.xsd' for the namespace 'TNS_A', 
				 *  	     since this namespace was already imported with the schema located at 'types.xsd'.
				 *  
				 *  Questo perche' importando il file f1.xsd, a sua volta questo importava lo schema types.xsd che definisce lo stesso namespace usato dopo.
				 *  Se invece si inverte l'import nel file root.xsd tale problema non avviene poiche' al momento di importare f1.xsd, 
				 *  il namespace TNS_A e' gia' stato creato ed il file types.xsd gia' stato importato correttamente (avendo il solito nome di file puo' essere ignorato):
				 *  
				 * root.xsd:
				 * 	<xs:schema targetNamespace="TNS_A" xmlns:xs="http://www.w3.org/2001/XMLSchema">
				 *     <xs:import namespace="TNS_A" schemaLocation="f2.xsd" />
				 *     <xs:import namespace="TNS_B" schemaLocation="f1.xsd" />
				 *  </xs:schema> 
				 *  
				 *  Soluzione: La classe contenitore conterra' il namespace che viene utilizzato da piu' schemi e li includera'. 
				 *  		   Dagli schemi inclusi nella classe contenitore vengono esclusi gli schemi che sono inclusi o importati in un altro schema xsd sempre con stesso namespace.
				 *  		   Di fatto quindi la classe contenitore conterra' solo le inclusioni degli schemi con stesso namespace che non vengono importati/inclusi da altri.
				 *  		   Tali schemi possono poi essere utilizzati anche in altri schemi con differenti namespace come nel caso di esempio sopra riportato. 
				 *  		   Siccome comunque saranno importati sicuramente anche nella classe contenitore, vengono definiti prima gli import delle classi contenitori e
				 *  		   solo dopo gli import 'normali'. 
				 **/

				if(splitLocations.length==1){
					bfImportNormali.append("\t<xsd:import namespace=\""+targetNamespace+"\" schemaLocation=\""+splitLocations[0]+"\" />\n");
				}
				else{
					String systemIdNewSchema = "System_OpenSPCoop_Id_"+indexSystemId+".xsd";
					indexSystemId++;
					bfContenitori.append("\t<xsd:import namespace=\""+targetNamespace+"\" schemaLocation=\""+systemIdNewSchema+"\" />\n");

					// Creo schema che contiene tutti gli schemi con stesso target namespace e registro la nuova risorsa 
					StringBuffer bfStessoNamespace = new StringBuffer();
					bfStessoNamespace.append("<xsd:schema targetNamespace=\""+targetNamespace+"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
					for(int i=0;i<splitLocations.length;i++){

						// Devo includere uno schema solo se questo non e' gia' a sua volta incluso o importato da un altro schema con stesso namespace.
						// Altrimenti ottengo un errore simile al seguente:
						//  A schema cannot contain two global components with the same name; this schema contains two occurrences of ....
						boolean findImportInclude = false;
						for(int j=0;j<splitLocations.length;j++){
							if(j==i){
								continue; // salto me stesso
							}
							byte [] xsd = resources.get(splitLocations[j]);
							Document d = this.xmlUtils.newDocument(xsd);

							List<Node> imports = this.readImports(d);
							if(imports!=null){
								for (Node node : imports) {
									if(node!=null){
										String schemaLocation = this.getImportSchemaLocation(node);
										if(schemaLocation!=null){
											//System.out.println("IMPORT: "+schemaLocation);
											String baseLocation = getBaseNameXSDLocation(schemaLocation);
											//System.out.println("IMPORT-BASE: "+baseLocation);
											if(splitLocations[i].equals(baseLocation)){
												findImportInclude = true;
												break;
											}
										}
									}
								}
							}
							if(findImportInclude){
								break;
							}

							List<Node> includes = this.readIncludes(d);
							if(includes!=null){
								for (Node node : includes) {
									if(node!=null){
										String schemaLocation = this.getIncludeSchemaLocation(node);
										if(schemaLocation!=null){
											//System.out.println("INCLUDE: "+schemaLocation);
											String baseLocation = getBaseNameXSDLocation(schemaLocation);
											//System.out.println("INCLUDE-BASE: "+baseLocation);
											if(splitLocations[i].equals(baseLocation)){
												findImportInclude = true;
												break;
											}
										}
									}
								}
							}
							if(findImportInclude){
								break;
							}
						}

						if(findImportInclude==false)
							bfStessoNamespace.append("\t<xsd:include schemaLocation=\""+splitLocations[i]+"\" />\n");
					}
					bfStessoNamespace.append("</xsd:schema>");
					//System.out.println("NUOVA REGISTRAZIONE PER ["+systemIdNewSchema+"] ["+bfStessoNamespace.toString()+"]");
					resources.put(systemIdNewSchema, bfStessoNamespace.toString().getBytes());

				}
			}
			bf.append(bfContenitori.toString());
			bf.append(bfImportNormali.toString());
			bf.append("</xsd:schema>");
			schemaPerValidazione = bf.toString().getBytes();
			//System.out.println("XSD: ["+bf.toString()+"]");

		}catch(Exception e){
			throw new XMLException("Creazione dello schema fallita: "+e.getMessage(),e);
		}

		XSDSchemaCollection collection = new XSDSchemaCollection();
		collection.setSchemaRoot(schemaPerValidazione);
		collection.setResources(resources);
		collection.setMappingNamespaceLocations(mappingNamespaceLocations);
		
		return collection;

	}
	
	
	
	
	
	// UTILITIES GENERICHE
	
	public void registraMappingNamespaceLocations(byte[] resource,String location,Hashtable<String, String> mappingNamespaceLocations) throws XMLException {
		String targetNamespaceXSD = this.getTargetNamespace(resource);
		if(targetNamespaceXSD!=null){
			//System.out.println("ADD MAPPING PER ["+targetNamespaceXSD+"]...");
			if(mappingNamespaceLocations.containsKey(targetNamespaceXSD)){
				String locationGiaAssociataTargetNamespace = mappingNamespaceLocations.remove(targetNamespaceXSD);
				String newValue = locationGiaAssociataTargetNamespace+" "+location;
				mappingNamespaceLocations.put(targetNamespaceXSD, newValue);
				//System.out.println("ADJUNCT a ["+locationGiaAssociataTargetNamespace+"], new: ["+newValue+"]");
			}
			else{
				mappingNamespaceLocations.put(targetNamespaceXSD, location);
				//System.out.println("FIRST ADD");
			}
		}
	}
	
	public String getBaseNameXSDLocation(String location) throws MalformedURLException{
		if(location.startsWith("http://") || location.startsWith("https://") || location.startsWith("file://")){
			URL url = new URL(location);
			File fileUrl = new File(url.getFile());
			return fileUrl.getName();
		}
		else{
			File f = new File(location);
			return f.getName();
		}
	}
	

}
