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

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;

/**
 * SoapMediaTypeCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapMediaTypeCollection extends AbstractMediaTypeCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private SoapBinding binding;
	
	public SoapMediaTypeCollection(SoapBinding binding){
		this.binding = binding;
	}
	
	private void checkVersion(MessageType version) throws MessageException{
		if(MessageType.SOAP_11.equals(version)){
			if(!this.binding.isBinding_soap11()){
				throw new MessageException("MessageType ["+version+"] not supported in SoapBinding; Soap11 disabled");
			}
		}
		else if(MessageType.SOAP_12.equals(version)){
			if(!this.binding.isBinding_soap12()){
				throw new MessageException("MessageType ["+version+"] not supported in SoapBinding; Soap12 disabled");
			}
		}
		else{
			throw new MessageException("MessageType ["+version+"] not supported in SoapBinding");
		}
	}
	
	@Override
	public void addDefaultMediaType(MessageType version) throws MessageException{
		this.checkVersion(version);
		super.addDefaultMediaType(version);
	}
	@Override
	public void addUndefinedMediaType(MessageType version) throws MessageException{
		this.checkVersion(version);
		super.addUndefinedMediaType(version);
	}
	@Override
	public void addMediaType(String mediaType,MessageType version,boolean regExpr) throws MessageException{
		this.checkVersion(version);
		super.addMediaType(mediaType, version, regExpr);
	}
	
}
