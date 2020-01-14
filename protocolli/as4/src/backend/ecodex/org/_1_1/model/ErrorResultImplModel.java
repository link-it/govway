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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.ErrorResultImpl;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErrorResultImpl 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorResultImplModel extends AbstractModel<ErrorResultImpl> {

	public ErrorResultImplModel(){
	
		super();
	
		this.ERROR_CODE = new Field("errorCode",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.ERROR_DETAIL = new Field("errorDetail",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.MESSAGE_IN_ERROR_ID = new Field("messageInErrorId",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.MSH_ROLE = new Field("mshRole",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.NOTIFIED = new Field("notified",java.util.Date.class,"errorResultImpl",ErrorResultImpl.class);
		this.TIMESTAMP = new Field("timestamp",java.util.Date.class,"errorResultImpl",ErrorResultImpl.class);
	
	}
	
	public ErrorResultImplModel(IField father){
	
		super(father);
	
		this.ERROR_CODE = new ComplexField(father,"errorCode",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.ERROR_DETAIL = new ComplexField(father,"errorDetail",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.MESSAGE_IN_ERROR_ID = new ComplexField(father,"messageInErrorId",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.MSH_ROLE = new ComplexField(father,"mshRole",java.lang.String.class,"errorResultImpl",ErrorResultImpl.class);
		this.NOTIFIED = new ComplexField(father,"notified",java.util.Date.class,"errorResultImpl",ErrorResultImpl.class);
		this.TIMESTAMP = new ComplexField(father,"timestamp",java.util.Date.class,"errorResultImpl",ErrorResultImpl.class);
	
	}
	
	

	public IField ERROR_CODE = null;
	 
	public IField ERROR_DETAIL = null;
	 
	public IField MESSAGE_IN_ERROR_ID = null;
	 
	public IField MSH_ROLE = null;
	 
	public IField NOTIFIED = null;
	 
	public IField TIMESTAMP = null;
	 

	@Override
	public Class<ErrorResultImpl> getModeledClass(){
		return ErrorResultImpl.class;
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