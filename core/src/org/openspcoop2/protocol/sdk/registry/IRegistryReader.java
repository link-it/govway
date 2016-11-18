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
package org.openspcoop2.protocol.sdk.registry;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public interface IRegistryReader {

	// PDD
	
	public boolean existsPortaDominio(String nome);
	
	public List<String> findPorteDominio(boolean operativo) throws RegistryNotFound;
	
	
	// SOGGETTI
	
	public boolean existsSoggettoByCodiceIPA(String codiceIPA);
	
	public boolean existsSoggetto(IDSoggetto idSoggetto);
	
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA) throws RegistryNotFound;
	
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public IDSoggetto getIdSoggettoProprietarioPortaDelegata(String location) throws RegistryNotFound;
	
	public IDSoggetto getIdSoggettoProprietarioPortaApplicativa(String location) throws RegistryNotFound;
	
	
	// ACCORDI PARTE COMUNE
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws RegistryNotFound;
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readAllegati) throws RegistryNotFound;
	public List<IDAccordo> findAccordiServizioParteComune(FiltroRicercaAccordi filtro) throws RegistryNotFound; 
	
	
	// ACCORDI PARTE SPECIFICA
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo,boolean readAllegati) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readAllegati) throws RegistryNotFound;
	public List<IDAccordo> findAccordiServizioParteSpecifica(FiltroRicercaServizi filtro) throws RegistryNotFound; 
	
	public IDServizio convertToIDServizio(IDAccordo idAccordoServizioParteSpecifica) throws RegistryNotFound;
	
	public IDAccordo convertToIDAccordo(IDServizio idServizio) throws RegistryNotFound;
	
	
	// ACCORDI COOPERAZIONE
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws RegistryNotFound;
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readAllegati) throws RegistryNotFound;
	
	
	// SERVIZI APPLICATIVI
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo);
	public boolean existsServizioApplicativo(String username, String password);
	public boolean existsServizioApplicativo(String subject);
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound;
	
	
	// PORTA DELEGATA
	
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata); 
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound; 
	
	
	// PORTA APPLICATIVA
	
	public boolean existsPortaApplicativa(IDPortaApplicativaByNome idPortaApplicativa); 
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativaByNome idPortaApplicativa) throws RegistryNotFound; 
	
	
}
