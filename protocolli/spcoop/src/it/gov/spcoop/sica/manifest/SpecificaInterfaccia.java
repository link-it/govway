/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SpecificaInterfaccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaInterfaccia"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="interfacciaConcettuale" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="interfacciaLogicaLatoErogatore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="interfacciaLogicaLatoFruitore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "SpecificaInterfaccia", 
  propOrder = {
  	"interfacciaConcettuale",
  	"interfacciaLogicaLatoErogatore",
  	"interfacciaLogicaLatoFruitore"
  }
)

@XmlRootElement(name = "SpecificaInterfaccia")

public class SpecificaInterfaccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaInterfaccia() {
    super();
  }

  public DocumentoInterfaccia getInterfacciaConcettuale() {
    return this.interfacciaConcettuale;
  }

  public void setInterfacciaConcettuale(DocumentoInterfaccia interfacciaConcettuale) {
    this.interfacciaConcettuale = interfacciaConcettuale;
  }

  public DocumentoInterfaccia getInterfacciaLogicaLatoErogatore() {
    return this.interfacciaLogicaLatoErogatore;
  }

  public void setInterfacciaLogicaLatoErogatore(DocumentoInterfaccia interfacciaLogicaLatoErogatore) {
    this.interfacciaLogicaLatoErogatore = interfacciaLogicaLatoErogatore;
  }

  public DocumentoInterfaccia getInterfacciaLogicaLatoFruitore() {
    return this.interfacciaLogicaLatoFruitore;
  }

  public void setInterfacciaLogicaLatoFruitore(DocumentoInterfaccia interfacciaLogicaLatoFruitore) {
    this.interfacciaLogicaLatoFruitore = interfacciaLogicaLatoFruitore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="interfacciaConcettuale",required=true,nillable=false)
  protected DocumentoInterfaccia interfacciaConcettuale;

  @XmlElement(name="interfacciaLogicaLatoErogatore",required=true,nillable=false)
  protected DocumentoInterfaccia interfacciaLogicaLatoErogatore;

  @XmlElement(name="interfacciaLogicaLatoFruitore",required=false,nillable=false)
  protected DocumentoInterfaccia interfacciaLogicaLatoFruitore;

}
