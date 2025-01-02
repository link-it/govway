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
package org.openspcoop2.web.lib.audit.log;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
 * &lt;complexType name="operation"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="object_details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="binary" type="{http://www.openspcoop2.org/web/lib/audit/log}binary" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="tipologia" type="{http://www.openspcoop2.org/web/lib/audit/log}tipologia" use="required"/&gt;
 * 		&lt;attribute name="tipo-oggetto" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="object-id" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="object-old-id" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="utente" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/web/lib/audit/log}stato" use="required"/&gt;
 * 		&lt;attribute name="object_class" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="error" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="time-request" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * 		&lt;attribute name="time-execute" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * 		&lt;attribute name="interface-msg" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
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

public class Operation extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Operation() {
    super();
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

  public void setTipologiaRawEnumValue(String value) {
    this.tipologia = (Tipologia) Tipologia.toEnumConstantFromString(value);
  }

  public String getTipologiaRawEnumValue() {
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

  public void setStatoRawEnumValue(String value) {
    this.stato = (Stato) Stato.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
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


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="object_details",required=false,nillable=false)
  protected java.lang.String objectDetails;

  @XmlElement(name="binary",required=true,nillable=false)
  private List<Binary> binary = new ArrayList<>();

  /**
   * Use method getBinaryList
   * @return List&lt;Binary&gt;
  */
  public List<Binary> getBinary() {
  	return this.getBinaryList();
  }

  /**
   * Use method setBinaryList
   * @param binary List&lt;Binary&gt;
  */
  public void setBinary(List<Binary> binary) {
  	this.setBinaryList(binary);
  }

  /**
   * Use method sizeBinaryList
   * @return lunghezza della lista
  */
  public int sizeBinary() {
  	return this.sizeBinaryList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipologiaRawEnumValue;

  @XmlAttribute(name="tipologia",required=true)
  protected Tipologia tipologia;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-oggetto",required=false)
  protected java.lang.String tipoOggetto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object-id",required=false)
  protected java.lang.String objectId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object-old-id",required=false)
  protected java.lang.String objectOldId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="utente",required=true)
  protected java.lang.String utente;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=true)
  protected Stato stato;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="object_class",required=false)
  protected java.lang.String objectClass;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="error",required=false)
  protected java.lang.String error;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="time-request",required=true)
  protected java.util.Date timeRequest;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="time-execute",required=true)
  protected java.util.Date timeExecute;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="interface-msg",required=false)
  protected java.lang.String interfaceMsg;

}
