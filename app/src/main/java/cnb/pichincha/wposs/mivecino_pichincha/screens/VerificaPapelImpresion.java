package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import com.pos.device.SDKException;
import com.pos.device.beeper.Beeper;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;

import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

import static java.lang.Thread.sleep;

public class VerificaPapelImpresion extends ToolsAppCompact {

    TextView details ;
    ImageView face ;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);
        theToolbar();
        details =  findViewById(R.id.result_details);
        details.setTypeface(tipoFuente1(this));
        face =  findViewById(R.id.result_img);
        details.setText("No hay papel");
        face.setImageResource(R.drawable.pichi_impresora);
        details.setTextColor(Color.parseColor("#f54d4f"));
        showImprimiendo();
    }


    public void showImprimiendo() {
        setContentView(R.layout.vista_imprimiendo);
        showHanding("Impresora sin papel");
        TextView handing_msginfo = findViewById(R.id.handing_msginfo);
        handing_msginfo.setTypeface(tipoFuente3(this));
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1(this));
        temporizador();
    }

    private void showHanding(String msg) {
        TextView tv = (TextView) findViewById(R.id.handing_msginfo);
        tv.setText(msg);
    }

    public void temporizador(){
        countDownTimer = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                countDownTimer.cancel();
                UIUtils.startView(VerificaPapelImpresion.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    public void onBackPressed() {

    }

}
