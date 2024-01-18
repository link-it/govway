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

package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * RuoliUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class RuoliUtilities {

	public static void findOggettiDaAggiornare(IDRuolo oldIdRuolo, Ruolo ruoloNEW, RuoliCore ruoliCore, List<Object> listOggettiDaAggiornare) throws Exception {
		
		
		// Cerco se utilizzato in soggetti
		SoggettiCore soggettiCore = new SoggettiCore(ruoliCore);
		FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
		filtroRicercaSoggetti.setIdRuolo(oldIdRuolo);
		List<IDSoggetto> listSoggetti = soggettiCore.getAllIdSoggettiRegistro(filtroRicercaSoggetti);
		if(listSoggetti!=null && listSoggetti.size()>0){
			for (IDSoggetto idSoggettoWithRuolo : listSoggetti) {
				Soggetto soggettoWithRuolo = soggettiCore.getSoggettoRegistro(idSoggettoWithRuolo);
				if(soggettoWithRuolo.getRuoli()!=null){
					for (org.openspcoop2.core.registry.RuoloSoggetto ruoloSoggetto : soggettoWithRuolo.getRuoli().getRuoloList()) {
						if(ruoloSoggetto.getNome().equals(oldIdRuolo.getNome())){
							ruoloSoggetto.setNome(ruoloNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(soggettoWithRuolo);
			}
		}
		
		
		
		
		// Cerco se utilizzato in servizi applicativi
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(ruoliCore);
		FiltroRicercaServiziApplicativi filtroRicercaSA = new FiltroRicercaServiziApplicativi();
		filtroRicercaSA.setIdRuolo(oldIdRuolo);
		List<IDServizioApplicativo> listSA = saCore.getAllIdServiziApplicativi(filtroRicercaSA);
		if(listSA!=null && listSA.size()>0){
			for (IDServizioApplicativo idServizioApplicativo : listSA) {
				ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
				if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : sa.getInvocazionePorta().getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(sa);
			}
		}
		
		
		// Cerco se utilizzato in porte delegate
		PorteDelegateCore pdCore = new PorteDelegateCore(ruoliCore);
		
		List<PortaDelegata> listPDdaAggiornare = new ArrayList<PortaDelegata>();
		
		FiltroRicercaPorteDelegate filtroRicercaPD = new FiltroRicercaPorteDelegate();
		filtroRicercaPD.setIdRuolo(oldIdRuolo);
		List<IDPortaDelegata> _listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
		if(_listPD!=null && _listPD.size()>0){
			for (IDPortaDelegata idPD : _listPD) {
				PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
				if(portaDelegata.getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getAutorizzazioneToken().getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				listPDdaAggiornare.add(portaDelegata);
			}
		}
		
		filtroRicercaPD = new FiltroRicercaPorteDelegate();
		filtroRicercaPD.setIdRuoloToken(oldIdRuolo);
		_listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
		if(_listPD!=null && _listPD.size()>0){
			for (IDPortaDelegata idPD : _listPD) {
				boolean find = false;
				if(listPDdaAggiornare!=null && !listPDdaAggiornare.isEmpty()) {
					for (PortaDelegata pd : listPDdaAggiornare) {
						if(pd.getNome().equals(idPD.getNome())) {
							find = true;
							break;
						}
					}
				}
				if(!find) {
					PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
					if(portaDelegata.getRuoli()!=null){
						for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getRuoli().getRuoloList()) {
							if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
								ruoloConfig.setNome(ruoloNEW.getNome());
							}
						}
					}
					if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
						for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getAutorizzazioneToken().getRuoli().getRuoloList()) {
							if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
								ruoloConfig.setNome(ruoloNEW.getNome());
							}
						}
					}
					listPDdaAggiornare.add(portaDelegata);
				}
			}
		}
		
		if(listPDdaAggiornare!=null && !listPDdaAggiornare.isEmpty()) {
			for (PortaDelegata pd : listPDdaAggiornare) {
				listOggettiDaAggiornare.add(pd);
			}
		}
		
		
		
		// Cerco se utilizzato in porte applicative
		PorteApplicativeCore paCore = new PorteApplicativeCore(ruoliCore);
		
		List<PortaApplicativa> listPAdaAggiornare = new ArrayList<PortaApplicativa>();
		
		FiltroRicercaPorteApplicative filtroRicercaPA = new FiltroRicercaPorteApplicative();
		filtroRicercaPA.setIdRuolo(oldIdRuolo);
		List<IDPortaApplicativa> _listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
		if(_listPA!=null && _listPA.size()>0){
			for (IDPortaApplicativa idPA : _listPA) {
				PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
				if(portaApplicativa.getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				if(portaApplicativa.getAutorizzazioneToken()!=null && portaApplicativa.getAutorizzazioneToken().getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getAutorizzazioneToken().getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				listPAdaAggiornare.add(portaApplicativa);
			}
		}
		
		filtroRicercaPA = new FiltroRicercaPorteApplicative();
		filtroRicercaPA.setIdRuoloToken(oldIdRuolo);
		_listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
		if(_listPA!=null && _listPA.size()>0){
			for (IDPortaApplicativa idPA : _listPA) {
				boolean find = false;
				if(listPAdaAggiornare!=null && !listPAdaAggiornare.isEmpty()) {
					for (PortaApplicativa pa : listPAdaAggiornare) {
						if(pa.getNome().equals(idPA.getNome())) {
							find = true;
							break;
						}
					}
				}
				if(!find) {
					PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
					if(portaApplicativa.getRuoli()!=null){
						for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getRuoli().getRuoloList()) {
							if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
								ruoloConfig.setNome(ruoloNEW.getNome());
							}
						}
					}
					if(portaApplicativa.getAutorizzazioneToken()!=null && portaApplicativa.getAutorizzazioneToken().getRuoli()!=null){
						for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getAutorizzazioneToken().getRuoli().getRuoloList()) {
							if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
								ruoloConfig.setNome(ruoloNEW.getNome());
							}
						}
					}
					listPAdaAggiornare.add(portaApplicativa);
				}
			}
		}
		
		if(listPAdaAggiornare!=null && !listPAdaAggiornare.isEmpty()) {
			for (PortaApplicativa pa : listPAdaAggiornare) {
				listOggettiDaAggiornare.add(pa);
			}
		}
		
		
		
		// Cerco Rate Limiting policy
		ConfigurazioneCore confCore = new ConfigurazioneCore(ruoliCore);
		
		// Nelle erogazioni
		ConsoleSearch ricercaPolicies = new ConsoleSearch(true);
		List<AttivazionePolicy> listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, RuoloPolicy.APPLICATIVA, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {
			// ignore
		}
		if(listaPolicies!=null && !listaPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : listaPolicies) {
				_updateAttivazionePolicy(attivazionePolicy, oldIdRuolo.getNome(), ruoloNEW.getNome());
				listOggettiDaAggiornare.add(attivazionePolicy);
			}
		}
		
		// Nelle fruizioni
		ricercaPolicies = new ConsoleSearch(true);
		listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, RuoloPolicy.DELEGATA, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {
			// ignore
		}
		if(listaPolicies!=null && !listaPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : listaPolicies) {
				_updateAttivazionePolicy(attivazionePolicy, oldIdRuolo.getNome(), ruoloNEW.getNome());
				listOggettiDaAggiornare.add(attivazionePolicy);
			}
		}
		
		// Globali
		ricercaPolicies = new ConsoleSearch(true);
		listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, null, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {
			// ignore
		}
		if(listaPolicies!=null && !listaPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : listaPolicies) {
				_updateAttivazionePolicy(attivazionePolicy, oldIdRuolo.getNome(), ruoloNEW.getNome());
				listOggettiDaAggiornare.add(attivazionePolicy);
			}
		}
		
	}
	
	private static void _updateAttivazionePolicy(AttivazionePolicy policy, String oldNomeRuolo, String nuovoNomeRuolo) {
		if(policy.getFiltro()!=null) {
			if(oldNomeRuolo.equals(policy.getFiltro().getRuoloFruitore())) {
				policy.getFiltro().setRuoloFruitore(nuovoNomeRuolo);
			}
			if(oldNomeRuolo.equals(policy.getFiltro().getRuoloErogatore())) {
				policy.getFiltro().setRuoloErogatore(nuovoNomeRuolo);
			}
		}
	}
	
	public static boolean deleteRuolo(Ruolo ruolo, String userLogin, RuoliCore ruoliCore, RuoliHelper ruoliHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !ruoliHelper.isModalitaCompleta();
		boolean ruoloInUso = ruoliCore.isRuoloInUso(ruolo.getNome(),whereIsInUso,normalizeObjectIds);
		
		if (ruoloInUso) {
			inUsoMessage.append(DBOggettiInUsoUtils.toString(new IDRuolo(ruolo.getNome()), whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			ruoliCore.performDeleteOperation(userLogin, ruoliHelper.smista(), ruolo);
			return true;
		}
		
		return false;
	}
}
