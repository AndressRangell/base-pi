package newpos.libpay.helper.iso8583;

/**
 * 域属性的格式
 * @author
 */
public class FieldAttr {
	private int lenType;
	private int lenAttr;
	private int dataType;
	private int dataLen;

	public int getLenType() {
		return lenType;
	}

	public void setLenType(int lenType) {
		this.lenType = lenType;
	}

	public int getLenAttr() {
		return lenAttr;
	}

	public void setLenAttr(int lenAttr) {
		this.lenAttr = lenAttr;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

}
