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
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniSoggetto;
import org.openspcoop2.utils.service.beans.TransazioneInformazioniApi;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtInformazioniApiBase", propOrder =
    { "informazioniErogatore", "tipo", "tags"
})


public class TransazioneExtInformazioniApiBase extends TransazioneInformazioniApi {
  @XmlElement(name="informazioni_erogatore", required = true)
  
  @Schema(required = true, description = "")
  private TransazioneExtInformazioniSoggetto informazioniErogatore = null;
  @XmlElement(name="tipo", required = true)
  
  @Schema(required = true, description = "")
  private String tipo = null;
  @XmlElement(name="tags")
  
  @Schema(description = "")
  private List<String> tags = null;
 /**
   * Get informazioniErogatore
   * @return informazioniErogatore
  **/
  @JsonProperty("informazioni_erogatore")
  @NotNull
  @Valid
  public TransazioneExtInformazioniSoggetto getInformazioniErogatore() {
    return this.informazioniErogatore;
  }

  public void setInformazioniErogatore(TransazioneExtInformazioniSoggetto informazioniErogatore) {
    this.informazioniErogatore = informazioniErogatore;
  }

  public TransazioneExtInformazioniApiBase informazioniErogatore(TransazioneExtInformazioniSoggetto informazioniErogatore) {
    this.informazioniErogatore = informazioniErogatore;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
 @Size(max=20)  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public TransazioneExtInformazioniApiBase tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get tags
   * @return tags
  **/
  @JsonProperty("tags")
  @Valid
  public List<String> getTags() {
    return this.tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public TransazioneExtInformazioniApiBase tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public TransazioneExtInformazioniApiBase addTagsItem(String tagsItem) {
    this.tags.add(tagsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniApiBase {\n");
    sb.append("    ").append(TransazioneExtInformazioniApiBase.toIndentedString(super.toString())).append("\n");
    sb.append("    informazioniErogatore: ").append(TransazioneExtInformazioniApiBase.toIndentedString(this.informazioniErogatore)).append("\n");
    sb.append("    tipo: ").append(TransazioneExtInformazioniApiBase.toIndentedString(this.tipo)).append("\n");
    sb.append("    tags: ").append(TransazioneExtInformazioniApiBase.toIndentedString(this.tags)).append("\n");
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
