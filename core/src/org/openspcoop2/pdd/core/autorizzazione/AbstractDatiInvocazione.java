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

package org.openspcoop2.pdd.core.autorizzazione;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * AbstractDatiInvocazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDatiInvocazione {

	private PdDContext pddContext;
	
	private InfoConnettoreIngresso infoConnettoreIngresso;
	
	private RequestInfo requestInfo;
	
	private IDServizio idServizio;
	
	private IState state;
	
	private String token;
	
	public String getToken() {
		return this.token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	
	public InfoConnettoreIngresso getInfoConnettoreIngresso() {
		return this.infoConnettoreIngresso;
	}
	public void setInfoConnettoreIngresso(
			InfoConnettoreIngresso infoConnettoreIngresso) {
		this.infoConnettoreIngresso = infoConnettoreIngresso;
	}

	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}

	public IState getState() {
		return this.state;
	}
	public void setState(IState state) {
		this.state = state;
	}
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	public String getKeyCache(){
		return this._toString(true);
	}
	
	@Override
	public String toString(){
		return this._toString(false);
	}
	public String _toString(boolean keyCache){
		StringBuilder bf = new StringBuilder();
		
		if(this.idServizio!=null){
			bf.append("IDServizio(");
			bf.append(this.idServizio.toString());
			bf.append(")");
		}
		
		if(this.infoConnettoreIngresso!=null){
			if(this.infoConnettoreIngresso.getSoapAction()!=null){
				bf.append(" SOAPAction:").append(this.infoConnettoreIngresso.getSoapAction());
			}
			if(keyCache==false){
				if(this.infoConnettoreIngresso.getFromLocation()!=null){
					bf.append(" FromLocation:").append(this.infoConnettoreIngresso.getFromLocation());
				}
			}
			if(this.infoConnettoreIngresso.getCredenziali()!=null){
				if(this.infoConnettoreIngresso.getCredenziali()!=null){
					bf.append(" Credenziali(").append(this.infoConnettoreIngresso.getCredenziali().toString());
					bf.append(")");
				}
				if(keyCache==false){
					if(this.infoConnettoreIngresso.getUrlProtocolContext()!=null){
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getFunction()!=null){
							bf.append(" UrlProtocolContext_Function:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getFunction());
						}
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getFunctionParameters()!=null){
							bf.append(" UrlProtocolContext_FunctionParameters:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getFunctionParameters());
						}
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocolName()!=null){
							bf.append(" UrlProtocolContext_ProtocolName:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocolName());
						}
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocolWebContext()!=null){
							bf.append(" UrlProtocolContext_ProtocolWebContext:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocolWebContext());
						}
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getRequestURI()!=null){
							bf.append(" UrlProtocolContext_RequestURI:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getRequestURI());
						}
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getWebContext()!=null){
							bf.append(" UrlProtocolContext_WebContext:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getWebContext());
						}
					}
				}
			}
		}
				
		if(keyCache==false){
			if(this.state!=null){
				bf.append(" State:defined");
			}
		}
		
		if(this.token!=null) {
			bf.append(" Token:").append(this.token);
		}
		
		return bf.toString();
	}
}
