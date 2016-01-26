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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.SchemaXSD;
import org.openspcoop2.utils.xml.XMLException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Rappresentazione di uno schema xsd presente in un accordo di servizio
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SchemaXSDAccordoServizio extends SchemaXSD {

	private TipoSchemaXSDAccordoServizio tipoSchema;
	public TipoSchemaXSDAccordoServizio getTipoSchema() {
		return this.tipoSchema;
	}
	
	
	public SchemaXSDAccordoServizio(byte[] xsd,String fileName,TipoSchemaXSDAccordoServizio tipo) throws IOException,SAXException,ParserConfigurationException, XMLException{
		this(xsd, fileName, new File(tipo.getDirectory()+File.separatorChar+fileName), tipo);
	}
	public SchemaXSDAccordoServizio(byte[] xsd,String fileName,File fileSource,TipoSchemaXSDAccordoServizio tipo) throws IOException,SAXException,ParserConfigurationException, XMLException{
		super(xsd,fileName,fileSource,org.openspcoop2.message.XMLUtils.getInstance());
		this.tipoSchema = tipo;
	}
	public SchemaXSDAccordoServizio(Element xsd,String fileName,TipoSchemaXSDAccordoServizio tipo) throws IOException,SAXException,ParserConfigurationException{
		this(xsd, fileName, new File(tipo.getDirectory()+File.separatorChar+fileName), tipo);
	}
	public SchemaXSDAccordoServizio(Element xsd,String fileName,File fileSource,TipoSchemaXSDAccordoServizio tipo) throws IOException,SAXException,ParserConfigurationException{
		super(xsd,fileName,fileSource,org.openspcoop2.message.XMLUtils.getInstance());
		this.tipoSchema = tipo;
	}
	
	public static SchemaXSDAccordoServizio creaSchema(byte[] xsd,File file) throws IOException,SAXException,ParserConfigurationException, XMLException{
		if(file==null){
			throw new IOException("File non esistente");
		}
		if(file.getParentFile()==null){
			throw new IOException("File non risiede in una cartella");
		}
		String parent = file.getParentFile().getName();
		if(parent==null){
			throw new IOException("File non risiede in una cartella (null?)");
		}
		SchemaXSDAccordoServizio schema = null;
		if(CostantiRegistroServizi.ALLEGATI_DIR.equals(parent)){
			schema = new SchemaXSDAccordoServizio(xsd,file.getName(),file,TipoSchemaXSDAccordoServizio.ALLEGATO);
		}
		else if(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR.equals(parent)){
			schema = new SchemaXSDAccordoServizio(xsd,file.getName(),file,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		}
		else{
			throw new ParserConfigurationException("Directory contenente lo schema diverso da "+CostantiRegistroServizi.ALLEGATI_DIR+" e da "+CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR);
		}
		return schema;
	}
	
	public static SchemaXSDAccordoServizio creaSchema(Element xsd,File file) throws IOException,SAXException,ParserConfigurationException{
		if(file==null){
			throw new IOException("File non esistente");
		}
		if(file.getParentFile()==null){
			throw new IOException("File non risiede in una cartella");
		}
		String parent = file.getParentFile().getName();
		if(parent==null){
			throw new IOException("File non risiede in una cartella (null?)");
		}
		SchemaXSDAccordoServizio schema = null;
		if(CostantiRegistroServizi.ALLEGATI_DIR.equals(parent)){
			schema = new SchemaXSDAccordoServizio(xsd,file.getName(),file,TipoSchemaXSDAccordoServizio.ALLEGATO);
		}
		else if(CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR.equals(parent)){
			schema = new SchemaXSDAccordoServizio(xsd,file.getName(),file,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		}
		else{
			throw new ParserConfigurationException("Directory contenente lo schema diverso da "+CostantiRegistroServizi.ALLEGATI_DIR+" e da "+CostantiRegistroServizi.SPECIFICA_SEMIFORMALE_DIR);
		}
		return schema;
	}
	
	@Override
	public void writeTo(File folder) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		this.writeTo(folder,false);
	}
	@Override
	public void writeTo(File folder,boolean prettyPrint) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		if(super.getFilename()==null){
			throw new FileNotFoundException("File name non impostato");
		}
		
		File out = new File(folder,this.tipoSchema.getDirectory()+File.separatorChar+super.getFilename());
		try{
			FileSystemUtilities.mkdirParentDirectory(out);
		}catch(Exception e){
			throw new TransformerException(e.getMessage(),e);
		}
		
		if(prettyPrint){
			PrettyPrintXMLUtils.prettyPrintWithTrAX(this.getXml(), out);
		}else{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			xmlUtils.writeTo(this.getXml(), out);
		}
	}
}
