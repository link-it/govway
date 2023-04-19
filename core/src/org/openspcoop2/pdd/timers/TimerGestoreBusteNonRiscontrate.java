/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.timers;

import java.rmi.RemoteException;



/**
 * Interfaccia del Gestore del timer di servizio di OpenSPCoop.
 * Sostituisce il Gestore dei threads di servizio.
 *
 *
 * @author Marcello Spadafora (ma.spadafora@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public interface TimerGestoreBusteNonRiscontrate extends javax.ejb.EJBObject {	

    /** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
    public static final String ID_MODULO = "GestoreBusteNonRiscontrate";

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

