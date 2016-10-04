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


package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * pddSinglePdDChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PddSinglePdDChange extends Action {

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
			String id = request.getParameter(PddCostanti.PARAMETRO_PDD_ID);
			String descr = request.getParameter(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
			String implementazione = request.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String subject = request.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);
			String clientAuth = request.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			String tipo = request.getParameter(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			String nome = request.getParameter(PddCostanti.PARAMETRO_PDD_NOME);
			
			PddCore pddCore = new PddCore();

			// Preparo il menu
			pddHelper.makeMenu();

			// Prendo il nome della pdd

			if ((id == null) || id.equals("")) {
				pd.setMessage("L'identificativo della porta di dominio &egrave; necessario!");
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardGeneralError(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.CHANGE());
			}
			long idPdd = Long.parseLong(id);
			PdDControlStation pdd = pddCore.getPdDControlStation(idPdd);

			// Se nomehid = null, devo visualizzare la pagina per la modifica
			// dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, 
						PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST, pdd.getNome());

				if(descr==null)
					descr = pdd.getDescrizione();
				if(nome==null)
					nome = pdd.getNome();
				if(implementazione==null)
					implementazione = pdd.getImplementazione();
				if(subject==null)
					subject = pdd.getSubject();
				if(clientAuth==null){
					if(pdd.getClientAuth()!=null)
						clientAuth = pdd.getClientAuth().toString();
				}
				tipo = pdd.getTipo();

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = pddHelper.addPddToDati(dati, nome, id, "", subject, "", "", PddTipologia.toPddTipologia(tipo), TipoOperazione.CHANGE, 
						null, "", "", 0, descr, "", 0, implementazione, clientAuth, true);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = pddHelper.pddCheckData(TipoOperazione.CHANGE, true);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, 
						PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST, pdd.getNome());

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = pddHelper.addPddToDati(dati, nome, id, "", subject, "", "", PddTipologia.toPddTipologia(tipo), TipoOperazione.CHANGE, 
						null, "", "", 0, descr, "", 0, implementazione, clientAuth, true);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.CHANGE());
			}

			// Modifico i dati del pdd nel db
			pdd.setDescrizione(descr);
			pdd.setImplementazione(implementazione);
			if(subject!=null && !"".equals(subject))
				pdd.setSubject(subject);
			else
				pdd.setSubject(null);
			pdd.setClientAuth(StatoFunzionalita.toEnumConstant(clientAuth));
			
			ArrayList<Object> oggettiDaAggiornare = new ArrayList<Object>();
			
			// Se e' stato modificato il nome della pdd, modifico i soggetti
			if(pdd.getNome().equals(nome)==false){
				List<SoggettoCtrlStat> soggetti = pddCore.soggettiWithServer(pdd.getNome());
				for (SoggettoCtrlStat soggettoCtrlStat : soggetti) {
					if(soggettoCtrlStat.getSoggettoReg()!=null)
						soggettoCtrlStat.getSoggettoReg().setPortaDominio(nome);
					oggettiDaAggiornare.add(soggettoCtrlStat);
				}
				pdd.setOldNomeForUpdate(pdd.getNome());
				pdd.setNome(nome);
			}
			
			oggettiDaAggiornare.add(pdd);
			
			// effettuo le operazioni
			pddCore.performUpdateOperation(userLogin, pddHelper.smista(), oggettiDaAggiornare.toArray(new Object[1]));

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
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.CHANGE());
		} 
	}
}
