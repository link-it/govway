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
package org.openspcoop2.core.integrazione.model;

import org.openspcoop2.core.integrazione.EsitoRichiesta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model EsitoRichiesta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoRichiestaModel extends AbstractModel<EsitoRichiesta> {

	public EsitoRichiestaModel(){
	
		super();
	
		this.IDENTIFICATIVO_MESSAGGIO = new Field("identificativo-messaggio",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
		this.STATO = new Field("stato",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
	
	}
	
	public EsitoRichiestaModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_MESSAGGIO = new ComplexField(father,"identificativo-messaggio",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
	
	}
	
	

	public IField IDENTIFICATIVO_MESSAGGIO = null;
	 
	public IField STATO = null;
	 

	@Override
	public Class<EsitoRichiesta> getModeledClass(){
		return EsitoRichiesta.class;
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