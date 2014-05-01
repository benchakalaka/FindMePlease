package edu.glyndwr.tools;

import android.media.MediaPlayer;
import android.os.Environment;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
 
public class TextPlayer extends Thread {
 
    private String text;
 
    public TextPlayer(String text) {
        this.text = text;
    }
 
    @Override
    public void run() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://translate.google.com/translate_tts?tl=en&q="+ URLEncoder.encode(text, "UTF-8"));
            //HttpGet get = new HttpGet("http://translate.google.com/translate_tts?tl=ru&q="+ URLEncoder.encode(text, "UTF-8"));
            get.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11"));
            HttpResponse response = httpclient.execute(get);
            long length = response.getEntity().getContentLength();
            InputStream content = response.getEntity().getContent();  
            String tmpf = Environment.getExternalStorageDirectory()+"/speech.mp3";
            File tmpMp3 = new File(tmpf);
            FileOutputStream fos = new FileOutputStream(tmpMp3);
            long readed = 0;
            byte[] buf = new byte[512];
            while ((readed+=content.read(buf))<length) {
                fos.write(buf);
            }
            fos.flush();
            fos.close();
            MediaPlayer player = new MediaPlayer();
            player.reset();
            player.setDataSource(tmpf);
            player.prepare();
            player.start();
            while (player.isPlaying()) {}
            tmpMp3.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
