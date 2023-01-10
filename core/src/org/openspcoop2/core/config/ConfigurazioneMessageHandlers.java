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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-message-handlers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-message-handlers"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="pre-in" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="in" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="inProtocolInfo" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="out" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="postOut" type="{http://www.openspcoop2.org/core/config}configurazione-handler" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "configurazione-message-handlers", 
  propOrder = {
  	"preIn",
  	"in",
  	"inProtocolInfo",
  	"out",
  	"postOut"
  }
)

@XmlRootElement(name = "configurazione-message-handlers")

public class ConfigurazioneMessageHandlers extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneMessageHandlers() {
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

  public void addPreIn(ConfigurazioneHandler preIn) {
    this.preIn.add(preIn);
  }

  public ConfigurazioneHandler getPreIn(int index) {
    return this.preIn.get( index );
  }

  public ConfigurazioneHandler removePreIn(int index) {
    return this.preIn.remove( index );
  }

  public List<ConfigurazioneHandler> getPreInList() {
    return this.preIn;
  }

  public void setPreInList(List<ConfigurazioneHandler> preIn) {
    this.preIn=preIn;
  }

  public int sizePreInList() {
    return this.preIn.size();
  }

  public void addIn(ConfigurazioneHandler in) {
    this.in.add(in);
  }

  public ConfigurazioneHandler getIn(int index) {
    return this.in.get( index );
  }

  public ConfigurazioneHandler removeIn(int index) {
    return this.in.remove( index );
  }

  public List<ConfigurazioneHandler> getInList() {
    return this.in;
  }

  public void setInList(List<ConfigurazioneHandler> in) {
    this.in=in;
  }

  public int sizeInList() {
    return this.in.size();
  }

  public void addInProtocolInfo(ConfigurazioneHandler inProtocolInfo) {
    this.inProtocolInfo.add(inProtocolInfo);
  }

  public ConfigurazioneHandler getInProtocolInfo(int index) {
    return this.inProtocolInfo.get( index );
  }

  public ConfigurazioneHandler removeInProtocolInfo(int index) {
    return this.inProtocolInfo.remove( index );
  }

  public List<ConfigurazioneHandler> getInProtocolInfoList() {
    return this.inProtocolInfo;
  }

  public void setInProtocolInfoList(List<ConfigurazioneHandler> inProtocolInfo) {
    this.inProtocolInfo=inProtocolInfo;
  }

  public int sizeInProtocolInfoList() {
    return this.inProtocolInfo.size();
  }

  public void addOut(ConfigurazioneHandler out) {
    this.out.add(out);
  }

  public ConfigurazioneHandler getOut(int index) {
    return this.out.get( index );
  }

  public ConfigurazioneHandler removeOut(int index) {
    return this.out.remove( index );
  }

  public List<ConfigurazioneHandler> getOutList() {
    return this.out;
  }

  public void setOutList(List<ConfigurazioneHandler> out) {
    this.out=out;
  }

  public int sizeOutList() {
    return this.out.size();
  }

  public void addPostOut(ConfigurazioneHandler postOut) {
    this.postOut.add(postOut);
  }

  public ConfigurazioneHandler getPostOut(int index) {
    return this.postOut.get( index );
  }

  public ConfigurazioneHandler removePostOut(int index) {
    return this.postOut.remove( index );
  }

  public List<ConfigurazioneHandler> getPostOutList() {
    return this.postOut;
  }

  public void setPostOutList(List<ConfigurazioneHandler> postOut) {
    this.postOut=postOut;
  }

  public int sizePostOutList() {
    return this.postOut.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="pre-in",required=true,nillable=false)
  protected List<ConfigurazioneHandler> preIn = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getPreInList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getPreIn() {
  	return this.preIn;
  }

  /**
   * @deprecated Use method setPreInList
   * @param preIn List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setPreIn(List<ConfigurazioneHandler> preIn) {
  	this.preIn=preIn;
  }

  /**
   * @deprecated Use method sizePreInList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePreIn() {
  	return this.preIn.size();
  }

  @XmlElement(name="in",required=true,nillable=false)
  protected List<ConfigurazioneHandler> in = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getInList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getIn() {
  	return this.in;
  }

  /**
   * @deprecated Use method setInList
   * @param in List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setIn(List<ConfigurazioneHandler> in) {
  	this.in=in;
  }

  /**
   * @deprecated Use method sizeInList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIn() {
  	return this.in.size();
  }

  @XmlElement(name="inProtocolInfo",required=true,nillable=false)
  protected List<ConfigurazioneHandler> inProtocolInfo = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getInProtocolInfoList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getInProtocolInfo() {
  	return this.inProtocolInfo;
  }

  /**
   * @deprecated Use method setInProtocolInfoList
   * @param inProtocolInfo List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setInProtocolInfo(List<ConfigurazioneHandler> inProtocolInfo) {
  	this.inProtocolInfo=inProtocolInfo;
  }

  /**
   * @deprecated Use method sizeInProtocolInfoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeInProtocolInfo() {
  	return this.inProtocolInfo.size();
  }

  @XmlElement(name="out",required=true,nillable=false)
  protected List<ConfigurazioneHandler> out = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getOutList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getOut() {
  	return this.out;
  }

  /**
   * @deprecated Use method setOutList
   * @param out List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setOut(List<ConfigurazioneHandler> out) {
  	this.out=out;
  }

  /**
   * @deprecated Use method sizeOutList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeOut() {
  	return this.out.size();
  }

  @XmlElement(name="postOut",required=true,nillable=false)
  protected List<ConfigurazioneHandler> postOut = new ArrayList<ConfigurazioneHandler>();

  /**
   * @deprecated Use method getPostOutList
   * @return List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public List<ConfigurazioneHandler> getPostOut() {
  	return this.postOut;
  }

  /**
   * @deprecated Use method setPostOutList
   * @param postOut List&lt;ConfigurazioneHandler&gt;
  */
  @Deprecated
  public void setPostOut(List<ConfigurazioneHandler> postOut) {
  	this.postOut=postOut;
  }

  /**
   * @deprecated Use method sizePostOutList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePostOut() {
  	return this.postOut.size();
  }

}
