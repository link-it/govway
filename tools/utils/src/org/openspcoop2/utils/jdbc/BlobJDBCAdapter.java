/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.utils.jdbc;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;

/**
 * Implementazione dell'interfaccia JDBCAdapter 
 * che definisce un adapter JDBC per la gestione del repository del messaggi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class BlobJDBCAdapter extends AbstractJDBCAdapter {

	
	public BlobJDBCAdapter(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}

    
    
	/* ***** BYTES ****** */

	/**
	 * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
	 * effettuando una get all'indice <var>index</var>.
	 *
	 * @param rs ResultSet da utilizzare.
	 * @param index Indice su cui prelevare il messaggio
	 * 
	 */
	@Override
	public byte[] getBinaryData(ResultSet rs, int index) throws SQLException{
		// Get as a BLOB
		Blob aBlob = rs.getBlob(index);
		if (aBlob == null) {
			return null;
		}
		return aBlob.getBytes(1, (int) aBlob.length());
	}

	/**
	 * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
	 * effettuando una get all'indice <var>index</var>.
	 *
	 * @param rs ResultSet da utilizzare.
	 * @param rsName Nome rs su cui prelevare il messaggio
	 * 
	 */
	@Override
	public byte[] getBinaryData(ResultSet rs, String rsName) throws UtilsException,SQLException{
		// Get as a BLOB
		Blob aBlob = rs.getBlob(rsName);
		if (aBlob == null) {
			return null;
		}
		return aBlob.getBytes(1, (int) aBlob.length());
	}


	
	
	
    /* ***** INPUT STREAM ****** */
    
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param index Indice su cui prelevare il messaggio
     * 
     */
    @Override
	public InputStream getBinaryStream(ResultSet rs, int index) throws UtilsException,SQLException{
    	// Get as a BLOB
		Blob aBlob = rs.getBlob(index);
		if (aBlob == null) {
			return null;
		}
		return aBlob.getBinaryStream();
    }
    
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param rsName Nome rs su cui prelevare il messaggio
     * 
     */
    @Override
	public InputStream getBinaryStream(ResultSet rs, String rsName) throws UtilsException,SQLException{
    	// Get as a BLOB
		Blob aBlob = rs.getBlob(rsName);
		if (aBlob == null) {
			return null;
		}
		return aBlob.getBinaryStream();
    }

}





