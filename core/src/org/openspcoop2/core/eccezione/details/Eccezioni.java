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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for eccezioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezioni"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="exception" type="{http://govway.org/integration/fault/details}eccezione" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "eccezioni", 
  propOrder = {
  	"exception"
  }
)

@XmlRootElement(name = "eccezioni")

public class Eccezioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezioni() {
    super();
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
  private List<Eccezione> exception = new ArrayList<>();

  /**
   * Use method getExceptionList
   * @return List&lt;Eccezione&gt;
  */
  public List<Eccezione> getException() {
  	return this.getExceptionList();
  }

  /**
   * Use method setExceptionList
   * @param exception List&lt;Eccezione&gt;
  */
  public void setException(List<Eccezione> exception) {
  	this.setExceptionList(exception);
  }

  /**
   * Use method sizeExceptionList
   * @return lunghezza della lista
  */
  public int sizeException() {
  	return this.sizeExceptionList();
  }

}
