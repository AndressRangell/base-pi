package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.SDKException;
import com.pos.device.beeper.Beeper;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import cnb.pichincha.wposs.mivecino_pichincha.R;

public class VerificaTarjeta extends ToolsAppCompact implements TextToSpeech.OnInitListener{
    TextView details ;
    ImageView face ;
    private Timer timer = new Timer();
    private IccReader iccReader0;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);
        theToolbar();
        textToSpeech = new TextToSpeech( this, this );
        details =  findViewById(R.id.result_details);
        details.setTypeface(tipoFuente1(this));
        face =  findViewById(R.id.result_img);
        details.setText("Por favor, retira la tarjeta!");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verificaICCinsert();
            }
        } , 3*1000);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){}

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    public void onBackPressed() {}
    
    private void verificaICCinsert(){
        boolean cardPresent;
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        textToSpeech.setLanguage( new Locale( "spa", "ESP" ) );
        do {
            cardPresent = iccReader0.isCardPresent();
            if (cardPresent){
                try{
                    speak();
                    //speak( "please, remove the card!" );
                    Thread.sleep(2000);
                    Beeper.getInstance().beep(400,500);
                    Thread.sleep(1000);
                }catch (Exception e){
                    Log.e("Error", ""+e);
                }
            }
        } while (cardPresent);
        over();
    }

    private void over(){
        finish();
    }

    @Override
    public void onInit(int status) {
        if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED )
        {
            Toast.makeText( this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT ).show();
        }
    }

    private void speak()
    {
        textToSpeech.speak("Por favor, retira la tarjeta", TextToSpeech.QUEUE_FLUSH,null,"hola");
        textToSpeech.setSpeechRate( 0.0f );
        textToSpeech.setPitch( 0.0f );
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
