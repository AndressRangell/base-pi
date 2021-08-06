package newpos.libpay.trans.translog;

import java.io.Serializable;

/**
 * 交易日志详情信息类
 * @author zhouqiang
 */
public class TransLogData implements Serializable {

	private String MsgID;

	public String getMsgID() {
		return MsgID;
	}

	public void setMsgID(String msgID) {
		MsgID = msgID;
	}

	private String termID;
	private String merchID;

	public String getTermID() {
		return termID;
	}

	public void setTermID(String termID) {
		this.termID = termID;
	}

	public String getMerchID() {
		return merchID;
	}

	public void setMerchID(String merchID) {
		this.merchID = merchID;
	}

	boolean flagOrderByProcCode;

	public boolean isFlagOrderByProcCode() {
		return flagOrderByProcCode;
	}

	public void setFlagOrderByProcCode(boolean flagOrderByProcCode) {
		this.flagOrderByProcCode = flagOrderByProcCode;
	}


	/** 小费 **/
	private long TipAmout = 0;
	/** 原交易流水号 **/
	private String BatchNo;

	public String getBatchNo() {
		return BatchNo;
	}

	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}



	/**
	 * 交易英文名称
	 * 详见 @{@link newpos.libpay.trans.Trans.Type}
	 */
	private String TransEName;
	public String getEName() {
		return TransEName;
	}

	public void setEName(String eName) {
		TransEName = eName;
	}


	/**
	 * 第二域卡号，加了*号的字串
	 */
	private String Pan;
	public String getPan() {
		return Pan;
	}

	public void setPan(String pan) {
		Pan = pan;
	}

	/**
	 * 第四域，标记此次交易的金额
	 */
	private Long Amount;
	public Long getAmount() {
		return Amount;
	}

	public void setAmount(Long amount) {
		Amount = amount;
	}

	/**
	 * MONTO TOTAL
	 * La suma del costo de servicio mas el monto
	 * Si no existe costo de servicio solo el monto.
	 */
	private Long TotalAmount;

	public Long getTotalAmount() {
		return TotalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		TotalAmount = totalAmount;
	}

	private Long Comision;

	public Long getComision() {
		return Comision;
	}

	public void setComision(Long comision) {
		Comision = comision;
	}

	/**
	 * 如果是卡交易，存储此次交易的卡片55域信息
	 */
	private byte[] ICCData;
	public byte[] getICCData() {
		return ICCData;
	}

	public void setICCData(byte[] iCCData) {
		ICCData = iCCData;
	}

	/**
	 * Field 11, transaction serial number
	 */
	private String TraceNo;
	public String getTraceNo() {
		return TraceNo;
	}

	public void setTraceNo(String traceNo) {
		TraceNo = traceNo;
	}

	/**
	 * Field 12, Local Time
	 */
	private String LocalTime;
	public String getLocalTime() {
		return LocalTime;
	}

	public void setLocalTime(String localTime) {
		LocalTime = localTime;
	}

	/**
	 * Field 13,
	 *
	 */
	private String LocalDate;
	public String getLocalDate() {
		return LocalDate;
	}

	public void setLocalDate(String localDate) {
		LocalDate = localDate;
	}

	/**
	 * Field 14，卡片有效期
	 */
	private String ExpDate;
	public String getExpDate() {
		return ExpDate;
	}

	public void setExpDate(String expDate) {
		ExpDate = expDate;
	}

	/**
	 * 第15域。交易日期
	 */
	private String SettleDate;
	public String getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}

	/**
	 * 第22域，输入方式
	 */
	private String EntryMode;
	public String getEntryMode() {
		return EntryMode;
	}

	public void setEntryMode(String entryMode) {
		EntryMode = entryMode;
	}


	/**
	 * 第32域
	 */
	private String AcquirerID;

	public String getAcquirerID() {
		return AcquirerID;
	}

	public void setAcquirerID(String acquirerID) {
		AcquirerID = acquirerID;
	}

	/**
	 * 第37域
	 */
	private String RRN;
	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}


	/**
	 * 第39域，响应码
	 */
	private String RspCode;
	public String getRspCode() {
		return RspCode;
	}

	public void setRspCode(String rspCode) {
		RspCode = rspCode;
	}



	/**
	 * 第三域，预处理码
	 */
	private String ProcCode;
	public String getProcCode() {
		return ProcCode;
	}

	public void setProcCode(String procCode) {
		ProcCode = procCode;
	}
	/**
	 * Field 48
	 */
	private String Field48;
	public String getField48() {
		return Field48;
	}

	public void setField48(String field48) {
		Field48 = field48;
	}

	/**
	 * 备注 63 从第三位开始到最后
	 */
	private String Refence;
	public String getRefence() {
		return Refence;
	}

	public void setRefence(String refence) {
		Refence = refence;
	}

	/**
	 * 操作员号码
	 */
	private int oprNo;
	public int getOprNo() {
		return oprNo;
	}

	public void setOprNo(int oprNo) {
		this.oprNo = oprNo;
	}
}
