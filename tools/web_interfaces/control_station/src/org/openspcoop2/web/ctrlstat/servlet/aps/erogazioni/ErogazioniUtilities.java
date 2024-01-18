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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ErogazioniUtilities
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniUtilities {

	public static boolean isChangeAPIEnabled(AccordoServizioParteSpecifica asps, AccordiServizioParteSpecificaCore aspsCore) throws Exception {
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		SoggettiCore soggettiCore = new SoggettiCore(aspsCore);
		PddCore pddCore = new PddCore(aspsCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(aspsCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(aspsCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(aspsCore);
			
		List<IDServizio> lIdServizio = new ArrayList<IDServizio>();
		lIdServizio.add(idServizio);
		
		boolean addDefault = true;
		boolean addNotDefault = true;
		
		// Fruizioni
		for (Fruitore fruitore : asps.getFruitoreList()) {
			IDSoggetto idSoggettoFr = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
			Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoFr);
			if(!pddCore.isPddEsterna(soggetto.getPortaDominio())){
				
				List<MappingFruizionePortaDelegata> listNotDefault = porteDelegateCore.getMapping(lIdServizio, !addDefault, addNotDefault);
				if(listNotDefault!=null && !listNotDefault.isEmpty()) {
					return false;
				}
				
				List<MappingFruizionePortaDelegata> listDefault = porteDelegateCore.getMapping(lIdServizio, addDefault, !addNotDefault);
				for (MappingFruizionePortaDelegata mappingDefault : listDefault) {
					IDPortaDelegata idPD = mappingDefault.getIdPortaDelegata();
					PortaDelegata pd = porteDelegateCore.getPortaDelegata(idPD);
					if(isChangeAPIEnabled(pd,confCore)==false) {
						return false;
					}
				}
			}	
		}
			
		// Erogazioni
		List<MappingErogazionePortaApplicativa> listNotDefault = porteApplicativeCore.getMapping(lIdServizio, !addDefault, addNotDefault);
		if(listNotDefault!=null && !listNotDefault.isEmpty()) {
			return false;
		}
		
		List<MappingErogazionePortaApplicativa> listDefault = porteApplicativeCore.getMapping(lIdServizio, addDefault, !addNotDefault);
		for (MappingErogazionePortaApplicativa mappingDefault : listDefault) {
			IDPortaApplicativa idPA = mappingDefault.getIdPortaApplicativa();
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idPA);
			if(isChangeAPIEnabled(pa,confCore)==false) {
				return false;
			}
		}
			
		return true;
	}
	
	private static boolean isChangeAPIEnabled(PortaDelegata pd, ConfigurazioneCore confCore) throws DriverControlStationException {
		
		if(pd.getTrasformazioni()!=null) {
			if(pd.getTrasformazioni().sizeRegolaList()>0) {
				for (TrasformazioneRegola regola : pd.getTrasformazioni().getRegolaList()) {
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeAzioneList()>0) {
						return false;
					}
				}
			}
		}
		
		List<AttivazionePolicy> list = confCore.attivazionePolicyList(new ConsoleSearch(true), RuoloPolicy.DELEGATA, pd.getNome());
		if(list!=null && !list.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : list) {
				if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().isEnabled() && attivazionePolicy.getFiltro().getAzione()!=null) {
					return false;
				}
			}
		}
		
		if(confCore.isConfigurazioneAllarmiEnabled()) {
			List<ConfigurazioneAllarmeBean> listAllarmi = confCore.allarmiList(new ConsoleSearch(true), RuoloPorta.DELEGATA, pd.getNome());
			if(listAllarmi!=null && !listAllarmi.isEmpty()) {
				for (ConfigurazioneAllarmeBean allarme : listAllarmi) {
					if(allarme.getFiltro()!=null && allarme.getFiltro().isEnabled() && allarme.getFiltro().getAzione()!=null) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean isChangeAPIEnabled(PortaApplicativa pa, ConfigurazioneCore confCore) throws DriverControlStationException {
		
		if(pa.getTrasformazioni()!=null) {
			if(pa.getTrasformazioni().sizeRegolaList()>0) {
				for (TrasformazioneRegola regola : pa.getTrasformazioni().getRegolaList()) {
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeAzioneList()>0) {
						return false;
					}
				}
			}
		}
		
		List<AttivazionePolicy> list = confCore.attivazionePolicyList(new ConsoleSearch(true), RuoloPolicy.APPLICATIVA, pa.getNome());
		if(list!=null && !list.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : list) {
				if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().isEnabled() && attivazionePolicy.getFiltro().getAzione()!=null) {
					return false;
				}
			}
		}
		
		if(confCore.isConfigurazioneAllarmiEnabled()) {
			List<ConfigurazioneAllarmeBean> listAllarmi = confCore.allarmiList(new ConsoleSearch(true), RuoloPorta.APPLICATIVA, pa.getNome());
			if(listAllarmi!=null && !listAllarmi.isEmpty()) {
				for (ConfigurazioneAllarmeBean allarme : listAllarmi) {
					if(allarme.getFiltro()!=null && allarme.getFiltro().isEnabled() && allarme.getFiltro().getAzione()!=null) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
}
