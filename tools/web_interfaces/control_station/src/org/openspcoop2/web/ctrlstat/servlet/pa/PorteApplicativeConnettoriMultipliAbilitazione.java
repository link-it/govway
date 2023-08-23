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

import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeConnettoriMultipliAbilitazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliAbilitazione extends Action {

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
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			String changeAbilitato = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA);
			String schedulingP = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCHEDULING);
			boolean scheduling = "true".equalsIgnoreCase(schedulingP);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String nomeSAConnettore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOME_SA);

			String idConnTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isNotEmpty(idConnTab)) {
				ServletUtils.setObjectIntoSession(request, session, idConnTab, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			}

			String fromAPIPageInfo = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
			boolean fromApi = Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(fromAPIPageInfo);

			String actionConferma = porteApplicativeHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			// check su oldNomePD
			//			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			//			String oldNomePA = pdOld.getHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA);
			//			oldNomePA = (((oldNomePA != null) && !oldNomePA.equals("")) ? oldNomePA : nomePorta);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			// Prendo la porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

			PortaApplicativaServizioApplicativo oldPaSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					oldPaSA = paSATmp;					
				}
			}

			// in progress segnalo l'azione che si sta effettuando
			if(actionConferma == null) {
				boolean eseguiOperazione = true;

				// solo per i casi in cui sto disabilitando
				StringBuilder sbErrore = new StringBuilder();
				if(!ServletUtils.isCheckBoxEnabled(changeAbilitato)) {
					AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
					AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
					
					String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
					if(idAsps == null) 
						idAsps = "";
					long idAspsLong = Long.parseLong(idAsps);
					AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idAspsLong);
					AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
					ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
					
					// controllo che il connettore non sia utilizzato in altri punti della configurazione
					List<String> messaggiSezioniConnettore = new ArrayList<>();
					int numeroElementiDaControllare = 1;
					
					boolean connettoreUtilizzatiConfig = porteApplicativeHelper.isConnettoreMultiploInUso(numeroElementiDaControllare,
							nomeSAConnettore, pa, asps, apc, serviceBinding, messaggiSezioniConnettore);
					
					if(!scheduling) {
					
						if(connettoreUtilizzatiConfig) {
							sbErrore.append(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_DISABILITARE_IL_CONNETTORE_UTILIZZATI_IN_CONFIGURAZIONE);
							sbErrore.append(":").append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							for (String s : messaggiSezioniConnettore) {
								sbErrore.append(s);
							}
	
							eseguiOperazione = false;
						}
						
						
						if(!connettoreUtilizzatiConfig) {
							// controllare che almeno un connettore rimanga abilitato
							int numeroAbilitati = 0;
							for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
								if(!paSATmp.getNome().equals(nomeSAConnettore)) { // controllo che tutti gli altri non siano disabilitati
									boolean abilitato = paSATmp.getDatiConnettore()	!= null ? 	paSATmp.getDatiConnettore().getStato().equals(StatoFunzionalita.ABILITATO) : true;
		
									if(abilitato)
										numeroAbilitati ++;
								}
							}
							
							
							if(numeroAbilitati < 1) {
								eseguiOperazione = false;
								sbErrore.append(MessageFormat.format(PorteApplicativeCostanti.MESSAGGIO_IMPOSSIBILE_DISABILITARE_IL_CONNETTORE_0_DEVE_RIMANARE_ALMENTO_UN_CONNETTORE_ABILITATO,
										porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(oldPaSA)));
							}
						}
						
					}
				}

				String messaggio = null;
				String title = null;
				
				if(eseguiOperazione) {
					messaggio = porteApplicativeHelper.getMessaggioConfermaModificaRegolaStatoConnettoreMultiplo(fromApi, oldPaSA, ServletUtils.isCheckBoxEnabled(changeAbilitato), true,true,scheduling);
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

							},
							{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};
					pd.setBottoni(bottoni );
				} else {
					messaggio = sbErrore.toString();
					title = "Attenzione";
					String[][] bottoni = { 
							{ Costanti.LABEL_MONITOR_BUTTON_CHIUDI, 
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
								Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

							}
							};
					pd.setBottoni(bottoni );
				}
				pd.setMessage(messaggio, title, MessageType.CONFIRM);
			} 

			// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
			if(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)) {
				// Prendo la porta applicativa
				pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

				PortaApplicativaServizioApplicativo paSA = null;
				for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
					if(paSATmp.getNome().equals(nomeSAConnettore)) {
						paSA = paSATmp;					
					}
				}

				PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();

				if(datiConnettore == null) { // succede solo se e' la prima volta che modifico la configurazione di default
					datiConnettore = new PortaApplicativaServizioApplicativoConnettore();
					datiConnettore.setNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
				}
				String nomeConnettore = datiConnettore.getNome();

				paSA.setDatiConnettore(datiConnettore);

				//				// Modifico i dati della porta applicativa nel db
				//				pa.setNome(nomePorta);
				//				IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
				//				oldIDPortaApplicativaForUpdate.setNome(oldNomePA);
				//				pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);


				// cambio solo la modalita'
				if(scheduling) {
					if(ServletUtils.isCheckBoxEnabled(changeAbilitato)) {
						datiConnettore.setScheduling(StatoFunzionalita.ABILITATO);
					}
					else{
						datiConnettore.setScheduling(StatoFunzionalita.DISABILITATO);
					}
				}
				else {
					if(ServletUtils.isCheckBoxEnabled(changeAbilitato)) {
						datiConnettore.setStato(StatoFunzionalita.ABILITATO);
					}
					else{
						datiConnettore.setStato(StatoFunzionalita.DISABILITATO);
					}
				}

				String userLogin = ServletUtils.getUserLoginFromSession(session);
				porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
				
				List<String> aliasJmx = porteApplicativeCore.getJmxPdDAliases();
				if(aliasJmx!=null && !aliasJmx.isEmpty()) {
					
					boolean repositoryUpdated = false;
					
					for (String alias : aliasJmx) {
						
						// config
						boolean success = false;
						String metodo = null;
						if(scheduling) {
							metodo = StatoFunzionalita.ABILITATO.equals(datiConnettore.getScheduling()) ? 
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo(alias) :
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo(alias);
						}
						else {
							metodo = StatoFunzionalita.ABILITATO.equals(datiConnettore.getStato()) ? 
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo(alias) :
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo(alias);
						}
						try{
							String stato = porteApplicativeCore.getInvoker().invokeJMXMethod(alias, 
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaType(alias),
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
									metodo, 
									pa.getNome(),
									nomeConnettore);
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
							else {
								success = true;
							}
						}catch(Exception e){
							String msgErrore = "Errore durante l'aggiornamento dello "+(scheduling ? "scheduling" : "stato")+" del connettore "+nomeConnettore+" della PortaApplicativa '"+pa.getNome()+"' via jmx (jmxMethod '"+metodo+"') (node:"+alias+"): "+e.getMessage();
							ControlStationCore.logError(msgErrore, e);
						}
						
						if(scheduling && !repositoryUpdated && success) {
							// repository
							metodo = StatoFunzionalita.ABILITATO.equals(datiConnettore.getScheduling()) ? 
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository(alias) :
									porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository(alias);
							try{
								boolean slowOperation = true;
								String stato = porteApplicativeCore.getInvoker().invokeJMXMethod(alias, 
										porteApplicativeCore.getJmxPdDConfigurazioneSistemaType(alias),
										porteApplicativeCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
										metodo, 
										slowOperation,
										pa.getNome(),
										nomeConnettore);
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
								else {
									repositoryUpdated = true;
									ControlStationCore.logDebug("Aggiornato scheduling del connettore: "+stato);
								}
							}catch(Exception e){
								String msgErrore = "Errore durante l'aggiornamento dello scheduling del connettore "+nomeConnettore+" della PortaApplicativa '"+pa.getNome()+"' via jmx (jmxMethod '"+metodo+"') (node:"+alias+"): "+e.getMessage();
								ControlStationCore.logError(msgErrore, e);
							}
						}
					}
				}
				
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI);
			}

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
			IDSoggetto idSoggettoProprietario = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
			List<PortaApplicativaServizioApplicativo> listaFiltrata = porteApplicativeHelper.applicaFiltriRicercaConnettoriMultipli(ricerca, idLista, pa.getServizioApplicativoList(), idSoggettoProprietario);
						
			PortaApplicativaServizioApplicativo paSA = null;
			for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
				if(paSATmp.getNome().equals(nomeSAConnettore)) {
					paSA = paSATmp;					
				}
			}

			if(paSA==null) {
				throw new Exception("Connettore '"+nomeSAConnettore+"' non trovato");
			}
			
			PortaApplicativaServizioApplicativoConnettore datiConnettore = paSA.getDatiConnettore();

			if(datiConnettore == null) { // succede solo se e' la prima volta che modifico la configurazione di default
				datiConnettore = new PortaApplicativaServizioApplicativoConnettore();
				datiConnettore.setNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
			}
			
			String nomeConnettoreChangeList = datiConnettore.getNome();
			if(nomeConnettoreChangeList==null) {
				nomeConnettoreChangeList = CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
			}
			
			porteApplicativeHelper.preparePorteAppConnettoriMultipliList_fromChangeConnettore(nomePorta, ricerca, listaFiltrata, portaApplicativa,
					nomeConnettoreChangeList);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ABILITAZIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_ABILITAZIONE,
					ForwardParams.OTHER(""));

		} 
	}
}
