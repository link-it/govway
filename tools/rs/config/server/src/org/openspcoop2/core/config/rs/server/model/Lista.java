package org.openspcoop2.core.config.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Lista  {
  
  @Schema(required = true, description = "How many items to return at one time")
 /**
   * How many items to return at one time  
  **/
  private Integer limit = null;
  
  @Schema(required = true, description = "The zero-ary offset index into the results")
 /**
   * The zero-ary offset index into the results  
  **/
  private Long offset = null;
  
  @Schema(required = true, description = "Number of items matching the filter criteria")
 /**
   * Number of items matching the filter criteria  
  **/
  private Long total = null;
  
  @Schema(description = "Link to first result page. Null if you are already on first page.")
 /**
   * Link to first result page. Null if you are already on first page.  
  **/
  private String first = null;
  
  @Schema(description = "Link to next result page. Null if you are on last page.")
 /**
   * Link to next result page. Null if you are on last page.  
  **/
  private String next = null;
  
  @Schema(description = "Link to previous result page. Null if you are on first page.")
 /**
   * Link to previous result page. Null if you are on first page.  
  **/
  private String prev = null;
  
  @Schema(description = "Link to last result page. Null if you are already on last page.")
 /**
   * Link to last result page. Null if you are already on last page.  
  **/
  private String last = null;
 /**
   * How many items to return at one time
   * @return limit
  **/
  @JsonProperty("limit")
  @NotNull
  @Valid
  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Lista limit(Integer limit) {
    this.limit = limit;
    return this;
  }

 /**
   * The zero-ary offset index into the results
   * @return offset
  **/
  @JsonProperty("offset")
  @NotNull
  @Valid
  public Long getOffset() {
    return this.offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public Lista offset(Long offset) {
    this.offset = offset;
    return this;
  }

 /**
   * Number of items matching the filter criteria
   * minimum: 0
   * @return total
  **/
  @JsonProperty("total")
  @NotNull
  @Valid
 @Min(0L)  public Long getTotal() {
    return this.total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Lista total(Long total) {
    this.total = total;
    return this;
  }

 /**
   * Link to first result page. Null if you are already on first page.
   * @return first
  **/
  @JsonProperty("first")
  @Valid
  public String getFirst() {
    return this.first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public Lista first(String first) {
    this.first = first;
    return this;
  }

 /**
   * Link to next result page. Null if you are on last page.
   * @return next
  **/
  @JsonProperty("next")
  @Valid
  public String getNext() {
    return this.next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public Lista next(String next) {
    this.next = next;
    return this;
  }

 /**
   * Link to previous result page. Null if you are on first page.
   * @return prev
  **/
  @JsonProperty("prev")
  @Valid
  public String getPrev() {
    return this.prev;
  }

  public void setPrev(String prev) {
    this.prev = prev;
  }

  public Lista prev(String prev) {
    this.prev = prev;
    return this;
  }

 /**
   * Link to last result page. Null if you are already on last page.
   * @return last
  **/
  @JsonProperty("last")
  @Valid
  public String getLast() {
    return this.last;
  }

  public void setLast(String last) {
    this.last = last;
  }

  public Lista last(String last) {
    this.last = last;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Lista {\n");
    
    sb.append("    limit: ").append(Lista.toIndentedString(this.limit)).append("\n");
    sb.append("    offset: ").append(Lista.toIndentedString(this.offset)).append("\n");
    sb.append("    total: ").append(Lista.toIndentedString(this.total)).append("\n");
    sb.append("    first: ").append(Lista.toIndentedString(this.first)).append("\n");
    sb.append("    next: ").append(Lista.toIndentedString(this.next)).append("\n");
    sb.append("    prev: ").append(Lista.toIndentedString(this.prev)).append("\n");
    sb.append("    last: ").append(Lista.toIndentedString(this.last)).append("\n");
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
