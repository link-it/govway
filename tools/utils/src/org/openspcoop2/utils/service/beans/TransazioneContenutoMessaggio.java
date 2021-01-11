/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneContenutoMessaggio", propOrder =
    { "headers", "body"
})

@XmlRootElement(name="TransazioneContenutoMessaggio")
public class TransazioneContenutoMessaggio  {
  @XmlElement(name="headers")
  
  @Schema(description = "")
  private List<TransazioneContenutoMessaggioHeader> headers = null;
  @XmlElement(name="body")
  
  @Schema(description = "")
  private byte[] body = null;
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

  public TransazioneContenutoMessaggio headers(List<TransazioneContenutoMessaggioHeader> headers) {
    this.headers = headers;
    return this;
  }

  public TransazioneContenutoMessaggio addHeadersItem(TransazioneContenutoMessaggioHeader headersItem) {
    this.headers.add(headersItem);
    return this;
  }

 /**
   * Get body
   * @return body
  **/
  @JsonProperty("body")
  @Valid
  public byte[] getBody() {
    return this.body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public TransazioneContenutoMessaggio body(byte[] body) {
    this.body = body;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneContenutoMessaggio {\n");
    
    sb.append("    headers: ").append(TransazioneContenutoMessaggio.toIndentedString(this.headers)).append("\n");
    sb.append("    body: ").append(TransazioneContenutoMessaggio.toIndentedString(this.body)).append("\n");
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
