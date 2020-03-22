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
package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatistica;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RicercaBaseStatisticaSoggetti extends RicercaBaseStatistica {
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroErogazione.class, name = "erogazione"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroFruizione.class, name = "fruizione"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroApiQualsiasi.class, name = "qualsiasi")  })
  private OneOfRicercaBaseStatisticaSoggettiApi api = null;
 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @Valid
  public OneOfRicercaBaseStatisticaSoggettiApi getApi() {
    return this.api;
  }

  public void setApi(OneOfRicercaBaseStatisticaSoggettiApi api) {
    this.api = api;
  }

  public RicercaBaseStatisticaSoggetti api(OneOfRicercaBaseStatisticaSoggettiApi api) {
    this.api = api;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaBaseStatisticaSoggetti {\n");
    sb.append("    ").append(RicercaBaseStatisticaSoggetti.toIndentedString(super.toString())).append("\n");
    sb.append("    api: ").append(RicercaBaseStatisticaSoggetti.toIndentedString(this.api)).append("\n");
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
