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



package org.openspcoop2.utils.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Rappresentazione di uno schema xsd
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SchemaXSD {
	private Element xml;
	private String filename;
	private File source;
	private org.openspcoop2.utils.xml.AbstractXMLUtils xmlUtils;
	
	public SchemaXSD(Element xml, String filename, File source,org.openspcoop2.utils.xml.AbstractXMLUtils xmlUtils){
		this.xmlUtils = xmlUtils;
		this.xml = xml;
		this.filename = filename;
		this.source = source;
	}
	public SchemaXSD(byte[] xml, String filename, File source,org.openspcoop2.utils.xml.AbstractXMLUtils xmlUtils) throws IOException,SAXException,ParserConfigurationException, XMLException{
		this.xmlUtils = xmlUtils;
		this.xml = this.xmlUtils.newElement(xml);
		this.filename = filename;
		this.source = source;
	}

	public Element getXml() {
		return this.xml;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public File getSource() {
		return this.source;
	}
	
	public String getSourceAbsolutePath() {
		return this.source.getAbsolutePath();
	}
	
	
	public boolean compareSource(SchemaXSD definitorio){
		return this.source.getAbsolutePath().equalsIgnoreCase(definitorio.getSource().getAbsolutePath());
	}
	
	public boolean compareFilename(SchemaXSD definitorio){
		return this.filename.equalsIgnoreCase(definitorio.getFilename());
	}
	
	public void writeTo(File folder) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		writeTo(folder,false);
	}
	public void writeTo(File folder,boolean prettyPrint) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		if(this.filename==null){
			throw new FileNotFoundException("File name non impostato");
		}
		
		File out = new File(folder.getAbsolutePath() + File.separator + getFilename());
		try{
			FileSystemUtilities.mkdirParentDirectory(out);
		}catch(Exception e){
			throw new TransformerException(e.getMessage(),e);
		}
		
		if(prettyPrint){
			PrettyPrintXMLUtils.prettyPrintWithTrAX(this.xml, out);
		}else{
			this.xmlUtils.writeTo(this.xml, out);
		}
			
	}
	
	public byte[] toByteArray() throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		return toByteArray(false);
	}
	public byte[] toByteArray(boolean prettyPrint) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException, IOException, XMLException{
		
		if(prettyPrint){
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(this.xml).getBytes();
		}else{
			return this.xmlUtils.toByteArray(this.xml);
		}

	}
	
}
