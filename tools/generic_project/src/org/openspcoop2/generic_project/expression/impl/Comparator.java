/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.expression.impl;

import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

/**
 * Comparator
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum Comparator {

	EQUALS("="),NOT_EQUALS("<>"),
	GREATER_THAN(">"),GREATER_EQUALS(">="),
	LESS_THAN("<"),LESS_EQUALS("<="),
	IS_NULL("is null"),IS_NOT_NULL("is not null"),
	IS_EMPTY("= ''"),IS_NOT_EMPTY("<> ''");
	
	private String operatore = null; 
	
	Comparator(String value){
		this.operatore = value;
	}
	
	public String getOperatore() {
		return this.getOperatore(null);
	}
	public String getOperatore(ISQLFieldConverter sqlFieldConverter) {
		try {
			if(sqlFieldConverter!=null && sqlFieldConverter.getDatabaseType()!=null) {
				if(TipiDatabase.ORACLE.equals(sqlFieldConverter.getDatabaseType())) {
					if(IS_EMPTY.equals(this)) {
						return IS_NULL.getOperatore();
					}
					else if(IS_NOT_EMPTY.equals(this)) {
						return IS_NOT_NULL.getOperatore();
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace(System.err); // log solamente l'errore
		}
		return this.operatore;
	}
}
