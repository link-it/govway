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
package org.openspcoop2.generic_project.web.service;

import java.lang.reflect.ParameterizedType;

import org.openspcoop2.generic_project.web.business.BaseBD;
import org.openspcoop2.generic_project.web.form.SearchForm;


/**
*
* BaseServiceWithBD Classe per la definizione dei metodi per interfacciarsi il livello Business Delegate.
* Il parametro S di tipo BaseSearchForm e' un filtro di ricerca che viene popolato dall'interfaccia grafica.
* Il parametro BD di tipo {@link BaseBD} e' il tipo generico della Business Delegate da utilizzare.
* 
* @param <BD> tipo della Business Delegate.
* @param <S> tipo del form di ricerca.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class BaseServiceWithBD<S extends SearchForm, BD extends BaseBD<?, ?>> { 

	protected BD business;
	
	@SuppressWarnings("unchecked")
	public BaseServiceWithBD() throws Exception{
		try{
			ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
			this.business = ((Class<BD>)parameterizedType.getActualTypeArguments()[0]).newInstance();
		}catch (Exception e) {
			 throw e;
		}
	}

	protected S search;
	
	public void setSearch(S search) {
		this.search = search;
		
	}

	public S getSearch() {
		return this.search;
	}

	public BD getBusiness() {
		return this.business;
	}

	public void setBusiness(BD business) {
		this.business = business;
	}
}
