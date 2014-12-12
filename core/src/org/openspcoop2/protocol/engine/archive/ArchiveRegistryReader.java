/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

package org.openspcoop2.protocol.engine.archive;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.archive.RegistryNotFound;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveRegistryReader implements IRegistryReader {

	private DriverRegistroServiziDB driverRegistroServiziDB;
	public ArchiveRegistryReader(DriverRegistroServiziDB driverRegistroServiziDB) throws Exception{
		this.driverRegistroServiziDB = driverRegistroServiziDB;
	}
	
	@Override
	public boolean existsSoggettoByCodiceIPA(String codiceIPA)
			throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.existsSoggetto(codiceIPA);
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto)
			throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.existsSoggetto(idSoggetto);
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA)
			throws RegistryNotFound {
		try{
			Soggetto s = this.driverRegistroServiziDB.getSoggetto(codiceIPA);
			IDSoggetto idSoggetto = new IDSoggetto(s.getTipo(), s.getNome(), s.getIdentificativoPorta());
			return idSoggetto;
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getCodiceIPA(idSoggetto);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getSoggetto(idSoggetto);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteComune(idAccordo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo, boolean readAllegati) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteComune(idAccordo,readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idAccordo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo, boolean readAllegati) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idAccordo,readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}


	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idServizio);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio, boolean readAllegati)
			throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idServizio,readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public IDServizio convertToIDServizio(
			IDAccordo idAccordoServizioParteSpecifica) throws RegistryNotFound {
		try{
			AccordoServizioParteSpecifica as = this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
			IDServizio idServizio = new IDServizio(idAccordoServizioParteSpecifica.getSoggettoReferente(), as.getServizio().getTipo(), as.getServizio().getNome());
			return idServizio;
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public IDAccordo convertToIDAccordo(IDServizio idServizio)
			throws RegistryNotFound {
		try{
			AccordoServizioParteSpecifica as = this.driverRegistroServiziDB.getAccordoServizioParteSpecifica(idServizio);
			return IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo) throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoCooperazione(idAccordo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo, boolean readAllegati)
			throws RegistryNotFound {
		try{
			return this.driverRegistroServiziDB.getAccordoCooperazione(idAccordo,readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}


}
