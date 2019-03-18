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
import org.openspcoop2.utils.service.beans.TransazioneContenutoMessaggioHeader;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtContenutoMessaggioBodyMultipart", propOrder =
    { "contentType", "contentId", "contentLocation", "headers"
})

@XmlRootElement(name="TransazioneExtContenutoMessaggioBodyMultipart")
public class TransazioneExtContenutoMessaggioBodyMultipart  {
  @XmlElement(name="content_type")
  
  @Schema(example = "application/json", description = "")
  private String contentType = null;
  @XmlElement(name="content_id")
  
  @Schema(description = "")
  private String contentId = null;
  @XmlElement(name="content_location")
  
  @Schema(description = "")
  private String contentLocation = null;
  @XmlElement(name="headers")
  
  @Schema(description = "")
  private List<TransazioneContenutoMessaggioHeader> headers = null;
 /**
   * Get contentType
   * @return contentType
  **/
  @JsonProperty("content_type")
  @Valid
  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public TransazioneExtContenutoMessaggioBodyMultipart contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

 /**
   * Get contentId
   * @return contentId
  **/
  @JsonProperty("content_id")
  @Valid
  public String getContentId() {
    return this.contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public TransazioneExtContenutoMessaggioBodyMultipart contentId(String contentId) {
    this.contentId = contentId;
    return this;
  }

 /**
   * Get contentLocation
   * @return contentLocation
  **/
  @JsonProperty("content_location")
  @Valid
  public String getContentLocation() {
    return this.contentLocation;
  }

  public void setContentLocation(String contentLocation) {
    this.contentLocation = contentLocation;
  }

  public TransazioneExtContenutoMessaggioBodyMultipart contentLocation(String contentLocation) {
    this.contentLocation = contentLocation;
    return this;
  }

 /**
   * Get headers
   * @return headers
  **/
  @JsonProperty("headers")
  @Valid
  public List<TransazioneContenutoMessaggioHeader> getHeaders() {
    return this.headers;
  }

  public void setHeaders(List<TransazioneContenutoMessaggioHeader> headers) {
    this.headers = headers;
  }

  public TransazioneExtContenutoMessaggioBodyMultipart headers(List<TransazioneContenutoMessaggioHeader> headers) {
    this.headers = headers;
    return this;
  }

  public TransazioneExtContenutoMessaggioBodyMultipart addHeadersItem(TransazioneContenutoMessaggioHeader headersItem) {
    this.headers.add(headersItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtContenutoMessaggioBodyMultipart {\n");
    
    sb.append("    contentType: ").append(TransazioneExtContenutoMessaggioBodyMultipart.toIndentedString(this.contentType)).append("\n");
    sb.append("    contentId: ").append(TransazioneExtContenutoMessaggioBodyMultipart.toIndentedString(this.contentId)).append("\n");
    sb.append("    contentLocation: ").append(TransazioneExtContenutoMessaggioBodyMultipart.toIndentedString(this.contentLocation)).append("\n");
    sb.append("    headers: ").append(TransazioneExtContenutoMessaggioBodyMultipart.toIndentedString(this.headers)).append("\n");
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
