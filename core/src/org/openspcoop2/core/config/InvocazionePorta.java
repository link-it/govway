/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for invocazione-porta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta">
 * 		&lt;sequence>
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config}credenziali" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}invocazione-porta-gestione-errore" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-porta", 
  propOrder = {
  	"credenziali",
  	"gestioneErrore"
  }
)

@XmlRootElement(name = "invocazione-porta")

public class InvocazionePorta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InvocazionePorta() {
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

  public void addCredenziali(Credenziali credenziali) {
    this.credenziali.add(credenziali);
  }

  public Credenziali getCredenziali(int index) {
    return this.credenziali.get( index );
  }

  public Credenziali removeCredenziali(int index) {
    return this.credenziali.remove( index );
  }

  public List<Credenziali> getCredenzialiList() {
    return this.credenziali;
  }

  public void setCredenzialiList(List<Credenziali> credenziali) {
    this.credenziali=credenziali;
  }

  public int sizeCredenzialiList() {
    return this.credenziali.size();
  }

  public InvocazionePortaGestioneErrore getGestioneErrore() {
    return this.gestioneErrore;
  }

  public void setGestioneErrore(InvocazionePortaGestioneErrore gestioneErrore) {
    this.gestioneErrore = gestioneErrore;
  }

  public void set_value_invioPerRiferimento(String value) {
    this.invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_invioPerRiferimento() {
    if(this.invioPerRiferimento == null){
    	return null;
    }else{
    	return this.invioPerRiferimento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getInvioPerRiferimento() {
    return this.invioPerRiferimento;
  }

  public void setInvioPerRiferimento(org.openspcoop2.core.config.constants.StatoFunzionalita invioPerRiferimento) {
    this.invioPerRiferimento = invioPerRiferimento;
  }

  public void set_value_sbustamentoInformazioniProtocollo(String value) {
    this.sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_sbustamentoInformazioniProtocollo() {
    if(this.sbustamentoInformazioniProtocollo == null){
    	return null;
    }else{
    	return this.sbustamentoInformazioniProtocollo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getSbustamentoInformazioniProtocollo() {
    return this.sbustamentoInformazioniProtocollo;
  }

  public void setSbustamentoInformazioniProtocollo(org.openspcoop2.core.config.constants.StatoFunzionalita sbustamentoInformazioniProtocollo) {
    this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="credenziali",required=true,nillable=false)
  protected List<Credenziali> credenziali = new ArrayList<Credenziali>();

  /**
   * @deprecated Use method getCredenzialiList
   * @return List<Credenziali>
  */
  @Deprecated
  public List<Credenziali> getCredenziali() {
  	return this.credenziali;
  }

  /**
   * @deprecated Use method setCredenzialiList
   * @param credenziali List<Credenziali>
  */
  @Deprecated
  public void setCredenziali(List<Credenziali> credenziali) {
  	this.credenziali=credenziali;
  }

  /**
   * @deprecated Use method sizeCredenzialiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCredenziali() {
  	return this.credenziali.size();
  }

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected InvocazionePortaGestioneErrore gestioneErrore;

  @XmlTransient
  protected java.lang.String _value_invioPerRiferimento;

  @XmlAttribute(name="invio-per-riferimento",required=false)
  protected StatoFunzionalita invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_sbustamentoInformazioniProtocollo;

  @XmlAttribute(name="sbustamento-informazioni-protocollo",required=false)
  protected StatoFunzionalita sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
