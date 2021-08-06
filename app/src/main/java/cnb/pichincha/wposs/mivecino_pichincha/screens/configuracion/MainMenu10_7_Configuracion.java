package cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.SDKException;
import com.pos.device.emv.CAPublicKey;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class MainMenu10_7_Configuracion extends ToolsAppCompact implements View.OnClickListener{

    TextView tv1;
    Button btn1, btn2, btn3;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Configuración.");
        temporizador();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);

        tv1.setText("Configuración");

        botones();

    }

    /**
     *
     */
    public void botones() {
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        btn1 = new Button(this);
        btn1.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn1.setLayoutParams(lp);
        btn1.setText("Comunicación");
        btn1.setTextColor(Color.parseColor("#0F265C"));
        btn1.setTypeface(tipoFuente3);
        btn1.setTextSize(22);
        btn1.setOnClickListener(this);
        btn1.setAllCaps(false);
        linearLayout.addView(btn1);

        btn2 = new Button(this);
        btn2.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn2.setLayoutParams(lp);
        btn2.setText("Pantalla");
        btn2.setTextColor(Color.parseColor("#0F265C"));
        btn2.setTypeface(tipoFuente3);
        btn2.setTextSize(22);
        btn2.setOnClickListener(this);
        btn2.setAllCaps(false);
        linearLayout.addView(btn2);


        btn3 = new Button(this);
        btn3.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn3.setLayoutParams(lp);
        btn3.setText("Reporte Emv");
        btn3.setTextColor(Color.parseColor("#0F265C"));
        btn3.setTypeface(tipoFuente3);
        btn3.setTextSize(22);
        btn3.setOnClickListener(this);
        btn3.setAllCaps(false);
        linearLayout.addView(btn3);
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Configuración, desde botón back, ingresa a administrativo.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_7_Configuracion.this, MainMenu10_Administrativo.class);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btn1)) {
            InitTrans.wrlg.wrDataTxt("Fin timer Configuración, ingresa a MainMenu10_7_2_Comunicacion.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_7_Configuracion.this,MainMenu10_7_2_Comunicacion.class);
        } else if (view.equals(btn2)) {
            InitTrans.wrlg.wrDataTxt("Fin timer Configuración, ingresa a MainMenu10_7_3_Pantalla.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_7_Configuracion.this,MainMenu10_7_3_Pantalla.class);
        }else if (view.equals(btn3)) {
            if(InitTrans.initialization && InitTrans.initEMV) {
                String bufEMV = "";
                try {
                    bufEMV = print_aidInfo();
                } catch (SDKException e) {
                    e.printStackTrace();
                }
                PrintManager manager = new PrintManager();
                manager = PrintManager.getmInstance(getApplicationContext());
                int i = manager.printPichincha(bufEMV);
            }else{
                Toast.makeText(this, "Debe inicializar para poder usar esta opción", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static String print_aidInfo() throws SDKException {

        String mensaje = "";

        IEMVHandler emvHandler = EMVHandler.getInstance();
        int cantAid = emvHandler.getAidInfoNum();

        TerminalAidInfo aidInfo = new TerminalAidInfo();
        int ret = -1;
        for(int i=0; i<cantAid; i++) {
            aidInfo = emvHandler.getAidInfo(i);

            mensaje  += "REGISTRO AID: " + Integer.toString(i) + "/" + Integer.toString(cantAid)+
                    "\nAid Length :" + Integer.toString(aidInfo.getAIDLength() ) + "\nAID :" + ISOUtil.bcd2str(aidInfo.getAId(), 0, aidInfo.getAIDLength() ) +
                    "\nThreshouldValue : " + Integer.toString(aidInfo.getThresholdValue()) +
                    "\nTarget %:" + Integer.toString(aidInfo.getTargetPercentage()) + " - MaxTarget % : " + Integer.toString(aidInfo.getMaximumTargetPercentage()) +
                    "\nTAC Denial  :" + ISOUtil.bcd2str(aidInfo.getTerminalActionCodeDenial(), 0, aidInfo.getTerminalActionCodeDenial().length) +
                    "\nTAC Online  :" + ISOUtil.bcd2str(aidInfo.getTerminalActionCodeOnline(), 0, aidInfo.getTerminalActionCodeOnline().length) +
                    "\nTAC Default :" + ISOUtil.bcd2str(aidInfo.getTerminalActionCodeDefault(), 0, aidInfo.getTerminalActionCodeDefault().length) +
                    "\nT Floor Limit : " + Integer.toString(aidInfo.getTerminalFloorLimit()) +
                    "\nAcquirer Identi :" + ISOUtil.bcd2str(aidInfo.getAcquirerIdentifier(), 0, aidInfo.getAcquirerIdentifier().length) +
                    "\nLen Ddol :" + Integer.toString(aidInfo.getLenOfDefaultDDOL()) +
                    "\nDDOL : " + ISOUtil.bcd2str(aidInfo.getDefaultDDOL(), 0, aidInfo.getLenOfDefaultDDOL())  +
                    "\nLen Tdol :" + Integer.toString(aidInfo.getLenOfDefaultTDOL()) +
                    "\nTDOL : " + ISOUtil.bcd2str(aidInfo.getDefaultTDOL(), 0, aidInfo.getLenOfDefaultTDOL()) +
                    "\nApp Version :" + ISOUtil.bcd2str(aidInfo.getApplicationVersion(), 0, aidInfo.getApplicationVersion().length) +
                    "\nLength TRM :" + Integer.toString(aidInfo.getLenOfTerminalRiskManagementData()) +
                    "\nTRM :"  + ISOUtil.bcd2str(aidInfo.getTerminalRiskManagementData(), 0, aidInfo.getTerminalRiskManagementData().length) + "\n\n";
        }


        CAPublicKey capk = new CAPublicKey();
        int cantCapks = emvHandler.getCAPublicKeyNum();
        for (int i=0 ; i< cantCapks;i++){
            capk = emvHandler.getCAPubliKeyByNo(i);

            mensaje  += "REGISTRO CAPK: " + Integer.toString(i) + "/" + Integer.toString(cantCapks)+
                    "\nIndex :" + Integer.toString(capk.getIndex() ) + "\nRID :" + ISOUtil.bcd2str(capk.getRID(), 0, capk.getRID().length) +
                    "\nLen Modulus %:" + Integer.toString(capk.getLenOfModulus()) + " - Len Exponent % : " + Integer.toString(capk.getLenOfExponent()) +
                    "\nModulus : " + ISOUtil.bcd2str(capk.getModulus(), 0, capk.getLenOfModulus()) +
                    "\nExponent : " + ISOUtil.bcd2str(capk.getExponent(), 0, capk.getLenOfExponent()) +
                    "\nExpiration Date : " + ISOUtil.bcd2str(capk.getExpirationDate(), 0, capk.getExpirationDate().length) +
                    "\nExponent : " + ISOUtil.bcd2str(capk.getChecksum(), 0, capk.getChecksum().length) + "\n\n";
        }

        return mensaje;
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Configuración.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Configuración, desde método temporizador, ingresa a administrativo.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_7_Configuracion.this, MainMenu10_Administrativo.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Configuración, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Configuración, desde onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }

}