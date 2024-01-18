/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.utils.UpdateProprietaOggetto;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
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

/**     
 * PorteDelegateConnettoreRidefinito
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateConnettoreRidefinito  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null &&
				AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
						
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE));
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			
			IDSoggetto idSoggettoFruitore = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitore));
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitore));
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			
			String modalita = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE);
			
			String [] modalitaLabels = { PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO };
			String [] modalitaValues = { PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT, PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO };

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String connettoreLabelDi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_RIDEFINITO_DI;
			String connettoreLabel = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_RIDEFINITO;
			if(!porteDelegateHelper.isModalitaCompleta()) {
				connettoreLabelDi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI;
				connettoreLabel = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
			}
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						connettoreLabelDi,
						connettoreLabel,
						portaDelegata);
			}
			else {
				labelPerPorta = connettoreLabelDi+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			AccordoServizioParteSpecifica asps = null;
			IDServizio idServizio = null;
			if(idAsps!=null && !"".equals(idAsps)) {
				asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps));
				idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			}
			else {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(), 
						portaDelegata.getServizio().getNome(), 
						new IDSoggetto(portaDelegata.getSoggettoErogatore().getTipo(), portaDelegata.getSoggettoErogatore().getNome()), 
						portaDelegata.getServizio().getVersione());
				asps = apsCore.getServizio(idServizio);
			}
			
			if(asps==null) {
				throw new Exception("AccordoServizioParteSpecifica non trovato");
			}
			
			Soggetto soggFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitore));
			Fruitore fru = null;
			if(gestioneFruitori) {
				for (Fruitore fruCheck : asps.getFruitoreList()) {
					if(fruCheck.getTipo().equals(idSoggettoFruitore.getTipo()) &&
							fruCheck.getNome().equals(idSoggettoFruitore.getNome())) {
						fru = fruCheck;
						break;
					}
				}
				if(fru==null) {
					throw new Exception("Fruitore con id '"+idSoggettoFruitore.getTipo()+"/"+idSoggettoFruitore.getNome()+"' non trovato");
				}
			}
			else {
				for (Fruitore fruCheck : asps.getFruitoreList()) {
					if(fruCheck.getTipo().equals(soggFruitore.getTipo()) &&
							fruCheck.getNome().equals(soggFruitore.getNome())) {
						fru = fruCheck;
						break;
					}
				}
				if(fru==null) {
					throw new Exception("Fruitore con id '"+soggFruitore.getTipo()+"/"+soggFruitore.getNome()+"' non trovato");
				}
			}
			Long idSoggettoLong = fru.getIdSoggetto();
			if(idSoggettoLong==null) {
				idSoggettoLong = soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo());
			}
			
			List<String> listaAzioni  = portaDelegata.getAzione().getAzioneDelegataList();
			
			Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
			Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fru.getId() + "");				
			Parameter actionIdPorta = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA,portaDelegata.getId()+"");
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			List<Parameter> listParameter = new ArrayList<>();
			listParameter.add(pId);
			listParameter.add(pMyId);
			listParameter.add(pIdSoggettoErogatore);
			listParameter.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + ""));
			listParameter.add(actionIdPorta);
			if(listaAzioni!=null && !listaAzioni.isEmpty()) {
				Parameter action = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE,listaAzioni.get(0));
				listParameter.add(action);
			}
			
			String servletConnettore = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE;
			Parameter[] parametriServletConnettore =listParameter.toArray(new Parameter[listParameter.size()]); 
			
			if(	porteDelegateHelper.isEditModeInProgress()){
				
				if(modalita == null) {
					modalita = PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO;
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,true,servletConnettore,parametriServletConnettore);
				
				pd.setDati(dati);
				
				if(modalita.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO)) {
					pd.disableOnlyButton();
				}
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				// Forward control to the specified success URI
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO, 
						ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.connettoreDefaultRidefinitoCheckData(TipoOperazione.OTHER, modalita);
			
			if(!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addConnettoreDefaultRidefinitoToDati(dati,TipoOperazione.OTHER, modalita, modalitaValues,modalitaLabels,false,servletConnettore,parametriServletConnettore);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO, ForwardParams.OTHER(""));
			}
			
			
			
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(idSoggettoFruitore.getTipo()) && fruitoreCheck.getNome().equals(idSoggettoFruitore.getNome())) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			if(fruitore==null) {
				throw new Exception("Fruitore con id '"+idSoggettoFruitore.getTipo()+"/"+idSoggettoFruitore.getNome()+"' non trovato");
			}
			for (int i = 0; i < fruitore.sizeConfigurazioneAzioneList(); i++) {
				ConfigurazioneServizioAzione confAz = fruitore.getConfigurazioneAzione(i);
				if(confAz.getAzioneList().contains(portaDelegata.getAzione().getAzioneDelegata(0))) {
					fruitore.removeConfigurazioneAzione(i);
					break;
				}
			}
			
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(portaDelegata.getNome());
			UpdateProprietaOggetto updateProprietaOggetto = new UpdateProprietaOggetto(idPD, userLogin);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), asps, updateProprietaOggetto);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);


			List<PortaDelegata> lista = null;
			int idLista = -1;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				
				int idAspsInt = Integer.parseInt(idAsps);
				AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
				
				if(datiInvocazione) {
					
					if(gestioneFruitori) {
						
						idLista = Liste.SERVIZI;
						
						ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
						
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
						
						String superUser =   ServletUtils.getUserLoginFromSession(session); 
						PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
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
					
					List<MappingFruizionePortaDelegata> listaMapping = apsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio, ricerca);
					apsHelper.serviziFruitoriMappingList(listaMapping, idAsps, idSoggFruitore, idSoggettoFruitore, idFruizione, ricerca); 
				}
				
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(Integer.parseInt(idSoggFruitore), ricerca); 
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
			
			ForwardParams fwP = ForwardParams.OTHER("");
			if(!porteDelegateHelper.isModalitaCompleta()) {
				fwP = PorteDelegateCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}			
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO, fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO,	ForwardParams.OTHER(""));
		}  
	}
}



