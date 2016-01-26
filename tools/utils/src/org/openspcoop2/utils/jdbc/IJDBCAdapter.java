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

import org.openspcoop2.utils.UtilsException;

/**
 * Interfaccia che definisce un adapter JDBC per la gestione del repository del messaggi
 * Esistono attualmente due implementazioni OpenSPCoop di questa interfaccia :
 * <ul>
 * <li> {@link StreamJDBCAdapter},  binaryStream per set/get.
 * <li> {@link BytesJDBCAdapter},  byte[] per set/get.
 * <li> {@link BlobJDBCAdapter},  Blob per set/get
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IJDBCAdapter {
    
	
	/* ***** BYTES ****** */
	
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param index Indice su cui prelevare il messaggio
     * 
     */
    public byte[] getBinaryData(ResultSet rs, int index) throws UtilsException,SQLException;
    
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param rsName Nome rs su cui prelevare il messaggio
     * 
     */
    public byte[] getBinaryData(ResultSet rs, String rsName) throws UtilsException,SQLException;
    
    /**
     * Si occupa di registrare il messaggio sul DB,
     * all'indice <var>index</var>.
     *
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param data Messaggio 
     * 
     */
    public void setBinaryData(PreparedStatement s, int index, byte[] data) throws UtilsException,SQLException;
    
    
    
    /* ***** INPUT STREAM ****** */
   
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param index Indice su cui prelevare il messaggio
     * 
     */
    public InputStream getBinaryStream(ResultSet rs, int index) throws UtilsException,SQLException;
    
    /**
     * Si occupa di ottenere il messaggio precedentemente salvato sul DB,
     * effettuando una get all'indice <var>index</var>.
     *
     * @param rs ResultSet da utilizzare.
     * @param rsName Nome rs su cui prelevare il messaggio
     * 
     */
    public InputStream getBinaryStream(ResultSet rs, String rsName) throws UtilsException,SQLException;
    
    /**
     * Si occupa di registrare il messaggio sul DB, all'indice <var>index</var>.
     * 
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param is InputStream da cui leggere i bytes del messaggio da salvare
     * @param length Lunghezza del Messaggio
     */
    public void setBinaryData(PreparedStatement s, int index, InputStream is, int length) throws UtilsException,SQLException;
    
    /**
     * Si occupa di registrare il messaggio sul DB, all'indice <var>index</var>.
     * 
     * @param s PreparedStatement da utilizzare utilizzare.
     * @param index Indice su cui registrare il messaggio
     * @param is InputStream da cui leggere i bytes del messaggio da salvare
     * @param bufferingIfNotEnabled Non tutti i database supportano il salvataggio via streaming senza fornire comunque la lunghezza del messaggio. Il parametro indica se deve essere attivato un buffer (true) o se deve essere generata una eccezione 
     */
    public void setBinaryData(PreparedStatement s, int index, InputStream is, boolean bufferingIfNotEnabled) throws UtilsException,SQLException,BinaryStreamNotSupportedException;
}





