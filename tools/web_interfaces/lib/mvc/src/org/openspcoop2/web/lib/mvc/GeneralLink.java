/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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




package org.openspcoop2.web.lib.mvc;

/**
 * GeneralLink
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GeneralLink {

    String label;
    String url;
    String target;
    String onclick;

    public GeneralLink() {
    	this.label = "";
    	this.url = "";
    	this.target = "";
    	this.onclick = "";
    }

    public void setLabel(String s) {
    	this.label = s;
    }

    public String getLabel() {
	return this.label;
    }

    public void setUrl(String s) {
    	this.url = s;
    }
    
    public void setUrl(String servletName,Parameter ... parameter) {
		this.url = servletName;
		if(parameter!=null && parameter.length>0){
			this.url = this.url + "?";
			for (int i = 0; i < parameter.length; i++) {
				if(i>0){
					this.url = this.url + "&";
				}
				this.url = this.url + parameter[i].toString();
			}
		}
	}

    public String getUrl() {
	return this.url;
    }

    public void setTarget(String s) {
    	this.target = s;
    }
    public void setTargetNew(){
    	this.target = "new";
    }

    public String getTarget() {
	return this.target;
    }

    public void setOnClick(String s) {
    	this.onclick = s;
    }

    public String getOnClick() {
	return this.onclick;
    }
}
