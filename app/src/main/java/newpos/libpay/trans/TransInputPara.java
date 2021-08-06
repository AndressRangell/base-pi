package newpos.libpay.trans;

import newpos.libpay.presenter.TransUI;

/**
 * Created by zhouqiang on 2017/3/30.
 * @author zhouqiang
 * 交易输入参数
 */

public class TransInputPara {
    private boolean isNeedAmount = false;
    private boolean isNeedPass = false;
    private boolean isChangePin = false;
    private boolean isNeedConfirmCard = false; //是否需要确认卡号
    private long amount ;//金额
    private long otherAmount ;//第二金额
    private boolean isNeedOnline = false;//是否强制联机
    private int inputMode ;// 外界输入模式
    private String transType ;//交易类型
    private boolean isEmvAll = true ;//是否EMV完整流程
    private boolean isNeedPrint = false;// 是否需要打印
    private boolean isECTrans = false; //是否是电子现金交易
    private TransUI transUI ;// UIP层接口实例
    private boolean isNeedConfirmPrint = false;

    public boolean isNeedAmount() {
        return isNeedAmount;
    }

    public void setNeedAmount(boolean needAmount) {
        isNeedAmount = needAmount;
    }

    public boolean isNeedPass() {
        return isNeedPass;
    }

    public void setNeedPass(boolean needPass) {
        isNeedPass = needPass;
    }

    public boolean isChangePin() {
        return isChangePin;
    }

    public void setChangePin(boolean changePin) {
        isChangePin = changePin;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(long otherAmount) {
        this.otherAmount = otherAmount;
    }

    public boolean isNeedOnline() {
        return isNeedOnline;
    }

    public void setNeedOnline(boolean needOnline) {
        isNeedOnline = needOnline;
    }

    public int getInputMode() {
        return inputMode;
    }

    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public TransUI getTransUI() {
        return transUI;
    }

    public void setTransUI(TransUI transUI) {
        this.transUI = transUI;
    }

    public boolean isNeedConfirmCard() {
        return isNeedConfirmCard;
    }

    public void setNeedConfirmCard(boolean needConfirmCard) {
        isNeedConfirmCard = needConfirmCard;
    }

    public boolean isECTrans() {
        return isECTrans;
    }

    public void setECTrans(boolean ECTrans) {
        isECTrans = ECTrans;
    }

    public boolean isNeedPrint() {
        return isNeedPrint;
    }

    public void setNeedPrint(boolean needPrint) {
        isNeedPrint = needPrint;
    }

    public boolean isEmvAll() {
        return isEmvAll;
    }

    public void setEmvAll(boolean emvAll) {
        isEmvAll = emvAll;
    }

    public boolean isNeedConfirmPrint() {return isNeedConfirmPrint;}

    public void setNeedConfirmPrint(boolean needConfirmPrint) {isNeedConfirmPrint = needConfirmPrint;}
}
