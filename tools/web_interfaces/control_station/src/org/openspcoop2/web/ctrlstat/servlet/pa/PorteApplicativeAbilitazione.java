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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * porteAppChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeAbilitazione extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String changeAbilitato = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA);
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			int soggInt = Integer.parseInt(idsogg);
			
			String idTab = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String fromAPIPageInfo = porteApplicativeHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
			boolean fromApi = Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(fromAPIPageInfo);
			
			String actionConferma = porteApplicativeHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);

			// Prendo la porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico aspc = apcCore.getAccordoServizioSintetico(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
			
			// in progress segnalo l'azione che si sta effettuando
			if(actionConferma == null) {
				String messaggio = porteApplicativeHelper.getMessaggioConfermaModificaRegolaMappingErogazionePortaApplicativa(fromApi, pa, serviceBinding, ServletUtils.isCheckBoxEnabled(changeAbilitato), true,true);

				pd.setMessage(messaggio, MessageType.CONFIRM);
				
				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX
							
						},
						{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni );
			} 

			// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
			if(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)) {
				// Prendo la porta applicativa
				pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
				IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
				oldIDPortaApplicativaForUpdate.setNome(pa.getNome());
				pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
				
				 // cambio solo la modalita'
	            if(ServletUtils.isCheckBoxEnabled(changeAbilitato)) {
	                pa.setStato(StatoFunzionalita.ABILITATO);
	            }
	            else{
	                pa.setStato(StatoFunzionalita.DISABILITATO);
	            }
				
				String userLogin = ServletUtils.getUserLoginFromSession(session);
	
				porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
				
				List<String> aliasJmx = porteApplicativeCore.getJmxPdDAliases();
				if(aliasJmx!=null && !aliasJmx.isEmpty()) {
					for (String alias : aliasJmx) {
						String metodo = StatoFunzionalita.ABILITATO.equals(pa.getStato()) ? 
								porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa(alias) :
								porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa(alias);
						try{
							String stato = porteApplicativeCore.getInvoker().invokeJMXMethod(alias, 
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaType(alias),
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
									metodo, 
									pa.getNome());
							if(stato==null) {
								throw new ServletException("Aggiornamento fallito");
							}
							if(
								!(
									JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)
									||
									(stato!=null && stato.startsWith(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX))
								)
							){
								throw new ServletException(stato);
							}
						}catch(Exception e){
							String msgErrore = "Errore durante l'aggiornamento dello stato della PortaApplicativa '"+pa.getNome()+"' via jmx (jmxMethod '"+metodo+"') (node:"+alias+"): "+e.getMessage();
							ControlStationCore.logError(msgErrore, e);
						}
					}
				}
			}
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);


			List<PortaApplicativa> lista = null;
			int idLista = -1;
			
		
			switch (parentPA) {
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
				
				if(fromApi) {
					ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
					apsHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, null);	
				}
				else {
				
					boolean datiInvocazione = ServletUtils.isCheckBoxEnabled(porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE));
					if(datiInvocazione) {
						idLista = Liste.SERVIZI;
						ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
						boolean [] permessi = new boolean[2];
						PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
						permessi[0] = pu.isServizi();
						permessi[1] = pu.isAccordiCooperazione();
						List<AccordoServizioParteSpecifica> listaS = null;
						String superUser   = ServletUtils.getUserLoginFromSession(session);
						if(apsCore.isVisioneOggettiGlobale(superUser)){
							listaS = apsCore.soggettiServizioList(null, ricerca,permessi,session, request);
						}else{
							listaS = apsCore.soggettiServizioList(superUser, ricerca,permessi,session, request);
						}
						AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
						apsHelper.prepareServiziList(ricerca, listaS);
					}
					else {			
						idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
						ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
						IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
						Long idSoggetto = asps.getIdSoggetto() != null ? asps.getIdSoggetto() : -1L;
						List<MappingErogazionePortaApplicativa> lista2 = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(),ricerca);
						AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
						apsHelper.prepareServiziConfigurazioneList(lista2, idAsps, idSoggetto+"", ricerca);
					}
					
				}
					
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
				porteApplicativeHelper.preparePorteAppList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = porteApplicativeHelper.isModalitaCompleta() ? ForwardParams.OTHER("") : PorteApplicativeCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_ABILITAZIONE, fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_ABILITAZIONE,
					ForwardParams.OTHER(""));

		} 
	}
}
