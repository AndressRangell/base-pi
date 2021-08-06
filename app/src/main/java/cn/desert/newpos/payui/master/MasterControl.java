package cn.desert.newpos.payui.master;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import cnb.pichincha.wposs.mivecino_pichincha.Fragments.DialogFullscreenFragment;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.Adaptadores.AdaptadorActividad;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.Adaptadores.AdaptadorSector;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.Adaptadores.AdaptadorSituacionLaboral;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Actividad;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Sector;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.SituacionLaboral;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Tools.KeyboardUtil;
import newpos.libpay.global.TMConfig;
import newpos.libpay.device.scanner.QRCInfo;
import newpos.libpay.device.scanner.QRCListener;
import newpos.libpay.device.scanner.ScannerManager;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.pichincha.Tools.DatePickerFragment;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Global;

import newpos.libpay.trans.pichincha.Tools.TimePickerFragment;

import com.android.desert.keyboard.InputManager;

import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.ListViewAdapter;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.PaySdk;
import newpos.libpay.PaySdkException;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.device.user.OnUserResultListener;
import newpos.libpay.presenter.TransView;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.manager.DparaTrans;
import newpos.libpay.trans.pichincha.Tools.TransTools;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.utils.SaveData;


/**
 * Created by zhouqiang on 2017/7/3.
 */

public class MasterControl extends AppCompatActivity implements TransView, View.OnClickListener {

    public static CountDownTimer masterCDT;
    Boolean okTempo = false;
    Button btnConfirm;
    Button btnCancel;
    EditText transInfo;
    RadioButton rb_cte, rb_ah, rb_basic;
    Button btnCancelTypeAccount, btnAcceptTypeAccount;
    String typeAcc = "01";
    String tipoCuenta = "";
    Button btnCancelAmnt, btnAcceptAmnt;
    EditText et_amount;
    TextView tv_Service_Cost, tv_Total;
    String amountServiceCost = "0";
    double numeroTotal;
    String strMonto;
    Button btnCancelLast4, btnAcceptLast4;
    EditText et_last4;
    String hora, fecha;
    OnUserResultListener listener;
    String inputContent;
    String codSpinnerSector, codActivitySpinner, codSituacionLaboral;
    public static String TRANS_KEY = "TRANS_KEY";
    public static String tipoTrans = "tipoTrans";
    private String type;
    Typeface tipoFuente1, tipoFuente2, tipoFuente3, tipoFuente4;
    protected TransUI transUI;
    protected int timeout;
    Usuario usuario = new Usuario();
    public static boolean onBackCard = false;
    boolean noBack = false;
    boolean isFragmentBack = false;
    ClsConexion conexion = new ClsConexion(MasterControl.this);
    boolean VerficacionFaceId = false;

    private String mapFaceId = "";
    String nomarchivoFacePhi = "FacePhi" + ".txt";
    String carpetaFacePhi = "/sdcard/logs/facephi";
    String codigoSector = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PayApplication.getInstance().addActivity(this);
        type = getIntent().getStringExtra(TRANS_KEY);
        tipoTrans = getIntent().getStringExtra(tipoTrans);
        if (TMConfig.getInstance().getmodoOperacion().equals("C") && (TMConfig.getInstance().getsesionIniciada().equals("S") || TMConfig.getInstance().getsesionIniciada().equals("N"))) {
            obtener();
        }
        if (!type.equals(Trans.Type.INIT) && !type.equals(Trans.Type.CIERRE_TOTAL))
            DparaTrans.loadAIDCAPK2EMVKernelPichincha();

        if (MainMenuPrincipal.checkSettle != null)
            MainMenuPrincipal.checkSettle.setRunTrans(true);

