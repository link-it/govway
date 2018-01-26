/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import backend.ecodex.org._1_1.SubmitResponse;
import backend.ecodex.org._1_1.FaultDetail;
import backend.ecodex.org._1_1.PayloadURLType;
import backend.ecodex.org._1_1.MessageStatusRequest;
import backend.ecodex.org._1_1.ErrorResultImpl;
import backend.ecodex.org._1_1.ListPendingMessagesResponse;
import backend.ecodex.org._1_1.StatusRequest;
import backend.ecodex.org._1_1.LargePayloadType;
import backend.ecodex.org._1_1.RetrieveMessageResponse;
import backend.ecodex.org._1_1.RetrieveMessageRequest;
import backend.ecodex.org._1_1.GetStatusRequest;
import backend.ecodex.org._1_1.SendResponse;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import backend.ecodex.org._1_1.SendRequestURL;
import backend.ecodex.org._1_1.GetErrorsRequest;
import backend.ecodex.org._1_1.DownloadMessageRequest;
import backend.ecodex.org._1_1.DownloadMessageResponse;
import backend.ecodex.org._1_1.ErrorResultImplArray;
import backend.ecodex.org._1_1.MessageErrorsRequest;
import backend.ecodex.org._1_1.SubmitRequest;
import backend.ecodex.org._1_1.Collection;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

/**     
 * XML Deserializer of beans
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractDeserializer {



	protected abstract Object _xmlToObj(InputStream is, Class<?> c) throws Exception;
	
	private Object xmlToObj(InputStream is,Class<?> c) throws DeserializerException{
		try{
			return this._xmlToObj(is, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(String fileName,Class<?> c) throws DeserializerException{
		try{
			return this.xmlToObj(new File(fileName), c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(), e);
		}
	}
	private Object xmlToObj(File file,Class<?> c) throws DeserializerException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
	}
	private Object xmlToObj(byte[] file,Class<?> c) throws DeserializerException{
		ByteArrayInputStream fin = null;
		try{
			fin = new ByteArrayInputStream(file);
			return this._xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e.getMessage(),e);
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
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
	 Object: sendResponse
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendResponse readSendResponse(String fileName) throws DeserializerException {
		return (SendResponse) this.xmlToObj(fileName, SendResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendResponse readSendResponse(File file) throws DeserializerException {
		return (SendResponse) this.xmlToObj(file, SendResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendResponse readSendResponse(InputStream in) throws DeserializerException {
		return (SendResponse) this.xmlToObj(in, SendResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendResponse readSendResponse(byte[] in) throws DeserializerException {
		return (SendResponse) this.xmlToObj(in, SendResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.SendResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendResponse readSendResponseFromString(String in) throws DeserializerException {
		return (SendResponse) this.xmlToObj(in.getBytes(), SendResponse.class);
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
	 Object: sendRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequest readSendRequest(String fileName) throws DeserializerException {
		return (SendRequest) this.xmlToObj(fileName, SendRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequest readSendRequest(File file) throws DeserializerException {
		return (SendRequest) this.xmlToObj(file, SendRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequest readSendRequest(InputStream in) throws DeserializerException {
		return (SendRequest) this.xmlToObj(in, SendRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequest readSendRequest(byte[] in) throws DeserializerException {
		return (SendRequest) this.xmlToObj(in, SendRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequest readSendRequestFromString(String in) throws DeserializerException {
		return (SendRequest) this.xmlToObj(in.getBytes(), SendRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: sendRequestURL
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequestURL readSendRequestURL(String fileName) throws DeserializerException {
		return (SendRequestURL) this.xmlToObj(fileName, SendRequestURL.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequestURL readSendRequestURL(File file) throws DeserializerException {
		return (SendRequestURL) this.xmlToObj(file, SendRequestURL.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequestURL readSendRequestURL(InputStream in) throws DeserializerException {
		return (SendRequestURL) this.xmlToObj(in, SendRequestURL.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequestURL readSendRequestURL(byte[] in) throws DeserializerException {
		return (SendRequestURL) this.xmlToObj(in, SendRequestURL.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @return Object type {@link backend.ecodex.org._1_1.SendRequestURL}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public SendRequestURL readSendRequestURLFromString(String in) throws DeserializerException {
		return (SendRequestURL) this.xmlToObj(in.getBytes(), SendRequestURL.class);
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
	 Object: downloadMessageRequest
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageRequest readDownloadMessageRequest(String fileName) throws DeserializerException {
		return (DownloadMessageRequest) this.xmlToObj(fileName, DownloadMessageRequest.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageRequest readDownloadMessageRequest(File file) throws DeserializerException {
		return (DownloadMessageRequest) this.xmlToObj(file, DownloadMessageRequest.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageRequest readDownloadMessageRequest(InputStream in) throws DeserializerException {
		return (DownloadMessageRequest) this.xmlToObj(in, DownloadMessageRequest.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageRequest readDownloadMessageRequest(byte[] in) throws DeserializerException {
		return (DownloadMessageRequest) this.xmlToObj(in, DownloadMessageRequest.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageRequest}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageRequest readDownloadMessageRequestFromString(String in) throws DeserializerException {
		return (DownloadMessageRequest) this.xmlToObj(in.getBytes(), DownloadMessageRequest.class);
	}	
	
	
	
	/*
	 =================================================================================
	 Object: downloadMessageResponse
	 =================================================================================
	*/
	
	/**
	 * Transform the xml in <var>fileName</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param fileName Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageResponse readDownloadMessageResponse(String fileName) throws DeserializerException {
		return (DownloadMessageResponse) this.xmlToObj(fileName, DownloadMessageResponse.class);
	}
	
	/**
	 * Transform the xml in <var>file</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param file Xml file to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageResponse readDownloadMessageResponse(File file) throws DeserializerException {
		return (DownloadMessageResponse) this.xmlToObj(file, DownloadMessageResponse.class);
	}
	
	/**
	 * Transform the input stream <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param in InputStream to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageResponse readDownloadMessageResponse(InputStream in) throws DeserializerException {
		return (DownloadMessageResponse) this.xmlToObj(in, DownloadMessageResponse.class);
	}	
	
	/**
	 * Transform the byte array <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param in Byte array to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageResponse readDownloadMessageResponse(byte[] in) throws DeserializerException {
		return (DownloadMessageResponse) this.xmlToObj(in, DownloadMessageResponse.class);
	}	
	
	/**
	 * Transform the String <var>in</var> in the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * 
	 * @param in String to use for the reconstruction of the object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @return Object type {@link backend.ecodex.org._1_1.DownloadMessageResponse}
	 * @throws DeserializerException The exception that is thrown when an error occurs during deserialization
	 */
	public DownloadMessageResponse readDownloadMessageResponseFromString(String in) throws DeserializerException {
		return (DownloadMessageResponse) this.xmlToObj(in.getBytes(), DownloadMessageResponse.class);
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
	
	
	

}
