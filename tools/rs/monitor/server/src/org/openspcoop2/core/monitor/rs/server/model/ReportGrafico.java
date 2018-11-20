package org.openspcoop2.core.monitor.rs.server.model;

import java.util.List;
import org.openspcoop2.core.monitor.rs.server.model.ReportGraficoCategorie;
import org.openspcoop2.core.monitor.rs.server.model.ReportGraficoDati;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportGrafico  {
  
  @Schema(example = "Distribuzione Esito dei messaggi", description = "")
  private String titolo = null;
  
  @Schema(example = "(dal 01 maggio 2017 al 27 aprile 2018 )", description = "")
  private String sottotitolo = null;
  
  @Schema(example = "Numero Esiti", description = "")
  private String yAxisLabel = null;
  
  @Schema(description = "")
  private List<ReportGraficoCategorie> categorie = null;
  
  @Schema(description = "")
  private List<ReportGraficoDati> dati = null;
 /**
   * Get titolo
   * @return titolo
  **/
  @JsonProperty("titolo")
  public String getTitolo() {
    return this.titolo;
  }

  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  public ReportGrafico titolo(String titolo) {
    this.titolo = titolo;
    return this;
  }

 /**
   * Get sottotitolo
   * @return sottotitolo
  **/
  @JsonProperty("sottotitolo")
  public String getSottotitolo() {
    return this.sottotitolo;
  }

  public void setSottotitolo(String sottotitolo) {
    this.sottotitolo = sottotitolo;
  }

  public ReportGrafico sottotitolo(String sottotitolo) {
    this.sottotitolo = sottotitolo;
    return this;
  }

 /**
   * Get yAxisLabel
   * @return yAxisLabel
  **/
  @JsonProperty("yAxisLabel")
  public String getYAxisLabel() {
    return this.yAxisLabel;
  }

  public void setYAxisLabel(String yAxisLabel) {
    this.yAxisLabel = yAxisLabel;
  }

  public ReportGrafico yAxisLabel(String yAxisLabel) {
    this.yAxisLabel = yAxisLabel;
    return this;
  }

 /**
   * Get categorie
   * @return categorie
  **/
  @JsonProperty("categorie")
  public List<ReportGraficoCategorie> getCategorie() {
    return this.categorie;
  }

  public void setCategorie(List<ReportGraficoCategorie> categorie) {
    this.categorie = categorie;
  }

  public ReportGrafico categorie(List<ReportGraficoCategorie> categorie) {
    this.categorie = categorie;
    return this;
  }

  public ReportGrafico addCategorieItem(ReportGraficoCategorie categorieItem) {
    this.categorie.add(categorieItem);
    return this;
  }

 /**
   * Get dati
   * @return dati
  **/
  @JsonProperty("dati")
  public List<ReportGraficoDati> getDati() {
    return this.dati;
  }

  public void setDati(List<ReportGraficoDati> dati) {
    this.dati = dati;
  }

  public ReportGrafico dati(List<ReportGraficoDati> dati) {
    this.dati = dati;
    return this;
  }

  public ReportGrafico addDatiItem(ReportGraficoDati datiItem) {
    this.dati.add(datiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportGrafico {\n");
    
    sb.append("    titolo: ").append(ReportGrafico.toIndentedString(this.titolo)).append("\n");
    sb.append("    sottotitolo: ").append(ReportGrafico.toIndentedString(this.sottotitolo)).append("\n");
    sb.append("    yAxisLabel: ").append(ReportGrafico.toIndentedString(this.yAxisLabel)).append("\n");
    sb.append("    categorie: ").append(ReportGrafico.toIndentedString(this.categorie)).append("\n");
    sb.append("    dati: ").append(ReportGrafico.toIndentedString(this.dati)).append("\n");
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
