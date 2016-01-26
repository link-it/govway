/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ServizioAzione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioAzione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioAzioneModel extends AbstractModel<ServizioAzione> {

	public ServizioAzioneModel(){
	
		super();
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.registry.Connettore.class,"servizio-azione",ServizioAzione.class));
		this.PARAMETRI_FRUITORE = new org.openspcoop2.core.registry.model.ServizioAzioneFruitoreModel(new Field("parametri-fruitore",org.openspcoop2.core.registry.ServizioAzioneFruitore.class,"servizio-azione",ServizioAzione.class));
		this.NOME = new Field("nome",java.lang.String.class,"servizio-azione",ServizioAzione.class);
	
	}
	
	public ServizioAzioneModel(IField father){
	
		super(father);
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.registry.Connettore.class,"servizio-azione",ServizioAzione.class));
		this.PARAMETRI_FRUITORE = new org.openspcoop2.core.registry.model.ServizioAzioneFruitoreModel(new ComplexField(father,"parametri-fruitore",org.openspcoop2.core.registry.ServizioAzioneFruitore.class,"servizio-azione",ServizioAzione.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizio-azione",ServizioAzione.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ConnettoreModel CONNETTORE = null;
	 
	public org.openspcoop2.core.registry.model.ServizioAzioneFruitoreModel PARAMETRI_FRUITORE = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<ServizioAzione> getModeledClass(){
		return ServizioAzione.class;
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