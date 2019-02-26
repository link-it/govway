/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.message.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.regexp.RegularExpressionPatternCompileMode;

/**
 * AbstractMediaTypeCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractMediaTypeCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int STATUS_DEFAULT = 0;
	private static final String SEPARATORE = "__@@@__";
	private String buildKey(String mediaType, Integer status) {
		if(status==null) {
			status = STATUS_DEFAULT;
		}
		return status+SEPARATORE+mediaType;
	}
	private String extractMediaTypeFromKey(String key) {
		return key.split(SEPARATORE)[1];
	}
	private int extractStatusFromKey(String key) {
		return Integer.parseInt(key.split(SEPARATORE)[0]);
	}
	
	// Vengono utilizzate due liste per preservare l'ordine di inserimento che si perde in una hashtable,
	private List<String> map_status_mediaTypes = new ArrayList<String>();
	private List<MessageType> map_messageProcessor = new ArrayList<MessageType>();
	private List<Boolean> map_useRegularExpression = new ArrayList<Boolean>();

	public void addDefaultMediaType(MessageType version) throws MessageException{
		this.addDefaultMediaType(null, version);
	}
	public void addUndefinedMediaType(MessageType version) throws MessageException{
		this.addUndefinedMediaType(null, version);
	}
	public void addMediaType(String mediaType,MessageType version,boolean regExpr) throws MessageException{
		this.addMediaType(mediaType, null, version, regExpr);
	}
	private void addDefaultMediaType(Integer status, MessageType version) throws MessageException{
		this.addMediaType(Costanti.CONTENT_TYPE_ALL, status, version, false);
	}
	private void addUndefinedMediaType(Integer status, MessageType version) throws MessageException{
		this.addMediaType(Costanti.CONTENT_TYPE_NOT_DEFINED, status, version, false);
	}
	private void addMediaType(String mediaType,Integer status, MessageType version,boolean regExpr) throws MessageException{
		if(mediaType==null){
			throw new MessageException("MediaType not defined");
		}
		if(version==null){
			throw new MessageException("MessageProcessorVersion not defined");
		}
		String key = this.buildKey(mediaType, status);
		if(this.map_status_mediaTypes.contains(key)){
			String stato = "";
			if(status!=null && STATUS_DEFAULT!=status.intValue()) {
				stato = " (http-status:"+status+")";
			}
			throw new MessageException("MediaType"+stato+" already defined for MessageProcessorVersion "+this.getMessageProcessorVersion(mediaType, status, false));
		}
		this.map_status_mediaTypes.add(key);
		this.map_messageProcessor.add(version);
		this.map_useRegularExpression.add(regExpr);
	}
	
	public List<String> getContentTypes() {
		List<String> l = new ArrayList<String>();
		for (String key : this.map_status_mediaTypes) {
			String mediaType = extractMediaTypeFromKey(key);
			if(l.contains(mediaType)==false) {
				l.add(mediaType);
			}
		}
		return l;
	}
	
	public void clear(){
		this.map_status_mediaTypes.clear();
		this.map_messageProcessor.clear();
		this.map_useRegularExpression.clear();
	}
	
	public void addOrReplaceMediaType(String mediaType,MessageType version,boolean regExpr) throws MessageException{
		this.addOrReplaceMediaType(mediaType, null, version, regExpr);
	}
	public void addOrReplaceMediaType(String mediaType,Integer status,MessageType version,boolean regExpr) throws MessageException{
		String key = this.buildKey(mediaType, status);
		if(this.map_status_mediaTypes.contains(key)){
			this.removeMediaType(mediaType,status);
		}
		this.addMediaType(mediaType, status, version, regExpr);
	}
	
	public void removeMediaType(String mediaType) throws MessageException{
		this.removeMediaType(mediaType, null);
	}
	public void removeMediaType(String mediaType,Integer status) throws MessageException{
		String key = this.buildKey(mediaType, status);
		int index = -1;
		if(this.map_status_mediaTypes.contains(key)){
			for (int i = 0; i < this.map_status_mediaTypes.size(); i++) {
				String keyCheck = this.map_status_mediaTypes.get(i);
				if(keyCheck.equals(key)){
					index = i;
					break;
				}
			}
		}
		if(index>=0){
			this.map_useRegularExpression.remove(index);
			this.map_messageProcessor.remove(index);
			this.map_status_mediaTypes.remove(index);
		}
	}
	
	public MessageType getMessageProcessor(String mediaType) throws MessageException{
		return this.getMessageProcessor(mediaType, null);
	}
	public MessageType getMessageProcessor(String mediaType,Integer status) throws MessageException{
		return this.getMessageProcessorVersion(mediaType, status, true);
	}
	private MessageType getMessageProcessorVersion(String mediaType,Integer status, boolean checkExpression) throws MessageException{
		
		if(status==null) {
			status = STATUS_DEFAULT;
		}
		
		for (int i = 0; i < this.map_status_mediaTypes.size(); i++) {
			
			String keyCheck = this.map_status_mediaTypes.get(i);
			int statusCheck = this.extractStatusFromKey(keyCheck);
			if(statusCheck!=status.intValue()) {
				continue;
			}
			
			String mediaTypeCheck = this.extractMediaTypeFromKey(keyCheck);
			MessageType mt = this._getMessageProcessorVersionEngine(checkExpression, i, mediaType, mediaTypeCheck);
			if(mt!=null) {
				return mt;
			}
			
		}	
		
		// provo a cercare sugli stati uguali a default
		if(status.intValue()!=STATUS_DEFAULT) {
		
			for (int i = 0; i < this.map_status_mediaTypes.size(); i++) {
				
				String keyCheck = this.map_status_mediaTypes.get(i);
				int statusCheck = this.extractStatusFromKey(keyCheck);
				if(statusCheck==STATUS_DEFAULT) {
					String mediaTypeCheck = this.extractMediaTypeFromKey(keyCheck);
					MessageType mt = this._getMessageProcessorVersionEngine(checkExpression, i, mediaType, mediaTypeCheck);
					if(mt!=null) {
						return mt;
					}
				}
				
			}
			
		}
		
		return null; // ritorno anzi null per gestire la differenza rispetto all'eccezione sopra
	}
	private MessageType _getMessageProcessorVersionEngine(boolean checkExpression, int i, String mediaType, String mediaTypeCheck) throws MessageException {
		if(checkExpression){
			if(this.map_useRegularExpression.get(i)){
				if(mediaType==null){
					return null;
				}
				String pattern = mediaTypeCheck;
				try{
					if(RegularExpressionEngine.isMatch(mediaType, pattern, RegularExpressionPatternCompileMode.CASE_INSENSITIVE)){
						return this.map_messageProcessor.get(i);
					}
				}catch(Exception e){
					throw new MessageException("Errore durante la comprensione del content-type ["+mediaType+"] (pattern:"+pattern+"): "+e.getMessage(),e);
				}
			}
			else{
				if(Costanti.CONTENT_TYPE_ALL.equals(mediaTypeCheck)){
					return this.map_messageProcessor.get(i);
				}
				else if(Costanti.CONTENT_TYPE_NOT_DEFINED.equals(mediaTypeCheck) && 
						(mediaType==null || "".equals(mediaType.trim()))){
					return this.map_messageProcessor.get(i);
				}
				else if(mediaTypeCheck.equals(mediaType)){
					return this.map_messageProcessor.get(i);
				}
			}
		}
		else{
			if(mediaTypeCheck.equals(mediaType)){
				return this.map_messageProcessor.get(i);
			}
		}
		return null;
	}
}
