package newpos.libpay.trans;

import android.content.Context;

import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn92;
import newpos.libpay.Logger;
import newpos.libpay.global.TMConfig;
import newpos.libpay.global.TMConstants;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.helper.iso8583.ISO8583reverse;
import newpos.libpay.helper.ssl.NetworkHelper;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.process.EmvTransaction;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 交易抽象类，定义所有交易类的父类
 * @author zhouqiang
 */
public abstract class Trans {

    /**
     * 上下文对象
     */
    protected Context context;

    /**
     * 8583组包解包
     */
    public static ISO8583 iso8583;

    public static ISO8583reverse iso8583reverse;

    /**
     * 网络操作对象
     */
    protected NetworkHelper netWork;

    /**
     * 交易记录的集合
     */
    protected TransLog transLog;

    /**
     * 配置文件操作实例
     */
    protected TMConfig cfg;

    /**
     * MODEL与VIEW层接口实例
     */
    protected TransUI transUI;

    /**
     * 等待页面超时时间
     */
    protected int timeout;

    /**
     * 返回值全局定义
     */
    protected int retVal;

    /**
     * EMV流程控制实例
     */
    protected EmvTransaction emv;

    /**
     * 交易相关参数集合
     */
    protected TransInputPara para;

	/**
	 * 交易类型定义
	 */
	public interface Type{
		 String INIT = "INIT" ;
		 String INIT_EMV = "INIT_EMV";
		 String RETIRO = "RETIRO";
		 String DEPOSITO = "DEPOSITO";
		 String RETIRO_C = "RETIRO_C" ;
		 String CREARUSUARIO = "CREARUSUARIO";
		 String RECARGA = "RECARGA";
		 String CONSULTA = "CONSULTA";
		 String SALDOCONSULTA = "SALDOCONSULTA";
		 String TENLASTMOVES = "TENLASTMOVES";
		 String SALDOCUENTA="SALDOCUENTA";
		 String VISITAFUNCIONARIO = "VISITAFUNCIONARIO";
		 String IP = "IP";
		 String CUPON_HISTORICO = "CUPON_HISTORICO";
		 String PRINT = "PRINT";
		 String REVERSO = "REVERSO";
		 String BIMO = "BIMO";
		 String RECAU = "RECAU";
         String LOGINCENTRALIZADO = "LOGINCENTRALIZADO";
         String DESEMBOLSOCREDITO = "DESEMBOLSOCREDITO";
         String GIROSYREMESAS = "GIROSYREMESAS";
         String CONSULTAVALORCREDITO = "CONSULTAVALORCREDITO";
         String CUENTAXPERTA = "CUENTAXPERTA";
         String CIERRE_TOTAL = "CIERRE_TOTAL";
         String REVERSAL = "REVERSAL";
         String SENDSCRIPT = "SENDSCRIPT";
         String LOGOUT = "LOGOUT";
         String REFUND = "REFUND";
         String CAMBIOCLAVE = "CAMBIOCLAVE";
    }



	/** Message field definition */
    public static final String CIERRE_TOTAL_LOG = "CIERRE_TOTAL_LOG";
	/**
	 * 0 MsgType
	 */

	public static String MsgID;


    protected String MsgID_Consulta = "0100";

    protected String ProcCode_consulta = "300000";

    protected String MsgID_cierre = "0320";

	protected String ProcCode_cierre = "950300";
	/**
	 * 2-PAN
	 */
	protected String Pan;

	/**
	 *  3-Proccesing Code
	 */
	public static String ProcCode;

	/**
	 * 4- Amount
	 */
	protected long Amount;

    /**
     * 4- Amount
     */
    protected long TotalAmount;

    protected long Comision;

    /**
     * 6-  Billing Amount
     */
    protected long Field06;

	/**
	 * 7- Transmit Date Time
	 */
	protected String Field07;


	/**
	 * 11-STAN
	 */
	protected String TraceNo;

    protected String TraceNo2;

    /**
     * 12 hhmmss*
     */
    protected String LocalTime;

	/**
	 * 13-MMDD*
	 */
	protected String LocalDate;

