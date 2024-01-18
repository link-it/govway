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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteService;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;

/**
 * CredenzialiMittenteUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CredenzialiMittenteUtils {

	private CredenzialiMittenteUtils() {}
	
	public static List<CredenzialeMittente> translateByRef(List<CredenzialeMittente> findAll, ICredenzialeMittenteService credenzialeMittentiService) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		List<Long> ids = new ArrayList<>();
		if(findAll!=null && !findAll.isEmpty()) {
			for (CredenzialeMittente cm : findAll) {
				ids.add(cm.getRefCredenziale());
			}
		}
		if(!ids.isEmpty()) {
			IPaginatedExpression pagExprByRef = credenzialeMittentiService.newPaginatedExpression();
			pagExprByRef.and();
			JDBCCredenzialeMittenteService service = (JDBCCredenzialeMittenteService) credenzialeMittentiService;
			String tabella = service.getFieldConverter().toAliasTable(CredenzialeMittente.model());
			CustomField cfID = new CustomField("id", Long.class, "id", tabella);
			pagExprByRef.in(cfID, ids);
			findAll = credenzialeMittentiService.findAll(pagExprByRef);
		}
		return findAll;
	}
	
}
