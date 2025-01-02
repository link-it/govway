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


package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * pddChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PddChange extends Action {

	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PddHelper pddHelper = new PddHelper(request, pd, session);
			PddCore pddCore = new PddCore();
			SoggettiCore soggettiCore = new SoggettiCore(pddCore);
			
			String id = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_ID);
			int idPdd = Integer.parseInt(id);
			String tipo = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			String descrizione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
			String ip = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_PUBBLICO);
			String ipGestione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_GESTIONE);
			String porta = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_PUBBLICA);
			String portaGestione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_GESTIONE);
			String implementazione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String subject = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);
			String clientAuth = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			String protocollo = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_PROTOCOLLO);
			String protocolloGestione = pddHelper.getParameter(PddCostanti.PARAMETRO_PDD_PROTOCOLLO_GESTIONE);
			
			int portaInt = 0;
			int portaGestioneInt = 0;
			try {
				portaInt = Integer.parseInt(porta);
			} catch (NumberFormatException e) {
				portaInt = pddCore.getPortaPubblica();// porta default
			}
			try {
				portaGestioneInt = Integer.parseInt(portaGestione);
			} catch (NumberFormatException e) {
				portaGestioneInt = pddCore.getPortaGestione();// porta di default
			}
			
			// Protocolli supportati
			
			String oldTipo = "";
			String oldProtocollo = "";
			String oldIp = "";
			int oldPortaGestione = 0;
			int oldPorta = 0;
			@SuppressWarnings("unused")
			String oldProtocolloGestione = "";
			// Preparo il menu
			pddHelper.makeMenu();
	
			String nomePdd = "";
			PdDControlStation pdd = null;
			try {
	
				pdd = pddCore.getPdDControlStation(idPdd);
				nomePdd = pdd.getNome();
				oldTipo = pdd.getTipo();
				oldProtocollo = pdd.getProtocollo();
				oldPorta = pdd.getPorta();
				oldIp = pdd.getIp();
				oldProtocolloGestione = pdd.getProtocolloGestione();
				oldPortaGestione = pdd.getPortaGestione();
			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, request, session, gd, mapping, 
						PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());
			}
	
			// Se nomehid = null, devo visualizzare la pagina per la modifica dati
			if(pddHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO,
						PddCostanti.SERVLET_NAME_PDD_LIST,nomePdd);
	
				if (descrizione == null) {
				    ip = pdd.getIp();
				    tipo = pdd.getTipo();
					protocollo = pdd.getProtocollo();
					protocolloGestione = pdd.getProtocolloGestione();
				    subject = pdd.getSubject();
				    ipGestione = pdd.getIpGestione();
				    descrizione = pdd.getDescrizione();
				    if(pdd.getClientAuth()!=null)
				    	clientAuth = pdd.getClientAuth().toString();
				    implementazione = pdd.getImplementazione();
				}
	
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
	
				dati.add(ServletUtils.getDataElementForEditModeFinished());
	
				int portaPUBBLICA = oldPorta;
				int portaGESTIONE = oldPortaGestione;
				if(porta!=null && !"".equals(porta)){
					try{
						portaPUBBLICA = Integer.parseInt(porta);
					}catch(Exception e){
						// ignore
					}
				}
				if(portaGestione!=null && !"".equals(portaGestione)){
					try{
						portaGESTIONE = Integer.parseInt(portaGestione);
					}catch(Exception e){
						// ignore
					}
				}
				
				dati = pddHelper.addPddToDati(dati, nomePdd, id, 
						ip, subject, ""/* password */, "" /* password */, 
						PddTipologia.toPddTipologia(tipo), TipoOperazione.CHANGE,
						PddCostanti.getDefaultPddProtocolli(), protocollo, protocolloGestione,
						portaPUBBLICA, descrizione, ipGestione, portaGESTIONE, implementazione, clientAuth, false);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());
			}
	
			// Controlli sui campi immessi
			boolean isOk = pddHelper.pddCheckData(TipoOperazione.CHANGE, false);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO,
						PddCostanti.SERVLET_NAME_PDD_LIST,nomePdd);
	
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
	
				dati.add(ServletUtils.getDataElementForEditModeFinished());
	
				dati = pddHelper.addPddToDati(dati, nomePdd, id, 
						ip, subject, "" /* password */, "" /* confpw */, 
						PddTipologia.toPddTipologia(tipo), TipoOperazione.CHANGE,
						PddCostanti.getDefaultPddProtocolli(), protocollo, protocolloGestione,  
						portaInt, descrizione, ipGestione, portaGestioneInt, implementazione, clientAuth, false);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());
				
			}
	
			// Modifico i dati del pdd nel db
			pdd.setTipo(tipo);
			if(subject!=null && !"".equals(subject))
				pdd.setSubject(subject);
			else
				pdd.setSubject(null);
			pdd.setDescrizione(descrizione);
			pdd.setImplementazione(implementazione);
			pdd.setClientAuth(StatoFunzionalita.toEnumConstant(clientAuth));
			if (tipo.equals(PddTipologia.OPERATIVO.toString()) || tipo.equals(PddTipologia.NONOPERATIVO.toString())) {
				pdd.setIp(ip);
				pdd.setProtocollo(protocollo);
				pdd.setPorta(portaInt);
				pdd.setIpGestione(ipGestione);
				pdd.setPortaGestione(portaGestioneInt);
				pdd.setProtocolloGestione(protocolloGestione);
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			// se e' cambiato il protocollo, l'ip o la porta
			// devo modificare le informazioni del connettore automatico del
			// soggetto
			if (!tipo.equals(PddTipologia.ESTERNO.toString()) && (!protocollo.equals(oldProtocollo) || oldPorta != portaInt || !ip.equals(oldIp))) {
				List<SoggettoCtrlStat> listaSoggetti = pddCore.soggettiWithServer(pdd.getNome());
				for (SoggettoCtrlStat soggetto : listaSoggetti) {
					Soggetto soggReg = soggetto.getSoggettoReg();
					Connettore connettore = soggReg.getConnettore();
					// Se il connettore e' http allora devo effettuare la
					// modifica
					if (TipiConnettore.HTTP.getNome().equals(connettore.getTipo())) {

						// Suffisso connettore
						String msg = "[console] suffissoConnettoreAutomatico impostato al valore: " + pddCore.getSuffissoConnettoreAutomatico();
						ControlStationLogger.getPddConsoleCoreLogger().info(msg);

						Map<String, String> properties = connettore.getProperties();
						String oldHttpLocation = properties.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
						// se il connettore e' quello creato automaticamente
						// allora effettuo le modifiche
						String suffisso = pddCore.getSuffissoConnettoreAutomatico();
						suffisso = suffisso.replace(CostantiControlStation.PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA, 
										soggettiCore.getWebContextProtocolAssociatoTipoSoggetto(soggReg.getTipo()));
						if (oldHttpLocation.equals(oldProtocollo + "://" + oldIp + ":" + oldPorta + "/" + suffisso)) {
							String newHttpLocation = pdd.getProtocollo() + "://" + pdd.getIp() + ":" + pdd.getPorta() + "/" + suffisso;
							Map<String, String> newprop = new HashMap<>();
							newprop.put(CostantiDB.CONNETTORE_HTTP_LOCATION, newHttpLocation);
							connettore.setProperties(newprop);

							// scrive lo modifiche sul soggetto
							pddCore.performUpdateOperation(userLogin, pddHelper.smista(), soggetto);
						}

					}
				}

			}

			// Se passo da non-operativo ad operativo
			// avvio la sincronizzazione della Porta Di Dominio
			if (oldTipo.equals(PddTipologia.NONOPERATIVO.toString()) && tipo.equals(PddTipologia.OPERATIVO.toString()) &&
				pddCore.isSincronizzazionePddEngineEnabled()) {
				try {
					ControlStationCore.logInfo("Avvio sincronizzazione causa passaggio Pdd non-operativo->operativo su Pdd [" + pdd.getNome() + "] da parte dell'utente [" + userLogin + "]");
					pd.setMessage("Sincronizzazione Porta di Dominio " + pdd.getNome() + " Effettuata correttamente.",Costanti.MESSAGE_TYPE_INFO);

				} catch (Exception e) {
					pd.setMessage("Sincronizzazione Porta di Dominio " + pdd.getNome() + " Non effettuata a causa di errori");
					return ServletUtils.getStrutsForwardGeneralError(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());
				}
			}
			// se tutto e' andato a buon fine scrivo le modifiche sulla pdd
			pddCore.performUpdateOperation(userLogin, pddHelper.smista(), pdd);

			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class); 

			List<PdDControlStation> lista = null;
			if(pddCore.isVisioneOggettiGlobale(userLogin)){
				lista = pddCore.pddList(null, ricerca);
			}else{
				lista = pddCore.pddList(userLogin, ricerca);
			}

			pddHelper.preparePddList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PddCostanti.OBJECT_NAME_PDD, ForwardParams.CHANGE());
		} 
	}
}
