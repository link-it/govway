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

package org.openspcoop2.utils.resources;

import org.openspcoop2.utils.UtilsException;

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
	protected java.util.Properties parametersTrasporto;
	
	protected String codiceTrasporto = null; 
	protected long contentLength = -1;
	protected String errore = null;
	protected Exception exception = null;
	
	
	public TransportResponseContext() throws UtilsException{
		
	}
	public TransportResponseContext(java.util.Properties parametersTrasporto,String codiceTrasporto,long contentLength,String errore,Exception exception) throws UtilsException{
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
	public java.util.Properties getParametersTrasporto() {
		return this.parametersTrasporto;
	}
	public String getParameterTrasporto(String name){
		if(this.parametersTrasporto==null){
			return null;
		}
		String value = this.parametersTrasporto.getProperty(name);
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toLowerCase());
		}
		if(value==null){
			value = this.parametersTrasporto.getProperty(name.toUpperCase());
		}
		return value;
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
	public void setParametersTrasporto(java.util.Properties parametersTrasporto) {
		this.parametersTrasporto = parametersTrasporto;
	}
}
