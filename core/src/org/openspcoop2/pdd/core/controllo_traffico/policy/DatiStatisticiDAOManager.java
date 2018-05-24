package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.Date;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.constants.TipoPorta;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

public class DatiStatisticiDAOManager  {

	private static DatiStatisticiDAOManager staticInstance = null;
	public static synchronized void initialize(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws Exception{
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
	
	private ConfigurazioneControlloTraffico configurazioneControlloTraffico;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
		
	/** Database */
//	private DataSource ds = null;
//	private String datasource = null;
	private String tipoDatabase = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private Logger daoFactoryLogger = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesStatistiche = null;
	
	private Logger log;
	
	private DatiStatisticiDAOManager(ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws Exception{
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
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(this.debug);
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del datasource: "+e.getMessage(),e);
		}
    	
    }
    
	
	
	
	
	
	/* ********************** NUMERO RICHIESTE ************************** */
	
    public RisultatoStatistico readNumeroRichieste(String key,TipoRisorsa tipoRisorsa,
    		TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, Date leftInterval, Date rightInterval,
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy) throws Exception{
	
    	try{

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case MENSILE:
					model = StatisticaMensile.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaMensileServiceSearch();
					break;
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case SETTIMANALE:
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					break;
				}
			}
			
			IExpression expression = this.createWhereExpressionNumeroRichieste(dao, model, tipoRisorsa, leftInterval, rightInterval, datiTransazione.getTipoPdD(), groupByPolicy);
						
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
		}
    	
	}
    
