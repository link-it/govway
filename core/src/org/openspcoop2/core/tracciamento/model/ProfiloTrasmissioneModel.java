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

import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProfiloTrasmissione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProfiloTrasmissioneModel extends AbstractModel<ProfiloTrasmissione> {

	public ProfiloTrasmissioneModel(){
	
		super();
	
		this.INOLTRO = new org.openspcoop2.core.tracciamento.model.InoltroModel(new Field("inoltro",org.openspcoop2.core.tracciamento.Inoltro.class,"profilo-trasmissione",ProfiloTrasmissione.class));
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",boolean.class,"profilo-trasmissione",ProfiloTrasmissione.class);
		this.SEQUENZA = new Field("sequenza",java.lang.Integer.class,"profilo-trasmissione",ProfiloTrasmissione.class);
	
	}
	
	public ProfiloTrasmissioneModel(IField father){
	
		super(father);
	
		this.INOLTRO = new org.openspcoop2.core.tracciamento.model.InoltroModel(new ComplexField(father,"inoltro",org.openspcoop2.core.tracciamento.Inoltro.class,"profilo-trasmissione",ProfiloTrasmissione.class));
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",boolean.class,"profilo-trasmissione",ProfiloTrasmissione.class);
		this.SEQUENZA = new ComplexField(father,"sequenza",java.lang.Integer.class,"profilo-trasmissione",ProfiloTrasmissione.class);
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.InoltroModel INOLTRO = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField SEQUENZA = null;
	 

	@Override
	public Class<ProfiloTrasmissione> getModeledClass(){
		return ProfiloTrasmissione.class;
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