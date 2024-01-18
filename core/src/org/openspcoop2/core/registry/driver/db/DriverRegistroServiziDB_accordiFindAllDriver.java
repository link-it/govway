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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiFindAllDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiFindAllDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	protected DriverRegistroServiziDB_accordiFindAllDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(driver);
	}
	

	protected List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		List<IDAccordo> list = new ArrayList<IDAccordo>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAccordiServizioParteComune", filtroRicerca, null, null, null, null, list);
		return list;
	
	}
	
	protected List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDPortType> list = new ArrayList<IDPortType>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdPortType", filtroRicerca, filtroRicerca, null, null, null, list);
		return list;
		
	}
	
	protected List<IDPortTypeAzione> getAllIdAzionePortType(FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
	
		List<IDPortTypeAzione> list = new ArrayList<IDPortTypeAzione>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAzionePortType", filtroRicerca, null, filtroRicerca, null, null, list);
		return list;
		
	}
	
	protected List<IDAccordoAzione> getAllIdAzioneAccordo(FiltroRicercaAzioni filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDAccordoAzione> list = new ArrayList<IDAccordoAzione>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAzioneAccordo", filtroRicerca, null, null, filtroRicerca, null, list);
		return list;
		
	}
	
	protected List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDResource> list = new ArrayList<IDResource>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdResource", filtroRicerca, null, null, null, filtroRicerca, list);
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> void _fillAllIdAccordiServizioParteComuneEngine(String nomeMetodo, 
			FiltroRicercaAccordi filtroRicercaBase,
			FiltroRicercaPortTypes filtroPT, FiltroRicercaOperations filtroOP, FiltroRicercaAzioni filtroAZ,
			FiltroRicercaResources filtroResource,
			List<T> listReturn) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug(nomeMetodo+"...");

		if(listReturn==null){
			throw new DriverRegistroServiziException("Lista per collezionare i risultati non definita");
		}
		
		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			
			List<String> tipoSoggettiProtocollo = null;
			try {
				if(filtroRicercaBase!=null && (filtroRicercaBase.getProtocollo()!=null || (filtroRicercaBase.getProtocolli()!=null && !filtroRicercaBase.getProtocolli().isEmpty()))){
					tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filtroRicercaBase.getProtocollo(), Filtri.convertToString(filtroRicercaBase.getProtocolli()));
				}
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
			boolean searchByTipoSoggetto = (tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0);
			
			// from
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			if(filtroRicercaBase!=null){
				if( searchByTipoSoggetto || filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				}
			}
			if(filtroPT!=null){
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.PORT_TYPE+".id_accordo");
			}
			if(filtroOP!=null){
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.PORT_TYPE+".id_accordo");
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id="+CostantiDB.PORT_TYPE_AZIONI+".id_port_type");
			}
			if(filtroAZ!=null){
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_AZIONI+".id_accordo");
			}
			if(filtroResource!=null){
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.API_RESOURCES+".id_accordo");
			}
			if(filtroRicercaBase!=null){
				if(filtroRicercaBase.getIdGruppo()!=null && filtroRicercaBase.getIdGruppo().getNome()!=null) {
					sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".id="+CostantiDB.ACCORDI_GRUPPI+".id_gruppo");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_GRUPPI+".id_accordo");
				}
			}
			
			// select field
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"nome","nomeAccordo");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"versione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"id_referente");
			if(filtroPT!=null){
				sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePT");
			}
			if(filtroOP!=null){
				sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePT");
				sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeOP");
			}
			if(filtroAZ!=null){
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI_AZIONI,"nome","nomeAZ");
			}
			if(filtroResource!=null){
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"nome","nomeResource");
			}
			sqlQueryObject.setSelectDistinct(true);
			
			// order
			if(filtroRicercaBase!=null && filtroRicercaBase.isOrder()) {
				sqlQueryObject.addOrderBy("nomeAccordo");
				sqlQueryObject.addOrderBy("versione");
				if(searchByTipoSoggetto){
					sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti,"tipo_soggetto","tipoSoggettoReferente");
					sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti,"nome_soggetto","nomeSoggettoReferente");
					sqlQueryObject.addOrderBy("tipoSoggettoReferente");
					sqlQueryObject.addOrderBy("nomeSoggettoReferente");
				}
				if(filtroPT!=null){
					sqlQueryObject.addOrderBy("nomePT");
				}
				else if(filtroOP!=null){
					sqlQueryObject.addOrderBy("nomePT");
					sqlQueryObject.addOrderBy("nomeOP");
				}
				else if(filtroAZ!=null){
					sqlQueryObject.addOrderBy("nomeAZ");
				}
				else if(filtroResource!=null){
					sqlQueryObject.addOrderBy("nomeResource");
				}
			}
			
			if(filtroRicercaBase!=null){
				// Filtro Base
				if(filtroRicercaBase.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".ora_registrazione > ?");
				if(filtroRicercaBase.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".ora_registrazione < ?");
				if(filtroRicercaBase.getNomeAccordo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
				if(filtroRicercaBase.getVersione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
				if(searchByTipoSoggetto || filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+this.driver.tabellaSoggetti+".id");
					if(filtroRicercaBase.getTipoSoggettoReferente()!=null) {
						sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".tipo_soggetto=?");
					}
					else if(searchByTipoSoggetto) {
						sqlQueryObject.addWhereINCondition(this.driver.tabellaSoggetti+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
					if(filtroRicercaBase.getNomeSoggettoReferente()!=null)
						sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".nome_soggetto=?");
				}
				if(filtroRicercaBase.getServiceBinding()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
				if(filtroRicercaBase.getIdGruppo()!=null && filtroRicercaBase.getIdGruppo().getNome()!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				}
				
				if( (filtroRicercaBase.getIdAccordoCooperazione()!=null &&
						(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null || 
						filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente()!=null || 
						filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null)) ){
					ISQLQueryObject sqlQueryObjectASComposti = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
					sqlQueryObjectASComposti.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
					sqlQueryObjectASComposti.setANDLogicOperator(true);
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo_cooperazione");
					if(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null){
						sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome=?");
					}
					if(filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente()!=null){
						sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente=?");
					}
					if(filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null){
						sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione=?");
					}
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectASComposti);
				}
				else if(filtroRicercaBase.isServizioComposto()!=null){
					ISQLQueryObject sqlQueryObjectASComposti = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectASComposti.addSelectField("id");
					sqlQueryObjectASComposti.setANDLogicOperator(true);
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
					sqlQueryObject.addWhereExistsCondition(!filtroRicercaBase.isServizioComposto(), sqlQueryObjectASComposti);
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroRicercaBase, CostantiDB.ACCORDI);
			}
			
			if(filtroPT!=null){
				if(filtroPT.getNomePortType()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".nome=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroPT, CostantiDB.PORT_TYPE);
			}
			
			if(filtroOP!=null){
				if(filtroOP.getNomePortType()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".nome=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroOP, CostantiDB.PORT_TYPE);
				
				if(filtroOP.getNomeAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".nome=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroOP, CostantiDB.PORT_TYPE_AZIONI);
			}
			
			if(filtroAZ!=null){
				if(filtroAZ.getNomeAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".nome=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroAZ, CostantiDB.ACCORDI_AZIONI);
			}
			
			if(filtroResource!=null){
				if(filtroResource.getResourceName()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.API_RESOURCES+".nome=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroResource, CostantiDB.API_RESOURCES);
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicercaBase!=null){
				if(filtroRicercaBase.getMinDate()!=null){
					this.driver.logDebug("minDate stmt.setTimestamp("+filtroRicercaBase.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicercaBase.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicercaBase.getMaxDate()!=null){
					this.driver.logDebug("maxDate stmt.setTimestamp("+filtroRicercaBase.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicercaBase.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicercaBase.getNomeAccordo()!=null){
					this.driver.logDebug("nomeAccordo stmt.setString("+filtroRicercaBase.getNomeAccordo()+")");
					stm.setString(indexStmt, filtroRicercaBase.getNomeAccordo());
					indexStmt++;
				}	
				if(filtroRicercaBase.getVersione()!=null){
					this.driver.logDebug("versioneAccordo stmt.setString("+filtroRicercaBase.getVersione()+")");
					stm.setInt(indexStmt, filtroRicercaBase.getVersione());
					indexStmt++;
				}	
				if(filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
					if(filtroRicercaBase.getTipoSoggettoReferente()!=null){
						this.driver.logDebug("tipoSoggettoReferenteAccordo stmt.setString("+filtroRicercaBase.getTipoSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicercaBase.getTipoSoggettoReferente());
						indexStmt++;
					}
					if(filtroRicercaBase.getNomeSoggettoReferente()!=null){
						this.driver.logDebug("nomeSoggettoReferenteAccordo stmt.setString("+filtroRicercaBase.getNomeSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicercaBase.getNomeSoggettoReferente());
						indexStmt++;
					}
				}
				if(filtroRicercaBase.getServiceBinding()!=null) {
					this.driver.logDebug("serviceBinding stmt.setString("+filtroRicercaBase.getServiceBinding().getValue()+")");
					stm.setString(indexStmt, filtroRicercaBase.getServiceBinding().getValue());
					indexStmt++;
				}
				if(filtroRicercaBase.getIdGruppo()!=null && filtroRicercaBase.getIdGruppo().getNome()!=null) {
					this.driver.logDebug("gruppo stmt.setString("+filtroRicercaBase.getIdGruppo().getNome()+")");
					stm.setString(indexStmt, filtroRicercaBase.getIdGruppo().getNome());
					indexStmt++;
				}	
				
				if(filtroRicercaBase.getIdAccordoCooperazione()!=null &&
						(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null || 
						filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente()!=null || 
						filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null) ){
					if(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null){
						this.driver.logDebug("nomeAccordoCooperazione stmt.setString("+filtroRicercaBase.getIdAccordoCooperazione().getNome()+")");
						stm.setString(indexStmt, filtroRicercaBase.getIdAccordoCooperazione().getNome());
						indexStmt++;
					}
					if(filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente()!=null){
						long idReferenteAccordoCooperazione = DBUtils.getIdSoggetto(filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente().getNome(), 
								filtroRicercaBase.getIdAccordoCooperazione().getSoggettoReferente().getTipo(), con, this.driver.tipoDB, this.driver.tabellaSoggetti);
						this.driver.logDebug("referenteAccordoCooperazione stmt.setLong("+idReferenteAccordoCooperazione+")");
						stm.setLong(indexStmt, idReferenteAccordoCooperazione);
						indexStmt++;
					}
					if(filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null){
						this.driver.logDebug("versioneAccordoCooperazione stmt.setString("+filtroRicercaBase.getIdAccordoCooperazione().getVersione()+")");
						stm.setInt(indexStmt, filtroRicercaBase.getIdAccordoCooperazione().getVersione());
						indexStmt++;
					}
				}
				
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroRicercaBase, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE);
			}
			
			if(filtroPT!=null){
				if(filtroPT.getNomePortType()!=null){
					stm.setString(indexStmt, filtroPT.getNomePortType());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroPT, ProprietariProtocolProperty.PORT_TYPE);
			}
			
			if(filtroOP!=null){
				if(filtroOP.getNomePortType()!=null){
					stm.setString(indexStmt, filtroOP.getNomePortType());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroOP, ProprietariProtocolProperty.PORT_TYPE);
				
				if(filtroOP.getNomeAzione()!=null){
					stm.setString(indexStmt, filtroOP.getNomeAzione());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroOP, ProprietariProtocolProperty.OPERATION);
			}
			
			if(filtroAZ!=null){
				if(filtroAZ.getNomeAzione()!=null){
					stm.setString(indexStmt, filtroAZ.getNomeAzione());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroAZ, ProprietariProtocolProperty.AZIONE_ACCORDO);
			}
			
			if(filtroResource!=null){
				if(filtroResource.getResourceName()!=null){
					stm.setString(indexStmt, filtroResource.getResourceName());
					indexStmt++;
				}
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroResource, ProprietariProtocolProperty.RESOURCE);
			}
			
			rs = stm.executeQuery();
			while (rs.next()) {
				long idReferente = rs.getLong("id_referente");
				IDSoggetto idSoggettoReferente = null;
				Exception excp = null;
				if(idReferente>0){
					try {
						idSoggettoReferente = this.driver.getIdSoggetto(idReferente,con);
						if(idSoggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						try{
							excp = new Exception("Soggetto referente ["+idReferente+"] presente nell'accordo ["+rs.getString("nome")+"] (versione ["+rs.getInt("versione")+"]) non presente?");
						}finally{
							try{
								if(rs!=null){
									rs.close();
									rs = null;
								}
							}catch (Exception e) {
								// close
							}
							try{
								if(stm!=null){
									stm.close();
									stm=null;
								}
							}catch (Exception e) {
								// close
							}
						}
					}
				}
				
				if(excp!=null) {
					throw excp;
				}
				if(rs==null) {
					throw new Exception("Ricerca referente non riuscita");
				}

				IDAccordo idAccordo = this.driver.idAccordoFactory.getIDAccordoFromValues(rs.getString("nomeAccordo"),idSoggettoReferente,rs.getInt("versione"));
				if(filtroPT!=null){
					IDPortType idPT = new IDPortType();
					idPT.setIdAccordo(idAccordo);
					idPT.setNome(rs.getString("nomePT"));
					listReturn.add((T) idPT);	
				}
				else if(filtroOP!=null){
					IDPortTypeAzione idAzione = new IDPortTypeAzione();
					IDPortType idPT = new IDPortType();
					idPT.setIdAccordo(idAccordo);
					idPT.setNome(rs.getString("nomePT"));
					idAzione.setIdPortType(idPT);
					idAzione.setNome(rs.getString("nomeOP"));
					listReturn.add((T) idAzione);	
				}
				else if(filtroAZ!=null){
					IDAccordoAzione idAzione = new IDAccordoAzione();
					idAzione.setIdAccordo(idAccordo);
					idAzione.setNome(rs.getString("nomeAZ"));
					listReturn.add((T) idAzione);	
				}
				else if(filtroResource!=null){
					IDResource idResource = new IDResource();
					idResource.setIdAccordo(idAccordo);
					idResource.setNome(rs.getString("nomeResource"));
					listReturn.add((T) idResource);		
				}
				else{
					listReturn.add((T) idAccordo);	
				}
			}
			if(listReturn.size()<=0){
				String msgFiltro = "Elementi non trovati che rispettano il filtro di ricerca selezionato: ";
				if(filtroPT!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroPT.toString());
				}
				else if(filtroOP!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroOP.toString());
				}
				else if(filtroAZ!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroAZ.toString());
				}
				else if(filtroResource!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroResource.toString());
				}
				else if(filtroRicercaBase!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroRicercaBase.toString());
				}
				else{
					throw new DriverRegistroServiziNotFound("Elementi non trovati");
				}
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(nomeMetodo+" error",e);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}
}
