/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.sdk.condition;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.constants.CRUDType;
import org.openspcoop2.monitor.sdk.constants.SearchType;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.utils.TipiDatabase;

/**
 * Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface Context {

	public SearchType getTipoRicerca();
	
	public Date getIntervalloInferiore();
	
	public Date getIntervalloSuperiore();
	
	public String getTipoSoggettoMittente();
	public String getSoggettoMittente();
	
	public String getTipoSoggettoDestinatario();
	public String getSoggettoDestinatario();
	
	public String getTipoServizio();
	public String getServizio();
	public Integer getVersioneServizio();
	
	public String getAzione();
	
	public EsitoTransazione getEsitoTransazione();
	
	public Parameter<?> getParameter(String paramID);
		
	public Map<String, Parameter<?>> getParameters();
	
	public TipiDatabase getDatabaseType();
	
	public Logger getLogger();
	
	public DAOFactory getDAOFactory();
	
	public CRUDType getTipoOperazione();
		
}
