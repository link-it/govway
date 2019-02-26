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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Agreement;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Agreement 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AgreementModel extends AbstractModel<Agreement> {

	public AgreementModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"agreement",Agreement.class);
		this.VALUE = new Field("value",java.lang.String.class,"agreement",Agreement.class);
		this.TYPE = new Field("type",java.lang.String.class,"agreement",Agreement.class);
	
	}
	
	public AgreementModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"agreement",Agreement.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"agreement",Agreement.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"agreement",Agreement.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 
	public IField TYPE = null;
	 

	@Override
	public Class<Agreement> getModeledClass(){
		return Agreement.class;
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