package cnb.pichincha.wposs.mivecino_pichincha.Fragments;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cnb.pichincha.wposs.mivecino_pichincha.R;

public class DialogFullscreenFragment extends DialogFragment {

    public CallbackResult callbackResult;
    public String mensaje, titulo;
    Typeface tipoFuente1, tipoFuente3;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private int mTrue = 300;
    private int mFalse = 301;
    private View root_view;
    private TextView tv_mensaje, tv_titulo;
    private Button btAceptar, btCancelar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tipoFuente1 = Typeface.createFromAsset(getContext().getAssets(), "font/PreloSlab-Book.otf");
        tipoFuente3 = Typeface.createFromAsset(getContext().getAssets(), "font/Prelo-Medium.otf");

        root_view = inflater.inflate(R.layout.trans_vista_msg_confirmacion, container, false);

        tv_titulo = (TextView) root_view.findViewById(R.id.tv1);
        tv_mensaje = (TextView) root_view.findViewById(R.id.ms0);
        btAceptar = root_view.findViewById(R.id.btAceptar);
        btCancelar = root_view.findViewById(R.id.btCancel);

        tv_titulo.setTypeface(tipoFuente1);
        tv_titulo.setText(titulo);

        tv_mensaje.setTypeface(tipoFuente3);
        tv_mensaje.setText(mensaje);

        btAceptar.setTypeface(tipoFuente3);
        btCancelar.setTypeface(tipoFuente3);


        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestCode(mTrue);
                dismiss();
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestCode(mFalse);
                dismiss();
            }
        });

        return root_view;
    }

    private void sendRequestCode(int request_code) {
        if (callbackResult != null) {
            callbackResult.sendResult(request_code);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    public void setMensaje(String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
    }


    public interface CallbackResult {
        void sendResult(int requestCode);
    }

}