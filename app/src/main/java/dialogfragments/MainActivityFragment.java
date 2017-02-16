package dialogfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import model.DoodleView;
import xyz.georgihristov.doodify.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends android.support.v4.app.Fragment {

    private DoodleView doodleView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;

    //determine is device was shook
    private static final int ACCELERATION_THRESHHOLD = 100000;
    //identify the external storage request
    private static final int SAVE_IMAGE_PERMISSION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        //display items on top bar:
        setHasOptionsMenu(true);

        //get reference to doodleView
        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        //init acceler values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //switch based on menu id
        switch (item.getItemId()){
            case R.id.color:
                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");
                return true;
            case R.id.line_width:
                LineWidthDialogFragment width = new LineWidthDialogFragment();
                width.show(getFragmentManager(), "line width dialog");
                return true;
            case R.id.delete_drawing:
                confirmErase();
                return true;
            case R.id.save:
                saveImage();
                return true;
            case R.id.share:
                shareImage();
                return true;
            case R.id.print:
                doodleView.printImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
            doodleView.shareImage();
        //Toast.makeText(getActivity(), "TEST", Toast.LENGTH_SHORT).show();
    }

    /*request permission android  6.0 marshmallow model*/
    private void saveImage() {
        //check if the app does not have permissions to save
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getContext().checkSelfPermission(Manifest
                    .permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //show explanation why permission is needed
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.permission_explanation);

                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]
                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    SAVE_IMAGE_PERMISSION);
                        }
                    });
                    builder.create().show();
                }else {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            SAVE_IMAGE_PERMISSION);
                }
            }else {
                doodleView.saveImage();
            }
        }else {
            doodleView.saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case SAVE_IMAGE_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doodleView.saveImage();
                    return;
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    //listen for accelerometer events
    private void enableAccelerometerListening() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

    //register to listen for events
        sensorManager.registerListener(sensorEventListener,sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        disableSensorListener();
    }

    public DoodleView getDoodleView(){
        return doodleView;
    }

    public void setDialogOnScreen(boolean visible){

        dialogOnScreen = visible;

    }

    private void disableSensorListener() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorEventListener,sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        //determine if the device was shook
        @Override
        public void onSensorChanged(SensorEvent event) {
            //ensure that other dialogs are not visible
            if(!dialogOnScreen){
                //get x,y,z from the sensor
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                //save the previous values
                lastAcceleration = currentAcceleration;

                //calculate the current acceleration
                currentAcceleration = x * x + y * y + z * z;

                //calculate the change in acceleration
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                //if acceleration is above threshold - erase
                if(acceleration > ACCELERATION_THRESHHOLD){
                    confirmErase();
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(),"erase dialog");
    }
}
