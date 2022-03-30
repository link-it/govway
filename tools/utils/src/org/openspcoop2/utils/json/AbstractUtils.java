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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.SortedMap;
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
	
	
	// RENAME
	
	public void renameFieldByPath(JsonNode oNode, Map<String, String> pathToNewName) throws UtilsException {
		renameFieldByPath(oNode, pathToNewName, true);
	}
	public void renameFieldByPath(JsonNode oNode, Map<String, String> pathToNewName, boolean throwNotFound) throws UtilsException {
		renameFieldByPath(oNode, pathToNewName, false, throwNotFound);
	}
	public void renameFieldByPath(JsonNode oNode, Map<String, String> pathToNewName, boolean forceReorder, boolean throwNotFound) throws UtilsException {
		if(pathToNewName==null || pathToNewName.isEmpty()) {
			throw new UtilsException("Conversion map undefined");
		}
		for (String path : pathToNewName.keySet()) {
			String newName = pathToNewName.get(path);
			renameFieldByPath(oNode, path, newName, forceReorder, throwNotFound);
		}
	}
	public void renameFieldByPath(JsonNode oNode, SortedMap<String> pathToNewName) throws UtilsException {
		renameFieldByPath(oNode, pathToNewName, true);
	}
	public void renameFieldByPath(JsonNode oNode, SortedMap<String> pathToNewName, boolean throwNotFound) throws UtilsException {
		renameFieldByPath(oNode, pathToNewName, false, throwNotFound);
	}
	public void renameFieldByPath(JsonNode oNode, SortedMap<String> pathToNewName, boolean forceReorder, boolean throwNotFound) throws UtilsException {
		if(pathToNewName==null || pathToNewName.isEmpty()) {
			throw new UtilsException("Conversion map undefined");
		}
		for (String path : pathToNewName.keys()) {
			String newName = pathToNewName.get(path);
			renameFieldByPath(oNode, path, newName, forceReorder, throwNotFound);
		}
	}
	public void renameFieldByPath(JsonNode oNode, String path, String newName) throws UtilsException {
		renameFieldByPath(oNode, path, newName, true);
	}
	public void renameFieldByPath(JsonNode oNode, String path, String newName, boolean throwNotFound) throws UtilsException {
		renameFieldByPath(oNode, path, newName, false, throwNotFound);
	}
	public void renameFieldByPath(JsonNode oNode, String path, String newName, boolean forceReorder, boolean throwNotFound) throws UtilsException {
			
		boolean esito = true;
		int i = 0;
		while(esito == true && i < 1000000) { // per avere un limite
			try {
				_renameFieldByPath(oNode, path, newName, forceReorder, true); // lasciare true
				esito = true;
				if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName) || newName.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
					break; // serve un solo giro
				}
			}catch(UtilsException e) {
				if(i==0){
					if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName)) {
						if(! (e.getMessage()!=null && e.getMessage().contains(ALREADY_ARRAY_TYPE_ERROR)) ) {
							if(throwNotFound) {
								throw e;
							}
						}
					}
					else if(newName.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
						if(throwNotFound || (e.getMessage()!=null && !e.getMessage().contains("not found")) ) {
							throw e;
						}
					}
					else {
						if(throwNotFound) {
							throw e;
						}
					}
				}
				if(i>0) {
					// almeno 1 occorrenza gestita
					return;
				}
				esito = false;
			}
			i++;
		}
		
	}
	public void _renameFieldByPath(JsonNode oNode, String path, String newName, boolean forceReorder, boolean throwNotFound) throws UtilsException {

		String [] pathFragment = path.split("\\.");
		if(pathFragment==null || pathFragment.length<=0) {
			throw new UtilsException("Path undefined");
		}
		if(newName==null) {
			throw new UtilsException("New name undefined");
		}
		if(!oNode.isObject()) {
			throw new UtilsException("Node isn't object");
		}
		
		_StructureNode s = new _StructureNode();
		s.parentNode = oNode;
		s.nodeForRename = oNode;
		
		_renameFieldByPath(s, 
				path, pathFragment, 0,
				newName, forceReorder, throwNotFound);
	}
	
	private void _renameFieldByPath(_StructureNode structure, 
			String path, String [] pathFragment, int position,
			String newName, boolean forceReorder, boolean throwNotFound) throws UtilsException {
		
		//System.out.println("PROCESS ["+structure.parent+"]");
				
		boolean foglia = position==pathFragment.length-1;
		String name = pathFragment[position];
		
		JsonNode parentNode = structure.parentNode;
		JsonNode nodeForRename = structure.nodeForRename;
		/*String parent = structure.parent;
		System.out.println("navigo ["+name+"] (padre:"+parent+")");
		if(nodeForRename.get(name)==null) {
			System.out.println("isObject:"+nodeForRename.isObject());
			System.out.println("isArray:"+nodeForRename.isArray());
		}*/
		
		
		parentNode = nodeForRename;
		nodeForRename = nodeForRename.get(name);
		if(nodeForRename==null || nodeForRename instanceof com.fasterxml.jackson.databind.node.MissingNode) {
			if(throwNotFound && 
					!"".equals(path) // "" rappresenta il root element json
				) {
				throw new UtilsException("Element '"+name+"' not found (path '"+path+"')");
			}
		}
		if(foglia) {
			//System.out.println("padre:"+parent+")");
			//System.out.println("  newName:"+newName+")");
			//System.out.println("  nodeForRename:"+nodeForRename+")");
			//System.out.println("  parentNode:"+parentNode+")");
			
			if(forceReorder) {
				List<String> nomi = new ArrayList<String>();
				Iterator<String> it = ((ObjectNode)parentNode).fieldNames();
				if(it!=null) {
					while (it.hasNext()) {
						String fieldName = (String) it.next();
						nomi.add(fieldName);
					}
				}
				for (String fieldName : nomi) {
					JsonNode n = ((ObjectNode)parentNode).get(fieldName);
					if(fieldName.equals(name)) {
						
						// caso in gestione
						
						if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName)) {
							if(!n.isArray()) {
								//System.out.println("CONVERT TO ARRAY ["+path+"] ["+fieldName+"]");
								((ObjectNode)parentNode).remove(fieldName);
								ArrayNode arrayNode = ((ObjectNode)parentNode).arrayNode();
								arrayNode.add(n);
								((ObjectNode)parentNode).set(fieldName,arrayNode);
							}
							else {
								//System.out.println("GIA TROVATO COME ARRAY ["+path+"] ["+fieldName+"]");
								if(throwNotFound) {
									throw new UtilsException("Element '"+name+"' "+ALREADY_ARRAY_TYPE_ERROR+" (path '"+path+"')");
								}
							}
						}
						else {
							((ObjectNode)parentNode).remove(fieldName);
							((ObjectNode)parentNode).set(newName,n);
						}
					}
					else {
						
						// elementi da preservarne l'ordine
						
						((ObjectNode)parentNode).remove(fieldName);
						((ObjectNode)parentNode).set(fieldName,n);
					}
				}
			}
			else {
				if(newName.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
				
					// non supporta forceReorder, per quello non e' presente prima
					List<String> l = convertReorderFieldChildrenByPathKeyToList(newName);
					
					//System.out.println("REORDER--- "+l);
					
					JsonNode attuale = null;
					if("".equals(path)) {
						attuale = ((ObjectNode)parentNode);
					}
					else {
						attuale = ((ObjectNode)parentNode).get(name);
					}
					if(attuale.isArray()) {
						//throw new UtilsException("Cannot execute reorder in array object (path '"+path+"')");
						ArrayNode array = (ArrayNode) nodeForRename;
						if(array.size()>0) {
							for (int i = 0; i < array.size(); i++) {
								JsonNode arrayChildNode = array.get(i);
								if(arrayChildNode.isObject()) {
									// aggiungo elementi in ordine
									for (String nameReorder : l) {
										JsonNode n = arrayChildNode.get(nameReorder);
										if(n==null || n instanceof com.fasterxml.jackson.databind.node.MissingNode) {
											//System.out.println("REORDERN NOT FOUND PARENT throwNotFound["+throwNotFound+"] ["+(attuale)+"]");
											//throw new UtilsException("Element '"+nameReorder+"' for reorder not found (path '"+path+"')");
										}
										else {
											//System.out.println("REORDER["+nameReorder+"]");
											((ObjectNode)arrayChildNode).remove(nameReorder);
											((ObjectNode)arrayChildNode).set(nameReorder, n);
										}
									}
								}
								else {
									throw new UtilsException("Cannot execute reorder in simple object (path '"+path+"') (array position i="+i+")");
								}
							}
						}
					}
					else if(attuale.isObject()) {
						// aggiungo elementi in ordine
						for (String nameReorder : l) {
							JsonNode n = attuale.get(nameReorder);
							if(n==null || n instanceof com.fasterxml.jackson.databind.node.MissingNode) {
								//System.out.println("REORDERN NOT FOUND PARENT throwNotFound["+throwNotFound+"] ["+(attuale)+"]");
								//throw new UtilsException("Element '"+nameReorder+"' for reorder not found (path '"+path+"')");
							}
							else {
								//System.out.println("REORDER["+nameReorder+"]");
								((ObjectNode)attuale).remove(nameReorder);
								((ObjectNode)attuale).set(nameReorder, n);
							}
						}
					}
					else {
						throw new UtilsException("Cannot execute reorder in simple object (path '"+path+"')");
					}
					
				}
				else if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName)) {
					if(!nodeForRename.isArray()) {
						//System.out.println("CONVERT TO ARRAY ["+path+"] ["+name+"]");
						((ObjectNode)parentNode).remove(name);
						ArrayNode arrayNode = ((ObjectNode)parentNode).arrayNode();
						arrayNode.add(nodeForRename);
						((ObjectNode)parentNode).set(name,arrayNode);
					}
					else {
						//System.out.println("GIA TROVATO COME ARRAY ["+path+"] ["+name+"]");
						if(throwNotFound) {
							throw new UtilsException("Element '"+name+"' "+ALREADY_ARRAY_TYPE_ERROR+" (path '"+path+"')");
						}
					}
				}
				else {
					((ObjectNode)parentNode).remove(name);
					((ObjectNode)parentNode).set(newName,nodeForRename);
				}
				
			}
		}
		else {			
			if(nodeForRename.isObject() && nodeForRename instanceof ObjectNode) {
				_StructureNode s = new _StructureNode();
				s.parentNode = parentNode;
				s.nodeForRename = nodeForRename;
				s.parent = name;
				//System.out.println("CHIAMO PER OBJECT s.parent["+s.parent+"]");
				try {
					_renameFieldByPath(s, 
							path, pathFragment, position+1,
							newName, forceReorder, throwNotFound);
				}catch(UtilsException e) {
					if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName)) {
						if(! (e.getMessage()!=null && e.getMessage().contains(ALREADY_ARRAY_TYPE_ERROR)) ) {
							throw e;
						}
					}
					else if(newName.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
						if(e.getMessage()!=null && !e.getMessage().contains("not found")) {
							throw e;
						}
					}
					else {
						throw e;
					}
				}
			}
			else if(nodeForRename.isArray() && nodeForRename instanceof ArrayNode) {
				ArrayNode array = (ArrayNode) nodeForRename;
				if(array.size()>0) {
					for (int i = 0; i < array.size(); i++) {
						JsonNode arrayChildNode = array.get(i);
						_StructureNode s = new _StructureNode();
						s.parentNode = nodeForRename;
						s.nodeForRename = arrayChildNode;
						s.parent = name+"["+i+"]";
						//System.out.println("CHIAMO PER ARRAY s.parent["+s.parent+"]");
						try {
							_renameFieldByPath(s, 
									path, pathFragment, position+1,
									newName, forceReorder, throwNotFound);
						}catch(UtilsException e) {
							if(SPECIAL_KEY_CONVERT_TO_ARRAY.equals(newName)) {
								if(! (e.getMessage()!=null && e.getMessage().contains(ALREADY_ARRAY_TYPE_ERROR)) ) {
									throw e;
								}
							}
							else if(newName.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
								if(e.getMessage()!=null && !e.getMessage().contains("not found")) {
									throw e;
								}
							}
							else {
								throw e;
							}
						}
					}
				}
			}
			else {
				throw new UtilsException("Element '"+name+"' not found as object/array (path '"+path+"')");
			}
		}
		
	}
	
	public void renameFieldInCamelCase(JsonNode oNode, boolean firstLowerCase) throws UtilsException {
		renameFieldInCamelCase(oNode, firstLowerCase, false);
	}
	public void renameFieldInCamelCase(JsonNode oNode, boolean firstLowerCase, boolean forceReorder) throws UtilsException {
		SortedMap<String> mapForRename = new SortedMap<String>();
		if(!oNode.isObject()) {
			throw new UtilsException("Node isn't object");
		}
		renameFieldInCamelCase((ObjectNode)oNode, "", mapForRename, firstLowerCase, forceReorder);
		if(!mapForRename.isEmpty()) {
			/*for (String path : mapForRename.keys()) {
				System.out.println("MAP ["+path+"] -> ["+mapForRename.get(path)+"]");
			}*/
			renameFieldByPath(oNode, mapForRename);
		}
	}
	private static void renameFieldInCamelCase(ObjectNode oNode, String prefix, SortedMap<String> mapForRename,
			boolean firstLowerCase, boolean forceReorder) throws UtilsException {
		Iterator<String> it = oNode.fieldNames();
		if(it!=null) {
			while (it.hasNext()) {
				String fieldName = (String) it.next();
				JsonNode node = oNode.get(fieldName);
				
				if(node!=null){
					if(node.isObject() && node instanceof ObjectNode) {
						if(StringUtils.isNotEmpty(prefix) && !prefix.endsWith(".")){
							prefix = prefix + ".";
						}
						renameFieldInCamelCase((ObjectNode)node , prefix+fieldName, mapForRename, firstLowerCase, forceReorder);
					}
					else if(node.isArray() && node instanceof ArrayNode) {
						ArrayNode array = (ArrayNode) node;
						if(array.size()>0) {
							for (int i = 0; i < array.size(); i++) {
								JsonNode arrayChildNode = array.get(i);
								if(arrayChildNode.isObject() && arrayChildNode instanceof ObjectNode) {
									if(StringUtils.isNotEmpty(prefix) && !prefix.endsWith(".")){
										prefix = prefix + ".";
									}
									renameFieldInCamelCase((ObjectNode)arrayChildNode , prefix+fieldName, mapForRename, firstLowerCase, forceReorder);
								}
							}
						}
					}
				}
				
				String camelCaseName = camelCase(fieldName, firstLowerCase);
				if(!camelCaseName.equals(fieldName) || forceReorder) {
					
					if(StringUtils.isNotEmpty(prefix) && !prefix.endsWith(".")){
						prefix = prefix + ".";
					}
					String key = prefix+fieldName;
					if(!mapForRename.containsKey(key)) {
						mapForRename.add(key, camelCaseName);
					}
				}
			}
		}
	}
	
	public static String camelCase(String fieldName, boolean firstLowerCase) {
		
		boolean firstCase = firstLowerCase ? Character.isUpperCase(fieldName.charAt(0)) : Character.isLowerCase(fieldName.charAt(0));
		boolean charSpecial = fieldName.contains("-") || fieldName.contains("_");
		if (firstCase || charSpecial) {
		
			String newName = fieldName;
			//System.out.println("ANALIZZO ["+newName+"]");
			if(firstCase) {
				if(firstLowerCase) {
					newName = (fieldName.length()>1) ? 
							(((fieldName.charAt(0)+"").toLowerCase()) + fieldName.substring(1)) 
							: 
							(fieldName.toLowerCase());
				}
				else {
					newName = (fieldName.length()>1) ? 
							(((fieldName.charAt(0)+"").toUpperCase()) + fieldName.substring(1)) 
							: 
							(fieldName.toUpperCase());
				}
				//System.out.println("DOPO UPPER ["+newName+"]");
			}
			
			if(charSpecial) {
				int indexOf = newName.indexOf("_");
				while(indexOf>=0){
					if((indexOf+1)<newName.length()){
						if((indexOf+2)<newName.length()){
							newName = newName.substring(0, indexOf) + (newName.charAt(indexOf+1)+"").toUpperCase() + newName.substring(indexOf+2,newName.length());
						}else{
							newName = newName.substring(0, indexOf) + (newName.charAt(indexOf+1)+"").toUpperCase();
						}
						indexOf = newName.indexOf("_");
					}else{
						break;
					}
				}
	
				indexOf = newName.indexOf("-");
				while(indexOf>=0){
					if((indexOf+1)<newName.length()){
						if((indexOf+2)<newName.length()){
							newName = newName.substring(0, indexOf) + (newName.charAt(indexOf+1)+"").toUpperCase() + newName.substring(indexOf+2,newName.length());
						}else{
							newName = newName.substring(0, indexOf) + (newName.charAt(indexOf+1)+"").toUpperCase();
						}
						indexOf = newName.indexOf("-");
					}else{
						break;
					}
				}
				//System.out.println("DOPO CHAR ["+newName+"]");
			}
			
			return newName;
		}
		else {
			return fieldName;
		}
			
	}
	
	
	
	// CONVERT TYPE
	
	private static final String SPECIAL_KEY_CONVERT_TO_ARRAY = "___CONVERT_TO_ARRAY__";
	private static final String ALREADY_ARRAY_TYPE_ERROR = "already array type";
	
	public void convertFieldToArrayByPath(JsonNode oNode, List<String> listPath) throws UtilsException {
		SortedMap<String> pathToNewName = new SortedMap<String>();
		if(listPath!=null) {
			for (String path : listPath) {
				pathToNewName.add(path, SPECIAL_KEY_CONVERT_TO_ARRAY);
			}
		}
		renameFieldByPath(oNode, pathToNewName, true);
	}
	public void convertFieldToArrayByPath(JsonNode oNode, List<String> listPath, boolean throwNotFound) throws UtilsException {
		SortedMap<String> pathToNewName = new SortedMap<String>();
		if(listPath!=null) {
			for (String path : listPath) {
				pathToNewName.add(path, SPECIAL_KEY_CONVERT_TO_ARRAY);
			}
		}
		renameFieldByPath(oNode, pathToNewName, throwNotFound);
	}
	public void convertFieldToArrayByPath(JsonNode oNode, List<String> listPath, boolean forceReorder, boolean throwNotFound) throws UtilsException {
		SortedMap<String> pathToNewName = new SortedMap<String>();
		if(listPath!=null) {
			for (String path : listPath) {
				pathToNewName.add(path, SPECIAL_KEY_CONVERT_TO_ARRAY);
			}
		}
		renameFieldByPath(oNode, pathToNewName, forceReorder, throwNotFound);
	}
	public void convertFieldToArrayByPath(JsonNode oNode, String path) throws UtilsException {
		renameFieldByPath(oNode, path, SPECIAL_KEY_CONVERT_TO_ARRAY, true);
	}
	public void convertFieldToArrayByPath(JsonNode oNode, String path, boolean throwNotFound) throws UtilsException {
		renameFieldByPath(oNode, path, SPECIAL_KEY_CONVERT_TO_ARRAY, throwNotFound);
	}
	public void convertFieldToArrayByPath(JsonNode oNode, String path, boolean forceReorder, boolean throwNotFound) throws UtilsException {
		renameFieldByPath(oNode, path, SPECIAL_KEY_CONVERT_TO_ARRAY, forceReorder, throwNotFound);
	}
 
	
	// ORDER ELEMENTS TYPE
	
	private static final String SPECIAL_KEY_ORDER_ELEMENTS_PREFIX = "___REORDER__";
	private static final String SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR = ",";
	
	public void reorderFieldChildrenByPath(JsonNode oNode, String path, String ... child) throws UtilsException {
		renameFieldByPath(oNode, path, SPECIAL_KEY_ORDER_ELEMENTS_PREFIX+toReorderFieldChildrenByPathSuffix(child), true);
	}
	public void reorderFieldChildrenByPath(JsonNode oNode, String path, boolean throwNotFound, String ... child) throws UtilsException {
		String key = SPECIAL_KEY_ORDER_ELEMENTS_PREFIX+toReorderFieldChildrenByPathSuffix(child);
		//System.out.println("KEY ["+key+"]");
		renameFieldByPath(oNode, path, key, throwNotFound);
	}
	private String toReorderFieldChildrenByPathSuffix(String ... child) throws UtilsException {
		if(child==null || child.length<=0) {
			throw new UtilsException("Children undefined");
		}
		StringBuilder sb = new StringBuilder();
		for (String c : child) {
			if(sb.length()>0) {
				sb.append(SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR);
			}
			if(c.contains(SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR)) {
				throw new UtilsException("Invalid child name ["+c+"] (contains '"+SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR+"')");
			}
			sb.append(c);
		}
		return sb.toString();
	}
	private List<String> convertReorderFieldChildrenByPathKeyToList(String v) throws UtilsException{
		String parsV = v;
		if(parsV.startsWith(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX)) {
			parsV = parsV.substring(SPECIAL_KEY_ORDER_ELEMENTS_PREFIX.length());
		}
		List<String> l = new ArrayList<String>();
		if(parsV.contains(SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR)) {
			String [] tmp = parsV.split(SPECIAL_KEY_ORDER_ELEMENTS_SEPARATOR);
			if(tmp!=null && tmp.length>0) {
				for (String c : tmp) {
					l.add(c);
				}
			}
		}
		else {
			l.add(parsV);
		}
		if(l.isEmpty()) {
			throw new UtilsException("Children not found");
		}
		return l;
	}
}

class _StructureNode {
	
	JsonNode parentNode;
	JsonNode nodeForRename;
	String parent;
	
}
