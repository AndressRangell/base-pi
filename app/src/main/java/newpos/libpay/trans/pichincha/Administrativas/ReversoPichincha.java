package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;

import newpos.libpay.Logger;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

public class ReversoPichincha extends FinanceTrans implements TransPresenter {

    public ReversoPichincha(Context ctx, String transEname , TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isSaveLog = true;
        isProcPreTrans=true;
    }

    @Override
    public void start() {
        setFixedDatas();
        transUI.trannSuccess(timeout , verificaReverso());
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }
}
