/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.generic_project.expression.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
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
public class LikeExpressionImpl extends AbstractBaseExpressionImpl {

	private IField field;
	private String value;
	private LikeMode mode;
	private boolean caseInsensitive;
	
	public LikeExpressionImpl(IObjectFormatter objectFormatter,IField field,String value,LikeMode mode,boolean caseInsensitive){
		super(objectFormatter);
		this.field = field;
		this.value = value;
		this.mode = mode;
		this.caseInsensitive = caseInsensitive;
	}
	
	public IField getField() {
		return this.field;
	}
	public void setField(IField field) {
		this.field = field;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
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
		StringBuffer bf = new StringBuffer();
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
			e.printStackTrace(System.err);
		}
		
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
		bf.append(likeParam);
		bf.append("'");
		bf.append(escapeClausole);
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	
	@Override
	public boolean inUseField(IField field)
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return false;
		}
		return this.field.equals(field);
	}

	@Override
	public List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException{
		if(this.field==null){
			return null;
		}
		if(this.field.equals(field)){
			List<Object> lista = new ArrayList<Object>();
			lista.add(this.value);
			return lista;
		}
		return null;
	}
	
	@Override
	public boolean inUseModel(IModel<?> model)
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return false;
		}
		if(model.getBaseField()!=null){
			// Modello di un elemento non radice
			if(this.field instanceof ComplexField){
				ComplexField c = (ComplexField) this.field;
				return c.getFather().equals(model.getBaseField());
			}else{
				return model.getModeledClass().getName().equals(this.field.getClassType().getName());
			}
		}
		else{
			return model.getModeledClass().getName().equals(this.field.getClassType().getName());
		}
	}
	
	
	
	
	@Override
	public List<IField> getFields()
			throws ExpressionNotImplementedException, ExpressionException {
		if(this.field==null){
			return null;
		}
		List<IField> lista = new ArrayList<IField>();
		lista.add(this.field);
		return lista;
	}
}
