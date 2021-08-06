package com.example.facephipinchincha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facephi.fphiwidgetcore.FPhiImage;
import com.facephi.fphiwidgetcore.WidgetConfiguration;
import com.facephi.fphiwidgetcore.WidgetLivenessMode;
import com.facephi.fphiwidgetcore.WidgetMode;
import com.facephi.fphiwidgetcore.WidgetResult;
import com.facephi.sdk.extractor.LivenessDiagnostic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvStatus;
    private ImageView imgStatus;
    private Button btn_reintentar;
    private Button btn_cancelar;
    private int contador = 0;
    private String noPermisos="no se tiene los permisos necesarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        imgStatus = findViewById(R.id.image);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        btn_reintentar = findViewById(R.id.btn_reintentar);
        btn_cancelar = findViewById(R.id.btnCancel);
        btn_reintentar.setVisibility(View.INVISIBLE);
        btn_cancelar.setVisibility(View.INVISIBLE);

        btn_reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        imgStatus.setVisibility(View.INVISIBLE);
                        btn_reintentar.setVisibility(View.INVISIBLE);
                        btn_cancelar.setVisibility(View.INVISIBLE);
                        imgStatus.setImageDrawable(null);
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setIndeterminate(true);
                        tvStatus.setText("Abriendo camara FacePhi");
                        inicializarComponentes();
                    }
                }, 2000);
            }
        });


        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        if (getIntent().hasExtra("pin" ) ) {
            if (verificarPin(getIntent().getStringExtra("pin"))) {
                guardarPin(getIntent().getStringExtra("pin"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inicializarComponentes();
                    }
                }, 2000);
            } else {
                sinPermisos();
            }
        }else{
            sinPermisos();
        }
    }


    private void inicializarComponentes() {
        Intent intent = new Intent(getBaseContext(), com.facephi.selphi.Widget.class);
        WidgetConfiguration conf = new WidgetConfiguration(WidgetMode.Authenticate, "fphi-widget-resources-SelphiLive-1.2.zip");
        conf.setLivenessMode(WidgetLivenessMode.LIVENESS_BLINK);// LIVENESS_NONE, LIVENESS_BLINK
        conf.setBackFacingCameraAsPreferred();
        conf.setFullscreen(false);
        conf.setTutorialFlag(false);
        conf.enableImages(true);

        intent.putExtra("configuration", conf);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == -1) {
            finish();
        }

        tvStatus.setText("Recibiendo respuesta facePhi " + resultCode);

        if (data == null) {
            setMensaje(false, "No se puede abrir, Por favor contacte con soporte");
            return;
        }

        WidgetResult wResult = data.getParcelableExtra("result");
        if (wResult == null) {
            setMensaje(false, "No se puede abrir FacePhi, Por favor contacte con soporte ");
            return;
        }

        if (resultCode == RESULT_CANCELED) {

            if (wResult.getException() != null) {
                if (wResult.getException().getExceptionType() != null) {
                    String message = "";

                    switch (wResult.getException().getExceptionType()) {
                        case StoppedManually:
                            message = "Operacion Cancelada por Usuario, intente nuevamente";
                            break;
                        case Timeout:
                            message = "El tiempo del proceso se ha agotado, intente nuevamente";
                            break;
                        case CameraPermissionDenied:
                            message = "Los permisos de cámara están desactivados, Por favor contacte con soporte";
                            break;
                        case SettingsPermissionDenied:
                            message = "Los permisos de modificación de ajustes están desactivados, Por favor contacte con soporte";
                            break;
                        default:
                            message = "Error inesperado, por favor contacte con soporte";
                            break;
                    }
                    setMensaje(false, message);

//                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(MainActivity.this, wResult.getException().getMessage(), Toast.LENGTH_LONG).show();
                    setMensaje(false, "Error en la cámara, por favor contacte con soporte");
                }
            }

            return;
        }


        LivenessDiagnostic livenessDiagnostic = wResult.getLivenessDiagnostic();

        System.out.println("LivenessDiaganostic ******************  " + livenessDiagnostic);
        if (livenessDiagnostic == null) {
            setMensaje(false, "El diagnostico dejo de funcionar, Por favor contacte con soporte ");
            return;
        }
        // Processes the liveness result message.
        if (livenessDiagnostic != LivenessDiagnostic.LivenessDetected) {
            String liveness_message = "Error: ";
            switch (wResult.getLivenessDiagnostic()) {
                case UnsuccessLight:
//                    liveness_message += "El proceso de detección de vivacidad ha fallado por problemas de iluminación";
                    liveness_message += "Por favor aumente la luminosidad al tomar la foto e intente nuevamente ";
                    break;
                case UnsuccessGlasses:
//                    liveness_message = "El proceso de detección de vivacidad ha fallado por el posible uso de gafas";
                    liveness_message += "No fue posible identificar al usuario por el uso de gafas, por favor intente nuevamente ";
                    break;
                case UnsuccessLowPerformance:
//                    liveness_message = "El proceso de detección de vivacidad ha fallado por bajo rendimiento del dispositivo";
                    liveness_message += "La foto fallo por bajo rendimiento del dispositivo, por favor intente nuevamente o contacte con soporte";
                    break;
                default:
//                    liveness_message = "Fotografía detectada";
                    liveness_message += "El reconocimiento facial no se realizó con éxito. Regístralo nuevamente";
                    break;
            }
            setMensaje(false, liveness_message);
//            Toast.makeText(MainActivity.this, liveness_message, Toast.LENGTH_LONG).show();
            return;
        }

        List<FPhiImage> imageList = wResult.getImages();

        Bitmap bitmap = Bitmap.createBitmap(
                imageList.get(0).getWidth(),
                imageList.get(0).getHeight(),
                imageList.get(0).getFormat());

        bitmap.copyPixelsFromBuffer(imageList.get(0).getImage());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] template = byteArrayOutputStream .toByteArray();
        String template64 = Base64.encodeToString(template, Base64.NO_WRAP);
        System.out.println("TEMPLA64 ***************************** " + template64);
        if (!template64.isEmpty()) {
            if (guardarTemplate(template64, "/sdcard/logs/facephi", "FacePhi")) {
//                Toast.makeText(this, "Archivo guardadp correctamente", Toast.LENGTH_SHORT).show();
                //setMensaje(true, "Reconocimiento Facial se realizo con éxito");
                finish();
            } else {
//                Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
                setMensaje(false, "Error al guardar archivo, por favor contacte con soporte");
            }
        } else {
//            Toast.makeText(this, "Arhivo base64 empty", Toast.LENGTH_SHORT).show();
            setMensaje(false, "Error en el archivo, por favor contacte con soporte");
        }


    }

    private boolean guardarTemplate(String template64, String direc, String nameFile) {
        if (!template64.isEmpty()) {
            try {
                String nomarchivo = nameFile;
                nomarchivo += ".txt";
                try {

                    File dir = new File(direc);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    //File tarjeta = Environment.getExternalStorageDirectory();
                    File file = new File(direc + "/" + nomarchivo);

                    if (file.exists()) {
                        file.delete();
                    }
                    OutputStreamWriter osw = new OutputStreamWriter(
                            new FileOutputStream(file));
                    osw.write(template64);
                    osw.flush();
                    osw.close();
                } catch (IOException ioe) {
                    setMensaje(false, "Error 001 en la Informacion de la foto, contacte con soporte");
                }
            } catch (Exception e) {
                e.printStackTrace();
                setMensaje(false, "Error 002 en la Informacion de la foto, contacte con soporte");

            }
        } else {
            setMensaje(false, "Error 003 en la Informacion de la foto, contacte con soporte");
        }
        return true;
    }


    private void guardarPin(String dato) {
        SharedPreferences preferencias=getSharedPreferences("validar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("pin", dato);
        editor.commit();
    }

    private boolean verificarPin(String dato) {
        SharedPreferences prefe=getSharedPreferences("validar", Context.MODE_PRIVATE);
        String d=prefe.getString("pin", "");
        if (!d.equals(dato)) {
            return true;
        }
        if (d.length() == 0) {
            return true;
        }
        return false;
    }
    private void setMensaje(boolean acceptar, String mensaje) {
        if(mensaje!=noPermisos){
            btn_reintentar.setVisibility(View.VISIBLE);
        }

        btn_cancelar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvStatus.setText(mensaje);
        if (acceptar) {
            imgStatus.setImageDrawable(getDrawable(R.drawable.pichi_aceptado_new));
            btn_cancelar.setVisibility(View.INVISIBLE);
            btn_reintentar.setText("CONTINUAR");
            btn_reintentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } else {
            if (contador != 2){
                imgStatus.setImageDrawable(getDrawable(R.drawable.pichi_denegado3));
                contador++;
            }else {
                contador = 0;
                finish();
            }

        }
    }

    private void sinPermisos() {
        setMensaje(false, noPermisos);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }




}
