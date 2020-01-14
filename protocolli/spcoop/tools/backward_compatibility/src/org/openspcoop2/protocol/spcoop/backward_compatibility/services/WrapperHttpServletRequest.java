/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.spcoop.backward_compatibility.services;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.utils.transport.http.WrappedHttpServletRequest;

/**
 * WrapperHttpServletRequest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrapperHttpServletRequest extends WrappedHttpServletRequest {

	private String protocolContext;
	public WrapperHttpServletRequest(HttpServletRequest httpServletRequest,BackwardCompatibilityProperties backwardCompatibilityProperties) throws ProtocolException{
		super(httpServletRequest);

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(backwardCompatibilityProperties.getProtocolName());
		Web web = protocolFactory.getManifest().getWeb();
		if(web.sizeContextList()>0){
			// uso il primo contesto trovato, uno vale l'altro
			this.protocolContext = web.getContext(0).getName();
		}
		else if(web.getEmptyContext()!=null && web.getEmptyContext().getEnabled()){
			this.protocolContext = null; // empty context
		}
		else{
			throw new ProtocolException("Protocollo ["+backwardCompatibilityProperties.getProtocolName()+"] non possiede ne un web context ne un empty context ??");
		}
	}
	
	// Metodi modificati nei risultati
	
	@Override
	public String getRequestURI() {
		// System.out.println("getRequestURI=["+this.getRequestURI()+"]");
		// getRequestURI=[/govway/out/NOME_PORTA_DELEGATA]
		if(this.protocolContext!=null){
			String contextPath = this.getContextPath();
			if(this.httpServletRequest.getRequestURI().startsWith(contextPath))
				return contextPath + "/" + this.protocolContext + this.httpServletRequest.getRequestURI().substring(contextPath.length());
		}
		return this.httpServletRequest.getRequestURI();
	}
		
	@Override
	public String getContextPath() {
		//System.out.println("getContextPath=["+this.getContextPath()+"]");
		// getContextPath=[/openspcoop]
		return this.httpServletRequest.getContextPath();
	}
	
	@Override
	public String getPathInfo() {
		// System.out.println("getPathInfo=["+this.getPathInfo()+"]");
		// getPathInfo=[/PDTEST3]
		return this.httpServletRequest.getPathInfo();
	}
	
	@Override
	public String getPathTranslated() {
		// System.out.println("getPathTranslated=["+this.getPathTranslated()+"]");
		// getPathTranslated=[/opt/local/Programmi/jboss-eap-6.1/standalone/tmp/vfs/deployment6bee5b8b9c3c420/OpenSPCoopV1BackwardCompatibility.war-54bba00da721fbef/PDTEST3]
		return this.httpServletRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		// System.out.println("getQueryString=["+this.getQueryString()+"]");
		// getQueryString=[SPCoopServizioApplicativo=XYZ]
		return this.httpServletRequest.getQueryString();
	}
	
	@Override
	public StringBuffer getRequestURL() {
		//System.out.println("getRequestURL=["+this.getRequestURL()+"]");
		//getRequestURL=[http://127.0.0.1:3333/govway/out/PDTEST3]
		
		String requestURL = null;
		if(this.httpServletRequest.getRequestURL()!=null){
			requestURL = this.httpServletRequest.getRequestURL().toString();
		}
		if(requestURL!=null){
			if(this.protocolContext!=null){
				String contextPath = this.getContextPath();
				if(requestURL.contains(contextPath)){
					int indexOf = requestURL.indexOf(contextPath);
					StringBuffer bfReturn = new StringBuffer();
					bfReturn.append(requestURL.substring(0, indexOf));
					bfReturn.append(contextPath);
					bfReturn.append("/");
					bfReturn.append(this.protocolContext);
					bfReturn.append(requestURL.substring(indexOf+contextPath.length(), requestURL.length()));
					return bfReturn;
				}
			}
		}
		
		return this.httpServletRequest.getRequestURL();
	}
	
	@Override
	public String getServletPath() {
		//System.out.println("getServletPath=["+this.getServletPath()+"]");
		//getServletPath=[/PD]
		if(this.protocolContext!=null){
			return "/"+this.protocolContext+this.httpServletRequest.getServletPath();
		}else{
			return this.httpServletRequest.getServletPath();
		}
	}
	

}
