/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.ResourceSintetica;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLDiff;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UtilitiesSQLQuery;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaAdd;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * AccordiServizioParteComuneCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneCore extends ControlStationCore {

	public AccordiServizioParteComuneCore() throws Exception {
		super();
	}
	public AccordiServizioParteComuneCore(ControlStationCore core) throws Exception {
		super(core);
	}

	public boolean isSupportatoSoggettoReferente(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoSoggettoReferente";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public long getIdAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizio";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public void deleteAzione(long idAccordo, String nomeAzione) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "deleteAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			driver.getDriverRegistroServiziDB().deleteAzione(idAccordo, nomeAzione);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

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
				ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] ValidazioneStatoPackageException :" + e.getMessage(), e);
			}
			throw e;
		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean showWsdlDefinitorio(String protocollo,SoggettiCore soggettiCore, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showWsdlDefinitorio";
		try {
			return this.showWsdlDefinitorio(protocollo,serviceBinding, interfaceType);

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}
	
	public boolean showWsdlDefinitorio(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showWsdlDefinitorio";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoSchemaEsternoInterfaccia(serviceBinding, interfaceType);

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}
	
	public boolean showConversazioni(String protocollo,SoggettiCore soggettiCore, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showConversazioni";
		try {

			return this.showConversazioni(protocollo,serviceBinding,interfaceType);

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}
	
	public boolean showConversazioni(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showConversazioni";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoSpecificaConversazioni(serviceBinding,interfaceType);

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}
	
	public boolean showPortiAccesso(String protocollo, ServiceBinding serviceBinding, InterfaceType interfaceType) throws DriverRegistroServiziException{
		String nomeMetodo = "showPortiAccesso";
		try {

			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoPortiAccessoAccordiParteSpecifica(serviceBinding,interfaceType);

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validazione(AccordoServizioParteComune as,SoggettiCore soggettiCore, String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validazione";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneAccordi().valida(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validaInterfacciaWsdlParteComune(AccordoServizioParteComune as,SoggettiCore soggettiCore, String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validaInterfacciaWsdlParteComune";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaInterfaccia(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validaSpecificaConversazione(AccordoServizioParteComune as,SoggettiCore soggettiCore, String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "validaSpecificaConversazione";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaConversazione(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public boolean isAccordoInUso(IDAccordo idAccordo, Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAccordoInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isAccordoInUso(idAccordo, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean isAccordoInUso(AccordoServizioParteComune as, Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAccordoInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isAccordoInUso(as, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComuneSintetico getAccordoServizioSintetico(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioSintetico";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAccordoServizioParteComuneSintetico(idAccordo);
			}
			else{
				AccordoServizioParteComune aspc = GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAccordoServizioParteComune(idAccordo);
				return new AccordoServizioParteComuneSintetico(aspc);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccordoServizioParteComune getAccordoServizioFull(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizio";
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComune getAccordoServizioFull(IDAccordo idAccordo,boolean deepRead) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo,deepRead);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Azione> accordiAzioniList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiAzioniList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiAzioniList(idAccordo, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<Azione> accordiAzioniList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiAzioniList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiAzioniList(idAccordo, profiloCollaborazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiPorttypeList(idAccordo, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiPorttypeList(idAccordo, profiloCollaborazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<Operation> accordiPorttypeOperationList(long idPortType, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeOperationList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiPorttypeOperationList(idPortType, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Operation> accordiPorttypeOperationList(IDAccordo idAccordo,String nomePortType, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeOperationList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			List<Operation> list = new ArrayList<Operation>();

			AccordoServizioParteComune as = driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo);
			for(int i=0; i<as.sizePortTypeList();i++){
				PortType pt = as.getPortType(i);
				if(pt.getNome().equals(nomePortType)){
					for(int j=0; j<pt.sizeAzioneList(); j++){
						list.add(pt.getAzione(j));
					}
					break;
				}
			}

			return list;

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Operation> accordiPorttypeOperationList(long idPortType, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeOperationList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiPorttypeOperationList(idPortType, profiloCollaborazione, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MessagePart> accordiPorttypeOperationMessagePartList(long idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiPorttypeOperationMessagePartList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiPorttypeOperationMessagePartList(idOperation,isInput, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<org.openspcoop2.core.registry.Resource> accordiResourceList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiResourceList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiResourceList(idAccordo, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<org.openspcoop2.core.registry.ResourceResponse> accordiResourceResponseList(Long idRisorsa, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiResourceResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiResourceResponseList(idRisorsa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<org.openspcoop2.core.registry.ResourceRepresentation> accordiResourceRepresentationsList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiResourceRepresentationList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiResourceRepresentationsList(idRisorsa, isRequest, idRisposta, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<org.openspcoop2.core.registry.ResourceParameter> accordiResourceParametersList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiResourceParametersList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiResourceParametersList(idRisorsa, isRequest, idRisposta, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public boolean existsAccordoServizioPorttype(String nome, long idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioPorttype";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComunePorttype(nome, idAccordo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizioPorttypeOperation(String nome, long idPortType) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioPorttypeOperation";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComunePorttypeOperation(nome, idPortType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizioPorttypeOperation(String nome, IDAccordo idAccordo, String nomePortType) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioPorttypeOperation";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			AccordoServizioParteComune as = driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAccordo);
			for(int i=0; i<as.sizePortTypeList(); i++){
				PortType pt = as.getPortType(i);
				if(pt.getNome().equals(nomePortType)){
					for(int j=0; j<pt.sizeAzioneList(); j++){
						if(pt.getAzione(j).getNome().equals(nome))
							return true;
					}
				}
			}
			return false;

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsAccordoServizioResource(String nome, long idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioResource";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComuneResource(nome, idAccordo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsAccordoServizioResource(String httpMethod, String path, long idAccordo, String excludeResourceWithName) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioResource";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComuneResource(httpMethod, path, idAccordo, excludeResourceWithName);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsAccordoServizioResourceResponse(long idRisorsa, int httpStatus) throws DriverRegistroServiziException{
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioResourceResponse";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioResourceResponse(idRisorsa, httpStatus);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsAccordoServizioResourceRepresentation(Long idRisorsa, boolean isRequest, Long idResponse, String mediaType) throws DriverRegistroServiziException{
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioResourceRepresentation";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioResourceRepresentation(idRisorsa, isRequest,idResponse,mediaType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsAccordoServizioResourceParameter(Long idRisorsa, boolean isRequest, Long idResponse, ParameterType tipoParametro, String nome) throws DriverRegistroServiziException{
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioResourceParameter";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoServizioResourceParameter(idRisorsa, isRequest,idResponse,tipoParametro,nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}


	public boolean isUnicaAzioneInAccordi(String azione) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isUnicaAzioneInAccordi";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().isUnicaAzioneInAccordi(azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
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
		Connection con = null;
		String nomeMetodo = "isAzioneCorrelata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().isCorrelata(idAccordo, nomeAzione, owner);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
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
		Connection con = null;
		String nomeMetodo = "isOperationCorrelata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().isOperationCorrelata(idPortType, nomeCorrelata, owner);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isOperationCorrelata(nomePortType,idPortType,azioneDaVerificare,idAzioneDaVerificare)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean isOperationCorrelataRichiesta(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isOperationCorrelata(nomePortType,idPortType,azioneDaVerificare,idAzioneDaVerificare)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().isOperationCorrelataRichiesta(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteComuneSintetico> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizioParteComuneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiServizioParteComuneList(superuser, ricerca);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<AccordoServizioParteComuneSintetico> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizioCompostiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiServizioCompostiList(superuser, ricerca);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDAccordoDB> idAccordiServizioCompostiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "idAccordiServizioCompostiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().idAccordiServizioCompostiList(superuser, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizioAzione(long idAzione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComuneAzione(idAzione);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizioOperation(long idAzione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioOperation";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComuneOperation(idAzione);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsAccordoServizioAzione(String nome, long idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoServizioAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsAccordoServizioParteComuneAzione(nome, idAccordo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdResource";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAllIdResource(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdResource(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdPortType";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAllIdPortType(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdPortType(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortTypeAzione> getAllIdOperation(FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdOperation";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);

				return driver.getDriverRegistroServiziDB().getAllIdAzionePortType(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdAzionePortType(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComuneSintetico getAccordoServizioSintetico(long id) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAccordoServizioSintetico";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComuneSintetico(id);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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

			return driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(id);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public boolean isAzioneInUso(String nomeAzione) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAzioneInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().isAzioneInUso(nomeAzione);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}


	public List<AccordoServizioParteComune> accordiServizio_serviziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizio_serviziComponentiConSoggettoErogatore";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizio_serviziComponentiConSoggettoErogatore(idSoggetto);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteComune> accordiServizio_serviziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizio_serviziComponenti";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizio_serviziComponenti(idServizio);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizioWithAccordoCooperazione";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizioWithAccordoCooperazione(idAccordoCooperazione);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}







	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiComponentiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiComponentiList(idAccordo, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<String[]> getAccordiServizioCompostoLabels(AccordoServizioParteComune as, int idAccordoInt, String userLogin, 
			List<String> tipiServiziCompatibili, List<String> tipiSoggettiCompatibili, ConsoleHelper helper){
		String[] serviziList = null;
		String[] serviziListLabel = null;
		Connection con = null;
		String nomeMetodo = "getAccordiServizioCompostoLabels";
		try{

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(this);

			con = ControlStationCore.dbM.getConnection();

			// Servizi
			ISQLQueryObject sqlQueryObject = (new UtilitiesSQLQuery()).getSQLQueryObject();
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addSelectCountField(CostantiDB.SERVIZI + ".id", "tot", true);
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo<>?");
			if(as.getPrivato()==null || as.getPrivato()==false){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".privato=?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			if( isVisioneOggettiGlobale(userLogin)==false){
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			//voglio i servizi che non sono gia' stati usati come componenti
			ISQLQueryObject sqlQueryObjectNotExist = (new UtilitiesSQLQuery()).getSQLQueryObject();
			sqlQueryObjectNotExist.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
			sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectNotExist.setANDLogicOperator(true);

			sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectNotExist);

			String queryString = sqlQueryObject.createSQLQuery();
			PreparedStatement stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoInt);
			stmt.setLong(2, idAccordoInt);
			int index = 3;
			if(as.getPrivato()==null || as.getPrivato()==false){
				stmt.setInt(index, 0);
				index++;
			}
			if( isVisioneOggettiGlobale(userLogin)==false){
				stmt.setString(index, userLogin);
				index++;
			}
			ResultSet risultato = stmt.executeQuery();
			int totServ = 0;
			if (risultato.next()) {
				totServ = risultato.getInt("tot");
			}
			risultato.close();
			stmt.close();

			List<String> serviziL = new ArrayList<String>();
			List<String> serviziLabelL = new ArrayList<String>();
			//serviziList = new String[totServ+1];
			serviziL.add("-1");
			serviziLabelL.add("-");

			if (totServ != 0) {
				// Servizi
				sqlQueryObject = (new UtilitiesSQLQuery()).getSQLQueryObject();					
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI, "id","idServizio");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo<>?");
				if(as.getPrivato()==null || as.getPrivato()==false){
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".privato=?");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
				if( isVisioneOggettiGlobale(userLogin)==false){
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);

				//voglio i servizi che non sono gia' stati usati come componenti
				sqlQueryObjectNotExist = (new UtilitiesSQLQuery()).getSQLQueryObject();
				sqlQueryObjectNotExist.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
				sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
				sqlQueryObjectNotExist.setANDLogicOperator(true);

				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectNotExist);

				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idAccordoInt);
				stmt.setLong(2, idAccordoInt);
				index = 3;
				if(as.getPrivato()==null || as.getPrivato()==false){
					stmt.setInt(index, 0);
					index++;
				}
				if( isVisioneOggettiGlobale(userLogin)==false){
					stmt.setString(index, userLogin);
					index++;
				}
				risultato = stmt.executeQuery();
				//int i=1;
				while (risultato.next()) {
					long idServizio = risultato.getLong("idServizio");
					AccordoServizioParteSpecifica asps =  apsCore.getAccordoServizioParteSpecifica(idServizio);
					
					if(tipiServiziCompatibili.contains(asps.getTipo()) && tipiSoggettiCompatibili.contains(asps.getTipoSoggettoErogatore())){
						serviziL.add(""+idServizio);
						serviziLabelL.add(helper.getLabelIdServizio(asps));
					}

					//i++;
				}
				risultato.close();
				stmt.close();

				serviziList = serviziL.toArray(new String[1]);
				serviziListLabel = serviziLabelL.toArray(new String[1]);
			}
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
		} finally{
			ControlStationCore.dbM.releaseConnection(con);
		}

		List<String[]> toRet = new ArrayList<String[]>();
		toRet.add(serviziList);
		toRet.add(serviziListLabel);

		return toRet;
	}


	public void mappingAutomatico(String protocollo , AccordoServizioParteComune as, boolean validazioneDocumenti) throws DriverRegistroServiziException {
		String nomeMetodo = "mappingAutomatico";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			protocol.createArchive().setProtocolInfo(as);
		}catch (Exception e) {
			if(validazioneDocumenti) {
				ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
				throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			}
		}
	}

	
	public void popolaResourceDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaRisorseEsistenti, boolean eliminaRisorseNonPresentiNuovaInterfaccia, List<IDResource> risorseEliminate) throws Exception{
		String nomeMetodo = "popolaResourceDaUnAltroASPC";
		try {
			if(aspcSorgente.sizeResourceList() > 0){
				for (Resource nuovoResource : aspcSorgente.getResourceList()) {
					
					Resource vecchioResourceByMethodPath = find(nuovoResource, aspcDestinazione.getResourceList());
					
					// non ho trovato l'elemento corrente nel aspc destinazione
					if(vecchioResourceByMethodPath == null){
						
						// prima di aggiungerlo, avendolo cercato per method/path verifico che lo stesso nome non sia stato utilizzato per unl'altra risorsa
						boolean foundName = true;
						int index = 2;
						while(foundName) {
							foundName = false;
							for (Resource vecchioResourceTMP : aspcDestinazione.getResourceList()) {
								if(vecchioResourceTMP.getNome().equals(nuovoResource.getNome())){
									foundName = true;
									break;
								}
							}
							
							if(foundName) {
								nuovoResource.setNome(nuovoResource.getNome()+"_"+index);
								index++;
							}
						}
						
						aspcDestinazione.addResource(nuovoResource);
					} else {
						if(aggiornaRisorseEsistenti) {
							// ho trovato l'elemento, aggiorno i valori  rimpiazzando la risorsa
							Resource vecchiaResource = null;
							for (int i = 0; i < aspcDestinazione.sizeResourceList(); i++) {
								if(aspcDestinazione.getResource(i).getNome().equals(vecchioResourceByMethodPath.getNome())) {
									vecchiaResource = aspcDestinazione.removeResource(i);
									break;
								}
							}
							aspcDestinazione.addResource(nuovoResource);
							
							if(vecchiaResource!=null) {

								if(!CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(vecchiaResource.getProfAzione())) {
									continue;
								}

								boolean ridefinisci = false;
								if(vecchiaResource.getConfermaRicezione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getConfermaRicezione())) {
									nuovoResource.setConfermaRicezione(vecchiaResource.getConfermaRicezione());
									ridefinisci = true;
								}
								if(vecchiaResource.getConsegnaInOrdine()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getConsegnaInOrdine())) {
									nuovoResource.setConsegnaInOrdine(vecchiaResource.getConsegnaInOrdine());
									ridefinisci = true;
								}
								if(vecchiaResource.getIdCollaborazione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getIdCollaborazione())) {
									nuovoResource.setIdCollaborazione(vecchiaResource.getIdCollaborazione());
									ridefinisci = true;
								}
								if(vecchiaResource.getIdRiferimentoRichiesta()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getIdRiferimentoRichiesta())) {
									nuovoResource.setIdRiferimentoRichiesta(vecchiaResource.getIdRiferimentoRichiesta());
									ridefinisci = true;
								}
								if(vecchiaResource.getScadenza()!=null && !"".equals(vecchiaResource.getScadenza())) {
									nuovoResource.setScadenza(vecchiaResource.getScadenza());
									ridefinisci = true;
								}
								if(vecchiaResource.getDescrizione()!=null && !"".equals(vecchiaResource.getDescrizione())) {
									nuovoResource.setDescrizione(vecchiaResource.getDescrizione());
									ridefinisci = true;
								}
								// filtro duplicati gestito nel BasicArchive.setProtocolInfo
								if(vecchiaResource.getFiltroDuplicati()!=null) {
									if(!vecchiaResource.getFiltroDuplicati().equals(nuovoResource.getFiltroDuplicati())) {
										nuovoResource.setFiltroDuplicati(vecchiaResource.getFiltroDuplicati());
										ridefinisci = true;
									}
								}
								if(ridefinisci) {
									nuovoResource.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
								}
								
							}
						}
					}
				}
			}
			
			if(eliminaRisorseNonPresentiNuovaInterfaccia) {
				
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspcDestinazione);
				
				if(aspcDestinazione.sizeResourceList() > 0){
					List<Resource> risorseDaEliminare = new ArrayList<Resource>();
					for (Resource oldResource : aspcDestinazione.getResourceList()) {
						
						Resource find = find(oldResource, aspcSorgente.getResourceList());
						if(find==null) {
							risorseDaEliminare.add(oldResource);
						}
						
					}
					if(!risorseDaEliminare.isEmpty()) {
						while(risorseDaEliminare.size()>0) {
							Resource risorsaDaEliminare = risorseDaEliminare.remove(0);
							for (int i = 0; i < aspcDestinazione.sizeResourceList(); i++) {
								if(aspcDestinazione.getResource(i).getNome().equals(risorsaDaEliminare.getNome())) {
									aspcDestinazione.removeResource(i);
									IDResource idResource = new IDResource();
									idResource.setIdAccordo(idAccordo);
									idResource.setNome(risorsaDaEliminare.getNome());
									
									risorseEliminate.add(idResource);
									
									break;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		}
	}
	
	private Resource find(Resource resourceSearched, List<Resource> resources) {
		if(resources==null || resources.isEmpty()) {
			return null;
		}
		Resource resourceByMethodPath = null;
		for (Resource resourceTMP : resources) {
			
			if(resourceTMP.getMethod()==null) {
				if(resourceSearched.getMethod()!=null) {
					continue;
				}
			}else {
				if(!resourceTMP.getMethod().equals(resourceSearched.getMethod())) {
					continue;
				}
			}
			
			if(resourceTMP.getPath()==null) {
				if(resourceSearched.getPath()!=null) {
					continue;
				}
			}else {
				if(!resourceTMP.getPath().equals(resourceSearched.getPath())) {
					continue;
				}
			}
			
			resourceByMethodPath = resourceTMP;
			break;
		}	
		return resourceByMethodPath;
	}
	
	public void popolaPorttypeOperationDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaServiziAzioniEsistenti, boolean eliminaServiziAzioniNonPresentiNuovaInterfaccia,
			List<IDPortType> portTypeEliminati, List<IDPortTypeAzione> operationEliminate) throws Exception{
		String nomeMetodo = "popolaPorttypeOperationDaUnAltroASPC";
		try {
			IDAccordo idAccordo = null;
			if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspcDestinazione);
			}
			
			if(aspcSorgente.sizePortTypeList() > 0){
				for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
					PortType vecchioPortType = null;
					for (PortType vecchioPortTypeTMP : aspcDestinazione.getPortTypeList()) {
						if(vecchioPortTypeTMP.getNome().equals(nuovoPortType.getNome())){
							vecchioPortType = vecchioPortTypeTMP;
							break;
						}
					}
					
					// non ho trovato l'elemento corrente nel aspc destinazione
					if(vecchioPortType == null){
						aspcDestinazione.addPortType(nuovoPortType);
					} else {
						if(aggiornaServiziAzioniEsistenti) {
							// ho trovato l'elemento, aggiorno i valori  rimpiazzando la risorsa
							PortType oldPT = null;
							for (int i = 0; i < aspcDestinazione.sizePortTypeList(); i++) {
								if(aspcDestinazione.getPortType(i).getNome().equals(vecchioPortType.getNome())) {
									oldPT = aspcDestinazione.removePortType(i);
									break;
								}
							}
							aspcDestinazione.addPortType(nuovoPortType);
							
							if(oldPT!=null) {
								// riporto funzionalità non gestite nel BasicArchive.setProtocolInfo
								for (Operation vecchiaAzione : oldPT.getAzioneList()) {
									
									if(!CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(vecchiaAzione.getProfAzione())) {
										continue;
									}
																			
									Operation nuovaAzione = null;
									for (Operation azCheck : nuovoPortType.getAzioneList()) {
										if(azCheck.getNome().equals(vecchiaAzione.getNome())) {
											nuovaAzione = azCheck;
											break;
										}
									}
									if(nuovaAzione!=null) {
										boolean ridefinisci = false;
										if(vecchiaAzione.getConfermaRicezione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getConfermaRicezione())) {
											nuovaAzione.setConfermaRicezione(vecchiaAzione.getConfermaRicezione());
											ridefinisci = true;
										}
										if(vecchiaAzione.getConsegnaInOrdine()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getConsegnaInOrdine())) {
											nuovaAzione.setConsegnaInOrdine(vecchiaAzione.getConsegnaInOrdine());
											ridefinisci = true;
										}
										if(vecchiaAzione.getIdCollaborazione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getIdCollaborazione())) {
											nuovaAzione.setIdCollaborazione(vecchiaAzione.getIdCollaborazione());
											ridefinisci = true;
										}
										if(vecchiaAzione.getIdRiferimentoRichiesta()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getIdRiferimentoRichiesta())) {
											nuovaAzione.setIdRiferimentoRichiesta(vecchiaAzione.getIdRiferimentoRichiesta());
											ridefinisci = true;
										}
										if(vecchiaAzione.getScadenza()!=null && !"".equals(vecchiaAzione.getScadenza())) {
											nuovaAzione.setScadenza(vecchiaAzione.getScadenza());
											ridefinisci = true;
										}
										// filtro duplicati gestito nel BasicArchive.setProtocolInfo
										if(vecchiaAzione.getFiltroDuplicati()!=null) {
											if(!vecchiaAzione.getFiltroDuplicati().equals(nuovaAzione.getFiltroDuplicati())) {
												nuovaAzione.setFiltroDuplicati(vecchiaAzione.getFiltroDuplicati());
												ridefinisci = true;
											}
										}
										if(ridefinisci) {
											nuovaAzione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
											if(nuovaAzione.getProfiloCollaborazione()!=null) {
												nuovaAzione.setProfiloCollaborazione(nuovaAzione.getProfiloCollaborazione());
											}
											else if(nuovoPortType.getProfiloCollaborazione()!=null) {
												nuovaAzione.setProfiloCollaborazione(nuovoPortType.getProfiloCollaborazione());
											}
											else {
												nuovaAzione.setProfiloCollaborazione(aspcDestinazione.getProfiloCollaborazione());
											}
										}
									}
								}
							}
						
							// riporto le azioni presenti nel vecchio e non nel nuovo.
							if(nuovoPortType.sizeAzioneList() > 0){
								for (Operation vecchiaAzione : vecchioPortType.getAzioneList()) {
									boolean find = false;
									for (Operation nuovaAzione : nuovoPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(vecchiaAzione.getNome())) {
											find = true;
											break;
										}
									}
									if(!find) {
										if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
											IDPortType idPortType = new IDPortType();
											idPortType.setIdAccordo(idAccordo);
											idPortType.setNome(vecchioPortType.getNome());
											
											IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
											idPortTypeAzione.setIdPortType(idPortType);
											idPortTypeAzione.setNome(vecchiaAzione.getNome());
											operationEliminate.add(idPortTypeAzione);
										}
										else {
											nuovoPortType.addAzione(vecchiaAzione);
										}
									}
								}
							}
							else {
								if(vecchioPortType.sizeAzioneList()>0) {
									for (Operation op : vecchioPortType.getAzioneList()) {
										if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
											IDPortType idPortType = new IDPortType();
											idPortType.setIdAccordo(idAccordo);
											idPortType.setNome(vecchioPortType.getNome());
											
											IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
											idPortTypeAzione.setIdPortType(idPortType);
											idPortTypeAzione.setNome(op.getNome());
											operationEliminate.add(idPortTypeAzione);
										}
										else {
											nuovoPortType.addAzione(op);
										}
									}
								}
							}
						}
						else {
							// Aggiungo solo le azioni nuove
							if(nuovoPortType.sizeAzioneList() > 0){
								for (Operation nuovaAzione : nuovoPortType.getAzioneList()) {
									boolean find = false;
									for (Operation vecchiaAzione : vecchioPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(vecchiaAzione.getNome())) {
											find = true;
											break;
										}
									}
									if(!find) {
										vecchioPortType.addAzione(nuovaAzione);
									}
								}
							}
						}
					}
				}
			}
			if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
				
				// elimino port type non più presenti
				if(aspcDestinazione.sizePortTypeList() > 0){
					List<PortType> portTypeDaEliminare = new ArrayList<PortType>();
					for (PortType oldPortType : aspcDestinazione.getPortTypeList()) {
						
						if(aspcSorgente.sizePortTypeList() > 0){
							boolean find = false;
							for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
								if(nuovoPortType.getNome().equals(oldPortType.getNome())){
									find = true;
									break;
								}
							}
							if(!find) {
								portTypeDaEliminare.add(oldPortType);
							}
						}
						
					}
					if(!portTypeDaEliminare.isEmpty()) {
						while(portTypeDaEliminare.size()>0) {
							PortType ptDaEliminare = portTypeDaEliminare.remove(0);
							for (int i = 0; i < aspcDestinazione.sizePortTypeList(); i++) {
								if(aspcDestinazione.getPortType(i).getNome().equals(ptDaEliminare.getNome())) {
									aspcDestinazione.removePortType(i);
									
									IDPortType idPortType = new IDPortType();
									idPortType.setIdAccordo(idAccordo);
									idPortType.setNome(ptDaEliminare.getNome());
									portTypeEliminati.add(idPortType);
									
									break;
								}
							}
						}
					}
				}
				
				// elimino azioni non più presenti
				if(aspcDestinazione.sizePortTypeList() > 0){
					for (PortType oldPortType : aspcDestinazione.getPortTypeList()) {
						
						List<Operation> operationDaEliminare = new ArrayList<Operation>();
						if(oldPortType.sizeAzioneList() > 0){
							for (Operation oldOperation : oldPortType.getAzioneList()) {
								
								// find operation in new
								PortType newPortType = null;
								for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
									if(nuovoPortType.getNome().equals(oldPortType.getNome())){
										newPortType = nuovoPortType;
										break;
									}
								}
								boolean find = false;
								if(newPortType!=null && newPortType.sizeAzioneList()>0) {
									for (Operation nuovaAzione : newPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(oldOperation.getNome())) {
											find = true;
											break;
										}
									}
								}
								if(!find) {
									operationDaEliminare.add(oldOperation);
								}
							}
						}
						while(operationDaEliminare.size()>0) {
							Operation opDaEliminare = operationDaEliminare.remove(0);
							for (int i = 0; i < oldPortType.sizeAzioneList(); i++) {
								if(oldPortType.getAzione(i).getNome().equals(opDaEliminare.getNome())) {
									oldPortType.removeAzione(i);
									
									IDPortType idPortType = new IDPortType();
									idPortType.setIdAccordo(idAccordo);
									idPortType.setNome(oldPortType.getNome());
									
									IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
									idPortTypeAzione.setIdPortType(idPortType);
									idPortTypeAzione.setNome(opDaEliminare.getNome());
									operationEliminate.add(idPortTypeAzione);
									
									break;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		}
	}
	

	public void estraiSchemiFromWSDLTypesAsAllegati(AccordoServizioParteComune as, byte[] wsdl, String tipoWSDL, Hashtable<String, byte[]> schemiAggiuntiInQuestaOperazione) throws Exception{
				
		String nomeMetodo = "addSchemiFromWSDLTypesAsAllegati";
		try {
			
			// Allegati
			List<byte[]> schemiPresentiInternamenteTypes = new ArrayList<byte[]>();
			List<String> nomiSchemiPresentiInternamenteTypes = new ArrayList<String>();
			
			try{
				
				AbstractXMLUtils xmlUtils = XMLUtils.DEFAULT; 
				XSDUtils xsdUtils = new XSDUtils(xmlUtils);
				WSDLUtilities wsdlUtilities = new WSDLUtilities(xmlUtils);
				
				ArchiviCore archiviCore = null;
				
				Document dConAllegati = xmlUtils.newDocument(wsdl);
				
				Hashtable<String, String> declarationNamespacesWSDL = xmlUtils.getNamespaceDeclaration(dConAllegati.getDocumentElement());
				
				List<Node> schemi = wsdlUtilities.getSchemiXSD(dConAllegati);
				//System.out.println("SCHEMI ["+schemi.size()+"]");
				if(schemi.size()>0){
					wsdlUtilities.removeSchemiIntoTypes(dConAllegati);
					
					for (int i = 0; i < schemi.size(); i++) {
						
						Node schema = schemi.get(i);
						
						// NOTA: Volendo si potrebbe utilizzare la solita gestione anche per schemi che hanno solo gli include
						//		  Al momento dell'implementazione non avevo chiaro però se era utile avere tra gli XSD Collections (utilizzati per la validazione)
						//		  Lo schema originale che contiene gli include (per cambi di namespace)
						//		  Quindi ho preferito tenere l'originale schema con gli include tra gli schemi e qua importarlo.
						boolean schemaWithOnlyImport = xsdUtils.isSchemaWithOnlyImports(schema);
						if(schemaWithOnlyImport){
							// riaggiungo l'import
							//System.out.println("SOLO IMPORTS");
							wsdlUtilities.addSchemaIntoTypes(dConAllegati, schema);
							continue;
						}
						
						if(archiviCore==null)
							archiviCore = new ArchiviCore(this);
						
						String targetNamespace = xsdUtils.getTargetNamespace(schema);
						if(targetNamespace!=null){
											
							if(declarationNamespacesWSDL!=null && declarationNamespacesWSDL.size()>0){
								xmlUtils.addNamespaceDeclaration(declarationNamespacesWSDL, (Element) schema);
							}	
							
							String nomeSchema = null;
							if(schemiAggiuntiInQuestaOperazione!=null && schemiAggiuntiInQuestaOperazione.size()>0){
								Enumeration<String> enKeys = schemiAggiuntiInQuestaOperazione.keys();
								while (enKeys.hasMoreElements()) {
									String nomeFile = (String) enKeys.nextElement();
									byte[] content = schemiAggiuntiInQuestaOperazione.get(nomeFile);
									// check se si tratta di questo documento
									try{
										String tmp = checkXsdAlreadyExists(archiviCore, xmlUtils, content, nomeFile, schema);
										if(tmp!=null){
											nomeSchema = tmp;
											break;
										}
									}catch(Throwable t){
										log.error("Compare external failed: "+t.getMessage(),t);
									}
								}
							}
							if(nomeSchema==null){
								if(as.getByteWsdlDefinitorio()!=null){
									// check se si tratta di questo documento
									try{
										String tmp = checkXsdAlreadyExists(archiviCore, xmlUtils, as.getByteWsdlDefinitorio(), 
												Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA, schema);
										if(tmp!=null){
											nomeSchema = tmp;
										}
									}catch(Throwable t){
										log.error("Compare external failed: "+t.getMessage(),t);
									}
								}
							}
							if(nomeSchema==null){
								if(as.sizeAllegatoList()>0){
									for (Documento docTMP : as.getAllegatoList()) {
										String nomeDocumento = docTMP.getFile();
										try{
											Documento doc = archiviCore.getDocumento(docTMP.getId(),true);
											if(xsdUtils.isXSDSchema(doc.getByteContenuto())){
												// check se si tratta di questo documento
												String tmp = checkXsdAlreadyExists(archiviCore, xmlUtils, doc.getByteContenuto(), nomeDocumento, schema);
												if(tmp!=null){
													nomeSchema = tmp;
													break;
												}
											}
										}catch(Throwable t){
											log.error("Compare external failed: "+t.getMessage(),t);
										}
									}
								}
							}
							if(nomeSchema==null){
								if(as.sizeSpecificaSemiformaleList()>0){
									for (Documento docTMP : as.getSpecificaSemiformaleList()) {
										String nomeDocumento = docTMP.getFile();
										try{
											Documento doc = archiviCore.getDocumento(docTMP.getId(),true);
											if(xsdUtils.isXSDSchema(doc.getByteContenuto())){
												// check se si tratta di questo documento
												String tmp = checkXsdAlreadyExists(archiviCore, xmlUtils, doc.getByteContenuto(), nomeDocumento, schema);
												if(tmp!=null){
													nomeSchema = tmp;
													break;
												}
											}
										}catch(Throwable t){
											log.error("Compare external failed: "+t.getMessage(),t);
										}
									}
								}
							}
							
							boolean alreadyExistsSchema = (nomeSchema!=null);
							
							if(nomeSchema==null){
								// build new nome
								int index = 1;
								nomeSchema = "WsdlTypes_"+(index)+".xsd";
								while(index<10000){ // 10000 allegati??
									boolean found = false;
									if(nomiSchemiPresentiInternamenteTypes.contains(nomeSchema)){
										found = true;
									}
									else{
										for (Documento vecchioAllegatoTMP : as.getAllegatoList()) {
											if(vecchioAllegatoTMP.getFile().startsWith("WsdlTypes_") && vecchioAllegatoTMP.getFile().equals(nomeSchema)){
												found = true;
												break;
											}
										}
									}
									if(!found){
										break;
									}
									index++;
									nomeSchema = "WsdlTypes_"+(index)+".xsd";
								}
								//System.out.println("CALCOLATO ["+nomeSchema+"]");
							}
							
							//System.out.println("ADD MODIFICATO");
							wsdlUtilities.addImportSchemaIntoTypes(dConAllegati, targetNamespace, nomeSchema);
							
							if(alreadyExistsSchema==false){
								
								nomiSchemiPresentiInternamenteTypes.add(nomeSchema);
								schemiPresentiInternamenteTypes.add(xmlUtils.toByteArray(schema));

							}
														
						}
						else{
							try{
								log.error("Presente schema senza targetNamespace? (Viene usato l'originale) ["+xmlUtils.toString(schema)+"]");
							}catch(Throwable t){
								log.error("Presente schema senza targetNamespace? (Viene usato l'originale)");
							}
							//System.out.println("NON VALIDO??? RIPRISTINO ORIGINALE");
							wsdlUtilities.addSchemaIntoTypes(dConAllegati, schema);
						}
						
					}
					
					// Aggiorno WSDL
					
					byte [] wsdlPulito = xmlUtils.toByteArray(dConAllegati);
					
					if(AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE.equals(tipoWSDL)){
						as.setByteWsdlConcettuale(wsdlPulito);
					}
					else if(AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE.equals(tipoWSDL)){
						as.setByteWsdlLogicoErogatore(wsdlPulito);
					}
					else if(AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE.equals(tipoWSDL)){
						as.setByteWsdlLogicoFruitore(wsdlPulito);
					}
				}
				
			}catch(Throwable t){
				log.error("Errore durante la lettura degli schemi presenti all'interno del wsdl: "+t.getMessage(),t);
			}
			
			if(nomiSchemiPresentiInternamenteTypes.size() > 0){
				int i = 0;
				for (String nomeNuovoAllegato : nomiSchemiPresentiInternamenteTypes) {
					
					try{
					
						if(schemiAggiuntiInQuestaOperazione!=null){
							schemiAggiuntiInQuestaOperazione.put(nomeNuovoAllegato, schemiPresentiInternamenteTypes.get(i));
						}
						
						Documento vecchioAllegato = null;
						for (Documento vecchioAllegatoTMP : as.getAllegatoList()) {
							if(vecchioAllegatoTMP.getFile().equals(nomeNuovoAllegato)){
								vecchioAllegato = vecchioAllegatoTMP;
								break;
							}
						}
						
						// non ho trovato l'elemento corrente nel aspc destinazione
						if(vecchioAllegato == null){
							Documento allegato = new Documento();
							allegato.setRuolo(RuoliDocumento.allegato.toString());
							allegato.setByteContenuto(schemiPresentiInternamenteTypes.get(i));
							allegato.setFile(nomeNuovoAllegato);
							allegato.setTipo("xsd");
							allegato.setIdProprietarioDocumento(as.getId());
							as.addAllegato(allegato);
						} else {
							
							// CASO CHE NON DOVREBBE OCCORRERE MAI VISTO LA LOGICA CON XMLDIFF
							
							// ho trovato l'elemento, aggiorno i valori 
							vecchioAllegato.setByteContenuto(schemiPresentiInternamenteTypes.get(i));
						}
						
						i++;
						
					}catch(Throwable t){
						log.error("Errore durante l'aggiornamento dello schema ["+nomeNuovoAllegato+"] estratto dal wsdl: "+t.getMessage(),t);
					}
				}
			}
			
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		}
	}
	
	private String checkXsdAlreadyExists(ArchiviCore archiviCore,AbstractXMLUtils xmlUtils, byte[] xsdEsistenteContenuto, String xsdEsistenteNome,  Node schema){
		// check se si tratta di questo documento
		try{
			Node n = xmlUtils.newElement(xsdEsistenteContenuto);
			XMLDiff xmlDiff = new XMLDiff(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
//			System.out.println("["+vecchioAllegatoTMP.getFile()+"] N:"+xmlUtils.toString(n));
//			System.out.println("["+vecchioAllegatoTMP.getFile()+"] Schema:"+xmlUtils.toString(schema));
			// NOTA: la ricostruzione in N2 è necessaria, poichè schema si porta dietro il definition wsdl (non ho capito perchè)
			Node n2 = xmlUtils.newElement(xmlUtils.toString(schema).getBytes());
//			System.out.println("["+vecchioAllegatoTMP.getFile()+"] N2:"+xmlUtils.toString(n2));
			if(xmlDiff.diff(n, n2)){
				return xsdEsistenteNome;
				//System.out.println("["+vecchioAllegatoTMP.getFile()+"] TROVATO UGUALE ["+nomeSchema+"]");
			}
//			else{
//				System.out.println("["+vecchioAllegatoTMP.getFile()+"] TROVATO NON UGUALE: \n"+xmlDiff.getDifferenceDetails());
//			}
			return null;
		}catch(Throwable t){
			log.error("Compare failed: "+t.getMessage(),t);
			return null;
		}
	}
	
	public String readEndpoint(AccordoServizioParteComuneSintetico as, String portTypeParam, String servcorr, 
			BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru) {
		if(as==null) {
			return null;
		}
		String portType = portTypeParam;
		if("".equals(portTypeParam)) {
			portType = null;
		}
		try {
			FormatoSpecifica formato = as.getFormatoSpecifica();
			String urlSuggerita = null;
			switch (formato) {
			case OPEN_API_3:
			case SWAGGER_2:
			case WADL:
				if(as.getByteWsdlConcettuale()!=null) {
					IApiReader apiReader = null;
					if(FormatoSpecifica.OPEN_API_3.equals(formato)) {
						apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
					}
					else if(FormatoSpecifica.SWAGGER_2.equals(formato)) {
						apiReader = ApiFactory.newApiReader(ApiFormats.SWAGGER_2);
					}
					else {
						apiReader = ApiFactory.newApiReader(ApiFormats.WADL);
					}
					ApiReaderConfig config = new ApiReaderConfig();
					config.setProcessInclude(false);
					config.setProcessInlineSchema(false);
					apiReader.init(LoggerWrapperFactory.getLogger(AccordiServizioParteSpecificaAdd.class), as.getByteWsdlConcettuale(), config);
					Api api = apiReader.read();
					if(api.getBaseURL()!=null) {
						urlSuggerita = api.getBaseURL().toString();
					}
				}
				break;
			case WSDL_11:
				byte [] wsdl = null;
				if(ServletUtils.isCheckBoxEnabled(servcorr)) {
					if(wsdlimplfru!=null && wsdlimplfru.getValue()!=null) {
						wsdl = wsdlimplfru.getValue();
					}
					else {
						wsdl = as.getByteWsdlLogicoFruitore();
					}
				}
				else{
					if(wsdlimpler!=null && wsdlimpler.getValue()!=null) {
						wsdl = wsdlimpler.getValue();
					}
					else {
						wsdl = as.getByteWsdlLogicoErogatore();
					}
				}
				if(wsdl==null) {
					wsdl = as.getByteWsdlConcettuale();
				}
				if(wsdl!=null) {
					WSDLUtilities utilities = new WSDLUtilities(org.openspcoop2.utils.xml.XMLUtils.getInstance());
					urlSuggerita = utilities.getServiceEndpoint(wsdl, portType);
				}
				break;
			}
			return urlSuggerita;
		}catch(Throwable t){
			if( portType!=null || !FormatoSpecifica.WSDL_11.equals(as.getFormatoSpecifica()) ) {
				log.error("Read endpoint from interface failed: "+t.getMessage(),t);
			}
			else {
				log.debug("Read endpoint from interface failed: "+t.getMessage(),t);
			}
			return null;
		}
	}
	
	public String getDettagliAccordoInUso(IDAccordo idAccordo) throws DriverRegistroServiziException {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean apcInUso  = this.isAccordoInUso(idAccordo, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(apcInUso) {
			String s = DBOggettiInUsoUtils.toString(idAccordo, whereIsInUso, false, "\n", normalizeObjectIds);
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ApiCostanti.LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isRisorsaInUso(IDResource idRisorsa, HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isRisorsaInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isRisorsaInUso(idRisorsa, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliRisorsaInUso(IDResource idResource) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean risorsaInUso  = this.isRisorsaInUso(idResource, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(risorsaInUso) {
			
			// traduco nomeRisorsa in path
			AccordoServizioParteComuneSintetico as = this.getAccordoServizioSintetico(idResource.getIdAccordo());			
			String methodPath = null;
			if(as.getResource()!=null) {
				for (int j = 0; j < as.getResource().size(); j++) {
					ResourceSintetica risorsa = as.getResource().get(j);
					if (idResource.getNome().equals(risorsa.getNome())) {
						try {
							methodPath = NamingUtils.getLabelResource(risorsa);
						}catch(Exception e) {
							throw new DriverRegistroServiziException(e.getMessage(),e);
						}
						break;
					}
				}
			}
			if(methodPath==null) {
				methodPath = idResource.getNome();
			}
			
			String s = DBOggettiInUsoUtils.toString(idResource, methodPath, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(AccordiServizioParteComuneCostanti.LABEL_IN_USO_ACCORDO_RISORSA_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isPortTypeInUso(IDPortType idPT, HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isPortTypeInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isPortTypeInUso(idPT, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliPortTypeInUso(IDPortType idPT) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean ptInUso  = this.isPortTypeInUso(idPT, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(ptInUso) {
			String s = DBOggettiInUsoUtils.toString(idPT, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(AccordiServizioParteComuneCostanti.LABEL_IN_USO_ACCORDO_PORT_TYPE_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isOperazioneInUso(IDPortTypeAzione idOperazione, HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isOperazioneInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isOperazioneInUso(idOperazione, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliOperazioneInUso(IDPortTypeAzione idOperazione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean risorsaInUso  = this.isOperazioneInUso(idOperazione, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(risorsaInUso) {
			String s = DBOggettiInUsoUtils.toString(idOperazione, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(AccordiServizioParteComuneCostanti.LABEL_IN_USO_ACCORDO_OPERAZIONE_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
}
