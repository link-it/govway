/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.utils.service.beans.Lista;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ListaRateLimitingPolicy extends Lista {
  
  @Schema(required = true, description = "")
  private List<RateLimitingPolicyItem> items = new ArrayList<>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  @Valid
  public List<RateLimitingPolicyItem> getItems() {
    return this.items;
  }

  public void setItems(List<RateLimitingPolicyItem> items) {
    this.items = items;
  }

  public ListaRateLimitingPolicy items(List<RateLimitingPolicyItem> items) {
    this.items = items;
    return this;
  }

  public ListaRateLimitingPolicy addItemsItem(RateLimitingPolicyItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaRateLimitingPolicy {\n");
    sb.append("    ").append(ListaRateLimitingPolicy.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaRateLimitingPolicy.toIndentedString(this.items)).append("\n");
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
