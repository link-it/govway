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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for soapHeaderBypassMustUnderstand complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soapHeaderBypassMustUnderstand">
 * 		&lt;sequence>
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/protocol/manifest}soapHeaderBypassMustUnderstandHeader" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "soapHeaderBypassMustUnderstand", 
  propOrder = {
  	"header"
  }
)

@XmlRootElement(name = "soapHeaderBypassMustUnderstand")

public class SoapHeaderBypassMustUnderstand extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapHeaderBypassMustUnderstand() {
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

  public void addHeader(SoapHeaderBypassMustUnderstandHeader header) {
    this.header.add(header);
  }

  public SoapHeaderBypassMustUnderstandHeader getHeader(int index) {
    return this.header.get( index );
  }

  public SoapHeaderBypassMustUnderstandHeader removeHeader(int index) {
    return this.header.remove( index );
  }

  public List<SoapHeaderBypassMustUnderstandHeader> getHeaderList() {
    return this.header;
  }

  public void setHeaderList(List<SoapHeaderBypassMustUnderstandHeader> header) {
    this.header=header;
  }

  public int sizeHeaderList() {
    return this.header.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="header",required=true,nillable=false)
  protected List<SoapHeaderBypassMustUnderstandHeader> header = new ArrayList<SoapHeaderBypassMustUnderstandHeader>();

  /**
   * @deprecated Use method getHeaderList
   * @return List<SoapHeaderBypassMustUnderstandHeader>
  */
  @Deprecated
  public List<SoapHeaderBypassMustUnderstandHeader> getHeader() {
  	return this.header;
  }

  /**
   * @deprecated Use method setHeaderList
   * @param header List<SoapHeaderBypassMustUnderstandHeader>
  */
  @Deprecated
  public void setHeader(List<SoapHeaderBypassMustUnderstandHeader> header) {
  	this.header=header;
  }

  /**
   * @deprecated Use method sizeHeaderList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeHeader() {
  	return this.header.size();
  }

}
