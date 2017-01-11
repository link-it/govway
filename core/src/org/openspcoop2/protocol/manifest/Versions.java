/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Versions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Versions">
 * 		&lt;sequence>
 * 			&lt;element name="version" type="{http://www.openspcoop2.org/protocol/manifest}Version" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Versions", 
  propOrder = {
  	"version"
  }
)

@XmlRootElement(name = "Versions")

public class Versions extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Versions() {
  }

  public void addVersion(Version version) {
    this.version.add(version);
  }

  public Version getVersion(int index) {
    return this.version.get( index );
  }

  public Version removeVersion(int index) {
    return this.version.remove( index );
  }

  public List<Version> getVersionList() {
    return this.version;
  }

  public void setVersionList(List<Version> version) {
    this.version=version;
  }

  public int sizeVersionList() {
    return this.version.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="version",required=true,nillable=false)
  protected List<Version> version = new ArrayList<Version>();

  /**
   * @deprecated Use method getVersionList
   * @return List<Version>
  */
  @Deprecated
  public List<Version> getVersion() {
  	return this.version;
  }

  /**
   * @deprecated Use method setVersionList
   * @param version List<Version>
  */
  @Deprecated
  public void setVersion(List<Version> version) {
  	this.version=version;
  }

  /**
   * @deprecated Use method sizeVersionList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeVersion() {
  	return this.version.size();
  }

}
