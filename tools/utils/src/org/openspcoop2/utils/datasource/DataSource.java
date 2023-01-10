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

package org.openspcoop2.utils.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * Datasource
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataSource implements javax.sql.DataSource,java.sql.Wrapper {

	private javax.sql.DataSource datasource;
	private TipiDatabase tipoDatabase;
	private boolean wrapOriginalMethods;
	private String uuidDatasource;
	private String applicativeIdDatasource;
	
	/** Data di rilascio della risorsa */
	private Date date = null;

	private int transactionIsolationLevelDefault;
	private String jndiName;
	
	private boolean closed = false;

	private Map<String,org.openspcoop2.utils.datasource.Connection> releasedConnections = new ConcurrentHashMap<String,org.openspcoop2.utils.datasource.Connection>();
	
	public String[] getJmxStatus() throws UtilsException{	
		if(this.releasedConnections==null || this.releasedConnections.size()<=0)
			return null;
	
		org.openspcoop2.utils.datasource.Connection[] list = this.releasedConnections.values().toArray(new org.openspcoop2.utils.datasource.Connection[1]);
		List<String> listResource = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			org.openspcoop2.utils.datasource.Connection connection = (org.openspcoop2.utils.datasource.Connection) list[i];
			StringBuilder bf = new StringBuilder();
			SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
			bf.append("(").append(dateformat.format(connection.getDate())).append(") ");
			if(connection.getIdTransazione()!=null){
				if(bf.length() > 0){
					bf.append(" ");
				}
				bf.append("idTransazione:");
				bf.append(connection.getIdTransazione());
			}
			if(connection.getModuloFunzionale()!=null && (connection.getModuloFunzionale() instanceof String)){
				if(bf.length() > 0){
					bf.append(" ");
				}
				bf.append("moduloFunzionale:");
				bf.append(connection.getModuloFunzionale());
			}
			
			listResource.add(bf.toString());
		}
		if(listResource.size()>0){
			Collections.sort(listResource);
			return listResource.toArray(new String[1]);
		}else
			return null;
		
	}
	
	public String getInformazioniDatabase() throws Exception{
		InformazioniDatabaseChecker versioneBaseDatiChecker = new InformazioniDatabaseChecker(this);
		return Utilities.execute(5, versioneBaseDatiChecker);
	}
	
	protected DataSource(javax.sql.DataSource datasource, TipiDatabase tipoDatabase, boolean wrapOriginalMethods, String jndiName, String applicativeIdDatasource) throws SQLException{
		try{
			this.datasource = datasource;
			this.tipoDatabase = tipoDatabase;
			this.wrapOriginalMethods = wrapOriginalMethods;
			this.uuidDatasource = UUIDUtilsGenerator.newUUID();
			this.jndiName = jndiName;
			this.applicativeIdDatasource = applicativeIdDatasource;
			this.date = DateManager.getDate();
		}catch(Exception e){
			throw new SQLException(e.getMessage(),e);
		}
		try{
			// Prelevo livello di transaction isolation
			java.sql.Connection connectionTest = this.datasource.getConnection();
			this.transactionIsolationLevelDefault = connectionTest.getTransactionIsolation();
			connectionTest.close();	 
		}catch(Exception e){
			throw new SQLException("Test getConnection failed: "+e.getMessage(),e);
		}
	}
	
	public String getUuidDatasource() {
		return this.uuidDatasource;
	}
	
	public String getApplicativeIdDatasource() {
		return this.applicativeIdDatasource;
	}
	
	public int size() {
		return this.releasedConnections.size();
	}
	
	public int getTransactionIsolationLevelDefault() {
		return this.transactionIsolationLevelDefault;
	}
	
	public String getJndiName() {
		return this.jndiName;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public TipiDatabase getTipoDatabase() {
		return this.tipoDatabase;
	}
	
	// Metodi utilizzati dallo shutdown
	boolean isClosed() {
		return this.closed;
	}
	void setClosed(boolean closed) {
		this.closed = closed;
	}
	void releaseConnnections(){
		Collection<org.openspcoop2.utils.datasource.Connection> list = this.releasedConnections.values();
		Iterator<org.openspcoop2.utils.datasource.Connection> it = list.iterator();
		while (it.hasNext()) {
			org.openspcoop2.utils.datasource.Connection connection = (org.openspcoop2.utils.datasource.Connection) it.next();
			try{
				connection.close();
			}catch(Exception eclose){}
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if(this.wrapOriginalMethods){
			// wrap verso getConnection sottostante.
			return this.getWrappedConnection(null, null);
		}
		else{
			throw new SQLException("Not Supported, use getWrappedConnection");
		}
	}

	@Override
	public Connection getConnection(String idTransazione, String moduloFunzionale) throws SQLException {
		if(this.wrapOriginalMethods){
			// wrap verso getConnection sottostante.
			return this.getWrappedConnection(idTransazione, moduloFunzionale);
		}
		else{
			throw new SQLException("Not Supported, use getWrappedConnection");
		}
	}
	
	public org.openspcoop2.utils.datasource.Connection getWrappedConnection() throws SQLException {
		return this.getWrappedConnection(null, null);
	}
	
	public org.openspcoop2.utils.datasource.Connection getWrappedConnection(String idTransazione) throws SQLException {
		return this.getWrappedConnection(idTransazione, null);
	}
	
	public org.openspcoop2.utils.datasource.Connection getWrappedConnection(String idTransazione, Object moduloFunzionale) throws SQLException {
		if(this.closed){
			throw new SQLException("Shutdown in progress");
		}
		try{
			org.openspcoop2.utils.datasource.Connection c = new org.openspcoop2.utils.datasource.Connection(this.datasource.getConnection(), this.tipoDatabase, 
						idTransazione, moduloFunzionale, this.uuidDatasource);
			this.releasedConnections.put(c.getId(), c);
			return c;
		}catch(Exception e){
			throw new SQLException(e.getMessage(),e);
		}
	}

	
	protected void unregisterConnection(org.openspcoop2.utils.datasource.Connection connection) throws SQLException{
		if(this.releasedConnections.containsKey(connection.getId())){
			this.releasedConnections.remove(connection.getId()).closeWrappedConnection();
		}
	}
	public void closeConnection(org.openspcoop2.utils.datasource.Connection connection) throws SQLException{
		
		if(connection==null){
			throw new SQLException("Parameter undefined");
		}
		
		connection.close();
	}
	
	public void closeConnection(Connection connection) throws SQLException{
		
		if(connection==null){
			throw new SQLException("Parameter undefined");
		}
			
		if(connection instanceof org.openspcoop2.utils.datasource.Connection){
			((org.openspcoop2.utils.datasource.Connection)connection).close();
		}
		else{
			throw new SQLException("Connection type unsupported, expected:"+org.openspcoop2.utils.datasource.Connection.class+" found:"+connection.getClass().getName());
		}
	}
	
	
	
	// **** METODI SU CUI E' STATO SOLAMENTE EFFETTUATO IL WRAP ******
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.datasource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter pw) throws SQLException {
		this.datasource.setLogWriter(pw);
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.datasource.getLoginTimeout();
	}

	@Override
	public void setLoginTimeout(int timeout) throws SQLException {
		this.datasource.setLoginTimeout(timeout);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(this.datasource instanceof java.sql.Wrapper){
			return ((java.sql.Wrapper)this.datasource).unwrap(iface);
		}
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(this.datasource instanceof java.sql.Wrapper){
			return ((java.sql.Wrapper)this.datasource).isWrapperFor(iface);
		}
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.datasource.getParentLogger();
	}
	


}

class InformazioniDatabaseChecker implements Callable<String>{

	private DataSource dataSource;
	
	public InformazioniDatabaseChecker(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public String call() throws Exception {
		StringBuilder bf = new StringBuilder();

		if(this.dataSource.getTipoDatabase()!=null){
			bf.append("TipoDatabase: "+this.dataSource.getTipoDatabase().getNome());
		}
		else{
			throw new Exception("Tipo di Database non disponibile");
		}

		Connection c = null; 
		try{
			c = (Connection) this.dataSource.getConnection();

			JDBCUtilities.addInformazioniDatabaseFromMetaData(c, bf);
			
			if(bf.length()<=0){
				throw new Exception("Non sono disponibili informazioni sul database");
			}else{
				return bf.toString();
			}

		}finally{
			try{
				if(c!=null)
					this.dataSource.closeConnection(c);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
}
