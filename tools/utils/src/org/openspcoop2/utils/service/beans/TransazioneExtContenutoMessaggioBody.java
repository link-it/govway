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
package org.openspcoop2.utils.service.beans;

import java.util.List;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioBodyMultipart;
import org.openspcoop2.utils.service.beans.TransazioneExtContenutoMessaggioPorzioneBody;
import org.openspcoop2.utils.service.beans.TransazioneMessaggioFormatoEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtContenutoMessaggioBody", propOrder =
    { "formato", "contentType", "multipart", "contenutiBody"
})

@XmlRootElement(name="TransazioneExtContenutoMessaggioBody")
public class TransazioneExtContenutoMessaggioBody  {
  @XmlElement(name="formato")
  
  @Schema(description = "")
  private TransazioneMessaggioFormatoEnum formato = null;
  @XmlElement(name="content_type", required = true)
  
  @Schema(example = "application/json", required = true, description = "")
  private String contentType = null;
  @XmlElement(name="multipart")
  
  @Schema(description = "")
  private TransazioneExtContenutoMessaggioBodyMultipart multipart = null;
  @XmlElement(name="contenuti_body")
  
  @Schema(description = "")
  private List<TransazioneExtContenutoMessaggioPorzioneBody> contenutiBody = null;
 /**
   * Get formato
   * @return formato
  **/
  @JsonProperty("formato")
  @Valid
  public TransazioneMessaggioFormatoEnum getFormato() {
    return this.formato;
  }

  public void setFormato(TransazioneMessaggioFormatoEnum formato) {
    this.formato = formato;
  }

  public TransazioneExtContenutoMessaggioBody formato(TransazioneMessaggioFormatoEnum formato) {
    this.formato = formato;
    return this;
  }

 /**
   * Get contentType
   * @return contentType
  **/
  @JsonProperty("content_type")
  @NotNull
  @Valid
  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public TransazioneExtContenutoMessaggioBody contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

 /**
   * Get multipart
   * @return multipart
  **/
  @JsonProperty("multipart")
  @Valid
  public TransazioneExtContenutoMessaggioBodyMultipart getMultipart() {
    return this.multipart;
  }

  public void setMultipart(TransazioneExtContenutoMessaggioBodyMultipart multipart) {
    this.multipart = multipart;
  }

  public TransazioneExtContenutoMessaggioBody multipart(TransazioneExtContenutoMessaggioBodyMultipart multipart) {
    this.multipart = multipart;
    return this;
  }

 /**
   * Get contenutiBody
   * @return contenutiBody
  **/
  @JsonProperty("contenuti_body")
  @Valid
  public List<TransazioneExtContenutoMessaggioPorzioneBody> getContenutiBody() {
    return this.contenutiBody;
  }

  public void setContenutiBody(List<TransazioneExtContenutoMessaggioPorzioneBody> contenutiBody) {
    this.contenutiBody = contenutiBody;
  }

  public TransazioneExtContenutoMessaggioBody contenutiBody(List<TransazioneExtContenutoMessaggioPorzioneBody> contenutiBody) {
    this.contenutiBody = contenutiBody;
    return this;
  }

  public TransazioneExtContenutoMessaggioBody addContenutiBodyItem(TransazioneExtContenutoMessaggioPorzioneBody contenutiBodyItem) {
    this.contenutiBody.add(contenutiBodyItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtContenutoMessaggioBody {\n");
    
    sb.append("    formato: ").append(TransazioneExtContenutoMessaggioBody.toIndentedString(this.formato)).append("\n");
    sb.append("    contentType: ").append(TransazioneExtContenutoMessaggioBody.toIndentedString(this.contentType)).append("\n");
    sb.append("    multipart: ").append(TransazioneExtContenutoMessaggioBody.toIndentedString(this.multipart)).append("\n");
    sb.append("    contenutiBody: ").append(TransazioneExtContenutoMessaggioBody.toIndentedString(this.contenutiBody)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
