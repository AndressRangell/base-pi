package cn.desert.newpos.payui.master;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;

import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.VerificaTarjeta;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

/**
 * Created by zhouqiang on 2016/11/12.
 * Cambie BaseActivity por AppCompactActivty
 */
public class ResultControl extends AppCompatActivity {
    TextView details ;
    ImageView face ;
    private Timer timer = new Timer();
    private String info = null ;
    private IccReader iccReader0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        details =  findViewById(R.id.result_details);
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        details.setTypeface(tipoFuente1);
        face =  findViewById(R.id.result_img);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verificaICCinsert();
            }
        } , 3*1000);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            displayDetails(bundle.getBoolean("flag") ,
                    bundle.getString("info"));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            over();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void verificaICCinsert(){
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
            if (iccReader0.isCardPresent()){
                //finish();
                UIUtils.startView(this, VerificaTarjeta.class);
            }
        over();
    }
    private void over(){
        try{
            MasterControl.masterCDT.cancel();
        }catch (Exception e){

        }
        finish();
    }

    private void displayDetails(boolean flag , String info){
        this.info = info ;
        InitTrans.wrlg.wrDataTxt("ResultControl - info: " + info);
        details.setText(info);
        if(flag){
            face.setImageResource(R.drawable.pichi_aceptado_new);
            details.setTextColor(Color.parseColor("#616161"));
        }else {
            face.setImageResource(R.drawable.pichi_denegado3);
            details.setTextColor(Color.parseColor("#f54d4f"));
        }
    }
}
