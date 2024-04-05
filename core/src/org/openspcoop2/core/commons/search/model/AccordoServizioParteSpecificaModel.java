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

import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteSpecifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecificaModel extends AbstractModel<AccordoServizioParteSpecifica> {

	public AccordoServizioParteSpecificaModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME = new Field("nome",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PORT_TYPE = new Field("port-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_EROGATORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-erogatore",org.openspcoop2.core.commons.search.IdSoggetto.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new Field("id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
	
	}
	
	public AccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PORT_TYPE = new ComplexField(father,"port-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_EROGATORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-erogatore",org.openspcoop2.core.commons.search.IdSoggetto.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new ComplexField(father,"id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
	
	}
	
	

	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public IField PORT_TYPE = null;
	 
	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_EROGATORE = null;
	 
	public org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel ID_ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 

	@Override
	public Class<AccordoServizioParteSpecifica> getModeledClass(){
		return AccordoServizioParteSpecifica.class;
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