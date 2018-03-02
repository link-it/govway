/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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

		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			int idFruizioneInt = Integer.parseInt(idFruizione);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			// multiselect
			String[] azionis = porteDelegateHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			int idServizio = -1;
			AccordoServizioParteSpecifica asps = null;
			if(!idAsps.equals("")) {
				idServizio = Integer.parseInt(idAsps);
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			} else {
				// long idPorta = pa.getId();
				PortaDelegataServizio pds = portaDelegata.getServizio();
				  idServizio = -1;
				String tipo_servizio = null;
				String nome_servizio = null;
				Integer versione_servizio = null;
				if (pds != null) {
					idServizio = pds.getId().intValue();
					tipo_servizio = pds.getTipo();
					nome_servizio = pds.getNome();
					versione_servizio = pds.getVersione();
				}
				
				if (idServizio <= 0) {
				PortaDelegataSoggettoErogatore soggettoErogatore = portaDelegata.getSoggettoErogatore();
				idServizio = (int) apsCore.getIdAccordoServizioParteSpecifica(nome_servizio, tipo_servizio, versione_servizio, soggettoErogatore.getNome(), soggettoErogatore.getTipo()); 
				}
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			}
			
			AccordoServizioParteComune aspc = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
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
			List<String> azioni = porteDelegateCore.getAzioni(asps, aspc, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			if(azioni!=null && azioni.size()>0) {
				azioniDisponibiliList = new String[azioni.size()];
				for (int i = 0; i < azioni.size(); i++) {
					azioniDisponibiliList[i] = "" + azioni.get(i);
				}
			}

 
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONI_CONFIG_DI+
						porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_AZIONI_CONFIG_DI+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps),
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione)			
					));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					// si controlla 1 poiche' c'e' il trattino nelle azioni disponibili
					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_AZIONI_COMPLETATA, Costanti.MESSAGE_TYPE_INFO);
					pd.disableEditMode();
				}
				else {
					dati = porteDelegateHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList,azionis);
					dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, idFruizione, dati);
				}
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteAppAzioneCheckData(TipoOperazione.ADD,azioniOccupate);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList,azionis);
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
						ForwardParams.ADD());
			}

			// aggiungo azione nel db
			for(String azione: azionis) {
				portaDelegata.getAzione().addAzioneDelegata(azione);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// ricarico la pd
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
			portaDelegata = porteDelegateCore.getPortaDelegata(idPD );
			List<String> listaAzioni = portaDelegata.getAzione().getAzioneDelegataList();
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps));
			listaParametriSessione.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione));
			
			lstParam =  porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			porteDelegateHelper.preparePorteAzioneList(listaAzioni, idPorta, parentPD, lstParam, nomePorta, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					listaParametriSessione, labelPerPorta);
			

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE,
					ForwardParams.ADD());
		} 
	}
}
