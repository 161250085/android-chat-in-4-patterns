package nju.androidchat.client.hw1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemImgReceive;
import nju.androidchat.client.component.ItemImgSend;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        // 如果是自己发的，判断是图片还是文字信息分别处理
                        boolean is_image = false;
                        is_image = text.length()>7&&text.charAt(0) == '!' && text.charAt(1) == '[' && text.charAt(2) == ']' && text.charAt(3) == '(' && text.charAt(4) == '{' && text.charAt(text.length() - 2) == '}' && text.charAt(text.length() - 1) == ')';
                        String url = is_image?text.substring(5,text.length()-2):"";
                         if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                            if(!is_image) {
                                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
                            }else{
                                    try {
                                        content.addView(new ItemImgSend(this,url,message.getMessageId(),this));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                            }
                        } else {
                            if(!is_image){
                                content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                            }else {
                                    try {


                                        content.addView(new ItemImgReceive(this, url,message.getMessageId()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                                    }

                            }

                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
