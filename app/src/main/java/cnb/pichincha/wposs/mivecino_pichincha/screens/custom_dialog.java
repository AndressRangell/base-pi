package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import cnb.pichincha.wposs.mivecino_pichincha.R;

public class custom_dialog {

    public interface finalizarinterface{
        void resultado(String val);
    }

    private finalizarinterface interfaz;

    public custom_dialog(final Context context, finalizarinterface actividad){
        interfaz = actividad;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);

        final EditText txt = dialog.findViewById(R.id.txt_customDialog);
        Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
        Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaz.resultado(txt.getText().toString());
                dialog.dismiss();
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
