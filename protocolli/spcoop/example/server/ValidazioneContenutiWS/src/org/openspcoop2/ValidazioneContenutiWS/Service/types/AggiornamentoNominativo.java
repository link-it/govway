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

/**
 * AggiornamentoNominativo.java
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
public class AggiornamentoNominativo  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2810241804754541538L;

	private java.lang.String _value;

    private java.lang.String nomePrecedente;  // attribute

    public AggiornamentoNominativo() {
    }

    // Simple Types must have a String constructor
    public AggiornamentoNominativo(java.lang.String _value) {
        this._value = _value;
    }
    // Simple Types must have a toString for serializing the value
    @Override
	public java.lang.String toString() {
        return this._value;
    }


    /**
     * Gets the _value value for this AggiornamentoNominativo.
     * 
     * @return _value
     */
    public java.lang.String get_value() {
        return this._value;
    }


    /**
     * Sets the _value value for this AggiornamentoNominativo.
     * 
     * @param _value
     */
    public void set_value(java.lang.String _value) {
        this._value = _value;
    }


    /**
     * Gets the nomePrecedente value for this AggiornamentoNominativo.
     * 
     * @return nomePrecedente
     */
    public java.lang.String getNomePrecedente() {
        return this.nomePrecedente;
    }


    /**
     * Sets the nomePrecedente value for this AggiornamentoNominativo.
     * 
     * @param nomePrecedente
     */
    public void setNomePrecedente(java.lang.String nomePrecedente) {
        this.nomePrecedente = nomePrecedente;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AggiornamentoNominativo)) return false;
        AggiornamentoNominativo other = (AggiornamentoNominativo) obj;
        if (this == obj) return true;
        if (this.__equalsCalc != null) {
            return (this.__equalsCalc == obj);
        }
        this.__equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this._value==null && other.get_value()==null) || 
             (this._value!=null &&
              this._value.equals(other.get_value()))) &&
            ((this.nomePrecedente==null && other.getNomePrecedente()==null) || 
             (this.nomePrecedente!=null &&
              this.nomePrecedente.equals(other.getNomePrecedente())));
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
        if (get_value() != null) {
            _hashCode += get_value().hashCode();
        }
        if (getNomePrecedente() != null) {
            _hashCode += getNomePrecedente().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AggiornamentoNominativo.class, true);

    static {
        AggiornamentoNominativo.typeDesc.setXmlType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamento-nominativo"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("nomePrecedente");
        attrField.setXmlName(new javax.xml.namespace.QName("", "nomePrecedente"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        AggiornamentoNominativo.typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        AggiornamentoNominativo.typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return AggiornamentoNominativo.typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.SimpleSerializer(
            _javaType, _xmlType, AggiornamentoNominativo.typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, AggiornamentoNominativo.typeDesc);
    }

}
