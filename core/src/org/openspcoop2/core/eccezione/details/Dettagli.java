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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dettagli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettagli"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="detail" type="{http://govway.org/integration/fault/details}dettaglio" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "dettagli", 
  propOrder = {
  	"detail"
  }
)

@XmlRootElement(name = "dettagli")

public class Dettagli extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Dettagli() {
    super();
  }

  public void addDetail(Dettaglio detail) {
    this.detail.add(detail);
  }

  public Dettaglio getDetail(int index) {
    return this.detail.get( index );
  }

  public Dettaglio removeDetail(int index) {
    return this.detail.remove( index );
  }

  public List<Dettaglio> getDetailList() {
    return this.detail;
  }

  public void setDetailList(List<Dettaglio> detail) {
    this.detail=detail;
  }

  public int sizeDetailList() {
    return this.detail.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="detail",required=true,nillable=false)
  private List<Dettaglio> detail = new ArrayList<>();

}
