/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.messaggi.v1_0.model;

import it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErroreType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroreTypeModel extends AbstractModel<ErroreType> {

	public ErroreTypeModel(){
	
		super();
	
		this.CODICE = new Field("Codice",java.lang.String.class,"Errore_Type",ErroreType.class);
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"Errore_Type",ErroreType.class);
	
	}
	
	public ErroreTypeModel(IField father){
	
		super(father);
	
		this.CODICE = new ComplexField(father,"Codice",java.lang.String.class,"Errore_Type",ErroreType.class);
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"Errore_Type",ErroreType.class);
	
	}
	
	

	public IField CODICE = null;
	 
	public IField DESCRIZIONE = null;
	 

	@Override
	public Class<ErroreType> getModeledClass(){
		return ErroreType.class;
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