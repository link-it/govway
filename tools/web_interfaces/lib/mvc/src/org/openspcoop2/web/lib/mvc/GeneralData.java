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

import java.util.Vector;

/**
 * GeneralData
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GeneralData {

	String product;
	String url;
	String css;
	String language;
	String title;
	String titleImg;
	boolean usaTitleImg;
	String linkFoot;
	Vector<?> headerLinks;
	private String contextName;

	public GeneralData(String linkFoot) {
		this.product = "";
		this.url = "";
		this.css = "";
		this.language = "";
		this.title = "";
		this.titleImg = "";
		this.usaTitleImg = true;
		this.linkFoot = linkFoot;
		this.headerLinks = new Vector<Object>();
	}
	public GeneralData() {
		this("http://www.link.it");
	}

	public void setProduct(String p) {
		this.product = p;
	}
	public String getProduct() {
		return this.product;
	}

	public void setCss(String d) {
		this.css = d;
	}
	public String getCss() {
		return this.css;
	}

	public void setUrl(String u) {
		this.url = u;
	}
	public String getUrl() {
		return this.url;
	}

	public void setLanguage(String l) {
		this.language = l;
	}
	public String getLanguage() {
		return this.language;
	}

	public void setTitle(String t) {
		this.title = t;
	}
	public String getTitle() {
		return this.title;
	}

	public void setTitleImg(String ti) {
		this.titleImg = ti;
	}
	public String getTitleImg() {
		return this.titleImg;
	}

	public void setLinkFoot(String s) {
		this.linkFoot = s;
	}
	public String getLinkFoot() {
		return this.linkFoot;
	}

	public void setHeaderLinks(Vector<?> v) {
		this.headerLinks = v;
	}
	public Vector<?> getHeaderLinks() {
		return this.headerLinks;
	}

	@Override
	public String toString() {
		return "Product: "+this.product+"\nUrl: "+this.url+"\nStyleSheet: "+this.css+"\nLanguage: "+this.language+"\nTitle: "+this.title+"\nTitleImg: "+this.titleImg+"\nFooter Link: "+this.linkFoot;
	}
	public void setContextName(String contextName) {
		this.contextName=contextName;
	}
	public String getContextName(){
		return this.contextName;
	}
	public boolean isUsaTitleImg() {
		return this.usaTitleImg;
	}
	public void setUsaTitleImg(boolean usaTitleImg) {
		this.usaTitleImg = usaTitleImg;
	}
}
