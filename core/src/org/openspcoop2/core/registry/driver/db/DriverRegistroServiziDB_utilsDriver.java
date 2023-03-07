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
import java.sql.SQLException;
import java.sql.Statement;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_utilsDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_utilsDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_utilsDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected void reset() throws DriverRegistroServiziException {

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("reset");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		//this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			//Svuoto il db del registro servizi
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_RESPONSE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_PROPS);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_CREDENZIALI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SCOPE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PDD);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PROTOCOL_PROPERTIES);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

		} catch (SQLException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Errore durante il reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Errore durante il reset : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(stmt);

			this.driver.closeConnection(error,con);
		}
	}

	protected void resetCtrlstat() throws DriverRegistroServiziException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("resetCtrlstat");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		//this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
		} catch (SQLException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
		}finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void isAlive() throws CoreException{

		if(this.driver.create==false)
			throw new CoreException("Driver non inizializzato");

		if(this.driver.atomica){
			// Verifico la connessione
			Connection con = null;
			Statement stmtTest = null;
			try {
				con = this.driver.getConnectionFromDatasource("isAlive");
				if(con == null)
					throw new Exception("Connessione is null");
				// test:
				try {
					stmtTest = con.createStatement();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DB_INFO_CONSOLE);
					sqlQueryObject.addSelectField("*");
					String sqlQuery = sqlQueryObject.createSQLQuery();
					stmtTest.execute(sqlQuery);
				}catch(Throwable t) {
					JDBCUtilities.closeResources(stmtTest);
					try {
						stmtTest = con.createStatement();
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DB_INFO);
						sqlQueryObject.addSelectField("*");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stmtTest.execute(sqlQuery);
					}catch(Throwable tInternal) {
						throw new UtilsMultiException(t,tInternal);
					}
				}
			} catch (Exception e) {
				throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);

			}finally{
				JDBCUtilities.closeResources(stmtTest);
				try{
					if(con!=null)
						con.close();
				}catch(Exception e){
					// close
				}
			}
		}else{
			Statement stmtTest = null;
			try {
				if(this.driver.globalConnection == null)
					throw new Exception("Connessione is null");
				// test:
				try {
					stmtTest = this.driver.globalConnection.createStatement();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DB_INFO_CONSOLE);
					sqlQueryObject.addSelectField("*");
					String sqlQuery = sqlQueryObject.createSQLQuery();
					stmtTest.execute(sqlQuery);
				}catch(Throwable t) {
					JDBCUtilities.closeResources(stmtTest);
					try {
						stmtTest = this.driver.globalConnection.createStatement();
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DB_INFO);
						sqlQueryObject.addSelectField("*");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stmtTest.execute(sqlQuery);
					}catch(Throwable tInternal) {
						throw new UtilsMultiException(t,tInternal);
					}
				}
			} catch (Exception e) {
				throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);

			}finally{
				JDBCUtilities.closeResources(stmtTest);
			}
		}
	}
}
