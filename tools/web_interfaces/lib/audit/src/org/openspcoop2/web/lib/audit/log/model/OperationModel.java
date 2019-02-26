/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.lib.audit.log.model;

import org.openspcoop2.web.lib.audit.log.Operation;

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
	
		this.OBJECT_DETAILS = new Field("object_details",java.lang.String.class,"operation",Operation.class);
		this.BINARY = new org.openspcoop2.web.lib.audit.log.model.BinaryModel(new Field("binary",org.openspcoop2.web.lib.audit.log.Binary.class,"operation",Operation.class));
		this.TIPOLOGIA = new Field("tipologia",java.lang.String.class,"operation",Operation.class);
		this.TIPO_OGGETTO = new Field("tipo-oggetto",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_ID = new Field("object-id",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_OLD_ID = new Field("object-old-id",java.lang.String.class,"operation",Operation.class);
		this.UTENTE = new Field("utente",java.lang.String.class,"operation",Operation.class);
		this.STATO = new Field("stato",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_CLASS = new Field("object_class",java.lang.String.class,"operation",Operation.class);
		this.ERROR = new Field("error",java.lang.String.class,"operation",Operation.class);
		this.TIME_REQUEST = new Field("time-request",java.util.Date.class,"operation",Operation.class);
		this.TIME_EXECUTE = new Field("time-execute",java.util.Date.class,"operation",Operation.class);
		this.INTERFACE_MSG = new Field("interface-msg",java.lang.String.class,"operation",Operation.class);
	
	}
	
	public OperationModel(IField father){
	
		super(father);
	
		this.OBJECT_DETAILS = new ComplexField(father,"object_details",java.lang.String.class,"operation",Operation.class);
		this.BINARY = new org.openspcoop2.web.lib.audit.log.model.BinaryModel(new ComplexField(father,"binary",org.openspcoop2.web.lib.audit.log.Binary.class,"operation",Operation.class));
		this.TIPOLOGIA = new ComplexField(father,"tipologia",java.lang.String.class,"operation",Operation.class);
		this.TIPO_OGGETTO = new ComplexField(father,"tipo-oggetto",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_ID = new ComplexField(father,"object-id",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_OLD_ID = new ComplexField(father,"object-old-id",java.lang.String.class,"operation",Operation.class);
		this.UTENTE = new ComplexField(father,"utente",java.lang.String.class,"operation",Operation.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"operation",Operation.class);
		this.OBJECT_CLASS = new ComplexField(father,"object_class",java.lang.String.class,"operation",Operation.class);
		this.ERROR = new ComplexField(father,"error",java.lang.String.class,"operation",Operation.class);
		this.TIME_REQUEST = new ComplexField(father,"time-request",java.util.Date.class,"operation",Operation.class);
		this.TIME_EXECUTE = new ComplexField(father,"time-execute",java.util.Date.class,"operation",Operation.class);
		this.INTERFACE_MSG = new ComplexField(father,"interface-msg",java.lang.String.class,"operation",Operation.class);
	
	}
	
	

	public IField OBJECT_DETAILS = null;
	 
	public org.openspcoop2.web.lib.audit.log.model.BinaryModel BINARY = null;
	 
	public IField TIPOLOGIA = null;
	 
	public IField TIPO_OGGETTO = null;
	 
	public IField OBJECT_ID = null;
	 
	public IField OBJECT_OLD_ID = null;
	 
	public IField UTENTE = null;
	 
	public IField STATO = null;
	 
	public IField OBJECT_CLASS = null;
	 
	public IField ERROR = null;
	 
	public IField TIME_REQUEST = null;
	 
	public IField TIME_EXECUTE = null;
	 
	public IField INTERFACE_MSG = null;
	 

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