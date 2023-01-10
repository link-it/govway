/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Trasmissioni;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Trasmissioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasmissioniModel extends AbstractModel<Trasmissioni> {

	public TrasmissioniModel(){
	
		super();
	
		this.TRASMISSIONE = new org.openspcoop2.core.tracciamento.model.TrasmissioneModel(new Field("trasmissione",org.openspcoop2.core.tracciamento.Trasmissione.class,"trasmissioni",Trasmissioni.class));
	
	}
	
	public TrasmissioniModel(IField father){
	
		super(father);
	
		this.TRASMISSIONE = new org.openspcoop2.core.tracciamento.model.TrasmissioneModel(new ComplexField(father,"trasmissione",org.openspcoop2.core.tracciamento.Trasmissione.class,"trasmissioni",Trasmissioni.class));
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.TrasmissioneModel TRASMISSIONE = null;
	 

	@Override
	public Class<Trasmissioni> getModeledClass(){
		return Trasmissioni.class;
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