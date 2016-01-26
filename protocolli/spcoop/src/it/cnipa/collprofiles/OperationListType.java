/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.cnipa.collprofiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class OperationListType.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class OperationListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public OperationListType() {
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

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

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

  public static final String OPERATION = "operation";

}
