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
package org.openspcoop2.generic_project.beans;

import java.util.Date;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * TimestampIntervalField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UnixTimestampIntervalField extends CustomField {
	
	public UnixTimestampIntervalField(String fieldName,ISQLFieldConverter fieldConverter,boolean appendTablePrefix, IField maxInterval, IField minInterval) throws ExpressionException, SQLQueryObjectException{
		super(fieldName, Long.class, buildFunction(fieldConverter,appendTablePrefix,maxInterval,minInterval), fieldName, "", "");
	}
	
	private static String buildFunction(ISQLFieldConverter fieldConverter, boolean appendTablePrefix, IField maxInterval, IField minInterval) throws ExpressionException, SQLQueryObjectException{
		if(maxInterval==null){
			throw new ExpressionException("MaxInterval is null");
		}
		if(minInterval==null){
			throw new ExpressionException("MinInterval is null");
		}
		if(!Date.class.getName().equals(maxInterval.getFieldType().getName())){
			throw new ExpressionException("MaxInterval with wrong type, expected:"+Date.class.getName()+" found:"+maxInterval.getFieldType());
		}
		if(!Date.class.getName().equals(minInterval.getFieldType().getName())){
			throw new ExpressionException("MinInterval with wrong type, expected:"+Date.class.getName()+" found:"+minInterval.getFieldType());
		}
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(fieldConverter.getDatabaseType());
		return sqlQueryObject.getDiffUnixTimestamp(fieldConverter.toColumn(maxInterval, appendTablePrefix), fieldConverter.toColumn(minInterval, appendTablePrefix));
	}
	
	private static int counter = 0;
	private static synchronized int getNextCounter(){
		counter = counter+1;
		if(counter>1000){
			counter = 0; // ruoto
		}
		return counter;
	}
	
	private String alias = null;
	public boolean existsAlias(){
		return this.alias!=null;
	}
	public String getAlias(){
		return this.alias;
	}
	public synchronized void buildAlias(){
		if(this.alias==null){
			this.alias = "TS"+getNextCounter();
		}
	}
}
