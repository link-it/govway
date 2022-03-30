/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SpecificaConversazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaConversazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conversazioneConcettuale" type="{http://spcoop.gov.it/sica/manifest}DocumentoConversazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="conversazioneLogicaLatoFruitore" type="{http://spcoop.gov.it/sica/manifest}DocumentoConversazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="conversazioneLogicaLatoErogatore" type="{http://spcoop.gov.it/sica/manifest}DocumentoConversazione" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "SpecificaConversazione", 
  propOrder = {
  	"conversazioneConcettuale",
  	"conversazioneLogicaLatoFruitore",
  	"conversazioneLogicaLatoErogatore"
  }
)

@XmlRootElement(name = "SpecificaConversazione")

public class SpecificaConversazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaConversazione() {
  }

  public DocumentoConversazione getConversazioneConcettuale() {
    return this.conversazioneConcettuale;
  }

  public void setConversazioneConcettuale(DocumentoConversazione conversazioneConcettuale) {
    this.conversazioneConcettuale = conversazioneConcettuale;
  }

  public DocumentoConversazione getConversazioneLogicaLatoFruitore() {
    return this.conversazioneLogicaLatoFruitore;
  }

  public void setConversazioneLogicaLatoFruitore(DocumentoConversazione conversazioneLogicaLatoFruitore) {
    this.conversazioneLogicaLatoFruitore = conversazioneLogicaLatoFruitore;
  }

  public DocumentoConversazione getConversazioneLogicaLatoErogatore() {
    return this.conversazioneLogicaLatoErogatore;
  }

  public void setConversazioneLogicaLatoErogatore(DocumentoConversazione conversazioneLogicaLatoErogatore) {
    this.conversazioneLogicaLatoErogatore = conversazioneLogicaLatoErogatore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conversazioneConcettuale",required=false,nillable=false)
  protected DocumentoConversazione conversazioneConcettuale;

  @XmlElement(name="conversazioneLogicaLatoFruitore",required=false,nillable=false)
  protected DocumentoConversazione conversazioneLogicaLatoFruitore;

  @XmlElement(name="conversazioneLogicaLatoErogatore",required=false,nillable=false)
  protected DocumentoConversazione conversazioneLogicaLatoErogatore;

}
