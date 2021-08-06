package cn.desert.newpos.payui.setting.ui.simple;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import newpos.libpay.global.TMConfig;
import newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;
import cn.desert.newpos.payui.setting.view.IPEditText;
import cnb.pichincha.wposs.mivecino_pichincha.R;

/**
 * Created by zhouqiang on 2017/11/15.
 */
public class CommunSettings extends BaseActivity {
    EditText commun_timeout ;
    ImageView commun_public ;
    IPEditText commun_pub_ip ;
    EditText commun_pub_port ;
    IPEditText commun_inner_ip ;
    EditText commun_inner_port ;
    Typeface tipoFuente3;
    TextView tv1, tv2, tv3, tv4, tv5, tv6;

    private TMConfig config ;
    private boolean isOpen ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_commun);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);

        tv1.setTypeface(tipoFuente3);
        tv2.setTypeface(tipoFuente3);
        tv3.setTypeface(tipoFuente3);
        tv4.setTypeface(tipoFuente3);
        tv5.setTypeface(tipoFuente3);
        tv6.setTypeface(tipoFuente3);

        config = TMConfig.getInstance();
        initData();
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void initData(){
        commun_timeout = (EditText) findViewById(R.id.setting_com_timeout);
        commun_public = (ImageView) findViewById(R.id.setting_com_public);
        commun_pub_ip = (IPEditText) findViewById(R.id.setting_com_public_ip);
        commun_pub_port = (EditText) findViewById(R.id.setting_com_public_port);
        commun_inner_ip = (IPEditText) findViewById(R.id.setting_com_inner_ip);
        commun_inner_port = (EditText) findViewById(R.id.setting_com_inner_port);
        commun_timeout.setText(String.valueOf(config.getTimeout()/1000));
        setPubSwitch(config.getPubCommun());
        commun_pub_ip.setIPText(config.getIp().split("\\."));
        commun_pub_port.setText(config.getPort());
        commun_inner_ip.setIPText(config.getIP2().split("\\."));
        commun_inner_port.setText(config.getPort2());
        commun_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPubSwitch(!isOpen);
            }
        });
    }

    private void setPubSwitch(boolean is){
        isOpen = is ;
        if(is){
            commun_public.setImageResource(R.drawable.home2_setting_commun_open);
        }else {
            commun_public.setImageResource(R.drawable.home2_setting_commun_close);
        }
    }

    private void save(){
        String ip = commun_pub_ip.getIPText() ;
        String port = commun_pub_port.getText().toString() ;
        String ip2 = commun_inner_ip.getIPText() ;
        String port2 = commun_inner_port.getText().toString() ;
        String timeout = commun_timeout.getText().toString();
        if(PAYUtils.isNullWithTrim(ip)||
                PAYUtils.isNullWithTrim(port)||
                PAYUtils.isNullWithTrim(ip2)||
                PAYUtils.isNullWithTrim(port2)||
                PAYUtils.isNullWithTrim(timeout)){
            Toast.makeText(this , getString(R.string.data_null) ,Toast.LENGTH_SHORT).show();
            return;
        }
        config.setIp(ip)
              .setIp2(ip2)
              .setPort(port)
              .setPort2(port2)
              .setTimeout(Integer.parseInt(timeout)*1000)
              .setPubCommun(isOpen)
              .save();
        finish();
    }
}
