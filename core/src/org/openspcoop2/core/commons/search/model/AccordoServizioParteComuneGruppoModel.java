/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComuneGruppo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneGruppoModel extends AbstractModel<AccordoServizioParteComuneGruppo> {

	public AccordoServizioParteComuneGruppoModel(){
	
		super();
	
		this.ID_GRUPPO = new org.openspcoop2.core.commons.search.model.IdGruppoModel(new Field("id-gruppo",org.openspcoop2.core.commons.search.IdGruppo.class,"accordo-servizio-parte-comune-gruppo",AccordoServizioParteComuneGruppo.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new Field("id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"accordo-servizio-parte-comune-gruppo",AccordoServizioParteComuneGruppo.class));
	
	}
	
	public AccordoServizioParteComuneGruppoModel(IField father){
	
		super(father);
	
		this.ID_GRUPPO = new org.openspcoop2.core.commons.search.model.IdGruppoModel(new ComplexField(father,"id-gruppo",org.openspcoop2.core.commons.search.IdGruppo.class,"accordo-servizio-parte-comune-gruppo",AccordoServizioParteComuneGruppo.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new ComplexField(father,"id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"accordo-servizio-parte-comune-gruppo",AccordoServizioParteComuneGruppo.class));
	
	}
	
	

	public org.openspcoop2.core.commons.search.model.IdGruppoModel ID_GRUPPO = null;
	 
	public org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel ID_ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 

	@Override
	public Class<AccordoServizioParteComuneGruppo> getModeledClass(){
		return AccordoServizioParteComuneGruppo.class;
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