package eie.robot.com.common;

import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.ResponseCallback;

import java.lang.reflect.Parameter;
import java.util.Map;
import okhttp3.ResponseBody;

public class mHttpUtil {

    public static void post(String baseUrl,String url,Map<String, Object> maps,ResponseCallback<Object, ResponseBody> callbak ){
        new Novate.Builder(mGlobal.mApplication)
                .baseUrl(baseUrl)
                .build()
                .rxPost(url, maps,callbak);
    }

}
