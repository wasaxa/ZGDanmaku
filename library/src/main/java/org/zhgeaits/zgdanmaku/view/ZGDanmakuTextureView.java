/*
 * Copyright (C) 2016 Zhang Ge <zhgeaits@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zhgeaits.zgdanmaku.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import org.zhgeaits.zgdanmaku.controller.IZGDanmakuController;
import org.zhgeaits.zgdanmaku.controller.ZGDanmakuController;
import org.zhgeaits.zgdanmaku.model.ZGDanmakuItem;

/**
 * Created by zhgeaits on 16/2/25.
 * 基于GLTextureView实现的弹幕，GLTextureView来自github开源的实现
 * https://github.com/romannurik/muzei/blob/master/main/src/main/java/com/google/android/apps/muzei/render/GLTextureView.java
 * 这个效率比另外一个TextureView实现的要高，毕竟是google自己写的
 */
public class ZGDanmakuTextureView extends GLTextureView implements IZGDanmakuView {

    private Context mContext;
    private IZGDanmakuController mDanmakuController;
    private IZGDanmakuRenderer mRenderer;

    public ZGDanmakuTextureView(Context context) {
        super(context);
        init(context);
    }

    public ZGDanmakuTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setEGLContextClientVersion(2);

        //设置EGL的像素配置
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mRenderer = new ZGDanmakuRenderer();
        setRenderer((GLSurfaceView.Renderer) mRenderer);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mRenderer.setDisplayDensity(displayMetrics.density);

        //设置textureview透明
        setOpaque(false);

        // 设置渲染模式为被动渲染，在调用start()方法以后再设置为主动模式,
        // 主动模式有一条后台OPENGL线程每帧都调用onDrawFrame方法
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mDanmakuController = new ZGDanmakuController(context, mRenderer);
    }

    @Override
    public void setSpeed(float speed) {
        mDanmakuController.setSpeed(speed);
    }

    @Override
    public void start() {
        stop();
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
        mDanmakuController.start();
    }

    @Override
    public void stop() {
        mDanmakuController.stop();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //清屏
        mRenderer.getRendererDanmakuList().clear();
        requestRender();
    }

    @Override
    public void hide() {
        mDanmakuController.hide();
    }

    @Override
    public void show() {
        mDanmakuController.show();
    }

    @Override
    public void pause() {
        if (isStarted()) {
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mDanmakuController.pause();
        }
    }

    @Override
    public void resume() {
        if (isStarted()) {
            mDanmakuController.resume();
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }
    }

    @Override
    public boolean isStarted() {
        return mDanmakuController.isStarted();
    }

    @Override
    public boolean isPaused() {
        return mDanmakuController.isPause();
    }

    @Override
    public boolean isHided() {
        return mDanmakuController.isHide();
    }

    @Override
    public void setLines(int lines) {
        mDanmakuController.setLines(lines);
    }

    @Override
    public void setLeading(float leading) {
        mDanmakuController.setLeading(leading);
    }

    @Override
    public void shotTextDanmaku(String text) {
        ZGDanmakuItem item = new ZGDanmakuItem(text, mContext);
        mDanmakuController.addDanmaku(item);
    }

    @Override
    public void shotTextDanmaku(String text, int color, float size) {
        ZGDanmakuItem item = new ZGDanmakuItem(text, mContext);
        item.setTextColor(color);
        item.setTextSize(size);
        mDanmakuController.addDanmaku(item);
    }

    @Override
    public void shotTextDanmakuAt(String text, long time) {
        ZGDanmakuItem item = new ZGDanmakuItem(text, mContext, time);
        mDanmakuController.addDanmaku(item);
    }

    @Override
    public void shotTextDanmakuAt(String text, long time, int color, float size) {
        ZGDanmakuItem item = new ZGDanmakuItem(text, mContext, time);
        item.setTextColor(color);
        item.setTextSize(size);
        mDanmakuController.addDanmaku(item);
    }

    @Override
    public void seek(long time) {
        mDanmakuController.updateTime(time);
    }
}
