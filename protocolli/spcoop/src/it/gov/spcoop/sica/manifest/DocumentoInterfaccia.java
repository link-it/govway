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
package it.gov.spcoop.sica.manifest;

import it.gov.spcoop.sica.manifest.constants.TipoDocumentoInterfaccia;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DocumentoInterfaccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoInterfaccia"&gt;
 * 		&lt;xsd:simpleContent&gt;
 * 			&lt;xsd:extension base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 * 				&lt;attribute name="tipo" type="{http://spcoop.gov.it/sica/manifest}TipoDocumentoInterfaccia" use="optional" default="WSDL"/&gt;
 * 			&lt;/xsd:extension&gt;
 * 		&lt;/xsd:simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoInterfaccia")

@XmlRootElement(name = "DocumentoInterfaccia")

public class DocumentoInterfaccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DocumentoInterfaccia() {
    super();
  }

  public String getBase() {
    if(this.base!=null && ("".equals(this.base)==false)){
		return this.base.trim();
	}else{
		return null;
	}

  }

  public void setBase(String base) {
    this.base=base;
  }

  public void setTipoRawEnumValue(String value) {
    this.tipo = (TipoDocumentoInterfaccia) TipoDocumentoInterfaccia.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public it.gov.spcoop.sica.manifest.constants.TipoDocumentoInterfaccia getTipo() {
    return this.tipo;
  }

  public void setTipo(it.gov.spcoop.sica.manifest.constants.TipoDocumentoInterfaccia tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @jakarta.xml.bind.annotation.XmlValue()
  public String base;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=false)
  protected TipoDocumentoInterfaccia tipo = (TipoDocumentoInterfaccia) TipoDocumentoInterfaccia.toEnumConstantFromString("WSDL");

}
