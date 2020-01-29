/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 *
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class BaseCredenziali  {
  
  @Schema(description = "")
  private ModalitaAccessoEnum modalitaAccesso = null;
  
  @Schema(example = "{\"username\":\"user\",\"password\":\"pwd\"}", description = "")
  private Object credenziali = null;
 /**
   * Get modalitaAccesso
   * @return modalitaAccesso
  **/
  @JsonProperty("modalita_accesso")
  @Valid
  public ModalitaAccessoEnum getModalitaAccesso() {
    return this.modalitaAccesso;
  }

  public void setModalitaAccesso(ModalitaAccessoEnum modalitaAccesso) {
    this.modalitaAccesso = modalitaAccesso;
  }

  public BaseCredenziali modalitaAccesso(ModalitaAccessoEnum modalitaAccesso) {
    this.modalitaAccesso = modalitaAccesso;
    return this;
  }

 /**
   * Get credenziali
   * @return credenziali
  **/
  @JsonProperty("credenziali")
  @Valid
  public Object getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(Object credenziali) {
    this.credenziali = credenziali;
  }

  public BaseCredenziali credenziali(Object credenziali) {
    this.credenziali = credenziali;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseCredenziali {\n");
    
    sb.append("    modalitaAccesso: ").append(BaseCredenziali.toIndentedString(this.modalitaAccesso)).append("\n");
    sb.append("    credenziali: ").append(BaseCredenziali.toIndentedString(this.credenziali)).append("\n");
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
