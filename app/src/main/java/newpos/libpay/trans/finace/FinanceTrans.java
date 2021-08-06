package newpos.libpay.trans.finace;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import cn.desert.newpos.payui.base.PayApplication;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import cnb.pichincha.wposs.mivecino_pichincha.screens.BarraEstado;
import cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn92;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkns;
import newpos.libpay.Logger;
import newpos.libpay.device.card.CardInfo;
import newpos.libpay.device.card.CardManager;
import newpos.libpay.device.pinpad.PinInfo;
import newpos.libpay.device.pinpad.PinpadManager;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.process.EmvTransaction;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.manager.RevesalTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Tools.TransTools;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;
import newpos.libpay.utils.SaveData;

import com.android.desert.keyboard.InputInfo;
import com.pos.device.SDKException;
import com.pos.device.beeper.Beeper;
import com.pos.device.config.DevConfig;
import com.pos.device.led.Led;
import com.pos.device.picc.PiccReader;
import com.pos.device.printer.Printer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * 金融交易类
 * @author zhouqiang
 */
public class FinanceTrans extends Trans {

	/**
	 * 外界输入类型
	 */
	public static final int INMODE_HAND = 0x01;
	public static final int INMODE_MAG = 0x02;
	public static final int INMODE_QR = 0x04;
	public static final int INMODE_IC = 0x08;
	public static final int INMODE_NFC = 0x10;

	/**
	 * Seleccion de ingreso de tarjeta del cliente o CNB
	 */
	public static final int TARJETA_CLIENTE = 01;
	public static final int TARJETA_CNB = 02;
	public static final int TARJETA_FUNCIONARIO = 03;
	public static final int TARJETA_CAMBIO_CLAVE = 04;



	/** 卡片模式 */
	protected int inputMode = 0x02;

	/**
	 * 是否有密码
	 */
	protected boolean isPinExist = false;


	/**
	 * 标记此次交易是否需要冲正
	 */
	protected boolean isReversal;

	/**
	 * 标记此次交易是否需要存记录
	 */
	protected boolean isSaveLog;

	/**
	 * 是否借记卡交易
	 */
	protected boolean isDebit;

	protected boolean isNeed93;

	/**
	 * 标记此交易联机前是否进行冲正上送
	 */
	protected boolean isProcPreTrans;

	/**
	 * 后置交易
	 */
	protected boolean isProcSuffix;

	protected boolean isquery;

	StringBuilder tramaImpresion = new StringBuilder();

	/**
	 * 金融交易类构造
	 * @param ctx
	 * @param transEname
	 */
	public FinanceTrans(Context ctx, String transEname) {
		super(ctx, transEname);
		iso8583.setHasMac(false);
		setTraceNoInc(true);
        StartActivity.countDownTimer.cancel();
	}



