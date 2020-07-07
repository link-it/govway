/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginSessionUtilities;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * changePw
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtenteChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);
		String modalitaGatewayDisponibili = "";
		String soggettiDisponibili = "";

		try {
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);

			String newpw = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			String tipogui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI);
			String changeGui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI);
			String changepw = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_PASSWORD);
			String changeModalita = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_MODALITA);
			String tipoModalita = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
			String changeSoggetto = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_SOGGETTO);
			String idSoggetto = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
			
			UtentiCore utentiCore = new UtentiCore();
			
			User user = ServletUtils.getUserFromSession(session);
			
			InterfaceType interfaceType = null;
			if(tipogui==null) {
				interfaceType = utentiHelper.getTipoInterfaccia();
			}
			else {
				interfaceType = InterfaceType.convert(tipogui, true);
			}
			
			String soggettoSelezionatoUtente = null;
			
			if(idSoggetto == null) {
				// prelevo il vecchio valore del protocollo
				soggettoSelezionatoUtente = user.getSoggettoSelezionatoPddConsole();
			} else {
				// il caso all viene gestito impostando il valore del soggetto selezionato = null;
				if(!idSoggetto.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL))
					soggettoSelezionatoUtente  = idSoggetto;
			}
			
			String protocolloSelezionatoUtente = null;
			String oldProtocolloSelezionatoUtente = user.getProtocolloSelezionatoPddConsole();
			boolean updateSoggetto = false;
			
			if(tipoModalita == null) {
				// prelevo il vecchio valore del protocollo
				protocolloSelezionatoUtente = oldProtocolloSelezionatoUtente;
			} else {
				// il caso all viene gestito impostando il valore del protocollo selezionato = null;
				if(!tipoModalita.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL)) 
					protocolloSelezionatoUtente  = tipoModalita;

				// 	reset soggetto scelto se cambia il protocollo
				// 1. se ho messo tutti oppure se ho cambiato modalita'
				if((protocolloSelezionatoUtente == null) || !(protocolloSelezionatoUtente.equals(oldProtocolloSelezionatoUtente))) {
					soggettoSelezionatoUtente = null;
					updateSoggetto = true;
				}
