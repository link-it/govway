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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ErogazioniVerificaCertificati
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniVerificaCertificati  extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.OTHER;
		
		try {
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			
			String verificaCertificatiFromLista = apsHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
			
			boolean soloModI = false;
			if(!arrivoDaLista) {
				String par = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
				soloModI = "true".equalsIgnoreCase(par);
			}
						
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idInt  = Long.parseLong(id);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
			String idsogg = apsHelper.getParameter(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
			
			String tipoSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			IDSoggetto idSoggettoFruitore = null;
			if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
					nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			String alias = apsHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			Fruitore fruitore = null;
			String idFruizione = null;
			if(gestioneFruitori) {
				// In questa modalità ci deve essere un fruitore indirizzato
				for (Fruitore check : asps.getFruitoreList()) {
					if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
						fruitore = check;
						break;
					}
				}
			}
			if(fruitore!=null) {
				idFruizione = fruitore.getId()+"";
			}
			
			// Preparo il menu
			apsHelper.makeMenu();
						
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			// setto la barra del titolo
			ErogazioniVerificaCertificati.impostaBarraDelTitolo(pd, apsHelper, arrivoDaLista, asps, tipoSoggettoFruitore, nomeSoggettoFruitore,
					idSoggettoFruitore, gestioneFruitori, idServizio, tipoProtocollo);
			
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			// Esecuzione delle operazioni di verifica
			boolean verificaCertificatiEffettuata = apsHelper.eseguiVerificaCertificati(soloModI, asps, idSoggettoFruitore, alias, gestioneFruitori,
					gestioneErogatori, fruitore, idServizio, tipoProtocollo, dati);
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			
			if(idSoggettoFruitore != null) {
				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, idsogg, id, asps.getId()+"", idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);
			}else {
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, asps.getId()+"", null, null, dati);
			}

			DataElement	de = new DataElement();
			de.setValue(arrivoDaLista+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			dati.add(de);
			
			if(soloModI) {
				de = new DataElement();
				de.setValue(soloModI+"");
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
				dati.add(de);
			}
			
			pd.setDati(dati);

			if(verificaCertificatiEffettuata) {
			
				// verifica richiesta dal link nella lista, torno alla lista
				if(arrivoDaLista) {
					
					String userLogin = ServletUtils.getUserLoginFromSession(session);	
					
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
					int idLista = Liste.SERVIZI;
					
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<AccordoServizioParteSpecifica> lista = null;
					if(!ServletUtils.isSearchDone(apsHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  AccordoServizioParteSpecifica.class);
					}
					
					ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
					
					apsHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
										
					apsHelper.checkGestione(request, session, ricerca, idLista, tipologia,true);
					
					// preparo lista
					boolean [] permessi = AccordiServizioParteSpecificaUtilities.getPermessiUtente(apsHelper);
					
					if(lista==null) {
						if(apsCore.isVisioneOggettiGlobale(userLogin)){
							lista = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
						}else{
							lista = apsCore.soggettiServizioList(userLogin, ricerca,permessi, gestioneFruitori, gestioneErogatori);
						}
					}

					
					if(!apsHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
					
					apsHelper.prepareErogazioniList(ricerca, lista);
					
					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, CostantiControlStation.TIPO_OPERAZIONE_VERIFICA_CERTIFICATI);
				}
				
				// verifica richiesta dal dettaglio, torno al dettaglio
				else { 
					apsHelper.prepareErogazioneChange(tipoOp, asps, idSoggettoFruitore);
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
				}
				
			}
			else {
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
				
			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
	
	public static void impostaBarraDelTitolo(PageData pd, ErogazioniHelper apsHelper, boolean arrivoDaLista,
			AccordoServizioParteSpecifica asps, String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			IDSoggetto idSoggettoFruitore, boolean gestioneFruitori, IDServizio idServizio, String tipoProtocollo)
			throws DriverControlStationException {
		String tmpTitle = apsHelper.getLabelServizio(idSoggettoFruitore, gestioneFruitori, idServizio, tipoProtocollo);
		
		// setto la barra del titolo
		List<Parameter> listParameterChange = new ArrayList<>();
		Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
		Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
		Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
		Parameter pTipoSoggettoFruitore = null;
		Parameter pNomeSoggettoFruitore = null;
		if(gestioneFruitori) {
			pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
			pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
		}
		
		listParameterChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
		listParameterChange.add(pNomeServizio);
		listParameterChange.add(pTipoServizio);
		listParameterChange.add(pIdsoggErogatore);
		
		List<Parameter> lstParm = new ArrayList<>();

		if(gestioneFruitori) {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			
			listParameterChange.add(pTipoSoggettoFruitore);
			listParameterChange.add(pNomeSoggettoFruitore);
		}
		else {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		if(arrivoDaLista) {
			String labelVerifica = ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI_DI  + tmpTitle;
			lstParm.add(new Parameter(labelVerifica, null));
		}
		else {
			lstParm.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listParameterChange.toArray(new Parameter[listParameterChange.size()])));
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI, null));
		}

		// setto la barra del titolo
		ServletUtils.setPageDataTitle(pd, lstParm );
	}

	

}