	/**
	 * 14-YYMM*
	 */
	protected String ExpDate;

	/**
	 * 15-MMDD*
	 */
	protected String SettleDate;

	/**
	 * 22-POS Entry Mode
	 */
	protected String EntryMode;

    /**
     * 23*
     */
    protected String PanSeqNo;

    /**
     * 25
     */
    protected String SvrCode;

    /**
     * 26
     */
    protected String CaptureCode;

    /**
     * 32*
     */
    protected String AcquirerID;

	/**
	 * Track 1
	 */
	protected String Track1;

	/**
	 * 35- Track II
	 */
	protected String Track2;

    /**
     * 36
     */
    protected String Track3;

    /**
     * 37*
     */
    protected String RRN;

    /**
     * 38*
     */
    protected String AuthCode;

	/**
	 * 39-Response Code
	 */
	protected String RspCode;

	/**
	 * 41- Terminal ID
	 */
	protected String TermID;

    /**
     * 42
     */
    protected String MerchID;
    /**
     * 43 *
     */
    protected String AcquirerName;

    /**
     * 44 *
     */
    protected String Field44;

    /**
     * 48 *
     */
    protected String Field48;

    /**
     * 49*
     */
    protected String CurrencyCode;

    /**
     * 52
     */
    protected String PIN;

    /**
     * 53
     */
    protected String SecurityInfo;

    /**
     * 54
     */
    protected String ExtAmount;

    /**
     * 55*
     */
    protected byte[] ICCData;

	/**
	 * 59
	 */
	protected String Field59;
	/**
	 * 60
	 */
	protected String Field60;

    /**
     * 61
     */
    protected String Field61;

    /**
     * 62
     */
    protected String Field62;

    /**
     * 63
     */
    protected String Field63;

	/**
	 * Trading Chinese name
	 */
	protected String TransCName;

	/**
	 * Transaction English Name Primary Key Transaction Initialization Settings
	 */
	protected String TransEName;

	/**
	 * Lot number 60_2
	 */
	protected String BatchNo;

	/**
	 * Mark whether the transaction serial number is increasing
	 */
	protected boolean isTraceNoInc = false;


    /**
     * TypeAccount
     */
    protected String TypeAccount;

    /**
     * Account
     */
    protected String Account;

	/**
	 * Cedula o RUC
	 */
	protected String Cedula_or_Ruc;

    /**
     * Token 07
     */
    protected String Tkn07;

    /**
     * Token 07
     */
    protected String Tkn08;


    /**
     * Token 47
     */
    protected String Tkn47;

    /**
     * 使用原交易的第3域和 60.1域
     */
    protected boolean isUseOrgVal = false;

    protected String F60_1;
    protected String F60_3;

    protected Tkn92 tkn92;

	/**
	 * 22 domain service point input method
	 */
	public static final int ENTRY_MODE_HAND = 1;
	public static final int ENTRY_MODE_MAG = 2;
	public static final int ENTRY_MODE_ICC = 5;
	public static final int ENTRY_MODE_NFC = 7;
	public static final int ENTRY_MODE_QRC = 9;

    protected boolean imprimirUltimaTRX = false;

    /***
     * Trans 构造
     * @param ctx
     * @param transEname
     */
    public Trans(Context ctx, String transEname) {
        this.context = ctx;
        this.TransEName = transEname;
        this.timeout = 60 * 1000;
        loadConfig();
        cleanTkns();
    }

    /**
	 * Load initial settings
	 */
	private void loadConfig() {

		cfg = TMConfig.getInstance();
		TermID = cfg.getTermID();
		MerchID = cfg.getMerchID();
		CurrencyCode = cfg.getCurrencyCode();
		BatchNo = ISOUtil.padleft("" + cfg.getBatchNo(), 6, '0');
		TraceNo = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');
		boolean isPub = cfg.getPubCommun() ;
		String ip = isPub?cfg.getIp():cfg.getIP2();

        int port = Integer.parseInt(isPub ? cfg.getPort() : cfg.getPort2());
        int timeout = cfg.getTimeout();
        String tpdu = cfg.getTpdu();
        String header = cfg.getHeader();
        setFixedDatas();
        netWork = new NetworkHelper(ip, port, timeout, context);//valida puerto, ip a usar
        if(!TransEName.equals("REVERSAL")) {
            iso8583 = new ISO8583(this.context, tpdu, header);
        }
        iso8583reverse = new ISO8583reverse(this.context, tpdu, header);
        InitTrans.tkn93.clean();
    }

