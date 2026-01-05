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
import java.util.concurrent.TimeoutException;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.slf4j.Logger;

/**
 * Datasource
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataSource implements javax.sql.DataSource,java.sql.Wrapper {

	static Logger checkLogger = null;
	static boolean checkIsClosed = true;
	static boolean checkAutocommit = true;
	public static boolean isCheckIsClosed() {
		return checkIsClosed;
	}
	public static void setCheckIsClosed(boolean checkIsClosed) {
		DataSource.checkIsClosed = checkIsClosed;
	}
	public static boolean isCheckAutocommit() {
		return checkAutocommit;
	}
	public static void setCheckAutocommit(boolean checkAutocommit) {
		DataSource.checkAutocommit = checkAutocommit;
	}
	public static Logger getCheckLogger() {
		return checkLogger;
	}
	public static void setCheckLogger(Logger checkLogger) {
		DataSource.checkLogger = checkLogger;
	}
	
	private javax.sql.DataSource wrappedDatasource;
	private TipiDatabase tipoDatabase;
	private boolean wrapOriginalMethods;
	private String uuidDatasource;
	private String applicativeIdDatasource;
	
	/** Data di rilascio della risorsa */
	private Date date = null;

	private int transactionIsolationLevelDefault;
	private String jndiName;
	
	private boolean closed = false;

	private Map<String,org.openspcoop2.utils.datasource.Connection> releasedConnections = new ConcurrentHashMap<>();
	
	public String[] getJmxStatus() {	
		String[] sNull = null;
		if(this.releasedConnections==null || this.releasedConnections.size()<=0)
			return sNull;
	
		org.openspcoop2.utils.datasource.Connection[] list = this.releasedConnections.values().toArray(new org.openspcoop2.utils.datasource.Connection[1]);
		List<String> listResource = new ArrayList<>();
		for (int i = 0; i < list.length; i++) {
			org.openspcoop2.utils.datasource.Connection connection = list[i];
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
			if(connection.getModuloFunzionale() instanceof String){
				if(bf.length() > 0){
					bf.append(" ");
				}
				bf.append("moduloFunzionale:");
				bf.append(connection.getModuloFunzionale());
			}
			
			listResource.add(bf.toString());
		}
		if(!listResource.isEmpty()){
			Collections.sort(listResource);
			return listResource.toArray(new String[1]);
		}else
			return sNull;
		
	}
	
	public String getInformazioniDatabase() throws TimeoutException, UtilsException {
		InformazioniDatabaseChecker versioneBaseDatiChecker = new InformazioniDatabaseChecker(this);
		return Utilities.execute(5, versioneBaseDatiChecker);
	}
	
	protected DataSource(javax.sql.DataSource datasource, TipiDatabase tipoDatabase, boolean wrapOriginalMethods, String jndiName, String applicativeIdDatasource) throws SQLException{
		try{
			this.wrappedDatasource = datasource;
			this.tipoDatabase = tipoDatabase;
			this.wrapOriginalMethods = wrapOriginalMethods;
			this.uuidDatasource = UUIDUtilsGenerator.newUUID();
			this.jndiName = jndiName;
			this.applicativeIdDatasource = applicativeIdDatasource;
			this.date = DateManager.getDate();
		}catch(Exception e){
			throw new SQLException(e.getMessage(),e);
		}
		java.sql.Connection connectionTest = null;
		try{
			// Prelevo livello di transaction isolation
			connectionTest = this.wrappedDatasource.getConnection();
			this.transactionIsolationLevelDefault = connectionTest.getTransactionIsolation();
		}catch(Exception e){
			throw new SQLException("Test getConnection failed: "+e.getMessage(),e);
		}finally {
			if(connectionTest!=null) {
				JDBCUtilities.closeConnection(checkLogger, connectionTest, checkAutocommit, checkIsClosed);
			}
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
			org.openspcoop2.utils.datasource.Connection connection = it.next();
			try{
				connection.close();
			}catch(Exception eclose){
				// ignore
			}
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
			org.openspcoop2.utils.datasource.Connection c = new org.openspcoop2.utils.datasource.Connection(this.wrappedDatasource.getConnection(), this.tipoDatabase, 
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
		return this.wrappedDatasource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter pw) throws SQLException {
		this.wrappedDatasource.setLogWriter(pw);
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.wrappedDatasource.getLoginTimeout();
	}

	@Override
	public void setLoginTimeout(int timeout) throws SQLException {
		this.wrappedDatasource.setLoginTimeout(timeout);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(this.wrappedDatasource instanceof java.sql.Wrapper){
			return ((java.sql.Wrapper)this.wrappedDatasource).unwrap(iface);
		}
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(this.wrappedDatasource instanceof java.sql.Wrapper){
			return ((java.sql.Wrapper)this.wrappedDatasource).isWrapperFor(iface);
		}
		return false;
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.wrappedDatasource.getParentLogger();
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
			throw new UtilsException("Tipo di Database non disponibile");
		}

		Connection c = null; 
		try{
			c = this.dataSource.getConnection();

			JDBCUtilities.addInformazioniDatabaseFromMetaData(c, bf);
			
			if(bf.length()<=0){
				throw new UtilsException("Non sono disponibili informazioni sul database");
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
