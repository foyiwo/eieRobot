package eie.robot.com.common;

import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.ResponseCallback;

import org.json.JSONObject;

import java.lang.reflect.Parameter;
import java.util.Map;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class mHttpUtil {

    public static void postJson(String baseUrl, String url, String jsonStr,Subscriber<ResponseBody> callbak ){
        new Novate.Builder(mGlobal.mApplication)
                .baseUrl(baseUrl)
                .build().json(url,jsonStr,callbak);
    }
}
