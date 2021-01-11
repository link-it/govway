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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Reliability;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Reliability 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReliabilityModel extends AbstractModel<Reliability> {

	public ReliabilityModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"reliability",Reliability.class);
		this.REPLY_PATTERN = new Field("replyPattern",java.lang.String.class,"reliability",Reliability.class);
		this.NON_REPUDIATION = new Field("nonRepudiation",java.lang.String.class,"reliability",Reliability.class);
	
	}
	
	public ReliabilityModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"reliability",Reliability.class);
		this.REPLY_PATTERN = new ComplexField(father,"replyPattern",java.lang.String.class,"reliability",Reliability.class);
		this.NON_REPUDIATION = new ComplexField(father,"nonRepudiation",java.lang.String.class,"reliability",Reliability.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField REPLY_PATTERN = null;
	 
	public IField NON_REPUDIATION = null;
	 

	@Override
	public Class<Reliability> getModeledClass(){
		return Reliability.class;
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