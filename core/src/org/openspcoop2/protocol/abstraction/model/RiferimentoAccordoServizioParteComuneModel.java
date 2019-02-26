/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoAccordoServizioParteComune 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoAccordoServizioParteComuneModel extends AbstractModel<RiferimentoAccordoServizioParteComune> {

	public RiferimentoAccordoServizioParteComuneModel(){
	
		super();
	
		this.URI = new Field("uri",java.lang.String.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class);
		this.ID_ACCORDO = new org.openspcoop2.protocol.abstraction.model.IdentificatoreAccordoModel(new Field("id-accordo",org.openspcoop2.protocol.abstraction.IdentificatoreAccordo.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class));
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class);
	
	}
	
	public RiferimentoAccordoServizioParteComuneModel(IField father){
	
		super(father);
	
		this.URI = new ComplexField(father,"uri",java.lang.String.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class);
		this.ID_ACCORDO = new org.openspcoop2.protocol.abstraction.model.IdentificatoreAccordoModel(new ComplexField(father,"id-accordo",org.openspcoop2.protocol.abstraction.IdentificatoreAccordo.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class));
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"RiferimentoAccordoServizioParteComune",RiferimentoAccordoServizioParteComune.class);
	
	}
	
	

	public IField URI = null;
	 
	public org.openspcoop2.protocol.abstraction.model.IdentificatoreAccordoModel ID_ACCORDO = null;
	 
	public IField SERVIZIO = null;
	 

	@Override
	public Class<RiferimentoAccordoServizioParteComune> getModeledClass(){
		return RiferimentoAccordoServizioParteComune.class;
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