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

/**
 * AggiornamentoUtenteWDLRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service.types;

/**
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
@SuppressWarnings({  "rawtypes" })
public class AggiornamentoUtenteWDLRequest  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1766313758409135294L;

	private org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo aggiornamentoNominativo;

    private java.lang.String indirizzo;

    public AggiornamentoUtenteWDLRequest() {
    }

    public AggiornamentoUtenteWDLRequest(
           org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo aggiornamentoNominativo,
           java.lang.String indirizzo) {
           this.aggiornamentoNominativo = aggiornamentoNominativo;
           this.indirizzo = indirizzo;
    }


    /**
     * Gets the aggiornamentoNominativo value for this AggiornamentoUtenteWDLRequest.
     * 
     * @return aggiornamentoNominativo
     */
    public org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo getAggiornamentoNominativo() {
        return this.aggiornamentoNominativo;
    }


    /**
     * Sets the aggiornamentoNominativo value for this AggiornamentoUtenteWDLRequest.
     * 
     * @param aggiornamentoNominativo
     */
    public void setAggiornamentoNominativo(org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo aggiornamentoNominativo) {
        this.aggiornamentoNominativo = aggiornamentoNominativo;
    }


    /**
     * Gets the indirizzo value for this AggiornamentoUtenteWDLRequest.
     * 
     * @return indirizzo
     */
    public java.lang.String getIndirizzo() {
        return this.indirizzo;
    }


    /**
     * Sets the indirizzo value for this AggiornamentoUtenteWDLRequest.
     * 
     * @param indirizzo
     */
    public void setIndirizzo(java.lang.String indirizzo) {
        this.indirizzo = indirizzo;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AggiornamentoUtenteWDLRequest)) return false;
        AggiornamentoUtenteWDLRequest other = (AggiornamentoUtenteWDLRequest) obj;
        if (this == obj) return true;
        if (this.__equalsCalc != null) {
            return (this.__equalsCalc == obj);
        }
        this.__equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.aggiornamentoNominativo==null && other.getAggiornamentoNominativo()==null) || 
             (this.aggiornamentoNominativo!=null &&
              this.aggiornamentoNominativo.equals(other.getAggiornamentoNominativo()))) &&
            ((this.indirizzo==null && other.getIndirizzo()==null) || 
             (this.indirizzo!=null &&
              this.indirizzo.equals(other.getIndirizzo())));
        this.__equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    @Override
	public synchronized int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (getAggiornamentoNominativo() != null) {
            _hashCode += getAggiornamentoNominativo().hashCode();
        }
        if (getIndirizzo() != null) {
            _hashCode += getIndirizzo().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AggiornamentoUtenteWDLRequest.class, true);

    static {
        AggiornamentoUtenteWDLRequest.typeDesc.setXmlType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamentoUtenteWDLRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aggiornamentoNominativo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "aggiornamento-nominativo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamento-nominativo"));
        elemField.setNillable(false);
        AggiornamentoUtenteWDLRequest.typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("indirizzo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "indirizzo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        AggiornamentoUtenteWDLRequest.typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return AggiornamentoUtenteWDLRequest.typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, AggiornamentoUtenteWDLRequest.typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, AggiornamentoUtenteWDLRequest.typeDesc);
    }

}
