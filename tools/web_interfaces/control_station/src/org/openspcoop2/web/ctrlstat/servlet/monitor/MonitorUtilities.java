/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.math.BigInteger;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;

/**
*
* MonitorUtilities
* 
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public class MonitorUtilities {

	public static long countListaRichiestePendenti(FilterSearch filter,String pddName) throws Exception{
		if(Monitor.singlePdD){
			return Monitor.driverMonitoraggioLocale.countListaRichiestePendenti(filter);
		}
		else{
			return getMessaggioWS(pddName).count(convertToSearchFilterMessaggio(filter));
		}
	}
	
	public static List<Messaggio> getListaRichiestePendenti(FilterSearch filter,String pddName) throws Exception{
		if(Monitor.singlePdD){
			return Monitor.driverMonitoraggioLocale.getListaRichiestePendenti(filter);
		}
		else{
			return getMessaggioWS(pddName).findAll(convertToSearchFilterMessaggio(filter));
		}
	}
	
	public static long deleteRichiestePendenti(FilterSearch filter,String pddName) throws Exception{
		if(Monitor.singlePdD){
			return Monitor.driverMonitoraggioLocale.deleteRichiestePendenti(filter);
		}
		else{
			return getMessaggioWS(pddName).deleteAllByFilter(convertToSearchFilterMessaggio(filter));
		}
	}
	
	public static StatoPdd getStatoRichiestePendenti(FilterSearch filter,String pddName) throws Exception{
		if(Monitor.singlePdD){
			return Monitor.driverMonitoraggioLocale.getStatoRichiestePendenti(filter);
		}
		else{
			return getStatoPddWS(pddName).find(convertToSearchFilterStatoPdd(filter));
		}
	}
	
	
	private static String getWSUrl(String pddName, boolean statoPdD) throws Exception{
		String ipPdd = null;
		String protocollo = null;
		int porta = 80;
		try {
			// ipPdd = backEndConnector.getIPPdd(queueName);
			PdDControlStation pdd = Monitor.pddCore.getPdDControlStation(pddName);
			ipPdd = pdd.getIpGestione();
			protocollo = pdd.getProtocolloGestione();
			porta = pdd.getPortaGestione();

			if (ipPdd == null || protocollo == null || porta <= 0)
				throw new Exception("Parametri Porta di Dominio non validi.");

		} catch (Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error("Riscontrato errore durante la get dell'ip del pdd(" + pddName + "): " + e.toString(), e);
			throw new Exception("Riscontrato errore durante la get dell'ip del pdd(" + pddName + "): " + e.toString(), e);
		}
		String prefixUrl = protocollo + "://" + ipPdd + ":" + porta + "/";
		if(statoPdD){
			return prefixUrl + Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_endpointSuffixStatoPdd();
		}
		else{
			return prefixUrl + Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_endpointSuffixMessaggio();
		}
	}
	
	private static org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Messaggio getMessaggioWS(String pddName) throws Exception{
		org.openspcoop2.pdd.monitor.ws.client.messaggio.all.MessaggioSoap11Service messaggioService = 
				new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.MessaggioSoap11Service();
		org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Messaggio messaggioPort = messaggioService.getMessaggioPortSoap11();
		((BindingProvider)messaggioPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
				getWSUrl(pddName, false));
		((BindingProvider)messaggioPort).getRequestContext().put("schema-validation-enabled", true);
		String username = Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_credenzialiBasic_username();
		String password = Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_credenzialiBasic_password();
		if(username !=null && password!=null){
			// to use Basic HTTP Authentication: 
			((BindingProvider)messaggioPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			((BindingProvider)messaggioPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return messaggioPort;
	}
	
	private static org.openspcoop2.pdd.monitor.ws.client.statopdd.all.StatoPdd getStatoPddWS(String pddName) throws Exception{
		org.openspcoop2.pdd.monitor.ws.client.statopdd.all.StatoPddSoap11Service statoPddService = 
				new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.StatoPddSoap11Service();
		org.openspcoop2.pdd.monitor.ws.client.statopdd.all.StatoPdd statoPddPort = statoPddService.getStatoPddPortSoap11();
		((BindingProvider)statoPddPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
				getWSUrl(pddName, true));
		((BindingProvider)statoPddPort).getRequestContext().put("schema-validation-enabled", true);
		String username = Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_credenzialiBasic_username();
		String password = Monitor.consoleProperties.getGestioneCentralizzata_WSMonitor_credenzialiBasic_password();
		if(username !=null && password!=null){
			// to use Basic HTTP Authentication: 
			((BindingProvider)statoPddPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			((BindingProvider)statoPddPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return statoPddPort;
	}
	
	private static org.openspcoop2.pdd.monitor.ws.client.messaggio.all.SearchFilterMessaggio convertToSearchFilterMessaggio(FilterSearch filter){
		org.openspcoop2.pdd.monitor.ws.client.messaggio.all.SearchFilterMessaggio filterWS = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.SearchFilterMessaggio();
		
		if(filter.getLimit()>0){
			filterWS.setLimit(new BigInteger(filter.getLimit()+""));
		}
		if(filter.getOffset()>=0){
			filterWS.setOffset(new BigInteger(filter.getOffset()+""));
		}
		
		org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Filtro filtroInternoWS = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Filtro();
		filtroInternoWS.setTipo(filter.getTipo());
		filtroInternoWS.setStato(filter.getStato());
		if(filter.getSoglia()>0)
			filtroInternoWS.setSoglia(filter.getSoglia());
		filtroInternoWS.setMessagePattern(filter.getMessagePattern());
		filtroInternoWS.setIdMessaggio(filter.getIdMessaggio());
		filtroInternoWS.setCorrelazioneApplicativa(filter.getCorrelazioneApplicativa());
		org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Busta busta = null;
		if(filter.getBusta()!=null){
			busta = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.Busta();
			
			busta.setAttesaRiscontro(filter.getBusta().getAttesaRiscontro());
			busta.setAzione(filter.getBusta().getAzione());
			busta.setCollaborazione(filter.getBusta().getCollaborazione());
			
			org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaSoggetto bustaDestinatario = null;
			if(filter.getBusta().getDestinatario()!=null){
				bustaDestinatario = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaSoggetto();
				bustaDestinatario.setNome(filter.getBusta().getDestinatario().getNome());
				bustaDestinatario.setTipo(filter.getBusta().getDestinatario().getTipo());
			}
			busta.setDestinatario(bustaDestinatario);
			
			org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaSoggetto bustaMittente = null;
			if(filter.getBusta().getMittente()!=null){
				bustaMittente = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaSoggetto();
				bustaMittente.setNome(filter.getBusta().getMittente().getNome());
				bustaMittente.setTipo(filter.getBusta().getMittente().getTipo());
			}
			busta.setMittente(bustaMittente);
			
			org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaServizio bustaServizio = null;
			if(filter.getBusta().getServizio()!=null){
				bustaServizio = new org.openspcoop2.pdd.monitor.ws.client.messaggio.all.BustaServizio();
				bustaServizio.setNome(filter.getBusta().getServizio().getNome());
				bustaServizio.setTipo(filter.getBusta().getServizio().getTipo());
			}
			busta.setServizio(bustaServizio);
			
			busta.setProfiloCollaborazione(filter.getBusta().getProfiloCollaborazione());
			busta.setRiferimentoMessaggio(filter.getBusta().getRiferimentoMessaggio());
		}
		filtroInternoWS.setBusta(busta);
		filterWS.setFiltro(filtroInternoWS);
		
		return filterWS;
	}
	
	private static org.openspcoop2.pdd.monitor.ws.client.statopdd.all.SearchFilterStatoPdd convertToSearchFilterStatoPdd(FilterSearch filter){
		org.openspcoop2.pdd.monitor.ws.client.statopdd.all.SearchFilterStatoPdd filterWS = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.SearchFilterStatoPdd();
		
		if(filter.getLimit()>0){
			filterWS.setLimit(new BigInteger(filter.getLimit()+""));
		}
		if(filter.getOffset()>=0){
			filterWS.setOffset(new BigInteger(filter.getOffset()+""));
		}
		
		org.openspcoop2.pdd.monitor.ws.client.statopdd.all.Filtro filtroInternoWS = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.Filtro();
		filtroInternoWS.setTipo(filter.getTipo());
		filtroInternoWS.setStato(filter.getStato());
		if(filter.getSoglia()>0)
			filtroInternoWS.setSoglia(filter.getSoglia());
		filtroInternoWS.setMessagePattern(filter.getMessagePattern());
		filtroInternoWS.setIdMessaggio(filter.getIdMessaggio());
		filtroInternoWS.setCorrelazioneApplicativa(filter.getCorrelazioneApplicativa());
		org.openspcoop2.pdd.monitor.ws.client.statopdd.all.Busta busta = null;
		if(filter.getBusta()!=null){
			busta = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.Busta();
			
			busta.setAttesaRiscontro(filter.getBusta().getAttesaRiscontro());
			busta.setAzione(filter.getBusta().getAzione());
			busta.setCollaborazione(filter.getBusta().getCollaborazione());
			
			org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaSoggetto bustaDestinatario = null;
			if(filter.getBusta().getDestinatario()!=null){
				bustaDestinatario = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaSoggetto();
				bustaDestinatario.setNome(filter.getBusta().getDestinatario().getNome());
				bustaDestinatario.setTipo(filter.getBusta().getDestinatario().getTipo());
			}
			busta.setDestinatario(bustaDestinatario);
			
			org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaSoggetto bustaMittente = null;
			if(filter.getBusta().getMittente()!=null){
				bustaMittente = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaSoggetto();
				bustaMittente.setNome(filter.getBusta().getMittente().getNome());
				bustaMittente.setTipo(filter.getBusta().getMittente().getTipo());
			}
			busta.setMittente(bustaMittente);
			
			org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaServizio bustaServizio = null;
			if(filter.getBusta().getServizio()!=null){
				bustaServizio = new org.openspcoop2.pdd.monitor.ws.client.statopdd.all.BustaServizio();
				bustaServizio.setNome(filter.getBusta().getServizio().getNome());
				bustaServizio.setTipo(filter.getBusta().getServizio().getTipo());
			}
			busta.setServizio(bustaServizio);
			
			busta.setProfiloCollaborazione(filter.getBusta().getProfiloCollaborazione());
			busta.setRiferimentoMessaggio(filter.getBusta().getRiferimentoMessaggio());
		}
		filtroInternoWS.setBusta(busta);
		filterWS.setFiltro(filtroInternoWS);
		
		return filterWS;
	}
	

	
}
