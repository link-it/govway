/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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


/** <p>Java class for RestMediaTypeCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RestMediaTypeCollection"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="mediaType" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeMapping" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeDefaultMapping" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="undefined" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeUndefinedMapping" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "RestMediaTypeCollection", 
  propOrder = {
  	"mediaType",
  	"_default",
  	"undefined"
  }
)

@XmlRootElement(name = "RestMediaTypeCollection")

public class RestMediaTypeCollection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RestMediaTypeCollection() {
    super();
  }

  public void addMediaType(RestMediaTypeMapping mediaType) {
    this.mediaType.add(mediaType);
  }

  public RestMediaTypeMapping getMediaType(int index) {
    return this.mediaType.get( index );
  }

  public RestMediaTypeMapping removeMediaType(int index) {
    return this.mediaType.remove( index );
  }

  public List<RestMediaTypeMapping> getMediaTypeList() {
    return this.mediaType;
  }

  public void setMediaTypeList(List<RestMediaTypeMapping> mediaType) {
    this.mediaType=mediaType;
  }

  public int sizeMediaTypeList() {
    return this.mediaType.size();
  }

  public RestMediaTypeDefaultMapping getDefault() {
    return this._default;
  }

  public void setDefault(RestMediaTypeDefaultMapping _default) {
    this._default = _default;
  }

  public RestMediaTypeUndefinedMapping getUndefined() {
    return this.undefined;
  }

  public void setUndefined(RestMediaTypeUndefinedMapping undefined) {
    this.undefined = undefined;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="mediaType",required=true,nillable=false)
  private List<RestMediaTypeMapping> mediaType = new ArrayList<>();

  /**
   * Use method getMediaTypeList
   * @return List&lt;RestMediaTypeMapping&gt;
  */
  public List<RestMediaTypeMapping> getMediaType() {
  	return this.getMediaTypeList();
  }

  /**
   * Use method setMediaTypeList
   * @param mediaType List&lt;RestMediaTypeMapping&gt;
  */
  public void setMediaType(List<RestMediaTypeMapping> mediaType) {
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
  protected RestMediaTypeDefaultMapping _default;

  @XmlElement(name="undefined",required=false,nillable=false)
  protected RestMediaTypeUndefinedMapping undefined;

}
