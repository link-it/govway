/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransazioneApplicativoServer 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneApplicativoServerModel extends AbstractModel<TransazioneApplicativoServer> {

	public TransazioneApplicativoServerModel(){
	
		super();
	
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA = new Field("data-uscita-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new Field("data-accettazione-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA = new Field("data-ingresso-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RICHIESTA_USCITA_BYTES = new Field("richiesta-uscita-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RISPOSTA_INGRESSO_BYTES = new Field("risposta-ingresso-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA = new Field("codice-risposta",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_TENTATIVO = new Field("data-primo-tentativo",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ULTIMO_ERRORE = new Field("data-ultimo-errore",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA_ULTIMO_ERRORE = new Field("codice-risposta-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.ULTIMO_ERRORE = new Field("ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_TENTATIVI = new Field("numero-tentativi",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
	
	}
	
	public TransazioneApplicativoServerModel(IField father){
	
		super(father);
	
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA = new ComplexField(father,"data-uscita-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new ComplexField(father,"data-accettazione-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA = new ComplexField(father,"data-ingresso-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RICHIESTA_USCITA_BYTES = new ComplexField(father,"richiesta-uscita-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RISPOSTA_INGRESSO_BYTES = new ComplexField(father,"risposta-ingresso-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA = new ComplexField(father,"codice-risposta",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_TENTATIVO = new ComplexField(father,"data-primo-tentativo",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ULTIMO_ERRORE = new ComplexField(father,"data-ultimo-errore",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA_ULTIMO_ERRORE = new ComplexField(father,"codice-risposta-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.ULTIMO_ERRORE = new ComplexField(father,"ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_TENTATIVI = new ComplexField(father,"numero-tentativi",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
	
	}
	
	

	public IField ID_TRANSAZIONE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField DATA_USCITA_RICHIESTA = null;
	 
	public IField DATA_ACCETTAZIONE_RISPOSTA = null;
	 
	public IField DATA_INGRESSO_RISPOSTA = null;
	 
	public IField RICHIESTA_USCITA_BYTES = null;
	 
	public IField RISPOSTA_INGRESSO_BYTES = null;
	 
	public IField CODICE_RISPOSTA = null;
	 
	public IField DATA_PRIMO_TENTATIVO = null;
	 
	public IField DATA_ULTIMO_ERRORE = null;
	 
	public IField CODICE_RISPOSTA_ULTIMO_ERRORE = null;
	 
	public IField ULTIMO_ERRORE = null;
	 
	public IField NUMERO_TENTATIVI = null;
	 

	@Override
	public Class<TransazioneApplicativoServer> getModeledClass(){
		return TransazioneApplicativoServer.class;
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