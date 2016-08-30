/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.expression.impl.test;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotFoundException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.ExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.PaginatedExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.BooleanTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.CalendarTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.DateTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.DoubleTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.EnumTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.FloatTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.IntegerTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.LongTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.StringTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.formatter.TimestampTypeFormatter;
import org.openspcoop2.generic_project.expression.impl.sql.ExpressionSQL;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.expression.impl.sql.PaginatedExpressionSQL;
import org.openspcoop2.generic_project.expression.impl.test.beans.Author;
import org.openspcoop2.generic_project.expression.impl.test.beans.Book;
import org.openspcoop2.generic_project.expression.impl.test.beans.Fruitore;
import org.openspcoop2.generic_project.expression.impl.test.beans.IdAccordoServizioParteSpecifica;
import org.openspcoop2.generic_project.expression.impl.test.beans.IdSoggetto;
import org.openspcoop2.generic_project.expression.impl.test.beans.Version;
import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationDouble;
import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationString;
import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationWrapperPrimitiveInt;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * ClientTest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	private static String tipoDatabase = "oracle";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		
		/* TEST Funzionalita' non dipendenti dal tipo di gestione del SQL */
		
		typeFormatter();
		
		
		
		/* TEST Funzionalita' dipendenti dal tipo di gestione del SQL */
		
		List<TestType> testTypes = new ArrayList<TestType>();
		testTypes.add(TestType.TO_STRING);
		testTypes.add(TestType.SQL_STANDARD);
		testTypes.add(TestType.PREPARED_STATEMENT);
		testTypes.add(TestType.SQL_STANDARD_QUERY_OBJECT);
		testTypes.add(TestType.PREPARED_STATEMENT_QUERY_OBJECT);
		testTypes.add(TestType.SQL_STANDARD_QUERY_OBJECT_WITH_FROM_CONDITION);
		testTypes.add(TestType.PREPARED_STATEMENT_QUERY_OBJECT_WITH_FROM_CONDITION); 
		
		for (Iterator<TestType> iterator = testTypes.iterator(); iterator.hasNext();) {
			TestType testType = iterator.next();
			System.out.println("\n\n\n============== "+testType.name()+" =========================");
			ClientTest.mode = testType;
			
			ClientTest.testAuthor();
			
			ClientTest.testBook();
			
			ClientTest.testFruitore(testType);
		}
		
	}

	private static TestType mode = TestType.TO_STRING; 
	
	private static void cleanManuallyFieldAdd(IExpression expr){
		if(expr instanceof ExpressionSQL){
			List<Object> list = ((ExpressionSQL)expr).getFieldsManuallyAdd();
			List<Object> newList = new ArrayList<Object>();
			for (Object object : list) {
				newList.add(object);
			}
			while (newList.size()>0){
				((ExpressionSQL)expr).removeFieldManuallyAdd(newList.remove(0));
			}
		}
		else if(expr instanceof PaginatedExpressionSQL){
			List<Object> list = ((PaginatedExpressionSQL)expr).getFieldsManuallyAdd();
			List<Object> newList = new ArrayList<Object>();
			for (Object object : list) {
				newList.add(object);
			}
			while (newList.size()>0){
				((PaginatedExpressionSQL)expr).removeFieldManuallyAdd(newList.remove(0));
			}
		}
	}
	
	public static ExpressionImpl newExpressionImplForAuthor() throws ExpressionException{
		return ClientTest.newExpressionImpl(new AuthorSQLFieldConverter());
	}
	public static ExpressionImpl newExpressionImplForBook() throws ExpressionException{
		return ClientTest.newExpressionImpl(new BookSQLFieldConverter());
	}
	public static ExpressionImpl newExpressionImpl(ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		return ClientTest.newExpressionImpl(null,sqlFieldConverter);
	}
	public static ExpressionImpl newExpressionImpl(IPaginatedExpression expr) throws ExpressionException{
		return ClientTest.newExpressionImpl(expr,null);
	}
	private static ExpressionImpl newExpressionImpl(IExpression expr, ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		switch (ClientTest.mode) {
		case TO_STRING:
			if(expr==null)
				return new ExpressionImpl();
			else{
//				try{
//					expr.limit(-1);
//					expr.offset(-1);
//				}catch(Exception e){}
				cleanManuallyFieldAdd(expr);
				return new ExpressionImpl((ExpressionImpl)expr);
			}
		case SQL_STANDARD:
		case SQL_STANDARD_QUERY_OBJECT:
		case SQL_STANDARD_QUERY_OBJECT_WITH_FROM_CONDITION:
			if(expr==null)
				return new ExpressionSQL(sqlFieldConverter);
			else{
				if(expr instanceof IPaginatedExpression){
					cleanManuallyFieldAdd(expr);
					return new ExpressionSQL((PaginatedExpressionSQL)expr);		
				}else{
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				}
			}
		case PREPARED_STATEMENT:
		case PREPARED_STATEMENT_QUERY_OBJECT:
		case PREPARED_STATEMENT_QUERY_OBJECT_WITH_FROM_CONDITION:
			if(expr==null)
				return new JDBCExpression(sqlFieldConverter);
			else{
				if(expr instanceof IPaginatedExpression){
					cleanManuallyFieldAdd(expr);
					return new JDBCExpression((JDBCPaginatedExpression)expr);		
				}else{
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				}
			}
		}
		throw new ExpressionException("Not implemented");
	}
	
	public static PaginatedExpressionImpl newPaginatedExpressionImplForAuthor() throws ExpressionException{
		return ClientTest.newPaginatedExpressionImpl(new AuthorSQLFieldConverter());
	}
	public static PaginatedExpressionImpl newPaginatedExpressionImplForBook() throws ExpressionException{
		return ClientTest.newPaginatedExpressionImpl(new BookSQLFieldConverter());
	}
	public static PaginatedExpressionImpl newPaginatedExpressionImpl(ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		return ClientTest.newPaginatedExpressionImpl(null,sqlFieldConverter);
	}
	public static PaginatedExpressionImpl newPaginatedExpressionImpl(IExpression expr) throws ExpressionException{
		return ClientTest.newPaginatedExpressionImpl(expr,null);
	}
	private static PaginatedExpressionImpl newPaginatedExpressionImpl(IExpression expr,ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		switch (ClientTest.mode) {
		case TO_STRING:
			if(expr==null)
				return new PaginatedExpressionImpl();
			else{
				cleanManuallyFieldAdd(expr);
				return new  PaginatedExpressionImpl((ExpressionImpl)expr);
			}
		case SQL_STANDARD:
		case SQL_STANDARD_QUERY_OBJECT:
		case SQL_STANDARD_QUERY_OBJECT_WITH_FROM_CONDITION:
			if(expr==null)
				return new PaginatedExpressionSQL(sqlFieldConverter);
			else{
				cleanManuallyFieldAdd(expr);
				return new PaginatedExpressionSQL((ExpressionSQL)expr);
			}
		case PREPARED_STATEMENT:
		case PREPARED_STATEMENT_QUERY_OBJECT:
		case PREPARED_STATEMENT_QUERY_OBJECT_WITH_FROM_CONDITION:
			if(expr==null)
				return new JDBCPaginatedExpression(sqlFieldConverter);
			else{
				cleanManuallyFieldAdd(expr);
				return new JDBCPaginatedExpression((JDBCExpression)expr);
			}
		}
		throw new ExpressionException("Not implemented");
	}
	
	public static String toString(IExpression expr) throws ExpressionException{
		return toString(expr,null);
	}
	public static String toString(IExpression expr,ISQLQueryObject sqlQueryObjectParam) throws ExpressionException{
		try{
			ExpressionSQL expSQL = null;
			PaginatedExpressionSQL pagExpSQL = null;
			if(!TestType.TO_STRING.equals(ClientTest.mode)){
				if(expr instanceof ExpressionSQL)
					expSQL =  ((ExpressionSQL)expr);
				else if(expr instanceof PaginatedExpressionSQL)
					pagExpSQL =  ((PaginatedExpressionSQL)expr);
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
			}
			
			switch (ClientTest.mode) {
			case TO_STRING:
				return expr.toString();
			case SQL_STANDARD:
				if(expSQL!=null)
					return expSQL.toSql();
				else if(pagExpSQL!=null)
					return pagExpSQL.toSql();
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
			case PREPARED_STATEMENT:
				List<Object> lista = new ArrayList<Object>();
				String s = null;
				if(expSQL!=null)
					s = ((JDBCExpression)expSQL).toSqlForPreparedStatement(lista);
				else if(pagExpSQL!=null)
					s = ((JDBCPaginatedExpression)pagExpSQL).toSqlForPreparedStatement(lista);
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				for (Iterator<?> iterator = lista.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					System.out.println("Object["+object.getClass().getName()+"]: "+object);
				}
				return s;
			case SQL_STANDARD_QUERY_OBJECT:
				ISQLQueryObject sqlQueryObject = null;
				if(sqlQueryObjectParam!=null){
					sqlQueryObject = sqlQueryObjectParam;
				}else{
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
				}
				if(expSQL!=null){
					expSQL.toSql(sqlQueryObject);
				}else if(pagExpSQL!=null){
					pagExpSQL.toSql(sqlQueryObject);
				}
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				sqlQueryObject.addFromTable("NOMETABELLA");
				sqlQueryObject.addSelectField("PROVA"); // group by richiede un alias
				return sqlQueryObject.createSQLQuery();
			case PREPARED_STATEMENT_QUERY_OBJECT:
				ISQLQueryObject sqlQueryObjectPreparedStatement = null;
				if(sqlQueryObjectParam!=null){
					sqlQueryObjectPreparedStatement = sqlQueryObjectParam;
				}else{
					sqlQueryObjectPreparedStatement = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
				}
				List<Object> listaQuery = new ArrayList<Object>();
				if(expSQL!=null)
					((JDBCExpression)expSQL).toSqlForPreparedStatement(sqlQueryObjectPreparedStatement,listaQuery);
				else if(pagExpSQL!=null)
					((JDBCPaginatedExpression)pagExpSQL).toSqlForPreparedStatement(sqlQueryObjectPreparedStatement,listaQuery);
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				for (Iterator<?> iterator = listaQuery.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					System.out.println("Object["+object.getClass().getName()+"]: "+object);
				}
				sqlQueryObjectPreparedStatement.addFromTable("NOMETABELLA");
				sqlQueryObjectPreparedStatement.addSelectField("PROVA"); // group by richiede un alias
				return sqlQueryObjectPreparedStatement.createSQLQuery();
			case SQL_STANDARD_QUERY_OBJECT_WITH_FROM_CONDITION:
				ISQLQueryObject sqlQueryObjectWithFromCondition = null;
				if(sqlQueryObjectParam!=null){
					sqlQueryObjectWithFromCondition = sqlQueryObjectParam;
				}else{
					sqlQueryObjectWithFromCondition = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
				}
				if(expSQL!=null){
					expSQL.toSqlWithFromCondition(sqlQueryObjectWithFromCondition, "NOMETABELLA");
				}else if(pagExpSQL!=null){
					pagExpSQL.toSqlWithFromCondition(sqlQueryObjectWithFromCondition,"NOMETABELLA");
				}else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				sqlQueryObjectWithFromCondition.addSelectField("PROVA"); // group by richiede un alias
				return sqlQueryObjectWithFromCondition.createSQLQuery();
			case PREPARED_STATEMENT_QUERY_OBJECT_WITH_FROM_CONDITION:
				ISQLQueryObject sqlQueryObjectPreparedStatementWithFromCondition = null;
				if(sqlQueryObjectParam!=null){
					sqlQueryObjectPreparedStatementWithFromCondition = sqlQueryObjectParam;
				}else{
					sqlQueryObjectPreparedStatementWithFromCondition = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
				}
				listaQuery = new ArrayList<Object>();
				if(expSQL!=null)
					((JDBCExpression)expSQL).toSqlForPreparedStatementWithFromCondition(sqlQueryObjectPreparedStatementWithFromCondition,listaQuery,"NOMETABELLA");
				else if(pagExpSQL!=null)
					((JDBCPaginatedExpression)pagExpSQL).toSqlForPreparedStatementWithFromCondition(sqlQueryObjectPreparedStatementWithFromCondition,listaQuery,"NOMETABELLA");
				else
					throw new ExpressionException("Tipo non gestito: "+expr.getClass().getName()); 	
				for (Iterator<?> iterator = listaQuery.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					System.out.println("Object["+object.getClass().getName()+"]: "+object);
				}
				sqlQueryObjectPreparedStatementWithFromCondition.addSelectField("PROVA"); // group by richiede un alias
				return sqlQueryObjectPreparedStatementWithFromCondition.createSQLQuery();
			}
		}catch(Exception e){
			throw new ExpressionException(e);
		}
		throw new ExpressionException("Not implemented");
	}
	
	public static void typeFormatter() throws ExpressionNotImplementedException, ExpressionException{
	
		ClientTest.stringTypeFormatter();
		
		ClientTest.booleanTypeFormatter();
		
		ClientTest.integerTypeFormatter();
		ClientTest.longTypeFormatter();
		
		ClientTest.doubleTypeFormatter();
		ClientTest.floatTypeFormatter();
		
		ClientTest.calendarTypeFormatter();
		ClientTest.dateTypeFormatter();
		ClientTest.timestampTypeFormatter();
		
		ClientTest.enumTypeFormatter_string();
		ClientTest.enumTypeFormatter_double();
		ClientTest.enumTypeFormatter_integer();
		
	}
	
	public static void testBook() throws Exception{
		
		Book book = new Book("La casa dei fantasmi", "Giovanni");
		
		Version v = new Version();
		v.setNumber("1.0");
		v.setDate(new Date());
		book.addVersion(v);
		
		Version r = new Version();
		r.setNumber("1.2");
		r.setDate(new Date());
		book.addReissue(r);
		
		ClientTest.withSameTypes(book);
		
		ClientTest.inUseField(book);
		
		ClientTest.inUseModel(book);
		
		ClientTest.enumeration(book);
		
		ClientTest.groupBy(book,false);
		
		ClientTest.groupBy(book,true);
	}
	
	public static void testAuthor() throws Exception{
			
		Author author = new Author("Jack", "White");
		author.setAge(30);
		author.setWeight(68);
		author.setBankAccount(1000.00);
		author.setSecondBankAccount(1000.00f);
		author.setDateOfBirth(new Date());
		author.setFirstBookReleaseDate(Calendar.getInstance());
		author.setLastBookReleaseDate(new Timestamp(System.currentTimeMillis()));
		
		ClientTest.constructorExpression(author);
		
		ClientTest.conjunctionNull(author);
		
		ClientTest.empty(author);
		
		ClientTest.mixed(author);
		
		ClientTest.equals(author);
		
		ClientTest.notEquals(author);
		
		ClientTest.greaterEquals(author);
		
		ClientTest.greaterThan(author);
		
		ClientTest.lessEquals(author);
		
		ClientTest.lessThan(author);
		
		ClientTest.isNull(author);
		
		ClientTest.isNotNull(author);
		
		ClientTest.isEmpty(author);
		
		ClientTest.isNotEmpty(author);
		
		ClientTest.between(author);
		
		ClientTest.like(author);
		
		ClientTest.ilike(author);
		
		ClientTest.in(author);
		
		ClientTest.and(author);
		
		ClientTest.or(author);
		
		ClientTest.not(author);
		
		ClientTest.order(author);
		
		switch (ClientTest.mode) {
		case SQL_STANDARD_QUERY_OBJECT:
		case PREPARED_STATEMENT_QUERY_OBJECT:
		case SQL_STANDARD_QUERY_OBJECT_WITH_FROM_CONDITION:
		case PREPARED_STATEMENT_QUERY_OBJECT_WITH_FROM_CONDITION:
			ClientTest.limit(author,false);
			break;
		default:
			ClientTest.limit(author, true);
			break;
		}
		
	}
	
	public static void testFruitore(TestType testType) throws Exception{
		
		Fruitore fruitore = new Fruitore();
		fruitore.setOraRegistrazione(new Date());
		
		IdSoggetto idFruitore = new IdSoggetto();
		idFruitore.setTipo("TIPO_FR");
		idFruitore.setNome("FRUITORE");
		fruitore.setIdFruitore(idFruitore);
		
		IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
		IdSoggetto idErogatore = new IdSoggetto();
		idErogatore.setTipo("TIPO_ER");
		idErogatore.setNome("EROGATORE");
		idAccordoServizioParteSpecifica.setIdErogatore(idErogatore);
		idAccordoServizioParteSpecifica.setTipo("TIPO_S");
		idAccordoServizioParteSpecifica.setNome("SERVIZIO");
		fruitore.setIdAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
		
		ClientTest.testModelEqualsWithSameFatherType(fruitore,testType);
		
		ClientTest.testSQLFieldConverterToColumn(fruitore, testType);
		
		ClientTest.testSQLFieldConverterToModelModel(fruitore, testType);
		
		ClientTest.testSQLFieldConverterToModelField(fruitore, testType);
	}

	public static void constructorExpression(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** constructorExpression ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor();
		expr = expr.and().equals(Author.model().NAME, "Jack").
				isNotNull(Author.model().DATE_OF_BIRTH).
				sortOrder(SortOrder.ASC).
				addOrder(Author.model().DATE_OF_BIRTH).addOrder(Author.model().NAME).
				addGroupBy(Author.model().DATE_OF_BIRTH).addGroupBy(Author.model().NAME);
		System.out.println("- test 1 IExpression: "+ClientTest.toString(expr));
		
		IPaginatedExpression pagExpr = ClientTest.newPaginatedExpressionImplForAuthor();
		pagExpr = (IPaginatedExpression) pagExpr.and().equals(Author.model().NAME, "Jack").
				isNotNull(Author.model().DATE_OF_BIRTH).
				sortOrder(SortOrder.ASC).
				addOrder(Author.model().DATE_OF_BIRTH).addOrder(Author.model().NAME).
				addGroupBy(Author.model().DATE_OF_BIRTH).addGroupBy(Author.model().NAME);
		pagExpr.limit(10).offset(2);
		System.out.println("- test 1b IExpression: "+ClientTest.toString(pagExpr));
		
		IPaginatedExpression paginatedTransformed = ClientTest.newPaginatedExpressionImpl(expr);
		paginatedTransformed.limit(10).offset(2);
		System.out.println("- test 1 trasformato in IPaginatedExpression: "+ClientTest.toString(paginatedTransformed));
		
		IPaginatedExpression paginatedExpr = ClientTest.newPaginatedExpressionImplForAuthor();
		paginatedExpr = (IPaginatedExpression) paginatedExpr.or().equals(Author.model().NAME, "Jack").sortOrder(SortOrder.ASC).
				addOrder(Author.model().DATE_OF_BIRTH).addOrder(Author.model().NAME).
				addGroupBy(Author.model().DATE_OF_BIRTH).addGroupBy(Author.model().NAME);
		paginatedExpr.limit(20).offset(3);
		System.out.println("- test 2 IPaginatedExpression: "+ClientTest.toString(paginatedExpr));
		
		IExpression exprTransformed = ClientTest.newExpressionImpl(paginatedExpr);
		System.out.println("- test 2 trasformato in IExpression: "+ClientTest.toString(exprTransformed));
		
	}
	
	public static void stringTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** StringTypeFormatter ************************* ");
		
		String value = "TEST";
		StringTypeFormatter formatter = new StringTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		String valueFromString = formatter.toObject(stringValue, String.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		String valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, String.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void booleanTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** BooleanTypeFormatter ************************* ");
		
		Boolean value = true;
		BooleanTypeFormatter formatter = new BooleanTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Boolean valueFromString = formatter.toObject(stringValue, Boolean.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(value!=valueFromString){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Boolean valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Boolean.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(value!=valueFromSQL){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void integerTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IntegerTypeFormatter ************************* ");
		
		Integer value = 13;
		IntegerTypeFormatter formatter = new IntegerTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Integer valueFromString = formatter.toObject(stringValue, Integer.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Integer valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Integer.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void longTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** LongTypeFormatter ************************* ");
		
		Long value = 15l;
		LongTypeFormatter formatter = new LongTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Long valueFromString = formatter.toObject(stringValue, Long.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Long valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Long.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void doubleTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** DoubleTypeFormatter ************************* ");
		
		Double value = 10.0;
		DoubleTypeFormatter formatter = new DoubleTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Double valueFromString = formatter.toObject(stringValue, Double.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Double valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Double.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void floatTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** FloatTypeFormatter ************************* ");
		
		Float value = 10.0f;
		FloatTypeFormatter formatter = new FloatTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Float valueFromString = formatter.toObject(stringValue, Float.class);
		System.out.println("- test toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Float valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Float.class);
		System.out.println("- test toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void calendarTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** CalendarTypeFormatter ************************* ");
		
		Calendar value = Calendar.getInstance();
		CalendarTypeFormatter formatter = new CalendarTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Calendar valueFromString = formatter.toObject(stringValue, Calendar.class);
		System.out.println("- test toObjectFromString["+formatter.toString(valueFromString)+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Calendar valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Calendar.class);
		System.out.println("- test toObjectFromSql["+formatter.toString(valueFromSQL)+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void dateTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** DateTypeFormatter ************************* ");
		
		Date value = new Date();
		DateTypeFormatter formatter = new DateTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Date valueFromString = formatter.toObject(stringValue, Date.class);
		System.out.println("- test toObjectFromString["+formatter.toString(valueFromString)+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Date valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Date.class);
		System.out.println("- test toObjectFromSql["+formatter.toString(valueFromSQL)+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void timestampTypeFormatter() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** TimestampTypeFormatter ************************* ");
		
		Timestamp value = new Timestamp(new Date().getTime());
		TimestampTypeFormatter formatter = new TimestampTypeFormatter();
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		Timestamp valueFromString = formatter.toObject(stringValue, Timestamp.class);
		System.out.println("- test toObjectFromString["+formatter.toString(valueFromString)+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		Timestamp valueFromSQL = formatter.toObject(stringSqlValueSenzaApici, Timestamp.class);
		System.out.println("- test toObjectFromSql["+formatter.toString(valueFromSQL)+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void enumTypeFormatter_string() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** EnumTypeFormatter (String) ************************* ");
		
		EnumTypeFormatter formatter = new EnumTypeFormatter();
		EnumerationString value = EnumerationString.AMMINISTRATIVO;
		
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test EnumString String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		EnumerationString valueFromString = (EnumerationString) formatter.toObject(stringValue, EnumerationString.class);
		System.out.println("- test EnumString toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		EnumerationString valueFromSQL = (EnumerationString) formatter.toObject(stringSqlValueSenzaApici, EnumerationString.class);
		System.out.println("- test EnumString toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void enumTypeFormatter_double() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** EnumTypeFormatter (double) ************************* ");
		
		EnumTypeFormatter formatter = new EnumTypeFormatter();
		EnumerationDouble value = EnumerationDouble._2_2;
		
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test EnumerationDouble String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		EnumerationDouble valueFromString = (EnumerationDouble) formatter.toObject(stringValue, EnumerationDouble.class);
		System.out.println("- test EnumerationDouble toObjectFromString["+valueFromString.getValue()+"] PARTENZA["+value.getValue()+"]");
		System.out.println("- test ["+value.getValue()==valueFromString.getValue()+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente ()");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		EnumerationDouble valueFromSQL = (EnumerationDouble) formatter.toObject(stringSqlValueSenzaApici, EnumerationDouble.class);
		System.out.println("- test EnumerationDouble toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void enumTypeFormatter_integer() throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** EnumTypeFormatter (Integer) ************************* ");
		
		EnumTypeFormatter formatter = new EnumTypeFormatter();
		EnumerationWrapperPrimitiveInt value = EnumerationWrapperPrimitiveInt._3;
		
		String stringValue = formatter.toString(value);
		String stringSqlValue = formatter.toSQLString(value);
		System.out.println("- test EnumerationWrapperPrimitiveInt String["+stringValue+"] Sql["+stringSqlValue+"]");
		
		EnumerationWrapperPrimitiveInt valueFromString = (EnumerationWrapperPrimitiveInt) formatter.toObject(stringValue, EnumerationWrapperPrimitiveInt.class);
		System.out.println("- test EnumerationWrapperPrimitiveInt toObjectFromString["+valueFromString+"]");
		if(!value.equals(valueFromString)){
			throw new ExpressionException("Errore, conversione toObjectFromString non effettuata correttamente");
		}
		
		String stringSqlValueSenzaApici = stringSqlValue.replaceAll("'", "");
		EnumerationWrapperPrimitiveInt valueFromSQL = (EnumerationWrapperPrimitiveInt) formatter.toObject(stringSqlValueSenzaApici, EnumerationWrapperPrimitiveInt.class);
		System.out.println("- test EnumerationWrapperPrimitiveInt toObjectFromSql["+valueFromSQL+"]");
		if(!value.equals(valueFromSQL)){
			throw new ExpressionException("Errore, conversione toObjectFromSql non effettuata correttamente");
		}
		
	}
	
	public static void withSameTypes(Book book) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** withSameTypes ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().VERSION.DATE, new Date());
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr.equals(Book.model().VERSION.NUMBER, "3.0");
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		try{
			Thread.sleep(100);
		}catch(Exception e){}
		expr.equals(Book.model().REISSUE.DATE, new Date());
		expr.equals(Book.model().REISSUE.NUMBER, "3.0");
		System.out.println("- test 3: "+ClientTest.toString(expr));

		expr = ClientTest.newExpressionImplForBook();
		//expr.equals(Book.model().TITLE,"Titolo A");
		expr.sortOrder(SortOrder.ASC);
		expr.addOrder(Book.model().VERSION.DATE);
		expr.addOrder(Book.model().REISSUE.NUMBER);
		System.out.println("- test order: "+ClientTest.toString(expr));
		
		IPaginatedExpression paginatedExpr = ClientTest.newPaginatedExpressionImplForBook();
		//paginatedExpr.equals(Book.model().TITLE,"Titolo A");
		paginatedExpr.sortOrder(SortOrder.ASC);
		paginatedExpr.addOrder(Book.model().VERSION.DATE);
		paginatedExpr.addOrder(Book.model().REISSUE.NUMBER);
		paginatedExpr.limit(10);
		System.out.println("- test paginated-expression: "+ClientTest.toString(paginatedExpr));
	
	}
	
	
	
	public static void inUseField(Book book) throws ExpressionNotImplementedException, ExpressionException, ExpressionNotFoundException, SQLQueryObjectException{
		
		System.out.println("\n **************** InUse Field ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().VERSION.DATE, new Date());
		expr.equals(Book.model().TITLE,"Titolo");
		
		boolean value = expr.inUseField(Book.model().VERSION.DATE,true);
		boolean valoreAtteso = true;
		System.out.println("- test inUse version.date: "+value);
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		System.out.println("- test get version.date: "+expr.getWhereConditionFieldValues(Book.model().VERSION.DATE).get(0).toString());
		
		value = expr.inUseField(Book.model().REISSUE.DATE,true);
		valoreAtteso = false;
		System.out.println("- test inUse reissue.date: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		try{
			System.out.println("- test get reissue.date: "+expr.getWhereConditionFieldValues(Book.model().REISSUE.DATE).get(0).toString());
			throw new ExpressionException("Attesa not found exception");
		}catch(ExpressionNotFoundException e){
			System.out.println("- test get reissue.date: not found");
		}
		
		value = expr.inUseField(Book.model().TITLE,true);
		valoreAtteso = true;
		System.out.println("- test inUse title: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		System.out.println("- test get title: "+expr.getWhereConditionFieldValues(Book.model().TITLE).get(0).toString());	
		
		value = expr.inUseField(Book.model().AUTHOR,true);
		valoreAtteso = false;
		System.out.println("- test inUse author: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		try{
			System.out.println("- test get author: "+expr.getWhereConditionFieldValues(Book.model().AUTHOR).get(0).toString());
			throw new ExpressionException("Attesa not found exception");
		}catch(ExpressionNotFoundException e){
			System.out.println("- test get author: not found");
		}
		
		List<IField> listaFilelds = expr.getFields(true);
		for (int i = 0; i < listaFilelds.size(); i++) {
			System.out.println("FIELD["+i+"]:\n"+listaFilelds.get(i).toString());
		}
		
		
		// Test di elementi presenti in condizioni diverse da while
		IExpression groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
		groupByExpr.equals(Book.model().TITLE,"Titolo");	
		groupByExpr.addGroupBy(Book.model().ENUM_STRING);
		value = groupByExpr.inUseField(Book.model().ENUM_STRING,true);
		valoreAtteso = false;
		System.out.println("- test inUse enum_string (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		value = groupByExpr.inUseField(Book.model().ENUM_STRING,false);
		valoreAtteso = true;
		System.out.println("- test inUse enum_string (non solo in where, ma in groupby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		List<IField> listVerifica = groupByExpr.getFields(true);
		value = listVerifica.contains(Book.model().ENUM_STRING);
		valoreAtteso = false;
		System.out.println("- test getFields enum_string (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		listVerifica = groupByExpr.getFields(false);
		value = listVerifica.contains(Book.model().ENUM_STRING);
		valoreAtteso = true;
		System.out.println("- test getFields enum_string (non solo in where, ma in groupby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		groupByExpr.sortOrder(SortOrder.ASC);
		groupByExpr.addOrder(Book.model().VERSION.DATE);
		value = groupByExpr.inUseField(Book.model().VERSION.DATE,true);
		valoreAtteso = false;
		System.out.println("- test inUse version.date (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		value = groupByExpr.inUseField(Book.model().VERSION.DATE,false);
		valoreAtteso = true;
		System.out.println("- test inUse version.date (non solo in where, ma in orderby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		listVerifica = groupByExpr.getFields(true);
		value = listVerifica.contains(Book.model().VERSION.DATE);
		valoreAtteso = false;
		System.out.println("- test getFields version.date (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		listVerifica = groupByExpr.getFields(false);
		value = listVerifica.contains(Book.model().VERSION.DATE);
		valoreAtteso = true;
		System.out.println("- test getFields version.date (non solo in where, ma in groupby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		// Test con metodi addField
		expr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
		expr.equals(Book.model().TITLE,"Titolo");	
		value = expr.inUseField(Book.model().TITLE,false);
		valoreAtteso = true;
		System.out.println("- test inUse title: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		if(expr instanceof ExpressionSQL){
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			((ExpressionSQL)expr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = expr.inUseField(Book.model().VERSION.DATE,false);
			valoreAtteso = true;
			System.out.println("- test inUse version.date: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = expr.inUseField(Book.model().VERSION.DATE,true);
			valoreAtteso = false;
			System.out.println("- test inUse version.date (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			expr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			expr.equals(Book.model().TITLE,"Titolo");	
			FunctionField ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumVersion");
			((ExpressionSQL)expr).addField(sqlQueryObject, ffSum, true);
			value = expr.inUseField(Book.model().VERSION.NUMBER,false);
			valoreAtteso = true;
			System.out.println("- test inUse version.number: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = expr.inUseField(Book.model().VERSION.NUMBER,true);
			valoreAtteso = false;
			System.out.println("- test inUse version.number (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			IPaginatedExpression paginatedExpr = ClientTest.newPaginatedExpressionImpl(new BookSQLFieldConverter());
			paginatedExpr.equals(Book.model().TITLE,"Titolo");
			paginatedExpr.limit(10).offset(2);
			((PaginatedExpressionSQL)paginatedExpr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = paginatedExpr.inUseField(Book.model().VERSION.DATE,false);
			valoreAtteso = true;
			System.out.println("- test IPaginatedExpression  inUse version.date: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = paginatedExpr.inUseField(Book.model().VERSION.DATE,true);
			valoreAtteso = false;
			System.out.println("- test IPaginatedExpression  inUse version.date (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			paginatedExpr = ClientTest.newPaginatedExpressionImpl(new BookSQLFieldConverter());
			paginatedExpr.equals(Book.model().TITLE,"Titolo");	
			paginatedExpr.limit(10).offset(2);
			ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			((PaginatedExpressionSQL)paginatedExpr).addField(sqlQueryObject, ffSum, true);
			value = paginatedExpr.inUseField(Book.model().VERSION.NUMBER,false);
			valoreAtteso = true;
			System.out.println("- test IPaginatedExpression inUse version.number: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = paginatedExpr.inUseField(Book.model().VERSION.NUMBER,true);
			valoreAtteso = false;
			System.out.println("- test IPaginatedExpression inUse version.number (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			groupByExpr.equals(Book.model().TITLE,"Titolo");
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			((ExpressionSQL)groupByExpr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = groupByExpr.inUseField(Book.model().VERSION.DATE, false);
			valoreAtteso = true;
			System.out.println("- test IGroupByExpression  inUse version.date: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = groupByExpr.inUseField(Book.model().VERSION.DATE, true);
			valoreAtteso = false;
			System.out.println("- test IGroupByExpression  inUse version.date (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			groupByExpr.equals(Book.model().TITLE,"Titolo");	
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			value = groupByExpr.inUseField(Book.model().VERSION.NUMBER, false);
			valoreAtteso = true;
			System.out.println("- test IGroupByExpression inUse version.number: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = groupByExpr.inUseField(Book.model().VERSION.NUMBER, true);
			valoreAtteso = false;
			System.out.println("- test IGroupByExpression inUse version.number (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			groupByExpr.equals(Book.model().TITLE,"Titolo");	
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			value = groupByExpr.inUseField(Book.model().ENUM_STRING, false);
			valoreAtteso = true;
			System.out.println("- test IGroupByExpression inUse enum_string: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = groupByExpr.inUseField(Book.model().ENUM_STRING, true);
			valoreAtteso = false;
			System.out.println("- test IGroupByExpression inUse enum_string (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
		}
		

		
		
	}
	
	
	public static void inUseModel(Book book) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException, ServiceException{
		
		System.out.println("\n **************** InUse Model ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().VERSION.DATE, new Date());
		expr.equals(Book.model().VERSION.NUMBER, "3.0");
		expr.equals(Book.model().TITLE,"Titolo");
		
		boolean value = expr.inUseModel(Book.model().VERSION, false);
		boolean valoreAtteso = true;
		System.out.println("- test inUse version: "+value);
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		value = expr.inUseModel(Book.model().REISSUE, false);
		valoreAtteso = false;
		System.out.println("- test inUse reissue: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		value = expr.inUseModel(Book.model(), false);
		valoreAtteso = true;
		System.out.println("- test inUse book: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		value = expr.inUseModel(Author.model(), false);
		valoreAtteso = false;
		System.out.println("- test inUse author: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		// Test di elementi presenti in condizioni diverse da while
		IExpression groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
		groupByExpr.equals(Book.model().TITLE,"Titolo");	
		groupByExpr.addGroupBy(Book.model().VERSION.NUMBER);
		value = groupByExpr.inUseModel(Book.model().VERSION,true);
		valoreAtteso = false;
		System.out.println("- test inUse version (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		value = groupByExpr.inUseModel(Book.model().VERSION,false);
		valoreAtteso = true;
		System.out.println("- test inUse version (non solo in where, ma in groupby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
		groupByExpr.equals(Book.model().TITLE,"Titolo");	
		groupByExpr.sortOrder(SortOrder.ASC);
		groupByExpr.addOrder(Book.model().VERSION.DATE);
		value = groupByExpr.inUseModel(Book.model().VERSION,true);
		valoreAtteso = false;
		System.out.println("- test inUse version (solo in where): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		value = groupByExpr.inUseModel(Book.model().VERSION,false);
		valoreAtteso = true;
		System.out.println("- test inUse version (non solo in where, ma in orderby): "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		// Test con metodi addField
		expr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
		expr.equals(Book.model().TITLE,"Titolo");	
		value = expr.inUseModel(Book.model().VERSION,false);
		valoreAtteso = false;
		System.out.println("- test inUse version: "+value);	
		if(value!=valoreAtteso){
			throw new ExpressionException("Valore atteso: "+valoreAtteso);
		}
		
		if(expr instanceof ExpressionSQL){
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			((ExpressionSQL)expr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = expr.inUseModel(Book.model().VERSION,false);
			valoreAtteso = true;
			System.out.println("- test inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = expr.inUseModel(Book.model().VERSION,true);
			valoreAtteso = false;
			System.out.println("- test inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			expr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			expr.equals(Book.model().TITLE,"Titolo");	
			FunctionField ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumVersion");
			((ExpressionSQL)expr).addField(sqlQueryObject, ffSum, true);
			value = expr.inUseModel(Book.model().VERSION,false);
			valoreAtteso = true;
			System.out.println("- test inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = expr.inUseModel(Book.model().VERSION,true);
			valoreAtteso = false;
			System.out.println("- test inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			IPaginatedExpression paginatedExpr = ClientTest.newPaginatedExpressionImpl(new BookSQLFieldConverter());
			paginatedExpr.equals(Book.model().TITLE,"Titolo");
			paginatedExpr.limit(10).offset(2);
			((PaginatedExpressionSQL)paginatedExpr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = paginatedExpr.inUseModel(Book.model().VERSION,false);
			valoreAtteso = true;
			System.out.println("- test IPaginatedExpression  inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = paginatedExpr.inUseModel(Book.model().VERSION,true);
			valoreAtteso = false;
			System.out.println("- test IPaginatedExpression  inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			paginatedExpr = ClientTest.newPaginatedExpressionImpl(new BookSQLFieldConverter());
			paginatedExpr.equals(Book.model().TITLE,"Titolo");	
			paginatedExpr.limit(10).offset(2);
			ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			((PaginatedExpressionSQL)paginatedExpr).addField(sqlQueryObject, ffSum, true);
			value = paginatedExpr.inUseModel(Book.model().VERSION,false);
			valoreAtteso = true;
			System.out.println("- test IPaginatedExpression inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = paginatedExpr.inUseModel(Book.model().VERSION,true);
			valoreAtteso = false;
			System.out.println("- test IPaginatedExpression inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			groupByExpr.equals(Book.model().TITLE,"Titolo");
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			((ExpressionSQL)groupByExpr).addField(sqlQueryObject, Book.model().VERSION.DATE, true);
			value = groupByExpr.inUseModel(Book.model().VERSION, false);
			valoreAtteso = true;
			System.out.println("- test IGroupByExpression  inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso: "+valoreAtteso);
			}
			value = groupByExpr.inUseModel(Book.model().VERSION, true);
			valoreAtteso = false;
			System.out.println("- test IGroupByExpression  inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore atteso (solo where): "+valoreAtteso);
			}
		
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			groupByExpr = ClientTest.newExpressionImpl(new BookSQLFieldConverter());
			groupByExpr.equals(Book.model().TITLE,"Titolo");	
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			value = groupByExpr.inUseModel(Book.model().VERSION, false);
			valoreAtteso = true;
			System.out.println("- test IGroupByExpression inUse version: "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number: "+valoreAtteso);
			}
			value = groupByExpr.inUseModel(Book.model().VERSION, true);
			valoreAtteso = false;
			System.out.println("- test IGroupByExpression inUse version (solo where): "+value);	
			if(value!=valoreAtteso){
				throw new ExpressionException("Valore number (solo where): "+valoreAtteso);
			}
			
		}
	}
	
	public static void enumeration(Book book) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Enumeration ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
		expr.equals(Book.model().ENUM_STRING, EnumerationString.ALTRO);
		expr.or();
		System.out.println("- test enum strong: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._1_1);
		expr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._2_2);
		expr.or();
		System.out.println("- test enum double: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._1);
		expr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._2);
		expr.or();
		System.out.println("- test enum wrapper primitive int: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForBook();
		expr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
		expr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._1_1);
		expr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._1);
		expr.or();
		System.out.println("- test enum join: "+ClientTest.toString(expr));
		
		IPaginatedExpression pagExpr = ClientTest.newPaginatedExpressionImplForBook();
		pagExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
		pagExpr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._1_1);
		pagExpr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._1);
		pagExpr.or();
		pagExpr.sortOrder(SortOrder.ASC);
		pagExpr.addOrder(Book.model().ENUM_STRING);
		pagExpr.limit(10);
		System.out.println("- test enum join (pagExpr): "+ClientTest.toString(pagExpr));
		
	}
	
	public static void groupBy(Book book, boolean paginated) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException{
		
		System.out.println("\n **************** GROUP BY ************************* ");
		
		IExpression groupByExpr = null;
		if(paginated){
			groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
		}else{
			groupByExpr = ClientTest.newExpressionImplForBook();
		}
		groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
		groupByExpr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._1_1);
		groupByExpr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._1);
		groupByExpr.or();
		groupByExpr.sortOrder(SortOrder.ASC);
		groupByExpr.addOrder(Book.model().ENUM_STRING);
		groupByExpr.addGroupBy(Book.model().AUTHOR);
		groupByExpr.addGroupBy(Book.model().TITLE);
		groupByExpr.addGroupBy(Book.model().ENUM_STRING);
		if(paginated){
			((IPaginatedExpression)groupByExpr).limit(10).offset(2);
		}
		System.out.println("- test group by (groupByExpr): "+ClientTest.toString(groupByExpr));
		
		
		if(paginated){
			groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
		}else{
			groupByExpr = ClientTest.newExpressionImplForBook();
		}
		groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
		groupByExpr.equals(Book.model().ENUM_DOUBLE, EnumerationDouble._1_1);
		groupByExpr.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT, EnumerationWrapperPrimitiveInt._1);
		groupByExpr.or();
		groupByExpr.sortOrder(SortOrder.ASC);
		groupByExpr.addOrder(Book.model().ENUM_STRING);
		groupByExpr.addGroupBy(Book.model().AUTHOR);
		groupByExpr.addGroupBy(Book.model().TITLE);
		groupByExpr.addGroupBy(Book.model().VERSION.NUMBER);
		groupByExpr.addGroupBy(Book.model().ENUM_STRING);
		if(paginated){
			((IPaginatedExpression)groupByExpr).limit(10).offset(2);
		}
		System.out.println("- test group by (groupByExpr2) con versione table : "+ClientTest.toString(groupByExpr));
		
		
		
		if( (groupByExpr instanceof ExpressionSQL) || (groupByExpr instanceof PaginatedExpressionSQL) ){
			
			// SUM
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			FunctionField ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			}
			System.out.println("- test group by con SUM (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffSUMDate = new FunctionField(Book.model().VERSION.DATE,Function.SUM,"sumDate");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSUMDate, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSUMDate, true);
			}
			System.out.println("- test group by con SUM Date (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			
			// AVG
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffAvg = new FunctionField(Book.model().VERSION.NUMBER,Function.AVG,"avgNumber");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvg, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvg, true);
			}
			System.out.println("- test group by con AVG (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffAVGDate = new FunctionField(Book.model().VERSION.DATE,Function.AVG,"avgDate");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAVGDate, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAVGDate, true);
			}
			System.out.println("- test group by con AVG Date (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			
			// AVG_DOUBLE
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffAvgDouble = new FunctionField(Book.model().VERSION.NUMBER,Function.AVG_DOUBLE,"avgNumber");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvgDouble, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvgDouble, true);
			}
			System.out.println("- test group by con AVG_DOUBLE (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffAVGDateDouble = new FunctionField(Book.model().VERSION.DATE,Function.AVG_DOUBLE,"avgDate");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAVGDateDouble, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAVGDateDouble, true);
			}
			System.out.println("- test group by con AVG_DOUBLE Date (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// MAX
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMax = new FunctionField(Book.model().VERSION.NUMBER,Function.MAX,"maxNumber");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMax, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMax, true);
			}
			System.out.println("- test group by con MAX (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMAXDate = new FunctionField(Book.model().VERSION.DATE,Function.MAX,"maxDate");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMAXDate, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMAXDate, true);
			}
			System.out.println("- test group by con MAX Date (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// MIN
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMin = new FunctionField(Book.model().VERSION.NUMBER,Function.MIN,"minNumber");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMin, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMin, true);
			}
			System.out.println("- test group by con MIN (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMINDate = new FunctionField(Book.model().VERSION.DATE,Function.MIN,"minDate");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMINDate, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMINDate, true);
			}
			System.out.println("- test group by con MIN Date (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// INSIEME PIU' FUNZIONI
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
			}
			
			ffSum = new FunctionField(Book.model().VERSION.NUMBER,Function.SUM,"sumNumber");
			if(paginated){
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffSum, true);
			}
			
			ffAvg = new FunctionField(Book.model().VERSION.NUMBER,Function.AVG,"avgNumber");
			if(paginated){
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvg, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvg, true);
			}
			
			ffAvgDouble = new FunctionField(Book.model().VERSION.NUMBER,Function.AVG_DOUBLE,"avgDouble");
			if(paginated){
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvgDouble, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffAvgDouble, true);
			}
			
			ffMax = new FunctionField(Book.model().VERSION.NUMBER,Function.MAX,"maxNumber");
			if(paginated){
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMax, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMax, true);
			}
			
			System.out.println("- test group by con SUM / AVG / AVG_DOUBLE / MAX (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			
			// MIN (con valore custom)
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMinFunctionValue = new FunctionField("ValoreCustom",String.class,Function.MIN,"minCustom");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMinFunctionValue, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMinFunctionValue, true);
			}
			System.out.println("- test group by con MIN valore Custom (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// MIN (con valori e operator)
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffMinIFieldsWithOperator = new FunctionField(Function.MIN,"minCustom","-",Book.model().TITLE,Book.model().AUTHOR);
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMinIFieldsWithOperator, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffMinIFieldsWithOperator, true);
			}
			System.out.println("- test group by con MIN con operatore (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
						
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
									
			// FunctionCustom
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffCustom = new FunctionField(Book.model().VERSION.NUMBER,"CUSTOM_FUNCTION(",")","custom");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustom, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustom, true);
			}
			System.out.println("- test group by con FunctionCustom (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// FunctionCustom (con valore custom)
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffCustomFunctionValue = new FunctionField("ValoreCustom",String.class,"CUSTOM_FUNCTION(",")","custom");
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustomFunctionValue, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustomFunctionValue, true);
			}
			System.out.println("- test group by con FunctionCustom e valore Custom (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
			
			// FunctionCustom (con operatore)
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(ClientTest.tipoDatabase);
			if(paginated){
				groupByExpr = ClientTest.newPaginatedExpressionImplForBook();
			}else{
				groupByExpr = ClientTest.newExpressionImplForBook();
			}
			groupByExpr.equals(Book.model().ENUM_STRING, EnumerationString.AMMINISTRATIVO);
			groupByExpr.sortOrder(SortOrder.ASC);
			groupByExpr.addOrder(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().ENUM_STRING);
			groupByExpr.addGroupBy(Book.model().AUTHOR);
			FunctionField ffCustomWithOperator = new FunctionField("CUSTOM_FUNCTION(",")","custom","-",Book.model().TITLE,Book.model().AUTHOR);
			if(paginated){
				((IPaginatedExpression)groupByExpr).limit(10).offset(2);
				((PaginatedExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustomWithOperator, true);
			}
			else{
				((ExpressionSQL)groupByExpr).addField(sqlQueryObject, ffCustomWithOperator, true);
			}
			System.out.println("- test group by con FunctionCustom e operatore (groupByExpr): "+ClientTest.toString(groupByExpr,sqlQueryObject));
		}
		
	}
	
	public static void conjunctionNull(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** conjunctionNull ************************* ");
		
		IExpression exp = ClientTest.newExpressionImplForAuthor();
		System.out.println("- test expression empty: "+ClientTest.toString(exp));

		IExpression expValorizzata = ClientTest.newExpressionImplForAuthor();
		expValorizzata.equals(Author.model().AGE,3);
		System.out.println("- test expression valorizzata: "+ClientTest.toString(expValorizzata));

		expValorizzata.and(exp);
		System.out.println("- test expression valorizzata con aggiunta exp vuota: "+ClientTest.toString(expValorizzata));
		
		IExpression expValorizzata2 = ClientTest.newExpressionImplForAuthor();
		expValorizzata2.equals(Author.model().AGE,6);
		expValorizzata.and(exp,expValorizzata2);
		System.out.println("- test expression valorizzata con aggiunta exp valorizzata e exp vuota: "+ClientTest.toString(expValorizzata));
	
	}
	
	
	
	public static void empty(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Empty ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor();
		System.out.println("- test expression: "+ClientTest.toString(expr));
		
		boolean empty = expr.isWhereConditionsPresent();
		System.out.println("- test expression isEmpty 1: "+empty);
		if(!empty){
			System.out.println("ERROR: Excepected true, returned false");
			return;
		}
		
		expr.equals(Author.model().AGE,3);
		empty = expr.isWhereConditionsPresent();
		System.out.println("- test expression isEmpty 2: "+empty);
		if(empty){
			System.out.println("ERROR: Excepected false, returned true");
			return;
		}
		

		IPaginatedExpression paginatedExpr = ClientTest.newPaginatedExpressionImplForAuthor();
		System.out.println("- test paginated-expression: "+ClientTest.toString(paginatedExpr));
		
		empty = paginatedExpr.isWhereConditionsPresent();
		System.out.println("- test paginated-expression isEmpty 1: "+empty);
		if(!empty){
			System.out.println("ERROR: Excepected true, returned false");
			return;
		}
		
		paginatedExpr.equals(Author.model().AGE,3);
		empty = paginatedExpr.isWhereConditionsPresent();
		System.out.println("- test paginated-expression isEmpty 2: "+empty);
		if(empty){
			System.out.println("ERROR: Excepected false, returned true");
			return;
		}
	
	}
	
	
	public static void mixed(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Mixed ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().
		or().equals(Author.model().NAME, "Jack").notEquals(Author.model().NAME, "Jack2").greaterThan(Author.model().AGE, 30);
		System.out.println("- test 1: "+ClientTest.toString(expr));

		expr = ClientTest.newExpressionImplForAuthor().
		not().or().equals(Author.model().NAME, "Jack").notEquals(Author.model().NAME, "Jack2").greaterThan(Author.model().AGE, 30);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr.and();
		System.out.println("- test 3: "+ClientTest.toString(expr));
	}
	
	
	public static void equals(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Equals ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack Do'Rien");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.equals(Author.model().NAME, "Johnny");
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.equals(Author.model().NAME, "Frederick");
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		Hashtable<IField, Object> objects = new Hashtable<IField, Object>();
		objects.put(Author.model().AGE, author.getAge());
		objects.put(Author.model().WEIGHT, author.getWeight());
		objects.put(Author.model().BANK_ACCOUNT,author.getBankAccount());
		objects.put(Author.model().SECOND_BANK_ACCOUNT,author.getSecondBankAccount());
		objects.put(Author.model().DATE_OF_BIRTH,author.getDateOfBirth());
		objects.put(Author.model().FIRST_BOOK_RELEASE_DATE,author.getFirstBookReleaseDate());
		objects.put(Author.model().LAST_BOOK_RELEASE_DATE,author.getLastBookReleaseDate());
		expr = expr.allEquals(objects);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack");
		expr = expr.not(expr);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		IExpression expr2 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Mickael");
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack");
		expr = expr2.not(expr);
		System.out.println("- test 6: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack").allEquals(objects,false);
		System.out.println("- test 7: "+ClientTest.toString(expr));
		
	}
	
	
	public static void notEquals(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** NotEquals ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().notEquals(Author.model().NAME, "Jack");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.notEquals(Author.model().NAME, "Johnny");
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.notEquals(Author.model().NAME, "Frederick");
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		Hashtable<IField, Object> objects = new Hashtable<IField, Object>();
		objects.put(Author.model().AGE, author.getAge());
		objects.put(Author.model().WEIGHT, author.getWeight());
		objects.put(Author.model().BANK_ACCOUNT,author.getBankAccount());
		objects.put(Author.model().SECOND_BANK_ACCOUNT,author.getSecondBankAccount());
		objects.put(Author.model().DATE_OF_BIRTH,author.getDateOfBirth());
		objects.put(Author.model().FIRST_BOOK_RELEASE_DATE,author.getFirstBookReleaseDate());
		objects.put(Author.model().LAST_BOOK_RELEASE_DATE,author.getLastBookReleaseDate());
		expr = expr.allNotEquals(objects);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().notEquals(Author.model().NAME, "Jack");
		expr = expr.not(expr);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().notEquals(Author.model().NAME, "Jack").allNotEquals(objects,false);
		System.out.println("- test 6: "+ClientTest.toString(expr));
		
	}
	
	
	public static void greaterEquals(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** GreaterEquals ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().greaterEquals(Author.model().DATE_OF_BIRTH, new Date());
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.greaterEquals(Author.model().FIRST_BOOK_RELEASE_DATE, Calendar.getInstance());
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.greaterEquals(Author.model().LAST_BOOK_RELEASE_DATE, new Timestamp(System.currentTimeMillis()));
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().greaterEquals(Author.model().DATE_OF_BIRTH, new Date());
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void greaterThan(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** greaterThan ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().greaterThan(Author.model().DATE_OF_BIRTH, new Date());
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.greaterThan(Author.model().FIRST_BOOK_RELEASE_DATE, Calendar.getInstance());
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.greaterThan(Author.model().LAST_BOOK_RELEASE_DATE, new Timestamp(System.currentTimeMillis()));
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().greaterThan(Author.model().DATE_OF_BIRTH, new Date());
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void lessEquals(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** LessEquals ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().lessEquals(Author.model().DATE_OF_BIRTH, new Date());
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.lessEquals(Author.model().FIRST_BOOK_RELEASE_DATE, Calendar.getInstance());
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.lessEquals(Author.model().LAST_BOOK_RELEASE_DATE, new Timestamp(System.currentTimeMillis()));
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().lessEquals(Author.model().DATE_OF_BIRTH, new Date());
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void lessThan(Author author) throws ExpressionNotImplementedException, ExpressionException{
			
		System.out.println("\n **************** LessThan ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().lessThan(Author.model().DATE_OF_BIRTH, new Date());
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.lessThan(Author.model().FIRST_BOOK_RELEASE_DATE, Calendar.getInstance());
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.lessThan(Author.model().LAST_BOOK_RELEASE_DATE, new Timestamp(System.currentTimeMillis()));
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().lessThan(Author.model().DATE_OF_BIRTH, new Date());
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void isNull(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IsNull ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().isNull(Author.model().AGE);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.isNull(Author.model().FIRST_BOOK_RELEASE_DATE);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.isNull(Author.model().LAST_BOOK_RELEASE_DATE);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().isNull(Author.model().DATE_OF_BIRTH);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void isNotNull(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IsNotNull ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().isNotNull(Author.model().AGE);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.isNotNull(Author.model().FIRST_BOOK_RELEASE_DATE);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.isNotNull(Author.model().LAST_BOOK_RELEASE_DATE);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().isNotNull(Author.model().DATE_OF_BIRTH);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void isEmpty(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IsEmpty ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().isEmpty(Author.model().AGE);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.isEmpty(Author.model().FIRST_BOOK_RELEASE_DATE);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.isEmpty(Author.model().LAST_BOOK_RELEASE_DATE);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().isEmpty(Author.model().DATE_OF_BIRTH);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void isNotEmpty(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IsNotEmpty ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().isNotEmpty(Author.model().AGE);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.isNotEmpty(Author.model().FIRST_BOOK_RELEASE_DATE);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.isNotEmpty(Author.model().LAST_BOOK_RELEASE_DATE);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().isNotEmpty(Author.model().DATE_OF_BIRTH);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void between(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Between ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().between(Author.model().AGE,10,20);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.between(Author.model().WEIGHT,60,70);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.between(Author.model().BANK_ACCOUNT,1000.00,2000.00);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().between(Author.model().AGE,20,30);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
	}
	
	public static void like(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** Like ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().like(Author.model().NAME,"ANY");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.like(Author.model().NAME,"END",LikeMode.END);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.like(Author.model().NAME,"EXACT",LikeMode.EXACT);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().like(Author.model().NAME,"START",LikeMode.START);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().like(Author.model().NAME,"Dell'Asilo"); // test per escape
		System.out.println("- test 5 (escape): "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().like(Author.model().NAME,"Dell'Asilo_peresempio%piu'complesso"); // test per escape
		System.out.println("- test 6 (escape complesso): "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().
				like(Author.model().NAME,"Dell'Asilo_peresempio%piu'complesso"); // test per escape
		expr.and().equals(Author.model().NAME,"Altro");
		System.out.println("- test 7 (escape complesso con and): "+ClientTest.toString(expr));
		
	}
	
	public static void ilike(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** ilike ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().ilike(Author.model().NAME,"ANY");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.ilike(Author.model().NAME,"END",LikeMode.END);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.ilike(Author.model().NAME,"EXACT",LikeMode.EXACT);
		System.out.println("- test 3: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().ilike(Author.model().NAME,"START",LikeMode.START);
		expr = expr.not(expr);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().ilike(Author.model().NAME,"Dell'Asilo"); // test per escape
		System.out.println("- test 5 (escape): "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().ilike(Author.model().NAME,"Dell'Asilo_peresempio%piu'complesso"); // test per escape
		System.out.println("- test 6 (escape complesso): "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().
				ilike(Author.model().NAME,"Dell'Asilo_peresempio%piu'complesso"); // test per escape
		expr.and().equals(Author.model().NAME,"Altro");
		System.out.println("- test 7 (escape complesso con and): "+ClientTest.toString(expr));
		
	}
	
	public static void in(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** IN ************************* ");
		
		List<Object> l = new ArrayList<Object>();
		l.add(author.getAge());
		l.add(author.getWeight());
		l.add(author.getBankAccount());
		l.add(author.getSecondBankAccount());
		l.add(author.getDateOfBirth());
		l.add(author.getFirstBookReleaseDate());
		l.add(author.getLastBookReleaseDate());
		
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().in(Author.model().NAME,l);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.in(Author.model().NAME,l.toArray(new Object[1]));
		System.out.println("- test 2: "+ClientTest.toString(expr));
				
		expr = ClientTest.newExpressionImplForAuthor().in(Author.model().NAME,l);
		expr = expr.not(expr);
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
	}
	
	public static void and(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** And ************************* ");
		
		IExpression expr1 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack1");
		IExpression expr2 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack2");
		IExpression expr3 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack3");
		IExpression expr4 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack4");
		
		IExpression exprA = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "MikeA");
		IExpression exprB = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "MikeB");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().and(expr1, expr2);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().and(expr1, expr2, expr3, expr4);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().SURNAME, "Black").and(expr1, expr2);
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().SURNAME, "Black").and(expr1, expr2, expr3, expr4);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().and(exprA, exprB).and(expr1, expr2);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().and(exprA, exprB).and(expr1, expr2, expr3, expr4);
		System.out.println("- test 6: "+ClientTest.toString(expr));

	}
	
	public static void or(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** or ************************* ");
		
		IExpression expr1 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack1");
		IExpression expr2 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack2");
		IExpression expr3 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack3");
		IExpression expr4 = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "Jack4");
		
		IExpression exprA = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "MikeA");
		IExpression exprB = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME, "MikeB");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().or(expr1, expr2);
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().or(expr1, expr2, expr3, expr4);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().SURNAME, "Black").or(expr1, expr2);
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().SURNAME, "Black").or(expr1, expr2, expr3, expr4);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().or(exprA, exprB).or(expr1, expr2);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().or(exprA, exprB).or(expr1, expr2, expr3, expr4);
		System.out.println("- test 6: "+ClientTest.toString(expr));

	}
	
	public static void not(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** not ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().not(expr);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		IExpression exprA = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME-2");
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME").not(exprA);
		System.out.println("- test 3: "+ClientTest.toString(expr));
						
	}
	
	public static void order(Author author) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** order ************************* ");
		
		IExpression expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		expr = expr.sortOrder(SortOrder.ASC);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = expr.sortOrder(SortOrder.DESC);
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		expr = expr.sortOrder(SortOrder.UNSORTED);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = expr.sortOrder(SortOrder.ASC).addOrder(Author.model().AGE);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		expr = expr.addOrder(Author.model().DATE_OF_BIRTH);
		System.out.println("- test 6: "+ClientTest.toString(expr));
		
		expr = expr.sortOrder(SortOrder.DESC);
		System.out.println("- test 7: "+ClientTest.toString(expr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		expr = expr.addOrder(Author.model().DATE_OF_BIRTH,SortOrder.DESC);
		expr = expr.addOrder(Author.model().AGE,SortOrder.ASC);
		System.out.println("- test 8 (expr) Date-DESC Age-ASC: "+ClientTest.toString(expr));
		
		IPaginatedExpression pagExpr = (IPaginatedExpression) ClientTest.newPaginatedExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		pagExpr = (IPaginatedExpression) pagExpr.addOrder(Author.model().DATE_OF_BIRTH,SortOrder.DESC);
		pagExpr = (IPaginatedExpression) pagExpr.addOrder(Author.model().AGE,SortOrder.ASC);
		pagExpr.offset(0);
		pagExpr.limit(10);
		System.out.println("- test 8 (pagExpr) Date-DESC Age-ASC: "+ClientTest.toString(pagExpr));
		
		expr = ClientTest.newExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		expr = expr.sortOrder(SortOrder.DESC);
		expr = expr.addOrder(Author.model().DATE_OF_BIRTH);
		expr = expr.addOrder(Author.model().AGE,SortOrder.ASC);
		System.out.println("- test 9 (expr) Date-DefaultDESC Age-ASC: "+ClientTest.toString(expr));
		
		pagExpr = (IPaginatedExpression) ClientTest.newPaginatedExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		pagExpr = (IPaginatedExpression) pagExpr.sortOrder(SortOrder.DESC);
		pagExpr = (IPaginatedExpression) pagExpr.addOrder(Author.model().DATE_OF_BIRTH);
		pagExpr = (IPaginatedExpression) pagExpr.addOrder(Author.model().AGE,SortOrder.ASC);
		pagExpr.offset(0);
		pagExpr.limit(10);
		System.out.println("- test 9 (pagExpr) Date-DefaultDESC Age-ASC: "+ClientTest.toString(pagExpr));
						
	}
	
	public static void limit(Author author,boolean testWithOutOrder) throws ExpressionNotImplementedException, ExpressionException{
		
		System.out.println("\n **************** limit ************************* ");
		
		IPaginatedExpression expr = (IPaginatedExpression) ClientTest.newPaginatedExpressionImplForAuthor().equals(Author.model().NAME,"NAME");
		System.out.println("- test 1: "+ClientTest.toString(expr));
		
		if(testWithOutOrder){
			expr = expr.offset(10).limit(20);
			System.out.println("- test limit: "+ClientTest.toString(expr));
		}
		
		expr = (IPaginatedExpression) expr.sortOrder(SortOrder.ASC);
		System.out.println("- test 2: "+ClientTest.toString(expr));
		
		expr = (IPaginatedExpression) expr.sortOrder(SortOrder.ASC).addOrder(Author.model().AGE);
		System.out.println("- test 3: "+ClientTest.toString(expr));
		
		if(!testWithOutOrder){
			expr = expr.offset(10).limit(20);
			System.out.println("- test limit: "+ClientTest.toString(expr));
		}
		
		expr = (IPaginatedExpression) expr.addOrder(Author.model().DATE_OF_BIRTH);
		System.out.println("- test 4: "+ClientTest.toString(expr));
		
		expr = (IPaginatedExpression) expr.sortOrder(SortOrder.DESC);
		System.out.println("- test 5: "+ClientTest.toString(expr));
		
		expr = expr.offset(-1);
		System.out.println("- test 6: "+ClientTest.toString(expr));
		
		expr = expr.limit(-1);
		System.out.println("- test 7: "+ClientTest.toString(expr));
						
	}
	
	public static void testModelEqualsWithSameFatherType(Fruitore fruitore,TestType testType) throws ExpressionNotImplementedException, ExpressionException{
		
		
		System.out.println("\n **************** testModelEqualsWithSameFatherType ************************* ");
		
		FruitoreSQLFieldConverter fruitoreConverter = new FruitoreSQLFieldConverter();
		IExpression expr = ClientTest.newExpressionImpl(fruitoreConverter);
		
		expr.equals(Fruitore.model().ID_FRUITORE.TIPO, "FRUITORE_TIPO");
		expr.equals(Fruitore.model().ID_FRUITORE.NOME, "FRUITORE_NOME");
		
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO, "EROGATORE_TIPO");
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME, "EROGATORE_NOME");
		
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, "SERVIZIO_TIPO");
		expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME, "SERVIZIO_NOME");
		
		String sql = ClientTest.toString(expr);
		System.out.println("- test 1: "+sql);
		if(TestType.TO_STRING.equals(testType)==false){
			if(sql.contains("soggFruitori")==false){
				throw new ExpressionException("'soggFruitori' non trovato come atteso dal test");
			}
			if(sql.contains("soggErogatori")==false){
				throw new ExpressionException("'soggErogatori' non trovato come atteso dal test");
			}
			
			String tabellaFruitori = fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE);
			System.out.println("- test 2: "+tabellaFruitori);
			if("soggetti as soggFruitori".equals(tabellaFruitori)==false){
				throw new ExpressionException("'soggetti as soggFruitori' non trovato come atteso dal test, trovato invece ["+tabellaFruitori+"]");
			}
			
			String tabellaErogatori = fruitoreConverter.toTable(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE);
			System.out.println("- test 3: "+tabellaErogatori);
			if("soggetti as soggErogatori".equals(tabellaErogatori)==false){
				throw new ExpressionException("'soggetti as soggErogatori' non trovato come atteso dal test, trovato invece ["+tabellaErogatori+"]");
			}
		}
		
		
		// test 4
		expr = ClientTest.newExpressionImpl(fruitoreConverter);
		expr.sortOrder(SortOrder.ASC);
		expr.addOrder(Fruitore.model().ORA_REGISTRAZIONE);
		expr.addOrder(Fruitore.model().ID_FRUITORE.TIPO);
		sql = ClientTest.toString(expr);
		System.out.println("- test 4: "+sql);
		
		boolean inUse = expr.inUseModel(Fruitore.model().ID_FRUITORE, false);
		System.out.println("- test 5 Exp (atteso true): "+inUse);
		if(!inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE, false) = true");
		}
		
		inUse = expr.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA, false);
		System.out.println("- test 6 Exp (atteso false): "+inUse);
		if(inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA, false) = false");
		}
		
		inUse = expr.inUseField(Fruitore.model().ID_FRUITORE.TIPO, false);
		System.out.println("- test 7 Exp (atteso true): "+inUse);
		if(!inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE.TIPO, false) = true");
		}
		
		inUse = expr.inUseField(Fruitore.model().ID_FRUITORE.NOME, false);
		System.out.println("- test 8 Exp (atteso false): "+inUse);
		if(inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE.NOME, false) = false");
		}

		IPaginatedExpression pagExpr = ClientTest.newPaginatedExpressionImpl(expr);
		
		inUse = pagExpr.inUseModel(Fruitore.model().ID_FRUITORE, false);
		System.out.println("- test 9 pagExpr (atteso true): "+inUse);
		if(!inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE, false) = true");
		}
		
		inUse = pagExpr.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA, false);
		System.out.println("- test 10 pagExpr (atteso false): "+inUse);
		if(inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA, false) = false");
		}
		
		inUse = pagExpr.inUseField(Fruitore.model().ID_FRUITORE.TIPO, false);
		System.out.println("- test 11 pagExpr (atteso true): "+inUse);
		if(!inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE.TIPO, false) = true");
		}
		
		inUse = pagExpr.inUseField(Fruitore.model().ID_FRUITORE.NOME, false);
		System.out.println("- test 12 pagExpr (atteso false): "+inUse);
		if(inUse){
			throw new ExpressionException("Atteso expr.inUseModel(Fruitore.model().ID_FRUITORE.NOME, false) = false");
		}
	}
	
	
	public static void testSQLFieldConverterToColumn(Fruitore fruitore,TestType testType) throws ExpressionNotImplementedException, ExpressionException{
		
		
		System.out.println("\n **************** testSQLFieldConverterToColumn ************************* ");
		
		FruitoreSQLFieldConverter fruitoreConverter = new FruitoreSQLFieldConverter();
		
		
		// test toColumn con colonna che non ha alias nella colonna ma possiede un alias nella tabella.
		System.out.println("\ntest toColumn con colonna che non ha alias nella colonna ma possiede un alias nella tabella.\n");
		String columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, true);
		System.out.println("- test 1: "+columnName);
		if("soggFruitori.tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, false);
		System.out.println("- test 2: "+columnName);
		if("tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_FRUITORE.TIPO, true);
		System.out.println("- test 3: "+columnName);
		if("soggFruitori.tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_FRUITORE.TIPO, false);
		System.out.println("- test 4: "+columnName);
		if("tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, false, true);
		System.out.println("- test 5: "+columnName);
		if("soggFruitori.tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, false, false);
		System.out.println("- test 6: "+columnName);
		if("tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, true, true);
		System.out.println("- test 7: "+columnName);
		if("soggFruitori.tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.TIPO, true, false);
		System.out.println("- test 8: "+columnName);
		if("tipo_soggetto".equals(columnName)==false){
			throw new ExpressionException("'tipo_soggetto' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		
		
		// test toColumn con colonna che non ha alias nella colonna ne nella tabella.
		System.out.println("\ntest toColumn con colonna che non ha alias nella colonna ne nella tabella.\n");
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, true);
		System.out.println("- test 1b: "+columnName);
		if("servizi.tipo".equals(columnName)==false){
			throw new ExpressionException("'servizi.tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, false);
		System.out.println("- test 2b: "+columnName);
		if("tipo".equals(columnName)==false){
			throw new ExpressionException("'tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, true);
		System.out.println("- test 3b: "+columnName);
		if("servizi.tipo".equals(columnName)==false){
			throw new ExpressionException("'servizi.tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, false);
		System.out.println("- test 4b: "+columnName);
		if("tipo".equals(columnName)==false){
			throw new ExpressionException("'tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, false, true);
		System.out.println("- test 5b: "+columnName);
		if("servizi.tipo".equals(columnName)==false){
			throw new ExpressionException("'servizi.tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, false, false);
		System.out.println("- test 6b: "+columnName);
		if("tipo".equals(columnName)==false){
			throw new ExpressionException("'tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, true, true);
		System.out.println("- test 7b: "+columnName);
		if("servizi.tipo".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, true, false);
		System.out.println("- test 8b: "+columnName);
		if("tipo".equals(columnName)==false){
			throw new ExpressionException("'tipo' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		
		
		
		
		// test toColumn con colonna ha alias  sia nella colonna che nella tabella.
		System.out.println("\ntest toColumn con colonna ha alias  sia nella colonna che nella tabella.\n");
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, true);
		System.out.println("- test 1c: "+columnName);
		if("soggFruitori.nome_soggetto nomSog".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.nome_soggetto nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, false);
		System.out.println("- test 2c: "+columnName);
		if("nome_soggetto nomSog".equals(columnName)==false){
			throw new ExpressionException("'nome_soggetto nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_FRUITORE.NOME, true);
		System.out.println("- test 3c: "+columnName);
		if("soggFruitori.nomSog".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ID_FRUITORE.NOME, false);
		System.out.println("- test 4c: "+columnName);
		if("nomSog".equals(columnName)==false){
			throw new ExpressionException("'nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, false, true);
		System.out.println("- test 5c: "+columnName);
		if("soggFruitori.nome_soggetto nomSog".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.nome_soggetto nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, false, false);
		System.out.println("- test 6c: "+columnName);
		if("nome_soggetto nomSog".equals(columnName)==false){
			throw new ExpressionException("'nome_soggetto nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, true, true);
		System.out.println("- test 7c: "+columnName);
		if("soggFruitori.nomSog".equals(columnName)==false){
			throw new ExpressionException("'soggFruitori.nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ID_FRUITORE.NOME, true, false);
		System.out.println("- test 8c: "+columnName);
		if("nomSog".equals(columnName)==false){
			throw new ExpressionException("'nomSog' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		
		
		
		
		// test toColumn con colonna che ha alias nella colonna ma non nella tabella.
		System.out.println("\ntest toColumn con colonna che ha alias nella colonna ma non nella tabella.\n");
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, true);
		System.out.println("- test 1d: "+columnName);
		if("servizi_fruitori.ora_registrazione oraReg".equals(columnName)==false){
			throw new ExpressionException("'servizi_fruitori.ora_registrazione oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, false);
		System.out.println("- test 2d: "+columnName);
		if("ora_registrazione oraReg".equals(columnName)==false){
			throw new ExpressionException("'ora_registrazione oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ORA_REGISTRAZIONE, true);
		System.out.println("- test 3d: "+columnName);
		if("servizi_fruitori.oraReg".equals(columnName)==false){
			throw new ExpressionException("'servizi_fruitori.oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toAliasColumn(Fruitore.model().ORA_REGISTRAZIONE, false);
		System.out.println("- test 4d: "+columnName);
		if("oraReg".equals(columnName)==false){
			throw new ExpressionException("'oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, false, true);
		System.out.println("- test 5d: "+columnName);
		if("servizi_fruitori.ora_registrazione oraReg".equals(columnName)==false){
			throw new ExpressionException("'servizi_fruitori.ora_registrazione oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, false, false);
		System.out.println("- test 6d: "+columnName);
		if("ora_registrazione oraReg".equals(columnName)==false){
			throw new ExpressionException("'ora_registrazione oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, true, true);
		System.out.println("- test 7d: "+columnName);
		if("servizi_fruitori.oraReg".equals(columnName)==false){
			throw new ExpressionException("'servizi_fruitori.oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
		
		columnName =  fruitoreConverter.toColumn(Fruitore.model().ORA_REGISTRAZIONE, true, false);
		System.out.println("- test 8d: "+columnName);
		if("oraReg".equals(columnName)==false){
			throw new ExpressionException("'oraReg' non trovato come atteso dal test, trovato invece ["+columnName+"]");
		}
	}
	
	
	public static void testSQLFieldConverterToModelModel(Fruitore fruitore,TestType testType) throws ExpressionNotImplementedException, ExpressionException{
		
		
		System.out.println("\n **************** testSQLFieldConverterToModelModel ************************* ");
		
		FruitoreSQLFieldConverter fruitoreConverter = new FruitoreSQLFieldConverter();
		
		
		// test con tabella che possiede un alias
		System.out.println("\ntest con tabella che possiede un alias.\n");
		String tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE);
		System.out.println("- test 1: "+tableName);
		if("soggetti as soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggetti as soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toAliasTable(Fruitore.model().ID_FRUITORE);
		System.out.println("- test 2: "+tableName);
		if("soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE,false);
		System.out.println("- test 3: "+tableName);
		if("soggetti as soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggetti as soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE,true);
		System.out.println("- test 4: "+tableName);
		if("soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		
		// test con tabella che non possiede un alias
		System.out.println("\ntest con tabella che non possiede un alias.\n");
		tableName =  fruitoreConverter.toTable(Fruitore.model());
		System.out.println("- test 1b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toAliasTable(Fruitore.model());
		System.out.println("- test 2b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model(),false);
		System.out.println("- test 3b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model(),true);
		System.out.println("- test 4b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}

	}
	
	
	public static void testSQLFieldConverterToModelField(Fruitore fruitore,TestType testType) throws ExpressionNotImplementedException, ExpressionException{
		
		
		System.out.println("\n **************** testSQLFieldConverterToModelField ************************* ");
		
		FruitoreSQLFieldConverter fruitoreConverter = new FruitoreSQLFieldConverter();
		
		
		// test con tabella che possiede un alias
		System.out.println("\ntest con tabella che possiede un alias.\n");
		String tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE.TIPO);
		System.out.println("- test 1: "+tableName);
		if("soggetti as soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggetti as soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toAliasTable(Fruitore.model().ID_FRUITORE.TIPO);
		System.out.println("- test 2: "+tableName);
		if("soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE.TIPO,false);
		System.out.println("- test 3: "+tableName);
		if("soggetti as soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggetti as soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ID_FRUITORE.TIPO,true);
		System.out.println("- test 4: "+tableName);
		if("soggFruitori".equals(tableName)==false){
			throw new ExpressionException("'soggFruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		
		// test con tabella che non possiede un alias
		System.out.println("\ntest con tabella che non possiede un alias.\n");
		tableName =  fruitoreConverter.toTable(Fruitore.model().ORA_REGISTRAZIONE);
		System.out.println("- test 1b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toAliasTable(Fruitore.model().ORA_REGISTRAZIONE);
		System.out.println("- test 2b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ORA_REGISTRAZIONE,false);
		System.out.println("- test 3b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}
		
		tableName =  fruitoreConverter.toTable(Fruitore.model().ORA_REGISTRAZIONE,true);
		System.out.println("- test 4b: "+tableName);
		if("servizi_fruitori".equals(tableName)==false){
			throw new ExpressionException("'servizi_fruitori' non trovato come atteso dal test, trovato invece ["+tableName+"]");
		}

	}
}
