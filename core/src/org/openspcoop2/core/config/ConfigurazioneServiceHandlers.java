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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

public class ConfigurazioneServiceHandlers extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneServiceHandlers() {
    super();
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



  @XmlElement(name="init",required=true,nillable=false)
  private List<ConfigurazioneHandler> init = new ArrayList<>();

  /**
   * Use method getInitList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  public List<ConfigurazioneHandler> getInit() {
  	return this.getInitList();
  }

  /**
   * Use method setInitList
   * @param init List&lt;ConfigurazioneHandler&gt;
  */
  public void setInit(List<ConfigurazioneHandler> init) {
  	this.setInitList(init);
  }

  /**
   * Use method sizeInitList
   * @return lunghezza della lista
  */
  public int sizeInit() {
  	return this.sizeInitList();
  }

  @XmlElement(name="exit",required=true,nillable=false)
  private List<ConfigurazioneHandler> exit = new ArrayList<>();

  /**
   * Use method getExitList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  public List<ConfigurazioneHandler> getExit() {
  	return this.getExitList();
  }

  /**
   * Use method setExitList
   * @param exit List&lt;ConfigurazioneHandler&gt;
  */
  public void setExit(List<ConfigurazioneHandler> exit) {
  	this.setExitList(exit);
  }

  /**
   * Use method sizeExitList
   * @return lunghezza della lista
  */
  public int sizeExit() {
  	return this.sizeExitList();
  }

  @XmlElement(name="integrationManagerRequest",required=true,nillable=false)
  private List<ConfigurazioneHandler> integrationManagerRequest = new ArrayList<>();

  /**
   * Use method getIntegrationManagerRequestList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  public List<ConfigurazioneHandler> getIntegrationManagerRequest() {
  	return this.getIntegrationManagerRequestList();
  }

  /**
   * Use method setIntegrationManagerRequestList
   * @param integrationManagerRequest List&lt;ConfigurazioneHandler&gt;
  */
  public void setIntegrationManagerRequest(List<ConfigurazioneHandler> integrationManagerRequest) {
  	this.setIntegrationManagerRequestList(integrationManagerRequest);
  }

  /**
   * Use method sizeIntegrationManagerRequestList
   * @return lunghezza della lista
  */
  public int sizeIntegrationManagerRequest() {
  	return this.sizeIntegrationManagerRequestList();
  }

  @XmlElement(name="integrationManagerResponse",required=true,nillable=false)
  private List<ConfigurazioneHandler> integrationManagerResponse = new ArrayList<>();

  /**
   * Use method getIntegrationManagerResponseList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  public List<ConfigurazioneHandler> getIntegrationManagerResponse() {
  	return this.getIntegrationManagerResponseList();
  }

  /**
   * Use method setIntegrationManagerResponseList
   * @param integrationManagerResponse List&lt;ConfigurazioneHandler&gt;
  */
  public void setIntegrationManagerResponse(List<ConfigurazioneHandler> integrationManagerResponse) {
  	this.setIntegrationManagerResponseList(integrationManagerResponse);
  }

  /**
   * Use method sizeIntegrationManagerResponseList
   * @return lunghezza della lista
  */
  public int sizeIntegrationManagerResponse() {
  	return this.sizeIntegrationManagerResponseList();
  }

}
