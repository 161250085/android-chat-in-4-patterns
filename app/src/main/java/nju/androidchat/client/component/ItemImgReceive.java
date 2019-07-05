package nju.androidchat.client.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import nju.androidchat.client.R;

public class ItemImgReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;


    public ItemImgReceive(Context context, String image, UUID messageId) throws IOException {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_img_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        setImage(image);
    }

    public void init(Context context) {

    }

    public String getImage() {
        return imageView.getDrawable().toString();
    }

    public void setImage(String img_url) throws IOException {
        URL url = new URL(img_url);
        Drawable mDrawable= Drawable.createFromStream(url.openStream(), "src");
        imageView.setImageDrawable(mDrawable);
    }
}
