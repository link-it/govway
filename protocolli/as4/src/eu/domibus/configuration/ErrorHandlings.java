/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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


/** <p>Java class for errorHandlings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="errorHandlings">
 * 		&lt;sequence>
 * 			&lt;element name="errorHandling" type="{http://www.domibus.eu/configuration}errorHandling" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "errorHandlings", 
  propOrder = {
  	"errorHandling"
  }
)

@XmlRootElement(name = "errorHandlings")

public class ErrorHandlings extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ErrorHandlings() {
  }

  public void addErrorHandling(ErrorHandling errorHandling) {
    this.errorHandling.add(errorHandling);
  }

  public ErrorHandling getErrorHandling(int index) {
    return this.errorHandling.get( index );
  }

  public ErrorHandling removeErrorHandling(int index) {
    return this.errorHandling.remove( index );
  }

  public List<ErrorHandling> getErrorHandlingList() {
    return this.errorHandling;
  }

  public void setErrorHandlingList(List<ErrorHandling> errorHandling) {
    this.errorHandling=errorHandling;
  }

  public int sizeErrorHandlingList() {
    return this.errorHandling.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="errorHandling",required=true,nillable=false)
  protected List<ErrorHandling> errorHandling = new ArrayList<ErrorHandling>();

  /**
   * @deprecated Use method getErrorHandlingList
   * @return List<ErrorHandling>
  */
  @Deprecated
  public List<ErrorHandling> getErrorHandling() {
  	return this.errorHandling;
  }

  /**
   * @deprecated Use method setErrorHandlingList
   * @param errorHandling List<ErrorHandling>
  */
  @Deprecated
  public void setErrorHandling(List<ErrorHandling> errorHandling) {
  	this.errorHandling=errorHandling;
  }

  /**
   * @deprecated Use method sizeErrorHandlingList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeErrorHandling() {
  	return this.errorHandling.size();
  }

}
