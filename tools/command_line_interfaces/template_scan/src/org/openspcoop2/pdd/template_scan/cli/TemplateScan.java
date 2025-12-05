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

package org.openspcoop2.pdd.template_scan.cli;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.PropertiesEnvUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 *  Template Scan
 *  Permette di cercare all'interno di tutti i template di govway determinati pattern
 *  (utilizzando per controllare quale librerie vengono richieste dai template usati da govway
 *  per future deprecazioni)
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateScan {

	private static Logger log = LoggerWrapperFactory.getLogger(TemplateScan.class);
	private static Logger logSql = LoggerWrapperFactory.getLogger(TemplateScan.class);
	
	private static final String TEMPLATE_ATTR = "template";
	private static final String NAME_ATTR = "name";
	private static final String TRANS_NAME = "trans_name";
	private static final String SERVICE_NAME = "service_name";
	private static final String SERVICE_VERSION = "service_version";
	private static final String SUBJECT_NAME = "subject_name";
	private static final String EROGATOR_NAME = "erogator_name";
	private static final String IS_DEFAULT = "is_default";
	private static final String GROUP_NAME = "group_name";
	
	public static final String DS_JNDI_NAME = "org.govway.datasource.console";
	

    
	public static void main(String[] args) throws CoreException, SQLException {
		
		Connection con = null;
		try {
		
			// Logger
			initLogger();
			log = LoggerWrapperFactory.getLogger("template_scan.core");	
			logSql = LoggerWrapperFactory.getLogger("template_scan.sql");	
			
			
			log.debug("Raccolta parametri in corso...");
			
			// properties
			TemplateScanProperties properties = TemplateScanProperties.getInstance();
			
			// args
			if (args.length < 1)
				throw new IllegalStateException("Richiesto almeno un parametro, il pattern da ricercare");
			Pattern regex = Pattern.compile(args[0]);
			
			log.debug("Raccolta parametri terminata");
			
			
			log.debug("Inizializzazione connessione database in corso...");
			
			String driver = properties.getDbDriver();
			String username = properties.getDbUsername();
			String password = properties.getDbPassword();
			String connectionURL = properties.getDbUrl();
			
			org.openspcoop2.utils.resources.Loader.getInstance().newInstance(driver);
			if(username!=null && password!=null){
				con = DriverManager.getConnection(connectionURL, username, password);
			}else{
				con = DriverManager.getConnection(connectionURL);
			}
		

			log.info("Inizializzazione effettuata avvio la ricerca...");
			TemplateScan instance = new TemplateScan();
			instance.process(regex, properties, con);
			
			log.info("completato con successo");
		}
		catch(Exception t) {
			if(log != null) {
				log.error(t.getMessage(),t);
			}
			throw new CoreException(t.getMessage(),t);
		}
		finally {
			if(con != null) {
				con.close();
			}
		}

	}
	
	
	private TreeMap<TreePath, ScanResult> results = new TreeMap<>();
	
	private void process(Pattern regex, TemplateScanProperties properties, Connection con) throws SQLQueryObjectException, SQLException, DriverRegistroServiziException {
		
		log.info("Ricerca del pattern: {}, all'interno del db", regex);
		
		if (properties.isEnableTransformationRequest()) {
			processTemplateRequest(false, regex, properties, con);
			processTemplateRequest(true, regex, properties, con);
		}
		
		if (properties.isEnableTransformationResponse()) {
			processTemplateResponse(false, regex, properties, con);
			processTemplateResponse(true, regex, properties, con);
		}

		if (properties.isEnableConnectors())
			processTemplatePAConnectors(regex, properties, con);
		
		if (properties.isEnableTokenPolicy() || properties.isEnableAtttributeAuthority())
			processTemplateGenericProperties(regex, properties, con);
		
		log.info("Risultati individuati: {}", this.results.size());
		print();
	}
	
	private void print() {
		this.results.forEach((k, v) -> System.out.println(k + ": {" + v + "}"));
	}
	
	private String color(String text) {
		return "\033[1;34m" + text + "\033[0m";
	}
	
	private TreePath convertPortName(IDServizio idServizio, IDSoggetto fruitore, String group)  {
		TreePath path = TreePath.of(color(
				(fruitore == null ? "" : fruitore.getNome() + "->") + idServizio.getNome() + "@" + idServizio.getSoggettoErogatore().getNome() + " v" + idServizio.getVersione()));
		path.add(TemplateScanProperties.CONFIGURATION_SEGMENT);
		if (group != null)
			path.add(group);
		return path;
	}
	
	private void processTemplateRequest(boolean isPA, Pattern regex, TemplateScanProperties props, Connection con) throws SQLQueryObjectException, SQLException, DriverRegistroServiziException {
		log.debug("Eseguo ricerca dei template sulle trasformazioni della richiesta, porta applicativa: {}", isPA);
		
		String portTable =      isPA ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
		String transformTable = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI;
		String columnTemplate = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_REQ_CONVERSIONE_TEMPLATE : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_REQ_CONVERSIONE_TEMPLATE;
		String columnName     = isPA ? CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_PORTA : CostantiDB.PORTE_DELEGATE_COLUMN_NOME_PORTA;
		String columnPortId   = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_ID_PORTA : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_ID_PORTA;
		String consoleSegment = isPA ? TemplateScanProperties.EROGATION_SEGMENT : TemplateScanProperties.FRUITION_SEGMENT;
		String mappingPort    = isPA ? CostantiDB.MAPPING_EROGAZIONE_PA : CostantiDB.MAPPING_FRUIZIONE_PD;
		String columnService  = isPA ? CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_SERVIZIO : CostantiDB.PORTE_DELEGATE_COLUMN_NOME_SERVIZIO;
		ISQLQueryObject sqlObject = SQLObjectFactory.createSQLQueryObject(props.getDbType());
		
		sqlObject.addSelectAliasField("p." + columnName, NAME_ATTR);
		sqlObject.addSelectAliasField("t.nome", TRANS_NAME);
		sqlObject.addSelectAliasField("t." + columnTemplate, TEMPLATE_ATTR);
		sqlObject.addSelectAliasField("p." + columnService, SERVICE_NAME);
		sqlObject.addSelectAliasField("p.tipo_servizio", "tipo");
		sqlObject.addSelectAliasField("p.versione_servizio", SERVICE_VERSION);
		sqlObject.addSelectAliasField("sogg.nome_soggetto", SUBJECT_NAME);
		sqlObject.addSelectAliasField("mapp.nome", GROUP_NAME);
		sqlObject.addSelectAliasField("mapp.is_default", IS_DEFAULT);
		if (!isPA) {
			sqlObject.addSelectAliasField("p.nome_soggetto_erogatore", EROGATOR_NAME);
		} else {
			sqlObject.addSelectAliasField("sogg.nome_soggetto", EROGATOR_NAME);
		}
		sqlObject.addFromTable(portTable, "p");
		sqlObject.addFromTable(transformTable, "t");
		sqlObject.addFromTable(CostantiDB.SOGGETTI, "sogg");
		sqlObject.addFromTable(mappingPort, "mapp");
		
		sqlObject.setANDLogicOperator(true);
		sqlObject.addWhereIsNotNullCondition("t." + columnTemplate);
		sqlObject.addWhereCondition("p.id = t." + columnPortId);
		sqlObject.addWhereCondition("sogg.id = p.id_soggetto");
		sqlObject.addWhereCondition("p.id = mapp.id_porta");
		
		String query = sqlObject.createSQLQuery();
		
		logSql.debug("Eseguo query, porta applicativa: {}, Trasformazioni richiesta, query: {}", isPA, query);
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String nomeErogatore = rs.getString(EROGATOR_NAME);
					String nomeSoggetto = rs.getString(SUBJECT_NAME);
					String tipo = rs.getString("tipo");
					String group = rs.getBoolean(IS_DEFAULT) ? null : rs.getString(GROUP_NAME);
					IDSoggetto erogatore = new IDSoggetto(tipo, nomeErogatore);
					IDSoggetto fruitore = new IDSoggetto(tipo, nomeSoggetto);
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo, rs.getString(SERVICE_NAME), erogatore, rs.getInt(SERVICE_VERSION));
					InputStream template = rs.getBinaryStream(TEMPLATE_ATTR);
					TreePath key = TreePath.of(
							TemplateScanProperties.REGISTRY_SEGMENT,
							consoleSegment).add(convertPortName(idServizio, isPA ? null : fruitore, group))
							.add(TemplateScanProperties.TRANSFORMATION_SEGMENT)
							.add(color(rs.getString(TRANS_NAME)))
							.add(TemplateScanProperties.TRANSFORMATION_REQUEST_SEGMENT)
							.add(TemplateScanProperties.TRANSFORMATION_REQUEST_CONTENT_SEGMENT);
					
					ScanResult.parse(template, regex)
						.ifPresent(sc -> this.results.put(key, sc));
				}
			}
		}
	}
	
	
	private void parseTemplateResponseResultSet(Pattern regex, boolean isPA, ISQLQueryObject sqlObject, Connection con) throws SQLException, SQLQueryObjectException, DriverRegistroServiziException {
		String query = sqlObject.createSQLQuery();
		String consoleSegment = isPA ? TemplateScanProperties.EROGATION_SEGMENT : TemplateScanProperties.FRUITION_SEGMENT;

		logSql.debug("Eseguo query, porta applicativa: {}, Trasformazioni risposta, query: {}", isPA, query);

		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String nomeErogatore = rs.getString(EROGATOR_NAME);
					String nomeSoggetto = rs.getString(SUBJECT_NAME);
					String tipo = rs.getString("tipo");
					String group = rs.getBoolean(IS_DEFAULT) ? null : rs.getString(GROUP_NAME);
					IDSoggetto erogatore = new IDSoggetto(tipo, nomeErogatore);
					IDSoggetto fruitore = new IDSoggetto(tipo, nomeSoggetto);
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo, rs.getString(SERVICE_NAME), erogatore, rs.getInt(SERVICE_VERSION));

					InputStream template = rs.getBinaryStream(TEMPLATE_ATTR);
					TreePath key = TreePath.of(
							TemplateScanProperties.REGISTRY_SEGMENT,
							consoleSegment).add(convertPortName(idServizio, isPA ? null : fruitore, group))
							.add(TemplateScanProperties.TRANSFORMATION_SEGMENT)
							.add(color(rs.getString(TRANS_NAME)))
							.add(TemplateScanProperties.TRANSFORMATION_RESPONSE_SEGMENT)
							.add(color(rs.getString("resp_name")))
							.add(TemplateScanProperties.TRANSFORMATION_RESPONSE_CONTENT_SEGMENT);
					
					ScanResult.parse(template, regex)
						.ifPresent(sc -> this.results.put(key, sc));
				}
			}
		}
	}
	
	private void processTemplateResponse(boolean isPA, Pattern regex, TemplateScanProperties props, Connection con) throws SQLQueryObjectException, SQLException, DriverRegistroServiziException {
		log.debug("Eseguo ricerca dei template sulle trasformazioni della risposta, porta applicativa: {}", isPA);
		
		String portTable =      isPA ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
		String transformTable = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI;
		String responseTable  = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
		String columnTemplate = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_COLUMN_CONVERSIONE_TEMPLATE : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_COLUMN_CONVERSIONE_TEMPLATE;
		String columnName     = isPA ? CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_PORTA : CostantiDB.PORTE_DELEGATE_COLUMN_NOME_PORTA;
		String columnPortId   = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_ID_PORTA : CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_ID_PORTA;
		String columnTransId  = isPA ? CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_COLUMN_TRANSFORM_ID: CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_COLUMN_TRANSFORM_ID;
		String mappingPort    = isPA ? CostantiDB.MAPPING_EROGAZIONE_PA : CostantiDB.MAPPING_FRUIZIONE_PD;
		String columnService  = isPA ? CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_SERVIZIO : CostantiDB.PORTE_DELEGATE_COLUMN_NOME_SERVIZIO;

		ISQLQueryObject sqlObject = SQLObjectFactory.createSQLQueryObject(props.getDbType());
		sqlObject.addSelectAliasField("p." + columnName, NAME_ATTR);
		sqlObject.addSelectAliasField("r." + columnTemplate, TEMPLATE_ATTR);
		sqlObject.addSelectAliasField("p." + columnService, SERVICE_NAME);
		sqlObject.addSelectAliasField("t.nome", TRANS_NAME);
		sqlObject.addSelectAliasField("r.nome", "resp_name");
		sqlObject.addSelectAliasField("p.tipo_servizio", "tipo");
		sqlObject.addSelectAliasField("p.versione_servizio", SERVICE_VERSION);
		sqlObject.addSelectAliasField("sog.nome_soggetto", SUBJECT_NAME);
		sqlObject.addSelectAliasField("m.nome", GROUP_NAME);
		sqlObject.addSelectAliasField("m.is_default", IS_DEFAULT);
		if (!isPA) {
			sqlObject.addSelectAliasField("p.nome_soggetto_erogatore", EROGATOR_NAME);
		} else {
			sqlObject.addSelectAliasField("sog.nome_soggetto", EROGATOR_NAME);
		}
		
		sqlObject.addFromTable(portTable, "p");
		sqlObject.addFromTable(transformTable, "t");
		sqlObject.addFromTable(responseTable, "r");
		sqlObject.addFromTable(CostantiDB.SOGGETTI, "sog");
		sqlObject.addFromTable(mappingPort, "m");
		
		sqlObject.setANDLogicOperator(true);
		sqlObject.addWhereIsNotNullCondition("r." + columnTemplate);
		sqlObject.addWhereCondition("p.id = t." + columnPortId);
		sqlObject.addWhereCondition("t.id = r." + columnTransId);
		sqlObject.addWhereCondition("sog.id = p.id_soggetto");
		sqlObject.addWhereCondition("p.id = m.id_porta");
		
			
		parseTemplateResponseResultSet(regex,isPA,sqlObject,con);
	}
	
	private void processTemplatePAConnectors(Pattern regex, TemplateScanProperties props, Connection con) throws SQLQueryObjectException, SQLException, DriverRegistroServiziException {
		log.debug("Eseguo ricerca dei template sui connettori condizionali, porta applicativa: {}", true);

		ISQLQueryObject sqlObject = SQLObjectFactory.createSQLQueryObject(props.getDbType());
		sqlObject.addSelectAliasField("p." + CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS_COLUMN_VALUE, TEMPLATE_ATTR);
		sqlObject.addSelectAliasField("pa." + CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_PORTA, "name");
		sqlObject.addSelectAliasField("pa." + CostantiDB.PORTE_APPLICATIVE_COLUMN_NOME_SERVIZIO, SERVICE_NAME);
		sqlObject.addSelectAliasField("pa.tipo_servizio", "tipo");
		sqlObject.addSelectAliasField("pa.versione_servizio", SERVICE_VERSION);
		sqlObject.addSelectAliasField("so.nome_soggetto", SUBJECT_NAME);
		sqlObject.addSelectAliasField("m.nome", GROUP_NAME);
		sqlObject.addSelectAliasField("so.nome_soggetto", EROGATOR_NAME);
		sqlObject.addSelectAliasField("m.is_default", IS_DEFAULT);
		
		sqlObject.addFromTable(CostantiDB.PORTE_APPLICATIVE, "pa");
		sqlObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS, "p");
		sqlObject.addFromTable(CostantiDB.SOGGETTI, "so");
		sqlObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA, "m");
		
		sqlObject.setANDLogicOperator(true);
		sqlObject.addWhereCondition("p." + CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS_COLUMN_NAME + "=?");
		sqlObject.addWhereCondition("pa." + CostantiDB.PORTE_APPLICATIVE_COLUMN_ID
				+ "= p." + CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS_COLUMN_PORT_ID);
		sqlObject.addWhereCondition("so.id = pa.id_soggetto");
		sqlObject.addWhereCondition("pa.id = m.id_porta");
		
		String query = sqlObject.createSQLQuery();
		
		logSql.debug("Eseguo query, porta applicativa: true, Connettori condizionali, query: {}", query);
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			int i = 1;
			stmt.setString(i++, org.openspcoop2.pdd.core.behaviour.conditional.Costanti.CONDITIONAL_PATTERN);
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String nomeErogatore = rs.getString(EROGATOR_NAME);
					String tipo = rs.getString("tipo");
					IDSoggetto erogatore = new IDSoggetto(tipo, nomeErogatore);
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo, rs.getString(SERVICE_NAME), erogatore, rs.getInt(SERVICE_VERSION));
					String group = rs.getBoolean(IS_DEFAULT) ? null : rs.getString(GROUP_NAME);

					InputStream template = rs.getBinaryStream(TEMPLATE_ATTR);
					TreePath key = TreePath.of(
							TemplateScanProperties.REGISTRY_SEGMENT,
							TemplateScanProperties.EROGATION_SEGMENT).add(convertPortName(idServizio, null, group))
							.add("Connettori Multipli");
					
					ScanResult.parse(template, regex)
						.ifPresent(sc -> this.results.put(key, sc));
				}
			}
		}
	}
	
	private void processTemplateGenericProperties(Pattern regex, TemplateScanProperties props, Connection con) throws SQLQueryObjectException, SQLException {
		log.debug("Eseguo ricerca dei template sulle generic property");
		
		ISQLQueryObject sqlObject = SQLObjectFactory.createSQLQueryObject(props.getDbType());
		sqlObject.addSelectAliasField("ps." + CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_NAME, NAME_ATTR);
		sqlObject.addSelectAliasField("ps." + CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_TYPE, "type");
		sqlObject.addSelectAliasField("p." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, TEMPLATE_ATTR);
		sqlObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES, "ps");
		sqlObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY, "p");
		sqlObject.setANDLogicOperator(true);
		sqlObject.addWhereCondition("ps." + CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_ID + " = p." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS);
		sqlObject.addWhereCondition("(p." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME + " = ? OR p." + CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME + " = ?)");
		
		String query = sqlObject.createSQLQuery();
		
		logSql.debug("Eseguo query, token policy e attribute authority, query: {}", query);
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			int i = 1;
			stmt.setString(i++, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_DYNAMIC_PAYLOAD);
			stmt.setString(i++, org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_HTTP_PAYLOAD);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String name = rs.getString(NAME_ATTR);
					String type = rs.getString("type");
					InputStream template = rs.getBinaryStream(TEMPLATE_ATTR);
					TreePath key = null;
					if (type.equals(CostantiProprieta.ATTRIBUTE_AUTHORITY_ID)) {
						key = TreePath.of(
								TemplateScanProperties.CONFIG_SEGMENT,
								TemplateScanProperties.ATTRIBUTE_AUTHORITY_SEGMENT,
								color(name), 
								TemplateScanProperties.ATTRIBUTE_AUTHORITY_REQUEST_SEGMENT);
					} else if (type.equals(CostantiProprieta.TOKEN_NEGOZIAZIONE_ID)) {
						key = TreePath.of(
								TemplateScanProperties.CONFIG_SEGMENT,
								TemplateScanProperties.TOKEN_POLICY_SEGMENT,
								color(name), 
								TemplateScanProperties.TOKEN_POLICY_TOKEN_ENDPOINT_SEGMENT);
					} else {
						key = TreePath.of(
								TemplateScanProperties.MISC_SEGMENT,
								color(name));
					}
					
					TreePath fKey = key;
					ScanResult.parse(template, regex)
						.ifPresent(sc -> this.results.put(fKey, sc));
				}
			}
		}
	}
	
	private static void initLogger() throws CoreException {
		Properties propertiesLog4j = new Properties();
		try (InputStream inPropLog4j = TemplateScan.class.getResourceAsStream(TemplateScanProperties.LOG_PROPERTIES_FILE);){
			propertiesLog4j.load(inPropLog4j);
			PropertiesEnvUtils.resolveGovWayEnvVariables(propertiesLog4j);
			LoggerWrapperFactory.setLogConfiguration(propertiesLog4j);
		} catch(java.lang.Exception e) {
			throw new CoreException("Impossibile leggere i dati dal file '" + TemplateScanProperties.LOG_PROPERTIES_FILE + "': "+e.getMessage());
		}
	}
}
