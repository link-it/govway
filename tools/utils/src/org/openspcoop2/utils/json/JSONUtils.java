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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**	
 * JSONUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JSONUtils {
	
	private static JSONUtils jsonUtils = null;
	private static JSONUtils jsonUtilsPretty = null;
	private static synchronized void init(boolean prettyPrint){
		if(prettyPrint) {
			if(JSONUtils.jsonUtilsPretty==null){
				JSONUtils.jsonUtilsPretty = new JSONUtils(true);
			}
		}
		else {
			if(JSONUtils.jsonUtils==null){
				JSONUtils.jsonUtils = new JSONUtils(false);
			}
		}
	}
	public static JSONUtils getInstance(){
		return getInstance(false);
	}
	public static JSONUtils getInstance(boolean prettyPrint){
		if(prettyPrint) {
			if(JSONUtils.jsonUtilsPretty==null){
				JSONUtils.init(true);
			}
			return JSONUtils.jsonUtilsPretty;
		}
		else {
			if(JSONUtils.jsonUtils==null){
				JSONUtils.init(false);
			}
			return JSONUtils.jsonUtils;
		}
	}
	

	private static ObjectMapper mapper;
	private synchronized static void initMapper()  {
		if(mapper==null){
			mapper = new ObjectMapper();
		}
	}
	private static ObjectMapper getObjectMapper() {
		if(mapper==null){
			initMapper();
		}
		return mapper;
	}
	
	private static ObjectWriter writer;
	private synchronized static void initWriter()  {
		if(writer==null){
			writer = mapper.writer();
		}
	}
	private static ObjectWriter getObjectWriter() {
		if(writer==null){
			initWriter();
		}
		return writer;
	}
	
	private static ObjectWriter writerPrettyPrint;
	private synchronized static void initWriterPrettyPrint()  {
		if(writerPrettyPrint==null){
			writerPrettyPrint = mapper.writer().withDefaultPrettyPrinter();
		}
	}
	private static ObjectWriter getObjectWriterPrettyPrint() {
		if(writerPrettyPrint==null){
			initWriterPrettyPrint();
		}
		return writerPrettyPrint;
	}
	
	
	
	private boolean prettyPrint;
	private JSONUtils(boolean prettyPrint) {
		initMapper();
		this.prettyPrint = prettyPrint;
		if(this.prettyPrint) {
			initWriterPrettyPrint();
		}
		else {
			initWriter();
		}
	}
	
	// GET AS
	
	public JsonNode getAsNode(String jsonString) throws UtilsException {
		try {
			return getObjectMapper().readTree(jsonString);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(byte[] jsonBytes) throws UtilsException {
		try {
			return getObjectMapper().readTree(jsonBytes);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(InputStream jsonStream) throws UtilsException {
		try {
			return getObjectMapper().readTree(jsonStream);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(Reader jsonReader) throws UtilsException {
		try {
			return getObjectMapper().readTree(jsonReader);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// NEW DOCUMENT

	public JsonNode newNode() throws UtilsException {
		try {
			return getObjectMapper().createObjectNode();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


	// TO BYTE ARRAY

	public byte[] toByteArray(JsonNode doc) throws UtilsException {
		try {
			return (this.prettyPrint ? getObjectWriterPrettyPrint() : getObjectWriter()).writeValueAsBytes(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// TO STRING

	public String toString(JsonNode doc) throws UtilsException {
		try {
			return (this.prettyPrint ? getObjectWriterPrettyPrint() : getObjectWriter()).writeValueAsString(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// WRITE TO

	public void writeTo(JsonNode doc, OutputStream os) throws UtilsException {
		try {
			(this.prettyPrint ? getObjectWriterPrettyPrint() : getObjectWriter()).writeValue(os, doc);
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
	
	// UTILITIES
	
	public Map<String, String> convertToSimpleMap(JsonNode node){
		return this.convertToSimpleMap(node, true, false, true, false, ".");
	}
	public Map<String, String> convertToSimpleMap(JsonNode node, String separator){
		return this.convertToSimpleMap(node, true, false, true, false, separator);
	}
	public Map<String, String> convertToSimpleMap(JsonNode node, 
			boolean analyzeArrayNode, boolean analyzeAsStringArrayNode,
			boolean analyzeObjectNode, boolean analyzeAsStringObjectNode,
			String separator){
		Map<String, String> map = new HashMap<>();
		_convertToSimpleMap(null, node, null, map,
				analyzeArrayNode, analyzeAsStringArrayNode,
				analyzeObjectNode, analyzeAsStringObjectNode,
				separator);
		return map;
	}
	private void _convertToSimpleMap(String name, JsonNode node, String prefix, Map<String, String> map,
			boolean analyzeArrayNode, boolean analyzeAsStringArrayNode,
			boolean analyzeObjectNode, boolean analyzeAsStringObjectNode,
			String separator){
		
		String newPrefix = null;
		if(prefix!=null) {
			newPrefix = prefix+separator;
		}
		
		if(node instanceof ObjectNode) {
		
			Iterator<String> iterator = node.fieldNames();
			while(iterator.hasNext()) {
				String field = iterator.next();
				JsonNode child = node.get(field);
				if(child instanceof ArrayNode) {
					if(analyzeArrayNode) {
						ArrayNode array = (ArrayNode) child;
						if(array.size()>0) {
							for (int i = 0; i < array.size(); i++) {
								JsonNode arrayChildNode = array.get(i);
								_convertToSimpleMap(field, arrayChildNode, newPrefix+i, map,
										analyzeArrayNode, analyzeAsStringArrayNode,
										analyzeObjectNode, analyzeAsStringObjectNode,
										separator);
							}
						}
					}
					else if(analyzeAsStringArrayNode){
						String text = child.asText();
						if(text != null && !text.isEmpty())
							map.put(field, text);
					}
				}
				else if(child instanceof ObjectNode) {
					if(analyzeObjectNode) {
						ObjectNode object = (ObjectNode) child;
						_convertToSimpleMap(field, object, newPrefix+field, map,
								analyzeArrayNode, analyzeAsStringArrayNode,
								analyzeObjectNode, analyzeAsStringObjectNode,
								separator);
					}
					else if(analyzeAsStringObjectNode){
						String text = child.asText();
						if(text != null && !text.isEmpty())
							map.put(field, text);
					}
				}
				else {
					String text = child.asText();
					if(text != null && !text.isEmpty())
						map.put(field, text);
				}
			}
			
		}
		else if(node instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) node;
			if(array.size()>0) {
				for (int i = 0; i < array.size(); i++) {
					JsonNode arrayChildNode = array.get(i);
					_convertToSimpleMap(name, arrayChildNode, newPrefix+i, map,
							analyzeArrayNode, analyzeAsStringArrayNode,
							analyzeObjectNode, analyzeAsStringObjectNode,
							separator);
				}
			}
		}
		else {
			String text = node.asText();
			if(text != null && !text.isEmpty())
				map.put(name, text);
		}
	}
}
