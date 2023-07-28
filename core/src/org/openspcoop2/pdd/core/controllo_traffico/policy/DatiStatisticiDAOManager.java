/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.constants.TipoPorta;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.core.statistiche.utils.StatisticheUtils;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneGatewayControlloTraffico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**     
 * DatiStatisticiDAOManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiStatisticiDAOManager  {

	private static DatiStatisticiDAOManager staticInstance = null;
	public static synchronized void initialize(ConfigurazioneGatewayControlloTraffico configurazioneControlloTraffico) throws Exception{
		if(staticInstance==null){
			staticInstance = new DatiStatisticiDAOManager(configurazioneControlloTraffico);
		}
	}
	public static DatiStatisticiDAOManager getInstance() throws Exception{
		if(staticInstance==null){
			throw new Exception("DatiStatisticiDAOManager non inizializzato");
		}
		return staticInstance;
	}
	
	private ConfigurazioneGatewayControlloTraffico configurazioneControlloTraffico;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
		
	/** Database */
//	private DataSource ds = null;
//	private String datasource = null;
	private String tipoDatabase = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private Logger daoFactoryLogger = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesStatistiche = null;
	
	private DBStatisticheManager dbStatisticheManager = null;
	private boolean checkState = false;
	
	private Logger log;
	
	private DatiStatisticiDAOManager(ConfigurazioneGatewayControlloTraffico configurazioneControlloTraffico) throws Exception{
		try{
			
    		this.configurazioneControlloTraffico = configurazioneControlloTraffico;
    		
    		this.debug = this.configurazioneControlloTraffico.isDebug();
    		
			this.tipoDatabase = this.configurazioneControlloTraffico.getTipoDatabaseConfig();
			
			if(this.tipoDatabase==null){
				throw new Exception("Tipo Database non definito");
			}

			// DAOFactory
			DAOFactoryProperties daoFactoryProperties = null;
			this.daoFactoryLogger = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTrafficoSql(this.debug);
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLogger);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLogger);
						
			this.daoFactoryServiceManagerPropertiesStatistiche = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesStatistiche.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesStatistiche.setDatabaseType(this.tipoDatabase);
			
			this.dbStatisticheManager = DBStatisticheManager.getInstance();
			if(this.dbStatisticheManager.useRuntimePdD()) {
				this.checkState = true;
    		}
			else if(this.dbStatisticheManager.useTransazioni()) {
				if(DBTransazioniManager.getInstance().useRuntimePdD()) {
					this.checkState = true;
				}
    		}
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(this.debug);
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del datasource: "+e.getMessage(),e);
		}
    	
    }
    
	
	
	
	
	
	/* ********************** NUMERO RICHIESTE ************************** */
	
    public RisultatoStatistico readNumeroRichieste(String key,TipoRisorsa tipoRisorsa,
    		TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, Date leftInterval, Date rightInterval,
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state,
			RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory) throws Exception{
	
    	Resource r = null;
    	boolean useConnectionRuntime = false;
		IDSoggetto dominio = datiTransazione.getDominio();
    	String idModulo = datiTransazione.getModulo()+".statistiche.readNumeroRichieste";
    	String idTransazione = datiTransazione.getIdTransazione();
    	try{
			
    		Connection con = null;
			if(this.checkState) {
				if(state!=null) {
					if(state instanceof StateMessage) {
						StateMessage s = (StateMessage) state;
						if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
							con = s.getConnectionDB();
							useConnectionRuntime = true;
						}
					}
				}
			}
			if(useConnectionRuntime==false){
				r = this.dbStatisticheManager.getResource(dominio, idModulo, idTransazione);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				con = (Connection) r.getResource();
			}
			if(con == null)
				throw new Exception("Connessione non disponibile");	
    		
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							con, this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;
		
			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case SETTIMANALE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneSettimanale_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					}
					else {
						model = StatisticaSettimanale.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					}
					break;
				case MENSILE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneMensile_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					}
					else {
						model = StatisticaMensile.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaMensileServiceSearch();
					}
					break;
				}
			}
			if(model==null) {
				throw new Exception("Model unknown");
			}
			if(dao==null) {
				throw new Exception("DAO unknown");
			}
			
			IExpression expression = this.createWhereExpressionNumeroRichieste(dao, model, tipoRisorsa, leftInterval, rightInterval, 
					datiTransazione.getTipoPdD(), protocolFactory, datiTransazione.getProtocollo(), groupByPolicy, filtro, state, requestInfo);
						
			FunctionField ff = new  FunctionField(model.NUMERO_TRANSAZIONI, Function.SUM, "somma");
			
			RisultatoStatistico risultato = new RisultatoStatistico();
			risultato.setDataInizio(leftInterval);
			risultato.setDataFine(rightInterval);
			risultato.setDateCheck(DateManager.getDate());
			
			try{
				Object result = dao.aggregate(expression, ff);
				if(result!=null && this.isKnownType(result) ){
					//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
					this.log.debug("NumeroRichiesteFound ["+result.getClass().getName()+"]: "+result);
					risultato.setRisultato(this.translateType(result));
				}
				else{
					if(result!=null){
						//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
						this.log.debug("NumeroRichiesteNotFound ["+result.getClass().getName()+"]: "+result);
					}else{
						this.log.debug("NumeroRichiesteNotFound, result is null");
					}
					risultato.setRisultato(0);
				}
			}catch(NotFoundException notFound){
				this.log.debug("NumeroRichiesteNotFound:"+notFound.getMessage(),notFound);
				risultato.setRisultato(0);
			}
			
			return risultato;
			
		}catch(Exception e){
			this.log.error("Errore durante la raccolta dei dati statisti (key:"+key+"): "+e.getMessage(),e);
			throw e;
		}finally {
			try{
				if(useConnectionRuntime==false) {
					if(r!=null) {
						this.dbStatisticheManager.releaseResource(dominio, idModulo, r);
					}
				}
			}catch(Exception eClose){}
		}
    	
	}
    
    private IExpression createWhereExpressionNumeroRichieste(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IProtocolFactory<?> protocolFactory, String protocollo,
			IDUnivocoGroupByPolicy groupByPolicy,
			AttivazionePolicyFiltro filtro,
			IState state,
			RequestInfo requestInfo) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, protocolFactory, protocollo, groupByPolicy, filtro, state, requestInfo);

    	expr.isNotNull(model.NUMERO_TRANSAZIONI);
    	
		return expr;
    	
    }
	
	
	
	
	
	
	
	
	
	/* ********************** OCCUPAZIONE BANDA ************************** */
	
    public RisultatoStatistico readOccupazioneBanda(String key,TipoRisorsa tipoRisorsa,
    		TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, Date leftInterval, Date rightInterval,
    		TipoBanda tipoBanda,
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state,
			RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory) throws Exception{
	
    	Resource r = null;
    	boolean useConnectionRuntime = false;
		IDSoggetto dominio = datiTransazione.getDominio();
    	String idModulo = datiTransazione.getModulo()+".statistiche.readOccupazioneBanda";
    	String idTransazione = datiTransazione.getIdTransazione();
    	try{
    		Connection con = null;
			if(this.checkState) {
				if(state!=null) {
					if(state instanceof StateMessage) {
						StateMessage s = (StateMessage) state;
						if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
							con = s.getConnectionDB();
							useConnectionRuntime = true;
						}
					}
				}
			}
			if(useConnectionRuntime==false){
				r = this.dbStatisticheManager.getResource(dominio, idModulo, idTransazione);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				con = (Connection) r.getResource();
			}
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							con, this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case SETTIMANALE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneSettimanale_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					}
					else {
						model = StatisticaSettimanale.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					}
					break;
				case MENSILE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneMensile_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					}
					else {
						model = StatisticaMensile.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaMensileServiceSearch();
					}
					break;
				}
			}
			
			if(model==null) {
				throw new Exception("Model unknown");
			}
			if(dao==null) {
				throw new Exception("DAO unknown");
			}
			
			IExpression expression = this.createWhereExpressionBanda(dao, model, tipoRisorsa, leftInterval, rightInterval, 
					datiTransazione.getTipoPdD(), protocolFactory, datiTransazione.getProtocollo(), groupByPolicy, filtro, tipoBanda, state, requestInfo);
						
			FunctionField ff = null;
			switch (tipoBanda) {
			case COMPLESSIVA:
				ff = new  FunctionField(model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA, Function.SUM, "somma");
				break;
			case INTERNA:
				ff = new  FunctionField(model.DIMENSIONI_BYTES_BANDA_INTERNA, Function.SUM, "somma");
				break;
			case ESTERNA:
				ff = new  FunctionField(model.DIMENSIONI_BYTES_BANDA_ESTERNA, Function.SUM, "somma");
				break;
			}
			
			
			RisultatoStatistico risultato = new RisultatoStatistico();
			risultato.setDataInizio(leftInterval);
			risultato.setDataFine(rightInterval);
			risultato.setDateCheck(DateManager.getDate());
			
			try{
				Object result = dao.aggregate(expression, ff);
				if(result!=null && this.isKnownType(result) ){
					//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
					this.log.debug("BandaFound ["+result.getClass().getName()+"]: "+result);
					risultato.setRisultato(this.translateType(result));
				}
				else{
					if(result!=null){
						//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
						this.log.debug("BandaNotFound ["+result.getClass().getName()+"]: "+result);
					}else{
						this.log.debug("BandaNotFound, result is null");
					}
					risultato.setRisultato(0);
				}
			}catch(NotFoundException notFound){
				this.log.debug("BandaNotFound:"+notFound.getMessage(),notFound);
				risultato.setRisultato(0);
			}
			
			return risultato;
			
		}catch(Exception e){
			this.log.error("Errore durante la raccolta dei dati statisti (key:"+key+"): "+e.getMessage(),e);
			throw e;
		}finally {
			try{
				if(useConnectionRuntime==false) {
					if(r!=null) {
						this.dbStatisticheManager.releaseResource(dominio, idModulo, r);
					}
				}
			}catch(Exception eClose){}
		}
    	
	}
    
    private IExpression createWhereExpressionBanda(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IProtocolFactory<?> protocolFactory, String protocollo,
			IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
			TipoBanda tipoBanda,
			IState state,
			RequestInfo requestInfo) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, protocolFactory, protocollo, groupByPolicy, filtro, state, requestInfo);

    	switch (tipoBanda) {
		case COMPLESSIVA:
			expr.isNotNull(model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA);
			break;
		case INTERNA:
			expr.isNotNull(model.DIMENSIONI_BYTES_BANDA_INTERNA);
			break;
		case ESTERNA:
			expr.isNotNull(model.DIMENSIONI_BYTES_BANDA_ESTERNA);
			break;
		}
		
		return expr;
    	
    }
	
	
	
	/* ********************** LATENZA ************************** */
	
    public RisultatoStatistico readLatenza(String key,TipoRisorsa tipoRisorsa, 
    		TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, Date leftInterval, Date rightInterval,
    		TipoLatenza tipoLatenza,
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
    		IState state,
			RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory) throws Exception{
	
    	Resource r = null;
    	boolean useConnectionRuntime = false;
		IDSoggetto dominio = datiTransazione.getDominio();
    	String idModulo = datiTransazione.getModulo()+".statistiche.readLatenza";
    	String idTransazione = datiTransazione.getIdTransazione();
    	try{
    		Connection con = null;
			if(this.checkState) {
				if(state!=null) {
					if(state instanceof StateMessage) {
						StateMessage s = (StateMessage) state;
						if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
							con = s.getConnectionDB();
							useConnectionRuntime = true;
						}
					}
				}
			}
			if(useConnectionRuntime==false){
				r = this.dbStatisticheManager.getResource(dominio, idModulo, idTransazione);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				con = (Connection) r.getResource();
			}
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							con, this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;
			boolean calcolaSommeMediaPesata = false;
			
			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case SETTIMANALE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneSettimanale_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
						calcolaSommeMediaPesata = this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneSettimanaleMensile_usaStatisticheGiornaliere_latenza_mediaPesata();
					}
					else {
						model = StatisticaSettimanale.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					}
					break;
				case MENSILE:
					if(this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneMensile_usaStatisticheGiornaliere()) {
						model = StatisticaGiornaliera.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
						calcolaSommeMediaPesata = this.configurazioneControlloTraffico.isElaborazioneStatistica_distribuzioneSettimanaleMensile_usaStatisticheGiornaliere_latenza_mediaPesata();
					}
					else {
						model = StatisticaMensile.model().STATISTICA_BASE;
						dao = statisticheSM.getStatisticaMensileServiceSearch();
					}
					break;
				}
			}
			
			if(model==null) {
				throw new Exception("Model unknown");
			}
			if(dao==null) {
				throw new Exception("DAO unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)dao).getFieldConverter(); 
			
			IExpression expression = this.createWhereExpressionLatenza(dao, model, tipoRisorsa, leftInterval, rightInterval, 
					datiTransazione.getTipoPdD(), protocolFactory, datiTransazione.getProtocollo(), groupByPolicy, filtro, tipoLatenza, state, requestInfo);
						
			FunctionField ff = null;
			switch (tipoLatenza) {
			case PORTA:
				//ff = new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza");
				ff = StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "somma_latenza");
				break;
			case SERVIZIO:
				//ff = new  FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza");
				ff = StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "somma_latenza");
				break;
			case TOTALE:
			default:
				//ff = new  FunctionField(model.LATENZA_TOTALE, Function.AVG, "somma_latenza");
				ff = StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "somma_latenza");
				break;
			}
			
			FunctionField ffSommaPesata = null;
			if(calcolaSommeMediaPesata) {
				// per media pesata
				ffSommaPesata = new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma_media_pesata");
			}
			
			RisultatoStatistico risultato = new RisultatoStatistico();
			risultato.setDataInizio(leftInterval);
			risultato.setDataFine(rightInterval);
			risultato.setDateCheck(DateManager.getDate());
			
			try{
				if(ffSommaPesata!=null) {
					
					expression.addGroupBy(model.DATA);
					
					List<Map<String, Object>> list = dao.groupBy(expression, ff, ffSommaPesata);
					if(list!=null && !list.isEmpty()) {
						long sommaMediePesate = 0;
						long sommaNumeroTransazioni = 0;
						for (Map<String, Object> map : list) {
							Object result = map.get("somma_latenza");
							Object resultPesata = map.get("somma_media_pesata");
							long latenza = 0;
							long numeroTransazioni = 0;
							if(result!=null && this.isKnownType(result) ){
								latenza = this.translateType(result);
							}
							if(resultPesata!=null && this.isKnownType(resultPesata) ){
								numeroTransazioni = this.translateType(resultPesata);
							}
							sommaNumeroTransazioni+=numeroTransazioni;
							long mediaPesata = latenza*numeroTransazioni;
							sommaMediePesate+=mediaPesata;
						}
						long risultatoPesato = (sommaNumeroTransazioni>0) ? (sommaMediePesate / sommaNumeroTransazioni) : 0;
						risultato.setRisultato(risultatoPesato);
					}
					else {
						this.log.debug("LatenzaNotFound");
						risultato.setRisultato(0);
					}
				}
				else {
					Object result = dao.aggregate(expression, ff);
					if(result!=null && this.isKnownType(result) ){
						//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
						this.log.debug("LatenzaFound ["+result.getClass().getName()+"]: "+result);
						risultato.setRisultato(this.translateType(result));
					}
					else{
						if(result!=null){
							//System.out.println("RITORNO OGGETTO LETTO DA DB CON CHIAVE ["+key+"]: ["+result+"] ["+result.getClass().getName()+"]");
							this.log.debug("LatenzaNotFound ["+result.getClass().getName()+"]: "+result);
						}else{
							this.log.debug("LatenzaNotFound, result is null");
						}
						risultato.setRisultato(0);
					}
				}
			}catch(NotFoundException notFound){
				this.log.debug("LatenzaNotFound:"+notFound.getMessage(),notFound);
				risultato.setRisultato(0);
			}
			
			return risultato;
			
		}catch(Exception e){
			this.log.error("Errore durante la raccolta dei dati statisti (key:"+key+"): "+e.getMessage(),e);
			throw e;
		}finally {
			try{
				if(useConnectionRuntime==false) {
					if(r!=null) {
						this.dbStatisticheManager.releaseResource(dominio, idModulo, r);
					}
				}
			}catch(Exception eClose){}
		}
    	
	}
    
    private IExpression createWhereExpressionLatenza(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IProtocolFactory<?> protocolFactory, String protocollo,
			IDUnivocoGroupByPolicy groupByPolicy, AttivazionePolicyFiltro filtro,
			TipoLatenza tipoLatenza,
			IState state,
			RequestInfo requestInfo) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, protocolFactory, protocollo, groupByPolicy, filtro, state, requestInfo);

		int [] esiti = null;
		if(TipoPdD.DELEGATA.equals(tipoPdDTransazioneInCorso)){
			esiti = this.configurazioneControlloTraffico.getCalcoloLatenzaPortaDelegataEsitiConsiderati().get(protocollo);
		}
		else{
			esiti = this.configurazioneControlloTraffico.getCalcoloLatenzaPortaApplicativaEsitiConsiderati().get(protocollo);		
		}
		
		// esito
		IExpression exprEsiti = dao.newExpression();
		exprEsiti.or();
		for (int i = 0; i < esiti.length; i++) {
			exprEsiti.equals(model.ESITO, esiti[i]);
		}
		expr.and(exprEsiti);
			
		
		switch (tipoLatenza) {
		case PORTA:
			expr.isNotNull(model.LATENZA_PORTA);
			break;
		case SERVIZIO:
			expr.isNotNull(model.LATENZA_SERVIZIO);
			break;
		case TOTALE:
		default:
			expr.isNotNull(model.LATENZA_TOTALE);
			break;
		}
		
		return expr;
    	
    }
    
    
    
    // ***************** UTILS ************************
    
    
    private IExpression createWhereExpression(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IProtocolFactory<?> protocolFactory, String protocollo,
			IDUnivocoGroupByPolicy groupByPolicy,
			AttivazionePolicyFiltro filtro,
			IState state,
			RequestInfo requestInfo) throws Exception {

		IExpression expr = null;

		expr = dao.newExpression();
		expr.and();
		
		// Data
		expr.between(model.DATA,
				dataInizio,
				dataFine);
		
		// Record validi
		StatisticheUtils.selezionaRecordValidi(expr, model);

		
        // escludo le transazioni con esito policy violate
		int [] esitiPolicyViolate = this.configurazioneControlloTraffico.getEsitiPolicyViolate().get(protocollo);    
		IExpression exprEsitiPolicyViolate = dao.newExpression();
		exprEsitiPolicyViolate.or();
		for (int i = 0; i < esitiPolicyViolate.length; i++) {
			exprEsitiPolicyViolate.equals(model.ESITO, esitiPolicyViolate[i]);
		}
		expr.not(exprEsitiPolicyViolate);

		
		// Aggiungo esiti specifici
		switch (tipoRisorsa) {
		case NUMERO_RICHIESTE:
		case DIMENSIONE_MASSIMA_MESSAGGIO:
		case OCCUPAZIONE_BANDA:
		case TEMPO_COMPLESSIVO_RISPOSTA:
		case TEMPO_MEDIO_RISPOSTA:
			// non serve filtrare altri esiti
			break;

		case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger,protocolFactory).getEsitiCodeOk());
			break;
		case NUMERO_RICHIESTE_FALLITE:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger,protocolFactory).getEsitiCodeKo_senzaFaultApplicativo());
			break;
		case NUMERO_FAULT_APPLICATIVI:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger,protocolFactory).getEsitiCodeFaultApplicativo());
			break;
		case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger,protocolFactory).getEsitiCodeKo());
			break;
		}
		
		
		// capisco tipologia di policy
		boolean policyGlobale = true;
		if(filtro!=null && filtro.isEnabled() && filtro.getNomePorta()!=null && !"".equals(filtro.getNomePorta())){
			policyGlobale = false;
		}
		
		
		// Tipo Porta
		TipoPorta tipoPortaStat = null;
		TipoPdD tipoPdD = groupByPolicy.getRuoloPortaAsTipoPdD();
		if(tipoPdD==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getRuoloPorta()!=null && !"".equals(filtro.getRuoloPorta().getValue()) && 
						!RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())){				
					if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
						tipoPdD = TipoPdD.DELEGATA;
					}
					if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta())){
						tipoPdD = TipoPdD.APPLICATIVA;
					}
					
				}
			}
		}
		if(tipoPdD!=null){
			switch (tipoPdD) {
			case DELEGATA:
				tipoPortaStat = TipoPorta.DELEGATA;
				break;
			case APPLICATIVA:
				tipoPortaStat = TipoPorta.APPLICATIVA;
				break;
			case INTEGRATION_MANAGER:
				tipoPortaStat = TipoPorta.INTEGRATION_MANAGER;
				break;
			case ROUTER:
				tipoPortaStat = TipoPorta.ROUTER;
				break;
	
			}
			expr.equals(model.TIPO_PORTA, tipoPortaStat);
		}
		
		// mittente
		IDSoggetto fruitore = groupByPolicy.getFruitoreIfDefined();
		List<IDSoggetto> fruitoriByRuolo = null;
		if(fruitore==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getTipoFruitore()!=null && !"".equals(filtro.getTipoFruitore()) &&
						filtro.getNomeFruitore()!=null && !"".equals(filtro.getNomeFruitore())){
					fruitore = new IDSoggetto(filtro.getTipoFruitore(), filtro.getNomeFruitore());
				}
				else if(filtro.getRuoloFruitore()!=null && !"".equals(filtro.getRuoloFruitore())){
					/*
					 * Se policyGlobale:
					 *    si controlla sia il fruitore che l'applicativo. Basta che uno sia soddisfatto.
					 * else
					 * 	  nel caso di delegata si controlla solo l'applicativo.
					 *    nel caso di applicativa entrambi, e basta che uno sia soddisfatto.
					 **/
					if(policyGlobale || TipoPdD.APPLICATIVA.equals(tipoPdD)){
						RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
						FiltroRicercaSoggetti filtroRicerca = new FiltroRicercaSoggetti();
						IDRuolo idRuolo = new IDRuolo(filtro.getRuoloFruitore());
						filtroRicerca.setIdRuolo(idRuolo);
						try {
							fruitoriByRuolo = registroManager.getAllIdSoggetti(filtroRicerca, null);
						}catch(DriverRegistroServiziNotFound notFound) {}
					}
				}
			}
		}
		if(fruitore!=null){
			expr.equals(model.TIPO_MITTENTE, fruitore.getTipo());
			expr.equals(model.MITTENTE, fruitore.getNome());
		}
		else if (fruitoriByRuolo!=null && !fruitoriByRuolo.isEmpty()) {
			List<IExpression> l = new ArrayList<IExpression>();
			for (IDSoggetto soggetto : fruitoriByRuolo) {
				IExpression exprSoggetto = dao.newExpression();
				exprSoggetto.equals(model.TIPO_MITTENTE, soggetto.getTipo());
				exprSoggetto.and();
				exprSoggetto.equals(model.MITTENTE, soggetto.getNome());
				l.add(exprSoggetto);
			}
			expr.or(l.toArray(new IExpression[l.size()]));
		}
		
		// destinatario
		IDSoggetto erogatore = groupByPolicy.getErogatoreIfDefined();
		List<IDSoggetto> erogatoriByRuolo = null;
		if(erogatore==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getTipoErogatore()!=null && !"".equals(filtro.getTipoErogatore()) &&
						filtro.getNomeErogatore()!=null && !"".equals(filtro.getNomeErogatore())){
					erogatore = new IDSoggetto(filtro.getTipoErogatore(), filtro.getNomeErogatore());
				}
				else if(filtro.getRuoloErogatore()!=null && !"".equals(filtro.getRuoloErogatore())){
					RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
					FiltroRicercaSoggetti filtroRicerca = new FiltroRicercaSoggetti();
					IDRuolo idRuolo = new IDRuolo(filtro.getRuoloErogatore());
					filtroRicerca.setIdRuolo(idRuolo);
					try {
						erogatoriByRuolo = registroManager.getAllIdSoggetti(filtroRicerca, null);
					}catch(DriverRegistroServiziNotFound notFound) {}
				}
			}
		}
		if(erogatore!=null){
			expr.equals(model.TIPO_DESTINATARIO, erogatore.getTipo());
			expr.equals(model.DESTINATARIO, erogatore.getNome());
		}
		else if (erogatoriByRuolo!=null && !erogatoriByRuolo.isEmpty()) {
			List<IExpression> l = new ArrayList<IExpression>();
			for (IDSoggetto soggetto : erogatoriByRuolo) {
				IExpression exprSoggetto = dao.newExpression();
				exprSoggetto.equals(model.TIPO_DESTINATARIO, soggetto.getTipo());
				exprSoggetto.and();
				exprSoggetto.equals(model.DESTINATARIO, soggetto.getNome());
				l.add(exprSoggetto);
			}
			expr.or(l.toArray(new IExpression[l.size()]));
		}
		
		// servizio
		IDServizio idServizio = groupByPolicy.getServizioIfDefined();
		List<IDServizio> idServiziByTag = null;
		if(idServizio==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getTipoServizio()!=null && !"".equals(filtro.getTipoServizio()) &&
						filtro.getNomeServizio()!=null && !"".equals(filtro.getNomeServizio()) &&
						filtro.getVersioneServizio()!=null){
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(filtro.getTipoServizio(), filtro.getNomeServizio(), null, null, filtro.getVersioneServizio().intValue());
				}
				else if(!policyGlobale && filtro.getNomePorta()!=null && !"".equals(filtro.getNomePorta()) && filtro.getRuoloPorta()!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
					if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(filtro.getNomePorta());
						PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD, requestInfo);
						if(pd!=null && pd.getServizio()!=null && pd.getSoggettoErogatore()!=null) {
							idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
									pd.getServizio().getVersione());
						}
					}
					else {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(filtro.getNomePorta());
						PortaApplicativa pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA, requestInfo);
						if(pa!=null && pa.getServizio()!=null) {
							idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
									pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
									pa.getServizio().getVersione());
						}
					}
				}
				else if(filtro.getTag()!=null && !"".equals(filtro.getTag())){
					RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
					FiltroRicercaAccordi filtroRicercaAccordi = new FiltroRicercaAccordi(); 
					filtroRicercaAccordi.setIdGruppo(new IDGruppo(filtro.getTag()));
					List<IDAccordo> lIdAccordi = null;
					try {
						lIdAccordi = registroManager.getAllIdAccordiServizioParteComune(filtroRicercaAccordi, null);
					}catch(DriverRegistroServiziNotFound notFound) {}
					if(lIdAccordi!=null && !lIdAccordi.isEmpty()) {
						for (IDAccordo idAccordoByTag : lIdAccordi) {
							FiltroRicercaServizi filtroRicerca = new FiltroRicercaServizi();
							filtroRicerca.setIdAccordoServizioParteComune(idAccordoByTag);
							if(erogatore!=null) {
								filtroRicerca.setTipoSoggettoErogatore(erogatore.getTipo());
								filtroRicerca.setNomeSoggettoErogatore(erogatore.getNome());
							}
							List<IDServizio> lIdServizi = null;
							try {
								lIdServizi = registroManager.getAllIdServizi(filtroRicerca, null);
							}catch(DriverRegistroServiziNotFound notFound) {}
							if(lIdServizi!=null && !lIdServizi.isEmpty()) {
								if(idServiziByTag==null) {
									idServiziByTag = new ArrayList<IDServizio>();
								}
								for (IDServizio idServizioByTag : lIdServizi) {
									idServiziByTag.add(idServizioByTag);
								}
							}
						}
					}
				}
			}
		}
		if(idServizio!=null){
			expr.equals(model.TIPO_SERVIZIO, idServizio.getTipo());
			expr.equals(model.SERVIZIO, idServizio.getNome());
			expr.equals(model.VERSIONE_SERVIZIO, idServizio.getVersione());
		}
		else if (idServiziByTag!=null && !idServiziByTag.isEmpty()) {
			List<IExpression> l = new ArrayList<IExpression>();
			for (IDServizio idServizioByTag : idServiziByTag) {
				IExpression exprServizio = dao.newExpression();
				exprServizio.and();
				exprServizio.equals(model.TIPO_DESTINATARIO, idServizioByTag.getSoggettoErogatore().getTipo());
				exprServizio.equals(model.DESTINATARIO, idServizioByTag.getSoggettoErogatore().getNome());
				exprServizio.equals(model.TIPO_SERVIZIO, idServizioByTag.getTipo());
				exprServizio.equals(model.SERVIZIO, idServizioByTag.getNome());
				exprServizio.equals(model.VERSIONE_SERVIZIO, idServizioByTag.getVersione());
				l.add(exprServizio);
			}
			expr.or(l.toArray(new IExpression[l.size()]));
		}
		
		// protocollo
		String protocolloGroupBy = groupByPolicy.getProtocolloIfDefined();
		if(protocolloGroupBy==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getProtocollo()!=null && !"".equals(filtro.getProtocollo())){
					protocolloGroupBy = filtro.getProtocollo();
				}
			}
		}
		if(protocolloGroupBy!=null){
			if(fruitore==null && erogatore==null && idServizio==null){
				// il tipo se è definito uno dei 3 elementi è insito nell'elemento stesso
				List<String> listaTipiSoggetto = ProtocolFactoryManager.getInstance().getOrganizationTypes().get(protocolloGroupBy);
				expr.in(model.TIPO_MITTENTE, listaTipiSoggetto);
				expr.in(model.TIPO_DESTINATARIO, listaTipiSoggetto);
			}
		}
		
		// azione
		String azione = groupByPolicy.getAzioneIfDefined();
		if(azione!=null){
			expr.equals(model.AZIONE, azione);
		}
		else {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getAzione()!=null && !"".equals(filtro.getAzione()) && idServizio!=null){
					String [] tmp = filtro.getAzione().split(",");
					List<String> azioneList = new ArrayList<>();
					if(tmp!=null && tmp.length>0) {
						for (String az : tmp) {
							azioneList.add(az);
						}
					}
					expr.in(model.AZIONE, azioneList);
				}
			}
		}
		
		// Servizio Applicativo
		//if(TipoPdD.DELEGATA.equals(tipoPdDTransazioneInCorso)){
			
		// servizioApplicativoFruitore
		String servizioApplicativoFruitore = groupByPolicy.getServizioApplicativoFruitoreIfDefined();
		List<IDServizioApplicativo> servizioApplicativoFruitoreByRuolo = null;
		if(servizioApplicativoFruitore==null) {
			// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
			if(filtro!=null && filtro.isEnabled()){
				if(filtro.getServizioApplicativoFruitore()!=null && !"".equals(filtro.getServizioApplicativoFruitore())){
					servizioApplicativoFruitore = filtro.getServizioApplicativoFruitore();
				}
				else if(filtro.getRuoloFruitore()!=null && !"".equals(filtro.getRuoloFruitore())){
					/*
					 * Se policyGlobale:
					 *    si controlla sia il fruitore che l'applicativo. Basta che uno sia soddisfatto.
					 * else
					 * 	  nel caso di delegata si controlla solo l'applicativo.
					 *    nel caso di applicativa entrambi, e basta che uno sia soddisfatto.
					 **/
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
					FiltroRicercaServiziApplicativi filtroRicerca = new FiltroRicercaServiziApplicativi();
					IDRuolo idRuolo = new IDRuolo(filtro.getRuoloFruitore());
					filtroRicerca.setIdRuolo(idRuolo);
					List<IDServizioApplicativo> list = null;
					try {
						list = configurazionePdDManager.getAllIdServiziApplicativi(filtroRicerca);
					}catch(DriverConfigurazioneNotFound notFound) {}
					if(list!=null && !list.isEmpty()) {
						servizioApplicativoFruitoreByRuolo = list;
					}
				}
			}
		}
		if(servizioApplicativoFruitore!=null){
			expr.equals(model.SERVIZIO_APPLICATIVO, servizioApplicativoFruitore);
		}
		else if (servizioApplicativoFruitoreByRuolo!=null && !servizioApplicativoFruitoreByRuolo.isEmpty()) {
			List<IExpression> l = new ArrayList<IExpression>();
			for (IDServizioApplicativo idServizioApplicativo : servizioApplicativoFruitoreByRuolo) {
				IExpression exprServizioApplicativo = dao.newExpression();
				exprServizioApplicativo.and();
				exprServizioApplicativo.equals(model.TIPO_MITTENTE, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
				exprServizioApplicativo.equals(model.MITTENTE, idServizioApplicativo.getIdSoggettoProprietario().getNome());
				exprServizioApplicativo.equals(model.SERVIZIO_APPLICATIVO, idServizioApplicativo.getNome());
				l.add(exprServizioApplicativo);
			}
			expr.or(l.toArray(new IExpression[l.size()]));
		}
			
		//}
		
		if(TipoPdD.APPLICATIVA.equals(tipoPdDTransazioneInCorso)){
			
			// servizioApplicativoErogatore
			String servizioApplicativoErogatore = groupByPolicy.getServizioApplicativoErogatoreIfDefined();
			if(servizioApplicativoErogatore==null) {
				// se è stato applicato un filtro, devo prelevare solo le informazioni statistiche che hanno un match non il filtro
				if(filtro!=null && filtro.isEnabled()){
					if(filtro.getServizioApplicativoErogatore()!=null && !"".equals(filtro.getServizioApplicativoErogatore())){
						servizioApplicativoErogatore = filtro.getServizioApplicativoErogatore();
					}
				}
			}
			if(servizioApplicativoErogatore!=null){
				expr.equals(model.SERVIZIO_APPLICATIVO, servizioApplicativoErogatore);
			}
			
		}
		return expr;
	}

    private boolean isKnownType(Object result){
    	return (result instanceof Long) || (result instanceof Integer) || (result instanceof Short) 
    			|| (result instanceof Double) || (result instanceof Float)  ;
    }
    
    private Long translateType(Object result){
    	if ( result instanceof Long  ){
			return (Long) result;
		}
		else if ( result instanceof Integer  ){
			return ((Integer) result).longValue();
		}
		else if ( result instanceof Short  ){
			return ((Short) result).longValue();
		}
		else if ( result instanceof Double  ){
			return ((Double) result).longValue();
		}
		else if ( result instanceof Float  ){
			return ((Float) result).longValue();
		}
		else{
			return null;
		}
    }
}
