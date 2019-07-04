package kim.galaxy.webserver.http;

import kim.galaxy.webserver.context.HttpContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 响应
 * 该类的每个实例用于表示一个具体回复客户端的响应
 * 响应包含三部分:状态行,响应头,响应正文
 *
 * @author adminitartor
 */
public class HttpResponse {
    private Socket socket;
    /*
     * 该输出流从Socket中获取,通过这个流可以将
     * 响应内容发送给客户端
     */
    private OutputStream out;

    /*
     * 状态代码
     */
    private int statusCode;

    /*
     * 响应头
     */
    private Map<String, String> headers = new HashMap<String, String>();


    /*
     * 响应实体文件
     */
    private byte[] data;
    private File entity;


    public HttpResponse(Socket socket) {
        try {
            this.socket = socket;
            this.out = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将当前响应内容发送给客户端
     */
    public void flush() {
        /*
         * 1:发送状态行
         * 2:发送响应头
         * 3:发送响应正文
         */
        sendStatusLine();
        sendHeaders();
        sendContent();
    }

    /**
     * 发送状态行
     */
    private void sendStatusLine() {
        try {
            String line = "HTTP/1.1" + " " + statusCode + " " + HttpContext.getStatusReasonByCode(statusCode);
            System.out.println("状态行:" + line);
            println(line);
            System.out.println("发送状态行!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送响应头
     */
    private void sendHeaders() {
        try {
            //发送响应头
            //获取当前响应中所有的响应头
            Set<Entry<String, String>> entrySet = headers.entrySet();
            for (Entry<String, String> e : entrySet) {
                String line = e.getKey() + ": " + e.getValue();
                System.out.println("响应头:" + line);
                println(line);
            }

            //单独发送CRLF
            println("");

            System.out.println("发送响应头!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送响应正文
     */
    private void sendContent() {
        if (this.data != null) {
            try {
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (entity != null) {
            try (FileInputStream fis = new FileInputStream(entity);) {
                //发送响应正文
                byte[] data = new byte[1024 * 10];
                int len = -1;
                while ((len = fis.read(data)) != -1) {
                    out.write(data, 0, len);
                }
                System.out.println("发送响应正文!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送一行字符串(CRLF结尾)
     *
     * @param line
     */
    private void println(String line) {
        try {
            out.write(line.getBytes("ISO8859-1"));
            out.write(HttpContext.CR);//written cr
            out.write(HttpContext.LF);//written lf
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;
    }

    /**
     * 设置响应头信息
     *
     * @param name  响应头名字
     * @param value 响应头值
     */
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * 设置状态代码
     *
     * @param code
     */
    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * 使客户端重定向到指定资源
     *
     * @param url
     */
    public void sendRedirect(String url) {
        //设置状态代码
        this.setStatusCode(302);
        //设置响应头
        this.setHeader("Location", url);
        //响应客户端
        this.flush();
    }

}