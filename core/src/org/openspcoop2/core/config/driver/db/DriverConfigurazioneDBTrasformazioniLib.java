/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
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
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_trasformazioniLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDBTrasformazioniLib {
	
	private DriverConfigurazioneDBTrasformazioniLib() {}

	private static final String ESEGUO_QUERY_PREFIX = "eseguo query : ";
	
	private static void normalizePositionsEngine(Trasformazioni trasformazioni) {
		if(trasformazioni==null || trasformazioni.sizeRegolaList()<=0) {
			return;
		}
		
		List<Integer> posizioni = new ArrayList<>();
		HashMap<Integer, TrasformazioneRegola> regole = new HashMap<>();
		for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
			posizioni.add(regola.getPosizione());
			regole.put(regola.getPosizione(), regola);
		}
		while(trasformazioni.sizeRegolaList()>0) {
			trasformazioni.removeRegola(0);
		}
		Collections.sort( posizioni );
		int posNew = 1;
		for (Integer pos : posizioni) {
			TrasformazioneRegola regola = regole.get(pos);
			regola.setPosizione(posNew);
			trasformazioni.addRegola(regola);
			posNew++;
		}
		
		// normalizzazione delle risposte
		for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
			
			if(regola.getRispostaList()==null || regola.sizeRispostaList()<=0){
				continue;
			}
			
			List<Integer> posizioniRisposta = new ArrayList<>();
			HashMap<Integer, TrasformazioneRegolaRisposta> regoleRisposta = new HashMap<>();
			for (TrasformazioneRegolaRisposta regolaRisposta : regola.getRispostaList()) {
				posizioniRisposta.add(regolaRisposta.getPosizione());
				regoleRisposta.put(regolaRisposta.getPosizione(), regolaRisposta);
			}
			while(regola.sizeRispostaList()>0) {
				regola.removeRisposta(0);
			}
			Collections.sort( posizioniRisposta );
			int posRispostaNew = 1;
			for (Integer posRisposta : posizioniRisposta) {
				TrasformazioneRegolaRisposta regolaRisposta = regoleRisposta.get(posRisposta);
				regolaRisposta.setPosizione(posRispostaNew);
				regola.addRisposta(regolaRisposta);
				posRispostaNew++;
			}
		}
	}
	
	static void CRUDTrasformazioni(int type, Connection con, Trasformazioni trasformazioni, 
			Long idProprietario, boolean portaDelegata) throws DriverConfigurazioneException, JDBCAdapterException {
		
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
		try {
			switch (type) {
			case CREATE:
		
				if(trasformazioni==null || trasformazioni.sizeRegolaList()<=0) {
					break;
				}
				
				normalizePositionsEngine(trasformazioni);
				
				for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
					
					String applicabilitaAzioni = null;
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeAzioneList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < regola.getApplicabilita().sizeAzioneList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(regola.getApplicabilita().getAzione(i));
						}
						if(bf.length()>0) {
							applicabilitaAzioni = bf.toString();
						}
					}
					
					String applicabilitaCT = null;
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeContentTypeList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < regola.getApplicabilita().sizeContentTypeList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(regola.getApplicabilita().getContentType(i));
						}
						if(bf.length()>0) {
							applicabilitaCT = bf.toString();
						}
					}
					
					String applicabilitaConnettori = null;
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeConnettoreList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < regola.getApplicabilita().sizeConnettoreList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(regola.getApplicabilita().getConnettore(i));
						}
						if(bf.length()>0) {
							applicabilitaConnettori = bf.toString();
						}
					}
					
					List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("id_porta", idProprietario , InsertAndGeneratedKeyJDBCType.LONG) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", regola.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE, regola.getPosizione() , InsertAndGeneratedKeyJDBCType.INT) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("stato", DriverConfigurazioneDBLib.getValue(regola.getStato()) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_azioni", applicabilitaAzioni , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_CT, applicabilitaCT , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_PATTERN, (regola.getApplicabilita()!=null ? regola.getApplicabilita().getPattern() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_connettori", applicabilitaConnettori , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_conversione_enabled", ( regola.getRichiesta()!=null && regola.getRichiesta().getConversione()) ? CostantiDB.TRUE : CostantiDB.FALSE , InsertAndGeneratedKeyJDBCType.INT) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_conversione_tipo", (regola.getRichiesta()!=null ? regola.getRichiesta().getConversioneTipo() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_content_type", (regola.getRichiesta()!=null ? regola.getRichiesta().getContentType() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					if(regola.getRichiesta()!=null && regola.getRichiesta().getTrasformazioneRest()!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_transformation", CostantiDB.TRUE , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_method", regola.getRichiesta().getTrasformazioneRest().getMetodo() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_path", regola.getRichiesta().getTrasformazioneRest().getPath() , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					if(regola.getRichiesta()!=null && regola.getRichiesta().getTrasformazioneSoap()!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_transformation", CostantiDB.TRUE , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_version", DriverConfigurazioneDBLib.getValue(regola.getRichiesta().getTrasformazioneSoap().getVersione()) , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_action", regola.getRichiesta().getTrasformazioneSoap().getSoapAction() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE, (regola.getRichiesta().getTrasformazioneSoap().getEnvelope() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_AS_ATTACH, (regola.getRichiesta().getTrasformazioneSoap().getEnvelopeAsAttachment() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TIPO, regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					// Insert and return generated key
					String tableName = null;
					String columnIdName = null;
					String sequence = null;
					String tableForId = null;
					if(portaDelegata) {
						tableName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI;
						columnIdName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_ID;
						sequence = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SEQUENCE;
						tableForId = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_TABLE_FOR_ID;
					}
					else {
						tableName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
						columnIdName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_ID;
						sequence = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SEQUENCE;
						tableForId = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_TABLE_FOR_ID;
					}
					long idtrasformazione = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
							new CustomKeyGeneratorObject(tableName, columnIdName, sequence, tableForId),
							listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
					if(idtrasformazione<=0){
						throw new DriverConfigurazioneException("ID autoincrementale non ottenuto");
					}
					
					if( regola.getRichiesta()!=null && 
							(
									regola.getRichiesta().getConversioneTemplate()!=null 
									||
									(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null)
							) ) {
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addUpdateTable(tableName);
						if(regola.getRichiesta().getConversioneTemplate()!=null ) {
							sqlQueryObject.addUpdateField("req_conversione_template", "?");
						}
						if(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
							sqlQueryObject.addUpdateField(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TEMPLATE, "?");
						}
						sqlQueryObject.addWhereCondition(columnIdName+"=?");
						String updateQuery = sqlQueryObject.createSQLUpdate();
						updateStmt = con.prepareStatement(updateQuery);
						int index = 1;
						if(regola.getRichiesta().getConversioneTemplate()!=null ) {
							jdbcAdapter.setBinaryData(updateStmt, index++, regola.getRichiesta()!=null ? regola.getRichiesta().getConversioneTemplate() : null);
						}
						if(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
							jdbcAdapter.setBinaryData(updateStmt, index++, regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
						}
						updateStmt.setLong(index++, idtrasformazione);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				
					
					if(regola.getRichiesta()!=null) {
						
						if(regola.getApplicabilita()!=null) {
							
							if(!portaDelegata && regola.getApplicabilita().sizeSoggettoList()>0) {
								// Soggetti
								int n=0;
								for (int j = 0; j < regola.getApplicabilita().sizeSoggettoList(); j++) {
									TrasformazioneRegolaApplicabilitaSoggetto soggetto = regola.getApplicabilita().getSoggetto(j);
									ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
									sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE, "?");
									sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_TIPO_SOGGETTO, "?");
									sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_NOME_SOGGETTO, "?");
									String sqlQuery = sqlQueryObject.createSQLInsert();
									updateStmt = con.prepareStatement(sqlQuery);
									updateStmt.setLong(1, idtrasformazione);
									updateStmt.setString(2, soggetto.getTipo());
									updateStmt.setString(3, soggetto.getNome());
									updateStmt.executeUpdate();
									updateStmt.close();
									n++;
									DriverConfigurazioneDBLib.logDebug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla Trasformazione[" + idtrasformazione + "]");
								}
								
								DriverConfigurazioneDBLib.logDebug("Aggiunti " + n + " soggetti alla Trasformazione[" + idtrasformazione + "]");
							}
							
							
							// serviziapplicativi
							if(regola.getApplicabilita().sizeServizioApplicativoList()>0) {
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
								}
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE, "?");
								sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
								String sqlQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(sqlQuery);
				
								int i = 0;
								for (i = 0; i < regola.getApplicabilita().sizeServizioApplicativoList(); i++) {
									TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = regola.getApplicabilita().getServizioApplicativo(i);
									String nomeSA = servizioApplicativo.getNome();
									String nomeProprietarioSA = servizioApplicativo.getNomeSoggettoProprietario();
									String tipoProprietarioSA = servizioApplicativo.getTipoSoggettoProprietario();
									if (nomeSA == null || nomeSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Nome del ServizioApplicativo associato non valido.");
									if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Nome Proprietario del ServizioApplicativo associato non valido.");
									if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Tipo Proprietario del ServizioApplicativo associato non valido.");
				
									long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				
									if (idSA <= 0)
										throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
				
									updateStmt.setLong(1, idtrasformazione);
									updateStmt.setLong(2, idSA);
									updateStmt.executeUpdate();
								}
								updateStmt.close();
								DriverConfigurazioneDBLib.logDebug("Inseriti " + i + " servizi applicativi autorizzati associati alla Trasformazione[" + idtrasformazione + "]");
							}
							
						}
						
						if(regola.getRichiesta().sizeHeaderList()>0) {
							for (TrasformazioneRegolaParametro parametro : regola.getRichiesta().getHeaderList()) {
								
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER);
								}
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE, "?");
								sqlQueryObject.addInsertField("tipo", "?");
								sqlQueryObject.addInsertField("nome", "?");
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE, "?");
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA, "?");
								
								String updateQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								updateStmt.setLong(index++, idtrasformazione);
								updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getConversioneTipo()));
								updateStmt.setString(index++, parametro.getNome());
								updateStmt.setString(index++, parametro.getValore());
								updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getIdentificazioneFallita()));
								updateStmt.executeUpdate();
								updateStmt.close();
							}
						}
						
						if(regola.getRichiesta().sizeParametroUrlList()>0) {
							for (TrasformazioneRegolaParametro parametro : regola.getRichiesta().getParametroUrlList()) {
								
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL);
								}
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE, "?");
								sqlQueryObject.addInsertField("tipo", "?");
								sqlQueryObject.addInsertField("nome", "?");
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE, "?");
								sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA, "?");
								
								String updateQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								updateStmt.setLong(index++, idtrasformazione);
								updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getConversioneTipo()));
								updateStmt.setString(index++, parametro.getNome());
								updateStmt.setString(index++, parametro.getValore());
								updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getIdentificazioneFallita()));
								updateStmt.executeUpdate();
								updateStmt.close();
							}
						}
					}
					
					if(regola.sizeRispostaList()>0) {
						
						for (TrasformazioneRegolaRisposta regolaRisposta : regola.getRispostaList()) {
							
							String applicabilitaCtResponse = null;
							if(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().sizeContentTypeList()>0) {
								StringBuilder bf = new StringBuilder();
								for (int i = 0; i < regolaRisposta.getApplicabilita().sizeContentTypeList(); i++) {
									if(i>0) {
										bf.append(",");
									}
									bf.append(regolaRisposta.getApplicabilita().getContentType(i));
								}
								if(bf.length()>0) {
									applicabilitaCtResponse = bf.toString();
								}
							}
							
							listInsertAndGeneratedKeyObject = new ArrayList<>();
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE, idtrasformazione , InsertAndGeneratedKeyJDBCType.LONG) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", regolaRisposta.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE, regolaRisposta.getPosizione() , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_status_min", 
									(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().getReturnCodeMin()!=null && 
									regolaRisposta.getApplicabilita().getReturnCodeMin().intValue()>0) ? regolaRisposta.getApplicabilita().getReturnCodeMin().intValue() : null , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_status_max", 
									(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().getReturnCodeMax()!=null && 
									regolaRisposta.getApplicabilita().getReturnCodeMax().intValue()>0) ? regolaRisposta.getApplicabilita().getReturnCodeMax().intValue() : null , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_CT, applicabilitaCtResponse , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_PATTERN, (regolaRisposta.getApplicabilita()!=null ? regolaRisposta.getApplicabilita().getPattern() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("conversione_enabled", regolaRisposta.getConversione() ? CostantiDB.TRUE : CostantiDB.FALSE , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("conversione_tipo", regolaRisposta.getConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("content_type", regolaRisposta.getContentType() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("return_code", regolaRisposta.getReturnCode(), InsertAndGeneratedKeyJDBCType.STRING) );
							if(regolaRisposta.getTrasformazioneSoap()!=null) {
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE, (regolaRisposta.getTrasformazioneSoap().getEnvelope() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_AS_ATTACH, (regolaRisposta.getTrasformazioneSoap().getEnvelopeAsAttachment() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TIPO, regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
							}
							// Insert and return generated key
							tableName = null;
							columnIdName = null;
							sequence = null;
							tableForId = null;
							if(portaDelegata) {
								tableName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
								columnIdName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID;
								sequence = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_SEQUENCE;
								tableForId = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID;
							}
							else {
								tableName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
								columnIdName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID;
								sequence = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_SEQUENCE;
								tableForId = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID;
							}
							long idtrasformazioneRisposta = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDBLib.tipoDB), 
									new CustomKeyGeneratorObject(tableName, columnIdName, sequence, tableForId),
									listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
							if(idtrasformazioneRisposta<=0){
								throw new DriverConfigurazioneException("ID autoincrementale non ottenuto");
							}
							
							if( 	regolaRisposta.getConversioneTemplate()!=null 
											||
											(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null)
								 ) {
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
								sqlQueryObject.addUpdateTable(tableName);
								if(regolaRisposta.getConversioneTemplate()!=null ) {
									sqlQueryObject.addUpdateField("conversione_template", "?");
								}
								if(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
									sqlQueryObject.addUpdateField(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TEMPLATE, "?");
								}
								sqlQueryObject.addWhereCondition(columnIdName+"=?");
								String updateQuery = sqlQueryObject.createSQLUpdate();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								if(regolaRisposta.getConversioneTemplate()!=null ) {
									jdbcAdapter.setBinaryData(updateStmt, index++, regolaRisposta.getConversioneTemplate());
								}
								if(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
									jdbcAdapter.setBinaryData(updateStmt, index++, regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
								}
								updateStmt.setLong(index++, idtrasformazioneRisposta);
								updateStmt.executeUpdate();
								updateStmt.close();
							}
							
							if(regolaRisposta.sizeHeaderList()>0) {
								for (TrasformazioneRegolaParametro parametro : regolaRisposta.getHeaderList()) {
									
									ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									if(portaDelegata) {
										sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER);
									}
									else {
										sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER);
									}
									sqlQueryObject.addInsertField("id_transform_risp", "?");
									sqlQueryObject.addInsertField("tipo", "?");
									sqlQueryObject.addInsertField("nome", "?");
									sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE, "?");
									sqlQueryObject.addInsertField(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA, "?");
									
									String updateQuery = sqlQueryObject.createSQLInsert();
									updateStmt = con.prepareStatement(updateQuery);
									int index = 1;
									updateStmt.setLong(index++, idtrasformazioneRisposta);
									updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getConversioneTipo()));
									updateStmt.setString(index++, parametro.getNome());
									updateStmt.setString(index++, parametro.getValore());
									updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(parametro.getIdentificazioneFallita()));
									updateStmt.executeUpdate();
									updateStmt.close();
								}
							}
						}
						
					}
					
				}
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del configurazione
				Trasformazioni trasformazioniOld = readTrasformazioni(idProprietario, portaDelegata, con);
				if(trasformazioniOld!=null) {
					CRUDTrasformazioni(DELETE, con, trasformazioniOld, idProprietario, portaDelegata);
				}
				
				// Creo la nuova immagine
				if(trasformazioni!=null) {
					
					normalizePositionsEngine(trasformazioni);
					
					CRUDTrasformazioni(CREATE, con, trasformazioni, idProprietario, portaDelegata);
				}
				break;
				
			case DELETE:
				
				if(trasformazioni==null) {
					break;
				}
				
				String tableName = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
				String tableNameSoggetti = portaDelegata ? null : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
				String tableNameSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
				String tableNameHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
				String tableNameUrl = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
				String tableNameRisposte = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
				String tableNameRisposteHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
				List<Long> idTrasformazioneList = new ArrayList<>();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(tableName);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_REF+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				String updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idProprietario);
				rs = updateStmt.executeQuery();
				while(rs.next()) {
					idTrasformazioneList.add(rs.getLong("id"));
				}
				rs.close();
				updateStmt.close();
				
				if(idTrasformazioneList.isEmpty()) {
					break;
				}
				
				for (Long idTrasformazione : idTrasformazioneList) {
					
					if(tableNameSoggetti!=null) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addDeleteTable(tableNameSoggetti);
						sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt = con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idTrasformazione);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameSA);
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameHeader);
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameUrl);
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					List<Long> idTrasformazioneRisposteList = new ArrayList<>();
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addFromTable(tableNameRisposte);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLQuery();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					rs = updateStmt.executeQuery();
					while(rs.next()) {
						idTrasformazioneRisposteList.add(rs.getLong("id"));
					}
					rs.close();
					updateStmt.close();
					
					if(!idTrasformazioneRisposteList.isEmpty()) {
						
						for (Long idTrasformazioneRisposta : idTrasformazioneRisposteList) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
							sqlQueryObject.addDeleteTable(tableNameRisposteHeader);
							sqlQueryObject.addWhereCondition("id_transform_risp=?");
							sqlQueryObject.setANDLogicOperator(true);
							updateQuery = sqlQueryObject.createSQLDelete();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTrasformazioneRisposta);
							updateStmt.executeUpdate();
							updateStmt.close();
							
						}
						
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameRisposte);
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(tableName);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_REF+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idProprietario);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
				
			default:
				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			JDBCUtilities.closeResources(rs, updateStmt);
			
		}
	}
	
	public static Trasformazioni readTrasformazioni(long idPorta, boolean portaDelegata, Connection con) throws Exception {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		Trasformazioni trasformazioni = null;
		try {
			
			String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_REF+"=?");
			sqlQueryObject.addOrderBy(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPorta);
	
			DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idPorta));
			rs = stm.executeQuery();
	
			while (rs.next()) {
				
				if(trasformazioni==null) {
					trasformazioni = new Trasformazioni();
				}
				
				TrasformazioneRegola regola = new TrasformazioneRegola();
				
				String nome = rs.getString("nome");
				regola.setNome(nome);
				
				int posizione = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE);
				regola.setPosizione(posizione);
				
				StatoFunzionalita stato = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs.getString("stato"));
				regola.setStato(stato);
				
				String applicabilitaAzioni = rs.getString("applicabilita_azioni");
				String applicabilitaCT = rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_CT);
				String applicabilitaPattern = rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_PATTERN);
				String applicabilitaConnettori = rs.getString("applicabilita_connettori");
				if( (applicabilitaAzioni!=null && !"".equals(applicabilitaAzioni)) ||
						(applicabilitaCT!=null && !"".equals(applicabilitaCT)) ||
						(applicabilitaPattern!=null && !"".equals(applicabilitaPattern))  ||
						(applicabilitaConnettori!=null && !"".equals(applicabilitaConnettori)) 
						) {
					TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
					
					if( (applicabilitaAzioni!=null && !"".equals(applicabilitaAzioni)) ) {
						if(applicabilitaAzioni.contains(",")) {
							String [] tmp = applicabilitaAzioni.split(",");
							for (int i = 0; i < tmp.length; i++) {
								applicabilita.addAzione(tmp[i].trim());
							}
						}
						else {
							applicabilita.addAzione(applicabilitaAzioni);
						}
					}
					
					if( (applicabilitaCT!=null && !"".equals(applicabilitaCT)) ) {
						if(applicabilitaCT.contains(",")) {
							String [] tmp = applicabilitaCT.split(",");
							for (int i = 0; i < tmp.length; i++) {
								applicabilita.addContentType(tmp[i].trim());
							}
						}
						else {
							applicabilita.addContentType(applicabilitaCT);
						}
					}
					
					if(applicabilitaPattern!=null && !"".equals(applicabilitaPattern)){
						applicabilita.setPattern(applicabilitaPattern);
					}
					
					if( (applicabilitaConnettori!=null && !"".equals(applicabilitaConnettori)) ) {
						if(applicabilitaConnettori.contains(",")) {
							String [] tmp = applicabilitaConnettori.split(",");
							for (int i = 0; i < tmp.length; i++) {
								applicabilita.addConnettore(tmp[i].trim());
							}
						}
						else {
							applicabilita.addConnettore(applicabilitaConnettori);
						}
					}
					
					regola.setApplicabilita(applicabilita);
				}
				
				TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
				
				int reqConversioneEnabled = rs.getInt("req_conversione_enabled");
				richiesta.setConversione(CostantiDB.TRUE == reqConversioneEnabled);

				richiesta.setConversioneTipo(rs.getString("req_conversione_tipo"));
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
				richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "req_conversione_template"));
				richiesta.setContentType(rs.getString("req_content_type"));
				
				int trasformazioneRestIntValue = rs.getInt("rest_transformation");
				if(CostantiDB.TRUE == trasformazioneRestIntValue) {
					TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
					trasformazioneRest.setMetodo(rs.getString("rest_method"));
					trasformazioneRest.setPath(rs.getString("rest_path"));
					richiesta.setTrasformazioneRest(trasformazioneRest);
				}
					
				int trasformazioneSoapIntValue = rs.getInt("soap_transformation");
				if(CostantiDB.TRUE == trasformazioneSoapIntValue) {
					TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
					
					trasformazioneSoap.setVersione(DriverConfigurazioneDBLib.getEnumVersioneSOAP(rs.getString("soap_version")));
					trasformazioneSoap.setSoapAction(rs.getString("soap_action"));
					
					int envelope = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE);
					trasformazioneSoap.setEnvelope(CostantiDB.TRUE == envelope);
					
					int envelopeAsAttach = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_AS_ATTACH);
					if(CostantiDB.TRUE == envelopeAsAttach) {
						trasformazioneSoap.setEnvelopeAsAttachment(true);
						
						trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TIPO));
						trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TEMPLATE));
					}
					else {
						trasformazioneSoap.setEnvelopeAsAttachment(false);
					}
					
					richiesta.setTrasformazioneSoap(trasformazioneSoap);
				}
				
				regola.setId(rs.getLong("id"));
				
				regola.setRichiesta(richiesta);
				
				trasformazioni.addRegola(regola);
				
			}
			rs.close();
			stm.close();
			
			if(trasformazioni==null) {
				return trasformazioni;
			}
			
			for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
				

				// ** SOGGETTI **
				
				if(!portaDelegata) {
					
					nomeTabella = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, regola.getId());
					rs = stm.executeQuery();
	
					while (rs.next()) {
						
						if(regola.getApplicabilita()==null) {
							regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
						}
						
						TrasformazioneRegolaApplicabilitaSoggetto soggetto = new TrasformazioneRegolaApplicabilitaSoggetto();
						soggetto.setTipo(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_TIPO_SOGGETTO));
						soggetto.setNome(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_NOME_SOGGETTO));
						
						regola.getApplicabilita().addSoggetto(soggetto);
					
					}
					rs.close();
					stm.close();
				}
				
				
				// ** APPLICATIVI **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
				rs = stm.executeQuery();

				// per ogni entry 
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA = rs.getLong("id_servizio_applicativo");

					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							servizioApplicativo.setTipoSoggettoProprietario(rs1.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO));
							servizioApplicativo.setNomeSoggettoProprietario(rs1.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO));
							if(regola.getApplicabilita()==null) {
								regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
							}
							regola.getApplicabilita().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				
				
				// ** HEADER **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
					TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
					parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
					parametro.setNome(rs.getString("nome"));
					parametro.setValore(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE));
					parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA)));
					parametro.setId(rs.getLong("id"));
					regola.getRichiesta().addHeader(parametro);
				}
				
				rs.close();
				stm.close();
				
				
				// ** URL **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
					TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
					parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
					parametro.setNome(rs.getString("nome"));
					parametro.setValore(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE));
					parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA)));
					parametro.setId(rs.getLong("id"));
					regola.getRichiesta().addParametroUrl(parametro);
				}
				
				rs.close();
				stm.close();
				
				
				
				// ** RISPOSTE **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRASFORMAZIONI_COLUMN_ID_RIF_TRASFORMAZIONE+"=?");
				sqlQueryObject.addOrderBy(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
				
					TrasformazioneRegolaRisposta risposta = new TrasformazioneRegolaRisposta();
					
					String nome = rs.getString("nome");
					risposta.setNome(nome);
					
					int posizione = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_POSIZIONE);
					risposta.setPosizione(posizione);
					
					int applicabilitaStatusMin = rs.getInt("applicabilita_status_min");
					int applicabilitaStatusMax = rs.getInt("applicabilita_status_max");
					String applicabilitaCT = rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_CT);
					String applicabilitaPattern = rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_APPLICABILITA_PATTERN);
					if( (applicabilitaStatusMin >0 || applicabilitaStatusMax>0) ||
							(applicabilitaCT!=null && !"".equals(applicabilitaCT)) ||
							(applicabilitaPattern!=null && !"".equals(applicabilitaPattern)) 
							) {
						TrasformazioneRegolaApplicabilitaRisposta applicabilita = new TrasformazioneRegolaApplicabilitaRisposta();
						
						if(applicabilitaStatusMin>0) {
							applicabilita.setReturnCodeMin(applicabilitaStatusMin);
						}
						if(applicabilitaStatusMax>0) {
							applicabilita.setReturnCodeMax(applicabilitaStatusMax);
						}

						if( (applicabilitaCT!=null && !"".equals(applicabilitaCT)) ) {
							if(applicabilitaCT.contains(",")) {
								String [] tmp = applicabilitaCT.split(",");
								for (int i = 0; i < tmp.length; i++) {
									applicabilita.addContentType(tmp[i].trim());
								}
							}
							else {
								applicabilita.addContentType(applicabilitaCT);
							}
						}
						
						if(applicabilitaPattern!=null && !"".equals(applicabilitaPattern)){
							applicabilita.setPattern(applicabilitaPattern);
						}
						
						risposta.setApplicabilita(applicabilita);
					}
					
					int conversioneEnabled = rs.getInt("conversione_enabled");
					risposta.setConversione(CostantiDB.TRUE == conversioneEnabled);

					risposta.setConversioneTipo(rs.getString("conversione_tipo"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
					risposta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "conversione_template"));
					risposta.setContentType(rs.getString("content_type"));
					risposta.setReturnCode(rs.getString("return_code"));
					
					if(regola.getRichiesta().getTrasformazioneRest()!=null) { // La risposta deve essere ritrasformata in soap
					
						TrasformazioneSoapRisposta trasformazioneSoap = new TrasformazioneSoapRisposta();
						
						int envelope = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE);
						trasformazioneSoap.setEnvelope(CostantiDB.TRUE == envelope);
						
						int envelopeAsAttach = rs.getInt(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_AS_ATTACH);
						if(CostantiDB.TRUE == envelopeAsAttach) {
							trasformazioneSoap.setEnvelopeAsAttachment(true);
							
							trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TIPO));
							trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, CostantiDB.TRASFORMAZIONI_COLUMN_SOAP_ENVELOPE_TEMPLATE));
						}
						else {
							trasformazioneSoap.setEnvelopeAsAttachment(false);
						}
						
						risposta.setTrasformazioneSoap(trasformazioneSoap);
						
					}

					risposta.setId(rs.getLong("id"));
					
					regola.addRisposta(risposta);
					
				}
				
				rs.close();
				stm.close();
				
				
				// ** HEADER RISPOSTA **
				
				for (TrasformazioneRegolaRisposta risposta : regola.getRispostaList()) {
					
					nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_transform_risp=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, risposta.getId());
			
					DriverConfigurazioneDBLib.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idPorta));
					rs = stm.executeQuery();
			
					while (rs.next()) {
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDBLib.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_VALORE));
						parametro.setIdentificazioneFallita(DriverConfigurazioneDBLib.getEnumTrasformazioneIdentificazioneRisorsaFallita(rs.getString(CostantiDB.TRASFORMAZIONI_COLUMN_IDENTIFICAZIONE_FALLITA)));
						parametro.setId(rs.getLong("id"));
						risposta.addHeader(parametro);
					}
					
					rs.close();
					stm.close();
					
				}
			}
			
		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs, stm);
		}
		
		return trasformazioni;
	}
	
	
}
