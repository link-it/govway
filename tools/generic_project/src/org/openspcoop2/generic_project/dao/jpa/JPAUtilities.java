/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.dao.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;

/**
 * JPAUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JPAUtilities {

	/**
	 * Crea la query parsando i criteri di ricerca della JPAExpression.
	 *
	 * Tramite la JPAExpression crea la parte WHERE della query
	 *
	 * @param queryPrefix Il prefisso della query da mettere prima della condizione WHERE
	 * @param expression I criteri di filtro che costituiscono la parte WHERE della query
	 * @throws ExpressionException 
	 * @throws ExpressionNotImplementedException 
	 */
	public static Query createQuery(EntityManager em, String queryPrefix, JPAExpression expression) throws ExpressionException, ExpressionNotImplementedException{

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		String sqlJPA = expression.toSqlForJPA(map);

		// Nota: la stringa puo' essere non vuota, anche se non ci sono condizioni di where, in caso di order by
		Query q = null;
		if(sqlJPA==null || "".equals(sqlJPA)){
			q = em.createQuery(queryPrefix);
		}else{
			if(expression.isWhereConditionsPresent()){
				q = em.createQuery(queryPrefix+" "+sqlJPA);
			}else{
				q = em.createQuery(queryPrefix+" WHERE "+sqlJPA);
			}
		}

		Enumeration<String> ids = map.keys();
		while (ids.hasMoreElements()) {
			String id = ids.nextElement();
			
			Object val = map.get(id);
			
			if(val instanceof Calendar){
				q.setParameter(id, ((Calendar)val).getTime(), TemporalType.TIMESTAMP);
			
			}else if(val instanceof Date){
				q.setParameter(id, (Date)val, TemporalType.TIMESTAMP);
			
			}else if(val instanceof IEnumeration){

				IEnumeration e = ((IEnumeration)map.get(id));
				q.setParameter(id, e.toString());
			}else{
				q.setParameter(id, map.get(id));
			}
		}

		return q;
	}
	public static Query createQuery(EntityManager em, String queryPrefix, JPAPaginatedExpression expression) throws ExpressionException, ExpressionNotImplementedException{

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		String sqlJPA = expression.toSqlForJPA(map);

		// Nota: la stringa puo' essere non vuota, anche se non ci sono condizioni di where, in caso di order by
		Query q = null;
		if(sqlJPA==null || "".equals(sqlJPA)){
			q = em.createQuery(queryPrefix);
		}else{
			if(expression.isWhereConditionsPresent()){
				q = em.createQuery(queryPrefix+" "+sqlJPA);
			}else{
				q = em.createQuery(queryPrefix+" WHERE "+sqlJPA);
			}
		}

		Enumeration<String> ids = map.keys();
		while (ids.hasMoreElements()) {
			String id = ids.nextElement();
			
			Object val = map.get(id);
			
			if(val instanceof Calendar){
				q.setParameter(id, ((Calendar)val).getTime(), TemporalType.TIMESTAMP);
			
			}else if(val instanceof Date){
				q.setParameter(id, (Date)val, TemporalType.TIMESTAMP);
			
			}else if(val instanceof IEnumeration){

				IEnumeration e = ((IEnumeration)map.get(id));
				q.setParameter(id, e.toString());
			}else{
				q.setParameter(id, map.get(id));
			}
		}

		return q;
	}

}
