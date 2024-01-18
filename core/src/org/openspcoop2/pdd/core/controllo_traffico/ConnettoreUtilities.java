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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**     
 * ConnettoreUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreUtilities {
	
	private ConnettoreUtilities() {}

	public static DatiTempiRisposta readDatiGlobaliTimeout(ConfigurazionePdDManager configPdDManager, TipoPdD tipoPdD, RequestInfo requestInfo, OpenSPCoop2Properties properties) throws DriverConfigurazioneException {
		// Prelevo la configurazione del Controllo del Traffico
		
		AccessoConfigurazionePdD accessoConfigurazione = null;
		try {
			accessoConfigurazione = properties.getAccessoConfigurazionePdD();
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		ConfigurazioneGenerale configurazioneGenerale = null;
		if(accessoConfigurazione!=null && CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(accessoConfigurazione.getTipo())) {
			try {
				configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico(requestInfo);
			}catch(DriverConfigurazioneNotFound notFound) {
				// ignore
			}
		}
		
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			return readDatiGlobaliTimeoutFruizione(configurazioneGenerale, properties);
		}
		else{
			return readDatiGlobaliTimeoutErogazione(configurazioneGenerale, properties);
		}
	
	}
	private static DatiTempiRisposta readDatiGlobaliTimeoutFruizione(ConfigurazioneGenerale configurazioneGenerale, OpenSPCoop2Properties properties) {
		DatiTempiRisposta datiTempiRisposta = new DatiTempiRisposta();
		if(configurazioneGenerale!=null && configurazioneGenerale.getTempiRispostaFruizione()!=null) {
			if(configurazioneGenerale.getTempiRispostaFruizione().getConnectionTimeout()!=null) {
				datiTempiRisposta.setConnectionTimeout(configurazioneGenerale.getTempiRispostaFruizione().getConnectionTimeout());
			}
			else {
				datiTempiRisposta.setConnectionTimeout(properties.getConnectionTimeout_inoltroBuste());
			}
			if(configurazioneGenerale.getTempiRispostaFruizione().getReadTimeout()!=null) {
				datiTempiRisposta.setReadConnectionTimeout(configurazioneGenerale.getTempiRispostaFruizione().getReadTimeout());
			}
			else {
				datiTempiRisposta.setReadConnectionTimeout(properties.getReadConnectionTimeout_inoltroBuste());
			}
		}
		else {
			datiTempiRisposta.setConnectionTimeout(properties.getConnectionTimeout_inoltroBuste());
			datiTempiRisposta.setReadConnectionTimeout(properties.getReadConnectionTimeout_inoltroBuste());
		}
		return datiTempiRisposta;
	}
	private static DatiTempiRisposta readDatiGlobaliTimeoutErogazione(ConfigurazioneGenerale configurazioneGenerale, OpenSPCoop2Properties properties) {
		DatiTempiRisposta datiTempiRisposta = new DatiTempiRisposta();
		if(configurazioneGenerale!=null && configurazioneGenerale.getTempiRispostaErogazione()!=null) {
			if(configurazioneGenerale.getTempiRispostaErogazione().getConnectionTimeout()!=null) {
				datiTempiRisposta.setConnectionTimeout(configurazioneGenerale.getTempiRispostaErogazione().getConnectionTimeout());
			}
			else {
				datiTempiRisposta.setConnectionTimeout(properties.getConnectionTimeout_consegnaContenutiApplicativi());
			}
			if(configurazioneGenerale.getTempiRispostaErogazione().getReadTimeout()!=null) {
				datiTempiRisposta.setReadConnectionTimeout(configurazioneGenerale.getTempiRispostaErogazione().getReadTimeout());
			}
			else {
				datiTempiRisposta.setReadConnectionTimeout(properties.getReadConnectionTimeout_consegnaContenutiApplicativi());
			}
		}
		else {
			datiTempiRisposta.setConnectionTimeout(properties.getConnectionTimeout_consegnaContenutiApplicativi());
			datiTempiRisposta.setReadConnectionTimeout(properties.getReadConnectionTimeout_consegnaContenutiApplicativi());
		}
		return datiTempiRisposta;
	}
	
	public static DatiTempiRisposta readDatiTempiRisposta(TipoPdD tipoPdD, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// Prelevo la configurazione del Controllo del Traffico
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		ConfigurazioneGenerale configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico(requestInfo);		
		
		
		// Imposto i valori di default
		DatiTempiRisposta datiTempiRisposta = new DatiTempiRisposta();
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			datiTempiRisposta.setConnectionTimeout(configurazioneGenerale.getTempiRispostaFruizione().getConnectionTimeout());
			datiTempiRisposta.setReadConnectionTimeout(configurazioneGenerale.getTempiRispostaFruizione().getReadTimeout());
			datiTempiRisposta.setAvgResponseTime(configurazioneGenerale.getTempiRispostaFruizione().getTempoMedioRisposta());
		}
		else{
			datiTempiRisposta.setConnectionTimeout(configurazioneGenerale.getTempiRispostaErogazione().getConnectionTimeout());
			datiTempiRisposta.setReadConnectionTimeout(configurazioneGenerale.getTempiRispostaErogazione().getReadTimeout());
			datiTempiRisposta.setAvgResponseTime(configurazioneGenerale.getTempiRispostaErogazione().getTempoMedioRisposta());
		}
	
		return datiTempiRisposta;
	}
	
	public static void mergeTempiRisposta(DatiTempiRisposta datiTempiRisposta, Map<String, String> properties){
		// Leggo i valori se fossero ridefiniti sul connettore
		if(properties!=null && properties.size()>0){
			
			Integer connectionTimeout = null; 
			Integer readTimeout = null; 
			Integer avgResponseTime = null; 
			Iterator<String> keys = properties.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if(key.equals(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)){
					connectionTimeout = Integer.parseInt(properties.get(key));
				}
				else if(key.endsWith(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)){
					readTimeout = Integer.parseInt(properties.get(key));
				}
				else if(key.endsWith(CostantiConnettori.CONNETTORE_TEMPO_MEDIO_RISPOSTA)){
					avgResponseTime = Integer.parseInt(properties.get(key));
				}
			}
			if(connectionTimeout!=null){
				datiTempiRisposta.setConnectionTimeout(connectionTimeout);
			}
			if(readTimeout!=null){
				datiTempiRisposta.setReadConnectionTimeout(readTimeout);
			}
			if(avgResponseTime!=null){
				datiTempiRisposta.setAvgResponseTime(avgResponseTime);
			}
		}
		
	}
	
	public static DatiTempiRisposta getDatiTempiRisposta(InRequestProtocolContext context, DatiTransazione datiTransazione, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		DatiTempiRisposta datiTempiRisposta = readDatiTempiRisposta(context.getTipoPorta(), requestInfo);
		
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		
		if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
			
			setDatiTempiRispostaFruizione(configPdDManager, datiTempiRisposta,
					context, datiTransazione, requestInfo);
			
		}
		else{
			
			setDatiTempiRispostaErogazione(configPdDManager, datiTempiRisposta,
					context, requestInfo);
			
		}
		
		return datiTempiRisposta;
		
	}
	
	private static void setDatiTempiRispostaFruizione(ConfigurazionePdDManager configPdDManager, DatiTempiRisposta datiTempiRisposta,
			InRequestProtocolContext context, DatiTransazione datiTransazione, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try{
			Connettore connettore = configPdDManager.getForwardRoute(datiTransazione.getSoggettoFruitore(),datiTransazione.getIdServizio(),false, requestInfo);
			mergeTempiRisposta(datiTempiRisposta, connettore.getProperties());
		}catch(Exception e){
			// registro solamente l'errore su log.
			// sicuramente avverrà un errore durante la gestione del messaggio
			try{
				OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug()).error(e.getMessage(),e);
			}catch(Exception eLogger){
				context.getLogCore().error(e.getMessage(),e);
			}
		}
	}
	
	private static void setDatiTempiRispostaErogazione(ConfigurazionePdDManager configPdDManager, DatiTempiRisposta datiTempiRisposta,
			InRequestProtocolContext context, RequestInfo requestInfo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try{
			if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPA()!=null) {
				IDPortaApplicativa idPA = context.getIntegrazione().getIdPA();
				PortaApplicativa pa = configPdDManager.getPortaApplicativa(idPA, requestInfo);
				if(pa.sizeServizioApplicativoList()>0) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
					idSA.setNome(pa.getServizioApplicativo(0).getNome()); // uso il primo
							
					ServizioApplicativo sa = configPdDManager.getServizioApplicativo(idSA, requestInfo);
					setConnettoreDatiTempiRispostaErogazione(context, sa, datiTempiRisposta);
				}
			}
		}catch(Exception e){
			// registro solamente l'errore su log.
			// sicuramente avverrà un errore durante la gestione del messaggio
			try{
				OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(OpenSPCoop2Properties.getInstance().isControlloTrafficoDebug()).error(e.getMessage(),e);
			}catch(Exception eLogger){
				context.getLogCore().error(e.getMessage(),e);
			}
		}
	}
	private static void setConnettoreDatiTempiRispostaErogazione(InRequestProtocolContext context, ServizioApplicativo sa, DatiTempiRisposta datiTempiRisposta) {
		
		Connettore connettore = null;
		
		String scenarioCooperazione = context.getProtocollo().getScenarioCooperazione();
		if(scenarioCooperazione!=null){
			if(org.openspcoop2.protocol.engine.constants.Costanti.SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI.equals(scenarioCooperazione) ||
					org.openspcoop2.protocol.engine.constants.Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ||
					org.openspcoop2.protocol.engine.constants.Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione) ){
				if(sa.getRispostaAsincrona()!=null){
					connettore = sa.getRispostaAsincrona().getConnettore();
				}
			}else{
				if(sa.getInvocazioneServizio()!=null){
					connettore = sa.getInvocazioneServizio().getConnettore();
				}
			}
		}
		
		if(connettore!=null){
			mergeTempiRisposta(datiTempiRisposta, connettore.getProperties());
		}
		
	}
}
