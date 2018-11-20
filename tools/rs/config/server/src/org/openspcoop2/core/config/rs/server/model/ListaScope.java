package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ListaPaginata;
import org.openspcoop2.core.config.rs.server.model.ScopeItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaScope extends ListaPaginata {
  
  @Schema(required = true, description = "")
  private List<ScopeItem> items = new ArrayList<ScopeItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  public List<ScopeItem> getItems() {
    return this.items;
  }

  public void setItems(List<ScopeItem> items) {
    this.items = items;
  }

  public ListaScope items(List<ScopeItem> items) {
    this.items = items;
    return this;
  }

  public ListaScope addItemsItem(ScopeItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaScope {\n");
    sb.append("    ").append(ListaScope.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaScope.toIndentedString(this.items)).append("\n");
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
