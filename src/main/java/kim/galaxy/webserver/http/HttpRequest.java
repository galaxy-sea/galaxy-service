package kim.galaxy.webserver.http;

import kim.galaxy.webserver.context.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequest用来表示浏览器发送给服务端的一个
 * 具体的HTTP请求内容.
 * 一个HTTP请求应当包含:请求行,消息头,消息正文.
 *
 * @author adminitartor
 */
public class HttpRequest {
    /*
     * 请求行相关信息定义
     */
    //请求方式
    private String method;

    //请求资源路径
    private String url;

    //请求使用的协议版本
    private String protocol;

    /*
     * 消息头内容
     */
    private Map<String, String> headers = new HashMap<String, String>();


    /*
     * 请求地址,通常与url属性值一样,但是对于
     * 客户端GET形式提交一个form表单数据这样
     * 的会在请求路径中附带数据的情况时,requestURI
     * 则只保存请求路径中"?"左侧的实际请求内容部分
     */
    private String requestURI;
    /*
     * 若请求路径中附带数据,则保存"?"右侧内容
     */
    private String queryString;
    /*
     * 请求附带的所有参数
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    //对应客户端的Socket
    private Socket socket;
    //用于读取客户端发送过来数据的输入流
    private InputStream in;

    /**
     * 根据给定的Socket解析对应客户端发送过来的
     * 请求
     *
     * @param socket
     */
    public HttpRequest(Socket socket) {
        //System.out.println("开始解析请求");
        try {
            this.socket = socket;
            in = socket.getInputStream();
            /*
             * 解析分为三步:
             * 1:解析请求行
             * 2:解析消息头
             * 3:解析消息正文
             */
            //1
            parseRequestLine();

            //2
            parseHeaders();

            //3
            parseContent();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("解析请求完毕");
    }

    /**
     * 解析消息正文
     */
    private void parseContent() {
        /*
         * 查看消息头中是否包含:Content-Length
         * 有则说明这个请求中包含消息正文
         */
        if (headers.containsKey("Content-Length")) {
            //根据消息头Content-Length获取正文长度
            int length = Integer.parseInt(headers.get("Content-Length"));
            /*
             * 从in当中连续读取指定的字节量,将消息正文
             * 数据全部读取出来
             */
            try {
                byte[] data = new byte[length];
                in.read(data);
                /*
                 * 读取完正文数据后,再根据消息头:
                 * Content-Type查看是什么类型的数据
                 */
                String contentType = headers.get("Content-Type");
                /*
                 * 判断是否为form表单提交的信息
                 */
                if ("application/x-www-form-urlencoded".equals(contentType)) {
                    /*
                     * form表单提交的这些字节数据就是原来使用GET请求
                     * 包含在URL地址栏中"?"右侧的内容
                     */
                    String line = new String(data, "ISO8859-1");
                    System.out.println("form表单数据:" + line);
                    this.queryString = line;
                    parseQueryString();

                }


            } catch (Exception e) {
            }

        }
    }

    /**
     * 解析消息头
     */
    private void parseHeaders() {
        //System.out.println("开始解析消息头");
        while (true) {
            String line = readLine();
            if ("".equals(line)) {
                break;
            }
            System.out.println("头:" + line);
            String[] data = line.split(":\\s");
            //			//System.out.println(data[0]+","+data[1]);
            headers.put(data[0], data[1]);
        }
        //System.out.println("headers:"+headers);
        //System.out.println("解析消息头完毕");
    }

    /**
     * 解析请求行
     */
    private void parseRequestLine() {
        //System.out.println("开始解析请求行");
        /*
         * 先通过输入流读取一行字符串(CRLF结尾),
         * 因为HTTP请求第一行是请求行内容.
         * 读取出来的内容如:
         * GET / HTTP/1.1(CRLF)
         * 请求行格式是根据空格将内容分为三部分
         * 分别是:method,url,protocol
         * 所以读取了请求行字符串后,要根据空格将字符串
         * 的三部分分别拆分出来,设置到HttpRequest的三个
         * 对应属性上来完成解析请求行工作.
         */
        //1 读取请求行内容
        String line = readLine();
        //System.out.println("请求行内容:"+line);
        //2
        //2.1 按照空格拆分字符串
        String[] data = line.split("\\s");
        /*
         * 补充:如果循环接收客户端连接,这里可能会出现
         * 数组下标越界的情况.后面再做修改.
         */
        //2.2 将拆分的内容设置到对应属性上
        this.method = data[0];
        this.url = data[1];
        this.protocol = data[2];
        //进一步解析URL部分
        parseURL();
        /*
         * 地址栏输入:localhost:8080/index.html后
         * 测试解析结果,下面输出应当为:
         * method:GET
         * url:/index.html
         * protocol:HTTP/1.1
         */
        //System.out.println("method:"+method);
        //System.out.println("url:"+url);
        //System.out.println("protocol:"+protocol);
        //System.out.println("解析请求行完毕");
    }

    /**
     * 进一步解析请求行中URL部分.
     */
    private void parseURL() {
        /*
         * 1 查看url中是否含有"?"
         * 2 若含有?,则按照?将url拆分为两部分
         *   将?左侧内容设置到requestURI上,将
         *   ?右侧内容设置到queryString上
         *   再对queryString进行拆分,按照"&"
         *   拆分出每一个参数,将"="左侧内容作为
         *   key,将"="右侧内容作为value存入到
         *   parameters这个Map中
         *
         *   若不含有?,则直接将url内容设置到
         *   requestURI上即可.
         *
         *   解析的url通常有两种情况:
         *
         *   /myweb/index.html
         *   /myweb/reg?username=fancq&password=123123...
         */
        //System.out.println("进一步解析URL");
        int index = this.url.indexOf("?");
        //是否含有?
        if (index != -1) {
            this.requestURI = this.url.substring(0, index);
            this.queryString = this.url.substring(index + 1);
            //将queryString按照"&"拆分
            parseQueryString();
        } else {
            this.requestURI = this.url;
        }

        //System.out.println("URL解析完毕");
        //System.out.println("requestURI:"+requestURI);
        //System.out.println("queryString:"+queryString);
        //System.out.println("parameters:"+parameters);
    }

    private void parseQueryString() {
        String[] data = this.queryString.split("&");
        for (String para : data) {
            //按照"="拆分
            String[] arr = para.split("=");
            if (arr.length == 2) {
                this.parameters.put(arr[0], arr[1]);
            } else {
                this.parameters.put(arr[0], "");
            }
        }
    }


    /**
     * 从给定的输入流中读取一行字符串,以(CRLF)结尾
     * 认定为一行结束.返回的字符串中不含有(CRLF)
     *
     * @param in
     * @return
     */
    private String readLine() {
        StringBuilder builder = new StringBuilder();
        try {
            int d = -1;
            char c1 = 'a';//上次读取的字符
            char c2 = 'a';//本次读取的字符
            while ((d = in.read()) != -1) {
                c2 = (char) d;
                if (c1 == HttpContext.CR && c2 == HttpContext.LF) {
                    break;
                }
                builder.append(c2);
                c1 = c2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //trim是因为要把读取到的CRLF去除
        return builder.toString().trim();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    /**
     * 获取给定消息头名字对应的消息头值
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    /**
     * 根据个给定的参数名获取对应的参数值
     *
     * @param name
     * @return
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }
}