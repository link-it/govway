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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiParteSpecificaDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiParteSpecificaDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	protected DriverRegistroServiziDB_accordiParteSpecificaDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(driver);
	}
	
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio,false,null);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio,false,con);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio, readContenutoAllegati, null);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// questo e' il tipo 1 cioe Servizio con parametro IDService
		AccordoServizioParteSpecifica servizio = getAccordoServizioParteSpecifica(idServizio,null, null,readContenutoAllegati,con);
		return servizio;
	}

	
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune,false,null);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune,false,con);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune, readContenutoAllegati, null);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// questo e' il tipo 2 
		AccordoServizioParteSpecifica servizio = getAccordoServizioParteSpecifica(null,idSoggetto, idAccordoServizioParteComune,readContenutoAllegati,con);
		return servizio;
	}

	protected List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		List<IDServizio> list = new ArrayList<>();
		_fillAllIdServiziEngine("getAllIdServizi", filtroRicerca, list);
		return list;
		
	}
	
	protected List<IDFruizione> getAllIdFruizioniServizio(
			FiltroRicercaFruizioniServizio filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
	
		List<IDFruizione> list = new ArrayList<IDFruizione>();
		_fillAllIdServiziEngine("getAllIdFruizioniServizio", filtroRicerca, list);
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	protected <T> void _fillAllIdServiziEngine(String nomeMetodo, 
			FiltroRicercaServizi filtroRicerca,
			List<T> listReturn) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		FiltroRicercaFruizioniServizio filtroFruizioni = null;
		if(filtroRicerca instanceof FiltroRicercaFruizioniServizio){
			filtroFruizioni = (FiltroRicercaFruizioniServizio) filtroRicerca;
		}
		
		this.driver.logDebug(nomeMetodo+" ...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);

			String aliasFruitore = "fruitore";
			String aliasErogatore = "erogatore";
			
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI,aliasErogatore);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			
			boolean setFruitore = false;
			if(filtroFruizioni!=null){
				setFruitore = true;
			}
			if(!setFruitore && filtroRicerca!=null){
				if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
					setFruitore = true;
				}
			}
			if(setFruitore){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI,aliasFruitore);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			}

			String aliasTipoSoggettoErogatore = "tipo_soggetto_erogatore";
			String aliasNomeSoggettoErogatore = "nome_soggetto_erogatore";
			sqlQueryObject.addSelectAliasField(aliasErogatore, "tipo_soggetto", aliasTipoSoggettoErogatore);
			sqlQueryObject.addSelectAliasField(aliasErogatore, "nome_soggetto", aliasNomeSoggettoErogatore);
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addSelectField("servizio_correlato");
			String aliasTipoSoggettoFruitore = "tipo_soggetto_fruitore";
			String aliasNomeSoggettoFruitore = "nome_soggetto_fruitore";
			if(setFruitore){
				sqlQueryObject.addSelectAliasField(aliasFruitore, "tipo_soggetto", aliasTipoSoggettoFruitore);
				sqlQueryObject.addSelectAliasField(aliasFruitore, "nome_soggetto", aliasNomeSoggettoFruitore);
			}
			sqlQueryObject.setSelectDistinct(true);
			
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+aliasErogatore+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = "+CostantiDB.ACCORDI+".id");

			if(setFruitore){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = "+CostantiDB.SERVIZI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+aliasFruitore+".id");
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione < ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio = ?");
				if(filtroRicerca.getVersione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".versione_servizio = ?");
				if(filtroRicerca.getPortType()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type = ?");
				if(filtroRicerca.getTipologia()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".servizio_correlato = ?");
				if(filtroRicerca.getTipoSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(aliasErogatore+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(aliasErogatore+".nome_soggetto = ?");
				if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordoServizioParteComune();
					if(idAccordo.getNome()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					}
					if(idAccordo.getSoggettoReferente()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente = ?");
					}
					if(idAccordo.getVersione()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					}
				}
				if(filtroRicerca.getTipoSoggettoFruitore()!=null)
					sqlQueryObject.addWhereCondition(aliasFruitore+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggettoFruitore()!=null)
					sqlQueryObject.addWhereCondition(aliasFruitore+".nome_soggetto = ?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca, CostantiDB.SERVIZI);
			}
			
			if(filtroFruizioni!=null){
				if(filtroFruizioni.getTipoSoggettoFruitore()!=null)
					sqlQueryObject.addWhereCondition(aliasFruitore+".tipo_soggetto = ?");
				if(filtroFruizioni.getNomeSoggettoFruitore()!=null)
					sqlQueryObject.addWhereCondition(aliasFruitore+".nome_soggetto = ?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroFruizioni, CostantiDB.SERVIZI_FRUITORI);
			}

			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.logDebug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.logDebug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.driver.logDebug("tipoServizio stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nomeServizio stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getVersione()!=null){
					this.driver.logDebug("versioneServizio stmt.setString("+filtroRicerca.getVersione()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersione());
					indexStmt++;
				}
				if(filtroRicerca.getPortType()!=null){
					this.driver.logDebug("portType stmt.setString("+filtroRicerca.getPortType()+")");
					stm.setString(indexStmt, filtroRicerca.getPortType());
					indexStmt++;
				}
				if(filtroRicerca.getTipologia()!=null){
					StatoFunzionalita servizioCorrelato = (org.openspcoop2.core.constants.TipologiaServizio.CORRELATO.equals(filtroRicerca.getTipologia()) ? CostantiRegistroServizi.ABILITATO : CostantiRegistroServizi.DISABILITATO);
					this.driver.logDebug("tipologiaServizio stmt.setString("+servizioCorrelato.getValue()+") original:["+filtroRicerca.getTipologia()+"]");
					stm.setString(indexStmt, servizioCorrelato.getValue());
					indexStmt++;
				}
				if(filtroRicerca.getTipologia()!=null){
					if(org.openspcoop2.core.constants.TipologiaServizio.CORRELATO.equals(filtroRicerca.getTipologia())){
						this.driver.logDebug("tipologiaServizio stmt.setString("+CostantiRegistroServizi.ABILITATO.toString()+")");
						stm.setString(indexStmt, CostantiRegistroServizi.ABILITATO.toString());
					}
					else{
						this.driver.logDebug("tipologiaServizio stmt.setString("+CostantiRegistroServizi.DISABILITATO.toString()+")");
						stm.setString(indexStmt, CostantiRegistroServizi.DISABILITATO.toString());
					}
					indexStmt++;
				}
				if(filtroRicerca.getTipoSoggettoErogatore()!=null){
					this.driver.logDebug("tipoSoggettoErogatore stmt.setString("+filtroRicerca.getTipoSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoErogatore()!=null){
					this.driver.logDebug("nomeSoggettoErogatore stmt.setString("+filtroRicerca.getNomeSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordoServizioParteComune();
					if(idAccordo.getNome()!=null){
						this.driver.logDebug("nomeAccordo stmt.setString("+idAccordo.getNome()+")");
						stm.setString(indexStmt, idAccordo.getNome());
						indexStmt++;
					}
					if(idAccordo.getSoggettoReferente()!=null){
						long idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, this.driver.tipoDB,this.driver.tabellaSoggetti);
						if(idSoggettoReferente<=0){
							throw new Exception("Soggetto referente ["+idAccordo.getSoggettoReferente().toString()+"] non trovato");
						}
						this.driver.logDebug("idReferenteAccordi stmt.setLong("+idSoggettoReferente+")");
						stm.setLong(indexStmt, idSoggettoReferente);
						indexStmt++;
					}
					if(idAccordo.getVersione()!=null){
						this.driver.logDebug("versioneAccordo stmt.setString("+idAccordo.getVersione()+")");
						stm.setInt(indexStmt, idAccordo.getVersione());
						indexStmt++;
					}
				}
				if(filtroRicerca.getTipoSoggettoFruitore()!=null){
					this.driver.logDebug("tipoSoggettoFruitore stmt.setString("+filtroRicerca.getTipoSoggettoFruitore()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoFruitore());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoFruitore()!=null){
					this.driver.logDebug("nomeSoggettoFruitore stmt.setString("+filtroRicerca.getNomeSoggettoFruitore()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoFruitore());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroRicerca, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			}
			
			if(filtroFruizioni!=null){
				if(filtroFruizioni.getTipoSoggettoFruitore()!=null){
					this.driver.logDebug("tipoSoggettoFruitore stmt.setString("+filtroFruizioni.getTipoSoggettoFruitore()+")");
					stm.setString(indexStmt, filtroFruizioni.getTipoSoggettoFruitore());
					indexStmt++;
				}
				if(filtroFruizioni.getNomeSoggettoFruitore()!=null){
					this.driver.logDebug("nomeSoggettoFruitore stmt.setString("+filtroFruizioni.getNomeSoggettoFruitore()+")");
					stm.setString(indexStmt, filtroFruizioni.getNomeSoggettoFruitore());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroFruizioni, ProprietariProtocolProperty.FRUITORE);
			}
			
			rs = stm.executeQuery();
			while (rs.next()) {
				IDSoggetto idSoggettoErogatore = new IDSoggetto(rs.getString(aliasTipoSoggettoErogatore),rs.getString(aliasNomeSoggettoErogatore));
				IDServizio idServ = this.driver.idServizioFactory.getIDServizioFromValues(rs.getString("tipo_servizio"),rs.getString("nome_servizio"), 
						idSoggettoErogatore, rs.getInt("versione_servizio"));

				// uriAccordoServizio
				IDSoggetto soggettoReferente = null;
				long idSoggettoReferente = rs.getLong("id_referente");
				if(idSoggettoReferente>0){
					try {
						soggettoReferente = this.driver.getIdSoggetto(idSoggettoReferente,con);
						if(soggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						try {
							throw new Exception ("Soggetto referente ["+idSoggettoReferente+"] dell'accordo non esiste");
						}finally {
							try{
								if(rs!=null) 
									rs.close();
							}catch (Exception e) {
								//ignore
							}
							try{
								if(stm!=null) 
									stm.close();
							}catch (Exception e) {
								//ignore
							}
						}
					}
				}
				IDAccordo idAccordo = this.driver.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),soggettoReferente,rs.getInt("versione"));
				idServ.setUriAccordoServizioParteComune(this.driver.idAccordoFactory.getUriFromIDAccordo(idAccordo));

				String servizioCorrelato = rs.getString("servizio_correlato");
				if(CostantiRegistroServizi.ABILITATO.toString().equals(servizioCorrelato) || TipologiaServizio.CORRELATO.toString().equals(servizioCorrelato))
					idServ.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.CORRELATO);
				else
					idServ.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.NORMALE);
				
				if(filtroFruizioni!=null){
					IDFruizione idFruizione = new IDFruizione();
					idFruizione.setIdServizio(idServ);
					idFruizione.setIdFruitore(new IDSoggetto(rs.getString("tipo_soggetto_fruitore"),rs.getString("nome_soggetto_fruitore")));
					listReturn.add((T)idFruizione);
				}
				else{
					listReturn.add((T)idServ);
				}
			}
			if(listReturn.isEmpty()){
				String msgFiltro = "Elementi non trovati che rispettano il filtro di ricerca selezionato: ";
				if(filtroFruizioni!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroFruizioni.toString());
				}
				else if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroRicerca.toString());
				}
				else
					throw new DriverRegistroServiziNotFound("Elementi non trovati");
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(nomeMetodo+" error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}

	protected IDServizio[] getAllIdServiziWithSoggettoErogatore(Long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String operazione = "getAllIdServiziWithSoggettoErogatore";

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug(operazione+"...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdServiziWithSoggettoErogatore");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);

			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = ?");

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();
			List<IDServizio> idServizi = new ArrayList<>();
			while (rs.next()) {
				IDSoggetto idSoggettoErogatore = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDServizio idServ = this.driver.idServizioFactory.getIDServizioFromValues(rs.getString("tipo_servizio"),rs.getString("nome_servizio"), 
						idSoggettoErogatore, rs.getInt("versione_servizio"));
				idServizi.add(idServ);
			}
			if(idServizi.isEmpty()){
				throw new DriverRegistroServiziNotFound("Servizi non trovati per il soggetto con id: "+idSoggetto);
			}else{
				IDServizio[] res = new IDServizio[1];
				return idServizi.toArray(res);
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(operazione+" error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}

	protected void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		if (accordoServizioParteSpecifica == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDServizio tupe=1");
			// CREATE
			DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecifica(1, accordoServizioParteSpecifica, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Errore durante la creazione del servizio : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException {

		if (idServizio == null)
			throw new DriverRegistroServiziException("IDServizio non valido.");

		IDSoggetto erogatore = idServizio.getSoggettoErogatore();
		if (erogatore == null)
			throw new DriverRegistroServiziException("Soggetto Erogatore non valido.");

		String nomeServizio = idServizio.getNome();
		String tipoServizio = idServizio.getTipo();
		Integer versioneServizio = idServizio.getVersione();
		String nomeProprietario = erogatore.getNome();
		String tipoProprietario = erogatore.getTipo();

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			long idS = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietario, tipoProprietario, con, false,this.driver.tipoDB);
			return idS > 0;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existsAccordoServizioParteSpecifica : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsAccordoServizioParteSpecifica(longId)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza servizio :", e);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);
		}

		return exist;
	}


	protected long getServizioWithSoggettoAccordoServCorr(long idSoggetto, long idAccordo, String servizioCorrelato) throws DriverRegistroServiziException {
		return getEngineServizioWithSoggettoAccordoServCorrPT(idSoggetto, idAccordo, servizioCorrelato, null);
	}

	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto e port-type
	 * accordo e servizio correlato
	 */
	private long getEngineServizioWithSoggettoAccordoServCorrPT(long idSoggetto, long idAccordo, String servizioCorrelato,String portType) throws DriverRegistroServiziException {

		long idServ = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("getEngineServizioWithSoggettoAccordoServCorrPT");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioWithSoggettoAccordoServCorr] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("servizio_correlato = ?");

			if(portType!=null) sqlQueryObject.addWhereCondition("port_type = ?");
			else sqlQueryObject.addWhereCondition("port_type is null");

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setLong(2, idAccordo);
			stm.setString(3, servizioCorrelato);
			if(portType!=null) stm.setString(4, portType);
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getLong("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(connection);
		}

		return idServ;
	}

	protected long getServizioWithSoggettoAccordoServCorrPt(long idSoggetto, long idAccordo, String servizioCorrelato,String portType) throws DriverRegistroServiziException {
		return getEngineServizioWithSoggettoAccordoServCorrPT(idSoggetto, idAccordo, servizioCorrelato, portType);
	}

	

	protected void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		if (servizio == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Parametro non valido.");
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			// UPDATE
			DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecifica(2, servizio, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Errore durante l'update del servizio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		if (servizio == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDServizio type = 3");
			// creo soggetto
			DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecifica(3, servizio, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Errore durante la delete del servizio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsSoggettoServiziWithoutConnettore(long idSoggetto) throws DriverRegistroServiziException {
		if (idSoggetto <= 0)
			throw new DriverRegistroServiziException("idSoggetto non valido.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		boolean trovatoServ = false;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsSoggettoServiziWithoutConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_connettore = " + CostantiDB.CONNETTORI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("endpointtype = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.DISABILITATO));
			rs = stm.executeQuery();
			if (rs.next())
				trovatoServ = true;

			return trovatoServ;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existsSoggettoServiziWithoutConnettore: " + qe.getMessage(), qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	private AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio, 
			IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune,
			boolean readContenutoAllegati,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		AccordoServizioParteSpecifica accordoServizioParteSpecifica = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;

		if(idServizio!=null){
			this.driver.logDebug("chiamato getAccordoServizioParteSpecifica (IDServizio)");
			// in questi casi idService non deve essere null
			// faccio i controlli vari
			if (idServizio == null || idServizio.getNome() == null || idServizio.getNome().trim().equals("") || 
				idServizio.getTipo() == null || idServizio.getTipo().trim().equals("") || 
				idServizio.getVersione() == null ||
				idServizio.getSoggettoErogatore() == null || idServizio.getSoggettoErogatore().getNome() == null || idServizio.getSoggettoErogatore().getNome().trim().equals("") || idServizio.getSoggettoErogatore().getTipo() == null || idServizio.getSoggettoErogatore().getTipo().trim().equals(""))
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] : errore nei parametri d'ingresso (IDServizio)");
		}
		else{
			this.driver.logDebug("chiamato getAccordoServizioParteSpecifica (IDSoggetto e IDAccordo)");
			// in questo caso idSoggetto non deve essere null e anche
			// nomeAccordo
			if (idAccordoServizioParteComune == null || idAccordoServizioParteComune.getNome()==null || idAccordoServizioParteComune.getNome().trim().equals("") || idSoggetto == null || idSoggetto.getNome() == null || idSoggetto.getNome().trim().equals("") || idSoggetto.getTipo() == null || idSoggetto.getTipo().trim().equals(""))
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] : errore nei parametri d'ingresso (IDSoggetto e IDAccordo)");
		
		}

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteSpecifica");
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			accordoServizioParteSpecifica = new AccordoServizioParteSpecifica();


			String nomeSoggEr = null;
			String tipoSoggEr = null;
			long longIdAccordoServizioParteComune = 0;
			String nomeServizio = null;
			String tipoServizio = null;
			Integer versioneServizio = null;
			String superUser = null;
			long idSoggErogatore = 0;
			long longIdAccordoServizioParteSpecifica = 0;


			// se tipo 1 utilizzo idServio per recuperare qualche parametro
			if(idServizio!=null){
				nomeServizio = idServizio.getNome();
				tipoServizio = idServizio.getTipo();
				versioneServizio = idServizio.getVersione();

				// nome e tipo soggetto erogatore
				nomeSoggEr = idServizio.getSoggettoErogatore().getNome();
				tipoSoggEr = idServizio.getSoggettoErogatore().getTipo();

				// Prendo l'id del soggetto

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nomeSoggEr);
				stm.setString(2, tipoSoggEr);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSoggEr, tipoSoggEr));

				rs = stm.executeQuery();
				if (rs.next()) {
					idSoggErogatore = rs.getLong("id");
				}
				rs.close();
				stm.close();
			}

			else{

				// In questo caso non ho IDService ma IDSoggetto
				// quindi prendo il nome del soggetto erogatore e il tipo da
				// idSoggetto
				nomeSoggEr = idSoggetto.getNome();
				tipoSoggEr = idSoggetto.getTipo();

				// per settare il nome del servizio devo accedere al db
				// tramite id del soggetto erogatore e id dell-accordo (che lo
				// recupero tramite il nomeAccordo passato come parametro)
				longIdAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idAccordoServizioParteComune, con, this.driver.tipoDB);

				// Prendo l'id del soggetto
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nomeSoggEr);
				stm.setString(2, tipoSoggEr);

				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSoggEr, tipoSoggEr));
				rs = stm.executeQuery();
				if (rs.next()) {
					idSoggErogatore = rs.getLong("id");
				}
				rs.close();
				stm.close();

				// ora che ho l'id recupero nome-servizio e tipo-servizio dalla
				// tabella
				// regserv_servizi acceduta tramite id-soggetto e id-accordo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition("servizio_correlato = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idSoggErogatore);
				stm.setLong(2, longIdAccordoServizioParteComune);
				stm.setString(3, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.ABILITATO));
				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggErogatore, longIdAccordoServizioParteComune, CostantiRegistroServizi.ABILITATO));
				rs = stm.executeQuery();
				if (rs.next()) {
					nomeServizio = rs.getString("nome_servizio");
					tipoServizio = rs.getString("tipo_servizio");
					versioneServizio = rs.getInt("versione_servizio");
					superUser = rs.getString("superuser");

				}
				rs.close();
				stm.close();
			}

			// Prendo l'id del servizio
			long idConnettore = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nomeServizio);
			stm.setString(2, tipoServizio);
			if(versioneServizio!=null) {
				stm.setInt(3, versioneServizio);
			}
			else {
				stm.setInt(3, 1); // default
			}
			stm.setLong(4, idSoggErogatore);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeServizio, tipoServizio, versioneServizio, idSoggErogatore));
			rs = stm.executeQuery();
			if (rs.next()) {
				longIdAccordoServizioParteSpecifica = rs.getLong("id");
				idConnettore = rs.getLong("id_connettore");
				longIdAccordoServizioParteComune = rs.getLong("id_accordo");
				superUser = rs.getString("superuser");
				accordoServizioParteSpecifica.setId(longIdAccordoServizioParteSpecifica);
				accordoServizioParteSpecifica.setIdAccordo(longIdAccordoServizioParteComune);

				// setNome servizio
				accordoServizioParteSpecifica.setNome(rs.getString("nome_servizio"));
				// setTipo servizio
				accordoServizioParteSpecifica.setTipo(rs.getString("tipo_servizio"));
				// versione
				accordoServizioParteSpecifica.setVersione(rs.getInt("versione_servizio"));

				accordoServizioParteSpecifica.setConfigurazioneServizio(new ConfigurazioneServizio());
				
				//setto connettore
				accordoServizioParteSpecifica.getConfigurazioneServizio().setConnettore(this.driver.getConnettore(idConnettore, con));
				// setWsdlImplementativoErogatore
				String wsdlimpler = rs.getString("wsdl_implementativo_erogatore");
				accordoServizioParteSpecifica.setByteWsdlImplementativoErogatore((wsdlimpler != null && !wsdlimpler.trim().equals("")) ? wsdlimpler.trim().getBytes() : null);
				// setWddlImplementativoFruitore
				String wsdlimplfru = rs.getString("wsdl_implementativo_fruitore");
				accordoServizioParteSpecifica.setByteWsdlImplementativoFruitore((wsdlimplfru != null && !wsdlimplfru.trim().equals("")) ? wsdlimplfru.trim().getBytes() : null);

				// Setto informazione sul servizio correlato
				String servizioCorrelato = rs.getString("servizio_correlato");
				if(CostantiRegistroServizi.ABILITATO.toString().equals(servizioCorrelato) || TipologiaServizio.CORRELATO.toString().equals(servizioCorrelato))
					accordoServizioParteSpecifica.setTipologiaServizio(TipologiaServizio.CORRELATO);
				else
					accordoServizioParteSpecifica.setTipologiaServizio(TipologiaServizio.NORMALE);

				//setto erogatore servizio
				accordoServizioParteSpecifica.setTipoSoggettoErogatore(tipoSoggEr);
				accordoServizioParteSpecifica.setNomeSoggettoErogatore(nomeSoggEr);
				accordoServizioParteSpecifica.setIdSoggetto(idSoggErogatore);

				if(rs.getInt("privato")==CostantiDB.TRUE)
					accordoServizioParteSpecifica.setPrivato(true);
				else
					accordoServizioParteSpecifica.setPrivato(false);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoServizioParteSpecifica.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// porttype
				String tmp = rs.getString("port_type");
				if(tmp!=null && (!"".equals(tmp)))
					accordoServizioParteSpecifica.setPortType(tmp);

				// Profilo 
				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					accordoServizioParteSpecifica.setVersioneProtocollo(profilo);
				}

				accordoServizioParteSpecifica.setSuperUser(superUser);

				// Descrizione
				accordoServizioParteSpecifica.setDescrizione(rs.getString("descrizione"));

				// Stato Documento
				accordoServizioParteSpecifica.setStatoPackage(rs.getString("stato"));

				// MessageType
				tmp = rs.getString("message_type");
				accordoServizioParteSpecifica.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				// Proprieta Oggetto
				accordoServizioParteSpecifica.setProprietaOggetto(DriverRegistroServiziDB_utilsDriver.readProprietaOggetto(rs,false));
				
			}else{
				throw new DriverRegistroServiziNotFound("Servizio ["+tipoServizio+"/"+nomeServizio+":"+versioneServizio+"] erogato dal soggetto ["+tipoSoggEr+"/"+nomeSoggEr+"] non esiste");
			}
			rs.close();
			stm.close();
			long idSoggFruitore = 0;
			Fruitore fruitore = null;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteSpecifica);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteSpecifica));
			rs = stm.executeQuery();

			while (rs.next()) {
				fruitore = new Fruitore();

				idSoggFruitore = rs.getLong("id_soggetto"); // recupero id del
				// soggetto fruitore
				// del servizio
				idConnettore = rs.getLong("id_connettore"); // recuper id del
				// connettore

				fruitore.setConnettore(this.driver.getConnettore(idConnettore, con));

				String wsdlimpler = rs.getString("wsdl_implementativo_erogatore");
				fruitore.setByteWsdlImplementativoErogatore(wsdlimpler!=null && !wsdlimpler.trim().equals("") ? wsdlimpler.getBytes() : null );
				String wsdlimplfru = rs.getString("wsdl_implementativo_fruitore");
				fruitore.setByteWsdlImplementativoFruitore(wsdlimplfru!=null && !wsdlimplfru.trim().equals("") ? wsdlimplfru.getBytes() : null);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					fruitore.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Stato Documento
				fruitore.setStatoPackage(rs.getString("stato"));

				// Descrizione
				fruitore.setDescrizione(rs.getString("descrizione"));
				
				// Proprieta Oggetto
				fruitore.setProprietaOggetto(DriverRegistroServiziDB_utilsDriver.readProprietaOggetto(rs,false));
				
				// recupero informazioni del soggetto fruitore
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idSoggFruitore);
				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggFruitore));
				rs1 = stm1.executeQuery();

				if (rs1.next()) {
					fruitore.setNome(rs1.getString("nome_soggetto"));
					fruitore.setTipo(rs1.getString("tipo_soggetto"));
				} else {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Non ho trovato i dati del soggetto fruitore necessario eseguendo: \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggFruitore));
				}
				rs1.close();
				stm1.close();

				// aggiungo il fruitore al servizio da restituire
				fruitore.setId(rs.getLong("id"));
				
				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(fruitore.getId(), ProprietariProtocolProperty.FRUITORE, con, this.driver.tipoDB);
					if(listPP!=null && !listPP.isEmpty()){
						for (ProtocolProperty protocolProperty : listPP) {
							fruitore.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				
				accordoServizioParteSpecifica.addFruitore(fruitore);

			}
			rs.close();
			stm.close();
			
			if(accordoServizioParteSpecifica.sizeFruitoreList()>0) {
				for (Fruitore fruitoreLetto : accordoServizioParteSpecifica.getFruitoreList()) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_fruizione = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, fruitoreLetto.getId());

					this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, fruitoreLetto.getId()));
					rs = stm.executeQuery();

					while (rs.next()) {
						ConfigurazioneServizioAzione conf = new ConfigurazioneServizioAzione();

						idConnettore = rs.getLong("id_connettore"); // recuper id del
						// connettore
						conf.setConnettore(this.driver.getConnettore(idConnettore, con));

						// aggiungo il fruitore al servizio da restituire
						conf.setId(rs.getLong("id"));
						fruitoreLetto.addConfigurazioneAzione(conf);

					}
					rs.close();
					stm.close();
					
					
					if(fruitoreLetto.sizeConfigurazioneAzioneList()>0) {
						for (ConfigurazioneServizioAzione conf : fruitoreLetto.getConfigurazioneAzioneList()) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
							sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
							sqlQueryObject.addSelectField("*");
							sqlQueryObject.addWhereCondition("id_fruizione_azioni = ?");
							sqlQuery = sqlQueryObject.createSQLQuery();
							stm = con.prepareStatement(sqlQuery);
							stm.setLong(1, conf.getId());

							this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, conf.getId()));
							rs = stm.executeQuery();

							while (rs.next()) {
								
								conf.addAzione(rs.getString("nome_azione"));

							}
							rs.close();
							stm.close();
							
						}
					}
				}
			}


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteSpecifica);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteSpecifica));
			rs = stm.executeQuery();

			while (rs.next()) {
				ConfigurazioneServizioAzione conf = new ConfigurazioneServizioAzione();

				idConnettore = rs.getLong("id_connettore"); // recuper id del
				// connettore
				conf.setConnettore(this.driver.getConnettore(idConnettore, con));

				// aggiungo il fruitore al servizio da restituire
				conf.setId(rs.getLong("id"));
				accordoServizioParteSpecifica.getConfigurazioneServizio().addConfigurazioneAzione(conf);

			}
			rs.close();
			stm.close();
			
			
			if(accordoServizioParteSpecifica.getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0) {
				for (ConfigurazioneServizioAzione conf : accordoServizioParteSpecifica.getConfigurazioneServizio().getConfigurazioneAzioneList()) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_servizio_azioni = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, conf.getId());

					this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, conf.getId()));
					rs = stm.executeQuery();

					while (rs.next()) {
						
						conf.addAzione(rs.getString("nome_azione"));

					}
					rs.close();
					stm.close();
					
				}
			}


			// imposto uri accordo di servizio parte comune
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteComune);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteComune));
			rs = stm.executeQuery();

			if (rs.next()) {
				// setAccordoServizio
				accordoServizioParteSpecifica.setIdAccordo(longIdAccordoServizioParteComune);

				String tmp = rs.getString("nome");
				int tmpVersione = rs.getInt("versione");
				long id_referente = rs.getLong("id_referente");
				IDSoggetto soggettoReferente = null;
				if(id_referente>0){
					try {
						soggettoReferente = this.driver.getIdSoggetto(id_referente,con);
						if(soggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						throw new Exception ("Soggetto referente ["+id_referente+"] dell'accordo non esiste");
					}
				}
				accordoServizioParteSpecifica.setAccordoServizioParteComune(this.driver.idAccordoFactory.getUriFromValues(tmp, soggettoReferente, tmpVersione));


				/** Non servono poiche' non presenti nel db dei servizi */
				/*
				// setFiltroduplicati
				tmp = rs.getString("filtro_duplicati");
				servizio.setFiltroDuplicati((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setConfermaRicezione
				tmp = rs.getString("conferma_ricezione");
				servizio.setConfermaRicezione((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setIdCollaborazione
				tmp = rs.getString("identificativo_collaborazione");
				servizio.setIdCollaborazione((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setIdRiferimentoRichiesta
				tmp = rs.getString("id_riferimento_richiesta");
				servizio.setIdRiferimentoRichiesta((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setConsegnaInOrdine
				tmp = rs.getString("consegna_in_ordine");
				servizio.setConsegnaInOrdine((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setScadenza
				tmp = rs.getString("scadenza");
				servizio.setScadenza((tmp != null && !(tmp.trim().equals(""))) ? tmp : null);
				 */
			}
			rs.close();
			stm.close();




			// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
			//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
			try{
				List<?> allegati = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.allegato.toString(), longIdAccordoServizioParteSpecifica, 
						ProprietariDocumento.servizio,readContenutoAllegati, con, this.driver.tipoDB);
				for(int i=0; i<allegati.size();i++){
					accordoServizioParteSpecifica.addAllegato((Documento) allegati.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheSemiformali = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.driver.tipoDB);
				for(int i=0; i<specificheSemiformali.size();i++){
					accordoServizioParteSpecifica.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheLivelloServizio = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaLivelloServizio.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.driver.tipoDB);
				for(int i=0; i<specificheLivelloServizio.size();i++){
					accordoServizioParteSpecifica.addSpecificaLivelloServizio((Documento) specificheLivelloServizio.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheSicurezza = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaSicurezza.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.driver.tipoDB);
				for(int i=0; i<specificheSicurezza.size();i++){
					accordoServizioParteSpecifica.addSpecificaSicurezza((Documento) specificheSicurezza.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}

			
			
			
			// Protocol Properties
			try{
				List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(longIdAccordoServizioParteSpecifica, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, this.driver.tipoDB);
				if(listPP!=null && listPP.size()>0){
					for (ProtocolProperty protocolProperty : listPP) {
						accordoServizioParteSpecifica.addProtocolProperty(protocolProperty);
					}
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}	
			
			
			return accordoServizioParteSpecifica;

		} catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			this.driver.logDebug("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception:"+se.getMessage(),se);
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception:" + se.getMessage(),se);

		} finally {
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(rs1, stm1);

			this.driver.closeConnection(conParam, con);
		}
	}
	
	
	
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio,null);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio,null,readContenutoAllegati);
	}

	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio, conParam,false);
	}
	protected AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		// conrollo consistenza
		if (idServizio <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] L'id del servizio deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getAccordoServizioParteSpecifica(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		IDServizio idServizioObject = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizio));

			rs = stm.executeQuery();

			if (rs.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				idServizioObject = this.driver.idServizioFactory.getIDServizioFromValues(rs.getString("tipo_servizio"),rs.getString("nome_servizio"), soggettoErogatore, rs.getInt("versione_servizio"));
				if(CostantiRegistroServizi.ABILITATO.toString().equals(rs.getString("servizio_correlato")) || 
						TipologiaServizio.CORRELATO.toString().equals(rs.getString("servizio_correlato"))){
					idServizioObject.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.CORRELATO);
				}
				else{
					idServizioObject.setTipologia(org.openspcoop2.core.constants.TipologiaServizio.NORMALE);
				}

			}else{
				throw new DriverRegistroServiziNotFound("Nessun Servizio trovato con id="+idServizio);
			}
			rs.close();
			stm.close();
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] SqlException: " + se.getMessage(),se);
		} catch (DriverRegistroServiziNotFound nf) {
			throw nf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}

		return this.getAccordoServizioParteSpecifica(idServizioObject,readContenutoAllegati,conParam);

	}
	
	
	protected List<AccordoServizioParteSpecifica> serviziWithIdAccordoList(long idAccordo) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziWithIdAccordoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> lista = new ArrayList<AccordoServizioParteSpecifica>();
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziWithIdAccordoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				AccordoServizioParteSpecifica serv = this.getAccordoServizioParteSpecifica(risultato.getLong("id"));
				lista.add(serv);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<IDSoggetto> getAllIdSoggettiErogatori(String tipoServizio,String nomeServizio,String uriAccordo,String nomePortType,String tipoSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "getAllIdSoggettiErogatori";
		ArrayList<IDSoggetto> lista = new ArrayList<IDSoggetto>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getAllIdSoggettiErogatori");

				} catch (Exception e) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

				}

			} else {
				con = this.driver.globalConnection;
			}

			this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);
			
			//recupero idAccordo
			IDAccordo idAccordo = this.driver.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.driver.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "tipo_soggetto", "tipoSoggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "nome_soggetto", "nomeSoggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio= ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio= ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type = ?");
			if(tipoSoggetto!=null) sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoServizio);
			stmt.setString(2, nomeServizio);
			stmt.setLong(3, idAccordoLong);
			stmt.setString(4, nomePortType);
			if(tipoSoggetto!=null) stmt.setString(5, tipoSoggetto);

			risultato = stmt.executeQuery();

			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipoSoggetto");
				String nome_soggetto = risultato.getString("nomeSoggetto");
				lista.add(new IDSoggetto(tipo_soggetto,nome_soggetto));
			}

			return lista;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	
	protected void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio, boolean gestioneWsdlImplementativo, boolean checkConnettore) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("Servizio",servizio.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(servizio.getPrivato()==null || servizio.getPrivato()==false){
				IDSoggetto idS = new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore());
				try{
					Soggetto s = this.driver.getSoggetto(idS);
					if(s.getPrivato()!=null && s.getPrivato()){
						erroreValidazione.addErroreValidazione("soggetto erogatore ["+idS+"] con visibilita' privata, in un servizio con visibilita' pubblica");
					}
				}catch(DriverRegistroServiziNotFound dNot){}
				try{
					AccordoServizioParteComune as = this.driver.getAccordoServizioParteComune(this.driver.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
					if(as.getPrivato()!=null && as.getPrivato()){
						erroreValidazione.addErroreValidazione("accordo di servizio ["+servizio.getAccordoServizioParteComune()+"] con visibilita' privata, in un servizio con visibilita' pubblica");
					}
				}catch(DriverRegistroServiziNotFound dNot){}
			}	

			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(servizio.getStatoPackage()) == false){

				if(StatiAccordo.operativo.toString().equals(servizio.getStatoPackage())){
					try{
						AccordoServizioParteComune as = this.driver.getAccordoServizioParteComune(this.driver.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
						if(StatiAccordo.finale.toString().equals(as.getStatoPackage())==false && StatiAccordo.operativo.toString().equals(as.getStatoPackage())==false){
							erroreValidazione.addErroreValidazione("accordo di servizio riferito ["+this.driver.idAccordoFactory.getUriFromAccordo(as)+"] possiede lo stato ["+as.getStatoPackage()+"]");
						}
					}catch(DriverRegistroServiziNotFound dNot){
						erroreValidazione.addErroreValidazione("accordo di servizio non definito");
					}
				}
				
				else if(StatiAccordo.finale.toString().equals(servizio.getStatoPackage())){
					AccordoServizioParteComune as = null;
					try{
						as = this.driver.getAccordoServizioParteComune(this.driver.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
						if(StatiAccordo.finale.toString().equals(as.getStatoPackage())==false){
							erroreValidazione.addErroreValidazione("accordo di servizio ["+this.driver.idAccordoFactory.getUriFromAccordo(as)+"] in uno stato non finale ["+as.getStatoPackage()+"]");
						}
					}catch(DriverRegistroServiziNotFound dNot){
						erroreValidazione.addErroreValidazione("accordo di servizio non definito");
					}

					if(as!=null) {
						if(ServiceBinding.SOAP.equals(as.getServiceBinding()) && gestioneWsdlImplementativo) {
						
							if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
								String wsdlImplementativoFruitore = (servizio.getByteWsdlImplementativoFruitore()!=null ? new String(servizio.getByteWsdlImplementativoFruitore()) : null);
								wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replaceAll("\n", "")) ? wsdlImplementativoFruitore : null;
								if(	wsdlImplementativoFruitore == null){
									erroreValidazione.addErroreValidazione("WSDL Implementativo fruitore non definito");
								}
							}else{
								String wsdlImplementativoErogatore = (servizio.getByteWsdlImplementativoErogatore()!=null ? new String(servizio.getByteWsdlImplementativoErogatore()) : null);
								wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replaceAll("\n", "")) ? wsdlImplementativoErogatore : null;
								if(	wsdlImplementativoErogatore == null){
									erroreValidazione.addErroreValidazione("WSDL Implementativo erogatore non definito");
								}
							}
							
						}
					}


					// check connettore: un servizio puo' essere finale con jms: il check sara' poi al momento dell'esportazione nei package cnipa
					/*if(servizio.getConnettore()!=null && !CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(servizio.getConnettore().getTipo()) && 
							!CostantiDB.CONNETTORE_TIPO_HTTP.equals(servizio.getConnettore().getTipo()) && !CostantiDB.CONNETTORE_TIPO_HTTPS.equals(servizio.getConnettore().getTipo())){
						erroreValidazione.addErroreValidazione("Accordo di servizio parte specifica possiede un connettore ("+servizio.getConnettore().getTipo()+") non utilizzabile nella rete SPC");
					}
					else */
					
					if(checkConnettore) {
						
						if(servizio.getConfigurazioneServizio().getConnettore()==null || CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(servizio.getConfigurazioneServizio().getConnettore().getTipo())){
							// check connettore del soggetto erogatore: un servizio puo' essere finale con jms: il check sara' poi al momento dell'esportazione nei package cnipa
							Soggetto soggettoErogatore = this.driver.getSoggetto(new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore()));
							/*if(soggettoErogatore.getConnettore()!=null && !CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(soggettoErogatore.getConnettore().getTipo()) && 
									!CostantiDB.CONNETTORE_TIPO_HTTP.equals(soggettoErogatore.getConnettore().getTipo()) && !CostantiDB.CONNETTORE_TIPO_HTTPS.equals(soggettoErogatore.getConnettore().getTipo())){
								erroreValidazione.addErroreValidazione("Accordo di servizio parte specifica non possiede un connettore e soggetto erogatore "+servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore()+" possiede un connettore ("+soggettoErogatore.getConnettore().getTipo()+") non utilizzabile nella rete SPC");
							}
							else */
							if(soggettoErogatore.getConnettore()==null || CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(soggettoErogatore.getConnettore().getTipo()) ){
								erroreValidazione.addErroreValidazione("Sia l'Accordo di servizio parte specifica che il soggetto erogatore non possiedono un connettore");
							}
						}
					}
				}

			}

		}catch(Exception e){
			throw new ValidazioneStatoPackageException(e);
		}

		if(erroreValidazione.sizeErroriValidazione()>0){
			throw erroreValidazione;
		}
	}

	
	
	
	
	
	protected void updateProprietaOggettoErogazione(IDServizio idServizioObject, String user) throws DriverRegistroServiziException {
		
		String nomeMetodo = "updateProprietaOggettoErogazione";
		Connection con = null;
		long idServizio = -1;
		try {
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource(nomeMetodo);

				} catch (Exception e) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

				}

			} else {
				con = this.driver.globalConnection;
			}

			this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);
			
			idServizio = DBUtils.getIdAccordoServizioParteSpecifica(idServizioObject, con, DriverRegistroServiziDB_LIB.tipoDB);

			if(idServizio<=0) {
				throw new DriverRegistroServiziException("Servizio con id '"+idServizioObject+"' non esistente");
			}
			
		} catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			this.driver.closeConnection(con);
		}
		updateProprietaOggettoEngine(idServizio, user, CostantiDB.SERVIZI);
	}
	protected void updateProprietaOggettoErogazione(long idServizio, String user) throws DriverRegistroServiziException {
		updateProprietaOggettoEngine(idServizio, user, CostantiDB.SERVIZI);
	}
	protected void updateProprietaOggettoFruizione(IDServizio idServizioObject, IDSoggetto idFruitore, String user) throws DriverRegistroServiziException {
		
		String nomeMetodo = "updateProprietaOggettoFruizione";
		Connection con = null;
		long idFruizione = -1;
		try {
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource(nomeMetodo);

				} catch (Exception e) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

				}

			} else {
				con = this.driver.globalConnection;
			}

			this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);
			
			idFruizione = DBUtils.getIdFruizioneServizio(idServizioObject, idFruitore, con, DriverRegistroServiziDB_LIB.tipoDB);

			if(idFruizione<=0) {
				throw new DriverRegistroServiziException("Servizio con id '"+idServizioObject+"' non esistente");
			}
			
		} catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			this.driver.closeConnection(con);
		}
		updateProprietaOggettoEngine(idFruizione, user, CostantiDB.SERVIZI_FRUITORI);
	}
	protected void updateProprietaOggettoFruizione(long idFruizione, String user) throws DriverRegistroServiziException {
		updateProprietaOggettoEngine(idFruizione, user, CostantiDB.SERVIZI_FRUITORI);
	}
	private void updateProprietaOggettoEngine(long id, String user, String tabella) throws DriverRegistroServiziException {
		
		String nomeMetodo = "updateProprietaOggetto_"+tabella;
		
		Connection con = null;
		PreparedStatement stm = null;
		try {
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource(nomeMetodo);

				} catch (Exception e) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

				}

			} else {
				con = this.driver.globalConnection;
			}

			this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);
			
			ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObjectUpdate.addUpdateTable(tabella);
			sqlQueryObjectUpdate.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, "?");
			sqlQueryObjectUpdate.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, "?");
			sqlQueryObjectUpdate.addWhereCondition("id=?");
			String updateString = sqlQueryObjectUpdate.createSQLUpdate();
			stm = con.prepareStatement(updateString);
			int index = 1;
			stm.setString(index++, user);
			stm.setTimestamp(index++, DateManager.getTimestamp());
			stm.setLong(index, id);
			int n=stm.executeUpdate();
			stm.close();
			DriverRegistroServiziDB_LIB.logDebug("Aggiornata "+n+" entry per l'operazione di ultima modifica della tabella '"+tabella+"' con id: "+id);
		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.logError("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(stm);

			this.driver.closeConnection(con);
		}
	}
}
