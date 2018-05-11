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
package org.openspcoop2.web.lib.audit.log;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operation">
 * 		&lt;sequence>
 * 			&lt;element name="object_details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="binary" type="{http://www.openspcoop2.org/web/lib/audit/log}binary" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="tipologia" type="{http://www.openspcoop2.org/web/lib/audit/log}tipologia" use="required"/>
 * 		&lt;attribute name="tipo-oggetto" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="object-id" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="object-old-id" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="utente" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/web/lib/audit/log}stato" use="required"/>
 * 		&lt;attribute name="object_class" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="error" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="time-request" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/>
 * 		&lt;attribute name="time-execute" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/>
 * 		&lt;attribute name="interface-msg" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operation", 
  propOrder = {
  	"objectDetails",
  	"binary"
  }
)

@XmlRootElement(name = "operation")

public class Operation extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Operation() {
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

  public java.lang.String getObjectDetails() {
    return this.objectDetails;
  }

  public void setObjectDetails(java.lang.String objectDetails) {
    this.objectDetails = objectDetails;
  }

  public void addBinary(Binary binary) {
    this.binary.add(binary);
  }

  public Binary getBinary(int index) {
    return this.binary.get( index );
  }

  public Binary removeBinary(int index) {
    return this.binary.remove( index );
  }

  public List<Binary> getBinaryList() {
    return this.binary;
  }

  public void setBinaryList(List<Binary> binary) {
    this.binary=binary;
  }

  public int sizeBinaryList() {
    return this.binary.size();
  }

  public void set_value_tipologia(String value) {
    this.tipologia = (Tipologia) Tipologia.toEnumConstantFromString(value);
  }

  public String get_value_tipologia() {
    if(this.tipologia == null){
    	return null;
    }else{
    	return this.tipologia.toString();
    }
  }

  public org.openspcoop2.web.lib.audit.log.constants.Tipologia getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(org.openspcoop2.web.lib.audit.log.constants.Tipologia tipologia) {
    this.tipologia = tipologia;
  }

  public java.lang.String getTipoOggetto() {
    return this.tipoOggetto;
  }

  public void setTipoOggetto(java.lang.String tipoOggetto) {
    this.tipoOggetto = tipoOggetto;
  }

  public java.lang.String getObjectId() {
    return this.objectId;
  }

  public void setObjectId(java.lang.String objectId) {
    this.objectId = objectId;
  }

  public java.lang.String getObjectOldId() {
    return this.objectOldId;
  }

  public void setObjectOldId(java.lang.String objectOldId) {
    this.objectOldId = objectOldId;
  }

  public java.lang.String getUtente() {
    return this.utente;
  }

  public void setUtente(java.lang.String utente) {
    this.utente = utente;
  }

  public void set_value_stato(String value) {
    this.stato = (Stato) Stato.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.web.lib.audit.log.constants.Stato getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.web.lib.audit.log.constants.Stato stato) {
    this.stato = stato;
  }

  public java.lang.String getObjectClass() {
    return this.objectClass;
  }

  public void setObjectClass(java.lang.String objectClass) {
    this.objectClass = objectClass;
  }

  public java.lang.String getError() {
    return this.error;
  }

  public void setError(java.lang.String error) {
    this.error = error;
  }

  public java.util.Date getTimeRequest() {
    return this.timeRequest;
  }

  public void setTimeRequest(java.util.Date timeRequest) {
    this.timeRequest = timeRequest;
  }

  public java.util.Date getTimeExecute() {
    return this.timeExecute;
  }

  public void setTimeExecute(java.util.Date timeExecute) {
    this.timeExecute = timeExecute;
  }

  public java.lang.String getInterfaceMsg() {
    return this.interfaceMsg;
  }

  public void setInterfaceMsg(java.lang.String interfaceMsg) {
    this.interfaceMsg = interfaceMsg;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.web.lib.audit.log.model.OperationModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.web.lib.audit.log.Operation.modelStaticInstance==null){
  			org.openspcoop2.web.lib.audit.log.Operation.modelStaticInstance = new org.openspcoop2.web.lib.audit.log.model.OperationModel();
	  }
  }
  public static org.openspcoop2.web.lib.audit.log.model.OperationModel model(){
	  if(org.openspcoop2.web.lib.audit.log.Operation.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.web.lib.audit.log.Operation.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="object_details",required=false,nillable=false)
  protected java.lang.String objectDetails;

  @XmlElement(name="binary",required=true,nillable=false)
  protected List<Binary> binary = new ArrayList<Binary>();

  /**
   * @deprecated Use method getBinaryList
   * @return List<Binary>
  */
  @Deprecated
  public List<Binary> getBinary() {
  	return this.binary;
  }

  /**
   * @deprecated Use method setBinaryList
   * @param binary List<Binary>
  */
  @Deprecated
  public void setBinary(List<Binary> binary) {
  	this.binary=binary;
  }

  /**
   * @deprecated Use method sizeBinaryList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeBinary() {
  	return this.binary.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipologia;

  @XmlAttribute(name="tipologia",required=true)
  protected Tipologia tipologia;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-oggetto",required=false)
  protected java.lang.String tipoOggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object-id",required=false)
  protected java.lang.String objectId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object-old-id",required=false)
  protected java.lang.String objectOldId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="utente",required=true)
  protected java.lang.String utente;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=true)
  protected Stato stato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object_class",required=false)
  protected java.lang.String objectClass;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="error",required=false)
  protected java.lang.String error;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="time-request",required=true)
  protected java.util.Date timeRequest;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="time-execute",required=true)
  protected java.util.Date timeExecute;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="interface-msg",required=false)
  protected java.lang.String interfaceMsg;

}
