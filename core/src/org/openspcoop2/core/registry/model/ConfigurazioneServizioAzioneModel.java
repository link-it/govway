/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneServizioAzione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneServizioAzioneModel extends AbstractModel<ConfigurazioneServizioAzione> {

	public ConfigurazioneServizioAzioneModel(){
	
		super();
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.registry.Connettore.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class));
		this.CONFIGURAZIONE_FRUITORE = new org.openspcoop2.core.registry.model.ConfigurazioneServizioAzioneFruitoreModel(new Field("configurazione-fruitore",org.openspcoop2.core.registry.ConfigurazioneServizioAzioneFruitore.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class));
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class);
	
	}
	
	public ConfigurazioneServizioAzioneModel(IField father){
	
		super(father);
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.registry.Connettore.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class));
		this.CONFIGURAZIONE_FRUITORE = new org.openspcoop2.core.registry.model.ConfigurazioneServizioAzioneFruitoreModel(new ComplexField(father,"configurazione-fruitore",org.openspcoop2.core.registry.ConfigurazioneServizioAzioneFruitore.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-servizio-azione",ConfigurazioneServizioAzione.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ConnettoreModel CONNETTORE = null;
	 
	public org.openspcoop2.core.registry.model.ConfigurazioneServizioAzioneFruitoreModel CONFIGURAZIONE_FRUITORE = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<ConfigurazioneServizioAzione> getModeledClass(){
		return ConfigurazioneServizioAzione.class;
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