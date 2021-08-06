package newpos.libpay.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveData {

    private static final String NOMBRE_ARCHIVO = "DataFile";
    private final String FECHA_ULT_TRANS_KEY = "FECHA_ULT_TRANS_KEY";
    private final String TIEMPO_REVERSO = "TIEMPO_REVERSO";
    private final String HASH_PARAM_RECAU_KEY = "HASH_PARAM_RECAU_KEY";
    private final String ULT_TRANS_KEY = "ULT_TRANS_KEY";
    private final String HASH_TCP_KEY = "HASH_TCP_KEY";
    private final String RECIBO_CONSULTA_AUTO = "RECIBO_CONSULTA_AUTO";
    private final String START_APP_KEY = "START_APP_KEY";

    private static SaveData instance = null;
    private static SharedPreferences archivoDatos;

    private SaveData()
    {
        readDataFromLocalArchive();
    }

    public static SaveData getInstance()
    {
        return instance;
    }

    public static void Initialize(Context context)
    {
        if(instance == null)
        {
            archivoDatos = context.getSharedPreferences(NOMBRE_ARCHIVO, Context.MODE_PRIVATE);
            instance = new SaveData();
        }
    }

    private String fechaUltimaTrans;

    public String getFechaUltimaTrans() {
        return fechaUltimaTrans;
    }

    public void setFechaUltimaTrans(String fechaUltimaTrans) {
        this.fechaUltimaTrans = fechaUltimaTrans;
        archivoDatos.edit()
                .putString(FECHA_ULT_TRANS_KEY, fechaUltimaTrans)
                .apply();
    }

    private String tiempoReverso;

    public String getTiempoReverso() {
        return tiempoReverso;
    }

    public void setTiempoReverso(String tiempoReverso) {
        this.tiempoReverso = tiempoReverso;
        archivoDatos.edit()
                .putString(TIEMPO_REVERSO, tiempoReverso)
                .apply();
    }

    private String hashParamRecauJSON;

    public String getHashParamRecauJSON() {
        return hashParamRecauJSON;
    }

    public void setHashParamRecauJSON(String hashParamRecauJSON) {
        this.hashParamRecauJSON = hashParamRecauJSON;
        archivoDatos.edit()
                .putString(HASH_PARAM_RECAU_KEY, hashParamRecauJSON)
                .apply();
    }

    private String hashTCPJSON;

    public String getHashTCPJSON() {
        return hashTCPJSON;
    }

    public void setHashTCPJSON(String hashTCPJSON) {
        this.hashTCPJSON = hashTCPJSON;
        archivoDatos.edit()
                .putString(HASH_TCP_KEY, hashTCPJSON)
                .apply();
    }

    private String ultimaTrx;

    public String getUltimaTrx() {
        return ultimaTrx;
    }

    public void setUltimaTrx(String ultimaTrx) {
        this.ultimaTrx = ultimaTrx;
        archivoDatos.edit()
                .putString(ULT_TRANS_KEY, this.ultimaTrx)
                .apply();
    }

    private boolean startApp;

    public boolean getStartApp() {
        return startApp;
    }

    public void setStartApp(boolean startApp) {
        this.startApp = startApp;
        archivoDatos.edit()
                .putBoolean(START_APP_KEY, startApp)
                .apply();
    }

    private String reciboConsultaAuto;

    public String getReciboConsultaAuto() {
        return reciboConsultaAuto;
    }

    public void setReciboConsultaAuto(String reciboConsultaAuto) {
        this.reciboConsultaAuto = reciboConsultaAuto;
        archivoDatos.edit()
                .putString(RECIBO_CONSULTA_AUTO, reciboConsultaAuto)
                .apply();
    }

    /*********
     Método       : readDataFromLocalArchiveReverso
     Description  : Lee Datos del archivo guardado
     *********/
    public void readDataFromLocalArchive(){
        fechaUltimaTrans = archivoDatos.getString(FECHA_ULT_TRANS_KEY, "");
        tiempoReverso = archivoDatos.getString(TIEMPO_REVERSO, "15");
        hashParamRecauJSON = archivoDatos.getString(HASH_PARAM_RECAU_KEY,"");
        hashTCPJSON = archivoDatos.getString(HASH_TCP_KEY,"");
        ultimaTrx = archivoDatos.getString(ULT_TRANS_KEY,"");
        reciboConsultaAuto = archivoDatos.getString(RECIBO_CONSULTA_AUTO,"");
        setStartApp(archivoDatos.getBoolean(START_APP_KEY, true));
    }

}
