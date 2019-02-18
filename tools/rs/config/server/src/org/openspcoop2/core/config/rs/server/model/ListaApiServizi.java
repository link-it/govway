package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.Lista;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaApiServizi extends Lista {
  
  @Schema(required = true, description = "")
  private List<ApiServizio> items = new ArrayList<ApiServizio>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  public List<ApiServizio> getItems() {
    return this.items;
  }

  public void setItems(List<ApiServizio> items) {
    this.items = items;
  }

  public ListaApiServizi items(List<ApiServizio> items) {
    this.items = items;
    return this;
  }

  public ListaApiServizi addItemsItem(ApiServizio itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaApiServizi {\n");
    sb.append("    ").append(ListaApiServizi.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaApiServizi.toIndentedString(this.items)).append("\n");
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