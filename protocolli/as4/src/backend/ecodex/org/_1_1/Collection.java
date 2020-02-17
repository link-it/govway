/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package backend.ecodex.org._1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for collection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="collection"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="FaultDetail" type="{http://org.ecodex.backend/1_1/}FaultDetail" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="retrieveMessageRequest" type="{http://org.ecodex.backend/1_1/}retrieveMessageRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="retrieveMessageResponse" type="{http://org.ecodex.backend/1_1/}retrieveMessageResponse" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="listPendingMessagesResponse" type="{http://org.ecodex.backend/1_1/}listPendingMessagesResponse" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="messageErrorsRequest" type="{http://org.ecodex.backend/1_1/}messageErrorsRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="messageStatusRequest" type="{http://org.ecodex.backend/1_1/}messageStatusRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="submitRequest" type="{http://org.ecodex.backend/1_1/}submitRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="submitResponse" type="{http://org.ecodex.backend/1_1/}submitResponse" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="getStatusRequest" type="{http://org.ecodex.backend/1_1/}getStatusRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="getErrorsRequest" type="{http://org.ecodex.backend/1_1/}getErrorsRequest" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="getMessageErrorsResponse" type="{http://org.ecodex.backend/1_1/}errorResultImplArray" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "collection", 
  propOrder = {
  	"faultDetail",
  	"retrieveMessageRequest",
  	"retrieveMessageResponse",
  	"listPendingMessagesResponse",
  	"messageErrorsRequest",
  	"messageStatusRequest",
  	"submitRequest",
  	"submitResponse",
  	"getStatusRequest",
  	"getErrorsRequest",
  	"getMessageErrorsResponse"
  }
)

@XmlRootElement(name = "collection")

public class Collection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Collection() {
  }

  public FaultDetail getFaultDetail() {
    return this.faultDetail;
  }

  public void setFaultDetail(FaultDetail faultDetail) {
    this.faultDetail = faultDetail;
  }

  public RetrieveMessageRequest getRetrieveMessageRequest() {
    return this.retrieveMessageRequest;
  }

  public void setRetrieveMessageRequest(RetrieveMessageRequest retrieveMessageRequest) {
    this.retrieveMessageRequest = retrieveMessageRequest;
  }

  public RetrieveMessageResponse getRetrieveMessageResponse() {
    return this.retrieveMessageResponse;
  }

  public void setRetrieveMessageResponse(RetrieveMessageResponse retrieveMessageResponse) {
    this.retrieveMessageResponse = retrieveMessageResponse;
  }

  public ListPendingMessagesResponse getListPendingMessagesResponse() {
    return this.listPendingMessagesResponse;
  }

  public void setListPendingMessagesResponse(ListPendingMessagesResponse listPendingMessagesResponse) {
    this.listPendingMessagesResponse = listPendingMessagesResponse;
  }

  public MessageErrorsRequest getMessageErrorsRequest() {
    return this.messageErrorsRequest;
  }

  public void setMessageErrorsRequest(MessageErrorsRequest messageErrorsRequest) {
    this.messageErrorsRequest = messageErrorsRequest;
  }

  public MessageStatusRequest getMessageStatusRequest() {
    return this.messageStatusRequest;
  }

  public void setMessageStatusRequest(MessageStatusRequest messageStatusRequest) {
    this.messageStatusRequest = messageStatusRequest;
  }

  public SubmitRequest getSubmitRequest() {
    return this.submitRequest;
  }

  public void setSubmitRequest(SubmitRequest submitRequest) {
    this.submitRequest = submitRequest;
  }

  public SubmitResponse getSubmitResponse() {
    return this.submitResponse;
  }

  public void setSubmitResponse(SubmitResponse submitResponse) {
    this.submitResponse = submitResponse;
  }

  public GetStatusRequest getGetStatusRequest() {
    return this.getStatusRequest;
  }

  public void setGetStatusRequest(GetStatusRequest getStatusRequest) {
    this.getStatusRequest = getStatusRequest;
  }

  public GetErrorsRequest getGetErrorsRequest() {
    return this.getErrorsRequest;
  }

  public void setGetErrorsRequest(GetErrorsRequest getErrorsRequest) {
    this.getErrorsRequest = getErrorsRequest;
  }

  public ErrorResultImplArray getGetMessageErrorsResponse() {
    return this.getMessageErrorsResponse;
  }

  public void setGetMessageErrorsResponse(ErrorResultImplArray getMessageErrorsResponse) {
    this.getMessageErrorsResponse = getMessageErrorsResponse;
  }

  private static final long serialVersionUID = 1L;

  private static backend.ecodex.org._1_1.model.CollectionModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(backend.ecodex.org._1_1.Collection.modelStaticInstance==null){
  			backend.ecodex.org._1_1.Collection.modelStaticInstance = new backend.ecodex.org._1_1.model.CollectionModel();
	  }
  }
  public static backend.ecodex.org._1_1.model.CollectionModel model(){
	  if(backend.ecodex.org._1_1.Collection.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return backend.ecodex.org._1_1.Collection.modelStaticInstance;
  }


  @XmlElement(name="FaultDetail",required=true,nillable=false)
  protected FaultDetail faultDetail;

  @XmlElement(name="retrieveMessageRequest",required=true,nillable=false)
  protected RetrieveMessageRequest retrieveMessageRequest;

  @XmlElement(name="retrieveMessageResponse",required=true,nillable=false)
  protected RetrieveMessageResponse retrieveMessageResponse;

  @XmlElement(name="listPendingMessagesResponse",required=true,nillable=false)
  protected ListPendingMessagesResponse listPendingMessagesResponse;

  @XmlElement(name="messageErrorsRequest",required=true,nillable=false)
  protected MessageErrorsRequest messageErrorsRequest;

  @XmlElement(name="messageStatusRequest",required=true,nillable=false)
  protected MessageStatusRequest messageStatusRequest;

  @XmlElement(name="submitRequest",required=true,nillable=false)
  protected SubmitRequest submitRequest;

  @XmlElement(name="submitResponse",required=true,nillable=false)
  protected SubmitResponse submitResponse;

  @XmlElement(name="getStatusRequest",required=true,nillable=false)
  protected GetStatusRequest getStatusRequest;

  @XmlElement(name="getErrorsRequest",required=true,nillable=false)
  protected GetErrorsRequest getErrorsRequest;

  @XmlElement(name="getMessageErrorsResponse",required=true,nillable=false)
  protected ErrorResultImplArray getMessageErrorsResponse;

}
