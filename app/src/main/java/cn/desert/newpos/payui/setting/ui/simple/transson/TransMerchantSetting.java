package cn.desert.newpos.payui.setting.ui.simple.transson;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import newpos.libpay.global.TMConfig;
import newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.base.BaseActivity;
import cnb.pichincha.wposs.mivecino_pichincha.R;

/**
 * Created by zhouqiang on 2017/11/15.
 * @author zhouqiang
 */
public class TransMerchantSetting extends BaseActivity {
    EditText merchant_mid ;
    EditText merchant_tid ;
    EditText merchant_name ;

    private TMConfig config ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_trans_merchant);
        config = TMConfig.getInstance() ;
        initData();
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void initData(){
        merchant_mid = (EditText) findViewById(R.id.merchant_merid);
        merchant_tid = (EditText) findViewById(R.id.merchant_tid);
        merchant_name = (EditText) findViewById(R.id.merchant_name);
        merchant_name.setText(config.getMerchName());
        merchant_tid.setText(config.getTermID());
        merchant_mid.setText(config.getMerchID());
    }

    private void save(){
        String mid = merchant_mid.getText().toString();
        String tid = merchant_tid.getText().toString();
        String name = merchant_name.getText().toString();
        if(PAYUtils.isNullWithTrim(mid)||
                PAYUtils.isNullWithTrim(tid)||
                PAYUtils.isNullWithTrim(name)){
            Toast.makeText(this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
            return;
        }
        if(mid.length() != 15 || tid.length() != 8){
            Toast.makeText(this , getString(R.string.len_err) , Toast.LENGTH_SHORT).show();
            return;
        }
        config.setMerchID(mid)
                .setMerchName(name)
                .setTermID(tid)
                .save();
        finish();
    }
}
