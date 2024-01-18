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
package org.openspcoop2.generic_project.expression.impl;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.EscapeSQLPattern;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * LikeExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LikeExpressionImpl extends AbstractCommonFieldValueBaseExpressionImpl {

	private LikeMode mode;
	private boolean caseInsensitive;
	
	public LikeExpressionImpl(IObjectFormatter objectFormatter,IField field,String value,LikeMode mode,boolean caseInsensitive){
		super(objectFormatter, field, value);
		this.mode = mode;
		this.caseInsensitive = caseInsensitive;
	}
	
	public LikeMode getMode() {
		return this.mode;
	}

	public void setMode(LikeMode mode) {
		this.mode = mode;
	}

	public boolean isCaseInsensitive() {
		return this.caseInsensitive;
	}

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		if(this.caseInsensitive){
			bf.append("lower(");
		}
		if(this.field instanceof ComplexField){
			ComplexField cf = (ComplexField) this.field;
			if(cf.getFather()!=null){
				bf.append(cf.getFather().getFieldName());
			}else{
				bf.append(this.field.getClassName());
			}
		}else{
			bf.append(this.field.getClassName());
		}
		bf.append(".");
		bf.append(this.field.getFieldName());
		if(this.caseInsensitive){
			bf.append(")");
		}
		bf.append(" like '");
		
		String likeValue = this.value;
		
		// escape
		String escapeClausole = "";
		try{
			ISQLQueryObject sqlQueryObjectForEscape = SQLObjectFactory.createSQLQueryObject(TipiDatabase.POSTGRESQL); // lo uso come default per produrre la stringa
			EscapeSQLPattern escapePattern = sqlQueryObjectForEscape.escapePatternValue(likeValue);
			if(escapePattern.isUseEscapeClausole()){
				escapeClausole = " ESCAPE '"+escapePattern.getEscapeClausole()+"'";
			}
			likeValue = escapePattern.getEscapeValue();
		}catch(Exception e){
			LoggerWrapperFactory.getLogger(LikeExpressionImpl.class).error(e.getMessage(),e);
		}
		
		String likeParam = getLikeParam(likeValue);
		bf.append(likeParam);
		bf.append("'");
		bf.append(escapeClausole);
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	
	private String getLikeParam(String likeValue) {
		String likeParam = null;
		switch (this.mode) {
		case EXACT:
			if(this.caseInsensitive){
				likeParam = likeValue.toLowerCase();
			}else{
				likeParam = likeValue;
			}
			break;
		case ANYWHERE:
			if(this.caseInsensitive){
				likeParam = "%"+likeValue.toLowerCase()+"%";
			}else{
				likeParam = "%"+likeValue+"%";
			}
			break;
		case END:
			if(this.caseInsensitive){
				likeParam = "%"+likeValue.toLowerCase();
			}else{
				likeParam = "%"+likeValue;
			}
			break;
		case START:
			if(this.caseInsensitive){
				likeParam = likeValue.toLowerCase()+"%";
			}else{
				likeParam = likeValue+"%";
			}
			break;
		default:
			break;
		}
		return likeParam;
	}

	

}
