package newpos.libpay.process;

import android.os.SystemClock;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import newpos.libpay.device.pinpad.OfflineRSA;
import newpos.libpay.device.pinpad.PinInfo;
import newpos.libpay.global.TMConfig;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKException;
import com.pos.device.emv.CandidateListApp;
import com.pos.device.emv.CoreParam;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVCallback;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalMckConfigure;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;
import com.pos.device.ped.RsaPinKey;
import com.pos.device.picc.EmvContactlessCard;
import com.pos.device.picc.PiccReader;


/**
 * EMV交易流程
 * @author zhouqiang
 */
public class EmvTransaction {

	private static TMConfig cfg = null;

	private IccReader icCard = null ;
	private ContactCard contactCard = null ;
	private IEMVHandler emvHandler = null ;
	private PiccReader nfcCard = null ;
	private EmvContactlessCard emvContactlessCard = null ;
	private int timeout ;
	private int offlinePinTryCounts ;

	private long Amount;
	private int inputMode ;
	private long otherAmount;

	private String rspCode;
	private String authCode;
	private byte[] rspICCData;
	private int onlineResult;
	private String pinBlock = "";
	private String ECAmount = null ;
	private String newPinBlock = "";
	private String traceNo;

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public String getTraceNo() {
		return this.traceNo;
	}

	final int wOnlineTags[] = { 0x9F26,
            0x9F27, // CID
            0x9F10, // IAD (Issuer Application Data)
            0x9F37, // Unpredicatable Number
            0x9F36, // ATC (Application Transaction Counter)
            0x95, // TVR
            0x9A, // Transaction Date
            0x9C, // Transaction Type
            0x9F02, // Amount Authorised
            0x5F2A, // Transaction Currency Code
            0x82, // AIP
            0x9F1A, // Terminal Country Code
            0x9F03, // Amount Other
            0x9F33, // Terminal Capabilities
              0x9F34, // CVM Result
            0x9F35, // Terminal Type
            0x9F1E, // IFD Serial Number
            0x84, // Dedicated File Name
            0x9F09, // Application Version #
            0x9F41, // Transaction Sequence Counter

	0 };


	final int wISR_tags[] = { 0x9F33,
			0x95, // TVR
			0x9F37, // Unpredicatable Number
			0x9F1E, // IFD Serial Number
			0x9F10, // Issuer Application Data
			0x9F26, // Application Cryptogram
			0x9F36, // Application Tranaction Counter
			0x82, // AIP
			0xDF31, // 发卡行脚本结果
			0x9F1A, // Terminal Country Code
			0x9A, // Transaction Date
	0 };

	final int reversal_tag[] = { 0x95,
			0x9F1E, // IFD Serial Number
			0x9F10, // Issuer Application Data
			0x9F36, // Application Transaction Counter
			0xDF31, // 发卡行脚本结果
	0 };

	private TransUI transUI ;
	private TransInputPara para ;

	/**
	 * 初始化内核专用构造器
	 */
	public EmvTransaction() {
		emvHandler = EMVHandler.getInstance();
	}

