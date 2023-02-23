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

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;

/**
 * AccordoServizioParteComuneRestCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneRestCore extends ControlStationCore {

	protected AccordoServizioParteComuneRestCore(ControlStationCore core) throws Exception {
		super(core);
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
}
