package com.vice.imtranslate.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vice.imtranslate.utils.MD5Encoder;
import com.vice.imtranslate.utils.ReplaceExpressionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

//import static com.vice.test.MainActivity.handler;

/**
 * Created by Administrator on 2016/5/26 0026.
 */

public class TranslateHelper {
    public interface TranslateCallBack {
        void onSuccess(String result);
        void onFailure(String exception);
    }


    private static final String UTF8 = "utf-8";

    //申请者开发者id，实际使用时请修改成开发者自己的appid
    private static final String appId = "2015063000000001";

    //申请成功后的证书token，实际使用时请修改成开发者自己的token
    private static final String token = "12345678";

    private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    //随机数，用于生成md5值，开发者使用时请激活下边第四行代码
    private static final Random random = new Random();

    private Context mContext;
    private final RequestQueue mQueue;

    public TranslateHelper(Context context) {
        mContext=context;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public RequestQueue getmQueue() {
        return mQueue;
    }

    public  void translate(final String content, final String from, final String to, final TranslateCallBack callBack) throws Exception {


        //先对内容做去表情处理
        List<String> list=ReplaceExpressionUtil.replaceAll(content);
        String q=list.get(0);
        final StringBuilder sb=new StringBuilder();
        for(int i=1;i<list.size();i++){
            sb.append(list.get(i));
        }


        //用于md5加密
        //int salt = random.nextInt(10000);
        //本演示使用指定的随机数为1435660288
        final int salt = 1435660288;

        // 对appId+源文+随机数+token计算md5值
        //应该对 appid+q+salt+密钥 拼接成的字符串做MD5得到32位小写的sign。确保要翻译的文本q为UTF-8编码。
        String md5String=appId+new String(q.getBytes(),"utf-8")+salt+token;
        final String sign = MD5Encoder.encode(md5String.toString());

        //注意在生成签名拼接 appid+q+salt+密钥 字符串时，q不需要做URL encode，在生成签名之后，发送HTTP请求之前才需要对要发送的待翻译文本字段q做URL encode。
        final String url1=url+"?"+"q="+ URLEncoder.encode(q,"utf-8")+"&from="+from+"&to="+to+"&appid="+appId+"&salt="+salt+"&sign="+sign;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        //开发者自行处理错误，本示例失败返回为null
                        try {
                            String error_code = response.getString("error_code");
                            if (error_code != null) {
                                System.out.println("出错代码:" + error_code);
                                System.out.println("出错信息:" + response.getString("error_msg"));
                                callBack.onFailure("出错信息:" + response.getString("error_msg"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            //获取返回翻译结果
                            StringBuilder sbResult=new StringBuilder();
                            JSONArray array = (JSONArray) response.get("trans_result");
                            for (int i=0;i<array.length();i++){
                                JSONObject dst = (JSONObject) array.get(i);
                                String result = dst.getString("dst");
                                result = URLDecoder.decode(result, UTF8);
                                sbResult.append(result);
                            }
                            callBack.onSuccess(sbResult+sb.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                callBack.onFailure(error.toString());
            }
        });

        mQueue.add(jsonObjectRequest);
    }
}
