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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-azione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="azione-delegata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="identificazione" type="{http://www.openspcoop2.org/core/config}PortaApplicativaAzioneIdentificazione" use="optional" default="static"/&gt;
 * 		&lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome-porta-delegante" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="force-interface-based" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa-azione", 
  propOrder = {
  	"azioneDelegata"
  }
)

@XmlRootElement(name = "porta-applicativa-azione")

public class PortaApplicativaAzione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaApplicativaAzione() {
    super();
  }

  public void addAzioneDelegata(java.lang.String azioneDelegata) {
    this.azioneDelegata.add(azioneDelegata);
  }

  public java.lang.String getAzioneDelegata(int index) {
    return this.azioneDelegata.get( index );
  }

  public java.lang.String removeAzioneDelegata(int index) {
    return this.azioneDelegata.remove( index );
  }

  public List<java.lang.String> getAzioneDelegataList() {
    return this.azioneDelegata;
  }

  public void setAzioneDelegataList(List<java.lang.String> azioneDelegata) {
    this.azioneDelegata=azioneDelegata;
  }

  public int sizeAzioneDelegataList() {
    return this.azioneDelegata.size();
  }

  public void setIdentificazioneRawEnumValue(String value) {
    this.identificazione = (PortaApplicativaAzioneIdentificazione) PortaApplicativaAzioneIdentificazione.toEnumConstantFromString(value);
  }

  public String getIdentificazioneRawEnumValue() {
    if(this.identificazione == null){
    	return null;
    }else{
    	return this.identificazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione identificazione) {
    this.identificazione = identificazione;
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getNomePortaDelegante() {
    return this.nomePortaDelegante;
  }

  public void setNomePortaDelegante(java.lang.String nomePortaDelegante) {
    this.nomePortaDelegante = nomePortaDelegante;
  }

  public void setForceInterfaceBasedRawEnumValue(String value) {
    this.forceInterfaceBased = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getForceInterfaceBasedRawEnumValue() {
    if(this.forceInterfaceBased == null){
    	return null;
    }else{
    	return this.forceInterfaceBased.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getForceInterfaceBased() {
    return this.forceInterfaceBased;
  }

  public void setForceInterfaceBased(org.openspcoop2.core.config.constants.StatoFunzionalita forceInterfaceBased) {
    this.forceInterfaceBased = forceInterfaceBased;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione-delegata",required=true,nillable=false)
  private List<java.lang.String> azioneDelegata = new ArrayList<>();

  /**
   * Use method getAzioneDelegataList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getAzioneDelegata() {
  	return this.getAzioneDelegataList();
  }

  /**
   * Use method setAzioneDelegataList
   * @param azioneDelegata List&lt;java.lang.String&gt;
  */
  public void setAzioneDelegata(List<java.lang.String> azioneDelegata) {
  	this.setAzioneDelegataList(azioneDelegata);
  }

  /**
   * Use method sizeAzioneDelegataList
   * @return lunghezza della lista
  */
  public int sizeAzioneDelegata() {
  	return this.sizeAzioneDelegataList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String identificazioneRawEnumValue;

  @XmlAttribute(name="identificazione",required=false)
  protected PortaApplicativaAzioneIdentificazione identificazione = (PortaApplicativaAzioneIdentificazione) PortaApplicativaAzioneIdentificazione.toEnumConstantFromString("static");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pattern",required=false)
  protected java.lang.String pattern;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-porta-delegante",required=false)
  protected java.lang.String nomePortaDelegante;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String forceInterfaceBasedRawEnumValue;

  @XmlAttribute(name="force-interface-based",required=false)
  protected StatoFunzionalita forceInterfaceBased = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
