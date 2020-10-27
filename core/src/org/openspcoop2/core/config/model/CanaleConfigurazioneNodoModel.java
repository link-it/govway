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

import org.openspcoop2.core.config.CanaleConfigurazioneNodo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CanaleConfigurazioneNodo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CanaleConfigurazioneNodoModel extends AbstractModel<CanaleConfigurazioneNodo> {

	public CanaleConfigurazioneNodoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
		this.CANALE = new Field("canale",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
	
	}
	
	public CanaleConfigurazioneNodoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
		this.CANALE = new ComplexField(father,"canale",java.lang.String.class,"canale-configurazione-nodo",CanaleConfigurazioneNodo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField CANALE = null;
	 

	@Override
	public Class<CanaleConfigurazioneNodo> getModeledClass(){
		return CanaleConfigurazioneNodo.class;
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