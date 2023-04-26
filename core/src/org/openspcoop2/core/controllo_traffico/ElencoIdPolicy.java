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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-id-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-id-policy"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-policy" type="{http://www.openspcoop2.org/core/controllo_traffico}id-policy" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-id-policy", 
  propOrder = {
  	"idPolicy"
  }
)

@XmlRootElement(name = "elenco-id-policy")

public class ElencoIdPolicy extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ElencoIdPolicy() {
    super();
  }

  public void addIdPolicy(IdPolicy idPolicy) {
    this.idPolicy.add(idPolicy);
  }

  public IdPolicy getIdPolicy(int index) {
    return this.idPolicy.get( index );
  }

  public IdPolicy removeIdPolicy(int index) {
    return this.idPolicy.remove( index );
  }

  public List<IdPolicy> getIdPolicyList() {
    return this.idPolicy;
  }

  public void setIdPolicyList(List<IdPolicy> idPolicy) {
    this.idPolicy=idPolicy;
  }

  public int sizeIdPolicyList() {
    return this.idPolicy.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="id-policy",required=true,nillable=false)
  private List<IdPolicy> idPolicy = new ArrayList<>();

}
