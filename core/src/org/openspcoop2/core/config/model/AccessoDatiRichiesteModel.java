/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.AccessoDatiRichieste;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccessoDatiRichieste 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccessoDatiRichiesteModel extends AbstractModel<AccessoDatiRichieste> {

	public AccessoDatiRichiesteModel(){
	
		super();
	
		this.CACHE = new org.openspcoop2.core.config.model.CacheModel(new Field("cache",org.openspcoop2.core.config.Cache.class,"accesso-dati-richieste",AccessoDatiRichieste.class));
	
	}
	
	public AccessoDatiRichiesteModel(IField father){
	
		super(father);
	
		this.CACHE = new org.openspcoop2.core.config.model.CacheModel(new ComplexField(father,"cache",org.openspcoop2.core.config.Cache.class,"accesso-dati-richieste",AccessoDatiRichieste.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.CacheModel CACHE = null;
	 

	@Override
	public Class<AccessoDatiRichieste> getModeledClass(){
		return AccessoDatiRichieste.class;
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