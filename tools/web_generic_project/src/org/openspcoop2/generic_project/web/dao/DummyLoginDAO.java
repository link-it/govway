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
package org.openspcoop2.generic_project.web.dao;

import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * DummyLoginDAO Fornisce un implementazione Dummy per l'interfaccia di login. 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DummyLoginDAO implements ILoginDAO{

	public DummyLoginDAO(){
	}


	@Override
	public boolean login(String username,String password) throws ServiceException{

		if(username ==null && password ==null){
			return false;
		}

		return true;
	}

}
