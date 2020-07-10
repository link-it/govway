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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.CredenzialiSoggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CredenzialiSoggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiSoggettoModel extends AbstractModel<CredenzialiSoggetto> {

	public CredenzialiSoggettoModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.USER = new Field("user",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.APP_ID = new Field("app-id",boolean.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CN_SUBJECT = new Field("cn-subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.ISSUER = new Field("issuer",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CN_ISSUER = new Field("cn-issuer",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CERTIFICATE = new Field("certificate",byte[].class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CERTIFICATE_STRICT_VERIFICATION = new Field("certificate-strict-verification",boolean.class,"credenziali-soggetto",CredenzialiSoggetto.class);
	
	}
	
	public CredenzialiSoggettoModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.USER = new ComplexField(father,"user",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.APP_ID = new ComplexField(father,"app-id",boolean.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CN_SUBJECT = new ComplexField(father,"cn-subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.ISSUER = new ComplexField(father,"issuer",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CN_ISSUER = new ComplexField(father,"cn-issuer",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CERTIFICATE = new ComplexField(father,"certificate",byte[].class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.CERTIFICATE_STRICT_VERIFICATION = new ComplexField(father,"certificate-strict-verification",boolean.class,"credenziali-soggetto",CredenzialiSoggetto.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField USER = null;
	 
	public IField PASSWORD = null;
	 
	public IField APP_ID = null;
	 
	public IField SUBJECT = null;
	 
	public IField CN_SUBJECT = null;
	 
	public IField ISSUER = null;
	 
	public IField CN_ISSUER = null;
	 
	public IField CERTIFICATE = null;
	 
	public IField CERTIFICATE_STRICT_VERIFICATION = null;
	 

	@Override
	public Class<CredenzialiSoggetto> getModeledClass(){
		return CredenzialiSoggetto.class;
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