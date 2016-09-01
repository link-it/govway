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


package org.openspcoop2.web.loader.servlet;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.core.LoaderCore;


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

	public GeneralHelper(HttpSession session) {
		this.session = session;
		try {
			this.loaderCore = new LoaderCore();
		} catch (Exception e) {
			this.log = LoggerWrapperFactory.getLogger("openspcoop2.loader");
			this.log.error("Exception: " + e.getMessage(), e);
		}
		this.log = LoggerWrapperFactory.getLogger("openspcoop2.loader");	
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

		boolean displayLogin = true;
		boolean displayLogout = true;
		boolean displayMonitor = true;
		if ((baseUrl.indexOf("/"+Costanti.SERVLET_NAME_LOGIN) != -1 && userLogin == null) || (baseUrl.indexOf("/"+Costanti.SERVLET_NAME_LOGOUT) != -1)) {
			displayLogin = false;
			displayLogout = false;
		}
		if (userLogin != null)
			displayLogin = false;

		GeneralData gd = new GeneralData(Costanti.LABEL_LINKIT_WEB);
		gd.setTitleImg(this.loaderCore.getLoaderIMGNomeApplicazione());
		gd.setProduct(this.loaderCore.getLoaderNomeSintesi());
		gd.setLanguage(this.loaderCore.getLoaderLanguage());
		gd.setTitle(this.loaderCore.getLoaderNomeEsteso());
		gd.setUsaTitleImg(this.loaderCore.isLoaderUsaIMGNomeApplicazione()); 
		gd.setUrl(baseUrl);
		gd.setCss(css);
		if (displayLogin || displayLogout || displayMonitor) {
			Vector<GeneralLink> link = new Vector<GeneralLink>();
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
		PageData pd = new PageData();
		Vector<GeneralLink> titlelist = new Vector<GeneralLink>();
		GeneralLink tl1 = new GeneralLink();
		tl1.setLabel(Costanti.LABEL_LOGIN);
		titlelist.addElement(tl1);
		pd.setTitleList(titlelist);
		Vector<DataElement> dati = new Vector<DataElement>();
		DataElement login = new DataElement();
		login.setLabel(Costanti.LABEL_LOGIN);
		login.setType(DataElementType.TEXT_EDIT);
		login.setName(Costanti.PARAMETRO_UTENTE_LOGIN);
		DataElement pwd = new DataElement();
		pwd.setLabel(Costanti.LABEL_PASSWORD);
		pwd.setType(DataElementType.CRYPT);
		pwd.setName(Costanti.PARAMETRO_UTENTE_PASSWORD);
		dati.addElement(login);
		dati.addElement(pwd);
		pd.setDati(dati);
		return pd;
	}

	public int getSize() {
		return 50;
	}
}
