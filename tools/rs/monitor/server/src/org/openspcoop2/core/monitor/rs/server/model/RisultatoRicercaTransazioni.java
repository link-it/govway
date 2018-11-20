package org.openspcoop2.core.monitor.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import org.openspcoop2.core.monitor.rs.server.model.ListaPaginata;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneItem;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RisultatoRicercaTransazioni  {
  
  @Schema(required = true, description = "")
  private ListaPaginata listaPaginata = null;
  
  @Schema(required = true, description = "")
  private List<TransazioneItem> transazioni = new ArrayList<TransazioneItem>();
 /**
   * Get listaPaginata
   * @return listaPaginata
  **/
  @JsonProperty("listaPaginata")
  @NotNull
  public ListaPaginata getListaPaginata() {
    return this.listaPaginata;
  }

  public void setListaPaginata(ListaPaginata listaPaginata) {
    this.listaPaginata = listaPaginata;
  }

  public RisultatoRicercaTransazioni listaPaginata(ListaPaginata listaPaginata) {
    this.listaPaginata = listaPaginata;
    return this;
  }

 /**
   * Get transazioni
   * @return transazioni
  **/
  @JsonProperty("transazioni")
  @NotNull
  public List<TransazioneItem> getTransazioni() {
    return this.transazioni;
  }

  public void setTransazioni(List<TransazioneItem> transazioni) {
    this.transazioni = transazioni;
  }

  public RisultatoRicercaTransazioni transazioni(List<TransazioneItem> transazioni) {
    this.transazioni = transazioni;
    return this;
  }

  public RisultatoRicercaTransazioni addTransazioniItem(TransazioneItem transazioniItem) {
    this.transazioni.add(transazioniItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RisultatoRicercaTransazioni {\n");
    
    sb.append("    listaPaginata: ").append(RisultatoRicercaTransazioni.toIndentedString(this.listaPaginata)).append("\n");
    sb.append("    transazioni: ").append(RisultatoRicercaTransazioni.toIndentedString(this.transazioni)).append("\n");
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