    /**
     * Set message type and 60 domain 3 subdomain data
     */
    protected void setFixedDatas() {

        if (null == TransEName) {
            return;
        }
        Properties pro = PAYUtils.lodeConfig(context, TMConstants.TRANS);
        if (pro == null) {
            return;
        }
        String prop = pro.getProperty(TransEName);
        String[] propGroup = prop.split(",");
        if (!PAYUtils.isNullWithTrim(propGroup[0])) {
            MsgID = propGroup[0];
        } else {
            MsgID = null;
        }
        if (isUseOrgVal == false) {
            if (!PAYUtils.isNullWithTrim(propGroup[1])) {
                ProcCode = propGroup[1];
            } else {
                ProcCode = null;
            }
        }
        if (!PAYUtils.isNullWithTrim(propGroup[2])) {
            SvrCode = propGroup[2];
        } else {
            SvrCode = null;
        }
        if (isUseOrgVal == false) {
            if (!PAYUtils.isNullWithTrim(propGroup[3])) {
                F60_1 = propGroup[3];
            } else {
                F60_1 = null;
            }
        }
        if (!PAYUtils.isNullWithTrim(propGroup[4])) {
            F60_3 = propGroup[4];
        } else {
            F60_3 = null;
        }
        if (F60_1 != null && F60_3 != null) {
            Field60 = F60_1 + cfg.getBatchNo() + F60_3;
        }
        try {
            TransCName = new String(propGroup[5].getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    /**
     * 设置流水号是否自增
     *
     * @param isTraceNoInc
     */
    public void setTraceNoInc(boolean isTraceNoInc) {
        this.isTraceNoInc = isTraceNoInc;
    }

    /**
     * 追加60域内容
     *
     * @param f60
     */
    protected void appendField60(String f60) {
        Field60 = Field60 + f60;
    }

    /**
     * 连接
     *
     * @return
     */
    protected int connect() {

        return netWork.Connect(cfg.getTimeConnect());
    }

    /**
     * 发送
     *
     * @return
     */
	protected int send(boolean reversal) {
	    if (!reversal) {
            byte[] pack = iso8583.packetISO8583();
            if (pack == null) {
                return Tcode.T_package_mac_err;
            }
            return netWork.Send(pack);
        }else {
            byte[] pack = iso8583reverse.packetISO8583();
            if (pack == null) {
                return Tcode.T_package_mac_err;
            }
            return netWork.Send(pack);
        }
	}

	/**
	 * 接收
	 * @return
     */
	protected byte[] recive() {
		byte[] recive = null;
		try {
			recive = netWork.Recive(3*1024, cfg.getTimeout());
		} catch (IOException e) {
			return null;
		}

		return recive;
	}

    /**
     * 清除关键信息
     */
    protected void clearPan() {
        Pan = null;
        Track2 = null;
        Track3 = null;
        System.gc();//显示调用清除内存
    }

    private int indice;
    private byte[] idTkn;
    private byte[] lenTkn = new byte[2];
    private int auxLenTkn;
    String auxIdTkn;

    protected String impresion(String trama){
        String ret = "";
        idTkn = new byte[1];
        indice = 0;

        byte[] dataTkn = ISOUtil.str2bcd(trama, false);
        label:
        for (int i = 0; i < dataTkn.length; i = indice) {
            System.arraycopy(dataTkn, indice, idTkn, 0, 1);
            indice += 1;
            auxIdTkn = ISOUtil.bcd2str(idTkn, 0, 1);

            switch (auxIdTkn) {
                case "08":
                    ret = unpackImpresion(dataTkn);
                    if (ProcCode != null) {
                        if (ProcCode.equals("360100") || ProcCode.equals("370100")) {
                            ret = ret.replace("&#64;", "@");
                        } else if (ProcCode.equals("930200") || ProcCode.equals("930210")) {
                            ret = ret.substring(ret.indexOf("\n") + 1);
                        }
                    }
                    break;
                default:
                    break label;
            }
        }

        return ret;
    }

    protected void getTkn(String StrFld48) {

        idTkn = new byte[1];
        indice = 0;
        byte[] dataTkn = ISOUtil.str2bcd(StrFld48, false);
        label:
        for (int i = 0; i < dataTkn.length; i = indice) {
            System.arraycopy(dataTkn, indice, idTkn, 0, 1);
            indice += 1;
            auxIdTkn = ISOUtil.bcd2str(idTkn, 0, 1);

            switch (auxIdTkn) {
                case "07":
                    unpackTkn07(dataTkn);
                    break;

                case "47":
                    unpackTkn47(dataTkn);
                    break;
                case "08":
                    unpackTkn08(dataTkn);
                    break;


                default:
                    break label;
            }
        }
    }

    private void unpackTkn07(byte[] tkn) {

        System.arraycopy(tkn, indice, lenTkn, 0, 2);
        indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        byte[] tkn07 = new byte[auxLenTkn];

        System.arraycopy(tkn, indice, tkn07, 0, auxLenTkn);
        indice += auxLenTkn;

        Tkn07 = get_info_string(tkn07);

    }

    private void unpackTkn47(byte[] tkn) {

        System.arraycopy(tkn, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        byte[] tkn47 = new byte[auxLenTkn];
        System.arraycopy(tkn, this.indice, tkn47, 0, auxLenTkn);

        Tkn47 = ISOUtil.bcd2str(tkn47, 0, tkn47.length);

        this.indice += tkn47.length;


    }

    public void unpackTkn08(byte[] tkn) {

        int indice;
        StringBuilder respuesta = null;
        String auxCaracter, tramaImpresion = null;

        System.arraycopy(tkn, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        byte[] tkn08 = new byte[auxLenTkn];
        System.arraycopy(tkn, this.indice, tkn08, 0, auxLenTkn);

        indice = 0;
        byte[] auxtkn08 = new byte[1];
        System.arraycopy(tkn08, indice, auxtkn08, 0, 1);
        indice += 1;

        auxtkn08 = new byte[1];
        System.arraycopy(tkn08, indice, auxtkn08, 0, 1);
        indice += 1;

        auxCaracter = ISOUtil.bcd2str(auxtkn08, 0, 1);

        if (auxCaracter.equals("40")) {

            tramaImpresion = "";

            for (int i = 0; i < auxLenTkn - 2; i++) {

                auxtkn08 = new byte[1];
                System.arraycopy(tkn08, indice, auxtkn08, 0, 1);
                indice += 1;

                auxCaracter = ISOUtil.bcd2str(auxtkn08, 0, 1);

				if (auxCaracter.equals("40")) {
					tramaImpresion += "\n";
				} else {
					respuesta = ISOUtil.stringToHex(auxCaracter);
					tramaImpresion += respuesta;
				}
			}
		}
		Tkn08 = tramaImpresion;
		this.indice += indice;
	}

    public String unpackImpresion(byte[] tkn) {

        int indice;
        StringBuilder respuesta = null;
        String auxCaracter, tramaImpresion = null;

        System.arraycopy(tkn, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        byte[] tkn08 = new byte[auxLenTkn];
        System.arraycopy(tkn, this.indice, tkn08, 0, auxLenTkn);

        indice = 0;
        byte[] auxoken08 = new byte[1];
        System.arraycopy(tkn08, indice, auxoken08, 0, 1);
        indice += 1;

        auxCaracter = "40";

        if (auxCaracter.equals("40")) {
            tramaImpresion = "";
            for (int i = 0; i < auxLenTkn - 2; i++) {
                auxoken08 = new byte[1];
                System.arraycopy(tkn08, indice, auxoken08, 0, 1);
                indice += 1;
                auxCaracter = ISOUtil.bcd2str(auxoken08, 0, 1);

                if (auxCaracter.equals("40")) {
                    tramaImpresion += "\n";
                } else {
                    respuesta = ISOUtil.stringToHex(auxCaracter);
                    tramaImpresion += respuesta;
                }
            }
            tramaImpresion += "\n";
        }
        this.indice += indice;
        return tramaImpresion;
    }

	public void  clearDataFields (){
		MsgID=null;
		Pan = null;
		ProcCode=null;
		LocalTime=null;
		LocalDate=null;
		Field06=-1;
		ExpDate=null;
		SettleDate=null;
		EntryMode=null;
		PanSeqNo=null;
		SvrCode=null;
		CaptureCode=null;
		AcquirerID=null;
		Track1=null;
		Track2=null;
		Track3=null;
		AuthCode=null;
		TermID=null;
		MerchID=null;
		Field44=null;
		Field48=null;
		CurrencyCode=null;
		PIN=null;
		SecurityInfo=null;
		ExtAmount=null;
		ICCData=null;
		Field59=null;
		Field60=null;
		Field61=null;
		Field62=null;
		Field63=null;
		TransCName=null;
		//TransEName=null;
		BatchNo=null;
	}

    private void cleanTkns() {
        InitTrans.tkn93.cleanTkn93();
    }

	private String get_info_string(byte[] info) {
		String auxTkn07 = ISOUtil.bcd2str(info, 0, info.length);
		StringBuilder auxStr = ISOUtil.stringToHex(auxTkn07);
		return auxStr.toString();
	}

    /**
     * Interfaz que almacena los códigos de proceso de las nuevas recaudaciones
     */
	public interface proCodes {
        String recauUTPLConsulta = "495001";
        String recauUTPLMontoVariable = "495011";
        String recauUTPLEfectivacion = "495021";

        String recauITSCOConsulta = "495002";
        String recauITSCOMontoVariable = "495012";
        String recauITSCOEfectivacion = "495022";

        String recauCNTFIJOConsulta = "470101";
        String recauCNTFIJOMontoVariable = "470111";
        String recauCNTEfectivacion = "470121";

        String recauCNTPLANESConsulta = "470105";
        String recauCNTPLANESMontoVariable = "470115";
        String recauCNTPLANESEfectivacion = "470125";

        String recauPYCCAConsulta = "490201";
        String recauPYCCAMontoVariable = "490211";
        String recauPYCCAEfectivacion = "490221";

        String recauATMConsultaCitaciones ="494201";
        String recauATMontoVariableCitaciones ="494211";
        String recauATMEfectivacionCitaciones ="494221";

        String recauATMConsultaSolicitud ="494202";
        String recauATMontoVariableSolicitud ="494212";
        String recauATMEfectivacionSolicitud ="494222";

        String recauATMConsultaRenovacion ="494203";
        String recauATMontoVariableRenovacion ="494213";
        String recauATMEfectivacionRenovacion ="494223";

        String recauATMConsultaRodaje ="494204";
        String recauATMontoVariableRodaje ="494214";
        String recauATMEfectivacionRodaje ="494224";

        String recauCNTTVConsulta = "460205";
        String recauCNTTVEfectivacion = "460225";

        String recauCNTMOVILConsulta = "460103";
        String recauCNTMOVILEfectivacion = "460123";

        String recauCNTINTERNETConsulta = "470401";
        String recauCNTINTERNETMontoVariable = "470411";
        String recauCNTINTERNETEfectivacion = "470421";

        String recauCNTtvpagadaConsulta = "460306";
        String recauCNTtvpagadaMontoVariable = "460316";
        String recauCNTtvpagadaEfectivacion = "460326";


        //REVISEN ESTAS VARIABLES Y SUS FUNCIONES
        String recauMicrocobrosConsulta = "496001";
        String recauMicrocobrosMontoVariable = "496011";
        String recauMicrocobrosEfectivacion = "496021";

    }

}
