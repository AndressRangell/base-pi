package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_6_1_ModificarClave extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1, tvMsg1, tvMsg2, tvMsg3;
    Button btnAceptar, btnCancelar;
    EditText et_user, et_pw, et_pwc;
    private Usuario person101 = Login.persona;
    private String usuario, estado;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_crearusuario_bd);
        theToolbar();

        tv1 = findViewById(R.id.tv1);
        btnAceptar = findViewById(R.id.btAceptar);
        btnCancelar = findViewById(R.id.btCancel);
        et_user = findViewById(R.id.et_user);
        et_pw = findViewById(R.id.et_pw);
        et_pwc = findViewById(R.id.et_pwc);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg3 = findViewById(R.id.tvMsg3);

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        tv1.setTypeface(tipoFuente1);
        tv1.setText("Modificar clave");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Modificar clave.");
        temporizador();
        tvMsg1.setTypeface(tipoFuente3);
        tvMsg2.setTypeface(tipoFuente3);
        tvMsg3.setTypeface(tipoFuente3);
        btnAceptar.setTypeface(tipoFuente3);
        btnCancelar.setTypeface(tipoFuente3);

        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitTrans.wrlg.wrDataTxt("Fin timer Modificar clave, desde botón cancelar.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_6_1_ModificarClave.this,MainMenu10_4_UsuariosLocales.class);
            }
        });
        usuario = person101.getUser().toString();
        estado = person101.getEstado().toString();
        et_user.setText(usuario);
        et_user.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        String pass = et_pw.getText().toString();
        String passConf = et_pwc.getText().toString();

        ClsConexion conexion = new ClsConexion(this);

        if (pass.length() <4 || passConf.length() <4) {
            InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar clave, la clave debe ser de 4 dígitos.");
            reiniciarTimer();
            Toast.makeText(getApplicationContext(),"La clave debe ser de 4 dígitos.",Toast.LENGTH_SHORT).show();
            et_pw.setText("");
            et_pwc.setText("");
        } else {
            if (pass.equals(passConf)){
                conexion.updatePass(usuario,pass,estado);
                Toast.makeText(this,"Clave actualizada",Toast.LENGTH_SHORT).show();
                InitTrans.wrlg.wrDataTxt("Fin timer Modificar clave, ingresa a usuarios locales, clave actualizada.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_6_1_ModificarClave.this, MainMenu10_4_UsuariosLocales.class);
            } else {
                InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar clave, las contraseñas no coinciden.");
                reiniciarTimer();
                Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                et_pw.setText("");
                et_pwc.setText("");
            }
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Modificar clave, desde botón back, ingresa a usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_6_1_ModificarClave.this, MainMenu10_4_UsuariosLocales.class);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    /**
     *
     */
    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Modificar clave");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Modificar clave, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_6_1_ModificarClave.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Modificar clave, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reincio timer Modificar clave, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
