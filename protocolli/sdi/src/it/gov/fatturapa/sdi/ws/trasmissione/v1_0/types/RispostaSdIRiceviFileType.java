/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.constants.ErroreInvioType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for rispostaSdIRiceviFile_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rispostaSdIRiceviFile_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataOraRicezione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Errore" type="{http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types}erroreInvio_Type" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "rispostaSdIRiceviFile_Type", 
  propOrder = {
  	"identificativoSdI",
  	"dataOraRicezione",
  	"errore"
  }
)

@XmlRootElement(name = "rispostaSdIRiceviFile_Type")

public class RispostaSdIRiceviFileType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RispostaSdIRiceviFileType() {
  }

  public java.lang.String getIdentificativoSdI() {
    return this.identificativoSdI;
  }

  public void setIdentificativoSdI(java.lang.String identificativoSdI) {
    this.identificativoSdI = identificativoSdI;
  }

  public java.util.Date getDataOraRicezione() {
    return this.dataOraRicezione;
  }

  public void setDataOraRicezione(java.util.Date dataOraRicezione) {
    this.dataOraRicezione = dataOraRicezione;
  }

  public void set_value_errore(String value) {
    this.errore = (ErroreInvioType) ErroreInvioType.toEnumConstantFromString(value);
  }

  public String get_value_errore() {
    if(this.errore == null){
    	return null;
    }else{
    	return this.errore.toString();
    }
  }

  public it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.constants.ErroreInvioType getErrore() {
    return this.errore;
  }

  public void setErrore(it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.constants.ErroreInvioType errore) {
    this.errore = errore;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.RispostaSdIRiceviFileTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType.modelStaticInstance = new it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.RispostaSdIRiceviFileTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.RispostaSdIRiceviFileTypeModel model(){
	  if(it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraRicezione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraRicezione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_errore;

  @XmlElement(name="Errore",required=false,nillable=false)
  protected ErroreInvioType errore;

}
