/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.core.statistiche.StatisticaInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatisticaInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaInfoModel extends AbstractModel<StatisticaInfo> {

	public StatisticaInfoModel(){
	
		super();
	
		this.TIPO_STATISTICA = new Field("tipo-statistica",java.lang.String.class,"statistica-info",StatisticaInfo.class);
		this.DATA_ULTIMA_GENERAZIONE = new Field("data-ultima-generazione",java.util.Date.class,"statistica-info",StatisticaInfo.class);
	
	}
	
	public StatisticaInfoModel(IField father){
	
		super(father);
	
		this.TIPO_STATISTICA = new ComplexField(father,"tipo-statistica",java.lang.String.class,"statistica-info",StatisticaInfo.class);
		this.DATA_ULTIMA_GENERAZIONE = new ComplexField(father,"data-ultima-generazione",java.util.Date.class,"statistica-info",StatisticaInfo.class);
	
	}
	
	

	public IField TIPO_STATISTICA = null;
	 
	public IField DATA_ULTIMA_GENERAZIONE = null;
	 

	@Override
	public Class<StatisticaInfo> getModeledClass(){
		return StatisticaInfo.class;
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