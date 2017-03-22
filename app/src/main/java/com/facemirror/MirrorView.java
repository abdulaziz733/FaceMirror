package com.facemirror;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera.CameraInfo;

/**
 * Created by abdul on 3/21/2017.
 */

public class MirrorView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String DEBUG_TAG = MirrorView.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraId;
    private Context mContext;

    public MirrorView(Context context, Camera camera, int mCameraId) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.mCameraId = mCameraId;
        this.mContext = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception error) {
            Log.d(DEBUG_TAG,
                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }

        // can't make changes while mPreviewLayout is active
        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }
        try {

            // start up the mPreviewLayout
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception error) {
            Log.d(DEBUG_TAG,
                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setCameraDisplayOrientationAndSize(int rotate) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int rotation = rotate;
        int degrees = rotation * 90;

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);

        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        if (result == 90 || result == 270) {
            mHolder.setFixedSize(previewSize.height, previewSize.width);
        } else {
            mHolder.setFixedSize(previewSize.width, previewSize.height);

        }
    }
}
