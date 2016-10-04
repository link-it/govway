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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DettaglioPagamentoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DettaglioPagamentoTypeModel extends AbstractModel<DettaglioPagamentoType> {

	public DettaglioPagamentoTypeModel(){
	
		super();
	
		this.BENEFICIARIO = new Field("Beneficiario",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.MODALITA_PAGAMENTO = new Field("ModalitaPagamento",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_RIFERIMENTO_TERMINI_PAGAMENTO = new Field("DataRiferimentoTerminiPagamento",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.GIORNI_TERMINI_PAGAMENTO = new Field("GiorniTerminiPagamento",java.lang.Integer.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_SCADENZA_PAGAMENTO = new Field("DataScadenzaPagamento",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.IMPORTO_PAGAMENTO = new Field("ImportoPagamento",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.COD_UFFICIO_POSTALE = new Field("CodUfficioPostale",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.COGNOME_QUIETANZANTE = new Field("CognomeQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.NOME_QUIETANZANTE = new Field("NomeQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CFQUIETANZANTE = new Field("CFQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.TITOLO_QUIETANZANTE = new Field("TitoloQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.ISTITUTO_FINANZIARIO = new Field("IstitutoFinanziario",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.IBAN = new Field("IBAN",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.ABI = new Field("ABI",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CAB = new Field("CAB",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.BIC = new Field("BIC",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.SCONTO_PAGAMENTO_ANTICIPATO = new Field("ScontoPagamentoAnticipato",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_LIMITE_PAGAMENTO_ANTICIPATO = new Field("DataLimitePagamentoAnticipato",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.PENALITA_PAGAMENTI_RITARDATI = new Field("PenalitaPagamentiRitardati",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_DECORRENZA_PENALE = new Field("DataDecorrenzaPenale",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CODICE_PAGAMENTO = new Field("CodicePagamento",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
	
	}
	
	public DettaglioPagamentoTypeModel(IField father){
	
		super(father);
	
		this.BENEFICIARIO = new ComplexField(father,"Beneficiario",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.MODALITA_PAGAMENTO = new ComplexField(father,"ModalitaPagamento",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_RIFERIMENTO_TERMINI_PAGAMENTO = new ComplexField(father,"DataRiferimentoTerminiPagamento",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.GIORNI_TERMINI_PAGAMENTO = new ComplexField(father,"GiorniTerminiPagamento",java.lang.Integer.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_SCADENZA_PAGAMENTO = new ComplexField(father,"DataScadenzaPagamento",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.IMPORTO_PAGAMENTO = new ComplexField(father,"ImportoPagamento",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.COD_UFFICIO_POSTALE = new ComplexField(father,"CodUfficioPostale",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.COGNOME_QUIETANZANTE = new ComplexField(father,"CognomeQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.NOME_QUIETANZANTE = new ComplexField(father,"NomeQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CFQUIETANZANTE = new ComplexField(father,"CFQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.TITOLO_QUIETANZANTE = new ComplexField(father,"TitoloQuietanzante",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.ISTITUTO_FINANZIARIO = new ComplexField(father,"IstitutoFinanziario",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.IBAN = new ComplexField(father,"IBAN",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.ABI = new ComplexField(father,"ABI",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CAB = new ComplexField(father,"CAB",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.BIC = new ComplexField(father,"BIC",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.SCONTO_PAGAMENTO_ANTICIPATO = new ComplexField(father,"ScontoPagamentoAnticipato",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_LIMITE_PAGAMENTO_ANTICIPATO = new ComplexField(father,"DataLimitePagamentoAnticipato",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.PENALITA_PAGAMENTI_RITARDATI = new ComplexField(father,"PenalitaPagamentiRitardati",java.lang.Double.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.DATA_DECORRENZA_PENALE = new ComplexField(father,"DataDecorrenzaPenale",java.util.Date.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
		this.CODICE_PAGAMENTO = new ComplexField(father,"CodicePagamento",java.lang.String.class,"DettaglioPagamentoType",DettaglioPagamentoType.class);
	
	}
	
	

	public IField BENEFICIARIO = null;
	 
	public IField MODALITA_PAGAMENTO = null;
	 
	public IField DATA_RIFERIMENTO_TERMINI_PAGAMENTO = null;
	 
	public IField GIORNI_TERMINI_PAGAMENTO = null;
	 
	public IField DATA_SCADENZA_PAGAMENTO = null;
	 
	public IField IMPORTO_PAGAMENTO = null;
	 
	public IField COD_UFFICIO_POSTALE = null;
	 
	public IField COGNOME_QUIETANZANTE = null;
	 
	public IField NOME_QUIETANZANTE = null;
	 
	public IField CFQUIETANZANTE = null;
	 
	public IField TITOLO_QUIETANZANTE = null;
	 
	public IField ISTITUTO_FINANZIARIO = null;
	 
	public IField IBAN = null;
	 
	public IField ABI = null;
	 
	public IField CAB = null;
	 
	public IField BIC = null;
	 
	public IField SCONTO_PAGAMENTO_ANTICIPATO = null;
	 
	public IField DATA_LIMITE_PAGAMENTO_ANTICIPATO = null;
	 
	public IField PENALITA_PAGAMENTI_RITARDATI = null;
	 
	public IField DATA_DECORRENZA_PENALE = null;
	 
	public IField CODICE_PAGAMENTO = null;
	 

	@Override
	public Class<DettaglioPagamentoType> getModeledClass(){
		return DettaglioPagamentoType.class;
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