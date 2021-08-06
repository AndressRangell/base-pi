package newpos.libpay.trans.translog;

import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.PAYUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易日志管理类
 *
 * @author
 */

public class TransLog implements Serializable {
    private static String TranLogPath = "translog.dat";
    private static String ScriptPath = "script.dat";
    private static String ReversalPath = "reversal.dat";

    private List<TransLogData> transLogData = new ArrayList<TransLogData>();
    private static TransLog tranLog;

    private TransLog() {
    }

    public static TransLog getInstance() {
        if (tranLog == null) {
            String filepath = TMConfig.getRootFilePath() + TranLogPath;
            try {
                tranLog = ((TransLog) PAYUtils.file2Object(filepath));
            } catch (FileNotFoundException e) {
                tranLog = null;
            } catch (IOException e) {
                tranLog = null;
            } catch (ClassNotFoundException e) {
                tranLog = null;
            }
            if (tranLog == null) {
                tranLog = new TransLog();
            }
        }
        return tranLog;
    }

    public static TransLog getInstance(String merchID, boolean isNewInstance) {
        if (isNewInstance) {
            tranLog = null;
        }
        if (tranLog == null) {
            String filepath = TMConfig.getRootFilePath() + merchID.trim() + TranLogPath;
            try {
                tranLog = ((TransLog) PAYUtils.file2Object(filepath));
            } catch (FileNotFoundException e) {
                tranLog = null;
            } catch (IOException e) {
                tranLog = null;
            } catch (ClassNotFoundException e) {
                tranLog = null;
            }
            if (tranLog == null) {
                tranLog = new TransLog();
            }
        }
        return tranLog;
    }

    public List<TransLogData> getData() {
        return transLogData;
    }

    public int getSize() {
        return transLogData.size();
    }

    public TransLogData get(int position) {
        if (!(position > getSize())) {
            return transLogData.get(position);
        }
        return null;
    }


    public void clearAll(String merchIDe) {
        transLogData.clear();
        String FullName = TMConfig.getRootFilePath() + merchIDe.trim() + TranLogPath;
        File file = new File(FullName);
        if (file.exists()) {
            file.delete();
        }
    }


    /*
    Elimina los archivos que contengan TranLogPath : "translog.dat"
     */
    public void clearAll() {
        transLogData.clear();

        String path = TMConfig.getRootFilePath();
        String[] filesString = getFiles( path );
        if ( filesString != null ) {
            int size = filesString.length;
            for ( int i = 0; i < size; i ++ ) {
                if( filesString[ i ].contains(TranLogPath) == true ){
                    File file = new File(filesString[ i ]);
                    if (file.exists() && file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 获取上一条交易记录
     */
    public TransLogData getLastTransLog() {
        if (getSize() >= 1) {
            return transLogData.get(getSize() - 1);
        }
        return null;
    }

    /**
     * 保存交易记录
     *
     * @return
     */
    public boolean saveLog(TransLogData data) {
        transLogData.add(data);

        try {
            PAYUtils.object2File(tranLog, TMConfig.getRootFilePath() + TranLogPath);
        } catch (FileNotFoundException e) {

            return false;
        } catch (IOException e) {

            return false;
        }
        return true;
    }

    public boolean saveLog(TransLogData data, String merchID) {
        transLogData.add(data);

        try {
            String auxPath = TMConfig.getRootFilePath() + merchID.trim() + TranLogPath  ;
            PAYUtils.object2File(tranLog, auxPath);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     * 更新交易记录
     *
     * @param logIndex 交易记录索引
     * @param newData  更新后的数据
     * @return 更新结果
     */
    public boolean updateTransLog(int logIndex, TransLogData newData) {
        if (getSize() > 0) {
            transLogData.set(transLogData.indexOf(transLogData.get(logIndex)), newData);
            return true;
        }
        return false;
    }

    /**
     * 获取当前交易的索引号
     *
     * @param data
     * @return
     */
    public int getCurrentIndex(TransLogData data) {
        int current = -1;
        for (int i = 0; i < transLogData.size(); i++) {
            if (transLogData.get(i).getTraceNo().equals(data.getTraceNo())) {
                current = i;
            }
        }
        return current;
    }


    /**
     * 根据流水号获取交易记录
     *
     * @param TraceNo 交易流水号
     * @return 交易记录
     */
    public TransLogData searchTransLogByTraceNo(String TraceNo) {
        if (getSize() > 0) {
            for (int i = 0; i < getSize(); i++) {
                if (!PAYUtils.isNullWithTrim(transLogData.get(i).getTraceNo())) {
                    if (transLogData.get(i).getTraceNo().equals("" + TraceNo)) {
                        return transLogData.get(i);
                    }
                }
            }
        }
        return null;
    }


    /**
     * 根据参考号及日期获取交易记录
     *
     * @param refer date
     * @return 交易记录
     */
    public TransLogData searchTransLogByREFERDATE(String refer, String date) {
        if (getSize() > 0) {
            for (int i = 0; i < getSize(); i++) {
                TransLogData data = transLogData.get(i);
                if (!PAYUtils.isNullWithTrim(data.getRRN()) &&
                        !PAYUtils.isNullWithTrim(data.getLocalDate())) {
                    if (data.getRRN().equals("" + refer) &&
                            data.getLocalDate().equals("" + date)) {
                        return data;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 保存脚本结果
     *
     * @return
     */
    public static boolean saveScriptResult(TransLogData data) {
        try {
            PAYUtils.object2File(data, TMConfig.getRootFilePath() + ScriptPath);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return false;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存冲正信息
     *
     * @return
     */
    public static boolean saveReversal(TransLogData data) {
        try {
            PAYUtils.object2File(data, TMConfig.getRootFilePath() + ReversalPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取冲正信息
     *
     * @return
     */
    public static TransLogData getReversal() {
        try {
            return (TransLogData) PAYUtils.file2Object(TMConfig.getRootFilePath() + ReversalPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取脚本信息
     *
     * @return
     */
    public static TransLogData getScriptResult() {
        try {
            return (TransLogData) PAYUtils.file2Object(TMConfig.getRootFilePath() + ScriptPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除冲正
     *
     * @return
     */
    public static boolean clearReveral() {
        File file = new File(TMConfig.getRootFilePath() + ReversalPath);
        if (file.exists() && file.isFile()) {
            InitTrans.wrlg.wrDataTxt("Eliminando reverso");
            file.delete();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 清除脚本执行结果
     *
     * @return
     */
    public static boolean clearScriptResult() {
        File file = new File(TMConfig.getRootFilePath() + ScriptPath);
        if (file.exists() && file.isFile()) {
            file.delete();
            return false;
        } else {
            return true;
        }
    }


    public static String[] getFiles( String dir_path ) {
        String[] arr_res = null;
        File f = new File( dir_path );
        if ( f.isDirectory( )) {
            List<String> res   = new ArrayList<>();
            File[] arr_content = f.listFiles();
            int size = arr_content.length;
            for ( int i = 0; i < size; i ++ ) {
                if ( arr_content[ i ].isFile( ))
                    res.add( arr_content[ i ].toString( ));
            }
            arr_res = res.toArray( new String[ 0 ] );
        }
        return arr_res;
    }

}