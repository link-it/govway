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
package org.openspcoop2.web.monitor.transazioni.dao;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.transazioni.bean.DumpMessaggioBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;

/**
 * ITransazioniService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface ITransazioniService extends ISearchFormService<TransazioneBean, String, TransazioniSearchForm> {

	public List<TransazioneBean> findAllLive();

	public TransazioneBean findByIdTransazione(String id) throws Exception;

	/**
	 * Recupera gli esiti sul numero transazioni (ok,ko) nell'intervallo
	 * specificato
	 * 
	 * @param permessiUtente
	 * @param min
	 * @param max
	 * @param esitoContesto
	 * @return esiti sul numero transazioni (ok,ko) nell'intervallo
	 */
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max, String esitoContesto,
			String protocolloSelected, String protocolloDefault, TipologiaRicerca tipologiaRicerca);

	/**
	 * Recupera le informazioni per la live view
	 * 
	 * @return L'oggetto contenente le informazioni sul numero di transazioni corrette/errate
	 */
	public ResLive getEsitiInfoLive(PermessiUtenteOperatore permessiUtente, Date lastDatePick,String protocolloSelected, String protocolloDefault);

	public boolean hasInfoDumpAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio);

	public boolean hasInfoHeaderTrasportoAvailable(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio);
	
	public String getContentTypeMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio);
	
	public Long getContentLengthMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio);

	public DumpMessaggio getDumpMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) throws Exception;
	
	public InputStream getContentInputStream(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio) throws Exception;
	
	public int countDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore);
	
	public Date getDataConsegnaErogatore(String idTransazione, String saErogatore, Date dataAccettazione) ;
	
	public List<DumpMessaggioBean> listDumpMessaggiGByDataConsegnaErogatore(String idTransazione, String saErogatore, int start, int limit);

	public List<DumpAllegato> getAllegatiMessaggio(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio,Long idDump);

	public List<DumpContenuto> getContenutiSpecifici(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio,Long idDump);

	public List<DumpHeaderTrasporto> getHeaderTrasporto(String idTransazione, String saErogatore, Date dataConsegnaErogatore, TipoMessaggio tipoMessaggio,Long idDump);

	/**
	 * Effettua una ricerca dei duplicati della transazione
	 * 
	 * @param idTransazione
	 * @param idEgov
	 * @param isRisposta
	 * @return duplicati della transazione
	 */
	public List<TransazioneBean> findAllDuplicati(String idTransazione,
			String idEgov, boolean isRisposta, int start, int limit);

	public int countAllDuplicati(String idTransazione, String idEgov,
			boolean isRisposta);

	public TransazioneBean findTransazioneOriginale(String idTransazioneDuplicata,
			String idEgov, boolean isRisposta);

	public List<ConfigurazioneRicerca> getRicercheByValues(IDAccordo idAccordo,
			String nomeServizio, String nomeAzione);

	public List<Parameter<?>> instanceParameters(ConfigurazioneRicerca configurazioneRicerca, Context context)
			throws SearchException;

	/**
	 * Recupera gli {@link ConfigurazioneTransazioneStato}
	 * 
	 * @param idAccordo
	 * @param nomeServizio
	 * @param nomeAzione
	 * @return elenco degli stati
	 */
	public List<ConfigurazioneTransazioneStato> getStatiByValues(IDAccordo idAccordo, String nomeServizio, String nomeAzione);

	/**
	 * Recupera la risorse di configurazione
	 * 
	 * @param idAccordo (required)
	 * @param nomeServizio (required)
	 * @param nomeAzione (required)
	 * @param nomeStato (opzionale)
	 * @return la lista di risorse
	 */
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseContenutoByValues(
			IDAccordo idAccordo, String nomeServizio, String nomeAzione,
			String nomeStato);

	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder);
	public int totalCount(SortOrder sortOrder, String sortField);

	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder, String sortField);

	public void setLiveMaxResults(Integer limit);
	
	public ITransazioniApplicativoServerService getTransazioniApplicativoServerService();
	
	public List<String> getHostnames(String gruppo, int refreshSecondsInterval);
	public List<String> getClusterIdDinamici(String gruppo, int refreshSecondsInterval);

	public boolean isTimeoutEvent() ;
	public boolean isProfiloDifferenteEvent() ;
	public boolean isSoggettoDifferenteEvent() ;
	
}
