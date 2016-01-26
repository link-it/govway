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


package org.openspcoop2.core.config.driver;



import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;

/**
 * Interfaccia per la ricerca di informazioni su oggetti presenti in una 
 * configurazione di OpenSPCoop. I driver che implementano l'interfaccia 
 * sono attualmente:
 * <ul>
 * <li> {@link org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB}, interroga un registro dei servizi Relazionale.
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public interface IDriverConfigurazioneSearch {

	/**
	 * Mi restituisce la lista dei soggetti che soddisfano i criteri di ricerca settati
	 */
	public List<Soggetto> soggettiList(String superuser, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Restituisce le porte applicative associate al soggetto e che soddisfano i criteri di ricerca
	 * settati
	 */
	public List<PortaApplicativa> porteAppList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Ritorna la liste delle proprieta associate alla porta applicativa di un soggetto
	 * tiene conto dei criteri di ricerca settati
	 */
	public List<ProprietaProtocollo> porteAppPropList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Ritorna la lista di Servizi Applicativi di una Porta Applicativa associata ad un Soggetto
	 * Tiene conto dei criteri di ricerca settati
	 */
	public List<ServizioApplicativo> porteAppServizioApplicativoList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Ritorna la lista di RequestFlow di una PortaApplicativa associata ad un Soggetto
	 * Tiene conto dei Criteri di ricerca settati 
	 */
	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Ritorna la lista di ResponseFlow di una PortaApplicativa associata ad un Soggetto
	 * Tiene conto dei Criteri di ricerca settati
	 */
	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException;

	/**
	 * Ritorna la lista delle Porte Delegate di un Soggetto
	 */
	public List<PortaDelegata> porteDelegateList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException;


	/**
	 * Ritorna la lista di Servizi Applicativi di una Porta Delegata associata ad un Soggetto
	 * Tiene conto dei criteri di ricerca settati
	 */
	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException;

	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException;

	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException;
	
	/***
	 * Porte Delegate: Ritorna la lista dei parametri MTOM della richiesta 
	 */
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException;
	
	/***
	 * Porte Delegate: Ritorna la lista dei parametri MTOM della risposta 
	 */
	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException ;

	
	/***
	 * Porte Applicative: Ritorna la lista dei parametri MTOM della richiesta 
	 */
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException;
	
	/***
	 * Porte Applicative: Ritorna la lista dei parametri MTOM della risposta 
	 */
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException ;

	
	/**
	 * Ritorna la lista di Servizi Applicativi che soddisfano i criteri di ricerca 
	 */
	public List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException;
	
}
