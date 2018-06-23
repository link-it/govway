/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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


/** <p>Java class for RiferimentoServizioApplicativoFruitore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoServizioApplicativoFruitore">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-porta-delegata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="dati-applicativi" type="{http://www.openspcoop2.org/protocol/abstraction}DatiApplicativiFruizione" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "RiferimentoServizioApplicativoFruitore", 
  propOrder = {
  	"nome",
  	"nomePortaDelegata",
  	"datiApplicativi"
  }
)

@XmlRootElement(name = "RiferimentoServizioApplicativoFruitore")

public class RiferimentoServizioApplicativoFruitore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoServizioApplicativoFruitore() {
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getNomePortaDelegata() {
    return this.nomePortaDelegata;
  }

  public void setNomePortaDelegata(java.lang.String nomePortaDelegata) {
    this.nomePortaDelegata = nomePortaDelegata;
  }

  public DatiApplicativiFruizione getDatiApplicativi() {
    return this.datiApplicativi;
  }

  public void setDatiApplicativi(DatiApplicativiFruizione datiApplicativi) {
    this.datiApplicativi = datiApplicativi;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta-delegata",required=false,nillable=false)
  protected java.lang.String nomePortaDelegata;

  @XmlElement(name="dati-applicativi",required=false,nillable=false)
  protected DatiApplicativiFruizione datiApplicativi;

}
