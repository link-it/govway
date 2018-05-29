package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
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


public class ConnettoreUtilities {

	public static DatiTempiRisposta readDatiTempiRisposta(TipoPdD tipoPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// Prelevo la configurazione del Controllo del Traffico
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		ConfigurazioneGenerale configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico();		
		
		
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
				String key = (String) keys.next();
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
	
	public static DatiTempiRisposta getDatiTempiRisposta(InRequestProtocolContext context, DatiTransazione datiTransazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		DatiTempiRisposta datiTempiRisposta = readDatiTempiRisposta(context.getTipoPorta());
		
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
		
		if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
			
			try{
				Connettore connettore = configPdDManager.getForwardRoute(datiTransazione.getSoggettoFruitore(),datiTransazione.getIdServizio(),false);
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
		else{
			
			try{
				if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPA()!=null) {
					IDPortaApplicativa idPA = context.getIntegrazione().getIdPA();
					PortaApplicativa pa = configPdDManager.getPortaApplicativa(idPA);
					if(pa.sizeServizioApplicativoList()>0) {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						idSA.setNome(pa.getServizioApplicativo(0).getNome()); // uso il primo
								
						ServizioApplicativo sa = configPdDManager.getServizioApplicativo(idSA);
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
		
		return datiTempiRisposta;
		
	}
	
}
