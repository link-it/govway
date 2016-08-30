/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.pdd.core.handlers.statistics;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;

/**
 * StatisticsCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsCollection {

	private static final StatisticsCollection statisticsCollection =  new StatisticsCollection();
	public static StatisticsCollection getStatisticsCollection(){
		return StatisticsCollection.statisticsCollection;
	}

	private static final long SOGLIA_DIMENSIONE = Long.MAX_VALUE-100000;
	private static final int SOGLIA_TEMPORALE = 1000*60*30; // statistiche relative all'ultima mezz'ora.
	
	private static EsitiProperties esitiProperties = null;
	private static synchronized void initEsitiProperties(){
		if(esitiProperties==null){
			try{
				esitiProperties = EsitiProperties.getInstance(LoggerWrapperFactory.getLogger(StatisticsCollection.class));
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}
	private static boolean isEsitoOk(EsitoTransazione esito){
		try{
			if(esitiProperties==null){
				initEsitiProperties();
			}
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk();
			for (Integer esitoOk : esitiOk) {
				if(esitoOk == esito.getCode()){
					return true;
				}
			}
			return false;
		}catch(Exception e){
			// non sono previsti errori
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	
	/* ***** UPDATE STATO ******* */
	public static synchronized void update(Statistic stat){

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
		/*if(needResetForOverSizeLong()){
			reset();
		}*/
		
		// Contatori
		StatisticsCollection.incrementCount(stat.getEsito(), StatisticsCollection.statisticsCollection.statNumeroTransazioni);
		if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
			StatisticsCollection.incrementCount(stat.getEsito(), StatisticsCollection.statisticsCollection.statNumeroTransazioni_PA);
		}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
			StatisticsCollection.incrementCount(stat.getEsito(), StatisticsCollection.statisticsCollection.statNumeroTransazioni_PD);
		}
		
		// Latenze
		if(isEsitoOk(stat.getEsito())){
			
			long latenzaTotale = -1;
			if(stat.getTimeMillisUscitaRisposta()>0 && stat.getTimeMillisIngressoRichiesta()>0)
				latenzaTotale = stat.getTimeMillisUscitaRisposta() - stat.getTimeMillisIngressoRichiesta();
			if(latenzaTotale>0){
				StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PA);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaTotale, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PD);
				}	
			}
			
			long latenzaRichiesta = -1;
			if(stat.getTimeMillisUscitaRichiesta()>0 && stat.getTimeMillisIngressoRichiesta()>0)
				latenzaRichiesta = stat.getTimeMillisUscitaRichiesta() - stat.getTimeMillisIngressoRichiesta();
			if(latenzaRichiesta>0){
				StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_request);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PA_request);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRichiesta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PD_request);
				}	
			}
			
			long latenzaRisposta = -1;
			if(stat.getTimeMillisUscitaRisposta()>0 && stat.getTimeMillisIngressoRisposta()>0)
				latenzaRisposta = stat.getTimeMillisUscitaRisposta() - stat.getTimeMillisIngressoRisposta();
			if(latenzaRisposta>0){
				StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_response);
				if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PA_response);
				}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
					StatisticsCollection.computeLatenza(latenzaRisposta, StatisticsCollection.statisticsCollection.statLatenzaAttraversamento_PD_response);
				}	
			}
				
		}
		
		
		// Dimensioni
		
		long inRequestSize = stat.getDimensioneIngressoRichiesta();
		if(inRequestSize>0){
			StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_in_request);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA);
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA_in_request);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD);
				StatisticsCollection.computeDimensione(inRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD_in_request);
			}
		}
		
		long outRequestSize = stat.getDimensioneUscitaRichiesta();
		if(outRequestSize>0){
			StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_out_request);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA);
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA_out_request);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD);
				StatisticsCollection.computeDimensione(outRequestSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD_out_request);
			}
		}
		
		long inResponseSize = stat.getDimensioneIngressoRisposta();
		if(inResponseSize>0){
			StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_in_response);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA);
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA_in_response);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD);
				StatisticsCollection.computeDimensione(inResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD_in_response);
			}
		}
		
		long outResponseSize = stat.getDimensioneUscitaRisposta();
		if(outResponseSize>0){
			StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio);
			StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_out_response);
			if(TipoPdD.APPLICATIVA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA);
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PA_out_response);
			}else if(TipoPdD.DELEGATA.equals(stat.getTipoPdD())){
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD);
				StatisticsCollection.computeDimensione(outResponseSize, StatisticsCollection.statisticsCollection.statDimensioneMessaggio_PD_out_response);
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private static boolean needResetForOverSizeLong(){
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_request.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_response.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD_request.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD_response.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA_request.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA_response.tmp_latenzaMediaAttraversamento_sumLatenze>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}

		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_in_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_out_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_in_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_out_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_in_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_out_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_in_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_out_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}

		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_in_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_out_request.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_in_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		if(StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_out_response.tmp_dimensioneMediaMessaggio_sumDimensioni>StatisticsCollection.SOGLIA_DIMENSIONE){
			return true;
		}
		
		return false;
		
	}
	
	
	public static void reset(){
		
		StatisticsCollection.getStatisticsCollection().dataUltimoRefresh = -1;
		
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioni = new StatisticCount();
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioni_PD = new StatisticCount();
		StatisticsCollection.getStatisticsCollection().statNumeroTransazioni_PA = new StatisticCount();
		
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_request = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_response = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD_request = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PD_response = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA_request = new StatisticTime();
		StatisticsCollection.getStatisticsCollection().statLatenzaAttraversamento_PA_response = new StatisticTime();
		
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_in_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_out_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_in_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_out_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_in_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_out_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_in_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PD_out_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_in_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_out_request = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_in_response = new StatisticSize();
		StatisticsCollection.getStatisticsCollection().statDimensioneMessaggio_PA_out_response = new StatisticSize();
	}
		
	private static void incrementCount(EsitoTransazione esito,StatisticCount statCount){
		statCount.numeroTransazioni++;
		if(isEsitoOk(esito)){
			statCount.numeroTransazioni_esitoOK++;
		}else{
			statCount.numeroTransazioni_esitoErrore++;
		}
	}
	
	private static void computeLatenza(long latenza,StatisticTime statTime){
		
		// Minima
		if(statTime.latenzaMinimaAttraversamento<=0)
			statTime.latenzaMinimaAttraversamento = latenza;
		else if(latenza<statTime.latenzaMinimaAttraversamento)
			statTime.latenzaMinimaAttraversamento = latenza;
		
		// Massima
		if(latenza>statTime.latenzaMassimaAttraversamento)
			statTime.latenzaMassimaAttraversamento = latenza;
		
		// Media
		statTime.tmp_latenzaMediaAttraversamento_sumLatenze+=latenza;
		statTime.tmp_latenzaMediaAttraversamento_countTransazioni++;
		statTime.latenzaMediaAttraversamento = statTime.tmp_latenzaMediaAttraversamento_sumLatenze/statTime.tmp_latenzaMediaAttraversamento_countTransazioni;
		// Effettuo refresh nel controllo iniziale, almeno tutti i dati sono consistenti rispetto all'intervallo temporale
//		if(statTime.tmp_latenzaMediaAttraversamento_sumLatenze>SOGLIA_DIMENSIONE){
//			// azzero temporanei
//			statTime.tmp_latenzaMediaAttraversamento_sumLatenze=statTime.tmp_latenzaMediaAttraversamento_sumLatenze;
//			statTime.tmp_latenzaMediaAttraversamento_countTransazioni=1;
//		}
	}
	
	
	
	private static void computeDimensione(long dimensione,StatisticSize statSize){
		
		// Minima
		if(statSize.dimensioneMinimaMessaggio<=0)
			statSize.dimensioneMinimaMessaggio = dimensione;
		else if(dimensione<statSize.dimensioneMinimaMessaggio)
			statSize.dimensioneMinimaMessaggio = dimensione;
		
		// Massima
		if(dimensione>statSize.dimensioneMassimaMessaggio)
			statSize.dimensioneMassimaMessaggio = dimensione;
		
		// Media
		statSize.tmp_dimensioneMediaMessaggio_sumDimensioni+=dimensione;
		statSize.tmp_dimensioneMediaMessaggio_countTransazioni++;
		statSize.dimensioneMediaMessaggio = statSize.tmp_dimensioneMediaMessaggio_sumDimensioni/statSize.tmp_dimensioneMediaMessaggio_countTransazioni;
		// Effettuo refresh nel controllo iniziale, almeno tutti i dati sono consistenti rispetto all'intervallo temporale
//		if(statSize.tmp_dimensioneMediaMessaggio_sumDimensioni>SOGLIA_DIMENSIONE){
//			// azzero temporanei
//			statSize.tmp_dimensioneMediaMessaggio_sumDimensioni=statSize.dimensioneMediaMessaggio;
//			statSize.tmp_dimensioneMediaMessaggio_countTransazioni=1;
//		}
	}
	
	
	
	/* BEAN */
	
	long dataUltimoRefresh = -1;
	
	StatisticCount statNumeroTransazioni = new StatisticCount();
	StatisticCount statNumeroTransazioni_PD = new StatisticCount();
	StatisticCount statNumeroTransazioni_PA = new StatisticCount();
	
	StatisticTime statLatenzaAttraversamento = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_request = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_response = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PD = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PD_request = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PD_response = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PA = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PA_request = new StatisticTime();
	StatisticTime statLatenzaAttraversamento_PA_response = new StatisticTime();
	
	StatisticSize statDimensioneMessaggio = new StatisticSize();
	StatisticSize statDimensioneMessaggio_in_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_out_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_in_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_out_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PD = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PD_in_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PD_out_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PD_in_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PD_out_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PA = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PA_in_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PA_out_request = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PA_in_response = new StatisticSize();
	StatisticSize statDimensioneMessaggio_PA_out_response = new StatisticSize();
	
	
	public StatisticSize getStatDimensioneMessaggio() {
		return this.statDimensioneMessaggio;
	}

	public StatisticSize getStatDimensioneMessaggio_in_request() {
		return this.statDimensioneMessaggio_in_request;
	}

	public StatisticSize getStatDimensioneMessaggio_out_request() {
		return this.statDimensioneMessaggio_out_request;
	}

	public StatisticSize getStatDimensioneMessaggio_in_response() {
		return this.statDimensioneMessaggio_in_response;
	}

	public StatisticSize getStatDimensioneMessaggio_out_response() {
		return this.statDimensioneMessaggio_out_response;
	}

	public StatisticSize getStatDimensioneMessaggio_response() {
		return this.statDimensioneMessaggio_response;
	}

	public StatisticSize getStatDimensioneMessaggio_PD() {
		return this.statDimensioneMessaggio_PD;
	}

	public StatisticSize getStatDimensioneMessaggio_PD_in_request() {
		return this.statDimensioneMessaggio_PD_in_request;
	}

	public StatisticSize getStatDimensioneMessaggio_PD_out_request() {
		return this.statDimensioneMessaggio_PD_out_request;
	}

	public StatisticSize getStatDimensioneMessaggio_PD_in_response() {
		return this.statDimensioneMessaggio_PD_in_response;
	}

	public StatisticSize getStatDimensioneMessaggio_PD_out_response() {
		return this.statDimensioneMessaggio_PD_out_response;
	}

	public StatisticSize getStatDimensioneMessaggio_PA() {
		return this.statDimensioneMessaggio_PA;
	}

	public StatisticSize getStatDimensioneMessaggio_PA_in_request() {
		return this.statDimensioneMessaggio_PA_in_request;
	}

	public StatisticSize getStatDimensioneMessaggio_PA_out_request() {
		return this.statDimensioneMessaggio_PA_out_request;
	}

	public StatisticSize getStatDimensioneMessaggio_PA_in_response() {
		return this.statDimensioneMessaggio_PA_in_response;
	}

	public StatisticSize getStatDimensioneMessaggio_PA_out_response() {
		return this.statDimensioneMessaggio_PA_out_response;
	}

	public StatisticCount getStatNumeroTransazioni() {
		return this.statNumeroTransazioni;
	}

	public StatisticCount getStatNumeroTransazioni_PD() {
		return this.statNumeroTransazioni_PD;
	}

	public StatisticCount getStatNumeroTransazioni_PA() {
		return this.statNumeroTransazioni_PA;
	}

	public StatisticTime getStatLatenzaAttraversamento() {
		return this.statLatenzaAttraversamento;
	}

	public StatisticTime getStatLatenzaAttraversamento_request() {
		return this.statLatenzaAttraversamento_request;
	}

	public StatisticTime getStatLatenzaAttraversamento_response() {
		return this.statLatenzaAttraversamento_response;
	}

	public StatisticTime getStatLatenzaAttraversamento_PD() {
		return this.statLatenzaAttraversamento_PD;
	}

	public StatisticTime getStatLatenzaAttraversamento_PD_request() {
		return this.statLatenzaAttraversamento_PD_request;
	}

	public StatisticTime getStatLatenzaAttraversamento_PD_response() {
		return this.statLatenzaAttraversamento_PD_response;
	}

	public StatisticTime getStatLatenzaAttraversamento_PA() {
		return this.statLatenzaAttraversamento_PA;
	}

	public StatisticTime getStatLatenzaAttraversamento_PA_request() {
		return this.statLatenzaAttraversamento_PA_request;
	}

	public StatisticTime getStatLatenzaAttraversamento_PA_response() {
		return this.statLatenzaAttraversamento_PA_response;
	}

	public long getDataUltimoRefresh() {
		return this.dataUltimoRefresh;
	}
	
}
