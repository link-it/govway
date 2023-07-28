/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for AllegatiType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllegatiType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NomeAttachment" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="AlgoritmoCompressione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="FormatoAttachment" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DescrizioneAttachment" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Attachment" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "AllegatiType", 
  propOrder = {
  	"nomeAttachment",
  	"algoritmoCompressione",
  	"formatoAttachment",
  	"descrizioneAttachment",
  	"attachment"
  }
)

@XmlRootElement(name = "AllegatiType")

public class AllegatiType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AllegatiType() {
    super();
  }

  public java.lang.String getNomeAttachment() {
    return this.nomeAttachment;
  }

  public void setNomeAttachment(java.lang.String nomeAttachment) {
    this.nomeAttachment = nomeAttachment;
  }

  public java.lang.String getAlgoritmoCompressione() {
    return this.algoritmoCompressione;
  }

  public void setAlgoritmoCompressione(java.lang.String algoritmoCompressione) {
    this.algoritmoCompressione = algoritmoCompressione;
  }

  public java.lang.String getFormatoAttachment() {
    return this.formatoAttachment;
  }

  public void setFormatoAttachment(java.lang.String formatoAttachment) {
    this.formatoAttachment = formatoAttachment;
  }

  public java.lang.String getDescrizioneAttachment() {
    return this.descrizioneAttachment;
  }

  public void setDescrizioneAttachment(java.lang.String descrizioneAttachment) {
    this.descrizioneAttachment = descrizioneAttachment;
  }

  public byte[] getAttachment() {
    return this.attachment;
  }

  public void setAttachment(byte[] attachment) {
    this.attachment = attachment;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NomeAttachment",required=true,nillable=false)
  protected java.lang.String nomeAttachment;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="AlgoritmoCompressione",required=false,nillable=false)
  protected java.lang.String algoritmoCompressione;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="FormatoAttachment",required=false,nillable=false)
  protected java.lang.String formatoAttachment;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="DescrizioneAttachment",required=false,nillable=false)
  protected java.lang.String descrizioneAttachment;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="Attachment",required=true,nillable=false)
  protected byte[] attachment;

}
