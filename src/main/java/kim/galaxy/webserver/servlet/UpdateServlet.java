package kim.galaxy.webserver.servlet;

import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;

public class UpdateServlet extends HttpServlet {

    public void service(HttpRequest request, HttpResponse response) {
        System.out.println("开始修改用户信息");

        System.out.println("用户信息修改完毕!");
    }

}
