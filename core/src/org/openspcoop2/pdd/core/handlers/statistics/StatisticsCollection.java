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

package org.openspcoop2.pdd.core.handlers.statistics;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * StatisticsCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsCollection {

	private StatisticsCollection() {}
	
	private static final StatisticsCollection statisticsCollection =  new StatisticsCollection();
	public static StatisticsCollection getStatisticsCollection(){
		return StatisticsCollection.statisticsCollection;
	}

	private static final long SOGLIA_DIMENSIONE = Long.MAX_VALUE-100000;
	private static final int SOGLIA_TEMPORALE = 1000*60*30; // statistiche relative all'ultima mezz'ora.
	
	private static Logger log = LoggerWrapperFactory.getLogger(StatisticsCollection.class);
	private static boolean isEsitoOk(EsitoTransazione esito, IProtocolFactory<?> protocollo){
		try{
			if(esito==null) {
				return false;
			}
			List<Integer> esitiOk = EsitiProperties.getInstance(log,protocollo).getEsitiCodeOk();
			for (Integer esitoOk : esitiOk) {
				if(esitoOk!=null && esito.getCode()!=null) {
					if(esitoOk.intValue() == esito.getCode().intValue()){
						return true;
					}
				}
			}
			return false;
		}catch(Exception e){
			// non sono previsti errori
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	
	
	
	/* ***** UPDATE STATO ******* */
	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("StatisticsCollection");
	public static void update(Statistic stat){
		semaphore.acquireThrowRuntime("update");
		try {
			updateEngine(stat);
		}finally {
			semaphore.release("update");
		}
	}
	private static void updateEngine(Statistic stat){

		if(!TipoPdD.APPLICATIVA.equals(stat.getTipoPdD()) &&
				!TipoPdD.DELEGATA.equals(stat.getTipoPdD()) ){
			return;
		}
		
		// Check soglia temporale
		if(StatisticsCollection.getStatisticsCollection().dataUltimoRefresh>0){
			if((DateManager.getTimeMillis()-StatisticsCollection.getStatisticsCollection().dataUltimoRefresh)>StatisticsCollection.SOGLIA_TEMPORALE){
				StatisticsCollection.reset();
			}
		}
		else{
			// Inserisco data arrivo richiesta 
			StatisticsCollection.getStatisticsCollection().dataUltimoRefresh = stat.getTimeMillisIngressoRichiesta();
			if(StatisticsCollection.getStatisticsCollection().dataUltimoRefresh<=0){
				StatisticsCollection.getStatisticsCollection().dataUltimoRefresh = DateManager.getTimeMillis();
			}
		}
		
		// Cechk soglia sulla dimensione
		/**if(needResetForOverSizeLong()){
			reset();
		}*/
		
		// Contatori
		StatisticsCollection.incrementCount(stat.getEsito(), stat.getProtocollo(), StatisticsCollection.statisticsCollection.statNumeroTransazioni);
		if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
			StatisticsCollection.incrementCount(stat.getEsito(), stat.getProtocollo(), StatisticsCollection.statisticsCollection.statNumeroTransazioniPA);
		}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
			StatisticsCollection.incrementCount(stat.getEsito(), stat.getProtocollo(), StatisticsCollection.statisticsCollection.statNumeroTransazioniPD);
		}
		
		// Latenze
		if(isEsitoOk(stat.getEsito(),stat.getProtocollo())){
			
			long latenzaTotale = -1;
			if(stat.getTimeMillisUscitaRisposta()>0 && stat.getTimeMillisIngressoRichiesta()>0)
				latenzaTotale = stat.getTimeMillisUscitaRisposta() - stat.getTimeMillisIngressoRichiesta();
			if(latenzaTotale>0){
				StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPA);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPD);
				}	
			}
			
			long latenzaRichiesta = -1;
			if(stat.getTimeMillisUscitaRichiesta()>0 && stat.getTimeMillisIngressoRichiesta()>0)
				latenzaRichiesta = stat.getTimeMillisUscitaRichiesta() - stat.getTimeMillisIngressoRichiesta();
			if(latenzaRichiesta>0){
				StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoRequest);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPARequest);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPDRequest);
				}	
			}
			
			long latenzaRisposta = -1;
			if(stat.getTimeMillisUscitaRisposta()>0 && stat.getTimeMillisIngressoRisposta()>0)
				latenzaRisposta = stat.getTimeMillisUscitaRisposta() - stat.getTimeMillisIngressoRisposta();
			if(latenzaRisposta>0){
				StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoResponse);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPAResponse);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamentoPDResponse);
				}	
			}
				
		}
		
		
		// Dimensioni
		
		long inRequestSize = stat.getDimensioneIngressoRichiesta();
		if(inRequestSize>0){
			StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioInRequest);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPA);
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPAInRequest);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPD);
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPDInRequest);
			}
		}
		
		long outRequestSize = stat.getDimensioneUscitaRichiesta();
		if(outRequestSize>0){
			StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioOutRequest);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPA);
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPAOutRequest);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPD);
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPDOutRequest);
			}
		}
		
		long inResponseSize = stat.getDimensioneIngressoRisposta();
		if(inResponseSize>0){
			StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioInResponse);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPA);
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPAInResponse);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPD);
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPDInResponse);
			}
		}
		
		long outResponseSize = stat.getDimensioneUscitaRisposta();
		if(outResponseSize>0){
			StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioOutResponse);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPA);
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPAOutResponse);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPD);
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggioPDOutResponse);
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private static boolean needResetForOverSizeLong(){
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoRequest.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoResponse.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPD.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPDRequest.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPDResponse.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPA.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPARequest.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPAResponse.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}

		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioInRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioOutRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioInResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioOutResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPD.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDInRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDOutRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDInResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDOutResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}

		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPA.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAInRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAOutRequest.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAInResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAOutResponse.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		return false;
		
	}
	
	
	public static void reset(){
		
		StatisticsCollection.getStatisticsCollection().dataUltimoRefresh = -1;
		
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioni = new StatisticCount();
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioniPD = new StatisticCount();
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioniPA = new StatisticCount();
		
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoRequest = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoResponse = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPD = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPDRequest = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPDResponse = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPA = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPARequest = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamentoPAResponse = new StatisticTime();
		
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioInRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioOutRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioInResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioOutResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPD = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDInRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDOutRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDInResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPDOutResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPA = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAInRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAOutRequest = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAInResponse = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggioPAOutResponse = new StatisticSize();
	}
		
	private static void incrementCount(EsitoTransazione esito,IProtocolFactory<?> protocollo, StatisticCount statCount){
		statCount.numeroTransazioni++;
		if(isEsitoOk(esito,protocollo)){
			statCount.numeroTransazioni_esitoOK++;
		}else{
			statCount.numeroTransazioni_esitoErrore++;
		}
	}
	
	private static void computeLatenza(long latenza,StatisticTime statTime){
		
		// Minima
		if(statTime.latenzaMinimaAttraversamento<=0 || latenza<statTime.latenzaMinimaAttraversamento)
			statTime.latenzaMinimaAttraversamento = latenza;
		
		// Massima
		if(latenza>statTime.latenzaMassimaAttraversamento)
			statTime.latenzaMassimaAttraversamento = latenza;
		
		// Media
		statTime.tmp_latenzaMediaAttraversamento_sumLatenze+=latenza;
		statTime.tmp_latenzaMediaAttraversamento_countTransazioni++;
		statTime.latenzaMediaAttraversamento = statTime.tmp_latenzaMediaAttraversamento_sumLatenze/statTime.tmp_latenzaMediaAttraversamento_countTransazioni;
		// Effettuo refresh nel controllo iniziale, almeno tutti i dati sono consistenti rispetto all'intervallo temporale
/**		if(statTime.tmp_latenzaMediaAttraversamento_sumLatenze>SOGLIA_DIMENSIONE){
//			// azzero temporanei
//			statTime.tmp_latenzaMediaAttraversamento_sumLatenze=statTime.tmp_latenzaMediaAttraversamento_sumLatenze;
//			statTime.tmp_latenzaMediaAttraversamento_countTransazioni=1;
//		}*/
	}
	
	
	
	private static void computeDimensione(long dimensione,StatisticSize statSize){
		
		// Minima
		if(statSize.dimensioneMinimaMessaggio<=0 || dimensione<statSize.dimensioneMinimaMessaggio)
			statSize.dimensioneMinimaMessaggio = dimensione;
		
		// Massima
		if(dimensione>statSize.dimensioneMassimaMessaggio)
			statSize.dimensioneMassimaMessaggio = dimensione;
		
		// Media
		statSize.tmp_dimensioneMediaMessaggio_sumDimensioni+=dimensione;
		statSize.tmp_dimensioneMediaMessaggio_countTransazioni++;
		statSize.dimensioneMediaMessaggio = statSize.tmp_dimensioneMediaMessaggio_sumDimensioni/statSize.tmp_dimensioneMediaMessaggio_countTransazioni;
		// Effettuo refresh nel controllo iniziale, almeno tutti i dati sono consistenti rispetto all'intervallo temporale
/**		if(statSize.tmp_dimensioneMediaMessaggio_sumDimensioni>SOGLIA_DIMENSIONE){
//			// azzero temporanei
//			statSize.tmp_dimensioneMediaMessaggio_sumDimensioni=statSize.dimensioneMediaMessaggio;
//			statSize.tmp_dimensioneMediaMessaggio_countTransazioni=1;
//		}*/
	}
	
	
	
	/* BEAN */
	
	long dataUltimoRefresh = -1;
	
	StatisticCount statNumeroTransazioni = new StatisticCount();
	StatisticCount statNumeroTransazioniPD = new StatisticCount();
	StatisticCount statNumeroTransazioniPA = new StatisticCount();
	
	StatisticTime statLatenzaAttraversamento = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoRequest = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoResponse = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPD = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPDRequest = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPDResponse = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPA = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPARequest = new StatisticTime();
	StatisticTime statLatenzaAttraversamentoPAResponse = new StatisticTime();
	
	StatisticSize statDimensioneMessaggio = new StatisticSize();
	StatisticSize statDimensioneMessaggioInRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioOutRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioInResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioOutResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioPD = new StatisticSize();
	StatisticSize statDimensioneMessaggioPDInRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioPDOutRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioPDInResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioPDOutResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioPA = new StatisticSize();
	StatisticSize statDimensioneMessaggioPAInRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioPAOutRequest = new StatisticSize();
	StatisticSize statDimensioneMessaggioPAInResponse = new StatisticSize();
	StatisticSize statDimensioneMessaggioPAOutResponse = new StatisticSize();
	
	
	public StatisticSize getStatDimensioneMessaggio() {
		return this.statDimensioneMessaggio;
	}

	public StatisticSize getStatDimensioneMessaggioInRequest() {
		return this.statDimensioneMessaggioInRequest;
	}

	public StatisticSize getStatDimensioneMessaggioOutRequest() {
		return this.statDimensioneMessaggioOutRequest;
	}

	public StatisticSize getStatDimensioneMessaggioInResponse() {
		return this.statDimensioneMessaggioInResponse;
	}

	public StatisticSize getStatDimensioneMessaggioOutResponse() {
		return this.statDimensioneMessaggioOutResponse;
	}

	public StatisticSize getStatDimensioneMessaggioResponse() {
		return this.statDimensioneMessaggioResponse;
	}

	public StatisticSize getStatDimensioneMessaggioPD() {
		return this.statDimensioneMessaggioPD;
	}

	public StatisticSize getStatDimensioneMessaggioPDInRequest() {
		return this.statDimensioneMessaggioPDInRequest;
	}

	public StatisticSize getStatDimensioneMessaggioPDOutRequest() {
		return this.statDimensioneMessaggioPDOutRequest;
	}

	public StatisticSize getStatDimensioneMessaggioPDInResponse() {
		return this.statDimensioneMessaggioPDInResponse;
	}

	public StatisticSize getStatDimensioneMessaggioPDOutResponse() {
		return this.statDimensioneMessaggioPDOutResponse;
	}

	public StatisticSize getStatDimensioneMessaggioPA() {
		return this.statDimensioneMessaggioPA;
	}

	public StatisticSize getStatDimensioneMessaggioPAInRequest() {
		return this.statDimensioneMessaggioPAInRequest;
	}

	public StatisticSize getStatDimensioneMessaggioPAOutRequest() {
		return this.statDimensioneMessaggioPAOutRequest;
	}

	public StatisticSize getStatDimensioneMessaggioPAInResponse() {
		return this.statDimensioneMessaggioPAInResponse;
	}

	public StatisticSize getStatDimensioneMessaggioPAOutResponse() {
		return this.statDimensioneMessaggioPAOutResponse;
	}

	public StatisticCount getStatNumeroTransazioni() {
		return this.statNumeroTransazioni;
	}

	public StatisticCount getStatNumeroTransazioniPD() {
		return this.statNumeroTransazioniPD;
	}

	public StatisticCount getStatNumeroTransazioniPA() {
		return this.statNumeroTransazioniPA;
	}

	public StatisticTime getStatLatenzaAttraversamento() {
		return this.statLatenzaAttraversamento;
	}

	public StatisticTime getStatLatenzaAttraversamentoRequest() {
		return this.statLatenzaAttraversamentoRequest;
	}

	public StatisticTime getStatLatenzaAttraversamentoResponse() {
		return this.statLatenzaAttraversamentoResponse;
	}

	public StatisticTime getStatLatenzaAttraversamentoPD() {
		return this.statLatenzaAttraversamentoPD;
	}

	public StatisticTime getStatLatenzaAttraversamentoPDRequest() {
		return this.statLatenzaAttraversamentoPDRequest;
	}

	public StatisticTime getStatLatenzaAttraversamentoPDResponse() {
		return this.statLatenzaAttraversamentoPDResponse;
	}

	public StatisticTime getStatLatenzaAttraversamentoPA() {
		return this.statLatenzaAttraversamentoPA;
	}

	public StatisticTime getStatLatenzaAttraversamentoPARequest() {
		return this.statLatenzaAttraversamentoPARequest;
	}

	public StatisticTime getStatLatenzaAttraversamentoPAResponse() {
		return this.statLatenzaAttraversamentoPAResponse;
	}

	public long getDataUltimoRefresh() {
		return this.dataUltimoRefresh;
	}
	
}
