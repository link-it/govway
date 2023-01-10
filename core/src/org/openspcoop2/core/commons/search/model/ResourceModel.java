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

import org.openspcoop2.core.commons.search.Resource;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Resource 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceModel extends AbstractModel<Resource> {

	public ResourceModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"resource",Resource.class);
		this.HTTP_METHOD = new Field("http-method",java.lang.String.class,"resource",Resource.class);
		this.PATH = new Field("path",java.lang.String.class,"resource",Resource.class);
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new Field("id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"resource",Resource.class));
	
	}
	
	public ResourceModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"resource",Resource.class);
		this.HTTP_METHOD = new ComplexField(father,"http-method",java.lang.String.class,"resource",Resource.class);
		this.PATH = new ComplexField(father,"path",java.lang.String.class,"resource",Resource.class);
		this.ID_ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel(new ComplexField(father,"id-accordo-servizio-parte-comune",org.openspcoop2.core.commons.search.IdAccordoServizioParteComune.class,"resource",Resource.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField HTTP_METHOD = null;
	 
	public IField PATH = null;
	 
	public org.openspcoop2.core.commons.search.model.IdAccordoServizioParteComuneModel ID_ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 

	@Override
	public Class<Resource> getModeledClass(){
		return Resource.class;
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