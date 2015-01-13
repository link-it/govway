/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.registry.driver;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;

/**
 * Utility sui bean del package
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IBeanUtilities  {

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoCooperazione
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaAccordoCooperazione(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordoCooperazione idAccordo,
			@WebParam(name = "accordoCooperazione", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoCooperazione accordoCooperazione)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaAccordoCooperazioneCheck")
	public boolean verificaAccordoCooperazione(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordoCooperazione idAccordo,
			@WebParam(name = "accordoCooperazione", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoCooperazione accordoCooperazione,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteComune
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaAccordoServizioParteComune(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordo,
			@WebParam(name = "accordoServizioParteComune", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteComune accordoServizioParteComune)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaAccordoCheck")
	public boolean verificaAccordoServizioParteComune(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordo,
			@WebParam(name = "accordoServizioParteComune", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteComune accordoServizioParteComune,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param nomePdd
	 * @param pdd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaPortaDominio(
			@WebParam(name = "nomePdd", targetNamespace = "http://ws.management.openspcoop2.org")
			String nomePdd,
			@WebParam(name = "pdd", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaDominio pdd)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaPortaDominioCheck")
	public boolean verificaPortaDominio(
			@WebParam(name = "nomePdd", targetNamespace = "http://ws.management.openspcoop2.org")
			String nomePdd,
			@WebParam(name = "pdd", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaDominio pdd,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@WebMethod
	public boolean verificaSoggetto(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaSoggettoCheck")
	public boolean verificaSoggetto(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idServizio
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@WebMethod
	public boolean verificaAccordoServizioParteSpecifica(
			@WebParam(name = "idServizio", targetNamespace = "http://ws.management.openspcoop2.org")
			IDServizio idServizio,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaServizioCheck")
	public boolean verificaAccordoServizioParteSpecifica(
			@WebParam(name = "idServizio", targetNamespace = "http://ws.management.openspcoop2.org")
			IDServizio idService,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@WebMethod
	public boolean verificaAccordoServizioParteSpecifica(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordo,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaServizioCheck")
	public boolean verificaAccordoServizioParteSpecifica(
			@WebParam(name = "idAccordo", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordo,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;

	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordoServizioParteComune ID dell'accordo che deve implementare il servizio correlato
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@WebMethod(operationName="verificaServizioCorrelatoByAccordo")
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto,
			@WebParam(name = "idAccordoServizioParteComune", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordoServizioParteComune,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	@WebMethod(operationName="verificaServizioCorrelatoByAccordoCheck")
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto, 
			@WebParam(name = "idAccordoServizioParteComune", targetNamespace = "http://ws.management.openspcoop2.org")
			IDAccordo idAccordoServizioParteComune,
			@WebParam(name = "accordoServizioParteSpecifica", targetNamespace = "http://ws.management.openspcoop2.org")
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverRegistroServiziException;

}
