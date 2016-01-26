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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * Implementazione dell'interfaccia JDBCAdapter 
 * che definisce un adapter JDBC per la gestione del repository del messaggi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class StreamJDBCAdapter extends AbstractJDBCAdapter {

	
    protected StreamJDBCAdapter(TipiDatabase tipoDatabase) {
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
	public byte[] getBinaryData(ResultSet rs, int index) throws UtilsException,SQLException{
    	return readIs(rs.getBinaryStream(index),index+""); 
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
    	return readIs(rs.getBinaryStream(rsName),rsName);  	
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
    	ByteArrayInputStream bin = null;
    	try{
    		if(data!=null)
    			bin = new ByteArrayInputStream(data);
    		else{
    			bin = new ByteArrayInputStream("".getBytes());
    		}
    	}catch (Exception e) {
    		throw new UtilsException("StreamJDBCAppender error, set binary parameter [indice:"+index+"]"+e.getMessage());
    	}	
    	s.setBinaryStream(index, bin , data.length);
    }
    
    
  
    
	
	
	
	
	/* ***** UTILITIES ****** */
	
	private byte[] readIs(InputStream is,String indice) throws UtilsException{
		ByteArrayOutputStream os = null;
    	try {
    		os = new ByteArrayOutputStream();
			byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
			int readByte = 0;
			while((readByte = is.read(readB))!= -1){
				os.write(readB,0,readByte);
			}
			is.close();
			
			byte[]dati = os.toByteArray();
			os.close();

    		return dati;
    	} catch (Exception e) {
    		try{
    			if(is!=null)
    				is.close();
    		}catch(Exception io){}
    		try{
    			if(os!=null)
    				os.close();
    		}catch(Exception io){}
    		throw new UtilsException("StreamJDBCAppender error, reading binary parameter [indice:"+indice+"]"+e.getMessage());
    	}
	}
    
}





