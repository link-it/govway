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
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public abstract class AbstractMediaTypeCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Vengono utilizzate due liste per preservare l'ordine di inserimento che si perde in una hashtable,
	protected List<String> map_mediaTypes = new ArrayList<String>();
	protected List<MessageType> map_messageProcessor = new ArrayList<MessageType>();
	protected List<Boolean> map_useRegularExpression = new ArrayList<Boolean>();

	public void addDefaultMediaType(MessageType version) throws MessageException{
		this.addMediaType(Costanti.CONTENT_TYPE_ALL, version, false);
	}
	public void addUndefinedMediaType(MessageType version) throws MessageException{
		this.addMediaType(Costanti.CONTENT_TYPE_NOT_DEFINED, version, false);
	}
	public void addMediaType(String mediaType,MessageType version,boolean regExpr) throws MessageException{
		if(mediaType==null){
			throw new MessageException("MediaType not defined");
		}
		if(version==null){
			throw new MessageException("MessageProcessorVersion not defined");
		}
		if(this.map_mediaTypes.contains(mediaType)){
			throw new MessageException("MediaType already defined for MessageProcessorVersion "+this.getMessageProcessorVersion(mediaType, false));
		}
		this.map_mediaTypes.add(mediaType);
		this.map_messageProcessor.add(version);
		this.map_useRegularExpression.add(regExpr);
	}
	
	public List<String> getContentTypes() {
		return this.map_mediaTypes;
	}
	
	public void clear(){
		this.map_mediaTypes.clear();
		this.map_messageProcessor.clear();
		this.map_useRegularExpression.clear();
	}
	
	public void addOrReplaceMediaType(String mediaType,MessageType version,boolean regExpr) throws MessageException{
		if(this.map_mediaTypes.contains(mediaType)){
			this.removeMediaType(mediaType);
		}
		this.addMediaType(mediaType, version, regExpr);
	}
	
	public void removeMediaType(String mediaType) throws MessageException{
		int index = -1;
		if(this.map_mediaTypes.contains(mediaType)){
			for (int i = 0; i < this.map_mediaTypes.size(); i++) {
				if(this.map_mediaTypes.get(i).equals(mediaType)){
					index = i;
					break;
				}
			}
		}
		if(index>=0){
			this.map_useRegularExpression.remove(index);
			this.map_messageProcessor.remove(index);
			this.map_mediaTypes.remove(index);
		}
	}
	
	public MessageType getMessageProcessor(String mediaType) throws MessageException{
		return this.getMessageProcessorVersion(mediaType, true);
	}
	
	private MessageType getMessageProcessorVersion(String mediaType, boolean checkExpression) throws MessageException{
		for (int i = 0; i < this.map_mediaTypes.size(); i++) {
			if(checkExpression){
				if(this.map_useRegularExpression.get(i)){
					if(mediaType==null){
						continue;
					}
					String pattern = this.map_mediaTypes.get(i);
					try{
						if(RegularExpressionEngine.isMatch(mediaType, pattern, RegularExpressionPatternCompileMode.CASE_INSENSITIVE)){
							return this.map_messageProcessor.get(i);
						}
					}catch(Exception e){
						throw new MessageException("Errore durante la comprensione del content-type ["+mediaType+"] (pattern:"+pattern+"): "+e.getMessage(),e);
					}
				}
				else{
					if(Costanti.CONTENT_TYPE_ALL.equals(this.map_mediaTypes.get(i))){
						return this.map_messageProcessor.get(i);
					}
					else if(Costanti.CONTENT_TYPE_NOT_DEFINED.equals(this.map_mediaTypes.get(i)) && 
							(mediaType==null || "".equals(mediaType.trim()))){
						return this.map_messageProcessor.get(i);
					}
					else if(this.map_mediaTypes.get(i).equals(mediaType)){
						return this.map_messageProcessor.get(i);
					}
				}
			}
			else{
				if(this.map_mediaTypes.get(i).equals(mediaType)){
					return this.map_messageProcessor.get(i);
				}
			}
		}	
		return null; // ritorno anzi null per gestire la differenza rispetto all'eccezione sopra
	}
}
