package newpos.libpay;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.pos.device.SDKManager;
import com.pos.device.SDKManagerCallback;

import newpos.libpay.device.card.CardManager;
import newpos.libpay.global.TMConfig;
import newpos.libpay.global.TMConstants;
import newpos.libpay.paras.EmvAidInfo;
import newpos.libpay.paras.EmvCapkInfo;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.presenter.TransUIImpl;
import newpos.libpay.presenter.TransView;
import newpos.libpay.trans.Trans;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.manager.SettleTrans;
import newpos.libpay.trans.pichincha.Administrativas.CambioClave;
import newpos.libpay.trans.pichincha.Administrativas.Consultas;
import newpos.libpay.trans.pichincha.Administrativas.CrearUsuario;
import newpos.libpay.trans.pichincha.Administrativas.CuponHistorico;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Administrativas.LoginCentral;
import newpos.libpay.trans.pichincha.Administrativas.ReversoPichincha;
import newpos.libpay.trans.pichincha.Administrativas.VisitaFuncioario;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.trans.pichincha.financieras.ConsultaValorCredito.ConsultaValorCredito;
import newpos.libpay.trans.pichincha.financieras.CuentaXperta.CuentaXperta;
import newpos.libpay.trans.pichincha.financieras.Deposito.Deposito;
import newpos.libpay.trans.pichincha.financieras.DesembolsoCredito.DesembolsoCredito;
import newpos.libpay.trans.pichincha.financieras.GirosyRemesas.GirosyRemesas;
import newpos.libpay.trans.pichincha.financieras.Retiro.Retiro;
import newpos.libpay.trans.pichincha.financieras.bimo.Bimo;
import newpos.libpay.utils.PAYUtils;

/**
 * Created by zhouqiang on 2017/4/25.
 * @author zhouqiang
 * 支付sdk管理者
 */
public class PaySdk {

    /**
     * 单例
     */
    private static PaySdk mInstance = null ;

    /**
     * 上下文对象，用于获取相关资源和使用其相应方法
     */
    private Context mContext = null ;

    /**
     * 获前端段activity对象，主要用于扫码交易
     */
    private AppCompatActivity mActivity = null ;

    /**
     * MVP交媾P层接口，用于对m和v的交互
     */
    private TransPresenter presenter = null;

    /**
     * 标记sdk环境前端是否进行初始化操作
     */
    private static boolean isInit = false;

    /**
     * 初始化PaySdk环境的回调接口
     */
    private PaySdkListener mListener = null;

    /**
     * PaySdk产生的相关文件的保存路径
     * 如代码不进行设置，默认使用程序data分区
     * @link @{@link String}
     */
    private String cacheFilePath = null;

    /**
     * 终端参数文件路径,用于设置一些交易中的偏好属性
     * 如代码不进行设置，默认使用程序自带配置文件
     * @link @{@link String}
     */
    private String paraFilepath = null;

    public Context getContext() throws PaySdkException {
        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        return mContext;
    }

    public PaySdk setActivity(AppCompatActivity activity) {
        this.mActivity = activity;
        return mInstance;
    }

    public PaySdk setParaFilePath(String path) {
        this.paraFilepath = path;
        return mInstance;
    }

    public String getParaFilepath() {
        return this.paraFilepath;
    }

    public PaySdk setCacheFilePath(String path) {
        this.cacheFilePath = path;
        return mInstance;
    }

    public String getCacheFilePath() {
        return this.cacheFilePath;
    }

    private PaySdk() {
    }

    public PaySdk setListener(PaySdkListener listener) {
        this.mListener = listener;
        return mInstance;
    }

    public static PaySdk getInstance() {
        if (mInstance == null) {
            mInstance = new PaySdk();
        }
        return mInstance;
    }

    public void init(Context context) throws PaySdkException {
        this.mContext = context;
        this.init();
    }

    public void init(Context context, PaySdkListener listener) throws PaySdkException {
        this.mContext = context;
        this.mListener = listener;
        this.init();
    }

    public void init() throws PaySdkException {

        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }

        if (this.paraFilepath == null || !this.paraFilepath.endsWith("properties")) {
            this.paraFilepath = TMConstants.DEFAULTCONFIG;
        }

        if (this.cacheFilePath == null) {
            this.cacheFilePath = mContext.getFilesDir() + "/";
        } else if (!this.cacheFilePath.endsWith("/")) {
            this.cacheFilePath += "/";
        }

