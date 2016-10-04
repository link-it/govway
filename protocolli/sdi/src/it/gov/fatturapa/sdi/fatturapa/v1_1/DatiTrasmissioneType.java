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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiTrasmissioneType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiTrasmissioneType">
 * 		&lt;sequence>
 * 			&lt;element name="IdTrasmittente" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}IdFiscaleType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ProgressivoInvio" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="FormatoTrasmissione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}FormatoTrasmissioneType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CodiceDestinatario" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ContattiTrasmittente" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}ContattiTrasmittenteType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DatiTrasmissioneType", 
  propOrder = {
  	"idTrasmittente",
  	"progressivoInvio",
  	"formatoTrasmissione",
  	"codiceDestinatario",
  	"contattiTrasmittente"
  }
)

@XmlRootElement(name = "DatiTrasmissioneType")

public class DatiTrasmissioneType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiTrasmissioneType() {
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

  public IdFiscaleType getIdTrasmittente() {
    return this.idTrasmittente;
  }

  public void setIdTrasmittente(IdFiscaleType idTrasmittente) {
    this.idTrasmittente = idTrasmittente;
  }

  public java.lang.String getProgressivoInvio() {
    return this.progressivoInvio;
  }

  public void setProgressivoInvio(java.lang.String progressivoInvio) {
    this.progressivoInvio = progressivoInvio;
  }

  public void set_value_formatoTrasmissione(String value) {
    this.formatoTrasmissione = (FormatoTrasmissioneType) FormatoTrasmissioneType.toEnumConstantFromString(value);
  }

  public String get_value_formatoTrasmissione() {
    if(this.formatoTrasmissione == null){
    	return null;
    }else{
    	return this.formatoTrasmissione.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType getFormatoTrasmissione() {
    return this.formatoTrasmissione;
  }

  public void setFormatoTrasmissione(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType formatoTrasmissione) {
    this.formatoTrasmissione = formatoTrasmissione;
  }

  public java.lang.String getCodiceDestinatario() {
    return this.codiceDestinatario;
  }

  public void setCodiceDestinatario(java.lang.String codiceDestinatario) {
    this.codiceDestinatario = codiceDestinatario;
  }

  public ContattiTrasmittenteType getContattiTrasmittente() {
    return this.contattiTrasmittente;
  }

  public void setContattiTrasmittente(ContattiTrasmittenteType contattiTrasmittente) {
    this.contattiTrasmittente = contattiTrasmittente;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="IdTrasmittente",required=true,nillable=false)
  protected IdFiscaleType idTrasmittente;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="ProgressivoInvio",required=true,nillable=false)
  protected java.lang.String progressivoInvio;

  @XmlTransient
  protected java.lang.String _value_formatoTrasmissione;

  @XmlElement(name="FormatoTrasmissione",required=true,nillable=false)
  protected FormatoTrasmissioneType formatoTrasmissione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodiceDestinatario",required=true,nillable=false)
  protected java.lang.String codiceDestinatario;

  @XmlElement(name="ContattiTrasmittente",required=false,nillable=false)
  protected ContattiTrasmittenteType contattiTrasmittente;

}
