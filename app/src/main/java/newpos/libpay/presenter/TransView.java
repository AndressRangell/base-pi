package newpos.libpay.presenter;

import com.android.desert.keyboard.InputManager;

import java.util.List;

import newpos.libpay.device.user.OnUserResultListener;
import newpos.libpay.trans.pichincha.Tools.itemMenu;
import newpos.libpay.trans.translog.TransLogData;

/**
 * Created by zhouqiang on 2017/4/25.
 * 交易用户显示接口
 * @author zhouqiang
 * 面向用户的接口
 */

public interface TransView {
    /**
     * 通知上层UI显示刷卡样式
     * @param timeout 超时时间 单位 秒
     * @param mode 输入模式，详见@{@link newpos.libpay.device.card.CardManager}
     */
    void showCardView(int timeout, int mode, int tipo, OnUserResultListener l);

    void showCedulaNfcView(int timeout, int mode, OnUserResultListener l);

    /**
     * showMsgConfirm
     * @param timeout 超时时间
     * @param title 当前卡号
     * @param text 当前卡号
     * @param l 需要上层通过此接口给底层回调通知 详见@{@link OnUserResultListener}
     */
    void showMsgConfirmView(int timeout, final String title, final String text, OnUserResultListener l);

    /**
     *  获取输入信息
     * @param type 输入类型 @{@link InputManager.Mode}
     * @return 输入结果
     */
    String getInput(InputManager.Mode type);

    /**
     * 通知UI显示卡片多应用
     * @param timeout 超时时间
     * @param apps 应用列表
     * @param l 需要上层通过此接口给底层回调用户行为 详见@{@link OnUserResultListener}
     */
    void showCardAppListView(int timeout, String[] apps, OnUserResultListener l);



    /**
     *  通知UI交易结束成功后续处理
     * @param timeout 超时时间
     * @param info 交易结果详情
     */
    void showSuccess(int timeout, String info);

    void showSuccessView(int timeout, int code, String info);

    /**
     *  通过UI交易结束失败后续处理
     * @param timeout 超时时间
     * @param err 错误详情信息
     */
    void showError(int timeout, String err);

    void showMsgError(int timeout, String MsgError);

    /**
     * 通知UI显示交易进行到某一状态
     * @param timeout 超时时间
     * @param status 状态信息
     */
    void showMsgInfo(String titulo, int timeout, String status);

    void showMsgInfo(String titulo, int timeout, int posicion);

    void showImprimiendo(int timeout, int status);

    void showAlmacenarRecibo(boolean secuencial);

    void actualizarEstadoCierre();

    /**
     *
     * @param cls
     */
    void showView(final Class<?> cls);



    /**
     *
     * @param timeout
     * @param title
     * @param l
     */
    void showAmountCnbView(int timeout, final String title, OnUserResultListener l);

    void showPrincipalesView(int timeout, final String title, OnUserResultListener l);

    void showAdministrativasView(int timeout, final String title, OnUserResultListener l);

    void showVisitaView(int timeout, final String title, OnUserResultListener l);

    void showRecaudacionesView(int timeout, final String title, OnUserResultListener l);

    void showEmpresasPrediosView(int timeout, final String[] mensaje, final int maxLeng[], final int inputType, final int carcRequeridos, OnUserResultListener l);

    void showBonoDesarrollo(int timeout, String tiutlo, final String[] mensaje, final int maxLeng[], final int inputType, final int carcRequeridos, OnUserResultListener l);

    void showConsultasView(int timeout, final String title,final long costo, OnUserResultListener l);

    void showListView(int timeout, final String title, final String[] menu, OnUserResultListener l);

    void showInformacionVisitaView(int timeout, final String title, OnUserResultListener l);

    void show_input_text_date_view(int timeout, final String title, String mensaje, OnUserResultListener l);

    void showMsgConfirmacionView(int timeout, final String mensajes[], boolean corregir, OnUserResultListener l);

    void showMontoVariableView(int timeout, final String mensajes[], OnUserResultListener l);

    void showContrapartidaView(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo, OnUserResultListener l);

    void showContrapartidaRecaudView(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo, OnUserResultListener l);

    void showIngreso2EditTextView(String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo, OnUserResultListener l);

    void show1Fecha1EditTextView(String[] mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo, OnUserResultListener l);

    void showIngresoDocumentoView(int timeout, OnUserResultListener l);

    void showCuentaYMontoDepositoView(int timeout, OnUserResultListener l);

    void showFechaHoraView(int timeout, String titulo, boolean flag, OnUserResultListener l);

    void showFechaView(int timeout, String titulo, OnUserResultListener l);

    void showBotonesView(final int cantBtn, final String[] btnTitulo, final String title, OnUserResultListener l);

    void showListCierreView(int timeout, final String title, OnUserResultListener l);

    void showConfirmacionView(int timeout, final String mensajes[], OnUserResultListener l);

    void showMsgConfirmacionRemesaView(int timeout, final String mensajes[], boolean corregir, OnUserResultListener l);

    void showMsgConfirmacionCheckView(int timeout, final String mensajes[], OnUserResultListener l);

    void showVerficacionFaceId(int timeout, OnUserResultListener l);

    void showDatosDireccion(int timeout, String titulo, final String mensajes[], OnUserResultListener l);

    void showDatosComplementarios(int timeout, String titulo, final String mensajes[], OnUserResultListener l);

    void showInfoKit(int timeout, String titulo, final String mensajes[], final String[] contenido, OnUserResultListener l);

    void showContrato(int timeout, String titulo, final String mensajes[], OnUserResultListener l);

    void showIngresoOTP(int timeout, String titulo, final String mensajes[], OnUserResultListener l);

    void showMensajeConfirmacion(int timeout, String titulo, final String mensajes[], OnUserResultListener l);

    void showReposicionInfoKit(int timeout, String titulo, final String mensajes[], final String[] contenido, OnUserResultListener l);

    void showSuccesView(int timeout, String titulo, String mensaje, boolean isSucces, boolean isButtonCancelar, OnUserResultListener l);

    void handlingOTP(boolean flag, String info);

    void showIngreso2EditTextMonto(int timeout, String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo, OnUserResultListener l);
}
