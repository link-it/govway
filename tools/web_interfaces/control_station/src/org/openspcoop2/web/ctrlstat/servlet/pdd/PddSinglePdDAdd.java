/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PddSinglePdDAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PddSinglePdDAdd extends Action {

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
			PddHelper pddHelper = new PddHelper(request, pd, session);
			String nome = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_NOME);
			String descr = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
			String implementazione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String subject = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);
			String clientAuth = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			
			PddCore pddCore = new PddCore();
	
			// Preparo il menu
			pddHelper.makeMenu();
	
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(pddHelper.isEditModeInProgress()){
			
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST);
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				if (implementazione == null || "".equals(implementazione))
					implementazione = PddCostanti.DEFAULT_PDD_IMPLEMENTAZIONE;
				
				dati = pddHelper.addPddToDati(dati, nome, null, "", subject, "", "", PddTipologia.ESTERNO, TipoOperazione.ADD, 
						null, "", "",  0, descr, "", 0, implementazione, clientAuth, true);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.ADD());
			}
	
			// Controlli sui campi immessi
			boolean isOk = pddHelper.pddCheckData(TipoOperazione.ADD, true);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST);
					
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				dati = pddHelper.addPddToDati(dati, nome, null, "", subject, "", "", PddTipologia.ESTERNO, TipoOperazione.ADD, 
						null, "", "", 0, descr, "", 0, implementazione, clientAuth, true);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.ADD());
			}
	
			// Inserisco il pdd nel db
			PdDControlStation pdd = new PdDControlStation();
			pdd.setNome(nome);
			
			List<PortaDominio> listaPddOperative = null;
			if(pddCore.isVisioneOggettiGlobale(userLogin)){
				listaPddOperative = pddCore.pddListSinglePdd(null,PddTipologia.OPERATIVO.toString(), new Search(true));
			}else{
				listaPddOperative = pddCore.pddListSinglePdd(userLogin,PddTipologia.OPERATIVO.toString(), new Search(true));
			}
			if(listaPddOperative.size()>0)
				pdd.setTipo(PddTipologia.ESTERNO.toString());
			else
				pdd.setTipo(PddTipologia.OPERATIVO.toString());
			if(subject!=null && !"".equals(subject))
				pdd.setSubject(subject);
			else
				pdd.setSubject(null);
			pdd.setDescrizione(descr);
			pdd.setImplementazione(implementazione);
			pdd.setClientAuth(StatoFunzionalita.toEnumConstant(clientAuth));
			pdd.setSuperUser(userLogin);

			pddCore.performCreateOperation(userLogin, pddHelper.smista(), pdd);

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session,Search.class); 

			List<PdDControlStation> lista = null;
			if(pddCore.isVisioneOggettiGlobale(userLogin)){
				lista = pddCore.pddList(null, ricerca);
			}else{
				lista = pddCore.pddList(userLogin, ricerca);
			}

			pddHelper.preparePddSinglePddList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.ADD());
		} 
	}
}
