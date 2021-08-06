package newpos.libpay.trans.finace.transfer;

import android.content.Context;

import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;

/**
 * Created by zhouqiang on 2017/4/25.
 * 转账处理交易类
 * @author zhouqiang
 */

@Deprecated
public class TransferTrans extends FinanceTrans implements TransPresenter {

    public TransferTrans(Context ctx , String transEn , TransInputPara p){
        super(ctx , transEn);
        para = p ;
        transUI = para.getTransUI() ;
        isReversal = true;
        isSaveLog = true;
        isDebit = true;
        isProcPreTrans = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

    }
}