        TMConfig.setRootFilePath(this.cacheFilePath);
        if (!TMConfig.getInstance().isOnline()) {
            PAYUtils.copyAssetsToData(this.mContext, EmvAidInfo.FILENAME);
            PAYUtils.copyAssetsToData(this.mContext, EmvCapkInfo.FILENAME);
        }
        SDKManager.init(mContext, new SDKManagerCallback() {
            @Override
            public void onFinish() {
                isInit = true;

                if (mListener != null) {
                    mListener.success();
                }
            }
        });
    }

    /**
     * 释放卡片驱动资源
     */
    public void releaseCard() {
        if (isInit) {
            CardManager.getInstance(0).releaseAll();
        }
    }

    /**
     * 释放sdk环境资源
     */
    public void exit() {
        if (isInit) {
            SDKManager.release();
            isInit = false;
        }
    }

    public void startTrans(String transType, String tipoTrans, TransView tv) throws PaySdkException {
        InitTrans.wrlg.wrDataTxt("StartTrans - " + transType + " - " + tipoTrans);
        if (this.mActivity == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        TransInputPara para = new TransInputPara();
        para.setTransUI(new TransUIImpl(mActivity, tv));
        if (transType.equals(Trans.Type.INIT)) {
            para.setTransType(Trans.Type.INIT);
            presenter = new InitTrans(this.mContext, Trans.Type.INIT, para);
        }
        if (transType.equals(Trans.Type.RETIRO)) {
            para.setTransType(Trans.Type.RETIRO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new Retiro(this.mContext, Trans.Type.RETIRO, para);
        }
        if (transType.equals(Trans.Type.CREARUSUARIO)) {
            para.setTransType(Trans.Type.CREARUSUARIO);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new CrearUsuario(this.mContext, Trans.Type.CREARUSUARIO, para);
        }
        if (transType.equals(Trans.Type.VISITAFUNCIONARIO)) {
            para.setTransType(Trans.Type.VISITAFUNCIONARIO);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new VisitaFuncioario(this.mContext, Trans.Type.VISITAFUNCIONARIO, para);
        }
        if (transType.equals(Trans.Type.CONSULTA)) {
            para.setTransType(Trans.Type.CONSULTA);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedPass(true);
            para.setEmvAll(true);
            presenter = new Consultas(this.mContext, Trans.Type.CONSULTA, para, tipoTrans);
        }
        if (transType.equals(Trans.Type.DEPOSITO)) {
            para.setTransType(Trans.Type.DEPOSITO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new Deposito(this.mContext, Trans.Type.DEPOSITO, para);
        }
        if(transType.equals(Trans.Type.IP)){
            para.setTransType(Trans.Type.IP);
            presenter = new InitTrans(this.mContext , Trans.Type.IP , para);
        }
        if (transType.equals(Trans.Type.CIERRE_TOTAL)) {
            para.setTransType(Trans.Type.CIERRE_TOTAL);
            presenter = new SettleTrans(this.mContext, Trans.Type.CIERRE_TOTAL, para);
        }
        if (transType.equals(Trans.Type.CUPON_HISTORICO)) {
            para.setTransType(Trans.Type.CUPON_HISTORICO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            presenter = new CuponHistorico(this.mContext, Trans.Type.CUPON_HISTORICO, para,tipoTrans);
        }
        if (transType.equals(Trans.Type.REVERSO)){
            para.setTransType(Trans.Type.REVERSO);
            para.setNeedOnline(true);
            presenter = new ReversoPichincha(this.mContext, Trans.Type.REVERSO, para);
        }if (transType.equals(Trans.Type.RECAU)){
            para.setTransType(Trans.Type.RECAU);
            para.setNeedOnline(true);
            para.setNeedAmount(true);
            presenter = new Recaudaciones(this.mContext, Trans.Type.RECAU, tipoTrans, para);
        }
        if (transType.equals(Trans.Type.BIMO)) {
            para.setTransType(Trans.Type.BIMO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            presenter = new Bimo(this.mContext, Trans.Type.BIMO, para);
        }
        if (transType.equals(Trans.Type.LOGINCENTRALIZADO)) {
            para.setTransType(Trans.Type.LOGINCENTRALIZADO);

            presenter = new LoginCentral(this.mContext, Trans.Type.LOGINCENTRALIZADO,tipoTrans, para);
        }
        if (transType.equals(Trans.Type.DESEMBOLSOCREDITO)) {
            para.setTransType(Trans.Type.DESEMBOLSOCREDITO);
            para.setNeedOnline(true);
            para.setNeedAmount(true);
            presenter = new DesembolsoCredito(this.mContext, Trans.Type.DESEMBOLSOCREDITO, para);
        }
        if (transType.equals(Trans.Type.CONSULTAVALORCREDITO)) {
            para.setTransType(Trans.Type.CONSULTAVALORCREDITO);
            para.setNeedOnline(true);
            para.setNeedAmount(true);
            presenter = new ConsultaValorCredito(this.mContext, Trans.Type.CONSULTAVALORCREDITO, para);
        }
        if (transType.equals(Trans.Type.GIROSYREMESAS)) {
            para.setTransType(Trans.Type.GIROSYREMESAS);
            para.setNeedOnline(true);
            para.setNeedAmount(false);
            presenter = new GirosyRemesas(this.mContext, Trans.Type.GIROSYREMESAS, tipoTrans, para);
        }
        if (transType.equals(Trans.Type.CUENTAXPERTA)) {
            para.setTransType(Trans.Type.CUENTAXPERTA);
            para.setNeedOnline(true);
            para.setNeedAmount(false);
            presenter = new CuentaXperta(this.mContext, Trans.Type.CUENTAXPERTA, para);
        }
        if (transType.equals(Trans.Type.CAMBIOCLAVE)) {
            para.setTransType(Trans.Type.CAMBIOCLAVE);
            para.setNeedOnline(true);
            para.setNeedAmount(false);
            para.setNeedPass(true);
            para.setChangePin(true);
            presenter = new CambioClave(this.mContext, Trans.Type.CAMBIOCLAVE, para);
        }
        if (isInit) {
            new Thread() {
                @Override
                public void run() {
                    presenter.start();
                }
            }.start();
        } else {
            throw new PaySdkException(PaySdkException.NOT_INIT);
        }
    }
}
