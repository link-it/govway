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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for FatturaElettronicaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaType">
 * 		&lt;sequence>
 * 			&lt;element name="FatturaElettronicaHeader" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}FatturaElettronicaHeaderType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="FatturaElettronicaBody" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}FatturaElettronicaBodyType" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" use="required"/>
 * &lt;/complexType>
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

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static it.gov.fatturapa.sdi.fatturapa.v1_0.model.FatturaElettronicaTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType.modelStaticInstance = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.FatturaElettronicaTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.fatturapa.v1_0.model.FatturaElettronicaTypeModel model(){
	  if(it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType.modelStaticInstance;
  }


  @XmlElement(name="FatturaElettronicaHeader",required=true,nillable=false)
  protected FatturaElettronicaHeaderType fatturaElettronicaHeader;

  @XmlElement(name="FatturaElettronicaBody",required=true,nillable=false)
  protected List<FatturaElettronicaBodyType> fatturaElettronicaBody = new ArrayList<FatturaElettronicaBodyType>();

  /**
   * @deprecated Use method getFatturaElettronicaBodyList
   * @return List<FatturaElettronicaBodyType>
  */
  @Deprecated
  public List<FatturaElettronicaBodyType> getFatturaElettronicaBody() {
  	return this.fatturaElettronicaBody;
  }

  /**
   * @deprecated Use method setFatturaElettronicaBodyList
   * @param fatturaElettronicaBody List<FatturaElettronicaBodyType>
  */
  @Deprecated
  public void setFatturaElettronicaBody(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
  	this.fatturaElettronicaBody=fatturaElettronicaBody;
  }

  /**
   * @deprecated Use method sizeFatturaElettronicaBodyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFatturaElettronicaBody() {
  	return this.fatturaElettronicaBody.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}
