package newpos.libpay.device.printer;

import java.util.Locale;

/**
 * Created by zhouqiang on 2017/4/4.
 * 打印常量类
 * @author zhouqiang
 */

public class PrintRes {
    public interface CH{
        boolean zh = Locale.getDefault().getLanguage().equals("zh");
        String WANNING = zh?"警告:该固件是测试版本，不能用于商业用途，在此版本上进行交易可能危害到持卡人的用卡安全。" : "Warning:Debug firmware,use for commercial forbidden,it will be hurt the benefit of cardholder through this version.";
        String MERCHANT_COPY = zh?"商户存根     MERCHANT COPY" :"MERCHANT COPY";
        String CARDHOLDER_COPY = zh?"持卡人存根     CARDHOLDER COPY" :"CARDHOLDER COPY";
        String BANK_COPY = zh?"银行存根     BANK COPY" :"BANK COPY";
        String MERCHANT_NAME = zh?"商户名称(MERCHANT NAME):" :"MERCHANT NAME:";
        String MERCHANT_ID = zh?"商户编号(MERCHANT NO):" :"MERCHANT NO:";
        String TERNIMAL_ID = zh?"终端编号(TERMINAL NO):" :"TERMINAL NO:";
        String OPERATOR_NO = zh?"操作员号(OPERATOR NO):" :"OPERATOR NO";
        String ISSUER = zh?"发卡行(ISSUER):  中信银行" :"ISSUER : China Bank";
        String ACQUIRER = zh?"收单行(ACQ):  银联商务" :"ACQ : Unionpay";
        String TRANS_TYPE = zh?"交易类型(TXN. TYPE):" :"TXN. TYPE :";
        String CARD_EXPDATE = zh?"卡有效期(EXP. DATE):" :"EXP. DATE:";
        String BATCH_NO = zh?"批次号(BATCH NO):" :"BATCH NO:";
        String VOUCHER_NO = zh?"凭证号(VOUCHER NO):" :"VOUCHER NO:";
        String DATE_TIME = zh?"日期/时间(DATE/TIME):" :"DATE/TIME:";
        String REF_NO = zh?"交易参考号(REF. NO):" :"REF. NO:";
        String AMOUNT = zh?"金额(AMOUNT):" :"AMOUNT:";
        String RMB = zh?"RMB:" :"$:";
        String REPRINT = zh?"***** 重打印 *****" :"***** REPRINT *****";
        String SETTLE_SUMMARY = zh?"结算总计单" :"Settle Sum Receipt";
        String SETTLE_LIST = zh?"类型/TYPE      笔数/SUM      金额/AMOUNT" :"TYPE      SUM      AMOUNT";
        String SETTLE_INNER_CARD = zh?"内卡：对账平" :"Inner card；Reconciliation";
        String SETTLE_OUTER_CARD = zh?"外卡：对账平" :"Outer card:Reconciliation";
        String SETTLE_DETAILS = zh?"结算明细单" :"Settle Detail Receipt";
        String SETTLE_DETAILS_LIST_CH = zh?"凭证号   类型   授权码   金额   卡号" :"VOUCHER     TYPE     AUTHNO     AMOUNT    CARDNO";
        String SETTLE_DETAILS_LIST_EN = zh?"VOUCHER     TYPE     AUTHNO     AMOUNT    CARDNO" :"VOUCHER     TYPE     AUTHNO     AMOUNT    CARDNO";
        String DETAILS = zh?"交易明细" :"Transaction Details";
    }

    public static final String[] TRANSCH = {

    };

    public static final String[] TRANSEN = {
            "INIT",
            "INIT_EMV",
            "RETIRO",
            "RETIRO_C",
            "CREARUSUARIO",
            "RECARGA",
            "DEPOSITO",
            "CONSULTA",
            "SALDOCONSULTA",
            "SALDOCUENTA",
            "COSTOSERVICIO",
            "VISITAFUNCIONARIO",
            "IP",
            "CIERRE_TOTAL",
            "CUPON_HISTORICO",
            "REVERSO"  ,
            "BIMO",
            "RECAU", //17
            "LOGINCENTRALIZADO",
            "DESEMBOLSOCREDITO",
            "GIROSYREMESAS",
            "CONSULTAVALORCREDITO",
            "CUENTAXPERTA",
            "CAMBIOCLAVE"
    };
}