        startTrans(type, tipoTrans);
        Context context = this;
        tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/PreloSlab-Book.otf");
        tipoFuente2 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Bold.otf");
        tipoFuente3 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Medium.otf");
        tipoFuente4 = Typeface.createFromAsset(context.getAssets(), "font/PreloSlab-Bold.otf");

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (VerficacionFaceId) {
            VerficacionFaceId = false;

            StringBuilder archivo = new StringBuilder();
            try {
                File dir = new File(carpetaFacePhi);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File f = new File(carpetaFacePhi + "/" + nomarchivoFacePhi);
                if (f.exists()) {
                    FileReader fr = new FileReader(f);
                    try (BufferedReader br = new BufferedReader(fr)) {
                        for (String leer; (leer = br.readLine()) != null; ) {
                            archivo.append(leer);
                            //archivo.append("\n");
                        }
                        f.delete();
                        inputContent = archivo.toString();
//                        Toast.makeText(this, "Lectura del archivo exitosa", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
//                      Toast.makeText(this, "Lectura del archivo fallida", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        System.out.println("LecturaFallida");
                        inputContent = "LecturaFallida";

                    }
                } else {
//                    Toast.makeText(this, "Archivo no Existe", Toast.LENGTH_SHORT).show();
                    System.out.println("ArchivoNoExiste onResume");
                    inputContent = "ArchivoNoExiste";

                }
            } catch (Exception e) {
                System.out.println(e + "");

                inputContent = "ProcesoFallido";
                if (listener == null) {
//                    Toast.makeText(this, "Listener Null", Toast.LENGTH_SHORT).show();
                }
            }
            if (listener == null) {
//                Toast.makeText(this, "Listener Null", Toast.LENGTH_SHORT).show();
            }
            System.out.println("Haciendo listener.confirm");
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnCancelTypeAccount)) {
            listener.cancel();
        }
        if (view.equals(btnAcceptTypeAccount)) {
            inputContent = typeAcc;
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(rb_cte)) {
            rb_cte.setChecked(true);
            rb_ah.setChecked(false);
            rb_basic.setChecked(false);
            typeAcc = "01";
        }
        if (view.equals(rb_ah)) {
            rb_cte.setChecked(false);
            rb_ah.setChecked(true);
            rb_basic.setChecked(false);
            typeAcc = "02";
        }
        if (view.equals(rb_basic)) {
            rb_cte.setChecked(false);
            rb_ah.setChecked(false);
            rb_basic.setChecked(true);
            typeAcc = "03";
        }

        if (view.equals(btnAcceptAmnt)) {
            hideKeyBoard(et_amount.getWindowToken());
            inputContent = tv_Total.getText().toString().replace(".", "");
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(btnCancelAmnt)) {
            listener.cancel();
        }

        if (view.equals(btnAcceptLast4)) {
            hideKeyBoard(et_last4.getWindowToken());
            inputContent = et_last4.getText().toString();
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(btnCancelLast4)) {
            listener.cancel();
        }
    }

    @Override
    public void showCardView(int timeout, int mode, final int tipo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //onBackCard = true;
                setContentView(R.layout.vista_insertar_tarjeta);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showCardView");
                temporizador(InitTrans.time);
                okTempo = true;
                noBack = false;
                TextView tv1 = findViewById(R.id.tv1);
                TextView tv2 = findViewById(R.id.tv2);
                tv1.setTypeface(tipoFuente1);
                tv2.setTypeface(tipoFuente3);

                if (tipo == FinanceTrans.TARJETA_CLIENTE) {
                    //getString(R.string.hello)
                    tv1.setText(getString(R.string.ingresaTarjetaCliente));
                    tv2.setText(getString(R.string.asegurarIngresarTarjetaCliente));
                } else if (tipo == FinanceTrans.TARJETA_CNB) {
                    tv1.setText(getString(R.string.ingresaTarjetaCNB));
                    tv2.setText(getString(R.string.asegurarIngresarTarjetaCNB));
                } else if (tipo == FinanceTrans.TARJETA_FUNCIONARIO) {
                    tv1.setText(getString(R.string.ingresaTarjetaFuncionario));
                    tv2.setText(getString(R.string.asegurarIngresarTarjetaFunci));
                } else if (tipo == FinanceTrans.TARJETA_CAMBIO_CLAVE) {
                    tv1.setText(getString(R.string.cambioClave));
                    tv2.setText("Inserta/Desliza la tarjeta \n (No la retires hasta el final de la transacción)");
                }
                InitTrans.wrlg.wrDataTxt("Fin timer, método showCardView");
                cancelarCountDown();
            }
        });
    }

    @Override
    public void showCedulaNfcView(int timeout, int mode, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //onBackCard = true;
                noBack = false;
                setContentView(R.layout.vista_cedula_nfc);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showCedulaNfcView");
                temporizador(InitTrans.time);
                TextView tv1 = findViewById(R.id.tv1);
                TextView tv2 = findViewById(R.id.tv2);
                tv1.setTypeface(tipoFuente1);
                tv2.setTypeface(tipoFuente3);
                tv2.setText("Acerca tu cédula al\n" +
                        "lector de contactless");
                InitTrans.wrlg.wrDataTxt("Fin timer, método showCedulaNfcView");
                cancelarCountDown();
            }
        });
    }

    @Override
    public void showMsgConfirmView(int timeout, final String title, final String text, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.trans_vista_msg_confirmacion);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showMsgConfirmView");
                temporizador(InitTrans.time);
                okTempo = true;
                noBack = false;
                btnConfirm = findViewById(R.id.btAceptar);
                btnCancel = findViewById(R.id.btCancel);
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms1 = findViewById(R.id.ms1);
                TextView ms3 = findViewById(R.id.ms3);

                tv1.setTypeface(tipoFuente1);
                tv1.setText(title);
                ms1.setTypeface(tipoFuente3);
                ms1.setTextSize(20);
                ms1.setText(text);
                ms1.setGravity(1);
                ms3.setTypeface(tipoFuente3);
                ms3.setText(getString(R.string.deseasRealizarCierre));
                ms3.setGravity(1);
                btnConfirm.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showMsgConfirmView, desde botón confirmar.");
                        cancelarCountDown();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showMsgConfirmView, desde botón cancelar.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public String getInput(InputManager.Mode type) {
        return inputContent;
    }

    @Override
    public void showCardAppListView(int timeout, String[] apps, OnUserResultListener l) {
        this.listener = l;
    }

    @Override
    public void showSuccess(int timeout, final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startResult(MasterControl.this, true, info);
                if (type.equals(Trans.Type.CREARUSUARIO)) {
                    String user = usuario.getUser();
                    String pass = usuario.getPassword();
                    String role = usuario.getRole();
                    conexion.crearUsuario(user, pass, role);
                }
            }
        });
    }

    @Override
    public void showSuccessView(int timeout, final int code, final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String array = showProcensando(code);
                String mensaje = array + " " + info;
                UIUtils.startResult(MasterControl.this, true, mensaje);
                if (type.equals(Trans.Type.CREARUSUARIO)) {
                    String user = usuario.getUser();
                    String pass = usuario.getPassword();
                    String role = usuario.getRole();
                    conexion.crearUsuario(user, pass, role);
                }
            }
        });
    }

    @Override
    public void showError(int timeout, final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startResult(MasterControl.this, false, err);
            }
        });
    }

    @Override
    public void showMsgError(int timeout, final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startResult(MasterControl.this, false, err);
            }
        });
    }

    @Override
    public void showImprimiendo(int timeout, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_imprimiendo);
                noBack = true;
                showHanding(showProcensando(status));
                TextView handing_msginfo = findViewById(R.id.handing_msginfo);
                handing_msginfo.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);

            }
        });
    }

    @Override
    public void showAlmacenarRecibo(final boolean secuencial) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                noBack = false;
                if (!secuencial) {
                    almacenarRecibo();
                }
                showHanding(getString(R.string.guardandoRecibo));
                TextView handing_msginfo = findViewById(R.id.handing_msginfo);
                handing_msginfo.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);

                if (type.equals(Trans.Type.INIT)) {
                    tv1.setVisibility(View.INVISIBLE);
                }

                tv1.setText(getString(R.string.procesando));
            }
        });
    }

    @Override
    public void actualizarEstadoCierre() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conexion.updateFields("Abierto", "user_estadocierre", "Cerrado",
                        "user_estadocierre");
            }
        });
    }

    public void almacenarRecibo() {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("recibo", InitTrans.ultimoRecibo);
        editor.apply();
    }

    public void obtenerRecibo() {
        SharedPreferences prefe = getSharedPreferences("datos", Context.MODE_PRIVATE);
        InitTrans.ultimoRecibo = prefe.getString("recibo", "NO");
    }

    public String showProcensando(int posicion) {
        Context context = this;
        String[] menu = context.getResources().getStringArray(R.array.mensajes_handling);
        return menu[posicion];
    }

    @Override
    public void showMsgInfo(final String titulo, int timeout, final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                cancelarCountDown();
                showHanding(status);
                noBack = true;
                TextView handing_msginfo = findViewById(R.id.handing_msginfo);
                handing_msginfo.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);

                if (type.equals(Trans.Type.INIT)) {
                    tv1.setVisibility(View.INVISIBLE);
                }

                tv1.setText(titulo);

            }
        });
    }

    @Override
    public void showMsgInfo(final String titulo, int timeout, final int posicion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.trans_handling);
                noBack = true;
                cancelarCountDown();
                //WebView handling = findViewById(R.id.handling);
                TextView handing_msginfo = findViewById(R.id.handing_msginfo);
                handing_msginfo.setTypeface(tipoFuente3);
                handing_msginfo.setText(showProcensando(posicion));
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);

                if (type.equals(Trans.Type.INIT)) {
                    tv1.setVisibility(View.INVISIBLE);
                }

                tv1.setText(titulo);

                /*
                setContentView(R.layout.vista_progressbar);
                TextView mensaje = findViewById(R.id.mensaje);
                mensaje.setTypeface(tipoFuente3);
                mensaje.setText(showProcensando(posicion));
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                if (type.equals(Trans.Type.INIT)){
                    tv1.setVisibility(View.INVISIBLE);
                }
                ProgressBar progressBar = findViewById(R.id.progressBar);
                */

            }
        });
    }

    @Override
    public void showView(final Class<?> cls) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startView(MasterControl.this, cls);
            }
        });
    }

    @Override
    public void showAmountCnbView(int timeout, final String title, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.amount_cnb);
                noBack = false;
                et_amount = findViewById(R.id.et_Amount);
                tv_Service_Cost = findViewById(R.id.tv_CostoServicio);
                tv_Total = findViewById(R.id.tv_Total);
                btnCancelAmnt = findViewById(R.id.btCancel);
                btnAcceptAmnt = findViewById(R.id.btAceptar);


                amountServiceCost = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(), 0,
                        InitTrans.tkn80.getCostoServicio().length);
                numeroTotal = Double.valueOf(amountServiceCost);
                amountServiceCost = TransTools.changeFormatAmnt(amountServiceCost);
                tv_Service_Cost.setText(String.valueOf(amountServiceCost));
                tv_Total.setText(String.valueOf(numeroTotal));
                btnAcceptAmnt.setOnClickListener(MasterControl.this);
                btnCancelAmnt.setOnClickListener(MasterControl.this);
                setToolbar(title);

                et_amount.addTextChangedListener(new TextWatcher() {
                    private boolean isChanged = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isChanged) {
                            return;
                        }
                        String str = editable.toString();
                        isChanged = true;
                        String cuttedStr = str;
                        for (int i = str.length() - 1; i >= 0; i--) {
                            char c = str.charAt(i);
                            if ('.' == c) {
                                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                break;
                            }
                        }
                        int NUM = cuttedStr.length();
                        int zeroIndex = -1;
                        for (int i = 0; i < NUM - 2; i++) {
                            char c = cuttedStr.charAt(i);
                            if (c != '0') {
                                zeroIndex = i;
                                break;
                            } else if (i == NUM - 3) {
                                zeroIndex = i;
                                break;
                            }
                        }
                        if (zeroIndex != -1) {
                            cuttedStr = cuttedStr.substring(zeroIndex);
                        }
                        if (cuttedStr.length() < 3) {
                            cuttedStr = "0" + cuttedStr;
                        }
                        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                + "." + cuttedStr.substring(cuttedStr.length() - 2);

                        et_amount.setText(cuttedStr);
                        numeroTotal = Double.valueOf(amountServiceCost);
                        numeroTotal = numeroTotal + Double.valueOf(cuttedStr);
                        String auxNum = String.valueOf(numeroTotal);

                        if (auxNum.length() >= 3) {
                            String[] parts = auxNum.split("\\.");
                            String p = parts[1];
                            if (p.length() == 1)
                                auxNum += "0";
                        }
                        tv_Total.setText(auxNum);
                        et_amount.setSelection(et_amount.length());
                        isChanged = false;
                    }
                });
            }
        });
    }

    /**
     * @param view InputData: Array de views (EditText, TextView, etc)
     */
    private void touch(View[] view) {
        for (int i = 0; i < view.length; i++) {
            if (view[i] instanceof EditText) {
                view[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == KeyEvent.ACTION_UP) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método touch, setOnTouchListener");
                            reiniciarCountDown();
                        }
                        return false;
                    }
                });

                view[i].setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método touch, setOnKeyListener");
                            reiniciarCountDown();
                            if (keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                                hideKeyBoard(view.getWindowToken());
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void showAdministrativasView(int timeout, String title, OnUserResultListener l) {
        if (title == "CREARUSUARIO") {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.vista_crearusuario_bd);
                    KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                    keyboardUtil.enable();
                    noBack = true;
                    final EditText et_user = findViewById(R.id.et_user);
                    final EditText et_pw = findViewById(R.id.et_pw);
                    final EditText et_pwc = findViewById(R.id.et_pwc);
                    final TextView txtErrorCodigo = findViewById(R.id.txtErrorCodigo);
                    final TextView txtErrorClave = findViewById(R.id.txtErrorClave);
                    TextView tv1 = findViewById(R.id.tv1);
                    Button btAceptar = findViewById(R.id.btAceptar);
                    Button btCancelar = findViewById(R.id.btCancel);
                    tv1.setTypeface(tipoFuente1);
                    tv1.setText(getString(R.string.crearUsuario));
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showAdministrativasView.");
                    temporizador(InitTrans.time);
                    btAceptar.setTypeface(tipoFuente3);
                    btCancelar.setTypeface(tipoFuente3);

                    View[] viewArray = new View[]{et_user, et_pw, et_pwc};
                    touch(viewArray);

                    /*et_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reiniciarCountDown();
                        }
                    });

                    et_pw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reiniciarCountDown();
                        }
                    });

                    et_pwc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reiniciarCountDown();
                        }
                    });*/

                    btAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, botón aceptar.");
                            reiniciarCountDown();
                            String user = et_user.getText().toString();
                            String pass = et_pw.getText().toString();
                            String passConf = et_pwc.getText().toString();

                            if (user.length() < 10) {
                                changeToBackgroundError(et_user, txtErrorCodigo, "Código usuario debe ser de 10 dígitos", R.drawable.edittext_pichi_normal);
                            } else {
                                if ((pass.length() < 6 || passConf.length() < 6)) {
                                    et_pw.setText("");
                                    et_pwc.setText("");
                                    changeToBackgroundError(et_pw, txtErrorClave, "La clave debe ser de 6 dígitos.", R.drawable.edittext_pichi_normal);
                                } else {
                                    if (pass.equals(passConf)) {
                                        if (!conexion.validarUser(user)) {
                                            InitTrans.wrlg.wrDataTxt("Fin timer, método showAdministrativasView, usuario creado.");
                                            cancelarCountDown();
                                            inputContent = et_user.getText().toString();
                                            listener.confirm(InputManager.Style.COMMONINPUT);
                                            usuario.setUser(user);
                                            usuario.setPassword(pass);
                                            usuario.setRole("USUARIO");
                                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            manager.hideSoftInputFromWindow(et_user.getWindowToken(), 0);
                                        } else {
                                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método showAdministrativasView, usuario ya existe.");
                                            reiniciarCountDown();
                                            et_user.setText("");
                                            changeToBackgroundError(et_user, txtErrorCodigo, "Usuario ya existe", R.drawable.edittext_pichi_normal);

                                        }
                                    } else {
                                        InitTrans.wrlg.wrDataTxt("Reiniciar timer, método showAdministrativasView, las contraseñas no coinciden.");
                                        reiniciarCountDown();
                                        et_pw.setText("");
                                        et_pwc.setText("");
                                        changeToBackgroundError(et_pwc, txtErrorClave, "Las contraseñas no coinciden", R.drawable.edittext_pichi_normal);
                                    }
                                }
                            }
                        }
                    });

                    btCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showAdministrativasView, botón cancelar.");
                            cancelarCountDown();
                            listener.cancel();
                        }
                    });
                }

            });
        }
    }

    @Override
    public void showVisitaView(int timeout, String title, OnUserResultListener l) {
        if (title == "VISITAFUNCIONARIO") {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.vista_listview_banner2);
                    noBack = false;
                    theToolbarVisitaView();
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showVisitaView.");
                    temporizador(InitTrans.time);
                    Typeface tipoFuente1 = Typeface.createFromAsset(MasterControl.this.getAssets(), "font/PreloSlab-Book.otf");
                    TextView tv1 = findViewById(R.id.tv1);
                    ListView lv1 = findViewById(R.id.lv1);
                    tv1.setTypeface(tipoFuente1);
                    tv1.setText(getString(R.string.visitaFuncionario));
                    String[] opcion = {getString(R.string.visitaFuncionario)};
                    int[] imagenes = {R.mipmap.ic_delante_azul};
                    ListViewAdapter adapter2 = new ListViewAdapter(MasterControl.this, opcion, imagenes);
                    lv1.setAdapter(adapter2);
                    InitTrans.tkn15.getCargo();

                    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showVisitaView, selección en listview.");
                            cancelarCountDown();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void showInformacionVisitaView(int timeout, String title, OnUserResultListener l) {
        if (title == "VISITAFUNCIONARIO_C") {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noBack = false;
                    setContentView(R.layout.trans_vista_msg_confirmacion);
                    theToolbarVisitaView();
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showInformacionVisitaView.");
                    temporizador(InitTrans.time);
                    TextView tv1 = findViewById(R.id.tv1);
                    TextView ms1 = findViewById(R.id.ms1);
                    TextView ms2 = findViewById(R.id.ms2);
                    TextView ms3 = findViewById(R.id.ms3);
                    TextView ms4 = findViewById(R.id.ms4);
                    Button btCancel = findViewById(R.id.btCancel);
                    Button btAceptar = findViewById(R.id.btAceptar);
                    btCancel.setTypeface(tipoFuente3);
                    btAceptar.setTypeface(tipoFuente3);
                    tv1.setText(getString(R.string.visitaFuncionario));
                    tv1.setTypeface(tipoFuente1);
                    ms1.setText(getString(R.string.nombre2P) + String.valueOf(InitTrans.tkn15.getNombre().trim()));
                    ms1.setTypeface(tipoFuente1);
                    ms1.setTextSize(17);
                    ms1.setTypeface(tipoFuente3);
                    ms2.setText("Cédula: " + String.valueOf(InitTrans.tkn15.getCedula().trim()));
                    ms2.setTypeface(tipoFuente1);
                    ms2.setTextSize(17);
                    ms2.setTypeface(tipoFuente3);
                    ms3.setText("Cargo: " + String.valueOf(InitTrans.tkn15.getCargo().trim()));
                    ms3.setTypeface(tipoFuente1);
                    ms3.setTextSize(17);
                    ms3.setTypeface(tipoFuente3);
                    ms4.setText("¿Deseas imprimir esta información?");
                    ms4.setTypeface(tipoFuente1);
                    ms4.setTextSize(17);
                    ms4.setTypeface(tipoFuente3);

                    btAceptar.setText("Sí");
                    btCancel.setText("No");

                    btAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showInformacionVisitaView, botón aceptar.");
                            cancelarCountDown();
                            inputContent = "si";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });

                    btCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showInformacionVisitaView, botón cancelar.");
                            cancelarCountDown();
                            inputContent = "no";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void showMsgConfirmacionView(int timeout, final String[] mensajes, final boolean isCorregir, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_vista_msg_confirmacion);
                theToolbarVisitaView();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showMsgConfirmacionView.");
                temporizador(InitTrans.time);
                noBack = false;
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                TextView ms1 = findViewById(R.id.ms1);
                TextView ms2 = findViewById(R.id.ms2);
                TextView ms3 = findViewById(R.id.ms3);
                TextView ms4 = findViewById(R.id.ms4);
                Button btCancel = findViewById(R.id.btCancel);
                Button btAceptar = findViewById(R.id.btAceptar);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                LinearLayout linearCorregir = findViewById(R.id.linearCorregir);
                TextView tvCorregir = findViewById(R.id.tvCorregir);
                Button btnCorregir = findViewById(R.id.btnCorregir);

                if (isCorregir) {
                    linearCorregir.setVisibility(View.VISIBLE);
                    tvCorregir.setTypeface(tipoFuente3);
                    btnCorregir.setTypeface(tipoFuente3);
                    btnCorregir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showMsgConfirmacionView, if(isCorregir).");
                            cancelarCountDown();
                            inputContent = "corregir";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                }

                tv1.setTypeface(tipoFuente1);
                if (mensajes.length <= 5) {
                    tv1.setText(getString(R.string.bancoPichincha));
                } else {
                    tv1.setText(mensajes[5]);
                }
                ms0.setText(mensajes[0]);
                ms0.setTextSize(20);
                ms0.setTypeface(tipoFuente3);
                ms1.setText(mensajes[1]);
                ms1.setTextSize(20);
                ms1.setTypeface(tipoFuente3);
                ms2.setText(mensajes[2]);
                ms2.setTextSize(20);
                ms2.setTypeface(tipoFuente3);
                ms3.setText(mensajes[3]);
                ms3.setTextSize(20);
                ms3.setTypeface(tipoFuente3);
                ms4.setText(mensajes[4]);
                ms4.setTextSize(20);
                ms4.setTypeface(tipoFuente3);

                btAceptar.setText(getString(R.string.btn_accept));
                btCancel.setText(R.string.btn_cancel);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showMsgConfirmacionView, botón aceptar.");
                        cancelarCountDown();
                        inputContent = "aceptar";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showMsgConfirmacionView, botón cancelar.");
                        cancelarCountDown();
                        inputContent = "cancelar";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });
            }
        });
    }

    @Override
    public void showMsgConfirmacionCheckView(int timeout, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_vista_msg_confirmacion_check);
                theToolbarVisitaView();
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                final RadioButton opc1 = findViewById(R.id.opc1);
                final RadioButton opc2 = findViewById(R.id.opc2);
                Button btCancel = findViewById(R.id.btCancel);
                Button btAceptar = findViewById(R.id.btAceptar);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                tv1.setTypeface(tipoFuente1);
                if (mensajes.length <= 5) {
                    tv1.setText(getString(R.string.bancoPichincha));
                } else {
                    tv1.setText(mensajes[0]);
                }
                ms0.setText(mensajes[1]);
                ms0.setTextSize(20);
                ms0.setTypeface(tipoFuente3);

                opc1.setText(mensajes[2]);
                opc1.setTextSize(20);
                opc1.setTypeface(tipoFuente3);
                opc1.setSelected(true);

                opc2.setText(mensajes[3]);
                opc2.setTextSize(20);
                opc2.setTypeface(tipoFuente3);
                opc2.setSelected(false);

                btAceptar.setText(getString(R.string.btn_accept));
                btCancel.setText(R.string.btn_cancel);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelarCountDown();

                        if (opc1.isChecked()) {
                            inputContent = opc1.getText().toString();
                        } else {
                            inputContent = opc2.getText().toString();
                        }

                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelarCountDown();
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showMontoVariableView(int timeout, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_monto_recau39);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                theToolbarVisitaView();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showMontoVariableView.");
                temporizador(InitTrans.time);
                noBack = false;
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                TextView ms1 = findViewById(R.id.ms1);
                TextView ms2 = findViewById(R.id.ms2);
                TextView ms3 = findViewById(R.id.ms3);
                TextView tvMSG1 = findViewById(R.id.tvMSG1);
                final TextView txtErrorMonto = findViewById(R.id.txtErrorMonto);
                final EditText etMonto = findViewById(R.id.etMonto);
                etMonto.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                Button btCancel = findViewById(R.id.btnCancel);
                Button btAceptar = findViewById(R.id.btnAccept);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                tv1.setTypeface(tipoFuente1);
                if (mensajes.length <= 4) {
                    tv1.setText(getString(R.string.bancoPichincha));
                } else {
                    tv1.setText(mensajes[4]);
                }

                ms0.setText(mensajes[0]);
                ms0.setTextSize(14);
                ms0.setTypeface(tipoFuente2);
                ms1.setText(mensajes[1]);
                ms1.setTextSize(14);
                ms1.setTypeface(tipoFuente2);
                ms2.setText(mensajes[2]);
                ms2.setTextSize(14);
                ms2.setTypeface(tipoFuente3);
                ms3.setText(mensajes[3]);
                ms3.setTextSize(14);
                ms3.setTypeface(tipoFuente2);

                tvMSG1.setTypeface(tipoFuente3);
                etMonto.setTypeface(tipoFuente3);
                /*etMonto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });*/
                etMonto.addTextChangedListener(new TextWatcher() {
                    private boolean isChanged = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isChanged) {
                            return;
                        }
                        String str = editable.toString();
                        isChanged = true;
                        String cuttedStr = str;
                        for (int i = str.length() - 1; i >= 0; i--) {
                            char c = str.charAt(i);
                            if ('.' == c) {
                                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                break;
                            }
                        }
                        int NUM = cuttedStr.length();
                        int zeroIndex = -1;
                        for (int i = 0; i < NUM - 2; i++) {
                            char c = cuttedStr.charAt(i);
                            if (c != '0') {
                                zeroIndex = i;
                                break;
                            } else if (i == NUM - 3) {
                                zeroIndex = i;
                                break;
                            }
                        }
                        if (zeroIndex != -1) {
                            cuttedStr = cuttedStr.substring(zeroIndex);
                        }
                        if (cuttedStr.length() < 3) {
                            cuttedStr = "0" + cuttedStr;
                        }
                        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                + "." + cuttedStr.substring(cuttedStr.length() - 2);

                        etMonto.setText(cuttedStr);

                        NumberFormat numberFormat = new DecimalFormat("###.##");
                        String auxNum = String.valueOf(numberFormat.format(numeroTotal));

                        etMonto.setSelection(etMonto.length());
                        isChanged = false;
                    }
                });

                touch(new View[]{etMonto});

                btAceptar.setText(getString(R.string.btn_accept));
                btCancel.setText(getString(R.string.btn_cancel));

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validarMonto(etMonto, txtErrorMonto)) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showMontoVariableView, botón aceptar.");
                            cancelarCountDown();
                            String monto = etMonto.getText().toString().replace(".", "");
                            hideKeyBoard(etMonto.getWindowToken());
                            inputContent = monto;
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showMontoVariableView, botón cancelar.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showConfirmacionView(int timeout, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_confirmacion_resp99);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                theToolbarVisitaView();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showConfirmacionView.");
                temporizador(InitTrans.time);
                noBack = false;
                ScrollView scrollView = findViewById(R.id.scroll);
                scrollView.arrowScroll(View.SCROLL_INDICATOR_BOTTOM);

                TextView tv1 = findViewById(R.id.tv1);
                TextView ms1 = findViewById(R.id.ms1);
                TextView ms2 = findViewById(R.id.ms2);
                TextView ms3 = findViewById(R.id.ms3);
                TextView ms4 = findViewById(R.id.ms4);
                TextView ms5 = findViewById(R.id.ms5);
                TextView tvMSG1 = findViewById(R.id.tvMSG1);
                final EditText etMonto = findViewById(R.id.etMonto);
                final TextView txtErrorMonto = findViewById(R.id.txtErrorMonto);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etMonto, InputMethodManager.SHOW_IMPLICIT);
                Button btCancel = findViewById(R.id.btnCancel);
                Button btAceptar = findViewById(R.id.btnAccept);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                tv1.setTypeface(tipoFuente1);
                if (mensajes.length <= 6) {
                    tv1.setText(getString(R.string.bancoPichincha));
                } else {
                    tv1.setText(mensajes[6]);
                }

                ms1.setText(mensajes[0]);
                ms1.setTextSize(16);
                ms1.setTypeface(tipoFuente2);
                ms2.setText(mensajes[1]);
                ms2.setTextSize(14);
                ms2.setTypeface(tipoFuente3);
                ms3.setText(mensajes[2]);
                ms3.setTextSize(14);
                ms3.setTypeface(tipoFuente3);
                ms4.setText(mensajes[3]);
                ms4.setTextSize(14);
                ms4.setTypeface(tipoFuente3);
                ms5.setText(mensajes[4]);
                ms5.setTextSize(14);
                ms5.setTypeface(tipoFuente3);

                tvMSG1.setTypeface(tipoFuente3);
                etMonto.setTypeface(tipoFuente3);
                /*etMonto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });*/
                etMonto.addTextChangedListener(new TextWatcher() {
                    private boolean isChanged = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isChanged) {
                            return;
                        }
                        String str = editable.toString();
                        isChanged = true;
                        String cuttedStr = str;
                        for (int i = str.length() - 1; i >= 0; i--) {
                            char c = str.charAt(i);
                            if ('.' == c) {
                                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                break;
                            }
                        }
                        int NUM = cuttedStr.length();
                        int zeroIndex = -1;
                        for (int i = 0; i < NUM - 2; i++) {
                            char c = cuttedStr.charAt(i);
                            if (c != '0') {
                                zeroIndex = i;
                                break;
                            } else if (i == NUM - 3) {
                                zeroIndex = i;
                                break;
                            }
                        }
                        if (zeroIndex != -1) {
                            cuttedStr = cuttedStr.substring(zeroIndex);
                        }
                        if (cuttedStr.length() < 3) {
                            cuttedStr = "0" + cuttedStr;
                        }
                        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                + "." + cuttedStr.substring(cuttedStr.length() - 2);

                        etMonto.setText(cuttedStr);

                        etMonto.setSelection(etMonto.length());
                        isChanged = false;
                    }
                });

                touch(new View[]{etMonto});

                btAceptar.setText(getString(R.string.btn_accept));
                btCancel.setText(getString(R.string.btn_cancel));

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validarMonto(etMonto, txtErrorMonto)) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showConfirmacionView, botón aceptar.");
                            cancelarCountDown();
                            String monto = etMonto.getText().toString().replace(".", "");
                            hideKeyBoard(etMonto.getWindowToken());
                            inputContent = monto;
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showConfirmacionView, botón cancalar.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showMsgConfirmacionRemesaView(int timeout, final String[] mensajes, final boolean corregir, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_vista_msg_confirmacionremesas);
                theToolbarVisitaView();
                temporizador(InitTrans.time);

                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                final TextView ms1 = findViewById(R.id.ms1);
                final EditText ms2 = findViewById(R.id.ms2);
                TextView ms3 = findViewById(R.id.ms3);
                TextView ms4 = findViewById(R.id.ms4);
                TextView ms5 = findViewById(R.id.ms5);
                Button btCancel = findViewById(R.id.btCancel);
                Button btAceptar = findViewById(R.id.btAceptar);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                LinearLayout linearCorregir = findViewById(R.id.linearCorregir);
                TextView tvCorregir = findViewById(R.id.tvCorregir);
                Button btnCorregir = findViewById(R.id.btnCorregir);

                if (corregir) {
                    linearCorregir.setVisibility(View.VISIBLE);
                    tvCorregir.setTypeface(tipoFuente3);
                    btnCorregir.setTypeface(tipoFuente3);
                    ms2.setEnabled(false);
                    btnCorregir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ms2.setEnabled(true);
                            ms2.requestFocus();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(ms2, InputMethodManager.SHOW_IMPLICIT);
                            /*masterCDT.cancel();
                            inputContent = "corregir";
                            listener.confirm(InputManager.Style.COMMONINPUT);*/
                        }
                    });
                }

                tv1.setTypeface(tipoFuente1);
                if (mensajes.length <= 5) {
                    tv1.setText(getString(R.string.bancoPichincha));
                } else {
                    tv1.setText(mensajes[5]);
                }
                ms0.setText(mensajes[0]);
                ms0.setTextSize(20);
                ms0.setTypeface(tipoFuente3);

                if (!corregir) {
                    ms2.setVisibility(View.INVISIBLE);
                }
                ms1.setText(mensajes[1]);
                ms1.setTextSize(20);
                ms1.setTypeface(tipoFuente3);
                ms2.setText(mensajes[2]);
                ms2.setTextSize(20);
                ms2.setTypeface(tipoFuente3);
                ms3.setText(mensajes[3]);
                ms3.setTextSize(20);
                ms3.setTypeface(tipoFuente3);
                ms4.setText(mensajes[4]);
                ms4.setTextSize(20);
                ms4.setTypeface(tipoFuente3);
                /*ms5.setText(mensajes[5]);
                ms5.setTextSize(20);
                ms5.setTypeface(tipoFuente3);*/

                btAceptar.setText(getString(R.string.btn_accept));
                btCancel.setText(R.string.btn_cancel);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputContent = ms2.getText().toString();
                        if (inputContent.isEmpty()) {
                            ms1.setTextColor(getResources().getColor(R.color.red));
                            changeToBackgroundError(ms2, ms1, "El campo dirección no puede estar vacío.", R.drawable.edittext_pichi_error);
                        } else {
                            inputContent = ms2.getText().toString();
                            masterCDT.cancel();
                            hideKeyBoard(ms2.getWindowToken());
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        masterCDT.cancel();
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }


    @Override
    public void showVerficacionFaceId(final int timeout, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                TextView txt = findViewById(R.id.handing_msginfo);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                txt.setText("Por favor espere un momento ");
                progressBar.setIndeterminate(true);
                String nombrePackage = "com.example.facephipinchincha";
                if (isAppInstalada(nombrePackage)) {
                    VerficacionFaceId = true;
                    try {
                        File dir = new File(carpetaFacePhi);
                        File f = new File(carpetaFacePhi + "/" + nomarchivoFacePhi);
                        if (f.exists()) {
                            f.delete();
                        }
                    } catch (Exception e) {

                    }
//                    Toast.makeText(MasterControl.this, "Aplicacion si esta instalada", Toast.LENGTH_SHORT).show();
                    Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(nombrePackage);
                    String date = PAYUtils.getLocalDate();
                    String time = PAYUtils.getLocalTime();
                    intent.putExtra("pin", date + time);
                    startActivity(intent);
                } else {
                    inputContent = "ApliacionNoInstalada";
//                    Toast.makeText(MasterControl.this, "Aplicacion no esta instalada", Toast.LENGTH_SHORT).show();
                    listener.confirm(InputManager.Style.COMMONINPUT);
                }

            }
        });
    }

    public boolean isAppInstalada(String packageFaceId) {
        boolean ret = false;
        List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packageList.size(); i++) {
            PackageInfo packageInfo = packageList.get(i);
            if (packageInfo.packageName.equals(packageFaceId)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private boolean existe(String archbusca) {
        String[] archivos = fileList();
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == -1) {
            finish();
        }
    }


    @Override
    public void showPrincipalesView(int timeout, String title, OnUserResultListener l) {
        noBack = false;
        if (title == "MONTO") {
            this.listener = l;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.amount_cnb);
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showPrincipalesView, MONTO.");
                    temporizador(InitTrans.time);
                    KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                    keyboardUtil.enable();

                    TextView tv1 = findViewById(R.id.tv1);
                    final TextView txtErrorMonto = findViewById(R.id.txtErrorMonto);
                    if (type.equals(Trans.Type.RETIRO)) {
                        tv1.setText("Ingresa monto para retiro");
                    } else if (type.equals(Trans.Type.DEPOSITO)) {
                        tv1.setText("Ingresa los datos del depósito");
                    }

                    tv1.setTypeface(tipoFuente1);

                    et_amount = findViewById(R.id.et_Amount);
                    tv_Service_Cost = findViewById(R.id.tv_CostoServicio);
                    tv_Total = (TextView) findViewById(R.id.tv_Total);
                    btnCancelAmnt = (Button) findViewById(R.id.btCancel);
                    btnAcceptAmnt = (Button) findViewById(R.id.btAceptar);
                    btnAcceptAmnt.setTypeface(tipoFuente3);
                    btnCancelAmnt.setTypeface(tipoFuente3);

                    /*et_amount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reiniciarCountDown();
                        }
                    });*/

                    et_amount.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_amount, InputMethodManager.SHOW_IMPLICIT);

                    if (type.equals("RETIRO")) {
                        amountServiceCost = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(), 0,
                                InitTrans.tkn80.getCostoServicio().length);
                        amountServiceCost = TransTools.changeFormatAmnt(amountServiceCost);
                        numeroTotal = Double.valueOf(amountServiceCost);
                        tv_Service_Cost.setText(String.valueOf(amountServiceCost));
                        tv_Total.setText(String.valueOf(numeroTotal));
                    } else {
                        numeroTotal = 0;
                        tv_Service_Cost.setText(String.valueOf(numeroTotal));
                        tv_Total.setText(String.valueOf(numeroTotal));
                    }

                    et_amount.addTextChangedListener(new TextWatcher() {
                        private boolean isChanged = false;

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (isChanged) {
                                return;
                            }
                            String str = editable.toString();
                            isChanged = true;
                            String cuttedStr = str;
                            for (int i = str.length() - 1; i >= 0; i--) {
                                char c = str.charAt(i);
                                if ('.' == c) {
                                    cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                    break;
                                }
                            }
                            int NUM = cuttedStr.length();
                            int zeroIndex = -1;
                            for (int i = 0; i < NUM - 2; i++) {
                                char c = cuttedStr.charAt(i);
                                if (c != '0') {
                                    zeroIndex = i;
                                    break;
                                } else if (i == NUM - 3) {
                                    zeroIndex = i;
                                    break;
                                }
                            }
                            if (zeroIndex != -1) {
                                cuttedStr = cuttedStr.substring(zeroIndex);
                            }
                            if (cuttedStr.length() < 3) {
                                cuttedStr = "0" + cuttedStr;
                            }
                            cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                    + "." + cuttedStr.substring(cuttedStr.length() - 2);

                            et_amount.setText(cuttedStr);
                            numeroTotal = Double.valueOf(amountServiceCost);
                            numeroTotal = numeroTotal + Double.valueOf(cuttedStr);
                            NumberFormat numberFormat = new DecimalFormat("###.##");
                            String auxNum = String.valueOf(numberFormat.format(numeroTotal));

                            tv_Total.setText(auxNum);
                            et_amount.setSelection(et_amount.length());
                            isChanged = false;
                        }
                    });

                    /*et_amount.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                hideKeyBoard(et_amount.getWindowToken());
                            }
                            return false;
                        }
                    });*/

                    touch(new View[]{et_amount});

                    btnAcceptAmnt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método showPrincipalesView, botón btnAcceptAmnt.");
                            reiniciarCountDown();
                            if (!et_amount.getText().toString().equals("")) {
                                strMonto = et_amount.getText().toString();
                                strMonto = strMonto.replace(".", "");
                                long monto = Long.parseLong(strMonto);
                                int val;

                                if (type.equals(PrintRes.TRANSEN[2])) {
                                    val = TransTools.validateAmountRetiro(monto / 100);
                                } else if (type.equals(PrintRes.TRANSEN[6])) {
                                    val = TransTools.validateAmountDeposito(monto / 100);
                                } else {
                                    val = 0;
                                }


                                if (val == 1) {
                                    et_amount.setText("0.00");
                                    changeToBackgroundError(et_amount, txtErrorMonto, "Monto inferior al permitido", R.drawable.edittext_pichi_normal);
                                } else if (val == 2) {
                                    et_amount.setText("0.00");
                                    changeToBackgroundError(et_amount, txtErrorMonto, "Monto superior al permitido", R.drawable.edittext_pichi_normal);
                                } else {
                                    hideKeyBoard(et_amount.getWindowToken());
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método showPrincipalesView, monto.");
                                    cancelarCountDown();
                                    inputContent = et_amount.getText().toString().replace(".", "");
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                }
                            } else {
                                changeToBackgroundError(et_amount, txtErrorMonto, "Monto no digitado", R.drawable.edittext_pichi_normal);
                            }

                        }
                    });

                    btnCancelAmnt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showPrincipalesView, botón cancelar.");
                            cancelarCountDown();
                            listener.cancel();

                        }
                    });
                }
            });

        } else if (title == "MULTIPLECUENTA") {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.vista_buttons_banner2);
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showPrincipalesView, MULTIPLECUENTA.");
                    temporizador(InitTrans.time);
                    setToolbar();
                    TextView tv1 = findViewById(R.id.tv1);
                    tv1.setTypeface(tipoFuente1);
                    tv1.setText("Múltiple cuenta");

                    final String[] cuentas = InitTrans.tkn98.getLasCuentas();
                    final int codCuenta = cuentas.length;

                    LinearLayout linearLayout = findViewById(R.id.linearBotones);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                            , 120);
                    lp.setMargins(0, 10, 0, 0);


                    int i;
                    Button btn[] = new Button[codCuenta];
                    for (i = 0; i < codCuenta; i++) {
                        btn[i] = new Button(MasterControl.this);
                        btn[i].setBackgroundResource(R.drawable.pichi_btn_menu);
                        btn[i].setLayoutParams(lp);
                        btn[i].setText(cuentas[i]);
                        btn[i].setTextColor(Color.parseColor("#0F265C"));
                        btn[i].setTypeface(tipoFuente3);
                        btn[i].setTextSize(22);
                        final int j = i;
                        btn[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showPrincipalesView, selección boton MULTIPLECUENTA.");
                                cancelarCountDown();
                                tipoCuenta = cuentas[j];
                                InitTrans.tkn98.setCuentaSeleccionada(tipoCuenta);
                                typeAcc = tipoCuenta;
                                inputContent = tipoCuenta;
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        });
                        btn[i].setAllCaps(false);
                        linearLayout.addView(btn[i]);
                    }

                }
            });
        } else if (title == "DOCUMENTO") {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.vista_ingreso_documento);
                    TextView tv1 = findViewById(R.id.tv1);
                    KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                    keyboardUtil.enable();
                    tv1.setText("Ingresa los datos del depósito");
                    tv1.setTypeface(tipoFuente1);
                    final EditText etDocu = findViewById(R.id.et1);
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showPrincipalesView, DOCUMENTO.");
                    temporizador(InitTrans.time, etDocu.getWindowToken());
                    TextView tvTipo = findViewById(R.id.tvTipo);
                    TextView tvDocu = findViewById(R.id.tvDocu);
                    tvTipo.setTypeface(tipoFuente3);
                    tvDocu.setTypeface(tipoFuente3);
                    Button btnCancel = findViewById(R.id.btnCancel);
                    Button btnAccept = findViewById(R.id.btnAccept);
                    final TextView txtError = findViewById(R.id.txtError);
                    btnAccept.setTypeface(tipoFuente3);
                    btnCancel.setTypeface(tipoFuente3);

                    final Spinner spinner = findViewById(R.id.spinner);
                    final String[] opciones = {getString(R.string.cedula), getString(R.string.ruc)};
                    spinner.setAdapter(new ArrayAdapter<String>(MasterControl.this,
                            R.layout.spinner_item_iden, opciones));

                    etDocu.setTypeface(tipoFuente3);
                    etDocu.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    touch(new View[]{etDocu});

                    /*etDocu.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                hideKeyBoard(etDocu.getWindowToken());
                            }
                            return false;
                        }
                    });*/

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            String name = opciones[arg2];
                            if (name.equalsIgnoreCase(getString(R.string.cedula))) {
                                InitTrans.wrlg.wrDataTxt("Reinicio timer, método showPrincipalesView, selección item spinner DOCUMENTO.");
                                reiniciarCountDown();

                                etDocu.setText("");
                                etDocu.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showPrincipalesView, selección item spinner DOCUMENTO.");
                                        reiniciarCountDown();
                                    }
                                });
                                int maxLengthofEditText = 10;
                                etDocu.setFilters(new InputFilter[]{new
                                        InputFilter.LengthFilter(maxLengthofEditText)});
                                listenerValidadorDeCedulas(etDocu, txtError, maxLengthofEditText);
                            }
                            if (name.equalsIgnoreCase("Ruc")) {
                                InitTrans.wrlg.wrDataTxt("Reinicio timer, método showPrincipalesView, selección item spinner DOCUMENTO.");
                                reiniciarCountDown();
                                int maxLengthofEditText = 13;
                                etDocu.setFilters(new InputFilter[]{new
                                        InputFilter.LengthFilter(maxLengthofEditText)});
                                listenerValidadorDeCedulas(etDocu, txtError, maxLengthofEditText);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showPrincipalesView, botón cancelar DOCUMENTO.");
                            cancelarCountDown();
                            hideKeyBoard(etDocu.getWindowToken());
                            listener.cancel();

                        }
                    });

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método showPrincipalesView, botón aceptar DOCUMENTO.");
                            reiniciarCountDown();
                            hideKeyBoard(etDocu.getWindowToken());
                            boolean doc = false;
                            final EditText etDocu = findViewById(R.id.et1);
                            String docEt = etDocu.getText().toString();
                            String selec = spinner.getSelectedItem().toString();
                            if (selec.equals(getString(R.string.cedula))) {
                                if (docEt.length() == 0 || docEt.length() < 10) {
                                    changeToBackgroundError(etDocu, txtError, "Escriba el documento primero");
                                    //changeToBackgroundError(etDocu,txtError,"Escriba el documento primero",R.drawable.edittext_pichi_normal);
                                    return;
                                } else {
                                    Global.documento = etDocu.getText().toString();
                                    doc = TransTools.ValidaCedulaRuc(Global.documento);
                                }
                            } else if (selec.equals("Ruc")) {
                                if (docEt.length() == 0 || docEt.length() < 13) {
                                    changeToBackgroundError(etDocu, txtError, "Escriba el documento primero");
                                    //changeToBackgroundError(etDocu,txtError,"Escriba el documento primero",R.drawable.edittext_pichi_normal);
                                    return;
                                } else if (etDocu.getText().toString().length() == 13) {
                                    Global.documento = etDocu.getText().toString().substring(0, 10);
                                    doc = TransTools.ValidaCedulaRuc(Global.documento);
                                }
                            }

                            if (doc == false) {
                                changeToBackgroundError(etDocu, txtError, "Documento no válido");
                                //changeToBackgroundError(etDocu,txtError,"Documento no válido",R.drawable.edittext_pichi_normal);
                            } else {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showPrincipalesView, DOCUMENTO.");
                                cancelarCountDown();
                                inputContent = Global.documento;
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        }
                    });

                }
            });

        }
    }

    @Override
    public void showBotonesView(final int cantBtn, final String[] btnTitulo, final String title, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_buttons_banner2);
                setToolbarBack(MainMenuPrincipal.class);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setText(title);
                tv1.setTypeface(tipoFuente1);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showBotonesView.");
                temporizador(InitTrans.time);
                okTempo = true;
                obtenerRecibo();
                InitTrans.ultimoReciboSecuencial = SaveData.getInstance().getUltimaTrx();
                LinearLayout linearLayout = findViewById(R.id.linearBotones);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                        , 120);
                lp.setMargins(0, 10, 0, 0);

                int i;
                Button btn[] = new Button[cantBtn];
                for (i = 0; i < cantBtn; i++) {
                    btn[i] = new Button(MasterControl.this);
                    btn[i].setBackgroundResource(R.drawable.pichi_btn_menu);
                    btn[i].setLayoutParams(lp);
                    btn[i].setText(btnTitulo[i]);
                    btn[i].setTextColor(Color.parseColor("#0F265C"));
                    btn[i].setTypeface(tipoFuente3);
                    btn[i].setTextSize(22);
                    final int j = i;
                    btn[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showBotonesView, selección item botón.");
                            cancelarCountDown();
                            inputContent = Integer.toString(j);
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                    btn[i].setAllCaps(false);
                    linearLayout.addView(btn[i]);
                }
            }
        });

    }


    @Override
    public void showConsultasView(int timeout, String title, final long costo, OnUserResultListener l) {
        noBack = false;
        if (title.equals("COSTOSERVICIO")) {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.trans_vista_msg_confirmacion);
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showConsultasView.");
                    temporizador(InitTrans.time);
                    okTempo = true;
                    setToolbar();
                    btnCancel = findViewById(R.id.btCancel);
                    btnConfirm = findViewById(R.id.btAceptar);
                    btnConfirm.setTypeface(tipoFuente3);
                    btnCancel.setTypeface(tipoFuente3);
                    TextView tv1 = findViewById(R.id.tv1);
                    tv1.setText("Consulta");
                    tv1.setTypeface(tipoFuente1);
                    TextView ms1 = findViewById(R.id.ms1);
                    TextView ms2 = findViewById(R.id.ms2);
                    TextView ms3 = findViewById(R.id.ms3);
                    TextView ms4 = findViewById(R.id.ms4);

                    ms1.setText("");
                    ms2.setText("Costo servicio:       $" + PAYUtils.getStrAmount(costo));
                    ms2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ms3.setText("");
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showConsultasView, btnConfirm.");
                            cancelarCountDown();
                            inputContent = "OK";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showConsultasView, btnCancel.");
                            cancelarCountDown();
                            listener.cancel();
                        }
                    });
                }
            });
        }
        if (title.equals("COSTOSERVICIOBIMO")) {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.trans_vista_msg_confirmacion);
                    InitTrans.wrlg.wrDataTxt("Inicio timer, método showConsultasView, COSTOSERVICIOBIMO.");
                    temporizador(InitTrans.time);
                    setToolbar();
                    btnCancel = findViewById(R.id.btCancel);
                    btnConfirm = findViewById(R.id.btAceptar);
                    btnConfirm.setTypeface(tipoFuente3);
                    btnCancel.setTypeface(tipoFuente3);
                    TextView tv1 = findViewById(R.id.tv1);
                    tv1.setText(getString(R.string.consulta));
                    tv1.setTypeface(tipoFuente1);
                    TextView ms1 = findViewById(R.id.ms1);
                    TextView ms2 = findViewById(R.id.ms2);
                    TextView ms3 = findViewById(R.id.ms3);
                    TextView ms4 = findViewById(R.id.ms4);

                    ms1.setText("");
                    ms2.setText("Costo servicio:       $" + PAYUtils.getStrAmount(costo));
                    ms2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ms3.setText("");
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showConsultasView, btnConfirm COSTOSERVICIOBIMO.");
                            cancelarCountDown();
                            inputContent = "OK";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showConsultasView, btnCancel COSTOSERVICIOBIMO.");
                            cancelarCountDown();
                            listener.cancel();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void showRecaudacionesView(int timeout, String title, OnUserResultListener l) {
        noBack = false;
        if (title.equals("ingresoContrapartida")) {
            this.listener = l;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.vista_ingreso_1text);
                    KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                    keyboardUtil.enable();
                    setToolbar();
                    final EditText et_num = findViewById(R.id.et1);
                    Button btnCancel = findViewById(R.id.btnCancel);
                    Button btnAccept = findViewById(R.id.btnAccept);
                    btnCancel.setTypeface(tipoFuente3);
                    btnAccept.setTypeface(tipoFuente3);

                    if (tipoTrans.equals("RecargaOperadores")) {
                        TextView tv1 = findViewById(R.id.tv1);
                        tv1.setTypeface(tipoFuente1);
                        tv1.setText(tipoCuenta);

                        TextView tv_msg = findViewById(R.id.tvMSG);
                        tv_msg.setText(getString(R.string.numeroTelefonico2P));

                        et_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (tipoTrans.equals("RecargaTVPre")) {
                        TextView tv1 = findViewById(R.id.tv1);
                        tv1.setText(tipoCuenta);

                        TextView tv_msg = findViewById(R.id.tvMSG);
                        tv_msg.setText("Número smartcard: ");

                        et_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                        et_num.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            hideKeyBoard(et_num.getWindowToken());
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyBoard(et_num.getWindowToken());
                            listener.cancel();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void showEmpresasPrediosView(int timeout, final String[] mensaje, final int maxLeng[], final int inputType, final int carcRequeridos, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                int i;
                setContentView(R.layout.vista_ingreso_empre_publi);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showEmpresasPrediosView.");
                temporizador(InitTrans.time);
                setToolbar();
                final EditText et_num1 = findViewById(R.id.et1);
                final EditText et_num2 = findViewById(R.id.et2);
                final EditText et_num3 = findViewById(R.id.et3);
                final EditText et_num4 = findViewById(R.id.et4);
                final EditText et_num5 = findViewById(R.id.et5);
                final EditText et_num6 = findViewById(R.id.et6);
                final EditText et_num7 = findViewById(R.id.et7);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(getString(R.string.ingresoCortapartida));
                TextView tv_msg1 = findViewById(R.id.tvMSG1);
                TextView tv_msg2 = findViewById(R.id.tvMSG2);
                TextView tv_msg3 = findViewById(R.id.tvMSG3);
                TextView tv_msg4 = findViewById(R.id.tvMSG4);
                TextView tv_msg5 = findViewById(R.id.tvMSG5);
                TextView tv_msg6 = findViewById(R.id.tvMSG6);
                TextView tv_msg7 = findViewById(R.id.tvMSG7);
                TextView[] textViews = {tv_msg1, tv_msg2, tv_msg3, tv_msg4, tv_msg5, tv_msg6, tv_msg7};
                final EditText[] editTexts = {et_num1, et_num2, et_num3, et_num4, et_num5, et_num6, et_num7};
                for (i = 0; i < mensaje.length; i++) {
                    textViews[i].setText(mensaje[i]);
                    editTexts[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng[i])});
                    /*editTexts[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reiniciarCountDown();
                        }
                    });*/
                    editTexts[i].setInputType(inputType);
                }

                View[] views = editTexts;
                touch(views);

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    int i;
                    String[] mensajes = new String[8];
                    String contenido = "";

                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showEmpresasPrediosView, btnAccept.");
                        reiniciarCountDown();
                        for (int i = 0; i < mensaje.length; i++) {
                            if (editTexts[i].getText().toString().equals("")) {
                                Toast.makeText(MasterControl.this, "Campo vacío", Toast.LENGTH_SHORT).show();
                                contenido = "";
                                return;
                            } else {
                                if (editTexts[i].getText().toString().length() < maxLeng[i]) {
                                    mensajes[i] = ISOUtil.padleft(editTexts[i].getText().toString(), maxLeng[i], '0');
                                    contenido += mensajes[i];
                                } else {
                                    contenido += editTexts[i].getText();
                                }
                            }
                        }
                        inputContent = contenido;
                        if (!inputContent.equals("")) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showEmpresasPrediosView, btnAccept.");
                            cancelarCountDown();
                            hideKeyBoard(editTexts[i].getWindowToken());
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showEmpresasPrediosView, btnCancel.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showBonoDesarrollo(final int timeout, final String titulo, final String[] mensaje, final int[] maxLeng, final int inputType, int carcRequeridos, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                int i;
                setContentView(R.layout.vista_ingreso_documento_scan);
                final KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showBonoDesarrollo.");

                setToolbar();
                final EditText etCedula = findViewById(R.id.etCedula);
                final EditText etCodigo = findViewById(R.id.etCodigo);
                final EditText etFecha = findViewById(R.id.etFecha);
                etCedula.setTypeface(tipoFuente3);
                etCodigo.setTypeface(tipoFuente3);
                etFecha.setTypeface(tipoFuente3);
                final Button btnScan = findViewById(R.id.btnScan);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnScan.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);

                temporizador(InitTrans.time,etCodigo.getWindowToken());
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                TextView tv_msg1 = findViewById(R.id.tvMsgCedula);
                TextView tv_msg2 = findViewById(R.id.tvMsgCodigo);
                TextView tv_msg3 = findViewById(R.id.tvMsgFecha);
                tv_msg1.setTypeface(tipoFuente3);
                tv_msg2.setTypeface(tipoFuente3);
                tv_msg3.setTypeface(tipoFuente3);
                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                final TextView txtError3 = findViewById(R.id.txtError3);
                ImageButton button_date = findViewById(R.id.button_fecha);
                TextView[] textViews = {tv_msg1, tv_msg2, tv_msg3};
                final EditText[] editTexts = {etCedula, etCodigo};
                etCedula.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng[0])});
                etCodigo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng[1])});
                etFecha.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng[2])});
                for (i = 0; i < mensaje.length; i++) {
                    textViews[i].setText(mensaje[i]);
                }

                listenerValidadorDeCedulaXperta(etCedula, txtError1, 10, titulo);

                etCodigo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, etCodigo.setOnClickListener.");
                        reiniciarCountDown();
                        etCodigo.setFocusable(true);
                        etCodigo.setFocusableInTouchMode(true);
                        etCodigo.setCursorVisible(true);
                    }
                });

                etCodigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View view, boolean b) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, etCodigo.setOnFocusChangeListener.");
                        reiniciarCountDown();
                        etCodigo.setFocusable(true);
                        etCodigo.setCursorVisible(true);
                        if (b) {
                            final Dialog dialog = new Dialog(MasterControl.this);
                            dialog.setContentView(R.layout.custom_img_dialog);
                            Button dialogButtonAceptar = (Button) dialog.findViewById(R.id.btn_aceptar);
                            Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                            ImageView image = (ImageView) dialog.findViewById(R.id.img);
                            Bitmap img = PAYUtils.getImageFromAssetsFile(getApplicationContext(), "img/codigo_dactilar.png");
                            image.setImageBitmap(img);
                            dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    showKeyBoard();
                                }
                            });
                            dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideKeyBoard(etCodigo.getWindowToken());
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }
                });

                etCodigo.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, etCodigo.setOnKeyListener.");
                            reiniciarCountDown();
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                hideKeyBoard(etCedula.getWindowToken());
                                hideKeyBoard(etCodigo.getWindowToken());

                                final Dialog dialog = new Dialog(MasterControl.this);
                                dialog.setContentView(R.layout.custom_img_dialog);
                                Button dialogButtonAceptar = (Button) dialog.findViewById(R.id.btn_aceptar);
                                Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                                ImageView image = (ImageView) dialog.findViewById(R.id.img);
                                Bitmap img = PAYUtils.getImageFromAssetsFile(getApplicationContext(), "img/fecha.png");
                                image.setImageBitmap(img);
                                dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        showDatePickerDialog(etFecha);
                                        if (!etCedula.getText().toString().equals("")) {
                                            etCedula.setFocusableInTouchMode(true);
                                            etCedula.setFocusable(true);
                                            etCedula.setCursorVisible(false);
                                        }
                                        if (!etCodigo.getText().toString().equals("")) {
                                            etCodigo.setFocusableInTouchMode(true);
                                            etCodigo.setFocusable(true);
                                            etCodigo.setCursorVisible(false);
                                        }
                                    }
                                });

                                dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                return true;
                            }
                        }
                        return false;
                    }
                });

                btnScan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, btnScan.setOnClickListener.");
                        reiniciarCountDown();
                        final Dialog dialog = new Dialog(MasterControl.this);
                        dialog.setContentView(R.layout.custom_img_dialog);
                        Button dialogButtonAceptar = (Button) dialog.findViewById(R.id.btn_aceptar);
                        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                        ImageView image = (ImageView) dialog.findViewById(R.id.img);
                        Bitmap img = PAYUtils.getImageFromAssetsFile(getApplicationContext(), "img/barras.png");
                        image.setImageBitmap(img);
                        txtError1.setText("");
                        dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                final ScannerManager manager = ScannerManager.getScannerInstance();
                                ScannerManager.isBackC = true;
                                manager.startScanner(new QRCListener() {
                                    @Override
                                    public void callback(QRCInfo info) {
                                        if (info.isResultFalg()) {
                                            String dataScan = info.getQrc();
                                            int revisarAsteriscos = dataScan.indexOf("*");
                                            if (revisarAsteriscos >= 0) {
                                                etCedula.setText("");
                                                btnScan.setClickable(false);
                                                btnScan.setFocusable(false);
                                                txtError1.setText("Digite manualmente la cedula");
                                            } else {
                                                if (ISOUtil.isNumerico(dataScan)) {
                                                    etCedula.setText(dataScan);
                                                } else {
                                                    txtError1.setText("Digite manualmente la cedula");
                                                }
                                            }
                                        } else {
                                            transUI.showMsgError(timeout, "Back scan code: \nScan fail result =  " + info.getErrno());
                                        }
                                    }
                                });
                            }
                        });
                        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, button_date.setOnClickListener.");
                        reiniciarCountDown();
                        hideKeyBoard(etCedula.getWindowToken());
                        hideKeyBoard(etCodigo.getWindowToken());

                        final Dialog dialog = new Dialog(MasterControl.this);
                        dialog.setContentView(R.layout.custom_img_dialog);
                        Button dialogButtonAceptar = (Button) dialog.findViewById(R.id.btn_aceptar);
                        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                        ImageView image = (ImageView) dialog.findViewById(R.id.img);
                        Bitmap img = PAYUtils.getImageFromAssetsFile(getApplicationContext(), "img/cedula_fecha.png");
                        image.setImageBitmap(img);
                        dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                showDatePickerDialog(etFecha);
                                if (!etCedula.getText().toString().equals("")) {
                                    etCedula.setFocusableInTouchMode(true);
                                    etCedula.setFocusable(true);
                                    etCedula.setCursorVisible(false);
                                }
                                if (!etCodigo.getText().toString().equals("")) {
                                    etCodigo.setFocusableInTouchMode(true);
                                    etCodigo.setFocusable(true);
                                    etCodigo.setCursorVisible(false);
                                }
                            }
                        });

                        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                etFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showBonoDesarrollo, etFecha.setOnClickListener.");
                        reiniciarCountDown();
                        hideKeyBoard(etCedula.getWindowToken());
                        hideKeyBoard(etCodigo.getWindowToken());

                        final Dialog dialog = new Dialog(MasterControl.this);
                        dialog.setContentView(R.layout.custom_img_dialog);
                        Button dialogButtonAceptar = (Button) dialog.findViewById(R.id.btn_aceptar);
                        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                        ImageView image = (ImageView) dialog.findViewById(R.id.img);
                        Bitmap img = PAYUtils.getImageFromAssetsFile(getApplicationContext(), "img/fecha.png");
                        image.setImageBitmap(img);
                        dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                showDatePickerDialog(etFecha);
                                if (!etCedula.getText().toString().equals("")) {
                                    etCedula.setFocusableInTouchMode(true);
                                    etCedula.setFocusable(true);
                                    etCedula.setCursorVisible(true);
                                }
                                if (!etCodigo.getText().toString().equals("")) {
                                    etCodigo.setFocusableInTouchMode(true);
                                    etCodigo.setFocusable(true);
                                    etCodigo.setCursorVisible(true);
                                }
                            }
                        });

                        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    int i;
                    String contenido = "";

                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showBonoDesarrollo, btnAccept.setOnClickListener.");
                        cancelarCountDown();
                        hideKeyBoard(editTexts[i].getWindowToken());
                        if (!etCedula.getText().toString().equals("")) {
                            if (TransTools.ValidaCedulaRuc(etCedula.getText().toString())) {
                                if (!etCodigo.getText().toString().equals("")) {
                                    if (etCodigo.getText().toString().length() >= 6 || etCodigo.getText().toString().length() <= 10) {
                                        if (!etFecha.getText().toString().equals("")) {
                                            contenido = etCedula.getText().toString() + "@" +
                                                    etCodigo.getText().toString() + "@" +
                                                    etFecha.getText().toString();
                                        } else {
                                            changeToBackgroundError(etFecha, txtError3, "Campo vacío", R.drawable.edittext_pichi_normal);
                                            contenido = "";
                                            etCedula.setFocusableInTouchMode(true);
                                            etCedula.setFocusable(true);
                                            etCedula.setCursorVisible(true);
                                            return;
                                        }
                                    } else {
                                        changeToBackgroundError(etCodigo, txtError2, "Confirme el código digitado", R.drawable.edittext_pichi_normal);
                                        contenido = "";
                                        etCedula.setFocusableInTouchMode(true);
                                        etCedula.setFocusable(true);
                                        etCedula.setCursorVisible(true);
                                        return;
                                    }
                                } else {
                                    changeToBackgroundError(etCodigo, txtError2, "Campo vacío", R.drawable.edittext_pichi_normal);
                                    contenido = "";
                                    etCedula.setFocusableInTouchMode(true);
                                    etCedula.setFocusable(true);
                                    etCedula.setCursorVisible(true);
                                    return;
                                }
                            } else {
                                changeToBackgroundError(etCedula, txtError1, "Documento inválido");
                                contenido = "";
                                etCedula.setFocusableInTouchMode(true);
                                etCedula.setFocusable(true);
                                etCedula.setCursorVisible(true);
                                return;
                            }
                        } else {
                            changeToBackgroundError(etCedula, txtError1, "Campo vacío");
                            contenido = "";
                            etCedula.setFocusableInTouchMode(true);
                            etCedula.setFocusable(true);
                            etCedula.setCursorVisible(true);
                            return;
                        }

                        inputContent = contenido;
                        if (!inputContent.equals("")) {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método showBonoDesarrollo, if (!inputContent.equals()).");
                            cancelarCountDown();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showBonoDesarrollo, btnCancel.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });


            }
        });
    }

    @Override
    public void showContrapartidaView(int timeout, final String mensaje, final int maxLeng, final int inputType, final int carcRequeridos, final String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_1text);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                final EditText et_num = findViewById(R.id.et1);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showContrapartidaView.");
                temporizador(InitTrans.time, et_num.getWindowToken());
                setToolbar();
                final TextView txtError = findViewById(R.id.txtError);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                TextView tv_msg = findViewById(R.id.tvMSG);
                tv_msg.setText(mensaje);
                et_num.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                et_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng)});
                et_num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showContrapartidaView, et_num.setOnClickListener.");
                        reiniciarCountDown();
                    }
                });
                et_num.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            hideKeyBoard(et_num.getWindowToken());
                        }
                        return false;
                    }
                });
                if (inputType == 1010) {
                    et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    listenerValidadorDeCedulas(et_num, txtError, carcRequeridos);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos)) {
                                if (TransTools.ValidaCedulaRuc(inputContent.substring(0, 10))) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    changeToBackgroundError(et_num, txtError, "Documento ingresado no válido");
                                    //et_num.setText("");
                                    //changeToBackgroundError(et_num,txtError,"Documento ingresado no válido");
                                }
                            } else {
                                changeToBackgroundError(et_num, txtError, "Ingresa un valor válido");
                            }
                        }
                    });
                } else if (inputType == 12) {
                    et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos, et_num, txtError)) {
                                if (Integer.parseInt(inputContent) < 13) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    //et_num.setText("");
                                    changeToBackgroundError(et_num, txtError, "Ingrese un mes válido", R.drawable.edittext_pichi_normal);
                                }
                            }
                        }
                    });
                } else {
                    et_num.setInputType(inputType);
                    if (inputType == 10) {
                        et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_num.addTextChangedListener(new TextWatcher() {
                            private boolean isChanged = false;

                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (isChanged) {
                                    return;
                                }
                                String str = editable.toString();
                                isChanged = true;
                                String cuttedStr = str;
                                for (int i = str.length() - 1; i >= 0; i--) {
                                    char c = str.charAt(i);
                                    if ('.' == c) {
                                        cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                        break;
                                    }
                                }
                                int NUM = cuttedStr.length();
                                int zeroIndex = -1;
                                for (int i = 0; i < NUM - 2; i++) {
                                    char c = cuttedStr.charAt(i);
                                    if (c != '0') {
                                        zeroIndex = i;
                                        break;
                                    } else if (i == NUM - 3) {
                                        zeroIndex = i;
                                        break;
                                    }
                                }
                                if (zeroIndex != -1) {
                                    cuttedStr = cuttedStr.substring(zeroIndex);
                                }
                                if (cuttedStr.length() < 3) {
                                    cuttedStr = "0" + cuttedStr;
                                }
                                cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                        + "." + cuttedStr.substring(cuttedStr.length() - 2);

                                et_num.setText(cuttedStr);
                                numeroTotal = Double.valueOf(amountServiceCost);
                                numeroTotal = numeroTotal + Double.valueOf(cuttedStr);
                                et_num.setSelection(et_num.length());
                                isChanged = false;
                            }
                        });
                    }
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos, et_num, txtError)) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                masterCDT.cancel();
                                hideKeyBoard(et_num.getWindowToken());
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        }
                    });
                }
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnCancel.");
                        cancelarCountDown();
                        //hideKeyBoard(et_num.getWindowToken());
                        listener.cancel();
                    }
                });
            }
        });
    }


    @Override
    public void showContrapartidaRecaudView(int timeout, final String mensaje, final int maxLeng, final int inputType, final int carcRequeridos, final String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_1text);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                final EditText et_num = findViewById(R.id.et1);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showContrapartidaView.");
                temporizador(InitTrans.time, et_num.getWindowToken());
                setToolbar();
                final TextView txtError = findViewById(R.id.txtError);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                TextView tv_msg = findViewById(R.id.tvMSG);
                tv_msg.setText(mensaje);
                et_num.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                et_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng)});
                et_num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showContrapartidaView, et_num.setOnClickListener.");
                        reiniciarCountDown();
                    }
                });
                et_num.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            hideKeyBoard(et_num.getWindowToken());
                        }
                        return false;
                    }
                });
                if (inputType == 1010) {
                    et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    listenerValidadorDeCedulasRecaud(et_num, txtError, carcRequeridos);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos)) {
                                if (TransTools.validadorDeCedula(inputContent.substring(0, 10))) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    changeToBackgroundError(et_num, txtError, "Documento ingresado no válido");
                                    //et_num.setText("");
                                    //changeToBackgroundError(et_num,txtError,"Documento ingresado no válido");
                                }
                            } else {
                                changeToBackgroundError(et_num, txtError, "Ingresa un valor válido");
                            }
                        }
                    });
                } else if (inputType == 12) {
                    et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos, et_num, txtError)) {
                                if (Integer.parseInt(inputContent) < 13) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    //et_num.setText("");
                                    changeToBackgroundError(et_num, txtError, "Ingrese un mes válido", R.drawable.edittext_pichi_normal);
                                }
                            }
                        }
                    });
                } else {
                    et_num.setInputType(inputType);
                    if (inputType == 10) {
                        et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_num.addTextChangedListener(new TextWatcher() {
                            private boolean isChanged = false;

                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (isChanged) {
                                    return;
                                }
                                String str = editable.toString();
                                isChanged = true;
                                String cuttedStr = str;
                                for (int i = str.length() - 1; i >= 0; i--) {
                                    char c = str.charAt(i);
                                    if ('.' == c) {
                                        cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                        break;
                                    }
                                }
                                int NUM = cuttedStr.length();
                                int zeroIndex = -1;
                                for (int i = 0; i < NUM - 2; i++) {
                                    char c = cuttedStr.charAt(i);
                                    if (c != '0') {
                                        zeroIndex = i;
                                        break;
                                    } else if (i == NUM - 3) {
                                        zeroIndex = i;
                                        break;
                                    }
                                }
                                if (zeroIndex != -1) {
                                    cuttedStr = cuttedStr.substring(zeroIndex);
                                }
                                if (cuttedStr.length() < 3) {
                                    cuttedStr = "0" + cuttedStr;
                                }
                                cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                        + "." + cuttedStr.substring(cuttedStr.length() - 2);

                                et_num.setText(cuttedStr);
                                numeroTotal = Double.valueOf(amountServiceCost);
                                numeroTotal = numeroTotal + Double.valueOf(cuttedStr);
                                et_num.setSelection(et_num.length());
                                isChanged = false;
                            }
                        });
                    }
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputContent = et_num.getText().toString();
                            if (validarContraPartida(inputContent, carcRequeridos, et_num, txtError)) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnAccept.setOnClickListener.");
                                masterCDT.cancel();
                                hideKeyBoard(et_num.getWindowToken());
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        }
                    });
                }
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showContrapartidaView, btnCancel.");
                        cancelarCountDown();
                        //hideKeyBoard(et_num.getWindowToken());
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showIngreso2EditTextView(final String[] mensajes, final int[] maxLengs, final int[] inputType, final int[] carcRequeridos, final String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_2text);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                setToolbar();
                final EditText et_1 = findViewById(R.id.et1);
                final EditText et_2 = findViewById(R.id.et2);
                et_1.requestFocus();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showIngreso2EditTextView.");
                temporizador(75000, getCurrentFocus().getWindowToken());
                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                TextView tvMsg1 = findViewById(R.id.tvMsg1);
                TextView tvMsg2 = findViewById(R.id.tvMsg2);
                tvMsg1.setTypeface(tipoFuente3);
                tvMsg1.setText(mensajes[0]);
                tvMsg2.setTypeface(tipoFuente3);
                tvMsg2.setText(mensajes[1]);
                et_1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengs[0])});
                et_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });
                et_2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengs[1])});
                et_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                et_1.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            et_1.clearFocus();
                            et_2.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_2, InputMethodManager.SHOW_IMPLICIT);
                        }
                        return false;
                    }
                });

                et_2.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            hideKeyBoard(et_2.getWindowToken());
                        }
                        return false;
                    }
                });

                if (inputType[0] == 1010) {
                    et_1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    listenerValidadorDeCedulas(et_1, txtError1, 10);
                } else {
                    et_1.setInputType(inputType[0]);
                }
                if (inputType[1] == 1010) {
                    et_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    listenerValidadorDeCedulas(et_2, txtError2, 10);
                } else {
                    et_2.setInputType(inputType[1]);
                }


                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dato1 = et_1.getText().toString();
                        String dato2 = et_2.getText().toString();
                        if (validarContraPartida(dato1, carcRequeridos[0], et_1, txtError1)) {
                            if (validarContraPartida(dato2, carcRequeridos[1], et_2, txtError2)) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showIngreso2EditTextView, btnAccept.");
                                masterCDT.cancel();
                                hideKeyBoard(et_2.getWindowToken());
                                inputContent = dato1 + "@" + dato2;
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            } else {
                                hideKeyBoard(et_2.getWindowToken());
                            }
                        } else {
                            hideKeyBoard(et_1.getWindowToken());
                        }
                    }
                });


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showIngreso2EditTextView, btnCancel.");
                        masterCDT.cancel();
                        hideKeyBoard(et_1.getWindowToken());
                        hideKeyBoard(et_2.getWindowToken());
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void show1Fecha1EditTextView(final String[] mensajes, final int maxLeng, final int inputType, final int carcRequeridos, final String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_1fecha_1text);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método show1Fecha1EditTextView.");
                temporizador(75000);
                setToolbar();
                final EditText et_num = findViewById(R.id.et2);
                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);
                TextView tvMsg1 = findViewById(R.id.tvMsg1);
                TextView tvMsg2 = findViewById(R.id.tvMsg2);
                tvMsg1.setTypeface(tipoFuente3);
                tvMsg1.setText(mensajes[0]);
                tvMsg2.setTypeface(tipoFuente3);
                tvMsg2.setText(mensajes[1]);
                et_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLeng)});
                et_num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });
                et_num.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            hideKeyBoard(et_num.getWindowToken());
                        }
                        return false;
                    }
                });
                final EditText etFecha = findViewById(R.id.etFecha);
                etFecha.setTypeface(tipoFuente3);
                ImageButton button_date = findViewById(R.id.button_fecha);
                etFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método show1Fecha1EditTextView, etFecha.");
                        reiniciarCountDown();
                        showDatePickerDialog(etFecha);
                    }
                });
                button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método show1Fecha1EditTextView, button_date.");
                        reiniciarCountDown();
                        showDatePickerDialog(etFecha);
                    }
                });
                if (inputType == 1010) {
                    et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dato1 = etFecha.getText().toString();
                            String dato2 = et_num.getText().toString();
                            if (validarContraPartida(dato2, carcRequeridos, et_num, txtError2)) {
                                if (TransTools.ValidaCedulaRuc(inputContent.substring(0, 10))) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método show1Fecha1EditTextView, btnAccept.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    inputContent = dato1 + "@" + dato2;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    et_num.setText("");
                                    changeToBackgroundError(et_num, txtError2, "Documento ingresado no válido", R.drawable.edittext_pichi_normal);
                                }
                            }
                        }
                    });
                } else {
                    et_num.setInputType(inputType);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dato1 = etFecha.getText().toString();
                            String dato2 = et_num.getText().toString();
                            if (validarContraPartida(dato1, carcRequeridos, etFecha, txtError1)) {
                                if (validarContraPartida(dato2, carcRequeridos, et_num, txtError2)) {
                                    InitTrans.wrlg.wrDataTxt("Fin timer, método show1Fecha1EditTextView, btnAccept.");
                                    masterCDT.cancel();
                                    hideKeyBoard(et_num.getWindowToken());
                                    inputContent = dato1 + "@" + dato2;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    hideKeyBoard(et_num.getWindowToken());
                                }
                            }
                        }
                    });
                }
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método show1Fecha1EditTextView, btnCancel.");
                        masterCDT.cancel();
                        hideKeyBoard(et_num.getWindowToken());
                        listener.cancel();
                    }
                });
            }
        });
    }

    private int currentEtMaxLength = 0;

    public void listenerValidadorDeCedulas(final EditText editText, final TextView textView, int maxLength) {
        currentEtMaxLength = maxLength;

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().length() == currentEtMaxLength) {
                    if (!TransTools.ValidaCedulaRuc(editText.getText().toString().substring(0, 10))) {
                        editText.setBackgroundResource(R.drawable.edittext_pichi_error);
                        textView.setText("Documento ingresado no válido");
                        //changeToBackgroundError(editText, textView, "Documento ingresado no válido", R.drawable.edittext_pichi_normal);
                        editText.setText("");
                    }
                }

                if (editText.getText().length() == 1) {
                    editText.setBackgroundResource(R.drawable.edittext_pichi_normal);
                    textView.setText("");
                }
            }
        });
    }

    public void listenerValidadorDeCedulasRecaud(final EditText editText, final TextView textView, int maxLength) {
        currentEtMaxLength = maxLength;

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().length() == currentEtMaxLength) {
                    if (!TransTools.validadorDeCedula(editText.getText().toString().substring(0, 10))) {
                        editText.setBackgroundResource(R.drawable.edittext_pichi_error);
                        textView.setText("Documento ingresado no válido");
                        //changeToBackgroundError(editText, textView, "Documento ingresado no válido", R.drawable.edittext_pichi_normal);
                        editText.setText("");
                    }
                }

                if (editText.getText().length() == 1) {
                    editText.setBackgroundResource(R.drawable.edittext_pichi_normal);
                    textView.setText("");
                }
            }
        });
    }

    @Override
    public void showCuentaYMontoDepositoView(int timeout, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_numcuenta_monto);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showCuentaYMontoDepositoView.");
                temporizador(150000);    // AUMENTO DE TIEMPO SOLICITADO POR BANCO - CR
                setToolbar();
                TextView tv1 = findViewById(R.id.tv1);
                TextView tvMSG1 = findViewById(R.id.tvMSG1);
                TextView tvMSG2 = findViewById(R.id.tvMSG2);
                TextView tvMSG3 = findViewById(R.id.tvMSG3);
                tv1.setTypeface(tipoFuente1);
                tvMSG1.setTypeface(tipoFuente3);
                tvMSG2.setTypeface(tipoFuente3);
                tvMSG3.setTypeface(tipoFuente3);
                btnCancel = findViewById(R.id.btnCancel);
                btnConfirm = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnConfirm.setTypeface(tipoFuente3);
                final EditText et1_cuenta = findViewById(R.id.et1);
                final EditText et2_cuenta = findViewById(R.id.et2);
                final TextView txtErrorCuenta = findViewById(R.id.txtErrorCuenta);
                final TextView txtErrorConfirma = findViewById(R.id.txtErrorConfirma);
                final TextView txtErrorMonto = findViewById(R.id.txtErrorMonto);
                et1_cuenta.setTypeface(tipoFuente3);
                et2_cuenta.setTypeface(tipoFuente3);
                final EditText etMonto = findViewById(R.id.etMonto);

                /*et1_cuenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });

                et2_cuenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reiniciarCountDown();
                    }
                });*/

                et1_cuenta.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et1_cuenta, InputMethodManager.SHOW_IMPLICIT);

                touch(new View[]{et2_cuenta, et1_cuenta, etMonto});

                et1_cuenta.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            et1_cuenta.clearFocus();
                            et2_cuenta.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et2_cuenta, InputMethodManager.SHOW_IMPLICIT);
                        }
                        return false;
                    }
                });

                et2_cuenta.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            et2_cuenta.clearFocus();
                            etMonto.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(etMonto, InputMethodManager.SHOW_IMPLICIT);
                        }
                        return false;
                    }
                });

                etMonto.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            hideKeyBoard(etMonto.getWindowToken());
                        }
                        return false;
                    }
                });

                etMonto.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                etMonto.setTypeface(tipoFuente3);

                etMonto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showCuentaYMontoDepositoView, etMonto.");
                        reiniciarCountDown();
                    }
                });

                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etMonto, InputMethodManager.SHOW_IMPLICIT);

                etMonto.addTextChangedListener(new TextWatcher() {
                    private boolean isChanged = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isChanged) {
                            return;
                        }
                        String str = editable.toString();
                        isChanged = true;
                        String cuttedStr = str;
                        for (int i = str.length() - 1; i >= 0; i--) {
                            char c = str.charAt(i);
                            if ('.' == c) {
                                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                break;
                            }
                        }
                        int NUM = cuttedStr.length();
                        int zeroIndex = -1;
                        for (int i = 0; i < NUM - 2; i++) {
                            char c = cuttedStr.charAt(i);
                            if (c != '0') {
                                zeroIndex = i;
                                break;
                            } else if (i == NUM - 3) {
                                zeroIndex = i;
                                break;
                            }
                        }
                        if (zeroIndex != -1) {
                            cuttedStr = cuttedStr.substring(zeroIndex);
                        }
                        if (cuttedStr.length() < 3) {
                            cuttedStr = "0" + cuttedStr;
                        }
                        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                + "." + cuttedStr.substring(cuttedStr.length() - 2);

                        etMonto.setText(cuttedStr);
                        etMonto.setSelection(etMonto.length());
                        isChanged = false;
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showCuentaYMontoDepositoView, btnConfirm.");
                        cancelarCountDown();
                        String ct1 = et1_cuenta.getText().toString();
                        String ct2 = et2_cuenta.getText().toString();
                        if (ct1.length() == 10) {
                            if (ct1.equals(ct2)) {
                                if (validarMonto(etMonto, txtErrorMonto)) {
                                    String monto = etMonto.getText().toString().replace(".", "");
                                    hideKeyBoard(etMonto.getWindowToken());
                                    inputContent = ct1 + "@" + monto;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                }
                            } else {
                                changeToBackgroundError(et2_cuenta, txtErrorConfirma, "No coinciden", R.drawable.edittext_pichi_normal);
                            }
                        } else {
                            changeToBackgroundError(et1_cuenta, txtErrorCuenta, "Mínimo 10 dígitos", R.drawable.edittext_pichi_normal);
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showCuentaYMontoDepositoView, btnCancel.");
                        cancelarCountDown();
                        listener.cancel();
                    }
                });

            }
        });
    }

    @Override
    public void showFechaHoraView(int timeout, String titulo, final boolean flag, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_fecha_hora);

                final TextView tv1 = findViewById(R.id.tv1);
                tv1.setText("Ingresa datos de informe");
                tv1.setTypeface(tipoFuente1);
                InitTrans.wrlg.wrDataTxt("Fin timer, método showFechaHoraView.");
                temporizador(InitTrans.time);

                TextView tvFecha = findViewById(R.id.tvFecha);
                TextView tvHora = findViewById(R.id.tvHora);
                tvFecha.setTypeface(tipoFuente3);
                tvHora.setTypeface(tipoFuente3);
                if (flag) {
                    tvFecha.setText("Fecha inicial");
                    tvHora.setText("Hora inicial");
                } else {
                    tvFecha.setText("Fecha final");
                    tvHora.setText("Hora final");
                }

                final EditText etFecha = findViewById(R.id.etFecha);
                etFecha.setTypeface(tipoFuente3);
                ImageButton button_date = findViewById(R.id.button_fecha);
                etFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaHoraView, etFecha.");
                        reiniciarCountDown();
                        showDatePickerDialog(etFecha);
                    }
                });
                button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaHoraView, button_date.");
                        reiniciarCountDown();
                        showDatePickerDialog(etFecha);
                    }
                });
                final EditText etHora = findViewById(R.id.etHora);
                etHora.setTypeface(tipoFuente3);
                ImageButton button_Hora = findViewById(R.id.button_hora);
                etHora.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaHoraView, etHora.");
                        reiniciarCountDown();
                        showTimePickerDialog(etHora);
                    }
                });
                button_Hora.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaHoraView, button_Hora.");
                        reiniciarCountDown();
                        showTimePickerDialog(etHora);
                    }
                });
                Button btnAceptar = findViewById(R.id.btnAceptar);
                Button btnCancelar = findViewById(R.id.btnCancelar);
                btnAceptar.setTypeface(tipoFuente3);
                btnCancelar.setTypeface(tipoFuente3);
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showFechaHoraView, btnAceptar.");
                        masterCDT.cancel();
                        fecha = etFecha.getText().toString();
                        hora = etHora.getText().toString();
                        if (!fecha.equals("") || !hora.equals("")) {
                            inputContent = fecha + hora;
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            Toast.makeText(MasterControl.this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showFechaHoraView, btnCancelar.");
                        masterCDT.cancel();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showFechaView(int timeout, String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_fecha);

                final TextView tv1 = findViewById(R.id.tv1);
                tv1.setText("Ingresa datos de informe");
                tv1.setTypeface(tipoFuente1);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método showFechaView.");
                temporizador(InitTrans.time);

                TextView tvFecha = findViewById(R.id.tvFecha);
                tvFecha.setTypeface(tipoFuente3);

                final EditText etFecha = findViewById(R.id.etFecha);
                etFecha.setTypeface(tipoFuente3);
                ImageButton button_date = findViewById(R.id.button_fecha);
                etFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaView, etFecha.");
                        reiniciarCountDown();
                        showDatePickerDialogMsg(etFecha);
                    }
                });
                button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showFechaView, button_date.");
                        reiniciarCountDown();
                        showDatePickerDialogMsg(etFecha);
                    }
                });
                Button btnAceptar = findViewById(R.id.btnAceptar);
                Button btnCancelar = findViewById(R.id.btnCancelar);
                btnAceptar.setTypeface(tipoFuente3);
                btnCancelar.setTypeface(tipoFuente3);
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showFechaView, btnAceptar.");
                        masterCDT.cancel();
                        fecha = etFecha.getText().toString();
                        inputContent = fecha;
                        listener.confirm(InputManager.Style.COMMONINPUT);

                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showFechaView, btnCancelar.");
                        masterCDT.cancel();
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showIngresoDocumentoView(int timeout, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_ingreso_documento);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                TextView tv1 = findViewById(R.id.tv1);
                tv1.setTypeface(tipoFuente1);
                TextView tvTipo = findViewById(R.id.tvTipo);
                TextView tvDocu = findViewById(R.id.tvDocu);
                final TextView txtError = findViewById(R.id.txtError);
                tvTipo.setTypeface(tipoFuente3);
                tvDocu.setTypeface(tipoFuente3);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);

                btnAccept.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);

                final Spinner spinner = findViewById(R.id.spinner);
                final String[] opciones = {getString(R.string.cedula), getString(R.string.ruc)};
                spinner.setAdapter(new ArrayAdapter<String>(MasterControl.this,
                        R.layout.spinner_item_iden, opciones));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String name = opciones[arg2];

                        if (name.equalsIgnoreCase(getString(R.string.cedula))) {
                            EditText etDocu = findViewById(R.id.et1);
                            etDocu.setText("");
                            int maxLengthofEditText = 10;
                            etDocu.setFilters(new InputFilter[]{new
                                    InputFilter.LengthFilter(maxLengthofEditText)});
                            listenerValidadorDeCedulas(etDocu, txtError, maxLengthofEditText);
                        }
                        if (name.equalsIgnoreCase(getString(R.string.ruc))) {
                            EditText etDocu = findViewById(R.id.et1);
                            int maxLengthofEditText = 13;
                            etDocu.setFilters(new InputFilter[]{new
                                    InputFilter.LengthFilter(maxLengthofEditText)});
                            listenerValidadorDeCedulas(etDocu, txtError, maxLengthofEditText);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();

                    }
                });


                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean doc = false;
                        final EditText etDocu = findViewById(R.id.et1);
                        String docEt = etDocu.getText().toString();
                        String selec = spinner.getSelectedItem().toString();
                        if (selec.equals(getString(R.string.cedula))) {
                            if (docEt.length() == 0) {
                                changeToBackgroundError(etDocu, txtError, getString(R.string.escribaDocuementoPrimero));
                                //changeToBackgroundError(etDocu,txtError,getString(R.string.escribaDocuementoPrimero),R.drawable.edittext_pichi_normal);
                                return;
                            } else {
                                Global.documento = etDocu.getText().toString();
                                doc = TransTools.ValidaCedulaRuc(Global.documento);
                            }
                        } else if (selec.equals(getString(R.string.ruc))) {
                            if (docEt.length() == 0) {
                                changeToBackgroundError(etDocu, txtError, getString(R.string.escribaDocuementoPrimero));
                                //changeToBackgroundError(etDocu,txtError,getString(R.string.escribaDocuementoPrimero),R.drawable.edittext_pichi_normal);
                                return;
                            } else if (etDocu.getText().toString().length() == 13) {
                                Global.documento = etDocu.getText().toString().substring(0, 10);
                                doc = TransTools.ValidaCedulaRuc(Global.documento);
                            }
                        }

                        if (doc == false) {
                            changeToBackgroundError(etDocu, txtError, "Documento no válido");
                            //changeToBackgroundError(etDocu,txtError,"Documento no válido",R.drawable.edittext_pichi_normal);
                        } else {
                            inputContent = Global.documento;
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void showListView(int timeout, final String title, final String[] menu, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.trans_show_list);
                TextView textView_titulo_lista = findViewById(R.id.textView_titulo_lista);
                textView_titulo_lista.setTypeface(tipoFuente1);
                textView_titulo_lista.setText(title);
                initList(menu);
            }
        });

    }

    @Override
    public void showListCierreView(int timeout, final String title, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.vista_listview_banner2);

                TextView tv1, tv2;
                ListView listUsers;
                ArrayList<String> listaInfo = null;
                final ArrayList<Usuario> listaUsuario;

                tv1 = findViewById(R.id.tv1);
                tv2 = findViewById(R.id.tv2);

                tv1.setTypeface(tipoFuente1);
                tv1.setText("Cierre parcial");
                tv2.setText("Selecciona usuario");

                listUsers = findViewById(R.id.lv1);
                listaUsuario = conexion.readUserList();

                InitTrans.wrlg.wrDataTxt("Inicio timer, método showListCierreView.");
                temporizador(InitTrans.time);

                if (listaUsuario != null) {
                    listaInfo = new ArrayList<>();

                    for (int i = 0; i < listaUsuario.size(); i++) {
                        listaInfo.add("\n" + "Usuario: " + listaUsuario.get(i).getUser() + "\n" +
                                "Rol: " + listaUsuario.get(i).getRole() + "\n" +
                                "Estado: " + listaUsuario.get(i).getEstado() + "\n" +
                                "Intentos: " + listaUsuario.get(i).getIntento() + "\n" +
                                "Fecha creación: " + listaUsuario.get(i).getFecha() + "\n" +
                                "Fecha último cierre " + listaUsuario.get(i).getFechaCierre() + "\n");
                    }
                } else {
                    Toast.makeText(MasterControl.this, "No fue posible realizar consultas", Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter adapter = new ArrayAdapter(MasterControl.this, android.R.layout.simple_list_item_1, listaInfo);
                listUsers.setAdapter(adapter);

                listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        reiniciarCountDown();
                        final String user = listaUsuario.get(i).getUser();
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                                MasterControl.this, R.style.Theme_AppCompat_Light_Dialog));

                        builder.setTitle("Advertencia");
                        builder.setMessage("¿Deseas realizar cierre para el usuario: " + user + "?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showListCierreView, builder.setPositiveButton.");
                                cancelarCountDown();
                                inputContent = user;
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InitTrans.wrlg.wrDataTxt("Fin timer, método showListCierreView, builder.setNegativeButton.");
                                reiniciarCountDown();
                                dialogInterface.cancel();
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();

                    }
                });


            }
        });

    }

    @Override
    public void show_input_text_date_view(int timeout, final String title, final String mensaje, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noBack = false;
                setContentView(R.layout.input_text_date);
                InitTrans.wrlg.wrDataTxt("Inicio timer, método show_input_text_date_view.");
                temporizador(InitTrans.time);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                Button btnCancelCH, btnAcceptCH;
                TextView tvTittleCH, tvMensajeCH, tvFECHA;
                final EditText editText_inputCH;

                final EditText etDate = findViewById(R.id.etFecha);
                final TextView txtError = findViewById(R.id.txtError);
                editText_inputCH = findViewById(R.id.editText_inputCH);
                tvTittleCH = findViewById(R.id.tv1);
                tvMensajeCH = findViewById(R.id.tvMSG);
                tvFECHA = findViewById(R.id.tvFECHA);
                btnAcceptCH = findViewById(R.id.btnAcceptCH);
                btnCancelCH = findViewById(R.id.btnCancelCH);

                btnCancelCH.setTypeface(tipoFuente3);
                btnAcceptCH.setTypeface(tipoFuente3);

                tvTittleCH.setTypeface(tipoFuente1);
                tvTittleCH.setText(title);
                tvMensajeCH.setTypeface(tipoFuente3);
                tvMensajeCH.setText(mensaje);
                tvFECHA.setTypeface(tipoFuente3);

                ImageButton button_date = findViewById(R.id.button_fecha);
                button_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método show_input_text_date_view, button_date.");
                        reiniciarCountDown();
                        showDatePickerDialog(etDate);
                    }
                });

                btnAcceptCH.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText_inputCH.getText().toString().equals("") || etDate.getText().toString().equals("")) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer, método show_input_text_date_view, btnAcceptCH.");
                            reiniciarCountDown();
                            changeToBackgroundError(editText_inputCH, txtError, "Debe completar todos los campos", R.drawable.edittext_pichi_normal);
                        } else {
                            InitTrans.wrlg.wrDataTxt("Fin timer, método show_input_text_date_view, btnAcceptCH.");
                            masterCDT.cancel();
                            inputContent = editText_inputCH.getText().toString().trim() + ";" + etDate.getText().toString().trim();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(editText_inputCH.getWindowToken(), 0);
                        }
                    }
                });

                btnCancelCH.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método show_input_text_date_view, btnCancelCH.");
                        masterCDT.cancel();
                        listener.cancel();
                    }
                });
            }
        });

    }

    @Override
    public void showDatosDireccion(int timeout, final String titulo, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_datos_direccion);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                setToolbar();
                temporizador(InitTrans.time);
                TextView tv1 = findViewById(R.id.tv1);
                TextView tvMSG1 = findViewById(R.id.tvMSG1);
                TextView tvMSG2 = findViewById(R.id.tvMSG2);
                TextView tvMSG3 = findViewById(R.id.tvMSG3);
                tv1.setTypeface(tipoFuente1);
                tvMSG1.setTypeface(tipoFuente3);
                tvMSG2.setTypeface(tipoFuente3);
                tvMSG3.setTypeface(tipoFuente3);
                btnCancel = findViewById(R.id.btnCancel);
                btnConfirm = findViewById(R.id.btnAccept);
                btnCancel.setTypeface(tipoFuente3);
                btnConfirm.setTypeface(tipoFuente3);
                final EditText et1_cuenta = findViewById(R.id.et1);
                final EditText et2_cuenta = findViewById(R.id.et2);
                final EditText edtCalle = findViewById(R.id.etMonto);
                final TextView txtError1 = findViewById(R.id.txtErrorCuenta);
                final TextView txtError2 = findViewById(R.id.txtErrorConfirma);
                final TextView txtError3 = findViewById(R.id.txtError3);
                et1_cuenta.setTypeface(tipoFuente3);
                et2_cuenta.setTypeface(tipoFuente3);
                edtCalle.setTypeface(tipoFuente3);
                et1_cuenta.setInputType(InputType.TYPE_CLASS_TEXT);
                et2_cuenta.setInputType(InputType.TYPE_CLASS_TEXT);
                edtCalle.setInputType(InputType.TYPE_CLASS_TEXT);
                final EditText etMonto = findViewById(R.id.etMonto);
                
                et1_cuenta.setFilters(new InputFilter[] { new InputFilter() {
                    @Override public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd)
                    { if(cs.toString().matches("[a-zA-Z0-9 ]+")){ return cs; } return ""; } } });

                edtCalle.setFilters(new InputFilter[] { new InputFilter() {
                    @Override public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd)
                    { if(cs.toString().matches("[a-zA-Z0-9 ]+")){ return cs; } return ""; } } });


                //UIUtils.validacionCaracterEspecial(et1_cuenta);
                //UIUtils.validacionCaracterEspecial(edtCalle);

                tv1.setTypeface(tipoFuente1);
                tv1.setText(titulo);

                tvMSG1.setText(mensajes[0]);
                tvMSG2.setText(mensajes[1]);
                tvMSG3.setText(mensajes[2]);

                View[] editTexts = new View[]{et1_cuenta, et2_cuenta, edtCalle};
                touch(editTexts);

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyBoard(et1_cuenta.getWindowToken());
                        hideKeyBoard(et2_cuenta.getWindowToken());
                        hideKeyBoard(edtCalle.getWindowToken());

                        if (!et1_cuenta.getText().toString().equals("")) {
                            if (validarCaracterNoPermitido(et1_cuenta, txtError1, "~")) {
                                if (!et2_cuenta.getText().toString().equals("")) {
                                    if (validarCaracterNoPermitido(et2_cuenta, txtError2, "~")) {
                                        if (!edtCalle.getText().toString().equals("")) {
                                            if (validarCaracterNoPermitido(edtCalle, txtError3, "~")) {
                                                cancelarCountDown();
                                                inputContent = et1_cuenta.getText().toString() + "~" +
                                                        et2_cuenta.getText().toString() + "~" +
                                                        edtCalle.getText().toString();
                                                listener.confirm(InputManager.Style.COMMONINPUT);
                                            } else {
                                                reiniciarCountDown();
                                                inputContent = "";
                                                return;
                                            }
                                        } else {
                                            reiniciarCountDown();
                                            changeToBackgroundError(edtCalle, txtError3, "Campo vacío", R.drawable.edittext_pichi_normal);
                                            edtCalle.requestFocus();
                                            inputContent = "";
                                            return;
                                        }
                                    } else {
                                        reiniciarCountDown();
                                        inputContent = "";
                                        return;
                                    }
                                } else {
                                    reiniciarCountDown();
                                    changeToBackgroundError(et2_cuenta, txtError2, "Campo vacío", R.drawable.edittext_pichi_normal);
                                    et2_cuenta.requestFocus();
                                    inputContent = "";
                                    return;
                                }
                            } else {
                                reiniciarCountDown();
                                inputContent = "";
                                return;
                            }
                        } else {
                            reiniciarCountDown();
                            changeToBackgroundError(et1_cuenta, txtError1, "Campo vacío", R.drawable.edittext_pichi_normal);
                            et1_cuenta.requestFocus();
                            inputContent = "";
                            return;
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelarCountDown();
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }



    @Override
    public void showDatosComplementarios(int timeout, final String titulo, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.datos_complementarios);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();
                setToolbar();
                temporizador(InitTrans.time*3);
                TextView tv1 = findViewById(R.id.tv1);
                final TextView tvSector = findViewById(R.id.tvSector);
                final TextView tvActividad = findViewById(R.id.tvActividad);
                final TextView tvIngreso = findViewById(R.id.tvIngreso);
                final TextView tvSituación = findViewById(R.id.tvSituacion);
                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                final TextView txtError3 = findViewById(R.id.txtError3);
                final TextView txtError4 = findViewById(R.id.txtError4);
                final Spinner spinnerSector = findViewById(R.id.spinnerSector);
                final Spinner spinnerActividad = findViewById(R.id.spinnerActividad);
                final EditText etIngreso = findViewById(R.id.etIngreso);
                final Spinner spinnerSituacion = findViewById(R.id.spinnerSituacion);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                tv1.setTypeface(tipoFuente1);
                tvSector.setTypeface(tipoFuente3);
                tvActividad.setTypeface(tipoFuente3);
                tvIngreso.setTypeface(tipoFuente3);
                tvSituación.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);
                etIngreso.setInputType(InputType.TYPE_CLASS_NUMBER);
                etIngreso.setTypeface(tipoFuente3);
                tv1.setText(titulo);
                tvSector.setText(mensajes[0]);
                tvActividad.setText(mensajes[1]);
                tvIngreso.setText(mensajes[2]);
                tvSituación.setText(mensajes[3]);

                View[] editTexts = new View[]{etIngreso};
                touch(editTexts);

                final ArrayList<Sector> sectores = conexion.selectAllSectores();
                sectores.add(0, new Sector("-1", "SELECCIONAR"));

                spinnerSector.setAdapter(new ArrayAdapter<>(MasterControl.this,
                        R.layout.spinner_item_iden, sectores));

                final ArrayList<SituacionLaboral> situacionesLaborales = conexion.selectAllSituacionLaboral();
                situacionesLaborales.add(0, new SituacionLaboral("-1", "SELECCIONAR"));

                spinnerSituacion.setAdapter(new ArrayAdapter<>(MasterControl.this,
                        R.layout.spinner_item_iden, situacionesLaborales));

                spinnerSector.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (!sectores.isEmpty()) {
                                reiniciarCountDown();
                                String titulosector = "Seleccione sector economico";
                                mostrarDialogoSectorSocioEconomico(sectores, titulosector, spinnerSector, spinnerActividad);
                                txtError1.setText("");
                            }
                        }

                        return true;
                    }
                });
                final String tituloActividad = "Seleccione la actividad economica";

                spinnerActividad.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN) {

                            if (codSpinnerSector != null) {
                                final ArrayList<Actividad> actividads = conexion.selectAllActividades(codSpinnerSector);
                                if (!actividads.isEmpty()) {
                                    mostrarDialogoSectorActividad(actividads, tituloActividad, spinnerActividad);
                                }
                                txtError2.setText("");
                            }
                        }
                        return true;
                    }
                });

                final String tituloSituacionLaboral = "Seleccione situacion laboral";

                spinnerSituacion.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (!situacionesLaborales.isEmpty()) {
                                mostrarDialogoSituacionLaboral(situacionesLaborales, tituloSituacionLaboral, spinnerSituacion);
                            }
                            txtError4.setText("");
                        }
                        return true;
                    }
                });


                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarCountDown();
                        System.out.println("Cod Sector ***********  " + codSpinnerSector);
                        System.out.println("Cod Activity *********  " + codActivitySpinner);
                        System.out.println("Cod SituacionLaboral **** " + codSituacionLaboral);
                        hideKeyBoard(etIngreso.getWindowToken());

                        if (!spinnerSector.getSelectedItem().toString().equals("SELECCIONAR")) {
                            if (!etIngreso.getText().toString().equals("")) {
                                if (!spinnerSituacion.getSelectedItem().toString().equals("SELECCIONAR")) {
                                    if (validarCaracterNoPermitido(etIngreso, txtError3, "~")) {
                                        cancelarCountDown();
                                        inputContent = codSpinnerSector + "~" + codActivitySpinner + "~" + etIngreso.getText().toString() + "~" + codSituacionLaboral + "Ø" +
                                                spinnerSector.getSelectedItem().toString() + "~" + spinnerActividad.getSelectedItem().toString() + "~" + spinnerSituacion.getSelectedItem().toString();
                                        listener.confirm(InputManager.Style.COMMONINPUT);
                                    } else {
                                        reiniciarCountDown();
                                        inputContent = "";
                                        return;
                                    }

                                } else {
                                    reiniciarCountDown();
                                    validarErrorSpinner(spinnerSituacion, txtError4, "Seleccione un valor válido");
                                    inputContent = "";
                                    return;
                                }
                            } else {
                                reiniciarCountDown();
                                changeToBackgroundError(etIngreso, txtError3, "Campo vacío", R.drawable.edittext_pichi_normal);
                                etIngreso.requestFocus();
                                inputContent = "";
                                return;
                            }
                        } else {
                            reiniciarCountDown();
                            validarErrorSpinner(spinnerSector, txtError1, "Seleccione un valor válido");
                            inputContent = "";
                            return;
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarCountDown();
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }

    private void mostrarDialogoSituacionLaboral(final ArrayList<SituacionLaboral> datos, final String titulo, final Spinner spinner) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MasterControl.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.viewinfo, null);

        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        final ListView listView = view.findViewById(R.id.basecarview);
        TextView textView = view.findViewById(R.id.titulo);
        textView.setText(titulo);

        final AdaptadorSituacionLaboral adapt = new AdaptadorSituacionLaboral(getApplicationContext(), datos);
        listView.setAdapter(adapt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                listView.getSelectedItem();
                String tituloActividad = "Seleccione la actividad economica";
                codSituacionLaboral = datos.get(i).getId_laboral();
                spinner.setSelection(i);
            }
        });

    }

    private void mostrarDialogoSectorSocioEconomico(final ArrayList<Sector> datos, final String titulo, final Spinner spinner, final Spinner spinnerActividad) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MasterControl.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.viewinfo, null);

        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        final ListView listView = view.findViewById(R.id.basecarview);
        TextView textView = view.findViewById(R.id.titulo);
        textView.setText(titulo);

        final AdaptadorSector adapt = new AdaptadorSector(getApplicationContext(), datos);
        listView.setAdapter(adapt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                if (!datos.get(i).getIdSector().equals("-1")) {
                    listView.getSelectedItem();
                    String tituloActividad = "Seleccione la actividad economica";
                    codSpinnerSector = datos.get(i).getIdSector();
                    final ArrayList<Actividad> actividads = conexion.selectAllActividades(datos.get(i).getIdSector());
                    mostrarDialogoSectorActividad(actividads, tituloActividad, spinnerActividad);
                    spinner.setSelection(i);
                }
            }
        });

    }

    private void mostrarDialogoSectorActividad(final ArrayList<Actividad> datos, String titulo, final Spinner spinnerAcitdad) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MasterControl.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.viewinfo, null);

        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        spinnerAcitdad.setAdapter(new ArrayAdapter<>(MasterControl.this,
                R.layout.spinner_item_iden, datos));

        final ListView listView = view.findViewById(R.id.basecarview);
        TextView textView = view.findViewById(R.id.titulo);
        textView.setText(titulo);

        final AdaptadorActividad adapt = new AdaptadorActividad(getApplicationContext(), datos);
        listView.setAdapter(adapt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                if (!datos.get(i).getIdActividad().equals("-1")) {
                    listView.getSelectedItem();
                    codActivitySpinner = datos.get(i).getIdActividad();
                    spinnerAcitdad.setSelection(i);
                }

            }
        });

    }


    /*
 Si la posicion 3 (0,1,2,3) de mensajes, se activa vista de reposicion de kit "modificar datos"
  */
    @Override
    public void showInfoKit(final int timeout, final String titulo, final String[] mensajes, final String[] contenido, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_informacion_kit);
                setToolbar();
                temporizador(InitTrans.time);
                TextView tv1 = findViewById(R.id.tv1);
                TextView tvMsgKit = findViewById(R.id.tvMsgKit);
                TextView tvMsgCelular = findViewById(R.id.tvMsgCelular);
                TextView tvMsgCorreo = findViewById(R.id.tvMsgCorreo);
                final TextView tvMsgConfirmacionCorreo = findViewById(R.id.tvMsgConfirmacionCorreo);

                final ImageView punto1, punto2, punto3, punto4, punto5, punto6, punto7, punto8, punto9, punto10;

                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                final TextView txtError3 = findViewById(R.id.txtError3);
                final TextView txtError4 = findViewById(R.id.txtError4);

                final EditText etNumKit = findViewById(R.id.etNumKit);
                final EditText etCelular = findViewById(R.id.etCelular);
                final EditText etCorreo = findViewById(R.id.etCorreo);
                final EditText etConfiCorreo = findViewById(R.id.etConfiCorreo);


                punto1 = findViewById(R.id.punto1);
                punto2 = findViewById(R.id.punto2);
                punto3 = findViewById(R.id.punto3);
                punto4 = findViewById(R.id.punto4);
                punto5 = findViewById(R.id.punto5);
                punto6 = findViewById(R.id.punto6);
                punto7 = findViewById(R.id.punto7);
                punto8 = findViewById(R.id.punto8);
                punto9 = findViewById(R.id.punto9);
                punto10 = findViewById(R.id.punto10);


                etNumKit.addTextChangedListener(new TextWatcher() {
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
                                etNumKit.setCursorVisible(true);
                                punto1.setVisibility(View.INVISIBLE);
                                punto2.setVisibility(View.INVISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 1:
                                etNumKit.setCursorVisible(false);
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.INVISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 4:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 5:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 6:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 7:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 8:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 9:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.VISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 10:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.VISIBLE);
                                punto10.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });

                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);

                tv1.setTypeface(tipoFuente1);
                tvMsgKit.setTypeface(tipoFuente3);
                tvMsgCelular.setTypeface(tipoFuente3);
                tvMsgCorreo.setTypeface(tipoFuente3);
                tvMsgConfirmacionCorreo.setTypeface(tipoFuente3);
                etNumKit.setTypeface(tipoFuente3);
                etCelular.setTypeface(tipoFuente3);
                etCorreo.setTypeface(tipoFuente3);
                etConfiCorreo.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);

                etNumKit.setInputType(InputType.TYPE_CLASS_NUMBER);
                etCelular.setInputType(InputType.TYPE_CLASS_NUMBER);
                etCorreo.setInputType(InputType.TYPE_CLASS_TEXT);
                etConfiCorreo.setInputType(InputType.TYPE_CLASS_TEXT);

                tv1.setText(titulo);
                tvMsgKit.setText(mensajes[0]);
                tvMsgCelular.setText(mensajes[1]);
                tvMsgCorreo.setText(mensajes[2]);

                View[] editTexts = new View[]{etNumKit, etCelular, etCorreo, etConfiCorreo};
                touch(editTexts);

                if (contenido != null) {
                    if (contenido.length == 3) {
                        etCelular.setText(contenido[1]);
                        etCorreo.setText(contenido[2]);
                        etConfiCorreo.setText(contenido[2]);
                    }
                }

                etCorreo.setLongClickable(false);

                etConfiCorreo.setLongClickable(false);

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        hideKeyBoard(etNumKit.getWindowToken());
                        hideKeyBoard(etCelular.getWindowToken());
                        hideKeyBoard(etCorreo.getWindowToken());
                        hideKeyBoard(etConfiCorreo.getWindowToken());

                        if (!etNumKit.getText().toString().equals("")) {
                            if (!etCelular.getText().toString().equals("") && etCelular.getText().toString().length() == 10 && etCelular.getText().toString().substring(0,2).equals("09")){
                                if (!etCorreo.getText().toString().equals("")) {
                                    if (validaEmail(etCorreo.getText().toString())) {
                                        if (validarCaracterNoPermitido(etCorreo, txtError3, "~")) {
                                            if (!etConfiCorreo.getText().toString().equals("")) {
                                                if (validaEmail(etConfiCorreo.getText().toString())) {
                                                    if (validarCaracterNoPermitido(etConfiCorreo, txtError4, "~")) {
                                                        if (etConfiCorreo.getText().toString().equals(etCorreo.getText().toString())) {
                                                            cancelarCountDown();
                                                            inputContent = etNumKit.getText().toString() + "~" +
                                                                    etCelular.getText().toString() + "~" +
                                                                    etCorreo.getText().toString();
                                                            listener.confirm(InputManager.Style.COMMONINPUT);
                                                        } else {
                                                            reiniciarCountDown();
                                                            changeToBackgroundError(etConfiCorreo, txtError4, "El correo no coincide", R.drawable.edittext_pichi_normal);
                                                            etConfiCorreo.requestFocus();
                                                            inputContent = "";
                                                            return;
                                                        }
                                                    } else {
                                                        reiniciarCountDown();
                                                        etConfiCorreo.requestFocus();
                                                        inputContent = "";
                                                        return;
                                                    }
                                                } else {
                                                    reiniciarCountDown();
                                                    changeToBackgroundError(etConfiCorreo, txtError4, "Correo electrónico no válido", R.drawable.edittext_pichi_normal);
                                                    etConfiCorreo.requestFocus();
                                                    inputContent = "";
                                                    return;
                                                }
                                            } else {
                                                reiniciarCountDown();
                                                changeToBackgroundError(etConfiCorreo, txtError4, "Campo vacío", R.drawable.edittext_pichi_normal);
                                                etConfiCorreo.requestFocus();
                                                inputContent = "";
                                                return;
                                            }
                                        } else {
                                            reiniciarCountDown();
                                            etCorreo.requestFocus();
                                            inputContent = "";
                                            return;
                                        }
                                    } else {
                                        reiniciarCountDown();
                                        changeToBackgroundError(etCorreo, txtError3, "Correo electrónico no válido", R.drawable.edittext_pichi_normal);
                                        etCorreo.requestFocus();
                                        inputContent = "";
                                        return;
                                    }
                                } else {
                                    reiniciarCountDown();
                                    changeToBackgroundError(etCorreo, txtError3, "Campo vacío", R.drawable.edittext_pichi_normal);
                                    etCorreo.requestFocus();
                                    inputContent = "";
                                    return;
                                }
                            } else {
                                reiniciarCountDown();
                                changeToBackgroundError(etCelular, txtError2, "Ingrese un valor válido", R.drawable.edittext_pichi_normal);
                                etCelular.requestFocus();
                                inputContent = "";
                                return;
                            }
                        } else {
                            reiniciarCountDown();
                            changeToBackgroundError(etNumKit, txtError1, "Campo vacío", R.drawable.edittext_pichi_normal);
                            etNumKit.requestFocus();
                            inputContent = "";
                            return;
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }


    @Override
    public void showReposicionInfoKit(final int timeout, final String titulo, final String[] mensajes, final String[] contenido, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_informacion_kit);
                setToolbar();
                temporizador(InitTrans.time);
                TextView tv1 = findViewById(R.id.tv1);
                TextView tvMsgKit = findViewById(R.id.tvMsgKit);
                TextView tvMsgCelular = findViewById(R.id.tvMsgCelular);
                TextView tvMsgCorreo = findViewById(R.id.tvMsgCorreo);

                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtError2 = findViewById(R.id.txtError2);
                final TextView txtError3 = findViewById(R.id.txtError3);

                final EditText etNumKit = findViewById(R.id.etNumKit);
                final EditText etCelular = findViewById(R.id.etCelular);
                final EditText etCorreo = findViewById(R.id.etCorreo);

                etCelular.setEnabled(false);
                etCorreo.setEnabled(true);

                etCelular.setText(contenido[0]);
                etCorreo.setText(contenido[1]);

                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                //final Button btnEdit = findViewById(R.id.btnEditar);

                tv1.setTypeface(tipoFuente1);
                tvMsgKit.setTypeface(tipoFuente3);
                tvMsgCelular.setTypeface(tipoFuente3);
                tvMsgCorreo.setTypeface(tipoFuente3);
                etNumKit.setTypeface(tipoFuente3);
                etCelular.setTypeface(tipoFuente3);
                etCorreo.setTypeface(tipoFuente3);
                btnAccept.setTypeface(tipoFuente3);
                btnCancel.setTypeface(tipoFuente3);
                //btnEdit.setTypeface(tipoFuente3);

                final ImageView punto1, punto2, punto3, punto4, punto5, punto6, punto7, punto8, punto9, punto10;


                punto1 = findViewById(R.id.punto1);
                punto2 = findViewById(R.id.punto2);
                punto3 = findViewById(R.id.punto3);
                punto4 = findViewById(R.id.punto4);
                punto5 = findViewById(R.id.punto5);
                punto6 = findViewById(R.id.punto6);
                punto7 = findViewById(R.id.punto7);
                punto8 = findViewById(R.id.punto8);
                punto9 = findViewById(R.id.punto9);
                punto10 = findViewById(R.id.punto10);


                etNumKit.setInputType(InputType.TYPE_CLASS_NUMBER);
                etCelular.setInputType(InputType.TYPE_CLASS_NUMBER);
                etCorreo.setInputType(InputType.TYPE_CLASS_TEXT);



                etNumKit.addTextChangedListener(new TextWatcher() {
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
                                etNumKit.setCursorVisible(true);
                                punto1.setVisibility(View.INVISIBLE);
                                punto2.setVisibility(View.INVISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 1:
                                etNumKit.setCursorVisible(false);
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.INVISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.INVISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.INVISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 4:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.INVISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 5:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.INVISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 6:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.INVISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 7:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.INVISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 8:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.INVISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 9:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.VISIBLE);
                                punto10.setVisibility(View.INVISIBLE);
                                break;
                            case 10:
                                punto1.setVisibility(View.VISIBLE);
                                punto2.setVisibility(View.VISIBLE);
                                punto3.setVisibility(View.VISIBLE);
                                punto4.setVisibility(View.VISIBLE);
                                punto5.setVisibility(View.VISIBLE);
                                punto6.setVisibility(View.VISIBLE);
                                punto7.setVisibility(View.VISIBLE);
                                punto8.setVisibility(View.VISIBLE);
                                punto9.setVisibility(View.VISIBLE);
                                punto10.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });



                tv1.setText(titulo);
                tvMsgKit.setText(mensajes[0]);
                tvMsgCelular.setText(mensajes[1]);
                tvMsgCorreo.setText(mensajes[2]);

                etNumKit.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == keyEvent.ACTION_DOWN && i == keyEvent.KEYCODE_ENTER) {
                            hideKeyBoard(view.getWindowToken());
                            return true;
                        }
                        return false;
                    }
                });

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        hideKeyBoard(etNumKit.getWindowToken());
                        hideKeyBoard(etCelular.getWindowToken());
                        hideKeyBoard(etCorreo.getWindowToken());

                        if (!etNumKit.getText().toString().equals("")) {
                            if (!etCelular.getText().toString().equals("") && etCelular.getText().toString().length() == 10) {
                                if (!etCorreo.getText().toString().equals("")) {
                                    if (validaEmail(etCorreo.getText().toString())) {
                                        if (validarCaracterNoPermitido(etCorreo, txtError3, "~")) {
                                            cancelarCountDown();
                                            inputContent = etNumKit.getText().toString() + "~" +
                                                    etCelular.getText().toString() + "~" +
                                                    etCorreo.getText().toString();

                                            listener.confirm(InputManager.Style.COMMONINPUT);

                                        } else {
                                            reiniciarCountDown();
                                            etCorreo.requestFocus();
                                            inputContent = "";
                                            return;
                                        }
                                    } else {
                                        reiniciarCountDown();
                                        changeToBackgroundError(etCorreo, txtError3, "Correo electrónico no válido", R.drawable.edittext_pichi_normal);
                                        etCorreo.requestFocus();
                                        inputContent = "";
                                        return;
                                    }
                                } else {
                                    reiniciarCountDown();
                                    changeToBackgroundError(etCorreo, txtError3, "Campo vacío", R.drawable.edittext_pichi_normal);
                                    etCorreo.requestFocus();
                                    inputContent = "";
                                    return;
                                }
                            } else {
                                reiniciarCountDown();
                                changeToBackgroundError(etCelular, txtError2, "Ingrese un valor válido", R.drawable.edittext_pichi_normal);
                                etCelular.requestFocus();
                                inputContent = "";
                                return;
                            }
                        } else {
                            reiniciarCountDown();
                            changeToBackgroundError(etNumKit, txtError1, "Campo vacío", R.drawable.edittext_pichi_normal);
                            etNumKit.requestFocus();
                            inputContent = "";
                            return;
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });

                /*btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        noBack = true;
                        isFragmentBack = true;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        DialogFullscreenFragment newFragment = new DialogFullscreenFragment();
                        newFragment.setMensaje(titulo,"Declaro que la información ingresada por este medio es real, " +
                                "por lo que autorizo que se actualice la información de mi número de celular" +
                                " y mi correo electrónico registrados en esta transacción.");
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
                        newFragment.setOnCallbackResult(new DialogFullscreenFragment.CallbackResult() {
                            @Override
                            public void sendResult(int requestCode) {
                                noBack = false;
                                isFragmentBack = false;

                                cancelarCountDown();
                                inputContent = "Modificar";

                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        });
                    }
                });*/
            }
        });
    }

    @Override
    public void showSuccesView(int timeout, final String titulo, final String mensaje, final boolean isSucces, final boolean isButtonCancelar, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.showsucess);
                TextView tv1 = findViewById(R.id.tv1);
                ImageView image = findViewById(R.id.image);
                TextView tv2 = findViewById(R.id.tv2);
                Button btnCancel = findViewById(R.id.btnCancel);
                Button btnAccept = findViewById(R.id.btnAccept);
                btnAccept.setText("CONTINUAR");

                tv1.setText(titulo);
                tv2.setText(mensaje);


                if (isButtonCancelar){
                    btnCancel.setVisibility(View.VISIBLE);
                }else {
                    btnCancel.setVisibility(View.INVISIBLE);
                }

                if (isSucces) {
                    image.setImageDrawable(getDrawable(R.drawable.pichi_aceptado_new));
                } else {
                    image.setImageDrawable(getDrawable(R.drawable.pichi_denegado3));
                }

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputContent = "Exito";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });

            }
        });
    }


    @Override
    public void showContrato(final int timeout, final String titulo, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_mostrar_contrato);
                setToolbar();
                okTempo = false;
                //temporizador(InitTrans.time);
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                TextView ms1 = findViewById(R.id.ms1);
                final TextView txtError1 = findViewById(R.id.txtError);
                final CheckBox check1 = findViewById(R.id.ms2);
                Button btCancel = findViewById(R.id.btCancel);
                Button btAceptar = findViewById(R.id.btAceptar);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);
                tv1.setTypeface(tipoFuente1);

                tv1.setText(titulo);
                ms0.setTypeface(tipoFuente3);
                ms1.setTypeface(tipoFuente3);
                ms0.setText(mensajes[0]);
                ms1.setText(mensajes[1]);
                check1.setText(mensajes[2]);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (masterCDT != null) {
                            masterCDT.cancel();
                        }
                        if (check1.isChecked()) {
                            inputContent = "aceptar";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            txtError1.setText("Por favor acepte términos y condiciones al final del contrato");
                        }
                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void showIngresoOTP(int timeout, final String titulo, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_validar_codigo_seguridad);
                setToolbar();
                temporizador(InitTrans.time);

                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                TextView ms2 = findViewById(R.id.ms2);
                final TextView txtError1 = findViewById(R.id.txtError1);
                final EditText et1 = findViewById(R.id.et1);
                Button btnGenerar = findViewById(R.id.btnGenerar);
                Button btCancel = findViewById(R.id.btCancel);
                Button btAceptar = findViewById(R.id.btAceptar);

                tv1.setTypeface(tipoFuente1);
                ms0.setTypeface(tipoFuente3);
                ms2.setTypeface(tipoFuente3);
                et1.setTypeface(tipoFuente3);
                btnGenerar.setTypeface(tipoFuente3);
                btCancel.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                et1.setInputType(InputType.TYPE_CLASS_NUMBER);

                View[] editTexts = new View[]{et1};
                touch(editTexts);

                tv1.setText(titulo);
                ms0.setText(mensajes[0]);
                ms2.setText(mensajes[1]);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!et1.getText().toString().equals("")) {
                            cancelarCountDown();
                            hideKeyBoard(et1.getWindowToken());
                            inputContent = et1.getText().toString();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            reiniciarCountDown();
                            changeToBackgroundError(et1, txtError1, "Campo vacío", R.drawable.edittext_pichi_normal);
                            inputContent = "";
                            return;
                        }

                    }
                });

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarCountDown();
                        inputContent = "cancelar";
                        listener.cancel();
                    }
                });

                btnGenerar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarCountDown();
                        inputContent = "generar";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });
            }
        });
    }

    @Override
    public void showMensajeConfirmacion(int timeout, final String titulo, final String[] mensajes, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_vista_msg_confirmacion);
                theToolbarVisitaView();
                temporizador(InitTrans.time);
                noBack = true;
                TextView tv1 = findViewById(R.id.tv1);
                TextView ms0 = findViewById(R.id.ms0);
                TextView ms1 = findViewById(R.id.ms1);
                TextView ms2 = findViewById(R.id.ms2);
                TextView ms3 = findViewById(R.id.ms3);
                TextView ms4 = findViewById(R.id.ms4);
                Button btAceptar = findViewById(R.id.btAceptar);
                Button btCancel = findViewById(R.id.btCancel);

                tv1.setTypeface(tipoFuente1);
                ms0.setTypeface(tipoFuente3);
                ms1.setTypeface(tipoFuente3);
                ms2.setTypeface(tipoFuente3);
                ms3.setTypeface(tipoFuente3);
                ms4.setTypeface(tipoFuente3);
                btAceptar.setTypeface(tipoFuente3);

                tv1.setText(titulo);
                ms0.setText(mensajes[0]);
                ms1.setText(mensajes[1]);

                btAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarCountDown();
                        inputContent = "aceptar";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btCancel.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void showDatePickerDialogMsg(final EditText date) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + ISOUtil.padleft(month + 1 + "", 2, '0') + ISOUtil.padleft(day + "", 2, '0');
                date.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showDatePickerDialog(final EditText date) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + ISOUtil.padleft(month + 1 + "", 2, '0') + ISOUtil.padleft(day + "", 2, '0');
                date.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(final EditText dateTime) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final String selectedTime = ISOUtil.padleft(hourOfDay + "", 2, '0') + ISOUtil.padleft(minute + "", 2, '0') + "00";
                dateTime.setText(selectedTime);
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void initList(String[] menu) {
        noBack = false;

        final ListView listview = findViewById(R.id.simpleListView);

        int[] imagenes = {R.mipmap.ic_delante_azul, R.mipmap.ic_delante_azul, R.mipmap.ic_delante_azul,
                R.mipmap.ic_delante_azul};

        ListViewAdapter adapter = new ListViewAdapter(MasterControl.this, menu, imagenes);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = position + 1 + "";
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (!item.equals("")) {
                                    inputContent = item;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                }

                            }
                        });
            }

        });
    }


    private void startTrans(String type, String tipoTrans) {
        try {
            PaySdk.getInstance().startTrans(type, tipoTrans, this);
        } catch (PaySdkException e) {
            e.printStackTrace();
        }
    }

    private void showHanding(String msg) {
        TextView tv = (TextView) findViewById(R.id.handing_msginfo);
        tv.setText(msg);
    }

    private void setToolbar(String titleToolbar) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titleToolbar);
        toolbar.setLogo(R.drawable.iconoinicio);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSupportActionBar(toolbar);
            }
        }, 0);
    }

    private void hideKeyBoard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setToolbarBack(final Class<?> cls) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void theToolbarVisitaView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected boolean validarMonto(EditText etMonto, TextView txtErrorMonto) {
        boolean ret = false;
        if (!etMonto.getText().toString().equals("")) {
            strMonto = etMonto.getText().toString();
            strMonto = strMonto.replace(".", "");
            long monto = Long.parseLong(strMonto);
            int val;
            if (monto != 00 || monto != 000) {
                if (type.equals(PrintRes.TRANSEN[2])) {
                    val = TransTools.validateAmountRetiro(monto / 100);
                } else if (type.equals(PrintRes.TRANSEN[6])) {
                    val = TransTools.validateAmountDeposito(monto / 100);
                } else {
                    val = 0;
                }
            } else {
                val = 1;
            }
            if (val == 1) {
                etMonto.setText("0.00");
                changeToBackgroundError(etMonto, txtErrorMonto, "Monto inferior al permitido", R.drawable.edittext_pichi_normal);
            } else if (val == 2) {
                etMonto.setText("0.00");
                changeToBackgroundError(etMonto, txtErrorMonto, "Monto superior al permitido", R.drawable.edittext_pichi_normal);
            } else {
                ret = true;
            }
        } else {
            changeToBackgroundError(etMonto, txtErrorMonto, "Monto no digitado", R.drawable.edittext_pichi_normal);
        }

        return ret;
    }

    public boolean validarContraPartida(String inputContent, int carcRequeridos, EditText etNum, TextView txtError) {
        boolean ret = false;
        if (inputContent.equals("")) {
            changeToBackgroundError(etNum, txtError, "Ingresa un valor válido", R.drawable.edittext_pichi_normal);
        } else if (carcRequeridos > 0) {
            if (inputContent.length() >= carcRequeridos) {
                ret = true;
            } else {
                changeToBackgroundError(etNum, txtError, "Ingresa un valor válido", R.drawable.edittext_pichi_normal);
            }
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean validarContraPartida(String inputContent, int carcRequeridos) {

        if (inputContent.equals("")) {
            return false;
        } else if ((carcRequeridos > 0)) {
            if (inputContent.length() < carcRequeridos)
                return false;
        }

        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (okTempo) {
                masterCDT.cancel();
                masterCDT.start();
            }
        }
        return super.onTouchEvent(event);
    }

    public void temporizador(long timer) {
        okTempo = true;
        InitTrans.wrlg.wrDataTxt("Inicio timer MasterControl1, método temporizador");
        masterCDT = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (InitTrans.initialization || InitTrans.initEMV) {
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl1, método temporizador, ingresa a menú principal");
                    cancelarCountDown();
                    UIUtils.startView(MasterControl.this, MainMenuPrincipal.class);
                } else {
                    cancelarCountDown();
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl1, método temporizador, ingresa a start activity");
                    UIUtils.startView(MasterControl.this, StartActivity.class);
                }

            }
        }.start();
    }

    public void temporizador(long timer, final IBinder windowToken) {
        okTempo = true;
        InitTrans.wrlg.wrDataTxt("Inicio timer MasterControl2, método temporizador");
        masterCDT = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (InitTrans.initialization || InitTrans.initEMV) {
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl2, método temporizador, ingresa a menú principal");
                    masterCDT.cancel();
                    hideKeyBoard(windowToken);
                    UIUtils.startView(MasterControl.this, MainMenuPrincipal.class);
                } else {
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl2, método temporizador, ingresa a start activity");
                    masterCDT.cancel();
                    UIUtils.startView(MasterControl.this, StartActivity.class);
                }

            }
        }.start();
    }

    public void reiniciarCountDown() {
        masterCDT.cancel();
        masterCDT.start();
    }

    private void changeToBackgroundError(EditText edt, TextView txt, String msgError, final int direccion) {

        final EditText editText = edt;
        final TextView textView = txt;

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

    private void validarErrorSpinner(Spinner spn, TextView txt, String msgError) {
        final TextView textView = txt;
        textView.setText(msgError);
    }

    private void changeToBackgroundError(EditText edt, TextView txt, String msgError) {
        txt.setText(msgError);
        edt.setBackgroundResource(R.drawable.edittext_pichi_error);
        edt.setText("");
    }

    @Override
    public void onBackPressed() {
        if (!noBack) {
            if (!onBackCard) {
                super.onBackPressed();
                if (okTempo) {
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl, onBackPressed.");
                    masterCDT.cancel();
                }
                this.finish();

            } else {
                listener.cancel();
                super.onBackPressed();
                if (okTempo) {
                    InitTrans.wrlg.wrDataTxt("Fin timer MasterControl, onBackPressed.");
                    masterCDT.cancel();
                }
                this.finish();
            }
        } else {
            if (!isFragmentBack) {
                UIUtils.toast(this, "Transacción en proceso, espere un momento", 1000);
            } else {
                super.onBackPressed();
            }
            //Toast.makeText(this, "Transaccion en proceso, espere un momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void obtener() {
        SharedPreferences sharedPreferences = getSharedPreferences("userCentralizado", Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre", "");
        String cedula = sharedPreferences.getString("cedula", "");
        String ruc = sharedPreferences.getString("ruc", "");
        String empresa = sharedPreferences.getString("empresa", "");
        String fechaUltimoCierre = sharedPreferences.getString("fechaUltimoCierre", "");
        String fechaRegistro = sharedPreferences.getString("fechaRegistro", "");
        InitTrans.user.setCedula(cedula);
        InitTrans.user.setNombre(nombre);
        InitTrans.user.setRuc(ruc);
        InitTrans.user.setEmpresa(empresa);
        InitTrans.user.setFechaUltimoCierre(fechaUltimoCierre);
        InitTrans.user.setFechaRegistro(fechaRegistro);
    }

    private boolean validarCaracterNoPermitido(final EditText edt, final TextView txt, final String caracter) {

        edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt.setBackgroundResource(R.drawable.edittext_pichi_normal);
                txt.setText("");
            }
        });

        if (edt.getText().toString().contains(caracter)) {
            edt.setBackgroundResource(R.drawable.edittext_pichi_error);
            txt.setText("Caracter no permitido (" + caracter + ")");
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarCountDown();
    }

    private void cancelarCountDown() {
        if (masterCDT != null)
            masterCDT.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (masterCDT != null) {
            masterCDT.cancel();
        }
    }

    private boolean validaEmail(String email) {
        if (!email.contains("*")){
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(email).matches();
        }else {
            // Patrón para validar el email
            Pattern pattern = Pattern
                    .compile("^[_A-Za-z0-9-\\*-\\+]+(\\.[_A-Za-z0-9-\\*-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");


            Matcher mather = pattern.matcher(email);

            if (mather.find() == true) {
                System.out.println("El email ingresado es válido.");
                return true;
            } else {
                System.out.println("El email ingresado es inválido.");
                return false;
            }
        }

    }

    @Override
    public void handlingOTP(final boolean flag , final String info){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_result);
                TextView details ;
                ImageView face ;
                details =  findViewById(R.id.result_details);
                details.setTypeface(tipoFuente1);
                face =  findViewById(R.id.result_img);
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
        });
    }

    @Override
    public void showIngreso2EditTextMonto(int timeout, final String[] mensajes, final int[] maxLengs, final int[] inputType, final int[] carcRequeridos, final String titulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_monto_2_edittext);
                temporizador(InitTrans.time);
                KeyboardUtil keyboardUtil = new KeyboardUtil(MasterControl.this, getWindow().getDecorView().getRootView());
                keyboardUtil.enable();

                TextView tv1 = findViewById(R.id.tv1);
                TextView txtView1 = findViewById(R.id.textView1);
                TextView txtView2 = findViewById(R.id.textView2);
                TextView txtView3 = findViewById(R.id.textView3);
                final EditText edtCelular = findViewById(R.id.et1);
                et_amount = findViewById(R.id.et_Amount);
                final EditText edtCodSeguridad = findViewById(R.id.et2);
                final TextView txtError1 = findViewById(R.id.txtError1);
                final TextView txtErrorMonto = findViewById(R.id.txtErrorMonto);
                final TextView txtError2 = findViewById(R.id.txtError2);
                tv1.setText(titulo);
                tv1.setTypeface(tipoFuente1);

                txtView1.setText(mensajes[0]);
                txtView1.setTypeface(tipoFuente2);
                txtView2.setText(mensajes[1]);
                txtView2.setTypeface(tipoFuente2);
                txtView3.setText(mensajes[2]);
                txtView3.setTypeface(tipoFuente2);

                btnCancelAmnt = (Button) findViewById(R.id.btCancel);
                btnAcceptAmnt = (Button) findViewById(R.id.btAceptar);
                btnAcceptAmnt.setTypeface(tipoFuente3);
                btnCancelAmnt.setTypeface(tipoFuente3);

                edtCelular.setTypeface(tipoFuente3);
                edtCodSeguridad.setTypeface(tipoFuente3);

                edtCelular.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengs[0])});
                edtCelular.setInputType(inputType[0]);

                edtCodSeguridad.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengs[1])});
                edtCodSeguridad.setInputType(inputType[1]);

                edtCelular.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_amount, InputMethodManager.SHOW_IMPLICIT);

                et_amount.addTextChangedListener(new TextWatcher() {
                    private boolean isChanged = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isChanged) {
                            return;
                        }
                        String str = editable.toString();
                        isChanged = true;
                        String cuttedStr = str;
                        for (int i = str.length() - 1; i >= 0; i--) {
                            char c = str.charAt(i);
                            if ('.' == c) {
                                cuttedStr = str.substring(0, i) + str.substring(i + 1);
                                break;
                            }
                        }
                        int NUM = cuttedStr.length();
                        int zeroIndex = -1;
                        for (int i = 0; i < NUM - 2; i++) {
                            char c = cuttedStr.charAt(i);
                            if (c != '0') {
                                zeroIndex = i;
                                break;
                            } else if (i == NUM - 3) {
                                zeroIndex = i;
                                break;
                            }
                        }
                        if (zeroIndex != -1) {
                            cuttedStr = cuttedStr.substring(zeroIndex);
                        }
                        if (cuttedStr.length() < 3) {
                            cuttedStr = "0" + cuttedStr;
                        }
                        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                                + "." + cuttedStr.substring(cuttedStr.length() - 2);

                        et_amount.setText(cuttedStr);
                        et_amount.setSelection(et_amount.length());
                        isChanged = false;
                    }
                });

                touch(new View[]{et_amount});

                btnAcceptAmnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer, método showIngreso2EditTextMonto, botón btnAcceptAmnt.");
                        reiniciarCountDown();
                        if (!edtCelular.getText().toString().equals("") && edtCelular.getText().toString().length() >= carcRequeridos[0] &&
                                edtCelular.getText().toString().substring(0,2).equals("09")){

                            if (!et_amount.getText().toString().equals("")) {
                                strMonto = et_amount.getText().toString();
                                strMonto = strMonto.replace(".", "");
                                long monto = Long.parseLong(strMonto);
                                int val;

                                if (type.equals(PrintRes.TRANSEN[2])) {
                                    val = TransTools.validateAmountRetiro(monto / 100);
                                } else if (type.equals(PrintRes.TRANSEN[6])) {
                                    val = TransTools.validateAmountDeposito(monto / 100);
                                } else {
                                    val = 0;
                                }

                                double isMultiplo = ( Double.parseDouble(strMonto) % 10);           // Valida si el monto es multiplo de 1

                                if (val == 1) {
                                    et_amount.setText("0.00");
                                    changeToBackgroundError(et_amount, txtErrorMonto, "Monto inferior al permitido", R.drawable.edittext_pichi_normal);
                                } else if (val == 2) {
                                    et_amount.setText("0.00");
                                    changeToBackgroundError(et_amount, txtErrorMonto, "Monto superior al permitido", R.drawable.edittext_pichi_normal);
                                } else if (isMultiplo != 0){
                                    et_amount.setText("0.00");
                                    changeToBackgroundError(et_amount, txtErrorMonto, "Monto inválido", R.drawable.edittext_pichi_normal);
                                } else {
                                    if (!edtCodSeguridad.getText().toString().equals("") && edtCodSeguridad.getText().toString().length() >= carcRequeridos[1]){
                                        hideKeyBoard(et_amount.getWindowToken());
                                        InitTrans.wrlg.wrDataTxt("Fin timer, método showIngreso2EditTextMonto, monto.");
                                        cancelarCountDown();
                                        inputContent = edtCelular.getText().toString() + "@" +
                                                et_amount.getText().toString().replace(".", "") + "@" +
                                                edtCodSeguridad.getText().toString();
                                        listener.confirm(InputManager.Style.COMMONINPUT);
                                    }else {
                                        changeToBackgroundError(edtCodSeguridad, txtError2, "Ingresa un valor válido", R.drawable.edittext_pichi_normal);
                                        reiniciarCountDown();
                                        edtCodSeguridad.requestFocus();
                                        inputContent = "";
                                        return;
                                    }
                                }
                            } else {
                                changeToBackgroundError(et_amount, txtErrorMonto, "Monto no digitado", R.drawable.edittext_pichi_normal);
                                reiniciarCountDown();
                                et_amount.requestFocus();
                                inputContent = "";
                                return;
                            }
                        }else {
                            changeToBackgroundError(edtCelular, txtError1, "Ingresa un valor válido", R.drawable.edittext_pichi_normal);
                            reiniciarCountDown();
                            edtCelular.requestFocus();
                            inputContent = "";
                            return;
                        }
                    }
                });

                btnCancelAmnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Fin timer, método showIngreso2EditTextMonto, botón cancelar.");
                        cancelarCountDown();
                        listener.cancel();

                    }
                });
            }
        });
    }

    private void listenerValidadorDeCedulaXperta(final EditText editText, final TextView textView, int maxLength, final String titulo) {
        currentEtMaxLength = maxLength;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (titulo) {
                    case "Crear cuenta básica":
                    case "Reposición de kit":
                        if (editText.getText().length() > 2) {
                            if (editText.getText().length() == currentEtMaxLength) {
                                int editValue = Integer.parseInt(editText.getText().toString().substring(0, 2));
                                if (!TransTools.ValidaCedulaRuc(editText.getText().toString().substring(0, 10)) || editValue > 24) {
                                    changeToBackgroundError(editText, textView, "Documento ingresado no válido");
                                }
                            }
                        }
                        break;
                    default:
                        if (editText.getText().length() == currentEtMaxLength) {
                            if (!TransTools.ValidaCedulaRuc(editText.getText().toString().substring(0, 10))) {
                                changeToBackgroundError(editText, textView, "Documento ingresado no válido");
                            }
                        }
                        break;
                }
                if (editText.getText().length() == 1) {
                    editText.setBackgroundResource(R.drawable.edittext_pichi_normal);
                    textView.setText("");
                }
            }
        });

    }
}
