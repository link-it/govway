package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.config.rs.server.model.ApplicativoItem;
import org.openspcoop2.core.config.rs.server.model.ListaPaginata;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaApplicativi extends ListaPaginata {
  
  @Schema(required = true, description = "")
  private List<ApplicativoItem> items = new ArrayList<ApplicativoItem>();
 /**
   * Get items
   * @return items
  **/
  @JsonProperty("items")
  @NotNull
  public List<ApplicativoItem> getItems() {
    return this.items;
  }

  public void setItems(List<ApplicativoItem> items) {
    this.items = items;
  }

  public ListaApplicativi items(List<ApplicativoItem> items) {
    this.items = items;
    return this;
  }

  public ListaApplicativi addItemsItem(ApplicativoItem itemsItem) {
    this.items.add(itemsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaApplicativi {\n");
    sb.append("    ").append(ListaApplicativi.toIndentedString(super.toString())).append("\n");
    sb.append("    items: ").append(ListaApplicativi.toIndentedString(this.items)).append("\n");
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
