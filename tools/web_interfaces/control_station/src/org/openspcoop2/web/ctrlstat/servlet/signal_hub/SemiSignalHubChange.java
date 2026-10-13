/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.signal_hub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.pdd.config.DigestServiceParams;
import org.openspcoop2.pdd.config.DigestServiceParamsRotationRequest;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.remote_stores.RemoteStoresCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Dettaglio del seme di pseudoanonimizzazione Signal Hub di un'erogazione, con lo storico dei semi precedenti
 * e l'azione di rotazione forzata del seme in uso
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemiSignalHubChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);
		
		try {
			SemiSignalHubHelper helper = new SemiSignalHubHelper(request, pd, session);

			SemiSignalHubCore core = new SemiSignalHubCore();

			// Preparo il menu
			helper.makeMenu();
			
			String idS = helper.getParameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SERVIZIO);
			long idServizioRef = Long.parseLong(idS);
			
			List<DigestServiceParams> storico = leggiStorico(core, idServizioRef);
			DigestServiceParams inUso = storico.get(0);
			
			boolean rotazione = Costanti.CHECK_BOX_ENABLED_TRUE.equals(helper.getParameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ROTAZIONE));
			String actionConferma = helper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			boolean confermaRotazione = false;
			
			if(rotazione) {
				if(actionConferma == null) {
					// richiedo la conferma dell'operazione
					confermaRotazione = richiediConferma(helper, pd, inUso);
				}
				else if(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK.equals(actionConferma) &&
						eseguiRotazione(helper, core, pd, userLogin, idServizioRef, inUso)) {
					storico = leggiStorico(core, idServizioRef);
					inUso = storico.get(0);
					pd.setMessage(SemiSignalHubHelper.getMessaggioRotazioneRichiesta(inUso, SemiSignalHubCore.getSeedCacheRefreshSeconds()), MessageType.INFO);
				}
				// annullata: viene visualizzato il dettaglio
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_CACHE_PDND),
					new Parameter(SemiSignalHubCostanti.LABEL_SIGNAL_HUB_SEMI, SemiSignalHubCostanti.SERVLET_NAME_SIGNAL_HUB_SEMI_LIST),
					new Parameter(helper.getLabelServizio(inUso.getIdServizio()), null));
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			String eServiceId = core.getEServiceId(idServizioRef);
			String[] ultimoSignalId = leggiUltimoSignalId(core, eServiceId);
			
			dati = helper.addSemiSignalHubToDati(idServizioRef, storico, eServiceId, ultimoSignalId, confermaRotazione, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SemiSignalHubCostanti.OBJECT_NAME_SIGNAL_HUB_SEMI, ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					SemiSignalHubCostanti.OBJECT_NAME_SIGNAL_HUB_SEMI, ForwardParams.CHANGE());
		}
	}
	
	private static List<DigestServiceParams> leggiStorico(SemiSignalHubCore core, long idServizioRef) throws DriverControlStationException {
		List<DigestServiceParams> storico = core.getStorico(idServizioRef);
		if(storico.isEmpty()) {
			throw new IllegalArgumentException(SemiSignalHubCostanti.MESSAGGIO_SEME_NON_PRESENTE);
		}
		return storico;
	}
	
	/**
	 * Ritorna l'ultimo signalId generato per l'e-service [0] e, se sono configurate piu' sorgenti dati runtime,
	 * l'etichetta della sorgente in cui e' stato trovato [1]; null se le basi dati runtime non sono consultabili
	 */
	private static String[] leggiUltimoSignalId(SemiSignalHubCore core, String eServiceId) throws DriverControlStationException {
		// il progressivo dei signalId e' mantenuto nella base dati runtime, associato all'e-service:
		// vengono consultate le sorgenti dati runtime configurate, fermandosi alla prima che contiene il progressivo
		if(eServiceId==null) {
			return null;
		}
		List<String> sorgenti = core.getSorgentiDatiRuntime();
		if(sorgenti==null || sorgenti.isEmpty()) {
			return null;
		}
		boolean piuSorgenti = sorgenti.size()>1;
		List<String> labelSorgenti = piuSorgenti ? core.getLabelSorgentiDatiRuntime() : null;
		boolean letturaRiuscita = false;
		for (int i = 0; i < sorgenti.size(); i++) {
			String sorgente = sorgenti.get(i);
			try {
				Long ultimo = core.getUltimoSignalId(eServiceId, sorgente);
				letturaRiuscita = true;
				if(ultimo!=null) {
					String labelSorgente = null;
					if(piuSorgenti) {
						labelSorgente = labelSorgenti!=null && labelSorgenti.size()>i ? labelSorgenti.get(i) : sorgente;
					}
					return new String[] {ultimo.toString(), labelSorgente};
				}
			}catch(Exception e) {
				ControlStationCore.logError("Lettura dell'ultimo signalId dell'e-service '"+eServiceId+"' (sorgente: "+sorgente+") non riuscita: "+e.getMessage(), e);
			}
		}
		return new String[] {letturaRiuscita ? SemiSignalHubCostanti.VALUE_ULTIMO_SIGNAL_ID_NON_GENERATO : SemiSignalHubCostanti.VALUE_ULTIMO_SIGNAL_ID_NON_DISPONIBILE, null};
	}
	
	private static boolean richiediConferma(SemiSignalHubHelper helper, PageData pd, DigestServiceParams inUso) {
		if(inUso.getDataRichiestaRotazione()!=null) {
			pd.setMessage(SemiSignalHubCostanti.MESSAGGIO_ROTAZIONE_GIA_RICHIESTA, MessageType.INFO);
			return false;
		}
		pd.setMessage(helper.getMessaggioConfermaRotazione(inUso, SemiSignalHubCore.getSeedCacheRefreshSeconds()), MessageType.CONFIRM);
		String[][] bottoni = { 
				{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
					Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
					Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX
				},
				{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
					Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
					Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};
		pd.setBottoni(bottoni);
		return true;
	}
	
	private static boolean eseguiRotazione(SemiSignalHubHelper helper, SemiSignalHubCore core, PageData pd, String userLogin, 
			long idServizioRef, DigestServiceParams inUso) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, 
			DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, UtilsException {
		long idSeme = Long.parseLong(helper.getParameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SEME));
		
		if(inUso.getId().longValue() != idSeme) {
			pd.setMessage(SemiSignalHubCostanti.MESSAGGIO_SEME_GIA_RUOTATO, MessageType.ERROR);
			return false;
		}
		if(inUso.getDataRichiestaRotazione()!=null) {
			pd.setMessage(SemiSignalHubCostanti.MESSAGGIO_ROTAZIONE_GIA_RICHIESTA, MessageType.INFO);
			return false;
		}
		
		DigestServiceParamsRotationRequest richiesta = new DigestServiceParamsRotationRequest();
		richiesta.setIdServizioRef(idServizioRef);
		richiesta.setServizio(inUso.getIdServizio().toString());
		richiesta.setIdSeme(idSeme);
		richiesta.setSignalId(inUso.getSerialNumber());
		richiesta.setDataGenerazioneSeme(Date.from(inUso.getDataRegistrazione()));
		richiesta.setDataRichiesta(DateManager.getDate());
		
		// l'operazione viene registrata nell'audit
		core.performUpdateOperation(userLogin, helper.smista(), richiesta);
		
		return true;
	}
}
