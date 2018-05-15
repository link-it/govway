package org.openspcoop2.web.monitor.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioServiceSearch;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.dao.IConfigurazioneTransazioneServiceSearch;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.costants.NomiTabelle;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

public class GenericService implements IGenericService {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private org.openspcoop2.web.lib.users.DriverUsersDB utenteDAO;

	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager basePluginsServiceManager;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager transazioniPluginsServiceManager;
	private IConfigurazioneServizioAzioneServiceSearch confSerAzSearchDAO;
	private IConfigurazioneServizioServiceSearch confSerSearchDAO;
	private IConfigurazioneTransazioneServiceSearch transazioneSearchDAO;

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private ISoggettoServiceSearch soggettoDAO;

	public GenericService() {
		try {

			// init Service Manager utenti
			this.utenteDAO = (org.openspcoop2.web.lib.users.DriverUsersDB) DAOFactory.getInstance(GenericService.log).getServiceManager(org.openspcoop2.web.lib.users.ProjectInfo.getInstance());
						

			// init Service Manager BASE
			this.basePluginsServiceManager = (org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory
					.getInstance(GenericService.log).getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),GenericService.log);
			this.confSerAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();
			this.confSerSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioServiceSearch();
			
			// init Service Manager RICERCHE (Transazioni.plugins)
			this.transazioniPluginsServiceManager = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(GenericService.log).getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),GenericService.log);
			this.transazioneSearchDAO = this.transazioniPluginsServiceManager
					.getConfigurazioneTransazioneServiceSearch();

			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(GenericService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),GenericService.log);
			// dao utils
			this.soggettoDAO = this.utilsServiceManager
					.getSoggettoServiceSearch();

		} catch (Exception e) {
			GenericService.log.error(e.getMessage(), e);
		}
	}

	/**
	 * verifica l'esistenza di un'azione di configurazione tramite il nome
	 * 
	 * @param nomeAzione
	 * @return
	 */
	@Override
	public boolean existServizioAzioneConfigByName(IDAccordo idAccordo,
			String nomeServizio, String nomeAzione) {
		try {

			IExpression espr = this.confSerAzSearchDAO.newExpression();

			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);
			espr.and();
			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());
			espr.and().equals(ConfigurazioneServizioAzione.model().AZIONE,
					nomeAzione);

			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getTipo());
				if (idAccordo.getSoggettoReferente().getNome() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getNome());
			}

			if (idAccordo.getVersione() != null)
				espr.and()
						.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
								idAccordo.getVersione());

			ConfigurazioneServizioAzione c = this.confSerAzSearchDAO.find(espr);

			return c != null;

			// StringBuffer sb = new StringBuffer(
			// "SELECT a FROM ServizioAzioneConfig a JOIN a.configurazioneServizio s WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo AND a.azione=:nome_azione");
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
			// }
			//
			// if (idAccordo.getVersione() != null)
			// sb.append(" AND s.versione=:versione");
			//
			// Query q1 = this.em.createQuery(sb.toString());
			//
			// q1.setParameter("nome_servizio", nomeServizio);
			// q1.setParameter("nome_accordo", idAccordo.getNome());
			// q1.setParameter("nome_azione", nomeAzione);
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// q1.setParameter("tipo_referente", idAccordo
			// .getSoggettoReferente().getTipo());
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// q1.setParameter("nome_referente", idAccordo
			// .getSoggettoReferente().getNome());
			// }
			//
			// if (idAccordo.getVersione() != null)
			// q1.setParameter("versione", idAccordo.getVersione());
			//
			// return q1.getSingleResult() != null;
		} catch (NotFoundException e) {
			return false;
		}

		catch (Exception e) {
			GenericService.log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public List<ConfigurazioneServizio> findServiziByIDAccordo(IDAccordo accordo) {
		try {

			IExpression expr = this.confSerSearchDAO.newExpression();

			if (accordo != null) {
				expr.equals(ConfigurazioneServizio.model().ACCORDO,
						accordo.getNome());
				if (accordo.getSoggettoReferente() != null) {
					expr.and()
							.equals(ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE,
									accordo.getSoggettoReferente().getNome());
					expr.and()
							.equals(ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE,
									accordo.getSoggettoReferente().getTipo());
				}

				if (accordo.getVersione() != null)
					expr.and().equals(ConfigurazioneServizio.model().VERSIONE,
							accordo.getVersione());
			}

			IPaginatedExpression pagExpr = this.confSerSearchDAO
					.toPaginatedExpression(expr);

			return this.confSerSearchDAO.findAll(pagExpr);

		} catch (ServiceException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			GenericService.log.error(e.getMessage(), e);
		}

		// // recupero la lista di pt non utilizzati
		// StringBuffer sb = new StringBuffer(
		// "SELECT sc FROM ServizioConfig sc WHERE sc.accordo=:nome_accordo ");
		//
		// String tipoReferente = null;
		// String nomeReferente = null;
		// String versione = null;
		//
		// if (idAccordo.getSoggettoReferente() != null) {
		// tipoReferente = idAccordo.getSoggettoReferente().getTipo();
		// nomeReferente = idAccordo.getSoggettoReferente().getNome();
		// }
		//
		// if (idAccordo.getVersione() != null)
		// versione = idAccordo.getVersione();
		//
		// if (tipoReferente != null)
		// sb.append(" AND sc.tipoSoggettoReferente=:tipo_referente");
		// if (nomeReferente != null)
		// sb.append(" AND sc.nomeSoggettoReferente=:nome_referente");
		// if (versione != null)
		// sb.append(" AND sc.versione=:versione");
		//
		// Query q1 = this.em.createQuery(sb.toString());
		//
		// q1.setParameter("nome_accordo", idAccordo.getNome());
		//
		// if (tipoReferente != null)
		// q1.setParameter("tipo_referente", tipoReferente);
		// if (nomeReferente != null)
		// q1.setParameter("nome_referente", nomeReferente);
		// if (versione != null)
		// q1.setParameter("versione", versione);
		//
		// return q1.getResultList();
		//
		// } catch (Exception e) {
		// GenericService.log.error(e.getMessage(), e);
		// }
		return new ArrayList<ConfigurazioneServizio>();
	}

	// @Transactional(readOnly=true)
	// @SuppressWarnings("unchecked")
	// public List<ServizioAzioneConfig> findAzioni(IDAccordo
	// idAccordoSelezionato, String nomeServizioSelezionato) {
	// try{
	// if(idAccordoSelezionato==null || nomeServizioSelezionato==null){
	// GenericService.log.error("Impossibile recuperare le azioni: nessun idAccordo e/o nomeServizio forniti.");
	//
	// return new ArrayList<ServizioAzioneConfig>();
	// }
	//
	// // StringBuffer sb = new
	// StringBuffer("SELECT a FROM ServizioAzioneConfig a JOIN a.configurazioneServizio s WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo ");
	// //
	// // if(idAccordoSelezionato.getSoggettoReferente()!=null){
	// // if(idAccordoSelezionato.getSoggettoReferente().getTipo()!=null)
	// // sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
	// // if(idAccordoSelezionato.getSoggettoReferente().getNome()!=null)
	// // sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
	// // }
	// //
	// // if(idAccordoSelezionato.getVersione()!=null)
	// // sb.append(" AND s.versione=:versione");
	//
	// //recupero le azioni (port_type_azioni)
	// StringBuffer sb = new
	// StringBuffer("SELECT a FROM Azione a JOIN a.portType s WHERE s.nome=:nome_servizio AND s.accordo.nome=:nome_accordo ");
	//
	// if(idAccordoSelezionato.getSoggettoReferente()!=null){
	// if(idAccordoSelezionato.getSoggettoReferente().getTipo()!=null)
	// sb.append(" AND s.accordo.referente.tipo=:tipo_referente");
	// if(idAccordoSelezionato.getSoggettoReferente().getNome()!=null)
	// sb.append(" AND s.accordo.referente.nome=:nome_referente");
	// }
	//
	// if(idAccordoSelezionato.getVersione()!=null)
	// sb.append(" AND s.accordo.versione=:versione");
	//
	// Query q1 = this.em.createQuery(sb.toString());
	//
	// q1.setParameter("nome_servizio", nomeServizioSelezionato);
	// q1.setParameter("nome_accordo", idAccordoSelezionato.getNome());
	//
	// if(idAccordoSelezionato.getSoggettoReferente()!=null){
	// if(idAccordoSelezionato.getSoggettoReferente().getTipo()!=null)
	// q1.setParameter("tipo_referente",idAccordoSelezionato.getSoggettoReferente().getTipo());
	// if(idAccordoSelezionato.getSoggettoReferente().getNome()!=null)
	// q1.setParameter("nome_referente",idAccordoSelezionato.getSoggettoReferente().getNome());
	// }
	//
	// if(idAccordoSelezionato.getVersione()!=null)
	// q1.setParameter("versione",idAccordoSelezionato.getVersione());
	//
	// List<ServizioAzioneConfig> az = q1.getResultList();
	//
	// return az;
	// }catch(Exception e){
	// GenericService.log.error(e,e);
	// }
	// return new ArrayList<ServizioAzioneConfig>();
	// }

	@Override
	public List<ConfigurazioneTransazioneStato> findStatiByAzione(
			IDAccordo idAccordo, String nomeServizio, String azione) {

		try {
			// l'azione deve essere specificata e' possibile specificare il
			// valore '*' come nome azione
			if (idAccordo == null || nomeServizio == null || azione == null) {
				GenericService.log
						.error("Impossibile recuperare gli stati: idAccordo, nomeServizio e/o azione non forniti.");

				return new ArrayList<ConfigurazioneTransazioneStato>();
			}

			IExpression espr = this.confSerAzSearchDAO.newExpression();

			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);
			espr.and();
			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());

			if (azione.equals("*"))
				espr.and().equals(ConfigurazioneServizioAzione.model().AZIONE,
						azione);
			else
				espr.and().or(
						espr.equals(
								ConfigurazioneServizioAzione.model().AZIONE,
								"*"),
						espr.equals(
								ConfigurazioneServizioAzione.model().AZIONE,
								azione));

			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getTipo());
				if (idAccordo.getSoggettoReferente().getNome() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getNome());
			}

			if (idAccordo.getVersione() != null)
				espr.and()
						.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
								idAccordo.getVersione());

			IPaginatedExpression pagExpr = this.confSerAzSearchDAO
					.toPaginatedExpression(espr);

			List<ConfigurazioneServizioAzione> lst = this.confSerAzSearchDAO
					.findAll(pagExpr);

			TreeMap<Long, ConfigurazioneTransazione> mappa = null;
			if (lst != null && lst.size() > 0) {
				IExpression expr = null;
				mappa = new TreeMap<Long, ConfigurazioneTransazione>();

				for (ConfigurazioneServizioAzione conf : lst) {

					expr = this.transazioneSearchDAO.newExpression();

					expr.equals(
							ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
							conf.getIdConfigurazioneServizio().getAccordo());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
									conf.getIdConfigurazioneServizio()
											.getNomeSoggettoReferente());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
									conf.getIdConfigurazioneServizio()
											.getTipoSoggettoReferente());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
									conf.getIdConfigurazioneServizio()
											.getServizio());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
									conf.getIdConfigurazioneServizio()
											.getVersione());

					ConfigurazioneTransazione t = this.transazioneSearchDAO
							.find(expr);

					if (t != null) {
						if (!mappa.containsKey(t.getId())) {
							mappa.put(t.getId(), t);
						}
					}
				}

				if (mappa != null && mappa.size() > 0) {
					ArrayList<ConfigurazioneTransazioneStato> ts = new ArrayList<ConfigurazioneTransazioneStato>();
					for (ConfigurazioneTransazione tr : mappa.values()) {
						ts.addAll(tr.getConfigurazioneTransazioneStatoList());
					}

					return ts;
				}

			}

			// StringBuffer sb = new StringBuffer(
			// "SELECT st FROM StatoConfig st JOIN st.transazione t JOIN t.azioneConfig a JOIN a.configurazioneServizio s WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo ");
			//
			// if (idAccordoSelezionato.getSoggettoReferente() != null) {
			// if (idAccordoSelezionato.getSoggettoReferente().getTipo() !=
			// null)
			// sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
			// if (idAccordoSelezionato.getSoggettoReferente().getNome() !=
			// null)
			// sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
			// }
			//
			// if (idAccordoSelezionato.getVersione() != null)
			// sb.append(" AND s.versione=:versione ");
			//
			// if (!"*".equals(azione))
			// sb.append(" AND (a.azione=:nome_azione or a.azione=:nome_azione_star) ");
			// else
			// sb.append(" AND (a.azione=:nome_azione_star)");
			//
			// Query q1 = this.em.createQuery(sb.toString());
			//
			// q1.setParameter("nome_servizio", nomeServizioSelezionato);
			// q1.setParameter("nome_accordo", idAccordoSelezionato.getNome());
			//
			// if (!"*".equals(azione))
			// q1.setParameter("nome_azione", azione);
			//
			//
			// q1.setParameter("nome_azione_star", "*");
			// if (idAccordoSelezionato.getSoggettoReferente() != null) {
			// if (idAccordoSelezionato.getSoggettoReferente().getTipo() !=
			// null)
			// q1.setParameter("tipo_referente", idAccordoSelezionato
			// .getSoggettoReferente().getTipo());
			// if (idAccordoSelezionato.getSoggettoReferente().getNome() !=
			// null)
			// q1.setParameter("nome_referente", idAccordoSelezionato
			// .getSoggettoReferente().getNome());
			// }
			//
			// if (idAccordoSelezionato.getVersione() != null)
			// q1.setParameter("versione", idAccordoSelezionato.getVersione());
			//
			// return q1.getResultList();
		} catch (Exception e) {
			GenericService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneTransazioneStato>();
	}

	@Override
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseByValues(
			IDAccordo idAccordo, String nomeServizio, String nomeAzione,
			String nomeStato) {

		// l'azione deve essere specificata e' possibile specificare il valore
		// '*' come nome azione
		if (idAccordo == null || nomeServizio == null || nomeAzione == null) {
			GenericService.log
					.error("Impossibile recuperare gli stati: idAccordo , nomeServizio e/o azione non forniti.");

			return null;
		}

		try {

			IExpression espr = this.confSerAzSearchDAO.newExpression();

			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);
			espr.and();
			espr.equals(
					ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());
			if (nomeAzione.equals("*"))
				espr.and().equals(ConfigurazioneServizioAzione.model().AZIONE,
						nomeAzione);
			else
				espr.and().or(
						espr.equals(
								ConfigurazioneServizioAzione.model().AZIONE,
								"*"),
						espr.equals(
								ConfigurazioneServizioAzione.model().AZIONE,
								nomeAzione));

			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getTipo());
				if (idAccordo.getSoggettoReferente().getNome() != null)
					espr.and()
							.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
									idAccordo.getSoggettoReferente().getNome());
			}

			if (idAccordo.getVersione() != null)
				espr.and()
						.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
								idAccordo.getVersione());

			IPaginatedExpression pagExpr = this.confSerAzSearchDAO
					.toPaginatedExpression(espr);

			List<ConfigurazioneServizioAzione> lst = this.confSerAzSearchDAO
					.findAll(pagExpr);

			TreeMap<Long, ConfigurazioneTransazione> mappa = null;
			if (lst != null && lst.size() > 0) {
				IExpression expr = null;
				mappa = new TreeMap<Long, ConfigurazioneTransazione>();

				for (ConfigurazioneServizioAzione conf : lst) {

					expr = this.transazioneSearchDAO.newExpression();

					expr.equals(
							ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
							conf.getIdConfigurazioneServizio().getAccordo());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
									conf.getIdConfigurazioneServizio()
											.getNomeSoggettoReferente());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
									conf.getIdConfigurazioneServizio()
											.getTipoSoggettoReferente());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
									conf.getIdConfigurazioneServizio()
											.getServizio());
					expr.and()
							.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
									conf.getIdConfigurazioneServizio()
											.getVersione());

					if (nomeStato != null && !"*".equals(nomeStato))
						expr.and()
								.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,
										nomeStato);

					ConfigurazioneTransazione t = this.transazioneSearchDAO
							.find(expr);

					if (t != null) {
						if (!mappa.containsKey(t.getId())) {
							mappa.put(t.getId(), t);
						}
					}

				}

				if (mappa != null && mappa.size() > 0) {
					ArrayList<ConfigurazioneTransazioneRisorsaContenuto> toRet = new ArrayList<ConfigurazioneTransazioneRisorsaContenuto>();
					for (ConfigurazioneTransazione tr : mappa.values()) {
						toRet.addAll(tr
								.getConfigurazioneTransazioneRisorsaContenutoList());
					}

					return toRet;
				}

			}

			// StringBuffer sb = new StringBuffer(
			// "SELECT r FROM RisorsaContenutoConfig r ");
			//
			// // if(StringUtils.isNotEmpty(nomeStato) &&
			// !"*".equals(nomeStato))
			// //
			// sb.append(" LEFT JOIN r.stato st LEFT JOIN st.transazione t LEFT JOIN t.azioneConfig a LEFT JOIN a.configurazioneServizio s ");
			// // else
			// //
			// sb.append(" JOIN r.transazione t JOIN t.azioneConfig a JOIN a.configurazioneServizio s ");
			//
			// sb.append(" JOIN r.transazione t JOIN t.azioneConfig a JOIN a.configurazioneServizio s ");
			//
			// // lo stato puo essere vuoto
			// // stato null,"","*" vengono trattati alla stessa maniera
			// if (StringUtils.isNotEmpty(nomeStato) && !"*".equals(nomeStato))
			// sb.append(" LEFT JOIN r.stato st ");
			//
			// sb.append(" WHERE s.servizio=:nome_servizio AND s.accordo=:nome_accordo ");
			//
			// // se l'azione non e' '*' allora voglio le risorse di '*' piu
			// quelle
			// // dell'azione specificata
			// if (!"*".equals(nomeAzione))
			// sb.append(" AND (a.azione=:nome_azione or a.azione=:nome_azione_star) ");
			// else
			// sb.append(" AND (a.azione=:nome_azione_star)");
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// sb.append(" AND s.tipoSoggettoReferente=:tipo_referente");
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// sb.append(" AND s.nomeSoggettoReferente=:nome_referente");
			// }
			//
			// if (idAccordo.getVersione() != null)
			// sb.append(" AND s.versione=:versione");
			//
			// // lo stato puo essere vuoto
			// // stato null,"","*" vengono trattati alla stessa maniera
			// if (StringUtils.isNotEmpty(nomeStato) && !"*".equals(nomeStato))
			// sb.append(" AND (st.nome=:nome_stato or st.nome is null) ");
			//
			// Query q1 = this.em.createQuery(sb.toString());
			//
			// q1.setParameter("nome_servizio", nomeServizio);
			// q1.setParameter("nome_accordo", idAccordo.getNome());
			//
			// if (!"*".equals(nomeAzione))
			// q1.setParameter("nome_azione", nomeAzione);
			//
			// q1.setParameter("nome_azione_star", "*");
			// // lo stato puo essere vuoto
			// // stato null,"","*" vengono trattati alla stessa maniera
			// if (StringUtils.isNotEmpty(nomeStato) && !"*".equals(nomeStato))
			// q1.setParameter("nome_stato", nomeStato);
			//
			// if (idAccordo.getSoggettoReferente() != null) {
			// if (idAccordo.getSoggettoReferente().getTipo() != null)
			// q1.setParameter("tipo_referente", idAccordo
			// .getSoggettoReferente().getTipo());
			// if (idAccordo.getSoggettoReferente().getNome() != null)
			// q1.setParameter("nome_referente", idAccordo
			// .getSoggettoReferente().getNome());
			// }
			//
			// if (idAccordo.getVersione() != null)
			// q1.setParameter("versione", idAccordo.getVersione());
			//
			// return q1.getResultList();
		} catch (Exception e) {
			GenericService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneTransazioneRisorsaContenuto>();
	}

	@Override
	public Stato getTableState(User utente, NomiTabelle nomeTabella) {
		Stato state = null;

		try {
			state = this.utenteDAO.getStato(utente.getLogin(), nomeTabella.toString());
			if(state == null) {
				state = new Stato();
				state.setOggetto(nomeTabella.toString());
				state.setStato(null);
			}
			return state;			
			
		} catch (DriverUsersDBException e) {
			GenericService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void saveTableState(User user, Stato stato) {
		try {
			this.utenteDAO.saveStato(user.getLogin(), stato.getOggetto(), stato.getStato());
		} catch (DriverUsersDBException e) {
			GenericService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<Soggetto> soggettiAutoComplete(String input) {

		// .service.getEntityManager().
		// createQuery("from Soggetto s where lower(s.nome) like :nome order by s.nome asc")
		// .setParameter("nome", "%"+((String)val).toLowerCase()+"%")
		// .setFirstResult(0)
		// .setMaxResults(100)
		// .getResultList();

		IExpression expr;
		try {
			expr = this.soggettoDAO.newExpression();

			expr.ilike(Soggetto.model().NOME_SOGGETTO,
					"%" + input.toLowerCase() + "%");

			expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().NOME_SOGGETTO);

			IPaginatedExpression pagExpr = this.soggettoDAO
					.toPaginatedExpression(expr);
			pagExpr.offset(0).limit(DynamicUtilsService.LIMIT_SEARCH);

			return this.soggettoDAO.findAll(pagExpr);
		} catch (ServiceException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			GenericService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			GenericService.log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}
}
