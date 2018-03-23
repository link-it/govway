/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.openspcoop2.utils.UtilsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**	
 * JSONUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JSONUtils {
	
	private ObjectMapper mapper;
	private ObjectWriter writer;

	public JSONUtils() throws UtilsException {
		this(false);
	}
	
	public JSONUtils(boolean prettyPrint) throws UtilsException {
		try {
			this.mapper = new ObjectMapper();
			this.writer =  (prettyPrint) ? this.mapper.writer().withDefaultPrettyPrinter() : this.mapper.writer();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// GET AS
	
	public JsonNode getAsNode(String jsonString) throws UtilsException {
		try {
			return this.mapper.readTree(jsonString);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(byte[] jsonBytes) throws UtilsException {
		try {
			return this.mapper.readTree(jsonBytes);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(InputStream jsonStream) throws UtilsException {
		try {
			return this.mapper.readTree(jsonStream);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(Reader jsonReader) throws UtilsException {
		try {
			return this.mapper.readTree(jsonReader);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// NEW DOCUMENT

	public JsonNode newNode() throws UtilsException {
		try {
			return this.mapper.createObjectNode();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


	// TO BYTE ARRAY

	public byte[] toByteArray(JsonNode doc) throws UtilsException {
		try {
			return this.writer.writeValueAsBytes(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// TO STRING

	public String toString(JsonNode doc) throws UtilsException {
		try {
			return this.writer.writeValueAsString(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// WRITE TO

	public void writeTo(JsonNode doc, OutputStream os) throws UtilsException {
		try {
			this.writer.writeValue(os, doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// IS
	
	public boolean isJson(byte[]jsonBytes){
		try {
			getAsNode(jsonBytes);
			return true;
		} catch(Throwable e) {
			return false;
		}
	}
	
	public boolean isJson(String jsonString){
		try {
			getAsNode(jsonString);
			return true;
		} catch(Throwable e) {
			return false;
		}
	}
	
}
