/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor.model;

import org.openspcoop2.pdd.monitor.Filtro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Filtro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroModel extends AbstractModel<Filtro> {

	public FiltroModel(){
	
		super();
	
		this.CORRELAZIONE_APPLICATIVA = new Field("correlazione-applicativa",java.lang.String.class,"filtro",Filtro.class);
		this.BUSTA = new org.openspcoop2.pdd.monitor.model.BustaModel(new Field("busta",org.openspcoop2.pdd.monitor.Busta.class,"filtro",Filtro.class));
		this.ID_MESSAGGIO = new Field("id-messaggio",java.lang.String.class,"filtro",Filtro.class);
		this.MESSAGE_PATTERN = new Field("message-pattern",java.lang.String.class,"filtro",Filtro.class);
		this.SOGLIA = new Field("soglia",long.class,"filtro",Filtro.class);
		this.STATO = new Field("stato",java.lang.String.class,"filtro",Filtro.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"filtro",Filtro.class);
		this.SOGGETTO = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new Field("soggetto",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"filtro",Filtro.class));
		this.PROPRIETA = new org.openspcoop2.pdd.monitor.model.ProprietaModel(new Field("proprieta",org.openspcoop2.pdd.monitor.Proprieta.class,"filtro",Filtro.class));
	
	}
	
	public FiltroModel(IField father){
	
		super(father);
	
		this.CORRELAZIONE_APPLICATIVA = new ComplexField(father,"correlazione-applicativa",java.lang.String.class,"filtro",Filtro.class);
		this.BUSTA = new org.openspcoop2.pdd.monitor.model.BustaModel(new ComplexField(father,"busta",org.openspcoop2.pdd.monitor.Busta.class,"filtro",Filtro.class));
		this.ID_MESSAGGIO = new ComplexField(father,"id-messaggio",java.lang.String.class,"filtro",Filtro.class);
		this.MESSAGE_PATTERN = new ComplexField(father,"message-pattern",java.lang.String.class,"filtro",Filtro.class);
		this.SOGLIA = new ComplexField(father,"soglia",long.class,"filtro",Filtro.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"filtro",Filtro.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"filtro",Filtro.class);
		this.SOGGETTO = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"filtro",Filtro.class));
		this.PROPRIETA = new org.openspcoop2.pdd.monitor.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.pdd.monitor.Proprieta.class,"filtro",Filtro.class));
	
	}
	
	

	public IField CORRELAZIONE_APPLICATIVA = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaModel BUSTA = null;
	 
	public IField ID_MESSAGGIO = null;
	 
	public IField MESSAGE_PATTERN = null;
	 
	public IField SOGLIA = null;
	 
	public IField STATO = null;
	 
	public IField TIPO = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaSoggettoModel SOGGETTO = null;
	 
	public org.openspcoop2.pdd.monitor.model.ProprietaModel PROPRIETA = null;
	 

	@Override
	public Class<Filtro> getModeledClass(){
		return Filtro.class;
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