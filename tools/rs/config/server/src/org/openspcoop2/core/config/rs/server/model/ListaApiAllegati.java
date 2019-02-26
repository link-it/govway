package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItem;
import org.openspcoop2.core.config.rs.server.model.Lista;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ListaApiAllegati extends Lista {
  
  @Schema(required = true, description = "")
  private List<ApiAllegatoItem> items = new ArrayList<ApiAllegatoItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  @Valid
  public List<ApiAllegatoItem> getItems() {
    return this.items;
  }

  public void setItems(List<ApiAllegatoItem> items) {
    this.items = items;
  }

  public ListaApiAllegati items(List<ApiAllegatoItem> items) {
    this.items = items;
    return this;
  }

  public ListaApiAllegati addItemsItem(ApiAllegatoItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaApiAllegati {\n");
    sb.append("    ").append(ListaApiAllegati.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaApiAllegati.toIndentedString(this.items)).append("\n");
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
