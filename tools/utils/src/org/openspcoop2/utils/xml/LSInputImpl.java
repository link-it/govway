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


package org.openspcoop2.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.openspcoop2.utils.Utilities;
import org.w3c.dom.ls.LSInput;

/**
 * Classe utilizzabile per raccogliere uno schema XSD
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class LSInputImpl implements LSInput {

	private String type;  // http://www.w3.org/2001/XMLSchema
	private String namespaceURI;
	private String publicId;
	private String systemId; // es. esempio.xsd
	private String baseURI; // es. /etc/openspcoop2/esempio.xsd
	private byte[] resource;
	private String encoding = Charset.defaultCharset().name();

	public LSInputImpl(String type, String namespaceURI, String publicId, String systemId,
			String baseURI, byte[] resource){
		this.type = type;
		this.namespaceURI = namespaceURI;
		this.publicId = publicId;
		this.systemId = systemId;
		this.baseURI = baseURI;
		this.resource = resource;
	} 
	
	@Override
	public Reader getCharacterStream() {
		try{
			return new InputStreamReader(new ByteArrayInputStream(this.resource),this.encoding);
		}catch(Exception e){
			throw new RuntimeException("Metodo getCharacterStream() ha causato un errore",e);
		}
	}

	@Override
	public void setCharacterStream(Reader characterStream) {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int letti = 0;
			char [] buffer = new char[Utilities.DIMENSIONE_BUFFER];
			while( (letti=characterStream.read(buffer)) != -1 ){
				for(int i=0;i<letti;i++)
					bout.write(buffer[i]);
			}
			this.resource = bout.toByteArray();
		}catch(Exception e){
			throw new RuntimeException("Metodo setCharacterStream(Reader characterStream) ha causato un errore",e);
		}
	}

	@Override
	public InputStream getByteStream() {
		return new ByteArrayInputStream(this.resource);
	}

	@Override
	public void setByteStream(InputStream byteStream) {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int letti = 0;
			byte[] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
			while( (letti=byteStream.read(buffer)) != -1 ){
				for(int i=0;i<letti;i++)
					bout.write(buffer,0,letti);
			}
			this.resource = bout.toByteArray();
		}catch(Exception e){
			throw new RuntimeException("Metodo setByteStream(InputStream byteStream) ha causato un errore",e);
		}
	}

	@Override
	public String getStringData() {
		try{
			return new String(this.resource,this.encoding);
		}catch(Exception e){
			throw new RuntimeException("Metodo getStringData() ha causato un errore",e);
		}
	}

	@Override
	public void setStringData(String stringData) {
		this.resource = stringData.getBytes();
	}

	@Override
	public String getSystemId() {
		return this.systemId;
	}

	@Override
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@Override
	public String getPublicId() {
		return this.publicId;
	}

	@Override
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	@Override
	public String getBaseURI() {
		return this.baseURI;
	}

	@Override
	public void setBaseURI(String baseURI){ 
		this.baseURI = baseURI;
	}

	@Override
	public String getEncoding() {
		return this.encoding; 
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public boolean getCertifiedText() {
		throw new RuntimeException("Metodo getCertifiedText() non implementato");
	}

	@Override
	public void setCertifiedText(boolean certifiedText) {
		throw new RuntimeException("Metodo setCertifiedText(boolean certifiedText) non implementato");
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNamespaceURI() {
		return this.namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public byte[] getResource() {
		return this.resource;
	}

	public void setResource(byte[] resource) {
		this.resource = resource;
	}
}
