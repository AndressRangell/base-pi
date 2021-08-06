package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion.MainMenu10_7_2_Comunicacion;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class MensajeCampo60 extends ToolsAppCompact {


    PrintManager manager;
    String mensaje;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_vista_msg_confirmacion);
        temporizador();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnAceptar = findViewById(R.id.btAceptar);
        Button btnCancelar = findViewById(R.id.btCancel);
        btnCancelar.setVisibility(View.INVISIBLE);
        btnAceptar.setTypeface(tipoFuente3(this));
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setText("Mensaje");
        tv1.setTypeface(tipoFuente1(this));
        TextView ms1 = findViewById(R.id.ms1);
        ms1.setTypeface(tipoFuente3(this));
        mensaje = ISOUtil.hex2AsciiStr(InitTrans.mensaje60);
        char inicial = mensaje.charAt(0);
        mensaje = mensaje.substring(1);

        switch (inicial) {
            case 'P':
                ms1.setText(mensaje);
                btnAceptar.setText("Aceptar");
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menu();
                    }
                });
                break;
            case 'T':
                ms1.setText(mensaje);
                btnAceptar.setText("Imprimir");
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imprimir();
                    }
                });
                break;
            case 'I':
                imprimir();
                break;
            default:
                ms1.setText("Mensaje no valido");
                btnAceptar.setVisibility(View.INVISIBLE);
                btnCancelar.setVisibility(View.VISIBLE);
                btnCancelar.setText("Salir");
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.isMensaje60 = false;
                        UIUtils.startView(MensajeCampo60.this, MainMenuPrincipal.class);
                    }
                });

        }
    }

    public void imprimir(){
        InitTrans.isMensaje60 = false;
        UIUtils.startView(MensajeCampo60.this, MainMenuPrincipal.class);
        manager = PrintManager.getmInstance(getApplicationContext());
        manager.printPichincha(mensaje);
    }

    public void menu(){
        InitTrans.isMensaje60 = false;
        UIUtils.startView(MensajeCampo60.this, MainMenuPrincipal.class);
    }

    @Override
    public void onBackPressed() {

    }

    public void temporizador(){
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                InitTrans.isMensaje60 = false;
                if(!InitTrans.initialization && !InitTrans.initEMV){
                    countDownTimer.cancel();
                    UIUtils.startView(MensajeCampo60.this, StartActivity.class);
                }else{
                    countDownTimer.cancel();
                    UIUtils.startView(MensajeCampo60.this, MainMenuPrincipal.class);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
