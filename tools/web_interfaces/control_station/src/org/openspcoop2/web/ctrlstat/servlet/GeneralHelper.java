/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.about.AboutCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;


// Questa classe, volendo, potrebbe essere usata anche dalla Porta di Dominio e
// dal registro servizi, dato che serve per settare pezzi di codice comuni a
// piu' applicazioni
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

	protected HttpSession session;
	protected ControlStationCore core;
	protected PddCore pddCore;
	protected UtentiCore utentiCore;
	protected SoggettiCore soggettiCore;
	protected Logger log;

	public GeneralHelper(HttpSession session) {
		this.session = session;
		try {
			this.core = new ControlStationCore();
			this.pddCore = new PddCore(this.core);
			this.utentiCore = new UtentiCore(this.core);
			this.soggettiCore = new SoggettiCore(this.core);
		} catch (Exception e) {
			this.log = ControlStationLogger.getPddConsoleCoreLogger();
			this.log.error("Exception: " + e.getMessage(), e);
		}
		this.log = ControlStationLogger.getPddConsoleCoreLogger();
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
		String css = this.core.getConsoleCSS();

		User u = null;
		try {
			u = this.utentiCore.getUser(userLogin,false);
		} catch (DriverUsersDBException dude) {
			// Se arrivo qui, è successo qualcosa di strano
			//this.log.error("initGeneralData: " + dude.getMessage(), dude);
		}

		boolean displayUtente = false;
		boolean displayLogin = true;
		boolean displayLogout = true;
		if ((baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) != -1 && userLogin == null) || (baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGOUT) != -1)) {
			displayLogin = false;
			displayLogout = false;
		}
		if (userLogin != null){
			displayLogin = false;
			displayUtente = true;
		}

		GeneralData gd = new GeneralData(CostantiControlStation.LABEL_LINKIT_WEB);
		gd.setProduct(this.core.getConsoleNomeSintesi());
		gd.setLanguage(this.core.getConsoleLanguage());
		gd.setTitle(this.core.getConsoleNomeEsteso(false));
		gd.setLogoHeaderImage(this.core.getLogoHeaderImage());
		gd.setLogoHeaderLink(this.core.getLogoHeaderLink());
		gd.setLogoHeaderTitolo(this.core.getLogoHeaderTitolo()); 
		gd.setUrl(baseUrl);
		gd.setCss(css);
		if (displayLogin || displayLogout) {
			Vector<GeneralLink> link = new Vector<GeneralLink>();
			if (displayLogin) {
				// in questo ramo non si dovrebbe mai passare, l'authorizationfilter blocca le chiamate quando l'utente nn e' loggato
				GeneralLink gl1 = new GeneralLink();
				gl1.setLabel(LoginCostanti.LABEL_LOGIN);
				gl1.setUrl(LoginCostanti.SERVLET_NAME_LOGIN);
				link.addElement(gl1);
			}else{
				// Ordine dei link da visualizzare nel menu'

				// 1. Utente collegato
				if (displayUtente){
					GeneralLink glUtente = new GeneralLink();
					glUtente.setLabel(userLogin);
					glUtente.setUrl("");
					link.addElement(glUtente);
				}

				// 2. modalita' standard/avanzata
				GeneralLink glUtente = new GeneralLink();
				InterfaceType tipoInterfaccia = u.getInterfaceType();
				if(tipoInterfaccia.equals(InterfaceType.STANDARD)){
					glUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_MODALITA_AVANZATA);
					glUtente.setIcon("");
					glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.AVANZATA.toString()),
							new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED)
							);

				} else {
					glUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_MODALITA_AVANZATA);
					glUtente.setIcon(LoginCostanti.ICONA_MENU_UTENTE_MODALITA_AVANZATA);
					glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.STANDARD.toString()),
							new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED));
				}
				link.addElement(glUtente);

				// 3. informazioni/about
				GeneralLink glO = new GeneralLink();
				glO.setLabel(LoginCostanti.LABEL_MENU_UTENTE_INFORMAZIONI);
				glO.setUrl(AboutCostanti.SERVLET_NAME_ABOUT);
				link.addElement(glO);

				// 4. profilo utente
				if (displayUtente){
					GeneralLink glProfiloUtente = new GeneralLink();
					glProfiloUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_PROFILO_UTENTE);
					glProfiloUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE);
					link.addElement(glProfiloUtente);
				}
			}

			// 5. logout
			if (displayLogout) {
				GeneralLink gl2 = new GeneralLink();
				gl2.setLabel(LoginCostanti.LABEL_MENU_UTENTE_LOGOUT);
				gl2.setUrl(LoginCostanti.SERVLET_NAME_LOGOUT);
				link.addElement(gl2);
			}

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
		Vector<DataElement> dati = new Vector<DataElement>();
		// titolo sezione login 
		DataElement titoloSezione = new DataElement();
		titoloSezione.setLabel(LoginCostanti.LABEL_LOGIN);
		titoloSezione.setType(DataElementType.TITLE);
		titoloSezione.setName("");

		DataElement login = new DataElement();
		login.setLabel(LoginCostanti.LABEL_LOGIN);
		login.setType(DataElementType.TEXT_EDIT);
		login.setName(UtentiCostanti.PARAMETRO_UTENTE_LOGIN);
		login.setStyleClass(Costanti.INPUT_CSS_CLASS);
		DataElement pwd = new DataElement();
		pwd.setLabel(UtentiCostanti.LABEL_PASSWORD);
		pwd.setType(DataElementType.CRYPT);
		pwd.setName(UtentiCostanti.PARAMETRO_UTENTE_PASSWORD);
		pwd.setStyleClass(Costanti.INPUT_CSS_CLASS);

		dati.addElement(titoloSezione);
		dati.addElement(login);
		dati.addElement(pwd);
		pd.setDati(dati);
		return pd;
	}



	public int getSize() {
		return 50;
	}




}
