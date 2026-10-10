/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.pdnd.async;

import java.sql.Connection;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;

/**
 * Accesso a runtime allo stato delle interazioni relative agli scambi di dati asincroni PDND.
 * 
 * Viene utilizzata la connessione associata allo stato della transazione, se disponibile; altrimenti viene
 * ottenuta (e rilasciata al termine dell'operazione) una connessione dal DBManager.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreInterazioniAsincronePDND {

	public static final String ID_MODULO = "GestoreInterazioniAsincronePDND";
	
	private final IState state;
	private final IDSoggetto idDominio;
	private final String idTransazione;
	private final String tipoDatabase;
	
	public GestoreInterazioniAsincronePDND(IState state, IDSoggetto idDominio, String idTransazione) {
		this.state = state;
		this.idDominio = idDominio;
		this.idTransazione = idTransazione;
		this.tipoDatabase = OpenSPCoop2Properties.getInstance().getDatabaseType();
	}
	
	@FunctionalInterface
	private interface Operazione<T> {
		T esegui(Connection con) throws InterazioniAsincronePDNDException;
	}
	
	private <T> T esegui(Operazione<T> operazione) throws InterazioniAsincronePDNDException {
		Connection con = null;
		if(this.state instanceof StateMessage) {
			con = StateMessage.getConnection((StateMessage) this.state);
		}
		if(con!=null) {
			return operazione.esegui(con);
		}
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		try {
			resource = dbManager.getResource(this.idDominio, ID_MODULO, this.idTransazione);
			if(resource==null || resource.getResource()==null) {
				throw new InterazioniAsincronePDNDException("Connessione alla base dati non disponibile");
			}
			return operazione.esegui((Connection) resource.getResource());
		}catch(InterazioniAsincronePDNDException e) {
			throw e;
		}catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}finally {
			if(resource!=null) {
				dbManager.releaseResource(this.idDominio, ID_MODULO, resource);
			}
		}
	}
	
	public InterazioneAsincronaPDND get(String interactionId, TipoPdD ruolo) throws InterazioniAsincronePDNDException {
		return esegui(con -> InterazioniAsincronePDNDDriverUtils.get(con, this.tipoDatabase, interactionId, ruolo));
	}
	
	public long insert(InterazioneAsincronaPDND interazione) throws InterazioniAsincronePDNDException {
		return esegui(con -> InterazioniAsincronePDNDDriverUtils.insert(con, this.tipoDatabase, interazione));
	}
	
	public boolean registraCallback(long id, IDServizio servizioCallback, Integer entityNumber, Date dataCallback, Date dataScadenza) throws InterazioniAsincronePDNDException {
		return esegui(con -> InterazioniAsincronePDNDDriverUtils.registraCallback(con, this.tipoDatabase, id, servizioCallback, entityNumber, dataCallback, dataScadenza))>0;
	}
	
	public boolean registraGetResource(long id, Date dataGetResource) throws InterazioniAsincronePDNDException {
		return esegui(con -> InterazioniAsincronePDNDDriverUtils.registraGetResource(con, this.tipoDatabase, id, dataGetResource))>0;
	}
	
	public boolean registraConfirmation(long id, Date dataConfirmation) throws InterazioniAsincronePDNDException {
		return esegui(con -> InterazioniAsincronePDNDDriverUtils.registraConfirmation(con, this.tipoDatabase, id, dataConfirmation))>0;
	}
}
