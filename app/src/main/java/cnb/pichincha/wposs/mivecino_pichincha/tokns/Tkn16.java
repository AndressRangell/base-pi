package cnb.pichincha.wposs.mivecino_pichincha.tokns;

public class Tkn16 {

    private byte[] msg_tipo = new byte[1];
    private byte[] msg_largo = new byte[150];

    public void clean(){
        msg_tipo = new byte[1];
        msg_largo = new byte[150];
    }

    public byte[] getMsg_tipo() {
        return msg_tipo;
    }

    public void setMsg_tipo(byte[] msg_tipo) {
        this.msg_tipo = msg_tipo;
    }

    public byte[] getMsg_largo() {
        return msg_largo;
    }

    public void setMsg_largo(byte[] msg_largo) {
        this.msg_largo = msg_largo;
    }

}
