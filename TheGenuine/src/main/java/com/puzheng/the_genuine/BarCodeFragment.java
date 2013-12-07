package com.puzheng.the_genuine;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.puzheng.the_genuine.camera.AmbientLightManager;
import com.puzheng.the_genuine.camera.CameraManager;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.decoding.CaptureActivityHandler;
import com.puzheng.the_genuine.decoding.InactivityTimer;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;
import com.puzheng.the_genuine.views.NavBar;
import com.puzheng.the_genuine.views.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public class BarCodeFragment extends Fragment implements SurfaceHolder.Callback {
    public static final String TAG_VERIFICATION_INFO = "VERIFICATION_INFO";
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private AmbientLightManager ambientLightManager;
    private View mRootView;

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public Handler getHandler() {
        return handler;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        if (Misc.isEmptyString(resultString)) {
            Toast.makeText(this.getActivity(), "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            //显示
            onPause();
            PoliteBackgroundTask.Builder<VerificationInfo> builder = new PoliteBackgroundTask.Builder<VerificationInfo>(this.getActivity());
            builder.msg("已读取条码信息，正在验证真伪");
            builder.run(new PoliteBackgroundTask.XRunnable<VerificationInfo>() {
                @Override
                public VerificationInfo run() throws Exception {
                    Pair<Float, Float> location = MyApp.getLocation();
                    return WebService.getInstance(BarCodeFragment.this.getActivity()).verify(resultString,
                            location.first, location.second);
                }
            });
            builder.after(new PoliteBackgroundTask.OnAfter<VerificationInfo>() {


                @Override
                public void onAfter(VerificationInfo verificationInfo) {
                    Intent intent;
                    if (verificationInfo != null) {
                        intent = new Intent(BarCodeFragment.this.getActivity(), ProductActivity.class);
                        intent.putExtra(TAG_VERIFICATION_INFO, verificationInfo);
                    } else {
                        intent = new Intent(BarCodeFragment.this.getActivity(), CounterfeitActivity.class);
                    }
                    startActivity(intent);
                }
            });
            builder.create().start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_barcode, container, false);
        CameraManager.init(this.getActivity());

        ambientLightManager = new AmbientLightManager(this.getActivity());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());

        ImageButton imageButton = (ImageButton) mRootView.findViewById(R.id.imageButton);
        if (!MainActivity.isNfcEnabled) {
            imageButton.setVisibility(View.GONE);
        } else {
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container, new NfcFragment());
                    ft.commit();
                }
            });
        }


        NavBar navBar = (NavBar) mRootView.findViewById(R.id.navBar);
        navBar.setContext(this.getActivity());
        return mRootView;
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        ambientLightManager.stop();
        CameraManager.get().closeDriver();
    }

    @Override
    public void onResume() {
        super.onResume();
        ambientLightManager.start(CameraManager.get());
        SurfaceView surfaceView = (SurfaceView) mRootView.findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (audioService != null && audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        viewfinderView = (ViewfinderView) mRootView.findViewById(R.id.viewfinder_view);
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

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            this.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
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

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
}
