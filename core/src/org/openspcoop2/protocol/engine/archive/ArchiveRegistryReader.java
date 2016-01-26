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

package org.openspcoop2.protocol.engine.archive;

import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.sdk.archive.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.archive.FiltroRicercaServizi;
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
	private DriverConfigurazioneDB driverConfigurazioneDB;
	public ArchiveRegistryReader(DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB) throws Exception{
		this.driverRegistroServiziDB = driverRegistroServiziDB;
		this.driverConfigurazioneDB = driverConfigurazioneDB;
	}
	
	@Override
	public boolean existsPortaDominio(String nome){
		try{
			return this.driverRegistroServiziDB.existsPortaDominio(nome);
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public List<String> findPorteDominio(boolean operativo) throws RegistryNotFound{
		try{
			FiltroRicerca filtroRicerca = new FiltroRicerca();
			if(operativo){
				filtroRicerca.setTipo("operativo");
			}
			else{
				filtroRicerca.setTipo("esterno");
			}
			return this.driverRegistroServiziDB.getAllIdPorteDominio(filtroRicerca);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public boolean existsSoggettoByCodiceIPA(String codiceIPA) {
		try{
			return this.driverRegistroServiziDB.existsSoggetto(codiceIPA);
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) {
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
	public List<IDAccordo> findAccordiServizioParteComune(FiltroRicercaAccordi filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaAccordi filtroRicerca = new org.openspcoop2.core.registry.driver.FiltroRicercaAccordi();
			if(filtro.getNome()!=null)
				filtroRicerca.setNomeAccordo(filtro.getNome());
			if(filtro.getVersione()!=null)
				filtroRicerca.setVersione(filtro.getVersione()+"");
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroRicerca.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroRicerca.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			return this.driverRegistroServiziDB.getAllIdAccordiServizioParteComune(filtroRicerca);
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
	public List<IDAccordo> findAccordiServizioParteSpecifica(FiltroRicercaServizi filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaServizi filtroRicerca = new org.openspcoop2.core.registry.driver.FiltroRicercaServizi();
			if(filtro.getTipo()!=null){
				filtroRicerca.setTipo(filtro.getTipo());
			}
			if(filtro.getNome()!=null){
				filtroRicerca.setNome(filtro.getNome());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroRicerca.setTipoSoggettoErogatore(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroRicerca.setNomeSoggettoErogatore(filtro.getSoggetto().getNome());
				}
			}
			return this.driverRegistroServiziDB.getAllIdAccordiServizioParteSpecifica(filtroRicerca);
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

	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		try{
			return this.driverConfigurazioneDB.existsServizioApplicativo(idServizioApplicativo);
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativo(String username, String password){
		try{
			return this.driverConfigurazioneDB.getServizioApplicativoAutenticato(username, password)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativo(String subject){
		try{
			return this.driverConfigurazioneDB.getServizioApplicativoAutenticato(subject)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneDB.getServizioApplicativo(idServizioApplicativo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
}
