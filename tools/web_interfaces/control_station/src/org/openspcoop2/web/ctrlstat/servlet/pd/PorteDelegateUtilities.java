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

package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;

/**
 * PorteDelegateUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateUtilities {
	
	private PorteDelegateUtilities() {}

	public static void deletePortaDelegataAzioni(PortaDelegata portaDelegata, AccordoServizioParteSpecifica asps,
			PorteDelegateCore porteDelegateCore, PorteDelegateHelper porteDelegateHelper, 
			StringBuilder inUsoMessage, List<String> azioni, 
			String userLogin) throws Exception {
		
		String azioneGiaEsistente = portaDelegata.getAzione().getAzioneDelegata(0); // prendo la prima
		
		ConfigurazioneServizioAzione configAzioni = null; 
		boolean updateASPS = false;
		
		Fruitore fruitore = null;
		for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
			if(fruitoreCheck.getTipo().equals(portaDelegata.getTipoSoggettoProprietario()) && fruitoreCheck.getNome().equals(portaDelegata.getNomeSoggettoProprietario())) {
				fruitore = fruitoreCheck;
				break;
			}
		}
		if(fruitore==null) {
			throw new Exception("Fruitore con id '"+portaDelegata.getTipoSoggettoProprietario()+"/"+portaDelegata.getNomeSoggettoProprietario()+"' non trovata");
		}
		for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
			ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
			if(config!=null) {
				if(config.getAzioneList().contains(azioneGiaEsistente)) {
					configAzioni = config;
					break;
				}
			}
		}
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
		StringBuilder bfTrasformazioni = new StringBuilder();
		StringBuilder bfCT = new StringBuilder();
		
		for (int i = 0; i < azioni.size(); i++) {

			String azione = azioni.get(i);
			
			boolean usedInTrasformazioni = false;
			if(portaDelegata.getTrasformazioni()!=null && portaDelegata.getTrasformazioni().sizeRegolaList()>0) {
				for (TrasformazioneRegola trasformazioneRegola : portaDelegata.getTrasformazioni().getRegolaList()) {
					if(trasformazioneRegola.getApplicabilita()!=null && trasformazioneRegola.getApplicabilita().getAzioneList()!=null &&
							trasformazioneRegola.getApplicabilita().getAzioneList().contains(azione)) {
						usedInTrasformazioni = true;
						break;
					}
				}
			}
			
			if(confCore.usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy.DELEGATA, portaDelegata.getNome(), azione)) {
				if(bfCT.length()>0) {
					bfCT.append(",");
				}
				bfCT.append(azione);
			}	
			else if(usedInTrasformazioni) {
				if(bfTrasformazioni.length()>0) {
					bfTrasformazioni.append(",");
				}
				bfTrasformazioni.append(azione);
			}
			else {
				for (int j = 0; j < portaDelegata.getAzione().sizeAzioneDelegataList(); j++) {
					String azioneDelegata = portaDelegata.getAzione().getAzioneDelegata(j);
					if (azione.equals(azioneDelegata)) {
						portaDelegata.getAzione().removeAzioneDelegata(j);
						break;
					}
				}
				
				if(configAzioni!=null) {
					for (int j = 0; j < configAzioni.sizeAzioneList(); j++) {
						if(configAzioni.getAzione(j).equals(azione)) {
							configAzioni.removeAzione(j);
							updateASPS = true;
							break;
						}
					}
				}
			}

		}
		
		// non posso eliminare tutte le azioni
		if(portaDelegata.getAzione().sizeAzioneDelegataList() == 0) {
			inUsoMessage.append(PorteDelegateCostanti.MESSAGGIO_ERRORE_NON_E_POSSIBILE_ELIMINARE_TUTTE_LE_AZIONI_ASSOCIATE_ALLA_CONFIGURAZIONE); 
		} 
		else if(bfCT.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè utilizzate in configurazione di Rate Limiting: "+bfCT.toString()); 
		}
		else if(bfTrasformazioni.length()>0) {
			inUsoMessage.append("Non è stato possibile procedere con l'eliminazione poichè utilizzate in criteri di applicabilità di una Trasformazione: "+bfTrasformazioni.toString()); 
		}
		else {
			
			List<Object> listaOggettiDaModificare = new ArrayList<>();
			
			listaOggettiDaModificare.add(portaDelegata);
			
			if(updateASPS) {
				listaOggettiDaModificare.add(asps);
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), listaOggettiDaModificare.toArray());
			
		}
		
	}
	
	public static void initTracciamento(PortaDelegata pde, PorteDelegateCore porteDelegateCore, Configurazione config,
			String tracciamentoStato, String statoDiagnostici, String severita) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		PortaTracciamento portaTracciamento = pde.getTracciamento();
		if(portaTracciamento==null) {
			portaTracciamento = new PortaTracciamento();
			pde.setTracciamento(portaTracciamento);
		}
		
		portaTracciamento.setStato(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoStato) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
		if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoStato)) {
			if(config==null) {
				config = porteDelegateCore.getConfigurazioneGenerale();
			}
			if(config.getTracciamento()!=null && config.getTracciamento().getPortaDelegata()!=null) {
				portaTracciamento.setDatabase(config.getTracciamento().getPortaDelegata().getDatabase());
				portaTracciamento.setFiletrace(config.getTracciamento().getPortaDelegata().getFiletrace());
				if(portaTracciamento.getFiletrace()!=null && org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.CONFIGURAZIONE_ESTERNA.equals(portaTracciamento.getFiletrace().getStato())) {
					portaTracciamento.getFiletrace().setStato(org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.DISABILITATO);
				}
				portaTracciamento.setFiletraceConfig(config.getTracciamento().getPortaDelegata().getFiletraceConfig());
				portaTracciamento.setEsiti(config.getTracciamento().getPortaDelegata().getEsiti());
				portaTracciamento.setTransazioni(config.getTracciamento().getPortaDelegata().getTransazioni());
			}
		}
		
		if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(statoDiagnostici)) {
			portaTracciamento.setSeverita(Severita.toEnumConstant(severita));
		}
		else {
			portaTracciamento.setSeverita(null);
		}
	}
	
	public static void setTracciamentoTransazioni(PortaDelegata pde, PorteDelegateCore porteDelegateCore,
			String dbStato,
			String dbStatoReqIn, String dbStatoReqOut, String dbStatoResOut, String dbStatoResOutComplete,
			boolean dbFiltroEsiti,
			String fsStato,
			String fsStatoReqIn, String fsStatoReqOut, String fsStatoResOut, String fsStatoResOutComplete,
			boolean fsFiltroEsiti,
			String nuovaConfigurazioneEsiti,
			String transazioniTempiElaborazione, String transazioniToken,
			String fileTraceStato, String fileTraceConfigFile,
			String fileTraceClient, String fileTraceClientHdr, String fileTraceClientBody,
			String fileTraceServer, String fileTraceServerHdr, String fileTraceServerBody) {
		PortaTracciamento portaTracciamento = pde.getTracciamento();
		if(portaTracciamento==null) {
			portaTracciamento = new PortaTracciamento();
			pde.setTracciamento(portaTracciamento);
		}
		
		TracciamentoConfigurazione database = porteDelegateCore.buildTracciamentoConfigurazioneDatabase(dbStato,
				dbStatoReqIn, dbStatoReqOut, dbStatoResOut, dbStatoResOutComplete,
				dbFiltroEsiti);
		portaTracciamento.setDatabase(database);
		
		TracciamentoConfigurazione filetrace = porteDelegateCore.buildTracciamentoConfigurazioneFiletrace(fsStato,
				fsStatoReqIn, fsStatoReqOut, fsStatoResOut, fsStatoResOutComplete,
				fsFiltroEsiti);
		portaTracciamento.setFiletrace(filetrace);
		
		if(StringUtils.isEmpty(nuovaConfigurazioneEsiti)) {
			portaTracciamento.setEsiti(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"");
		}
		else {
			portaTracciamento.setEsiti(nuovaConfigurazioneEsiti);
		}
		
		if(portaTracciamento.getTransazioni()==null) {
			portaTracciamento.setTransazioni( new Transazioni() );
		}
		portaTracciamento.getTransazioni().setTempiElaborazione(StatoFunzionalita.toEnumConstant(transazioniTempiElaborazione));
		portaTracciamento.getTransazioni().setToken(StatoFunzionalita.toEnumConstant(transazioniToken));
		
		if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(fileTraceStato)) {
			portaTracciamento.setFiletraceConfig(new TracciamentoConfigurazioneFiletrace());
			portaTracciamento.getFiletraceConfig().setConfig(fileTraceConfigFile);
			
			portaTracciamento.getFiletraceConfig().setDumpIn(new TracciamentoConfigurazioneFiletraceConnector());			
			portaTracciamento.getFiletraceConfig().getDumpIn().setStato(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClient) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			portaTracciamento.getFiletraceConfig().getDumpIn().setHeader(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClientHdr) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			portaTracciamento.getFiletraceConfig().getDumpIn().setPayload(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceClientBody) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			
			portaTracciamento.getFiletraceConfig().setDumpOut(new TracciamentoConfigurazioneFiletraceConnector());			
			portaTracciamento.getFiletraceConfig().getDumpOut().setStato(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServer) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			portaTracciamento.getFiletraceConfig().getDumpOut().setHeader(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServerHdr) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
			portaTracciamento.getFiletraceConfig().getDumpOut().setPayload(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(fileTraceServerBody) ?
					StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
		}
		else {
			portaTracciamento.setFiletraceConfig(null);
		}
	}
}
