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
package org.openspcoop2.core.statistiche.model;

import org.openspcoop2.core.statistiche.Statistica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Statistica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaModel extends AbstractModel<Statistica> {

	public StatisticaModel(){
	
		super();
	
		this.DATA = new Field("data",java.util.Date.class,"statistica",Statistica.class);
		this.STATO_RECORD = new Field("stato-record",int.class,"statistica",Statistica.class);
		this.ID_PORTA = new Field("id-porta",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_PORTA = new Field("tipo-porta",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_MITTENTE = new Field("tipo-mittente",java.lang.String.class,"statistica",Statistica.class);
		this.MITTENTE = new Field("mittente",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_DESTINATARIO = new Field("tipo-destinatario",java.lang.String.class,"statistica",Statistica.class);
		this.DESTINATARIO = new Field("destinatario",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"statistica",Statistica.class);
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"statistica",Statistica.class);
		this.VERSIONE_SERVIZIO = new Field("versione-servizio",int.class,"statistica",Statistica.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"statistica",Statistica.class);
		this.SERVIZIO_APPLICATIVO = new Field("servizio-applicativo",java.lang.String.class,"statistica",Statistica.class);
		this.TRASPORTO_MITTENTE = new Field("trasporto-mittente",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_ISSUER = new Field("token-issuer",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_CLIENT_ID = new Field("token-client-id",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_SUBJECT = new Field("token-subject",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_USERNAME = new Field("token-username",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_MAIL = new Field("token-mail",java.lang.String.class,"statistica",Statistica.class);
		this.ESITO = new Field("esito",java.lang.Integer.class,"statistica",Statistica.class);
		this.ESITO_CONTESTO = new Field("esito-contesto",java.lang.String.class,"statistica",Statistica.class);
		this.CLIENT_ADDRESS = new Field("client-address",java.lang.String.class,"statistica",Statistica.class);
		this.GRUPPI = new Field("gruppi",java.lang.String.class,"statistica",Statistica.class);
		this.URI_API = new Field("uri-api",java.lang.String.class,"statistica",Statistica.class);
		this.CLUSTER_ID = new Field("cluster-id",java.lang.String.class,"statistica",Statistica.class);
		this.NUMERO_TRANSAZIONI = new Field("numero-transazioni",java.lang.Integer.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_COMPLESSIVA = new Field("dimensioni-bytes-banda-complessiva",java.lang.Long.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_INTERNA = new Field("dimensioni-bytes-banda-interna",java.lang.Long.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_ESTERNA = new Field("dimensioni-bytes-banda-esterna",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_TOTALE = new Field("latenza-totale",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_PORTA = new Field("latenza-porta",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_SERVIZIO = new Field("latenza-servizio",java.lang.Long.class,"statistica",Statistica.class);
	
	}
	
	public StatisticaModel(IField father){
	
		super(father);
	
		this.DATA = new ComplexField(father,"data",java.util.Date.class,"statistica",Statistica.class);
		this.STATO_RECORD = new ComplexField(father,"stato-record",int.class,"statistica",Statistica.class);
		this.ID_PORTA = new ComplexField(father,"id-porta",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_PORTA = new ComplexField(father,"tipo-porta",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_MITTENTE = new ComplexField(father,"tipo-mittente",java.lang.String.class,"statistica",Statistica.class);
		this.MITTENTE = new ComplexField(father,"mittente",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_DESTINATARIO = new ComplexField(father,"tipo-destinatario",java.lang.String.class,"statistica",Statistica.class);
		this.DESTINATARIO = new ComplexField(father,"destinatario",java.lang.String.class,"statistica",Statistica.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"statistica",Statistica.class);
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"statistica",Statistica.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione-servizio",int.class,"statistica",Statistica.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"statistica",Statistica.class);
		this.SERVIZIO_APPLICATIVO = new ComplexField(father,"servizio-applicativo",java.lang.String.class,"statistica",Statistica.class);
		this.TRASPORTO_MITTENTE = new ComplexField(father,"trasporto-mittente",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_ISSUER = new ComplexField(father,"token-issuer",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_CLIENT_ID = new ComplexField(father,"token-client-id",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_SUBJECT = new ComplexField(father,"token-subject",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_USERNAME = new ComplexField(father,"token-username",java.lang.String.class,"statistica",Statistica.class);
		this.TOKEN_MAIL = new ComplexField(father,"token-mail",java.lang.String.class,"statistica",Statistica.class);
		this.ESITO = new ComplexField(father,"esito",java.lang.Integer.class,"statistica",Statistica.class);
		this.ESITO_CONTESTO = new ComplexField(father,"esito-contesto",java.lang.String.class,"statistica",Statistica.class);
		this.CLIENT_ADDRESS = new ComplexField(father,"client-address",java.lang.String.class,"statistica",Statistica.class);
		this.GRUPPI = new ComplexField(father,"gruppi",java.lang.String.class,"statistica",Statistica.class);
		this.URI_API = new ComplexField(father,"uri-api",java.lang.String.class,"statistica",Statistica.class);
		this.CLUSTER_ID = new ComplexField(father,"cluster-id",java.lang.String.class,"statistica",Statistica.class);
		this.NUMERO_TRANSAZIONI = new ComplexField(father,"numero-transazioni",java.lang.Integer.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_COMPLESSIVA = new ComplexField(father,"dimensioni-bytes-banda-complessiva",java.lang.Long.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_INTERNA = new ComplexField(father,"dimensioni-bytes-banda-interna",java.lang.Long.class,"statistica",Statistica.class);
		this.DIMENSIONI_BYTES_BANDA_ESTERNA = new ComplexField(father,"dimensioni-bytes-banda-esterna",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_TOTALE = new ComplexField(father,"latenza-totale",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_PORTA = new ComplexField(father,"latenza-porta",java.lang.Long.class,"statistica",Statistica.class);
		this.LATENZA_SERVIZIO = new ComplexField(father,"latenza-servizio",java.lang.Long.class,"statistica",Statistica.class);
	
	}
	
	

	public IField DATA = null;
	 
	public IField STATO_RECORD = null;
	 
	public IField ID_PORTA = null;
	 
	public IField TIPO_PORTA = null;
	 
	public IField TIPO_MITTENTE = null;
	 
	public IField MITTENTE = null;
	 
	public IField TIPO_DESTINATARIO = null;
	 
	public IField DESTINATARIO = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField SERVIZIO_APPLICATIVO = null;
	 
	public IField TRASPORTO_MITTENTE = null;
	 
	public IField TOKEN_ISSUER = null;
	 
	public IField TOKEN_CLIENT_ID = null;
	 
	public IField TOKEN_SUBJECT = null;
	 
	public IField TOKEN_USERNAME = null;
	 
	public IField TOKEN_MAIL = null;
	 
	public IField ESITO = null;
	 
	public IField ESITO_CONTESTO = null;
	 
	public IField CLIENT_ADDRESS = null;
	 
	public IField GRUPPI = null;
	 
	public IField URI_API = null;
	 
	public IField CLUSTER_ID = null;
	 
	public IField NUMERO_TRANSAZIONI = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_COMPLESSIVA = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_INTERNA = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_ESTERNA = null;
	 
	public IField LATENZA_TOTALE = null;
	 
	public IField LATENZA_PORTA = null;
	 
	public IField LATENZA_SERVIZIO = null;
	 

	@Override
	public Class<Statistica> getModeledClass(){
		return Statistica.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}