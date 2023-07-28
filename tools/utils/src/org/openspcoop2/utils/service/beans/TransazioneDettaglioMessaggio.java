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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioMessaggio", propOrder =
    { "id", "idApplicativo"
})

@XmlRootElement(name="TransazioneDettaglioMessaggio")
public class TransazioneDettaglioMessaggio  {
  @XmlElement(name="id")
  
  @Schema(example = "eba4355e-403f-4e75-8d56-0751710409c2", description = "")
  private String id = null;
  @XmlElement(name="id_applicativo")
  
  @Schema(description = "")
  private String idApplicativo = null;
 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @Valid
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TransazioneDettaglioMessaggio id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get idApplicativo
   * @return idApplicativo
  **/
  @JsonProperty("id_applicativo")
  @Valid
  public String getIdApplicativo() {
    return this.idApplicativo;
  }

  public void setIdApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
  }

  public TransazioneDettaglioMessaggio idApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioMessaggio {\n");
    
    sb.append("    id: ").append(TransazioneDettaglioMessaggio.toIndentedString(this.id)).append("\n");
    sb.append("    idApplicativo: ").append(TransazioneDettaglioMessaggio.toIndentedString(this.idApplicativo)).append("\n");
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
