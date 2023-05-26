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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;

/**
 * AccordiServizioParteComuneCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneCore extends ControlStationCore {

	private AccordoServizioParteComuneSoapCore soapCore;
	private AccordoServizioParteComuneRestCore restCore;
	private AccordoServizioParteComuneInUsoCore inUsoCore;
	private AccordoServizioParteComuneSinteticoCore sinteticoCore;
	private AccordoServizioParteComuneServiziCompostiCore serviziCompostiCore;
	private AccordoServizioParteComuneMappingCore mappingCore;
	
	public AccordiServizioParteComuneCore() throws DriverControlStationException {
		super();
		this.soapCore = new AccordoServizioParteComuneSoapCore(this);
		this.restCore = new AccordoServizioParteComuneRestCore(this);
		this.inUsoCore = new AccordoServizioParteComuneInUsoCore(this);
		this.sinteticoCore = new AccordoServizioParteComuneSinteticoCore(this);
		this.serviziCompostiCore = new AccordoServizioParteComuneServiziCompostiCore(this);
		this.mappingCore = new AccordoServizioParteComuneMappingCore(this);
	}
	public AccordiServizioParteComuneCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
		this.soapCore = new AccordoServizioParteComuneSoapCore(this);
		this.restCore = new AccordoServizioParteComuneRestCore(this);
		this.inUsoCore = new AccordoServizioParteComuneInUsoCore(this);
		this.sinteticoCore = new AccordoServizioParteComuneSinteticoCore(this);
		this.serviziCompostiCore = new AccordoServizioParteComuneServiziCompostiCore(this);
		this.mappingCore = new AccordoServizioParteComuneMappingCore(this);
	}

	/* Opzioni */
	
	public boolean isSupportatoSoggettoReferente(String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoSoggettoReferente";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public boolean showWsdlDefinitorio(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showWsdlDefinitorio";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoSchemaEsternoInterfaccia(serviceBinding, interfaceType);

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	public boolean showConversazioni(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showConversazioni";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoSpecificaConversazioni(serviceBinding,interfaceType);

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	public boolean showPortiAccesso(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showPortiAccesso";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoPortiAccessoAccordiParteSpecifica(serviceBinding,interfaceType);

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	
	/* Validazione */
	
	public void validaStatoAccordoServizio(AccordoServizioParteComune as,boolean utilizzoAzioniDiretteInAccordoAbilitato, boolean logValidazioneError) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
		Connection con = null;
		String nomeMetodo = "validaStatoAccordoServizio";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			driver.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);

		} catch (ValidazioneStatoPackageException e) {
			if(logValidazioneError) {
				ControlStationCore.logError("[ControlStationCore::" + nomeMetodo + "] ValidazioneStatoPackageException :" + e.getMessage(), e);
			}
			throw e;
		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.logError("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ValidazioneResult validazione(AccordoServizioParteComune as,String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validazione";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneAccordi().valida(as);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}

	public ValidazioneResult validaInterfacciaWsdlParteComune(AccordoServizioParteComune as,String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validaInterfacciaWsdlParteComune";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaInterfaccia(as);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}

	public ValidazioneResult validaSpecificaConversazione(AccordoServizioParteComune as,String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validaSpecificaConversazione";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaConversazione(as);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		}
	}
	
	
	
	/* InUso */
	
	public boolean isAccordoInUso(IDAccordo idAccordo, Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return this.inUsoCore.isAccordoInUso(idAccordo, whereIsInUso, normalizeObjectIds);
	}
	
	public boolean isAccordoInUso(AccordoServizioParteComune as, Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return this.inUsoCore.isAccordoInUso(as, whereIsInUso, normalizeObjectIds);
	}
	
	
	
	/* Gesitone accordi */
	
	public long getIdAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizio";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AccordoServizioParteComune getAccordoServizioFull(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioFull";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAccordoServizioParteComune(idAccordo);
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

	public AccordoServizioParteComune getAccordoServizioFull(IDAccordo idAccordo,boolean deepRead) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioFull(idAccordo,deepRead)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo,deepRead,deepRead);
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

	public AccordoServizioParteComune[] getAllIdAccordiWithSoggettoReferente(IDSoggetto idsoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		String nomeMetodo = "getAllIdAccordiWithSoggettoReferente";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAllIdAccordiWithSoggettoReferente(idsoggetto);

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

	public List<Documento> accordiAllegatiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiAllegatiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiAllegatiList(idAccordo, ricerca);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<org.openspcoop2.core.registry.Soggetto> accordiErogatoriList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiErogatoriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiErogatoriList(idAccordo, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDAccordoDB> idAccordiServizioParteComuneList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "idAccordiServizioParteComuneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().idAccordiServizioParteComuneList(superuser, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
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
	

	public boolean existsAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComune(idAccordo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDAccordo> getAllIdAccordiServizio(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdAccordiServizio";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAllIdAccordiServizioParteComune(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdAccordiServizioParteComune(filtroRicerca);
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

	public IDAccordo getIdAccordoServizio(long id) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getIdAccordoServizioParteComune(id);

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
	
	public AccordoServizioParteComune getAccordoServizioFull(long id) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			boolean readContenutoAllegati = false;
			boolean readDatiRegistro = true; // servono nelle varie pagine della console
			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(id, readContenutoAllegati, readDatiRegistro);

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
	
	public int getAccordoServizioParteComuneNextVersion(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioParteComuneNextVersion";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComuneNextVersion(idAccordo);

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

	
	/* Gesitone accordi SOAP */
	
	public void deleteAzione(long idAccordo, String nomeAzione) throws DriverRegistroServiziException {
		this.soapCore.deleteAzione(idAccordo, nomeAzione);
	}
	
	public List<Azione> accordiAzioniList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiAzioniList(idAccordo, ricerca);
	}

	public List<Azione> accordiAzioniList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiAzioniList(idAccordo, profiloCollaborazione, ricerca);
	}
	
	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiPorttypeList(idAccordo, ricerca);
	}

	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiPorttypeList(idAccordo, profiloCollaborazione, ricerca);
	}

	public List<Operation> accordiPorttypeOperationList(long idPortType, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiPorttypeOperationList(idPortType, ricerca);
	}

	public List<Operation> accordiPorttypeOperationList(long idPortType, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiPorttypeOperationList(idPortType, profiloCollaborazione, ricerca);
	}
	
	public List<MessagePart> accordiPorttypeOperationMessagePartList(long idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soapCore.accordiPorttypeOperationMessagePartList(idOperation, isInput, ricerca);
	}

	public boolean existsAccordoServizioPorttype(String nome, long idAccordo) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioPorttype(nome, idAccordo);
	}

	public boolean existsAccordoServizioPorttypeOperation(String nome, long idPortType) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioPorttypeOperation(nome, idPortType);
	}

	public boolean existsAccordoServizioPorttypeOperation(String nome, IDAccordo idAccordo, String nomePortType) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioPorttypeOperation(nome, idAccordo, nomePortType);
	}
	
	public boolean isUnicaAzioneInAccordi(String azione) throws DriverRegistroServiziException {
		return this.soapCore.isUnicaAzioneInAccordi(azione);
	}

	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione,
	 * dell'accordo con id <i>idAccordo</i>, diversa da <i>owner</i>
	 * 
	 * @param idAccordo
	 *            L'id dell'Accordo
	 * @param nomeAzione
	 *            Il nome dell'azione correlata da cercare
	 * @param owner
	 *            Il nome dell'azione che contiene <i>nomeAzione</i> come
	 *            correlata
	 * @return true se l'azione e' usata come correlata, false altrimenti
	 * @throws DriverRegistroServiziException
	 */
	public boolean isAzioneCorrelata(long idAccordo, String nomeAzione, String owner) throws DriverRegistroServiziException {
		return this.soapCore.isAzioneCorrelata(idAccordo, nomeAzione, owner);
	}

	/**
	 * Controlla se l'operation e' usata come Operation correlata in qualche
	 * Operation del port-type con id <i>idPortType</i> diversa da <i>owner</i>
	 * 
	 * @param idPortType
	 *            L'id del port-type a cui appartengono le operation
	 * @param nomeCorrelata
	 *            Il nome dell'operation correlata da cercare
	 * @param owner
	 *            Il nome dell'operation che contiene come correlata
	 *            <i>nomeCorrelata</i>
	 * @return true se l'operazione e' usata come correlata, false altrimenti
	 * @throws DriverRegistroServiziException
	 */
	public boolean isOperationCorrelata(long idPortType, String nomeCorrelata, String owner) throws DriverRegistroServiziException {
		return this.soapCore.isOperationCorrelata(idPortType, nomeCorrelata, owner);
	}

	public boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.soapCore.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
	}

	public boolean isOperationCorrelataRichiesta(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.soapCore.isOperationCorrelataRichiesta(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
	}
	
	public boolean existsAccordoServizioAzione(long idAzione) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioAzione(idAzione);
	}

	public boolean existsAccordoServizioOperation(long idAzione) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioOperation(idAzione);
	}

	public boolean existsAccordoServizioAzione(String nome, long idAccordo) throws DriverRegistroServiziException {
		return this.soapCore.existsAccordoServizioAzione(nome, idAccordo);
	}
	
	public List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.soapCore.getAllIdPortType(filtroRicerca);
	}
	
	public List<IDPortTypeAzione> getAllIdOperation(FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.soapCore.getAllIdOperation(filtroRicerca);
	}
	
	
	/* Gesitone accordi REST */
	
	public List<org.openspcoop2.core.registry.Resource> accordiResourceList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.restCore.accordiResourceList(idAccordo, ricerca);
	}
	
	public List<org.openspcoop2.core.registry.ResourceResponse> accordiResourceResponseList(Long idRisorsa, ISearch ricerca) throws DriverRegistroServiziException {
		return this.restCore.accordiResourceResponseList(idRisorsa, ricerca);
	}
	
	public List<org.openspcoop2.core.registry.ResourceRepresentation> accordiResourceRepresentationsList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca) throws DriverRegistroServiziException {
		return this.restCore.accordiResourceRepresentationsList(idRisorsa, isRequest, idRisposta, ricerca);
	}
	
	public List<org.openspcoop2.core.registry.ResourceParameter> accordiResourceParametersList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca) throws DriverRegistroServiziException {
		return this.restCore.accordiResourceParametersList(idRisorsa, isRequest, idRisposta, ricerca);
	}
	
	public boolean existsAccordoServizioResource(String nome, long idAccordo) throws DriverRegistroServiziException {
		return this.restCore.existsAccordoServizioResource(nome, idAccordo);
	}
	
	public boolean existsAccordoServizioResource(String httpMethod, String path, long idAccordo, String excludeResourceWithName) throws DriverRegistroServiziException {
		return this.restCore.existsAccordoServizioResource(httpMethod, path, idAccordo, excludeResourceWithName);
	}
	
	public boolean existsAccordoServizioResourceResponse(long idRisorsa, int httpStatus) throws DriverRegistroServiziException{
		return this.restCore.existsAccordoServizioResourceResponse(idRisorsa, httpStatus);
	}
	
	public boolean existsAccordoServizioResourceRepresentation(Long idRisorsa, boolean isRequest, Long idResponse, String mediaType) throws DriverRegistroServiziException{
		return this.restCore.existsAccordoServizioResourceRepresentation(idRisorsa, isRequest, idResponse, mediaType);
	}
	
	public boolean existsAccordoServizioResourceParameter(Long idRisorsa, boolean isRequest, Long idResponse, ParameterType tipoParametro, String nome) throws DriverRegistroServiziException{
		return this.restCore.existsAccordoServizioResourceParameter(idRisorsa, isRequest, idResponse, tipoParametro, nome);
	}
	
	public List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.restCore.getAllIdResource(filtroRicerca);
	}
	
	
	/* Gesitone accordi Sintetici */
	
	public AccordoServizioParteComuneSintetico getAccordoServizioSintetico(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		return this.sinteticoCore.getAccordoServizioSintetico(idAccordo);
	}
	
	public List<AccordoServizioParteComuneSintetico> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.sinteticoCore.accordiServizioParteComuneList(superuser, ricerca);
	}
	public List<AccordoServizioParteComuneSintetico> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.sinteticoCore.accordiServizioCompostiList(superuser, ricerca);
	}
	
	public AccordoServizioParteComuneSintetico getAccordoServizioSintetico(long id) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		return this.sinteticoCore.getAccordoServizioSintetico(id);
	}
	
	
	
	/* Gesitone accordi servizi composti */
	
	public List<IDAccordoDB> idAccordiServizioCompostiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return this.serviziCompostiCore.idAccordiServizioCompostiList(superuser, ricerca, soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	
	public List<AccordoServizioParteComune> accordiServizioServiziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.serviziCompostiCore.accordiServizioServiziComponentiConSoggettoErogatore(idSoggetto);
	}

	public List<AccordoServizioParteComune> accordiServizioServiziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		return this.serviziCompostiCore.accordiServizioServiziComponenti(idServizio);
	}

	public List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		return this.serviziCompostiCore.accordiServizioWithAccordoCooperazione(idAccordoCooperazione);
	}

	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.serviziCompostiCore.accordiComponentiList(idAccordo, ricerca);
	}

	public List<String[]> getAccordiServizioCompostoLabels(AccordoServizioParteComune as, long idAccordoLong, String userLogin, 
			List<String> tipiServiziCompatibili, List<String> tipiSoggettiCompatibili, ConsoleHelper helper){
		return this.serviziCompostiCore.getAccordiServizioCompostoLabels(as, idAccordoLong, userLogin, tipiServiziCompatibili, tipiSoggettiCompatibili, helper);
	}

	
	
	
	/* Gesitone accordi rispetto a interfacce e risorse esterne */

	public void mappingAutomatico(String protocollo , AccordoServizioParteComune as, boolean validazioneDocumenti) throws DriverRegistroServiziException {
		this.mappingCore.mappingAutomatico(protocollo, as, validazioneDocumenti);
	}

	public void popolaResourceDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaRisorseEsistenti, boolean eliminaRisorseNonPresentiNuovaInterfaccia, List<IDResource> risorseEliminate) throws DriverRegistroServiziException{
		this.mappingCore.popolaResourceDaUnAltroASPC(aspcDestinazione, aspcSorgente, 
				aggiornaRisorseEsistenti, eliminaRisorseNonPresentiNuovaInterfaccia, risorseEliminate);
	}
		
	public void popolaPorttypeOperationDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaServiziAzioniEsistenti, boolean eliminaServiziAzioniNonPresentiNuovaInterfaccia,
			List<IDPortType> portTypeEliminati, List<IDPortTypeAzione> operationEliminate) throws DriverRegistroServiziException{
		this.mappingCore.popolaPorttypeOperationDaUnAltroASPC(aspcDestinazione, aspcSorgente, 
				aggiornaServiziAzioniEsistenti, eliminaServiziAzioniNonPresentiNuovaInterfaccia, 
				portTypeEliminati, operationEliminate);
	}
	
	public void estraiSchemiFromWSDLTypesAsAllegati(AccordoServizioParteComune as, byte[] wsdl, String tipoWSDL, Map<String, byte[]> schemiAggiuntiInQuestaOperazione) throws Exception{
		this.mappingCore.estraiSchemiFromWSDLTypesAsAllegati(as, wsdl, tipoWSDL, schemiAggiuntiInQuestaOperazione);
	}
	
	public String readEndpoint(AccordoServizioParteComuneSintetico as, String portTypeParam, String servcorr, 
			BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru) {
		return this.mappingCore.readEndpoint(as, portTypeParam, servcorr, wsdlimpler, wsdlimplfru);
	}
	
	
	
	
	/* In uso */
	
	public String getDettagliAccordoInUso(IDAccordo idAccordo) throws DriverRegistroServiziException {
		return this.inUsoCore.getDettagliAccordoInUso(idAccordo);
	}
	
	public boolean isRisorsaInUso(IDResource idRisorsa, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return this.inUsoCore.isRisorsaInUso(idRisorsa, whereIsInUso, normalizeObjectIds);
	}
	
	public String getDettagliRisorsaInUso(IDResource idResource) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		return this.inUsoCore.getDettagliRisorsaInUso(idResource);
	}
	
	public boolean isPortTypeInUso(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return this.inUsoCore.isPortTypeInUso(idPT, whereIsInUso, normalizeObjectIds);
	}
	
	public String getDettagliPortTypeInUso(IDPortType idPT) throws DriverRegistroServiziException {
		return this.inUsoCore.getDettagliPortTypeInUso(idPT);
	}
	
	public boolean isOperazioneInUso(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return this.inUsoCore.isOperazioneInUso(idOperazione, whereIsInUso, normalizeObjectIds);
	}
	
	public String getDettagliOperazioneInUso(IDPortTypeAzione idOperazione) throws DriverRegistroServiziException {
		return this.inUsoCore.getDettagliOperazioneInUso(idOperazione);
	}
	
}
