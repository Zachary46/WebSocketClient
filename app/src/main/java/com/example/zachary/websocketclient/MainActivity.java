package com.example.zachary.websocketclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String wsurl = "ws://169.254.193.190:8080/firstWs/websocketServer";
    private static final String TAG = "MainActivity";
    private WebSocketConnection mConnect = new WebSocketConnection();
    private EditText mContent;
    private Button mSend,mLogin;
    private TextView mText;
    private EditText mUserName;
    private EditText mToSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindObject();
        connect();
    }

    /**
     * 绑定控件
     */
    private void bindObject() {
        mContent = (EditText) findViewById(R.id.et_content);
        mSend = (Button) findViewById(R.id.btn_send);
        mLogin = (Button) findViewById(R.id.btn_login);
        mText = (TextView) findViewById(R.id.tv_test);
        mUserName = (EditText) findViewById(R.id.et_username);
        mToSb = (EditText) findViewById(R.id.et_to);
        mSend.setOnClickListener(this);
        mLogin.setOnClickListener(this);
    }

    /**
     * websocket连接，接收服务器消息
     */
    private void connect() {
        Log.i(TAG, "ws connect....");
        try {
            mConnect.connect(wsurl, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.i(TAG, "Status:Connect to " + wsurl);
                 //   sendUsername();
                }

                //服务端发送来的信息
                @Override
                public void onTextMessage(String payload) {
                    Log.i(TAG, payload);
                    mText.setText(payload != null ? payload : "");
//
                    //发送状态栏通知
                    OpenNotice();
                }


                @Override
                public void onClose(int code, String reason) {
                    Log.i(TAG, "Connection lost..");
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    private void OpenNotice() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")//设置通知栏标题
                .setContentText("你有新消息") //设置通知栏显示内容
        .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
            //  .setNumber(number) //设置通知集合的数量
                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(1, mBuilder.build());
    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 发送用户名给服务器
     */
    private void sendUsername() {
        String user = mUserName.getText().toString();
        if (user != null && user.length() != 0)
            mConnect.sendTextMessage(user);
        else
            Toast.makeText(getApplicationContext(), "不能为空", Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessage(String msg) {
        if (mConnect.isConnected()) {
            mConnect.sendTextMessage(msg);
        } else {
            Log.i(TAG, "no connection!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConnect.disconnect();
    }

    @Override
    public void onClick(View view) {
        if (view == mSend) {
            String content = mToSb.getText().toString() + "@" + mContent.getText().toString();
            if (content != null && content.length() != 0)
                sendMessage(content);
            else
                Toast.makeText(getApplicationContext(), "不能为空", Toast.LENGTH_SHORT).show();
        }else if (view==mLogin){
            sendUsername();
        }
    }
}
