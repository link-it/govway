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


package org.openspcoop2.web.loader.servlet;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.config.LoaderProperties;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.core.LoaderCore;
import org.openspcoop2.web.loader.servlet.about.AboutCostanti;
import org.slf4j.Logger;


//Questa classe, volendo, potrebbe essere usata anche dalla Porta di Dominio e
//dal registro servizi, dato che serve per settare pezzi di codice comuni a
//piu' applicazioni
/**
 * generalHelper
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GeneralHelper {

	HttpSession session;
	//ControlStationCore core;
	public Logger log;
	private LoaderCore loaderCore;

	/** Lunghezza label */
	protected int size = 50;

	public GeneralHelper(HttpSession session) {
		this.session = session;
		try {
			this.loaderCore = new LoaderCore();
			this.size = LoaderProperties.getInstance().getConsoleLunghezzaLabel();
		} catch (Exception e) {
			this.log = LoggerWrapperFactory.getLogger("govway.loader");
			this.log.error("Exception: " + e.getMessage(), e);
		}
		this.log = LoggerWrapperFactory.getLogger("govway.loader");	
	}



	public GeneralData initGeneralData(HttpServletRequest request){
		String baseUrl = request.getRequestURI();
		return this.initGeneralData_engine(baseUrl);
	}
	public GeneralData initGeneralData(HttpServletRequest request,String servletName){
		String baseUrl = request.getContextPath();
		if(servletName.startsWith("/")){
			baseUrl = baseUrl + servletName;
		}else{
			baseUrl = baseUrl + "/" + servletName;
		}
		return this.initGeneralData_engine(baseUrl);
	}
	@Deprecated
	public GeneralData initGeneralData(String baseUrl) {
		return this.initGeneralData_engine(baseUrl);
	}
	private GeneralData initGeneralData_engine(String baseUrl) {
		String userLogin = ServletUtils.getUserLoginFromSession(this.session);
		String css = this.loaderCore.getLoaderCSS();

		boolean displayUtente = false;
		boolean displayLogin = true;
		boolean displayLogout = true;
		if ((baseUrl.indexOf("/"+Costanti.SERVLET_NAME_LOGIN) != -1 && userLogin == null) || (baseUrl.indexOf("/"+Costanti.SERVLET_NAME_LOGOUT) != -1)) {
			displayLogin = false;
			displayLogout = false;
		}
		if (userLogin != null){
			displayLogin = false;
			displayUtente = true;
		}

		GeneralData gd = new GeneralData(Costanti.LABEL_LINKIT_WEB);
		gd.setProduct(this.loaderCore.getLoaderNomeSintesi());
		gd.setLanguage(this.loaderCore.getLoaderLanguage());
		gd.setTitle(StringEscapeUtils.escapeHtml(this.loaderCore.getLoaderNomeEsteso(this.session)));
		gd.setUrl(baseUrl);
		gd.setCss(css);
		gd.setLogoHeaderImage(this.loaderCore.getLogoHeaderImage());
		gd.setLogoHeaderLink(this.loaderCore.getLogoHeaderLink());
		gd.setLogoHeaderTitolo(this.loaderCore.getLogoHeaderTitolo()); 
		gd.setVisualizzaLinkHome(this.loaderCore.isVisualizzaLinkHomeHeader());
		if (displayLogin || displayLogout) {
			Vector<GeneralLink> link = new Vector<GeneralLink>();
			// 1. Utente collegato
			if (displayUtente){
				GeneralLink glUtente = new GeneralLink();
				glUtente.setLabel(userLogin);
				glUtente.setUrl("");
				link.addElement(glUtente);
			}
			
			// 3. informazioni/about
			GeneralLink glO = new GeneralLink();
			glO.setLabel(Costanti.LABEL_MENU_UTENTE_INFORMAZIONI);
			glO.setUrl(AboutCostanti.SERVLET_NAME_ABOUT);
			link.addElement(glO);

			if (displayLogin) {
				GeneralLink gl1 = new GeneralLink();
				gl1.setLabel(Costanti.LABEL_LOGIN);
				gl1.setUrl(Costanti.SERVLET_NAME_LOGIN);
				link.addElement(gl1);
			}
			if (displayLogout) {
				GeneralLink gl2 = new GeneralLink();
				gl2.setLabel(Costanti.LABEL_LOGOUT);
				gl2.setUrl(Costanti.SERVLET_NAME_LOGOUT);
				link.addElement(gl2);
			}
			GeneralLink gl4 = new GeneralLink();
			gl4.setLabel("");
			link.addElement(gl4);
			gd.setHeaderLinks(link);
		}

		return gd;
	}

	public PageData initPageData() {
		return  initPageData(null);		
	}

	public PageData initPageData(String breadcrumb) {
		PageData pd = new PageData();
		if(breadcrumb != null) {
			Vector<GeneralLink> titlelist = new Vector<GeneralLink>();
			GeneralLink tl1 = new GeneralLink();
			tl1.setLabel(breadcrumb);
			titlelist.addElement(tl1);
			pd.setTitleList(titlelist);
		}
		// titolo sezione login 
		DataElement titoloSezione = new DataElement();
		titoloSezione.setLabel(Costanti.LABEL_LOGIN);
		titoloSezione.setType(DataElementType.TITLE);
		titoloSezione.setName("");
		
		Vector<DataElement> dati = new Vector<DataElement>();
		DataElement login = new DataElement();
		login.setLabel(Costanti.LABEL_LOGIN);
		login.setType(DataElementType.TEXT_EDIT);
		login.setName(Costanti.PARAMETRO_UTENTE_LOGIN);
		login.setStyleClass(org.openspcoop2.web.lib.mvc.Costanti.INPUT_CSS_CLASS);
		DataElement pwd = new DataElement();
		pwd.setLabel(Costanti.LABEL_PASSWORD);
		pwd.setType(DataElementType.CRYPT);
		pwd.setName(Costanti.PARAMETRO_UTENTE_PASSWORD);
		pwd.setStyleClass(org.openspcoop2.web.lib.mvc.Costanti.INPUT_CSS_CLASS);
		
		dati.addElement(titoloSezione);
		dati.addElement(login);
		dati.addElement(pwd);
		pd.setDati(dati);
		return pd;
	}

	public int getSize() {
		return this.size;
	}
}
