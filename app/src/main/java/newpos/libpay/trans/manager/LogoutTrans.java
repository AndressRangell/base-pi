package newpos.libpay.trans.manager;

import android.content.Context;

import newpos.libpay.Logger;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.utils.ISOUtil;

/**
 * 签出实体类
 * @author zhouqiang
 */

@Deprecated
public class LogoutTrans extends Trans implements TransPresenter{

	public LogoutTrans(Context ctx , String transEN , TransInputPara p) {
		super(ctx, transEN);
		para = p ;
		setTraceNoInc(false);
		TransEName = transEN ;
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

	}

	/**
	 * 签退
	 * @throws
	 **/
	public int Logout() {
		setFixedDatas();
		iso8583.clearData();
		iso8583.setField(0, MsgID);
		iso8583.setField(11, cfg.getTraceNo());
		iso8583.setField(41, cfg.getTermID());
		iso8583.setField(42, cfg.getMerchID());
		iso8583.setField(60, Field60);
		iso8583.setField(63, ISOUtil.padleft(cfg.getOprNo()+"",2,'0') + " ");

		if (retVal != 0) {
			return retVal ;
		}
		String rspCode = iso8583.getfield(39);
		netWork.close();
		if (rspCode != null && rspCode.equals("00")) {

			return 0 ;
		} else {
			if (rspCode == null) {
				return Tcode.T_receive_err;
			} else {
				return Integer.valueOf(rspCode);
			}
		}
	}
}
