package ai.dm.streamv0;

import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.video.VideoQuality;

import android.content.DialogInterface;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;

import android.preference.PreferenceManager;

import android.util.Log;

import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity implements OnClickListener, Session.Callback, SurfaceHolder.Callback{

    private final static String TAG = "MainActivity";

    private Button mButton1, mButton2;
    private SurfaceView mSurfaceView;
    private EditText mEditText;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        // Sets the port of the RTSP server to 1234
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(RtspServer.KEY_PORT, String.valueOf(3000));
        editor.commit();

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mEditText = (EditText) findViewById(R.id.editText1);

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .build();

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(this);


    }

    @Override
    public void onResume(){
        super.onResume();
        if(mSession.isStreaming()){
            mButton1.setText(R.string.stop);
        } else {
            mButton1.setText(R.string.start);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSession.release();
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
        mSession.stop();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button1){
            // Begins/Stops stream
            mSession.setDestination(mEditText.getText().toString());

            if(!mSession.isStreaming()){
                mSession.configure();
                this.startService(new Intent(this,RtspServer.class));

            } else {
                mSession.stop();
            }
            mButton1.setEnabled(false);
        } else{
            mSession.switchCamera();
        }
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        Log.d(TAG, "Bitrate: "+bitrate);
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mButton1.setEnabled(true);
        if(e != null){
            logError(e.getMessage());
        }
    }

    @Override
    public void onPreviewStarted() {
        Log.d(TAG, "Preview configured");
    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG, "Preview Configure.");
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();

    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG, "Session started.");
        mButton1.setEnabled(true);
        mButton1.setText(R.string.stop);
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG, "Sessoim stopped.");
        mButton1.setEnabled(true);
        mButton1.setText(R.string.start);
    }

    private void logError(final String msg){
        final String error = (msg == null) ? "Error unknown.": msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(error).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
