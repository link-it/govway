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
 * AggiornamentoUtenteWDLResponse.java
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
@SuppressWarnings({ "rawtypes" })
public class AggiornamentoUtenteWDLResponse  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2275717772042303976L;

	private java.lang.String esito;

    private java.util.Date oraRegistrazione;

    public AggiornamentoUtenteWDLResponse() {
    }

    public AggiornamentoUtenteWDLResponse(
           java.lang.String esito,
           java.util.Date oraRegistrazione) {
           this.esito = esito;
           this.oraRegistrazione = oraRegistrazione;
    }


    /**
     * Gets the esito value for this AggiornamentoUtenteWDLResponse.
     * 
     * @return esito
     */
    public java.lang.String getEsito() {
        return this.esito;
    }


    /**
     * Sets the esito value for this AggiornamentoUtenteWDLResponse.
     * 
     * @param esito
     */
    public void setEsito(java.lang.String esito) {
        this.esito = esito;
    }


    /**
     * Gets the oraRegistrazione value for this AggiornamentoUtenteWDLResponse.
     * 
     * @return oraRegistrazione
     */
    public java.util.Date getOraRegistrazione() {
        return this.oraRegistrazione;
    }


    /**
     * Sets the oraRegistrazione value for this AggiornamentoUtenteWDLResponse.
     * 
     * @param oraRegistrazione
     */
    public void setOraRegistrazione(java.util.Date oraRegistrazione) {
        this.oraRegistrazione = oraRegistrazione;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AggiornamentoUtenteWDLResponse)) return false;
        AggiornamentoUtenteWDLResponse other = (AggiornamentoUtenteWDLResponse) obj;
        if (this == obj) return true;
        if (this.__equalsCalc != null) {
            return (this.__equalsCalc == obj);
        }
        this.__equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.esito==null && other.getEsito()==null) || 
             (this.esito!=null &&
              this.esito.equals(other.getEsito()))) &&
            ((this.oraRegistrazione==null && other.getOraRegistrazione()==null) || 
             (this.oraRegistrazione!=null &&
              this.oraRegistrazione.equals(other.getOraRegistrazione())));
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
        if (getEsito() != null) {
            _hashCode += getEsito().hashCode();
        }
        if (getOraRegistrazione() != null) {
            _hashCode += getOraRegistrazione().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AggiornamentoUtenteWDLResponse.class, true);

    static {
        AggiornamentoUtenteWDLResponse.typeDesc.setXmlType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamentoUtenteWDLResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esito");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        AggiornamentoUtenteWDLResponse.typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oraRegistrazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "ora-registrazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        AggiornamentoUtenteWDLResponse.typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return AggiornamentoUtenteWDLResponse.typeDesc;
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
            _javaType, _xmlType, AggiornamentoUtenteWDLResponse.typeDesc);
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
            _javaType, _xmlType, AggiornamentoUtenteWDLResponse.typeDesc);
    }

}
