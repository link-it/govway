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
 * RegistrazioneUtenteWDLRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1;

/**
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
@SuppressWarnings({ "rawtypes" })
public class RegistrazioneUtenteWDLRequestType  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5019126621255686919L;

	private java.lang.String nominativo;

    private java.lang.String indirizzo;

    private java.util.Date oraRegistrazione;

    public RegistrazioneUtenteWDLRequestType() {
    }

    public RegistrazioneUtenteWDLRequestType(
           java.lang.String nominativo,
           java.lang.String indirizzo,
           java.util.Date oraRegistrazione) {
           this.nominativo = nominativo;
           this.indirizzo = indirizzo;
           this.oraRegistrazione = oraRegistrazione;
    }


    /**
     * Gets the nominativo value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @return nominativo
     */
    public java.lang.String getNominativo() {
        return this.nominativo;
    }


    /**
     * Sets the nominativo value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @param nominativo
     */
    public void setNominativo(java.lang.String nominativo) {
        this.nominativo = nominativo;
    }


    /**
     * Gets the indirizzo value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @return indirizzo
     */
    public java.lang.String getIndirizzo() {
        return this.indirizzo;
    }


    /**
     * Sets the indirizzo value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @param indirizzo
     */
    public void setIndirizzo(java.lang.String indirizzo) {
        this.indirizzo = indirizzo;
    }


    /**
     * Gets the oraRegistrazione value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @return oraRegistrazione
     */
    public java.util.Date getOraRegistrazione() {
        return this.oraRegistrazione;
    }


    /**
     * Sets the oraRegistrazione value for this RegistrazioneUtenteWDLRequestType.
     * 
     * @param oraRegistrazione
     */
    public void setOraRegistrazione(java.util.Date oraRegistrazione) {
        this.oraRegistrazione = oraRegistrazione;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegistrazioneUtenteWDLRequestType)) return false;
        RegistrazioneUtenteWDLRequestType other = (RegistrazioneUtenteWDLRequestType) obj;
        if (this == obj) return true;
        if (this.__equalsCalc != null) {
            return (this.__equalsCalc == obj);
        }
        this.__equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nominativo==null && other.getNominativo()==null) || 
             (this.nominativo!=null &&
              this.nominativo.equals(other.getNominativo()))) &&
            ((this.indirizzo==null && other.getIndirizzo()==null) || 
             (this.indirizzo!=null &&
              this.indirizzo.equals(other.getIndirizzo()))) &&
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
        if (getNominativo() != null) {
            _hashCode += getNominativo().hashCode();
        }
        if (getIndirizzo() != null) {
            _hashCode += getIndirizzo().hashCode();
        }
        if (getOraRegistrazione() != null) {
            _hashCode += getOraRegistrazione().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegistrazioneUtenteWDLRequestType.class, true);

    static {
        RegistrazioneUtenteWDLRequestType.typeDesc.setXmlType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types/esempio1", "registrazioneUtenteWDLRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nominativo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "nominativo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        RegistrazioneUtenteWDLRequestType.typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("indirizzo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "indirizzo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        RegistrazioneUtenteWDLRequestType.typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oraRegistrazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "ora-registrazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        RegistrazioneUtenteWDLRequestType.typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return RegistrazioneUtenteWDLRequestType.typeDesc;
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
            _javaType, _xmlType, RegistrazioneUtenteWDLRequestType.typeDesc);
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
            _javaType, _xmlType, RegistrazioneUtenteWDLRequestType.typeDesc);
    }

}
