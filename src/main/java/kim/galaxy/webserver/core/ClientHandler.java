package kim.galaxy.webserver.core;

import kim.galaxy.webserver.context.HttpContext;
import kim.galaxy.webserver.context.ServerContext;
import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;
import kim.galaxy.webserver.servlet.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * 线程任务,处理客户端请求并响应客户端
 *
 * @author adminitartor
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("开始处理客户端请求!");
        try {
            /*
             * 处理客户端请求大致流程:
             * 1:解析请求
             *   将Socket传递给HttpRequest,用于
             *   实例化该请求,实例化过程就是解析
             *   请求的过程.内部会通过Socket获取
             *   输入流读取客户端发送过来的HTTP请求
             *   并将内容保存在HttpRequest对象相关
             *   属性上.
             *
             * 2:分析请求
             *   通过HttpRequest对象,获取用户请求
             *   的意图,是请求一个具体资源,还是请求
             *   处理某个业务,如注册,登录等功能(处理
             *   业务后期完善)
             *
             * 3:处理请求
             * 4:响应客户端
             */

            //1
            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(socket);


            /*
             * 2  分析请求
             * 2.1:获取用户请求中的URl地址
             * 2.2:分析请求URL是具体资源还是业务
             *
             */
            //2.1
            String url = request.getRequestURI();
            System.out.println("url:" + url);
            //2.2
            //判断请求的是否为注册业务
            String servletName = ServerContext.getServletNameByUrl(url);
            if (servletName != null) {
                /*
                 * 利用反射实例化Servlet并调用service方法
                 */
                Class cls = Class.forName(servletName);
                //				Object o = cls.newInstance();
                //				Method m = cls.getDeclaredMethod(
                //					"service",new Class[]{
                //						HttpRequest.class,HttpResponse.class}
                //				);
                //				m.invoke(o,new Object[]{request,response});


                HttpServlet servlet = (HttpServlet) cls.newInstance();
                servlet.service(request, response);
            } else {
                File file = new File("webapps" + url);
                if (file.exists()) {
                    System.out.println("文件已找到! " + file.getName());
                    /*
                     * 根据请求的资源的名字获取到该资源文件后缀名
                     * 然后根据后缀名到HttpContext中获取该后缀名对应
                     * 的介质类型值,然后通过下面的response将头信息:
                     * Content-Type设置好.
                     */
                    // fileName:index.html
                    String fileName = file.getName();
                    // 找到"."后面第一个字符的位置
                    int index = fileName.lastIndexOf(".") + 1;
                    // 截取后缀名
                    String ex = fileName.substring(index);
                    // 根据后缀名得到消息头对应的值
                    String contentType = HttpContext.getMimeType(ex);

                    //设置状态代码
                    response.setStatusCode(200);

                    //设置响应头信息
                    response.setHeader("Content-Type", contentType);
                    response.setHeader("Content-Length", file.length() + "");
                    //设置响应实体文件
                    response.setEntity(file);
                    //响应客户端
                    response.flush();
                } else {
                    System.out.println("文件未找到!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}