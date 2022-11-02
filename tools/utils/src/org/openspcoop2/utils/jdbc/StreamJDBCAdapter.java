/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.utils.CopyStream;
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

public class StreamJDBCAdapter extends AbstractJDBCAdapter {

	
	public StreamJDBCAdapter(TipiDatabase tipoDatabase) {
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
    	
    	if(data!=null && data.length>0){
	    	ByteArrayInputStream bin = null;
	    	try{
    			bin = new ByteArrayInputStream(data);
	    	}catch (Exception e) {
	    		throw new UtilsException("StreamJDBCAppender error, set binary parameter [indice:"+index+"]"+e.getMessage());
	    	}	
	    	s.setBinaryStream(index, bin , data.length);
    	}
    	else{
    		s.setBinaryStream(index, null , 0);
    	}
    }
    
    
  
    
	
	
	
	
	/* ***** UTILITIES ****** */
	
	private byte[] readIs(InputStream is,String indice) throws UtilsException{
		
		if(is==null){
			return null;
		}
		
		ByteArrayOutputStream os = null;
    	try {
    		os = new ByteArrayOutputStream();
//			byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
//			int readByte = 0;
//			while((readByte = is.read(readB))!= -1){
//				os.write(readB,0,readByte);
//			}
    		CopyStream.copy(is, os);
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
    		}catch(Exception io){
    			// close
    		}
    		throw new UtilsException("StreamJDBCAppender error, reading binary parameter [indice:"+indice+"]"+e.getMessage(),e);
    	}
	}
    
}





