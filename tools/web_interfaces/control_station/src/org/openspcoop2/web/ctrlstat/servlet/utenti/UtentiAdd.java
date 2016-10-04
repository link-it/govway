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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.utils.crypt.Password;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.Permessi;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * suAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtentiAdd extends Action {

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
	
			String nomesu = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String pwsu = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PASSWORD);
			String confpwsu = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			String tipoGui = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
			InterfaceType interfaceType = InterfaceType.STANDARD;
			if(InterfaceType.AVANZATA.toString().equals(tipoGui)){
				interfaceType = InterfaceType.AVANZATA;
			}
			String isServizi = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
			String isDiagnostica = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
			String isSistema = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
			String isMessaggi = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
			String isUtenti = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
			String isAuditing = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
			String isAccordiCooperazione = request.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
	
			Boolean singlePdD = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			
			// Preparo il menu
			utentiHelper.makeMenu();
	
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletAdd(pd, UtentiCostanti.LABEL_UTENTI);
				
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE,null),
						new Parameter(UtentiCostanti.LABEL_UTENTI,null), 
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				if(nomesu==null){
					nomesu = "";
				}
				if(pwsu==null){
					pwsu = "";
				}
				if(confpwsu==null){
					confpwsu = "";
				}
				
				utentiHelper.addUtentiToDati(dati, TipoOperazione.ADD, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						null);
				
				pd.setDati(dati);
		
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTI,ForwardParams.ADD());

			}
	
			// Controlli sui campi immessi
			boolean isOk = utentiHelper.utentiCheckData(TipoOperazione.ADD,singlePdD);
			if (!isOk) {
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletAdd(pd, UtentiCostanti.LABEL_UTENTI);
				
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE,null),
						new Parameter(UtentiCostanti.LABEL_UTENTI,null), 
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				utentiHelper.addUtentiToDati(dati, TipoOperazione.ADD, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						null);
				
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
			}
	
			// Cripto la password
			Password procToCall = new Password();
			pwsu = procToCall.cryptPw(pwsu);
	
			// Inserisco l'utente nel db
			User newU = new User();
			newU.setLogin(nomesu);
			newU.setPassword(pwsu);
			newU.setInterfaceType(InterfaceType.valueOf(tipoGui));
			String puString = "";
			if (ServletUtils.isCheckBoxEnabled(isServizi))
				puString = Permessi.SERVIZI.toString();
			if (ServletUtils.isCheckBoxEnabled(isDiagnostica)) {
				if (puString.equals(""))
					puString = Permessi.DIAGNOSTICA.toString();
				else
					puString = puString+","+Permessi.DIAGNOSTICA.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isSistema)) {
				if (puString.equals(""))
					puString = Permessi.SISTEMA.toString();
				else
					puString = puString+","+Permessi.SISTEMA.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isMessaggi)) {
				if (puString.equals(""))
					puString = Permessi.CODE_MESSAGGI.toString();
				else
					puString = puString+","+Permessi.CODE_MESSAGGI.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isUtenti)) {
				if (puString.equals(""))
					puString = Permessi.UTENTI.toString();
				else
					puString = puString+","+Permessi.UTENTI.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isAuditing)) {
				if (puString.equals(""))
					puString = Permessi.AUDITING.toString();
				else
					puString = puString+","+Permessi.AUDITING.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isAccordiCooperazione)) {
				if (puString.equals(""))
					puString = Permessi.ACCORDI_COOPERAZIONE.toString();
				else
					puString = puString+","+Permessi.ACCORDI_COOPERAZIONE.toString();
			}
			newU.setPermessi(PermessiUtente.toPermessiUtente(puString));
	
			UtentiCore utentiCore = new UtentiCore();
			utentiCore.performCreateOperation(userLogin, utentiHelper.smista(), newU);
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	
			int idLista = Liste.SU;
	
			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
	
			List<User> lista = utentiCore.userList(ricerca);
	
			utentiHelper.prepareUtentiList(ricerca, lista, singlePdD);
	
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
		} 
	}
}
