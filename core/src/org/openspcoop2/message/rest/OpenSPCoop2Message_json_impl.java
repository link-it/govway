/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathReturnType;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

import com.fasterxml.jackson.databind.JsonNode;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi json
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_json_impl extends AbstractBaseOpenSPCoop2RestMessage<String> implements OpenSPCoop2RestJsonMessage {

	public OpenSPCoop2Message_json_impl(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.supportReadOnly = false; // il contenuto e' gia una stringa.
	}
	public OpenSPCoop2Message_json_impl(OpenSPCoop2MessageFactory messageFactory, InputStream is,String contentType) throws MessageException {
		super(messageFactory, is, contentType);
		this.supportReadOnly = false; // il contenuto e' gia una stringa.
	}
	
	@Override
	protected String buildContent() throws MessageException{
		try{
			//return org.openspcoop2.utils.Utilities.getAsString(this.countingInputStream, this.contentTypeCharsetName);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			CopyStream.copy(this._getInputStream(), bout);
			bout.flush();
			bout.close();
			return bout.toString(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally {
			try {
				this._getInputStream().close();
			}catch(Exception eClose) {
				// close
			}
		}
	}
	@Override
	protected String buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException{
		try{
			return contentBuffer.toString(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected String buildContentAsString() throws MessageException{
		return this.content;
	}
	@Override
	protected byte[] buildContentAsByteArray() throws MessageException{
		try{
			return this.content.getBytes(this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		Writer w = null;
		try{
			w = new OutputStreamWriter(os,this.contentTypeCharsetName);
			w.write(this.content);
			w.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally{
			try{
				if(w!=null){
					w.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	@Override
	public boolean isProblemDetailsForHttpApis_RFC7807() throws MessageException,MessageNotSupportedException {
		try{
			if(this.contentType==null) {
				return false;
			}
			String baseType = ContentTypeUtilities.readBaseTypeFromContentType(this.contentType);
			return HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equalsIgnoreCase(baseType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	public void prettyFormatContent() throws MessageException,MessageNotSupportedException{
		try {
			if(this.hasContent()) {
				String content = this.getContent();
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				JsonNode node = jsonUtils.getAsNode(content);
				this.updateContent(jsonUtils.toString(node));
			}
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public void addSimpleElement(String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(true, null, name, value, true, false, false);
	}
	@Override
	public void addSimpleElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(false, jsonPath, name, value, true, false, false);
	}
	@Override
	public void addObjectElement(String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(true, null, name, toJSONObject(value), true, false, false);
	}
	@Override
	public void addObjectElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(false, jsonPath, name, toJSONObject(value), true, false, false);
	}
	private net.minidev.json.JSONObject toJSONObject(Object valueParam) throws MessageException {
		net.minidev.json.JSONObject value = null;
		if(valueParam instanceof net.minidev.json.JSONObject) {
			value = (net.minidev.json.JSONObject) valueParam;
		}
		else if(valueParam instanceof String) {
			try {
				value = JsonPathExpressionEngine.getJSONObject((String)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else if(valueParam instanceof JsonNode) {
			try {
				value = JsonPathExpressionEngine.getJSONObject((JsonNode)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else if(valueParam instanceof InputStream) {
			try {
				value = JsonPathExpressionEngine.getJSONObject((InputStream)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else {
			throw new MessageException("Unsupported type '"+valueParam.getClass().getName()+"'");
		}
		return value;
	}
	
	@Override
	public void addArrayElement(String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(true, null, name, toJSONArray(value), true, false, false);
	}
	@Override
	public void addArrayElement(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(false, jsonPath, name, toJSONArray(value), true, false, false);
	}
	private net.minidev.json.JSONArray toJSONArray(Object valueParam) throws MessageException {
		net.minidev.json.JSONArray value = null;
		if(valueParam instanceof net.minidev.json.JSONArray) {
			value = (net.minidev.json.JSONArray) valueParam;
		}
		else if(valueParam instanceof String) {
			try {
				value = JsonPathExpressionEngine.getJSONArray((String)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else if(valueParam instanceof JsonNode) {
			try {
				value = JsonPathExpressionEngine.getJSONArray((JsonNode)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else if(valueParam instanceof InputStream) {
			try {
				value = JsonPathExpressionEngine.getJSONArray((InputStream)valueParam);
			}catch(Exception e) {
				throw new MessageException(e.getMessage(),e);
			}
		}
		else {
			throw new MessageException("Unsupported type '"+valueParam.getClass().getName()+"'");
		}
		return value;
	}

	@Override
	public void removeElement(String name) throws MessageException,MessageNotSupportedException{
		this._processJsonField(true, null, name, null, false, true, false);
	}
	@Override
	public void removeElement(String jsonPath, String name) throws MessageException,MessageNotSupportedException{
		this._processJsonField(false, jsonPath, name, null, false, true, false);
	}
	
	@Override
	public void replaceValue(String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(true, null, name, value, false, false, true);
	}
	@Override
	public void replaceValue(String jsonPath, String name, Object value) throws MessageException,MessageNotSupportedException{
		this._processJsonField(false, jsonPath, name, value, false, false, true);
	}
	
	private void _processJsonField(boolean rootElement, String jsonPath, String name, Object value, boolean add, boolean remove, boolean replaceValue) throws MessageException {
		try {
			if(!this.hasContent()) {
				return;
			}
			JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
			JSONObject rootObject = JsonPathExpressionEngine.getJSONObject(this.getContent());
			Object o = rootObject;
			if(!rootElement) {
				if(jsonPath==null) {
					throw new Exception("JsonPath undefined");
				}
				o = engine.getMatchPattern(rootObject, jsonPath, JsonPathReturnType.JSON_PATH_OBJECT);
			}
			if(o instanceof net.minidev.json.JSONObject) {
				net.minidev.json.JSONObject oNode = (net.minidev.json.JSONObject) o;
				if(add) {
					oNode.appendField(name, value);
				}
				else if(replaceValue) {
					oNode.remove(name);
					oNode.appendField(name, value);
				}
				else {
					oNode.remove(name);
				}
			}
			else if(o instanceof net.minidev.json.JSONArray){
				net.minidev.json.JSONArray arrayNode = (net.minidev.json.JSONArray) o;
				if(arrayNode.size()>0) {
					for (int i = 0; i < arrayNode.size(); i++) {
						Object oNodeArray = arrayNode.get(i);
						if(oNodeArray instanceof net.minidev.json.JSONObject) {
							net.minidev.json.JSONObject oNode = (net.minidev.json.JSONObject) oNodeArray;
							if(add) {
								oNode.appendField(name, value);
							}
							else if(replaceValue) {
								oNode.remove(name);
								oNode.appendField(name, value);
							}
							else {
								oNode.remove(name);
							}
						}
						else {
							throw new Exception("Tipo dell'oggetto individuato tramite jsonPath (posizione array '"+i+"') non consente l'operazione richiesta: "+oNodeArray.getClass().getName());
						}
					}
				}
			}
			else {
				throw new Exception("Tipo dell'oggetto individuato tramite jsonPath non consente l'operazione richiesta: "+o.getClass().getName());
			}
			this.updateContent(rootObject.toJSONString(JSONStyle.NO_COMPRESS));
		}catch(Exception e) {
			throw new MessageException("Operazione fallita (pattern: "+jsonPath+"): "+e.getMessage(),e);
		}
	}
}
