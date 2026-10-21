/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * AbstractSQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSQLFieldConverter implements ISQLFieldConverter {

	
	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		if(field instanceof CustomField){
			CustomField cf = (CustomField) field;
			
			if(appendTablePrefix){
				String table =  this.toAliasTable(field);
				if(table!=null && !"".equals(table)){
					if(returnAlias){
						return table+"."+cf.getAliasColumnName();
					}else{
						return table+"."+cf.getColumnName();
					}
				}
				else{
					if(returnAlias){
						return cf.getAliasColumnName();
					}else{
						return cf.getColumnName();
					}
				}
			}else{
				if(returnAlias){
					return cf.getAliasColumnName();
				}else{
					return cf.getColumnName();
				}
			}
		}
		
		else if(field instanceof ConstantField){
			ConstantField cf = (ConstantField) field;
			if(returnAlias){
				return cf.getAlias();
			}
			else{
				return cf.getConstantValue(this.getDatabaseType());
			}
		}
		
		else if(field instanceof AliasField){
			AliasField af = (AliasField) field;
/**			if(returnAlias){
//				return af.getAlias();
//			}
//			else{
//				return this.toColumn(af.getField(), returnAlias, appendTablePrefix); 
//			}*/
			return af.getAlias(); // un alias deve usare sempre l'alias
		}
		
		else if(field instanceof IAliasTableField){
			
			IAliasTableField atf = (IAliasTableField) field;
			if(appendTablePrefix){
				return atf.getAliasTable()+"."+this.toColumn(atf.getField(), returnAlias, false);
			}
			else{
				return this.toColumn(atf.getField(), returnAlias, false);
			}
			
		}
		
		else{
			
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toColumn: "+this.getClass().getName());
			
		}
		
	}
	
	@Override
	public String toColumn(IField field,boolean appendTablePrefix) throws ExpressionException{
		return this.toColumnWithCache(field, false, appendTablePrefix);
	}

	@Override
	public String toAliasColumn(IField field,boolean appendTablePrefix) throws ExpressionException{
		return this.toColumnWithCache(field, true, appendTablePrefix);
	}


	/*
	 * Cache dei nomi delle colonne dei field del modello.
	 * I converter generati risolvono un field tramite una sequenza di confronti con tutti i field del modello (toColumn con 3 parametri):
	 * il risultato dipende solamente dalla classe del converter, dal tipo di database, dal field e dai due parametri, ed è quindi mantenuto in cache.
	 * Vengono mantenuti solamente i field del modello (classi Field e ComplexField), confrontati per identità: i field creati dinamicamente
	 * (CustomField, ConstantField, AliasField, IAliasTableField) continuano ad essere risolti ad ogni invocazione.
	 * Il numero di elementi è limitato: superata la soglia, i nuovi field vengono risolti senza essere aggiunti alla cache.
	 * Le eccezioni non vengono mantenute in cache.
	 */
	private static final int TO_COLUMN_CACHE_MAX_ENTRIES = 20000; // circa 4.800 field nei modelli, per 4 combinazioni dei parametri
	private static final Map<ToColumnCacheKey, String> TO_COLUMN_CACHE = new ConcurrentHashMap<>();

	private static final class ToColumnCacheKey {
		private final Class<?> converterClass;
		private final TipiDatabase databaseType;
		private final IField field;
		private final boolean returnAlias;
		private final boolean appendTablePrefix;
		private final int hash;
		private ToColumnCacheKey(Class<?> converterClass, TipiDatabase databaseType, IField field, boolean returnAlias, boolean appendTablePrefix) {
			this.converterClass = converterClass;
			this.databaseType = databaseType;
			this.field = field;
			this.returnAlias = returnAlias;
			this.appendTablePrefix = appendTablePrefix;
			this.hash = Objects.hash(converterClass, databaseType, System.identityHashCode(field), returnAlias, appendTablePrefix);
		}
		@Override
		public int hashCode() {
			return this.hash;
		}
		@Override
		public boolean equals(Object o) {
			if(this==o) {
				return true;
			}
			if(!(o instanceof ToColumnCacheKey)) {
				return false;
			}
			ToColumnCacheKey k = (ToColumnCacheKey) o;
			return this.field==k.field && // identità del field
					this.converterClass==k.converterClass &&
					this.databaseType==k.databaseType &&
					this.returnAlias==k.returnAlias &&
					this.appendTablePrefix==k.appendTablePrefix;
		}
	}

	private static boolean isToColumnCacheable(IField field) {
		return field!=null && (field.getClass()==Field.class || field.getClass()==ComplexField.class);
	}

	private String toColumnWithCache(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException{
		if(!isToColumnCacheable(field)) {
			return this.toColumn(field, returnAlias, appendTablePrefix);
		}
		ToColumnCacheKey key = new ToColumnCacheKey(this.getClass(), this.getDatabaseType(), field, returnAlias, appendTablePrefix);
		String column = TO_COLUMN_CACHE.get(key);
		if(column==null) {
			column = this.toColumn(field, returnAlias, appendTablePrefix);
			if(column!=null && TO_COLUMN_CACHE.size()<TO_COLUMN_CACHE_MAX_ENTRIES) {
				TO_COLUMN_CACHE.put(key, column);
			}
		}
		return column;
	}
	
	
	
	
	
	
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		if(field instanceof CustomField){
			CustomField cf = (CustomField) field;
			if(returnAlias){
				return cf.getAliasTableName();
			}else{
				return cf.getTableName();
			}
		}

		else if(field instanceof ConstantField){
			return this.toTable(this.getRootModel(), returnAlias);
		}
		
		else if(field instanceof AliasField){
			AliasField af = (AliasField) field;
			return this.toTable(af.getField(), returnAlias);
		}
		
		else if(field instanceof IAliasTableField){
			IAliasTableField af = (IAliasTableField) field;
			if(returnAlias){
				return af.getAliasTable();
			}
			else{
				return this.toTable(af.getField());
			}
		}
		
		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
		}
	}
	
	@Override
	public String toTable(IField field) throws ExpressionException {
		return this.toTable(field, false);
	}

	@Override
	public String toAliasTable(IField field) throws ExpressionException{
		return this.toTable(field, true);
	}
	
	
	
	
	
	
	
	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		throw new ExpressionException("Model ["+model.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
	}

	@Override
	public String toTable(IModel<?> model) throws ExpressionException {
		return this.toTable(model, false);
	}

	@Override
	public String toAliasTable(IModel<?> model) throws ExpressionException{
		return this.toTable(model, true);
	}
	


	

	

}
