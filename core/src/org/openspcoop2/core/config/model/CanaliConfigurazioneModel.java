/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.CanaliConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CanaliConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CanaliConfigurazioneModel extends AbstractModel<CanaliConfigurazione> {

	public CanaliConfigurazioneModel(){
	
		super();
	
		this.CANALE = new org.openspcoop2.core.config.model.CanaleConfigurazioneModel(new Field("canale",org.openspcoop2.core.config.CanaleConfigurazione.class,"canali-configurazione",CanaliConfigurazione.class));
		this.NODO = new org.openspcoop2.core.config.model.CanaleConfigurazioneNodoModel(new Field("nodo",org.openspcoop2.core.config.CanaleConfigurazioneNodo.class,"canali-configurazione",CanaliConfigurazione.class));
		this.STATO = new Field("stato",java.lang.String.class,"canali-configurazione",CanaliConfigurazione.class);
	
	}
	
	public CanaliConfigurazioneModel(IField father){
	
		super(father);
	
		this.CANALE = new org.openspcoop2.core.config.model.CanaleConfigurazioneModel(new ComplexField(father,"canale",org.openspcoop2.core.config.CanaleConfigurazione.class,"canali-configurazione",CanaliConfigurazione.class));
		this.NODO = new org.openspcoop2.core.config.model.CanaleConfigurazioneNodoModel(new ComplexField(father,"nodo",org.openspcoop2.core.config.CanaleConfigurazioneNodo.class,"canali-configurazione",CanaliConfigurazione.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"canali-configurazione",CanaliConfigurazione.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.CanaleConfigurazioneModel CANALE = null;
	 
	public org.openspcoop2.core.config.model.CanaleConfigurazioneNodoModel NODO = null;
	 
	public IField STATO = null;
	 

	@Override
	public Class<CanaliConfigurazione> getModeledClass(){
		return CanaliConfigurazione.class;
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