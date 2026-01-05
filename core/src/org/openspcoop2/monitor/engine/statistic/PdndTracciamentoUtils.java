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
package org.openspcoop2.monitor.engine.statistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.commons.search.dao.IServiceManager;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCSoggettoServiceSearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * PdndTracciamentoUtils
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndTracciamentoUtils {

	private PdndTracciamentoUtils() {}
	
	public static void logDebugSoggettiAbilitati(PdndTracciamentoInfo internalPddCodes, Logger logger) {
		int dimensione = 0;
		if(internalPddCodes!=null && internalPddCodes.getSoggetti()!=null) {
			dimensione = internalPddCodes.getSoggetti().size();
		}
		logger.debug("Soggetti abilitato al tracing PDND: {}",dimensione) ;
		if(internalPddCodes!=null && internalPddCodes.getSoggetti()!=null) {
			for (PdndTracciamentoSoggetto soggettoEntry : internalPddCodes.getSoggetti()) {
				String nomeSoggetto = soggettoEntry.getIdSoggetto().getNome();
				
				List<String> soggettiAggregati = getNomiSoggettiAggregati(soggettoEntry);
						logger.debug("Soggetto abilitato al tracing PDND: {} (soggetti aggregati: {})", nomeSoggetto, soggettiAggregati);
			}
		}
	}
	public static List<String> getNomiSoggettiAggregati(PdndTracciamentoSoggetto soggettoEntry){
		List<String> soggettiAggregati = new ArrayList<>();
		if(soggettoEntry.getIdSoggettiAggregati()!=null && !soggettoEntry.getIdSoggettiAggregati().isEmpty()) {
			for (IDSoggetto idSoggettoAggregato : soggettoEntry.getIdSoggettiAggregati()) {
				soggettiAggregati.add(idSoggettoAggregato.getCodicePorta());
			}
		}
		return soggettiAggregati;
	}
	
	public static PdndTracciamentoInfo getEnabledPddCodes(IServiceManager utilsSM, StatisticsConfig config) throws SQLQueryObjectException, StatisticsEngineException, ServiceException, NotImplementedException, ExpressionException, NotFoundException, ProtocolException {
		
		if (!(utilsSM.getSoggettoServiceSearch() instanceof JDBCSoggettoServiceSearch)) {
			throw new StatisticsEngineException("service manager non di tipo JDBC");
		}
		JDBCSoggettoServiceSearch soggettiSM = (JDBCSoggettoServiceSearch) utilsSM.getSoggettoServiceSearch();

		// Set contenente i nomi dei soggetti abilitati dal file di properties
		Set<String> defaultEnabled = config.getPdndTracciamentoSoggettiEnabled();

		// Mappa nome, valore del codice porta e nome soggetto per  tutti i soggetti interno
		Map<String, String> internalPdd = getCodesSoggettiInterni(soggettiSM);

		// Informazioni ridefinite sul soggetto nella configurazione
		PdndTracciamentoInfo infoTracciamento = getInfoTracciamento(soggettiSM,  config.getLogCore());

		// se non sono stati abilitati soggetti allora tutti i soggetti interni sono abilitati
		if (defaultEnabled.isEmpty()) {
			if(config.isPdndTracciamentoSoggettiDisabled()) {
				defaultEnabled = new HashSet<>();
			}
			else {
				// questo ramo else, per come viene implementato 'isPdndTracciamentoSoggettiDisabled' non sarà mai acceduto
				// per avere come comportamento di default che il tracciamento pdnd è disabilito per default.
				defaultEnabled = new HashSet<>(internalPdd.values());
			}
		}	
		return getMergeEnabledPddCodes(defaultEnabled, internalPdd, infoTracciamento, config);
	}
	private static PdndTracciamentoInfo getMergeEnabledPddCodes(Set<String> defaultEnabled, Map<String, String> internalPdd, PdndTracciamentoInfo infoTracciamento, StatisticsConfig config) throws ProtocolException {
		
		if(config!=null) {
			// nop
		}
		
		PdndTracciamentoInfo regoleTracciamentoMergeGovWayProperties = new PdndTracciamentoInfo();
		
		for (Map.Entry<String, String> entry : internalPdd.entrySet()) {
			
			String codicePortaSoggetto = entry.getKey();
			String nomeSoggetto = entry.getValue();

			PdndTracciamentoSoggetto customConfigSoggettoAggregato = infoTracciamento.getInfoByIdentificativoPorta(codicePortaSoggetto, false, true);
			if(customConfigSoggettoAggregato!=null) {
				// il soggetto è stato aggregato ad un altro
				continue;
			}

			addSoggetto(defaultEnabled, infoTracciamento,
					codicePortaSoggetto, nomeSoggetto,
					regoleTracciamentoMergeGovWayProperties);
		}
		return regoleTracciamentoMergeGovWayProperties;
	}
	private static void addSoggetto(Set<String> defaultEnabled, PdndTracciamentoInfo infoTracciamento,
			String codicePortaSoggetto, String nomeSoggetto,
			PdndTracciamentoInfo regoleTracciamentoMergeGovWayProperties) throws ProtocolException {
		PdndTracciamentoSoggetto customConfig = infoTracciamento.getInfoByIdentificativoPorta(codicePortaSoggetto, true, false); // se non esiste vuol dire che non è stato mai definito con la nuova proprietà tracciamento, quindi sarà default

		if (customConfig==null || customConfig.isTracciamentoDefault()) {
			if (defaultEnabled.contains(nomeSoggetto)) {
				if(customConfig==null) {
					customConfig = new PdndTracciamentoSoggetto(new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, nomeSoggetto, codicePortaSoggetto), CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
				}
				regoleTracciamentoMergeGovWayProperties.addSoggetto(customConfig);
			}
		} else {
			if (customConfig.isTracciamentoAbilitato()) {
				regoleTracciamentoMergeGovWayProperties.addSoggetto(customConfig);
			}
		}
	}
	
	private static Map<String, String> getCodesSoggettiInterni(JDBCSoggettoServiceSearch soggettiSM) throws SQLQueryObjectException, ServiceException, NotImplementedException, ExpressionException, NotFoundException {
		

		TipiDatabase tipoDb = soggettiSM.getFieldConverter().getDatabaseType();
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDb);
		
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addFromTable(CostantiDB.PDD);
		
		query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_IDENTIFICATIVO_PORTA);
		query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
		
		query.addWhereCondition(true, 
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_SERVER + "=" + CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_NOME,
				CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_TIPO + "= '"+TipoPdD.OPERATIVO.getValue()+"'",
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO + "='" + CostantiLabel.MODIPA_PROTOCOL_NAME + "'");

		return soggettiSM
				.nativeQuery(query.createSQLQuery(), List.of(String.class, String.class))
				.stream()
				.collect(Collectors.toMap(e -> e.get(0).toString(), e -> e.get(1).toString()));

	}
	
	public static PdndTracciamentoInfo getInfoTracciamento(JDBCSoggettoServiceSearch soggettiSM, Logger log) throws SQLQueryObjectException, ServiceException, NotImplementedException, ExpressionException, ProtocolException {
		
		if(log!=null) {
			// nop
		}
		
		PdndTracciamentoInfo info = new PdndTracciamentoInfo();
		
		List<List<Object>> result = readInfoTracciamento(soggettiSM, CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ID);
		
		if(result!=null && !result.isEmpty()) {
			for (List<Object> list : result) {
				if(list!=null && !list.isEmpty()) {
					IDSoggetto idSoggetto = new IDSoggetto(getStringValueFromList(list, 0), getStringValueFromList(list, 1), getStringValueFromList(list, 2));
					String valoreProprieta = getStringValueFromList(list, 3);
					PdndTracciamentoSoggetto soggetto = new PdndTracciamentoSoggetto(idSoggetto, valoreProprieta);
					info.addSoggetto(soggetto);
				}
			}
		}
		
		// aggiungo informazione sui soggetti aggregati
		addSoggettiAggregati(soggettiSM, info, log);
		
		return info;
	}
	private static void addSoggettiAggregati(JDBCSoggettoServiceSearch soggettiSM, PdndTracciamentoInfo info, Logger log) throws ProtocolException, SQLQueryObjectException, ServiceException, NotImplementedException, ExpressionException {
		if(log!=null) {
			// nop
		}
		
		List<List<Object>> resultAggregati = readInfoTracciamento(soggettiSM, CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID);
		if(resultAggregati!=null && !resultAggregati.isEmpty()) {
			for (List<Object> list : resultAggregati) {
				if(list!=null && !list.isEmpty()) {
					addSoggettoAggregato(list, info, log);
				}
			}
		}
	}
	private static void addSoggettoAggregato(List<Object> list, PdndTracciamentoInfo info, Logger log) throws ProtocolException {
		if(log!=null) {
			// nop
		}
		IDSoggetto idSoggetto = new IDSoggetto(getStringValueFromList(list, 0), getStringValueFromList(list, 1), getStringValueFromList(list, 2));
		PdndTracciamentoSoggetto soggetto = info.getInfoByNomeSoggetto(idSoggetto.getNome(), true, false);
		if(soggetto==null) {
			throw new ProtocolException("Soggetto '"+idSoggetto.getNome()+"' non esistente nella configurazione PdndTracciamento ?");
		}
		String valoreProprieta = getStringValueFromList(list, 3);
		if(valoreProprieta!=null && StringUtils.isNoneEmpty(valoreProprieta)) {
			PdndTracciamentoSoggetto soggettoAggregato = info.getInfoByNomeSoggetto(valoreProprieta, true, false);
			if(soggettoAggregato==null) {
				throw new ProtocolException("Soggetto '"+valoreProprieta+"' non esistente nella configurazione PdndTracciamento (ricerca per aggregato); verifica che il soggetto possieda il tracciamento abilitato?");
			}
			soggettoAggregato.addSoggettoAggregato(idSoggetto);
		}
	}
	
	private static List<List<Object>> readInfoTracciamento(JDBCSoggettoServiceSearch soggettiSM, String pName) throws SQLQueryObjectException, ServiceException, NotImplementedException, ExpressionException {
		
		TipiDatabase tipoDb = soggettiSM.getFieldConverter().getDatabaseType();
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDb);
		
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addFromTable(CostantiDB.PDD);
		query.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);

		query.addSelectAliasField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, "tipoSoggetto");
		query.addSelectAliasField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO, "nomeSoggetto");
		query.addSelectAliasField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_IDENTIFICATIVO_PORTA, "codicePorta");
		query.addSelectAliasField(CostantiDB.PROTOCOL_PROPERTIES, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, "valoreProprieta");
		
		query.addWhereCondition(true, 
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_SERVER + "=" + CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_NOME,
				CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_TIPO + "= '"+org.openspcoop2.core.commons.search.constants.TipoPdD.OPERATIVO.getValue()+"'",
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO + "='" + CostantiLabel.MODIPA_PROTOCOL_NAME + "'",
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO + "= "+ CostantiDB.SOGGETTI + ".id", 
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO + "= '"+ProprietariProtocolProperty.SOGGETTO.name()+"'",
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME + "= '" + pName + "'"
				);

		List<List<Object>> result = null;
		try {
			result = soggettiSM.nativeQuery(query.createSQLQuery(), List.of(String.class, String.class, String.class, String.class));
		}catch(NotFoundException notFound) {
			// ignore
		}
		return result;
		
	}
	
	private static String getStringValueFromList(List<Object> list, int index) {
		if(list!=null && !list.isEmpty() && list.size()>=(index+1)) {
			Object o = list.get(index);
			if(o instanceof String) {
				return (String) o;
			}
		}
		return null;
	}
}

