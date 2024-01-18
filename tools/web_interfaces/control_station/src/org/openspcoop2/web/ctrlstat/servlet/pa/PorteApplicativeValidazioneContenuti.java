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

package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
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
 * PorteApplicativeValidazioneContenuti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeValidazioneContenuti extends Action {

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
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			String applicaModificaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String statoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
			String tipoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			String applicaMTOM = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);

			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore aspcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), pa.getServizio().getVersione());
			long idServizioLong = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idServizioLong, false);
			AccordoServizioParteComuneSintetico aspc = aspcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			if(	porteApplicativeHelper.isEditModeInProgress() && !applicaModifica){

				if (statoValidazione == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
					if (vx == null) {
						statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
					} else {
						if(vx.getStato()!=null)
							statoValidazione = vx.getStato().toString();
						if ((statoValidazione == null) || "".equals(statoValidazione)) {
							statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
					if (vx == null) {
						tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
					} else {
						if(vx.getTipo()!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(vx.getStato()))
							tipoValidazione = vx.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE ;
						}
					}
				}
				if (applicaMTOM == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
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

				porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.validazioneContenutiCheck(TipoOperazione.OTHER, isPortaDelegata);

			if (!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
						ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI,
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

			pa.setValidazioneContenutiApplicativi(vx);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			pa = porteApplicativeCore.getPortaApplicativa(idInt);

			if (statoValidazione == null) {
				vx = pa.getValidazioneContenutiApplicativi();
				if (vx == null) {
					statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
				} else {
					if(vx.getStato()!=null)
						statoValidazione = vx.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
					}
				}
			}
			if (tipoValidazione == null) {
				vx = pa.getValidazioneContenutiApplicativi();
				if (vx == null) {
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
				} else {
					if(vx.getTipo()!=null)
						tipoValidazione = vx.getTipo().toString();
					if (tipoValidazione == null || "".equals(tipoValidazione)) {
						tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE ;
					}
				}
			}
			if (applicaMTOM == null) {
				vx = pa.getValidazioneContenutiApplicativi();
				applicaMTOM = "";
				if (vx != null &&
					vx.getAcceptMtomMessage()!=null &&
						vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) { 
							applicaMTOM = Costanti.CHECK_BOX_ENABLED;
				}
			}

			porteApplicativeHelper.validazioneContenuti(TipoOperazione.OTHER,dati, isPortaDelegata, statoValidazione, tipoValidazione, applicaMTOM,
					ServiceBinding.valueOf(aspc.getServiceBinding().name()), aspc.getFormatoSpecifica());

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,idAsps, dati);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, 
					ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI , 
					ForwardParams.OTHER(""));
		} 
	}			
}
