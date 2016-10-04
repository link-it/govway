/*
 * OpenSPCoop - Customizable API Gateway 
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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.EsitoCommittenteType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for NotificaEsitoCommittente_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificaEsitoCommittente_Type">
 * 		&lt;sequence>
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoFattura_Type" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Esito" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}EsitoCommittente_Type" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Descrizione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="MessageIdCommittente" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" use="required"/>
 * &lt;/complexType>
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
  	"_decimalWrapper_identificativoSdI",
  	"riferimentoFattura",
  	"esito",
  	"descrizione",
  	"messageIdCommittente"
  }
)

@XmlRootElement(name = "NotificaEsitoCommittente_Type")

public class NotificaEsitoCommittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public NotificaEsitoCommittenteType() {
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

  public java.lang.Integer getIdentificativoSdI() {
    if(this._decimalWrapper_identificativoSdI!=null){
		return (java.lang.Integer) this._decimalWrapper_identificativoSdI.getObject(java.lang.Integer.class);
	}else{
		return this.identificativoSdI;
	}
  }

  public void setIdentificativoSdI(java.lang.Integer identificativoSdI) {
    if(identificativoSdI!=null){
		this._decimalWrapper_identificativoSdI = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,12,identificativoSdI);
	}
  }

  public RiferimentoFatturaType getRiferimentoFattura() {
    return this.riferimentoFattura;
  }

  public void setRiferimentoFattura(RiferimentoFatturaType riferimentoFattura) {
    this.riferimentoFattura = riferimentoFattura;
  }

  public void set_value_esito(String value) {
    this.esito = (EsitoCommittenteType) EsitoCommittenteType.toEnumConstantFromString(value);
  }

  public String get_value_esito() {
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

  @XmlTransient
  private Long id;

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


  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_identificativoSdI = null;

  @XmlTransient
  protected java.lang.Integer identificativoSdI;

  @XmlElement(name="RiferimentoFattura",required=false,nillable=false)
  protected RiferimentoFatturaType riferimentoFattura;

  @XmlTransient
  protected java.lang.String _value_esito;

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
