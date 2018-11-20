package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.BaseItem;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseSoggettoItem extends BaseItem {
  
  @Schema(example = "EnteInterno", description = "")
  private String soggetto = null;
 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public BaseSoggettoItem soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseSoggettoItem {\n");
    sb.append("    ").append(BaseSoggettoItem.toIndentedString(super.toString())).append("\n");
    sb.append("    soggetto: ").append(BaseSoggettoItem.toIndentedString(this.soggetto)).append("\n");
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
