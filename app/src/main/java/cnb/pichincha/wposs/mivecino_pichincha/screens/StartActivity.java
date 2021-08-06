package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.pos.device.config.DevConfig;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;
import cn.desert.newpos.payui.master.MasterControl;
import cn.desert.newpos.payui.master.ResultControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.tools.Wrlg;
import newpos.libpay.PaySdk;
import newpos.libpay.device.pinpad.InjectMasterKey;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.CheckSettle;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;
import newpos.libpay.utils.SaveData;

import static cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal.checkSettle;
import static newpos.libpay.device.pinpad.InjectMasterKey.MASTERKEYIDX;
import static newpos.libpay.device.pinpad.InjectMasterKey.threreIsKey;
import static newpos.libpay.trans.Trans.CIERRE_TOTAL_LOG;

public class StartActivity extends ToolsAppCompact {


    Button btn_Init, btnConf;
    TextView tvMsg, tvMsg2;
    private Timer timer = new Timer();
    public static CountDownTimer countDownTimer;
    private String typeTrans;
    public static boolean MODE_KIOSK = false;
    private String lastOperMode, newOperMode;

    private ArrayList<String> arrayUsuarios;
    private ArrayList<Usuario> listaUsuario;

    Wrlg wrlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wrlg = new Wrlg();
        wrlg.wrDataTxt("Inicia StartActivity");
        SaveData.getInstance().Initialize(StartActivity.this);
        SaveData.getInstance().setStartApp(true);
        final boolean masterkey = threreIsKey(MASTERKEYIDX, "Debe cargar master key", StartActivity.this);
        if (!masterkey) {
            wrlg.wrDataTxt("Dispositivo sin MasterKey - Enviando a solicitud de MasterKey");
            UIUtils.startView(StartActivity.this, InjectMasterKey.class);
        } else if (idioma()) {
            if (cargarSdk()) {
                if (preInicio()) {
                    EliminarLogs eliminarLogs = new EliminarLogs(StartActivity.this,10000, new EliminarLogs.TcpCallback() {
                        @Override
                        public void RspLog(int RspLog) {
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    inicioAutomatico();
                                }
                            }, 1000);
                        }
                    });
                    eliminarLogs.execute();
                }
            }
        }
    }

    private boolean idioma() {
        boolean ret = false;
        String idiomaLocal = Locale.getDefault().toString();
        if (!idiomaLocal.equals("es_US")){
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                    this, R.style.Theme_AppCompat_Light_Dialog));

            builder.setTitle("Advertencia");
            builder.setMessage("Por favor cambia el idioma del dispositivo.\nPreferencia: Español - Estados Unidos");
            builder.setCancelable(false);

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        } else {
            ret = true;
        }
        return ret;
    }

    private void horaDispositivo() {
        //boolean ret = false;
        final Dialog dialog = new Dialog(StartActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        tvTitulo.setText("Advertencia");
        tvMensaje.setText("Por favor verifica la hora y fecha del dispositivo.");

        EditText txt = dialog.findViewById(R.id.txt_customDialog);
        txt.setVisibility(View.INVISIBLE);
        Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                InitTrans.initEMV = false;
                InitTrans.initialization = false;
                InitTrans.isNeedConfirmTime = false;
                dialog.dismiss();
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public int validaHora(){
        int rta = 0;
        int horaActual = Integer.parseInt(PAYUtils.getHMS().substring(0,4));
        int horaServidor = -1;
        try {
            horaServidor = Integer.parseInt(Trans.iso8583.getfield(12).substring(0, 4));
        }catch (Exception e){
        }
        String fechaActual = PAYUtils.getLocalDate();
        String fechaServidor = Trans.iso8583.getfield(13);
        if (horaServidor == -1) {
            int diferencia = horaServidor - horaActual;
            if (diferencia < 0) {
                diferencia = diferencia * (-1);
            }
            if (diferencia <= 5 && fechaActual.equals(fechaServidor)) {
                rta = 1;
            }
        }else{
            rta = -2;
            InitTrans.wrlg.wrDataTxt("Validacion hora. No fue posible leer la hora del servidor.");
        }
        return rta;
    }

    /**
     *
     */
    private void inicioAutomatico() {
        if (!InitTrans.initialization) {
            try {
                InitTrans.initVarInitialization();
            }catch (Exception e){
                startResult(this,false,"Error en init inicializacion");
                return;
            }
            try {
                if (checkBatch()) {
                    try {
                        if (!checkFechaCierre()) {
                            try {
                                countDownTimer.cancel();
                                Inicializacion();
                            } catch (Exception e) {
                                startResult(this, false, "Error en inicializacion");
                                return;
                            }
                        }
                    } catch (Exception e) {
                        startResult(this, false, "Error en check fecha cierre");
                        return;
                    }
                } else {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String date = dateFormat.format(new Date());
                    TMConfig.getInstance().setFechaCierre(date).save();
                    /*ClsConexion conexion = new ClsConexion(this);
                    capturarUsuarios();
                    for (int i = 0; i<arrayUsuarios.size(); i++) {
                        String nuevaFecha = PAYUtils.getLocalDateCierre();
                        conexion.updateFields(arrayUsuarios.get(i), "user_fechacierre", nuevaFecha,"user");
                    }*/
                    Inicializacion();
                }
            }catch (Exception e){
                startResult(this, false, "Error en check batch");
                return;
            }
        }
    }

    public static void startResult(AppCompatActivity activity , boolean flag , String info){
        Intent intent = new Intent();
        intent.setClass(activity , ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag" , flag);
        bundle.putString("info" , info);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    private boolean checkCambioModoOperacion() {
        boolean ret = false;
        try {
            if(!lastOperMode.equals(" ")){
                newOperMode = TMConfig.getInstance().getmodoOperacion();
                if (!lastOperMode.equals(newOperMode)){
                    checkSettle.setCierreCambioOperacion(true);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(StartActivity.this, MasterControl.class);
                    intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                    startActivity(intent);
                    //TMConfig.getInstance().setsesionIniciada("L");
                    InitTrans.loginCentral=false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    private boolean cargarSdk() {
        if (!InitTrans.initialization) {
            PaySdk.getInstance().setActivity(this);
            PayApplication.getInstance().addActivity(this);
            PayApplication.getInstance().setRunned();
            checkSettle = new CheckSettle(false, false);
            parametrosIniciales();
        }
        return true;
    }

    /**
     * @return
     */
    private boolean preInicio() {
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        Typeface tipoFuente4 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Bold.otf");
        temporizador();

        /*if(InitTrans.isMensaje60){
            countDownTimer.cancel();
            UIUtils.startView(this, MensajeCampo60.class);
        }*/

        tvMsg = findViewById(R.id.tvMsg1);
        tvMsg.setTypeface(tipoFuente4);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg2.setTypeface(tipoFuente3);
        tvMsg2.setText("V " + UIUtils.versionApk(this));

        //ImageButton Inicialization
        btn_Init = findViewById(R.id.btnInit);
        btn_Init.setTypeface(tipoFuente3);
        btn_Init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                iniciar();
            }
        });

        btnConf = findViewById(R.id.btnConf);
        btnConf.setTypeface(tipoFuente3);
        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                UIUtils.startView(StartActivity.this, Login.class);
            }
        });
        return true;
    }

    private void iniciar() {
        countDownTimer.cancel();
        if (!InitTrans.initialization) {
            InitTrans.initVarInitialization();
            if (checkBatch()) {
                if (!checkFechaCierre()) {
                    Inicializacion();
                }
            } else {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = dateFormat.format(new Date());
                TMConfig.getInstance().setFechaCierre(date).save();
                /*ClsConexion conexion = new ClsConexion(this);
                capturarUsuarios();
                for (int i = 0; i<arrayUsuarios.size(); i++) {
                    String nuevaFecha = PAYUtils.getLocalDateCierre();
                    conexion.updateFields(arrayUsuarios.get(i), "user_fechacierre", nuevaFecha,"user");
                }*/
                Inicializacion();
            }
        } else {
            if (checkBatch()) {
                if (!checkFechaCierre()) {
                    if (InitTrans.isNeedConfirmTime){
                        horaDispositivo();
                    }else {
                        inicioSesion();
                    }
                }
            }else{
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = dateFormat.format(new Date());
                TMConfig.getInstance().setFechaCierre(date).save();
                /*ClsConexion conexion = new ClsConexion(this);
                capturarUsuarios();
                for (int i = 0; i<arrayUsuarios.size(); i++) {
                    String nuevaFecha = PAYUtils.getLocalDateCierre();
                    conexion.updateFields(arrayUsuarios.get(i), "user_fechacierre", nuevaFecha,"user");
                }*/
                if (InitTrans.isNeedConfirmTime){
                    horaDispositivo();
                }else {
                    inicioSesion();
                }
            }
        }
    }

    private void inicioSesion() {
        if(SaveData.getInstance().getStartApp()) {
            TransLogData revesalData = TransLog.getReversal();
            if (revesalData != null) {
                reverso();
            }
            else {
                iniciarSesion();
            }
        }
        else {
            iniciarSesion();
        }
    }

    private void iniciarSesion() {
        if (TMConfig.getInstance().getmodoOperacion().equals("C")) {
            if (!TMConfig.getInstance().getsesionIniciada().equals("")) {
                if (TMConfig.getInstance().getsesionIniciada().equals("S") || TMConfig.getInstance().getsesionIniciada().equals("L")) {
                    countDownTimer.cancel();
                    UIUtils.startView(StartActivity.this, LoginCentralizado.class);
                }
                if (TMConfig.getInstance().getsesionIniciada().equals("N")) {
                    countDownTimer.cancel();
                    UIUtils.startView(StartActivity.this, MainMenuPrincipal.class);
                }
            }
        } else {
            UIUtils.startView(StartActivity.this, Login.class);
        }
    }

    private void reverso() {
        wrlg = new Wrlg();
        wrlg.wrDataTxt("Enviando reverso - desde StartActivity");
        if (InitTrans.initialization) {
            TransLogData revesalData = TransLog.getReversal();
            if (revesalData != null) {
                typeTrans = PrintRes.TRANSEN[15];
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[15]);
                startActivity(intent);
            }
        }

    }

    private void temporizador() {
        countDownTimer = new CountDownTimer(1000 * 60 * 15, 1000) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                if (InitTrans.initialization) {
                    reverso();
                }

            }
        }.start();
    }

    public void parametrosIniciales() {
        String user = " ";
        String id = ISOUtil.padright(user, 15, ' ');
        TMConfig.getInstance().setMerchID(id).save();
        lastOperMode = TMConfig.getInstance().getmodoOperacion();
        TMConfig.getInstance().setmodoOperacion(" ").save();
        String SN = DevConfig.getSN().substring(2,10);
        TMConfig.getInstance().setTermID(SN).save();
        SharedPreferences sharedPreferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        int traceShared =  Integer.parseInt(sharedPreferences.getString("trace","100"));
        int traceSys = Integer.parseInt(ISOUtil.padleft("" + TMConfig.getInstance().getTraceNo(), 6, '0'));

        if(traceShared != 100){
            if(traceShared != traceSys) {
                if (traceSys == 100 || traceSys >= 120) {
                    TMConfig.getInstance().setTraceNo(traceShared);
                }
            }
        }
    }

    private void Inicializacion() {
        typeTrans = PrintRes.TRANSEN[0];
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[0]);
        startActivity(intent);
        InitTrans.tkn07.setMensaje(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (InitTrans.initialization) {
            //Validar si existen reversos
            TransLogData revesalData = TransLog.getReversal();
            if (revesalData == null) {
                btnConf.setVisibility(View.GONE);
            } else {
                InitTrans.isReversalTrans = true;

            }
            if(!InitTrans.cierreRealizado) {
                if(checkBatch()) {
                    checkCambioModoOperacion();
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        temporizador();
    }

    /**
     * @return
     */
    public static boolean checkBatch() {
        List<TransLogData> list = TransLog.getInstance(CIERRE_TOTAL_LOG, true).getData();
        int lenList = list.size();
        return lenList > 0;
        //return false;
    }

    /**
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private boolean checkFechaCierre() {
        String fechaCierre = TMConfig.getInstance().getFechaCierre();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        if (fechaCierre.equals("0")) {
            TMConfig.getInstance().setFechaCierre(date);
            return false;
        } else {
            Date date_actual = null;
            Date date_cierre = null;
            try {
                date_actual = dateFormat.parse(date);
                date_cierre = dateFormat.parse(fechaCierre);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int rta = 0;
            if (date_actual != null) {
                rta = date_actual.compareTo(date_cierre);
            }
            if (rta > 0) {
                checkSettle.setSettleAutomatic(true);
                if (InitTrans.initialization) {
                    wrlg.wrDataTxt("Realizando cierre automatico - FechaUltCierre: " + fechaCierre);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(StartActivity.this, MasterControl.class);
                    intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                    startActivity(intent);
                } else {
                    Inicializacion();
                }
            } else {
                if(TMConfig.getInstance().getmodoOperacion().equals("N")){
                    int horaActual = Integer.parseInt(PAYUtils.getLocalTime().substring(0,4));
                    int horaSerial = verificarHora();
                    if(horaActual >= horaSerial) {
                        checkSettle.setSettleAutomatic(true);
                        if (InitTrans.initialization) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(StartActivity.this, MasterControl.class);
                            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                            startActivity(intent);
                        } else {
                            Inicializacion();
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public int verificarHora() {
        int hora;
        String serial = DevConfig.getSN();
        String ultimoSerial = serial.substring(serial.length() - 1);
        switch (ultimoSerial) {
            case "1":
                hora = 2100;
                break;
            case "2":
                hora = 2105;
                break;
            case "3":
                hora = 2110;
                break;
            case "4":
                hora = 2115;
                break;
            case "5":
                hora = 2120;
                break;
            case "6":
                hora = 2125;
                break;
            case "7":
                hora = 2130;
                break;
            case "8":
                hora = 2135;
                break;
            case "9":
                hora = 2140;
                break;
            case "0":
                hora = 2145;
                break;
            default:
                hora = 2150;
                break;
        }
        return hora;
    }

    public void capturarUsuarios() {
        ClsConexion conexion = new ClsConexion(this);
        listaUsuario = conexion.readUserList();

        if (listaUsuario != null) {
            arrayUsuarios = new ArrayList<String>();

            for (int i=0; i<listaUsuario.size(); i++) {
                arrayUsuarios.add(listaUsuario.get(i).getUser());
            }
        }
    }

    @Override
    public void onBackPressed() {}
}