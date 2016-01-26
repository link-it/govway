/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.pools.pdd.db;

/**
 * Contiene la definizione di un DataSource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBInfo {

	private String jndiName;

	private String connectionUrl;

	private String driverClass;

	private String username;

	private String password;

	private boolean preparedStatementPool;

	private boolean autoCommit;

	private boolean readOnly;

	private String transactionIsolation;
	
	private int pool_initial;

	private int pool_min;

	private int pool_max;

	private boolean validation_abilitato = false;
	
	private String validation_operation;

	private boolean validation_testOnGet;

	private boolean validation_testOnRelease;
	
	private String when_exhausted_action;

	private int when_exhausted_blockingTimeout = -1;
	
	private boolean idle_abilitato = false;
	
	private int idle_timeBetweenEvictionRuns = -1;

	private int idle_numTestsPerEvictionRun = 3;

	private int idle_idleObjectTimeout = 1800000;

	private boolean idle_validateObject = false;

	public boolean isAutoCommit() {
		return this.autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String getConnectionUrl() {
		return this.connectionUrl;
	}

	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public String getDriverClass() {
		return this.driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public int getIdle_idleObjectTimeout() {
		return this.idle_idleObjectTimeout;
	}

	public void setIdle_idleObjectTimeout(int idle_idleObjectTimeout) {
		this.idle_idleObjectTimeout = idle_idleObjectTimeout;
	}

	public int getIdle_numTestsPerEvictionRun() {
		return this.idle_numTestsPerEvictionRun;
	}

	public void setIdle_numTestsPerEvictionRun(int idle_numTestsPerEvictionRun) {
		this.idle_numTestsPerEvictionRun = idle_numTestsPerEvictionRun;
	}

	public int getIdle_timeBetweenEvictionRuns() {
		return this.idle_timeBetweenEvictionRuns;
	}

	public void setIdle_timeBetweenEvictionRuns(int idle_timeBetweenEvictionRuns) {
		this.idle_timeBetweenEvictionRuns = idle_timeBetweenEvictionRuns;
	}

	public boolean isIdle_validateObject() {
		return this.idle_validateObject;
	}

	public void setIdle_validateObject(boolean idle_validateObject) {
		this.idle_validateObject = idle_validateObject;
	}

	public String getJndiName() {
		return this.jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPool_initial() {
		return this.pool_initial;
	}

	public void setPool_initial(int pool_initial) {
		this.pool_initial = pool_initial;
	}

	public int getPool_max() {
		return this.pool_max;
	}

	public void setPool_max(int pool_max) {
		this.pool_max = pool_max;
	}

	public int getPool_min() {
		return this.pool_min;
	}

	public void setPool_min(int pool_min) {
		this.pool_min = pool_min;
	}

	public boolean isPreparedStatementPool() {
		return this.preparedStatementPool;
	}

	public void setPreparedStatementPool(boolean preparedStatementPool) {
		this.preparedStatementPool = preparedStatementPool;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getTransactionIsolation() {
		return this.transactionIsolation;
	}

	public void setTransactionIsolation(String transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getValidation_operation() {
		return this.validation_operation;
	}

	public void setValidation_operation(String validation_operation) {
		this.validation_operation = validation_operation;
	}

	public boolean isValidation_testOnGet() {
		return this.validation_testOnGet;
	}

	public void setValidation_testOnGet(boolean validation_testOnGet) {
		this.validation_testOnGet = validation_testOnGet;
	}

	public boolean isValidation_testOnRelease() {
		return this.validation_testOnRelease;
	}

	public void setValidation_testOnRelease(boolean validation_testOnRelease) {
		this.validation_testOnRelease = validation_testOnRelease;
	}

	public String getWhen_exhausted_action() {
		return this.when_exhausted_action;
	}

	public void setWhen_exhausted_action(String when_exhausted_action) {
		this.when_exhausted_action = when_exhausted_action;
	}

	public int getWhen_exhausted_blockingTimeout() {
		return this.when_exhausted_blockingTimeout;
	}

	public void setWhen_exhausted_blockingTimeout(
			int when_exhausted_blockingTimeout) {
		this.when_exhausted_blockingTimeout = when_exhausted_blockingTimeout;
	}

	public boolean isIdle_abilitato() {
		return this.idle_abilitato;
	}

	public void setIdle_abilitato(boolean idle_abilitato) {
		this.idle_abilitato = idle_abilitato;
	}

	public boolean isValidation_abilitato() {
		return this.validation_abilitato;
	}

	public void setValidation_abilitato(boolean validation_abilitato) {
		this.validation_abilitato = validation_abilitato;
	}
}
