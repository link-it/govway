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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SpecificaCoordinamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaCoordinamento">
 * 		&lt;sequence>
 * 			&lt;element name="documentoCoordinamento" type="{http://spcoop.gov.it/sica/manifest}DocumentoCoordinamento" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "SpecificaCoordinamento", 
  propOrder = {
  	"documentoCoordinamento"
  }
)

@XmlRootElement(name = "SpecificaCoordinamento")

public class SpecificaCoordinamento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaCoordinamento() {
  }

  public void addDocumentoCoordinamento(DocumentoCoordinamento documentoCoordinamento) {
    this.documentoCoordinamento.add(documentoCoordinamento);
  }

  public DocumentoCoordinamento getDocumentoCoordinamento(int index) {
    return this.documentoCoordinamento.get( index );
  }

  public DocumentoCoordinamento removeDocumentoCoordinamento(int index) {
    return this.documentoCoordinamento.remove( index );
  }

  public List<DocumentoCoordinamento> getDocumentoCoordinamentoList() {
    return this.documentoCoordinamento;
  }

  public void setDocumentoCoordinamentoList(List<DocumentoCoordinamento> documentoCoordinamento) {
    this.documentoCoordinamento=documentoCoordinamento;
  }

  public int sizeDocumentoCoordinamentoList() {
    return this.documentoCoordinamento.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="documentoCoordinamento",required=true,nillable=false)
  protected List<DocumentoCoordinamento> documentoCoordinamento = new ArrayList<DocumentoCoordinamento>();

  /**
   * @deprecated Use method getDocumentoCoordinamentoList
   * @return List<DocumentoCoordinamento>
  */
  @Deprecated
  public List<DocumentoCoordinamento> getDocumentoCoordinamento() {
  	return this.documentoCoordinamento;
  }

  /**
   * @deprecated Use method setDocumentoCoordinamentoList
   * @param documentoCoordinamento List<DocumentoCoordinamento>
  */
  @Deprecated
  public void setDocumentoCoordinamento(List<DocumentoCoordinamento> documentoCoordinamento) {
  	this.documentoCoordinamento=documentoCoordinamento;
  }

  /**
   * @deprecated Use method sizeDocumentoCoordinamentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDocumentoCoordinamento() {
  	return this.documentoCoordinamento.size();
  }

}
