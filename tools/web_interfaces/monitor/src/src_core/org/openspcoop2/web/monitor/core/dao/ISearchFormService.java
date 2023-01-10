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
package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.web.monitor.core.bean.AbstractCoreSearchForm;


/**
 * IBaseService definisce un interfaccia tra il livello di accesso ai dati e i Bean della pagina.
 * form: Bean del form (ricerca/CRUD) condiviso con il livello superiore.
 * 
 * @param <T> tipo dell'oggetto
 * @param <K> tipo della chiave dell'oggetto
 * @param <S> bean della ricerca paginata.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISearchFormService<T, K, S extends AbstractCoreSearchForm> extends IService<T, K> {

	public void setSearch(S search);

	public S getSearch();
	
}
