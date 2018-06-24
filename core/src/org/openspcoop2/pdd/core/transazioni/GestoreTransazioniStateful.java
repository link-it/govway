/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.RuoloTransazione;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;


/**     
 * GestoreTransazioniStateful
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreTransazioniStateful {

	private Logger log = null;
	private Logger logSql = null;
//	private String datasource = null;
	private String tipoDatabase = null;
	private boolean debug = false;
	
	private ITracciaProducer tracciamentoOpenSPCoopAppender = null;
	private IDiagnosticProducer msgDiagnosticiOpenSPCoopAppender = null;
	private IDumpProducer dumpOpenSPCoopAppender = null;
	
	public GestoreTransazioniStateful(Logger log,Logger logSql,
			//String dataSource,
			String tipoDatabase,boolean debug) throws Exception{
		
		this.log = log;
		this.logSql = logSql;
		this.tipoDatabase = tipoDatabase;
//		this.datasource = dataSource;
		this.debug = debug;
			
		boolean usePdDConnection = true;
		
		try{
			
			// Init
			this.tracciamentoOpenSPCoopAppender = new org.openspcoop2.pdd.logger.TracciamentoOpenSPCoopProtocolAppender();
			OpenspcoopAppender tracciamentoOpenSPCoopAppender = new OpenspcoopAppender();
			tracciamentoOpenSPCoopAppender.setTipo("__gestoreTransazioniStateful");
			List<Property> tracciamentoOpenSPCoopAppenderProperties = new ArrayList<Property>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.logSql, tracciamentoOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					this.tipoDatabase,
					usePdDConnection, // viene usata la connessione della PdD 
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(tracciamentoOpenSPCoopAppenderProperties, false);

			tracciamentoOpenSPCoopAppender.setPropertyList(tracciamentoOpenSPCoopAppenderProperties);
			this.tracciamentoOpenSPCoopAppender.initializeAppender(tracciamentoOpenSPCoopAppender);
			this.tracciamentoOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del TracciamentoAppender: "+e.getMessage(),e);
		} 
		
		try{
			
			// Init
			this.msgDiagnosticiOpenSPCoopAppender = new org.openspcoop2.pdd.logger.MsgDiagnosticoOpenSPCoopProtocolAppender();
			OpenspcoopAppender diagnosticoOpenSPCoopAppender = new OpenspcoopAppender();
			diagnosticoOpenSPCoopAppender.setTipo("__gestoreTransazioniStateful");
			List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<Property>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.logSql, diagnosticoOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					this.tipoDatabase,
					usePdDConnection, // viene usata la connessione della PdD
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(diagnosticoOpenSPCoopAppenderProperties, false);

			diagnosticoOpenSPCoopAppender.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
			this.msgDiagnosticiOpenSPCoopAppender.initializeAppender(diagnosticoOpenSPCoopAppender);
			this.msgDiagnosticiOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
		} 
		
		try{
			
			// Init
			this.dumpOpenSPCoopAppender = new org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender();
			OpenspcoopAppender dumpOpenSPCoopAppender = new OpenspcoopAppender();
			dumpOpenSPCoopAppender.setTipo("__gestoreTransazioniStateful");
			List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<Property>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.logSql, dumpOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					this.tipoDatabase,
					usePdDConnection, // viene usata la connessione della PdD
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(dumpOpenSPCoopAppenderProperties, false);

			dumpOpenSPCoopAppender.setPropertyList(dumpOpenSPCoopAppenderProperties);
			this.dumpOpenSPCoopAppender.initializeAppender(dumpOpenSPCoopAppender);
			this.dumpOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
		} 

	}
	
	
	public void verificaOggettiPresentiRepository(DAOFactory daoFactory,  ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni,
			Logger daoFactoryLoggerTransazioni, Connection con) throws Exception{
		
		
		try{
			if(this.debug)
				this.log.debug("\n\n****************************************************************************************************");
			boolean autoCommit = false;
			con.setAutoCommit(autoCommit);
		
			
			org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
					(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
							con, autoCommit,
							daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioni);
			jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
			ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
			
			int size = RepositoryGestioneStateful.size();
			if(this.debug)
				this.log.debug("Trovati "+size+" elementi da gestire");
			while(size>0){
				
				StatefulObject so = RepositoryGestioneStateful.removeObject();
				try{
					
					String type = "";
					if(so.getType()!=null){
						type = so.getType().toString();
					}
					String className = "";
					if(so.getObject()!=null){
						type = so.getObject().getClass().getName();
					}
					String idTransazione = "";
					if(so.getObject()!=null){
						idTransazione = so.getIdTransazione();
					}
					
					if(so.getIdTransazione()==null){
						throw new Exception("Oggetto presente nel repository (Tipo:"+type+")(Class:"+className+") senza associato un id di transazione");
					}
					if(so.getType()==null){
						throw new Exception("Oggetto presente nel repository (ID:"+idTransazione+")(Class:"+className+") senza associato un tipo");
					}
					if(so.getObject()==null){
						throw new Exception("Oggetto presente nel repository (ID:"+idTransazione+")(Tipo:"+type+") senza un valore");
					}
					
					if(this.debug)
						this.log.debug("-------------- "+so.getType().toString()+"/"+so.getObject().getClass().getName()+"/"+so.getIdTransazione()+" --------------");
					
					TransactionDB transactionDB = readTransazione(transazioneService, so.getIdTransazione());
					if(transactionDB==null){
						// Transazione non ancora inserita nel database dal modulo PostOutResponseHandler
						if(this.debug)
							this.log.debug("Transazione non ancora presente, reinserisco nel repository");
						RepositoryGestioneStateful.addObject(so);
						continue;
					}
					
					gestioneStatefulObject(transazioneService, con, so, transactionDB,true);
					
					con.commit();
					if(this.debug)
						this.log.debug("Commit eseguito con successo");
					
				}catch(Exception e){
					try{
						if(this.debug)
							this.log.debug("Errore durante la gestione dell'oggetto (reinserisco nel repository): "+e.getMessage());
						RepositoryGestioneStateful.addObject(so);
						con.rollback();
					}catch(Exception eRollback){}
					throw e;
				}finally{
					size--;
				}
			}
									
			
		}finally{
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){}
		}
		
	}
	
	public void gestioneStatefulObject(ITransazioneService transazioneService,Connection con,StatefulObject so) throws Exception{
		gestioneStatefulObject(transazioneService, con, so, null,false);
	}
	public void gestioneStatefulObject(ITransazioneService transazioneService,Connection con,StatefulObject so,TransactionDB transactionDB) throws Exception{
		gestioneStatefulObject(transazioneService, con, so, transactionDB,false);
	}
	private void gestioneStatefulObject(ITransazioneService transazioneService,Connection con,StatefulObject so,TransactionDB transactionDB,boolean thread) throws Exception{
		
		String tipoGestione = "[Thread] ";
		if(!thread)
			tipoGestione = "[GestioneDiretta] ";
		
		switch (so.getType()) {
		case MSGDIAGNOSTICO:
			
			this.msgDiagnosticiOpenSPCoopAppender.log(con,(MsgDiagnostico)so.getObject());		
			if(this.debug)
				this.log.debug(tipoGestione+"MsgDiagnostico inserito nel database");
			
			break;
		
		case TRACCIA:
			
			Traccia traccia = (Traccia) so.getObject();
			this.tracciamentoOpenSPCoopAppender.log(con, traccia);
			if(this.debug)
				this.log.debug(tipoGestione+"Traccia ("+traccia.getTipoMessaggio()+") inserita nel database");
			
			break;
			
		case MESSAGGIO:
			
			this.dumpOpenSPCoopAppender.dump(con,(Messaggio)so.getObject());		
			if(this.debug)
				this.log.debug(tipoGestione+"DumpMessaggio inserito nel database");
			
			break;
			
		case DATA_USCITA_RICHIESTA:
			
			updateDataUscitaRichiesta(transazioneService, (Date)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sulla data di uscita della richiesta aggiornata");
			
			break;
			
		case DATA_INGRESSO_RISPOSTA:
			
			updateDataIngressoRisposta(transazioneService, (Date)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sulla data di ingresso della risposta aggiornata");
			
			break;
			
		case DIMENSIONE_USCITA_RICHIESTA:
			
			updateDimensioneUscitaRichiesta(transazioneService, (Long)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sulla dimensione di uscita della richiesta aggiornata");
			
			break;
			
		case DIMENSIONE_INGRESSO_RISPOSTA:
			
			updateDimensioneIngressoRisposta(transazioneService, (Long)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sulla dimensione di ingresso della risposta aggiornata");
			
			break;
			
		case LOCATION:
			
			updateLocationConnettore(transazioneService, (String)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sulla location aggiornata");
			
			break;
			
		case TIPO_CONNETTORE:
			
			updateTipoConnettore(transazioneService, (String)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sul tipo di connettore aggiornata");
			
			break;
			
		case CODICE_TRASPORTO_RICHIESTA:
			
			updateCodiceTrasportoRichiesta(transazioneService, (String)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sul codice di trasporto aggiornata");
			
			break;
			
		case SCENARIO_COOPERAZIONE:
			
			updateScenarioDiCooperazione(transazioneService, (String)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione sullo scenario di cooperazione aggiornata");
			
			break;
			
		case OUT_REQUEST_STATEFUL_OBJECT:
			
			updateOutRequestStatefulObject(transazioneService, (OutRequestStatefulObject)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione emesse dall'out request handler aggiornate");
			
			break;
		
		case IN_RESPONSE_STATEFUL_OBJECT:
			
			updateInResponseStatefulObject(transazioneService, (InResponseStatefulObject)so.getObject(), so.getIdTransazione());
			if(this.debug)
				this.log.debug(tipoGestione+"Informazione emesse dall'in response handler aggiornate");
			
			break;
		}
	}
	
	
	public TransactionDB readTransazione(ITransazioneService transazioneService,String idTransazione) throws Exception{
		
		IPaginatedExpression expression = transazioneService.newPaginatedExpression();
		expression.equals(Transazione.model().ID_TRANSAZIONE, idTransazione);
		
		List<Map<String, Object>> result = null;
		try{
			result = transazioneService.select(expression,
					Transazione.model().ESITO, Transazione.model().ESITO_CONTESTO);
			if(result==null || result.size()<=0){
				if(this.debug){
					this.log.debug("Transazione con id ["+idTransazione+"] non trovata");
				}
				return null;
			}
		}catch(NotFoundException notFound){
			if(this.debug){
				this.log.debug("Transazione con id ["+idTransazione+"] non trovata (DriverNotFound): "+notFound.getMessage());
			}
			return null;
		}
		if(result.size()>1){
			if(this.debug){
				this.log.debug("Trovata piu' di una Transazione con id ["+idTransazione+"]");
			}
		}
		Map<String, Object> map = result.remove(0);
		
		TransactionDB tr = new TransactionDB();
		tr.setIdTransazione(idTransazione);
				
		Object oEsito = map.get(Transazione.model().ESITO.getFieldName());
		if(oEsito==null || !(oEsito instanceof Integer) ){
			throw new Exception("Salvata transazione senza esito ["+oEsito+"]");
		}
		Integer esito = (Integer) oEsito;
		
		Object oEsitoContesto = map.get(Transazione.model().ESITO_CONTESTO.getFieldName());
		if(oEsitoContesto==null || !(oEsitoContesto instanceof String) ){
			throw new Exception("Salvata transazione senza esito contesto ["+oEsitoContesto+"]");
		}
		String esitoContesto = (String) oEsitoContesto;
		
		tr.setEsitoTransazione(EsitiProperties.getInstance(this.log).convertToEsitoTransazione(esito, esitoContesto));
		
		if(this.debug){
			this.log.debug("Trovata transazione");
		}
		return tr;
		
	}
	
	
	private void updateDataUscitaRichiesta(ITransazioneService transazioneService,Date data,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().DATA_USCITA_RICHIESTA, data);
		transazioneService.updateFields(idTransazione, updateField);
		
	}
	
	private void updateDataIngressoRisposta(ITransazioneService transazioneService,Date data,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().DATA_INGRESSO_RISPOSTA, data);
		transazioneService.updateFields(idTransazione, updateField);
		
	}
	
	private void updateDimensioneUscitaRichiesta(ITransazioneService transazioneService,Long dimensione,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().RICHIESTA_USCITA_BYTES, dimensione);
		transazioneService.updateFields(idTransazione, updateField);
	
	}
	
	private void updateDimensioneIngressoRisposta(ITransazioneService transazioneService,Long dimensione,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().RISPOSTA_INGRESSO_BYTES, dimensione);
		transazioneService.updateFields(idTransazione, updateField);
		
	}
	
	private void updateLocationConnettore(ITransazioneService transazioneService,String value,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().LOCATION_CONNETTORE, value);
		transazioneService.updateFields(idTransazione, updateField);
		
	}
	
	private void updateTipoConnettore(ITransazioneService transazioneService,String value,String idTransazione) throws Exception{
		
		// TODO leggere le attuali informazioni e aggiornarle
		
	}
	
	private void updateCodiceTrasportoRichiesta(ITransazioneService transazioneService,String value,String idTransazione) throws Exception{
		
		// TODO leggere le attuali informazioni e aggiornarle
		
	}
	
	
	private void updateScenarioDiCooperazione(ITransazioneService transazioneService,String value,String idTransazione) throws Exception{
		
		UpdateField updateField = new UpdateField(Transazione.model().RUOLO_TRANSAZIONE, value);
		transazioneService.updateFields(idTransazione, updateField);
		
	}
	
	private void updateOutRequestStatefulObject(ITransazioneService transazioneService,OutRequestStatefulObject object,String idTransazione) throws Exception{
		
		List<UpdateField> updateFields = new ArrayList<UpdateField>();
		
		if(object.getDataUscitaRichiesta()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().DATA_USCITA_RICHIESTA, object.getDataUscitaRichiesta());
			updateFields.add(updateField);
		}
					
		RuoloTransazione ruolo = null;
		if(object.getScenarioCooperazione()!=null){
			ruolo = RuoloTransazione.getEnumConstantFromOpenSPCoopValue(object.getScenarioCooperazione());
			UpdateField updateField = new UpdateField(Transazione.model().RUOLO_TRANSAZIONE, ruolo.getValoreAsInt());
			updateFields.add(updateField);
		}
		
		if(object.getLocation()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().LOCATION_CONNETTORE, object.getLocation());
			updateFields.add(updateField);
		}
		
		if(object.getTipoConnettore()!=null){
			// TODO leggere le attuali informazioni e aggiornarle
		}
		
		if(object.getServiziApplicativiErogatore()!=null && object.getServiziApplicativiErogatore().size()>0){
			StringBuffer sa_erogatori = new StringBuffer();
			for (int i=0; i<object.getServiziApplicativiErogatore().size(); i++) {
				if (i>0){
					sa_erogatori.append(",");
				}
				sa_erogatori.append( object.getServiziApplicativiErogatore().get(i) );
			}
			UpdateField updateField = new UpdateField(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE, sa_erogatori.toString());
			updateFields.add(updateField);
		}
		
		if(object.getEventiGestione()!=null && object.getEventiGestione().size()>0){
			
			StringBuffer bf = new StringBuffer();
			for (String s : object.getEventiGestione()) {
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(s);
			}
			
			String attuale = null;
			try{
				IPaginatedExpression expression = transazioneService.newPaginatedExpression();
				expression.equals(Transazione.model().ID_TRANSAZIONE, idTransazione);
				List<Object> list = transazioneService.select(expression, Transazione.model().EVENTI_GESTIONE);
				if(list!=null && list.size()>0){
					Object o = list.get(0);
					if(o!=null && o instanceof String){
						attuale = (String) o;
					}
				}
			}catch(NotFoundException notFound){}

			if(attuale!=null){
				attuale = attuale + ",";
				attuale = attuale + bf.toString();
			}
			else{
				attuale = bf.toString();
			}
			
			UpdateField updateField = new UpdateField(Transazione.model().EVENTI_GESTIONE, attuale);
			updateFields.add(updateField);
		}
		
		if(updateFields.size()>0)
			transazioneService.updateFields(idTransazione, updateFields.toArray(new UpdateField[1]));
		
	}
	
	
	private void updateInResponseStatefulObject(ITransazioneService transazioneService,InResponseStatefulObject object,String idTransazione) throws Exception{
		
		List<UpdateField> updateFields = new ArrayList<UpdateField>();
		
		if(object.getDataAccettazioneRisposta()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA, object.getDataAccettazioneRisposta());
			updateFields.add(updateField);
		}
		
		if(object.getDataIngressoRisposta()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().DATA_INGRESSO_RISPOSTA, object.getDataIngressoRisposta());
			updateFields.add(updateField);
		}
		
		if(object.getDataUscitaRichiesta()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().DATA_USCITA_RICHIESTA, object.getDataUscitaRichiesta());
			updateFields.add(updateField);
		}
		
		if(object.getLocation()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().LOCATION_CONNETTORE, object.getLocation());
			updateFields.add(updateField);
		}
		
		if(object.getReturnCode()!=null){
			// TODO leggere le attuali informazioni e aggiornarle
		}
		
		if(object.getFaultIntegrazione()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().FAULT_INTEGRAZIONE, object.getFaultIntegrazione());
			updateFields.add(updateField);
		}
		
		if(object.getFormatoFaultIntegrazione()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().FORMATO_FAULT_INTEGRAZIONE, object.getFormatoFaultIntegrazione());
			updateFields.add(updateField);
		}
		
		if(object.getFaultCooperazione()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().FAULT_COOPERAZIONE, object.getFaultCooperazione());
			updateFields.add(updateField);
		}
		
		if(object.getFormatoFaultCooperazione()!=null){
			UpdateField updateField = new UpdateField(Transazione.model().FORMATO_FAULT_COOPERAZIONE, object.getFormatoFaultCooperazione());
			updateFields.add(updateField);
		}
		
		if(object.getEventiGestione()!=null && object.getEventiGestione().size()>0){
			
			StringBuffer bf = new StringBuffer();
			for (String s : object.getEventiGestione()) {
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(s);
			}
			
			String attuale = null;
			try{
				IPaginatedExpression expression = transazioneService.newPaginatedExpression();
				expression.equals(Transazione.model().ID_TRANSAZIONE, idTransazione);
				List<Object> list = transazioneService.select(expression, Transazione.model().EVENTI_GESTIONE);
				if(list!=null && list.size()>0){
					Object o = list.get(0);
					if(o!=null && o instanceof String){
						attuale = (String) o;
					}
				}
			}catch(NotFoundException notFound){}

			if(attuale!=null){
				attuale = attuale + ",";
				attuale = attuale + bf.toString();
			}
			else{
				attuale = bf.toString();
			}
			
			UpdateField updateField = new UpdateField(Transazione.model().EVENTI_GESTIONE, attuale);
			updateFields.add(updateField);
		}
		
		if(updateFields.size()>0)
			transazioneService.updateFields(idTransazione, updateFields.toArray(new UpdateField[1]));
		
	}
	
	
}
