package kim.galaxy.webserver.servlet;

import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;

import java.io.File;

/**
 * 用于处理业务的类
 * 这是一个超类,所有的Servlet都需要继承它.
 *
 * @author adminitartor
 */
public abstract class HttpServlet {
    public abstract void service(HttpRequest request, HttpResponse response);

    /**
     * 跳转到指定地址
     *
     * @param url
     * @param request
     * @param response
     */
    public void forward(String url, HttpRequest request, HttpResponse response) {
        File file = new File("webapps" + url);
        response.setStatusCode(200);
        response.setHeader("Content-Type", "text/html");
        response.setHeader("Content-Length", file.length() + "");
        response.setEntity(file);
        response.flush();
    }
}
