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

package org.openspcoop2.pdd.config;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
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
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
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

	@SuppressWarnings("unused")
	private Logger log;
	private RegistroServiziManager registroServiziManager;
	private ConfigurazionePdDManager configurazionePdDMangager;
	private IProtocolFactory<?> protocolFactory;
	public RegistryReader(Logger log,IProtocolFactory<?> protocolFactory) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.registroServiziManager = RegistroServiziManager.getInstance();
		this.configurazionePdDMangager = ConfigurazionePdDManager.getInstance();
	}
	
	
	// PDD
	
	@Override
	public boolean existsPortaDominio(String nome){
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public List<String> findPorteDominio(boolean operativo) throws RegistryNotFound{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	// SOGGETTI
	
	@Override
	public boolean existsSoggettoByCodiceIPA(String codiceIPA) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA)
			throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound{
		try{
			return this.registroServiziManager.getDominio(idSoggetto, null, this.protocolFactory);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}
	
	
	// ACCORDI PARTE COMUNE
	
	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false);
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
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDAccordo> findAccordiServizioParteComune(FiltroRicercaAccordi filtro) throws RegistryNotFound{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	// ACCORDI PARTE SPECIFICA
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo, boolean readAllegati) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}


	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
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
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public List<IDAccordo> findAccordiServizioParteSpecifica(FiltroRicercaServizi filtro) throws RegistryNotFound{
		throw new RuntimeException("Not Implemented");
	}
	
	
	@Override
	public IDServizio convertToIDServizio(
			IDAccordo idAccordoServizioParteSpecifica) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public IDAccordo convertToIDAccordo(IDServizio idServizio)
			throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	
	
	// ACCORDI COOPERAZIONE
	
	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, false);
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
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	
	
	
	// SERVIZI APPLICATIVI
	
	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public boolean existsServizioApplicativo(String username, String password){
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public boolean existsServizioApplicativo(String subject){
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getIDPortaDelegata(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata){
		try{
			return this.configurazionePdDMangager.getPortaDelegata(idPortaDelegata)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	public String getAzione(PortaDelegata pd,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pd, transportContext, null, null, false, protocolFactory);
	}
	
	
	// PORTA APPLICATIVA
	
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getIDPortaApplicativa(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa){
		try{
			return this.configurazionePdDMangager.getPortaApplicativa(idPortaApplicativa)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	} 
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	public String getAzione(PortaApplicativa pa,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pa, transportContext, null, null, false, protocolFactory);
	}
	
}

