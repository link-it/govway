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
package eu.domibus.configuration.model;

import eu.domibus.configuration.ErrorHandling;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErrorHandling 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorHandlingModel extends AbstractModel<ErrorHandling> {

	public ErrorHandlingModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"errorHandling",ErrorHandling.class);
		this.ERROR_AS_RESPONSE = new Field("errorAsResponse",boolean.class,"errorHandling",ErrorHandling.class);
		this.BUSINESS_ERROR_NOTIFY_PRODUCER = new Field("businessErrorNotifyProducer",boolean.class,"errorHandling",ErrorHandling.class);
		this.BUSINESS_ERROR_NOTIFY_CONSUMER = new Field("businessErrorNotifyConsumer",boolean.class,"errorHandling",ErrorHandling.class);
		this.DELIVERY_FAILURE_NOTIFY_PRODUCER = new Field("deliveryFailureNotifyProducer",boolean.class,"errorHandling",ErrorHandling.class);
	
	}
	
	public ErrorHandlingModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"errorHandling",ErrorHandling.class);
		this.ERROR_AS_RESPONSE = new ComplexField(father,"errorAsResponse",boolean.class,"errorHandling",ErrorHandling.class);
		this.BUSINESS_ERROR_NOTIFY_PRODUCER = new ComplexField(father,"businessErrorNotifyProducer",boolean.class,"errorHandling",ErrorHandling.class);
		this.BUSINESS_ERROR_NOTIFY_CONSUMER = new ComplexField(father,"businessErrorNotifyConsumer",boolean.class,"errorHandling",ErrorHandling.class);
		this.DELIVERY_FAILURE_NOTIFY_PRODUCER = new ComplexField(father,"deliveryFailureNotifyProducer",boolean.class,"errorHandling",ErrorHandling.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField ERROR_AS_RESPONSE = null;
	 
	public IField BUSINESS_ERROR_NOTIFY_PRODUCER = null;
	 
	public IField BUSINESS_ERROR_NOTIFY_CONSUMER = null;
	 
	public IField DELIVERY_FAILURE_NOTIFY_PRODUCER = null;
	 

	@Override
	public Class<ErrorHandling> getModeledClass(){
		return ErrorHandling.class;
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