	/**
	 * EMV流程专用构造器
     */
	public EmvTransaction(TransInputPara p){
		this.emvHandler = EMVHandler.getInstance();
		this.para = p ;
		this.transUI = para.getTransUI() ;

		if(para.isNeedAmount()){
			this.Amount = para.getAmount();
			this.otherAmount = para.getOtherAmount();
		}
		this.inputMode = para.getInputMode();
		if(inputMode == Trans.ENTRY_MODE_NFC){
			try {
				nfcCard = PiccReader.getInstance();
				emvContactlessCard = EmvContactlessCard.connect() ;
			} catch (SDKException e) {
				e.printStackTrace();
			}
		}if(inputMode == Trans.ENTRY_MODE_ICC){
			try {
				icCard = IccReader.getInstance(SlotType.USER_CARD);
				contactCard = icCard.connectCard(VCC.VOLT_5 , OperatorMode.EMV_MODE) ;
			} catch (SDKException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inyectar el tag 9F1E IFDSerialNo
	 */
	private void getSerialNoEMV(){


		cfg = TMConfig.getInstance();
		String SN = cfg.getTermID();
		byte[] serialNo = SN.getBytes();
		emvHandler.setDataElement(new byte[]{(byte) 0x9f, 0x1e}, serialNo);
	}


	/**
	 * EMV交易流程开始
	 * @return
     */
	public int start() {

		timeout = 60 * 1000 ;
		initEmvKernel();
		int ret = emvReadData(true);
		if (ret != 0) {
			return ret;
		}
		getSerialNoEMV();

		if(para.isNeedConfirmCard()){
			String cn = getCardNo();
			if(ret != 0){
				return Tcode.T_user_cancel_operation ;
			}
		}

		if(!para.isEmvAll()){
			return 0 ;
		}

		emvHandler.processRestriction();

		try {
			ret = emvHandler.cardholderVerify();
		} catch (SDKException e) {
			e.printStackTrace();
		}

		if (ret != 0) {
			return Tcode.T_card_holder_auth_err ;
		}

		try {
			ret = emvHandler.terminalRiskManage();
		} catch (SDKException e) {
			e.printStackTrace();
		}

		if (ret != 0) {

			return Tcode.T_terminal_action_ana_err ;
		}


		boolean isNeedOnline = false ;
		try {
			isNeedOnline = emvHandler.terminalActionAnalysis();
		} catch (SDKException e) {
			e.printStackTrace();
		}

		if(isNeedOnline){

			if(pinBlock.equals("")){
				forzarPIN();
			}

			boolean ertr = para.isChangePin();
			if (ertr) {
				if (!pinBlock.equals("CANCEL")) {
					changePin();
					return 1;
				}
			}

			return 1;
		} else {

			if(pinBlock.equals("")){
				forzarPIN();
			}

			boolean ertr = para.isChangePin();
			if (ertr) {
				if (!pinBlock.equals("CANCEL")) {
					changePin();
					return 1;
				}
			}
		}
		return 0;
	}

	/**
	 * EMV读数据
	 * @param ifOfflineDataAuth
	 * @return
	 */
	private int emvReadData(boolean ifOfflineDataAuth) {

		if(para.isECTrans()){
			emvHandler.pbocECenable(true);
		}else {
			emvHandler.pbocECenable(false);

		}
		emvHandler.setEMVInitCallback(emvInitListener);
		emvHandler.setApduExchangeCallback(apduExchangeListener);
		emvHandler.setDataElement(new byte[] { (byte) 0x9c }, new byte[] { 0x00 });

		int ret  = emvHandler.selectApp(Integer.parseInt(ISOUtil.padleft(2 + "", 6, '0')));
		//int ret = emvHandler.selectApp(Integer.parseInt(traceNo));

		if (ret != 0) {

			return Tcode.T_select_app_err ;
		}

		if(para.isECTrans()){
			byte[] firstBal = emvHandler.pbocReadECBalance();
			if(firstBal!=null){
				ECAmount = ISOUtil.byte2hex(firstBal) ;
			}
		}

		try {
			ret = emvHandler.readAppData();
		} catch (SDKException e) {
			e.printStackTrace();
		}

		if (ret != 0) {
			return Tcode.T_read_app_data_err ;
		}

		if (ifOfflineDataAuth) {
			try {
				ret = emvHandler.offlineDataAuthentication();
			} catch (SDKException e) {
				e.printStackTrace();
			}

			if (ret != 0) {

				return Tcode.T_offline_dataauth_err ;
			}
		}

		return 0;
	}

	/**
	 * EMV联机后处理，二次授权
	 * @param rspCode
	 * @param authCode
	 * @param rspICCData
	 * @param onlineResult
     * @return
     */
	public int afterOnline(String rspCode, String authCode, byte[] rspICCData, int onlineResult) {

		if(rspICCData!=null) {
		}
		this.rspCode = rspCode;
		this.authCode = authCode;
		this.rspICCData = rspICCData;
		this.onlineResult = onlineResult;

		boolean onlineTransaction = false ;
		try {
			onlineTransaction = emvHandler.onlineTransaction();
		} catch (SDKException e) {
			e.printStackTrace();
		}
		if(onlineTransaction){
			return 0 ;
		}else {
			return -1 ;
		}
	}

	/**
	 * 获取当前交易卡号
	 * @return
     */
	public String getCardNo(){
		byte[] temp = new byte[256];
		int len = PAYUtils.get_tlv_data_kernal(0x5A, temp);
		return ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len));
	}

	/**
	 * 获取当前交易密码加密
	 * @return
     */
	public String getPinBlock(){
		return pinBlock ;
	}

	public String getNewPinBlock() {
		return newPinBlock;
	}

	/**
	 * 获取电子现金余额
	 * @return
     */
	public String getECAmount(){
		return ECAmount ;
	}

	/**
	 * 初始化Kernel
	 * @return
     */
	public boolean initEmvKernel() {
		emvHandler.initDataElement();
		emvHandler.setKernelType(EMVHandler.KERNEL_TYPE_PBOC);

		TerminalMckConfigure configure = new TerminalMckConfigure();
		configure.setTerminalType(0x22);
		configure.setTerminalCapabilities(new byte[] { (byte) 0xE0,
				(byte) 0xF8, (byte) 0xC8 });
		configure.setAdditionalTerminalCapabilities(new byte[] { 0x60, 0x00,
				(byte) 0xF0, (byte) 0xA0, 0x01 });

		configure.setSupportCardInitiatedVoiceReferrals(false);
		configure.setSupportForcedAcceptanceCapability(false);
		if(para.isNeedOnline()){
			configure.setSupportForcedOnlineCapability(true);
		}else {
			configure.setSupportForcedOnlineCapability(false);
		}
		configure.setPosEntryMode(0x05);

		int ret = emvHandler.setMckConfigure(configure);
		if (ret != 0) {
			return false;
		}
		CoreParam coreParam = new CoreParam();
		coreParam.setTerminalId("POS00001".getBytes());
		coreParam.setMerchantId("000000000000000".getBytes());
		coreParam.setMerchantCateCode(new byte[] { 0x00, 0x01 });
		coreParam.setMerchantNameLocLen(15);
		coreParam.setMerchantNameLoc("Banco Pichincha".getBytes());
		coreParam.setTerminalCountryCode(new byte[] { 0x02 ,0x18 });
		coreParam.setTransactionCurrencyCode(new byte[] { 0x08 ,0x40 });
		coreParam.setReferCurrencyCode(new byte[] { 0x08 ,0x40 });
		coreParam.setTransactionCurrencyExponent(0x02);
		coreParam.setReferCurrencyExponent(0x02);
		coreParam.setReferCurrencyCoefficient(1000);
		coreParam.setTransactionType(EMVHandler.EMVTransType.EMV_GOODS);

		ret = emvHandler.setCoreInitParameter(coreParam);
		if (ret != 0) {
			return false;
		}
		return true;
	}

	private IEMVCallback.EMVInitListener emvInitListener = new IEMVCallback.EMVInitListener() {
		@Override
		public int candidateAppsSelection() {
			int[] numData = new int[1];
			CandidateListApp[] listapp = new CandidateListApp[32];
			try {
				listapp = emvHandler.getCandidateList();
				if (listapp != null) {
					numData[0] = listapp.length;
				} else {
					return -1;
				}
			} catch (SDKException e) {
				e.printStackTrace();
			}
			int ret = 0 ;
			if (listapp.length > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < numData[0]; i++) {
					if (i == 0) {
						sb.append(new String(listapp[i].gettCandAppName()));
					}else {
						sb.append("," + new String(listapp[i].gettCandAppName()));
					}
				}
				if (numData[0] > 1) {
					ret = transUI.showCardApplist(timeout, sb.toString().split(","));
				}

				return ret;
			} else{
				return -1;
			}
		}

		@Override
		public void multiLanguageSelection() {
			byte[] tag = new byte[] { 0x5F, 0x2D };
			int ret = emvHandler.checkDataElement(tag);
		}

		@Override
		public int getAmount(int[] transAmount, int[] cashBackAmount) {

			if (para.isNeedAmount()) {
				transAmount[0] = Integer.valueOf(Amount + "");
				cashBackAmount[0] = Integer.valueOf(otherAmount + "");
			}
			return 0;
		}

		@Override
		public int getPin(int[] pinLen, byte[] cardPin) {
			return 0;
		}

		@Override
		public int pinVerifyResult(int tryCount) {

			if (tryCount == 0) {
			} else if (tryCount == 1) {
				offlinePinTryCounts = 1 ;
			} else {
				offlinePinTryCounts = tryCount ;
			}
			return 0;
		}

		@Override
		public int checkOnlinePIN() {

			if(para.isNeedPass()){

				byte[] val = new byte[16];
				String cardNum = "" ;
				try {
					val = emvHandler.getDataElement(new byte[] { 0x5A });
				} catch (SDKException e) {
					e.printStackTrace();
				}if (val!=null) {
					cardNum = ISOUtil.trimf(ISOUtil.byte2hex(val, 0, val.length));
				}
				PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNum, "Ingresa tu PIN:");
				if(info.isResultFlag()){
					if(info.isNoPin()){
						para.setChangePin(false);
						return -1 ;
					}else {
						pinBlock = ISOUtil.hexString(info.getPinblock()) ;
					}
				} else {
					para.setChangePin(false);
					pinBlock = "CANCEL" ;
					return -1 ;
				}
				return 0;
			}else {
				return 0 ;
			}
		}

