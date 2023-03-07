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
package org.openspcoop2.protocol.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RiferimentoServizioApplicativoErogatore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoServizioApplicativoErogatore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-porta-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dati-applicativi" type="{http://www.openspcoop2.org/protocol/abstraction}DatiApplicativiErogazione" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "RiferimentoServizioApplicativoErogatore", 
  propOrder = {
  	"nome",
  	"nomePortaApplicativa",
  	"datiApplicativi"
  }
)

@XmlRootElement(name = "RiferimentoServizioApplicativoErogatore")

public class RiferimentoServizioApplicativoErogatore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoServizioApplicativoErogatore() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getNomePortaApplicativa() {
    return this.nomePortaApplicativa;
  }

  public void setNomePortaApplicativa(java.lang.String nomePortaApplicativa) {
    this.nomePortaApplicativa = nomePortaApplicativa;
  }

  public DatiApplicativiErogazione getDatiApplicativi() {
    return this.datiApplicativi;
  }

  public void setDatiApplicativi(DatiApplicativiErogazione datiApplicativi) {
    this.datiApplicativi = datiApplicativi;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta-applicativa",required=false,nillable=false)
  protected java.lang.String nomePortaApplicativa;

  @XmlElement(name="dati-applicativi",required=false,nillable=false)
  protected DatiApplicativiErogazione datiApplicativi;

}
