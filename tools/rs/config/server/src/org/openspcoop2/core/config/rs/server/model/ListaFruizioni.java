package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.FruizioneItem;
import org.openspcoop2.core.config.rs.server.model.Lista;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ListaFruizioni extends Lista {
  
  @Schema(required = true, description = "")
  private List<FruizioneItem> items = new ArrayList<FruizioneItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  @Valid
  public List<FruizioneItem> getItems() {
    return this.items;
  }

  public void setItems(List<FruizioneItem> items) {
    this.items = items;
  }

  public ListaFruizioni items(List<FruizioneItem> items) {
    this.items = items;
    return this;
  }

  public ListaFruizioni addItemsItem(FruizioneItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaFruizioni {\n");
    sb.append("    ").append(ListaFruizioni.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaFruizioni.toIndentedString(this.items)).append("\n");
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
