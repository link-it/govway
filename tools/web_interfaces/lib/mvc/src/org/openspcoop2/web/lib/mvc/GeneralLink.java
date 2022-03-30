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
    String icon;
    int labelWidth;
    String tooltip;

    public GeneralLink() {
    	this.label = "";
    	this.url = "";
    	this.target = "";
    	this.onclick = "";
    	this.icon="";
    	this.tooltip = "";
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

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLabelWidth() {
		return this.labelWidth;
	}
	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}
	public String getTooltip() {
		return this.tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}
