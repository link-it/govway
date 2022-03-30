/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-service-handlers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-service-handlers"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="init" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="exit" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="integrationManagerRequest" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="integrationManagerResponse" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-service-handlers", 
  propOrder = {
  	"init",
  	"exit",
  	"integrationManagerRequest",
  	"integrationManagerResponse"
  }
)

@XmlRootElement(name = "configurazione-service-handlers")

public class ConfigurazioneServiceHandlers extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneServiceHandlers() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public void addInit(ConfigurazioneHandler init) {
    this.init.add(init);
  }

  public ConfigurazioneHandler getInit(int index) {
    return this.init.get( index );
  }

  public ConfigurazioneHandler removeInit(int index) {
    return this.init.remove( index );
  }

  public List<ConfigurazioneHandler> getInitList() {
    return this.init;
  }

  public void setInitList(List<ConfigurazioneHandler> init) {
    this.init=init;
  }

  public int sizeInitList() {
    return this.init.size();
  }

  public void addExit(ConfigurazioneHandler exit) {
    this.exit.add(exit);
  }

  public ConfigurazioneHandler getExit(int index) {
    return this.exit.get( index );
  }

  public ConfigurazioneHandler removeExit(int index) {
    return this.exit.remove( index );
  }

  public List<ConfigurazioneHandler> getExitList() {
    return this.exit;
  }

  public void setExitList(List<ConfigurazioneHandler> exit) {
    this.exit=exit;
  }

  public int sizeExitList() {
    return this.exit.size();
  }

  public void addIntegrationManagerRequest(ConfigurazioneHandler integrationManagerRequest) {
    this.integrationManagerRequest.add(integrationManagerRequest);
  }

  public ConfigurazioneHandler getIntegrationManagerRequest(int index) {
    return this.integrationManagerRequest.get( index );
  }

  public ConfigurazioneHandler removeIntegrationManagerRequest(int index) {
    return this.integrationManagerRequest.remove( index );
  }

  public List<ConfigurazioneHandler> getIntegrationManagerRequestList() {
    return this.integrationManagerRequest;
  }

  public void setIntegrationManagerRequestList(List<ConfigurazioneHandler> integrationManagerRequest) {
    this.integrationManagerRequest=integrationManagerRequest;
  }

  public int sizeIntegrationManagerRequestList() {
    return this.integrationManagerRequest.size();
  }

  public void addIntegrationManagerResponse(ConfigurazioneHandler integrationManagerResponse) {
    this.integrationManagerResponse.add(integrationManagerResponse);
  }

  public ConfigurazioneHandler getIntegrationManagerResponse(int index) {
    return this.integrationManagerResponse.get( index );
  }

  public ConfigurazioneHandler removeIntegrationManagerResponse(int index) {
    return this.integrationManagerResponse.remove( index );
  }

  public List<ConfigurazioneHandler> getIntegrationManagerResponseList() {
    return this.integrationManagerResponse;
  }

  public void setIntegrationManagerResponseList(List<ConfigurazioneHandler> integrationManagerResponse) {
    this.integrationManagerResponse=integrationManagerResponse;
  }

  public int sizeIntegrationManagerResponseList() {
    return this.integrationManagerResponse.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="init",required=true,nillable=false)
  protected List<ConfigurazioneHandler> init = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getInitList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getInit() {
  	return this.init;
  }

  /**
   * @deprecated Use method setInitList
   * @param init List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setInit(List<ConfigurazioneHandler> init) {
  	this.init=init;
  }

  /**
   * @deprecated Use method sizeInitList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeInit() {
  	return this.init.size();
  }

  @XmlElement(name="exit",required=true,nillable=false)
  protected List<ConfigurazioneHandler> exit = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getExitList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getExit() {
  	return this.exit;
  }

  /**
   * @deprecated Use method setExitList
   * @param exit List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setExit(List<ConfigurazioneHandler> exit) {
  	this.exit=exit;
  }

  /**
   * @deprecated Use method sizeExitList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeExit() {
  	return this.exit.size();
  }

  @XmlElement(name="integrationManagerRequest",required=true,nillable=false)
  protected List<ConfigurazioneHandler> integrationManagerRequest = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getIntegrationManagerRequestList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getIntegrationManagerRequest() {
  	return this.integrationManagerRequest;
  }

  /**
   * @deprecated Use method setIntegrationManagerRequestList
   * @param integrationManagerRequest List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setIntegrationManagerRequest(List<ConfigurazioneHandler> integrationManagerRequest) {
  	this.integrationManagerRequest=integrationManagerRequest;
  }

  /**
   * @deprecated Use method sizeIntegrationManagerRequestList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIntegrationManagerRequest() {
  	return this.integrationManagerRequest.size();
  }

  @XmlElement(name="integrationManagerResponse",required=true,nillable=false)
  protected List<ConfigurazioneHandler> integrationManagerResponse = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getIntegrationManagerResponseList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getIntegrationManagerResponse() {
  	return this.integrationManagerResponse;
  }

  /**
   * @deprecated Use method setIntegrationManagerResponseList
   * @param integrationManagerResponse List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setIntegrationManagerResponse(List<ConfigurazioneHandler> integrationManagerResponse) {
  	this.integrationManagerResponse=integrationManagerResponse;
  }

  /**
   * @deprecated Use method sizeIntegrationManagerResponseList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIntegrationManagerResponse() {
  	return this.integrationManagerResponse.size();
  }

}
