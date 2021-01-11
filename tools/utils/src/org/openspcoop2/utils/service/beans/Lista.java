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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Lista extends ListaSenzaTotale {
  
  @Schema(required = true, description = "Number of items matching the filter criteria")
 /**
   * Number of items matching the filter criteria  
  **/
  private Long total = null;
  
  @Schema(description = "Link to last result page. Null if you are already on last page.")
 /**
   * Link to last result page. Null if you are already on last page.  
  **/
  private String last = null;
 /**
   * Number of items matching the filter criteria
   * minimum: 0
   * @return total
  **/
  @JsonProperty("total")
  @NotNull
  @Valid
 @Min(0L)  public Long getTotal() {
    return this.total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Lista total(Long total) {
    this.total = total;
    return this;
  }

 /**
   * Link to last result page. Null if you are already on last page.
   * @return last
  **/
  @JsonProperty("last")
  @Valid
  public String getLast() {
    return this.last;
  }

  public void setLast(String last) {
    this.last = last;
  }

  public Lista last(String last) {
    this.last = last;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Lista {\n");
    sb.append("    ").append(Lista.toIndentedString(super.toString())).append("\n");
    sb.append("    total: ").append(Lista.toIndentedString(this.total)).append("\n");
    sb.append("    last: ").append(Lista.toIndentedString(this.last)).append("\n");
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
