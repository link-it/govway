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

package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**     
 * PorteDelegateValidazioneContenuti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateValidazioneContenuti extends Action {

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
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";

			String statoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
			String tipoValidazione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			String applicaMTOM = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);

			String applicaModificaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore aspcCore = new AccordiServizioParteComuneCore(porteDelegateCore);

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();

			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(), portaDelegata.getServizio().getNome(), 
					portaDelegata.getSoggettoErogatore().getTipo(), portaDelegata.getSoggettoErogatore().getNome(), portaDelegata.getServizio().getVersione());
			long idServizioLong = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idServizioLong, false);
			AccordoServizioParteComuneSintetico aspc = aspcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);

			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			if(	porteDelegateHelper.isEditModeInProgress() && !applicaModifica){

				if (statoValidazione == null) {
					ValidazioneContenutiApplicativi vx = portaDelegata.getValidazioneContenutiApplicativi();
					if (vx == null) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					} else {
						if(vx.getStato()!=null)
							statoValidazione = vx.getStato().toString();
						if ((statoValidazione == null) || "".equals(statoValidazione)) {
							statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					ValidazioneContenutiApplicativi vx = portaDelegata.getValidazioneContenutiApplicativi();
					if (vx == null) {
						tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
					} else {
						if(vx.getTipo()!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(vx.getStato()))
							tipoValidazione = vx.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
						}
					}
				}
				if (applicaMTOM == null) {
					ValidazioneContenutiApplicativi vx = portaDelegata.getValidazioneContenutiApplicativi();
					applicaMTOM = "";
					if (vx != null &&
						vx.getAcceptMtomMessage()!=null &&
							vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) { 
						applicaMTOM = Costanti.CHECK_BOX_ENABLED;
					}
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.validazioneContenutiCheck(TipoOperazione.OTHER, isPortaDelegata);

			if (!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI,
						ForwardParams.OTHER(""));
			}

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			vx.setStato(StatoFunzionalitaConWarning.toEnumConstant(statoValidazione));
			vx.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vx.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vx.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vx.setAcceptMtomMessage(null);

			portaDelegata.setValidazioneContenutiApplicativi(vx);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);

			if (statoValidazione == null) {
				vx = portaDelegata.getValidazioneContenutiApplicativi();
				if (vx == null) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				} else {
					if(vx.getStato()!=null)
						statoValidazione = vx.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					}
				}
			}
			if (tipoValidazione == null) {
				vx = portaDelegata.getValidazioneContenutiApplicativi();
				if (vx == null) {
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE;
				} else {
					if(vx.getTipo()!=null)
						tipoValidazione = vx.getTipo().toString();
					if (tipoValidazione == null || "".equals(tipoValidazione)) {
						tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_INTERFACE ;
					}
				}
			}
			if (applicaMTOM == null) {
				vx = portaDelegata.getValidazioneContenutiApplicativi();
				applicaMTOM = "";
				if (vx != null &&
					vx.getAcceptMtomMessage()!=null &&
						vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) { 
					applicaMTOM = Costanti.CHECK_BOX_ENABLED;
				}
			}

			porteDelegateHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
					ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));
		} 
	}
}
