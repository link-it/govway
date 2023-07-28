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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for FatturaPrincipaleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaPrincipaleType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NumeroFatturaPrincipale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataFatturaPrincipale" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "FatturaPrincipaleType", 
  propOrder = {
  	"numeroFatturaPrincipale",
  	"dataFatturaPrincipale"
  }
)

@XmlRootElement(name = "FatturaPrincipaleType")

public class FatturaPrincipaleType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaPrincipaleType() {
    super();
  }

  public java.lang.String getNumeroFatturaPrincipale() {
    return this.numeroFatturaPrincipale;
  }

  public void setNumeroFatturaPrincipale(java.lang.String numeroFatturaPrincipale) {
    this.numeroFatturaPrincipale = numeroFatturaPrincipale;
  }

  public java.util.Date getDataFatturaPrincipale() {
    return this.dataFatturaPrincipale;
  }

  public void setDataFatturaPrincipale(java.util.Date dataFatturaPrincipale) {
    this.dataFatturaPrincipale = dataFatturaPrincipale;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroFatturaPrincipale",required=true,nillable=false)
  protected java.lang.String numeroFatturaPrincipale;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataFatturaPrincipale",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataFatturaPrincipale;

}
