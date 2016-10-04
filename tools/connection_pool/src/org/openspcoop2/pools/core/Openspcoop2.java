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


/** <p>Java class Openspcoop2.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Jndi jndi;


  public Openspcoop2() {
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

  public Jndi getJndi() {
    return this.jndi;
  }

  public void setJndi(Jndi jndi) {
    this.jndi = jndi;
  }

  public void addDatasource(Datasource datasource) {
    this.datasource.add(datasource);
  }

  public Datasource getDatasource(int index) {
    return this.datasource.get( index );
  }

  public Datasource removeDatasource(int index) {
    return this.datasource.remove( index );
  }

  public List<Datasource> getDatasourceList() {
    return this.datasource;
  }

  public void setDatasourceList(List<Datasource> datasource) {
    this.datasource=datasource;
  }

  public int sizeDatasourceList() {
    return this.datasource.size();
  }

  public void addConnectionFactory(ConnectionFactory connectionFactory) {
    this.connectionFactory.add(connectionFactory);
  }

  public ConnectionFactory getConnectionFactory(int index) {
    return this.connectionFactory.get( index );
  }

  public ConnectionFactory removeConnectionFactory(int index) {
    return this.connectionFactory.remove( index );
  }

  public List<ConnectionFactory> getConnectionFactoryList() {
    return this.connectionFactory;
  }

  public void setConnectionFactoryList(List<ConnectionFactory> connectionFactory) {
    this.connectionFactory=connectionFactory;
  }

  public int sizeConnectionFactoryList() {
    return this.connectionFactory.size();
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

  public static final String JNDI = "jndi";

  protected List<Datasource> datasource = new ArrayList<Datasource>();

  /**
   * @deprecated Use method getDatasourceList
   * @return List<Datasource>
  */
  @Deprecated
  public List<Datasource> getDatasource() {
  	return this.datasource;
  }

  /**
   * @deprecated Use method setDatasourceList
   * @param datasource List<Datasource>
  */
  @Deprecated
  public void setDatasource(List<Datasource> datasource) {
  	this.datasource=datasource;
  }

  /**
   * @deprecated Use method sizeDatasourceList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatasource() {
  	return this.datasource.size();
  }

  public static final String DATASOURCE = "datasource";

  protected List<ConnectionFactory> connectionFactory = new ArrayList<ConnectionFactory>();

  /**
   * @deprecated Use method getConnectionFactoryList
   * @return List<ConnectionFactory>
  */
  @Deprecated
  public List<ConnectionFactory> getConnectionFactory() {
  	return this.connectionFactory;
  }

  /**
   * @deprecated Use method setConnectionFactoryList
   * @param connectionFactory List<ConnectionFactory>
  */
  @Deprecated
  public void setConnectionFactory(List<ConnectionFactory> connectionFactory) {
  	this.connectionFactory=connectionFactory;
  }

  /**
   * @deprecated Use method sizeConnectionFactoryList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConnectionFactory() {
  	return this.connectionFactory.size();
  }

  public static final String CONNECTION_FACTORY = "connectionFactory";

}
