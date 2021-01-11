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
package org.openspcoop2.core.statistiche.model;

import org.openspcoop2.core.statistiche.StatisticaSettimanale;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatisticaSettimanale 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaSettimanaleModel extends AbstractModel<StatisticaSettimanale> {

	public StatisticaSettimanaleModel(){
	
		super();
	
		this.STATISTICA_BASE = new org.openspcoop2.core.statistiche.model.StatisticaModel(new Field("statistica-base",org.openspcoop2.core.statistiche.Statistica.class,"statistica-settimanale",StatisticaSettimanale.class));
		this.STATISTICA_SETTIMANALE_CONTENUTI = new org.openspcoop2.core.statistiche.model.StatisticaContenutiModel(new Field("statistica-settimanale-contenuti",org.openspcoop2.core.statistiche.StatisticaContenuti.class,"statistica-settimanale",StatisticaSettimanale.class));
	
	}
	
	public StatisticaSettimanaleModel(IField father){
	
		super(father);
	
		this.STATISTICA_BASE = new org.openspcoop2.core.statistiche.model.StatisticaModel(new ComplexField(father,"statistica-base",org.openspcoop2.core.statistiche.Statistica.class,"statistica-settimanale",StatisticaSettimanale.class));
		this.STATISTICA_SETTIMANALE_CONTENUTI = new org.openspcoop2.core.statistiche.model.StatisticaContenutiModel(new ComplexField(father,"statistica-settimanale-contenuti",org.openspcoop2.core.statistiche.StatisticaContenuti.class,"statistica-settimanale",StatisticaSettimanale.class));
	
	}
	
	

	public org.openspcoop2.core.statistiche.model.StatisticaModel STATISTICA_BASE = null;
	 
	public org.openspcoop2.core.statistiche.model.StatisticaContenutiModel STATISTICA_SETTIMANALE_CONTENUTI = null;
	 

	@Override
	public Class<StatisticaSettimanale> getModeledClass(){
		return StatisticaSettimanale.class;
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