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
package org.openspcoop2.utils.jdbc;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * AbstractJDBCAdapter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractJDBCAdapter implements IJDBCAdapter {

	private TipiDatabase tipoDatabase;
	
	public AbstractJDBCAdapter(TipiDatabase tipoDatabase){
		this.tipoDatabase = tipoDatabase;
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
	public byte[] getBinaryData(ResultSet rs, int index) throws UtilsException,SQLException{
    	return rs.getBytes(index); 
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
		return rs.getBytes(rsName); 
	}
	
    /**
     * Si occupa di registrare il messaggio sul DB,
     * all'indice <var>index</var>.
     *
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param data Messaggio 
     * 
     */
    @Override
	public void setBinaryData(PreparedStatement s, int index, byte[] data) throws UtilsException,SQLException{
    	s.setBytes(index, data);
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
    	return rs.getBinaryStream(index);
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
    	return rs.getBinaryStream(rsName);
    }
	
    /**
     * Si occupa di registrare il messaggio sul DB, all'indice <var>index</var>.
     * 
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param is InputStream da cui leggere i bytes del messaggio da salvare
     * @param length Lunghezza del Messaggio
     */
    @Override
	public void setBinaryData(PreparedStatement s, int index, InputStream is, int length) throws UtilsException,SQLException{
    	s.setBinaryStream(index, is, length);
    }
    
    /**
     * Si occupa di registrare il messaggio sul DB, all'indice <var>index</var>.
     * 
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param is InputStream da cui leggere i bytes del messaggio da salvare
     * @param bufferingIfNotEnabled Non tutti i database supportano il salvataggio via streaming senza fornire comunque la lunghezza del messaggio. Il parametro indica se deve essere attivato un buffer (true) o se deve essere generata una eccezione 
     */
    @Override
	public void setBinaryData(PreparedStatement s, int index, InputStream is, boolean bufferingIfNotEnabled) throws UtilsException,SQLException,BinaryStreamNotSupportedException{
    	try{
        	s.setBinaryStream(index, is); // This will not work with JDBC 3.0 drivers     	
    	}catch(Throwable e){
    		if(bufferingIfNotEnabled){
    			this.setBinaryData(s, index, Utilities.getAsByteArray(is));
    		}
    		else{
    			throw new BinaryStreamNotSupportedException("SetBinaryStream non supportata dal tipo di database utilizzato ("+this.tipoDatabase.toString()+") (Verifica di utilizzare un driver JDBC 4.x o superiore): "+e.getMessage(),e);
    		}
    	}
    }
}
