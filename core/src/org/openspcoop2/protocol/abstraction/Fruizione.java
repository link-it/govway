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
package org.openspcoop2.protocol.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.abstraction.constants.Tipologia;
import java.io.Serializable;


/** <p>Java class for fruizione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fruizione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/protocol/abstraction}RiferimentoAccordoServizioParteSpecifica" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="soggetto-fruitore" type="{http://www.openspcoop2.org/protocol/abstraction}RiferimentoSoggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="fruizione" type="{http://www.openspcoop2.org/protocol/abstraction}DatiFruizione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/protocol/abstraction}RiferimentoServizioApplicativoFruitore" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="tipologia" type="{http://www.openspcoop2.org/protocol/abstraction}Tipologia" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fruizione", 
  propOrder = {
  	"accordoServizioParteSpecifica",
  	"soggettoFruitore",
  	"fruizione",
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "fruizione")

public class Fruizione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Fruizione() {
  }

  public RiferimentoAccordoServizioParteSpecifica getAccordoServizioParteSpecifica() {
    return this.accordoServizioParteSpecifica;
  }

  public void setAccordoServizioParteSpecifica(RiferimentoAccordoServizioParteSpecifica accordoServizioParteSpecifica) {
    this.accordoServizioParteSpecifica = accordoServizioParteSpecifica;
  }

  public RiferimentoSoggetto getSoggettoFruitore() {
    return this.soggettoFruitore;
  }

  public void setSoggettoFruitore(RiferimentoSoggetto soggettoFruitore) {
    this.soggettoFruitore = soggettoFruitore;
  }

  public DatiFruizione getFruizione() {
    return this.fruizione;
  }

  public void setFruizione(DatiFruizione fruizione) {
    this.fruizione = fruizione;
  }

  public RiferimentoServizioApplicativoFruitore getServizioApplicativo() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativo(RiferimentoServizioApplicativoFruitore servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
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

  public org.openspcoop2.protocol.abstraction.constants.Tipologia getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(org.openspcoop2.protocol.abstraction.constants.Tipologia tipologia) {
    this.tipologia = tipologia;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.protocol.abstraction.model.FruizioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.protocol.abstraction.Fruizione.modelStaticInstance==null){
  			org.openspcoop2.protocol.abstraction.Fruizione.modelStaticInstance = new org.openspcoop2.protocol.abstraction.model.FruizioneModel();
	  }
  }
  public static org.openspcoop2.protocol.abstraction.model.FruizioneModel model(){
	  if(org.openspcoop2.protocol.abstraction.Fruizione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.protocol.abstraction.Fruizione.modelStaticInstance;
  }


  @XmlElement(name="accordo-servizio-parte-specifica",required=true,nillable=false)
  protected RiferimentoAccordoServizioParteSpecifica accordoServizioParteSpecifica;

  @XmlElement(name="soggetto-fruitore",required=true,nillable=false)
  protected RiferimentoSoggetto soggettoFruitore;

  @XmlElement(name="fruizione",required=false,nillable=false)
  protected DatiFruizione fruizione;

  @XmlElement(name="servizio-applicativo",required=false,nillable=false)
  protected RiferimentoServizioApplicativoFruitore servizioApplicativo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipologia;

  @XmlAttribute(name="tipologia",required=true)
  protected Tipologia tipologia;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

}
