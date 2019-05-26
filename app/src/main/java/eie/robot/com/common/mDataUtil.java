package eie.robot.com.common;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.ResponseCallback;
import com.vondear.rxtool.RxDeviceTool;

import java.util.HashMap;
import java.util.Map;
import eie.robot.com.model.IncomeRecordModel;
import okhttp3.Call;
import okhttp3.ResponseBody;

public class mDataUtil {

    public static void postIncomeRecord(String appName,double rmb,double virtualCurrency){

        IncomeRecordModel input = new IncomeRecordModel();
        input.setRmb(rmb);
        input.setVirtualCurrency(virtualCurrency);
        input.setAppName(appName);

        ResponseCallback<Object, ResponseBody> callbak = new ResponseCallback<Object, ResponseBody>() {
            @Override
            public Object onHandleResponse(ResponseBody response) throws Exception {
                return null;
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, Call call, Object response) {

            }
        };

        postIncomeRecord(input,callbak);

    }

    public static void postIncomeRecord(IncomeRecordModel input, ResponseCallback<Object, ResponseBody> callbak){
        Map<String, Object> maps = new HashMap<>();
        maps.put("appName",input.getAppName());
        maps.put("phoneIMEA",RxDeviceTool.getIMEI(mGlobal.mApplication));
        maps.put("phoneName", RxDeviceTool.getAppVersionName(mGlobal.mApplication));
        maps.put("rmb",input.getRmb());
        maps.put("virtualCurrency",input.getVirtualCurrency());

        String url = "/App/UploadIncomeRecord";
        mHttpUtil.post(mGlobal.baseUrl,url,maps,callbak);
    }
}
