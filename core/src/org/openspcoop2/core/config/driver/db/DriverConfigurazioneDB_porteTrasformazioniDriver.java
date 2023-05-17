/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.config.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneRest;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteTrasformazioniDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_porteTrasformazioniDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected List<TrasformazioneRegola> porteDelegateTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI;
		String nomeMetodo = "porteDelegateTrasformazioniList";
		boolean delegata = true;
		return getEngineTrasformazioniList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegola> porteApplicativeTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeMetodo = "porteApplicativeTrasformazioniList";
		boolean delegata = false;
		return getEngineTrasformazioniList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegola> getEngineTrasformazioniList(long idPA, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm0 = null;
		ResultSet rs0 = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegola> lista = new ArrayList<TrasformazioneRegola>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("posizione");
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabella+".posizione");
			sqlQueryObject.addOrderBy(nomeTabella+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			rs = stmt.executeQuery();

			List<Long> idTrasformazione = new ArrayList<>();			
			while (rs.next()) {
				
				idTrasformazione.add(rs.getLong("id"));
				
			}
			rs.close();
			stmt.close();
			
			if(idTrasformazione!=null && !idTrasformazione.isEmpty()) {
			
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();
				
				for (Long idLongTrasformazione : idTrasformazione) {
					
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasformazione);

					rs = stmt.executeQuery();
					
					if (rs.next()) {
									
						TrasformazioneRegola regola = new TrasformazioneRegola();
						
						String nome = rs.getString("nome");
						regola.setNome(nome);
		
						int posizione = rs.getInt("posizione");
						regola.setPosizione(posizione);
						
						StatoFunzionalita stato = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("stato"));
						regola.setStato(stato);
						
						String applicabilita_azioni = rs.getString("applicabilita_azioni");
						String applicabilita_ct = rs.getString("applicabilita_ct");
						String applicabilita_pattern = rs.getString("applicabilita_pattern");
						String applicabilita_connettori = rs.getString("applicabilita_connettori");
						if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ||
								(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
								(applicabilita_pattern!=null && !"".equals(applicabilita_pattern))  ||
								(applicabilita_connettori!=null && !"".equals(applicabilita_connettori)) 
								) {
							TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
							
							if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ) {
								if(applicabilita_azioni.contains(",")) {
									String [] tmp = applicabilita_azioni.split(",");
									for (int i = 0; i < tmp.length; i++) {
										applicabilita.addAzione(tmp[i].trim());
									}
								}
								else {
									applicabilita.addAzione(applicabilita_azioni);
								}
							}
							
							if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
								if(applicabilita_ct.contains(",")) {
									String [] tmp = applicabilita_ct.split(",");
									for (int i = 0; i < tmp.length; i++) {
										applicabilita.addContentType(tmp[i].trim());
									}
								}
								else {
									applicabilita.addContentType(applicabilita_ct);
								}
							}
							
							if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
								applicabilita.setPattern(applicabilita_pattern);
							}
							
							if( (applicabilita_connettori!=null && !"".equals(applicabilita_connettori)) ) {
								if(applicabilita_connettori.contains(",")) {
									String [] tmp = applicabilita_connettori.split(",");
									for (int i = 0; i < tmp.length; i++) {
										applicabilita.addConnettore(tmp[i].trim());
									}
								}
								else {
									applicabilita.addConnettore(applicabilita_connettori);
								}
							}
							
							regola.setApplicabilita(applicabilita);
						}
						
						TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
						
						int req_conversione_enabled = rs.getInt("req_conversione_enabled");
						if(CostantiDB.TRUE == req_conversione_enabled) {
							richiesta.setConversione(true);
						}
						else {
							richiesta.setConversione(false);
						}
						richiesta.setConversioneTipo(rs.getString("req_conversione_tipo"));
						IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
						richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "req_conversione_template"));
						richiesta.setContentType(rs.getString("req_content_type"));
						
						int trasformazione_rest = rs.getInt("rest_transformation");
						if(CostantiDB.TRUE == trasformazione_rest) {
							TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
							trasformazioneRest.setMetodo(rs.getString("rest_method"));
							trasformazioneRest.setPath(rs.getString("rest_path"));
							richiesta.setTrasformazioneRest(trasformazioneRest);
						}
							
						int trasformazione_soap = rs.getInt("soap_transformation");
						if(CostantiDB.TRUE == trasformazione_soap) {
							TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
							
							trasformazioneSoap.setVersione(DriverConfigurazioneDBLib.getEnumVersioneSOAP(rs.getString("soap_version")));
							trasformazioneSoap.setSoapAction(rs.getString("soap_action"));
							
							int envelope = rs.getInt("soap_envelope");
							if(CostantiDB.TRUE == envelope) {
								trasformazioneSoap.setEnvelope(true);
							}
							else {
								trasformazioneSoap.setEnvelope(false);
							}
							
							int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
							if(CostantiDB.TRUE == envelope_as_attach) {
								trasformazioneSoap.setEnvelopeAsAttachment(true);
								
								trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
								trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
							}
							else {
								trasformazioneSoap.setEnvelopeAsAttachment(false);
							}
							
							richiesta.setTrasformazioneSoap(trasformazioneSoap);
						}
						
						
						regola.setId(rs.getLong("id"));
						
						regola.setRichiesta(richiesta);
						
						
						// ** SOGGETTI **
						
						if(!portaDelegata) {
							
							nomeTabella = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
							sqlQueryObject.addFromTable(nomeTabella);
							sqlQueryObject.addSelectField("*");
							sqlQueryObject.addWhereCondition("id_trasformazione=?");
							String sqlQuery = sqlQueryObject.createSQLQuery();
							stm0 = con.prepareStatement(sqlQuery);
							stm0.setLong(1, regola.getId());
							rs0 = stm0.executeQuery();
			
							while (rs0.next()) {
								
								if(regola.getApplicabilita()==null) {
									regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
								}
								
								TrasformazioneRegolaApplicabilitaSoggetto soggetto = new TrasformazioneRegolaApplicabilitaSoggetto();
								soggetto.setTipo(rs0.getString("tipo_soggetto"));
								soggetto.setNome(rs0.getString("nome_soggetto"));
								
								regola.getApplicabilita().addSoggetto(soggetto);
							
							}
							rs0.close();
							stm0.close();
						}
						
						
						// ** APPLICATIVI **
						
						nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(nomeTabella);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_trasformazione=?");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stm0 = con.prepareStatement(sqlQuery);
						stm0.setLong(1, regola.getId());
						rs0 = stm0.executeQuery();
		
						// per ogni entry 
						// prendo l'id del servizio associato, recupero il nome e
						// aggiungo
						// il servizio applicativo alla PortaDelegata da ritornare
						while (rs0.next()) {
							long idSA = rs0.getLong("id_servizio_applicativo");
		
							if (idSA != 0) {
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
								sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
								sqlQueryObject.addSelectField("nome");
								sqlQueryObject.addSelectField("tipo_soggetto");
								sqlQueryObject.addSelectField("nome_soggetto");
								sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
								sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQuery = sqlQueryObject.createSQLQuery();
								stm1 = con.prepareStatement(sqlQuery);
								stm1.setLong(1, idSA);
		
								this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));
		
								rs1 = stm1.executeQuery();
		
								TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = null;
								if (rs1.next()) {
									// setto solo il nome come da specifica
									servizioApplicativo = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
									servizioApplicativo.setId(idSA);
									servizioApplicativo.setNome(rs1.getString("nome"));
									servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
									servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
									if(regola.getApplicabilita()==null) {
										regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
									}
									regola.getApplicabilita().addServizioApplicativo(servizioApplicativo);
								}
								rs1.close();
								stm1.close();
							}
						}
						rs0.close();
						stm0.close();
						
										
						lista.add(regola);

					}
					
					rs.close();
					stmt.close();
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs0, stm0);
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	}
	
	protected boolean azioneUsataInTrasformazioniPortaDelegata(String azione) throws DriverConfigurazioneException {
		return this.azioneUsataInTrasformazioni(azione, true);
	}
	protected boolean azioneUsataInTrasformazioniPortaApplicativa(String azione) throws DriverConfigurazioneException {
		return this.azioneUsataInTrasformazioni(azione, false);
	}
	private boolean azioneUsataInTrasformazioni(String azione, boolean portaDelegata) throws DriverConfigurazioneException {

		String nomeMetodo = "azioneUsataInTrasformazioni";
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("applicabilita_azioni");
			sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azione, false, true, false);
			sqlQueryObject.setANDLogicOperator(true);
			stmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			rs = stmt.executeQuery();

			while (rs.next()) {
				
				String checkAz = rs.getString("applicabilita_azioni");
				if(checkAz!=null) {
					if(azione.equals(checkAz)) {
						return true;
					}
					if(checkAz.contains(",")) {
						String [] tmp = checkAz.split(",");
						if(tmp!=null && tmp.length>0) {
							for (int i = 0; i < tmp.length; i++) {
								if(azione.equals(tmp[i])) {
									return true;
								}
							}
						}
					}
				}

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return false;
	}
	
	
	protected TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			boolean interpretaNullList) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazione";
		boolean delegata = false;
		
		return getEngineTrasformazione(idPorta, azioni, pattern, contentType, connettori, soggetti, applicativi, nomeMetodo, delegata, interpretaNullList);
		
	}
	
	protected TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazione";
		boolean delegata = true;
		return getEngineTrasformazione(idPorta, azioni, pattern, contentType, connettori, null, applicativi, nomeMetodo, delegata, 
				true); // esiste solo una lista
	}
	
	private TrasformazioneRegola getEngineTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			String nomeMetodo, boolean portaDelegata, boolean interpretaNullList) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaSoggetti = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI; //portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SOGGETTI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
		String nomeTabellaSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegola regola = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if(azioni != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_azioni = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azioni, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_azioni");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_ct");
			}
			
			if(connettori != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_connettori = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_connettori", connettori, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_connettori");
			}
			
			if(!portaDelegata) {
				if(soggetti==null || soggetti.isEmpty()) {
					if(interpretaNullList) {
						ISQLQueryObject sqlQueryObjectExists = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectExists.addFromTable(nomeTabellaSoggetti);
						sqlQueryObjectExists.addSelectField("id_trasformazione");
						sqlQueryObjectExists.setANDLogicOperator(true);
						sqlQueryObjectExists.addWhereCondition("id_trasformazione="+nomeTabella+".id");
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExists);	
					}
				}
				else {
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione="+nomeTabella+".id");
					List<String> conditions = new ArrayList<>();
					for (@SuppressWarnings("unused") TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto : soggetti) {
						StringBuilder bf = new StringBuilder();
						bf.append("( ");
						bf.append(nomeTabellaSoggetti).append(".tipo_soggetto=?");
						bf.append(" AND ");
						bf.append(nomeTabellaSoggetti).append(".nome_soggetto=?");
						bf.append(") ");
						conditions.add(bf.toString());
					}
					sqlQueryObject.addWhereCondition(false, conditions.toArray(new String[1]));
				}
			}
			
			if(applicativi==null || applicativi.isEmpty()) {
				if(interpretaNullList) {
					ISQLQueryObject sqlQueryObjectExists = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectExists.addFromTable(nomeTabellaSA);
					sqlQueryObjectExists.addSelectField("id_trasformazione");
					sqlQueryObjectExists.setANDLogicOperator(true);
					sqlQueryObjectExists.addWhereCondition("id_trasformazione="+nomeTabella+".id");
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExists);	
				}
			}
			else {
				sqlQueryObject.addFromTable(nomeTabellaSA);
				sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_trasformazione="+nomeTabella+".id");
				List<String> conditions = new ArrayList<>();
				for (@SuppressWarnings("unused") TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaSA : applicativi) {
					StringBuilder bf = new StringBuilder();
					bf.append("( ");
					bf.append(nomeTabellaSA).append(".id_servizio_applicativo=?");
					bf.append(") ");
					conditions.add(bf.toString());
				}
				sqlQueryObject.addWhereCondition(false, conditions.toArray(new String[1]));
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
//			if(azioni != null)
//				stmt.setString(parameterIndex ++, azioni);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			if(!portaDelegata) {
				if(soggetti==null || soggetti.isEmpty()) {
					// nop;
				}
				else {
					for (TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto : soggetti) {
						stmt.setString(parameterIndex ++, trasformazioneRegolaApplicabilitaSoggetto.getTipo());
						stmt.setString(parameterIndex ++, trasformazioneRegolaApplicabilitaSoggetto.getNome());
					}
				}
			}
			
			if(applicativi==null || applicativi.isEmpty()) {
				// nop;
			}
			else {
				for (TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaSA : applicativi) {
					long idSA = DBUtils.getIdServizioApplicativo(trasformazioneRegolaApplicabilitaSA.getNome(), 
							trasformazioneRegolaApplicabilitaSA.getTipoSoggettoProprietario(), trasformazioneRegolaApplicabilitaSA.getNomeSoggettoProprietario(), con, this.driver.tipoDB);
					stmt.setLong(parameterIndex ++, idSA);
				}
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				
				regola = getEngineTrasformazione(risultato);
			}
			
			risultato.close();
			stmt.close();

			return regola;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazione";
		boolean delegata = false;
		return getEngineTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazione";
		boolean delegata = true;
		return getEngineTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegola getEngineTrasformazione(long idPorta, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegola regola = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setString(parameterIndex ++, nome);
						
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				
				regola = getEngineTrasformazione(risultato);
			}
			
			risultato.close();
			stmt.close();

			return regola;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	private TrasformazioneRegola getEngineTrasformazione(ResultSet risultato) throws Exception {
		TrasformazioneRegola regola  = new TrasformazioneRegola();
				
		StatoFunzionalita stato = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(risultato.getString("stato"));
		regola.setStato(stato);
		
		String applicabilita_azioni = risultato.getString("applicabilita_azioni");
		String applicabilita_ct = risultato.getString("applicabilita_ct");
		String applicabilita_pattern = risultato.getString("applicabilita_pattern");
		String applicabilita_connettori = risultato.getString("applicabilita_connettori");
		if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ||
				(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
				(applicabilita_pattern!=null && !"".equals(applicabilita_pattern))  ||
				(applicabilita_connettori!=null && !"".equals(applicabilita_connettori)) 
				) {
			TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
			
			if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ) {
				if(applicabilita_azioni.contains(",")) {
					String [] tmp = applicabilita_azioni.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addAzione(tmp[i].trim());
					}
				}
				else {
					applicabilita.addAzione(applicabilita_azioni);
				}
			}
			
			if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
				if(applicabilita_ct.contains(",")) {
					String [] tmp = applicabilita_ct.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addContentType(tmp[i].trim());
					}
				}
				else {
					applicabilita.addContentType(applicabilita_ct);
				}
			}
			
			if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
				applicabilita.setPattern(applicabilita_pattern);
			}
			
			if( (applicabilita_connettori!=null && !"".equals(applicabilita_connettori)) ) {
				if(applicabilita_connettori.contains(",")) {
					String [] tmp = applicabilita_connettori.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addConnettore(tmp[i].trim());
					}
				}
				else {
					applicabilita.addConnettore(applicabilita_connettori);
				}
			}
			
			regola.setApplicabilita(applicabilita);
		}
		
		TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
		
		int req_conversione_enabled = risultato.getInt("req_conversione_enabled");
		if(CostantiDB.TRUE == req_conversione_enabled) {
			richiesta.setConversione(true);
		}
		else {
			richiesta.setConversione(false);
		}
		richiesta.setConversioneTipo(risultato.getString("req_conversione_tipo"));
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
		richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(risultato, "req_conversione_template"));
		richiesta.setContentType(risultato.getString("req_content_type"));
		
		int trasformazione_rest = risultato.getInt("rest_transformation");
		if(CostantiDB.TRUE == trasformazione_rest) {
			TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
			trasformazioneRest.setMetodo(risultato.getString("rest_method"));
			trasformazioneRest.setPath(risultato.getString("rest_path"));
			richiesta.setTrasformazioneRest(trasformazioneRest);
		}
			
		int trasformazione_soap = risultato.getInt("soap_transformation");
		if(CostantiDB.TRUE == trasformazione_soap) {
			TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
			
			trasformazioneSoap.setVersione(DriverConfigurazioneDBLib.getEnumVersioneSOAP(risultato.getString("soap_version")));
			trasformazioneSoap.setSoapAction(risultato.getString("soap_action"));
			
			int envelope = risultato.getInt("soap_envelope");
			if(CostantiDB.TRUE == envelope) {
				trasformazioneSoap.setEnvelope(true);
			}
			else {
				trasformazioneSoap.setEnvelope(false);
			}
			
			int envelope_as_attach = risultato.getInt("soap_envelope_as_attach");
			if(CostantiDB.TRUE == envelope_as_attach) {
				trasformazioneSoap.setEnvelopeAsAttachment(true);
				
				trasformazioneSoap.setEnvelopeBodyConversioneTipo(risultato.getString("soap_envelope_tipo"));
				trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(risultato, "soap_envelope_template"));
			}
			else {
				trasformazioneSoap.setEnvelopeAsAttachment(false);
			}
			
			richiesta.setTrasformazioneSoap(trasformazioneSoap);
		}
		
		regola.setId(risultato.getLong("id"));
		
		regola.setRichiesta(richiesta);
		
		return regola;

	}
	
	protected boolean existsPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazione";
		boolean delegata = false;
		return _existsTrasformazione(idPorta, azioni, pattern, contentType, connettori, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazione";
		boolean delegata = true;
		return _existsTrasformazione(idPorta, azioni, pattern, contentType, connettori, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if(azioni != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_azioni = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azioni, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_azioni");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_ct");
			}
			
			if(connettori != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_connettori = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_connettori", connettori, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_connettori");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
//			if(azioni != null)
//				stmt.setString(parameterIndex ++, azioni);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected boolean existsPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazione";
		boolean delegata = false;
		return _existsTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazione";
		boolean delegata = true;
		return _existsTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazione(long idPorta, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
						
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setString(parameterIndex ++, nome);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected List<TrasformazioneRegolaRisposta> porteDelegateTrasformazioniRispostaList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaList";
		boolean delegata = true;
		return getEngineTrasformazioniRispostaList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegolaRisposta> porteApplicativeTrasformazioniRispostaList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeMetodo = "porteApplicativeTrasformazioniRispostaList";
		boolean delegata = false;
		return getEngineTrasformazioniRispostaList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaRisposta> getEngineTrasformazioniRispostaList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaRisposta> lista = new ArrayList<TrasformazioneRegolaRisposta>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectAliasField(nomeTabellaRisposta, "id", "idTrasRisposta");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addOrderBy(nomeTabellaRisposta+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idTrasformazioneRisposta = new ArrayList<>();
			while (rs.next()) { 
				idTrasformazioneRisposta.add(rs.getLong("idTrasRisposta"));
			}
			rs.close();
			stmt.close();
			
			if(idTrasformazioneRisposta!=null && !idTrasformazioneRisposta.isEmpty()) {
				for (Long idLongTrasformazioneRisposta : idTrasformazioneRisposta) {
						
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRisposta);
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
					sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasformazioneRisposta);

					rs = stmt.executeQuery();
					
					if (rs.next()) { 
						TrasformazioneRegolaRisposta risposta = _readTrasformazioneRegolaRisposta(rs);
						lista.add(risposta);
					}
					
					rs.close();
					stmt.close();
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	protected TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return getEngineTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return getEngineTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaRisposta getEngineTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegolaRisposta risposta = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setANDLogicOperator(true);
			
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_max");
			}
			
			if(pattern != null) {
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_pattern");
			}
			
			if(contentType != null) {
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_ct");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				
				risposta = _readTrasformazioneRegolaRisposta(rs);
			}
			
			rs.close();
			stmt.close();

			return risposta;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return getEngineTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return getEngineTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaRisposta getEngineTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegolaRisposta risposta = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			stmt.setString(parameterIndex ++, nome);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				
				risposta = _readTrasformazioneRegolaRisposta(rs);
			}
			
			rs.close();
			stmt.close();

			return risposta;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	private TrasformazioneRegolaRisposta _readTrasformazioneRegolaRisposta(ResultSet rs) throws Exception{
		TrasformazioneRegolaRisposta risposta = new TrasformazioneRegolaRisposta();
		
		String nome = rs.getString("nome");
		risposta.setNome(nome);

		int posizione = rs.getInt("posizione");
		risposta.setPosizione(posizione);
		
		int applicabilita_status_min = rs.getInt("applicabilita_status_min");
		int applicabilita_status_max = rs.getInt("applicabilita_status_max");
		String applicabilita_ct = rs.getString("applicabilita_ct");
		String applicabilita_pattern = rs.getString("applicabilita_pattern");
		if( (applicabilita_status_min >0 || applicabilita_status_max>0) ||
				(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
				(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
				) {
			TrasformazioneRegolaApplicabilitaRisposta applicabilita = new TrasformazioneRegolaApplicabilitaRisposta();
			
			if(applicabilita_status_min>0) {
				applicabilita.setReturnCodeMin(applicabilita_status_min);
			}
			if(applicabilita_status_max>0) {
				applicabilita.setReturnCodeMax(applicabilita_status_max);
			}

			if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
				if(applicabilita_ct.contains(",")) {
					String [] tmp = applicabilita_ct.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addContentType(tmp[i].trim());
					}
				}
				else {
					applicabilita.addContentType(applicabilita_ct);
				}
			}
			
			if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
				applicabilita.setPattern(applicabilita_pattern);
			}
			
			risposta.setApplicabilita(applicabilita);
		}
		
		int conversione_enabled = rs.getInt("conversione_enabled");
		if(CostantiDB.TRUE == conversione_enabled) {
			risposta.setConversione(true);
		}
		else {
			risposta.setConversione(false);
		}
		risposta.setConversioneTipo(rs.getString("conversione_tipo"));
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
		risposta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "conversione_template"));
		risposta.setContentType(rs.getString("content_type"));
		risposta.setReturnCode(rs.getString("return_code"));
		
		int trasformazione_rest = rs.getInt("rest_transformation");
		if(CostantiDB.TRUE == trasformazione_rest) {
		
			// la risposta deve essere ritornata in SOAP
			
			TrasformazioneSoapRisposta trasformazioneSoap = new TrasformazioneSoapRisposta();
			
			int envelope = rs.getInt("soap_envelope");
			if(CostantiDB.TRUE == envelope) {
				trasformazioneSoap.setEnvelope(true);
			}
			else {
				trasformazioneSoap.setEnvelope(false);
			}
			
			int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
			if(CostantiDB.TRUE == envelope_as_attach) {
				trasformazioneSoap.setEnvelopeAsAttachment(true);
				
				trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
				trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
			}
			else {
				trasformazioneSoap.setEnvelopeAsAttachment(false);
			}
			
			risposta.setTrasformazioneSoap(trasformazioneSoap);
			
		}

		risposta.setId(rs.getLong("id"));
		
		return  risposta;
	}
	
	protected boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			
			
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_max");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_ct");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			stmt.setString(parameterIndex ++, nome);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteDelegateTrasformazioniServiziApplicativiList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO;
		String nomeMetodo = "porteDelegateTrasformazioniServiziApplicativiList";
		boolean delegata = true;
		return getEngineTrasformazioniServiziApplicativiList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteApplicativeTrasformazioniServiziApplicativiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;
		String nomeMetodo = "porteApplicativeTrasformazioniServiziApplicativiList";
		boolean delegata = false;
		return getEngineTrasformazioniServiziApplicativiList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaApplicabilitaServizioApplicativo> getEngineTrasformazioniServiziApplicativiList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaTrasformazioneSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
		String nomeTabellaSA = CostantiDB.SERVIZI_APPLICATIVI;
		String nomeTabellaSoggetti = CostantiDB.SOGGETTI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaApplicabilitaServizioApplicativo> lista = new ArrayList<TrasformazioneRegolaApplicabilitaServizioApplicativo>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
			sqlQueryObject.addSelectCountField(nomeTabellaTrasformazioneSA+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
			sqlQueryObject.addFromTable(nomeTabellaSA);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectAliasField(nomeTabellaTrasformazioneSA, "id", "idTrasSA");
			sqlQueryObject.addSelectField(nomeTabellaSA+".nome");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaSA+".id="+nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_soggetto="+nomeTabellaSoggetti+".id");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaSA+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasSA"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
					sqlQueryObject.addFromTable(nomeTabellaSA);
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addSelectField(nomeTabellaSA+".nome");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id_trasformazione");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaSA+".id="+nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_soggetto="+nomeTabellaSoggetti+".id");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);
					
					rs = stmt.executeQuery();
					if(rs.next()) {
						TrasformazioneRegolaApplicabilitaServizioApplicativo risposta = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
						risposta.setId(rs.getLong("id"));
						risposta.setNome(rs.getString("nome"));
						risposta.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
						risposta.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));
						lista.add(risposta);		
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	protected List<TrasformazioneRegolaApplicabilitaSoggetto> porteApplicativeTrasformazioniSoggettiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;
		String nomeMetodo = "porteApplicativeTrasformazioniSoggettiList";
		boolean delegata = false;
		return getEngineTrasformazioniSoggettiList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaApplicabilitaSoggetto> getEngineTrasformazioniSoggettiList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaSoggetti = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaApplicabilitaSoggetto> lista = new ArrayList<TrasformazioneRegolaApplicabilitaSoggetto>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectCountField(nomeTabellaSoggetti+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectAliasField(nomeTabellaSoggetti, "id", "idTrasSoggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addOrderBy(nomeTabellaSoggetti+".nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasSoggetto"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();
					
					if(rs.next()) {
						TrasformazioneRegolaApplicabilitaSoggetto risposta = new TrasformazioneRegolaApplicabilitaSoggetto();
						risposta.setId(rs.getLong("id"));
						risposta.setTipo(rs.getString("tipo_soggetto"));
						risposta.setNome(rs.getString("nome_soggetto"));
						lista.add(risposta);
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	
	protected List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRispostaHeaderList(long idPD, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaHeaderList";
		boolean delegata = true;
		return getEngineTrasformazioniRispostaHeaderList(idPD, idTrasformazione, idTrasformazioneRisposta, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRispostaHeaderList(long idPA, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
		String nomeMetodo = "porteApplicativeTrasformazioniRispostaHeaderList";
		boolean delegata = false;
		return getEngineTrasformazioniRispostaHeaderList(idPA, idTrasformazione, idTrasformazioneRisposta, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> getEngineTrasformazioniRispostaHeaderList(long idPA, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRispostaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);
			stmt.setLong(3, idTrasformazioneRisposta);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRispostaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);
			stmt.setLong(3, idTrasformazioneRisposta);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRisposta);
					sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".identificazione_fallita");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
					sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();

					if(rs.next()) {
						
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
						
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	protected boolean existsPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRispostaHeader";
		boolean delegata = false;
		return _existsTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRispostaHeader";
		boolean delegata = true;
		return _existsTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRispostaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setLong(parameterIndex ++, idTrasformazioneRisposta);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRispostaHeader";
		boolean delegata = false;
		return getEngineTrasformazioneRispostaHeader(idPorta, idTrasformazione,  idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRispostaHeader";
		boolean delegata = true;
		return getEngineTrasformazioneRispostaHeader(idPorta, idTrasformazione,  idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro getEngineTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".identificazione_fallita");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setLong(parameterIndex ++, idTrasformazioneRisposta);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaHeaderList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaHeaderList";
		boolean delegata = true;
		return getEngineTrasformazioniRichiestaHeaderList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaHeaderList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER;
		String nomeMetodo = "porteApplicativeTrasformazioniRichiestaHeaderList";
		boolean delegata = false;
		return getEngineTrasformazioniRichiestaHeaderList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> getEngineTrasformazioniRichiestaHeaderList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRichiestaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".identificazione_fallita");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();
					
					if(rs.next()) {
						
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
						
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}
			

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	protected boolean existsPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRichiestaHeader";
		boolean delegata = false;
		return _existsTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRichiestaHeader";
		boolean delegata = true;
		return _existsTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRichiestaHeader";
		boolean delegata = false;
		return getEngineTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRichiestaHeader";
		boolean delegata = true;
		return getEngineTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro getEngineTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".identificazione_fallita");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaUrlParameterList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaUrlParameterList";
		boolean delegata = true;
		return getEngineTrasformazioniRichiestaUrlParameterList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	protected List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaUrlParameterList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
		String nomeMetodo = "porteApplicativeTrasformazioniRichiestaUrlParameterList";
		boolean delegata = false;
		return getEngineTrasformazioniRichiestaUrlParameterList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> getEngineTrasformazioniRichiestaUrlParameterList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRichiestaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".identificazione_fallita");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);
					
					rs = stmt.executeQuery();
					
					if(rs.next()) {
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
					}
					
					rs.close();
					stmt.close();
					
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	} 
	
	protected boolean existsPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRichiestaUrlParameter";
		boolean delegata = false;
		return _existsTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected boolean existsPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRichiestaUrlParameter";
		boolean delegata = true;
		return _existsTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRichiestaUrlParameter";
		boolean delegata = false;
		return getEngineTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	protected TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRichiestaUrlParameter";
		boolean delegata = true;
		return getEngineTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro getEngineTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".identificazione_fallita");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString("identificazione_fallita")));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmt);

			this.driver.closeConnection(error,con);
		}
	}
}
