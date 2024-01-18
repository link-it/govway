/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.constants.DeleteState;
import org.openspcoop2.core.transazioni.constants.ExportState;
import java.io.Serializable;


/** <p>Java class for transazione-export complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazione-export"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="intervallo-inizio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="intervallo-fine" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="export-state" type="{http://www.openspcoop2.org/core/transazioni}export-state" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="export-error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="export-time-start" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="export-time-end" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="delete-state" type="{http://www.openspcoop2.org/core/transazioni}delete-state" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="delete-error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="delete-time-start" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="delete-time-end" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "transazione-export", 
  propOrder = {
  	"intervalloInizio",
  	"intervalloFine",
  	"nome",
  	"exportState",
  	"exportError",
  	"exportTimeStart",
  	"exportTimeEnd",
  	"deleteState",
  	"deleteError",
  	"deleteTimeStart",
  	"deleteTimeEnd"
  }
)

@XmlRootElement(name = "transazione-export")

public class TransazioneExport extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TransazioneExport() {
    super();
  }

  public java.util.Date getIntervalloInizio() {
    return this.intervalloInizio;
  }

  public void setIntervalloInizio(java.util.Date intervalloInizio) {
    this.intervalloInizio = intervalloInizio;
  }

  public java.util.Date getIntervalloFine() {
    return this.intervalloFine;
  }

  public void setIntervalloFine(java.util.Date intervalloFine) {
    this.intervalloFine = intervalloFine;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setExportStateRawEnumValue(String value) {
    this.exportState = (ExportState) ExportState.toEnumConstantFromString(value);
  }

  public String getExportStateRawEnumValue() {
    if(this.exportState == null){
    	return null;
    }else{
    	return this.exportState.toString();
    }
  }

  public org.openspcoop2.core.transazioni.constants.ExportState getExportState() {
    return this.exportState;
  }

  public void setExportState(org.openspcoop2.core.transazioni.constants.ExportState exportState) {
    this.exportState = exportState;
  }

  public java.lang.String getExportError() {
    return this.exportError;
  }

  public void setExportError(java.lang.String exportError) {
    this.exportError = exportError;
  }

  public java.util.Date getExportTimeStart() {
    return this.exportTimeStart;
  }

  public void setExportTimeStart(java.util.Date exportTimeStart) {
    this.exportTimeStart = exportTimeStart;
  }

  public java.util.Date getExportTimeEnd() {
    return this.exportTimeEnd;
  }

  public void setExportTimeEnd(java.util.Date exportTimeEnd) {
    this.exportTimeEnd = exportTimeEnd;
  }

  public void setDeleteStateRawEnumValue(String value) {
    this.deleteState = (DeleteState) DeleteState.toEnumConstantFromString(value);
  }

  public String getDeleteStateRawEnumValue() {
    if(this.deleteState == null){
    	return null;
    }else{
    	return this.deleteState.toString();
    }
  }

  public org.openspcoop2.core.transazioni.constants.DeleteState getDeleteState() {
    return this.deleteState;
  }

  public void setDeleteState(org.openspcoop2.core.transazioni.constants.DeleteState deleteState) {
    this.deleteState = deleteState;
  }

  public java.lang.String getDeleteError() {
    return this.deleteError;
  }

  public void setDeleteError(java.lang.String deleteError) {
    this.deleteError = deleteError;
  }

  public java.util.Date getDeleteTimeStart() {
    return this.deleteTimeStart;
  }

  public void setDeleteTimeStart(java.util.Date deleteTimeStart) {
    this.deleteTimeStart = deleteTimeStart;
  }

  public java.util.Date getDeleteTimeEnd() {
    return this.deleteTimeEnd;
  }

  public void setDeleteTimeEnd(java.util.Date deleteTimeEnd) {
    this.deleteTimeEnd = deleteTimeEnd;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.transazioni.model.TransazioneExportModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.transazioni.TransazioneExport.modelStaticInstance==null){
  			org.openspcoop2.core.transazioni.TransazioneExport.modelStaticInstance = new org.openspcoop2.core.transazioni.model.TransazioneExportModel();
	  }
  }
  public static org.openspcoop2.core.transazioni.model.TransazioneExportModel model(){
	  if(org.openspcoop2.core.transazioni.TransazioneExport.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.transazioni.TransazioneExport.modelStaticInstance;
  }


  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="intervallo-inizio",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date intervalloInizio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="intervallo-fine",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date intervalloFine;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String exportStateRawEnumValue;

  @XmlElement(name="export-state",required=true,nillable=false)
  protected ExportState exportState;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="export-error",required=false,nillable=false)
  protected java.lang.String exportError;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="export-time-start",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date exportTimeStart;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="export-time-end",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date exportTimeEnd;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String deleteStateRawEnumValue;

  @XmlElement(name="delete-state",required=true,nillable=false)
  protected DeleteState deleteState;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="delete-error",required=false,nillable=false)
  protected java.lang.String deleteError;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="delete-time-start",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date deleteTimeStart;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="delete-time-end",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date deleteTimeEnd;

}
