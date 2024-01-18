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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for CessionarioCommittenteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CessionarioCommittenteType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiAnagrafici" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiAnagraficiCessionarioType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Sede" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IndirizzoType" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "CessionarioCommittenteType", 
  propOrder = {
  	"datiAnagrafici",
  	"sede"
  }
)

@XmlRootElement(name = "CessionarioCommittenteType")

public class CessionarioCommittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CessionarioCommittenteType() {
    super();
  }

  public DatiAnagraficiCessionarioType getDatiAnagrafici() {
    return this.datiAnagrafici;
  }

  public void setDatiAnagrafici(DatiAnagraficiCessionarioType datiAnagrafici) {
    this.datiAnagrafici = datiAnagrafici;
  }

  public IndirizzoType getSede() {
    return this.sede;
  }

  public void setSede(IndirizzoType sede) {
    this.sede = sede;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiAnagrafici",required=true,nillable=false)
  protected DatiAnagraficiCessionarioType datiAnagrafici;

  @XmlElement(name="Sede",required=true,nillable=false)
  protected IndirizzoType sede;

}
