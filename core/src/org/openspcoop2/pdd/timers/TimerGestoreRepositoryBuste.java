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



package org.openspcoop2.pdd.timers;

import java.rmi.RemoteException;


/**
 * Interfaccia del thread che si occupa di ripulire il repository.
 * 
 * 
 * @author Poli Andrea
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface TimerGestoreRepositoryBuste extends javax.ejb.EJBObject {

    /* ********  F I E L D S  P R I V A T I   S T A T I C I  ******** */
    
    /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public final static String ID_MODULO = "GestoreRepositoryBuste";

    /**
     * Inizializza il Timer di gestione 
     *
     * 
     */
    boolean start() throws RemoteException;
    
    /**
     * Restituisce lo stato del timer di gestione
     *
     * 
     */
    boolean isStarted() throws RemoteException;
    
    /**
     * Ferma il Timer di gestione
     *
     * 
     */
    void stop() throws RemoteException;

}
