/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openspcoop2.core.commons.search.dao.IServiceManager;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCSoggettoServiceSearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * PdndTracciamentoUtils
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndTracciamentoUtils {

	private PdndTracciamentoUtils() {}
	
	public static Map<String, String> getEnabledPddCodes(IServiceManager utilsSM, StatisticsConfig config) throws SQLQueryObjectException, StatisticsEngineException, ServiceException, NotImplementedException, ExpressionException, NotFoundException {
		
		if (!(utilsSM.getSoggettoServiceSearch() instanceof JDBCSoggettoServiceSearch)) {
			throw new StatisticsEngineException("service manager non di tipo JDBC");
		}
		JDBCSoggettoServiceSearch soggettiSM = (JDBCSoggettoServiceSearch) utilsSM.getSoggettoServiceSearch();
		
		// Set contenente i nomi dei soggetti abilitati dal file di properties
		Set<String> defaultEnabled = config.getPdndTracciamentoSoggettiEnabled();
		
		// Mappa nome, valore del codice porta e nome soggetto per  tutti i soggetti interno
		Map<String, String> internalPdd = getCodesSoggettiInterni(soggettiSM);
		
		// Mappa nome, valore del codice porta e proprieta custom per l'abilitazione
		Map<String, String> ridefinedEnabled = getCodesSoggettiProtocolProperty(soggettiSM);
		
		// se non sono stati abilitati soggetti allora tutti i soggetti interni sono abilitati
		if (defaultEnabled.isEmpty()) {
			if(config.isPdndTracciamentoSoggettiDisabled()) {
				defaultEnabled = new HashSet<>();
			}
			else {
				defaultEnabled = new HashSet<>(internalPdd.values());
			}
		}	
		
		return getMergeEnabledPddCodes(defaultEnabled, internalPdd, ridefinedEnabled);
	}
	private static Map<String, String> getMergeEnabledPddCodes(Set<String> defaultEnabled, Map<String, String> internalPdd, Map<String, String> ridefinedEnabled) {
		Map<String, String> merged = new HashMap<>();
		for (Map.Entry<String, String> entry : internalPdd.entrySet()) {
			String customProperty = ridefinedEnabled.get(entry.getKey());
			
			if (customProperty == null || customProperty.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID)) {
				if (defaultEnabled.contains(entry.getValue()))
					merged.put(entry.getKey(), entry.getValue());
			} else {
				if (customProperty.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID)) {
					merged.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return merged;
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
				CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_TIPO + "= 'operativo'",
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO + "='" + CostantiLabel.MODIPA_PROTOCOL_NAME + "'");

		return soggettiSM
				.nativeQuery(query.createSQLQuery(), List.of(String.class, String.class))
				.stream()
				.collect(Collectors.toMap(e -> e.get(0).toString(), e -> e.get(1).toString()));

	}
	
	private static Map<String, String> getCodesSoggettiProtocolProperty(JDBCSoggettoServiceSearch soggettiSM) throws SQLQueryObjectException, ServiceException, NotImplementedException, ExpressionException, NotFoundException {
		
		TipiDatabase tipoDb = soggettiSM.getFieldConverter().getDatabaseType();
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(tipoDb);
		
		query.addFromTable(CostantiDB.SOGGETTI);
		query.addFromTable(CostantiDB.PDD);
		query.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);

		query.addSelectField(CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_COLUMN_IDENTIFICATIVO_PORTA);
		query.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING);
		
		query.addWhereCondition(true, 
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_SERVER + "=" + CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_NOME,
				CostantiDB.PDD + "." + CostantiDB.PDD_COLUMN_TIPO + "= '"+org.openspcoop2.core.commons.search.constants.TipoPdD.OPERATIVO.getValue()+"'",
				CostantiDB.SOGGETTI + "." + CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO + "='" + CostantiLabel.MODIPA_PROTOCOL_NAME + "'",
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO + "= "+ CostantiDB.SOGGETTI + ".id", 
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO + "= 'SOGGETTO'",
				CostantiDB.PROTOCOL_PROPERTIES + "." + CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME + "= '" + CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ID + "'");

		return soggettiSM
				.nativeQuery(query.createSQLQuery(), List.of(String.class, String.class))
				.stream()
				.collect(Collectors.toMap(
						e -> e.get(0).toString(), 
						e -> e.get(1).toString()
				));
	}
}
