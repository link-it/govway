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



package org.openspcoop2.testsuite.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerSOAPFault extends ServerCore{

	/**
	 * 
	 */
	public ServerSOAPFault() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		try{

			// Lettura Richiesta
			InputStream in=request.getInputStream();
			String type=request.getContentType();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();
			
			String buildDetails = request.getParameter("details");
			boolean details = true; // default
			if(buildDetails!=null && "false".equals(buildDetails)){
				details = false;
			}
			
			// costruizione Fault
	        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
	        try{
	        	AxisFault ax = new AxisFault("Server.faultExample","Fault ritornato dalla servlet di esempio di OpenSPCoop",null,null);
	        	ax.removeFaultDetail(new QName("http://xml.apache.org/axis/","stackTrace"));
	        	ax.removeHostname();
	        	if(details){
	        		String detailProprietario = "<op:detailEsempioOpenSPCoop xmlns:op=\"http://www.openspcoop.org/example\">\n"+
	        			"\t<op:errore tipo=\"openspcoopTest\">TEST</op:errore>\n"+
	        			"\t<op:errore2 tipo=\"openspcoopTestAnnidato\">\n\t\t<op:errore2Annidato tipo=\"openspcoopInterno\">TESTANNIDATO</op:errore2Annidato></op:errore2>\n"+
	        			"</op:detailEsempioOpenSPCoop>";
	        		ax.addFaultDetail(org.apache.axis.utils.XMLUtils.newDocument(new ByteArrayInputStream(detailProprietario.getBytes())).getDocumentElement());
	        	}
	        	Message msg = new Message(ax);
	        	msg.writeTo(outByte);

	        }catch(Exception e){
	            this.log.error("Riscontrato errore durante la costruzione del fault: "+e.toString());
	        }


	        //	      Echo risposta
	        if(outByte.size() > 0){
	        	response.setContentLength(outByte.size());
				response.setContentType(type);
				response.setStatus(500);
	            ServletOutputStream sout = response.getOutputStream();
	            sout.write(outByte.toByteArray());
	        }

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