//				else {
//					soggettoSelezionatoUtente = null;
//					updateSoggetto = true;
//				}
			}
			
			// Preparo il menu
			utentiHelper.makeMenu();
			
			List<String> protocolliDisponibli = utentiCore.getProtocolli(session, true);
			StringBuilder sb= new StringBuilder();
			for (String protocollo : protocolliDisponibli) {
				if(sb.length() > 0)
					sb.append(", ");
				
				sb.append(utentiHelper.getLabelProtocollo(protocollo));
			}
			
			modalitaGatewayDisponibili = sb.toString();
			soggettiDisponibili = soggettoSelezionatoUtente != null ? soggettoSelezionatoUtente : UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL;

			// setto la barra del titolo
			if(changeGui == null && changeModalita==null && changeSoggetto == null) {
				ServletUtils.setPageDataTitle(pd, 
					new Parameter(UtentiCostanti.LABEL_UTENTE, null));
			}

			User myS = null;
			// Se idhid != null, modifico i dati della porta di dominio nel db
			if(utentiHelper.isEditModeInProgress() == false){

				//se e' richiesta la modifica pwd allora controllo dati inseriti per modifica pwd
				
				if(ServletUtils.isCheckBoxEnabled(changepw)){

					// Controlli sui campi immessi
					boolean isOk = utentiHelper.changePwCheckData();
					if (!isOk) {
						// preparo i campi
						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						utentiHelper.addUtenteChangeToDati(dati, interfaceType, changepw, userLogin, modalitaGatewayDisponibili,soggettiDisponibili);

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTE, ForwardParams.CHANGE());

					}else{
						//tutto ok modifico pwd
						// Cripto la nuova password
						if (!"".equals(newpw)) {
							newpw = utentiCore.getUtenzePasswordManager().crypt(newpw);

							// Modifico i dati della pw nel db
							myS = utentiCore.getUser(userLogin);
							myS.setPassword(newpw);

							utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), myS);

							//resetto changepwd
							changepw=null;
						}
					}
				}


				// modifica interface type oppure modalita gateway
				if(myS==null){
					myS = utentiCore.getUser(userLogin);
				}
				
				if(changeGui != null) {
					myS.setInterfaceType(interfaceType);
					utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), myS);
				} else if(changeModalita != null) {
					myS.setProtocolloSelezionatoPddConsole(protocolloSelezionatoUtente);
					utentiCore.salvaModalitaUserPddConsole(myS.getLogin(), protocolloSelezionatoUtente);
					if(updateSoggetto) {
						myS.setSoggettoSelezionatoPddConsole(soggettoSelezionatoUtente);
						utentiCore.salvaSoggettoOperativoUserPddConsole(myS.getLogin(), soggettoSelezionatoUtente);
					}
				}  else if(changeSoggetto != null) {
					myS.setSoggettoSelezionatoPddConsole(soggettoSelezionatoUtente);
					utentiCore.salvaSoggettoOperativoUserPddConsole(myS.getLogin(), soggettoSelezionatoUtente);
				} else {
					myS.setProtocolloSelezionatoPddConsole(protocolloSelezionatoUtente);
					myS.setSoggettoSelezionatoPddConsole(soggettoSelezionatoUtente);
					myS.setInterfaceType(interfaceType);
					utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), myS);
				}
				
				LoginSessionUtilities.cleanLoginParametersSession(session);

				ServletUtils.setUserIntoSession(session, myS); // update in sessione.
				utentiHelper.setTipoInterfaccia(myS.getInterfaceType()); // update InterfaceType
				LoginSessionUtilities.setLoginParametersSession(session, utentiCore, userLogin);

				pd.setMessage("Modifiche effettuate con successo", Costanti.MESSAGE_TYPE_INFO);

			}//fine modifica user interface
			
			
			// se ho cliccato sul link cambia modalita interfaccia
			if(changeGui != null) {
				// provengo dal link presente nell'header della pagina a dx.
				pd.setMessage("Passaggio all'interfaccia '"+interfaceType.toString().toLowerCase()+"' effettuato con successo.", Costanti.MESSAGE_TYPE_INFO_SINTETICO);

				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			} else if(changeModalita != null) { // clic sul link cambia modalita gateway 
				// messaggio di cambiamento del protocollo:
				List<String> protocolli = utentiCore.getProtocolli(session);
				StringBuilder sbProtocolli = new StringBuilder("<ul>");
				for (String protocollo : protocolli) {
					String descrizioneProtocollo = utentiHelper.getDescrizioneProtocollo(protocollo);
					String webSiteProtocollo = utentiHelper.getWebSiteProtocollo(protocollo);
					String labelProtocollo = utentiHelper.getLabelProtocollo(protocollo); 
					
//					if(sbProtocolli.length() > 0)
//						sbProtocolli.append("<br/>");
					
					sbProtocolli.append("</br>");
					sbProtocolli.append("<li style=\"list-style-type:disc; margin-left:12px;\">");
					sbProtocolli.append("<p><i>");
					String linkSito = "<a href=\""+webSiteProtocollo+"\" target=\"_blank\">"+labelProtocollo+"</a>";
					sbProtocolli.append(linkSito);
					//sbProtocolli.append(""+labelProtocollo+": ");
					sbProtocolli.append("</i></p>");
					sbProtocolli.append("<p>");
					sbProtocolli.append(descrizioneProtocollo);
//					sbProtocolli.append(" (");
//					String linkSito = "<a href=\""+webSiteProtocollo+"\" target=\"_blank\">"+webSiteProtocollo+"</a>";
//					sbProtocolli.append(linkSito);
//					sbProtocolli.append(")");
					sbProtocolli.append("</p>");
					
				sbProtocolli.append("</li>");
				}
				sbProtocolli.append("</ul>");
				
				//String labelProt = protocolloSelezionatoUtente != null ?  ConsoleHelper.getLabelProtocollo(protocolloSelezionatoUtente) : UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL;
				String pdMsg = "";
				String pdMsgTitle= "Passaggio al "+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE+" selezionato effettuato con successo.";
				if(protocolloSelezionatoUtente == null) {
					pdMsg = "<p>"+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_COMPACT+" disponibili:<p/>" + sbProtocolli.toString();
				} else {
					pdMsg = "<p>"+org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+" attuale:<p/>" + sbProtocolli.toString();
				}
				
				pd.setMessage(pdMsg, pdMsgTitle, Costanti.MESSAGE_TYPE_INFO);
				
				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			} else if(changeSoggetto != null) { // clic sul link cambia soggetto
				
				String pdMsg = "";
				String pdMsgTitle= "Passaggio al "+UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO+" selezionato effettuato con successo.";
				if(soggettoSelezionatoUtente == null) {
					pdMsg = "<p>"+UtentiCostanti.LABEL_PARAMETRO_SOGGETTI_COMPACT+" disponibili: " + "Tutti";
				} else {
					IDSoggetto idSoggettoOperativo = utentiCore.convertSoggettoSelezionatoToID(soggettoSelezionatoUtente);
					pdMsg = "<p>"+UtentiCostanti.LABEL_PARAMETRO_SOGGETTO_COMPACT+" attuale: " + utentiHelper.getLabelNomeSoggetto(idSoggettoOperativo);
				}
				
				pd.setMessage(pdMsg, pdMsgTitle, Costanti.MESSAGE_TYPE_INFO);
				
				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			}else {
				// provengo dalla maschera di modifica utente
				// preparo i campio
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtenteChangeToDati(dati, interfaceType, changepw, userLogin, modalitaGatewayDisponibili,soggettiDisponibili);

				pd.setDati(dati);
			}

			// Reinit general data per aggiornare lo stato della barra dell'header a dx.
			gd = generalHelper.initGeneralData(request);

			// Refresh Menu' Preparo il menu
			utentiHelper.makeMenu();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTE, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
		}
	}
}
