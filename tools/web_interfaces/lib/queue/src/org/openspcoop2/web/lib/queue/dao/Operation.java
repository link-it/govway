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



package org.openspcoop2.web.lib.queue.dao;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Vector;

import org.openspcoop2.web.lib.queue.costanti.OperationStatus;


/**
 * Operation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Operation {

	private Long id;
	private String operation;
	private String tipo;
	private String details;
	private String superUser;
	private String hostname;
	private OperationStatus status;
	private int waitTime;
	private Date timeReq;
	private Date timeExecute;
	private boolean deleted;
	private Vector<Operation> operazioniPrecedentiAncoraDaGestire = new Vector<Operation>();
	
	
	
	public void addOperazionePrecedenteAncoraDaGestire(Operation operation){
		this.operazioniPrecedentiAncoraDaGestire.add(operation);
	}
	
	public int sizeOperazioniPrecedentiAncoraDaGestireList(){
		return this.operazioniPrecedentiAncoraDaGestire.size();
	}
	
	public Operation getOperazionePrecedenteAncoraDaGestire(int index){
		return this.operazioniPrecedentiAncoraDaGestire.get(index);
	}
	
	private Vector<Parameter> parameters = new Vector<Parameter>();
	private java.util.Hashtable<String,String> parametersHash = new java.util.Hashtable<String,String>();
	
	public void addParameter(Parameter p){
		this.parameters.add(p);
		if(p.getValue()!=null){
			if(this.parametersHash.contains(p.getName())){
				String oldValue = this.parametersHash.get(p.getName());
				this.parametersHash.put(p.getName(),oldValue+" "+p.getValue());
			}else{
				this.parametersHash.put(p.getName(), p.getValue());
			}
		}
	}
	public Parameter getParameter(int index){
		return this.parameters.get(index);
	}
	public Parameter removeParameter(int index){
		return this.parameters.remove(index);
	}
	public Parameter[] getParameters(){
		return this.parameters.toArray(new Parameter[0]);
	}
	public String getParameterValue(String name){
		return this.parametersHash.get(name);
	}
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOperation() {
		return this.operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getSuperUser() {
		return this.superUser;
	}
	public void setSuperUser(String superUser) {
		this.superUser = superUser;
	}
	public String getHostname() {
		return this.hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public OperationStatus getStatus() {
		return this.status;
	}
	public void setStatus(OperationStatus status) {
		this.status = status;
	}
	
	public int getWaitTime() {
		return this.waitTime;
	}
	
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
		
	public Date getTimeReq() {
		return this.timeReq;
	}
	public void setTimeReq(Date timeReq) {
		this.timeReq = timeReq;
	}
	public Date getTimeExecute() {
		return this.timeExecute;
	}
	public void setTimeExecute(Date timeExecute) {
		this.timeExecute = timeExecute;
	}
	public boolean isDeleted() {
		return this.deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	@Override
	public String toString() {
		
		String pattern="id [{0,number,#}]" +
				" operation [{1}]" +
				" tipo [{2}]" +
				" superUser [{3}]" +
				" hostname [{4}]" +
				" status [{5}]" +
				" timeReq [{6}]" +
				" timeExecute [{7}]" +
				" deleted [{8}]" +
				" details [{9}]";
		return MessageFormat.format(pattern, this.id,this.operation,this.tipo,this.superUser,this.hostname,this.status.toString(),this.timeReq.toString(),this.timeExecute.toString(),""+this.deleted,this.details);
	}
	
}


