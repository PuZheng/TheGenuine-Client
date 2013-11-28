package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.puzheng.the_genuine.camera.CameraManager;
import com.puzheng.the_genuine.camera.AmbientLightManager;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.decoding.CaptureActivityHandler;
import com.puzheng.the_genuine.decoding.InactivityTimer;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;
import com.puzheng.the_genuine.views.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-28.
 */
public class BarCodeActivity extends Activity implements SurfaceHolder.Callback {
    public static final String TAG_VERIFICATION_INFO = "VERIFICATION_INFO";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private AmbientLightManager ambientLightManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(getApplicationContext());
        setContentView(R.layout.activity_barcode);
        ambientLightManager = new AmbientLightManager(getApplicationContext());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ambientLightManager.start(CameraManager.get());
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        ambientLightManager.stop();
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        if (Misc.isEmptyString(resultString)) {
            Toast.makeText(BarCodeActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            //显示
            onPause();
            PoliteBackgroundTask.Builder<VerificationInfo> builder = new PoliteBackgroundTask.Builder<VerificationInfo>(this);
            builder.msg("已读取条码信息，正在验证真伪");
            builder.run(new PoliteBackgroundTask.XRunnable<VerificationInfo>() {
                @Override
                public VerificationInfo run() throws Exception {
                    return WebService.getInstance(BarCodeActivity.this).verify(resultString);
                }
            });
            builder.after(new PoliteBackgroundTask.OnAfter<VerificationInfo>() {


                @Override
                public void onAfter(VerificationInfo verificationInfo) {
                    Intent intent;
                    if (verificationInfo != null) {
                        intent = new Intent(BarCodeActivity.this, ProductActivity.class);
                        intent.putExtra(TAG_VERIFICATION_INFO, verificationInfo);
                    } else {
                        intent = new Intent(BarCodeActivity.this, CounterfeitActivity.class);
                    }
                    startActivity(intent);
                }
            });
            builder.create().start();
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
