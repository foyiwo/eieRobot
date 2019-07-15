package eie.robot.com.common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.ResponseCallback;
import com.vondear.rxtool.RxDeviceTool;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import eie.robot.com.dto.FoLogsDto;
import eie.robot.com.dto.IncomeDto;
import eie.robot.com.model.IncomeRecordModel;
import okhttp3.Call;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class mUploadDataUtil {

    public static void postIncomeRecord(final String appName, final float rmb){
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Subscriber<ResponseBody> callbak = new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                            postLogs(appName+",收益上传成功");
                        }

                        @Override
                        public void onError(java.lang.Throwable e) {
                            postLogs(appName+","+e.getMessage());
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {

                        }
                    };

                    IncomeDto dto = new IncomeDto();
                    dto.setUniqueSerialNumber(RxDeviceTool.getUniqueSerialNumber());
                    dto.setWidth((float) mGlobal.mScreenWidth);
                    dto.setHeight((float) mGlobal.mScreenHeight);
                    dto.setImei(RxDeviceTool.getIMEI(mGlobal.mApplication));
                    dto.setImsi(RxDeviceTool.getIMSI(mGlobal.mApplication));
                    dto.setNetType(RxDeviceTool.getNetworkOperatorName(mGlobal.mApplication));
                    dto.setSimSerial(RxDeviceTool.getSimSerialNumber(mGlobal.mApplication));
                    dto.setDeviceModel(RxDeviceTool.getBuildBrandModel());
                    dto.setDeviceBrand(RxDeviceTool.getBuildBrand());
                    //dto.setDeviceVendor(RxDeviceTool.getDeviceInfo(mGlobal.mApplication));
                    dto.setAndroidId(RxDeviceTool.getAndroidId(mGlobal.mApplication));
                    dto.setTotalGold(rmb);
                    dto.setSoftwareName(appName);
                    Gson gson = new Gson();
                    String jsonDto = gson.toJson(dto);
                    String url = "/income/create";

                    mHttpUtil.postJson(mGlobal.baseUrl,url,jsonDto,callbak);

                }catch (Exception ex){

                }
            }
        });
    }

    public static void postLogs(final String Logs){
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Subscriber<ResponseBody> callbak = new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                            //mToast.success_sleep("数据上传成功");
                            String opMessage = "数据上传成功";
                        }

                        @Override
                        public void onError(java.lang.Throwable e) {
                            String opMessage = e.getMessage();
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {

                        }
                    };

                    FoLogsDto dto = new FoLogsDto();
                    dto.setId(0);
                    dto.setLog(RxDeviceTool.getUniqueSerialNumber()+"："+Logs);
                    Gson gson = new Gson();
                    String jsonDto = gson.toJson(dto);
                    String url = "/logs/new";

                    mHttpUtil.postJson(mGlobal.baseUrl,url,jsonDto,callbak);

                }catch (Exception ex){

                }
            }
        });
    }

    public static void getIsReBoot(){
        mFunction.runInChildThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Subscriber<ResponseBody> callbak = new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                            //mToast.success_sleep("数据上传成功");
                            String opMessage = "数据上传成功";
                        }

                        @Override
                        public void onError(java.lang.Throwable e) {
                            mUploadDataUtil.postLogs(e.getMessage());
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String str = responseBody.string();//获取字符串数据
                                if(str.equals("1")){
                                    mAdbShell.reboot();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    IncomeDto dto = new IncomeDto();
                    dto.setUniqueSerialNumber(RxDeviceTool.getUniqueSerialNumber());

                    Gson gson = new Gson();
                    String jsonDto = gson.toJson(dto);
                    String url = "/device/reboot";

                    mHttpUtil.postJson(mGlobal.baseUrl,url,jsonDto,callbak);

                }catch (Exception ex){

                }
            }
        });
    }


}
