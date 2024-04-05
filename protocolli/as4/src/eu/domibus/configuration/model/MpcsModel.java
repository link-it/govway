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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Mpcs;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Mpcs 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MpcsModel extends AbstractModel<Mpcs> {

	public MpcsModel(){
	
		super();
	
		this.MPC = new eu.domibus.configuration.model.MpcModel(new Field("mpc",eu.domibus.configuration.Mpc.class,"mpcs",Mpcs.class));
	
	}
	
	public MpcsModel(IField father){
	
		super(father);
	
		this.MPC = new eu.domibus.configuration.model.MpcModel(new ComplexField(father,"mpc",eu.domibus.configuration.Mpc.class,"mpcs",Mpcs.class));
	
	}
	
	

	public eu.domibus.configuration.model.MpcModel MPC = null;
	 

	@Override
	public Class<Mpcs> getModeledClass(){
		return Mpcs.class;
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