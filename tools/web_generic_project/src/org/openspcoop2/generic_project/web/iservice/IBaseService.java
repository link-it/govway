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
package org.openspcoop2.generic_project.web.iservice;

import java.util.List;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.web.dao.IService;
import org.openspcoop2.generic_project.web.form.Form;


/**
 * IBaseService definisce un interfaccia tra il livello di accesso ai dati e i Bean della pagina.
 * form: Bean del form (ricerca/CRUD) condiviso con il livello superiore.
 * 
 * @param <T> tipo dell'oggetto
 * @param <K> tipo della chiave dell'oggetto
 * @param <F> form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IBaseService<T, K, F extends Form> extends IService<T, K> {

	public void setForm(F form);

	public F getForm();
	
	public List<T> findAll(F form) throws ServiceException;
}
