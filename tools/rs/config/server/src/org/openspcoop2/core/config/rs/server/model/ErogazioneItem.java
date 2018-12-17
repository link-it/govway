package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.ApiImplItem;

public class ErogazioneItem extends ApiImplItem {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneItem {\n");
    sb.append("    ").append(ErogazioneItem.toIndentedString(super.toString())).append("\n");
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
