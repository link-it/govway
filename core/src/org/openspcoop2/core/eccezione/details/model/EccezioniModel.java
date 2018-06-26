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
package org.openspcoop2.core.eccezione.details.model;

import org.openspcoop2.core.eccezione.details.Eccezioni;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Eccezioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioniModel extends AbstractModel<Eccezioni> {

	public EccezioniModel(){
	
		super();
	
		this.EXCEPTION = new org.openspcoop2.core.eccezione.details.model.EccezioneModel(new Field("exception",org.openspcoop2.core.eccezione.details.Eccezione.class,"eccezioni",Eccezioni.class));
	
	}
	
	public EccezioniModel(IField father){
	
		super(father);
	
		this.EXCEPTION = new org.openspcoop2.core.eccezione.details.model.EccezioneModel(new ComplexField(father,"exception",org.openspcoop2.core.eccezione.details.Eccezione.class,"eccezioni",Eccezioni.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.details.model.EccezioneModel EXCEPTION = null;
	 

	@Override
	public Class<Eccezioni> getModeledClass(){
		return Eccezioni.class;
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