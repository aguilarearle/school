package ai.dm.rtspstream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.majorkernelpanic.streaming.MediaStream;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
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
        RtspClient.Callback, Session.Callback,
        SurfaceHolder.Callback, OnCheckedChangeListener{

    public final static String TAG = "MainActicity";

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

    private RtspClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mButtonSave     = (Button) findViewById(R.id.video);
        mButtonVideo    = (Button) findViewById(R.id.save);
        mButtonStart    = (ImageButton) findViewById(R.id.start);
        mButtonFlash    = (ImageButton) findViewById(R.id.flash);
        mButtonCamera   = (ImageButton) findViewById(R.id.camera);
        mButtonSettings = (ImageButton) findViewById(R.id.settings);

        mSurfaceView          = (SurfaceView) findViewById(R.id.surface);
        mEdittextURI          = (EditText) findViewById(R.id.uri);
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
        mEdittextURI.setText(mPrefs.getString("uri", getString(R.string.default_stream)));
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
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);


        mSurfaceView.getHolder().addCallback(this);
        selectQuality();

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
    private void toggleStream(){
        mProgressBar.setVisibility(View.VISIBLE);
        if(!mClient.isStreaming()){
            String ip, port, path;


            // Save the content user inputs in sharedprefs
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            Editor editor = mPrefs.edit();
            editor.putString("uri", mEdittextURI.getText().toString());
            editor.putString("password", mEditTextPassword.getText().toString());
            editor.putString("username", mEditTextUsername.getText().toString());
            editor.commit();

            Pattern uri = Pattern.compile("rtsp://(.+):(\\d*)/(.+)");
            Matcher m = uri.matcher(mEdittextURI.getText().toString());
            ip = m.group(1);
            port = m.group(2);
            path = m.group(3);

            mClient.setCredentials(mEditTextUsername.getText().toString(), mEditTextPassword.toString());
            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/"+path);
            mClient.startStream();
            
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start:
                mLayoutServerSettings.setVisibility(View.GONE);
                toggleStream();
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
    public void onRtspUpdate(int message, Exception exception) {

    }

    @Override
    public void onBitrateUpdate(long bitrate) {

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {

    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionStopped() {

    }
}
