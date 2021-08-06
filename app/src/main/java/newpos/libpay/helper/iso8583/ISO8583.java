package newpos.libpay.helper.iso8583;

import android.content.Context;
import newpos.libpay.device.pinpad.PinpadManager;
import newpos.libpay.global.TMConfig;
import newpos.libpay.global.TMConstants;
import newpos.libpay.trans.Tcode;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * ISO8583 data description, unpacking, grouping
 * @author
 */
public class ISO8583 {

	private boolean isHasMac = false;
	private int fieldNum;
	private FieldAttr[] listFieldAttr;
	private String[] listFieldData;

	private String[] listFieldRespData;
	private String tpdu;
	private String header;
	private String rspHeader;
	private String rspTpdu;

	private Context mContext ;

	public interface RSPCODE{
		   String RSP_00 = "00";
		   String RSP_BATCH_LOAD = "94";
		   String RSP_NEW_USER_XPERTA = "95";
		   String RSP_MULTICUENTA = "97";
		   String RSP_TELECARGA = "98";
	}


	/**
	 * Initialize the length of the domain, the content format of the domain,
	 * the response data, and the array of request data
	 * @param context tpdu header
	 */
	public ISO8583(Context context, String tpdu, String header) {
		this.mContext = context ;
		fieldNum = Integer.parseInt(PAYUtils.lodeConfig(context, TMConstants.ISO8583, "filedNum"));
		int attrLen = fieldNum + 3;
		listFieldAttr = new FieldAttr[attrLen];
		SetAttr(context, fieldNum);
		listFieldData = new String[fieldNum + 1];
		listFieldRespData = new String[fieldNum + 1];
		this.tpdu = tpdu;
		this.header = header;
		this.mContext = context ;
	}

	/**
	 * 设置每一个域的属性格式
	 * @param context
	 * @param totalFiled
     */
	private void SetAttr(Context context, int totalFiled) {
		Properties pro = PAYUtils.lodeConfig(context, TMConstants.ISO8583);
		listFieldAttr[0] = getAttr(pro, "tpdu");
		listFieldAttr[1] = getAttr(pro, "header");
		for (int i = 0; i <= totalFiled; i++) {
			listFieldAttr[i + 2] = getAttr(pro, i + "");
		}
	}

	/**
	 * 获取配置文件中域的属性
	 * @param pro 配置文件对象
	 * @param proName 索引
	 * @return 返回域属性对象
	 */
	private FieldAttr getAttr(Properties pro, String proName) {
		String prop = pro.getProperty(proName);
		if (prop == null) {
			return null;
		}
		String[] propGroup = prop.split(",");
		FieldAttr attr = new FieldAttr();
		int data_len = Integer.parseInt(propGroup[1]) ;
		attr.setDataLen(data_len);
		attr.setDataType(Integer.parseInt(propGroup[2]));
		attr.setLenAttr(Integer.parseInt(propGroup[0]));
		attr.setLenType(Integer.parseInt(propGroup[3]));

		return attr;
	}

	/**
	 * Set domain content
	 * @param fieldNo domain ID
	 * @param data domain content
	 * @return
	 */
	public int setField(int fieldNo, String data) {
		if (fieldNo > fieldNum) {
			return -1;
		}
		listFieldData[fieldNo] = data;
		return 0;
	}



	/**
	 * 设置响应域内容
	 * @param fieldNo
	 * @param data
     * @return
     */
	public int setRspField(int fieldNo, String data) {
		if (fieldNo > fieldNum) {
			return -1;
		}
		listFieldRespData[fieldNo] = data;
		return 0;
	}

	/**
	 * 取解包完成后的数据
	 * @param fieldNo 域ID
	 * @return
	 */
	public String getfield(int fieldNo) {
		if (fieldNo > fieldNum) {
			return null;
		}
		return listFieldRespData[fieldNo];
	}

