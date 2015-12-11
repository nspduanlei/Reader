package com.example.testscroll;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;

import com.example.testscroll.model.MyPage;
import com.example.testscroll.utils.CharsetDetector;
import com.example.testscroll.view.FlipperLayout;
import com.example.testscroll.view.FlipperLayout.TouchListener;
import com.example.testscroll.view.ReadView;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class MainActivity extends Activity implements OnClickListener, TouchListener {

    private String text = "";

    private int textLength = 8000;

    private static final int COUNT = 1000;

//    private int currentTopEndIndex = 0;
//    private int currentShowEndIndex = 0;
//    private int currentBottomEndIndex = 0;

    private static final int MSG_DRAW_TEXT = 1;

    //ReadView curReadView;

    CharBuffer buffer = CharBuffer.allocate(8000);

    //int position = 0;

    //ReadView nextReadView;

    //ReadView preReadView;

    int pageIndex = 1;

    boolean oneIsLayout, twoIsLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DRAW_TEXT:

                    FlipperLayout rootLayout = (FlipperLayout) findViewById(R.id.container);

                    View recoverView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_new, null);
                    View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_new, null);
                    View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_new, null);

                    rootLayout.initFlipperViews(MainActivity.this, view2, view1, recoverView);

                    final ReadView readView1 = (ReadView) view1.findViewById(R.id.textview);
                    final ReadView readView2 = (ReadView) view2.findViewById(R.id.textview);

                    buffer.position(0);
//                    readView1.setText(buffer, new MyPage(1), new ReadView.LayoutListener() {
//                        @Override
//                        public void onLayout(int charNum) { //第一页的view加载完成, 可以获取到第二页的开始位置
//
//                            buffer.position(charNum);
//                            readView2.setText(buffer, new MyPage(2));
//                        }
//                    });

                    //填充第一页的文本
                    readView1.setText(buffer);

                    //填充第二页的文本
                    ViewTreeObserver vto1 = readView1.getViewTreeObserver();
                    vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (oneIsLayout)
                                return;

                            int charNum = readView1.getCharNum();


                            MyPage page = new MyPage();
                            page.setPageSize(charNum);
                            page.setStartPosition(charNum);

                            //将第一页的数据存储在数据库中，如果该数据不存在
                            if (isSavePage(1)) {
                                page.update(1);
                            } else {
                                page.save();
                            }

                            buffer.position(charNum);
                            readView2.setText(buffer);
                            oneIsLayout = true;
                        }
                    });



                    ViewTreeObserver vto2 = readView2.getViewTreeObserver();
                    vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (twoIsLayout)
                                return;

                            int charNum = readView2.getCharNum();

                            //将第二页的数据存储在数据库中, 如果该数据不存在
                            MyPage page = new MyPage();
                            page.setPageSize(charNum);
                            page.setStartPosition(charNum + getStartPosition(1));

                            if (isSavePage(2)) {
                                page.update(2);
                            } else {
                                page.save();
                            }

                            twoIsLayout = true;
                        }
                    });


                    break;
            }
        }
    };



    //该页是否存储
    private boolean isSavePage(int pageNo) {
        return DataSupport.find(MyPage.class, pageNo) != null;
    }

    //获取该页的结束位置
    private int getStartPosition(int pageNo) {
        if (isSavePage(pageNo)) {
            return DataSupport.find(MyPage.class, pageNo).getStartPosition();
        }
        return 0;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ReadingThread().start();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public View createView(final int direction, View curView) {

        View newView;
        if (direction == TouchListener.MOVE_TO_LEFT) { //下一页

            pageIndex ++;

            buffer.position(getStartPosition(pageIndex));

            newView = LayoutInflater.from(this).inflate(R.layout.view_new, null);
            final ReadView readView = (ReadView) newView.findViewById(R.id.textview);
            readView.setText(buffer);

            ViewTreeObserver vto2 = readView.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int charNm = readView.getCharNum();

                    MyPage page = new MyPage();
                    page.setPageSize(charNm);
                    page.setStartPosition(getStartPosition(pageIndex) + charNm);
                    page.setBookId(pageIndex + 1);

                    if (isSavePage(pageIndex + 1)) {
                        page.update(pageIndex + 1);
                    } else {
                        page.save();
                    }
                }
            });
        } else {  //上一页
            pageIndex --;
            buffer.position(getStartPosition(pageIndex-1));

            newView = LayoutInflater.from(this).inflate(R.layout.view_new, null);
            ReadView readView = (ReadView) newView.findViewById(R.id.textview);
            readView.setText(buffer);
        }



        Log.d("test0001", "page=" + pageIndex);

        return newView;
    }

    @Override
    public boolean whetherHasPreviousPage() {
        //return currentShowEndIndex > COUNT;
        return true;
    }

    @Override
    public boolean whetherHasNextPage() {
        //return currentShowEndIndex < textLength;
        return true;
    }

    @Override
    public boolean currentIsFirstPage() {
//		boolean should = currentTopEndIndex > COUNT;
//		if (!should) {
//			currentBottomEndIndex = currentShowEndIndex;
//			currentShowEndIndex = currentTopEndIndex;
//			currentTopEndIndex = currentTopEndIndex - COUNT;
//		}

        boolean should = true;

        return should;
    }

    @Override
    public boolean currentIsLastPage() {
//		boolean should = currentBottomEndIndex < textLength;
//		if (!should) {
//			currentTopEndIndex = currentShowEndIndex;
//			final int nextIndex = currentBottomEndIndex + COUNT;
//			currentShowEndIndex = currentBottomEndIndex;
//			if (textLength > nextIndex) {
//				currentBottomEndIndex = nextIndex;
//			} else {
//				currentBottomEndIndex = textLength;
//			}
//		}

        boolean should = true;

        return should;
    }

    private class ReadingThread extends Thread {
        public void run() {
//			AssetManager am = getAssets();
//
//            InputStream inputStream = null;
//            ByteArrayOutputStream outputStream = null;
//
//			try {
//                inputStream = am.open("text.txt");
//				if (inputStream != null) {
//
//                    outputStream = new ByteArrayOutputStream();
//					int i;
//					while ((i = inputStream.read()) != -1) {
//                        outputStream.write(i);
//					}
//					text = new String(outputStream.toByteArray(), "UTF-8");
//
//					mHandler.obtainMessage(MSG_DRAW_TEXT).sendToTarget();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//
//                if (inputStream != null) {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (outputStream != null) {
//                    try {
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

            BufferedReader reader;
            AssetManager assets = getAssets();
            try {
                InputStream in = assets.open("text.txt");
                Charset charset = CharsetDetector.detect(in);
                reader = new BufferedReader(new InputStreamReader(in, charset));

                reader.read(buffer);

                mHandler.obtainMessage(MSG_DRAW_TEXT).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