    private IExpression createWhereExpressionNumeroRichieste(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IDUnivocoGroupByPolicy groupByPolicy) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, groupByPolicy);

    	expr.isNotNull(model.NUMERO_TRANSAZIONI);
    	
		return expr;
    	
    }
	
	
	
	
	
	
	
	
	
	/* ********************** OCCUPAZIONE BANDA ************************** */
	
    public RisultatoStatistico readOccupazioneBanda(String key,TipoRisorsa tipoRisorsa,
    		TipoFinestra tipoFinestra,TipoPeriodoStatistico tipoPeriodo, Date leftInterval, Date rightInterval,
    		TipoBanda tipoBanda,
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy) throws Exception{
	
    	try{

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case MENSILE:
					model = StatisticaMensile.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaMensileServiceSearch();
					break;
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case SETTIMANALE:
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					break;
				}
			}
			
			IExpression expression = this.createWhereExpressionBanda(dao, model, tipoRisorsa, leftInterval, rightInterval, datiTransazione.getTipoPdD(), groupByPolicy, tipoBanda);
						
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
		}
    	
	}
    
    private IExpression createWhereExpressionBanda(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IDUnivocoGroupByPolicy groupByPolicy,
			TipoBanda tipoBanda) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, groupByPolicy);

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
    		DatiTransazione datiTransazione,IDUnivocoGroupByPolicy groupByPolicy) throws Exception{
	
    	try{

			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							this.daoFactoryServiceManagerPropertiesStatistiche, this.daoFactoryLogger);
				
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			if(tipoFinestra!=null && TipoFinestra.SCORREVOLE.equals(tipoFinestra)){
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = statisticheSM.getStatisticaOrariaServiceSearch();
			}
			else{
				switch (tipoPeriodo) {
				case GIORNALIERO:
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaGiornalieraServiceSearch();
					break;
				case MENSILE:
					model = StatisticaMensile.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaMensileServiceSearch();
					break;
				case ORARIO:
					model = StatisticaOraria.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaOrariaServiceSearch();
					break;
				case SETTIMANALE:
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					dao = statisticheSM.getStatisticaSettimanaleServiceSearch();
					break;
				}
			}
			
			IExpression expression = this.createWhereExpressionLatenza(dao, model, tipoRisorsa, leftInterval, rightInterval, datiTransazione.getTipoPdD(), groupByPolicy, tipoLatenza);
						
			FunctionField ff = null;
			switch (tipoLatenza) {
			case PORTA:
				ff = new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza");
				break;
			case SERVIZIO:
				ff = new  FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza");
				break;
			case TOTALE:
			default:
				ff = new  FunctionField(model.LATENZA_TOTALE, Function.AVG, "somma_latenza");
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
			}catch(NotFoundException notFound){
				this.log.debug("LatenzaNotFound:"+notFound.getMessage(),notFound);
				risultato.setRisultato(0);
			}
			
			return risultato;
			
		}catch(Exception e){
			this.log.error("Errore durante la raccolta dei dati statisti (key:"+key+"): "+e.getMessage(),e);
			throw e;
		}
    	
	}
    
    private IExpression createWhereExpressionLatenza(
			IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			TipoRisorsa tipoRisorsa,
			Date dataInizio, Date dataFine,
			TipoPdD tipoPdDTransazioneInCorso,
			IDUnivocoGroupByPolicy groupByPolicy,
			TipoLatenza tipoLatenza) throws Exception {
    	
    	IExpression expr = this.createWhereExpression(dao, model, tipoRisorsa, dataInizio, dataFine, tipoPdDTransazioneInCorso, groupByPolicy);

		int [] esiti = null;
		if(TipoPdD.DELEGATA.equals(tipoPdDTransazioneInCorso)){
			esiti = this.configurazioneControlloTraffico.getCalcoloLatenzaPortaDelegataEsitiConsiderati();		
		}
		else{
			esiti = this.configurazioneControlloTraffico.getCalcoloLatenzaPortaApplicativaEsitiConsiderati();			
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
			IDUnivocoGroupByPolicy groupByPolicy) throws Exception {

		IExpression expr = null;

		expr = dao.newExpression();
		expr.and();
		
		// Data
		expr.between(model.DATA,
				dataInizio,
				dataFine);

		
        // escludo le transazioni con esito policy violate
		int [] esitiPolicyViolate = this.configurazioneControlloTraffico.getEsitiPolicyViolate();    
		IExpression exprEsitiPolicyViolate = dao.newExpression();
		exprEsitiPolicyViolate.or();
		for (int i = 0; i < esitiPolicyViolate.length; i++) {
			exprEsitiPolicyViolate.equals(model.ESITO, esitiPolicyViolate[i]);
		}
		expr.not(exprEsitiPolicyViolate);

		
		// Aggiungo esiti specifici
		switch (tipoRisorsa) {
		case NUMERO_RICHIESTE:
		case OCCUPAZIONE_BANDA:
		case TEMPO_COMPLESSIVO_RISPOSTA:
		case TEMPO_MEDIO_RISPOSTA:
			// non serve filtrare altri esiti
			break;

		case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger).getEsitiCodeOk());
			break;
		case NUMERO_RICHIESTE_FALLITE:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger).getEsitiCodeKo_senzaFaultApplicativo());
			break;
		case NUMERO_FAULT_APPLICATIVI:
			expr.in(model.ESITO, EsitiProperties.getInstance(this.daoFactoryLogger).getEsitiCodeFaultApplicativo());
			break;
		}
		
		
		// Tipo Porta
		TipoPorta tipoPortaStat = null;
		TipoPdD tipoPdD = groupByPolicy.getRuoloPortaAsTipoPdD();
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
		if(fruitore!=null){
			expr.equals(model.TIPO_MITTENTE, fruitore.getTipo());
			expr.equals(model.MITTENTE, fruitore.getNome());
		}
		
		// destinatario
		IDSoggetto erogatore = groupByPolicy.getErogatoreIfDefined();
		if(erogatore!=null){
			expr.equals(model.TIPO_DESTINATARIO, erogatore.getTipo());
			expr.equals(model.DESTINATARIO, erogatore.getNome());
		}
		
		// servizio
		IDServizio idServizio = groupByPolicy.getServizioIfDefined();
		if(idServizio!=null){
			expr.equals(model.TIPO_SERVIZIO, idServizio.getTipo());
			expr.equals(model.SERVIZIO, idServizio.getNome());
			expr.equals(model.VERSIONE_SERVIZIO, idServizio.getVersione());
		}
		
		// protocollo
		String protocollo = groupByPolicy.getProtocolloIfDefined();
		if(protocollo!=null){
			if(fruitore==null && erogatore==null && idServizio==null){
				// il tipo se è definito uno dei 3 elementi è insito nell'elemento stesso
				List<String> listaTipiSoggetto = ProtocolFactoryManager.getInstance().getOrganizationTypes().get(protocollo);
				expr.in(model.TIPO_MITTENTE, listaTipiSoggetto);
				expr.in(model.TIPO_DESTINATARIO, listaTipiSoggetto);
			}
		}
		
		// azione
		String azione = groupByPolicy.getAzioneIfDefined();
		if(azione!=null){
			expr.equals(model.AZIONE, azione);
		}
		
		// Servizio Applicativo
		if(TipoPdD.DELEGATA.equals(tipoPdDTransazioneInCorso)){
			
			// servizioApplicativoFruitore
			String servizioApplicativoFruitore = groupByPolicy.getServizioApplicativoFruitoreIfDefined();
			if(servizioApplicativoFruitore!=null){
				expr.equals(model.SERVIZIO_APPLICATIVO, servizioApplicativoFruitore);
			}
			
		}
		else{
			
			// servizioApplicativoErogatore
			String servizioApplicativoErogatore = groupByPolicy.getServizioApplicativoErogatoreIfDefined();
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
