package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaPaginata  {
  
  @Schema(example = "100", required = true, description = "")
  private Integer results = null;
  
  @Schema(example = "1", required = true, description = "")
  private Integer page = null;
  
  @Schema(example = "4", required = true, description = "")
  private Integer pages = null;
  
  @Schema(example = "25", required = true, description = "")
  private Integer pageResults = null;
  
  @Schema(example = "/risorsa?offset=2", required = true, description = "")
  private String nextResults = null;
 /**
   * Get results
   * @return results
  **/
  @JsonProperty("results")
  @NotNull
  public Integer getResults() {
    return this.results;
  }

  public void setResults(Integer results) {
    this.results = results;
  }

  public ListaPaginata results(Integer results) {
    this.results = results;
    return this;
  }

 /**
   * Get page
   * @return page
  **/
  @JsonProperty("page")
  @NotNull
  public Integer getPage() {
    return this.page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public ListaPaginata page(Integer page) {
    this.page = page;
    return this;
  }

 /**
   * Get pages
   * @return pages
  **/
  @JsonProperty("pages")
  @NotNull
  public Integer getPages() {
    return this.pages;
  }

  public void setPages(Integer pages) {
    this.pages = pages;
  }

  public ListaPaginata pages(Integer pages) {
    this.pages = pages;
    return this;
  }

 /**
   * Get pageResults
   * @return pageResults
  **/
  @JsonProperty("page_results")
  @NotNull
  public Integer getPageResults() {
    return this.pageResults;
  }

  public void setPageResults(Integer pageResults) {
    this.pageResults = pageResults;
  }

  public ListaPaginata pageResults(Integer pageResults) {
    this.pageResults = pageResults;
    return this;
  }

 /**
   * Get nextResults
   * @return nextResults
  **/
  @JsonProperty("next_results")
  @NotNull
  public String getNextResults() {
    return this.nextResults;
  }

  public void setNextResults(String nextResults) {
    this.nextResults = nextResults;
  }

  public ListaPaginata nextResults(String nextResults) {
    this.nextResults = nextResults;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListaPaginata {\n");
    
    sb.append("    results: ").append(ListaPaginata.toIndentedString(this.results)).append("\n");
    sb.append("    page: ").append(ListaPaginata.toIndentedString(this.page)).append("\n");
    sb.append("    pages: ").append(ListaPaginata.toIndentedString(this.pages)).append("\n");
    sb.append("    pageResults: ").append(ListaPaginata.toIndentedString(this.pageResults)).append("\n");
    sb.append("    nextResults: ").append(ListaPaginata.toIndentedString(this.nextResults)).append("\n");
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
