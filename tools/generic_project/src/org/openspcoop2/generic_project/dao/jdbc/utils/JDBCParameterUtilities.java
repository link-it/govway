/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.generic_project.dao.jdbc.utils;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBCParameterUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCParameterUtilities extends org.openspcoop2.utils.jdbc.JDBCParameterUtilities {

	public JDBCParameterUtilities(TipiDatabase tipoDatabaseOpenSPCoop2) throws SQLQueryObjectException, JDBCAdapterException{
		super(tipoDatabaseOpenSPCoop2);
	}
	
	
	public void setParameters(PreparedStatement pstmt,JDBCObject ... params) throws SQLException, JDBCAdapterException, UtilsException{
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				setParameter(pstmt, (i+1), params[i]);
			}
		}
	}
	
	public void setParameter(PreparedStatement pstmt,int index,JDBCObject param) throws SQLException, JDBCAdapterException, UtilsException{
		
		Object value = param.getObject();
		Class<?> type = param.getTypeObject();
		
		//System.out.println("SET PARAMETER VALUE["+value+"] TYPE["+type.getName()+"]");
		
		if( (value!=null) && (value instanceof IEnumeration) ){
			IEnumeration enumObject = (IEnumeration) value;
			Object valueEnum = enumObject.getValue();
			Class<?> cValueEnum = null;
			if(valueEnum!=null){
				cValueEnum = valueEnum.getClass();
			}
			JDBCObject jdbcObject = new JDBCObject(valueEnum, cValueEnum);
			setParameter(pstmt,index, jdbcObject); // ricorsione sul tipo semplice
		}
		
		else {
			
			super.setParameter(pstmt, index, value, type);
			
		}
				
	}
	
}
