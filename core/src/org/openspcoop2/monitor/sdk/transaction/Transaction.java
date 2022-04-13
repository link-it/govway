/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.exceptions.TransactionException;
import org.openspcoop2.monitor.sdk.exceptions.TransactionExceptionCode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Transaction
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Transaction {

	public Transaction(Logger log,DAOFactory daoFactory,Transazione transaction) throws ProtocolException{
		this(log,daoFactory,transaction,null,null,null);
	}
	public Transaction(Logger log,DAOFactory daoFactory,Transazione transaction,Traccia requestTrace,Traccia responseTrace,List<MsgDiagnostico> msgdiagnosticiList) throws ProtocolException{
		this.log = log;
		this.daoFactory = daoFactory;
		this.idTransazione = transaction.getIdTransazione();
		this.transactionResult = EsitiProperties.getInstanceFromProtocolName(this.log,transaction.getProtocollo()).convertToEsitoTransazione(transaction.getEsito(), transaction.getEsitoContesto());
		this.transaction = transaction;
		this.requestTrace = requestTrace;
		this.responseTrace = responseTrace;
		this.msgdiagnosticiList = msgdiagnosticiList;
	}
	
	/** Logger */
	private Logger log = LoggerWrapperFactory.getLogger(Transaction.class);
	public Logger getLogger(){
		return this.log;
	}
	
	/** DAOFactory */
	private DAOFactory daoFactory = null;
	public DAOFactory getDAOFactory(){
		return this.daoFactory;
	}
	
	
	/** Identificatore unico di transazione */
	private Transazione transaction;
	private String idTransazione;
	private EsitoTransazione transactionResult;
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public EsitoTransazione getTransactionResult() {
		return this.transactionResult;
	}
	public Transazione getTransaction() {
		return this.transaction;
	}
	
	
	/** Messaggi diagnostici */
	private List<MsgDiagnostico> msgdiagnosticiList = new ArrayList<MsgDiagnostico>();
	public void setMsgdiagnosticiList(List<MsgDiagnostico> msgdiagnosticiList) {
		this.msgdiagnosticiList = msgdiagnosticiList;
	}
	public List<MsgDiagnostico> getMsgDiagnostici(){
		return this.msgdiagnosticiList;
	}
	public void addMsgDiagnostico(MsgDiagnostico msgDiag){
		this.msgdiagnosticiList.add(msgDiag);
	}
	public void removeDiagnosticMessages(){
		this.msgdiagnosticiList.clear();
	}
	public MsgDiagnostico getDiagnosticMessage(int index){
		return this.msgdiagnosticiList.get(index);
	}
	public void removeDiagnosticMessage(int index){
		this.msgdiagnosticiList.remove(index);
	}
	public int countDiagnosticMessages(){
		return this.msgdiagnosticiList.size();
	}
	
	/** Tracce */
	private Traccia requestTrace;
	private Traccia responseTrace;
	public Traccia getRequestTrace() {
		return this.requestTrace;
	}
	public void setRequestTrace(Traccia requestTrace) {
		this.requestTrace = requestTrace;
	}
	public Traccia getResponseTrace() {
		return this.responseTrace;
	}
	public void setResponseTrace(Traccia responseTrace) {
		this.responseTrace = responseTrace;
	}

	/** Risorse di Contenuto */
	private List<AbstractContentResource> risorseContenuto = new ArrayList<AbstractContentResource>();
	public List<AbstractContentResource> getContentResources(){
		return this.risorseContenuto;
	}
	@SuppressWarnings("unchecked")
	public <ContentResourceType> List<ContentResourceType> getContentResourcesByType(Class<ContentResourceType> type){
		List<ContentResourceType> retList = new LinkedList<ContentResourceType>();
		if (this.risorseContenuto != null) {
			for (int i=0; i<this.risorseContenuto.size(); i++) {
				if (type.equals(this.risorseContenuto.get(i).getClass())) {
					retList.add((ContentResourceType)this.risorseContenuto.get(i));
				}
			}
		}
		return retList;
	}
	public AbstractContentResource getContentResourceByName(String name){
		AbstractContentResource retResource = null;
		if (this.risorseContenuto!=null) {
			for (int i=0; i<this.risorseContenuto.size(); i++) {
				if (name.equals(this.risorseContenuto.get(i).getName())) {
					retResource = this.risorseContenuto.get(i);
				}
			}
		}
		return retResource;
	}
	public void addContentResource(AbstractContentResource resource) throws TransactionException{
		// controlla che la risorsa che si vuole aggiungere non esista già
		for (int i=0; i<this.risorseContenuto.size(); i++) {
			if (resource.getName().equals(this.risorseContenuto.get(i).getName()))
				throw new TransactionException(TransactionExceptionCode.ADD_RES_EXIST);
		}
		this.risorseContenuto.add(resource);
	}
	public void updateContentResource(AbstractContentResource resource) throws TransactionException{
		int index = 0;
		boolean found = false;
		while (!found && this.risorseContenuto!=null && index<this.risorseContenuto.size()) {
			if (resource.getName().equals(this.risorseContenuto.get(index).getName()))
				found = true;
			else
				index++;
		}
		if (found)
			this.risorseContenuto.set(index, resource);
		else
			throw new TransactionException(TransactionExceptionCode.UPD_RES_NOT_EXIST);
	}
	public void removeContentResource(AbstractContentResource resource) throws TransactionException{
		int index = 0;
		boolean found = false;
		while (!found && this.risorseContenuto!=null && index<this.risorseContenuto.size()) {
			if (resource.getName().equals(this.risorseContenuto.get(index).getName()))
				found = true;
			else
				index++;
		}
		if (found)
			this.risorseContenuto.remove(index);
		else
			throw new TransactionException(TransactionExceptionCode.DEL_RES_NOT_EXIST);
	}
	
	


	/** Dati SLA */
	private SLATrace slaTrace;
	public SLATrace getSlaTrace() {
		return this.slaTrace;
	}
	public void setSlaTrace(SLATrace slaTrace) {
		this.slaTrace = slaTrace;
	}

	
}
