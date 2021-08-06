package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo.MainMenu10_4_3_2_Re_EstablecerClave;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;

import cn.desert.newpos.payui.UIUtils;
import newpos.libpay.utils.PAYUtils;

public class Login extends ToolsAppCompact {

    private EditText et_user, et_pw;
    private Button btnLogin;
    TextView tvMsg1, tvMsg2, tvMsg3,tvMsg4,txtErrorUser, txtErrorPw;
    String user, pass;
    ImageView punto1, punto2, punto3, punto4, punto5, punto6;
    private ClsConexion conexion = new ClsConexion(Login.this);
    private String fechaCierre;
    CountDownTimer countDownTimer;
    public static Usuario persona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        theToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde el onCreate, Login");
        temporizador();
        btnLogin = findViewById(R.id.btLogin);
        btnLogin.setTypeface(tipoFuente3);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresarMenuPpal();
            }
        });
        et_user = findViewById(R.id.editText_usuario);
        et_pw = findViewById(R.id.editText_Clave);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg3 = findViewById(R.id.tvMsg3);
        txtErrorUser = findViewById(R.id.txtErrorUsuario);
        txtErrorPw = findViewById(R.id.txtErrorClave);

        punto1 = findViewById(R.id.punto1);
        punto2 = findViewById(R.id.punto2);
        punto3 = findViewById(R.id.punto3);
        punto4 = findViewById(R.id.punto4);
        punto5 = findViewById(R.id.punto5);
        punto6 = findViewById(R.id.punto6);


        tvMsg4 = findViewById(R.id.tvMsg4);
        tvMsg4.setTypeface(tipoFuente3);
        tvMsg4.setText("V "+UIUtils.versionApk(this));


        tvMsg1.setTypeface(tipoFuente1);
        if(!InitTrans.initialization && !InitTrans.initEMV) {
            tvMsg1.setText("Mi vecino, modo fuera de línea");
        }

        tvMsg2.setTypeface(tipoFuente3);
        tvMsg3.setTypeface(tipoFuente3);

        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_pw.setBackgroundResource(R.drawable.edittext_pichi_normal);
                et_user.setBackgroundResource(R.drawable.edittext_pichi_normal);
                txtErrorUser.setText("");
                txtErrorPw.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_user.length() == 10){
                    user = et_user.getText().toString();
                    persona = conexion.readUser(new Usuario(user));
                    if(persona != null){
                        if(persona.getRole().equals("USUARIO") && persona.getEstado().equals("RESETEADO")){
                            InitTrans.wrlg.wrDataTxt("Fin timer Login, ingreso a pantalla de reestablecer clave");
                            countDownTimer.cancel();
                            UIUtils.startView(Login.this, MainMenu10_4_3_2_Re_EstablecerClave.class);
                        }else{
                            et_pw.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                            et_pw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                            et_pw.requestFocus();
                        }
                    }else{
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Login, usuario no existe");
                        reiniciarTimer();
                        et_user.setText("");
                        et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
                        txtErrorUser.setText("Usuario no existe");
                    }
                }else {
                    et_pw.setText("");
                    et_pw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
                }

            }
        });
        et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int tamanio=s.length();
                switch (tamanio) {
                    case 0:
                        et_pw.setCursorVisible(true);
                        punto1.setVisibility(View.INVISIBLE);
                        punto2.setVisibility(View.INVISIBLE);
                        punto3.setVisibility(View.INVISIBLE);
                        punto4.setVisibility(View.INVISIBLE);
                        punto5.setVisibility(View.INVISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        et_pw.setCursorVisible(false);
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.INVISIBLE);
                        punto3.setVisibility(View.INVISIBLE);
                        punto4.setVisibility(View.INVISIBLE);
                        punto5.setVisibility(View.INVISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.VISIBLE);
                        punto3.setVisibility(View.INVISIBLE);
                        punto4.setVisibility(View.INVISIBLE);
                        punto5.setVisibility(View.INVISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.VISIBLE);
                        punto3.setVisibility(View.VISIBLE);
                        punto4.setVisibility(View.INVISIBLE);
                        punto5.setVisibility(View.INVISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.VISIBLE);
                        punto3.setVisibility(View.VISIBLE);
                        punto4.setVisibility(View.VISIBLE);
                        punto5.setVisibility(View.INVISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 5:
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.VISIBLE);
                        punto3.setVisibility(View.VISIBLE);
                        punto4.setVisibility(View.VISIBLE);
                        punto5.setVisibility(View.VISIBLE);
                        punto6.setVisibility(View.INVISIBLE);
                        break;
                    case 6:
                        punto1.setVisibility(View.VISIBLE);
                        punto2.setVisibility(View.VISIBLE);
                        punto3.setVisibility(View.VISIBLE);
                        punto4.setVisibility(View.VISIBLE);
                        punto5.setVisibility(View.VISIBLE);
                        punto6.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }


    /**
     *
     * @return
     */
    public boolean ingresarMenuPpal(){
        user = et_user.getText().toString();
        pass = et_pw.getText().toString();
        persona = conexion.readUser(new Usuario(user));
        boolean rta = false;
        fechaCierre = PAYUtils.getLocalDateCierre();
        if (persona != null) {
            if(tipoUsuario()){
                rta=true;
            }
        }else {
            InitTrans.wrlg.wrDataTxt("Reinicio timer Login, Usuario no válido");
            reiniciarTimer();
            et_user.setText("");
            et_pw.setText("");
            et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
            txtErrorUser.setText("Usuario no válido. ");
        }
        return rta;
    }

    /**
     *
     * @return
     */
    private boolean tipoUsuario() {
        boolean rta = false;
            if (validaCredenciales()) {
                if (persona.getEstado().equals("HABILITADO")) {
                    parametrosIniciales();
                    TransLogData revesalData = TransLog.getReversal();
                    if (revesalData == null) {
                        InitTrans.isReversalTrans = false;
                    }
                    if (InitTrans.initialization && InitTrans.initEMV && !InitTrans.isReversalTrans) {
                        InitTrans.wrlg.wrDataTxt("Fin timer Login, ingreso al menú principal");
                        countDownTimer.cancel();
                        UIUtils.startView(Login.this, MainMenuPrincipal.class);
                        //conexion.updateFields(user,"user_fechacierre",fechaCierre,"user");
                        conexion.updateFields(user,"user_estadocierre","Abierto","user");
                    } else {
                        InitTrans.wrlg.wrDataTxt("Fin timer Login, ingreso al menú administrativo, no inicializado");
                        countDownTimer.cancel();
                        UIUtils.startView(Login.this, MainMenu10_Administrativo.class);
                    }
                }
                if (persona.getRole().equals("USUARIO")) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Login, ingreso a pantalla de reestablecer clave, usuario resetado por el administrador");
                    countDownTimer.cancel();
                    if (persona.getPassword().equals("") || persona.getEstado().equals("RESETEADO")) {
                        Toast.makeText(getApplicationContext(), "Usuario re-establecido", Toast.LENGTH_SHORT).show();
                        UIUtils.startView(Login.this, MainMenu10_4_3_2_Re_EstablecerClave.class);
                }
            }
        } return rta;
    }

    /**
     *
     * @return
     */
    private boolean validaCredenciales() {
        int intento=persona.getIntento();
        String valor="";
        boolean rta = false;
        StringBuffer cad = new StringBuffer();
        if (persona.getUser().equals(user) && persona.getPassword().equals(pass) && !persona.getEstado().equals("IN-HABILITADO")) {
            intento = 0;
            valor=Integer.toString(intento);
            conexion.updateFields(user,"user_intento",valor,"user");
            rta=true;
        }else {
                if (intento >= 3) {
                    if(persona.getRole().equals("USUARIO")) {
                        conexion.updateFields(user,"user_estado","IN-HABILITADO","user");
                        cad.append("Usuario deshabilitado. \n");
                        changeToBackgroundError(et_pw,txtErrorPw,"Comuníquese con el administrador para re-establecer usuario",R.drawable.edittext_pichi_normal);
                    }if(persona.getRole().equals("ADMINISTRADOR")){
                        changeToBackgroundError(et_pw,txtErrorPw,"Atención!: recientemente se ha intentado acceder fallidamente como administrador.",R.drawable.edittext_pichi_normal);
                    }
                }
                 else {
                     reiniciarTimer();
                    intento++;
                    InitTrans.wrlg.wrDataTxt("Reinicio timer Login, contraseña incorrecta, inteto" + intento);
                    valor=Integer.toString(intento);
                    conexion.updateFields(user,"user_intento",valor,"user");
                    et_pw.setText("");
                    cad.append("Contraseña incorrecta intento número : ");
                    cad.append(intento);
                    changeToBackgroundError(et_pw,txtErrorPw,"Contraseña incorrecta intento número : " + intento,R.drawable.edittext_pichi_normal);
                }
            }
        return rta;
    }

    private void changeToBackgroundError(EditText edt, TextView txt , String msgError , final int direccion) {

        final EditText editText =  edt;
        final TextView textView =  txt;

        textView.setText(msgError);
        editText.setBackgroundResource(R.drawable.edittext_pichi_error);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText("");
                editText.setBackgroundResource(direccion);
            }
        });
    }

    /**
     *
     */
    private void parametrosIniciales() {
        String id = ISOUtil.padright(user,15,' ');
        TMConfig.getInstance().setMerchID(id).save();
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Login, se presiona botón back");
        countDownTimer.cancel();
        UIUtils.startView(this, StartActivity.class);
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Login");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Login, desde método temporizador, ingresa a start activity.");
                countDownTimer.cancel();
                UIUtils.startView(Login.this, StartActivity.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Login, desde el onPause");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Login, desde el onResume");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
