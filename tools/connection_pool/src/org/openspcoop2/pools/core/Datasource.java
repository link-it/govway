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
package org.openspcoop2.pools.core;

import java.io.Serializable;


/** <p>Java class Datasource.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Datasource extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected PoolSize poolSize;

  protected Validation validation;

  protected WhenExhausted whenExhausted;

  protected IdleObjectEviction idleObjectEviction;

  protected String jndiName;

  protected String connectionUrl;

  protected String driverClass;

  protected String username;

  protected String password;

  protected boolean preparedStatementPool;

  protected boolean autoCommit;

  protected boolean readOnly;

  protected String transactionIsolation;


  public Datasource() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public PoolSize getPoolSize() {
    return this.poolSize;
  }

  public void setPoolSize(PoolSize poolSize) {
    this.poolSize = poolSize;
  }

  public Validation getValidation() {
    return this.validation;
  }

  public void setValidation(Validation validation) {
    this.validation = validation;
  }

  public WhenExhausted getWhenExhausted() {
    return this.whenExhausted;
  }

  public void setWhenExhausted(WhenExhausted whenExhausted) {
    this.whenExhausted = whenExhausted;
  }

  public IdleObjectEviction getIdleObjectEviction() {
    return this.idleObjectEviction;
  }

  public void setIdleObjectEviction(IdleObjectEviction idleObjectEviction) {
    this.idleObjectEviction = idleObjectEviction;
  }

  public String getJndiName() {
    if(this.jndiName!=null && ("".equals(this.jndiName)==false)){
		return this.jndiName.trim();
	}else{
		return null;
	}

  }

  public void setJndiName(String jndiName) {
    this.jndiName = jndiName;
  }

  public String getConnectionUrl() {
    if(this.connectionUrl!=null && ("".equals(this.connectionUrl)==false)){
		return this.connectionUrl.trim();
	}else{
		return null;
	}

  }

  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public String getDriverClass() {
    if(this.driverClass!=null && ("".equals(this.driverClass)==false)){
		return this.driverClass.trim();
	}else{
		return null;
	}

  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  public String getUsername() {
    if(this.username!=null && ("".equals(this.username)==false)){
		return this.username.trim();
	}else{
		return null;
	}

  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    if(this.password!=null && ("".equals(this.password)==false)){
		return this.password.trim();
	}else{
		return null;
	}

  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isPreparedStatementPool() {
    return this.preparedStatementPool;
  }

  public boolean getPreparedStatementPool() {
    return this.preparedStatementPool;
  }

  public void setPreparedStatementPool(boolean preparedStatementPool) {
    this.preparedStatementPool = preparedStatementPool;
  }

  public boolean isAutoCommit() {
    return this.autoCommit;
  }

  public boolean getAutoCommit() {
    return this.autoCommit;
  }

  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public boolean isReadOnly() {
    return this.readOnly;
  }

  public boolean getReadOnly() {
    return this.readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public String getTransactionIsolation() {
    if(this.transactionIsolation!=null && ("".equals(this.transactionIsolation)==false)){
		return this.transactionIsolation.trim();
	}else{
		return null;
	}

  }

  public void setTransactionIsolation(String transactionIsolation) {
    this.transactionIsolation = transactionIsolation;
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  public static final String POOL_SIZE = "poolSize";

  public static final String VALIDATION = "validation";

  public static final String WHEN_EXHAUSTED = "whenExhausted";

  public static final String IDLE_OBJECT_EVICTION = "idleObjectEviction";

  public static final String JNDI_NAME = "jndiName";

  public static final String CONNECTION_URL = "connectionUrl";

  public static final String DRIVER_CLASS = "driverClass";

  public static final String USERNAME = "username";

  public static final String PASSWORD = "password";

  public static final String PREPARED_STATEMENT_POOL = "preparedStatementPool";

  public static final String AUTO_COMMIT = "autoCommit";

  public static final String READ_ONLY = "readOnly";

  public static final String TRANSACTION_ISOLATION = "transactionIsolation";

}
