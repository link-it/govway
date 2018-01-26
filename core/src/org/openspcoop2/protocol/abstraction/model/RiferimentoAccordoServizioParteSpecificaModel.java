/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoAccordoServizioParteSpecifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoAccordoServizioParteSpecificaModel extends AbstractModel<RiferimentoAccordoServizioParteSpecifica> {

	public RiferimentoAccordoServizioParteSpecificaModel(){
	
		super();
	
		this.URI = new Field("uri",java.lang.String.class,"RiferimentoAccordoServizioParteSpecifica",RiferimentoAccordoServizioParteSpecifica.class);
		this.ID_SERVIZIO = new org.openspcoop2.protocol.abstraction.model.IdentificatoreServizioModel(new Field("id-servizio",org.openspcoop2.protocol.abstraction.IdentificatoreServizio.class,"RiferimentoAccordoServizioParteSpecifica",RiferimentoAccordoServizioParteSpecifica.class));
	
	}
	
	public RiferimentoAccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.URI = new ComplexField(father,"uri",java.lang.String.class,"RiferimentoAccordoServizioParteSpecifica",RiferimentoAccordoServizioParteSpecifica.class);
		this.ID_SERVIZIO = new org.openspcoop2.protocol.abstraction.model.IdentificatoreServizioModel(new ComplexField(father,"id-servizio",org.openspcoop2.protocol.abstraction.IdentificatoreServizio.class,"RiferimentoAccordoServizioParteSpecifica",RiferimentoAccordoServizioParteSpecifica.class));
	
	}
	
	

	public IField URI = null;
	 
	public org.openspcoop2.protocol.abstraction.model.IdentificatoreServizioModel ID_SERVIZIO = null;
	 

	@Override
	public Class<RiferimentoAccordoServizioParteSpecifica> getModeledClass(){
		return RiferimentoAccordoServizioParteSpecifica.class;
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