		/** 核对身份证证件 **/
		@Override
		public int checkCertificate() {

			return 0;
		}

		@Override
		public int onlineTransactionProcess(byte[] brspCode, byte[] bauthCode,
											int[] authCodeLen, byte[] bauthData, int[] authDataLen,
											byte[] script, int[] scriptLen, byte[] bonlineResult) {

			brspCode[0] = 0;
			brspCode[1] = 0;
			authCodeLen[0] = 0;
			scriptLen[0] = 0;
			authDataLen[0] = 0;
			bonlineResult[0] = (byte) onlineResult;
			if (rspCode == null || rspCode.equals("") || onlineResult != 0) {
				return 0;
			} else {
				System.arraycopy(rspCode.getBytes(), 0, brspCode, 0, 2);
			}if (authCode == null || authCode.equals("")) {
				authCodeLen[0] = 0;
			} else {
				authCodeLen[0] = authCode.length();
				System.arraycopy(authCode.getBytes(), 0, bauthCode, 0, authCodeLen[0]);
			}if (rspICCData != null && rspICCData.length > 0) {
				authDataLen[0] = PAYUtils.get_tlv_data(rspICCData, rspICCData.length, 0x91, bauthData, false);
				byte[] scriptTemp = new byte[256];
				int scriptLen1 = PAYUtils.get_tlv_data(rspICCData, rspICCData.length, 0x71, scriptTemp, true);
				System.arraycopy(scriptTemp, 0, script, 0, scriptLen1);
				int scriptLen2 = PAYUtils.get_tlv_data(rspICCData, rspICCData.length, 0x72, scriptTemp, true);
				System.arraycopy(scriptTemp, 0, script, scriptLen1, scriptLen2);
				scriptLen[0] = scriptLen1 + scriptLen2;
			}
			bonlineResult[0] = (byte) onlineResult;

			return 0;
		}

