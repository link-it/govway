package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.Lista;
import org.openspcoop2.core.config.rs.server.model.RuoloItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ListaRuoli extends Lista {
  
  @Schema(required = true, description = "")
  private List<RuoloItem> items = new ArrayList<RuoloItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  @Valid
  public List<RuoloItem> getItems() {
    return this.items;
  }

  public void setItems(List<RuoloItem> items) {
    this.items = items;
  }

  public ListaRuoli items(List<RuoloItem> items) {
    this.items = items;
    return this;
  }

  public ListaRuoli addItemsItem(RuoloItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaRuoli {\n");
    sb.append("    ").append(ListaRuoli.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaRuoli.toIndentedString(this.items)).append("\n");
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
