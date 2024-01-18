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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Default;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Default 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DefaultModel extends AbstractModel<Default> {

	public DefaultModel(){
	
		super();
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaDefaultModel(new Field("proprieta",org.openspcoop2.protocol.information_missing.ProprietaDefault.class,"Default",Default.class));
		this.VALORE = new Field("valore",java.lang.String.class,"Default",Default.class);
	
	}
	
	public DefaultModel(IField father){
	
		super(father);
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaDefaultModel(new ComplexField(father,"proprieta",org.openspcoop2.protocol.information_missing.ProprietaDefault.class,"Default",Default.class));
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"Default",Default.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ProprietaDefaultModel PROPRIETA = null;
	 
	public IField VALORE = null;
	 

	@Override
	public Class<Default> getModeledClass(){
		return Default.class;
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