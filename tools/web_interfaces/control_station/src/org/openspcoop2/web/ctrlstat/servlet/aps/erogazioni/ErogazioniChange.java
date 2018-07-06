/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ErogazioniChange
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14207 $, $Date: 2018-06-25 10:55:15 +0200 (Mon, 25 Jun 2018) $
 * 
 */
public final class ErogazioniChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;

		try {
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idInt  = Long.parseLong(id);

			apsHelper.makeMenu();

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = true;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			String tmpTitle = gestioneFruitori ? apsHelper.getLabelIdServizio(idServizio) :  apsHelper.getLabelIdServizioSenzaErogatore(idServizio);
			
			String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			AccordoServizioParteComune as = apcCore.getAccordoServizio(asps.getIdAccordo());
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = new ArrayList<>();
			List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
			if(gestioneErogatori) {
				// lettura delle configurazioni associate
				listaMappingErogazionePortaApplicativa = apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
				for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
					listaPorteApplicativeAssociate.add(porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
				}
			}

			List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = new ArrayList<>();
			List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();
			Fruitore fruitore = null;
			if(gestioneFruitori) {
				// In questa modalità ci deve essere solo un fruitore
				fruitore = asps.getFruitore(0);
				IDSoggetto idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
				listaMappingFruzionePortaDelegata = apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
				for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
					listaPorteDelegateAssociate.add(porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
				}
			}

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			else {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			lstParm.add(new Parameter(tmpTitle, null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );

//			if (apsHelper.isEditModeInProgress()) {

				// preparo i campi
				Vector<Vector<DataElement>> datiPagina = new Vector<Vector<DataElement>>();
				Vector<DataElement> dati = new Vector<DataElement>();
				datiPagina.add(dati);
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				datiPagina = apsHelper.addErogazioneToDati(datiPagina, tipoOp, asps, as, tipoProtocollo, serviceBinding, gestioneErogatori, gestioneFruitori, listaMappingErogazionePortaApplicativa, listaPorteApplicativeAssociate, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate, fruitore);

				pd.setDati(datiPagina);
				pd.disableEditMode();

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

//				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
//			}


			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
		}  

	}
}