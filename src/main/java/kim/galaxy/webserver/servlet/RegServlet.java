package kim.galaxy.webserver.servlet;

import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;

import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 用于处理用户注册业务
 *
 * @author adminitartor
 */
public class RegServlet extends HttpServlet {
    public void service(HttpRequest request, HttpResponse response) {
        System.out.println("RegServlet:开始处理注册");
        /*
         * 处理注册业务流程
         * 1:从request中获取用户输入的注册信息
         * 2:将用户的注册信息写入到文件中
         * 3:将注册成功的页面发送给客户端
         */
        /*
         * 1
         * 这里request.getParameter()中传递的参数为
         * 注册页面中对应输入框中name属性的值
         * 如:<input typ="text" name="username">
         * 要想获取这个输入框的值,需要写成
         * request.getParameter("username")
         */
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        int age = Integer.parseInt(request.getParameter("age"));
        String tel = request.getParameter("tel");

        /*
         * 2
         * 将用户数据写入到文件user.dat中
         * 格式固定,每条用户记录占用100字节,其中
         * 用户名,字符串类型,编码UTF-8,长度32字节
         * 密码,电话号码与用户名一致.
         * 年龄,int类型,长度4字节
         * 每条记录格式:用户名,密码,年龄,电话号码
         */
        try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw");) {
            //先将指针移动到文件末尾
            raf.seek(raf.length());
            //写用户名
            byte[] data = username.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);
            //写密码
            data = password.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);
            //写年龄
            raf.writeInt(age);
            //写电话号码
            data = tel.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            //3
            //			forward("/myweb/reg_success.html", request, response);

            /*
             * 重定向到注册成功页面
             * 路径应当是相对路径,因为是给客户端看的,
             * 所以对于客户端而言,这里相对的路径应当
             * 是当时客户端访问当前RegServlet时的请求
             * 路径
             * 即:客户端做注册时请求的路径为:
             * http://localhost:8080/myweb/reg
             * 那么相对路径就是相对:
             * http://localhost:8080/myweb/
             * 那么我们希望客户端访问myweb中的reg_success.html
             * 页面,所以我们应当在下面的方法中直接传入页面名即可.
             * 这样客户端就会访问:
             * http://localhost:8080/myweb/reg_success.html
             */
            response.sendRedirect("reg_success.html");

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("RegServlet:处理注册完毕");
    }
}