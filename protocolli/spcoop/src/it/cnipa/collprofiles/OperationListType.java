/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package it.cnipa.collprofiles;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operationListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationListType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="operation" type="{http://www.cnipa.it/collProfiles}operationType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "operationListType", 
  propOrder = {
  	"operation"
  }
)

@XmlRootElement(name = "operationListType")

public class OperationListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OperationListType() {
    super();
  }

  public void addOperation(OperationType operation) {
    this.operation.add(operation);
  }

  public OperationType getOperation(int index) {
    return this.operation.get( index );
  }

  public OperationType removeOperation(int index) {
    return this.operation.remove( index );
  }

  public List<OperationType> getOperationList() {
    return this.operation;
  }

  public void setOperationList(List<OperationType> operation) {
    this.operation=operation;
  }

  public int sizeOperationList() {
    return this.operation.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="operation",required=true,nillable=false)
  private List<OperationType> operation = new ArrayList<>();

  /**
   * Use method getOperationList
   * @return List&lt;OperationType&gt;
  */
  public List<OperationType> getOperation() {
  	return this.getOperationList();
  }

  /**
   * Use method setOperationList
   * @param operation List&lt;OperationType&gt;
  */
  public void setOperation(List<OperationType> operation) {
  	this.setOperationList(operation);
  }

  /**
   * Use method sizeOperationList
   * @return lunghezza della lista
  */
  public int sizeOperation() {
  	return this.sizeOperationList();
  }

}
