package newpos.libpay.trans.manager;

import android.content.Context;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Tools.TransTools;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

/**
 * Rushing transaction entity class
 * @author zhouqiang
 */
public class RevesalTrans extends FinanceTrans {



	public RevesalTrans(Context ctx, String transEname, TransInputPara p) {
		super(ctx, transEname);
		para = p;
		isUseOrgVal = true; // 使用原交易的60.1 60.3
		iso8583reverse.setHasMac(false);
		isTraceNoInc = true; // 冲正不需要自增流水号
	}

	protected void setFieldsReverso(TransLogData data) {
		LocalTime = PAYUtils.getLocalTime();
		LocalDate = PAYUtils.getLocalDate();
		AcquirerID = TransTools.ACQUIRER_ID;
		if (MsgID != null) {
			iso8583reverse.setField(0, MsgID);
		}
	/*	if (data.getPan() != null) {
			iso8583.setField(2, data.getPan());
		}*/
		if (data.getProcCode() != null) {
			iso8583reverse.setField(3, data.getProcCode());
		}

		if (TraceNo2 != null) {
			iso8583reverse.setField(11, TraceNo2);
		}
		if (LocalTime != null){
			iso8583reverse.setField(12, LocalTime);
		}
		if (LocalDate != null){
			iso8583reverse.setField(13, LocalDate);
		}

		iso8583reverse.setField(22,data.getEntryMode());

		if (AcquirerID != null) {
			iso8583reverse.setField(32, AcquirerID);
		}

		if (TermID != null) {
			iso8583reverse.setField(41, TermID);
		}

		if(TMConfig.getInstance().getmodoOperacion().equals("C")){
			if(!TMConfig.getInstance().getsesionIniciada().equals("L")){
				MerchID= ISOUtil.padright(TMConfig.getInstance().getCedula(),15,' ');
			}
		}
		if (MerchID != null) {
			iso8583reverse.setField(42, MerchID);
		}

		if (data.getField48() != null) {
			iso8583reverse.setField(48, data.getField48());
		}
		//Field63="01001401120218539D504D41494E415050";
		Field63= packField63();
		if (Field63 != null) {
			iso8583reverse.setField(63, Field63);
		}
	}

	public int sendRevesal() {
		InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
		int ret;
		TransLogData data = TransLog.getReversal();
		TraceNo2 = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');
		setFieldsReverso(data);
		InitTrans.wrlg.wrDataTxt("Enviando reverso de TraceNo " + data.getTraceNo());
		ret = OnLineTransReverso();
		if (ret == 0) {
			RspCode = iso8583reverse.getfield(39);
			if (RspCode != null) {
				if (RspCode.equals("05")){
					InitTrans.wrlg.wrDataTxt("Reverso enviado respuesta del servidor" + RspCode);
					ret = 521;
				}else {
					return Tcode.T_success;
				}
			}
		}
		return ret;
	}
}
