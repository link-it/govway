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

package org.openspcoop2.example.pdd.client.sdi.ricezione_fatture;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fileSdIConMetadati_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileSdIConMetadati_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}identificativoSdI_Type"/>
 *         &lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}nomeFile_Type"/>
 *         &lt;element name="File" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="NomeFileMetadati" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}nomeFile_Type"/>
 *         &lt;element name="Metadati" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileSdIConMetadati_Type", propOrder = {
    "identificativoSdI",
    "nomeFile",
    "file",
    "nomeFileMetadati",
    "metadati"
})
public class FileSdIConMetadatiType {

    @XmlElement(name = "IdentificativoSdI", required = true)
    protected BigInteger identificativoSdI;
    @XmlElement(name = "NomeFile", required = true)
    protected String nomeFile;
    @XmlElement(name = "File", required = true)
    protected byte[] file;
    @XmlElement(name = "NomeFileMetadati", required = true)
    protected String nomeFileMetadati;
    @XmlElement(name = "Metadati", required = true)
    protected byte[] metadati;

    /**
     * Gets the value of the identificativoSdI property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdentificativoSdI() {
        return this.identificativoSdI;
    }

    /**
     * Sets the value of the identificativoSdI property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdentificativoSdI(BigInteger value) {
        this.identificativoSdI = value;
    }

    /**
     * Gets the value of the nomeFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeFile() {
        return this.nomeFile;
    }

    /**
     * Sets the value of the nomeFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeFile(String value) {
        this.nomeFile = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFile() {
        return this.file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFile(byte[] value) {
        this.file = value;
    }

    /**
     * Gets the value of the nomeFileMetadati property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeFileMetadati() {
        return this.nomeFileMetadati;
    }

    /**
     * Sets the value of the nomeFileMetadati property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeFileMetadati(String value) {
        this.nomeFileMetadati = value;
    }

    /**
     * Gets the value of the metadati property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getMetadati() {
        return this.metadati;
    }

    /**
     * Sets the value of the metadati property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setMetadati(byte[] value) {
        this.metadati = value;
    }

}
