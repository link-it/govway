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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.TracciamentoConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TracciamentoConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoConfigurazioneModel extends AbstractModel<TracciamentoConfigurazione> {

	public TracciamentoConfigurazioneModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.FILTRO_ESITI = new Field("filtro-esiti",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.REQUEST_IN = new Field("request-in",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.REQUEST_OUT = new Field("request-out",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.RESPONSE_OUT = new Field("response-out",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.RESPONSE_OUT_COMPLETE = new Field("response-out-complete",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
	
	}
	
	public TracciamentoConfigurazioneModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.FILTRO_ESITI = new ComplexField(father,"filtro-esiti",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.REQUEST_IN = new ComplexField(father,"request-in",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.REQUEST_OUT = new ComplexField(father,"request-out",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.RESPONSE_OUT = new ComplexField(father,"response-out",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
		this.RESPONSE_OUT_COMPLETE = new ComplexField(father,"response-out-complete",java.lang.String.class,"tracciamento-configurazione",TracciamentoConfigurazione.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField FILTRO_ESITI = null;
	 
	public IField REQUEST_IN = null;
	 
	public IField REQUEST_OUT = null;
	 
	public IField RESPONSE_OUT = null;
	 
	public IField RESPONSE_OUT_COMPLETE = null;
	 

	@Override
	public Class<TracciamentoConfigurazione> getModeledClass(){
		return TracciamentoConfigurazione.class;
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