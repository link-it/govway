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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for eccezioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezioni">
 * 		&lt;sequence>
 * 			&lt;element name="exception" type="{http://govway.org/integration/fault/details}eccezione" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "eccezioni", 
  propOrder = {
  	"exception"
  }
)

@XmlRootElement(name = "eccezioni")

public class Eccezioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezioni() {
  }

  public void addException(Eccezione exception) {
    this.exception.add(exception);
  }

  public Eccezione getException(int index) {
    return this.exception.get( index );
  }

  public Eccezione removeException(int index) {
    return this.exception.remove( index );
  }

  public List<Eccezione> getExceptionList() {
    return this.exception;
  }

  public void setExceptionList(List<Eccezione> exception) {
    this.exception=exception;
  }

  public int sizeExceptionList() {
    return this.exception.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="exception",required=true,nillable=false)
  protected List<Eccezione> exception = new ArrayList<Eccezione>();

  /**
   * @deprecated Use method getExceptionList
   * @return List<Eccezione>
  */
  @Deprecated
  public List<Eccezione> getException() {
  	return this.exception;
  }

  /**
   * @deprecated Use method setExceptionList
   * @param exception List<Eccezione>
  */
  @Deprecated
  public void setException(List<Eccezione> exception) {
  	this.exception=exception;
  }

  /**
   * @deprecated Use method sizeExceptionList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeException() {
  	return this.exception.size();
  }

}
