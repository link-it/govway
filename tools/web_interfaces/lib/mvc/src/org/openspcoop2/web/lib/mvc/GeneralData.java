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




package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;
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
public class GeneralData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String product;
	String url;
	String css;
	String language;
	String title;
	String linkFoot;
	Vector<GeneralLink> headerLinks;
	Vector<GeneralLink> modalitaLinks;
	Vector<GeneralLink> soggettiLinks;
	private String contextName;
	private String logoHeaderImage;
	private String logoHeaderTitolo;
	private String logoHeaderLink;
	private boolean visualizzaLinkHome;

	public GeneralData(String linkFoot) {
		this.product = "";
		this.url = "";
		this.css = "";
		this.language = "";
		this.title = "";
		this.linkFoot = linkFoot;
		this.headerLinks = new Vector<GeneralLink>();
		this.modalitaLinks = new Vector<GeneralLink>();
		this.soggettiLinks = new Vector<GeneralLink>();
		this.logoHeaderImage = "";
		this.logoHeaderLink= "";
		this.logoHeaderTitolo= "";
		this.visualizzaLinkHome = false;
	}
	public GeneralData() {
		this("https://link.it");
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

	public void setLinkFoot(String s) {
		this.linkFoot = s;
	}
	public String getLinkFoot() {
		return this.linkFoot;
	}

	public void setHeaderLinks(Vector<GeneralLink> v) {
		this.headerLinks = v;
	}
	public Vector<GeneralLink> getHeaderLinks() {
		return this.headerLinks;
	}

	@Override
	public String toString() {
		return "Product: "+this.product+"\nUrl: "+this.url+"\nStyleSheet: "+this.css+"\nLanguage: "+this.language+"\nTitle: "+this.title+"\nFooter Link: "+this.linkFoot;
	}
	public void setContextName(String contextName) {
		this.contextName=contextName;
	}
	public String getContextName(){
		return this.contextName;
	}
	public String getLogoHeaderImage() {
		return this.logoHeaderImage;
	}
	public void setLogoHeaderImage(String logoHeaderImage) {
		this.logoHeaderImage = logoHeaderImage;
	}
	public String getLogoHeaderTitolo() {
		return this.logoHeaderTitolo;
	}
	public void setLogoHeaderTitolo(String logoHeaderTitolo) {
		this.logoHeaderTitolo = logoHeaderTitolo;
	}
	public String getLogoHeaderLink() {
		return this.logoHeaderLink;
	}
	public void setLogoHeaderLink(String logoHeaderLink) {
		this.logoHeaderLink = logoHeaderLink;
	}
	public Vector<GeneralLink> getModalitaLinks() {
		return this.modalitaLinks;
	}
	public void setModalitaLinks(Vector<GeneralLink> modalitaLinks) {
		this.modalitaLinks = modalitaLinks;
	}
	public Vector<GeneralLink> getSoggettiLinks() {
		return this.soggettiLinks;
	}
	public void setSoggettiLinks(Vector<GeneralLink> soggettiLinks) {
		this.soggettiLinks = soggettiLinks;
	}
	public boolean isVisualizzaLinkHome() {
		return this.visualizzaLinkHome;
	}
	public void setVisualizzaLinkHome(boolean visualizzaLinkHome) {
		this.visualizzaLinkHome = visualizzaLinkHome;
	}
}
