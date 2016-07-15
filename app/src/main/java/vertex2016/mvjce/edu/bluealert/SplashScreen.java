package vertex2016.mvjce.edu.bluealert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by AURO on 3/14/2016.
 */
public class SplashScreen extends AppCompatActivity {

    private int MAX_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Thread splsh = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(MAX_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(toMain);
                    finish();
                }

            }
        };
        splsh.start();
    }
}
