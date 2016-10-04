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

package org.openspcoop2.pdd.core.autorizzazione;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * AbstractDatiInvocazione
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDatiInvocazione {

	private InfoConnettoreIngresso infoConnettoreIngresso;
	
	private IDServizio idServizio;
	
	private IState state;
	
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
	
	public String getKeyCache(){
		return this._toString(true);
	}
	
	@Override
	public String toString(){
		return this._toString(false);
	}
	public String _toString(boolean keyCache){
		StringBuffer bf = new StringBuffer();
		
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
						if(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocol()!=null){
							bf.append(" UrlProtocolContext_Protocol:").append(this.infoConnettoreIngresso.getUrlProtocolContext().getProtocol());
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
		
		return bf.toString();
	}
}
