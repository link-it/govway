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



package org.openspcoop2.web.lib.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.lib.queue.config.QueueProperties;
import org.openspcoop2.web.lib.queue.costanti.CostantiDB;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.FilterParameter;
import org.openspcoop2.web.lib.queue.dao.Operation;
import org.openspcoop2.web.lib.queue.dao.Parameter;

/**
 * ClassQueue
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ClassQueue {

	/** Connessone al Database */
	private Connection connectionDB;
	/** Connessione JMS */
	private Session sessionJMS;
	/** Tipo Database */
	private String tipoDatabase;
	/** JNDI Properties per Lookup Queue*/
	private Properties jndiContext;
	/** ConnectionFactoryName */
	private String connectionFactoryName;
	/**
	 *  Numero di volte prima che una operazione in WAIT diventi INVALID
	 */
	private int defaultWaitTime;

	public ClassQueue(Connection con,String tipoDatabase) throws ClassQueueException{
		this(con,tipoDatabase,null,true);
	}

	public ClassQueue(Connection con,String tipoDatabase, Session s) throws ClassQueueException{
		this(con,tipoDatabase,s,false);
	}

	private ClassQueue(Connection con,String tipoDatabase, Session s ,boolean autoCommit) throws ClassQueueException{
		this.connectionDB = con;
		this.sessionJMS = s;
		if(con==null)
			throw new ClassQueueException("Connessione al Database non definita");
		if((s==null) && (autoCommit==false))
			throw new ClassQueueException("Sessione JMS non definita");

		this.tipoDatabase = tipoDatabase;
		if(this.tipoDatabase==null)
			throw new ClassQueueException("TipoDatabase non definito");

		// Leggo file Properties
		try{
			QueueProperties queueProperties = null;
			try {
				queueProperties = QueueProperties.getInstance();

				if(autoCommit){
					this.connectionFactoryName = queueProperties.getConnectionFactory();
				}

				this.defaultWaitTime = queueProperties.getWaitTime();

				// Raccolta Proprieta JNDIContext
				this.jndiContext = queueProperties.getConnectionFactoryContext();

			} catch(java.lang.Exception e) {
				throw new ClassQueueException("Errore durante la costruzione della ClassQueue (Operation)",e);
			} 
		}catch(Exception e){}

	}

	/**
	 * Indica il numero di volte prima che una operazione in WAIT diventi INVALID
	 * @return numero di volte prima che una operazione in WAIT diventi INVALID
	 */
	public int getDefaultWaitTime() {
		return this.defaultWaitTime;
	}

	public void setDefaultWaitTime(int defaultWaitTime) {
		this.defaultWaitTime = defaultWaitTime;
	}

	public long insertQueue(String queueName, QueueOperation po) throws ClassQueueException{
		return insertQueue(queueName, po, null);
	}

	public long insertQueue(String queueName, QueueOperation po, String idForFilter) throws ClassQueueException{


		//Inserisco l'operazione nel db
		long idOp = 0;
		try {
			idOp = insertOperation (po, queueName,this.tipoDatabase);
		} catch (Exception e) {
			throw new ClassQueueException("ERRORE DURANTE L'INSERIMENTO IN DB OPERATIONS",e);
		}

		//Inserisco l'operazione nella coda indicata
		Queue queue = null;
		try {
			if(this.connectionFactoryName!=null){
				// Gestione in autoCommit
				InitialContext ctx = new InitialContext(this.jndiContext);
				QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(this.connectionFactoryName);
				QueueConnection qc = qcf.createQueueConnection();
				this.sessionJMS = qc.createQueueSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
				ctx.close();
			}

			// Lookup Queue
			InitialContext ctx = new InitialContext(this.jndiContext);
			queue = (Queue) ctx.lookup(queueName);
			ctx.close();

			// Message Producer
			MessageProducer sender = this.sessionJMS.createProducer(queue);

			// Create a message
			ObjectMessage message = this.sessionJMS.createObjectMessage(idOp);

			// Se e' stato specificato un id da filtrare
			if (idForFilter!=null && !idForFilter.equals(""))
				message.setStringProperty("ID",idForFilter);

			// send a message
			sender.send(message);

		} catch (Exception e) {
			throw new ClassQueueException("ERRORE DURANTE L'INSERIMENTO IN CODA ["+queueName+"]",e);
		}

		return idOp;
	}

	public long insertOperation(QueueOperation po, String hostname,String tipoDatabase)
	throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			//Inserimento della traccia nel DB
			if(!TipiDatabase.isAMember(this.tipoDatabase)){
				throw new Exception("Tipo database ["+this.tipoDatabase+"] non supportato");
			}
			TipiDatabase tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
			// ** Preparazione parametri
			Timestamp timeRequestT = new java.sql.Timestamp(System.currentTimeMillis());
			Timestamp timeExecuteT = timeRequestT;
			// ** Insert and return generated key
			CustomKeyGeneratorObject customKeyGeneratorObject = new CustomKeyGeneratorObject(CostantiDB.OPERATIONS_TABLE, CostantiDB.OPERATIONS_TABLE_ID, 
					CostantiDB.OPERATIONS_TABLE_SEQUENCE, CostantiDB.OPERATIONS_TABLE_FOR_ID_SEQUENCE);
			long idoperazione = InsertAndGeneratedKey.insertAndReturnGeneratedKey(this.connectionDB, tipo, customKeyGeneratorObject, 
						new InsertAndGeneratedKeyObject("operation", po.getOperazione().name(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("tipo", po.getTipoOperazione().name(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("superuser", po.getSuperuser(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("hostname", hostname, InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("status", OperationStatus.NOT_SET.name(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("timereq", timeRequestT, InsertAndGeneratedKeyJDBCType.TIMESTAMP),
						new InsertAndGeneratedKeyObject("timexecute", timeExecuteT, InsertAndGeneratedKeyJDBCType.TIMESTAMP));
			if(idoperazione<=0){
				throw new Exception("ID autoincrementale non ottenuto");
			}						

			/* Inserisco parametri */
			for (int i=0; i<po.sizeParametri(); i++) {
				QueueParameter pp = po.getParametro(i);
				String updateString = "INSERT INTO parameters (id_operations, name, value) VALUES (?, ?, ?)";
				stmt = this.connectionDB.prepareStatement(updateString);
				stmt.setLong (1, idoperazione);
				stmt.setString (2, pp.getNome());
				stmt.setString (3, pp.getValore());
				stmt.executeUpdate();
				stmt.close();
			}

			return idoperazione;
		} catch (Exception ex) {
			java.util.Date now = new java.util.Date();
			throw new Exception (now+" OperationDBException: "+ex.getMessage(),ex);
		} finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception e){}
		}
	}
	/**
	 * Aggiorna le informazioni relative ad un'operazione
	 * L'operation passata come parametro deve avere id>0 e stato impostato altrimenti verra generata un'eccezione.
	 * @param operation L'operation da aggiornare
	 * @return righe modificate
	 * @throws Exception
	 */
	public int updateOperation(Operation operation) throws ClassQueueException{
		PreparedStatement stm=null;
		try{

			if(operation==null) throw new Exception("L'oggetto Operation passato come parametro non e' valido.");
			if(operation.getId()<=0) throw new Exception("L'id dell'oggetto Operation passato come parametro non e' valido.");
			if(operation.getStatus()==null || "".equals(operation.getStatus().toString())) throw new Exception("Stato Operation non impostato");

			//impostazioni time stamp
			Timestamp timereq = (operation.getTimeReq()!=null ? (new Timestamp(operation.getTimeReq().getTime())) : new Timestamp(System.currentTimeMillis()));
			Timestamp timexecute = (operation.getTimeExecute()!=null ? (new Timestamp(operation.getTimeExecute().getTime())) : new Timestamp(System.currentTimeMillis()));

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addUpdateField("operation", "?");
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addUpdateField("superuser", "?");
			sqlQueryObject.addUpdateField("hostname", "?");
			sqlQueryObject.addUpdateField("status", "?");
			sqlQueryObject.addUpdateField("details", "?");
			sqlQueryObject.addUpdateField("timereq", "?");
			sqlQueryObject.addUpdateField("timexecute", "?");
			sqlQueryObject.addUpdateField("deleted", "?");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLUpdate();

			stm=this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, operation.getOperation());
			stm.setString(2, operation.getTipo());
			stm.setString(3, operation.getSuperUser());
			stm.setString(4, operation.getHostname());

			String status = operation.getStatus().toString();
			//appendo il tempo di wait in caso sia una operazione di wait
			if(OperationStatus.WAIT==operation.getStatus()){
				status+="_"+operation.getWaitTime();
			}
			stm.setString(5, status);
			stm.setString(6, operation.getDetails());
			stm.setTimestamp(7,timereq);
			stm.setTimestamp(8, timexecute);
			stm.setInt(9, operation.isDeleted() ? 1 : 0);
			stm.setLong(10, operation.getId());

			int n = stm.executeUpdate();

			return n;
		}catch (Exception e) {
			throw new ClassQueueException("Errore durante updateOperation",e);
		}finally{
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	/**
	 * Filtra una operazione duplicata impostando come status SUCCESS
	 * @param operationDaFiltrare
	 * @param filtrante
	 * @return righe modificate
	 * @throws ClassQueueException
	 */
	public int filterOperation(Operation operationDaFiltrare,Operation filtrante) throws ClassQueueException{
		PreparedStatement stm=null;
		try{

			if(operationDaFiltrare==null) throw new Exception("L'oggetto Operation passato come parametro non e' valido.");
			if(operationDaFiltrare.getId()<=0) throw new Exception("L'id dell'oggetto Operation passato come parametro non e' valido.");
			if(operationDaFiltrare.getStatus()==null || "".equals(operationDaFiltrare.getStatus().toString())) throw new Exception("Stato Operation non impostato");

			if(filtrante==null) throw new Exception("L'oggetto Operation passato come parametro non e' valido.");
			if(filtrante.getId()<=0) throw new Exception("L'id dell'oggetto Operation passato come parametro non e' valido.");
			if(filtrante.getStatus()==null || "".equals(filtrante.getStatus().toString())) throw new Exception("Stato Operation non impostato");

			//impostazioni time stamp
			Timestamp timereq = (operationDaFiltrare.getTimeReq()!=null ? (new Timestamp(operationDaFiltrare.getTimeReq().getTime())) : new Timestamp(System.currentTimeMillis()));
			Timestamp timexecute = (operationDaFiltrare.getTimeExecute()!=null ? (new Timestamp(operationDaFiltrare.getTimeExecute().getTime())) : new Timestamp(System.currentTimeMillis()));

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addUpdateField("operation", "?");
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addUpdateField("superuser", "?");
			sqlQueryObject.addUpdateField("hostname", "?");
			sqlQueryObject.addUpdateField("status", "?");
			sqlQueryObject.addUpdateField("details", "?");
			sqlQueryObject.addUpdateField("timereq", "?");
			sqlQueryObject.addUpdateField("timexecute", "?");
			sqlQueryObject.addUpdateField("deleted", "?");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLUpdate();

			stm=this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, operationDaFiltrare.getOperation());
			stm.setString(2, operationDaFiltrare.getTipo());
			stm.setString(3, operationDaFiltrare.getSuperUser());
			stm.setString(4, operationDaFiltrare.getHostname());
			stm.setString(5, OperationStatus.SUCCESS.toString());//imposto status dell'operazione da filtrare a SUCCESS
			stm.setString(6, "Operazione ["+operationDaFiltrare.toString()+"] filtrata da operazione ["+filtrante.toString()+"]");
			stm.setTimestamp(7,timereq);
			stm.setTimestamp(8, timexecute);
			stm.setInt(9, operationDaFiltrare.isDeleted() ? 1 : 0);
			stm.setLong(10, operationDaFiltrare.getId());

			int n = stm.executeUpdate();

			return n;
		}catch (Exception e) {
			throw new ClassQueueException("Errore durante updateOperation",e);
		}finally{
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	/**
	 * Ritorna l'operazione con uno specifico id
	 * @param idOperation
	 * @return Operation
	 * @throws OperationNotFound
	 * @throws Exception
	 */
	public Operation getOperation(long idOperation) throws OperationNotFound, ClassQueueException{

		if(idOperation<=0) throw new ClassQueueException("L'id dell'oggetto Operation passato come parametro non e' valido.");


		Operation operation=null;
		PreparedStatement stm=null;
		ResultSet rs=null;
		try{

			// Select operation
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idOperation);
			rs=stm.executeQuery();
			if(rs.next()){
				operation=new Operation();
				operation.setId(idOperation);
				operation.setDeleted(rs.getInt("deleted")>0 ? true : false);
				operation.setDetails(rs.getString("details"));
				operation.setHostname(rs.getString("hostname"));
				operation.setOperation(rs.getString("operation"));

				String val=(rs.getString("status")!=null && !"".equals(rs.getString("status")) ? rs.getString("status") : OperationStatus.NOT_SET.toString());
				OperationStatus status=null;
				if(val.startsWith("WAIT_")){
					status = OperationStatus.WAIT;
					String timeWait=val.substring("WAIT_".length());
					try{
						//imposto il valore contenuto nella stringa
						operation.setWaitTime(Integer.valueOf(timeWait));
					}catch (Exception e) {
						//non riesco a parsare la stringa contenente il wait time
						//imposto quello di default cosi l'operazione verra impostata come invalid
						operation.setWaitTime(this.defaultWaitTime);
					}

				}else{
					status = OperationStatus.valueOf(val);
				}

				operation.setStatus(status);
				operation.setSuperUser(rs.getString("superuser"));
				Timestamp timexecute = rs.getTimestamp("timexecute");
				operation.setTimeExecute(new Date(timexecute.getTime()));
				Timestamp timereq = rs.getTimestamp("timereq");
				operation.setTimeReq(new Date(timereq.getTime()));
				operation.setTipo(rs.getString("tipo"));
			}else{
				throw new OperationNotFound("L'Operation con id="+idOperation+" non esiste");
			}
			rs.close();
			stm.close();


			// Select Parameter
			if(operation!=null){
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.PARAMETERS_TABLE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_operations = ?");
				String queryString = sqlQueryObject.createSQLQuery();
				stm=this.connectionDB.prepareStatement(queryString);
				stm.setLong(1, idOperation);
				rs = stm.executeQuery();
				while (rs.next()) {
					Parameter p = new Parameter();
					p.setName(rs.getString("name"));
					p.setValue(rs.getString("value"));
					operation.addParameter(p);
				}
				rs.close();
				stm.close();
			}

			return operation;
		}catch (OperationNotFound e) {
			throw e;
		}catch (Exception e) {
			throw new ClassQueueException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}


	/**
	 * Ritorna le operazioni che sono precedenti all'operazione in gestione, che rispettano i filtri selezionati
	 * 
	 * @param operation Operation
	 * @throws OperationNotFound
	 */
	public void setOperazioniPrecedentiByFilterSearch(Operation operation,FilterParameter[] filter,boolean deleted,String oggetto) throws OperationNotFound, ClassQueueException{

		if(filter==null) throw new ClassQueueException("Il parametro non e' valido.");


		PreparedStatement stm=null;
		ResultSet rs=null;
		String sqlQueryString=null;
		try{

			// Select operation
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addFromTable(CostantiDB.PARAMETERS_TABLE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.OPERATIONS_TABLE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".id = "+CostantiDB.PARAMETERS_TABLE+".id_operations");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".deleted = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".status <> '"+OperationStatus.SUCCESS+"'");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".status <> '"+OperationStatus.INVALID+"'");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".hostname = ?");
			sqlQueryObject.addWhereCondition(false, CostantiDB.OPERATIONS_TABLE+".timereq < ?", CostantiDB.OPERATIONS_TABLE+".timereq = ? AND "+CostantiDB.OPERATIONS_TABLE+".id < ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PARAMETERS_TABLE+".name = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PARAMETERS_TABLE+".value = ?");
			StringBuffer substring = new StringBuffer();
			substring.append(" ( ");
			for(int i=0; i<filter.length; i++){
				if(i>0)
					substring.append(" OR ");
				substring.append(" ( ");
				for(int j=0; j<filter[i].sizeFilterParameters(); j++){
					if(j>0)
						substring.append(" AND ");
					substring.append("(parameters.name=? AND parameters.value=?)");
				}
				substring.append(" ) ");
			}
			substring.append(" ) ");
			sqlQueryObject.addWhereCondition(substring.toString());
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryString = sqlQueryObject.createSQLQuery();
			stm=this.connectionDB.prepareStatement(sqlQueryString);
			if(deleted){
				stm.setLong(1, 1);
				sqlQueryString = sqlQueryString.replaceFirst("\\?", "'1'");
			}
			else{
				stm.setLong(1, 0);
				sqlQueryString = sqlQueryString.replaceFirst("\\?", "'0'");
			}
			stm.setString(2, operation.getHostname());
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+operation.getHostname()+"'");
			Timestamp t = new Timestamp(operation.getTimeReq().getTime());
			stm.setTimestamp(3, t);
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+t.toString()+"'");
			stm.setTimestamp(4, t);
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+t.toString()+"'");
			stm.setLong(5, operation.getId());
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+operation.getId()+"'");
			stm.setString(6, "Oggetto");
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'Oggetto'");
			stm.setString(7, oggetto);
			sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+oggetto+"'");
			int intp = 8;
			for(int i=0; i<filter.length; i++){
				for(int j=0; j<filter[i].sizeFilterParameters(); j++){
					stm.setString(intp, filter[i].getParameter(j).getName());
					sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+filter[i].getParameter(j).getName()+"'");
					intp++;
					stm.setString(intp, filter[i].getParameter(j).getValue());
					sqlQueryString = sqlQueryString.replaceFirst("\\?", "'"+filter[i].getParameter(j).getValue()+"'");
					intp++;
				}
			}
			//System.out.println("Query: "+sqlQueryString);
			rs=stm.executeQuery();
			while(rs.next()){
				Operation precedente = this.getOperation(rs.getLong("id"));
				operation.addOperazionePrecedenteAncoraDaGestire(precedente);
			}
			rs.close();
			stm.close();

		}catch (OperationNotFound e) {
			if(sqlQueryString!=null)
				throw new OperationNotFound("SQLQuery["+sqlQueryString+"]: "+e.getMessage(),e);
			else
				throw e;
		}catch (Exception e) {
			if(sqlQueryString!=null)
				throw new ClassQueueException("SQLQuery["+sqlQueryString+"]: "+e.getMessage(),e);
			else
				throw new ClassQueueException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	/*
	 * METODI PER INTERFACCIA GRAFICA
	 */
	public List<Operation> operationsList(ISearch ricerca, OperationStatus os, Vector<String> utenti, String logAdm, String hostname, String pezzoAny, String daSql, String aSql) throws ClassQueueException {
		int offset;
		int limit;
		int idLista = 0;
		if (os.toString().equals("SUCCESS"))
			idLista = Liste.OPERATIONS_ESEGUITE;
		if (os.toString().equals("ERROR"))
			idLista = Liste.OPERATIONS_FALLITE;
		if (os.toString().equals("INVALID"))
			idLista = Liste.OPERATIONS_INVALIDE;
		if (os.toString().equals("NOT_SET"))
			idLista = Liste.OPERATIONS_CODA;
		if (os.toString().equals("WAIT"))
			idLista = Liste.OPERATIONS_WAITING;
		String search;
		String queryString;
		String filtroSel;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		filtroSel = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getFilter(idLista)) ? "*" : ricerca.getFilter(idLista));

		Vector<String> newUtenti = new Vector<String>();
		if (filtroSel.equals("*")) {
			newUtenti.add(logAdm);
			for (int i = 0; i < utenti.size(); i++)
				newUtenti.add((String) utenti.elementAt(i));
		} else
			newUtenti.add(filtroSel);

		String pezzoUtenti = "(";
		for (int i = 0; i < newUtenti.size(); i++) {
			if (!pezzoUtenti.equals("("))
				pezzoUtenti += " OR";
			pezzoUtenti += " superuser='" + newUtenti.elementAt(i) + "'";
		}
		pezzoUtenti += ")";

		PreparedStatement stm = null;
		ResultSet rs = null;
		ArrayList<Operation> lista = new ArrayList<Operation>();

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("status = ?");
			if (!hostname.equals(""))
				sqlQueryObject.addWhereCondition("hostname = ?");
			sqlQueryObject.addWhereCondition(pezzoUtenti);
			if (!pezzoAny.equals(""))
				sqlQueryObject.addWhereCondition(pezzoAny);
			if (!daSql.equals(""))
				sqlQueryObject.addWhereCondition("timereq > ?");
			if (!aSql.equals(""))
				sqlQueryObject.addWhereCondition("timereq < ?");
			if (!search.equals(""))
				sqlQueryObject.addWhereLikeCondition("hostname", search, true, true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(queryString);
			stm.setString(1, os.toString());
			if (!hostname.equals(""))
				stm.setString(2, hostname);
			if (!daSql.equals(""))
				stm.setString(3, daSql);
			if (!aSql.equals(""))
				stm.setString(4, aSql);
			rs = stm.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista, rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("operation");
			sqlQueryObject.addSelectField("timereq");
			sqlQueryObject.addWhereCondition("status = ?");
			if (!hostname.equals(""))
				sqlQueryObject.addWhereCondition("hostname = ?");
			sqlQueryObject.addWhereCondition(pezzoUtenti);
			if (!pezzoAny.equals(""))
				sqlQueryObject.addWhereCondition(pezzoAny);
			if (!daSql.equals(""))
				sqlQueryObject.addWhereCondition("timereq > ?");
			if (!aSql.equals(""))
				sqlQueryObject.addWhereCondition("timereq < ?");
			if (!search.equals(""))
				sqlQueryObject.addWhereLikeCondition("hostname", search, true, true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("timereq");
			sqlQueryObject.setSortType(false);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(queryString);
			stm.setString(1, os.toString());
			if (!hostname.equals(""))
				stm.setString(2, hostname);
			if (!daSql.equals(""))
				stm.setString(3, daSql);
			if (!aSql.equals(""))
				stm.setString(4, aSql);
			rs = stm.executeQuery();

			Operation op = null;
			while (rs.next()) {
				op = getOperation(rs.getLong("id"));
				lista.add(op);
			}

			return lista;
		} catch (Exception qe) {
			throw new ClassQueueException("[ClassQueue::operationsList] Errore : " + qe.getMessage(),qe);
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) rs.close();
				if (stm != null) stm.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public Vector<String> hostnameList() throws ClassQueueException {
		String queryString;
		PreparedStatement stm = null;
		ResultSet rs = null;
		Vector<String> lista = new Vector<String>();

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("hostname");
			queryString = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(queryString);
			rs = stm.executeQuery();
			while (rs.next())
				lista.addElement(rs.getString("hostname"));
			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new ClassQueueException("[ClassQueue::hostnameList] Errore : " + qe.getMessage(),qe);
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) rs.close();
				if (stm != null) stm.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	
	
	public boolean existsOperationNotCompleted(String operation,String hostname,FilterParameter filtro) throws OperationNotFound, ClassQueueException{

		PreparedStatement stm=null;
		ResultSet rs=null;
		try{

			// Select operation
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.OPERATIONS_TABLE);
			sqlQueryObject.addFromTable(CostantiDB.PARAMETERS_TABLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".id = "+CostantiDB.PARAMETERS_TABLE+".id_operations");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".operation = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".hostname = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".status<>?");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".status<>?");
			sqlQueryObject.addWhereCondition(CostantiDB.OPERATIONS_TABLE+".status<>?");
			for(int i=0; i<filtro.sizeFilterParameters(); i++){
				ISQLQueryObject sqlQueryObjectEXISTS =  SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObjectEXISTS.addFromTable(CostantiDB.PARAMETERS_TABLE);
				sqlQueryObjectEXISTS.addWhereCondition(CostantiDB.PARAMETERS_TABLE+".name=?");
				sqlQueryObjectEXISTS.addWhereCondition(CostantiDB.PARAMETERS_TABLE+".value=?");
				sqlQueryObjectEXISTS.addWhereCondition(CostantiDB.PARAMETERS_TABLE+".id_operations="+CostantiDB.OPERATIONS_TABLE+".id");
				sqlQueryObjectEXISTS.setANDLogicOperator(true);
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectEXISTS);
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=this.connectionDB.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, operation);
			stm.setString(index++, hostname);
			stm.setString(index++, OperationStatus.SUCCESS.toString());
			stm.setString(index++, OperationStatus.INVALID.toString());
			stm.setString(index++, OperationStatus.DELETED.toString());
			for(int i=0; i<filtro.sizeFilterParameters(); i++){
				stm.setString(index++, filtro.getParameter(i).getName());
				stm.setString(index++, filtro.getParameter(i).getValue());
			}
			
			rs=stm.executeQuery();
			return rs.next();
			
		}catch (Exception e) {
			throw new ClassQueueException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

}
