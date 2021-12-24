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


package org.openspcoop2.core.config.driver;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
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
	public boolean verificaSoggetto(
			IDSoggetto idSoggetto,
			Soggetto soggetto)throws DriverConfigurazioneException;
	public boolean verificaSoggetto(
			IDSoggetto idSoggetto,
			Soggetto soggetto,
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaRouter(
			Soggetto soggetto) throws DriverConfigurazioneException;
	public boolean verificaRouter(
			Soggetto soggetto,
			boolean checkID)throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idPD
	 * @param pd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaPortaDelegata(
			IDPortaDelegata idPD,
			PortaDelegata pd)throws DriverConfigurazioneException;
	public boolean verificaPortaDelegata(
			IDPortaDelegata idPD,
			PortaDelegata pd,
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
	public boolean verificaPortaApplicativa(
			IDPortaApplicativa idPA,
			PortaApplicativa pa)throws DriverConfigurazioneException;
	public boolean verificaPortaApplicativa(
			IDPortaApplicativa idPA,
			PortaApplicativa pa,
			boolean checkID)throws DriverConfigurazioneException;
	
		
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSA
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaServizioApplicativo(
			IDServizioApplicativo idSA,
			ServizioApplicativo sa)throws DriverConfigurazioneException;
	public boolean verificaServizioApplicativo(
			IDServizioApplicativo idSA,
			ServizioApplicativo sa,
			boolean checkID) throws DriverConfigurazioneException;
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param rt
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaRoutingTable(
			RoutingTable rt) throws DriverConfigurazioneException;
	public boolean verificaRoutingTable(
			RoutingTable rt,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param ar
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaAccessoRegistro(
			AccessoRegistro ar) throws DriverConfigurazioneException;
	public boolean verificaAccessoRegistro(
			AccessoRegistro ar,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean, sia uguale al bean passato come parametro
	 * 
	 * @param ac
	 * @return true se il bean, sia uguale al bean passato come parametro
	 */
	public boolean verificaAccessoConfigurazione(
			AccessoConfigurazione ac) throws DriverConfigurazioneException;
	public boolean verificaAccessoConfigurazione(
			AccessoConfigurazione ac,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean, sia uguale al bean passato come parametro
	 * 
	 * @param ad
	 * @return true se il bean, sia uguale al bean passato come parametro
	 */
	public boolean verificaAccessoDatiAutorizzazione(
			AccessoDatiAutorizzazione ad) throws DriverConfigurazioneException;
	public boolean verificaAccessoDatiAutorizzazione(
			AccessoDatiAutorizzazione ad,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaGestioneErroreComponenteCooperazione(
			GestioneErrore gr) throws DriverConfigurazioneException;
	public boolean verificaGestioneErroreComponenteCooperazione(
			GestioneErrore gr,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaGestioneErroreComponenteIntegrazione(
			GestioneErrore gr )throws DriverConfigurazioneException;
	public boolean verificaGestioneErroreComponenteIntegrazione(
			GestioneErrore gr,
			boolean checkID) throws DriverConfigurazioneException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param configurazione
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaConfigurazione(
			Configurazione configurazione)throws DriverConfigurazioneException;
	public boolean verificaConfigurazione(
			Configurazione configurazione,
			boolean checkID)throws DriverConfigurazioneException;
}
