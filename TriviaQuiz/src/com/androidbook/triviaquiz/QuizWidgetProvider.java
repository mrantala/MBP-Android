package com.androidbook.triviaquiz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class QuizWidgetProvider extends AppWidgetProvider {

	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // push this to a Service so it runs in the background
        // We can't use a thread because the Provider may not remain around
        // (Don't forget to add the service entry to the Manifest)

        //Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
        //context.startService(serviceIntent);        
		
		//onUpdateAction(context);
		
		Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
		context.startService(serviceIntent);
    }
	
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Note: Ignoring the appWidgetids is safe, but could stop an update for instance
        // of this app widget if more than one is running. This widget is not designed to be
        // a multi-instance widget.
        Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
        context.stopService(serviceIntent);

        super.onDeleted(context, appWidgetIds);

    }
    
	public static class WidgetUpdateService extends Service {
		Thread widgetUpdateThread = null;
		private static final String DEBUG_TAG = "WidgetUpdateServices";
		
		@Override
		public int onStartCommand(Intent intent, int flags, final int startId){
			widgetUpdateThread = new Thread() {
				public void run (){
					Context context = WidgetUpdateService.this;
					WidgetData widgetData = new WidgetData("Unknown", "NA", "");
					widgetData.nickname = "Hey Matt";
					widgetData.score = "1280";
					
					String packageName = context.getPackageName();
					
					RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
					remoteView.setTextViewText(R.id.widget_nickname, widgetData.nickname);
					remoteView.setTextViewText(R.id.widget_score, "Score: "+widgetData.score);
					remoteView.setImageViewResource(R.id.widget_image, R.drawable.avatar);
		
					try {
						Intent launchAppIntent = new Intent(context, QuizMenuActivity.class);
						PendingIntent launchApppendingIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						remoteView.setOnClickPendingIntent(R.id.widget_view, launchApppendingIntent);
						
						 // get the Android component name for the QuizWidgetProvider
			            ComponentName quizWidget = new ComponentName(context, QuizWidgetProvider.class);

			            // get the instance of the AppWidgetManager
			            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

			            // update the widget
			            appWidgetManager.updateAppWidget(quizWidget, remoteView);
						
					} catch (Exception e ) {
						Log.e(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG, "Unable to set launch Intent");
					}


                    if (!WidgetUpdateService.this.stopSelfResult(startId)) {
                        Log.e(DEBUG_TAG, "Failed to stop service");
                    }
 
				}
			};
			widgetUpdateThread.start();
			return START_REDELIVER_INTENT;
		}

		@Override
		public void onDestroy(){
			widgetUpdateThread.interrupt();
			super.onDestroy();
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	public static class WidgetData {
        String nickname;
        String score;
        String avatarUrl;

        public WidgetData(String nickname, String score, String avatarUrl) {
            super();
            this.nickname = nickname;
            this.score = score;
            this.avatarUrl = avatarUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
