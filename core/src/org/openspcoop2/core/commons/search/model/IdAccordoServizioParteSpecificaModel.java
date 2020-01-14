/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.search.IdAccordoServizioParteSpecifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdAccordoServizioParteSpecifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdAccordoServizioParteSpecificaModel extends AbstractModel<IdAccordoServizioParteSpecifica> {

	public IdAccordoServizioParteSpecificaModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.NOME = new Field("nome",java.lang.String.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.ID_EROGATORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-erogatore",org.openspcoop2.core.commons.search.IdSoggetto.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class));
	
	}
	
	public IdAccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class);
		this.ID_EROGATORE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-erogatore",org.openspcoop2.core.commons.search.IdSoggetto.class,"id-accordo-servizio-parte-specifica",IdAccordoServizioParteSpecifica.class));
	
	}
	
	

	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_EROGATORE = null;
	 

	@Override
	public Class<IdAccordoServizioParteSpecifica> getModeledClass(){
		return IdAccordoServizioParteSpecifica.class;
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