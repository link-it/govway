/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;


/**
 * Utilities per il test sulle funzionalita' E-Gov
 *  
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class UtilitiesEGov {
	
	private static int countIDBuste=0;
	private static int identifier=9;
	
	private static synchronized int getID(){
		UtilitiesEGov.countIDBuste++;
		return UtilitiesEGov.countIDBuste;
	}
	
	public static void setIdentifier(int id) {
		identifier=id;
	}
	
	public static String getBustaEGov(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,
			String profiloCollaborazione,String tipoServizio,String servizio,String idEGov,boolean confermaRicezione,String inoltro,boolean scadenza,String azione){
		return getBustaEGov(tipoMittente, mittente, tipoDestinatario, destinatario, profiloCollaborazione, tipoServizio, servizio, idEGov, confermaRicezione, inoltro, scadenza, azione, null, SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date());
	}
	public static String getBustaEGov(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,
		String profiloCollaborazione,String tipoServizio,String servizio,String idEGov,boolean confermaRicezione,String inoltro,boolean scadenza,String azione,
		String collaborazione,String tipo_tempo){
		return getBustaEGov(tipoMittente, mittente, tipoDestinatario, destinatario, profiloCollaborazione, tipoServizio, servizio, idEGov, confermaRicezione, inoltro, scadenza, azione, collaborazione, tipo_tempo, new Date());
	}
	public static String getBustaEGov(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,
			String profiloCollaborazione,String tipoServizio,String servizio,String idEGov,boolean confermaRicezione,String inoltro,boolean scadenza,String azione,
			String collaborazione,String tipo_tempo,Date oraRegistrazione){
		return getBustaEGov(tipoMittente, mittente, tipoDestinatario, destinatario, profiloCollaborazione, tipoServizio, servizio, idEGov, confermaRicezione, inoltro, 
				scadenza, azione, collaborazione, tipo_tempo, oraRegistrazione, 
				"<helloworld><a>Hello World</a><b>Hello World</b></helloworld>\n");
	}
	public static String getBustaEGov(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,
			String profiloCollaborazione,String tipoServizio,String servizio,String idEGov,boolean confermaRicezione,String inoltro,boolean scadenza,String azione,
			String collaborazione,String tipo_tempo,Date oraRegistrazione,String contenutoBody){
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append(buildBustaEGov(tipoMittente, mittente, tipoDestinatario, destinatario, 
				profiloCollaborazione, tipoServizio, servizio, idEGov, 
				confermaRicezione, inoltro, scadenza, azione, 
				collaborazione, tipo_tempo, oraRegistrazione));
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append(contenutoBody);
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");

		return busta.toString();
	}
	public static String buildBustaEGov(String tipoMittente,String mittente,String tipoDestinatario,String destinatario,
			String profiloCollaborazione,String tipoServizio,String servizio,String idEGov,boolean confermaRicezione,String inoltro,boolean scadenza,String azione,
			String collaborazione,String tipo_tempo,Date oraRegistrazione){
		StringBuffer busta = new StringBuffer();
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+tipoMittente+"\">"+mittente+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+tipoDestinatario+"\">"+destinatario+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+profiloCollaborazione+"</eGov_IT:ProfiloCollaborazione>\n");
		if(collaborazione!=null){
			busta.append("<eGov_IT:Collaborazione>"+collaborazione+"</eGov_IT:Collaborazione>\n");
		}
		busta.append("<eGov_IT:Servizio tipo=\""+tipoServizio+"\">"+servizio+"</eGov_IT:Servizio>\n");
		if(azione!=null){
			busta.append("<eGov_IT:Azione>"+azione+"</eGov_IT:Azione>\n");
		}
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idEGov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+tipo_tempo+"\">"+SPCoopUtils.getDate_eGovFormat(oraRegistrazione)+"</eGov_IT:OraRegistrazione>\n");
		if(scadenza){
			Date scad=new Date(oraRegistrazione.getTime()-70000);	
			busta.append("<eGov_IT:Scadenza>"+SPCoopUtils.getDate_eGovFormat(scad)+"</eGov_IT:Scadenza>");
		}
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:ProfiloTrasmissione confermaRicezione=\""+confermaRicezione+"\" inoltro=\""+inoltro+"\"/>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:ListaTrasmissioni>\n");
		busta.append("<eGov_IT:Trasmissione>\n");
		busta.append("<eGov_IT:Origine><eGov_IT:IdentificativoParte tipo=\""+tipoMittente+"\">"+mittente+"</eGov_IT:IdentificativoParte></eGov_IT:Origine>\n");
		busta.append("<eGov_IT:Destinazione><eGov_IT:IdentificativoParte tipo=\""+tipoDestinatario+"\">"+destinatario+"</eGov_IT:IdentificativoParte></eGov_IT:Destinazione>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+tipo_tempo+"\">"+SPCoopUtils.getDate_eGovFormat(oraRegistrazione)+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Trasmissione>\n");
		busta.append("</eGov_IT:ListaTrasmissioni>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		return busta.toString();
	}
	
	
	public static synchronized String getIDEGov(String mittente,String pddMittente){
		return getIDEGov(mittente,pddMittente,new Date());
	}
	public static synchronized String getIDEGov(String mittente,String pddMittente,Date date){
		StringBuffer buff = new StringBuffer();
		buff.append(mittente);
		buff.append('_');
		buff.append(pddMittente);
		buff.append('_');
		int countN =  UtilitiesEGov.getID();
		String c = Integer.toString(countN);
		int padding= 6 - c.length();
		// L'identifier serve per non generare gli stessi ID prodotti dalla Porta di Dominio se usa l'impostazione static
		buff.append(UtilitiesEGov.identifier);
		for(int i=0;i<padding;i++)	
			buff.append('0');
		buff.append(c);
		buff.append('_');
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm"); // SimpleDateFormat non e' thread-safe
		buff.append(dateformat.format(date));
		return buff.toString();
	}
}