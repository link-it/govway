/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package backend.ecodex.org._1_1.utils.serializer;

import org.openspcoop2.generic_project.exception.DeserializerException;

import backend.ecodex.org._1_1.MessageErrorsRequest;
import backend.ecodex.org._1_1.GetStatusRequest;
import backend.ecodex.org._1_1.LargePayloadType;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.ErrorResultImpl;
import backend.ecodex.org._1_1.ErrorResultImplArray;
import backend.ecodex.org._1_1.SubmitResponse;
import backend.ecodex.org._1_1.FaultDetail;
import backend.ecodex.org._1_1.Collection;
import backend.ecodex.org._1_1.RetrieveMessageRequest;
import backend.ecodex.org._1_1.RetrieveMessageResponse;
import backend.ecodex.org._1_1.ListPendingMessagesResponse;
import backend.ecodex.org._1_1.MessageStatusRequest;
import backend.ecodex.org._1_1.SubmitRequest;
import backend.ecodex.org._1_1.GetErrorsRequest;
import backend.ecodex.org._1_1.StatusRequest;
import backend.ecodex.org._1_1.PayloadURLType;

import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer extends org.openspcoop2.generic_project.serializer.AbstractDeserializerBase {



	/*
	 =================================================================================
	 Object: messageErrorsRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageErrorsRequest readMessageErrorsRequest(String fileName) throws DeserializerException {
		return (MessageErrorsRequest) this.xmlToObj(fileName, MessageErrorsRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageErrorsRequest readMessageErrorsRequest(File file) throws DeserializerException {
		return (MessageErrorsRequest) this.xmlToObj(file, MessageErrorsRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageErrorsRequest readMessageErrorsRequest(InputStream in) throws DeserializerException {
		return (MessageErrorsRequest) this.xmlToObj(in, MessageErrorsRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageErrorsRequest readMessageErrorsRequest(byte[] in) throws DeserializerException {
		return (MessageErrorsRequest) this.xmlToObj(in, MessageErrorsRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageErrorsRequest readMessageErrorsRequestFromString(String in) throws DeserializerException {
		return (MessageErrorsRequest) this.xmlToObj(in.getBytes(), MessageErrorsRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: getStatusRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetStatusRequest readGetStatusRequest(String fileName) throws DeserializerException {
		return (GetStatusRequest) this.xmlToObj(fileName, GetStatusRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetStatusRequest readGetStatusRequest(File file) throws DeserializerException {
		return (GetStatusRequest) this.xmlToObj(file, GetStatusRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetStatusRequest readGetStatusRequest(InputStream in) throws DeserializerException {
		return (GetStatusRequest) this.xmlToObj(in, GetStatusRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetStatusRequest readGetStatusRequest(byte[] in) throws DeserializerException {
		return (GetStatusRequest) this.xmlToObj(in, GetStatusRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetStatusRequest readGetStatusRequestFromString(String in) throws DeserializerException {
		return (GetStatusRequest) this.xmlToObj(in.getBytes(), GetStatusRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: LargePayloadType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LargePayloadType readLargePayloadType(String fileName) throws DeserializerException {
		return (LargePayloadType) this.xmlToObj(fileName, LargePayloadType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LargePayloadType readLargePayloadType(File file) throws DeserializerException {
		return (LargePayloadType) this.xmlToObj(file, LargePayloadType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LargePayloadType readLargePayloadType(InputStream in) throws DeserializerException {
		return (LargePayloadType) this.xmlToObj(in, LargePayloadType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LargePayloadType readLargePayloadType(byte[] in) throws DeserializerException {
		return (LargePayloadType) this.xmlToObj(in, LargePayloadType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.LargePayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public LargePayloadType readLargePayloadTypeFromString(String in) throws DeserializerException {
		return (LargePayloadType) this.xmlToObj(in.getBytes(), LargePayloadType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PayloadType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadType readPayloadType(String fileName) throws DeserializerException {
		return (PayloadType) this.xmlToObj(fileName, PayloadType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadType readPayloadType(File file) throws DeserializerException {
		return (PayloadType) this.xmlToObj(file, PayloadType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadType readPayloadType(InputStream in) throws DeserializerException {
		return (PayloadType) this.xmlToObj(in, PayloadType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadType readPayloadType(byte[] in) throws DeserializerException {
		return (PayloadType) this.xmlToObj(in, PayloadType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadType readPayloadTypeFromString(String in) throws DeserializerException {
		return (PayloadType) this.xmlToObj(in.getBytes(), PayloadType.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: errorResultImpl
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImpl readErrorResultImpl(String fileName) throws DeserializerException {
		return (ErrorResultImpl) this.xmlToObj(fileName, ErrorResultImpl.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImpl readErrorResultImpl(File file) throws DeserializerException {
		return (ErrorResultImpl) this.xmlToObj(file, ErrorResultImpl.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImpl readErrorResultImpl(InputStream in) throws DeserializerException {
		return (ErrorResultImpl) this.xmlToObj(in, ErrorResultImpl.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImpl readErrorResultImpl(byte[] in) throws DeserializerException {
		return (ErrorResultImpl) this.xmlToObj(in, ErrorResultImpl.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImpl}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImpl readErrorResultImplFromString(String in) throws DeserializerException {
		return (ErrorResultImpl) this.xmlToObj(in.getBytes(), ErrorResultImpl.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: errorResultImplArray
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImplArray readErrorResultImplArray(String fileName) throws DeserializerException {
		return (ErrorResultImplArray) this.xmlToObj(fileName, ErrorResultImplArray.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImplArray readErrorResultImplArray(File file) throws DeserializerException {
		return (ErrorResultImplArray) this.xmlToObj(file, ErrorResultImplArray.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImplArray readErrorResultImplArray(InputStream in) throws DeserializerException {
		return (ErrorResultImplArray) this.xmlToObj(in, ErrorResultImplArray.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImplArray readErrorResultImplArray(byte[] in) throws DeserializerException {
		return (ErrorResultImplArray) this.xmlToObj(in, ErrorResultImplArray.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @return Object type {@link backend.ecodex.org._1_1.ErrorResultImplArray}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ErrorResultImplArray readErrorResultImplArrayFromString(String in) throws DeserializerException {
		return (ErrorResultImplArray) this.xmlToObj(in.getBytes(), ErrorResultImplArray.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: submitResponse
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitResponse readSubmitResponse(String fileName) throws DeserializerException {
		return (SubmitResponse) this.xmlToObj(fileName, SubmitResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitResponse readSubmitResponse(File file) throws DeserializerException {
		return (SubmitResponse) this.xmlToObj(file, SubmitResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitResponse readSubmitResponse(InputStream in) throws DeserializerException {
		return (SubmitResponse) this.xmlToObj(in, SubmitResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitResponse readSubmitResponse(byte[] in) throws DeserializerException {
		return (SubmitResponse) this.xmlToObj(in, SubmitResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitResponse readSubmitResponseFromString(String in) throws DeserializerException {
		return (SubmitResponse) this.xmlToObj(in.getBytes(), SubmitResponse.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: FaultDetail
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @return Object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FaultDetail readFaultDetail(String fileName) throws DeserializerException {
		return (FaultDetail) this.xmlToObj(fileName, FaultDetail.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @return Object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FaultDetail readFaultDetail(File file) throws DeserializerException {
		return (FaultDetail) this.xmlToObj(file, FaultDetail.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @return Object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FaultDetail readFaultDetail(InputStream in) throws DeserializerException {
		return (FaultDetail) this.xmlToObj(in, FaultDetail.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @return Object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FaultDetail readFaultDetail(byte[] in) throws DeserializerException {
		return (FaultDetail) this.xmlToObj(in, FaultDetail.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @return Object type {@link backend.ecodex.org._1_1.FaultDetail}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public FaultDetail readFaultDetailFromString(String in) throws DeserializerException {
		return (FaultDetail) this.xmlToObj(in.getBytes(), FaultDetail.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: collection
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.Collection}
	 * @return Object type {@link backend.ecodex.org._1_1.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(String fileName) throws DeserializerException {
		return (Collection) this.xmlToObj(fileName, Collection.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.Collection}
	 * @return Object type {@link backend.ecodex.org._1_1.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(File file) throws DeserializerException {
		return (Collection) this.xmlToObj(file, Collection.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.Collection}
	 * @return Object type {@link backend.ecodex.org._1_1.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(InputStream in) throws DeserializerException {
		return (Collection) this.xmlToObj(in, Collection.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.Collection}
	 * @return Object type {@link backend.ecodex.org._1_1.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollection(byte[] in) throws DeserializerException {
		return (Collection) this.xmlToObj(in, Collection.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.Collection}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.Collection}
	 * @return Object type {@link backend.ecodex.org._1_1.Collection}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public Collection readCollectionFromString(String in) throws DeserializerException {
		return (Collection) this.xmlToObj(in.getBytes(), Collection.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: retrieveMessageRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageRequest readRetrieveMessageRequest(String fileName) throws DeserializerException {
		return (RetrieveMessageRequest) this.xmlToObj(fileName, RetrieveMessageRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageRequest readRetrieveMessageRequest(File file) throws DeserializerException {
		return (RetrieveMessageRequest) this.xmlToObj(file, RetrieveMessageRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageRequest readRetrieveMessageRequest(InputStream in) throws DeserializerException {
		return (RetrieveMessageRequest) this.xmlToObj(in, RetrieveMessageRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageRequest readRetrieveMessageRequest(byte[] in) throws DeserializerException {
		return (RetrieveMessageRequest) this.xmlToObj(in, RetrieveMessageRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageRequest readRetrieveMessageRequestFromString(String in) throws DeserializerException {
		return (RetrieveMessageRequest) this.xmlToObj(in.getBytes(), RetrieveMessageRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: retrieveMessageResponse
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageResponse readRetrieveMessageResponse(String fileName) throws DeserializerException {
		return (RetrieveMessageResponse) this.xmlToObj(fileName, RetrieveMessageResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageResponse readRetrieveMessageResponse(File file) throws DeserializerException {
		return (RetrieveMessageResponse) this.xmlToObj(file, RetrieveMessageResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageResponse readRetrieveMessageResponse(InputStream in) throws DeserializerException {
		return (RetrieveMessageResponse) this.xmlToObj(in, RetrieveMessageResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageResponse readRetrieveMessageResponse(byte[] in) throws DeserializerException {
		return (RetrieveMessageResponse) this.xmlToObj(in, RetrieveMessageResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.RetrieveMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public RetrieveMessageResponse readRetrieveMessageResponseFromString(String in) throws DeserializerException {
		return (RetrieveMessageResponse) this.xmlToObj(in.getBytes(), RetrieveMessageResponse.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: listPendingMessagesResponse
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListPendingMessagesResponse readListPendingMessagesResponse(String fileName) throws DeserializerException {
		return (ListPendingMessagesResponse) this.xmlToObj(fileName, ListPendingMessagesResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListPendingMessagesResponse readListPendingMessagesResponse(File file) throws DeserializerException {
		return (ListPendingMessagesResponse) this.xmlToObj(file, ListPendingMessagesResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListPendingMessagesResponse readListPendingMessagesResponse(InputStream in) throws DeserializerException {
		return (ListPendingMessagesResponse) this.xmlToObj(in, ListPendingMessagesResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListPendingMessagesResponse readListPendingMessagesResponse(byte[] in) throws DeserializerException {
		return (ListPendingMessagesResponse) this.xmlToObj(in, ListPendingMessagesResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.ListPendingMessagesResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public ListPendingMessagesResponse readListPendingMessagesResponseFromString(String in) throws DeserializerException {
		return (ListPendingMessagesResponse) this.xmlToObj(in.getBytes(), ListPendingMessagesResponse.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: messageStatusRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageStatusRequest readMessageStatusRequest(String fileName) throws DeserializerException {
		return (MessageStatusRequest) this.xmlToObj(fileName, MessageStatusRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageStatusRequest readMessageStatusRequest(File file) throws DeserializerException {
		return (MessageStatusRequest) this.xmlToObj(file, MessageStatusRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageStatusRequest readMessageStatusRequest(InputStream in) throws DeserializerException {
		return (MessageStatusRequest) this.xmlToObj(in, MessageStatusRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageStatusRequest readMessageStatusRequest(byte[] in) throws DeserializerException {
		return (MessageStatusRequest) this.xmlToObj(in, MessageStatusRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.MessageStatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public MessageStatusRequest readMessageStatusRequestFromString(String in) throws DeserializerException {
		return (MessageStatusRequest) this.xmlToObj(in.getBytes(), MessageStatusRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: submitRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitRequest readSubmitRequest(String fileName) throws DeserializerException {
		return (SubmitRequest) this.xmlToObj(fileName, SubmitRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitRequest readSubmitRequest(File file) throws DeserializerException {
		return (SubmitRequest) this.xmlToObj(file, SubmitRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitRequest readSubmitRequest(InputStream in) throws DeserializerException {
		return (SubmitRequest) this.xmlToObj(in, SubmitRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitRequest readSubmitRequest(byte[] in) throws DeserializerException {
		return (SubmitRequest) this.xmlToObj(in, SubmitRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SubmitRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SubmitRequest readSubmitRequestFromString(String in) throws DeserializerException {
		return (SubmitRequest) this.xmlToObj(in.getBytes(), SubmitRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: getErrorsRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetErrorsRequest readGetErrorsRequest(String fileName) throws DeserializerException {
		return (GetErrorsRequest) this.xmlToObj(fileName, GetErrorsRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetErrorsRequest readGetErrorsRequest(File file) throws DeserializerException {
		return (GetErrorsRequest) this.xmlToObj(file, GetErrorsRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetErrorsRequest readGetErrorsRequest(InputStream in) throws DeserializerException {
		return (GetErrorsRequest) this.xmlToObj(in, GetErrorsRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetErrorsRequest readGetErrorsRequest(byte[] in) throws DeserializerException {
		return (GetErrorsRequest) this.xmlToObj(in, GetErrorsRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.GetErrorsRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public GetErrorsRequest readGetErrorsRequestFromString(String in) throws DeserializerException {
		return (GetErrorsRequest) this.xmlToObj(in.getBytes(), GetErrorsRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: statusRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatusRequest readStatusRequest(String fileName) throws DeserializerException {
		return (StatusRequest) this.xmlToObj(fileName, StatusRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatusRequest readStatusRequest(File file) throws DeserializerException {
		return (StatusRequest) this.xmlToObj(file, StatusRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatusRequest readStatusRequest(InputStream in) throws DeserializerException {
		return (StatusRequest) this.xmlToObj(in, StatusRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatusRequest readStatusRequest(byte[] in) throws DeserializerException {
		return (StatusRequest) this.xmlToObj(in, StatusRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.StatusRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public StatusRequest readStatusRequestFromString(String in) throws DeserializerException {
		return (StatusRequest) this.xmlToObj(in.getBytes(), StatusRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: PayloadURLType
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadURLType readPayloadURLType(String fileName) throws DeserializerException {
		return (PayloadURLType) this.xmlToObj(fileName, PayloadURLType.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadURLType readPayloadURLType(File file) throws DeserializerException {
		return (PayloadURLType) this.xmlToObj(file, PayloadURLType.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadURLType readPayloadURLType(InputStream in) throws DeserializerException {
		return (PayloadURLType) this.xmlToObj(in, PayloadURLType.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadURLType readPayloadURLType(byte[] in) throws DeserializerException {
		return (PayloadURLType) this.xmlToObj(in, PayloadURLType.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @return Object type {@link backend.ecodex.org._1_1.PayloadURLType}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public PayloadURLType readPayloadURLTypeFromString(String in) throws DeserializerException {
		return (PayloadURLType) this.xmlToObj(in.getBytes(), PayloadURLType.class);
	}	
	
	
	

}
