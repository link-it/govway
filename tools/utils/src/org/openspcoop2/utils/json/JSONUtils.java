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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

	public JSONUtils() {
		this(false);
	}
	
	public JSONUtils(boolean prettyPrint) {
		this.mapper = new ObjectMapper();
		this.writer =  (prettyPrint) ? this.mapper.writer().withDefaultPrettyPrinter() : this.mapper.writer();
	}
	
	// GET AS
	
	public JsonNode getAsDocument(String jsonString) throws IOException {
		return this.mapper.readTree(jsonString);
	}
	public JsonNode getAsDocument(byte[] jsonBytes) throws IOException {
		return this.mapper.readTree(jsonBytes);
	}
	public JsonNode getAsDocument(InputStream jsonStream) throws IOException {
		return this.mapper.readTree(jsonStream);
	}
	public JsonNode getAsDocument(Reader jsonReader) throws IOException {
		return this.mapper.readTree(jsonReader);
	}

	// NEW DOCUMENT

	public JsonNode newDocument() {
		return this.mapper.createObjectNode();
	}


	// TO BYTE ARRAY

	public byte[] toByteArray(JsonNode doc) throws JsonProcessingException {
		return this.writer.writeValueAsBytes(doc);
	}

	// TO STRING

	public String toString(JsonNode doc) throws JsonProcessingException {
		return this.writer.writeValueAsString(doc);
	}

	// WRITE TO

	public void writeTo(JsonNode doc, OutputStream os) throws JsonGenerationException, JsonMappingException, IOException {
		this.writer.writeValue(os, doc);
	}
	
	// IS
	
	public boolean isDocument(byte[]jsonBytes){
		try {
			getAsDocument(jsonBytes);
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
	public boolean isDocument(String jsonString){
		try {
			getAsDocument(jsonString);
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
}
