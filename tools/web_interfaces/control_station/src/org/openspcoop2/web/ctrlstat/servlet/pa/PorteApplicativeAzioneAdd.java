/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
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

/**
 * PorteApplicativeAzioneAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeAzioneAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			// multiselect
			String[] azionis = porteApplicativeHelper.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			int idServizio = -1;
			AccordoServizioParteSpecifica asps = null;
			if(!idAsps.equals("")) {
				idServizio = Integer.parseInt(idAsps);
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			} else {
				// long idPorta = pa.getId();
				PortaApplicativaServizio pas = pa.getServizio();
				  idServizio = -1;
				String tipo_servizio = null;
				String nome_servizio = null;
				Integer versione_servizio = null;
				if (pas != null) {
					idServizio = pas.getId().intValue();
					tipo_servizio = pas.getTipo();
					nome_servizio = pas.getNome();
					versione_servizio = pas.getVersione();
				}
				PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
				long id_soggetto_virtuale = -1;
				String tipo_soggetto_virtuale = null;
				String nome_soggetto_virtuale = null;
				if (pasv != null) {
					id_soggetto_virtuale = pasv.getId();
					tipo_soggetto_virtuale = pasv.getTipo();
					nome_soggetto_virtuale = pasv.getNome();
				}
				// Recupero eventuale idServizio mancante
				if(porteApplicativeCore.isRegistroServiziLocale()){
					if (idServizio <= 0) {
						long idSoggettoServizio = -1;
						if (id_soggetto_virtuale > 0) {
							idSoggettoServizio = id_soggetto_virtuale;
						} else if (tipo_soggetto_virtuale != null && (!("".equals(tipo_soggetto_virtuale))) && nome_soggetto_virtuale != null && (!("".equals(nome_soggetto_virtuale)))) {
							idSoggettoServizio = soggettiCore.getIdSoggetto(nome_soggetto_virtuale, tipo_soggetto_virtuale);
						} else {
							idSoggettoServizio = soggInt;
						}
						Soggetto soggServ = soggettiCore.getSoggetto(idSoggettoServizio);
						idServizio = (int) apsCore.getIdAccordoServizioParteSpecifica(nome_servizio, tipo_servizio, versione_servizio, soggServ.getNome(), soggServ.getTipo()); 
					}
				}
				asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			}
			
			AccordoServizioParteComune aspc = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
			
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			List<MappingErogazionePortaApplicativa> listaMappingErogazione = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), null);
			List<String> azioniOccupate = new ArrayList<>();
			int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
			if(listaMappingErogazioneSize > 0) {
				for (int i = 0; i < listaMappingErogazione.size(); i++) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
					// colleziono le azioni gia' configurate
					PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
					if(portaApplicativa.getAzione() != null && portaApplicativa.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaApplicativa.getAzione().getAzioneDelegataList());
				}
			}
			
			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			Map<String,String> azioni = porteApplicativeCore.getAzioniConLabel(asps, aspc, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
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

 
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						porteApplicativeHelper.getLabelAzioniDi(serviceBinding),
						porteApplicativeHelper.getLabelAzioni(serviceBinding),
						pa);
			}
			else {
				labelPerPorta = porteApplicativeHelper.getLabelAzioniDi(serviceBinding)+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)					
					));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					// si controlla 1 poiche' c'e' il trattino nelle azioni disponibili
					pd.setMessage(porteApplicativeHelper.getLabelAllAzioniConfigurate(serviceBinding), Costanti.MESSAGE_TYPE_INFO);
					pd.disableEditMode();
				}
				else {
					dati = porteApplicativeHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList, azioniDisponibiliLabelList, azionis, serviceBinding);
					dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);
				}
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppAzioneCheckData(TipoOperazione.ADD,azioniOccupate);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addPorteAzioneToDati(TipoOperazione.ADD,dati, "", azioniDisponibiliList, azioniDisponibiliLabelList, azionis, serviceBinding);
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,  dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE, 
						ForwardParams.ADD());
			}

			// aggiungo azione nel db
			for(String azione: azionis) {
				pa.getAzione().addAzioneDelegata(azione);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// ricarico la pa
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
			pa = porteApplicativeCore.getPortaApplicativa(idPA );
			List<String> listaAzioni = pa.getAzione().getAzioneDelegataList();
			List<Parameter> listaParametriSessione = new ArrayList<>();
			listaParametriSessione.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			listaParametriSessione.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			listaParametriSessione.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			
			lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			porteApplicativeHelper.preparePorteAzioneList(listaAzioni, idPorta, parentPA, lstParam, nomePorta, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE, 
					listaParametriSessione, labelPerPorta, serviceBinding, aspc);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_AZIONE,
					ForwardParams.ADD());
		} 
	}
}
