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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.ResourceSintetica;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;

/**
 * AccordoServizioParteComuneInUsoCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneInUsoCore extends ControlStationCore {

	private AccordoServizioParteComuneSinteticoCore sinteticoCore;
	
	protected AccordoServizioParteComuneInUsoCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
		this.sinteticoCore = new AccordoServizioParteComuneSinteticoCore(core);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliAccordoInUso(IDAccordo idAccordo) throws DriverRegistroServiziException {
		EnumMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new EnumMap<>(ErrorsHandlerCostant.class);
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
	
	public boolean isRisorsaInUso(IDResource idRisorsa, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliRisorsaInUso(IDResource idResource) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		EnumMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new EnumMap<>(ErrorsHandlerCostant.class);
		boolean normalizeObjectIds = true;
		boolean risorsaInUso  = this.isRisorsaInUso(idResource, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(risorsaInUso) {
			
			// traduco nomeRisorsa in path
			AccordoServizioParteComuneSintetico as = this.sinteticoCore.getAccordoServizioSintetico(idResource.getIdAccordo());			
			String methodPath = getMethodPath(idResource, as);
			
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
	private String getMethodPath(IDResource idResource, AccordoServizioParteComuneSintetico as) throws DriverRegistroServiziException {
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
		return methodPath;
	}
	
	public boolean isPortTypeInUso(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliPortTypeInUso(IDPortType idPT) throws DriverRegistroServiziException {
		EnumMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new EnumMap<>(ErrorsHandlerCostant.class);
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
	
	public boolean isOperazioneInUso(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String getDettagliOperazioneInUso(IDPortTypeAzione idOperazione) throws DriverRegistroServiziException {
		EnumMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new EnumMap<>(ErrorsHandlerCostant.class);
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
