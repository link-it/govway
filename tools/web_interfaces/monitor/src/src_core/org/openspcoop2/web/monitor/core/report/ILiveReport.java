package org.openspcoop2.web.monitor.core.report;

import java.util.Date;

import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;

/***
 * Interfaccia che descrive i metodi per avere i report Live.
 * 
 * @author pintori
 *
 */
public interface ILiveReport {

	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,String periodo,String contesto, String protocollo);
}
