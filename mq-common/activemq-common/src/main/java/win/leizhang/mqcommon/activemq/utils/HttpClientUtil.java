package win.leizhang.mqcommon.activemq.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class.getName());

    public static HttpClientUtil getInstance() {
        return new HttpClientUtil();
    }

    public String httpPost(String url, String jsonStr) {
        //post请求返回结果
        @SuppressWarnings("resource")
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String str = "";
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonStr) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonStr, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            //请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == 200) {

                try {
                    //读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity());

                    //把json字符串转换成json对象

                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return str;
    }
}
