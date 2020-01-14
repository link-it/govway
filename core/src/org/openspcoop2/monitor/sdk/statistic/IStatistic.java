/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.statistic;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.utils.TipiDatabase;

/**
 * IStatistic
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IStatistic {
	
	public TipoPdD getTipoPorta();
	public String getIdentificativoPorta();
	
	public String getTipoSoggettoMittente();
	public String getSoggettoMittente();
	
	public String getTipoSoggettoDestinatario();
	public String getSoggettoDestinatario();
	
	public String getTipoServizio();
	public String getServizio();
	public Integer getVersioneServizio();
	
	public String getAzione();
	
	public String getServizioApplicativo();	
	
	public String getIdentificativoAutenticato();	
	
	public String getTokenInfoIssuer();	
	public String getTokenInfoClientId();	
	public String getTokenInfoSubject();	
	public String getTokenInfoUsername();	
	public String getTokenInfoEmail();	
	
	public String getClientAddress();
	
	public String getGruppo();
	
	public TipiDatabase getDatabaseType();
	
	public StatisticType getStatisticType();
	
	public Logger getLogger();
	
	public DAOFactory getDAOFactory() throws StatisticException;
	
	/**
	 * Crea le statistiche basate sui contenuti relative ai valori della risorsa, presente nel repository delle transazioni, con identificativo idRisorsa.
	 * 
	 * @param idRisorsa Identificativo della risorsa nel repository delle transazioni i cui valori verranno aggregati come dati statistici
	 * @throws StatisticException
	 */
	public void createStatistics(String idRisorsa) throws StatisticException;
		
	/**
	 * Crea le statistiche basate sui contenuti relative ai valori della risorsa, presente nel repository delle transazioni, con identificativo idRisorsa.
	 * L'aggregazione del dato statistico tiene conto della possibilita' di effettuare filtri di ricerca in base alle risorse 
	 * presenti nel repository delle transazioni con identificativi uguali a quelli indicati tramite i parametri idRisorseFiltri.
	 * 
	 * @param idRisorsa Identificativo della risorsa nel repository delle transazioni i cui valori verranno aggregati come dati statistici
	 * @param idRisorseFiltri Identificativo delle risorse, nel repository delle transazioni, utilizzabili come filtro di ricerca
	 * @throws StatisticException
	 */
	public void createStatistics(String idRisorsa, StatisticResourceFilter ... idRisorseFiltri) throws StatisticException;
	
	/**
	 * Crea le statistiche basate sui contenuti relative ai valori della risorsa, presente nel repository delle transazioni, con identificativo idRisorsa.
	 * Siccome la createStatistics può essere chiamata più volte, può essere aggiunto un identificativo alla statistica che rende univoca ogni statistica generata
	 * 
	 * @param idStatistica Identificativo della statistica
	 * @param idRisorsa Identificativo della risorsa nel repository delle transazioni i cui valori verranno aggregati come dati statistici
	 * @throws StatisticException
	 */
	public void createStatistics(String idStatistica,String idRisorsa) throws StatisticException;
		
	/**
	 * Crea le statistiche basate sui contenuti relative ai valori della risorsa, presente nel repository delle transazioni, con identificativo idRisorsa.
	 * L'aggregazione del dato statistico tiene conto della possibilita' di effettuare filtri di ricerca in base alle risorse 
	 * presenti nel repository delle transazioni con identificativi uguali a quelli indicati tramite i parametri idRisorseFiltri.
	 * Siccome la createStatistics può essere chiamata più volte, può essere aggiunto un identificativo alla statistica che rende univoca ogni statistica generata
	 * 
	 * @param idStatistica Identificativo della statistica
	 * @param idRisorsa Identificativo della risorsa nel repository delle transazioni i cui valori verranno aggregati come dati statistici
	 * @param idRisorseFiltri Identificativo delle risorse, nel repository delle transazioni, utilizzabili come filtro di ricerca
	 * @throws StatisticException
	 */
	public void createStatistics(String idStatistica,String idRisorsa, StatisticResourceFilter ... idRisorseFiltri) throws StatisticException;
		
	/**
	 * Crea una statistica aggregando le transazioni identificate tramite il parametro filtro.
	 * Tate dato viene registrato nel repository dei dati statistici con valore uguale al parametro valoreRisorsaAggregata
	 * 
	 * @param filtro Filtro che identifica le transazioni da aggregare all'interno del repository delle transazioni
	 * @param valoreRisorsaAggregata Valore da assegnare alla risorsa aggregata
	 * @throws StatisticException
	 */
	public void createStatistics(IFilter filtro,String valoreRisorsaAggregata ) throws StatisticException;
		
	/**
	 * Crea una statistica aggregando le transazioni identificate tramite il parametro filtro.
	 * Tate dato viene registrato nel repository dei dati statistici con valore uguale al parametro valoreRisorsaAggregata.
	 * Inoltre la registrazione dei dati statistici tiene conto della possibilita' di effettuare filtri di ricerca in base alle risorse identificate tramite i parametri idFiltri 
	 * 
	 * @param filtro Filtro che identifica le transazioni da aggregare all'interno del repository delle transazioni
	 * @param valoreRisorsaAggregata Valore da assegnare alla risorsa aggregata
	 * @param filtriRicerca Identificativi e valori delle risorse da utilizzare poi come filtro di ricerca
	 * @throws StatisticException
	 */
	public void createStatistics(IFilter filtro,String valoreRisorsaAggregata, StatisticFilter ... filtriRicerca ) throws StatisticException;
	
}
