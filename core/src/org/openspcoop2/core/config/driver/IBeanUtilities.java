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


package org.openspcoop2.core.config.driver;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;


/**
 * Utility sui bean del package
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IBeanUtilities {
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaSoggettoById")
	public boolean verificaSoggetto(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaSoggettoByIdCheck")
	public boolean verificaSoggetto(
			@WebParam(name = "idSoggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto idSoggetto,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param location
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaSoggettoByLocation")
	public boolean verificaSoggetto(
			@WebParam(name = "location", targetNamespace = "http://ws.management.openspcoop2.org")
			String location,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaSoggettoByLocationCheck")
	public boolean verificaSoggetto(
			@WebParam(name = "location", targetNamespace = "http://ws.management.openspcoop2.org")
			String location,
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaRouter(
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto) throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaRouterCheck")
	public boolean verificaRouter(
			@WebParam(name = "soggetto", targetNamespace = "http://ws.management.openspcoop2.org")
			Soggetto soggetto,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPD
	 * @param pd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaPortaDelegata(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "pd", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaDelegata pd)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaDelegataCheck")
	public boolean verificaPortaDelegata(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "pd", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaDelegata pd,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
		
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * Utilizza la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, viene ricercata
	 * una Porta Applicativa che non possegga l'azione
	 * 
	 * @param idPA
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaPortaApplicativa(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaApplicativaCheck")
	public boolean verificaPortaApplicativa(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * Utilizza la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, 
	 * non vengono effettuate ricerche ulteriori se ricerca puntuale e' abilitato.
	 * 
	 * @param idPA
	 * @param ricercaPuntuale
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaPortaApplicativaPuntuale")
	public boolean verificaPortaApplicativa(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "ricercaPuntuale", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean ricercaPuntuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaApplicativaPuntualeCheck")
	public boolean verificaPortaApplicativa(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "ricercaPuntuale", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean ricercaPuntuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param nomePorta
	 * @param soggettoProprietario
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaPortaApplicativaByNome")
	public boolean verificaPortaApplicativa(
			@WebParam(name = "nomePorta", targetNamespace = "http://ws.management.openspcoop2.org")
			String nomePorta, 
			@WebParam(name = "soggettoProprietario", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoProprietario,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaApplicativaByNomeCheck")
	public boolean verificaPortaApplicativa(
			@WebParam(name = "nomePorta", targetNamespace = "http://ws.management.openspcoop2.org")
			String nomePorta, 
			@WebParam(name = "soggettoProprietario", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoProprietario,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPA
	 * @param soggettoVirtuale
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaPortaApplicativaVirtuale(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "soggettoVirtuale", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoVirtuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaApplicativaVirtualeCheck")
	public boolean verificaPortaApplicativaVirtuale(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "soggettoVirtuale", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoVirtuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * Nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, 
	 * non vengono effettuate ricerche ulteriori.
	 * 
	 * @param idPA
	 * @param soggettoVirtuale
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaPortaApplicativaVirtualePuntuale")
	public boolean verificaPortaApplicativaVirtuale(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "soggettoVirtuale", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoVirtuale,
			@WebParam(name = "ricercaPuntuale", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean ricercaPuntuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaPortaApplicativaVirtualePuntualeCheck")
	public boolean verificaPortaApplicativaVirtuale(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "soggettoVirtuale", targetNamespace = "http://ws.management.openspcoop2.org")
			IDSoggetto soggettoVirtuale,
			@WebParam(name = "ricercaPuntuale", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean ricercaPuntuale,
			@WebParam(name = "pa", targetNamespace = "http://ws.management.openspcoop2.org")
			PortaApplicativa pa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPD
	 * @param servizioApplicativo
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaServizioApplicativoPD")
	public boolean verificaServizioApplicativo(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "servizioApplicativo", targetNamespace = "http://ws.management.openspcoop2.org")
			String servizioApplicativo,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaServizioApplicativoPDCheck")
	public boolean verificaServizioApplicativo(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "servizioApplicativo", targetNamespace = "http://ws.management.openspcoop2.org")
			String servizioApplicativo,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPA
	 * @param servizioApplicativo
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaServizioApplicativoPA")
	public boolean verificaServizioApplicativo(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "servizioApplicativo", targetNamespace = "http://ws.management.openspcoop2.org")
			String servizioApplicativo,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaServizioApplicativoPACheck")
	public boolean verificaServizioApplicativo(
			@WebParam(name = "idPA", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaApplicativa idPA,
			@WebParam(name = "servizioApplicativo", targetNamespace = "http://ws.management.openspcoop2.org")
			String servizioApplicativo,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPD
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaServizioApplicativoAutenticatoBasic")
	public boolean verificaServizioApplicativoAutenticato(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "aUser", targetNamespace = "http://ws.management.openspcoop2.org")
			String aUser,
			@WebParam(name = "aPassword", targetNamespace = "http://ws.management.openspcoop2.org")
			String aPassword,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaServizioApplicativoAutenticatoBasicCheck")
	public boolean verificaServizioApplicativoAutenticato(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "aUser", targetNamespace = "http://ws.management.openspcoop2.org")
			String aUser,
			@WebParam(name = "aPassword", targetNamespace = "http://ws.management.openspcoop2.org")
			String aPassword,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPD
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod(operationName="verificaServizioApplicativoAutenticatoSSL")
	public boolean verificaServizioApplicativoAutenticato(
			@WebParam(name = "idPD", targetNamespace = "http://ws.management.openspcoop2.org")
			IDPortaDelegata idPD,
			@WebParam(name = "aSubject", targetNamespace = "http://ws.management.openspcoop2.org")
			String aSubject,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa) throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaServizioApplicativoAutenticatoSSLCheck")
	public boolean verificaServizioApplicativoAutenticato(IDPortaDelegata idPD,
			@WebParam(name = "aSubject", targetNamespace = "http://ws.management.openspcoop2.org")
			String aSubject,
			@WebParam(name = "sa", targetNamespace = "http://ws.management.openspcoop2.org")
			ServizioApplicativo sa,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param rt
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaRoutingTable(
			@WebParam(name = "rt", targetNamespace = "http://ws.management.openspcoop2.org")
			RoutingTable rt) throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaRoutingTableCheck")
	public boolean verificaRoutingTable(
			@WebParam(name = "rt", targetNamespace = "http://ws.management.openspcoop2.org")
			RoutingTable rt,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param ar
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaAccessoRegistro(
			@WebParam(name = "ar", targetNamespace = "http://ws.management.openspcoop2.org")
			AccessoRegistro ar) throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaAccessoRegistroCheck")
	public boolean verificaAccessoRegistro(
			@WebParam(name = "ar", targetNamespace = "http://ws.management.openspcoop2.org")
			AccessoRegistro ar,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaGestioneErroreComponenteCooperazione(
			@WebParam(name = "gr", targetNamespace = "http://ws.management.openspcoop2.org")
			GestioneErrore gr) throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaGestioneErroreComponenteCooperazioneCheck")
	public boolean verificaGestioneErroreComponenteCooperazione(
			@WebParam(name = "gr", targetNamespace = "http://ws.management.openspcoop2.org")
			GestioneErrore gr,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaGestioneErroreComponenteIntegrazione(
			@WebParam(name = "gr", targetNamespace = "http://ws.management.openspcoop2.org")
			GestioneErrore gr )throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaGestioneErroreComponenteIntegrazioneCheck")
	public boolean verificaGestioneErroreComponenteIntegrazione(
			@WebParam(name = "gr", targetNamespace = "http://ws.management.openspcoop2.org")
			GestioneErrore gr,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param configurazione
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@WebMethod
	public boolean verificaConfigurazione(
			@WebParam(name = "configurazione", targetNamespace = "http://ws.management.openspcoop2.org")
			Configurazione configurazione)throws DriverConfigurazioneException;
	@WebMethod(operationName="verificaConfigurazioneCheck")
	public boolean verificaConfigurazione(
			@WebParam(name = "configurazione", targetNamespace = "http://ws.management.openspcoop2.org")
			Configurazione configurazione,
			@WebParam(name = "checkID", targetNamespace = "http://ws.management.openspcoop2.org")
			boolean checkID)throws DriverConfigurazioneException;
}
