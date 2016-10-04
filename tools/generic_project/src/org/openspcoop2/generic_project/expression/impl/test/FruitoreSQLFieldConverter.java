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
package org.openspcoop2.generic_project.expression.impl.test;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.generic_project.expression.impl.test.beans.Fruitore;
import org.openspcoop2.utils.TipiDatabase;

/**
 * FruitoreSQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruitoreSQLFieldConverter extends AbstractSQLFieldConverter {

	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Fruitore.model();
	}

	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return TipiDatabase.DEFAULT;
	}
	
	
	
	
	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		if(field.equals(Fruitore.model().ID_FRUITORE.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(Fruitore.model().ID_FRUITORE.NOME)){
			if(appendTablePrefix){
				if(returnAlias){
					return this.toAliasTable(field)+".nomSog";
				}else{
					return this.toAliasTable(field)+".nome_soggetto nomSog";
				}
			}else{
				if(returnAlias){
					return "nomSog";
				}else{
					return "nome_soggetto nomSog";
				}
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(Fruitore.model().ORA_REGISTRAZIONE)){
			if(appendTablePrefix){
				if(returnAlias){
					return this.toAliasTable(field)+".oraReg";
				}else{
					return this.toAliasTable(field)+".ora_registrazione oraReg";
				}
			}else{
				if(returnAlias){
					return "oraReg";
				}else{
					return "ora_registrazione oraReg";
				}
			}
		}


		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toColumn: "+this.getClass().getName());
		}
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		if(field.equals(Fruitore.model().ID_FRUITORE.TIPO)){
			if(returnAlias){
				return "soggFruitori";
			}else{
				return "soggetti as soggFruitori";
			}
		}
		if(field.equals(Fruitore.model().ID_FRUITORE.NOME)){
			if(returnAlias){
				return "soggFruitori";
			}else{
				return "soggetti as soggFruitori";
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO)){
			return "servizi";
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME)){
			return "servizi";
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO)){
			if(returnAlias){
				return "soggErogatori";
			}else{
				return "soggetti as soggErogatori";
			}
		}
		if(field.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME)){
			if(returnAlias){
				return "soggErogatori";
			}else{
				return "soggetti as soggErogatori";
			}
		}
		if(field.equals(Fruitore.model().ORA_REGISTRAZIONE)){
			return "servizi_fruitori";
		}


		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
		}
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		if(model.equals(Fruitore.model())){
			return "servizi_fruitori";
		}
		if(model.equals(Fruitore.model().ID_FRUITORE)){
			if(returnAlias){
				return "soggFruitori";
			}else{
				return "soggetti as soggFruitori";
			}
		}
		if(model.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA)){
			return "servizi";
		}
		if(model.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE)){
			if(returnAlias){
				return "soggErogatori";
			}else{
				return "soggetti as soggErogatori";
			}
		}


		else{
			throw new ExpressionException("Model ["+model.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
		}
		
	}

}