		@Override
		public int issuerReferralProcess() {

			return 0;
		}

		@Override
		public int adviceProcess(int firstFlg) {

			return 0;
		}

		@Override
		public int checkRevocationCertificate(int caPublicKeyID, byte[] RID,
											  byte[] destBuf) {
			return -1;
		}

		/**
		 * 黑名单
		 */
		@Override
		public int checkExceptionFile(int panLen, byte[] pan, int panSN) {
			return -1;
		}

		/**
		 * 判断IC卡脱机的累计金额 超过就强制联机
		 */
		@Override
		public int getTransactionLogAmount(int panLen, byte[] pan, int panSN) {
			return 0;
		}


		@Override
		public int getOfflinePin(int i, RsaPinKey rsaPinKey, byte[] recvLen, byte[] recvData) {

			try {

				byte[] counts = emvHandler.getDataElement(new byte[]{(byte) 0x9F , (byte) 0x17});
				if(counts!=null){

					offlinePinTryCounts = counts[0] ;
				}
			} catch (SDKException e) {
				e.printStackTrace();
			}
			OfflineRSA offlineRSA = null ;
			if(i == 1){
				offlineRSA.setIccRandom(rsaPinKey.getIccrandom());
				offlineRSA.setModLen(rsaPinKey.getMod().length);
				offlineRSA.setMod(rsaPinKey.getMod());
				offlineRSA.setExpLen(rsaPinKey.getExp().length);
				offlineRSA.setExp(rsaPinKey.getExp());
			}
			/*PinInfo info = transUI.getPinpadOfflinePin(timeout , i , offlineRSA , offlinePinTryCounts);
			if(info.isResultFlag()){
				byte[] rsp = info.getPinblock();
				if(rsp==null){
					return -1 ;
				}
				recvLen[0] = (byte) rsp.length ;
				System.arraycopy(rsp , 0 , recvData , 0 , recvLen[0]);

				return 0;
			}
			return info.getErrno() ;*/
			return 0;
		}
	};

	public boolean changePin() {
		String pinBlockNew;
		String pinBlockConfir;
		boolean ret = false;
		if(para.isNeedPass()){

			byte[] val = new byte[16];
			String cardNum = "" ;
			try {
				val = emvHandler.getDataElement(new byte[] { 0x5A });
			} catch (SDKException e) {
				e.printStackTrace();
			}if (val!=null) {
				cardNum = ISOUtil.trimf(ISOUtil.byte2hex(val, 0, val.length));
			}
			PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNum, "Digita tu nuevo PIN:");
			if(info.isResultFlag()){
				if(info.isNoPin()){
					pinBlock = "CANCEL";
					ret = false;
				}else {
					pinBlockNew = ISOUtil.hexString(info.getPinblock()) ;
					if (!pinBlockNew.equals(pinBlock)) {
						info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNum, "Confirma tu nuevo PIN:");
						if(info.isResultFlag()){
							if(info.isNoPin()){
								pinBlock = "CANCEL";
								ret = false;
							}else {
								pinBlockConfir = ISOUtil.hexString(info.getPinblock()) ;
								if (pinBlockNew.equals(pinBlockConfir)) {
									newPinBlock = pinBlockConfir;
									ret = true;
								} else {
									ret = false;
									transUI.showMsgError(timeout, "Las claves no coinciden");
								}
							}
						} else {
							pinBlock = "CANCEL" ;
							return false;
						}
					} else {
						ret = false;
						transUI.showMsgError(timeout, "La nueva clave no puede ser igual a la anterior");
					}
				}
			} else {
				pinBlock = "CANCEL" ;
				return false;
			}
		}
		return ret;
	}

	private IEMVCallback.ApduExchangeListener apduExchangeListener = new IEMVCallback.ApduExchangeListener() {
		@Override
		public int apduExchange(byte[] sendData, int[] recvLen, byte[] recvData) {

			if(inputMode == Trans.ENTRY_MODE_NFC){
				int[] status = new int[1];
				long start = SystemClock.uptimeMillis();
				while(true){
					if(SystemClock.uptimeMillis()-start>3*1000){
						break;
					}try {
						status[0] = emvContactlessCard.getStatus();
					} catch (SDKException e) {
						e.printStackTrace();
					}if(status[0]== EmvContactlessCard.STATUS_EXCHANGE_APDU){
						break;
					}try {
						Thread.sleep(6);
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				int len = 0 ;
				try {
					byte[] rawData = emvContactlessCard.transmit(sendData) ;
					if(rawData!=null){

						len = rawData.length ;
					}if ( len <=  0 ) {
						return -1;
					}
					System.arraycopy(rawData, 0, recvData, 0, rawData.length);
				} catch (SDKException e) {
					e.printStackTrace();
				}if (len >= 0) {
					recvLen[0] = len;

					return 0;
				}
				return -1;
			}if( Trans.ENTRY_MODE_ICC == inputMode ){
				int len = 0 ;
				try {
					if(contactCard == null || icCard == null){
						return -1 ;
					}else {
						byte[] rawData = icCard.transmit(contactCard , sendData) ;
						if(rawData!=null){

							len = rawData.length ;
						}if ( len <= 0 ) {
							return -1;
						}
						System.arraycopy(rawData, 0, recvData, 0, rawData.length);
					}
				} catch (SDKException e) {
					e.printStackTrace();
				}if (len >= 0) {
					recvLen[0] = len;

					return 0;
				}
				return -1;
			}
		return -1 ;
		}
	};

	/**
	 * 与卡片进行APDU交互
	 * @param apdu
	 * @return
     */
	private byte[] exeAPDU(byte[] apdu){
		byte[] rawData = null ;
		int recvlen = 0 ;
		try {
			rawData = icCard.transmit(contactCard , apdu) ;
			if(rawData!=null){

				recvlen = rawData.length ;
			}
		} catch (SDKException e) {
			e.printStackTrace();
		}
		byte[] recv = new byte[recvlen];
		if(rawData!=null){
			System.arraycopy(rawData,0,recv,0,recvlen);
		}else {
			recv = null ;
		}
		return recv ;
	}

	/**
	 * 从apdu中格式化金额
	 * @param hex
	 * @return
     */
	private String fromApdu2Amount(String hex){
		int len = hex.length() ;
		if(len > 2 && ( (hex.contains("9F79") && hex.contains("9000"))  ||
				(hex.contains("9F78") && hex.contains("9000"))) ||
				(hex.contains("9F5D") && hex.contains("9000"))){
			int offset = 4 ;
			int l = Integer.parseInt(hex.substring(offset , offset+2));
			return hex.substring(offset+2 , offset+2+l*2);
		}
		return null ;
	}

	public int forzarPIN(){

		if(para.isNeedPass()){

			byte[] val = new byte[16];
			String cardNum = "" ;
			try {
				val = emvHandler.getDataElement(new byte[] { 0x5A });
			} catch (SDKException e) {
				e.printStackTrace();
			}if (val!=null) {
				cardNum = ISOUtil.trimf(ISOUtil.byte2hex(val, 0, val.length));

			}
			PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNum, "Ingresa tu PIN:");
			if(info.isResultFlag()){
				if(info.isNoPin()){
					return -1 ;
				}else {
					pinBlock = ISOUtil.hexString(info.getPinblock()) ;
				}
			}else {
				pinBlock = "CANCEL" ;
				return -1 ;
			}
			return 0;
		}else {
			return 0 ;
		}

	}
}
