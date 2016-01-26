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
package org.openspcoop2.generic_project.expression.impl.test.model;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.impl.test.beans.Fruitore;
import org.openspcoop2.generic_project.expression.impl.test.beans.IdAccordoServizioParteSpecifica;
import org.openspcoop2.generic_project.expression.impl.test.beans.IdSoggetto;


/**
 * FruitoreModel
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruitoreModel extends AbstractModel<Fruitore> {

	public FruitoreModel(){
	
		super();
		
		this.ID_FRUITORE = new IdSoggettoModel(new Field("id-fruitore",IdSoggetto.class,"fruitore",Fruitore.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = new IdAccordoServizioParteSpecificaModel(new Field("id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class,"fruitore",Fruitore.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
	
	}
	
	public FruitoreModel(IField father){
	
		super(father);
	
		this.ID_FRUITORE = new IdSoggettoModel(new ComplexField(father,"id-fruitore",IdSoggetto.class,"fruitore",Fruitore.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = new IdAccordoServizioParteSpecificaModel(new ComplexField(father,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class,"fruitore",Fruitore.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
	
	}
	
	

	public IdSoggettoModel ID_FRUITORE = null;
	 
	public IdAccordoServizioParteSpecificaModel ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 


	@Override
	public Class<Fruitore> getModeledClass(){
		return Fruitore.class;
	}

}