package newpos.libpay.trans;

/**
 * Created by zhouqiang on 2017/3/26.
 * @author zhouqiang
 * 交易代码集合
 */

public class Tcode {
    public static final int T_success = 0 ;//成功
    public static final int T_socket_err = 101 ;//Socket连接失败、999999999999
    public static final int T_send_err = 102 ;//发送数据失败
    public static final int T_receive_err = 103 ;//接收数据失败
    public static final int T_user_cancel_input = 104 ;//用户取消输入
    public static final int T_invoke_para_err = 105 ;//调用传参错误
    public static final int T_wait_timeout = 106 ;//等待超时
    public static final int T_search_card_err = 107 ;//寻卡错误
    public static final int T_sdk_err = 107 ;//SDK异常
    public static final int T_ic_power_err = 108 ;//IC卡上电错误
    public static final int T_ic_not_exist_err = 109 ;//IC卡不在位
    public static final int T_ped_card_err = 110 ;//计算PINBlock卡号错误
    public static final int T_user_cancel_pin_err = 111 ;//用户取消PIn输入
    public static final int T_print_no_log_err = 112 ;//无此交易日志
    public static final int T_terminal_no_aid = 113 ;//终端无AID
    public static final int T_not_find_trans = 114 ;//未查询到改交易
    public static final int T_trans_is_voided = 115 ;//此交易已撤销
    public static final int T_user_cancel_operation = 116 ;//用户取消操作
    public static final int T_original_trans_can_not_void = 117 ;//原交易不能撤销
    public static final int T_void_card_not_same = 118 ;//撤销交易非原交易卡
    public static final int T_package_mac_err = 119 ;//返回报文MAC校验错误
    public static final int T_package_illegal = 120 ;//返回报文非法处理
    public static final int T_receive_refuse = 121 ;//拒绝
    public static final int T_qpboc_read_err = 122 ;//非接QPBOC读卡信息失败
    public static final int T_sale_tc_err = 123 ;//联机消费不允许脱机
    public static final int T_select_app_err = 124 ;//选择应用失败
    public static final int T_read_app_data_err = 125 ;//读取卡片应用数据失败
    public static final int T_offline_dataauth_err = 126 ;//脱机数据认证失败
    public static final int T_card_holder_auth_err = 127 ;//持卡人认证失败
    public static final int T_terminal_action_ana_err = 128 ;//终端行为分析失败
    public static final int T_must_online = 129 ;//此交易必须联机
    public static final int T_read_ec_amount_err = 130 ;//读取电子现金余额失败
    public static final int T_quick_pass_first_offline = 132 ;//快速消费应优先选择脱机
    public static final int T_batch_no_trans = 133 ;//本批次暂无任何需要上送的交易
    public static final int T_pboc_refuse = 134 ;//PBOC流程拒绝
    public static final int T_ic_not_allow_swipe = 135 ;//IC卡不允许降级交易
    public static final int T_master_pass_err = 136 ;//主管密码错误
    public static final int T_settle_tc_send_err = 137; //结算交易明细上送失败
    public static final int T_reversal_fail = 138; //冲正失败，联系管理员
    public static final int T_qpboc_errno = 139; //非接读卡异常
    public static final int T_amount_not_same = 140; //金额与原交易金额不一致
    public static final int T_user_cancel = 141; //用户取消操作
    public static final int T_refund_amount_beyond = 142; //退货金额超限
    public static final int T_printer_exception = 143; //打印机异常
    public static final int T_scanner_user_exit = 144; //用户退出扫描
    public static final int T_scanner_timeout = 145; //扫描超时
    public static final int T_gen_2_ac_fail = 146 ; //二次授权失败
    public static final int T_orignal_comp = 147 ; //原交易已完成
    public static final int T_batch_empy = 148 ;//未知错误
    public static final int T_unknow_err = 999 ;//未知错误
    public static final int transReversada = 1231;
    public static final int realizarReverso = 1100;

    public interface Status{
        public int downing_capk = 1 ;
        public int downing_aid = 2 ;
        public int downing_succ = 3 ;
        public int terminal_logon = 4 ;
        public int terminal_logonout = 5 ;
        public int connecting_center = 6 ;
        public int printing_recept = 7 ;
        public int printing_details = 20 ;
        public int terminal_reversal = 21 ;
        public int printer_lack_paper = 22 ;
        public int sale_succ = 8 ;
        public int enquiry_succ = 9 ;
        public int void_succ = 10 ;
        public int ecenquiry_succ = 11 ;
        public int quickpass_succ = 12 ;
        public int logon_succ = 13 ;
        public int logonout_succ = 14 ;
        public int handling = 15 ;
        public int settling_start = 16 ;
        public int settling_send_trans = 17 ;
        public int settling_over = 18 ;
        public int settling_succ = 19 ;
        public int settle_send_shell = 23 ;
        public int settle_send_reversal = 24 ;
        public int logon_down_succ = 25 ;
        public int send_over_2_recv = 26 ;
        public int pre_auth_success = 27 ;
        public int pre_auth_comp_success = 28 ;
        public int pre_auth_comp_void_success = 29 ;
        public int pre_auth_void_success = 30 ;
        public int refund_success = 31 ;
        public int scan_pay_success = 32 ;
        public int send_data_2_server = 33 ;
        public int scan_void_success = 34 ;
        public int scan_refund_success = 35 ;
        public int scan_success = 36 ;

        public int terminal_Initialization = 37 ;
        public int Init_success = 38 ;
        public int initialization_emv = 39;
        public int init_emv_success = 40;

        public int retirement = 41;
        public int retirement_success = 42;
        public int creacionUsuarioCorrecto = 43;
        public int enviarConfirmacion = 44;
        public int consultaSaldoExitoso = 45;
        public int cons10UltExitoso = 46;
        public int operacionRealizadaConExito = 47;
        public int cierre_descuadrado = 48 ;
        public int reverso_exitoso = 49;
        public int NoHayReverso = 50;
        public int DepositoCorrectoValor = 51;
        public int InformeCorrecto = 52;
        public int impresionExitosa = 53;
        public int recaudExitosa = 54;
        public int recargaExitosa = 55;
    }

    public interface Mensajes{
        int inicalizacion = 0;
        int cierreTotal = 1;
        int conectandoConBanco = 2;
        int reversoExitoso = 3;
        int recibiendoInfoServidor = 4;
        int enviandoReverso = 5;
        int imprimiendoRecibo = 6;
        int conectandoServidor = 7;
        int transaccionEnProceso = 8;
        int downloadingEmvParas = 9;
        int downloadingEmvCapks = 10;
        int inicializandoEmv = 11;
        int cierreDescuadrado = 12;
        int depositoCorrecto = 13;
        int retiroExitoso = 14;
        int operacionRealizadaConExito = 15;
        int usuarioCreadoConExito = 16;
        int consultaExitosa = 17;
        int informeCorrecto = 18;
        int inicializacionExitosa = 19;
        int impresionExitosa = 20;
        int recaudacionExitosa = 21;
        int recargaExitosa = 22;
        int ordenPagoExitosa = 23;
        int impresoraSinPapel = 24;
        int pagoExitoso = 25;
        int cierePorCambioDeOperacion = 26;
        int cierreDeSesionExitoso = 27;
        int procesandoRespuesta = 28;
        int errorEnvio_generandoReverso = 29;
        int errorRecibir_generandoReverso = 30;
        int consultaAutomaticaEnProceso = 31;
        int consultaAutomaticaExitosa = 32;
    }

    public interface error{
        String fallido = "Fallido";
    }
}
