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

package org.openspcoop2.protocol.engine.mapping;

/**
 * MappingInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MappingInfo {

	private ModalitaIdentificazione modalitaIdentificazione;
	private String value;
	private String name;
	private String pattern;
	private String anonymous;
	private Boolean forceWsdlBased;

	public ModalitaIdentificazione getModalitaIdentificazione() {
		return this.modalitaIdentificazione;
	}
	public void setModalitaIdentificazione(
			ModalitaIdentificazione modalitaIdentificazione) {
		this.modalitaIdentificazione = modalitaIdentificazione;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return this.pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getAnonymous() {
		return this.anonymous;
	}
	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}
	public Boolean getForceWsdlBased() {
		return this.forceWsdlBased;
	}
	public void setForceWsdlBased(Boolean forceWsdlBased) {
		this.forceWsdlBased = forceWsdlBased;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append(this.modalitaIdentificazione.toString());
		
		if(this.value!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("value=");
			bf.append(this.value);
		}
		
		if(this.name!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("name=");
			bf.append(this.name);
		}
		
		if(this.pattern!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("pattern=");
			bf.append(this.pattern);
		}
		
		if(this.anonymous!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("anonymous=");
			bf.append(this.anonymous);
		}
		
		if(this.forceWsdlBased!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("forceWsdlBased=");
			bf.append(this.forceWsdlBased);
		}
		
		return bf.toString();
	}
}
