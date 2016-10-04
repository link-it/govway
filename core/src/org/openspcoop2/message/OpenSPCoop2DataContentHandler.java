/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.message;

import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.utils.resources.Loader;


/**
 * DataContentHandler per la gestione degli attachments
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2DataContentHandler implements DataContentHandler{

	public static final String OPENSPCOOP2_SIGNATURE = "===SIGNATURE=OPENSPCOOP2===";
	
	public OpenSPCoop2DataContentHandler(){}

	public static Object getContent(java.io.InputStream inputstream) throws IOException {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] bRead = new byte[8094];
			int byteLetti = 0;
			while( (byteLetti =inputstream.read(bRead))!= -1 ){
				bout.write(bRead, 0, byteLetti);
			}
			bout.flush();
			bout.close();
			
			if(bout.size() <= OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.length()){
				throw new Exception("OpenSPCoop2DataContentHandler Signature non presente (length is too small)");
			}
			boolean giaCodificato = true;
			String attach = bout.toString();
			for(int i=0; i<OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.length(); i++){	
				if( (attach.charAt(i)) !=  OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.charAt(i) ){
					giaCodificato = false;
					break;
				}
			}
			if(giaCodificato==false){
				throw new Exception("OpenSPCoop2DataContentHandler Signature non presente");
			}else{
				attach = attach.substring(OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.length(),attach.length());
			}
			
			OpenSPCoop2DataContentHandlerInputStream bin = new OpenSPCoop2DataContentHandlerInputStream(Base64.decode(attach));
			return bin;

		}catch(Exception e){
			throw new IOException("@@@ OpenSPCoop2DataContentHandler.getContent() error: "+e.getMessage());
		}
	}
	
	@Override
	public Object getContent(DataSource datasource) throws IOException{
		try{
			java.io.InputStream inputstream = datasource.getInputStream();
			return OpenSPCoop2DataContentHandler.getContent(inputstream);

		}catch(Exception e){
			throw new IOException("@@@ OpenSPCoop2DataContentHandler.getContent() error: "+e.getMessage());
		}
	}

	@Override
	public Object getTransferData(DataFlavor dataflavor, DataSource datasource)
	throws IOException
	{
		//logger.info("@@@ BinDataContentHandler.getTransferData: non implementato");
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		DataFlavor adataflavor[] = new DataFlavor[1];
		try
		{
			adataflavor[0] = new ActivationDataFlavor(Loader.getInstance().forName("org.openspcoop2.message.OpenSPCoop2DataContentHandler"), 
					Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP, "OpenSPCoop2AttachmentsTunnel");
		}
		catch(Exception exception) { }
		return adataflavor;
	}

	@Override
	public void writeTo(Object obj, String s, OutputStream outputstream)
	throws IOException
	{
		try{
			byte content[] = null;
			if(obj instanceof InputStream){
				InputStream inputstream = (InputStream) obj;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] bRead = new byte[8094];
				int byteLetti = 0;
				while( (byteLetti =inputstream.read(bRead))!= -1 ){
					bout.write(bRead, 0, byteLetti);
				}
				bout.flush();
				bout.close();
				content = bout.toByteArray();
			}else{
				content = (byte[])obj;
			}
			// Deve essere codificato solo una volta!!
			boolean giaCodificato = true;
			if( !(content.length<OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.length()+1) ){
				for(int i=0; i<OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.length(); i++){	
					if( ((char)content[i]) !=  OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE.charAt(i) ){
						giaCodificato = false;
						break;
					}
				}
			}
			
			if(giaCodificato){
				outputstream.write(content);
			}else{
				String encoded =  OpenSPCoop2DataContentHandler.OPENSPCOOP2_SIGNATURE + Base64.encode(content);
				outputstream.write(encoded.getBytes());
			}

		}
		catch(Exception exception)
		{
			throw new IOException("@@@ OpenSPCoop2DataContentHandler.writeTo: Unable to run the Binary decoding on a stream " + exception.getMessage());
		}
	}
}

