/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for mpc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mpc"&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="retention_downloaded" type="{http://www.w3.org/2001/XMLSchema}integer" use="required"/&gt;
 * 		&lt;attribute name="retention_undownloaded" type="{http://www.w3.org/2001/XMLSchema}integer" use="required"/&gt;
 * 		&lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="qualifiedName" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mpc")

@XmlRootElement(name = "mpc")

public class Mpc extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Mpc() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.math.BigInteger getRetentionDownloaded() {
    return this.retentionDownloaded;
  }

  public void setRetentionDownloaded(java.math.BigInteger retentionDownloaded) {
    this.retentionDownloaded = retentionDownloaded;
  }

  public java.math.BigInteger getRetentionUndownloaded() {
    return this.retentionUndownloaded;
  }

  public void setRetentionUndownloaded(java.math.BigInteger retentionUndownloaded) {
    this.retentionUndownloaded = retentionUndownloaded;
  }

  public boolean isDefault() {
    return this._default;
  }

  public boolean getDefault() {
    return this._default;
  }

  public void setDefault(boolean _default) {
    this._default = _default;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public java.lang.String getQualifiedName() {
    return this.qualifiedName;
  }

  public void setQualifiedName(java.lang.String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="retention_downloaded",required=true)
  protected java.math.BigInteger retentionDownloaded;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="retention_undownloaded",required=true)
  protected java.math.BigInteger retentionUndownloaded;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="default",required=true)
  protected boolean _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="enabled",required=true)
  protected boolean enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="qualifiedName",required=true)
  protected java.lang.String qualifiedName;

}
