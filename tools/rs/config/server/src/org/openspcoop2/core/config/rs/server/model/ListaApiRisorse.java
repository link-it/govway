package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.Lista;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaApiRisorse extends Lista {
  
  @Schema(required = true, description = "")
  private List<ApiRisorsa> items = new ArrayList<ApiRisorsa>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  public List<ApiRisorsa> getItems() {
    return this.items;
  }

  public void setItems(List<ApiRisorsa> items) {
    this.items = items;
  }

  public ListaApiRisorse items(List<ApiRisorsa> items) {
    this.items = items;
    return this;
  }

  public ListaApiRisorse addItemsItem(ApiRisorsa itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaApiRisorse {\n");
    sb.append("    ").append(ListaApiRisorse.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaApiRisorse.toIndentedString(this.items)).append("\n");
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
