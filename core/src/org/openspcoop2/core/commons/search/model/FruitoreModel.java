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
package org.openspcoop2.core.commons.search.model;

import org.openspcoop2.core.commons.search.Fruitore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Fruitore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruitoreModel extends AbstractModel<Fruitore> {

	public FruitoreModel(){
	
		super();
	
		this.ID_FRUITORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-fruitore",org.openspcoop2.core.commons.search.IdSoggetto.class,"fruitore",Fruitore.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteSpecificaModel(new Field("id-accordo-servizio-parte-specifica",org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica.class,"fruitore",Fruitore.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
	
	}
	
	public FruitoreModel(IField father){
	
		super(father);
	
		this.ID_FRUITORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-fruitore",org.openspcoop2.core.commons.search.IdSoggetto.class,"fruitore",Fruitore.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteSpecificaModel(new ComplexField(father,"id-accordo-servizio-parte-specifica",org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica.class,"fruitore",Fruitore.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
	
	}
	
	

	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_FRUITORE = null;
	 
	public org.openspcoop2.core.commons.search.model.IdAccordoServizioParteSpecificaModel ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<Fruitore> getModeledClass(){
		return Fruitore.class;
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