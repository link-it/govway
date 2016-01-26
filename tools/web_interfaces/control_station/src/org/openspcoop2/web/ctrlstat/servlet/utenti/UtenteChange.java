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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

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
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
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

		try {
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);

			String newpw = request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			String tipogui = request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_GUI);
			String changeGui = request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_GUI);
			String changepw = request.getParameter(UtentiCostanti.PARAMETRO_UTENTE_CHANGE_PASSWORD);

			UtentiCore utentiCore = new UtentiCore();

			tipogui = tipogui==null ? ServletUtils.getUserFromSession(session).getInterfaceType().toString() : tipogui;

			InterfaceType interfaceType = InterfaceType.STANDARD;
			if(InterfaceType.AVANZATA.toString().equals(tipogui)){
				interfaceType = InterfaceType.AVANZATA;
			}

			// Preparo il menu
			utentiHelper.makeMenu();

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE,null),
					new Parameter(UtentiCostanti.LABEL_UTENTE, null));

			// Se idhid != null, modifico i dati della porta di dominio nel db
			if(ServletUtils.isEditModeInProgress(request) == false){

				//se e' richiesta la modifica pwd allora controllo dati inseriti per modifica pwd
				User myS = null;
				if(ServletUtils.isCheckBoxEnabled(changepw)){

					// Controlli sui campi immessi
					boolean isOk = utentiHelper.changePwCheckData();
					if (!isOk) {
						// preparo i campi
						Vector<DataElement> dati = new Vector<DataElement>();

						dati.addElement(ServletUtils.getDataElementForEditModeFinished());

						utentiHelper.addUtenteChangeToDato(dati, interfaceType, changepw);

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


				// modifica interface type
				if(myS==null){
					myS = utentiCore.getUser(userLogin);
				}
				myS.setInterfaceType(InterfaceType.valueOf(tipogui));
				utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), myS);

				LoginSessionUtilities.cleanLoginParametersSession(session);

				ServletUtils.setUserIntoSession(session, myS); // update in sessione.
				LoginSessionUtilities.setLoginParametersSession(session, utentiCore, userLogin);

				pd.setMessage("Modifiche effettuate con successo");

			}//fine modifica user interface

			// provengo dalla maschera di modifica utente
			if(changeGui == null){
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtenteChangeToDato(dati, interfaceType, changepw);

				pd.setDati(dati);


			} else {
				// provengo dal link presente nell'header della pagina a dx.
				pd.setMessage("Passaggio all'interfaccia '"+interfaceType.toString().toLowerCase()+"' effettuato con successo.");

				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

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
