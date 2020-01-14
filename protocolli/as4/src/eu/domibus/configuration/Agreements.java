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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for agreements complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="agreements">
 * 		&lt;sequence>
 * 			&lt;element name="agreement" type="{http://www.domibus.eu/configuration}agreement" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "agreements", 
  propOrder = {
  	"agreement"
  }
)

@XmlRootElement(name = "agreements")

public class Agreements extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Agreements() {
  }

  public void addAgreement(Agreement agreement) {
    this.agreement.add(agreement);
  }

  public Agreement getAgreement(int index) {
    return this.agreement.get( index );
  }

  public Agreement removeAgreement(int index) {
    return this.agreement.remove( index );
  }

  public List<Agreement> getAgreementList() {
    return this.agreement;
  }

  public void setAgreementList(List<Agreement> agreement) {
    this.agreement=agreement;
  }

  public int sizeAgreementList() {
    return this.agreement.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="agreement",required=true,nillable=false)
  protected List<Agreement> agreement = new ArrayList<Agreement>();

  /**
   * @deprecated Use method getAgreementList
   * @return List<Agreement>
  */
  @Deprecated
  public List<Agreement> getAgreement() {
  	return this.agreement;
  }

  /**
   * @deprecated Use method setAgreementList
   * @param agreement List<Agreement>
  */
  @Deprecated
  public void setAgreement(List<Agreement> agreement) {
  	this.agreement=agreement;
  }

  /**
   * @deprecated Use method sizeAgreementList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAgreement() {
  	return this.agreement.size();
  }

}
