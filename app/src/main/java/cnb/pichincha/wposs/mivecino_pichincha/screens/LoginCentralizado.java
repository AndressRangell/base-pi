package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import static android.content.SharedPreferences.*;

/*****************************************************************************
 * Clase:      LoginCentral
 * Descripcion:  Realiza Login Centralizado .
 * **************************************************************************/
public class LoginCentralizado extends ToolsAppCompact {

    private EditText et_user;
    private Button btnLogin;
    TextView tvMsg1, tvMsg2, tvMsg3;
    private String typeTrans ;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logincentralizado);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde el onCreate, Login Centralizado.");
        temporizador();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        btnLogin = findViewById(R.id.btLogin);
        btnLogin.setTypeface(tipoFuente3);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                veriLogiCentr();
            }
        });
        et_user = findViewById(R.id.editText_usuario);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg3 = findViewById(R.id.tvMsg3);
        tvMsg1.setTypeface(tipoFuente1);
        tvMsg2.setTypeface(tipoFuente3);
    }

    /**
     * Si sesionIniciada : S  Verifica los 4 ultimos digitos de la cedula
     * Sino envia la transaccion  de LoGON
     */
    private void veriLogiCentr(){
        switch (TMConfig.getInstance().getsesionIniciada()) {
            case "S":
                String ced = TMConfig.getInstance().getCedula();
                int lenCed = ced.length();
                if (et_user.getText().toString().equals(ced.substring(lenCed - 4, lenCed))) {
                    Toast.makeText(LoginCentralizado.this, "Contraseña correcta!!", Toast.LENGTH_SHORT).show();

                    UIUtils.startView(LoginCentralizado.this, MainMenuPrincipal.class);
                } else {
                    InitTrans.wrlg.wrDataTxt("Reinicio timer Login Centralizado, contraseña incorrecta.");
                    reiniciarTimer();
                    Toast.makeText(LoginCentralizado.this, "Contraseña incorrecta!!", Toast.LENGTH_SHORT).show();
                    et_user.setText("");
                }
                break;
            case "L":
                typeTrans = PrintRes.TRANSEN[18];
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(LoginCentralizado.this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[18]);
                intent.putExtra(MasterControl.tipoTrans, et_user.getText().toString());
                startActivity(intent);
                break;
            default:
                InitTrans.wrlg.wrDataTxt("Fin timer Login centralizado, ingresa al menú principal.");
                countDownTimer.cancel();
                UIUtils.startView(LoginCentralizado.this, MainMenuPrincipal.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this , StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
        //super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
        if( TMConfig.getInstance().getsesionIniciada().equals("N")) {
            saveTkn82SharedPreferences();
            UIUtils.startView(this, MainMenuPrincipal.class);
        }
    }

    private void saveTkn82SharedPreferences() {
        String ruc,empresa,cedula,nombre,fechaDeRegistro;
        ruc =  ISOUtil.hexString(InitTrans.tkn82.getRuc()).replace("F","");
        empresa = ISOUtil.hex2AsciiStr(ISOUtil.hexString(InitTrans.tkn82.getNombreEmpresa())).trim();
        cedula = ISOUtil.hexString(InitTrans.tkn82.getCedula()).replace("F","");
        nombre = ISOUtil.hex2AsciiStr(ISOUtil.hexString(InitTrans.tkn82.getNombreUsuario())).trim();
        fechaDeRegistro = PAYUtils.getLocalDateCierre();
        //Actualizar fecha de cierre al iniciar sesion
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        TMConfig.getInstance().setFechaCierre(date);

        SharedPreferences sharedPreferences =  getSharedPreferences("userCentralizado",Context.MODE_PRIVATE);
        Editor editor=sharedPreferences.edit();
        editor.putString("ruc",ruc);
        editor.putString("empresa",empresa);
        editor.putString("cedula",cedula);
        editor.putString("nombre",nombre);
        editor.putString("fechaUltimoCierre", fechaDeRegistro);
        editor.putString("fechaRegistro", fechaDeRegistro);
        editor.apply();

    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Login centralizado.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Login centralizado, desde método temporizador, ingresa a start activity");
                countDownTimer.cancel();
                UIUtils.startView(LoginCentralizado.this, StartActivity.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Login centralizado, desde el onPause");
        countDownTimer.cancel();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

}
