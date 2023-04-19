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
package org.openspcoop2.pdd.core.transazioni;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**     
 * FiltroDuplicati
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroDuplicati implements IFiltroDuplicati {

	private OpenSPCoop2Properties openspcoop2Properties;
	
	private String idTransazione = null;
	private RequestInfo requestInfo = null;
	
	private org.openspcoop2.protocol.engine.driver.FiltroDuplicati filtroDuplicatiProtocol;
		
	private OpenSPCoopState openspcoop2State = null;
	private Boolean initDsResource = null;
	private Connection connection = null;
	private boolean isDirectConnection = false;
	@SuppressWarnings("unused")
	private String modeGetConnection = null;
	private boolean releaseRuntimeResourceBeforeCheck = false;
	private String tipoDatabaseRuntime = null; 
	private Logger log = null;
	private Logger logSql = null;
	private boolean debug = false;
	
	private TransazioneFieldConverter transazioneFieldConverter = null;
	private String nomeTabellaTransazioni = null;

	private String colonna_pdd_ruolo = null;
	
	private String colonna_duplicati_richiesta = null;
	private String colonna_data_id_msg_richiesta = null;
	private String colonna_id_messaggio_richiesta = null;

	private String colonna_duplicati_risposta = null;
	private String colonna_data_id_msg_risposta = null;
	private String colonna_id_messaggio_risposta = null;
	
	private boolean useTransactionIdForTest = false; // solo per test
	public boolean isUseTransactionIdForTest() {
		return this.useTransactionIdForTest;
	}
	public void setUseTransactionIdForTest(boolean useTransactionIdForTest) {
		this.useTransactionIdForTest = useTransactionIdForTest;
	}

	private static final String ID_MODULO = "FiltroDuplicati";
	

	@Override
	public void init(Object context) throws ProtocolException{
		
		this.openspcoop2Properties = OpenSPCoop2Properties.getInstance();
		
		if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled() == false) {
			this.filtroDuplicatiProtocol = new org.openspcoop2.protocol.engine.driver.FiltroDuplicati();
			this.filtroDuplicatiProtocol.init(context);
		}
		else {
			PdDContext pddContext = (PdDContext)context;
			if(pddContext==null) {
				throw new ProtocolException("PdDContext non fornito");
			}
			this.idTransazione = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			if(this.idTransazione==null){
				throw new ProtocolException("Id di transazione non fornito");
			}
			
			if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				this.requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			if(this.initDsResource==null && this.connection==null){
				this.init((PdDContext)context);
			}
		}
		
	}
	
	@Override
	public boolean releaseRuntimeResourceBeforeCheck() {
		return this.releaseRuntimeResourceBeforeCheck;
	}
	
	@Override
	public boolean isDuplicata(IProtocolFactory<?> protocolFactory, String idBustaParam) throws ProtocolException {
		
		String idBusta = this.useTransactionIdForTest ? this.idTransazione : idBustaParam;
				
		long timeStart = -1;
		TransazioniFiltroDuplicatiProcessTimes times = null;
		try {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				timeStart = DateManager.getTimeMillis();
				times = new TransazioniFiltroDuplicatiProcessTimes();
			}
		
			if(this.filtroDuplicatiProtocol!=null) {
				return this.filtroDuplicatiProtocol.isDuplicata(protocolFactory, idBusta);
			}
			
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] idTransaction("+this.idTransazione+") (useTransactionIdForTest:"+this.useTransactionIdForTest+") ...");
			
			// E' duplicata se esiste nel contesto una transazione con tale idBusta 
			if(TransactionContext.containsIdentificativoProtocollo(idBusta)){
				//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (CONTEXT)");
				try{
					TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
				}catch(Exception e){
					throw new ProtocolException(e);
				}
				return true;
			}
			
			// oppure se esiste una transazione registrata con tale idBusta sul database (Richiesta o Risposta).
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] READ FROM DB");
			if(esisteTransazione(protocolFactory,idBusta,idBusta,times)){
				//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (DATABASE)");
				try{
					TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
				}catch(Exception e){
					throw new ProtocolException(e);
				}
				return true;
			}
			
			// Se non esiste registro nel contesto questa transazione.
			// Comunque OpenSPCoop se torno false, procedera a chiamare registraBusta, il quale metodo non fara' nulla (vedi implementazione sotto stante)
			// Il metodo di registrazione dell'identificativo busta viene acceduto tramite un semaforo e controlla che non sia possibile registrare due identificativi busta.
			// Tale implementazione garantisce che nel contesto puo' esistere solo un idBusta, e tale id viene eliminato SOLO dopo 
			// aver salvato la transazione nel database (vedi implementazione PostOutResponseHandler, metodo removeIdentificativoProtocollo)
			// Se dopo aver eliminato dal contesto l'id, arriva una nuova busta con stesso id, questo metodo la trova nella tabelle delle transazioni 
			// e quindi ritornera' immediatamente l'informazione di busta duplicata.
			try{
				//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE ....");
				TransactionContext.registraIdentificativoProtocollo(idBusta, this.idTransazione);
				//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE REGISTRATA");
			}catch(Exception e){
				if(e.getMessage()!=null && "DUPLICATA".equals(e.getMessage())){
					//System.out.println("@@IS_DUPLICATA ["+idBusta+"] TRUE (ERRORE ECCEZIONE)");
					try{
						TransactionContext.getTransaction(this.idTransazione).addIdProtocolloDuplicato(idBusta);
					}catch(Exception eSetDuplicata){
						throw new ProtocolException(eSetDuplicata);
					}
					return true;
				}else{
					throw new ProtocolException(e);
				}
			}
			
			//System.out.println("@@IS_DUPLICATA ["+idBusta+"] FALSE FINE");
			return false;
			
		}finally {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=this.openspcoop2Properties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(this.idTransazione!=null) {
						sb.append(" <").append(this.idTransazione).append(">");
					}
					sb.append(" [isDuplicata]");
					sb.append(" ").append(idBusta);
					if(times!=null) {
						sb.append(" ").append(times.toString());
					}
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(sb.toString());
				}
			}
		}
	}

	@Override
	public void incrementaNumeroDuplicati(IProtocolFactory<?> protocolFactory, String idBusta) throws ProtocolException {
		
		long timeStart = -1;
		TransazioniFiltroDuplicatiProcessTimes times = null;
		try {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				timeStart = DateManager.getTimeMillis();
				times = new TransazioniFiltroDuplicatiProcessTimes();
			}
		
			if(this.filtroDuplicatiProtocol!=null) {
				this.filtroDuplicatiProtocol.incrementaNumeroDuplicati(protocolFactory, idBusta);
				return;
			}
			
			//System.out.println("@@incrementaNumeroDuplicati ["+idBusta+"] ...");
			
			incrementDuplicatiTransazione(protocolFactory, idBusta, times);
			
			//System.out.println("@@incrementaNumeroDuplicati richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"] FINE");
		}finally {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=this.openspcoop2Properties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(this.idTransazione!=null) {
						sb.append(" <").append(this.idTransazione).append(">");
					}
					sb.append(" [incrementaNumeroDuplicati]");
					sb.append(" ").append(idBusta);
					if(times!=null) {
						sb.append(" ").append(times.toString());
					}
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(sb.toString());
				}
			}
		}
	}

	@Override
	public void registraBusta(IProtocolFactory<?> protocolFactory, Busta busta) throws ProtocolException {
		
		long timeStart = -1;
		try {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				timeStart = DateManager.getTimeMillis();
			}
		
			if(this.filtroDuplicatiProtocol!=null) {
				this.filtroDuplicatiProtocol.registraBusta(protocolFactory, busta);
				return;
			}
			
			// Implementazione inserita in isDuplicata
			//System.out.println("@@registraBusta ["+busta.getID()+"] NON IMPLEMENTATO");
			
		}finally {
			if(this.openspcoop2Properties.isTransazioniRegistrazioneSlowLog()) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=this.openspcoop2Properties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(this.idTransazione!=null) {
						sb.append(" <").append(this.idTransazione).append(">");
					}
					sb.append(" [registraBusta]");
					if(busta!=null) {
						sb.append(" ").append(busta.getID());
					}
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(sb.toString());
				}
			}
		}
	}

	
	
	/* **** METODI INTERNI **** */
	
	private synchronized void init(PdDContext pddContext) throws ProtocolException {

		//if(this.dsRuntime==null && this.connection==null){
		if(this.initDsResource==null && this.connection==null){
			
			try{
				// Debug
				this.debug = this.openspcoop2Properties.isTransazioniDebug();
				this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(this.debug);
				this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(this.debug);
			}catch(Exception e){
				throw new ProtocolException("Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
			}
			
			try{
				
				// TipoDatabase
				this.tipoDatabaseRuntime = this.openspcoop2Properties.getDatabaseType();
				if(this.tipoDatabaseRuntime==null){
					throw new Exception("Tipo Database non definito");
				}
				
				// DB Resource
				Object openspcoopstate = pddContext.getObject(org.openspcoop2.core.constants.Costanti.OPENSPCOOP_STATE);
				if(openspcoopstate!=null) {
					this.openspcoop2State = (OpenSPCoopState) openspcoopstate;
				}
				if(this.openspcoop2State!=null && this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection()
						&& DBTransazioniManager.getInstance().useRuntimePdD() && !this.openspcoop2State.resourceReleased()){
					//System.out.println("[FILTRO] INIZIALIZZO CONNESSIONE");
					this.connection = this.openspcoop2State.getConnectionDB();
					//this.datasourceRuntime = "DirectConnection";
					this.modeGetConnection = "DirectConnection";
					this.isDirectConnection = true;
				}
				else{
					//System.out.println("[FILTRO] INIZIALIZZO DS");
//					this.datasourceRuntime = PddInterceptorConfig.getDataSource();
//					if(this.datasourceRuntime==null){
//						throw new Exception("Datasource non definito");
//					}
					this.initDsResource = true;
					this.modeGetConnection = "DatasourceRuntime";
					
					this.releaseRuntimeResourceBeforeCheck = true; // per evitare deadlock
					
					// Inizializzazione datasource
//					GestoreJNDI jndi = new GestoreJNDI();
//					this.dsRuntime = (DataSource) jndi.lookup(this.datasourceRuntime);
				}
								
				//System.out.println("DS["+this.datasource+"] TIPODB["+this.tipoDatabase+"]");
								
				this.transazioneFieldConverter = new TransazioneFieldConverter(this.tipoDatabaseRuntime);
				
				this.nomeTabellaTransazioni = this.transazioneFieldConverter.toTable(Transazione.model());
				
				this.colonna_pdd_ruolo = this.transazioneFieldConverter.toColumn(Transazione.model().PDD_RUOLO, false);
							
				this.colonna_duplicati_richiesta = this.transazioneFieldConverter.toColumn(Transazione.model().DUPLICATI_RICHIESTA, false);
				this.colonna_data_id_msg_richiesta = this.transazioneFieldConverter.toColumn(Transazione.model().DATA_ID_MSG_RICHIESTA, false);
				this.colonna_id_messaggio_richiesta = this.transazioneFieldConverter.toColumn(Transazione.model().ID_MESSAGGIO_RICHIESTA, false);
				
				this.colonna_duplicati_risposta = this.transazioneFieldConverter.toColumn(Transazione.model().DUPLICATI_RISPOSTA, false);
				this.colonna_data_id_msg_risposta = this.transazioneFieldConverter.toColumn(Transazione.model().DATA_ID_MSG_RISPOSTA, false);
				this.colonna_id_messaggio_risposta = this.transazioneFieldConverter.toColumn(Transazione.model().ID_MESSAGGIO_RISPOSTA, false);
				
			}catch(Exception e){
				throw new ProtocolException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
			}
		}
		
	}
	
	
	
	private boolean esisteTransazione(IProtocolFactory<?> protocolFactory, String idBustaRichiesta,String idBustaRisposta,
			TransazioniFiltroDuplicatiProcessTimes times) throws ProtocolException {
		Connection con = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = ID_MODULO+".esisteTransazione"; //_"+idBustaRichiesta+"/"+idBustaRisposta;
		try{
			
			IBustaBuilder<?> protocolBustaBuilder = null;
			if(this.openspcoop2State!=null) {
				if(idBustaRichiesta!=null) {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRichiesta());
				}
				else {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRisposta());
				}
			}
			else {					
				protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
			}
			
			if(idBustaRichiesta==null && idBustaRisposta==null){
				throw new ProtocolException("ID busta non forniti");
			}
			
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				if(this.connection!=null){
					//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY CONNECTION");
					con = this.connection;
				}else{
					//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY DATASOURCE");
					dbManager = DBTransazioniManager.getInstance();
					r = dbManager.getResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol(), this.requestInfo), idModulo, this.idTransazione);
					if(r==null){
						throw new Exception("Risorsa al database non disponibile");
					}
					con = (Connection) r.getResource();
				}
				if(con==null){
					throw new Exception("Connection is null");
				}
			}finally {	
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.getConnection = timeProcess;
				}
			}
			
			return esisteTransazione_query(protocolFactory, idBustaRichiesta, idBustaRisposta,
					con, protocolBustaBuilder, times);
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			if(this.isDirectConnection==false){
				try{
					if(r!=null)
						dbManager.releaseResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol(), this.requestInfo), idModulo, r);
				}catch(Exception eClose){}
			}
		}
		
	}
	
	private void incrementDuplicatiTransazione(IProtocolFactory<?> protocolFactory, String idBusta,
			TransazioniFiltroDuplicatiProcessTimes times) throws ProtocolException {
		Connection con = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = ID_MODULO+".incrementDuplicatiTransazione"; //_"+idBustaRichiesta+"/"+idBustaRisposta;
		try{
			
			IBustaBuilder<?> protocolBustaBuilder = null;
			if(this.openspcoop2State!=null) {
				if(this.openspcoop2State.getStatoRichiesta()!=null) {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRichiesta());
				}
				else {
					protocolBustaBuilder = protocolFactory.createBustaBuilder(this.openspcoop2State.getStatoRisposta());
				}
			}
			else {					
				protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
			}
			
			if(idBusta==null){
				throw new ProtocolException("ID busta non fornito");
			}
			
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				if(this.connection!=null){
					//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY CONNECTION");
					con = this.connection;
				}else{
					//System.out.println("[FILTRO] esisteTransazione idBustaRichiesta["+idBustaRichiesta+"] idBustaRisposta["+idBustaRisposta+"] BY DATASOURCE");
					dbManager = DBTransazioniManager.getInstance();
					r = dbManager.getResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol(), this.requestInfo), idModulo, this.idTransazione);
					if(r==null){
						throw new Exception("Risorsa al database non disponibile");
					}
					con = (Connection) r.getResource();
				}
				if(con==null){
					throw new Exception("Connection is null");
				}
			}finally {	
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.getConnection = timeProcess;
				}
			}
			
			
			// Aggiorno numero duplicati per transazione che possiede tale idBusta (Richiesta o Risposta)
			// Se non esiste una transazione sul database, devo attendere che questa compaia,
			// significa che una precedente transazione con stesso idBusta e' ancora in gestione
			
			long timeRequest = -1;
			long timeResponse = -1;
			long timeSleep = -1;
			boolean esisteRichiesta = false;
			boolean esisteRisposta = false;
			try{
				TransazioniFiltroDuplicatiProcessTimes checkRequest = null;
				TransazioniFiltroDuplicatiProcessTimes checkResponse = null;
				
				if(times!=null) {
					checkRequest = new TransazioniFiltroDuplicatiProcessTimes();
				}
				esisteRichiesta = esisteTransazione_query(protocolFactory,idBusta,null,con,protocolBustaBuilder,checkRequest);
				if(times!=null) {
					timeRequest=checkRequest.read;
				}
				
				if(!esisteRichiesta){
					if(times!=null) {
						checkResponse = new TransazioniFiltroDuplicatiProcessTimes();
					}
					esisteRisposta = esisteTransazione_query(protocolFactory,null,idBusta,con,protocolBustaBuilder,checkResponse);
					if(times!=null) {
						timeResponse=checkResponse.read;
					}
				}
				//System.out.println("@@incrementaNumeroDuplicati richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"] ...");
				
				int i=0;
				while(!esisteRichiesta && !esisteRisposta && i<60){
					//System.out.println("@@incrementaNumeroDuplicati WHILE richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"]  SLEEP ...");
					// ATTENDI
					org.openspcoop2.utils.Utilities.sleep(1000);
					if(timeSleep==-1) {
						timeSleep=1000;
					}else {
						timeSleep = timeSleep + 1000;
					}
					
					i++;
					
					if(times!=null) {
						checkRequest = new TransazioniFiltroDuplicatiProcessTimes();
					}
					esisteRichiesta = esisteTransazione_query(protocolFactory,idBusta,null,con,protocolBustaBuilder,checkRequest);
					if(times!=null) {
						timeRequest = timeRequest + checkRequest.read;
					}
					
					if(!esisteRichiesta){
						if(times!=null) {
							checkResponse = new TransazioniFiltroDuplicatiProcessTimes();
						}
						esisteRisposta = esisteTransazione_query(protocolFactory,null,idBusta,con,protocolBustaBuilder,checkResponse);
						if(times!=null) {
							timeResponse = timeResponse + checkResponse.read;
						}
					}
					//System.out.println("@@incrementaNumeroDuplicati WHILE richiesta["+esisteRichiesta+"] risposta["+esisteRisposta+"]  SLEEP FINE ...");
				}
			}finally {	
				if(times!=null) {
					times.checkExistsRequest = timeRequest;
					times.checkExistsResponse = timeResponse;
					times.checkSleep = timeSleep;
				}
			}
			
			if(esisteRichiesta){
				incrementDuplicatiTransazione(protocolFactory,true, idBusta,
						con, protocolBustaBuilder, times);
			}
			else if(esisteRisposta){
				incrementDuplicatiTransazione(protocolFactory,false, idBusta,
						con, protocolBustaBuilder, times);
			}
			else{
				throw new ProtocolException("Precedente transazione con solito idBusta risulta in gestione da oltre 60 secondi");
			}
			
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			if(this.isDirectConnection==false){
				try{
					if(r!=null)
						dbManager.releaseResource(this.openspcoop2Properties.getIdentitaPortaDefault(protocolFactory.getProtocol(), this.requestInfo), idModulo, r);
				}catch(Exception eClose){}
			}
		}
		
	}
	
	private boolean esisteTransazione_query(IProtocolFactory<?> protocolFactory, String idBustaRichiesta,String idBustaRisposta,
			Connection con, IBustaBuilder<?> protocolBustaBuilder, TransazioniFiltroDuplicatiProcessTimes times) throws ProtocolException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabaseRuntime);
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex()){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						//System.out.println("ADD FORCE INDEX esisteTransazione INDEX2");
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_2);
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_2);
					}else{
						//System.out.println("ADD FORCE INDEX esisteTransazione INDEX1");
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_1);
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_1);
					}
				}/*else{
					System.out.println("NON USO FORCE INDEX esisteTransazione");
				}*/
				sqlQueryObject.addFromTable(this.nomeTabellaTransazioni);
				if(idBustaRichiesta!=null){
					// Solo una porta applicativa puo' ricevere una busta di richiesta (serve per evitare i problemi in caso di loopback)
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						sqlQueryObject.addWhereCondition(true,this.colonna_data_id_msg_richiesta+"=?",this.colonna_id_messaggio_richiesta+"=?",this.colonna_pdd_ruolo+"=?");	
					}
					else{
						sqlQueryObject.addWhereCondition(true,this.colonna_id_messaggio_richiesta+"=?",this.colonna_pdd_ruolo+"=?");	
					}
				}
				if(idBustaRisposta!=null){
					 // Solo una porta delegata puo' ricevere una busta di risposta (serve per evitare i problemi in caso di loopback)
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						sqlQueryObject.addWhereCondition(true,this.colonna_data_id_msg_risposta+"=?",this.colonna_id_messaggio_risposta+"=?",this.colonna_pdd_ruolo+"=?");
					}
					else{
						sqlQueryObject.addWhereCondition(true,this.colonna_id_messaggio_risposta+"=?",this.colonna_pdd_ruolo+"=?");	
					}
				}
				sqlQueryObject.setANDLogicOperator(false); // OR
				
				String sql = sqlQueryObject.createSQLQuery();
				pstmt = con.prepareStatement(sql);
				int index = 1;
				Timestamp t = null;
				if(idBustaRichiesta!=null){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						t = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBustaRichiesta);
						if(t==null && (!this.useTransactionIdForTest)){
							throw new Exception("Estrazione data dall'id busta ["+idBustaRichiesta+"] non riuscita");
						}
						pstmt.setTimestamp(index++, t);
					}
					pstmt.setString(index++, idBustaRichiesta);
					pstmt.setString(index++, TipoPdD.APPLICATIVA.getTipo());
				}
				if(idBustaRisposta!=null){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						t = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBustaRisposta);
						if(t==null && (!this.useTransactionIdForTest)){
							throw new Exception("Estrazione data dall'id busta ["+idBustaRisposta+"] non riuscita");
						}
						pstmt.setTimestamp(index++, t);
					}
					pstmt.setString(index++, idBustaRisposta);
					pstmt.setString(index++, TipoPdD.DELEGATA.getTipo());
				}
				
				if(this.debug){
					SimpleDateFormat dateformat = null;
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory) && t!=null){
						dateformat = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm");
					}
					if(idBustaRichiesta!=null){
						if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory) && dateformat!=null){
							sql = sql.replaceFirst("\\?", "'"+dateformat.format(t)+"'");
						}
						sql = sql.replaceFirst("\\?", "'"+idBustaRichiesta+"'");
						sql = sql.replaceFirst("\\?", "'"+TipoPdD.APPLICATIVA.getTipo()+"'");
					}
					if(idBustaRisposta!=null){
						if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory) && dateformat!=null){
							sql = sql.replaceFirst("\\?", "'"+dateformat.format(t)+"'");
						}
						sql = sql.replaceFirst("\\?", "'"+idBustaRisposta+"'");
						sql = sql.replaceFirst("\\?", "'"+TipoPdD.DELEGATA.getTipo()+"'");
					}
					this.logSql.debug("Eseguo query: "+sql);
				}
				//System.out.println("esisteTransazione SQL: "+sql);
				
				rs = pstmt.executeQuery();
				if(rs.next()){
					if(this.debug){
						this.logSql.debug("Risultato query: "+true);
					}
					return true;
				}else{
					if(this.debug){
						this.logSql.debug("Risultato query: "+false);
					}
					return false;
				}
			}finally {	
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.read = timeProcess;
				}
			}
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception eClose){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	private void incrementDuplicatiTransazione(IProtocolFactory<?> protocolFactory, boolean richiesta,String idBusta,
			Connection con, IBustaBuilder<?> protocolBustaBuilder, TransazioniFiltroDuplicatiProcessTimes times) throws ProtocolException {
		PreparedStatement pstmt = null;
		try{
			
			if(idBusta==null){
				throw new ProtocolException("ID busta non fornito");
			}
			
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
			
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabaseRuntime);
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex()){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						//	System.out.println("ADD FORCE INDEX esisteTransazione INDEX1");
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_2);
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_2);
					}else{
						//	System.out.println("ADD FORCE INDEX esisteTransazione INDEX2");
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_REQ_1);
						sqlQueryObject.addSelectForceIndex(this.nomeTabellaTransazioni, CostantiDB.TABLE_TRANSAZIONI_INDEX_FILTRO_RES_1);
					}
				}
				sqlQueryObject.addUpdateTable(this.nomeTabellaTransazioni);
				if(richiesta){
					sqlQueryObject.addUpdateField(this.colonna_duplicati_richiesta, this.colonna_duplicati_richiesta+"+1");
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						sqlQueryObject.addWhereCondition(this.colonna_data_id_msg_richiesta+"=?");
					}
					sqlQueryObject.addWhereCondition(this.colonna_id_messaggio_richiesta+"=?");
					sqlQueryObject.addWhereCondition(this.colonna_duplicati_richiesta+">=?");
					// Solo una porta applicativa puo' ricevere una busta di richiesta (serve per evitare i problemi in caso di loopback)
					sqlQueryObject.addWhereCondition(this.colonna_pdd_ruolo+"=?");	
				}
				else{
					sqlQueryObject.addUpdateField(this.colonna_duplicati_risposta, this.colonna_duplicati_risposta+"+1");
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						sqlQueryObject.addWhereCondition(this.colonna_data_id_msg_risposta+"=?");
					}
					sqlQueryObject.addWhereCondition(this.colonna_id_messaggio_risposta+"=?");
					sqlQueryObject.addWhereCondition(this.colonna_duplicati_risposta+">=?");
					// Solo una porta delegata puo' ricevere una busta di risposta (serve per evitare i problemi in caso di loopback)
					sqlQueryObject.addWhereCondition(this.colonna_pdd_ruolo+"=?");	
				}
				sqlQueryObject.setANDLogicOperator(true); 
				
				Timestamp timestampId = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,idBusta);
				if(timestampId==null && this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory) && (!this.useTransactionIdForTest)){
					throw new Exception("Estrazione data dall'id busta ["+idBusta+"] non riuscita");
				}
				
				String sql = sqlQueryObject.createSQLUpdate();
				//System.out.println("incrementDuplicatiTransazione SQL: "+sql);
				pstmt = con.prepareStatement(sql);
				int index = 1;
				if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
					pstmt.setTimestamp(index++, timestampId);
				}
				pstmt.setString(index++, idBusta);
				pstmt.setInt(index++, 0);
				if(richiesta){
					pstmt.setString(index++, TipoPdD.APPLICATIVA.getTipo());
				}
				else {
					pstmt.setString(index++, TipoPdD.DELEGATA.getTipo());
				}
				
				if(this.debug){
					if(this.openspcoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm");
						sql = sql.replaceFirst("\\?", "'"+dateformat.format(timestampId)+"'");
					}
					if(idBusta!=null){
						sql = sql.replaceFirst("\\?", "'"+idBusta+"'");
					}
					sql = sql.replaceFirst("\\?", "0");
					if(richiesta){
						sql = sql.replaceFirst("\\?", "'"+TipoPdD.APPLICATIVA.getTipo()+"'");
					}
					else {
						sql = sql.replaceFirst("\\?", "'"+TipoPdD.DELEGATA.getTipo()+"'");
					}
					this.logSql.debug("Eseguo query: "+sql);
				}
				
				int righeModificate = pstmt.executeUpdate();
				if(this.debug){
					this.logSql.debug("ID["+idBusta+"] richiesta["+richiesta+"] modificate righe: "+righeModificate);
				}
				
			}finally {	
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.update = timeProcess;
				}
			}
			
		}catch(Exception e){
			throw new ProtocolException(e);
		}
		finally{
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){
				// close
			}
		}
		
	}
}

class TransazioniFiltroDuplicatiProcessTimes{

	long getConnection = -1;
	long read = -1;
	long update = -1;
	long checkExistsRequest = -1;
	long checkExistsResponse = -1;
	long checkSleep = -1;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.getConnection>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("getConnection:").append(this.getConnection);
		}
		if(this.read>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("read:").append(this.read);
		}
		if(this.update>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("update:").append(this.update);
		}
		if(this.checkExistsRequest>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("checkExistsRequest:").append(this.checkExistsRequest);
		}
		if(this.checkExistsResponse>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("checkExistsResponse:").append(this.checkExistsResponse);
		}
		if(this.checkSleep>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("checkSleep:").append(this.checkSleep);
		}
		return sb.toString();
	}
}
