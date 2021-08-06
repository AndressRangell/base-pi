package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.pichincha.Tools.BatteryStatus;

public class VerificaBateria extends ToolsAppCompact {
    TextView details ;
    ImageView face ;
    private Timer timer = new Timer();
    public static BatteryStatus batteryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);

        theToolbar();

        details =  findViewById(R.id.result_details);
        details.setTypeface(tipoFuente1(this));
        face =  findViewById(R.id.result_img);

        details.setText("Batería baja" + "\n" + "Conecte cargador");

        batteryStatus = new BatteryStatus();
        this.registerReceiver(batteryStatus, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verificaBateria();
            }
        } , 3*1000);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){}

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    public void onBackPressed() {}

    private void over(){
        finish();
    }

    private void verificaBateria(){
        boolean bateria = false;
        do {
            if ((batteryStatus.getLevelBattery() <= 5) && (!batteryStatus.isCharging())) {
                System.out.println("Batería baja.............." + batteryStatus.getLevelBattery());
            } else {
                bateria = true;
            }
        }
        while (!bateria);
        over();
    }

}
