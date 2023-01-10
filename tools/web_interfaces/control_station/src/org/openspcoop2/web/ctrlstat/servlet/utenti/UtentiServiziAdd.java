/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.text.MessageFormat;
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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;

/**     
 * UtentiServiziAdd
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtentiServiziAdd extends Action {

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
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String servizio = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SERVIZIO);
			String protocollo = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PROTOCOLLO);
			
			// Preparo il menu
			utentiHelper.makeMenu();
			
			UtentiCore utentiCore = new UtentiCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(utentiCore);
			User user = utentiCore.getUser(nomesu);
			ConsoleSearch searchServizi = new ConsoleSearch(true);
			List<String> protocolli = user.getProtocolliSupportati();
			if(protocollo == null) {
				if(protocolli!=null && protocolli.size()>0) {
					protocollo = utentiCore.getProtocolloDefault(request, null, protocolli);
				}
			}
			
			searchServizi.addFilter(Liste.SERVIZI, Filtri.FILTRO_PROTOCOLLO, protocollo);
			boolean[] permessiUtente = null;
			List<AccordoServizioParteSpecifica> serviziList = aspsCore.soggettiServizioList(null, searchServizi, permessiUtente , false, false);
			
			List<AccordoServizioParteSpecifica> listaServiziNonUtilizzati = new ArrayList<AccordoServizioParteSpecifica>();
			
			for (AccordoServizioParteSpecifica asps : serviziList) {
				boolean found = false;
				for (IDServizio idServ : user.getServizi()) {
					if(asps.getTipo().equals(idServ.getTipo()) && asps.getNome().equals(idServ.getNome()) 
							 && asps.getVersione().intValue() == idServ.getVersione().intValue() 
							 && asps.getTipoSoggettoErogatore().equals(idServ.getSoggettoErogatore().getTipo())
							 && asps.getNomeSoggettoErogatore().equals(idServ.getSoggettoErogatore().getNome())) {
						found = true;
						break;
					}
				}	
				
				if(!found) {
					listaServiziNonUtilizzati.add(asps);
				}
			}
			
			
			String [] serviziLabels = new String [listaServiziNonUtilizzati.size() + 1];
			String [] serviziValues = new String [listaServiziNonUtilizzati.size() + 1];

			serviziLabels[0] = CostantiControlStation.LABEL_LIST_VALORE_NON_PRESENTE;
			serviziValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < listaServiziNonUtilizzati.size(); i++) {
				AccordoServizioParteSpecifica asps = listaServiziNonUtilizzati.get(i);
				String _protocollo = aspsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
				serviziLabels[(i+1)] = utentiHelper.getLabelNomeServizio(_protocollo, asps.getTipo(), asps.getNome(), asps.getVersione()) + 
						" ("+utentiHelper.getLabelNomeSoggetto(_protocollo, asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore())+")";
				serviziValues[(i+1)] = asps.getId() + "";
			}
	
			
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST));
			lstParam.add(new Parameter(nomesu, UtentiCostanti.SERVLET_NAME_UTENTI_CHANGE, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu)));
			lstParam.add(new Parameter(UtentiCostanti.LABEL_UTENTI_SERVIZI, UtentiCostanti.SERVLET_NAME_UTENTI_SERVIZI_LIST, new Parameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME, nomesu)));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			if(utentiHelper.isEditModeInProgress()){
				ServletUtils.setPageDataTitle(pd,lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				if(servizio==null){
					servizio = "";
				}
				
				if(listaServiziNonUtilizzati.size()<=0){
					if(protocolli.size() == 1) {
						pd.setMessage(UtentiCostanti.LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI, MessageType.INFO);
						pd.disableEditMode();
						pd.setDati(dati);
						ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI,ForwardParams.ADD());
					} else {
						String msf = MessageFormat.format(UtentiCostanti.LABEL_UTENTI_SERVIZI_DISPONIBILI_ESAURITI_PER_LA_MODALITA_XX, utentiHelper.getLabelProtocollo(protocollo));
						pd.setMessage(msf, MessageType.INFO);
						pd.disableOnlyButton();
					}
				}
				
				utentiHelper.addUtentiServiziToDati(dati, TipoOperazione.ADD,nomesu,servizio,serviziValues,serviziLabels,protocolli,protocollo);
				
				pd.setDati(dati);
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI,ForwardParams.ADD());
			}
			
			// Controlli sui campi immessi
			boolean isOk = utentiHelper.utentiServiziCheckData(TipoOperazione.ADD,nomesu,servizio);
			if (!isOk) {
				ServletUtils.setPageDataTitle(pd,lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				utentiHelper.addUtentiServiziToDati(dati, TipoOperazione.ADD,nomesu,servizio,serviziValues,serviziLabels,protocolli,protocollo);
				
				pd.setDati(dati);
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI,ForwardParams.ADD());
			}
			
			// aggiungo il nuovo Servizio alla lista
			Long newId = Long.parseLong(servizio);
			IDServizio idServizio = aspsCore.getIdAccordoServizioParteSpecifica(newId);
			user.getServizi().add(idServizio);
			
			// salvataggio sul db
			utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), user); 
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class);
	
			int idLista = Liste.UTENTI_SERVIZI;
	
			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
	
		
			List<IDServizio> lista = utentiCore.utentiServiziList(nomesu, ricerca);
	
			utentiHelper.prepareUtentiServiziList(ricerca, lista, user);
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI_SERVIZI, ForwardParams.ADD());
		} 
	}

}
