/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package it.cnipa.collprofiles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operationListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationListType">
 * 		&lt;sequence>
 * 			&lt;element name="operation" type="{http://www.cnipa.it/collProfiles}operationType" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "operationListType", 
  propOrder = {
  	"operation"
  }
)

@XmlRootElement(name = "operationListType")

public class OperationListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OperationListType() {
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
  protected List<OperationType> operation = new ArrayList<OperationType>();

  /**
   * @deprecated Use method getOperationList
   * @return List<OperationType>
  */
  @Deprecated
  public List<OperationType> getOperation() {
  	return this.operation;
  }

  /**
   * @deprecated Use method setOperationList
   * @param operation List<OperationType>
  */
  @Deprecated
  public void setOperation(List<OperationType> operation) {
  	this.operation=operation;
  }

  /**
   * @deprecated Use method sizeOperationList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeOperation() {
  	return this.operation.size();
  }

}
