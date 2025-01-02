/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.Map;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * PorteDelegateAzioneAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAzioneAdd extends Action {

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
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String idPorta = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			int idFruizioneInt = Integer.parseInt(idFruizione);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			// multiselect
			String[] azionis = porteDelegateHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			int idServizio = -1;
			AccordoServizioParteSpecifica asps = null;
			if(!idAsps.equals("")) {
				idServizio = Integer.parseInt(idAsps);
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			} else {
				PortaDelegataServizio pds = portaDelegata.getServizio();
				  idServizio = -1;
				String tipoServizio = null;
				String nomeServizio = null;
				Integer versioneServizio = null;
				if (pds != null) {
					idServizio = pds.getId().intValue();
					tipoServizio = pds.getTipo();
					nomeServizio = pds.getNome();
					versioneServizio = pds.getVersione();
				}
				
				if (idServizio <= 0) {
				PortaDelegataSoggettoErogatore soggettoErogatore = portaDelegata.getSoggettoErogatore();
				idServizio = (int) apsCore.getIdAccordoServizioParteSpecifica(nomeServizio, tipoServizio, versioneServizio, soggettoErogatore.getNome(), soggettoErogatore.getTipo()); 
				}
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			}
			
			AccordoServizioParteComuneSintetico aspc = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
			IDSoggetto idSoggettoFruitore = soggettiCore.getIdSoggettoRegistro(soggInt); 
			
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList((long)idFruizioneInt, idSoggettoFruitore, idServizio2, null);
			List<String> azioniOccupate = new ArrayList<>();
			int listaMappingFruizioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
			if(listaMappingFruizioneSize > 0) {
				for (int i = 0; i < listaMappingFruizione.size(); i++) {
					MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
					// colleziono le azioni gia' configurate
					PortaDelegata portaDelegataTmp = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
					if(portaDelegataTmp.getAzione() != null && portaDelegataTmp.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaDelegataTmp.getAzione().getAzioneDelegataList());
				}
			}
			
			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			Map<String,String> azioni = porteDelegateCore.getAzioniConLabel(asps, aspc, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			if(azioni!=null && azioni.size()>0) {
				azioniDisponibiliList = new String[azioni.size()];
				azioniDisponibiliLabelList = new String[azioni.size()];
				int i = 0;
				for (String string : azioni.keySet()) {
					azioniDisponibiliList[i] = string;
					azioniDisponibiliLabelList[i] = azioni.get(string);
					i++;
				}
			}

 
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						porteDelegateHelper.getLabelAzioniDi(serviceBinding),
						porteDelegateHelper.getLabelAzioni(serviceBinding),
						portaDelegata);
			}
			else {
				labelPerPorta = porteDelegateHelper.getLabelAzioniDi(serviceBinding)+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione)			
					));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					// si controlla 1 poiche' c'e' il trattino nelle azioni disponibili
					pd.setMessage(porteDelegateHelper.getLabelAllAzioniConfigurate(serviceBinding), Costanti.MESSAGE_TYPE_INFO);
					pd.disableEditMode();
				}
				else {
					dati = porteDelegateHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList,azioniDisponibiliLabelList, azionis, serviceBinding);
					dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, 
							idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				}
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelAzioneCheckData(TipoOperazione.ADD,azioniOccupate,listaMappingFruizione);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList,azioniDisponibiliLabelList, azionis, serviceBinding);
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
						ForwardParams.ADD());
			}

			List<Object> listaOggettiDaModificare = new ArrayList<>();
			
			String azioneGiaEsistente = portaDelegata.getAzione().getAzioneDelegata(0); // prendo la prima
			
			// aggiungo azione nel db
			for(String azione: azionis) {
				portaDelegata.getAzione().addAzioneDelegata(azione);
			}
			listaOggettiDaModificare.add(portaDelegata);

			boolean updateASPS = false;
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(portaDelegata.getTipoSoggettoProprietario()) && fruitoreCheck.getNome().equals(portaDelegata.getNomeSoggettoProprietario())) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			if(fruitore==null) {
				throw new Exception("Fruitore con id '"+portaDelegata.getTipoSoggettoProprietario()+"/"+portaDelegata.getNomeSoggettoProprietario()+"' non trovato");
			}
			for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
				ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
				if(config!=null &&
						config.getAzioneList().contains(azioneGiaEsistente)) {
					for(String azione: azionis) {
						config.addAzione(azione);
					}
					updateASPS = true;
					break;
				}
			}
			if(updateASPS) {
				listaOggettiDaModificare.add(asps);
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());

			// ricarico la pd
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			portaDelegata = porteDelegateCore.getPortaDelegata(idPD );
			List<String> listaAzioni = portaDelegata.getAzione().getAzioneDelegataList();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			int idLista = Liste.PORTE_DELEGATE_AZIONI;
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));
			lstParam =  porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			// imposto menu' contestuale
			porteDelegateHelper.impostaComandiMenuContestualePD(idsogg, idAsps, idFruizione);
			
			porteDelegateHelper.preparePorteAzioneList(ricerca,
					listaAzioni, parentPD, lstParam, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					listaParametriSessione, labelPerPorta, serviceBinding, aspc);
			

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
					ForwardParams.ADD());
		} 
	}
}
