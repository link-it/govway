/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
	
		this.MESSAGE_ID = new Field("messageId",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
		this.STATE = new Field("state",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
	
	}
	
	public EsitoRichiestaModel(IField father){
	
		super(father);
	
		this.MESSAGE_ID = new ComplexField(father,"messageId",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
		this.STATE = new ComplexField(father,"state",java.lang.String.class,"esito-richiesta",EsitoRichiesta.class);
	
	}
	
	

	public IField MESSAGE_ID = null;
	 
	public IField STATE = null;
	 

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