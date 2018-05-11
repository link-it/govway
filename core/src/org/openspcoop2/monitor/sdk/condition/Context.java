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
	
	public String getAzione();
	
	public EsitoTransazione getEsitoTransazione();
	
	public Parameter<?> getParameter(String paramID);
		
	public Map<String, Parameter<?>> getParameters();
	
	public TipiDatabase getDatabaseType();
	
	public Logger getLogger();
	
	public DAOFactory getDAOFactory();
	
	public CRUDType getTipoOperazione();
		
}
