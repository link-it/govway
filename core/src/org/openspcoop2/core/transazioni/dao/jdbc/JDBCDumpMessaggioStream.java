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

package org.openspcoop2.core.transazioni.dao.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * JDBCDumpMessaggioStream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCDumpMessaggioStream {
	
	public JDBCDumpMessaggioStream(InputStream is,
			ResultSet rs, PreparedStatement pstmt, 
			Connection con, JDBCServiceManager jdbcServiceManager) {
		this.is = is;
		this.rs = rs;
		this.pstmt = pstmt;
		this.con = con;
		this.jdbcServiceManager = jdbcServiceManager;
	}

	private InputStream is;

	private ResultSet rs;
	private PreparedStatement pstmt;
	private Connection con;
	private JDBCServiceManager jdbcServiceManager;

	public InputStream getIs() {
		return this.is;
	}
	
	public void closeJdbcResources() {
		try {
    		if(this.rs!=null) {
    			this.rs.close();
    		}
    	}catch(Exception eClose) {
    		// ignore
    	}
    	try {
    		if(this.pstmt!=null) {
    			this.pstmt.close();
    		}
    	}catch(Exception eClose) {
    		// ignore
    	}
    	try {
    		if(this.con!=null && this.jdbcServiceManager!=null){
				this.jdbcServiceManager.closeConnection(this.con);
			}
    	}catch(Exception eClose) {
    		// ignore
    	}
	}
	
	
}
