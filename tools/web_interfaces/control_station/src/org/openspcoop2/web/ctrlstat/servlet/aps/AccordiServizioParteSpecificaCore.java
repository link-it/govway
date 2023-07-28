/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AccordiServizioParteSpecificaCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteSpecificaCore extends ControlStationCore {

	public AccordiServizioParteSpecificaCore() throws DriverControlStationException {
		super();
	}
	public AccordiServizioParteSpecificaCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}

	
	public boolean isConnettoreStatic(String protocollo) throws ProtocolException   {
		
		return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolVersionManager(null).isStaticRoute();
	}
	
	
	public boolean isSupportatoVersionamentoAccordiServizioParteSpecifica(String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoVersionamentoAccordiServizioParteSpecifica";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoVersionamentoAccordiParteSpecifica();
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	
	public List<String> getTipiServiziGestitiProtocollo(String protocollo,ServiceBinding serviceBinding) throws DriverRegistroServiziException {
		String nomeMetodo = "getTipiServiziGestitiProtocollo";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipiServizi(serviceBinding);
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public String getTipoServizioDefaultProtocollo(String protocollo,ServiceBinding serviceBinding) throws  DriverRegistroServiziException {
		String getTipoServizioDefault = "getTipoServizioDefaultProtocollo";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipoServizioDefault(serviceBinding); 
			
		}catch (Exception e) {
			String error = getPrefixError(getTipoServizioDefault, e);
			ControlStationCore.logError(error, e);
			throw new DriverRegistroServiziException(error,e);
		}
	}

	public String getProtocolloAssociatoTipoServizio(String tipoServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "getProtocolloAssociatoTipoServizio";
		try{
			
			return this.protocolFactoryManager.getProtocolByServiceType(tipoServizio);
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	
	public void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			TipoOperazione tipoOperazione,boolean servizioCorrelato) throws DriverRegistroServiziException{
		
		
		Connection con = null;
		String nomeMetodo = "controlloUnicitaImplementazioneAccordoPerSoggetto";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			DriverRegistroServiziDB driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			driver.controlloUnicitaImplementazioneAccordoPerSoggetto(portType, idSoggettoErogatore, idSoggettoErogatoreLong, 
					idAccordoServizioParteComune, idAccordoServizioParteComuneLong, 
					idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong, 
					TipoOperazione.CHANGE.equals(tipoOperazione), servizioCorrelato, 
					this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto(),
					this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto());
			
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		
		
		
		
		/*
		 * Controllo che non esistano 2 servizi con stesso soggetto erogatore e accordo di servizio 
		 * che siano entrambi correlati o non correlati. 
		 * Al massimo possono esistere 2 servizi di uno stesso accordo erogati da uno stesso soggetto, 
		 * purche' siano uno correlato e uno no. 
		 * Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato che sto
		 * cambiando 
		 */

		String tmpServCorr = CostantiRegistroServizi.DISABILITATO.toString();
		if(servizioCorrelato){
			tmpServCorr = CostantiRegistroServizi.ABILITATO.toString();
		}
		String s = "servizio";
		if (servizioCorrelato) {
			s = "servizio correlato";
		}
		
		// se il servizio non definisce port type effettuo controllo che non
		// esistano 2 servizi con stesso soggetto,
		// accordo e servizio correlato
		if (portType == null || "-".equals(portType)) {
			
			if(this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto()){
			
				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato
				// Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato

				long idAccordoServizioParteSpecificaAlreadyExists = 
						this.getServizioWithSoggettoAccordoServCorr(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								tmpServCorr);

				boolean addError = (tipoOperazione.equals(TipoOperazione.ADD) && (idAccordoServizioParteSpecificaAlreadyExists > 0));
				boolean changeError = false;
				if (tipoOperazione.equals(TipoOperazione.CHANGE) && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}
				
				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa l'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}
				
			}
				
		} else {
			
			if(this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto()){
			
				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato e port-type

				long idAccordoServizioParteSpecificaAlreadyExists =  
						this.getServizioWithSoggettoAccordoServCorrPt(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								tmpServCorr, portType);

				boolean addError = (tipoOperazione.equals(TipoOperazione.ADD) && (idAccordoServizioParteSpecificaAlreadyExists > 0));
				boolean changeError = false;
				if (tipoOperazione.equals(TipoOperazione.CHANGE) && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}
				
				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa il servizio "+portType+" dell'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}
				
			}
		}
	}
	
	
	
	public long getIdAccordoServizioParteSpecifica(IDServizio idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizioParteSpecifica(id)";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdServizio(idAccordo.getNome(), idAccordo.getTipo(), idAccordo.getVersione(),
					idAccordo.getSoggettoErogatore().getNome(), idAccordo.getSoggettoErogatore().getTipo(),
					con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public long getIdAccordoServizioParteSpecifica(Connection con, IDServizio idAccordo) throws DriverRegistroServiziException {
		String nomeMetodo = "getIdAccordoServizioParteSpecifica(con,id)";
		try {
			// prendo una connessione
			return DBUtils.getIdServizio(idAccordo.getNome(), idAccordo.getTipo(), idAccordo.getVersione(),
					idAccordo.getSoggettoErogatore().getNome(), idAccordo.getSoggettoErogatore().getTipo(),
					con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	public long getIdFruizioneAccordoServizioParteSpecifica(IDSoggetto idFruitore, IDServizio idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdFruizioneAccordoServizioParteSpecifica";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdFruizioneServizio(idAccordo, idFruitore, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<AccordoServizioParteComune> findAccordiParteComuneBySoggettoAndNome(String nomeAccordoParteComune,IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "findAccordiParteComuneBySoggettoAndNome(" + nomeAccordoParteComune + ", "+idSoggetto.toString()+")";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			IDriverRegistroServiziGet driverRegistro = null;
			if(this.isRegistroServiziLocale()){
				driverRegistro = driver.getDriverRegistroServiziDB();
			}
			else{
				driverRegistro = GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log);
			}

			FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
			filtroRicerca.setNomeAccordo(nomeAccordoParteComune);
			filtroRicerca.setTipoSoggettoReferente(idSoggetto.getTipo());
			filtroRicerca.setNomeSoggettoReferente(idSoggetto.getNome());
			
			List<IDAccordo> idAccordi = null;
			List<AccordoServizioParteComune> accordi = new ArrayList<>();
			try{
				idAccordi = driverRegistro.getAllIdAccordiServizioParteComune(filtroRicerca);
			}catch(DriverRegistroServiziNotFound dnf){
				// ignore
			}
			if(idAccordi!=null && !idAccordi.isEmpty()){
				// se long
				for (IDAccordo idAccordo : idAccordi) {
					accordi.add(driverRegistro.getAccordoServizioParteComune(idAccordo));
				}
			}

			return accordi;

		}  catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<AccordoServizioParteSpecifica> serviziByAccordoFilterList(IDAccordo idAccordo) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziByAccordoFilterList(" + idAccordo + ")";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			DriverRegistroServiziDB driverRegistro = driver.getDriverRegistroServiziDB();

			FiltroRicercaServizi filtroRicerca = new FiltroRicercaServizi();
			filtroRicerca.setIdAccordoServizioParteComune(idAccordo);
						
			List<IDServizio> idServizi = null;
			try{
				idServizi = driverRegistro.getAllIdServizi(filtroRicerca);
			}catch(DriverRegistroServiziNotFound dnf){
				// ignore
			}
			List<AccordoServizioParteSpecifica> lista = new ArrayList<>();
			if(idServizi!=null && !idServizi.isEmpty()){
				for(int i=0; i<idServizi.size(); i++){
					lista.add(driverRegistro.getAccordoServizioParteSpecifica(idServizi.get(i)));
				}
			}
			return lista;

		}  catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
		
	public boolean isAccordoServizioParteSpecificaInUso(AccordoServizioParteSpecifica as, Map<ErrorsHandlerCostant, List<String>> whereIsInUso,
			List<IDPortaDelegata> nomePDGenerateAutomaticamente, List<IDPortaApplicativa> nomePAGenerateAutomaticamente, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAccordoServizioPArteSpecificaInUso";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(as);
			return DBOggettiInUsoUtils.isAccordoServizioParteSpecificaInUso(con, this.tipoDB, idServizio, whereIsInUso, nomePDGenerateAutomaticamente, nomePAGenerateAutomaticamente, normalizeObjectIds);			
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica serv, boolean gestioneWsdlImplementativo, boolean checkConnettore) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
		Connection con = null;
		String nomeMetodo = "validaStatoAccordoServizioParteSpecifica";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			driver.validaStatoAccordoServizioParteSpecifica(serv, gestioneWsdlImplementativo, checkConnettore);
			
		} catch (ValidazioneStatoPackageException e) {
			ControlStationCore.logError("[ControlStationCore::" + nomeMetodo + "] ValidazioneStatoPackageException :" + e.getMessage(), e);
			throw e;
		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void validaStatoFruitoreAccordoServizioParteSpecifica(Fruitore fruitore,AccordoServizioParteSpecifica serv) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
		Connection con = null;
		String nomeMetodo = "validaStatoFruitoreAccordoServizioParteSpecifica";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			driver.validaStatoFruitoreServizio(fruitore,serv);
			
		} catch (ValidazioneStatoPackageException e) {
			ControlStationCore.logError("[ControlStationCore::" + nomeMetodo + "] validaStatoFruitoreAccordoServizioParteSpecifica :" + e.getMessage(), e);
			throw e;
		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdServizi";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAllIdServizi(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdServizi(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteSpecifica> servizioWithSoggettoFruitore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "servizioWithSoggettoFruitore";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.servizioWithSoggettoFruitore(idSoggetto);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AccordoServizioParteSpecifica> serviziWithIdAccordoList(long idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "serviziWithIdAccordoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().serviziWithIdAccordoList(idAccordo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Fruitore> getSoggettiWithServizioNotFruitori(long idServizio, boolean escludiSoggettiEsterni, CredenzialeTipo credenzialeTipo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getSoggettiWithServizioNotFruitori";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettiWithServizioNotFruitori(idServizio, escludiSoggettiEsterni, credenzialeTipo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Fruitore getErogatoreFruitore(long id) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getErogatoreFruitore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getErogatoreFruitore(id);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long getServizioWithSoggettoAccordoServCorr(long idSoggetto, long idAccordo, String servizioCorrelato) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioWithSoggettoAccordoServCorr";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioWithSoggettoAccordoServCorr(idSoggetto, idAccordo, servizioCorrelato);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public long getServizioWithSoggettoAccordoServCorrPt(long idSoggetto, long idAccordo, String servizioCorrelato, String portType) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioWithSoggettoAccordoServCorrPt";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioWithSoggettoAccordoServCorrPt(idSoggetto, idAccordo, servizioCorrelato, portType);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public Fruitore getServizioFruitore(long idServFru) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioFruitore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioFruitore(idServFru);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public long getServizioFruitore(IDServizio idServizio, long idSogg) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioFruitore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioFruitore(idServizio, idSogg);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public long getServizioFruitoreSoggettoFruitoreID(long idServFru) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioFruitoreSoggettoFruitoreID";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioFruitoreSoggettoFruitoreID(idServFru);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public long getServizioFruitoreServizioID(long idServFru) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioFruitoreServizioID";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServizioFruitoreServizioID(idServFru);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsServizio(long idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteSpecifica(idServizio);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public boolean existsServizio(IDServizio idSE) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteSpecifica(idSE);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}


	public boolean existsAccordoServizioParteSpecifica(IDServizio idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioParteSpecifica";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteSpecifica(idAccordo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AccordoServizioParteSpecifica getServizio(IDServizio idService) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizio";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idService);
				
			}else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAccordoServizioParteSpecifica(idService);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteSpecifica getServizio(IDServizio idService,boolean deepRead) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idService,deepRead);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	

	public AccordoServizioParteSpecifica getServizioCorrelato(IDSoggetto idSoggetto, IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServizioCorrelato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordo);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}


	
	
	public List<AccordoServizioParteSpecifica> soggettiServizioList(String superuser, ISearch ricerca,boolean [] permessiUtente, HttpSession session, HttpServletRequest request) throws DriverRegistroServiziException {
		boolean gestioneFruitori = false;
		boolean gestioneErogatori = false;
		String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				gestioneErogatori = true;
			}
		}
		return soggettiServizioList(superuser, ricerca, permessiUtente, gestioneFruitori, gestioneErogatori);

	}
	public List<AccordoServizioParteSpecifica> soggettiServizioList(String superuser, ISearch ricerca,boolean [] permessiUtente, 
			boolean gestioneFruitori, boolean gestioneErogatori) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "soggettiServizioList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettiServizioList(superuser, ricerca,permessiUtente, 
					gestioneFruitori, gestioneErogatori);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public List<IDServizio> getIdServiziWithPortType(IDPortType idPT) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getIdServiziWithPortType";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().getIdServiziWithPortType(idPT);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDServizio> getIdServiziWithAccordo(IDAccordo idAccordo,boolean checkPTisNull) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getIdServiziWithAccordo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().getIdServiziWithAccordo(idAccordo,checkPTisNull);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<AccordoServizioParteSpecifica> serviziList(ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "serviziList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().serviziList(null,ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<AccordoServizioParteSpecifica> serviziList(String superUser,ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "serviziList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().serviziList(superUser,ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	

	public long existServizio(String nomeServizio, String tipoServizio, int versioneServizio, long idSoggettoErogatore) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existServizio(nome,tipo,versione,idErogatore)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existServizio(nomeServizio, tipoServizio, versioneServizio, idSoggettoErogatore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public ValidazioneResult validazione(AccordoServizioParteSpecifica as,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validazione";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getTipoSoggettoErogatore());
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneAccordi().valida(as);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}

	public ValidazioneResult validaInterfacciaWsdlParteSpecifica(AccordoServizioParteSpecifica as,AccordoServizioParteComune apc,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validaInterfacciaWsdlParteSpecifica";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getTipoSoggettoErogatore());
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaInterfaccia(as,apc);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	public ValidazioneResult validaInterfacciaWsdlParteSpecifica(Fruitore fruitore,AccordoServizioParteSpecifica as,AccordoServizioParteComune apc,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validaInterfacciaWsdlParteSpecifica_Fruitore";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getTipoSoggettoErogatore());
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaInterfaccia(fruitore, as, apc);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}

	public  long getIdAccordoServizioParteSpecifica(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeProprietario,String tipoProprietario) throws DriverRegistroServiziException{
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizioParteSpecifica";

		long idServizio=-1;
		try
		{
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();

			idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietario, tipoProprietario, con, this.tipoDB);


		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}finally
		{
			ControlStationCore.dbM.releaseConnection(con);
		}
		return idServizio;
	}

	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioParteSpecifica";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idServizio);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<PortaApplicativa> serviziPorteAppList(String tipoServizio,String nomeServizio, Integer versioneServizio,
			long idServizio, long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "serviziPorteAppList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().serviziPorteAppList(tipoServizio,nomeServizio,versioneServizio,idServizio, idSoggettoErogatore, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MappingErogazionePortaApplicativa> mappingServiziPorteAppList(IDServizio idAccordoServizioParteSpecifica, ISearch ricerca) throws DriverConfigurazioneException, DriverRegistroServiziException {
		return mappingServiziPorteAppListEngine(idAccordoServizioParteSpecifica, this.getIdAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica), ricerca);
	}
	
	public List<MappingErogazionePortaApplicativa> mappingServiziPorteAppList(IDServizio idAccordoServizioParteSpecifica, Long idServizio, ISearch ricerca) throws DriverConfigurazioneException {
		return mappingServiziPorteAppListEngine(idAccordoServizioParteSpecifica, idServizio, ricerca);
	}
	
	private List<MappingErogazionePortaApplicativa> mappingServiziPorteAppListEngine(IDServizio idAccordoServizioParteSpecifica, Long idServizio, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "mappingServiziPorteAppList";

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			boolean orderByDescrizione = true;
			boolean ricercaUseOffsetLimit = false; // non esiste paginazione per adesso
			return DBMappingUtils.mappingErogazionePortaApplicativaList(con,this.tipoDB,idAccordoServizioParteSpecifica,idServizio, ricerca, ricercaUseOffsetLimit, orderByDescrizione);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void deleteMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativaByNome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "deleteMappingErogazione";
		try{
			con = ControlStationCore.dbM.getConnection();
			DBMappingUtils.deleteMappingErogazione(idServizio, idPortaApplicativaByNome, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isDefaultMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativaByNome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isDefaultMappingErogazione";
		try{
			con = ControlStationCore.dbM.getConnection();
			return DBMappingUtils.checkMappingErogazione(idServizio, idPortaApplicativaByNome, true, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioParteSpecifica";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idServizio,readContenutoAllegati);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Fruitore> serviziFruitoriList(long idServizi, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "serviziFruitoriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().serviziFruitoriList(idServizi, ricerca);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<Fruitore> serviziFruitoriList(Connection con, long idServizi, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziFruitoriList";
		DriverControlStationDB driver = null;

		try {
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().serviziFruitoriList(idServizi, ricerca);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}


	public IDServizio getIdAccordoServizioParteSpecifica(long idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizioParteSpecifica";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return IDServizioFactory.getInstance().getIDServizioFromAccordo(driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idAccordo,con));

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	


	public List<Fruitore> getServiziFruitoriWithServizio(long idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getServiziFruitoriWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getServiziFruitoriWithServizio(idServizio);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Documento> serviziAllegatiList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "serviziAllegatiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().serviziAllegatiList(idServizio, ricerca);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public List<PortaDelegata> serviziFruitoriPorteDelegateList(Long idSoggetto, 
			String tipoServizio, String nomeServizio, Long idServizio, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, Long idSoggettoErogatore, 
			ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "serviziPorteAppList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().serviziFruitoriPorteDelegateList(idSoggetto, 
					tipoServizio, nomeServizio, idServizio, 
					tipoSoggettoErogatore, nomeSoggettoErogatore, idSoggettoErogatore, 
					ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MappingFruizionePortaDelegata> serviziFruitoriMappingList(Long idFru, IDSoggetto idSoggettoFruitore,
			IDServizio idAccordoServizio,
			ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "serviziFruitoriMappingList";

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			boolean orderByDescrizione = true;
			boolean ricercaUseOffsetLimit = false; // non esiste paginazione per adesso
			return DBMappingUtils.mappingFruizionePortaDelegataList(con,this.tipoDB,idFru,idSoggettoFruitore,  
					idAccordoServizio, 
					ricerca, ricercaUseOffsetLimit,
					orderByDescrizione);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<MappingFruizionePortaDelegata> serviziFruitoriMappingList(IDSoggetto idSoggettoFruitore,
			IDServizio idAccordoServizio,
			ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "serviziFruitoriMappingList";

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			boolean orderByDescrizione = true;
			boolean ricercaUseOffsetLimit = false; // non esiste paginazione per adesso
			return DBMappingUtils.mappingFruizionePortaDelegataList(con,this.tipoDB,idSoggettoFruitore,  
					idAccordoServizio, 
					ricerca, ricercaUseOffsetLimit,
					orderByDescrizione);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "deleteMappingFruizione";
		try{
			con = ControlStationCore.dbM.getConnection();
			DBMappingUtils.deleteMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isDefaultMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isDefaultMappingFruizione";
		try{
			con = ControlStationCore.dbM.getConnection();
			return DBMappingUtils.checkMappingFruizione(idServizio, idFruitore, idPortaDelegata, true, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existFruizioniServizioWithoutConnettore(long idServizio, boolean escludiSoggettiEsterni) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existFruizioniServizioWithoutConnettore(idServizio,escludiSoggettiEsterni);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean filterFruitoriRispettoAutenticazione(AccordoServizioParteSpecifica asps) throws DriverConfigurazioneException{
		List<PortaApplicativa> lista = this.serviziPorteAppList(asps.getTipo(),asps.getNome(),asps.getVersione(),
				asps.getId().intValue(), asps.getIdSoggetto(), new ConsoleSearch(true));
		return lista.size()==1;
	}
	public boolean filterFruitoriRispettoAutenticazione(IDServizio idSE) throws DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException{
		AccordoServizioParteSpecifica asps = this.getServizio(idSE);
		return filterFruitoriRispettoAutenticazione(asps);
	}
	
	
	public List<IDServizio> getErogazioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws DriverControlStationException {
		String nomeMetodo = "getErogazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getErogazioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizio> getErogazioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, Integer versioneServizio, 
			String nomeAzione) throws DriverControlStationException {
		String nomeMetodo = "getErogazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getErogazioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, versioneServizio, nomeAzione);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<IDFruizione> getFruizioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws DriverControlStationException {
		String nomeMetodo = "getFruizioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getFruizioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDFruizione> getFruizioni(List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore,
			String tipoServizio ,String nomeServizio, Integer versioneServizio, 
			String nomeAzione) throws DriverControlStationException {
		String nomeMetodo = "getFruizioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getFruizioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto, tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, nomeAzione);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
