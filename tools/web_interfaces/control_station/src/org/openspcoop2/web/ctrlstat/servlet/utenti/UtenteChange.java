/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.utils.crypt.Password;
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

		try {
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);

			String newpw = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			String tipogui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI);
			String changeGui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI);
			String changepw = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_PASSWORD);
			String changeModalita = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_MODALITA);
			String tipoModalita = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
			String multiTenant = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_MULTI_TENANT);
			
			String first = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_FIRST);

			UtentiCore utentiCore = new UtentiCore();
			
			User user = ServletUtils.getUserFromSession(session);
			
			if(multiTenant==null && first==null) {
				if(user.isPermitMultiTenant()) {
					multiTenant = Costanti.CHECK_BOX_ENABLED;
				}
				else {
					multiTenant = Costanti.CHECK_BOX_DISABLED;
				}
			}
			
			InterfaceType interfaceType = null;
			if(tipogui==null) {
				interfaceType = utentiHelper.getTipoInterfaccia();
			}
			else {
				interfaceType = InterfaceType.convert(tipogui, true);
			}
			
			String protocolloSelezionatoUtente = null;
			
			if(tipoModalita == null) {
				// prelevo il vecchio valore del protocollo
				protocolloSelezionatoUtente = user.getProtocolloSelezionatoPddConsole();
			} else {
				// il caso all viene gestito impostando il valore del protocollo selezionato = null;
				if(!tipoModalita.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL))
					protocolloSelezionatoUtente  = tipoModalita;
			}
						
			// Check multitenant
			boolean forceEnableMultitenant = utentiCore.isForceEnableMultiTenant(user, true);
			if(forceEnableMultitenant) {
				multiTenant = Costanti.CHECK_BOX_ENABLED;
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

			// setto la barra del titolo
			if(changeGui == null && changeModalita==null) {
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

						utentiHelper.addUtenteChangeToDati(dati, interfaceType, changepw, userLogin, modalitaGatewayDisponibili, multiTenant, forceEnableMultitenant);

						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

						return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTE, ForwardParams.CHANGE());

					}else{
						//tutto ok modifico pwd
						// Cripto la nuova password
						if (!"".equals(newpw)) {
							Password procToCall = new Password();
							newpw = procToCall.cryptPw(newpw);

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
				myS.setInterfaceType(interfaceType);
				myS.setProtocolloSelezionatoPddConsole(protocolloSelezionatoUtente);
				myS.setPermitMultiTenant(ServletUtils.isCheckBoxEnabled(multiTenant));
				utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), myS);

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
				String pdMsgTitle= "Passaggio alla Modalit&agrave; Gateway selezionata effettuato con successo.";
				if(protocolloSelezionatoUtente == null) {
					pdMsg = "<p>Modalit&agrave; disponibili:<p/>" + sbProtocolli.toString();
				} else {
					pdMsg = "<p>Modalit&agrave; attuale:<p/>" + sbProtocolli.toString();
				}
				
				pd.setMessage(pdMsg, pdMsgTitle, Costanti.MESSAGE_TYPE_INFO);
				
				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			} else {
				// provengo dalla maschera di modifica utente
				// preparo i campio
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtenteChangeToDati(dati, interfaceType, changepw, userLogin, modalitaGatewayDisponibili, multiTenant, forceEnableMultitenant);

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
