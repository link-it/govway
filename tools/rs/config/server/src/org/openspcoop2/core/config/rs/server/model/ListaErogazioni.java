package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ErogazioneItem;
import org.openspcoop2.core.config.rs.server.model.Lista;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaErogazioni extends Lista {
  
  @Schema(required = true, description = "")
  private List<ErogazioneItem> items = new ArrayList<ErogazioneItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  public List<ErogazioneItem> getItems() {
    return this.items;
  }

  public void setItems(List<ErogazioneItem> items) {
    this.items = items;
  }

  public ListaErogazioni items(List<ErogazioneItem> items) {
    this.items = items;
    return this;
  }

  public ListaErogazioni addItemsItem(ErogazioneItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaErogazioni {\n");
    sb.append("    ").append(ListaErogazioni.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaErogazioni.toIndentedString(this.items)).append("\n");
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
