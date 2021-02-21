/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.condition;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.core.plugins.utils.FilterUtils;
import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;
import org.openspcoop2.utils.TipiDatabase;

/**
 * FilterTransactionRepositoryImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FilterTransactionRepositoryImpl extends FilterImpl {	

	public FilterTransactionRepositoryImpl(TipiDatabase databaseType) throws SearchException {
		super(newExpression(databaseType),databaseType,newFieldConverter(databaseType));
	}
	
	
	
	
	private static IExpression newExpression(TipiDatabase databaseType) throws SearchException{
		try{
			return new JDBCExpression(newFieldConverter(databaseType));
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	private static ISQLFieldConverter newFieldConverter(TipiDatabase databaseType){
		return new TransazioneFieldConverter(databaseType);
	}

	
	@Override
	protected IStatisticFilter newIFilter() throws SearchException{
		try{
			return new FilterTransactionRepositoryImpl(this.databaseType);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IExpression newIExpression() throws SearchException{
		try{
			return newExpression(this.databaseType);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IField getIFieldForMessageType() throws SearchException{
		return Transazione.model().DUMP_MESSAGGIO.TIPO_MESSAGGIO;
	}
	
	@Override
	protected List<IField> getIFieldForResourceName(StatisticFilterName statisticFilter) throws SearchException{
		try{
			List<IField> l = new ArrayList<IField>();
			l.add(new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME, FilterUtils.getNextAliasTransactionTable()));
			return l;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IField getIFieldForResourceValue(IField fieldResourceName) throws SearchException{
		try{
			if( !(fieldResourceName instanceof IAliasTableField)){
				throw new Exception("Unknown parameter type: "+fieldResourceName.getClass().getName());
			}
			IAliasTableField af = (IAliasTableField) fieldResourceName;
			String aliasTable = af.getAliasTable();
			return new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, aliasTable);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	
}
