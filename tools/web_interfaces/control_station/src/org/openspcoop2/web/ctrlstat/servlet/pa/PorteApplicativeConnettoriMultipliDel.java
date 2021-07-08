/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeConnettoriMultipliDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);



		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			// Elimino le properties della porta applicativa dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			String idConnTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isNotEmpty(idConnTab)) {
				ServletUtils.setObjectIntoSession(session, idConnTab, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			}

			String nome = "";
			String actionConferma = porteApplicativeHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			// Prendo la porta applicativa
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			long idAspsLong = Long.parseLong(idAsps);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());

			List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
			boolean eseguiOperazione = true;

			boolean connettoreUtilizzatiConfig = false;
			List<String> messaggiSezioniConnettore = new ArrayList<String>();
			int numeroElementiDaControllare = idsToRemove.size();
			for (int i = 0; i < idsToRemove.size(); i++) {
				nome = idsToRemove.get(i);
			
				connettoreUtilizzatiConfig = porteApplicativeHelper. isConnettoreMultiploInUso(numeroElementiDaControllare, nome, pa, asps, apc, serviceBinding, messaggiSezioniConnettore);
			} // end for elementi selezionati

			StringBuilder sbErrore = new StringBuilder();
			if(connettoreUtilizzatiConfig) {
				if(idsToRemove.size() > 1) {
					sbErrore.append(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_ELIMINARE_I_CONNETTORI_UTILIZZATI_IN_CONFIGURAZIONE);
				} else {
					sbErrore.append(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_ELIMINARE_IL_CONNETTORE_UTILIZZATI_IN_CONFIGURAZIONE);
				}
				sbErrore.append(":").append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				for (String s : messaggiSezioniConnettore) {
					sbErrore.append(s);
				}

				eseguiOperazione = false;
			}

			// se non devo visualizzare il messaggio di errore procedo ai controlli successivi 
			if(!connettoreUtilizzatiConfig) {
				int numeroAbilitati = 0;
				for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
					PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(j);

					boolean toDel = false;
					for (int i = 0; i < idsToRemove.size(); i++) {
						nome = idsToRemove.get(i);
						if (nome.equals(paSA.getNome())) {
							toDel = true;
							break;
						}
					}

					if(!toDel) {
						boolean abilitato = paSA.getDatiConnettore() != null ? paSA.getDatiConnettore().getStato().equals(StatoFunzionalita.ABILITATO) : true;

						if(abilitato)
							numeroAbilitati ++;
					}
				}


				if(numeroAbilitati < 1) {
					eseguiOperazione = false;
					if(idsToRemove.size() > 1) {
						sbErrore.append(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_ELIMINARE_I_CONNETTORI_DEVE_RIMANARE_ALMENTO_UN_CONNETTORE_ABILITATO);
					} else {
						sbErrore.append(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_ELIMINARE_IL_CONNETTORE_DEVE_RIMANARE_ALMENTO_UN_CONNETTORE_ABILITATO);
					}
				}
			}

			if(!eseguiOperazione) {
				if(actionConferma == null) {
					String messaggio = sbErrore.toString();
					String title = "Attenzione";
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_CHIUDI, 
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

							}
					};
					pd.setBottoni(bottoni );
					pd.setMessage(messaggio, title, MessageType.CONFIRM);
				}
			}


			if(eseguiOperazione) {
				for (int i = 0; i < idsToRemove.size(); i++) {

					// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
					// .elementAt(idToRemove[i])).elementAt(0);
					// nome = de.getValue();
					nome = idsToRemove.get(i);

					for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
						PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(j);
						if (nome.equals(paSA.getNome()) && !porteApplicativeHelper.isConnettoreDefault(paSA)) {
							pa.removeServizioApplicativo(j);

							// se non sto utilizzando un SA Server lo elimino
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setNome(paSA.getNome());
							IDSoggetto idSoggettoProprietario = new IDSoggetto();
							idSoggettoProprietario.setTipo(pa.getTipoSoggettoProprietario());
							idSoggettoProprietario.setNome(pa.getNomeSoggettoProprietario());
							idSA.setIdSoggettoProprietario(idSoggettoProprietario );
							ServizioApplicativo sa = saCore.getServizioApplicativo(idSA);

							if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
								listaOggettiDaEliminare.add(sa);
							}

							break;
						}
					}
				}


				String userLogin = ServletUtils.getUserLoginFromSession(session);

				porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
				
				if(!listaOggettiDaEliminare.isEmpty())
					porteApplicativeCore.performDeleteOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaEliminare.toArray(new Object[listaOggettiDaEliminare.size()]));

			} 

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// ricarico la configurazione
			pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			porteApplicativeHelper.preparePorteAppConnettoriMultipliList(pa.getNome(), ricerca, pa, true);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
					ForwardParams.DEL());
		}  
	}
}
