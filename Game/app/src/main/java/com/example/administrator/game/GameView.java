package com.example.administrator.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-11.
 */

public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener{
    private Thread mThread;
    volatile private boolean mIsRunnable;
    volatile private float mTouchedX;
    volatile private float mTouchedY;
    private final Bundle mSavedInstanceState;
    public GameView(final Context context, Bundle savedInstanceState){
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mSavedInstanceState = savedInstanceState;

        mHandler = new Handler(){
            public void handleMessage(Message message){
                Intent intent = new Intent(context,clearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());
                context.startActivity(intent);
            }
        };
    }

    private Pad mPad;
    private ArrayList<DrawableItem> mItemList;
    private ArrayList<Block> mBlockList;

    private float mPadHalfWidth;
    private Ball mBall;
    private float mBallRadius;

    private float mBlockWidth;
    private float mBlockHeight;

    static final int BLOCK_COUNT=100;

    private int mLife;
    private long mGameStarTime;
    private Handler mHandler;

    public void readyObjects(int width, int height){
        mBlockWidth = width /10;
        mBlockHeight = height/20;
        mItemList = new ArrayList<DrawableItem>();
        mBlockList = new ArrayList<Block>();
        mLife = 1;
        for(int i=0;i<BLOCK_COUNT;i++){
            float blockTop = i /10 * mBlockHeight;
            float blockLeft = i%10 * mBlockWidth;
            float blockBottom = blockTop+mBlockHeight;
            float blockRight = blockLeft+mBlockWidth;
            //mItemList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));
            mBlockList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));
        }
        mItemList.addAll(mBlockList);

        mPad = new Pad(height * 0.8f , height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;
        mBallRadius = width<height ? width/40: height/40;

        mBall = new Ball(mBallRadius,width/2,mPad.getTop()+10);
        mItemList.add(mBall);
        mGameStarTime = System.currentTimeMillis();

        if(mSavedInstanceState!=null){
            mLife = mSavedInstanceState.getInt(KEY_LIFE);
            mGameStarTime = mSavedInstanceState.getLong(KEY_GAMEE_START_TIME);
            mBall.restore(mSavedInstanceState.getBundle(KEY_BALL),width,height);
            for(int i=0;i<BLOCK_COUNT;i++){
                mBlockList.get(i).restore(mSavedInstanceState.getBundle((KEY_BLOCK+String.valueOf(i))) );
            }
        }

    }

    private int getBlockCount(){
        int count=0;
        for(Block block : mBlockList){
            if(block.isExist()){
                count++;
            }
        }
        return count;
    }

    private Block getBlock(float x, float y){
        int index = (int)(x/mBlockWidth) + (int)(y / mBlockHeight) *10;
        if(0<=index && index<BLOCK_COUNT){
            Block block = (Block) mItemList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
    }


     public void start(){
        mThread = new Thread(new Runnable(){
            public void run(){
                Paint paint = new Paint();

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC,ToneGenerator.MAX_VOLUME);
                int collisionTime = 0;
                int soundIndex = 0;
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                while (true) {
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        if (mBall == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;

                        //볼과 화면의 충돌판정(좌우)
                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                        }
                        //볼 화명 충돌(하)
                        if(ballTop<0){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                        }

                        if (ballTop > getHeight()) {
                            if (mLife > 0) {
                                mLife--;
                                mBall.reset();
                            } else {

                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(clearActivity.EXTRA_IS_CLEAR, false);
                                bundle.putInt(clearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                bundle.putLong(clearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStarTime);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                                return;

                            }
                        }


                        //블록과 공의 충돌판정(상)
                        Block leftBlock = getBlock(ballLeft,mBall.getY());
                        Block topBlock = getBlock(mBall.getX(),ballTop);
                        Block rightBlock = getBlock(ballRight,mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(),ballBottom);
                        boolean isCollision = false;
                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        if(topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        if(rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        if(bottomBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }

                        if(isCollision){
                            if(collisionTime>0){
                                if(soundIndex<15){
                                    soundIndex++;
                                }
                            }else{
                                soundIndex =1;
                            }
                            collisionTime =10;
                            toneGenerator.startTone(soundIndex,10);

                        }else{
                            collisionTime --;
                        }
                        //블록과 패드의 충돌판정(하)
                        float padTop = mPad.getTop();
                        float ballSpeedY =mBall.getSpeedY();

                        if(ballBottom > padTop && ballBottom-ballSpeedY< padTop && padLeft< ballRight&& padRight>ballLeft ){
                            if(ballSpeedY<mBlockHeight/3){
                                ballSpeedY *= -1.05f;
                            }else{
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX()+(mBall.getX()-mTouchedX)/10;
                            if(ballSpeedX>mBlockWidth/5){
                                ballSpeedX = mBlockWidth /5;
                            }
                            mBall.setSpeedX(ballSpeedX);
                            mBall.setSpeedY(ballSpeedY);
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0,10);
                        }

                        for (DrawableItem item : mItemList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);

                        if(isCollision && getBlockCount()==0){
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(clearActivity.EXTRA_IS_CLEAR,false);
                            bundle.putInt(clearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                            bundle.putLong(clearActivity.EXTRA_TIME,System.currentTimeMillis()-mGameStarTime);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                    long sleepTime = 16 - System.currentTimeMillis() + startTime;
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {

                        }
                    }
                }
                toneGenerator.release();
            }
        });
        mIsRunnable = true;
        mThread.start();
    }

    private static final String KEY_LIFE = "life";
    private static final String KEY_GAMEE_START_TIME = "game_start_time";
    private static final String KEY_BALL = "ball";
    private static final String KEY_BLOCK = "block";

    public void onSaveInstanceState(Bundle outState){
        outState.putInt(KEY_LIFE,mLife);
        outState.putLong(KEY_GAMEE_START_TIME,mGameStarTime);
        outState.putBundle(KEY_BALL,mBall.save(getWidth(),getHeight()));
        for(int i=0;i<BLOCK_COUNT;i++){
            outState.putBundle(KEY_BLOCK+String.valueOf(i),mBlockList.get(i).save());
        }
    }


    public void stop(){
        mIsRunnable = false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);

    }
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
       mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }






}
