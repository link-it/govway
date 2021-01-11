/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * RequestContext
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
	protected Map<String, String> parametersTrasporto;
	
	protected String codiceTrasporto = null; 
	protected long contentLength = -1;
	protected String errore = null;
	protected Exception exception = null;
	
	
	public TransportResponseContext() throws UtilsException{
		
	}
	public TransportResponseContext(Map<String, String> parametersTrasporto,String codiceTrasporto,long contentLength,String errore,Exception exception) throws UtilsException{
		this.parametersTrasporto = parametersTrasporto;
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
	public Map<String, String> getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public String getParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		return TransportUtils.get(this.parametersTrasporto, name);
	}
	public Object removeParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		return TransportUtils.remove(this.parametersTrasporto, name);
	}
	public String getContentType(){
		if(this.parametersTrasporto!=null){
			return this.getParameterTrasporto(HttpConstants.CONTENT_TYPE);
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
	public void setParametersTrasporto(Map<String, String> parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}

}
