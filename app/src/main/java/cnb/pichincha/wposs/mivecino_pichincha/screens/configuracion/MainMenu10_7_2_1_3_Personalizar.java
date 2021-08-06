package cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.setting.view.IPEditText;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.PAYUtils;

public class MainMenu10_7_2_1_3_Personalizar extends ToolsAppCompact {

    EditText setting_public_port1, setting_public_port2;
    IPEditText setting_public_ip1, setting_public_ip2;
    TMConfig config ;
    Button btnGuardar;
    TextView tv1;
    int port1, port2;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_tcp);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Personalizar.");
        temporizador();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        config = TMConfig.getInstance();
        tv1 = findViewById(R.id.tv1);
        setting_public_ip1 = findViewById(R.id.setting_public_ip1);
        setting_public_ip2 = findViewById(R.id.setting_public_ip2);
        setting_public_port1 = findViewById(R.id.setting_public_port1);
        setting_public_port2 = findViewById(R.id.setting_public_port2);
        btnGuardar = findViewById(R.id.btnGuardar);

        tv1.setTypeface(tipoFuente1);
        tv1.setText("Personalizar");

        btnGuardar.setTypeface(tipoFuente3);

        setting_public_ip1.setIPText(config.getIp().split("\\."));
        setting_public_ip2.setIPText(config.getIP2().split("\\."));
        setting_public_port1.setText(config.getPort());
        setting_public_port2.setText(config.getPort2());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String S_ip = setting_public_ip1.getIPText() ;
                String S_port = setting_public_port1.getText().toString() ;
                String S_ip2 = setting_public_ip2.getIPText() ;
                String S_port2 = setting_public_port2.getText().toString() ;

                if(PAYUtils.isNullWithTrim(S_ip) || PAYUtils.isNullWithTrim(S_port)
                        || PAYUtils.isNullWithTrim(S_ip2) || PAYUtils.isNullWithTrim(S_port2)){
                    InitTrans.wrlg.wrDataTxt("Reinicio timer Personalizar, los campor no pueden estar vacíos.");
                    reiniciarTimer();
                    Toast.makeText(getApplicationContext() , "¡Los campos no pueden estar vacíos!."
                            ,Toast.LENGTH_SHORT).show();
                } else {
                    port1 = Integer.parseInt(S_port);
                    port2 = Integer.parseInt(S_port2);

                    if (port1 > 65535 || port2 > 65535) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Personalizar, puerto no válido.");
                        reiniciarTimer();
                        Toast.makeText(getApplicationContext(),"¡Puerto no válido!.",Toast.LENGTH_SHORT).show();
                    } else {
                        InitTrans.wrlg.wrDataTxt("Fin timer Personalizar, ingresa a administrativo.");
                        countDownTimer.cancel();
                        config.setIp(S_ip)
                                .setIp2(S_ip2)
                                .setPort(S_port)
                                .setPort2(S_port2)
                                .save();
                        Toast.makeText(getApplicationContext(),"¡Configuración éxitosa!.",Toast.LENGTH_SHORT).show();
                        UIUtils.startView(MainMenu10_7_2_1_3_Personalizar.this,MainMenu10_Administrativo.class);
                    }
                }
            }
        });
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Personalizar, desde botón back, ingresa a red.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_7_2_1_3_Personalizar.this, MainMenu10_7_2_1_Red.class);
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Personalizar.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Personalizar, desde método temporizador, ingresa a red.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_7_2_1_3_Personalizar.this, MainMenu10_7_2_1_Red.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Personalizar, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Personalizar, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
