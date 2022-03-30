/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.Transazioni;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Transazioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioniModel extends AbstractModel<Transazioni> {

	public TransazioniModel(){
	
		super();
	
		this.TEMPI_ELABORAZIONE = new Field("tempi-elaborazione",java.lang.String.class,"transazioni",Transazioni.class);
		this.TOKEN = new Field("token",java.lang.String.class,"transazioni",Transazioni.class);
	
	}
	
	public TransazioniModel(IField father){
	
		super(father);
	
		this.TEMPI_ELABORAZIONE = new ComplexField(father,"tempi-elaborazione",java.lang.String.class,"transazioni",Transazioni.class);
		this.TOKEN = new ComplexField(father,"token",java.lang.String.class,"transazioni",Transazioni.class);
	
	}
	
	

	public IField TEMPI_ELABORAZIONE = null;
	 
	public IField TOKEN = null;
	 

	@Override
	public Class<Transazioni> getModeledClass(){
		return Transazioni.class;
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