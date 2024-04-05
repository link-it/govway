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
package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ControlloAccessiIdentificazioneAttributi extends ApiImplConfigurazioneStato {
  
  @Schema(required = true, description = "indica se la gestione del token è abilitata o meno")
 /**
   * indica se la gestione del token è abilitata o meno  
  **/
  private Boolean abilitato = null;
  
  @Schema(description = "")
  private List<ControlloAccessiAttributeAuthority> attributeAuthority = null;
 /**
   * indica se la gestione del token è abilitata o meno
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @NotNull
  @Valid
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public ControlloAccessiIdentificazioneAttributi abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }

 /**
   * Get attributeAuthority
   * @return attributeAuthority
  **/
  @JsonProperty("attributeAuthority")
  @Valid
  public List<ControlloAccessiAttributeAuthority> getAttributeAuthority() {
    return this.attributeAuthority;
  }

  public void setAttributeAuthority(List<ControlloAccessiAttributeAuthority> attributeAuthority) {
    this.attributeAuthority = attributeAuthority;
  }

  public ControlloAccessiIdentificazioneAttributi attributeAuthority(List<ControlloAccessiAttributeAuthority> attributeAuthority) {
    this.attributeAuthority = attributeAuthority;
    return this;
  }

  public ControlloAccessiIdentificazioneAttributi addAttributeAuthorityItem(ControlloAccessiAttributeAuthority attributeAuthorityItem) {
    this.attributeAuthority.add(attributeAuthorityItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ControlloAccessiIdentificazioneAttributi {\n");
    sb.append("    ").append(ControlloAccessiIdentificazioneAttributi.toIndentedString(super.toString())).append("\n");
    sb.append("    abilitato: ").append(ControlloAccessiIdentificazioneAttributi.toIndentedString(this.abilitato)).append("\n");
    sb.append("    attributeAuthority: ").append(ControlloAccessiIdentificazioneAttributi.toIndentedString(this.attributeAuthority)).append("\n");
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
