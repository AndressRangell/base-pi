package newpos.libpay.device.pinpad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pos.device.SDKException;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.KeyType;
import com.pos.device.ped.Ped;

import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class InjectMasterKey extends ToolsAppCompact {

    private String TAG = "InjectMasterKey";
    private ProcessMasterKey processMasterKey;
    private EditText password_input;
    private Button nextButton;
    private TextView cancel, tv1, tv2, txtError;
    public static final int MASTERKEYIDX = 0;
    TextView details ;
    ImageView face ;
    private Timer timer = new Timer();
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_insertar_tarjeta);
        temporizador();
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv1.setTypeface(tipoFuente1(this));
        tv1.setText("Ingresa masterkey");
        tv2.setTypeface(tipoFuente3(this));
        tv2.setText("");
        createSigninDialog();
    }

    public AlertDialog createSigninDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(InjectMasterKey.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_master_key, null);

        builder.setView(view);

        final AlertDialog dialog = builder.show();

        nextButton = (Button) view.findViewById(R.id.button_ingresar);
        nextButton.setTypeface(tipoFuente3(this));

        password_input = (EditText) view.findViewById(R.id.password_input);
        txtError = view.findViewById(R.id.txtErrorDialog);
        cancel = (TextView) view.findViewById(R.id.cancel);

        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(InjectMasterKey.this, R.anim.imagen_click));
                        if (!password_input.getText().toString().equals("")) {
                            reiniciarTimer();
                            dialog.dismiss();
                            processMk(password_input.getText().toString().trim());
                        } else {
                            reiniciarTimer();
                            changeToBackgroundError(password_input,txtError,"Por favor ingresa datos",R.drawable.edittext_pichi);
                        }
                    }
                }

        );

        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.cancel();
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(InjectMasterKey.this, MainMenu10_Administrativo.class);
                        startActivity(intent);
                    }
                });

        return dialog;
    }

    private void processMk(String pass) {

        processMasterKey = new ProcessMasterKey(new ProcessMasterKey.FileCallback() {
            @Override
            public String RspUnpack(String MK) {
                if (MK.length() > 1) {
                    int ret = injectMk(MK);
                    if (ret == 0) {
                        setContentView(R.layout.trans_result);
                        details =  findViewById(R.id.result_details);
                        face =  findViewById(R.id.result_img);
                        face.setImageResource(R.drawable.pichi_aceptado_new);
                        details.setTextColor(Color.parseColor("#616161"));
                        details.setText("Ingreso masterkey exitoso");
                        details.setTypeface(tipoFuente1(getApplicationContext()));
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                countDownTimer.cancel();
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (!InitTrans.initialization){
                                    intent.setClass(InjectMasterKey.this, StartActivity.class);
                                } else {
                                    intent.setClass(InjectMasterKey.this, MainMenu10_Administrativo.class);
                                }
                                startActivity(intent);
                            }
                        } , 5*1000);
                    }
                } else {
                    if (MK.equals("1")) {

                    } else if (MK.equals("3")) {

                    }
                    createSigninDialog();
                }

                return "";
            }
        }, pass);
        processMasterKey.execute();
    }

    private int injectMk(String masterKey) {

        byte[] masterKeyData = ISOUtil.str2bcd(masterKey, false);
        int masterKeyIdx = 0;
        int ret = Ped.getInstance().injectKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, masterKeyIdx, masterKeyData);//the app must be System User can inject success.
        return ret;
    }

    public void deleteMasterKey() throws SDKException {
        int indexKey=0;
        Ped.getInstance().deleteKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK,indexKey);
    }


    public static boolean threreIsKey(int indexKey, String msg, AppCompatActivity activity){
        int retTmp = Ped.getInstance().checkKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, indexKey, 0);
        if(retTmp == 0){
            return true;
        }else {
            return false;
        }
    }




    private void changeToBackgroundError(EditText edt,TextView txt ,String msgError ,final int direccion) {

        final EditText editText = (EditText) edt;
        final TextView textView = (TextView) txt;

        textView.setText(msgError);
        //editText.setBackgroundResource(R.drawable.edittext_pichi_copia);

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
                //editText.setBackgroundResource(direccion);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (processMasterKey != null)
            processMasterKey.cancel(true);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        reiniciarTimer();
    }

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        UIUtils.startView(InjectMasterKey.this, MainMenuPrincipal.class);
    }

    public void temporizador(){
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                if(!InitTrans.initialization && !InitTrans.initEMV){
                    countDownTimer.cancel();
                    UIUtils.startView(InjectMasterKey.this, StartActivity.class);
                }else{
                    countDownTimer.cancel();
                    UIUtils.startView(InjectMasterKey.this, MainMenu10_Administrativo.class);
                }
            }
        }.start();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
