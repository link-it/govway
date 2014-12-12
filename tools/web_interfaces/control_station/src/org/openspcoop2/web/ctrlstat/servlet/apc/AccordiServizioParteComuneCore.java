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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB_LIB;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UtilitiesSQLQuery;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

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

	public long getIdAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoServizio";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DriverRegistroServiziDB_LIB.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public void deleteAzione(int idAccordo, String nomeAzione) throws DriverRegistroServiziException {
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

	public void validaStatoAccordoServizio(AccordoServizioParteComune as,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] ValidazioneStatoPackageException :" + e.getMessage(), e);
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

	public boolean showConversazioni(String tipoSoggetto,SoggettiCore soggettiCore) throws DriverRegistroServiziException{
		String nomeMetodo = "showConversazioni";
		try {

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createProtocolConfiguration().isSupportoSpecificaConversazioni();

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validazione(AccordoServizioParteComune as,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validazione";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneAccordi().valida(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validaInterfacciaWsdlParteComune(AccordoServizioParteComune as,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validaInterfacciaWsdlParteComune";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaInterfacciaWsdlParteComune(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public ValidazioneResult validaSpecificaConversazione(AccordoServizioParteComune as,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validaSpecificaConversazione";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneDocumenti().validaSpecificaConversazione(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public boolean isAccordoInUso(AccordoServizioParteComune as, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAccordoInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isAccordoInUso(as, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComune getAccordoServizio(IDAccordo idAccordo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComune getAccordoServizio(IDAccordo idAccordo,boolean deepRead) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<Documento> accordiAllegatiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<Azione> accordiAzioniList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<Azione> accordiAzioniList(int idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<Fruitore> accordiErogatoriFruitoriList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiErogatoriFruitoriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiErogatoriFruitoriList(idServizio, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<org.openspcoop2.core.registry.Soggetto> accordiErogatoriList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<org.openspcoop2.core.registry.PortType> accordiPorttypeList(int idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<Operation> accordiPorttypeOperationList(int idPortType, ISearch ricerca) throws DriverRegistroServiziException {
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

	public List<Operation> accordiPorttypeOperationList(int idPortType, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
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
	
	public List<MessagePart> accordiPorttypeOperationMessagePartList(int idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
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
	public boolean isAzioneCorrelata(int idAccordo, String nomeAzione, String owner) throws DriverRegistroServiziException {
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
	public boolean isOperationCorrelata(int idPortType, String nomeCorrelata, String owner) throws DriverRegistroServiziException {
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

	public List<AccordoServizioParteComune> accordiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	public List<AccordoServizioParteComune> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
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
	public List<AccordoServizioParteComune> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public AccordoServizioParteComune getAccordoServizio(long id) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
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







	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
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
			List<String> tipiServiziCompatibili, List<String> tipiSoggettiCompatibili){
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
			if(as.isPrivato()==false){
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
			if(as.isPrivato()==false){
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
				if(as.isPrivato()==false){
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
				if(as.isPrivato()==false){
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
					Servizio ss = asps.getServizio();

					if(tipiServiziCompatibili.contains(ss.getTipo()) && tipiSoggettiCompatibili.contains(ss.getTipoSoggettoErogatore())){
						serviziL.add(""+idServizio);
						serviziLabelL.add(ss.getTipoSoggettoErogatore()+"/"+ss.getNomeSoggettoErogatore()+"_"+ss.getTipo()+"/"+ss.getNome());
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


	public void mappingAutomatico(String protocollo , AccordoServizioParteComune as) throws DriverRegistroServiziException {
		String nomeMetodo = "mappingAutomatico";
		try {
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			protocol.createArchive().setProtocolInfo(as);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}

	public void popolaPorttypeOperationDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente) throws Exception{
		String nomeMetodo = "popolaPorttypeOperationDaUnAltroASPC";
		try {
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
						// ho trovato l'elemento, aggiorno i valori 
						vecchioPortType.setNome(nuovoPortType.getNome());
						vecchioPortType.setConfermaRicezione(nuovoPortType.getConfermaRicezione());
						vecchioPortType.setConsegnaInOrdine(nuovoPortType.getConsegnaInOrdine());
						vecchioPortType.setDescrizione(nuovoPortType.getDescrizione());
						vecchioPortType.setFiltroDuplicati(nuovoPortType.getFiltroDuplicati());
						vecchioPortType.setProfiloCollaborazione(nuovoPortType.getProfiloCollaborazione());
						vecchioPortType.setProfiloPT(nuovoPortType.getProfiloPT());
						vecchioPortType.setScadenza(nuovoPortType.getScadenza());
						vecchioPortType.setStyle(nuovoPortType.getStyle());
						
						// analisi delle azioni presenti
						if(nuovoPortType.sizeAzioneList() > 0){
							for (Operation nuovaAzione : nuovoPortType.getAzioneList()) {
								Operation oldAzione = null;
								for (Operation oldAzioneTMP : vecchioPortType.getAzioneList()) {
									if(oldAzioneTMP.getNome().equals(nuovaAzione.getNome())){
										oldAzione = oldAzioneTMP ;
										break;
									}
								}
								
								// se non esiste una azione con lo stesso nome la aggiungo
								if(oldAzione == null){
									vecchioPortType.addAzione(nuovaAzione);
								}else {
									// aggiorno la vecchia aazione
									oldAzione.setNome(nuovaAzione.getNome());
									oldAzione.setConfermaRicezione(nuovaAzione.getConfermaRicezione());
									oldAzione.setConsegnaInOrdine(nuovaAzione.getConsegnaInOrdine());
									oldAzione.setCorrelata(nuovaAzione.getCorrelata());
									oldAzione.setCorrelataServizio(nuovaAzione.getCorrelataServizio());
									oldAzione.setFiltroDuplicati(nuovaAzione.getFiltroDuplicati());
									oldAzione.setIdCollaborazione(nuovaAzione.getIdCollaborazione());
									oldAzione.setMessageInput(nuovaAzione.getMessageInput());
									oldAzione.setMessageOutput(nuovaAzione.getMessageOutput());
									oldAzione.setProfAzione(nuovaAzione.getProfAzione());
									oldAzione.setProfiloCollaborazione(nuovaAzione.getProfiloCollaborazione());
									oldAzione.setScadenza(nuovaAzione.getScadenza());
									oldAzione.setSoapAction(nuovaAzione.getSoapAction());
									oldAzione.setStyle(nuovaAzione.getStyle());
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

}
