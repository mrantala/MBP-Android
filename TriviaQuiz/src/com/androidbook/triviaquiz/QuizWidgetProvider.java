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
		onUpdateAction(context);
    }
	
	public void onUpdateAction(Context context) {
		WidgetData widgetData = new WidgetData("Unknown", "NA", "");
		widgetData.nickname = "hello";
		widgetData.score = "666";
		
		String packageName = context.getPackageName();
		
		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteView.setTextViewText(R.id.widget_nickname, widgetData.nickname);
		remoteView.setTextViewText(R.id.widget_score, "Score: "+widgetData.score);
		
		if (widgetData.avatarUrl.length() > 0) {
            // remoteView.setImageViewUri(R.id.widget_image, Uri.parse(avatarUrl));
            URL image;
            try {
                image = new URL(widgetData.avatarUrl);
                Log.d(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG, "avatarUrl: " + widgetData.avatarUrl);

                // See http://bit.ly/bAtW6W and http://bit.ly/a3Qkw4 for the reasons for not using decodeStream directly
                // (in short, it works but not in certain situations)
                // The work around shown below was also used in Android Wireless Application Development.

                Bitmap bitmap = BitmapFactory.decodeStream(image.openStream());
                /*
                 * BufferedInputStream in;
                 * BufferedOutputStream out;
                 * 
                 * in = new BufferedInputStream(image.openStream(), IO_BUFFER_SIZE);
                 * final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                 * out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
                 * copy(in, out);
                 * // implementation provided at the bottom of this file; uncomment to use
                 * out.flush();
                 * 
                 * final byte[] data = dataStream.toByteArray();
                 * Log.d(DEBUG_TAG, "Length: "+ data.length);
                 * Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                 */
                if (bitmap == null) {
                    Log.w(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG, "Failed to decode image");

                    remoteView.setImageViewResource(R.id.widget_image, R.drawable.avatar);
                } else {
                    remoteView.setImageViewBitmap(R.id.widget_image, bitmap);
                }
            } catch (MalformedURLException e) {
                Log.e(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG , "Bad url in image", e);
            } catch (IOException e) {
                Log.e(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG, "IO failure for image", e);
            }

        } else {
            remoteView.setImageViewResource(R.id.widget_image, R.drawable.avatar);
        }


		
		try {
			ComponentName quizWidget = new ComponentName(context, QuizWidgetProvider.class);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			appWidgetManager.updateAppWidget(quizWidget, remoteView);
			appWidgetManager.updateAppWidget(quizWidget, remoteView);
		} catch (Exception e) {
			Log.e(com.androidbook.triviaquiz.QuizActivity.DEBUG_TAG, "Failed to update Widget");
			
		}
		//private void getWidgetData(WidgetData widgetData) {
		

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
