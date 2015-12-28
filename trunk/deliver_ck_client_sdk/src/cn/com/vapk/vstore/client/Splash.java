package cn.com.vapk.vstore.client;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.com.vapk.vstore.client.R;

public class Splash extends Activity{

	int tick = 0;
	
//	ImageView loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Configuration config = getResources().getConfiguration();  
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){ 
			setContentView(R.layout.splash_landscape);
		}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){  
			setContentView(R.layout.splash_portrait);
		}
		
//		loading = (ImageView)findViewById(R.id.loading);
		
        Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new MyTask(), 1, 30);  

 	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {	
		super.onConfigurationChanged(config);
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){ 
			setContentView(R.layout.splash_landscape);
		}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){  
			setContentView(R.layout.splash_portrait);
		}
//		loading = (ImageView)findViewById(R.id.loading);
		
        Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new MyTask(), 1, 30);  
	}
    
    private class MyTask extends TimerTask{  
        @Override  
        public void run() {  
              
        	tick++;
            Message message = new Message();  
            message.what = 1;  
            mHandler.sendMessage(message);  

        }     
    }  

    private Handler mHandler = new Handler(){  
        Boolean opened=false;
        public synchronized void handleMessage(Message msg) {  
        	if(opened) return;
            switch (msg.what) {  
            case 1: 
//            	if(tick<=30)
//            		loading.setImageResource(R.drawable.loading0);
//            	if(tick>30&&tick<=60)
//            		loading.setImageResource(R.drawable.loading1);
//            	else if(tick>60&&tick<=90)
//            		loading.setImageResource(R.drawable.loading2);
//            	else if(tick>90&&tick<100)
//            		loading.setImageResource(R.drawable.loading3);
            	if(tick >= 100){
            		opened=true;
            		Intent intent = new Intent();
            		intent.setClass(Splash.this, ASC.class);
            		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		startActivity(intent);
            		finish();
            	}
                break;  
            }  
        };  
    };
    
}
