/*
 * OpenSPCoop - Customizable API Gateway 
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

/**
 * GestioneUtentiDocumentLiteral.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service;

/**
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public interface GestioneUtentiDocumentLiteral extends java.rmi.Remote {
    public java.lang.String registrazioneUtenteDL(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException;
    public java.lang.String eliminazioneUtenteDL(java.lang.String nominativoUtente) throws java.rmi.RemoteException;
}
