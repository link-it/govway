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
package org.openspcoop2.utils.service.beans;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtContenutoMessaggio", propOrder =
    { "informazioniBody", "attachments"
})


public class TransazioneExtContenutoMessaggio extends TransazioneContenutoMessaggio {
  @XmlElement(name="informazioni_body")
  
  @Schema(description = "")
  private TransazioneExtContenutoMessaggioBody informazioniBody = null;
  @XmlElement(name="attachments")
  
  @Schema(description = "")
  private List<TransazioneExtContenutoMessaggioAllegato> attachments = null;
 /**
   * Get informazioniBody
   * @return informazioniBody
  **/
  @JsonProperty("informazioni_body")
  @Valid
  public TransazioneExtContenutoMessaggioBody getInformazioniBody() {
    return this.informazioniBody;
  }

  public void setInformazioniBody(TransazioneExtContenutoMessaggioBody informazioniBody) {
    this.informazioniBody = informazioniBody;
  }

  public TransazioneExtContenutoMessaggio informazioniBody(TransazioneExtContenutoMessaggioBody informazioniBody) {
    this.informazioniBody = informazioniBody;
    return this;
  }

 /**
   * Get attachments
   * @return attachments
  **/
  @JsonProperty("attachments")
  @Valid
  public List<TransazioneExtContenutoMessaggioAllegato> getAttachments() {
    return this.attachments;
  }

  public void setAttachments(List<TransazioneExtContenutoMessaggioAllegato> attachments) {
    this.attachments = attachments;
  }

  public TransazioneExtContenutoMessaggio attachments(List<TransazioneExtContenutoMessaggioAllegato> attachments) {
    this.attachments = attachments;
    return this;
  }

  public TransazioneExtContenutoMessaggio addAttachmentsItem(TransazioneExtContenutoMessaggioAllegato attachmentsItem) {
    this.attachments.add(attachmentsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtContenutoMessaggio {\n");
    sb.append("    ").append(TransazioneExtContenutoMessaggio.toIndentedString(super.toString())).append("\n");
    sb.append("    informazioniBody: ").append(TransazioneExtContenutoMessaggio.toIndentedString(this.informazioniBody)).append("\n");
    sb.append("    attachments: ").append(TransazioneExtContenutoMessaggio.toIndentedString(this.attachments)).append("\n");
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
