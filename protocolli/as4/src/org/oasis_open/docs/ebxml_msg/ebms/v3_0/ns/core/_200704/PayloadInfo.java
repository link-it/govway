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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for PayloadInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayloadInfo">
 * 		&lt;sequence>
 * 			&lt;element name="PartInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartInfo" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "PayloadInfo", 
  propOrder = {
  	"partInfo"
  }
)

@XmlRootElement(name = "PayloadInfo")

public class PayloadInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PayloadInfo() {
  }

  public void addPartInfo(PartInfo partInfo) {
    this.partInfo.add(partInfo);
  }

  public PartInfo getPartInfo(int index) {
    return this.partInfo.get( index );
  }

  public PartInfo removePartInfo(int index) {
    return this.partInfo.remove( index );
  }

  public List<PartInfo> getPartInfoList() {
    return this.partInfo;
  }

  public void setPartInfoList(List<PartInfo> partInfo) {
    this.partInfo=partInfo;
  }

  public int sizePartInfoList() {
    return this.partInfo.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="PartInfo",required=true,nillable=false)
  protected List<PartInfo> partInfo = new ArrayList<PartInfo>();

  /**
   * @deprecated Use method getPartInfoList
   * @return List<PartInfo>
  */
  @Deprecated
  public List<PartInfo> getPartInfo() {
  	return this.partInfo;
  }

  /**
   * @deprecated Use method setPartInfoList
   * @param partInfo List<PartInfo>
  */
  @Deprecated
  public void setPartInfo(List<PartInfo> partInfo) {
  	this.partInfo=partInfo;
  }

  /**
   * @deprecated Use method sizePartInfoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePartInfo() {
  	return this.partInfo.size();
  }

}
