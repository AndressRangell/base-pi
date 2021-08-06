package newpos.libpay.trans.finace.forload;

import android.content.Context;

import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.finace.FinanceTrans;

/**
 * Created by zhouqiang on 2017/4/27.
 * 圈提交易类
 * @author zhouqiang
 */

@Deprecated
public class DebitForLoad extends FinanceTrans implements TransPresenter{

    public DebitForLoad(Context ctx , String en){
        super(ctx , en);
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

    }
}
