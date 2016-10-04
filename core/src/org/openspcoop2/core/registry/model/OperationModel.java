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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Operation;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Operation 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationModel extends AbstractModel<Operation> {

	public OperationModel(){
	
		super();
	
		this.MESSAGE_INPUT = new org.openspcoop2.core.registry.model.MessageModel(new Field("message-input",org.openspcoop2.core.registry.Message.class,"operation",Operation.class));
		this.MESSAGE_OUTPUT = new org.openspcoop2.core.registry.model.MessageModel(new Field("message-output",org.openspcoop2.core.registry.Message.class,"operation",Operation.class));
		this.PROF_AZIONE = new Field("prof-azione",java.lang.String.class,"operation",Operation.class);
		this.ID_PORT_TYPE = new Field("id-port-type",java.lang.Long.class,"operation",Operation.class);
		this.NOME = new Field("nome",java.lang.String.class,"operation",Operation.class);
		this.STYLE = new Field("style",java.lang.String.class,"operation",Operation.class);
		this.SOAP_ACTION = new Field("soap-action",java.lang.String.class,"operation",Operation.class);
		this.PROFILO_COLLABORAZIONE = new Field("profilo-collaborazione",java.lang.String.class,"operation",Operation.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"operation",Operation.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"operation",Operation.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"operation",Operation.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"operation",Operation.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"operation",Operation.class);
		this.CORRELATA_SERVIZIO = new Field("correlata-servizio",java.lang.String.class,"operation",Operation.class);
		this.CORRELATA = new Field("correlata",java.lang.String.class,"operation",Operation.class);
	
	}
	
	public OperationModel(IField father){
	
		super(father);
	
		this.MESSAGE_INPUT = new org.openspcoop2.core.registry.model.MessageModel(new ComplexField(father,"message-input",org.openspcoop2.core.registry.Message.class,"operation",Operation.class));
		this.MESSAGE_OUTPUT = new org.openspcoop2.core.registry.model.MessageModel(new ComplexField(father,"message-output",org.openspcoop2.core.registry.Message.class,"operation",Operation.class));
		this.PROF_AZIONE = new ComplexField(father,"prof-azione",java.lang.String.class,"operation",Operation.class);
		this.ID_PORT_TYPE = new ComplexField(father,"id-port-type",java.lang.Long.class,"operation",Operation.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"operation",Operation.class);
		this.STYLE = new ComplexField(father,"style",java.lang.String.class,"operation",Operation.class);
		this.SOAP_ACTION = new ComplexField(father,"soap-action",java.lang.String.class,"operation",Operation.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profilo-collaborazione",java.lang.String.class,"operation",Operation.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"operation",Operation.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"operation",Operation.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"operation",Operation.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"operation",Operation.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"operation",Operation.class);
		this.CORRELATA_SERVIZIO = new ComplexField(father,"correlata-servizio",java.lang.String.class,"operation",Operation.class);
		this.CORRELATA = new ComplexField(father,"correlata",java.lang.String.class,"operation",Operation.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.MessageModel MESSAGE_INPUT = null;
	 
	public org.openspcoop2.core.registry.model.MessageModel MESSAGE_OUTPUT = null;
	 
	public IField PROF_AZIONE = null;
	 
	public IField ID_PORT_TYPE = null;
	 
	public IField NOME = null;
	 
	public IField STYLE = null;
	 
	public IField SOAP_ACTION = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField CORRELATA_SERVIZIO = null;
	 
	public IField CORRELATA = null;
	 

	@Override
	public Class<Operation> getModeledClass(){
		return Operation.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}