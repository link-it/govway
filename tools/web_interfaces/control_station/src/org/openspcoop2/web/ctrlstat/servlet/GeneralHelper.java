/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.awt.Font;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.about.AboutCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
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
	protected SoggettiCore soggettiCore;
	protected Logger log;

	public GeneralHelper(HttpSession session) {
		this.session = session;
		try {
			this.core = new ControlStationCore();
			this.pddCore = new PddCore(this.core);
			this.soggettiCore = new SoggettiCore(this.core);
		} catch (Exception e) {
			this.log = ControlStationLogger.getPddConsoleCoreLogger();
			this.log.error("Exception: " + e.getMessage(), e);
		}
		this.log = ControlStationLogger.getPddConsoleCoreLogger();
	}





	public GeneralData initGeneralData(HttpServletRequest request){
		String baseUrl = request.getRequestURI();
		return this.initGeneralData_engine(request, baseUrl);
	}
	public GeneralData initGeneralData(HttpServletRequest request,String servletName){
		String baseUrl = request.getContextPath();
		if(servletName.startsWith("/")){
			baseUrl = baseUrl + servletName;
		}else{
			baseUrl = baseUrl + "/" + servletName;
		}
		return this.initGeneralData_engine(request, baseUrl);
	}

	private GeneralData initGeneralData_engine(HttpServletRequest request, String baseUrl) {
		String userLogin = ServletUtils.getUserLoginFromSession(this.session);
		String css = this.core.getConsoleCSS();

		// per avere diversi 'profili' utente per i vari tab leggo l'utente dalla sessione del tab
		User u = ServletUtils.getUserFromSession(request, this.session);

		boolean displayUtente = false;
		boolean displayLogin = true;
		boolean displayLogout = true;
		if ((baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN) != -1 && u == null) || (baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGOUT) != -1)
				|| (baseUrl.indexOf("/"+LoginCostanti.SERVLET_NAME_LOGIN_MESSAGE_PAGE) != -1)
				|| (baseUrl.indexOf("/"+UtentiCostanti.SERVLET_NAME_UTENTE_PASSWORD_CHANGE) != -1 && u == null)) {
			displayLogin = false;
			displayLogout = false;
		}
		if (u != null){
			displayLogin = false;
			displayUtente = true;
		}

		GeneralData gd = new GeneralData(CostantiControlStation.LABEL_LINKIT_WEB);
		gd.setProduct(this.core.getConsoleNomeSintesi());
		gd.setLanguage(this.core.getConsoleLanguage());
		gd.setTitle(StringEscapeUtils.escapeHtml(this.core.getConsoleNomeEsteso(request, this.session)));
		gd.setLogoHeaderImage(this.core.getLogoHeaderImage());
		gd.setLogoHeaderLink(this.core.getLogoHeaderLink());
		gd.setLogoHeaderTitolo(this.core.getLogoHeaderTitolo()); 
		gd.setVisualizzaLinkHome(this.core.isVisualizzaLinkHomeHeader());
		gd.setUrl(baseUrl);
		gd.setCss(css);
		if (displayLogin || displayLogout) {
			List<GeneralLink> link = new ArrayList<GeneralLink>();
			if (displayLogin) {
				// in questo ramo non si dovrebbe mai passare, l'authorizationfilter blocca le chiamate quando l'utente nn e' loggato
				GeneralLink gl1 = new GeneralLink();
				gl1.setLabel(LoginCostanti.LABEL_LOGIN);
				gl1.setUrl(LoginCostanti.SERVLET_NAME_LOGIN);
				link.add(gl1);
			}else{
				// Ordine dei link da visualizzare nel menu'

				// 1. Utente collegato
				if (displayUtente){
					GeneralLink glUtente = new GeneralLink();
					glUtente.setLabel(userLogin);
					glUtente.setUrl("");
					link.add(glUtente);
				}

				// 2. modalita' standard/avanzata
				GeneralLink glUtente = new GeneralLink();
				InterfaceType tipoInterfaccia = u.getInterfaceType();
				if(!tipoInterfaccia.equals(InterfaceType.COMPLETA)){
					if(tipoInterfaccia.equals(InterfaceType.STANDARD)){
						glUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_MODALITA_AVANZATA);
						glUtente.setIcon(LoginCostanti.ICONA_MENU_UTENTE_UNCHECKED);
						glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.AVANZATA.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED)
								);
	
					} else {
						glUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_MODALITA_AVANZATA);
						glUtente.setIcon(LoginCostanti.ICONA_MENU_UTENTE_CHECKED);
						glUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI, InterfaceType.STANDARD.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI,Costanti.CHECK_BOX_ENABLED));
					}
					link.add(glUtente);
				}

				// 3. informazioni/about
				GeneralLink glO = new GeneralLink();
				glO.setLabel(LoginCostanti.LABEL_MENU_UTENTE_INFORMAZIONI);
				glO.setUrl(AboutCostanti.SERVLET_NAME_ABOUT);
				link.add(glO);

				// 4. profilo utente
				if (displayUtente){
					GeneralLink glProfiloUtente = new GeneralLink();
					glProfiloUtente.setLabel(LoginCostanti.LABEL_MENU_UTENTE_PROFILO_UTENTE);
					glProfiloUtente.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE);
					link.add(glProfiloUtente);
				}
			}

			// 5. logoutsetModalitaLinks
			if (displayLogout && this.core.isMostraButtonLogout()) {
				GeneralLink gl2 = new GeneralLink();
				gl2.setLabel(LoginCostanti.LABEL_MENU_UTENTE_LOGOUT);

				// se ho definito una url custom viene controllato nella procedura di logout
				gl2.setUrl(LoginCostanti.SERVLET_NAME_LOGOUT);
				link.add(gl2);
			}

			gd.setHeaderLinks(link);

			if(u != null) { // se ho fatto refresh della pagina dopo che e' scaduta la sessione l'utente e' null
				if(!u.hasOnlyPermessiUtenti())  
					gd.setModalitaLinks(this.caricaMenuProtocolliUtente(request, u));
				
				if(!u.hasOnlyPermessiUtenti())  // si e' deciso di farlo vedere sempre, sarà senza tendina: && this.core.isMultitenant())  
					gd.setSoggettiLinks(this.caricaMenuSoggetti(request, u));
			}
		}

		return gd;
	}

	public PageData initPageData() {
		return  initPageData(null);		
	}

	public PageData initPageData(String breadcrumb) {
		PageData pd = new PageData();
		if(breadcrumb != null) {
			List<GeneralLink> titlelist = new ArrayList<>();
			GeneralLink tl1 = new GeneralLink();
			tl1.setLabel(breadcrumb);
			titlelist.add(tl1);
			pd.setTitleList(titlelist);
		}
		List<DataElement> dati = new ArrayList<>();
		// titolo sezione login 
		DataElement titoloSezione = new DataElement();
		titoloSezione.setLabel(LoginCostanti.LABEL_LOGIN);
		titoloSezione.setType(DataElementType.TITLE);
		titoloSezione.setName("");

		DataElement login = new DataElement();
		login.setLabel(LoginCostanti.LABEL_USERNAME);
		login.setType(DataElementType.TEXT_EDIT);
		login.setName(UtentiCostanti.PARAMETRO_UTENTE_LOGIN);
		login.setStyleClass(Costanti.INPUT_CSS_CLASS);
		DataElement pwd = new DataElement();
		pwd.setLabel(UtentiCostanti.LABEL_PASSWORD);
		pwd.setType(DataElementType.CRYPT);
		pwd.setName(UtentiCostanti.PARAMETRO_UTENTE_PW);
		pwd.setStyleClass(Costanti.INPUT_CSS_CLASS);

		dati.add(titoloSezione);
		dati.add(login);
		dati.add(pwd);
		pd.setDati(dati);
		return pd;
	}



	public int getSize() {
		return 50;
	}

	public List<GeneralLink> caricaMenuProtocolliUtente(HttpServletRequest request, User u){
		List<GeneralLink> link = new ArrayList<>();

		// 1. controllo se ho piu' di un protocollo disponibile per l'utente
		try {
			List<String> protocolliDispondibili = this.core.getProtocolli(request, this.session,true);

			if(protocolliDispondibili != null && !protocolliDispondibili.isEmpty()) {
				// prelevo l'eventuale protocollo selezionato
				String protocolloSelezionato = u.getProtocolloSelezionatoPddConsole();
				if(protocolliDispondibili.size()==1) {
					protocolloSelezionato = protocolliDispondibili.get(0); // forzo
				}
				
				GeneralLink glModalitaCorrente = new GeneralLink();
 				String labelSelezionato = protocolloSelezionato == null ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : ConsoleHelper._getLabelProtocollo(protocolloSelezionato);
				String labelSelezionatoCompleta = MessageFormat.format(LoginCostanti.LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM, labelSelezionato);
				glModalitaCorrente.setLabel(labelSelezionatoCompleta); 
				glModalitaCorrente.setUrl("");
				glModalitaCorrente.setLabelWidth(this.core.getFontWidth(labelSelezionatoCompleta,  Font.BOLD, 16)); 
				link.add(glModalitaCorrente);

				if(protocolliDispondibili.size()>1) {
					// popolo la tendina con i protocolli disponibili
					for (String protocolloDisponibile : ProtocolUtils.orderProtocolli(protocolliDispondibili)) {
						GeneralLink glProt = new GeneralLink();
						
						String labelProt = ConsoleHelper._getLabelProtocollo(protocolloDisponibile);
						glProt.setLabel(labelProt);
						if(protocolloSelezionato != null && protocolloSelezionato.equals(protocolloDisponibile)) {
							glProt.setUrl("");
						} else {
							glProt.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA, protocolloDisponibile),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_MODALITA,Costanti.CHECK_BOX_ENABLED)
								);
						}

						glProt.setLabelWidth(this.core.getFontWidth(glProt.getLabel(), 14)); 
						link.add(glProt);
					}
				
					// seleziona tutti 
					GeneralLink glAll = new GeneralLink();
					glAll.setLabel(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
					if((protocolloSelezionato == null)) {
						glAll.setUrl("");
					} else {
						glAll.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA, UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL),
							new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
							new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_MODALITA,Costanti.CHECK_BOX_ENABLED)
							);
					}
					glAll.setLabelWidth(this.core.getFontWidth(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL, 14));
					link.add(glAll);
				}
				
			}
		} catch (Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error(e.getMessage(),e);
		}

		return link;
	}

	public List<GeneralLink> caricaMenuSoggetti(HttpServletRequest request, User u){
		List<GeneralLink> link = new ArrayList<>();

		try {
			// prelevo l'eventuale protocollo selezionato
			List<String> protocolliDispondibili = this.core.getProtocolli(request, this.session,true);
			String protocolloSelezionato = u.getProtocolloSelezionatoPddConsole();
			if(protocolliDispondibili.size()==1) {
				protocolloSelezionato = protocolliDispondibili.get(0); // forzo
			}
			
			// prelevo il soggetto selezionato
			String soggettoOperativoSelezionato = u.getSoggettoSelezionatoPddConsole();
			
			List<IDSoggetto> idSoggettiOperativi = this.soggettiCore.getIdSoggettiOperativi(protocolloSelezionato);
			// Si e' deciso che i soggetti associati valgono solo per govwayMonitor
			
			// visualizzo il menu' soggetti solo se e' stato selezionato un protocollo 
			if(protocolloSelezionato!=null && !"".equals(protocolloSelezionato) &&
					idSoggettiOperativi != null && !idSoggettiOperativi.isEmpty()) {
				
				if(soggettoOperativoSelezionato==null && idSoggettiOperativi.size()==1) {
					IDSoggetto idSoggetto = idSoggettiOperativi.get(0);
					soggettoOperativoSelezionato = idSoggetto.toString(); // forzo
				}
				
				GeneralLink glSoggettoCorrente = new GeneralLink();
				IDSoggetto idSoggettoOperativo = null;
				if(soggettoOperativoSelezionato!=null) {
					idSoggettoOperativo = this.soggettiCore.convertSoggettoSelezionatoToID(soggettoOperativoSelezionato);
				}
 				String labelSelezionato = soggettoOperativoSelezionato == null ? UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL : ConsoleHelper._getLabelNomeSoggetto(idSoggettoOperativo);
				String labelSelezionatoCompleta = MessageFormat.format(LoginCostanti.LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM, labelSelezionato);
				glSoggettoCorrente.setLabel(labelSelezionatoCompleta); 
				
				if(labelSelezionatoCompleta.length() > this.core.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente()) {
					glSoggettoCorrente.setLabel(ConsoleHelper.normalizeLabel(labelSelezionatoCompleta, this.core.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente()));
				}
				
				glSoggettoCorrente.setUrl("");
				glSoggettoCorrente.setLabelWidth(this.core.getFontWidth(glSoggettoCorrente.getLabel(),  Font.BOLD, 16)); 
				link.add(glSoggettoCorrente);
				
				Integer numeroMassimoSoggettiSelectListSoggettiOperatiti = this.core.getNumeroMassimoSoggettiSelectListSoggettiOperatiti();
				
				if(idSoggettiOperativi.size()>1) {
					List<String> listaLabel = new ArrayList<>();
					Map<String, IDSoggetto> mapLabelIds = new HashMap<>();
					for (IDSoggetto idSoggetto : idSoggettiOperativi) {
						String labelSoggetto = ConsoleHelper._getLabelNomeSoggetto(idSoggetto);
						if(!listaLabel.contains(labelSoggetto)) {
							listaLabel.add(labelSoggetto);
							mapLabelIds.put(labelSoggetto, idSoggetto);
						}
					}
					
					// Per ordinare in maniera case insensistive
					Collections.sort(listaLabel, new Comparator<String>() {
						 @Override
						public int compare(String o1, String o2) {
					           return o1.toLowerCase().compareTo(o2.toLowerCase());
					        }
						});
					
					for (String label : listaLabel) {
						GeneralLink glSoggetto = new GeneralLink();
						
						glSoggetto.setLabel(label);
						if(soggettoOperativoSelezionato != null && mapLabelIds.get(label).toString().equals(idSoggettoOperativo.toString())) {
							glSoggetto.setUrl("");
						} else {
							glSoggetto.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
									new Parameter(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO, NamingUtils.getSoggettoFromLabel(protocolloSelezionato, label).toString()),
									new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
									new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_SOGGETTO,Costanti.CHECK_BOX_ENABLED)
									);
						}
						
						if(label.length() > this.core.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente()) {
							glSoggetto.setLabel(ConsoleHelper.normalizeLabel(label, this.core.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente()));
							glSoggetto.setTooltip(label); 
						}
						
						glSoggetto.setLabelWidth(this.core.getFontWidth(glSoggetto.getLabel(), 14)); 
						link.add(glSoggetto);
					}
				
				
					// seleziona tutti 
					GeneralLink glAll = new GeneralLink();
					glAll.setLabel(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
					if((soggettoOperativoSelezionato == null)) {
						glAll.setUrl("");
					} else {
						glAll.setUrl(UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE,
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO, UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME,Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END),
								new Parameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_SOGGETTO,Costanti.CHECK_BOX_ENABLED)
								);
					}
					glAll.setLabelWidth(this.core.getFontWidth(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL, 14));
					link.add(glAll);
				}
				
				if( idSoggettiOperativi.size() > numeroMassimoSoggettiSelectListSoggettiOperatiti ) {	
					// Abilita l visualizzazione autocomplete
					glSoggettoCorrente.setUrl("abilitaAutocomplete");
				}
			}
		} catch (Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error(e.getMessage(),e);
		}

		return link;
	}

	public ControlStationCore getCore() {
		return this.core;
	}
}
