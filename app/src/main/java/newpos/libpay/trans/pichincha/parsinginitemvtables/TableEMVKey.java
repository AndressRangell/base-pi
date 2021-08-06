package newpos.libpay.trans.pichincha.parsinginitemvtables;

/**
 * Created by Jhon Morantes on 11/4/2018.
 */

public class TableEMVKey {

    byte[] idTable = new byte[1];
    byte[] lenTable = new byte[1];

    byte[] idRegister = new byte[1];
    byte[] pubKeyId = new byte[1];
    byte[] keyLen = new byte[2];
    byte[] publicKey = new byte[256];
    byte[] pubKeyRid = new byte[5];
    byte[] pubKeyExp = new byte[1];
    byte[] pubKeyHas = new byte[20];
    byte[] EffectDate = new byte[1];// in MMYY
    byte[] ExpiryDate = new byte[1];// in MMYY

    public byte[] getIdTable() {
        return idTable;
    }

    public void setIdTable(byte[] idTable) {
        this.idTable = idTable;
    }

    public byte[] getLenTable() {
        return lenTable;
    }

    public void setLenTable(byte[] lenTable) {
        this.lenTable = lenTable;
    }

    public byte[] getIdRegister() {
        return idRegister;
    }

    public void setIdRegister(byte[] idRegister) {
        this.idRegister = idRegister;
    }

    public byte[] getPubKeyId() {
        return pubKeyId;
    }

    public void setPubKeyId(byte[] pubKeyId) {
        this.pubKeyId = pubKeyId;
    }

    public byte[] getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(byte[] keyLen) {
        this.keyLen = keyLen;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPubKeyRid() {
        return pubKeyRid;
    }

    public void setPubKeyRid(byte[] pubKeyRid) {
        this.pubKeyRid = pubKeyRid;
    }

    public byte[] getPubKeyExp() {
        return pubKeyExp;
    }

    public void setPubKeyExp(byte[] pubKeyExp) {
        this.pubKeyExp = pubKeyExp;
    }

    public byte[] getPubKeyHas() {
        return pubKeyHas;
    }

    public void setPubKeyHas(byte[] pubKeyHas) {
        this.pubKeyHas = pubKeyHas;
    }

    public byte[] getEffectDate() {
        return EffectDate;
    }

    public void setEffectDate(byte[] effectDate) {
        EffectDate = effectDate;
    }

    public byte[] getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(byte[] expiryDate) {
        ExpiryDate = expiryDate;
    }
}
