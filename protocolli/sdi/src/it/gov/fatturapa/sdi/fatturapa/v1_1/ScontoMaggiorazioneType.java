/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoScontoMaggiorazioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for ScontoMaggiorazioneType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScontoMaggiorazioneType">
 * 		&lt;sequence>
 * 			&lt;element name="Tipo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}TipoScontoMaggiorazioneType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Percentuale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Importo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "ScontoMaggiorazioneType", 
  propOrder = {
  	"tipo",
  	"_decimalWrapper_percentuale",
  	"_decimalWrapper_importo"
  }
)

@XmlRootElement(name = "ScontoMaggiorazioneType")

public class ScontoMaggiorazioneType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ScontoMaggiorazioneType() {
  }

  public void set_value_tipo(String value) {
    this.tipo = (TipoScontoMaggiorazioneType) TipoScontoMaggiorazioneType.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoScontoMaggiorazioneType getTipo() {
    return this.tipo;
  }

  public void setTipo(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoScontoMaggiorazioneType tipo) {
    this.tipo = tipo;
  }

  public java.math.BigDecimal getPercentuale() {
    if(this._decimalWrapper_percentuale!=null){
		return (java.math.BigDecimal) this._decimalWrapper_percentuale.getObject(java.math.BigDecimal.class);
	}else{
		return this.percentuale;
	}
  }

  public void setPercentuale(java.math.BigDecimal percentuale) {
    if(percentuale!=null){
		this._decimalWrapper_percentuale = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,percentuale);
	}
  }

  public java.math.BigDecimal getImporto() {
    if(this._decimalWrapper_importo!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importo.getObject(java.math.BigDecimal.class);
	}else{
		return this.importo;
	}
  }

  public void setImporto(java.math.BigDecimal importo) {
    if(importo!=null){
		this._decimalWrapper_importo = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importo);
	}
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlElement(name="Tipo",required=true,nillable=false)
  protected TipoScontoMaggiorazioneType tipo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Percentuale",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_percentuale = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal percentuale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Importo",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importo = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importo;

}
