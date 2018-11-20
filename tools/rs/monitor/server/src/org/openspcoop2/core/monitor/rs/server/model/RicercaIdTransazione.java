package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.FiltroIdTransazione;

public class RicercaIdTransazione extends FiltroIdTransazione {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaIdTransazione {\n");
    sb.append("    ").append(RicercaIdTransazione.toIndentedString(super.toString())).append("\n");
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
