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


package org.openspcoop2.web.ctrlstat.driver;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.id.IDServizio;

/**
 * IRepositoryAutorizzazioniDriverCRUD
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public interface IRepositoryAutorizzazioniDriverCRUD {
	
	// soggetto
	
	public void createSoggetto(Soggetto soggetto) throws Exception;

	public void updateSoggetto(Soggetto soggetto) throws Exception;

	public void deleteSoggetto(Soggetto soggetto) throws Exception;

	public boolean existsSoggetto(IDSoggetto idSO) throws Exception;

	
	// servizio applicativo
	
	public void createServizioApplicativo(ServizioApplicativo servizioApplicativo) throws Exception;

	public void updateServizioApplicativo(ServizioApplicativo servizioApplicativo) throws Exception;

	public void deleteServizioApplicativo(ServizioApplicativo servizioApplicativo) throws Exception;

	public boolean existsServizioApplicativo(IDSoggetto idSO, String nomeServizioApplicativo) throws Exception;

	// servizio
	
	public void createServizio(AccordoServizioParteComune accordo, AccordoServizioParteSpecifica servizio) throws Exception;

	public void updateServizio(AccordoServizioParteComune accordo, AccordoServizioParteSpecifica servizio) throws Exception;

	public void deleteServizio(AccordoServizioParteSpecifica servizio) throws Exception;

	public boolean existsServizio(IDServizio idSE) throws Exception;

	// fruitore
	
	public void createFruitore(AccordoServizioParteSpecifica servizio) throws Exception;

	public void deleteFruitore(AccordoServizioParteSpecifica servizio) throws Exception;

	public boolean existsFruitore(IDSoggetto idSO) throws Exception;

	// ruolo
	
	public void createRuolo(String nome, String descrizione) throws Exception;

	public void updateRuolo(String nome, String descrizione, String oldNomeForUpdate) throws Exception;

	public void deleteRuolo(String nome) throws Exception;

	public boolean existsRuolo(String nomeRuolo) throws Exception;

	// reset
	
	public void reset() throws Exception;
}
