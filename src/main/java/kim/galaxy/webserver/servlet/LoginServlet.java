package kim.galaxy.webserver.servlet;

import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;

import java.io.RandomAccessFile;

/**
 * 处理登录业务
 *
 * @author adminitartor
 */
public class LoginServlet extends HttpServlet {
    public void service(HttpRequest request, HttpResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (RandomAccessFile raf = new RandomAccessFile("user.dat", "r");) {
            //设置登录成功状态(开关)
            boolean loginSuccess = false;
            for (int i = 0; i < raf.length() / 100; i++) {
                //移动指针到当前记录的开始位置
                raf.seek(i * 100);
                //读取用户名
                byte[] data = new byte[32];
                raf.read(data);
                String uname = new String(data, "UTF-8").trim();
                if (uname.equals(username)) {
                    //读取密码
                    data = new byte[32];
                    raf.read(data);
                    String upwd = new String(data, "UTF-8").trim();
                    if (upwd.equals(password)) {
                        //登录成功
                        loginSuccess = true;
                    }
                    break;
                }
            }// loop end

            if (loginSuccess) {
                forward("/myweb/login_success.html", request, response);
            } else {
                //登录失败
                forward("/myweb/login_fail.html", request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}