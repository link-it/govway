/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Soggetti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Soggetti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiModel extends AbstractModel<Soggetti> {

	public SoggettiModel(){
	
		super();
	
		this.TIPI = new org.openspcoop2.protocol.manifest.model.TipiModel(new Field("tipi",org.openspcoop2.protocol.manifest.Tipi.class,"soggetti",Soggetti.class));
		this.CODICE_IPA = new Field("codiceIPA",boolean.class,"soggetti",Soggetti.class);
		this.INDIRIZZO_RISPOSTA = new Field("indirizzoRisposta",boolean.class,"soggetti",Soggetti.class);
	
	}
	
	public SoggettiModel(IField father){
	
		super(father);
	
		this.TIPI = new org.openspcoop2.protocol.manifest.model.TipiModel(new ComplexField(father,"tipi",org.openspcoop2.protocol.manifest.Tipi.class,"soggetti",Soggetti.class));
		this.CODICE_IPA = new ComplexField(father,"codiceIPA",boolean.class,"soggetti",Soggetti.class);
		this.INDIRIZZO_RISPOSTA = new ComplexField(father,"indirizzoRisposta",boolean.class,"soggetti",Soggetti.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.TipiModel TIPI = null;
	 
	public IField CODICE_IPA = null;
	 
	public IField INDIRIZZO_RISPOSTA = null;
	 

	@Override
	public Class<Soggetti> getModeledClass(){
		return Soggetti.class;
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