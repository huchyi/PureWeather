package hanjie.app.pureweather.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUtils {

    public static String requestData(String address) throws Exception {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        String data = null;
        InputStream is = null;
        if (connection.getResponseCode() == 200) {
            is = connection.getInputStream();
            data = readFromStream(is);
        }
        if (is != null) {
            is.close();
        }
        return data;
    }

    /**
     * @param is 输入流
     * @return 返回流中获取的字符串
     * @throws IOException
     */
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            bos.write(buff, 0, len);
        }
        is.close();
        String result = bos.toString();
        bos.close();
        return result;
    }

}
