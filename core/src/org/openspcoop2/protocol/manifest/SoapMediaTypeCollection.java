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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SoapMediaTypeCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoapMediaTypeCollection"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="mediaType" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeMapping" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeDefaultMapping" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="undefined" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeUndefinedMapping" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "SoapMediaTypeCollection", 
  propOrder = {
  	"mediaType",
  	"_default",
  	"undefined"
  }
)

@XmlRootElement(name = "SoapMediaTypeCollection")

public class SoapMediaTypeCollection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapMediaTypeCollection() {
    super();
  }

  public void addMediaType(SoapMediaTypeMapping mediaType) {
    this.mediaType.add(mediaType);
  }

  public SoapMediaTypeMapping getMediaType(int index) {
    return this.mediaType.get( index );
  }

  public SoapMediaTypeMapping removeMediaType(int index) {
    return this.mediaType.remove( index );
  }

  public List<SoapMediaTypeMapping> getMediaTypeList() {
    return this.mediaType;
  }

  public void setMediaTypeList(List<SoapMediaTypeMapping> mediaType) {
    this.mediaType=mediaType;
  }

  public int sizeMediaTypeList() {
    return this.mediaType.size();
  }

  public SoapMediaTypeDefaultMapping getDefault() {
    return this._default;
  }

  public void setDefault(SoapMediaTypeDefaultMapping _default) {
    this._default = _default;
  }

  public SoapMediaTypeUndefinedMapping getUndefined() {
    return this.undefined;
  }

  public void setUndefined(SoapMediaTypeUndefinedMapping undefined) {
    this.undefined = undefined;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="mediaType",required=true,nillable=false)
  private List<SoapMediaTypeMapping> mediaType = new ArrayList<>();

  /**
   * Use method getMediaTypeList
   * @return List&lt;SoapMediaTypeMapping&gt;
  */
  public List<SoapMediaTypeMapping> getMediaType() {
  	return this.getMediaTypeList();
  }

  /**
   * Use method setMediaTypeList
   * @param mediaType List&lt;SoapMediaTypeMapping&gt;
  */
  public void setMediaType(List<SoapMediaTypeMapping> mediaType) {
  	this.setMediaTypeList(mediaType);
  }

  /**
   * Use method sizeMediaTypeList
   * @return lunghezza della lista
  */
  public int sizeMediaType() {
  	return this.sizeMediaTypeList();
  }

  @XmlElement(name="default",required=false,nillable=false)
  protected SoapMediaTypeDefaultMapping _default;

  @XmlElement(name="undefined",required=false,nillable=false)
  protected SoapMediaTypeUndefinedMapping undefined;

}
