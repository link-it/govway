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
package org.openspcoop2.generic_project.expression.impl.sql;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * ISQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISQLFieldConverter {

	public IModel<?> getRootModel() throws ExpressionException;
	
	public TipiDatabase getDatabaseType() throws ExpressionException;
	
	
	public String toTable(IField field) throws ExpressionException;
	
	public String toAliasTable(IField field) throws ExpressionException;
	
	public String toTable(IField field,boolean returnAlias) throws ExpressionException;
	
	
	public String toTable(IModel<?> model) throws ExpressionException;
	
	public String toAliasTable(IModel<?> model) throws ExpressionException;
	
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException;
	
	
	public String toColumn(IField field,boolean appendTablePrefix) throws ExpressionException;
	
	public String toAliasColumn(IField field,boolean appendTablePrefix) throws ExpressionException;
	
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException;
	
}
