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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for port-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="port-type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="operation" type="{http://www.openspcoop2.org/core/commons/search}operation" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="id-accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/core/commons/search}id-accordo-servizio-parte-comune" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "port-type", 
  propOrder = {
  	"nome",
  	"operation",
  	"idAccordoServizioParteComune"
  }
)

@XmlRootElement(name = "port-type")

public class PortType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortType() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void addOperation(Operation operation) {
    this.operation.add(operation);
  }

  public Operation getOperation(int index) {
    return this.operation.get( index );
  }

  public Operation removeOperation(int index) {
    return this.operation.remove( index );
  }

  public List<Operation> getOperationList() {
    return this.operation;
  }

  public void setOperationList(List<Operation> operation) {
    this.operation=operation;
  }

  public int sizeOperationList() {
    return this.operation.size();
  }

  public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
    return this.idAccordoServizioParteComune;
  }

  public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune idAccordoServizioParteComune) {
    this.idAccordoServizioParteComune = idAccordoServizioParteComune;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.PortTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.PortType.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.PortType.modelStaticInstance = new org.openspcoop2.core.commons.search.model.PortTypeModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.PortTypeModel model(){
	  if(org.openspcoop2.core.commons.search.PortType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.PortType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @XmlElement(name="operation",required=true,nillable=false)
  protected List<Operation> operation = new ArrayList<Operation>();

  /**
   * @deprecated Use method getOperationList
   * @return List&lt;Operation&gt;
  */
  @Deprecated
  public List<Operation> getOperation() {
  	return this.operation;
  }

  /**
   * @deprecated Use method setOperationList
   * @param operation List&lt;Operation&gt;
  */
  @Deprecated
  public void setOperation(List<Operation> operation) {
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

  @XmlElement(name="id-accordo-servizio-parte-comune",required=true,nillable=false)
  protected IdAccordoServizioParteComune idAccordoServizioParteComune;

}
