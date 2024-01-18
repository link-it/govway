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

import org.openspcoop2.core.config.ConfigurazioneMultitenant;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneMultitenant 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneMultitenantModel extends AbstractModel<ConfigurazioneMultitenant> {

	public ConfigurazioneMultitenantModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
		this.FRUIZIONE_SCELTA_SOGGETTI_EROGATORI = new Field("fruizioneSceltaSoggettiErogatori",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
		this.EROGAZIONE_SCELTA_SOGGETTI_FRUITORI = new Field("erogazioneSceltaSoggettiFruitori",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
	
	}
	
	public ConfigurazioneMultitenantModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
		this.FRUIZIONE_SCELTA_SOGGETTI_EROGATORI = new ComplexField(father,"fruizioneSceltaSoggettiErogatori",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
		this.EROGAZIONE_SCELTA_SOGGETTI_FRUITORI = new ComplexField(father,"erogazioneSceltaSoggettiFruitori",java.lang.String.class,"configurazione-multitenant",ConfigurazioneMultitenant.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField FRUIZIONE_SCELTA_SOGGETTI_EROGATORI = null;
	 
	public IField EROGAZIONE_SCELTA_SOGGETTI_FRUITORI = null;
	 

	@Override
	public Class<ConfigurazioneMultitenant> getModeledClass(){
		return ConfigurazioneMultitenant.class;
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