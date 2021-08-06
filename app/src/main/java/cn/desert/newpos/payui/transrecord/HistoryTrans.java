package cn.desert.newpos.payui.transrecord;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;

import java.util.ArrayList;
import java.util.List;

import cn.desert.newpos.payui.base.PayApplication;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.card.CardInfo;
import newpos.libpay.device.pinpad.OfflineRSA;
import newpos.libpay.device.pinpad.PinInfo;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.device.scanner.QRCInfo;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.PAYUtils;

public class HistoryTrans extends AppCompatActivity implements
        HistorylogAdapter.OnItemReprintClick,TransUI{

    ListView lv_trans ;
    View view_nodata ;
    View view_reprint ;
    EditText search_edit ;
    ImageView search ;
    LinearLayout z ;
    LinearLayout root ;
    TextView tv1 ;

    private HistorylogAdapter adapter;
    private boolean isSearch = false ;
    public static final String EVENTS = "EVENTS" ;
    public static final String LAST = "LAST" ;
    public static final String COMMON = "COMMON" ;
    public static final String ALL = "ALL" ;
    private boolean isCommonEvents = false ;
    private PrintManager manager = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_historial_trans);
        PayApplication.getInstance().addActivity(this);
        lv_trans = (ListView) findViewById(R.id.history_lv);
        tv1 = findViewById(R.id.tv1);
        view_nodata = findViewById(R.id.history_nodata);
        view_reprint = findViewById(R.id.reprint_process);
        search_edit = (EditText) findViewById(R.id.history_search_edit);
        search = (ImageView) findViewById(R.id.history_search);
        z = (LinearLayout) findViewById(R.id.history_search_layout);
        root = (LinearLayout) findViewById(R.id.transaction_details_root);
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1.setTypeface(tipoFuente1);
        adapter = new HistorylogAdapter(this , this) ;
        lv_trans.setAdapter(adapter);
        view_reprint.setVisibility(View.GONE);
        search.setOnClickListener(new SearchListener());
        manager = PrintManager.getmInstance(this , this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String even = bundle.getString(HistoryTrans.EVENTS);
            if(even.equals(LAST)){
                re_print(TransLog.getInstance().getLastTransLog());
            }else if(even.equals(ALL)){
                printAll();
            }else {
                isCommonEvents = true ;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<TransLogData> list = TransLog.getInstance().getData();
        List<TransLogData> temp = new ArrayList<>() ;
        int num = 0 ;
        for (int i = list.size()-1  ; i >= 0 ; i--){
            temp.add(num , list.get(i));
            num++;
        }
        if (list.size() > 0) {
            showView(false);
            adapter.setList(temp);
            adapter.notifyDataSetChanged();
            isSearch = true ;
            search.setImageResource(android.R.drawable.ic_menu_search);
        } else {
            showView(true);
        }
    }

    private void showView(boolean isShow) {
        if (isShow) {
            lv_trans.setVisibility(View.GONE);
            view_nodata.setVisibility(View.VISIBLE);
        } else {
            lv_trans.setVisibility(View.VISIBLE);
            view_nodata.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnItemClick(String traceNO) {
        re_print(TransLog.getInstance().searchTransLogByTraceNo(traceNO));
    }

    private void re_print(final TransLogData data){
        view_reprint.setVisibility(View.VISIBLE);
        lv_trans.setVisibility(View.GONE);
        z.setVisibility(View.GONE);
        new Thread(){
            @Override
            public void run() {
                manager.print(data, true);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void printAll(){
        new Thread(){
            @Override
            public void run() {
                manager.printDetails();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            view_reprint.setVisibility(View.GONE);
            lv_trans.setVisibility(View.VISIBLE);
            z.setVisibility(View.VISIBLE);
            if(!isCommonEvents){
                finish();
            }
        }
    };

    private final class SearchListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(isSearch){
                String edit = search_edit.getText().toString() ;
                if(!PAYUtils.isNullWithTrim(edit)){
                    TransLog transLog = TransLog.getInstance() ;
                    TransLogData data = transLog.searchTransLogByTraceNo(edit);
                    if(data != null){
                        InputMethodManager imm = (InputMethodManager) HistoryTrans.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search_edit.getWindowToken(),0);
                        List<TransLogData> list = new ArrayList<>() ;
                        list.add(0 , data);
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                        search.setImageResource(android.R.drawable.ic_menu_revert);
                        isSearch = false ;
                    }else {
                        Toast.makeText(HistoryTrans.this ,
                                HistoryTrans.this.getResources().getString(R.string.not_any_record) ,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                loadData();
            }
        }
    }

    @Override
    public void handling(String titulo, int timeout, final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv_trans.setVisibility(View.GONE);
                z.setVisibility(View.GONE);
                view_reprint.setVisibility(View.VISIBLE);
                ((TextView)view_reprint.findViewById(R.id.handing_msginfo))
                        .setText(getResources().getString(R.string.re_print));
            }
        });
    }

    @Override
    public void newHandling(String titulo, int timeout, int status) {

    }

    @Override
    public void imprimiendo(int timeout, int status) {
    }

    @Override
    public void almacenarRecibo(boolean secuencial) {

    }

    @Override
    public void actualizarEstadoCierre() {

    }

    @Override
    public int showMsgConfirm(int timeout, String title, String text) {
        return 0;
    }

    @Override
    public int showCardApplist(int timeout, String[] list) {
        return 0;
    }

    @Override
    public void trannSuccess(int timeout, int code, String... args) {

    }

    @Override
    public void showSuccess(int timeout, int code, String masInfo) {

    }

    @Override
    public void showError(int timeout, int errcode) {

    }

    @Override
    public void showMsgError(int timeout, String MsgError) {

    }

    @Override
    public void showView(Class<?> cls) {

    }

    @Override
    public InputInfo showAmountCnb(int timeout, final String title) {
        return null;
    }

    @Override
    public InputInfo showPrincipales(int timeout, String title) {
        return null;
    }

    @Override
    public InputInfo showAdministrativas(int timeout, String title) {
        return null;
    }

    @Override
    public InputInfo showVisita(int timeout, String title){
        return null;
    }

    @Override
    public InputInfo showRecaudaciones(int timeout, String title) {
        return null;
    }

    @Override
    public InputInfo showConsultas(int timeout, String title,long costo) {
        return null;
    }

    @Override
    public InputInfo showList(int timeout, String title, String[] menu) {
        return null;
    }

    @Override
    public CardInfo getCardUse(int i, int i1, int tipo) {
        return null;
    }

    @Override
    public CardInfo getCedulaNfc(int timeout, int mode) {
        return null;
    }

    @Override
    public PinInfo getPinpadOnlinePin(int i, String s, String s1, String title) {
        return null;
    }

    @Override
    public PinInfo getPinpadOfflinePin(int i, int i1 , OfflineRSA rsaPinKey, int i2) {
        return null;
    }

    @Override
    public InputInfo showInformacion(int timeout, String title){
        return null;
    }

    @Override
    public InputInfo show_input_text_date(int timeout, String title, String mensaje) {
        return null;
    }

    @Override
    public InputInfo showMsgConfirmacion(int timeout, String[] mensajes, boolean setCorregir) {
        return null;
    }

    @Override
    public InputInfo showMontoVariable(int timeout, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showContrapartida(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        return null;
    }

    @Override
    public InputInfo showContrapartidaRecaud(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        return null;
    }

    @Override
    public InputInfo showIngreso2EditText(String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo) {
        return null;
    }

    @Override
    public InputInfo show1Fecha1EditText(String[] mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        return null;
    }

    @Override
    public InputInfo showEmpresasPrediosView(int timeout, String[] mensaje, int[] maxLeng, int inputType, int carcRequeridos) {
        return null;
    }

    @Override
    public InputInfo showBonoDesarrollo(int timeout, String titulo, String[] mensaje, int[] maxLeng, int inputType, int carcRequeridos) {
        return null;
    }

    @Override
    public InputInfo showIngresoDocumento(int timeout) {
        return null;
    }

    @Override
    public InputInfo showCuentaYMontoDeposito(int timeout) {
        return null;
    }

    @Override
    public InputInfo showFechaHora(int timeout, String titulo, boolean flag) {
        return null;
    }

    @Override
    public InputInfo showFecha(int timeout, String titulo) {
        return null;
    }

    @Override
    public InputInfo showBotones(int cantBtn, String[] btnTitulo, String title) {
        return null;
    }

    @Override
    public InputInfo showListCierre(int timeout,String title) {
        return null;
    }

    @Override
    public InputInfo showConfirmacion(int timeout, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showMsgConfirmacionRemesa(int timeout, String[] mensajes, boolean setCorregir) {
        return null;
    }

    @Override
    public InputInfo showMsgConfirmacionCheck(int timeout, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showVerficacionFaceId(int timeout) {
        return null;
    }

    @Override
    public InputInfo showDatosDireccion(int timeout, String titulo, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showDatosComplementarios(int timeout, String titulo, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showInfoKit(int timeout, String titulo, String[] mensajes, String[] contenido) {
        return null;
    }


    @Override
    public InputInfo showContrato(int timeout, String titulo, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showIngresoOTP(int timeout, String titulo, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showMensajeConfirmacion(int timeout, String titulo, String[] mensajes) {
        return null;
    }

    @Override
    public InputInfo showReposicionInfoKit(int timeout, String titulo, String[] mensajes, String[] contenido) {
        return null;
    }

    @Override
    public InputInfo showSuccesView(int timeout, String titulo, String mensaje, boolean isSucces, boolean isButtonCancelar) {
        return null;
    }

    @Override
    public void handlingOTP(boolean flag, String info) {

    }

    @Override
    public InputInfo showIngreso2EditTextMonto(int timeout, String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo) {
        return null;
    }
}
