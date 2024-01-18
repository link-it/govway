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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.EsitoCommittenteType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for NotificaEsitoCommittente_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificaEsitoCommittente_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoFattura_Type" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Esito" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}EsitoCommittente_Type" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Descrizione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="MessageIdCommittente" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificaEsitoCommittente_Type", 
  propOrder = {
  	"identificativoSdI",
  	"riferimentoFattura",
  	"esito",
  	"descrizione",
  	"messageIdCommittente"
  }
)

@XmlRootElement(name = "NotificaEsitoCommittente_Type")

public class NotificaEsitoCommittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public NotificaEsitoCommittenteType() {
    super();
  }

  public java.lang.String getIdentificativoSdI() {
    return this.identificativoSdI;
  }

  public void setIdentificativoSdI(java.lang.String identificativoSdI) {
    this.identificativoSdI = identificativoSdI;
  }

  public RiferimentoFatturaType getRiferimentoFattura() {
    return this.riferimentoFattura;
  }

  public void setRiferimentoFattura(RiferimentoFatturaType riferimentoFattura) {
    this.riferimentoFattura = riferimentoFattura;
  }

  public void setEsitoRawEnumValue(String value) {
    this.esito = (EsitoCommittenteType) EsitoCommittenteType.toEnumConstantFromString(value);
  }

  public String getEsitoRawEnumValue() {
    if(this.esito == null){
    	return null;
    }else{
    	return this.esito.toString();
    }
  }

  public it.gov.fatturapa.sdi.messaggi.v1_0.constants.EsitoCommittenteType getEsito() {
    return this.esito;
  }

  public void setEsito(it.gov.fatturapa.sdi.messaggi.v1_0.constants.EsitoCommittenteType esito) {
    this.esito = esito;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getMessageIdCommittente() {
    return this.messageIdCommittente;
  }

  public void setMessageIdCommittente(java.lang.String messageIdCommittente) {
    this.messageIdCommittente = messageIdCommittente;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoCommittenteTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoCommittenteTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoCommittenteTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @XmlElement(name="RiferimentoFattura",required=false,nillable=false)
  protected RiferimentoFatturaType riferimentoFattura;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String esitoRawEnumValue;

  @XmlElement(name="Esito",required=true,nillable=false)
  protected EsitoCommittenteType esito;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageIdCommittente",required=false,nillable=false)
  protected java.lang.String messageIdCommittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}
