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
package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
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
 * PorteDelegateConfigurazioneChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateConfigurazioneChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		boolean isPortaDelegata = true;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idSoggFruitore);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			String nomeGruppo = porteDelegateHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			
			IDSoggetto idSoggettoFruitore = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			IDPortaDelegata idpd = new IDPortaDelegata();
			idpd.setNome(idporta);
			
			MappingFruizionePortaDelegata mappingFruizionePortaDelegata = porteDelegateCore.getMappingFruizionePortaDelegata(portaDelegata);
			
			List<String> listaNomiMappingUtilizzati = new ArrayList<>();
			int idAspsInt = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsInt);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			List<MappingFruizionePortaDelegata> listaMapping = apsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio2, null);
			
			for (MappingFruizionePortaDelegata mappingTmp : listaMapping) {
				if(mappingTmp.getTableId() != mappingFruizionePortaDelegata.getTableId()) {
					listaNomiMappingUtilizzati.add(mappingTmp.getDescrizione());
				}
			}

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String oldNomeGruppo =  mappingFruizionePortaDelegata.getDescrizione();
			
			lstParam.add(new Parameter(oldNomeGruppo,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			if(	porteDelegateHelper.isEditModeInProgress()){
				
				if(nomeGruppo == null) {
					nomeGruppo = mappingFruizionePortaDelegata.getDescrizione();
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.configurazioneCambiaNome(dati, TipoOperazione.OTHER, nomeGruppo,isPortaDelegata);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.configurazioneCambiaNomeCheck(TipoOperazione.OTHER, nomeGruppo,listaNomiMappingUtilizzati,isPortaDelegata);
			
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteDelegateHelper.configurazioneCambiaNome(dati, TipoOperazione.OTHER, nomeGruppo,isPortaDelegata);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE,
						ForwardParams.OTHER(""));
			}

			mappingFruizionePortaDelegata.setDescrizione(nomeGruppo); 
			porteDelegateCore.aggiornaDescrizioneMappingFruizionePortaDelegata(mappingFruizionePortaDelegata);
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = -1;
			List<PortaDelegata> lista = null;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
				
				if(datiInvocazione) {
					
					String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
					boolean gestioneFruitori = false;
					if(tipologia!=null) {
						if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
							gestioneFruitori = true;
						}
					}
					if(gestioneFruitori) {
						
						idLista = Liste.SERVIZI;
						
						ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
						
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
						
						String superUser =   ServletUtils.getUserLoginFromSession(session); 
						PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
						boolean [] permessi = new boolean[2];
						permessi[0] = pu.isServizi();
						permessi[1] = pu.isAccordiCooperazione();
						List<AccordoServizioParteSpecifica> lista2 = null;
						if(apsCore.isVisioneOggettiGlobale(superUser)){
							lista2 = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, false);
						}else{
							lista2 = apsCore.soggettiServizioList(superUser, ricerca, permessi, gestioneFruitori, false);
						}

						apsHelper.prepareServiziList(ricerca, lista2);
						
					}
					else {
					
						idLista = Liste.SERVIZI_FRUITORI;
						ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
						
						List<Fruitore> listaFruitori = apsCore.serviziFruitoriList(idAspsInt, ricerca);
						apsHelper.prepareServiziFruitoriList(listaFruitori, idAsps, ricerca);
						
					}
				}
				else {
					idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
					ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
					
					listaMapping = apsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio2, ricerca);
					apsHelper.serviziFruitoriMappingList(listaMapping, idAsps, idSoggFruitore, idSoggettoFruitore, idFruizione, ricerca); 
				}
				
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = porteDelegateHelper.isModalitaCompleta() ? ForwardParams.OTHER("") : PorteDelegateCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE, fwP);
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE , 
					ForwardParams.OTHER(""));
		} 
	}

}
