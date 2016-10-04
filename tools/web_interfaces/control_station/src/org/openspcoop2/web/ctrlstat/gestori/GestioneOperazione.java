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


package org.openspcoop2.web.ctrlstat.gestori;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;

import javax.jms.QueueSession;

import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.FilterParameter;
import org.openspcoop2.web.lib.queue.dao.Parameter;

/**
 * Gestisce l'operazione del gestore
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestioneOperazione {

	private DBManager dbManager;
	private Connection connectionDB;
	private QueueSession sessionJMS;
	private org.openspcoop2.web.lib.queue.dao.Operation operation;
	private ClassQueue operationManager;
	private Logger log;
	private String idGestore;
	private String tipoOperazioneCRUD;

	public GestioneOperazione(DBManager db, Connection c, QueueSession s, org.openspcoop2.web.lib.queue.dao.Operation o, ClassQueue operationManager, 
			Logger l, String tipoOperazioneCRUD, String idGestore) {
		this.dbManager = db;
		this.connectionDB = c;
		this.sessionJMS = s;
		this.operation = o;
		this.operationManager = operationManager;
		this.log = l;
		this.idGestore = idGestore;
		if (tipoOperazioneCRUD != null)
			this.tipoOperazioneCRUD = "." + tipoOperazioneCRUD;
		else
			this.tipoOperazioneCRUD = "";
	}

	public void error(String msg) throws Exception {
		error(msg, null);
	}

	public void error(String msgParam, Exception e) throws Exception {
		
		String msgErrore = msgParam;
		if(e!=null){
			PrintWriter pw = null;
			ByteArrayOutputStream out = null;
			try{
				out = new ByteArrayOutputStream();
				pw = new PrintWriter(out);
				e.printStackTrace(pw);
				pw.flush();
				out.flush();
			}finally{
				try{
					if(pw!=null){
						pw.close();
					}
				}catch(Exception eClose){}
				try{
					if(out!=null){
						out.close();
					}
				}catch(Exception eClose){}
			}
			msgErrore = msgErrore +"\n"+out.toString();
		}
		
		if ((e != null) && (e instanceof java.net.ConnectException)) {
			this.log.warn("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msgParam, e);
		} else {
			
			if(e!=null && Utilities.existsInnerException(e, java.net.ConnectException.class)){
				this.log.warn("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msgParam, e);
			}
			else{
				if (e != null)
					this.log.error("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msgParam, e);
				else
					this.log.error("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msgParam);
			}
		}
		this.operation.setStatus(OperationStatus.ERROR);
		this.operation.setDetails(msgErrore);
		this.operation.setTimeExecute(new Timestamp(System.currentTimeMillis()));
		this.operationManager.updateOperation(this.operation);
		this.sessionJMS.rollback();
		this.dbManager.releaseConnection(this.connectionDB);
	}

	public void invalid(String msg) throws Exception {
		this.log.error("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msg);
		this.operation.setStatus(OperationStatus.INVALID);
		this.operation.setDetails(msg);
		this.operation.setTimeExecute(new Timestamp(System.currentTimeMillis()));
		this.operationManager.updateOperation(this.operation);
		this.sessionJMS.commit();
		this.dbManager.releaseConnection(this.connectionDB);
	}

	/**
	 * Una operazione (DI ADD) puo essere in attesa (wait) che si verificano
	 * determinate condizioni (commit) affinche essa diventi valida.
	 * L'operazione puo rimanere in wait per un numero determinato di volte
	 * "waitTime" dopodicche verra settata come invalida
	 * 
	 * In caso di operazioni != da ADD viene subito invalidata
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void waitBeforeInvalid(String msg) throws Exception {
		this.log.warn("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msg);

		/**
		 * Se l'operazione non e' di add non attendo ma invalido subito
		 */
		if (!"add".equals(this.operation.getOperation())) {
			this.operation.setStatus(OperationStatus.INVALID);
			this.operation.setDetails(msg);
		} else if (this.operation.getWaitTime() > this.operationManager.getDefaultWaitTime()) {
			// se operazione di add ma limite wait superato allora invalido
			this.log.debug("L operazione " + this.operation.toString() + " ha superato il WAIT_TIME(" + this.operationManager.getDefaultWaitTime() + ") setto operazione come INVALID.");
			this.operation.setStatus(OperationStatus.INVALID);
			this.operation.setDetails("Superato WAIT_TIME(" + this.operationManager.getDefaultWaitTime() + ") <<>> Old details:" + msg);
		} else {
			// add e limite wait non superato, attendo incrementando il
			// wait_time di questa operation
			this.operation.setStatus(OperationStatus.WAIT);
			this.operation.setWaitTime(this.operation.getWaitTime() + 1);// incremento
			// waittime
			this.operation.setDetails(msg);
		}

		this.operation.setTimeExecute(new Timestamp(System.currentTimeMillis()));
		this.operationManager.updateOperation(this.operation);

		// se operazione in wait allora rollo, altrimenti committo
		switch (this.operation.getStatus()) {
			case WAIT:
				this.sessionJMS.rollback();
				break;
			default:
				this.sessionJMS.commit();
				break;
		}

		this.dbManager.releaseConnection(this.connectionDB);

	}

	public void success(String msg) throws Exception {
		this.log.info("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " " + msg);
		this.operation.setStatus(OperationStatus.SUCCESS);
		this.operation.setDetails(msg);
		this.operation.setTimeExecute(new Timestamp(System.currentTimeMillis()));
		this.operationManager.updateOperation(this.operation);
		this.sessionJMS.commit();
		this.dbManager.releaseConnection(this.connectionDB);
	}

	public void delete() throws Exception {
		this.operation.setStatus(OperationStatus.DELETED);
		this.operation.setDetails("[" + this.idGestore + "]" + this.tipoOperazioneCRUD + " Operazione Marcata come DELETED");
		this.operation.setTimeExecute(new Date(System.currentTimeMillis()));
		this.operationManager.updateOperation(this.operation);
		this.sessionJMS.commit();
		this.dbManager.releaseConnection(this.connectionDB);
	}
	
	public boolean existsOperationNotCompleted(String tipoOperazione,String hostname,FilterParameter filtro) throws Exception{
		return this.operationManager.existsOperationNotCompleted(tipoOperazione, hostname,filtro);
	}

	public FilterParameter getFilterChangeIDSoggetto(String tipoSogg,String nomeSogg,String oldTipoSogg,String oldNomeSogg){
		FilterParameter filtro = new FilterParameter();
		Parameter param = new Parameter();
		param.setName(OperationsParameter.OGGETTO.getNome());
		param.setValue("soggetto");
		filtro.addFilterParameter(param);
		Parameter paramTipoSoggetto = new Parameter();
		paramTipoSoggetto.setName(OperationsParameter.TIPO_SOGGETTO.getNome());
		paramTipoSoggetto.setValue(tipoSogg);
		filtro.addFilterParameter(paramTipoSoggetto);
		Parameter paramNomeSoggetto = new Parameter();
		paramNomeSoggetto.setName(OperationsParameter.NOME_SOGGETTO.getNome());
		paramNomeSoggetto.setValue(nomeSogg);
		filtro.addFilterParameter(paramNomeSoggetto);
		Parameter paramOldTipoSoggetto = new Parameter();
		paramOldTipoSoggetto.setName(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
		paramOldTipoSoggetto.setValue(oldTipoSogg);
		filtro.addFilterParameter(paramOldTipoSoggetto);
		Parameter paramOldNomeSoggetto = new Parameter();
		paramOldNomeSoggetto.setName(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
		paramOldNomeSoggetto.setValue(oldNomeSogg);
		filtro.addFilterParameter(paramOldNomeSoggetto);
		return filtro;
	}
}
