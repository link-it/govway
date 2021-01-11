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

import eu.domibus.configuration.Mep;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Mep 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MepModel extends AbstractModel<Mep> {

	public MepModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"mep",Mep.class);
		this.VALUE = new Field("value",java.net.URI.class,"mep",Mep.class);
		this.LEGS = new Field("legs",java.math.BigInteger.class,"mep",Mep.class);
	
	}
	
	public MepModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"mep",Mep.class);
		this.VALUE = new ComplexField(father,"value",java.net.URI.class,"mep",Mep.class);
		this.LEGS = new ComplexField(father,"legs",java.math.BigInteger.class,"mep",Mep.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 
	public IField LEGS = null;
	 

	@Override
	public Class<Mep> getModeledClass(){
		return Mep.class;
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