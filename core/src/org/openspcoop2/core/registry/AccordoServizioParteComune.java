/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-comune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-composto" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune-servizio-composto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.openspcoop2.org/core/registry}azione" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="port-type" type="{http://www.openspcoop2.org/core/registry}port-type" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="specifica-semiformale" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="privato" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/>
 * 		&lt;attribute name="byte-wsdl-definitorio" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-wsdl-concettuale" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-wsdl-logico-erogatore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-wsdl-logico-fruitore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-specifica-conversazione-concettuale" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-specifica-conversazione-erogatore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-specifica-conversazione-fruitore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/registry}ProfiloCollaborazione" use="required"/>
 * 		&lt;attribute name="wsdl-definitorio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="wsdl-concettuale" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="wsdl-logico-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="wsdl-logico-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="specifica-conversazione-concettuale" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="specifica-conversazione-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="specifica-conversazione-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="utilizzo-senza-azione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-servizio-parte-comune", 
  propOrder = {
  	"soggettoReferente",
  	"servizioComposto",
  	"azione",
  	"portType",
  	"allegato",
  	"specificaSemiformale"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune")

public class AccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteComune() {
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

  public IDAccordo getOldIDAccordoForUpdate() {
    return this.oldIDAccordoForUpdate;
  }

  public void setOldIDAccordoForUpdate(IDAccordo oldIDAccordoForUpdate) {
    this.oldIDAccordoForUpdate=oldIDAccordoForUpdate;
  }

  public IdSoggetto getSoggettoReferente() {
    return this.soggettoReferente;
  }

  public void setSoggettoReferente(IdSoggetto soggettoReferente) {
    this.soggettoReferente = soggettoReferente;
  }

  public AccordoServizioParteComuneServizioComposto getServizioComposto() {
    return this.servizioComposto;
  }

  public void setServizioComposto(AccordoServizioParteComuneServizioComposto servizioComposto) {
    this.servizioComposto = servizioComposto;
  }

  public void addAzione(Azione azione) {
    this.azione.add(azione);
  }

  public Azione getAzione(int index) {
    return this.azione.get( index );
  }

  public Azione removeAzione(int index) {
    return this.azione.remove( index );
  }

  public List<Azione> getAzioneList() {
    return this.azione;
  }

  public void setAzioneList(List<Azione> azione) {
    this.azione=azione;
  }

  public int sizeAzioneList() {
    return this.azione.size();
  }

  public void addPortType(PortType portType) {
    this.portType.add(portType);
  }

  public PortType getPortType(int index) {
    return this.portType.get( index );
  }

  public PortType removePortType(int index) {
    return this.portType.remove( index );
  }

  public List<PortType> getPortTypeList() {
    return this.portType;
  }

  public void setPortTypeList(List<PortType> portType) {
    this.portType=portType;
  }

  public int sizePortTypeList() {
    return this.portType.size();
  }

  public void addAllegato(Documento allegato) {
    this.allegato.add(allegato);
  }

  public Documento getAllegato(int index) {
    return this.allegato.get( index );
  }

  public Documento removeAllegato(int index) {
    return this.allegato.remove( index );
  }

  public List<Documento> getAllegatoList() {
    return this.allegato;
  }

  public void setAllegatoList(List<Documento> allegato) {
    this.allegato=allegato;
  }

  public int sizeAllegatoList() {
    return this.allegato.size();
  }

  public void addSpecificaSemiformale(Documento specificaSemiformale) {
    this.specificaSemiformale.add(specificaSemiformale);
  }

  public Documento getSpecificaSemiformale(int index) {
    return this.specificaSemiformale.get( index );
  }

  public Documento removeSpecificaSemiformale(int index) {
    return this.specificaSemiformale.remove( index );
  }

  public List<Documento> getSpecificaSemiformaleList() {
    return this.specificaSemiformale;
  }

  public void setSpecificaSemiformaleList(List<Documento> specificaSemiformale) {
    this.specificaSemiformale=specificaSemiformale;
  }

  public int sizeSpecificaSemiformaleList() {
    return this.specificaSemiformale.size();
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
  }

  public java.lang.String getStatoPackage() {
    return this.statoPackage;
  }

  public void setStatoPackage(java.lang.String statoPackage) {
    this.statoPackage = statoPackage;
  }

  public Boolean getPrivato() {
    return this.privato;
  }

  public void setPrivato(Boolean privato) {
    this.privato = privato;
  }

  public byte[] getByteWsdlDefinitorio() {
    return this.byteWsdlDefinitorio;
  }

  public void setByteWsdlDefinitorio(byte[] byteWsdlDefinitorio) {
    this.byteWsdlDefinitorio = byteWsdlDefinitorio;
  }

  public byte[] getByteWsdlConcettuale() {
    return this.byteWsdlConcettuale;
  }

  public void setByteWsdlConcettuale(byte[] byteWsdlConcettuale) {
    this.byteWsdlConcettuale = byteWsdlConcettuale;
  }

  public byte[] getByteWsdlLogicoErogatore() {
    return this.byteWsdlLogicoErogatore;
  }

  public void setByteWsdlLogicoErogatore(byte[] byteWsdlLogicoErogatore) {
    this.byteWsdlLogicoErogatore = byteWsdlLogicoErogatore;
  }

  public byte[] getByteWsdlLogicoFruitore() {
    return this.byteWsdlLogicoFruitore;
  }

  public void setByteWsdlLogicoFruitore(byte[] byteWsdlLogicoFruitore) {
    this.byteWsdlLogicoFruitore = byteWsdlLogicoFruitore;
  }

  public byte[] getByteSpecificaConversazioneConcettuale() {
    return this.byteSpecificaConversazioneConcettuale;
  }

  public void setByteSpecificaConversazioneConcettuale(byte[] byteSpecificaConversazioneConcettuale) {
    this.byteSpecificaConversazioneConcettuale = byteSpecificaConversazioneConcettuale;
  }

  public byte[] getByteSpecificaConversazioneErogatore() {
    return this.byteSpecificaConversazioneErogatore;
  }

  public void setByteSpecificaConversazioneErogatore(byte[] byteSpecificaConversazioneErogatore) {
    this.byteSpecificaConversazioneErogatore = byteSpecificaConversazioneErogatore;
  }

  public byte[] getByteSpecificaConversazioneFruitore() {
    return this.byteSpecificaConversazioneFruitore;
  }

  public void setByteSpecificaConversazioneFruitore(byte[] byteSpecificaConversazioneFruitore) {
    this.byteSpecificaConversazioneFruitore = byteSpecificaConversazioneFruitore;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void set_value_profiloCollaborazione(String value) {
    this.profiloCollaborazione = (ProfiloCollaborazione) ProfiloCollaborazione.toEnumConstantFromString(value);
  }

  public String get_value_profiloCollaborazione() {
    if(this.profiloCollaborazione == null){
    	return null;
    }else{
    	return this.profiloCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.ProfiloCollaborazione getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(org.openspcoop2.core.registry.constants.ProfiloCollaborazione profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public java.lang.String getWsdlDefinitorio() {
    return this.wsdlDefinitorio;
  }

  public void setWsdlDefinitorio(java.lang.String wsdlDefinitorio) {
    this.wsdlDefinitorio = wsdlDefinitorio;
  }

  public java.lang.String getWsdlConcettuale() {
    return this.wsdlConcettuale;
  }

  public void setWsdlConcettuale(java.lang.String wsdlConcettuale) {
    this.wsdlConcettuale = wsdlConcettuale;
  }

  public java.lang.String getWsdlLogicoErogatore() {
    return this.wsdlLogicoErogatore;
  }

  public void setWsdlLogicoErogatore(java.lang.String wsdlLogicoErogatore) {
    this.wsdlLogicoErogatore = wsdlLogicoErogatore;
  }

  public java.lang.String getWsdlLogicoFruitore() {
    return this.wsdlLogicoFruitore;
  }

  public void setWsdlLogicoFruitore(java.lang.String wsdlLogicoFruitore) {
    this.wsdlLogicoFruitore = wsdlLogicoFruitore;
  }

  public java.lang.String getSpecificaConversazioneConcettuale() {
    return this.specificaConversazioneConcettuale;
  }

  public void setSpecificaConversazioneConcettuale(java.lang.String specificaConversazioneConcettuale) {
    this.specificaConversazioneConcettuale = specificaConversazioneConcettuale;
  }

  public java.lang.String getSpecificaConversazioneErogatore() {
    return this.specificaConversazioneErogatore;
  }

  public void setSpecificaConversazioneErogatore(java.lang.String specificaConversazioneErogatore) {
    this.specificaConversazioneErogatore = specificaConversazioneErogatore;
  }

  public java.lang.String getSpecificaConversazioneFruitore() {
    return this.specificaConversazioneFruitore;
  }

  public void setSpecificaConversazioneFruitore(java.lang.String specificaConversazioneFruitore) {
    this.specificaConversazioneFruitore = specificaConversazioneFruitore;
  }

  public boolean isUtilizzoSenzaAzione() {
    return this.utilizzoSenzaAzione;
  }

  public boolean getUtilizzoSenzaAzione() {
    return this.utilizzoSenzaAzione;
  }

  public void setUtilizzoSenzaAzione(boolean utilizzoSenzaAzione) {
    this.utilizzoSenzaAzione = utilizzoSenzaAzione;
  }

  public void set_value_filtroDuplicati(String value) {
    this.filtroDuplicati = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_filtroDuplicati() {
    if(this.filtroDuplicati == null){
    	return null;
    }else{
    	return this.filtroDuplicati.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public void setFiltroDuplicati(org.openspcoop2.core.registry.constants.StatoFunzionalita filtroDuplicati) {
    this.filtroDuplicati = filtroDuplicati;
  }

  public void set_value_confermaRicezione(String value) {
    this.confermaRicezione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_confermaRicezione() {
    if(this.confermaRicezione == null){
    	return null;
    }else{
    	return this.confermaRicezione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(org.openspcoop2.core.registry.constants.StatoFunzionalita confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public void set_value_idCollaborazione(String value) {
    this.idCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_idCollaborazione() {
    if(this.idCollaborazione == null){
    	return null;
    }else{
    	return this.idCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(org.openspcoop2.core.registry.constants.StatoFunzionalita idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public void set_value_consegnaInOrdine(String value) {
    this.consegnaInOrdine = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_consegnaInOrdine() {
    if(this.consegnaInOrdine == null){
    	return null;
    }else{
    	return this.consegnaInOrdine.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public void setConsegnaInOrdine(org.openspcoop2.core.registry.constants.StatoFunzionalita consegnaInOrdine) {
    this.consegnaInOrdine = consegnaInOrdine;
  }

  public java.lang.String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.lang.String scadenza) {
    this.scadenza = scadenza;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.registry.model.AccordoServizioParteComuneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.AccordoServizioParteComune.modelStaticInstance==null){
  			org.openspcoop2.core.registry.AccordoServizioParteComune.modelStaticInstance = new org.openspcoop2.core.registry.model.AccordoServizioParteComuneModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.AccordoServizioParteComuneModel model(){
	  if(org.openspcoop2.core.registry.AccordoServizioParteComune.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.AccordoServizioParteComune.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDAccordo oldIDAccordoForUpdate;

  @XmlElement(name="soggetto-referente",required=false,nillable=false)
  protected IdSoggetto soggettoReferente;

  @XmlElement(name="servizio-composto",required=false,nillable=false)
  protected AccordoServizioParteComuneServizioComposto servizioComposto;

  @XmlElement(name="azione",required=true,nillable=false)
  protected List<Azione> azione = new ArrayList<Azione>();

  /**
   * @deprecated Use method getAzioneList
   * @return List<Azione>
  */
  @Deprecated
  public List<Azione> getAzione() {
  	return this.azione;
  }

  /**
   * @deprecated Use method setAzioneList
   * @param azione List<Azione>
  */
  @Deprecated
  public void setAzione(List<Azione> azione) {
  	this.azione=azione;
  }

  /**
   * @deprecated Use method sizeAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAzione() {
  	return this.azione.size();
  }

  @XmlElement(name="port-type",required=true,nillable=false)
  protected List<PortType> portType = new ArrayList<PortType>();

  /**
   * @deprecated Use method getPortTypeList
   * @return List<PortType>
  */
  @Deprecated
  public List<PortType> getPortType() {
  	return this.portType;
  }

  /**
   * @deprecated Use method setPortTypeList
   * @param portType List<PortType>
  */
  @Deprecated
  public void setPortType(List<PortType> portType) {
  	this.portType=portType;
  }

  /**
   * @deprecated Use method sizePortTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePortType() {
  	return this.portType.size();
  }

  @XmlElement(name="allegato",required=true,nillable=false)
  protected List<Documento> allegato = new ArrayList<Documento>();

  /**
   * @deprecated Use method getAllegatoList
   * @return List<Documento>
  */
  @Deprecated
  public List<Documento> getAllegato() {
  	return this.allegato;
  }

  /**
   * @deprecated Use method setAllegatoList
   * @param allegato List<Documento>
  */
  @Deprecated
  public void setAllegato(List<Documento> allegato) {
  	this.allegato=allegato;
  }

  /**
   * @deprecated Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllegato() {
  	return this.allegato.size();
  }

  @XmlElement(name="specifica-semiformale",required=true,nillable=false)
  protected List<Documento> specificaSemiformale = new ArrayList<Documento>();

  /**
   * @deprecated Use method getSpecificaSemiformaleList
   * @return List<Documento>
  */
  @Deprecated
  public List<Documento> getSpecificaSemiformale() {
  	return this.specificaSemiformale;
  }

  /**
   * @deprecated Use method setSpecificaSemiformaleList
   * @param specificaSemiformale List<Documento>
  */
  @Deprecated
  public void setSpecificaSemiformale(List<Documento> specificaSemiformale) {
  	this.specificaSemiformale=specificaSemiformale;
  }

  /**
   * @deprecated Use method sizeSpecificaSemiformaleList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSpecificaSemiformale() {
  	return this.specificaSemiformale.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-package",required=false)
  protected java.lang.String statoPackage;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="privato",required=false)
  protected Boolean privato = new Boolean("false");

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-definitorio",required=false)
  protected byte[] byteWsdlDefinitorio;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-concettuale",required=false)
  protected byte[] byteWsdlConcettuale;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-logico-erogatore",required=false)
  protected byte[] byteWsdlLogicoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-logico-fruitore",required=false)
  protected byte[] byteWsdlLogicoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-specifica-conversazione-concettuale",required=false)
  protected byte[] byteSpecificaConversazioneConcettuale;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-specifica-conversazione-erogatore",required=false)
  protected byte[] byteSpecificaConversazioneErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-specifica-conversazione-fruitore",required=false)
  protected byte[] byteSpecificaConversazioneFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @XmlTransient
  protected java.lang.String _value_profiloCollaborazione;

  @XmlAttribute(name="profilo-collaborazione",required=true)
  protected ProfiloCollaborazione profiloCollaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-definitorio",required=false)
  protected java.lang.String wsdlDefinitorio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-concettuale",required=false)
  protected java.lang.String wsdlConcettuale;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-logico-erogatore",required=false)
  protected java.lang.String wsdlLogicoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-logico-fruitore",required=false)
  protected java.lang.String wsdlLogicoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="specifica-conversazione-concettuale",required=false)
  protected java.lang.String specificaConversazioneConcettuale;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="specifica-conversazione-erogatore",required=false)
  protected java.lang.String specificaConversazioneErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="specifica-conversazione-fruitore",required=false)
  protected java.lang.String specificaConversazioneFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="utilizzo-senza-azione",required=false)
  protected boolean utilizzoSenzaAzione = false;

  @XmlTransient
  protected java.lang.String _value_filtroDuplicati;

  @XmlAttribute(name="filtro-duplicati",required=false)
  protected StatoFunzionalita filtroDuplicati = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_confermaRicezione;

  @XmlAttribute(name="conferma-ricezione",required=false)
  protected StatoFunzionalita confermaRicezione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_idCollaborazione;

  @XmlAttribute(name="id-collaborazione",required=false)
  protected StatoFunzionalita idCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_consegnaInOrdine;

  @XmlAttribute(name="consegna-in-ordine",required=false)
  protected StatoFunzionalita consegnaInOrdine = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.String versione;

}
