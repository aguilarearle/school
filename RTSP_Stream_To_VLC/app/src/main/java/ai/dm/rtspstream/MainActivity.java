package ai.dm.rtspstream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.dm.rtspstream.api.CustomRtspServer;
import net.majorkernelpanic.streaming.MediaStream;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.VideoQuality;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.ComponentName;


import android.hardware.Camera.CameraInfo;

import android.os.Bundle;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends Activity implements OnClickListener,
        Session.Callback,
        SurfaceHolder.Callback, OnCheckedChangeListener{

    public final static String TAG = "MainActivity";

    private Button mButtonSave;
    private Button mButtonVideo;

    private ImageButton mButtonStart;
    private ImageButton mButtonFlash;
    private ImageButton mButtonCamera;
    private ImageButton mButtonSettings;

    private RadioGroup mRadioGroup;

    private FrameLayout mLayoutVideoSettings;
    private FrameLayout mLayoutServerSettings;

    private SurfaceView mSurfaceView;

    private TextView mTextBitrate;

    private EditText mEdittextURI;
    private EditText mEditTextPassword;
    private EditText mEditTextUsername;

    private ProgressBar mProgressBar;

    private Session mSession;

    private RtspServer mRtspServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mButtonSave     = (Button) findViewById(R.id.video);
        mButtonVideo    = (Button) findViewById(R.id.save);
        mButtonStart    = (ImageButton) findViewById(R.id.start);
        mButtonFlash    = (ImageButton) findViewById(R.id.flash);
        mButtonCamera   = (ImageButton) findViewById(R.id.camera);
        mButtonSettings = (ImageButton) findViewById(R.id.settings);

        mSurfaceView          = (SurfaceView) findViewById(R.id.surface);
        //mEdittextURI          = (EditText) findViewById(R.id.uri);
        mEditTextPassword     = (EditText) findViewById(R.id.password);
        mEditTextUsername     = (EditText) findViewById(R.id.username);
        mTextBitrate          = (TextView) findViewById(R.id.bitrate);
        mLayoutVideoSettings  = (FrameLayout) findViewById(R.id.video_layout);
        mLayoutServerSettings = (FrameLayout) findViewById(R.id.server_layout);
        mRadioGroup           = (RadioGroup) findViewById(R.id.radio);
        mProgressBar          = (ProgressBar) findViewById(R.id.progress_bar);

        mButtonSave.setOnClickListener(this);
        mButtonVideo.setOnClickListener(this);
        mButtonStart.setOnClickListener(this);
        mButtonFlash.setTag("off");
        mButtonCamera.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (mPrefs.getString("uri", null) != null)
            mLayoutServerSettings.setVisibility(View.GONE);
        //mEdittextURI.setText(mPrefs.getString("uri", getString(R.string.default_stream)));
        mEditTextPassword.setText(mPrefs.getString("Password", ""));
        mEditTextUsername.setText(mPrefs.getString("username", ""));


        // Configure Session Builder
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality( new AudioQuality(8000, 160000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(0)
                .setCallback(this)
                .build();

        // Configures RTSP client
        //mClient = new RtspClient();
        //mClient.setSession(mSession);
        //mClient.setCallback(this);

        this.startService(new Intent(this, CustomRtspServer.class));
        mSurfaceView.getHolder().addCallback(this);
        selectQuality();

    }

    public void onStart(){
        super.onStart();

    }

    private void selectQuality(){
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton) findViewById(id);

        if(button == null) return;

        String text = button.getText().toString();
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)\\D+(\\d+)\\D+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        matcher.find();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int framerate = Integer.parseInt(matcher.group(3));
        int bitrate = Integer.parseInt(matcher.group(4)) * 1000;

        mSession.setVideoQuality(new VideoQuality(width, height, framerate, bitrate));
        Toast.makeText(this, ((RadioButton)findViewById(id)).getText(), Toast.LENGTH_SHORT);

        Log.d(TAG, "Selected Resolution:" + width+"x"+height);


    }

    // Starts/stops stream and connects/disconnects server.
    private void toggleStream(){
        mProgressBar.setVisibility(View.VISIBLE);
        if(mRtspServer == null){


            // Save the content user inputs in sharedprefs
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Editor editor = mPrefs.edit();
            //editor.putString("uri", mEdittextURI.getText().toString());
            editor.putString("password", mEditTextPassword.getText().toString());
            editor.putString("username", mEditTextUsername.getText().toString());
            editor.commit();

            //mRtspServer.setAuthorization(mEditTextUsername.getText().toString(),mEditTextPassword.toString());
            bindService(new Intent(this, CustomRtspServer.class), mRtspServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            mRtspServer.removeCallbackListener(mRtspCallbackListener);
            unbindService(mRtspServiceConnection);
            // Stop stream (Disconnect RTSP server)
            this.stopService(new Intent(this,CustomRtspServer.class));

        }
    }
    private void enableUI(){
        mButtonStart.setEnabled(true);
        mButtonCamera.setEnabled(true);
    }
    private void logError(final String msg){
        final String error = (msg == null) ? "Error unknown" : msg;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.stopService(new Intent(this,CustomRtspServer.class));

        //mClient.stopStream();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start:
                mLayoutServerSettings.setVisibility(View.GONE);
                toggleStream();
                break;
            case R.id.flash:
                if(mButtonFlash.getTag().equals("on")){
                    mButtonFlash.setTag("off");
                    mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                } else{
                    mButtonFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
                    mButtonFlash.setTag("on");
                }
            case R.id.camera:
                mSession.switchCamera();
                break;
            case R.id.settings:
                if (mLayoutVideoSettings.getVisibility() == View.GONE &&
                        mLayoutServerSettings.getVisibility() == View.GONE){
                    mLayoutServerSettings.setVisibility(View.VISIBLE);
                } else {
                    mLayoutServerSettings.setVisibility(View.GONE);
                    mLayoutVideoSettings.setVisibility(View.GONE);
                }
                break;
            case R.id.video:
                mRadioGroup.clearCheck();
                mLayoutServerSettings.setVisibility(View.GONE);
                mLayoutVideoSettings.setVisibility(View.VISIBLE);
            case R.id.save:
                mLayoutServerSettings.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        mLayoutVideoSettings.setVisibility(View.GONE);
        mLayoutServerSettings.setVisibility(View.VISIBLE);
        selectQuality();
    }


    @Override
    public void onBitrateUpdate(long bitrate) {
        mTextBitrate.setText(""+bitrate/1000+" kbps");
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        switch (reason){
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                mButtonFlash.setTag("off");
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                VideoQuality quality = mSession.getVideoTrack().getVideoQuality();
                logError("The following settings are not supported on this phone: " +
                quality.toString() + " " + "("+e.getMessage()+")" );
                e.printStackTrace();
                return;
            case Session.ERROR_OTHER:
                break;
        }
        if(e != null){
            logError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Client.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }

    @Override
    public void onPreviewStarted() {
        if (mSession.getCamera() == CameraInfo.CAMERA_FACING_FRONT){
            mButtonFlash.setEnabled(false);
            mButtonFlash.setTag("off");
            mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        }
        else {
            mButtonFlash.setEnabled(true);
        }
    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video_active);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionStopped() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video);
        mProgressBar.setVisibility(View.GONE);
    }

    private ServiceConnection mRtspServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            mRtspServer = (CustomRtspServer) ((RtspServer.LocalBinder)service).getService();
            mRtspServer.addCallbackListener(mRtspCallbackListener);
            mRtspServer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}


    };

    private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {
        @Override
        public void onError(RtspServer server, Exception e, int error) {
            // Port is alreadu being used by another app
            if(error == RtspServer.ERROR_BIND_FAILED){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.port_used)
                        .setMessage(getString(R.string.bind_failed, "RTSP")).show();
            }
        }

        @Override
        public void onMessage(RtspServer server, int message) {

        }
    };

}
