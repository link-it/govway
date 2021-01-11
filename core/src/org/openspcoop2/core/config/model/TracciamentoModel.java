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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.Tracciamento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Tracciamento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoModel extends AbstractModel<Tracciamento> {

	public TracciamentoModel(){
	
		super();
	
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new Field("openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"tracciamento",Tracciamento.class));
		this.OPENSPCOOP_SORGENTE_DATI = new org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel(new Field("openspcoop-sorgente-dati",org.openspcoop2.core.config.OpenspcoopSorgenteDati.class,"tracciamento",Tracciamento.class));
		this.STATO = new Field("stato",java.lang.String.class,"tracciamento",Tracciamento.class);
		this.ESITI = new Field("esiti",java.lang.String.class,"tracciamento",Tracciamento.class);
	
	}
	
	public TracciamentoModel(IField father){
	
		super(father);
	
		this.OPENSPCOOP_APPENDER = new org.openspcoop2.core.config.model.OpenspcoopAppenderModel(new ComplexField(father,"openspcoop-appender",org.openspcoop2.core.config.OpenspcoopAppender.class,"tracciamento",Tracciamento.class));
		this.OPENSPCOOP_SORGENTE_DATI = new org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel(new ComplexField(father,"openspcoop-sorgente-dati",org.openspcoop2.core.config.OpenspcoopSorgenteDati.class,"tracciamento",Tracciamento.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"tracciamento",Tracciamento.class);
		this.ESITI = new ComplexField(father,"esiti",java.lang.String.class,"tracciamento",Tracciamento.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.OpenspcoopAppenderModel OPENSPCOOP_APPENDER = null;
	 
	public org.openspcoop2.core.config.model.OpenspcoopSorgenteDatiModel OPENSPCOOP_SORGENTE_DATI = null;
	 
	public IField STATO = null;
	 
	public IField ESITI = null;
	 

	@Override
	public Class<Tracciamento> getModeledClass(){
		return Tracciamento.class;
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