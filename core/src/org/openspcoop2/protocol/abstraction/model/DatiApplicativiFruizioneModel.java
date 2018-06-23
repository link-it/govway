/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiApplicativiFruizione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiApplicativiFruizioneModel extends AbstractModel<DatiApplicativiFruizione> {

	public DatiApplicativiFruizioneModel(){
	
		super();
	
		this.BASIC_USERNAME = new Field("basic-username",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.BASIC_PASSWORD = new Field("basic-password",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.SSL_SUBJECT = new Field("ssl-subject",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.AUTENTICAZIONE = new Field("autenticazione",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
	
	}
	
	public DatiApplicativiFruizioneModel(IField father){
	
		super(father);
	
		this.BASIC_USERNAME = new ComplexField(father,"basic-username",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.BASIC_PASSWORD = new ComplexField(father,"basic-password",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.SSL_SUBJECT = new ComplexField(father,"ssl-subject",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
		this.AUTENTICAZIONE = new ComplexField(father,"autenticazione",java.lang.String.class,"DatiApplicativiFruizione",DatiApplicativiFruizione.class);
	
	}
	
	

	public IField BASIC_USERNAME = null;
	 
	public IField BASIC_PASSWORD = null;
	 
	public IField SSL_SUBJECT = null;
	 
	public IField AUTENTICAZIONE = null;
	 

	@Override
	public Class<DatiApplicativiFruizione> getModeledClass(){
		return DatiApplicativiFruizione.class;
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