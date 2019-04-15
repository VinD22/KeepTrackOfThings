package things.track.keep.chain.key.app.v.keeptrackofthings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    String url = "";
    long time  = 2000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras() == null) {
            // Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {

            Bundle extras = getIntent().getExtras();
            url = extras.getString("url");
            // Toast.makeText(this, "Url : " + url, Toast.LENGTH_SHORT).show();

            if(url != null) {

                if(!url.isEmpty()) {

                    try{

                        time = 1;

                    } catch (ActivityNotFoundException e) {

                    }


                } else {
                    // Run Animation Code!
                }

            }

        }

        TextView mTitle = (TextView) findViewById(R.id.tv);
        LinearLayout mLin = (LinearLayout) findViewById(R.id.lin);

        // Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/foober.otf");
        // mTitle.setTypeface(tf);


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade);
        anim.reset();
        mLin.clearAnimation();
        mLin.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        mTitle.clearAnimation();
        mTitle.startAnimation(anim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Create an intent that will start the main activity.
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainIntent.putExtra("url", url);
                SplashActivity.this.startActivity(mainIntent);

                //Finish splash activity so user cant go back to it.
                SplashActivity.this.finish();
                overridePendingTransition(0, R.anim.splash_fade_new);

                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                // overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
            }
        }, time);

    }

}