	/**
	 * Get from the kernel
	 * card number,
	 * Validity period,
	 * 2 tracks,
	 * 1 track,
	 * card serial number
	 * 55 domain data
	 */
	protected void setICCData(){

		byte[] temp = new byte[128];

		int len = PAYUtils.get_tlv_data_kernal(0x5A, temp);
		Pan = ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len));

		len = PAYUtils.get_tlv_data_kernal(0x5F24, temp);
		if(len==3) {
			ExpDate = ISOUtil.byte2hex(temp, 0, len - 1);
		}

		len = PAYUtils.get_tlv_data_kernal(0x57, temp);
		Track2 = ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len));

		len = PAYUtils.get_tlv_data_kernal(0x9F1F, temp);
		Track1 = new String(temp, 0, len);

		len = PAYUtils.get_tlv_data_kernal(0x5F34, temp);
		PanSeqNo = ISOUtil.padleft(ISOUtil.byte2int(temp, 0, len) + "", 3, '0');

		temp = new byte[512];
		len = PAYUtils.pack_tags(PAYUtils.wOnlineTags, temp);
		if (len > 0) {
			ICCData = new byte[len];
			System.arraycopy(temp, 0, ICCData, 0, len);
		} else{
			ICCData = null;
		}
	}

	/**
	 * Set the domain value of the transaction message 8583. After setting,
	 * judge the punch and so on, you can go online.
	 */
	protected void setFields() {

		AcquirerID = TransTools.ACQUIRER_ID;
		CurrencyCode = TransTools.CURRENCY_CODE;
		LocalTime = PAYUtils.getLocalTime();
		LocalDate = PAYUtils.getLocalDate();

		switch(ProcCode){
			case "951000": case "950200": case "950300":
				CurrencyCode = null;
				break;
			case "390300": case "390400":
				LocalTime = null;
				break;
			case "930300": case "930000": case "940000": case "9400001":
				AcquirerID = null;
				CurrencyCode = null;
				break;
		}

		if (isquery){
			RRN =  "000000000000";
			RspCode = "00";
		}
		if (MsgID.equals("0202")) {
			AcquirerID=null;
			CurrencyCode=null;
		}
		if (MsgID != null) {
			iso8583.setField(0, MsgID);
			msgIDultimaTrx = MsgID;
		}
		if (ProcCode != null) {
			iso8583.setField(3, ProcCode);
		}
		if (Amount >= 0) {
			String AmoutData = "";
			AmoutData = ISOUtil.padleft(Amount + "", 12, '0');
			iso8583.setField(4, AmoutData);
		}
		if (Field06 >= 0 && ProcCode.equals("410101")) {
			String BillingAmout = "";
			BillingAmout = ISOUtil.padleft(Field06 + "", 12, '0');
			iso8583.setField(6, BillingAmout);
		}
		if (Field07 != null) {
			iso8583.setField(7, Field07);
		}
		if (TraceNo != null) {
			iso8583.setField(11, TraceNo);
		}
		if (LocalTime != null) {
			iso8583.setField(12, LocalTime);
		}
		if (LocalDate != null) {
			iso8583.setField(13, LocalDate);
		}
		if (SettleDate != null) {
			iso8583.setField(15, SettleDate);
		}
		if (EntryMode != null) {
			iso8583.setField(22, EntryMode);
		}
		if (SvrCode != null) {
			iso8583.setField(25, SvrCode);
		}
		if (CaptureCode != null) {
			iso8583.setField(26, CaptureCode);
		}
		if (AcquirerID != null) {
			iso8583.setField(32, AcquirerID);
		}
		if (Track2 != null && cfg.isTrackEncrypt()) {
			Track2 = PinpadManager.getInstance().getEac(0 , Track2);
		}
		if (Track3 != null && cfg.isTrackEncrypt()) {
			Track3 = PinpadManager.getInstance().getEac(0 , Track3 );
		}
		iso8583.setField(36, Track3);
		if (RRN != null) {
			iso8583.setField(37, RRN);
		}
		if (AuthCode != null) {
			iso8583.setField(38, AuthCode);
		}
		if (RspCode != null) {
			iso8583.setField(39, RspCode);
		}
		if (TermID != null) {
			iso8583.setField(41, TermID);
		}
		if(TMConfig.getInstance().getmodoOperacion().equals("C")){
			if(!TMConfig.getInstance().getsesionIniciada().equals("L")){
				MerchID = ISOUtil.padright(TMConfig.getInstance().getCedula(),15,' ');
				if(ProcCode.equals("940000") || ProcCode.equals("940001")){
					MerchID = null;
				}
			}
		}
		if (MerchID != null) {
			MerchID = ISOUtil.padright(MerchID, 15, ' ');
			iso8583.setField(42, MerchID);
		}
		if (AcquirerName != null) {
			iso8583.setField(43, AcquirerName);
		}
		if (Field44 != null) {
			iso8583.setField(44, Field44);
		}
		if (Field48 != null) {
			iso8583.setField(48, Field48);
		}
		if (CurrencyCode != null) {
			iso8583.setField(49, CurrencyCode);
		}
		if (PIN != null) {
			iso8583.setField(52, PIN);
		}
		if (SecurityInfo != null) {
			iso8583.setField(53, SecurityInfo);
		}
		if (ExtAmount != null) {
			iso8583.setField(54, ExtAmount);
		}
		if (ICCData != null) {
			iso8583.setField(55, ISOUtil.byte2hex(ICCData));
		}
		if (Field59 != null) {
			iso8583.setField(59, Field59);
		}
		if (Field60 != null) {
			iso8583.setField(60, Field60);
		}
		if (Field61 != null) {
			iso8583.setField(61, Field61);
		}
		if (Field62 != null) {
			iso8583.setField(62, Field62);
		}
		if (Field63 != null) {
			iso8583.setField(63, Field63);
		}


	}

	/**
	 * 联机处理
	 *
	 * @return
	 */
	protected int OnLineTransReverso() {
		int ret;

		if (connect() == -1) {
			return Tcode.T_socket_err;
		}
		ret = send(true);
		if (ret != 0) {
			netWork.close();
			InitTrans.wrlg.wrDataTxt("Error en send() OnlineTransReverso() retVal = " + retVal);
			return ret;
		}
		if (isTraceNoInc) {
			cfg.incTraceNo().save();
		}
		byte[] respData = recive();
		netWork.close();
		if (respData == null) {
			return Tcode.T_receive_err;
		}
		ret = iso8583reverse.unPacketISO8583(respData);
		return ret;

	}

	protected int OnLineTransConsulta() {
		int ret;

		InitTrans.tkn07.setMensaje(null);

		if (connect() == -1) {
			return Tcode.T_socket_err;
		}
		ret = send(true);

		cfg.incTraceNo().save();
		
		if (ret != 0) {
			netWork.close();
			return ret;
		}
		/*if (isTraceNoInc) {
			cfg.incTraceNo().save();
		}*/
		byte[] respData = recive();
		netWork.close();
		if (respData == null) {
			return Tcode.T_receive_err;
		}
		ret = iso8583reverse.unPacketISO8583(respData);
		Tkns tok = new Tkns();
		if(iso8583reverse.getfield(48) != null) {
			tok.getTkn(iso8583reverse.getfield(48), iso8583reverse.getfield(39));
			String auxField48 = iso8583reverse.getfield(48);
			getTkn(auxField48);
		}
		return ret;

	}


	/**
	 * Online processing
	 * @param emvTrans
	 * @return
	 */
	protected int OnlineTrans(EmvTransaction emvTrans) {

		byte[] tag9f27 = new byte[1];
		byte[] tag9b = new byte[2];
		transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
		TraceNo = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');
		setFields();
		InitTrans.wrlg.wrDataTxt("Nuevo traceNo - " + TraceNo + " ProceCode - " + ProcCode);
		if (isProcPreTrans) {
			int ret;
			ret = verificaReverso();
			if( ret != Tcode.T_success ){
				return Tcode.T_reversal_fail;
			}
			TraceNo = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');
			if (TraceNo != null) {
				iso8583.setField(11, TraceNo);
                InitTrans.wrlg.wrDataTxt("Aumento de Trace despues de enviar reverso - " + TraceNo);
			}
		}

		int signal = BarraEstado.senal;
		setImageSignal(signal);

		if (connect() == -1){
			return Tcode.T_socket_err ;
		}
		if (isReversal) {
			try{
				InitTrans.wrlg.wrDataTxt("Guardando data de reversal, STAN " + TraceNo + " " + MsgID + "transaccion "+ TransEName);
                if (MsgID.equals("0200")) {
                    TransLogData Reveral = setReveralData();
                    TransLog.saveReversal(Reveral);
                }
			}catch (Exception e){
				InitTrans.wrlg.wrDataTxt("Fallo guardando reverso, transaccion " + TraceNo + " STAN "+ TransEName);
			}
		}

		retVal = send(false);
		if (retVal != 0){
			netWork.close();
			InitTrans.wrlg.wrDataTxt("Error en send() retVal = " + retVal);
			return retVal;
		}

		if(retVal == 0){

		    if (ProcCode == null ||	!ProcCode.equals("360010") && !ProcCode.equals("370010")) {

                cfg.incTraceNo().save();
            }
		}

		byte[] respData = recive();

		netWork.close();
		if (respData == null || respData.length <=0){
			InitTrans.wrlg.wrDataTxt("Error respData - recive");
			if (MsgID.equals("0200")) {
				int consulta = consultaAutomatica(true);
				InitTrans.wrlg.wrDataTxt("Respuesta de la consultaAutomatica - " + consulta);
				if (consulta == 0) {
					transUI.showSuccess(timeout, Tcode.Mensajes.consultaAutomaticaExitosa, "");
					return consulta;
				} else {
					return consulta;
				}
			} else {
				return Tcode.T_receive_err;
			}
		}



		retVal = iso8583.unPacketISO8583(respData);

		if(retVal!=0){
			if (retVal == Tcode.T_package_illegal){
				InitTrans.wrlg.wrDataTxt("Error unPacketISO8583");
				if(isReversal) {
					return Tcode.realizarReverso;
					/*
					int rev = verificaReverso();
					if (rev != Tcode.T_success) {
						return rev;
					} else {
						transUI.showSuccess(timeout, Tcode.Mensajes.reversoExitoso, "");
						return Tcode.transReversada;
					}
					*/
				}else {
					return Tcode.T_receive_err;
				}
			}
			return retVal ;
		}

		RspCode = iso8583.getfield(39);
		AuthCode = iso8583.getfield(38);

		Tkns tok = new Tkns();
		if(iso8583.getfield(48) != null) {
			String tokInfo = tok.getTkn(iso8583.getfield(48), iso8583.getfield(39));
			if (!tokInfo.equals("OK")){
				transUI.showMsgError(timeout, tokInfo);
			}
			String auxField48 = iso8583.getfield(48);
			getTkn(auxField48);
		}


		String strICC = iso8583.getfield(55);
		if (strICC != null && (!strICC.trim().equals(""))){
			ICCData = ISOUtil.str2bcd(strICC, false);
		}else{
			ICCData = null;
		}
		if(!"00".equals(RspCode)){
			if(isReversal) {
				TransLog.clearReveral();
			}
			if(RspCode.equals(ISO8583.RSPCODE.RSP_MULTICUENTA)){
				if(validar_rta_97()){
					InitTrans.tkn07.setMensaje(null);
					return multipleCuenta(emvTrans);
				}
			}
			return formatRsp(RspCode);
		}


		/*
		if (emvTrans != null && retVal == 0 ){
			int afterOnline = emvTrans.afterOnline(RspCode, AuthCode, ICCData, retVal);
			int lenOf9f27 = PAYUtils.get_tlv_data_kernal(0x9F27, tag9f27);
			if (lenOf9f27 != 1) {
				TransLogData revesalData = TransLog.getReversal();
				if(revesalData!=null){
					revesalData.setRspCode("06");
					TransLog.saveReversal(revesalData);
				}
			}

			if(tag9f27[0] == 0x40){
				int len9b = PAYUtils.get_tlv_data_kernal(0x9b, tag9b);
				if (len9b == 2 && (tag9b[0] & 0x04) != 0) {

					byte[] temp = new byte[256];
					int len = PAYUtils.pack_tags(PAYUtils.wISR_tags, temp);
					if (len > 0) {
						ICCData = new byte[len];
						System.arraycopy(temp, 0, ICCData, 0, len);
					} else {
						ICCData = null;
					}
					TransLogData scriptResult = setScriptData();
					TransLog.saveScriptResult(scriptResult);
				}
			}
		}

		if (retVal != 0){
			return retVal ;
		}
		*/


		/*
		TransLogData data = TransLog.getScriptResult();
		if (data != null) {
			ScriptTrans script = new ScriptTrans(context, "SENDSCRIPT");
			int ret = script.sendScriptResult(data);
			if (ret == 0) {
				TransLog.clearScriptResult();
			}
		}
		*/

		if (isSaveLog) {
			try {
				InitTrans.wrlg.wrDataTxt("SaveLog transaccion "+ TransEName + " MsgID " + MsgID);
				if (!MsgID.equals("0420")){
					if(ProcCode != null) {
						TransLogData logData = setLogData();
						TransLog usuario = TransLog.getInstance(MerchID, true);
						usuario.saveLog(logData, MerchID);

						TransLog settle = TransLog.getInstance(CIERRE_TOTAL_LOG, true);
						settle.saveLog(logData, CIERRE_TOTAL_LOG);

						String logDataStr = "MsgID = " + MsgID +
								"\nProcCode = " + ProcCode +
								"\nOprNo = " + cfg.getOprNo() +
								"\nBatchNo = " + BatchNo +
								"\nEName = " + TransEName +
								"\nTraceNo = " + iso8583.getfield(11) +
								"\nLocalTime = " + iso8583.getfield(12) +
								"\nLocalDate = " + PAYUtils.getYear() + iso8583.getfield(13) +
								"\nExpDate = " + iso8583.getfield(14) +
								"\nSeattleDate = " + iso8583.getfield(15) +
								"\nAcquirerID = " + iso8583.getfield(32) +
								"\nRRN = " + iso8583.getfield(37) +
								"\nRspCode = " + iso8583.getfield(39) +
								"\nAmount = " + Amount +
								"\nTotalAmount = " + TotalAmount +
								"\nComision = " + Comision +
								"\nTermID = " + TermID +
								"\nMerchID = " + MerchID;

						String nomarchivo = "LogData ";
						nomarchivo += PAYUtils.getLocalDate() + " " + PAYUtils.getLocalTime() + " ";
						nomarchivo += Trans.MsgID + " " + Trans.ProcCode;

						ISOUtil.guardarTxt(logDataStr, "/sdcard/logs/data", nomarchivo);
					}
				}
			} catch (Exception e) {
				InitTrans.wrlg.wrDataTxt("Fallo en SaveLog, transaccion "+ TransEName + " STAN " + TraceNo);
			}

		}

		if(isReversal) {
			TransLog.clearReveral();  // Elimina el reverso porque ya todo funciono bien
		}

		if(para.isNeedPrint()) {
			InitTrans.wrlg.wrDataTxt("NeedPrint inicia trans "+ TransEName + " MsgID " + MsgID);
			if (impresionRecibo()){
				InitTrans.wrlg.wrDataTxt("Impresion exitosa - OnlineTrans");
				retVal = 0;
			} else {
			    if(retVal == 93){
			        return retVal;
                }
				InitTrans.wrlg.wrDataTxt("Error en impresion " + retVal);
				if (MsgID.equals("0200")) {
					if (imprimirInfoLocal() == 0) {
						return 1995;
					}
				} else {
					return Tcode.T_receive_err;
				}
			}
		}
		return retVal ;
	}

    private int consultaAutomatica(boolean guardarLogConsulta) {
        int ret = 1;
        int reintentos = 1;
        String rspCode, mensaje;

        do {
			setFieldsConsulta();
            transUI.newHandling(context.getResources().getString(R.string.procesando), timeout, Tcode.Mensajes.consultaAutomaticaEnProceso);
			InitTrans.wrlg.wrDataTxt("Enviando consultaAutomatica() de recibo - intento # " + reintentos);
            ret = OnLineTransConsulta();
            if(ret == Tcode.T_success ){
				if(isReversal) {
					TransLog.clearReveral();
				}
                reintentos=0;
            }
            /*else {
				cfg.incTraceNo().save();
			}*/

            reintentos--;

        } while (reintentos>0);

		InitTrans.wrlg.wrDataTxt("Estado de la consultaAutomatica " + ret);

        rspCode = iso8583reverse.getfield(39);
        mensaje = InitTrans.tkn07.obtenerMensaje();
        if (rspCode == null) {
            rspCode = "";
        }

        if (rspCode.equals("00")) {
			isquery = true;
			if (guardarLogConsulta) {
				TransLogData logData = setLogData();
				TransLog usuario = TransLog.getInstance(MerchID, true);
				usuario.saveLog(logData, MerchID);
				TransLog settle = TransLog.getInstance(CIERRE_TOTAL_LOG, true);
				settle.saveLog(logData, CIERRE_TOTAL_LOG);
				saveLogConsulta();
			}
			if (isReversal) {
				TransLog.clearReveral();
			}

            if (impresionReciboConsulta()){
            	ret = 0;
            } else {
                if (ret == 93){
                    return 93;
                }
                ret = 1995;
            }
        } else if (!mensaje.equals("")){
            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
            if (isReversal){
				return Tcode.realizarReverso;
			} else {
            	return Tcode.T_receive_err;
			}

        } else {
            if (imprimirInfoLocal() == 0) {
				if (isReversal){
					return Tcode.realizarReverso;
				} else {
					return Tcode.T_receive_err;
				}
            }
        }

        return ret;
    }

    private void setFieldsConsulta() {
        String merchID = "", fld48 = "", date;
        String AcquirerID2 = TransTools.ACQUIRER_ID;
        String CurrencyCode2 = TransTools.CURRENCY_CODE;
        String LocalTime2 = PAYUtils.getLocalTime();
        String LocalDate2 = PAYUtils.getLocalDate();
        date = Utils.getDateYearMonthDay();
        date = date.replace("/", "");

        InitTrans.tkn12.setCedula(ISOUtil.padright(ProcCode, 16, 'F').getBytes());
        tkn92 = new Tkn92();
        tkn92.setItemSelect("1");
        tkn92.setInputDate(date);
        tkn92.setInputText(String.valueOf(Integer.parseInt(TraceNo)));

        TraceNo2 = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');

        fld48 = InitTrans.tkn12.packTkn12();
        fld48 += tkn92.packTkn92();
        fld48 += InitTrans.tkn93.packTkn93();

        EntryMode = "0021";

        iso8583reverse.setField(0, "0800");
        iso8583reverse.setField(3, "930210");
        iso8583reverse.setField(11, TraceNo2);
        iso8583reverse.setField(12, LocalTime2);
        iso8583reverse.setField(13, LocalDate2);
        iso8583reverse.setField(22, EntryMode);
        iso8583reverse.setField(32, AcquirerID2);
        iso8583reverse.setField(41, TermID);
        if(TMConfig.getInstance().getmodoOperacion().equals("C")){
            if(!TMConfig.getInstance().getsesionIniciada().equals("L")){
                merchID = ISOUtil.padright(TMConfig.getInstance().getCedula(),15,' ');
                if(ProcCode.equals("940000") || ProcCode.equals("940001")){
                    merchID = null;
                }
            }
        }
        merchID = ISOUtil.padright(TMConfig.getInstance().getCedula(), 15, ' ');
        iso8583reverse.setField(42, merchID);
        iso8583reverse.setField(48, fld48);
        iso8583reverse.setField(49, CurrencyCode2);
        iso8583reverse.setField(63, Field63);
    }

    public void saveLogConsulta() {
        String nomarchivo = "LogData ";
        nomarchivo += PAYUtils.getLocalDate() + " " + PAYUtils.getLocalTime() + " ";
        nomarchivo += Trans.MsgID + " " + Trans.ProcCode;

        String logDataStr = "MsgID = " + MsgID +
                "\nProcCode = " + ProcCode +
                "\nOprNo = " + cfg.getOprNo() +
                "\nBatchNo = " + BatchNo +
                "\nEName = " + TransEName +
                "\nTraceNo = " + iso8583reverse.getfield(11) +
                "\nLocalTime = " + iso8583reverse.getfield(12) +
                "\nLocalDate = " + PAYUtils.getYear() + iso8583reverse.getfield(13) +
                "\nExpDate = " + iso8583reverse.getfield(14) +
                "\nSeattleDate = " + iso8583reverse.getfield(15) +
                "\nAcquirerID = " + iso8583reverse.getfield(32) +
                "\nRRN = " + iso8583reverse.getfield(37) +
                "\nRspCode = " + iso8583reverse.getfield(39) +
                "\nAmount = " + Amount +
                "\nTotalAmount = " + TotalAmount +
                "\nComision = " + Comision +
                "\nTermID = " + TermID +
                "\nMerchID = " + MerchID;

        ISOUtil.guardarTxt(logDataStr, "/sdcard/logs/data", nomarchivo);

        String fechaActual = Utils.getDate();
        SaveData.getInstance().setFechaUltimaTrans(fechaActual);
    }

    private int imprimirInfoLocal(){
		String finalMinimoRetiro = "";
        int trace = Integer.parseInt(TraceNo);
        int AmountInt =0, ret = 1, CostoServicio = 0;
		tramaImpresion = new StringBuilder();
        String trans = TransEName;
        if (trans.equals("RECAU")){
            if (InitTrans.tkn06.getNomEmpresa() != null){
                trans = ISOUtil.hex2AsciiStr(String.valueOf(ISOUtil.bcd2str(InitTrans.tkn06.getNomEmpresa(), 0, InitTrans.tkn06.getNomEmpresa().length)));
            }
        }
        if(MsgID != null && MsgID.equals("0200")){
            if (Amount > 0) {
                AmountInt = Integer.parseInt(String.valueOf(Amount));
            }
            if (Comision > 0){

				String costoRetiro = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(),0,
						InitTrans.tkn80.getCostoServicio().length);
				Double converCostoRetiro = Double.parseDouble(costoRetiro);
				DecimalFormat formatMinimoRetiro = new DecimalFormat("00,00");
				finalMinimoRetiro = String.valueOf(formatMinimoRetiro.format(converCostoRetiro));

            	//CostoServicio = Integer.parseInt(String.valueOf(Comision));
			}
            tramaImpresion.append("BANCO PICHINCHA C.A.  \n");
			tramaImpresion.append("Transacción.....: ").append(trans).append("\n");
			if (TermID != null)
				tramaImpresion.append("ID POS..........: ").append(TermID).append("\n");
			tramaImpresion.append("Fecha...........: ").append(Utils.getDateYearMonthDay()).append(" ").append(Utils.getTime()).append("\n");
			tramaImpresion.append("Valor...........: ").append(ISOUtil.formatMiles(AmountInt)).append("\n");
			if (Comision > 0)
				tramaImpresion.append("Servicio........: ").append(finalMinimoRetiro).append("\n");
			if (!ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn03.getContrapartida())).trim().equals(""))
				tramaImpresion.append("Contrapartida...: ").append(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn03.getContrapartida()))).append("\n");
			if (!ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()).trim().equals(""))
				tramaImpresion.append(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()))).append("\n");
			tramaImpresion.append("\n" + "Favor espera unos minutos" + "\n"
					+ "(1) Realiza un cierre parcial" + "\n"
					+ "(2) Realiza una consulta de 10 últimos movimientos CNB" + "\n"
					+ "(3) Reimpresión de Cupón Histórico con el siguiente número de documento \n")
					.append(TraceNo).append(" o comunicate con el call center 02-3982200");




			SaveData.getInstance().setReciboConsultaAuto(tramaImpresion.toString());

            ret = imprimir();
            //transUI.almacenarRecibo(true);
        }
        return ret;
    }

    public boolean confirmacionConsulta(String code, String title) {
        boolean ret = false;
        RRN=iso8583.getfield(37);
        RspCode=iso8583.getfield(39);
        setFixedDatas();
        clearDataFields();
        iso8583.clearData();
        transUI.newHandling(title ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        TermID= TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        ProcCode = code;
        para.setNeedPrint(false);
        setFields();
        retVal = 0;
        retVal = enviarConfirmacion();
        if(retVal!=0){
            transUI.showError(timeout , retVal);
        }else{
            ret = true;
        }
        return ret;
    }

    private boolean impresionReciboConsulta(){
        boolean ret = false;
        byte[] estadoTok93_0 = new byte[]{0x00};
        byte[] estadoTok93_1 = new byte[]{0x01};
        byte[] estadoTok93_2 = new byte[]{0x02};
        if (iso8583reverse.getfield(61) != null || iso8583reverse.getfield(62) != null) {
            transUI.imprimiendo(timeout, Tcode.Mensajes.imprimiendoRecibo);
            if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_0)) {
                do {
                    if (iso8583reverse.getfield(61) != null) {
                        if (iso8583reverse.getfield(62) != null) {
                            tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                            tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                        } else {
                            tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                        }
                    } else if (iso8583reverse.getfield(62) != null) {
                        tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                    }
                    // Guardar en un txt la tramaImpresion
					guardarImpresionTxt();
                    InitTrans.ultimoRecibo = tramaImpresion.toString();
                    transUI.almacenarRecibo(false);
                    retVal = imprimir();
                } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
                if (retVal == Printer.PRINTER_OK) {
                    retVal = 0;
                } else {
                    retVal = Tcode.T_printer_exception;
                }
            } else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_1)) {
                if (iso8583reverse.getfield(61) != null) {
                    if (iso8583reverse.getfield(62) != null) {
                        tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                        tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                    } else {
                        tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                    }
                } else if (iso8583reverse.getfield(62) != null) {
                    tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                }
                // Guardar en un txt la tramaImpresion explicando 93
                retVal = 93;
                return false;
            } else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_2)) {
                transUI.imprimiendo(timeout, Tcode.Mensajes.imprimiendoRecibo);
                do {
                    if (iso8583reverse.getfield(61) != null) {
                        if (iso8583reverse.getfield(62) != null) {
                            tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                            tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                        } else {
                            tramaImpresion.append(impresion(iso8583reverse.getfield(61)));
                        }
                    } else if (iso8583reverse.getfield(62) != null) {
                        tramaImpresion.append(impresion(iso8583reverse.getfield(62)));
                    }
					guardarImpresionTxt();
                    InitTrans.ultimoRecibo = tramaImpresion.toString();
                    transUI.almacenarRecibo(false);
                    retVal = imprimir();
                } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
                if (retVal == Printer.PRINTER_OK) {
                    retVal = 0;
                } else {
                    retVal = Tcode.T_printer_exception;
                }
            }
            tramaImpresion.setLength(0);
            ret = true;
        } else {
			InitTrans.wrlg.wrDataTxt("Error - Validacion de los campos 61 y 62 en impresionReciboConsulta() fue null");
		}
        return ret;
    }

	/**
	 * impresionRecibo()
	 *
	 * @author Fabian Ardila
	 */
	private boolean impresionRecibo(){
		boolean ret = false;
		byte[] estadoTok93_0 = new byte[]{0x00};
		byte[] estadoTok93_1 = new byte[]{0x01};
		byte[] estadoTok93_2 = new byte[]{0x02};
		if (iso8583.getfield(61) != null || iso8583.getfield(62) != null) {
			transUI.imprimiendo(timeout, Tcode.Mensajes.imprimiendoRecibo);
			if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_0)) {
				do {

					String procCode = "";
					if (ProcCode != null) {
						if (ProcCode.equals("930200") || ProcCode.equals("930210")) {
							if (iso8583.getfield(61) != null) {
								procCode = iso8583.getfield(61).substring(8, 20);
								procCode = ISOUtil.stringToHex(procCode).toString();
							}
						}
					}

					for (int x = 0; x < context.getResources().getStringArray(R.array.ProcodeTemplate).length; x++){
						if (iso8583.getfield(3).equals(context.getResources().getStringArray(R.array.ProcodeTemplate)[x])){

							for (int i = 0; i < InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), false).size(); i++){
								tramaImpresion.append(InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), false).get(i)+"\n");
							}
							break;

						} else if (procCode.equals("496021")){
							for (int i = 0; i < InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).size(); i++){
								tramaImpresion.append(InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).get(i)+"\n");
							}
							break;

						}else {

							if (iso8583.getfield(61) != null) {
								if (iso8583.getfield(62) != null) {
									tramaImpresion.append(impresion(iso8583.getfield(61)));
									tramaImpresion.append(impresion(iso8583.getfield(62)));
								} else {
									tramaImpresion.append(impresion(iso8583.getfield(61)));
								}
							} else if (iso8583.getfield(62) != null) {
								tramaImpresion.append(impresion(iso8583.getfield(62)));
							}
							break;

						}
					}
					// Guardar en un txt la tramaImpresion
					guardarImpresionTxt();
					InitTrans.ultimoRecibo = tramaImpresion.toString();
					InitTrans.wrlg.wrDataTxt("Almacenando recibo" + "Trace" +iso8583.getfield(11));

					transUI.almacenarRecibo(false);
					retVal = imprimir();
				} while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
				if (retVal == Printer.PRINTER_OK) {
					retVal = 0;
				} else {
					retVal = Tcode.T_printer_exception;
				}
			} else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_1)) {
				InitTrans.wrlg.wrDataTxt("Impresion con token 93 en proceso");
				if (iso8583.getfield(61) != null) {
					if (iso8583.getfield(62) != null) {
						tramaImpresion.append(impresion(iso8583.getfield(61)));
						tramaImpresion.append(impresion(iso8583.getfield(62)));
					} else {
						tramaImpresion.append(impresion(iso8583.getfield(61)));
					}
				} else if (iso8583.getfield(62) != null) {
					tramaImpresion.append(impresion(iso8583.getfield(62)));
				}
				// Guardar en un txt la tramaImpresion explicando 93
				retVal = 93;
				return false;
			} else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_2)) {
				transUI.imprimiendo(timeout, Tcode.Mensajes.imprimiendoRecibo);
				InitTrans.wrlg.wrDataTxt("Impresion con token 93 finalizando");
				do {
					String procCode = "";
					if (ProcCode != null) {
						if (ProcCode.equals("930200") || ProcCode.equals("930210")) {
							if (iso8583.getfield(61) != null) {
								procCode = iso8583.getfield(61).substring(8, 20);
								procCode = ISOUtil.stringToHex(procCode).toString();
							}
						}
					}

					if (procCode.equals("496021")){
						if (iso8583.getfield(61) != null) {
							if (iso8583.getfield(62) != null) {
								for (int i = 0; i < InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).size(); i++){
									tramaImpresion.append(InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).get(i)+"\n");
								}
								tramaImpresion.append(impresion(iso8583.getfield(62)));
							} else {
								for (int i = 0; i < InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).size(); i++){
									tramaImpresion.append(InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(61)), true).get(i)+"\n");
								}
							}
						} else if (iso8583.getfield(62) != null) {
							for (int i = 0; i < InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(62)), true).size(); i++){
								tramaImpresion.append(InitTrans.tkn08.unpackImpresion(impresion(iso8583.getfield(62)), true).get(i)+"\n");
							}
						}

					} else {
						if (iso8583.getfield(61) != null) {
							if (iso8583.getfield(62) != null) {
								tramaImpresion.append(impresion(iso8583.getfield(61)));
								tramaImpresion.append(impresion(iso8583.getfield(62)));
							} else {
								tramaImpresion.append(impresion(iso8583.getfield(61)));
							}
						} else if (iso8583.getfield(62) != null) {
							tramaImpresion.append(impresion(iso8583.getfield(62)));
						}
					}
					guardarImpresionTxt();
					InitTrans.ultimoRecibo = tramaImpresion.toString();
					InitTrans.wrlg.wrDataTxt("Almacenando recibo" + "Trace" +iso8583.getfield(11));
					transUI.almacenarRecibo(false);
					retVal = imprimir();
				} while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
				if (retVal == Printer.PRINTER_OK) {
					retVal = 0;
				} else {
					retVal = Tcode.T_printer_exception;
				}
			}
			tramaImpresion.setLength(0);
			ret = true;
		} else {
			InitTrans.wrlg.wrDataTxt("Error - Validacion de los campos 61 y 62 en impresionRecibo() fue null");
		}
		return ret;
	}

	private void guardarImpresionTxt(){
		/*String nomarchivo = "Print ";
		try {
			nomarchivo += PAYUtils.getLocalDate()+ " " +PAYUtils.getLocalTime()+ " ";
			nomarchivo += Trans.MsgID + " " + Trans.ProcCode + " " + versionApk(context);

			ISOUtil.guardarTxt(tramaImpresion.toString(),"/sdcard/logs/print",nomarchivo);
		}catch (Exception e){
			InitTrans.wrlg.wrDataTxt("Error en guardarImpresionTxt() - " + nomarchivo + " - error " + e.toString());
		}*/

	}

	private int imprimir() {
		int ret = -1;
		PrintManager printManager = PrintManager.getmInstance(context , transUI);
		String mensajeImpresion = tramaImpresion.toString();
		int limite = mensajeImpresion.length();
		int offset = 0;
		int valor = 4500;
		int ind = mensajeImpresion.indexOf("PAUSE");
		if (ind == -1){
			if(para.isNeedConfirmPrint()) {
				String[] mensajePause = {"Presiona el botón aceptar para imprimir."," "," "," "," "};
				InputInfo inputInfo  = transUI.showMsgConfirmacion(timeout, mensajePause, false);
				if(inputInfo.isResultFlag()){
					if (inputInfo.getResult().equals("aceptar")){
						ret = printManager.printPichincha(mensajeImpresion,false);
					} else{
                        transUI.showSuccess(timeout, 15, "");
					}
				}else{
					ret = 0;
				}
			}else {
				if (limite > 5500) {
					while (limite > 0) {
						if (limite >= valor) {
							ret = printManager.printPichincha(mensajeImpresion.substring(offset, offset + valor), false);
							limite-=valor;
							offset+=valor;
						} else {
							ret = printManager.printPichincha(mensajeImpresion.substring(offset), false);
							break;
						}
					}
				} else {
					ret = printManager.printPichincha(mensajeImpresion, false);
				}
			}
		}else {
			String[] impresionPause;
			impresionPause = mensajeImpresion.split("PAUSE");
			ret = printManager.printPichincha(impresionPause[0], false);
			if (ret == 0){
				String[] mensajePause = {"Presiona el botón aceptar para continuar la impresión."," "," "," "," "};
				InputInfo inputInfo  = transUI.showMsgConfirmacion(timeout, mensajePause, false);
				if(inputInfo.isResultFlag()){
					if (inputInfo.getResult().equals("aceptar")){
						ret =  printManager.printPichincha(impresionPause[1], false);
					} else{
                        transUI.showSuccess(timeout, 15, "");
					}
				}
			}
		}

		return ret;
	}

	/*
    DEBE RETORNAR :
      0 - T_success :  si existió reverso y fue evacuado correctamente, o si no existió reverso
    T_reversal_fail : si existió fallo en el envío del reverso, o fallo en la recepcion de respuesta del host
     */
	public int verificaReverso(){
		int ret = Tcode.T_reversal_fail ;
		int reintentos = 3;
		String transEname = "";

		TransLogData revesalData = TransLog.getReversal();
		if (revesalData == null) {
			InitTrans.wrlg.wrDataTxt("Verificando Reverso - Reverso no encontrado");
			ret = Tcode.T_success;
		}else{
			try {
				if (revesalData.getEName().equals("RECAU")) {
					transEname = "RECAUDACION";
				} else {
					transEname = revesalData.getEName();
				}
				transUI.handling(context.getResources().getString(R.string.procesando), timeout, "Reversando " + transEname + "\nmonto: " + revesalData.getAmount());
			}catch (Exception e){
				InitTrans.wrlg.wrDataTxt("Error por no tener variable getEName en el archivos de logs. " + e);
			}
			//transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.enviandoReverso);
			RevesalTrans revesal = new RevesalTrans(context, "REVERSAL", para);
			if (!InitTrans.ultimoRecibo.equals("NO")){
				if (revesal.TraceNo.equals(InitTrans.ultimoRecibo.substring(0,6))){
					InitTrans.ultimoRecibo = "NO";
				}
			}
			do {
				InitTrans.wrlg.wrDataTxt("Enviando reverso - intento # " + reintentos);
				ret = revesal.sendRevesal();
				if(ret == Tcode.T_success ){
					TransLog.clearReveral();
					reintentos=0;
				}
				reintentos--;
			}while(reintentos>0);
		}

		if(ret != Tcode.T_success){
		    if (ret == 521){
                transUI.showMsgError(timeout, "Su transaccion sera reversada.\nProcesando reverso");
            }else {
                transUI.showMsgError(timeout, "Es probable que en el momento existan problemas" +
                        "de comunicacion reverso pendiente");
            }
			ret = Tcode.T_reversal_fail ;

			if(imprimirUltimaTRX) {
				//imprimirUltimaTrx();
			}
		}

		return ret;

	}

	private void imprimirUltimaTrx(){
		imprimirUltimaTRX = false;
		int rsp;
		PrintManager printManager = PrintManager.getmInstance(context);
		do{
			rsp = printManager.printReimpresion(InitTrans.ultimoReciboSecuencial, false);
		}while (rsp == Printer.PRINTER_STATUS_PAPER_LACK);
	}

	/*************************************************************
	 *  Descripcion: Empaqueta el campo 59 compuesto por los
	 *  			 siguientes tags EMV
	 *  			 0x9F26 - App Cryptogram
	 *  			 0x9F06 - AID
	 *  			 0x95   - Terminal Verification Results
	 *  			 0x9B   - Transaction Status Information
	 *  Argumento: N/A
	 *  Retorno:  N/A
	 * ***********************************************************/

	public String armar59(){
		byte[] temp = new byte[128];
		int len =0;
		len = PAYUtils.get_tlv_data_kernal(0x9F26, temp);
		Field59 = String.format("%02d",len)  + ISOUtil.byte2hex(temp, 0, len);
		len = PAYUtils.get_tlv_data_kernal(0x9F06, temp);
		Field59 =Field59 + String.format("%02d",len) +ISOUtil.byte2hex(temp, 0, len);
		len = PAYUtils.get_tlv_data_kernal(0x95, temp);
		Field59 =Field59 + String.format("%02d",len) +ISOUtil.byte2hex(temp, 0, len);
		len = PAYUtils.get_tlv_data_kernal(0x9B, temp);
		Field59 =Field59 + String.format("%02d",len) +ISOUtil.byte2hex(temp, 0, len);
		return Field59;
	}
	/**
	 * Set transaction information from the server Data From server
	 * @return TransLog
	 */
	private TransLogData setLogData() {
		TransLogData LogData = new TransLogData();
		LogData.setMsgID(MsgID);
		LogData.setProcCode(ProcCode);
		LogData.setOprNo(cfg.getOprNo());
		LogData.setBatchNo(BatchNo);
		LogData.setEName(TransEName);
		LogData.setTraceNo(iso8583.getfield(11));
		LogData.setLocalTime(iso8583.getfield(12));
		LogData.setLocalDate(PAYUtils.getYear() + iso8583.getfield(13));
		LogData.setExpDate(iso8583.getfield(14));
		LogData.setSettleDate(iso8583.getfield(15));
		LogData.setAcquirerID(iso8583.getfield(32));
		LogData.setRRN(iso8583.getfield(37));
		LogData.setRspCode(iso8583.getfield(39));
		LogData.setICCData(ICCData);
		LogData.setAmount(Amount);
		LogData.setTotalAmount(TotalAmount);
		LogData.setComision(Comision);
		LogData.setTermID(TermID);
		LogData.setMerchID(MerchID);
		return LogData;
	}

	/**
	 * 保存扫码交易数据
	 * @param code 付款码
	 * @return
	 */
	protected TransLogData setScanData(String code){
		TransLogData LogData = new TransLogData();
		LogData.setAmount(Amount);
		LogData.setPan(code);
		LogData.setOprNo(cfg.getOprNo());
		LogData.setBatchNo(BatchNo);
		LogData.setEName(TransEName);
		LogData.setICCData(ICCData);
		LogData.setLocalDate(PAYUtils.getYMD());
		LogData.setTraceNo(TraceNo);
		LogData.setLocalTime(PAYUtils.getHMS());
		LogData.setSettleDate(PAYUtils.getYMD());
		LogData.setAcquirerID("12345678");
		LogData.setRRN("170907084952");
		LogData.setRspCode("00");
		return LogData;
	}

	/**
	 * 脱机打单

	 * @return
	 */
	protected int offlineTrans( ){
		if (isSaveLog) {
			TransLogData LogData = new TransLogData();

			LogData.setPan(PAYUtils.getSecurityNum(Pan, 6, 4));
			LogData.setOprNo(cfg.getOprNo());
			LogData.setEName(TransEName);
			LogData.setTraceNo(cfg.getTraceNo());
			LogData.setBatchNo(cfg.getBatchNo());
			LogData.setLocalDate(PAYUtils.getYear() + PAYUtils.getLocalDate());
			LogData.setLocalTime(PAYUtils.getLocalTime());
			LogData.setICCData(ICCData);
			transLog.saveLog(LogData);
			if(isTraceNoInc){
				cfg.incTraceNo();
			}
		}
		if(para.isNeedPrint()){
			transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.imprimiendoRecibo);
			PrintManager print = PrintManager.getmInstance(context , transUI);
			do{
				retVal = print.print(transLog.getLastTransLog(), false);
			}while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
			if (retVal == Printer.PRINTER_OK) {
				return 0 ;
			}else {
				return Tcode.T_printer_exception ;
			}
		}else {
			return 0 ;
		}
	}

	/**
	 * 设置发卡行脚本数据
	 * @return
	 */
	private TransLogData setScriptData() {
		TransLogData LogData = new TransLogData();
		LogData.setPan(PAYUtils.getSecurityNum(Pan, 6, 4));
		LogData.setICCData(ICCData);
		LogData.setBatchNo(BatchNo);
		LogData.setAmount(Long.parseLong(iso8583.getfield(4)));
		LogData.setTraceNo(iso8583.getfield(11));
		LogData.setLocalTime(iso8583.getfield(12));
		LogData.setLocalDate(iso8583.getfield(13));
		LogData.setAcquirerID(iso8583.getfield(32));
		LogData.setRRN(iso8583.getfield(37));
		return LogData ;
	}

	/**
	 * Setea los campos que se guardan en el log del reverso.
	 * @return Archivo tipo TransLogData
	 */
	private TransLogData setReveralData() {
		InitTrans.wrlg.wrDataTxt("Inicio registro reverso");
		TransLogData LogData = new TransLogData();
		LogData.setPan(Pan);				//2
		LogData.setProcCode(ProcCode);
		LogData.setAmount(Amount);
		LogData.setTotalAmount(TotalAmount);
		LogData.setTraceNo(TraceNo);
		LogData.setExpDate(ExpDate);
		LogData.setEntryMode(EntryMode);
		LogData.setEName(TransEName);
		LogData.setRspCode("98");

		if(TMConfig.getInstance().getmodoOperacion().equals("C")){
			if(!TMConfig.getInstance().getsesionIniciada().equals("L")){
				MerchID= ISOUtil.padright(TMConfig.getInstance().getCedula(),15,' ');
				LogData.setMerchID(MerchID);
			}
		}

		LogData.setField48("900010"+MsgID+ TraceNo+ LocalDate +LocalTime);
		/*byte[] temp = new byte[156];
		if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
			int len = PAYUtils.pack_tags(PAYUtils.reversal_tag, temp);
			if (len > 0) {
				ICCData = new byte[len];
				System.arraycopy(temp, len, ICCData, 0, len);
				LogData.setICCData(ICCData);
			} else {
				ICCData = null;
			}
		}*/
		InitTrans.wrlg.wrDataTxt("Fin registro reverso");
		return LogData;
	}

	/**
	 * Formatted response code
	 * @param rsp
	 * @return
	 */
	private int formatRsp(String rsp){
		String[] stand_rsp = {"5A","5B","6A","A0","D1","D2","D3","D4","N6","N7"} ;
		int START = 200 ;
		boolean finded = false ;
		for (int i = 0 ; i < stand_rsp.length ; i++){
			if(stand_rsp[i].equals(rsp)){
				START += i ;
				finded = true ;
				break;
			}
		}
		if(finded){
			return START ;
		}else {
			try {
				return Integer.parseInt(rsp);
			} catch (Exception e){
				return 1994;
			}
		}
	}

	/**
	 *
	 * @param tipo
	 * @return
	 */
	public boolean leerTarjeta(int tipo) {
		boolean ret = false;
		CardInfo cardInfo = transUI.getCardUse(timeout , INMODE_IC|INMODE_MAG, tipo);
		if(cardInfo.isResultFalg()){
			int type = cardInfo.getCardType() ;
			switch (type){
				case CardManager.TYPE_MAG :
					EntryMode = "0021";
					inputMode = ENTRY_MODE_MAG ;
					break;
				case CardManager.TYPE_ICC :
					EntryMode = "0051";
					inputMode = ENTRY_MODE_ICC ;
					break;
			}
			para.setInputMode(inputMode);
			transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
			if(inputMode == ENTRY_MODE_ICC){
				ret = isICC();
			}if(inputMode == ENTRY_MODE_MAG){
				String tracks[] = cardInfo.getTrackNo();
				if(tracks[0].length() > 0 && tracks[1].length() > 0){
					ret = isMag(cardInfo.getTrackNo(), tipo);
				}else {
					transUI.showError(timeout , 2078);
				}
			}
		}else {
			int error = cardInfo.getErrno();
			if(error == 0){
				transUI.showMsgError(timeout , "Transacción cancelada");
			}else {
				transUI.showError(timeout , cardInfo.getErrno());
			}

		}
		return ret;
	}

	/**
	 *
	 * @return
	 */
	public String leerCedualNfc() {
		String ret = "";
		CardInfo cardInfo = transUI.getCedulaNfc(timeout , INMODE_NFC);
		transUI.newHandling("Lectura NFC" ,timeout , Tcode.Mensajes.transaccionEnProceso);
		if(cardInfo.isResultFalg()){
			int type = cardInfo.getCardType();
			if (type == CardManager.TYPE_NFC) {
				//ntryMode = "0051";
				inputMode = ENTRY_MODE_NFC ;
			}
			para.setInputMode(inputMode);
			if (inputMode == ENTRY_MODE_NFC) {
				if(isNfc()){
					String typeCard = setCardType(cardInfo.getNfcType());
					String uid = cardInfo.getNfcUid();
					if (typeCard.equals("B")) {
						uid = uid.substring(0, 8);
					}
					ret = typeCard + "@" + uid;
				}else{
					ret = "";
				}
			}
		}else {
			int error = cardInfo.getErrno();
			if(error == 0){
				transUI.showMsgError(timeout , "Transacción cancelada");
			}else {
				transUI.showError(timeout , cardInfo.getErrno());
			}

		}
		return ret;
	}

	private boolean isICC(){
		boolean ret = false;

		emv = new EmvTransaction(para);
		//transUI.newHandling("Procesando",timeout , Tcode.Mensajes.transaccionEnProceso);
		retVal = emv.start() ;
		if(retVal == 1 || retVal == 0){
			if (emv.getPinBlock().equals("CANCEL")) {
				return false;
			}
			isPinExist=!(PAYUtils.isNullWithTrim(emv.getPinBlock()));
			if (isPinExist) {
				PIN = emv.getPinBlock();
			}
			setICCData();
			ret = true;
		}else {
			transUI.showError(timeout , retVal);
		}
		return ret;
	}

	private boolean isNfc(){
		boolean ret = true;
		FnBeep();
		Led.setLight(Led.LED_NFC_1,Led.LED_ON);
		Led.setLight(Led.LED_NFC_2,Led.LED_ON);
		Led.setLight(Led.LED_NFC_3,Led.LED_ON);
		Led.setLight(Led.LED_NFC_4,Led.LED_ON);
		try{
			Thread.sleep(2000);
		}catch (Exception e){

		}
		Led.setLight(Led.LED_NFC_1,Led.LED_OFF);
		Led.setLight(Led.LED_NFC_2,Led.LED_OFF);
		Led.setLight(Led.LED_NFC_3,Led.LED_OFF);
		Led.setLight(Led.LED_NFC_4,Led.LED_OFF);

		return ret;
	}

	public void FnBeep() {
		try {
			Beeper.getInstance().beep(1000, 400);
		} catch (SDKException e) {
			Logger.error("Exception" + e.toString());
		}
	}

	private String setCardType(int type){
		String string;
		switch (type){
			case PiccReader.MIFARE_DESFIRE:
				string ="A";
				break;
			case PiccReader.MIFARE_ONE_S70:
				string = "A";
				break;
			case PiccReader.MIFARE_ONE_S50:
				string = "A";
				break;
			case PiccReader.MIFARE_PRO:
				string = "A";
				break;
			case PiccReader.MIFARE_PRO_S50:
				string = "A";
				break;
			case PiccReader.MIFARE_PRO_S70:
				string = "A";
				break;
			case PiccReader.TYPEB:
				string = "B";
				break;
			case PiccReader.TYPEB_TCL:
				string = "B";
				break;
			case PiccReader.MIFARE_ULTRALIGHT:
				string = "MIFARE_ULTRALIGHT(unsuported)";
				break;
			case PiccReader.UNKNOWN_TYPEA:
				string = "UNKNOWN_TYPEA";
				break;
			default:
				string ="unkonw card";
				break;
		}
		return string;
	}

	private boolean isMag(String[] tracks, int tipo){
		boolean ret = false;
		String data2 = null;
		String data3 = null;
		int msgLen = 0;

		if (tracks[1].length() >= 13 && tracks[1].length() <= 37) {
			data2 = new String(tracks[1]);
			if(!data2.contains("=")){
				retVal = Tcode.T_search_card_err ;
			}else {
				String judge = data2.substring(0, data2.indexOf('='));
				if(judge.length() < 13 || judge.length() > 19){
					retVal = Tcode.T_search_card_err ;
				}else {
					if (data2.indexOf('=') != -1) {
						msgLen++;
					}
				}
			}
		}
		if (tracks[2].length() >= 15 && tracks[2].length() <= 107) {
			data3 = new String(tracks[2]);
		}
		if(retVal!=0){
			transUI.showError(timeout , retVal);
		}else {
			if (msgLen == 0) {
				transUI.showError(timeout , Tcode.T_search_card_err);
			}else {
				if (cfg.isCheckICC()) {

					int splitIndex = data2.indexOf("=");
					if (data2.length() - splitIndex >= 5) {
						char iccChar = data2.charAt(splitIndex + 5);
						if (iccChar == '2' || iccChar == '6') {
							transUI.showError(timeout , Tcode.T_ic_not_allow_swipe);
						}else {
							return afterMAGJudge(data2 , data3, tipo);
						}
					} else {
						transUI.showError(timeout , Tcode.T_search_card_err);
					}
				}else {
					return afterMAGJudge(data2 , data3, tipo);
				}
			}
		}
		return ret;
	}

	private boolean afterMAGJudge(String data2 , String data3, int tipo){
		String cardNo = data2.substring(0, data2.indexOf('='));
		//retVal = last4Card(cardNo);
		retVal = 0;
		if(retVal == 0){
			Pan = cardNo;
			Track2 = data2;
			Track3 = data3;
			PinInfo info = transUI.getPinpadOnlinePin(timeout , String.valueOf(Amount), cardNo, "Ingrese PIN:");
			if(info.isResultFlag()){
				if(info.isNoPin()){
					isPinExist = false;
					return false;
				}else {
					if(null == info.getPinblock()){
						isPinExist = false;
						return false;
					}else {
						isPinExist = true;
					}
					PIN = ISOUtil.hexString(info.getPinblock());
				}
				if (tipo == TARJETA_CAMBIO_CLAVE) {
					 return cambiarPinnMag(cardNo);
				} else {
					return true;
				}
				//Se acabo... hace el online
			}else {
				return false;
			}
		}else if(retVal == 2) {
			transUI.showError(timeout , 2076);
			return false;
		}else {
			transUI.showError(timeout, Tcode.T_user_cancel_operation);
			return false;
		}
	}

	private boolean cambiarPinnMag(String cardNo) {
		String pinBlockNew;
		String pinBlockConfir;
		boolean ret = false;
		if(para.isNeedPass()){
			PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNo, "Digita tu nuevo PIN:");
			if(info.isResultFlag()){
				if(info.isNoPin()){
					PIN = "CANCEL";
					ret = false;
				}else {
					pinBlockNew = ISOUtil.hexString(info.getPinblock()) ;
					if (!pinBlockNew.equals(PIN)) {
						info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount) , cardNo, "Confirma tu nuevo PIN:");
						if(info.isResultFlag()){
							if(info.isNoPin()){
								PIN = "CANCEL";
								ret = false;
							}else {
								pinBlockConfir = ISOUtil.hexString(info.getPinblock()) ;
								if (pinBlockNew.equals(pinBlockConfir)) {
									SecurityInfo = pinBlockConfir;
									ret = true;
								} else {
									ret = false;
									transUI.showMsgError(timeout, "Las claves no coinciden");
								}
							}
						} else {
							PIN = "CANCEL" ;
							return false;
						}
					} else {
						ret = false;
						transUI.showMsgError(timeout, "La nueva clave no puede ser igual a la anterior");
					}
				}
			} else {
				PIN = "CANCEL" ;
				return false;
			}
		}
		return ret;
	}


	protected int enviarConfirmacion( ) {
		int ret;

		if (connect() == -1) {
			return Tcode.T_socket_err;
		}
		ret = send(false);
		if (ret != 0) {
			netWork.close();
			return ret;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		netWork.close();
		return retVal;
	}


	public static String versionApk(Context context) {
		String version=null;
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			return version;
		}
	}


	public static String idApk(Context context) {
		String version=null;
		String res="";
		int pr=0;
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			pr=pInfo.versionCode;
			version =Integer.toString(pr);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			return version;
		}
	}



	public static  String getMD5(String data) {
		String result = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes(Charset.forName("UTF-8")));
			result = String.format(Locale.ROOT, "%032x", new BigInteger(1, md.digest()));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}


	public static String getAppTimeStamp(Context context) {
		String timeStamp = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
			String appFile = appInfo.sourceDir;
			long time = new File(appFile).lastModified();

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			timeStamp = formatter.format(time);

		} catch (Exception e) {

		}
		return timeStamp;

	}


	public static String asciiToHex(String asciiStr) {
		char[] chars = asciiStr.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char ch : chars) {
			hex.append(Integer.toHexString((int) ch));
		}

		return hex.toString();
	}




	protected String packField63() {
		Field63="";
		Field63="01001401";
		Field63=Field63+idApk(context)+"E";
		Field63=Field63+versionApk(context).replace(".","").substring(0,3) + "0";
		Field63=Field63+getMD5(getAppTimeStamp(context)).substring(0,4);
		Field63=Field63+asciiToHex("MIVECINO");
		return Field63;
	}



	public byte[] packTkn43() {

		byte[] auxByte = new byte[40];
		Arrays.fill(auxByte, (byte) 0x20);
		byte[] bytes = new byte[20];
		int len = PAYUtils.get_tlv_data_kernal(0x57, bytes);
		String track2_str = ISOUtil.bcd2str(bytes, 0, len);
		String str_trak2_final = track2_str.replace("F", " ").trim();
		try {
			bytes = str_trak2_final.getBytes("US-ASCII");
			System.arraycopy(bytes, 0, auxByte, 0, bytes.length);
			auxByte = PayApplication.encryp3DES(auxByte, 40);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String x = ISOUtil.hexString(auxByte);
		String y = ISOUtil.convertStringToHex(x);
		auxByte = ISOUtil.str2bcd(y, false);

		byte[] auxBuff = new byte[]{0x43, 0x00, (byte) 0x80};
		byte[] buff_final = new byte[auxByte.length + auxBuff.length];

		System.arraycopy(auxBuff, 0, buff_final, 0, auxBuff.length);
		int indice = auxBuff.length;

		System.arraycopy(auxByte, 0, buff_final, indice, auxByte.length);

		return buff_final;
	}



	private int multipleCuenta(EmvTransaction emvTrans){
		int ret = -1;
		if (InitTrans.tkn98.getLasCuentas().length > 0) {
			InputInfo inputInfo = transUI.showPrincipales(timeout, "MULTIPLECUENTA");
			if(inputInfo.isResultFlag()){
				Field48 = Field48 + InitTrans.tkn98.packTkn98();
				if (!ProcCode.equals("450204")){
					RspCode = null;
					transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.conectandoConBanco);
					ret = OnlineTrans(emvTrans);
				} else {
					InitTrans.tkn47.setCuenta(ISOUtil.str2bcd(ISOUtil.padright(inputInfo.getResult(),
							14, 'F'),false));
					ret = 0;
				}
			}
		} else {
			ret = 0;
		}
		return ret;
	}


	public int newPrepareOnline(int inputMode) {
		int ret = -1;
		this.inputMode = inputMode;

		iso8583.setRspField(48,"");
		InitTrans.tkn07.setMensaje(null);
		ret = (inputMode == ENTRY_MODE_ICC) ? OnlineTrans(emv) : OnlineTrans(null);
		InitTrans.wrlg.wrDataTxt("RET OnlineTrans - "+ TransEName +" - " + ret);
		if (ret != Tcode.T_socket_err) {
			if (ret != Tcode.T_package_mac_err){
				validateField39();
			}
		}
		if (iso8583.getfield(60) != null){
			InitTrans.isMensaje60 = true;
			InitTrans.mensaje60 = iso8583.getfield(60);
		}else{
			InitTrans.isMensaje60 = false;
			InitTrans.mensaje60 = null;
		}
		/*if (realizarReverso() == 0){

		}*/
		if (ret == Tcode.T_socket_err) {
			transUI.showMsgError(timeout, "Error al conectar. \n Verifique conexión de red o configuración del APN.");
		}else if (ret == Tcode.T_send_err){
			if (isReversal) {
				try {
					transUI.newHandling(context.getResources().getString(R.string.procesando), timeout, Tcode.Mensajes.errorEnvio_generandoReverso);
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (realizarReverso() == Tcode.transReversada) {
					transUI.showSuccess(timeout, Tcode.Mensajes.reversoExitoso, "");
				} else {
					return Tcode.T_send_err;
				}
			}else{
				transUI.showMsgError(timeout, "");
			}
			//transUI.showMsgError(timeout, "");
		}else if (ret == Tcode.T_receive_err){
			if (isReversal) {
				try {
					transUI.newHandling(context.getResources().getString(R.string.procesando), timeout, Tcode.Mensajes.errorRecibir_generandoReverso);
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (realizarReverso() == Tcode.transReversada) {
					transUI.showSuccess(timeout, Tcode.Mensajes.reversoExitoso, "");
				} else {
					return Tcode.T_receive_err;
				}
			}else {
				transUI.showMsgError(timeout, "No se obtuvo respuesta del servidor");
			}
		} else if (ret == Tcode.realizarReverso){
			if (isReversal) {
				if (realizarReverso() == Tcode.transReversada) {
					transUI.showSuccess(timeout, Tcode.Mensajes.reversoExitoso, "");
				} else {
					return Tcode.T_receive_err;
				}
			}else {
				transUI.showMsgError(timeout, "");
			}
		} else if (ret == 1995){
			transUI.showMsgError(timeout, "Error en impresión");
		}
		return ret;
	}

	private String msgIDultimaTrx = null;
	private void almacenarUlttrx(boolean rspHost, boolean showWindown){
		try{
			imprimirUltimaTRX = true;
			int trace = Integer.parseInt(TraceNo);
			int AmountInt =0;

			String trans = (TransEName == null) ? "" : TransEName;
			if (trans.equals("RECAU")){
				if (InitTrans.tkn06.getNomEmpresa() != null){
					trans = ISOUtil.hex2AsciiStr(ISOUtil.bcd2str(InitTrans.tkn06.getNomEmpresa(), 0, InitTrans.tkn06.getNomEmpresa().length));
				}
			}

			if(msgIDultimaTrx != null && msgIDultimaTrx.equals("0200")){
				if (Amount > 0) {
					AmountInt = Integer.parseInt(String.valueOf(Amount));
				}
				InitTrans.ultimoReciboSecuencial = "BANCO PICHINCHA C.A.  \n" +
						"NOTIFICACION  \n" +
						"ID POS..........: " + DevConfig.getSN() +"\n" +
						"Transacción.....: " + trans +"\n" +
						"Fecha...........: " + Utils.getDateYearMonthDay() + " " + Utils.getTime() + "\n" +
						"Valor...........: " + ISOUtil.formatMiles(AmountInt)  + "\n" +
						"Num documento...: " + trace + "\n";
				if (rspHost){
					if (iso8583.getfield(48) != null && InitTrans.tkn07.obtenerMensaje().length() > 0) {
						InitTrans.ultimoReciboSecuencial += "\nMensaje de transacción \n" + InitTrans.tkn07.obtenerMensaje();
					}
				} else {
					if (iso8583.getfield(48) != null && InitTrans.tkn07.obtenerMensaje().length() > 0) {
						InitTrans.ultimoReciboSecuencial += "\nMensaje de transacción \n" + InitTrans.tkn07.obtenerMensaje();
					} else {
						InitTrans.ultimoReciboSecuencial += "\nMensaje de transacción \n" + "Mensaje de error desconocido";
					}
				}

				SaveData.getInstance().setUltimaTrx(InitTrans.ultimoReciboSecuencial);

				if(showWindown){
					transUI.almacenarRecibo(true);
				}
			}
		}
		catch (Exception e)
		{ }
	}

	private int realizarReverso() {
		int ret = -1;
		if(isReversal) {
			int rev = verificaReverso();
			if (rev != Tcode.T_success) {
				return rev;
			} else {
				return Tcode.transReversada;
			}
		}else {
			return Tcode.T_receive_err;
		}
	}

	/**
	 * Valida el campo 39 y muestra o no el mensaje correpondiente segun el codigo obtenido
	 */
	public void validateField39() {
		RspCode=iso8583.getfield(39);
		if (RspCode != null) {
			if (!isField39Valid(RspCode)) {
				almacenarUlttrx(false, true);
				if (RspCode.equals("  ")){
					transUI.showMsgError(timeout, "Mensaje de respuesta nulo!");
				} else {
					if (iso8583.getfield(48) != null && InitTrans.tkn07.obtenerMensaje().length() > 0) {
						transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());

					} else {
						transUI.showMsgError(timeout, "Mensaje de error desconocido!");
					}
				}
				iso8583.setRspField(48, "");
				InitTrans.tkn07.setMensaje(null);
			} else {
				almacenarUlttrx(true, true);
			}
		} else {
			almacenarUlttrx(true, true);
		}
	}

	/**
	 * Analiza ProdCode y valida para dicho codigo de proceso si es valido respuesta 97
	 * @return
	 * 		false : Codigo de proceso no aplica respuesta 97
	 * 		true : valor por defecto
	 */
	boolean validar_rta_97(){
		boolean rtn = true;

		if (ProcCode != null){
			switch (ProcCode) {
				// Transacciones que no aplican codigo 97
				case "410001":

				case "410100":
				case "410101":

				case "410200":
				case "410201":

				case "470101":

				case "311000":

					rtn = false;
					break;
			}
		}

		return rtn;
	}

	/**
	 * Permite comparar con la lista de valores de respuesta permitidos en el campo 39
	 * @param field39
	 * @return
	 */
	private boolean isField39Valid(String field39){
		boolean rta = false;
		List<String> codPermitido = new ArrayList<String>();
		codPermitido.add("00");		// Respuesta Exitosa
		codPermitido.add("98");		// Respuesta Telecarga

		rta = validar_rta_97();
		if(rta){
			codPermitido.add("97");		// Multiplecuenta general
		}

		codPermitido.add("66");		// Registro OTP / Giros nacionales
		codPermitido.add("94");		// Cierre descuadrado
		codPermitido.add("S");		// Recaudacion Simple
		codPermitido.add("N");		// Recaudaciones monto variable
		if (ProcCode != null){
			switch (ProcCode) {
				case "450202":
				case "450206":
				case "450208":
				case "360100":
				case "370100":
				case "311000":
					codPermitido.add("77");        // Registro de cliente /Giros nacionales
					break;
				case "420700":
				case "420601":
				case "420800":
					codPermitido.add("99");        // Multiplecuenta credito  //PagoCredito - ConsultaValorCredito - DesembolsoCredito
					break;
				case "360000":
					codPermitido.add("95");        // Ingreso nombre / Apertura cuenta básica
					break;
				case "360011":
				case "370011":
					codPermitido.add("91");
					break;
				case "360010":
				case "370010":
					codPermitido.add("03");
					 break;
			}
		}
		for(String cod : codPermitido){
			if(cod.equals(field39)){
				return true;
			}
		}
		return false;
	}

	private void setImageSignal(int dBm){
		if(dBm <= -120){
			InitTrans.wrlg.wrDataTxt("Nivel de señal null " + dBm);
		}else if(dBm >= -119 && dBm <= -104){
			InitTrans.wrlg.wrDataTxt("Nivel de señal baja " + dBm);
		}else if(dBm >= -103 && dBm <= -98){
			InitTrans.wrlg.wrDataTxt("Nivel de señal baja " + dBm);
		}else if(dBm >= -97 && dBm <= -90){
			InitTrans.wrlg.wrDataTxt("Nivel de señal media " + dBm);
		}else if(dBm >= -89 && dBm <= -77){
			InitTrans.wrlg.wrDataTxt("Nivel de señal media " + dBm);
		}else if(dBm >= -76 && dBm <= -60){
			InitTrans.wrlg.wrDataTxt("Nivel de señal alta " + dBm);
		}else if(dBm == 0){
			InitTrans.wrlg.wrDataTxt("Nivel de señal nula " + dBm);
		}else{
			InitTrans.wrlg.wrDataTxt("Nivel de señal nula " + dBm);
		}
	}
}
