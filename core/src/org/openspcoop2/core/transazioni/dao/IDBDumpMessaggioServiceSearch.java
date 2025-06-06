/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.dao;

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCDumpMessaggioStream;
import org.openspcoop2.generic_project.dao.IDBServiceSearchWithId;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;


/** 
* Service can be used for research objects on the backend of type org.openspcoop2.core.transazioni.DumpMessaggio  
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDBDumpMessaggioServiceSearch extends IDumpMessaggioServiceSearch,IDBServiceSearchWithId<DumpMessaggio, IdDumpMessaggio> {
	
	public JDBCDumpMessaggioStream getContentInputStream(IExpression expression) 
			throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException;
	
}
