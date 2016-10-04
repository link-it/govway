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


package org.openspcoop2.core.registry.driver;

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
	public boolean verificaAccordoCooperazione(
			IDAccordoCooperazione idAccordo,
			AccordoCooperazione accordoCooperazione)throws DriverRegistroServiziException;
	public boolean verificaAccordoCooperazione(
			IDAccordoCooperazione idAccordo,
			AccordoCooperazione accordoCooperazione,
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteComune
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaAccordoServizioParteComune(
			IDAccordo idAccordo,
			AccordoServizioParteComune accordoServizioParteComune)throws DriverRegistroServiziException;
	public boolean verificaAccordoServizioParteComune(
			IDAccordo idAccordo,
			AccordoServizioParteComune accordoServizioParteComune,
			boolean checkID)throws DriverRegistroServiziException;

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param nomePdd
	 * @param pdd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	public boolean verificaPortaDominio(
			String nomePdd,
			PortaDominio pdd)throws DriverRegistroServiziException;
	public boolean verificaPortaDominio(
			String nomePdd,
			PortaDominio pdd,
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	public boolean verificaSoggetto(
			IDSoggetto idSoggetto,
			Soggetto soggetto)throws DriverRegistroServiziException;
	public boolean verificaSoggetto(
			IDSoggetto idSoggetto,
			Soggetto soggetto,
			boolean checkID)throws DriverRegistroServiziException;
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idServizio
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	public boolean verificaAccordoServizioParteSpecifica(
			IDServizio idServizio,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	public boolean verificaAccordoServizioParteSpecifica(
			IDServizio idService,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			boolean checkID)throws DriverRegistroServiziException;
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	public boolean verificaAccordoServizioParteSpecifica(
			IDAccordo idAccordo,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	public boolean verificaAccordoServizioParteSpecifica(
			IDAccordo idAccordo,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
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
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(
			IDSoggetto idSoggetto,
			IDAccordo idAccordoServizioParteComune,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException;
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(
			IDSoggetto idSoggetto, 
			IDAccordo idAccordoServizioParteComune,
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			boolean checkID)throws DriverRegistroServiziException;

}
