package cnb.pichincha.wposs.mivecino_pichincha.tokns;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;


public class Tkn90 {

    /**
     *
     * @return
     */
    public StringBuilder packTkn90() {
        TransLogData data = TransLog.getReversal();
        StringBuilder tkn90 = new StringBuilder();
        tkn90.append("900010");               // id+length
        tkn90.append(data.getAcquirerID());                   // MsgType
        tkn90.append(data.getTraceNo());                      // STAN
        tkn90.append(data.getLocalDate());                    // Txn Date
        tkn90.append(data.getLocalTime());                    // Txn Time

        return tkn90;
    }

}
