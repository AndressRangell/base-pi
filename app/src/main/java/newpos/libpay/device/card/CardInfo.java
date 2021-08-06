package newpos.libpay.device.card;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 卡片信息详情
 */

public class CardInfo {

    private boolean resultFalg ;

    /** 成功返回 */
    private int cardType ;
    private byte[] cardAtr ;
    private String[] trackNo ;
    private int nfcType ;
    private String nfcUid;
    private String nfcCardInfo;

    /**
     * 失败返回
     */
    private int errno ;

    public String getNfcUid() {
        return nfcUid;
    }

    public void setNfcUid(String nfcUid) {
        this.nfcUid = nfcUid;
    }

    public String getNfcCardInfo() {
        return nfcCardInfo;
    }

    public void setNfcCardInfo(String nfcCardInfo) {
        this.nfcCardInfo = nfcCardInfo;
    }

    public CardInfo(){}

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public byte[] getCardAtr() {
        return cardAtr;
    }

    public void setCardAtr(byte[] cardAtr) {
        this.cardAtr = cardAtr;
    }

    public String[] getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String[] trackNo) {
        this.trackNo = trackNo;
    }

    public int getNfcType() {
        return nfcType;
    }

    public void setNfcType(int nfcType) {
        this.nfcType = nfcType;
    }

    public boolean isResultFalg() {
        return resultFalg;
    }

    public void setResultFalg(boolean resultFalg) {
        this.resultFalg = resultFalg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
