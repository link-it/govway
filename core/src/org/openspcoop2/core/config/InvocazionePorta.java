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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 * &lt;complexType name="invocazione-porta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config}credenziali" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config}servizio-applicativo-ruoli" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}invocazione-porta-gestione-errore" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
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
  	"ruoli",
  	"gestioneErrore"
  }
)

@XmlRootElement(name = "invocazione-porta")

public class InvocazionePorta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public InvocazionePorta() {
    super();
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

  public ServizioApplicativoRuoli getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(ServizioApplicativoRuoli ruoli) {
    this.ruoli = ruoli;
  }

  public InvocazionePortaGestioneErrore getGestioneErrore() {
    return this.gestioneErrore;
  }

  public void setGestioneErrore(InvocazionePortaGestioneErrore gestioneErrore) {
    this.gestioneErrore = gestioneErrore;
  }

  public void setInvioPerRiferimentoRawEnumValue(String value) {
    this.invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getInvioPerRiferimentoRawEnumValue() {
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

  public void setSbustamentoInformazioniProtocolloRawEnumValue(String value) {
    this.sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getSbustamentoInformazioniProtocolloRawEnumValue() {
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



  @XmlElement(name="credenziali",required=true,nillable=false)
  private List<Credenziali> credenziali = new ArrayList<>();

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected ServizioApplicativoRuoli ruoli;

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected InvocazionePortaGestioneErrore gestioneErrore;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String invioPerRiferimentoRawEnumValue;

  @XmlAttribute(name="invio-per-riferimento",required=false)
  protected StatoFunzionalita invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String sbustamentoInformazioniProtocolloRawEnumValue;

  @XmlAttribute(name="sbustamento-informazioni-protocollo",required=false)
  protected StatoFunzionalita sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
