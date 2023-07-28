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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for FatturaElettronicaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="FatturaElettronicaHeader" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}FatturaElettronicaHeaderType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="FatturaElettronicaBody" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}FatturaElettronicaBodyType" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="versione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}FormatoTrasmissioneType" use="required"/&gt;
 * 		&lt;attribute name="SistemaEmittente" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FatturaElettronicaType", 
  propOrder = {
  	"fatturaElettronicaHeader",
  	"fatturaElettronicaBody"
  }
)

@XmlRootElement(name = "FatturaElettronicaType")

public class FatturaElettronicaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaElettronicaType() {
    super();
  }

  public FatturaElettronicaHeaderType getFatturaElettronicaHeader() {
    return this.fatturaElettronicaHeader;
  }

  public void setFatturaElettronicaHeader(FatturaElettronicaHeaderType fatturaElettronicaHeader) {
    this.fatturaElettronicaHeader = fatturaElettronicaHeader;
  }

  public void addFatturaElettronicaBody(FatturaElettronicaBodyType fatturaElettronicaBody) {
    this.fatturaElettronicaBody.add(fatturaElettronicaBody);
  }

  public FatturaElettronicaBodyType getFatturaElettronicaBody(int index) {
    return this.fatturaElettronicaBody.get( index );
  }

  public FatturaElettronicaBodyType removeFatturaElettronicaBody(int index) {
    return this.fatturaElettronicaBody.remove( index );
  }

  public List<FatturaElettronicaBodyType> getFatturaElettronicaBodyList() {
    return this.fatturaElettronicaBody;
  }

  public void setFatturaElettronicaBodyList(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
    this.fatturaElettronicaBody=fatturaElettronicaBody;
  }

  public int sizeFatturaElettronicaBodyList() {
    return this.fatturaElettronicaBody.size();
  }

  public void setVersioneRawEnumValue(String value) {
    this.versione = (FormatoTrasmissioneType) FormatoTrasmissioneType.toEnumConstantFromString(value);
  }

  public String getVersioneRawEnumValue() {
    if(this.versione == null){
    	return null;
    }else{
    	return this.versione.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType getVersione() {
    return this.versione;
  }

  public void setVersione(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType versione) {
    this.versione = versione;
  }

  public java.lang.String getSistemaEmittente() {
    return this.sistemaEmittente;
  }

  public void setSistemaEmittente(java.lang.String sistemaEmittente) {
    this.sistemaEmittente = sistemaEmittente;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.FatturaElettronicaTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType.modelStaticInstance==null){
  			it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType.modelStaticInstance = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.FatturaElettronicaTypeModel();
	  }
  }
  public static it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.FatturaElettronicaTypeModel model(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType.modelStaticInstance;
  }


  @XmlElement(name="FatturaElettronicaHeader",required=true,nillable=false)
  protected FatturaElettronicaHeaderType fatturaElettronicaHeader;

  @XmlElement(name="FatturaElettronicaBody",required=true,nillable=false)
  private List<FatturaElettronicaBodyType> fatturaElettronicaBody = new ArrayList<>();

  /**
   * Use method getFatturaElettronicaBodyList
   * @return List&lt;FatturaElettronicaBodyType&gt;
  */
  public List<FatturaElettronicaBodyType> getFatturaElettronicaBody() {
  	return this.getFatturaElettronicaBodyList();
  }

  /**
   * Use method setFatturaElettronicaBodyList
   * @param fatturaElettronicaBody List&lt;FatturaElettronicaBodyType&gt;
  */
  public void setFatturaElettronicaBody(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
  	this.setFatturaElettronicaBodyList(fatturaElettronicaBody);
  }

  /**
   * Use method sizeFatturaElettronicaBodyList
   * @return lunghezza della lista
  */
  public int sizeFatturaElettronicaBody() {
  	return this.sizeFatturaElettronicaBodyList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String versioneRawEnumValue;

  @XmlAttribute(name="versione",required=true)
  protected FormatoTrasmissioneType versione;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlAttribute(name="SistemaEmittente",required=false)
  protected java.lang.String sistemaEmittente;

}
