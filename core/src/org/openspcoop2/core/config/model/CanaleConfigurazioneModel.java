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

import org.openspcoop2.core.config.CanaleConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CanaleConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CanaleConfigurazioneModel extends AbstractModel<CanaleConfigurazione> {

	public CanaleConfigurazioneModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"canale-configurazione",CanaleConfigurazione.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"canale-configurazione",CanaleConfigurazione.class);
		this.CANALE_DEFAULT = new Field("canale-default",boolean.class,"canale-configurazione",CanaleConfigurazione.class);
	
	}
	
	public CanaleConfigurazioneModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"canale-configurazione",CanaleConfigurazione.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"canale-configurazione",CanaleConfigurazione.class);
		this.CANALE_DEFAULT = new ComplexField(father,"canale-default",boolean.class,"canale-configurazione",CanaleConfigurazione.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField CANALE_DEFAULT = null;
	 

	@Override
	public Class<CanaleConfigurazione> getModeledClass(){
		return CanaleConfigurazione.class;
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