package org.openspcoop2.web.monitor.transazioni.dao;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.generic_project.expression.SortOrder;

import it.link.pdd.core.plugins.ricerche.ConfigurazioneRicerca;
import it.link.pdd.core.plugins.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import it.link.pdd.core.plugins.transazioni.ConfigurazioneTransazioneStato;
import it.link.pdd.core.transazioni.DumpAllegato;
import it.link.pdd.core.transazioni.DumpContenuto;
import it.link.pdd.core.transazioni.DumpHeaderTrasporto;
import it.link.pdd.core.transazioni.DumpMessaggio;
import it.link.pdd.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;

public interface ITransazioniService extends ISearchFormService<TransazioneBean, String, TransazioniSearchForm> {

	public List<TransazioneBean> findAllLive();

	public TransazioneBean findByIdTransazione(String id) throws Exception;

	/**
	 * Recupera gli esiti sul numero transazioni (ok,ko) nell'intervallo
	 * specificato
	 * 
	 * @param idPorta
	 * @param min
	 * @param max
	 * @return
	 */
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max, String esitoContesto);

	/**
	 * Recupera le informazioni per la live view
	 * 
	 * @return L'oggetto contenente le informazioni sul numero di transazioni
	 *         corrette/errate
	 */
	public ResLive getEsitiInfoLive(PermessiUtenteOperatore permessiUtente, Date lastDatePick);

	public boolean hasInfoDumpAvailable(String idTransazione,
			TipoMessaggio tipoMessaggio);

	public boolean hasInfoHeaderTrasportoAvailable(String idTransazione,
			TipoMessaggio tipoMessaggio);

	public DumpMessaggio getDumpMessaggio(String idTransazione,
			TipoMessaggio tipoMessaggio) throws Exception;

	public List<DumpAllegato> getAllegatiMessaggio(String idTransazione,
			TipoMessaggio tipoMessaggio,Long idDump);

	public List<DumpContenuto> getContenutiSpecifici(String idTransazione,
			TipoMessaggio tipoMessaggio,Long idDump);

	public List<DumpHeaderTrasporto> getHeaderTrasporto(String idTransazione,
			TipoMessaggio tipoMessaggio,Long idDump);

	/**
	 * Effettua una ricerca dei duplicati della transazione
	 * 
	 * @param idTransazione
	 * @param idEgov
	 * @param isRisposta
	 * @return
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
	 * @return
	 */
	public List<ConfigurazioneTransazioneStato> getStatiByValues(
			IDAccordo idAccordo, String nomeServizio, String nomeAzione);

	/**
	 * Recupera la risorse di configurazione
	 * 
	 * @param idAccordoSelezionato
	 *            (required)
	 * @param nomeServizioSelezionato
	 *            (required)
	 * @param nomeAzioneSelezionata
	 *            (required)
	 * @param nomeStatoSelezionato
	 *            (opzionale)
	 * @return la lista di risorse
	 */
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseContenutoByValues(
			IDAccordo idAccordo, String nomeServizio, String nomeAzione,
			String nomeStato);

	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder);
	public int totalCount(SortOrder sortOrder, String sortField);

	public List<TransazioneBean> findAll(int start, int limit, SortOrder sortOrder, String sortField);

	public void setLiveMaxResults(Integer limit);

}
