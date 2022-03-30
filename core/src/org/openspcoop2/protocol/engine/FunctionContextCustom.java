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

package org.openspcoop2.protocol.engine;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class FunctionContextCustom implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String context;
	private IDService idService;
	private HashMap<String,IDService> subcontext;
	
	public FunctionContextCustom(String context,IDService idService) throws ProtocolException {
		this(context,idService,null);
	}
	public FunctionContextCustom(String context,HashMap<String,IDService> subcontext) throws ProtocolException {
		this(context,null,subcontext);
	}
	private FunctionContextCustom(String context,IDService idServiceParam, HashMap<String,IDService> subcontextMap) throws ProtocolException {
		
		if(context==null) {
			throw new ProtocolException("Context undefined");
		}
		context = context.trim();
		if(!StringUtils.isAlphanumeric(context)) {
			throw new ProtocolException("Context '"+context+"' unsupported: only alphanumeric character permitted");
		}
		this.context = context;
		
		if(idServiceParam==null && (subcontextMap==null || subcontextMap.size()<=0)) {
			throw new ProtocolException("IDService and Subcontext not found");
		}
		
		if(idServiceParam!=null) {
			checkIDService(idServiceParam);
			this.idService = idServiceParam;
		}

		if(subcontextMap!=null && subcontextMap.size()>0) {	
			Iterator<String> it = subcontextMap.keySet().iterator();
			while (it.hasNext()) {
				String subcontext = (String) it.next();
				IDService idService = subcontextMap.get(subcontext);
							
				// controllo che il subcontext non possieda un prefisso gia' utilizzato in un altro subcontext
				int count = 0;
				Iterator<String> itCheck = subcontextMap.keySet().iterator();
				while (itCheck.hasNext()) {
					String subcontextRegistered = (String) itCheck.next();
					if(subcontextRegistered.equals(subcontext)) {
						count++;
						continue;
					}
					if(subcontext.length()>=subcontextRegistered.length()) {
						if(subcontext.startsWith(subcontextRegistered)) {
							throw new ProtocolException("Subcontext '"+subcontext+"' not valid: exists another subcontext '"+subcontextRegistered+"' with same prefix");
						}
					}
				}
				if(count>1) {
					throw new ProtocolException("Subcontext '"+subcontext+"' registered more than one (founded:"+count+")");
				}
			
				String tmp = new String(subcontext);
				while(tmp.contains("/")) {
					tmp = tmp.replace("/", "");
				}
				if(!StringUtils.isAlphanumeric(tmp)) {
					throw new ProtocolException("Subcontext '"+subcontext+"' unsupported: only alphanumeric character or '/' permitted");
				}
				if(subcontext.startsWith("/")) {
					throw new ProtocolException("Subcontext '"+subcontext+"' unsupported: cannot start with '/' character");
				}
				if(subcontext.endsWith("/")) {
					throw new ProtocolException("Subcontext '"+subcontext+"' unsupported: cannot ends with '/' character");
				}
				
				checkIDService(idService);
			}
			this.subcontext = subcontextMap;
		}
	}
	
	private static void checkIDService(IDService idService) throws ProtocolException {
		if(!IDService.PORTA_DELEGATA.equals(idService) &&
				!IDService.PORTA_DELEGATA_NIO.equals(idService) &&
				!IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(idService) &&
				!IDService.PORTA_DELEGATA_XML_TO_SOAP_NIO.equals(idService) &&
				!IDService.PORTA_APPLICATIVA.equals(idService) &&
				!IDService.PORTA_APPLICATIVA_NIO.equals(idService) &&
				!IDService.INTEGRATION_MANAGER_SOAP.equals(idService) ) {
			throw new ProtocolException("IDService '"+idService+"' unsupported");
		}
	}
	
	public boolean isMatch(String contextParam,String subContextParam) {
		return this.getServiceMatch(contextParam, subContextParam)!=null;
	}
	public IDService getServiceMatch(String contextParam,String subContextParam) {
		if(this.context.equals(contextParam)) {
			
			// cerco prima nel subcontext
			if(subContextParam!=null && !"".equals(subContextParam) &&
					this.subcontext!=null && this.subcontext.size()>0) {
				Iterator<String> it = this.subcontext.keySet().iterator();
				while (it.hasNext()) {
					String subcontext = (String) it.next();
					if(subContextParam.startsWith(subcontext)) {
						IDService idService = this.subcontext.get(subcontext);
						return idService;
					}
				}
			}
			
			// poi senza
			if(this.idService!=null) {
				return this.idService;
			}

		}
		return null;
	}
	public String getFunctionMatch(String contextParam,String subContextParam) {
		if(this.context.equals(contextParam)) {
			
			// cerco prima nel subcontext
			if(subContextParam!=null && !"".equals(subContextParam) &&
					this.subcontext!=null && this.subcontext.size()>0) {
				Iterator<String> it = this.subcontext.keySet().iterator();
				while (it.hasNext()) {
					String subcontext = (String) it.next();
					if(subContextParam.startsWith(subcontext)) {
						return this.context+"/"+subcontext;
					}
				}
			}
			
			// poi senza
			if(this.idService!=null) {
				return this.context;
			}

		}
		return null;
	}
	
	public String getContext() {
		return this.context;
	}
	public void setContext(String context) {
		this.context = context;
	}

	public IDService getIdService() {
		return this.idService;
	}
	public void setIdService(IDService idService) {
		this.idService = idService;
	}
	
	public HashMap<String, IDService> getSubcontext() {
		return this.subcontext;
	}
}
