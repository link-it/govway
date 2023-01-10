/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport;

import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * TransportResponseContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class TransportResponseContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/* ---- Coppie nome/valori di invocazione inserite nell'header del trasporto --- */
	protected Map<String, List<String>> headers;
	
	protected String codiceTrasporto = null; 
	protected long contentLength = -1;
	protected String errore = null;
	protected Exception exception = null;
	
	protected Logger log = null;
	
	public TransportResponseContext() throws UtilsException{
		this(null);
	}
	public TransportResponseContext(Logger log) throws UtilsException{
		if(log==null) {
			this.log = LoggerWrapperFactory.getLogger(TransportResponseContext.class);
		}
		else {
			this.log = log;
		}
	}
	@Deprecated
	public TransportResponseContext(Map<String, String> parametersTrasporto,String codiceTrasporto,long contentLength,String errore,Exception exception) throws UtilsException{
		this(null, codiceTrasporto, TransportUtils.convertToMapListValues(parametersTrasporto), contentLength, errore, exception);
	}
	@Deprecated
	public TransportResponseContext(Logger log, Map<String, String> parametersTrasporto,String codiceTrasporto,long contentLength,String errore,Exception exception) throws UtilsException{
		this(log, codiceTrasporto, TransportUtils.convertToMapListValues(parametersTrasporto), contentLength, errore, exception);
	}
	@Deprecated
	public TransportResponseContext(String codiceTrasporto,Map<String, List<String>> headers,long contentLength,String errore,Exception exception) throws UtilsException{
		this(null, codiceTrasporto, headers, contentLength, errore, exception);
	}
	public TransportResponseContext(Logger log, String codiceTrasporto,Map<String, List<String>> headers,long contentLength,String errore,Exception exception) throws UtilsException{
		if(log==null) {
			this.log = LoggerWrapperFactory.getLogger(TransportResponseContext.class);
		}
		else {
			this.log = log;
		}
		this.headers = headers;
		this.codiceTrasporto = codiceTrasporto;
		this.contentLength = contentLength;
		this.errore = errore;
		this.exception = exception;
	}
	
	
	public String getCodiceTrasporto() {
		return this.codiceTrasporto;
	}
	public long getContentLength() {
		return this.contentLength;
	}
	public String getErrore() {
		return this.errore;
	}
	
	@Deprecated
	public Map<String, String> getParametersTrasporto() {
		return TransportUtils.convertToMapSingleValue(this.headers);
	}
	public Map<String, List<String>> getHeaders(){
		return this.headers;
	}
	
	@Deprecated
	public String getParameterTrasporto(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.headers, name);
	}
	public String getHeader_compactMultipleValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getObjectAsString(this.headers, name);
	}
	public String getHeaderFirstValue(String name){
		List<String> l = getHeaderValues(name);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public List<String> getHeaderValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.getRawObject(this.headers, name);
	}
	
	@Deprecated
	public Object removeParameterTrasporto(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.headers, name);
	}
	public String removeHeader_compactMultipleValues(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeObjectAsString(this.headers, name);
	}
	public List<String> removeHeader(String name){
		if(this.headers==null){
			return null;
		}
		return TransportUtils.removeRawObject(this.headers, name);
	}
	
	public String getContentType(){
		if(this.headers!=null){
			return this.getHeaderFirstValue(HttpConstants.CONTENT_TYPE);
		}
		return null;
	}

	public void setCodiceTrasporto(String codiceTrasporto) {
		this.codiceTrasporto = codiceTrasporto;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public void setErrore(String errore) {
		this.errore = errore;
	}
	
	@Deprecated
	public void setParametersTrasporto(Map<String, String> parametersTrasporto) {
		this.headers = TransportUtils.convertToMapListValues(parametersTrasporto);
	}
	public void setHeaders(Map<String, List<String>> parametersFormBased) {
		this.headers = parametersFormBased;
	}

}
