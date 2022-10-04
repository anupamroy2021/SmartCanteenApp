package my.anupamroy.smartcanteenapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import my.anupamroy.smartcanteenapp.R;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat fcmSwitch;
    private TextView notificationStatusTv;
    private ImageButton backBtn;

    private static final String enabledMessage="Notifications are enabled";
    private static final String disabledMessage="Notifications are disabled";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //init ui views
        fcmSwitch=findViewById(R.id.fcmSwitch);
        notificationStatusTv=findViewById(R.id.notificationStatusTv);
        backBtn=findViewById(R.id.backBtn);

        firebaseAuth=FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
//we will work with topic based firebase messaging , for a user to receive topic based message/notification have to subscribe to that topic
//requirements: FCM Server key