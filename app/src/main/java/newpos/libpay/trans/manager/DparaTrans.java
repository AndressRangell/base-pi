package newpos.libpay.trans.manager;

import android.content.Context;
import android.util.Log;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MensajeCampo60;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.global.TMConfig;
import newpos.libpay.global.TMConstants;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.paras.EmvAidInfo;
import newpos.libpay.paras.EmvCapkInfo;
import newpos.libpay.paras.TerminalAid;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.parsinginitemvtables.TableEMVConf;
import newpos.libpay.trans.pichincha.parsinginitemvtables.TableEMVKey;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import com.pos.device.SDKException;
import com.pos.device.emv.CAPublicKey;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DparaTrans extends Trans implements TransPresenter{

	public DparaTrans(Context ctx , String transEName  , TransInputPara p) {
		super(ctx, transEName);
		para = p ;
		setTraceNoInc(false);
		TransEName = transEName ;
		if(para != null){
			transUI = para.getTransUI();
		}
	}

	@Override
	public ISO8583 getISO8583() {
		return iso8583;
	}

	@Override
	public void start() {
		timeout = 60 * 1000 ;
		transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.downloadingEmvParas);
		if(cfg.isOnline()){
			if(retVal!=0){
				transUI.showError(timeout , retVal);
			}else {
				transUI.newHandling(context.getResources().getString(R.string.procesando),timeout ,Tcode.Mensajes.downloadingEmvCapks);
				if(retVal!=0){
					transUI.showError(timeout , retVal);
				}else {
					loadAIDCAPK2EMVKernel();
					transUI.trannSuccess(timeout , Tcode.Status.downing_succ);
				}
			}
		}else {
			PAYUtils.copyAssetsToData(context , TMConstants.LOCALAID);
			PAYUtils.copyAssetsToData(context , TMConstants.LOCALCAPK);
			loadAIDCAPK2EMVKernel();
			transUI.trannSuccess(timeout , Tcode.Status.downing_succ);
		}
	}

	private List<byte[]> capkQueryList;
	private List<byte[]> aidQueryList;
	private EmvAidInfo emvAidInfo;
	private EmvCapkInfo emvCapkInfo;

	/***
	 * MSGID serFixDate获取 bitmap 组包自动生成41域 从config获取 42域 从config获取 60域 60_1
	 * 交易类型码，和60_3网络管理信息码从setfixdate获取 60_2批次号从config获取 62域
	 */
	private void setFields() {
		iso8583.clearData();
		if (MsgID != null){
			iso8583.setField(0, MsgID);
		}
		iso8583.setField(41, TermID); // 41
		if (MerchID != null){
			iso8583.setField(42, MerchID);// 42
		}
		if (Field60 != null){
			iso8583.setField(60, Field60);// 60
		}
		if (Field62 != null){
			iso8583.setField(62, Field62);// 62
		}
	}

	/**
	 * 将下载的CAPK和AID写入到内核中
	 */
	public static void loadAIDCAPK2EMVKernel() {
		IEMVHandler emvHandler = EMVHandler.getInstance();
		String aidFilePath = TMConfig.getRootFilePath() + EmvAidInfo.FILENAME;
		File aidFile = new File(aidFilePath);
		if (!aidFile.exists()) {
		}

		try {
			EmvAidInfo aidInfo = (EmvAidInfo) PAYUtils.file2Object(aidFilePath);
			if (aidInfo != null) {
				for (TerminalAid item : aidInfo.getAidInfoList()) {
					int result = emvHandler.addAidInfo(item.getTerminalAidInfo());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String capkFilePath = TMConfig.getRootFilePath()+ EmvCapkInfo.FILENAME;
		File capkFile = new File(capkFilePath);
		if (!capkFile.exists()) {

		}

		try {
			EmvCapkInfo capk = (EmvCapkInfo) PAYUtils.file2Object(capkFilePath);
			if (capk != null) {
				for (CAPublicKey item : capk.getCapkList()) {
					int result = emvHandler.addCAPublicKey(item);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将下载的CAPK和AID写入到内核中
	 */
	public static void loadAIDCAPK2EMVKernelPichincha() {

		TerminalAidInfo aidInfo ;
		CAPublicKey capk;
		TableEMVConf aidSpectra;
		TableEMVKey capkSpectra;
		IEMVHandler emvHandler = EMVHandler.getInstance();
		String auxtag;
        byte[] result ;
        int auxValue;
		int iret;

		for (int i = 0; i< InitTrans.arrayListBuffer_TableEMVConf.size(); i++){

			aidInfo = new TerminalAidInfo();
			aidSpectra = InitTrans.arrayListBuffer_TableEMVConf.get(i);
			aidInfo.setAId(aidSpectra.getAid());
			auxtag = ISOUtil.bcd2str(aidSpectra.getLenAid(), 0, aidSpectra.getLenAid().length);


			aidInfo.setAIDdLength( Integer.parseInt(auxtag) );
			aidInfo.setSupportPartialAIDSelect(true);
			aidInfo.setApplicationPriority(0x00);
			
            auxValue = ISOUtil.byte2int(aidSpectra.getRandoSelect(), 0, aidSpectra.getRandoSelect().length);
            aidInfo.setTargetPercentage( auxValue );

            auxValue = ISOUtil.byte2int(aidSpectra.getMaxRandSelect(), 0, aidSpectra.getMaxRandSelect().length);
            aidInfo.setMaximumTargetPercentage( auxValue );

			auxtag = ISOUtil.bcd2str(aidSpectra.getT_9F1B(), 0, aidSpectra.getLenAid().length);
			aidInfo.setTerminalFloorLimit( Integer.parseInt(auxtag) );
			auxtag = ISOUtil.bcd2str(aidSpectra.getThreshold(), 0, aidSpectra.getLenAid().length);

			aidInfo.setThresholdValue( Integer.parseInt(auxtag) );
			aidInfo.setTerminalActionCodeDenial(aidSpectra.getTacDenial());
			aidInfo.setTerminalActionCodeOnline(aidSpectra.getTacOnline());
			aidInfo.setTerminalActionCodeDefault(aidSpectra.getTacDefault());
			aidInfo.setAcquirerIdentifier("NEWPOS".getBytes());
			auxtag = ISOUtil.bcd2str(aidSpectra.getLenDdol(), 0, aidSpectra.getLenAid().length);
			aidInfo.setLenOfDefaultDDOL( Integer.parseInt(auxtag) );
			aidInfo.setDefaultDDOL(aidSpectra.getDdol());
			auxtag = ISOUtil.bcd2str(aidSpectra.getLenTdol(), 0, aidSpectra.getLenAid().length);
			aidInfo.setLenOfDefaultTDOL( Integer.parseInt(auxtag));
			aidInfo.setDefaultTDOL(aidSpectra.getTdol());
			aidInfo.setApplicationVersion(aidSpectra.getT_9F09());
			aidInfo.setLenOfTerminalRiskManagementData(0x00);
			aidInfo.setTerminalRiskManagementData(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
			aidInfo.setClReaderMaxTransAmount(0);

			aidInfo.setClFloorLimit(10000);
			aidInfo.setClCVMAmount(5000);
			iret = emvHandler.addAidInfo(aidInfo);
			Log.e("DparaTrans", "addAidInfo=" + iret);
		}

		for (int i=0 ; i< InitTrans.arrayListBuffer_TableEMVKey.size();i++){

			capk = new CAPublicKey();
			capkSpectra = InitTrans.arrayListBuffer_TableEMVKey.get(i);
			capk.setRID(capkSpectra.getPubKeyRid());
			result = capkSpectra.getPubKeyId();
			capk.setIndex( result[0] );
			auxtag = ISOUtil.bcd2str(capkSpectra.getKeyLen(), 0, capkSpectra.getKeyLen().length);
			capk.setLenOfModulus( Integer.parseInt(auxtag) );
			capk.setModulus(capkSpectra.getPublicKey());
			capk.setLenOfExponent(capkSpectra.getPubKeyExp().length);
			capk.setExponent(capkSpectra.getPubKeyExp());
			capk.setExpirationDate(capkSpectra.getExpiryDate());
			capk.setChecksum(capkSpectra.getPubKeyHas());
			iret = emvHandler.addCAPublicKey(capk);
			Log.e("DparaTrans", "addCAPublicKey2=" + iret);
		}
    }
}