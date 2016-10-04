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

package org.openspcoop2.protocol.engine.archive;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;

/**
 *  AbstractArchiveEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractArchiveEngine {

	private DriverRegistroServiziDB driverRegistroServizi;
	public DriverRegistroServiziDB getDriverRegistroServizi() {
		return this.driverRegistroServizi;
	}

	private DriverConfigurazioneDB driverConfigurazione;
	public DriverConfigurazioneDB getDriverConfigurazione() {
		return this.driverConfigurazione;
	}

	public AbstractArchiveEngine(DriverRegistroServiziDB driverRegistroServizi,DriverConfigurazioneDB driverConfigurazione){
		this.driverRegistroServizi = driverRegistroServizi;
		this.driverConfigurazione = driverConfigurazione;
	}
	
	// --- Users ---
	
	public abstract boolean isVisioneOggettiGlobale(String userLogin);
	
	
	// --- PDD ---
	
	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdPorteDominio(filtroRicerca);
	}
	
	public PortaDominio getPortaDominio(String nomePortaDominio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getPortaDominio(nomePortaDominio);
	}
	
	public boolean existsPortaDominio(String nomePortaDominio) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsPortaDominio(nomePortaDominio);
	}
	
	public void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createPortaDominio(pdd);
	}
	
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updatePortaDominio(pdd);
	}
	
	public void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deletePortaDominio(pdd);
	}
	
	public boolean isPddInUso(String nomePortaDominio, List<String> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isPddInUso");
			return DBOggettiInUsoUtils.isPddInUso(con, this.driverRegistroServizi.getTipoDB(), nomePortaDominio, whereIsInUso);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
		
	
	
	// --- Soggetti Registro ---
	
	public List<IDSoggetto> getAllIdSoggettiRegistro(org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAllIdSoggetti(filtroRicerca);
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistro(IDSoggetto idSoggetto) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getSoggetto(idSoggetto);
	}
	
	public boolean existsSoggettoRegistro(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsSoggetto(idSoggetto);
	}
	
	public void createSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createSoggetto(soggetto);
	}
	
	public void updateSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateSoggetto(soggetto);
	}
	
	public void deleteSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteSoggetto(soggetto);
	}
	
	public boolean isSoggettoRegistroInUso(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isSoggettoRegistroInUso");
			return DBOggettiInUsoUtils.isSoggettoRegistryInUso(con, this.driverRegistroServizi.getTipoDB(), idSoggetto, whereIsInUso);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	
	// --- Soggetti Configurazione ---
		
	public List<IDSoggetto> getAllIdSoggettiConfigurazione(FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdSoggetti(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.Soggetto getSoggettoConfigurazione(IDSoggetto idSoggetto) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getSoggetto(idSoggetto);
	}
	
	public boolean existsSoggettoConfigurazione(IDSoggetto idSoggetto) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsSoggetto(idSoggetto);
	}
	
	public void createSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.createSoggetto(soggetto);
	}
	
	public void updateSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.updateSoggetto(soggetto);
	}
	
	public void deleteSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.deleteSoggetto(soggetto);
	}
	
	public boolean isSoggettoConfigurazioneInUso(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverConfigurazioneException {
		Connection con = null;
		try{
			con = this.driverConfigurazione.getConnection("archive.isSoggettoConfigurazioneInUso");
			return DBOggettiInUsoUtils.isSoggettoConfigInUso(con, this.driverConfigurazione.getTipoDB(), idSoggetto, whereIsInUso);
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverConfigurazione.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	
	// --- Servizi Applicativi ---
	
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdServiziApplicativi(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getServizioApplicativo(idServizioApplicativo);
	}
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsServizioApplicativo(idServizioApplicativo);
	}
	
	public void createServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.createServizioApplicativo(servizioApplicativo);
	}
	
	public void updateServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.updateServizioApplicativo(servizioApplicativo);
	}
	
	public void deleteServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.deleteServizioApplicativo(servizioApplicativo);
	}
	
	public boolean isServizioApplicativoInUso(IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverConfigurazioneException {
		Connection con = null;
		try{
			con = this.driverConfigurazione.getConnection("archive.isServizioApplicativoInUso");
			return DBOggettiInUsoUtils.isServizioApplicativoInUso(con, this.driverConfigurazione.getTipoDB(), idServizioApplicativo, whereIsInUso, true);
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverConfigurazione.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	
	// --- Accordi di Cooperazione ---
	
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdAccordiCooperazione(filtroRicerca);
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoCooperazione(idAccordoCooperazione);
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoCooperazione(idAccordoCooperazione,readContenutoAllegati);
	}
	
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoCooperazione(idAccordoCooperazione);
	}
	
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoCooperazione(accordoCooperazione);
	}
	
	public void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoCooperazione(accordoCooperazione);
	}
	
	public void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoCooperazione(accordoCooperazione);
	}
	
	public void validaStatoAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoCooperazione(accordoCooperazione);
	}
	
	public boolean isAccordoCooperazioneInUso(IDAccordoCooperazione idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoCooperazioneInUso");
			return DBOggettiInUsoUtils.isAccordoCooperazioneInUso(con, this.driverRegistroServizi.getTipoDB(), idAccordo, whereIsInUso);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	// --- Accordi di Servizio Parte Comune ---
	
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdAccordiServizioParteComune(filtroRicerca);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteComune(idAccordoServizioParteComune);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteComune(idAccordoServizioParteComune, readContenutoAllegati);
	}
	
	public boolean existsAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoServizioParteComune(idAccordoServizioParteComune);
	}
	
	public void createAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void updateAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void deleteAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void validaStatoAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoServizio(accordoServizioParteComune,utilizzoAzioniDiretteInAccordoAbilitato);
	}
	
	public boolean isAccordoServizioParteComuneInUso(IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoServizioParteComuneInUso");
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(con, this.driverRegistroServizi.getTipoDB(), idAccordo, whereIsInUso);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	// --- Accordi di Servizio Parte Specifica ---
	
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdAccordiServizioParteSpecifica(filtroRicerca);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica, readContenutoAllegati);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio,readContenutoAllegati);
	}
	
	public boolean existsAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
	}
	
	public boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoServizioParteSpecifica(idServizio);
	}
	
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void validaStatoFruitoreServizio(Fruitore fruitore, AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoFruitoreServizio(fruitore, accordoServizioParteSpecifica);
	}
	
	public void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			boolean isUpdate,boolean isServizioCorrelato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws DriverRegistroServiziException{
		this.driverRegistroServizi.controlloUnicitaImplementazioneAccordoPerSoggetto(portType, idSoggettoErogatore, idSoggettoErogatoreLong, 
				idAccordoServizioParteComune, idAccordoServizioParteComuneLong, 
				idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong, 
				isUpdate, isServizioCorrelato, 
				isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto, 
				isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
	}
	
	public boolean isAccordoServizioParteSpecificaInUso(IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoServizioParteSpecificaInUso");
			return DBOggettiInUsoUtils.isAccordoServizioParteSpecificaInUso(con, this.driverRegistroServizi.getTipoDB(), idAccordo, whereIsInUso, null);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	public boolean isAccordoServizioParteSpecificaInUso(IDServizio idServizio, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoServizioParteSpecificaInUso");
			return DBOggettiInUsoUtils.isAccordoServizioParteSpecificaInUso(con, this.driverRegistroServizi.getTipoDB(), idServizio, whereIsInUso, null);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){}
		}
	}
	
	
	
	// --- Politiche di Sicurezza ---
	
	public void createServizioApplicativoAutorizzato(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore, String nomeServizioApplicativo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createServizioApplicativoAutorizzato(idAccordoServizioParteSpecifica, idFruitore, nomeServizioApplicativo);
	}
	
	public List<IDServizioApplicativo> getAllIdServiziApplicativiAutorizzati(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdServiziApplicativiAutorizzati(idAccordoServizioParteSpecifica, idFruitore);
	}
	
	public void deleteServizioApplicativoAutorizzato(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore, String nomeServizioApplicativo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteServizioApplicativoAutorizzato(idAccordoServizioParteSpecifica, idFruitore, nomeServizioApplicativo);
	}
		
	
	
	// --- Porte Delegate ---
	
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdPorteDelegate(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaDelegata(idPortaDelegata);
	}
	
	public org.openspcoop2.core.config.PortaDelegata getPortaDelegata(String nomePorta,IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaDelegata(nomePorta, soggettoProprietario);
	}
	
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaDelegata(idPortaDelegata);
	}
	
	public boolean existsPortaDelegata(String nomePorta,IDSoggetto soggettoProprietario) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaDelegata(nomePorta, soggettoProprietario);
	}
	
	public void createPortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.createPortaDelegata(portaDelegata);
	}
	
	public void deletePortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.deletePortaDelegata(portaDelegata);
	}
	
	public void updatePortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.updatePortaDelegata(portaDelegata);
	}
	
	
	
	
	// --- Porte Applicative ---
	
	public List<IDPortaApplicativaByNome> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdPorteApplicative(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaApplicativa(idPortaApplicativa, true);
	}
	
	public org.openspcoop2.core.config.PortaApplicativa getPortaApplicativa(IDPortaApplicativaByNome idPortaApplicativa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaApplicativa(idPortaApplicativa.getNome(),idPortaApplicativa.getSoggetto());
	}
	
	public org.openspcoop2.core.config.PortaApplicativa getPortaApplicativa(String nomePorta,IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaApplicativa(nomePorta,soggettoProprietario);
	}
	
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaApplicativa(idPortaApplicativa);
	}
	
	public boolean existsPortaApplicativa(String nomePorta,IDSoggetto soggettoProprietario) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaApplicativa(nomePorta, soggettoProprietario);
	}
	
	public void createPortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.createPortaApplicativa(portaApplicativa);
	}
	
	public void deletePortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.deletePortaApplicativa(portaApplicativa);
	}
	
	public void updatePortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.updatePortaApplicativa(portaApplicativa);
	}
	
	
	
	
	
	// --- ConfigurazionePdD ---
	
	public void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException{
		if(configurazione.getRoutingTable()!=null){
			this.driverConfigurazione.updateRoutingTable(configurazione.getRoutingTable());
		}
		if(configurazione.getAccessoRegistro()!=null){
			this.driverConfigurazione.updateAccessoRegistro(configurazione.getAccessoRegistro());
		}
		if(configurazione.getGestioneErrore()!=null){
			if(configurazione.getGestioneErrore().getComponenteCooperazione()!=null){
				this.driverConfigurazione.updateGestioneErroreComponenteCooperazione(configurazione.getGestioneErrore().getComponenteCooperazione());
			}
			if(configurazione.getGestioneErrore().getComponenteIntegrazione()!=null){
				this.driverConfigurazione.updateGestioneErroreComponenteIntegrazione(configurazione.getGestioneErrore().getComponenteIntegrazione());
			}
		}
		if(configurazione.getStatoServiziPdd()!=null){
			this.driverConfigurazione.updateStatoServiziPdD(configurazione.getStatoServiziPdd());
		}
		if(configurazione.getSystemProperties()!=null){
			this.driverConfigurazione.updateSystemPropertiesPdD(configurazione.getSystemProperties());
		}
		this.driverConfigurazione.updateConfigurazione(configurazione);
	}

	public Configurazione getConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Configurazione configurazione = this.driverConfigurazione.getConfigurazioneGenerale();
		configurazione.setRoutingTable(this.driverConfigurazione.getRoutingTable());
		configurazione.setAccessoRegistro(this.driverConfigurazione.getAccessoRegistro());
		try{
			configurazione.setAccessoConfigurazione(this.driverConfigurazione.getAccessoConfigurazione());
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			configurazione.setAccessoDatiAutorizzazione(this.driverConfigurazione.getAccessoDatiAutorizzazione());
		}catch(DriverConfigurazioneNotFound notFound){}
		GestioneErrore gestioneErroreCooperazione = null;
		GestioneErrore gestioneErroreIntegrazione = null;
		try{
			gestioneErroreCooperazione = this.driverConfigurazione.getGestioneErroreComponenteCooperazione();
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			gestioneErroreIntegrazione = this.driverConfigurazione.getGestioneErroreComponenteIntegrazione();
		}catch(DriverConfigurazioneNotFound notFound){}
		if(gestioneErroreCooperazione!=null || gestioneErroreIntegrazione!=null){
			if(configurazione.getGestioneErrore()==null){
				configurazione.setGestioneErrore(new ConfigurazioneGestioneErrore());
			}
			if(gestioneErroreCooperazione!=null){
				configurazione.getGestioneErrore().setComponenteCooperazione(gestioneErroreCooperazione);
			}
			if(gestioneErroreIntegrazione!=null){
				configurazione.getGestioneErrore().setComponenteIntegrazione(gestioneErroreIntegrazione);
			}
		}
		try{
			configurazione.setStatoServiziPdd(this.driverConfigurazione.getStatoServiziPdD());
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			configurazione.setSystemProperties(this.driverConfigurazione.getSystemPropertiesPdD());
		}catch(DriverConfigurazioneNotFound notFound){}
		return configurazione;
	}
}
