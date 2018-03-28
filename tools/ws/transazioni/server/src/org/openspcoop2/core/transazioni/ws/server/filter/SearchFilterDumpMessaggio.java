package org.openspcoop2.core.transazioni.ws.server.filter;

/**
 * <p>Java class for SearchFilterDumpMessaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-dump-messaggio">
 *     &lt;sequence>
 *         &lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/core/transazioni}tipo-messaggio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="multipart-content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="multipart-content-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="multipart-content-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;

/**     
 * SearchFilterDumpMessaggio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-dump-messaggio", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "idTransazione",
    "tipoMessaggio",
    "contentType",
    "multipartContentType",
    "multipartContentId",
    "multipartContentLocation",
    "limit",
    "offset",
    "descOrder"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-dump-messaggio")
public class SearchFilterDumpMessaggio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=false,nillable=false)
	private String idTransazione;
	
	public void setIdTransazione(String idTransazione){
		this.idTransazione = idTransazione;
	}
	
	public String getIdTransazione(){
		return this.idTransazione;
	}
	
	
	@XmlElement(name="tipo-messaggio",required=false,nillable=false)
	private TipoMessaggio tipoMessaggio;
	
	public void setTipoMessaggio(TipoMessaggio tipoMessaggio){
		this.tipoMessaggio = tipoMessaggio;
	}
	
	public TipoMessaggio getTipoMessaggio(){
		return this.tipoMessaggio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=false,nillable=false)
	private String contentType;
	
	public void setContentType(String contentType){
		this.contentType = contentType;
	}
	
	public String getContentType(){
		return this.contentType;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-type",required=false,nillable=false)
	private String multipartContentType;
	
	public void setMultipartContentType(String multipartContentType){
		this.multipartContentType = multipartContentType;
	}
	
	public String getMultipartContentType(){
		return this.multipartContentType;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-id",required=false,nillable=false)
	private String multipartContentId;
	
	public void setMultipartContentId(String multipartContentId){
		this.multipartContentId = multipartContentId;
	}
	
	public String getMultipartContentId(){
		return this.multipartContentId;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-location",required=false,nillable=false)
	private String multipartContentLocation;
	
	public void setMultipartContentLocation(String multipartContentLocation){
		this.multipartContentLocation = multipartContentLocation;
	}
	
	public String getMultipartContentLocation(){
		return this.multipartContentLocation;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="limit",required=false,nillable=false)
	private Integer limit;
	
	public void setLimit(Integer limit){
		this.limit = limit;
	}
	
	public Integer getLimit(){
		return this.limit;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="offset",required=false,nillable=false)
	private Integer offset;
	
	public void setOffset(Integer offset){
		this.offset = offset;
	}
	
	public Integer getOffset(){
		return this.offset;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="descOrder",required=false,nillable=false,defaultValue="false")
	private Boolean descOrder = new Boolean("false");
	
	public void setDescOrder(Boolean descOrder){
		this.descOrder = descOrder;
	}
	
	public Boolean getDescOrder(){
		return this.descOrder;
	}
	
	
	
	
}