	/**
	 * 组包
	 * @return 返回打包完成后的结果
	 */
	public byte[] packetISO8583() {
		byte[] temp = new byte[1024];
		byte[] bitmap = new byte[16];
		byte[] bb = new byte[2048];
		int lenAttr, lenType, dataType, dataMaxLen, headLen;
		int offset = 0;
		int dataLen = 0;
		int appResult = -1;

		FieldAttr attr = listFieldAttr[0];
		appResult = appendHeader(attr, tpdu, temp);
		if (appResult < 0) {
			return null;
		}
		System.arraycopy(temp, 0, bb, offset, appResult);
		offset += appResult;

		attr = listFieldAttr[2];
		String fieldData = listFieldData[0];
		appResult = appendHeader(attr, fieldData, temp);
		if (appResult < 0) {
			return null;
		}
		System.arraycopy(temp, 0, bb, offset, appResult);
		offset += appResult;

		headLen = offset;
		attr = listFieldAttr[3];
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			offset += fieldNum / 4;
		}else {
			offset += fieldNum / 8;
		}

		if (fieldNum == 128){
			bitmap[0] = (byte) 0x80;
		}

		for (int i = 2; i < fieldNum; i++) {
			attr = listFieldAttr[i + 2];
			fieldData = listFieldData[i];

			if (attr == null || fieldData == null) {
				continue;
			}

			bitmap[(i - 1) / 8] |= 0x80 >> ((i - 1) % 8);
			lenAttr = attr.getLenAttr();
			lenType = attr.getLenType();
			dataType = attr.getDataType();
			dataMaxLen = attr.getDataLen();
			if (dataType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
				dataLen = fieldData.length() / 2;
			}else {
				dataLen = fieldData.length();
			}

			if (lenAttr == FieldTypesDefine.FIELDATTR_LEN_TYPE_NO) {
				if (dataLen != dataMaxLen) {
					return null;
				}
			} else {
				if (dataLen > dataMaxLen) {
					return null;
				}
				if (lenType == FieldTypesDefine.FIELDATTR_TYPE_N) {
					temp = ISOUtil.int2bcd(dataLen, lenAttr);
					System.arraycopy(temp, 0, bb, offset, lenAttr);
					offset += lenAttr;
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {

				}
			}

			if (dataType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
				temp = ISOUtil.hex2byte(fieldData);
				System.arraycopy(temp, 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
				System.arraycopy(fieldData.getBytes(), 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_CN) {
				temp = ISOUtil.str2bcd(fieldData, false, (byte) 0); // 右补
				dataLen = temp.length;
				System.arraycopy(temp, 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_N) {
				temp = ISOUtil.str2bcd(fieldData, true, (byte) 0);// 左补
				dataLen = temp.length;
				System.arraycopy(temp, 0, bb, offset, dataLen);
			}
			offset += dataLen;
		}
		if (isHasMac) {
			bitmap[fieldNum / 8 - 1] |= 0x01;
		}
		attr = listFieldAttr[3];
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			System.arraycopy(ISOUtil.byte2hex(bitmap).getBytes(), 0, bb,
					headLen, fieldNum / 4);
		} else {
			System.arraycopy(bitmap, 0, bb, headLen, fieldNum / 8);
		}
		if (isHasMac) {

			byte[] mac ;
			if(TMConfig.getInstance().getStandard() == 2){
				mac = getCITICMAC(listFieldData);
			}else {
				mac = PinpadManager.getInstance().getMac(bb, headLen - 2, offset - headLen + 2);
			}
			if (mac != null) {
				System.arraycopy(mac, 0, bb, offset, 8);
			}else {
				return null;
			}
			offset += 8;
		}

		byte[] packet = new byte[offset];
		System.arraycopy(bb, 0, packet, 0, offset);
		return packet;
	}

	private byte[] getCITICMAC(String[] packages){

		int[] result_fileds = {0, 3, 4, 11, 12, 13, 25, 32, 38, 39,41,42,44,61} ;
		String temp = "" ;
		for (int i = 0 ; i < result_fileds.length ; i++){
			if(packages[result_fileds[i]]!=null){
				String str ;
				if(result_fileds[i] == 41 || result_fileds[i] == 42){
					byte[] bcd = ISOUtil.str2bcd(packages[result_fileds[i]] , false);
					int l = bcd.length*2 ;
					if(result_fileds[i] == 42){
						l -= 1 ;
					}
					str = ISOUtil.bcd2str(bcd , 0 , l , false);
				}else if(result_fileds[i] == 32 || result_fileds[i] == 44){
					int ll = packages[result_fileds[i]].length() ;
					if(ll < 10){
						str = "0"+ll+packages[result_fileds[i]];
					}else {
						str = ll + packages[result_fileds[i]];
					}
				}else if(result_fileds[i] == 61){
					int lll = packages[result_fileds[i]].length() ;
					if( lll < 16){
						str = packages[result_fileds[i]];
					}else {
						str = packages[result_fileds[i]].substring(0 , 16);
					}
				}else {
					str = packages[result_fileds[i]];
				}
				temp += str + " ";
			}
		}
		temp = temp.trim();
		String ascii = BCD2ASC(temp.getBytes()) ;
		byte[] mac_in =  ISOUtil.hex2byte(ascii);

		byte[] mac = PinpadManager.getInstance().getMac(mac_in , 0 , mac_in.length);
		if(mac != null){

		}
		String bcd2ascii = BCD2ASC(ISOUtil.hexString(mac).getBytes()).substring(0 , 16);
		mac = ISOUtil.hex2byte(bcd2ascii);

		return mac;
	}

	public final static char[] BToA = "0123456789abcdef".toCharArray() ;
	public static String BCD2ASC(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int h = ((bytes[i] & 0xf0) >>> 4);
			int l = (bytes[i] & 0x0f);
			temp.append(BToA[h]).append( BToA[l]);
		}
		return temp.toString() ;
	}

	/**
	 * 解包
	 * @param data 数据包
	 * @return 异常返回-1 解包完成后的数据保存在listfieldRespData中
	 */
	public int unPacketISO8583(byte[] data) {
		byte[] MacBlock = null;
		byte[] bitmap = new byte[16];
		int offset = 0;
		int lenAttr, lenType, dataMaxLen;
		int dataLen = 0;
		FieldAttr attr = new FieldAttr();
		int headLen = 0;
		attr = listFieldAttr[0];
		offset += appRsp(-2, attr, data, offset, attr.getDataLen());
		attr = listFieldAttr[2];
		offset += appRsp(0, attr, data, offset, attr.getDataLen());

		if (listFieldAttr[3].getDataLen() != 8) {
			bitmap = new byte[16];
		}else {
			bitmap = new byte[8];
		}
		if (listFieldAttr[3].getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			String strMap = new String(data, offset, bitmap.length);
			bitmap = ISOUtil.str2bcd(strMap, true);
			setRspField(1, strMap);
			offset += bitmap.length * 2;
		} else {
			System.arraycopy(data, offset, bitmap, 0, bitmap.length);
			setRspField(1, ISOUtil.byte2hex(bitmap));
			offset += bitmap.length;
		}

		for (int i = 2; i <= fieldNum; i++) {
			if (offset > data.length) {
				return Tcode.T_unknow_err;
			}
			if (offset == data.length) {
				break;
			}
			if ((bitmap[(i - 1) / 8] & (0x80 >> ((i - 1) % 8))) == (byte) 0) {
				continue;
			}
			attr = listFieldAttr[i + 2];

			if(attr == null){
				continue;
			}

			dataMaxLen = attr.getDataLen();
			lenAttr = attr.getLenAttr();
			lenType = attr.getLenType();

			if (lenAttr == FieldTypesDefine.FIELDATTR_LEN_TYPE_NO) {
				dataLen = dataMaxLen;
			} else {
				if (lenType == FieldTypesDefine.FIELDATTR_TYPE_N) {
					dataLen = ISOUtil.bcd2int(data, offset, lenAttr);
					offset += lenAttr;
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
					dataLen = Integer
							.parseInt(new String(data, offset, lenAttr));
					offset += lenAttr;
				} else {
					dataLen = ISOUtil.byte2int(data, offset, lenAttr);
					offset += lenAttr;
				}
			}

			if (i == 64) {
				if(TMConfig.getInstance().getStandard() == 2){
					MacBlock = getCITICMAC(listFieldRespData);
				}else {
					MacBlock = PinpadManager.getInstance().getMac(data, headLen, offset - headLen);
				}
			}
			if(data.length < offset || data.length < offset+dataLen){
				break;
			}else {
				int ret = appRsp(i, attr, data, offset, dataLen);
				if(ret == -1){
					break;
				}else {
					offset += ret ;
				}
			}
		}



		int sendMsgId = PAYUtils.Object2Int(listFieldData[0]);
		int reciveMsgId = PAYUtils.Object2Int(listFieldRespData[0]);
		String sendTrackNo = listFieldData[11];
		String reciveTrackNo = listFieldRespData[11];
		String sendTermID = listFieldData[41];
		String reciveTermID = listFieldRespData[41];

		if (reciveMsgId - sendMsgId != 10) {
			return Tcode.T_package_illegal;
		}
		if (reciveTrackNo != null && !sendTrackNo.equals(reciveTrackNo)) {
			return Tcode.T_package_illegal;
		}
		if (!sendTermID.equals(reciveTermID)) {
			return Tcode.T_package_illegal;
		}

		return 0;
	}

	/**
	 * Sign in to the 62 domain to send BCD, clear the ASCII
	 * @param type
     */
	public void set62AttrDataType(int type){
		listFieldAttr[64].setDataType(type);
	}

	/**
	 * Clear Data ISO8583
	 */
	public void clearData() {
		listFieldData = null;
		listFieldAttr = null;
		fieldNum = Integer.parseInt(PAYUtils.lodeConfig(mContext, TMConstants.ISO8583, "filedNum"));
		int attrLen = fieldNum + 3;
		listFieldAttr = new FieldAttr[attrLen];
		SetAttr(mContext, fieldNum);
		listFieldData = new String[fieldNum + 1];
		listFieldRespData = new String[fieldNum + 1];
	}

	public void setHasMac(boolean isHasMac) {
		this.isHasMac = isHasMac;
	}

	/***
	 * @param attr 域属性
	 * @param content 域内容
	 * @param bb 暂存区
	 * @return
	 */
	private int appendHeader(FieldAttr attr, String content, byte[] bb) {
		byte[] temp = null;
		int dataLen = -1;
		if (attr.getDataType() != FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			if (attr.getDataLen() != content.length()) {
				return -1;
			}
			temp = ISOUtil.str2bcd(content, false, (byte) 0);
			dataLen = temp.length;
			System.arraycopy(temp, 0, bb, 0, dataLen);
		} else {
			if (attr.getDataLen() != content.length()) {
				return -1;
			}
			dataLen = content.length();
			System.arraycopy(content.getBytes(), 0, bb, 0, dataLen);
		}
		return dataLen;
	}

	private int appRsp(int FieldNo, FieldAttr attr, byte[] data, int offset, int dataLen) {
		int rspOffset = 0;
		String temp = null;
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			if (FieldNo == 63) {
				byte[] a = new byte[dataLen];
				System.arraycopy(data, offset, a, 0, dataLen);
				try {
					temp = new String(a, "gbk");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				temp = new String(data, offset, dataLen);
			}
			rspOffset = dataLen;
		} else if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
			temp = ISOUtil.byte2hex(data, offset, dataLen);
			rspOffset = dataLen;
		} else {
			rspOffset = (dataLen + 1) / 2;

			if(data.length<=offset || data.length < offset+rspOffset){
				return -1 ;
			}else {
				temp = ISOUtil.byte2hex(data, offset, rspOffset);
			}
		}
		if (FieldNo == -1) {
			rspHeader = temp;
		}else if (FieldNo == -2) {
			rspTpdu = temp;
		}else {
			setRspField(FieldNo, temp);
		}
		return rspOffset;
	}
}