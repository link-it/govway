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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.TemporalConditionType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TemporalConditionType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemporalConditionTypeModel extends AbstractModel<TemporalConditionType> {

	public TemporalConditionTypeModel(){
	
		super();
	
		this.PREDICATE = new Field("predicate",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.BOOLOP = new Field("boolop",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.DATA = new Field("data",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.DESCRIPTION = new Field("description",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
	
	}
	
	public TemporalConditionTypeModel(IField father){
	
		super(father);
	
		this.PREDICATE = new ComplexField(father,"predicate",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.BOOLOP = new ComplexField(father,"boolop",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.DATA = new ComplexField(father,"data",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
		this.DESCRIPTION = new ComplexField(father,"description",java.lang.String.class,"temporalConditionType",TemporalConditionType.class);
	
	}
	
	

	public IField PREDICATE = null;
	 
	public IField BOOLOP = null;
	 
	public IField DATA = null;
	 
	public IField DESCRIPTION = null;
	 

	@Override
	public Class<TemporalConditionType> getModeledClass(){
		return TemporalConditionType.class;
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