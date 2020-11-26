/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import org.openspcoop2.web.ctrlstat.core.Search;
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
		FiltroRicercaPorteDelegate filtroRicercaPD = new FiltroRicercaPorteDelegate();
		filtroRicercaPD.setIdRuolo(oldIdRuolo);
		List<IDPortaDelegata> listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
		if(listPD!=null && listPD.size()>0){
			for (IDPortaDelegata idPD : listPD) {
				PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
				if(portaDelegata.getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(portaDelegata);
			}
		}
		
		
		
		// Cerco se utilizzato in porte applicative
		PorteApplicativeCore paCore = new PorteApplicativeCore(ruoliCore);
		FiltroRicercaPorteApplicative filtroRicercaPA = new FiltroRicercaPorteApplicative();
		filtroRicercaPA.setIdRuolo(oldIdRuolo);
		List<IDPortaApplicativa> listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
		if(listPA!=null && listPA.size()>0){
			for (IDPortaApplicativa idPA : listPA) {
				PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
				if(portaApplicativa.getRuoli()!=null){
					for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getRuoli().getRuoloList()) {
						if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
							ruoloConfig.setNome(ruoloNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(portaApplicativa);
			}
		}
		
		
		
		// Cerco Rate Limiting policy
		ConfigurazioneCore confCore = new ConfigurazioneCore(ruoliCore);
		
		// Nelle erogazioni
		Search ricercaPolicies = new Search(true);
		List<AttivazionePolicy> listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, RuoloPolicy.APPLICATIVA, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {}
		if(listaPolicies!=null && !listaPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : listaPolicies) {
				_updateAttivazionePolicy(attivazionePolicy, oldIdRuolo.getNome(), ruoloNEW.getNome());
				listOggettiDaAggiornare.add(attivazionePolicy);
			}
		}
		
		// Nelle fruizioni
		ricercaPolicies = new Search(true);
		listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, RuoloPolicy.DELEGATA, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {}
		if(listaPolicies!=null && !listaPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : listaPolicies) {
				_updateAttivazionePolicy(attivazionePolicy, oldIdRuolo.getNome(), ruoloNEW.getNome());
				listOggettiDaAggiornare.add(attivazionePolicy);
			}
		}
		
		// Globali
		ricercaPolicies = new Search(true);
		listaPolicies = null;
		try {
			listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, null, null,
					null, null, null,
					null, null,
					null, oldIdRuolo.getNome());
		}catch(Exception e) {}
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
