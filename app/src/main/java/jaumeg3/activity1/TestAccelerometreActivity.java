package jaumeg3.activity1;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {
    private SensorManager accelerometerSensorManager;
    private Sensor accelerometerSensor;
    private SensorManager lightSensorManager;
    private Sensor lightSensor;
    private boolean color = false;
    private TextView view1, view2, view3;
    private long lastUpdate;
    private float downLevel, upLevel, lastLightValue;


    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        view1 = findViewById(R.id.textView1);
        view2 = findViewById(R.id.textView2);
        view3 = findViewById(R.id.textView3);

        view1.setBackgroundColor(Color.GREEN);
        view3.setBackgroundColor(Color.YELLOW);

        accelerometerSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor =
                    accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            accelerometerSensorManager.registerListener(this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            // register this class as a listener for the accelerometer sensor

            String capabilities =
                    "Resolution: " + accelerometerSensor.getResolution() + "\n" +
                    "Power: " + accelerometerSensor.getPower() + "\n" +
                    "Maximum Range: " + accelerometerSensor.getMaximumRange();
            view2.setText(String.format("%s\n%s", getString(R.string.shake), capabilities));
        } else {
            view2.setText(R.string.accelerometerNotFound);
        }
        lastUpdate = System.currentTimeMillis();

        lightSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            lightSensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            float maxRang = lightSensor.getMaximumRange();
            downLevel = maxRang / 3 ;
            upLevel = 2 * (maxRang / 3);
            view3.setText(String.format("%s\n%s", getString(R.string.lightSensorUp), maxRang));
        } else {
            Toast.makeText(this, "Light Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accelerometerSensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        lightSensorManager.registerListener(this, lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        manageSensors(event);
    }

    private void manageSensors(SensorEvent event) {
        if (event.sensor == accelerometerSensor) getAccelerometer(event);
        else if (event.sensor == lightSensor) getLight(event);
    }

    private void getLight(SensorEvent event) {
        float values[] = event.values;
        float x = values[0];
        long actualTime = System.currentTimeMillis();
        if ((Math.abs(lastLightValue - x)) >= 100.0) {
            if (actualTime - lastUpdate < 200) {
                return;
            }

            lastUpdate = actualTime;
            lastLightValue = x;
            if (x < downLevel) {
                //Toast.makeText(this, "LOW Intensity", Toast.LENGTH_SHORT).show();
                view3.setText("LOW Intensity" + "\n" + "");
            } else if (x > upLevel) {
                //Toast.makeText(this, "HIGH Intensity", Toast.LENGTH_SHORT).show();
                view3.setText("HIGH Intensity");
            } else {
                //Toast.makeText(this, "MEDIUM Intensity", Toast.LENGTH_SHORT).show();
                view3.setText("MEDIUM Intensity");
            }
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float values[] = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
            if (color) {
                view1.setBackgroundColor(Color.GREEN);

            } else {
                view1.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        accelerometerSensorManager.unregisterListener(this);
        lightSensorManager.unregisterListener(this);
    }
} 
