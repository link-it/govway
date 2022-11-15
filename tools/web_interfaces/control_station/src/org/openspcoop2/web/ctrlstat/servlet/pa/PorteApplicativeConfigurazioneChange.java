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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/***
 * 
 * PorteApplicativeConfigurazioneChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeConfigurazioneChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		boolean isPortaDelegata = false;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String nomeGruppo = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = porteApplicativeCore.getMappingErogazionePortaApplicativa(pa); 
			
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			Long idSoggetto = asps.getIdSoggetto() != null ? asps.getIdSoggetto() : -1L;
			List<MappingErogazionePortaApplicativa> listaMapping = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),null);
			List<String> listaNomiMappingUtilizzati = new ArrayList<>();
			
			for (MappingErogazionePortaApplicativa mappingTmp : listaMapping) {
				if(mappingTmp.getTableId() != mappingErogazionePortaApplicativa.getTableId()) {
					listaNomiMappingUtilizzati.add(mappingTmp.getDescrizione());
				}
			}

			String oldNomeGruppo =  mappingErogazionePortaApplicativa.getDescrizione();
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			lstParam.add(new Parameter(oldNomeGruppo,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			if(	porteApplicativeHelper.isEditModeInProgress()){
				
				if(nomeGruppo == null) {
					nomeGruppo = mappingErogazionePortaApplicativa.getDescrizione();
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteApplicativeHelper.configurazioneCambiaNome(dati, TipoOperazione.OTHER, nomeGruppo,isPortaDelegata);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.configurazioneCambiaNomeCheck(TipoOperazione.OTHER, nomeGruppo,listaNomiMappingUtilizzati,isPortaDelegata);
					
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteApplicativeHelper.configurazioneCambiaNome(dati, TipoOperazione.OTHER, nomeGruppo,isPortaDelegata);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE,
						ForwardParams.OTHER(""));
			}
			
			mappingErogazionePortaApplicativa.setDescrizione(nomeGruppo); 
			porteApplicativeCore.aggiornaDescrizioneMappingErogazionePortaApplicativa(mappingErogazionePortaApplicativa);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);


			List<PortaApplicativa> lista = null;
			int idLista = -1;
			
		
			switch (parentPA) {
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
				
				boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
				if(datiInvocazione) {
					idLista = Liste.SERVIZI;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					
					String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
							ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
						}
					}
					
					boolean [] permessi = new boolean[2];
					PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
					permessi[0] = pu.isServizi();
					permessi[1] = pu.isAccordiCooperazione();
					List<AccordoServizioParteSpecifica> listaS = null;
					String superUser   = ServletUtils.getUserLoginFromSession(session);
					if(apsCore.isVisioneOggettiGlobale(superUser)){
						listaS = apsCore.soggettiServizioList(null, ricerca,permessi,session, request);
					}else{
						listaS = apsCore.soggettiServizioList(superUser, ricerca,permessi,session, request);
					}
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziList(ricerca, listaS);
				}
				else {			
					idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
					ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
					asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
					List<MappingErogazionePortaApplicativa> lista2 = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),ricerca);
					AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
					apsHelper.prepareServiziConfigurazioneList(lista2, idAsps, idSoggetto+"", ricerca);
				}
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = porteApplicativeHelper.isModalitaCompleta() ? ForwardParams.OTHER("") : PorteApplicativeCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE, fwP);
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE , 
					ForwardParams.OTHER(""));
		} 
	}

}
