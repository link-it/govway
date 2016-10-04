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


package org.openspcoop2.web.ctrlstat.servlet;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorCostanti;
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
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;


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
		Boolean singlePdD = this.core.isSinglePdD();
		String css = this.core.getConsoleCSS();

		PermessiUtente pu = null;
		User u = null;
		try {
			u = this.utentiCore.getUser(userLogin,false);
			pu = u.getPermessi();
		} catch (DriverUsersDBException dude) {
			// Se arrivo qui, è successo qualcosa di strano
			//this.log.error("initGeneralData: " + dude.getMessage(), dude);
		}

		boolean displayUtente = false;
		boolean displayLogin = true;
		boolean displayLogout = true;
		boolean displayMonitor = true;
		boolean displayInterfaceSwitchButton = this.core.isShowModalitaInterfacciaSwitchRapido();
		if ((baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) != -1 && userLogin == null) || (baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGOUT) != -1)) {
			displayLogin = false;
			displayLogout = false;
		}
		if (userLogin != null){
			displayLogin = false;
			displayUtente = true;
		}
		if (singlePdD || !singlePdD || pu == null || !pu.isCodeMessaggi())
			displayMonitor = false; // aggiunta condizione che viene false forzatamente allo scopo di far comparire la coda messaggi tra gli strumenti

		GeneralData gd = new GeneralData(CostantiControlStation.LABEL_LINKIT_WEB);
		gd.setTitleImg(this.core.getConsoleIMGNomeApplicazione());
		gd.setProduct(this.core.getConsoleNomeSintesi());
		gd.setLanguage(this.core.getConsoleLanguage());
		gd.setTitle(this.core.getConsoleNomeEsteso());
		gd.setUsaTitleImg(this.core.isConsoleUsaIMGNomeApplicazione()); 
		gd.setUrl(baseUrl);
		gd.setCss(css);
		if (displayLogin || displayLogout || displayMonitor) {
			Vector<GeneralLink> link = new Vector<GeneralLink>();
			if (displayLogin) {
				GeneralLink gl1 = new GeneralLink();
				gl1.setLabel(LoginCostanti.LABEL_LOGIN);
				gl1.setUrl(LoginCostanti.SERVLET_NAME_LOGIN);
				link.addElement(gl1);
			}else{
				// Visualizzo il tasto per lo switch dell'interfaccia grafica
				if(displayInterfaceSwitchButton){
					GeneralLink glUtente = new GeneralLink();
					InterfaceType tipoInterfaccia = u.getInterfaceType();
					if(tipoInterfaccia.equals(InterfaceType.STANDARD)){
						glUtente.setLabel(  this.core.getLabelSwitchRapidoModalitaInterfacciaStandard());
						glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.AVANZATA.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED)
								);
					} else {
						glUtente.setLabel( this.core.getLabelSwitchRapidoModalitaInterfacciaAvanzata());
						glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.STANDARD.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED));
					}
					link.addElement(glUtente);
				}
				if (displayUtente){
					GeneralLink glUtente = new GeneralLink();
					glUtente.setLabel(LoginCostanti.LABEL_LOGIN_ATTUALMENTE_CONNESSO+userLogin);
					glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE);
					link.addElement(glUtente);
				}
			}
			if (displayLogout) {
				GeneralLink gl2 = new GeneralLink();
				gl2.setLabel(LoginCostanti.LABEL_LOGOUT);
				gl2.setUrl(LoginCostanti.SERVLET_NAME_LOGOUT);
				link.addElement(gl2);
			}
			if (displayMonitor) {
				GeneralLink gl3 = new GeneralLink();
				gl3.setLabel(MonitorCostanti.LABEL_MONITOR);
				gl3.setUrl(MonitorCostanti.SERVLET_NAME_MONITOR);
				link.addElement(gl3);
			}
			GeneralLink glO = new GeneralLink();
			glO.setLabel(CostantiControlStation.LABEL_OPENSPCOOP2);
			glO.setUrl(CostantiControlStation.LABEL_OPENSPCOOP2_WEB);
			glO.setTargetNew();
			link.addElement(glO);
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
		tl1.setLabel(LoginCostanti.LABEL_LOGIN);
		titlelist.addElement(tl1);
		pd.setTitleList(titlelist);
		Vector<DataElement> dati = new Vector<DataElement>();
		DataElement login = new DataElement();
		login.setLabel(LoginCostanti.LABEL_LOGIN);
		login.setType(DataElementType.TEXT_EDIT);
		login.setName(UtentiCostanti.PARAMETRO_UTENTE_LOGIN);
		DataElement pwd = new DataElement();
		pwd.setLabel(UtentiCostanti.LABEL_PASSWORD);
		pwd.setType(DataElementType.CRYPT);
		pwd.setName(UtentiCostanti.PARAMETRO_UTENTE_PASSWORD);
		dati.addElement(login);
		dati.addElement(pwd);
		pd.setDati(dati);
		return pd;
	}



	public int getSize() {
		return 50;
	}




}
