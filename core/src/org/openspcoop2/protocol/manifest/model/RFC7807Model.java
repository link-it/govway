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

import org.openspcoop2.protocol.manifest.RFC7807;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RFC7807 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RFC7807Model extends AbstractModel<RFC7807> {

	public RFC7807Model(){
	
		super();
	
		this.USE_ACCEPT_HEADER = new Field("useAcceptHeader",boolean.class,"RFC7807",RFC7807.class);
		this.DETAILS = new Field("details",boolean.class,"RFC7807",RFC7807.class);
		this.INSTANCE = new Field("instance",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_TYPE = new Field("govwayType",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_STATUS = new Field("govwayStatus",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_TRANSACTION_ID = new Field("govwayTransactionId",boolean.class,"RFC7807",RFC7807.class);
		this.TYPE = new Field("type",boolean.class,"RFC7807",RFC7807.class);
		this.TYPE_FORMAT = new Field("typeFormat",java.lang.String.class,"RFC7807",RFC7807.class);
	
	}
	
	public RFC7807Model(IField father){
	
		super(father);
	
		this.USE_ACCEPT_HEADER = new ComplexField(father,"useAcceptHeader",boolean.class,"RFC7807",RFC7807.class);
		this.DETAILS = new ComplexField(father,"details",boolean.class,"RFC7807",RFC7807.class);
		this.INSTANCE = new ComplexField(father,"instance",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_TYPE = new ComplexField(father,"govwayType",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_STATUS = new ComplexField(father,"govwayStatus",boolean.class,"RFC7807",RFC7807.class);
		this.GOVWAY_TRANSACTION_ID = new ComplexField(father,"govwayTransactionId",boolean.class,"RFC7807",RFC7807.class);
		this.TYPE = new ComplexField(father,"type",boolean.class,"RFC7807",RFC7807.class);
		this.TYPE_FORMAT = new ComplexField(father,"typeFormat",java.lang.String.class,"RFC7807",RFC7807.class);
	
	}
	
	

	public IField USE_ACCEPT_HEADER = null;
	 
	public IField DETAILS = null;
	 
	public IField INSTANCE = null;
	 
	public IField GOVWAY_TYPE = null;
	 
	public IField GOVWAY_STATUS = null;
	 
	public IField GOVWAY_TRANSACTION_ID = null;
	 
	public IField TYPE = null;
	 
	public IField TYPE_FORMAT = null;
	 

	@Override
	public Class<RFC7807> getModeledClass(){
		return RFC7807.class;
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