/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.manifest.Protocol;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Protocol 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolModel extends AbstractModel<Protocol> {

	public ProtocolModel(){
	
		super();
	
		this.FACTORY = new Field("factory",java.lang.String.class,"protocol",Protocol.class);
		this.TRANSACTION = new org.openspcoop2.protocol.manifest.model.TransactionModel(new Field("transaction",org.openspcoop2.protocol.manifest.Transaction.class,"protocol",Protocol.class));
		this.NAME = new Field("name",java.lang.String.class,"protocol",Protocol.class);
		this.LABEL = new Field("label",java.lang.String.class,"protocol",Protocol.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"protocol",Protocol.class);
		this.WEB_SITE = new Field("webSite",java.lang.String.class,"protocol",Protocol.class);
		this.LOGGER = new Field("logger",boolean.class,"protocol",Protocol.class);
	
	}
	
	public ProtocolModel(IField father){
	
		super(father);
	
		this.FACTORY = new ComplexField(father,"factory",java.lang.String.class,"protocol",Protocol.class);
		this.TRANSACTION = new org.openspcoop2.protocol.manifest.model.TransactionModel(new ComplexField(father,"transaction",org.openspcoop2.protocol.manifest.Transaction.class,"protocol",Protocol.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"protocol",Protocol.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"protocol",Protocol.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"protocol",Protocol.class);
		this.WEB_SITE = new ComplexField(father,"webSite",java.lang.String.class,"protocol",Protocol.class);
		this.LOGGER = new ComplexField(father,"logger",boolean.class,"protocol",Protocol.class);
	
	}
	
	

	public IField FACTORY = null;
	 
	public org.openspcoop2.protocol.manifest.model.TransactionModel TRANSACTION = null;
	 
	public IField NAME = null;
	 
	public IField LABEL = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField WEB_SITE = null;
	 
	public IField LOGGER = null;
	 

	@Override
	public Class<Protocol> getModeledClass(){
		return Protocol.class;
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