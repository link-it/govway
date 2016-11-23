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

package org.openspcoop2.protocol.engine.registry;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
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
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class RegistryReader implements IRegistryReader {

	private DriverRegistroServiziDB driverRegistroServiziDB;
	private DriverConfigurazioneDB driverConfigurazioneDB;
	@SuppressWarnings("unused")
	private Logger log;
	public RegistryReader(DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB,Logger log) throws Exception{
		this.driverRegistroServiziDB = driverRegistroServiziDB;
		this.driverConfigurazioneDB = driverConfigurazioneDB;
		this.log = log;
	}
	
	
	// PDD
	
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
	
	
	
	// SOGGETTI
	
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
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound{
		try{
			return this.getSoggetto(idSoggetto).getIdentificativoPorta();
		} catch (RegistryNotFound de) {
			throw de;
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
	
	
	// ACCORDI PARTE COMUNE
	
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
	
	
	
	// ACCORDI PARTE SPECIFICA
	
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

	
	
	// ACCORDI COOPERAZIONE
	
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

	
	
	
	// SERVIZI APPLICATIVI
	
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
			return this.driverConfigurazioneDB.getServizioApplicativoByCredenzialiBasic(username, password)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativo(String subject){
		try{
			return this.driverConfigurazioneDB.getServizioApplicativoByCredenzialiSsl(subject)!=null;
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
	
	
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		
		IDPortaDelegata idPortaDelegata = null;
		try{
			idPortaDelegata = this.driverConfigurazioneDB.getIDPortaDelegata(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
		
		try{
			if(idPortaDelegata!=null && idPortaDelegata.getIdentificativiFruizione()!=null){
				if(idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.getDominio(soggetto));
					}
				}
				if(idPortaDelegata.getIdentificativiFruizione().getIdServizio()!=null &&
						idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaDelegata.getIdentificativiFruizione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.getDominio(soggetto));
					}
				}
			}
		}catch(Exception e){
			return null;
		}
		
		return idPortaDelegata;
	}
	
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata){
		try{
			return this.driverConfigurazioneDB.existsPortaDelegata(idPortaDelegata);
		}catch(Exception e){
			return false;
		}	
	}
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneDB.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
		
		
	// PORTA APPLICATIVA
		
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		
		IDPortaApplicativa idPortaApplicativa = null;
		try{
			idPortaApplicativa = this.driverConfigurazioneDB.getIDPortaApplicativa(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
		
		try{
			if(idPortaApplicativa!=null && idPortaApplicativa.getIdentificativiErogazione()!=null){
				if(idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getSoggettoVirtuale();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.getDominio(soggetto));
					}
				}
				if(idPortaApplicativa.getIdentificativiErogazione().getIdServizio()!=null &&
						idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore()!=null){
					IDSoggetto soggetto = idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore();
					if(soggetto.getCodicePorta()==null){
						soggetto.setCodicePorta(this.getDominio(soggetto));
					}
				}
			}
		}catch(Exception e){
			return null;
		}
		
		return idPortaApplicativa;
	}
	
	
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa){
		try{
			return this.driverConfigurazioneDB.existsPortaApplicativa(idPortaApplicativa);
		}catch(Exception e){
			return false;
		}	
	}
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneDB.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
}
