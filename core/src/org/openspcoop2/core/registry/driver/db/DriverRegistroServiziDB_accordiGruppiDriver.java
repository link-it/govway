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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiGruppiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiGruppiDriver {

	private DriverRegistroServiziDB driver = null;
	
	public DriverRegistroServiziDB_accordiGruppiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected void readAccordiGruppi(AccordoServizioParteComune as,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readAccordiGruppi");
			else
				con = this.driver.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "id", "identificativoGruppo");
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "nome", "nomeGruppo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo = "+CostantiDB.GRUPPI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.GRUPPI+".nome");
			sqlQueryObject.setSortType(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			while (rs.next()) {
				if(as.getGruppi()==null){
					as.setGruppi(new GruppiAccordo());
				}	
				GruppoAccordo gruppo = new GruppoAccordo();
				gruppo.setId(rs.getLong("identificativoGruppo"));
				gruppo.setNome(rs.getString("nomeGruppo"));
				as.getGruppi().addGruppo(gruppo);
			}
			rs.close();
			stm.close();
		
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readAccordiGruppi] Exception :" + se.getMessage(),se);
		} finally {

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

			try {
				if (conParam==null && this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}
}
