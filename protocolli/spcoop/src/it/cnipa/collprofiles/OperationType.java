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
package it.cnipa.collprofiles;

import it.cnipa.collprofiles.constants.ProfiloDiCollaborazioneType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for operationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationType"&gt;
 * 		&lt;attribute name="servizio" type="{http://www.w3.org/2001/XMLSchema}QName" use="required"/&gt;
 * 		&lt;attribute name="operazione" type="{http://www.w3.org/2001/XMLSchema}QName" use="required"/&gt;
 * 		&lt;attribute name="profiloDiCollaborazione" type="{http://www.cnipa.it/collProfiles}profiloDiCollaborazioneType" use="required"/&gt;
 * 		&lt;attribute name="servizioCorrelato" type="{http://www.w3.org/2001/XMLSchema}QName" use="optional"/&gt;
 * 		&lt;attribute name="operazioneCorrelata" type="{http://www.w3.org/2001/XMLSchema}QName" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operationType")

@XmlRootElement(name = "operationType")

public class OperationType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OperationType() {
    super();
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getOperazione() {
    return this.operazione;
  }

  public void setOperazione(java.lang.String operazione) {
    this.operazione = operazione;
  }

  public void setProfiloDiCollaborazioneRawEnumValue(String value) {
    this.profiloDiCollaborazione = (ProfiloDiCollaborazioneType) ProfiloDiCollaborazioneType.toEnumConstantFromString(value);
  }

  public String getProfiloDiCollaborazioneRawEnumValue() {
    if(this.profiloDiCollaborazione == null){
    	return null;
    }else{
    	return this.profiloDiCollaborazione.toString();
    }
  }

  public it.cnipa.collprofiles.constants.ProfiloDiCollaborazioneType getProfiloDiCollaborazione() {
    return this.profiloDiCollaborazione;
  }

  public void setProfiloDiCollaborazione(it.cnipa.collprofiles.constants.ProfiloDiCollaborazioneType profiloDiCollaborazione) {
    this.profiloDiCollaborazione = profiloDiCollaborazione;
  }

  public java.lang.String getServizioCorrelato() {
    return this.servizioCorrelato;
  }

  public void setServizioCorrelato(java.lang.String servizioCorrelato) {
    this.servizioCorrelato = servizioCorrelato;
  }

  public java.lang.String getOperazioneCorrelata() {
    return this.operazioneCorrelata;
  }

  public void setOperazioneCorrelata(java.lang.String operazioneCorrelata) {
    this.operazioneCorrelata = operazioneCorrelata;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="QName")
  @XmlAttribute(name="servizio",required=true)
  protected java.lang.String servizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="QName")
  @XmlAttribute(name="operazione",required=true)
  protected java.lang.String operazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String profiloDiCollaborazioneRawEnumValue;

  @XmlAttribute(name="profiloDiCollaborazione",required=true)
  protected ProfiloDiCollaborazioneType profiloDiCollaborazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="QName")
  @XmlAttribute(name="servizioCorrelato",required=false)
  protected java.lang.String servizioCorrelato;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="QName")
  @XmlAttribute(name="operazioneCorrelata",required=false)
  protected java.lang.String operazioneCorrelata;

}
