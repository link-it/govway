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
package org.openspcoop2.generic_project.web.service;

import org.openspcoop2.generic_project.web.form.Form;


/**
 *
 * BaseService Classe per la definizione dei metodi per interfacciarsi con un eventuale livello Dao/Ejb.
 * Il parametro F di tipo BaseForm un form (Ricerca/CRUD) che viene popolato dall'interfaccia grafica.
 * 
 * @param <F> tipo del form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseService <F extends Form> { 

	protected F form;
	
	public void setForm(F form){
		this.form = form;

	}
	
	public F getForm(){
		return this.form;
	}
}
