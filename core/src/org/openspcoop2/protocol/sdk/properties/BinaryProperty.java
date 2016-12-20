package org.openspcoop2.protocol.sdk.properties;

public class BinaryProperty extends AbstractProperty<byte[]> {

	private String fileName;
	
	protected BinaryProperty(String id, byte[] value, String fileName) {
		super(id, value);
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
