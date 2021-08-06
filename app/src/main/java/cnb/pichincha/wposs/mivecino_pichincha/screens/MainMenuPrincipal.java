package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.printer.Printer;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Tools.BatteryStatus;
import newpos.libpay.utils.CheckSettle;

/**
 * Creado por Fabian Ardila 18/07/18
 */
public class MainMenuPrincipal extends ToolsAppCompact implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static CheckSettle checkSettle;
    Button btnDepo, btnReti, btnRecar, btnPagSer, btnRecau;
    Button btnCons, btnGiro, btnXper, btnProd, btnAdmin;
    Button btnClose, btnBimo, btnCambioClave;
    TextView tv1;
    int nivelBateria = 5;
    public static BatteryStatus batteryStatus;

    Button[] buttons = {btnDepo, btnReti, btnRecar, btnPagSer, btnRecau, btnCons, btnGiro,
            btnXper, btnProd, btnBimo, btnCambioClave, btnAdmin};

    String[] titulos = {"Depósitos", "Retiros", "Recargas", "Pago de servicios", "Recaudaciones", "Consultas", "Giros y remesas",
            "Cuenta básica", "Productos", "Billetera móvil", "Cambio de clave tarjeta débito", "Administrativo"};

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_menus_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, MainMenuPrincipal.");
        temporizador();

        batteryStatus = new BatteryStatus();

        if(InitTrans.isMensaje60){
            InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, por campo 60");
            countDownTimer.cancel();
            UIUtils.startView(this, MensajeCampo60.class);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainMenuPrincipal.this);

        TextView tv1 = findViewById(R.id.tv1);
        tv1.setText("Menú principal");
        tv1.setTypeface(tipoFuente1(this));

        Botones();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(batteryStatus,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //this.registerReceiver(checkSettle, new IntentFilter(Intent.ACTION_TIME_TICK));
        if(TMConfig.getInstance().getmodoOperacion().equals("C")) {
            if (TMConfig.getInstance().getsesionIniciada().equals("L")) {
                UIUtils.startView(this, StartActivity.class);
            }
        } else {

            if (InitTrans.isMensaje60) {
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, por campo 60, desde onResume");
                countDownTimer.cancel();
                UIUtils.startView(this, MensajeCampo60.class);
            }
            /*if (checkSettle != null) {
                checkSettle.setRunTrans(false);
                checkSettle.setSettleAutomatic(false);
            }*/
        }
        reiniciarCountDown();
    }

    /**
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, desde menú hamburguesa, ingreso al menú administrativo.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenuPrincipal.this, MainMenu10_Administrativo.class);
        }else if (id == R.id.nav_help) {
            Toast.makeText(this, "V "+UIUtils.versionApk((this)) + " - " + UIUtils.getAppTimeStamp((this)), Toast.LENGTH_SHORT).show();
            }

        else if (id == R.id.nav_cerrarSesion) {
            if(TMConfig.getInstance().getmodoOperacion().equals("N")) {
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, desde menú hamburguesa, ingreso a StartActivity.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, StartActivity.class);
            }else{
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, desde menú hamburguesa, ingreso a menú administrativo para realizar cierre de usuario (Centralizado).");
                countDownTimer.cancel();
                Toast.makeText(this, "Realiza cierre de sesión en turnos", Toast.LENGTH_SHORT).show();
                UIUtils.startView(this, MainMenu10_Administrativo.class);
            }
        } else if (id == R.id.nav_salir) {
            InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, desde menú hamburguesa, ingreso a StartActivity.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenuPrincipal.this, StartActivity.class);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view){
        if(validarImpresoraBateria(view)){
            if(view.equals(buttons[0])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a depósito.");
                countDownTimer.cancel();
                iniciarTransaccion(this, 6);
            }
            if(view.equals(buttons[1])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a retiro.");
                countDownTimer.cancel();
                iniciarTransaccion(this, 2);
            }
            if(view.equals(buttons[2])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a recargas.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, MainMenu3_Recargas.class);
            }
            if(view.equals(buttons[3])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a pago de servicios.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, MainMenu4_PagoServicios.class);
            }
            if(view.equals(buttons[4])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a recaudaciones.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, MainMenu5_Recaudaciones.class);
            }
            if(view.equals(buttons[5])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a consultas.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, MainMenu6_1_ConsultaPersonas.class);
            }
            if(view.equals(buttons[6])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a giros y remesas.");
                countDownTimer.cancel();
                UIUtils.startView(this, MainMenu7_GirosRemesas.class);
            }
            if(view.equals(buttons[7])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a cuenta básica.");
                countDownTimer.cancel();
                iniciarTransaccion(this, 22);
            }
            if(view.equals(buttons[8])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a productos.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, MainMenu9_Productos.class);
            }
            if(view.equals(buttons[9])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a billetera móvil.");
                countDownTimer.cancel();
                iniciarTransaccion(this, 16);
            }
            //Cambio de clave tarjeta debito
            if(view.equals(buttons[10])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a cuenta básica.");
                countDownTimer.cancel();
                iniciarTransaccion(this, 23);
            }
            if(view.equals(buttons[11])){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a menú administrativo.");
                countDownTimer.cancel();
                checkSettle.setSettleAutomatic(false);
                UIUtils.startView(MainMenuPrincipal.this, MainMenu10_Administrativo.class);
            }
            if(view.equals(btnClose)){
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, ingreso a startactivity, se cerro sesión.");
                countDownTimer.cancel();
                if(TMConfig.getInstance().getmodoOperacion().equals("C")){
                    TMConfig.getInstance().setsesionIniciada("L");
                    InitTrans.loginCentral=false;
                    TMConfig.getInstance().setCedula("");
                }
                UIUtils.startView(MainMenuPrincipal.this, StartActivity.class);
            }
        }

    }

    public boolean validarImpresoraBateria(View view){
        boolean flag = false;
        if(view.equals(btnClose) || view.equals(buttons[11])){
            flag = true;
        }else if(validarBateria() && validarPapelImpresora(false)){
            flag = true;
        }
        return flag;
    }

    private boolean validarPapelImpresora(boolean Paso) {
        boolean retorno = false;
        Printer printer = Printer.getInstance() ;
        int ret = printer.getStatus();
        if(Printer.PRINTER_STATUS_PAPER_LACK == ret){
            if (!Paso) {
                InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, impresora no tiene papel.");
                countDownTimer.cancel();
                Intent intent = new Intent();
                intent.setClass(MainMenuPrincipal.this, VerificaPapelImpresion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                InitTrans.wrlg.wrDataTxt("Reinicio timer MenúPrincipal, impresora no tiene papel.");
                reiniciarCountDown();
                Toast.makeText(getApplicationContext(), "Impresora sin papel", Toast.LENGTH_SHORT).show();
                retorno = true;
            }
        } else {
            retorno = true;
        }
        return retorno;
    }

    private boolean validarBateria() {
        boolean retorno = false;
        if ((batteryStatus.getLevelBattery() <= nivelBateria) && (!batteryStatus.isCharging())) {
            InitTrans.wrlg.wrDataTxt("Fin timer MenúPrincipal, batería baja.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.setClass(MainMenuPrincipal.this, VerificaBateria.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            retorno = true;
        }
        return retorno;
    }


    /***
     * Desabilitar el boton de atras
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    /**
     *
     */
    public void Botones(){
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        lp.setMargins(0, 10, 0, 0);

        for (int i = 0; i < titulos.length; i++) {
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(titulos[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }
        if(TMConfig.getInstance().getmodoOperacion().equals("N")){
            btnClose = new Button(this);
            btnClose.setLayoutParams(lp);
            btnClose.setText("Cerrar sesión");
            btnClose.setTextColor(Color.parseColor("#0F265C"));
            btnClose.setTypeface(tipoFuente3);
            btnClose.setTextSize(22);
            btnClose.setOnClickListener(this);
            btnClose.setAllCaps(false);
            linearLayout.addView(btnClose);
        }

    }

    /**
     *
     */
    public void reiniciarCountDown(){
        countDownTimer.cancel();
        countDownTimer.start();
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
        InitTrans.wrlg.wrDataTxt("Inicio timer MenúPrincipal, método temporizador");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin time MenúPrincipal, método temporizador");
                countDownTimer.cancel();
                UIUtils.startView(MainMenuPrincipal.this, StartActivity.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        //this.unregisterReceiver(checkSettle);
        this.unregisterReceiver(batteryStatus);
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin time MenúPrincipal, desde onPause");
        countDownTimer.cancel();
    }
}
