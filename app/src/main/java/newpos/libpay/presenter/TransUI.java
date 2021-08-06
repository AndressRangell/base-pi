package newpos.libpay.presenter;

import android.app.Activity;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;

import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu6_1_ConsultaPersonas;
import newpos.libpay.device.card.CardInfo;
import newpos.libpay.device.pinpad.OfflineRSA;
import newpos.libpay.device.pinpad.PinInfo;
import newpos.libpay.device.scanner.QRCInfo;
import newpos.libpay.device.user.OnUserResultListener;
import newpos.libpay.trans.translog.TransLogData;

/**
 * Created by zhouqiang on 2017/3/15.
 * @author zhouqiang
 * 交易UI接口类
 */

public interface TransUI {


    /**
     * 获取外界卡片UI接口(提示用户用卡)
     *
     * @return
     */
    CardInfo getCardUse(int timeout, int mode, int tipo);


    CardInfo getCedulaNfc(int timeout, int mode);

    /**
     * 获取密码键盘输入联机PIN
     *
     * @param timeout
     * @param amount
     * @param cardNo
     */
    PinInfo getPinpadOnlinePin(int timeout, String amount, String cardNo, String title);

    /**
     * @param timeout
     * @param i
     * @param key
     * @param offlinecounts
     * @return
     */
    PinInfo getPinpadOfflinePin(int timeout, int i, OfflineRSA key, int offlinecounts);


    /**
     * showMsgConfirm
     *
     * @param title
     * @param text
     */
    int showMsgConfirm(int timeout, final String title, final String text);

    /**
     * 人机交互显示UI接口(多应用卡片选择)
     *
     * @param timeout
     * @param list
     * @return
     */
    int showCardApplist(int timeout, String[] list);



    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout
     * @param status  TransStatus 状态标志以获取详细错误信息
     */
    void handling(String titulo, int timeout, String status);

    void newHandling(String titulo, int timeout, int status);

    void imprimiendo(int timeout, int status);

    void almacenarRecibo(boolean secuencial);

    void actualizarEstadoCierre();

    /**
     * 交易成功处理结果
     *
     * @param code
     */
    void trannSuccess(int timeout, int code, String... args);

    void showSuccess(int timeout, int code, String masInfo);

    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param errcode 实际代码错误返回码
     */
    void showError(int timeout, int errcode);

    void showMsgError(int timeout, String MsgError);

    /**
     * @param cls
     */
    void showView(final Class<?> cls);

    /**
     * @param timeout
     * @param title
     * @return
     */
    InputInfo showAmountCnb(int timeout, final String title);

    InputInfo showPrincipales(int timeout, final String title);

    InputInfo showAdministrativas(int timeout, final String title);

    InputInfo showVisita(int timeout, final String title);

    InputInfo showRecaudaciones(int timeout, final String title);

    InputInfo showList(int timeout, final String title, final String[] menu);

    InputInfo showConsultas(int timeout, final String title, final long costo);

    InputInfo showInformacion(int timeout, final String title);

    InputInfo show_input_text_date(int timeout, final String title, String mensaje);

    InputInfo showMsgConfirmacion(int timeout, final String[] mensajes, boolean corregir);

    InputInfo showMontoVariable(int timeout, final String[] mensajes);

    InputInfo showContrapartida(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo);

    InputInfo showContrapartidaRecaud(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo);

    InputInfo showIngreso2EditText(String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo);

    InputInfo show1Fecha1EditText(String[] mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo);

    InputInfo showEmpresasPrediosView(int timeout, final String[] mensaje, final int maxLeng[], final int inputType, final int carcRequeridos);

    InputInfo showBonoDesarrollo(int timeout, String titulo, final String[] mensaje, final int maxLeng[], final int inputType, final int carcRequeridos);

    InputInfo showIngresoDocumento(int timeout);

    InputInfo showCuentaYMontoDeposito(int timeout);

    InputInfo showFechaHora(int timeout, String titulo, boolean flag);

    InputInfo showFecha(int timeout, String titulo);

    InputInfo showBotones(final int cantBtn, final String[] btnTitulo, final String title);

    InputInfo showListCierre(int timeout, final String title);

    InputInfo showConfirmacion(int timeout, final String mensajes[]);

    InputInfo showMsgConfirmacionRemesa(int timeout, final String[] mensajes, boolean corregir);

    InputInfo showMsgConfirmacionCheck(int timeout, final String[] mensajes);

    InputInfo showVerficacionFaceId(int timeout);

    InputInfo showDatosDireccion(int timeout, String titulo, final String[] mensajes);

    InputInfo showDatosComplementarios(int timeout, String titulo, final String[] mensajes);

    InputInfo showInfoKit(int timeout, String titulo, final String[] mensajes, final String[] contenido);

    InputInfo showContrato(int timeout, String titulo, final String[] mensajes);

    InputInfo showIngresoOTP(int timeout, String titulo, final String[] mensajes);

    InputInfo showMensajeConfirmacion(int timeout, String titulo, final String[] mensajes);

    InputInfo showReposicionInfoKit(int timeout, String titulo, final String[] mensajes, final String[] contenido);

    InputInfo showSuccesView(int timeout, String titulo, String mensaje, boolean isSucces, boolean isButtonCancelar);

    void handlingOTP(boolean flag, String info);

    InputInfo showIngreso2EditTextMonto(int timeout, String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo);
}