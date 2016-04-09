package soler.lluis.sensorstest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sMgr;
    Sensor mLight;
    TextView textLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        sMgr.registerListener(this,mLight,sMgr.SENSOR_DELAY_NORMAL);

        textLight = (TextView) findViewById(R.id.textLight);
    }

    protected void onResume(){
        super.onResume();
        sMgr.registerListener(this,mLight,sMgr.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        sMgr.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT){
            float lux = event.values[0];
            textLight.setText(Float.toString(lux));
        }
    }
}
