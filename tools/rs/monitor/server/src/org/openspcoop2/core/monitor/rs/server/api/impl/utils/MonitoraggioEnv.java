/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.Search;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.config.SoggettiConfig;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ProfiloUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.slf4j.Logger;

/**
 * MonitoraggioEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MonitoraggioEnv {
	
	public final IContext context;
	public final ProfiloEnum profilo;
	public final String tipoSoggetto;
	public final String nomeSoggettoLocale;
	public final Logger log;
	public final String tipo_protocollo;
	public final ProtocolFactoryManager protocolFactoryMgr;

	public final boolean supportatoSoggettoReferenteAPI;
	public IDSoggetto soggettoReferenteAPIDefault;
	
	public MonitoraggioEnv(IContext context, ProfiloEnum profilo, String nome_soggetto, Logger log) throws UtilsException, ProtocolException {
		this.context = context;
		
		if (profilo == null) {
			this.profilo = ProfiloUtils.getMapProtocolloToProfilo().get(ServerProperties.getInstance().getProtocolloDefault());
		} else {
			this.profilo = profilo;
		}
		
		String protocollo = Converter.toProtocollo(profilo);
		
		this.tipoSoggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(protocollo);
		
		ServerProperties serverProperties = ServerProperties.getInstance();
				
		this.supportatoSoggettoReferenteAPI = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
		if(!this.supportatoSoggettoReferenteAPI) {
			this.soggettoReferenteAPIDefault = getSoggettoOperativoDefault(protocollo);
		}
		
		if (nome_soggetto == null) {
			if(serverProperties.useSoggettoDefault()) {
				nome_soggetto = serverProperties.getSoggettoDefaultIfEnabled(ProfiloUtils.toProtocollo(this.profilo));
			}
		}
				
		if (nome_soggetto != null) {
			this.nomeSoggettoLocale = nome_soggetto;
			if(!SoggettiConfig.existsIdentificativoPorta(this.tipoSoggetto, this.nomeSoggettoLocale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto '"+this.nomeSoggettoLocale+"' indicato non esiste");
			}
		}
		else {
			this.nomeSoggettoLocale = null; // non verra' attuato alcun filtro sul soggetto locale.
		}
		
		this.log = log;
		
		this.tipo_protocollo = BaseHelper.tipoProtocolloFromProfilo.get(this.profilo);
		this.protocolFactoryMgr = ProtocolFactoryManager.getInstance();
	}
	
	private static HashMap<String, IDSoggetto> map = new HashMap<String, IDSoggetto>();
	private static IDSoggetto getSoggettoOperativoDefault(String protocollo) {
		if(map.containsKey(protocollo)) {
			return map.get(protocollo);
		}
		return _getSoggettoOperativoDefault(protocollo);
	}
	private static synchronized IDSoggetto _getSoggettoOperativoDefault(String protocollo) {
		
		if(map.containsKey(protocollo)) {
			return map.get(protocollo);
		}
		
		Search s = new Search();
		s.setPageSize(Liste.SOGGETTI, 1); // serve solo per il count
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, PddTipologia.OPERATIVO.toString()); // imposto dominio
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_SOGGETTO_DEFAULT, "true"); // imposto indicazione di volere il soggetto operativo di default
		List<org.openspcoop2.core.registry.Soggetto> lista = null;
		DBManager dbManager = DBManager.getInstance();
		Connection con = null;
		try {
			con = dbManager.getConnectionConfig();
			DriverRegistroServiziDB driver = new DriverRegistroServiziDB(con, LoggerProperties.getLoggerCore(), dbManager.getServiceManagerPropertiesConfig().getDatabaseType());
			lista = driver.soggettiRegistroList(null, s);
		}
		catch(Exception e) {
			LoggerProperties.getLoggerCore().error("getSoggettoOperativoDefault("+protocollo+"): "+e.getMessage(),e);
		}
		finally {
			dbManager.releaseConnectionConfig(con);
		}
		if(lista!=null && lista.size()>0) {
			IDSoggetto id = new IDSoggetto(lista.get(0).getTipo(), lista.get(0).getNome());
			map.put(protocollo, id);
			return id;
		}
		else {
			return null;
		}
	}

}
