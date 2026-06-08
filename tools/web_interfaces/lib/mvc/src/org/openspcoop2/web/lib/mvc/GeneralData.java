/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

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
	private String product;
	private String url;
	private String css;
	private String language;
	private String title;
	private String linkFoot;
	private List<GeneralLink> headerLinks;
	private List<GeneralLink> modalitaLinks;
	private List<GeneralLink> soggettiLinks;
	private String contextName;
	private String logoHeaderImage;
	private String logoHeaderTitolo;
	private String logoHeaderLink;
	private boolean visualizzaLinkHome;
	// Banner di avviso opzionale popolato da una implementazione IVersionInfo
	// titolo scomposto per poter colorare il solo suffisso (avviso) nell'header
	private String titleBase;
	private String titleSuffix;
	private String titleSeverityClass;
	private String noticeSeverity; // suffisso classe css del banner: errors|warn|info
	private String noticeMessage;
	private String noticeIcon;
	private String noticeActionLabel;
	private String noticeActionUrl;
	// Popup informativo opzionale (mostrato una volta dopo il login) e stato di blocco del menu
	private org.openspcoop2.utils.VersionInfoPopup popup;
	private boolean menuDisabled;

	public GeneralData(String linkFoot) {
		this.product = "";
		this.url = "";
		this.css = "";
		this.language = "";
		this.title = "";
		this.linkFoot = linkFoot;
		this.headerLinks = new ArrayList<>();
		this.modalitaLinks = new ArrayList<>();
		this.soggettiLinks = new ArrayList<>();
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

	public void setHeaderLinks(List<GeneralLink> v) {
		this.headerLinks = v;
	}
	public List<GeneralLink> getHeaderLinks() {
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
	public List<GeneralLink> getModalitaLinks() {
		return this.modalitaLinks;
	}
	public void setModalitaLinks(List<GeneralLink> modalitaLinks) {
		this.modalitaLinks = modalitaLinks;
	}
	public List<GeneralLink> getSoggettiLinks() {
		return this.soggettiLinks;
	}
	public void setSoggettiLinks(List<GeneralLink> soggettiLinks) {
		this.soggettiLinks = soggettiLinks;
	}
	public boolean isVisualizzaLinkHome() {
		return this.visualizzaLinkHome;
	}
	public void setVisualizzaLinkHome(boolean visualizzaLinkHome) {
		this.visualizzaLinkHome = visualizzaLinkHome;
	}
	// I getter dell'avviso restituiscono il valore gia' HTML-escaped: l'escaping resta centralizzato qui (Java),
	// cosi' il template puo' emetterli direttamente senza occuparsi della codifica.
	public String getTitleBase() {
		return (this.titleBase!=null) ? escapeHtml(this.titleBase) : this.title;
	}
	public void setTitleBase(String titleBase) {
		this.titleBase = titleBase;
	}
	public String getTitleSuffix() {
		return escapeHtml(this.titleSuffix);
	}
	public void setTitleSuffix(String titleSuffix) {
		this.titleSuffix = titleSuffix;
	}
	public String getTitleSeverityClass() {
		return (this.titleSeverityClass!=null) ? this.titleSeverityClass : "";
	}
	public void setTitleSeverityClass(String titleSeverityClass) {
		this.titleSeverityClass = titleSeverityClass;
	}
	public String getNoticeSeverity() {
		// valore controllato internamente (errors|warn|info), usato come suffisso di classe css: nessun escaping
		return this.noticeSeverity;
	}
	public void setNoticeSeverity(String noticeSeverity) {
		this.noticeSeverity = noticeSeverity;
	}
	public String getNoticeMessage() {
		return escapeHtml(this.noticeMessage);
	}
	public void setNoticeMessage(String noticeMessage) {
		this.noticeMessage = noticeMessage;
	}
	public String getNoticeIcon() {
		return escapeHtml(this.noticeIcon);
	}
	public void setNoticeIcon(String noticeIcon) {
		this.noticeIcon = noticeIcon;
	}
	public String getNoticeActionLabel() {
		return escapeHtml(this.noticeActionLabel);
	}
	public void setNoticeActionLabel(String noticeActionLabel) {
		this.noticeActionLabel = noticeActionLabel;
	}
	public String getNoticeActionUrl() {
		return escapeHtml(this.noticeActionUrl);
	}
	public void setNoticeActionUrl(String noticeActionUrl) {
		this.noticeActionUrl = noticeActionUrl;
	}
	private static String escapeHtml(String s) {
		return (s!=null) ? StringEscapeUtils.escapeHtml(s) : null;
	}
	// Popup: oggetto con frammenti HTML fidati (resi raw dal template); non si applica escaping
	public org.openspcoop2.utils.VersionInfoPopup getPopup() {
		return this.popup;
	}
	public void setPopup(org.openspcoop2.utils.VersionInfoPopup popup) {
		this.popup = popup;
	}
	public boolean isMenuDisabled() {
		return this.menuDisabled;
	}
	public void setMenuDisabled(boolean menuDisabled) {
		this.menuDisabled = menuDisabled;
	}
}
