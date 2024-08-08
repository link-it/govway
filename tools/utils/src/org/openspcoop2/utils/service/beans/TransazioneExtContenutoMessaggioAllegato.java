/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtContenutoMessaggioAllegato", propOrder =
    { "contenuto", "contentType", "contentId", "contentLocation", "headers"
})

@XmlRootElement(name="TransazioneExtContenutoMessaggioAllegato")
public class TransazioneExtContenutoMessaggioAllegato  {
  @XmlElement(name="contenuto", required = true)
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private byte[] contenuto = null;
  @XmlElement(name="content_type", required = true)
  
  @Schema(example = "application/json", requiredMode = Schema.RequiredMode.REQUIRED, description = "")
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
   * Get contenuto
   * @return contenuto
  **/
  @JsonProperty("contenuto")
  @NotNull
  @Valid
  public byte[] getContenuto() {
    return this.contenuto;
  }

  public void setContenuto(byte[] contenuto) {
    this.contenuto = contenuto;
  }

  public TransazioneExtContenutoMessaggioAllegato contenuto(byte[] contenuto) {
    this.contenuto = contenuto;
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

  public TransazioneExtContenutoMessaggioAllegato contentType(String contentType) {
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

  public TransazioneExtContenutoMessaggioAllegato contentId(String contentId) {
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

  public TransazioneExtContenutoMessaggioAllegato contentLocation(String contentLocation) {
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

  public TransazioneExtContenutoMessaggioAllegato headers(List<TransazioneContenutoMessaggioHeader> headers) {
    this.headers = headers;
    return this;
  }

  public TransazioneExtContenutoMessaggioAllegato addHeadersItem(TransazioneContenutoMessaggioHeader headersItem) {
    this.headers.add(headersItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtContenutoMessaggioAllegato {\n");
    
    sb.append("    contenuto: ").append(TransazioneExtContenutoMessaggioAllegato.toIndentedString(this.contenuto)).append("\n");
    sb.append("    contentType: ").append(TransazioneExtContenutoMessaggioAllegato.toIndentedString(this.contentType)).append("\n");
    sb.append("    contentId: ").append(TransazioneExtContenutoMessaggioAllegato.toIndentedString(this.contentId)).append("\n");
    sb.append("    contentLocation: ").append(TransazioneExtContenutoMessaggioAllegato.toIndentedString(this.contentLocation)).append("\n");
    sb.append("    headers: ").append(TransazioneExtContenutoMessaggioAllegato.toIndentedString(this.headers)).append("\n");
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
