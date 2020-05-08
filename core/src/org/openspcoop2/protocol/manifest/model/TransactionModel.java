/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Transaction;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Transaction 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionModel extends AbstractModel<Transaction> {

	public TransactionModel(){
	
		super();
	
		this.ERROR_PROTOCOL = new Field("errorProtocol",boolean.class,"transaction",Transaction.class);
		this.ENVELOPE_ERROR_PROTOCOL = new Field("envelopeErrorProtocol",boolean.class,"transaction",Transaction.class);
		this.LABEL_ERROR_PROTOCOL = new Field("labelErrorProtocol",java.lang.String.class,"transaction",Transaction.class);
		this.EXTERNAL_FAULT = new Field("externalFault",boolean.class,"transaction",Transaction.class);
		this.LABEL_EXTERNAL_FAULT = new Field("labelExternalFault",java.lang.String.class,"transaction",Transaction.class);
	
	}
	
	public TransactionModel(IField father){
	
		super(father);
	
		this.ERROR_PROTOCOL = new ComplexField(father,"errorProtocol",boolean.class,"transaction",Transaction.class);
		this.ENVELOPE_ERROR_PROTOCOL = new ComplexField(father,"envelopeErrorProtocol",boolean.class,"transaction",Transaction.class);
		this.LABEL_ERROR_PROTOCOL = new ComplexField(father,"labelErrorProtocol",java.lang.String.class,"transaction",Transaction.class);
		this.EXTERNAL_FAULT = new ComplexField(father,"externalFault",boolean.class,"transaction",Transaction.class);
		this.LABEL_EXTERNAL_FAULT = new ComplexField(father,"labelExternalFault",java.lang.String.class,"transaction",Transaction.class);
	
	}
	
	

	public IField ERROR_PROTOCOL = null;
	 
	public IField ENVELOPE_ERROR_PROTOCOL = null;
	 
	public IField LABEL_ERROR_PROTOCOL = null;
	 
	public IField EXTERNAL_FAULT = null;
	 
	public IField LABEL_EXTERNAL_FAULT = null;
	 

	@Override
	public Class<Transaction> getModeledClass(){
		return Transaction.class;
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