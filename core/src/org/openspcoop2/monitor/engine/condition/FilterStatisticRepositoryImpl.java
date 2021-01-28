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

import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaGiornalieraFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaMensileFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaOrariaFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaSettimanaleFieldConverter;
import org.openspcoop2.core.statistiche.model.StatisticaContenutiModel;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.core.plugins.utils.FilterUtils;
import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;
import org.openspcoop2.utils.TipiDatabase;

/**
 * FilterStatisticRepositoryImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FilterStatisticRepositoryImpl extends FilterImpl {	

	private StatisticType statisticType;
	@SuppressWarnings("unused")
	private StatisticaModel model;
	private StatisticaContenutiModel contenutiModel;
	
	public FilterStatisticRepositoryImpl(TipiDatabase databaseType,StatisticType statisticType) throws SearchException {
		super(newExpression(databaseType,statisticType),databaseType,newFieldConverter(databaseType,statisticType));
		this.statisticType = statisticType;
		switch (this.statisticType) {
		case ORARIA:
			this.model = StatisticaOraria.model().STATISTICA_BASE;
			this.contenutiModel = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
			break;
		case GIORNALIERA:
			this.model = StatisticaGiornaliera.model().STATISTICA_BASE;
			this.contenutiModel = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
			break;
		case SETTIMANALE:
			this.model = StatisticaSettimanale.model().STATISTICA_BASE;
			this.contenutiModel = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
			break;
		case MENSILE:
			this.model = StatisticaMensile.model().STATISTICA_BASE;
			this.contenutiModel = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
			break;
		}
	}
	
	
	
	
	private static IExpression newExpression(TipiDatabase databaseType, StatisticType statisticType) throws SearchException{
		try{
			return new JDBCExpression(newFieldConverter(databaseType,statisticType));
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	private static ISQLFieldConverter newFieldConverter(TipiDatabase databaseType, StatisticType statisticType) throws SearchException{
		switch (statisticType) {
		case ORARIA:
			return new StatisticaOrariaFieldConverter(databaseType);
		case GIORNALIERA:
			return new StatisticaGiornalieraFieldConverter(databaseType);
		case SETTIMANALE:
			return new StatisticaSettimanaleFieldConverter(databaseType);
		case MENSILE:
			return new StatisticaMensileFieldConverter(databaseType);
		}
		throw new SearchException("StatisticType["+statisticType+"] unknown");
	}

	
	@Override
	protected IStatisticFilter newIFilter() throws SearchException{
		try{
			return new FilterStatisticRepositoryImpl(this.databaseType,this.statisticType);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IExpression newIExpression() throws SearchException{
		try{
			return newExpression(this.databaseType,this.statisticType);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IField getIFieldForMessageType() throws SearchException{
		// tipizzazione non supportata nella statistica
		return null;
	}
	
	@Override
	protected List<IField> getIFieldForResourceName(StatisticFilterName statisticFilter) throws SearchException{
		try{
			List<IField> l = new ArrayList<IField>();
			if(statisticFilter==null){
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_1, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_2, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_3, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_4, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_5, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_6, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_7, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_8, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_9, FilterUtils.getNextAliasStatisticsTable()));
				l.add(new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_NOME_10, FilterUtils.getNextAliasStatisticsTable()));
			}
			else{
				switch (statisticFilter) {
				case FILTER_1:
					l.add(this.contenutiModel.FILTRO_NOME_1);
					break;
				case FILTER_2:
					l.add(this.contenutiModel.FILTRO_NOME_2);
					break;
				case FILTER_3:
					l.add(this.contenutiModel.FILTRO_NOME_3);
					break;
				case FILTER_4:
					l.add(this.contenutiModel.FILTRO_NOME_4);
					break;
				case FILTER_5:
					l.add(this.contenutiModel.FILTRO_NOME_5);
					break;
				case FILTER_6:
					l.add(this.contenutiModel.FILTRO_NOME_6);
					break;
				case FILTER_7:
					l.add(this.contenutiModel.FILTRO_NOME_7);
					break;
				case FILTER_8:
					l.add(this.contenutiModel.FILTRO_NOME_8);
					break;
				case FILTER_9:
					l.add(this.contenutiModel.FILTRO_NOME_9);
					break;
				case FILTER_10:
					l.add(this.contenutiModel.FILTRO_NOME_10);
					break;
				}
			}
			return l;
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	
	@Override
	protected IField getIFieldForResourceValue(IField fieldResourceName) throws SearchException{
		try{
			String aliasTable = null;
			if( fieldResourceName instanceof IAliasTableField ){
				IAliasTableField af = (IAliasTableField) fieldResourceName;
				aliasTable = af.getAliasTable();
			}
			
			if(this.contenutiModel.FILTRO_NOME_1.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_1, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_1;
			}
			else if(this.contenutiModel.FILTRO_NOME_2.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_2, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_2;
			}
			else if(this.contenutiModel.FILTRO_NOME_3.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_3, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_3;
			}
			else if(this.contenutiModel.FILTRO_NOME_4.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_4, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_4;
			}
			else if(this.contenutiModel.FILTRO_NOME_5.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_5, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_5;
			}
			else if(this.contenutiModel.FILTRO_NOME_6.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_6, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_6;
			}
			else if(this.contenutiModel.FILTRO_NOME_7.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_7, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_7;
			}
			else if(this.contenutiModel.FILTRO_NOME_8.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_8, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_8;
			}
			else if(this.contenutiModel.FILTRO_NOME_9.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_9, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_9;
			}
			else if(this.contenutiModel.FILTRO_NOME_10.equals(fieldResourceName)){
				if(aliasTable!=null)
					return new AliasTableComplexField((ComplexField)this.contenutiModel.FILTRO_VALORE_10, aliasTable);
				else
					return this.contenutiModel.FILTRO_VALORE_10;
			}
			throw new Exception("Unknown field: "+fieldResourceName);
		}catch(Exception e){
			throw new SearchException(e.getMessage(),e);
		}
	}
	

}
