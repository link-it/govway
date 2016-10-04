/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class ConnectionFactory.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class ConnectionFactory extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected PoolSize poolSize;

  protected Validation validation;

  protected WhenExhausted whenExhausted;

  protected IdleObjectEviction idleObjectEviction;

  protected String jndiName;

  protected String connectionFactory;

  protected String username;

  protected String password;

  protected String clientId;

  protected boolean autoCommit;

  protected String acknowledgmentType;

  protected String singleConnectionWithSessionPool;


  public ConnectionFactory() {
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

  public void addContextProperty(ContextProperty contextProperty) {
    this.contextProperty.add(contextProperty);
  }

  public ContextProperty getContextProperty(int index) {
    return this.contextProperty.get( index );
  }

  public ContextProperty removeContextProperty(int index) {
    return this.contextProperty.remove( index );
  }

  public List<ContextProperty> getContextPropertyList() {
    return this.contextProperty;
  }

  public void setContextPropertyList(List<ContextProperty> contextProperty) {
    this.contextProperty=contextProperty;
  }

  public int sizeContextPropertyList() {
    return this.contextProperty.size();
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

  public String getConnectionFactory() {
    if(this.connectionFactory!=null && ("".equals(this.connectionFactory)==false)){
		return this.connectionFactory.trim();
	}else{
		return null;
	}

  }

  public void setConnectionFactory(String connectionFactory) {
    this.connectionFactory = connectionFactory;
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

  public String getClientId() {
    if(this.clientId!=null && ("".equals(this.clientId)==false)){
		return this.clientId.trim();
	}else{
		return null;
	}

  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
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

  public String getAcknowledgmentType() {
    if(this.acknowledgmentType!=null && ("".equals(this.acknowledgmentType)==false)){
		return this.acknowledgmentType.trim();
	}else{
		return null;
	}

  }

  public void setAcknowledgmentType(String acknowledgmentType) {
    this.acknowledgmentType = acknowledgmentType;
  }

  public String getSingleConnectionWithSessionPool() {
    if(this.singleConnectionWithSessionPool!=null && ("".equals(this.singleConnectionWithSessionPool)==false)){
		return this.singleConnectionWithSessionPool.trim();
	}else{
		return null;
	}

  }

  public void setSingleConnectionWithSessionPool(String singleConnectionWithSessionPool) {
    this.singleConnectionWithSessionPool = singleConnectionWithSessionPool;
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

  protected List<ContextProperty> contextProperty = new ArrayList<ContextProperty>();

  /**
   * @deprecated Use method getContextPropertyList
   * @return List<ContextProperty>
  */
  @Deprecated
  public List<ContextProperty> getContextProperty() {
  	return this.contextProperty;
  }

  /**
   * @deprecated Use method setContextPropertyList
   * @param contextProperty List<ContextProperty>
  */
  @Deprecated
  public void setContextProperty(List<ContextProperty> contextProperty) {
  	this.contextProperty=contextProperty;
  }

  /**
   * @deprecated Use method sizeContextPropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeContextProperty() {
  	return this.contextProperty.size();
  }

  public static final String CONTEXT_PROPERTY = "contextProperty";

  public static final String POOL_SIZE = "poolSize";

  public static final String VALIDATION = "validation";

  public static final String WHEN_EXHAUSTED = "whenExhausted";

  public static final String IDLE_OBJECT_EVICTION = "idleObjectEviction";

  public static final String JNDI_NAME = "jndiName";

  public static final String CONNECTION_FACTORY = "connectionFactory";

  public static final String USERNAME = "username";

  public static final String PASSWORD = "password";

  public static final String CLIENT_ID = "clientId";

  public static final String AUTO_COMMIT = "autoCommit";

  public static final String ACKNOWLEDGMENT_TYPE = "acknowledgmentType";

  public static final String SINGLE_CONNECTION_WITH_SESSION_POOL = "singleConnectionWithSessionPool";

}
