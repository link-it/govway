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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiGeneraliType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiGeneraliDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}DatiGeneraliDocumentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiFatturaRettificata" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}DatiFatturaRettificataType" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiGeneraliType", 
  propOrder = {
  	"datiGeneraliDocumento",
  	"datiFatturaRettificata"
  }
)

@XmlRootElement(name = "DatiGeneraliType")

public class DatiGeneraliType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiGeneraliType() {
  }

  public DatiGeneraliDocumentoType getDatiGeneraliDocumento() {
    return this.datiGeneraliDocumento;
  }

  public void setDatiGeneraliDocumento(DatiGeneraliDocumentoType datiGeneraliDocumento) {
    this.datiGeneraliDocumento = datiGeneraliDocumento;
  }

  public DatiFatturaRettificataType getDatiFatturaRettificata() {
    return this.datiFatturaRettificata;
  }

  public void setDatiFatturaRettificata(DatiFatturaRettificataType datiFatturaRettificata) {
    this.datiFatturaRettificata = datiFatturaRettificata;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiGeneraliDocumento",required=true,nillable=false)
  protected DatiGeneraliDocumentoType datiGeneraliDocumento;

  @XmlElement(name="DatiFatturaRettificata",required=false,nillable=false)
  protected DatiFatturaRettificataType datiFatturaRettificata;

}
