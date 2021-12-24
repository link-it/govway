/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**	
 * AbstractUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractUtils {
	
	protected abstract void _initMapper();
	protected abstract void _initWriter(boolean prettyPrint);
	
	protected abstract ObjectMapper _getObjectMapper();
	protected abstract ObjectWriter _getObjectWriter(boolean prettyPrint);
	
	private boolean prettyPrint;
	protected AbstractUtils(boolean prettyPrint) {
		_initMapper();
		this.prettyPrint = prettyPrint;
		_initWriter(this.prettyPrint);
	}
	
	// GET AS
	
	public JsonNode getAsNode(String jsonString) throws UtilsException {
		try {
			return _getObjectMapper().readTree(jsonString);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(byte[] jsonBytes) throws UtilsException {
		try {
			return _getObjectMapper().readTree(jsonBytes);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(InputStream jsonStream) throws UtilsException {
		try {
			return _getObjectMapper().readTree(jsonStream);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(Reader jsonReader) throws UtilsException {
		try {
			return _getObjectMapper().readTree(jsonReader);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public JsonNode getAsNode(Map<?,?> map) throws UtilsException {
		try {
			return _getObjectMapper().valueToTree(map);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// GET AS OBJECT
	
	public <T> T getAsObject(String jsonString, Class<T> c) throws UtilsException {
		try {
			return _getObjectMapper().readValue(jsonString, c);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public <T> T getAsObject(byte[] jsonBytes, Class<T> c) throws UtilsException {
		try {
			return _getObjectMapper().readValue(jsonBytes, c);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public <T> T getAsObject(InputStream jsonStream, Class<T> c) throws UtilsException {
		try {
			return _getObjectMapper().readValue(jsonStream, c);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public <T> T getAsObject(Reader jsonReader, Class<T> c) throws UtilsException {
		try {
			return _getObjectMapper().readValue(jsonReader, c);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// NEW DOCUMENT

	public ObjectNode newObjectNode() throws UtilsException {
		try {
			return _getObjectMapper().createObjectNode();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public ArrayNode newArrayNode() throws UtilsException {
		try {
			return _getObjectMapper().createArrayNode();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public DateFormat getDateFormat() throws UtilsException {
		try {
			return _getObjectMapper().getDateFormat();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


	// TO BYTE ARRAY

	public byte[] toByteArray(JsonNode doc) throws UtilsException {
		try {
			return _getObjectWriter(this.prettyPrint).writeValueAsBytes(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// TO STRING

	public String toString(JsonNode doc) throws UtilsException {
		try {
			return _getObjectWriter(this.prettyPrint).writeValueAsString(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	// WRITE TO

	public void writeTo(JsonNode doc, OutputStream os) throws UtilsException {
		try {
			_getObjectWriter(this.prettyPrint).writeValue(os, doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public void writeTo(Object object, OutputStream os) throws UtilsException {
		try {
			_getObjectWriter(this.prettyPrint).writeValue(os, object);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	// IS
	
	protected boolean isValid(byte[]jsonBytes){
		try {
			getAsNode(jsonBytes);
			return true;
		} catch(Throwable e) {
			return false;
		}
	}
	
	protected boolean isValid(String jsonString){
		try {
			getAsNode(jsonString);
			return true;
		} catch(Throwable e) {
			return false;
		}
	}
	
	// UTILITIES
	
	public Map<String, Object> convertToSimpleMap(JsonNode node){
		return this.convertToSimpleMap(node, true, false, true, false, ".");
	}
	public Map<String, Object> convertToSimpleMap(JsonNode node, String separator){
		return this.convertToSimpleMap(node, true, false, true, false, separator);
	}
	public Map<String, Object> convertToSimpleMap(JsonNode node, 
			boolean analyzeArrayNode, boolean analyzeAsStringArrayNode,
			boolean analyzeObjectNode, boolean analyzeAsStringObjectNode,
			String separator){
		Map<String, Object> map = new HashMap<>();
		_convertToSimpleMap(null, node, null, map,
				analyzeArrayNode, analyzeAsStringArrayNode,
				analyzeObjectNode, analyzeAsStringObjectNode,
				separator);
		return map;
	}
	private void _convertToSimpleMap(String name, JsonNode node, String prefix, Map<String, Object> map,
			boolean analyzeArrayNode, boolean analyzeAsStringArrayNode,
			boolean analyzeObjectNode, boolean analyzeAsStringObjectNode,
			String separator){
		
		String newPrefix = "";
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
							List<String> lString = new ArrayList<>();
							for (int i = 0; i < array.size(); i++) {
								JsonNode arrayChildNode = array.get(i);
								if(arrayChildNode instanceof ArrayNode || arrayChildNode instanceof ObjectNode) {
									String prefixRecursive = newPrefix+normalizeKey(field)+"["+i+"]";
									_convertToSimpleMap(field, arrayChildNode, prefixRecursive, map,
											analyzeArrayNode, analyzeAsStringArrayNode,
											analyzeObjectNode, analyzeAsStringObjectNode,
											separator);
								}
								else {
									String text = arrayChildNode.asText();
									if(text != null && !text.isEmpty())
										lString.add(text);
								}
							}
							if(lString.size()>0) {
								map.put(newPrefix+field, lString);
							}
						}
					}
					else if(analyzeAsStringArrayNode){
						String text = child.asText();
						if(text != null && !text.isEmpty())
							map.put(newPrefix+field, text);
					}
				}
				else if(child instanceof ObjectNode) {
					if(analyzeObjectNode) {
						ObjectNode object = (ObjectNode) child;
						String prefixRecursive = newPrefix+normalizeKey(field);
						_convertToSimpleMap(field, object, prefixRecursive, map,
								analyzeArrayNode, analyzeAsStringArrayNode,
								analyzeObjectNode, analyzeAsStringObjectNode,
								separator);
					}
					else if(analyzeAsStringObjectNode){
						String text = child.asText();
						if(text != null && !text.isEmpty())
							map.put(newPrefix+field, text);
					}
				}
				else {
					String text = child.asText();
					if(text != null && !text.isEmpty())
						map.put(newPrefix+field, text);
				}
			}
			
		}
		else if(node instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) node;
			if(array.size()>0) {
				List<String> lString = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					JsonNode arrayChildNode = array.get(i);
					if(arrayChildNode instanceof ArrayNode || arrayChildNode instanceof ObjectNode) {
						String prefixRecursive = newPrefix+normalizeKey(name)+"["+i+"]";
						_convertToSimpleMap(name, arrayChildNode, prefixRecursive, map,
								analyzeArrayNode, analyzeAsStringArrayNode,
								analyzeObjectNode, analyzeAsStringObjectNode,
								separator);
					}
					else {
						String text = arrayChildNode.asText();
						if(text != null && !text.isEmpty())
							lString.add(text);
					}
				}
				if(lString.size()>0) {
					map.put(name, lString);
				}
			}
		}
		else {
			String text = node.asText();
			if(text != null && !text.isEmpty()) {
				map.put(name, text);
			}
		}
	}
	private static String normalizeKey(String keyParam) {
		String key = keyParam.trim();
		if(key.contains(" ")) {
			while(key.contains(" ")) {
				key = key.replace(" ", "_");
			}
		}
		return key;
	}
}
