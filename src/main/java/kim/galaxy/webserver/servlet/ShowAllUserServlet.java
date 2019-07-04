package kim.galaxy.webserver.servlet;

import kim.galaxy.webserver.http.HttpRequest;
import kim.galaxy.webserver.http.HttpResponse;

import java.io.RandomAccessFile;

/**
 * 显示所用用户信息
 *
 * @author adminitartor
 */
public class ShowAllUserServlet extends HttpServlet {
    public void service(HttpRequest request, HttpResponse response) {
        try (RandomAccessFile raf = new RandomAccessFile("user.dat", "r");) {
            //拼一个html页面内容
            StringBuilder builder = new StringBuilder();
            /*
             * <html>
             * 	<head>
             * 	  <meta charset="UTF-8">
             *    <title>用户登录</title>
             * 	</head>
             *  <body>
             *  	<center>
             *  		<h1>用户列表</h1><br>
             *  		<table border="1">
             *  			<tr>
             *  				<td>用户名</td>
             *  				<td>密码</td>
             *  				<td>年龄</td>
             *  				<td>电话</td>
             *  			</tr>
             *  		</table>
             *  	</center>
             *  </body>
             * </html>
             */
            builder.append("<html>");
            builder.append("<head>");
            builder.append("<meta charset=\"UTF-8\">");
            builder.append("<title>显示全部用户</title>");
            builder.append("</head>");
            builder.append("<body>");
            builder.append("<center>");
            builder.append("<h1>用户列表</h1><br>");
            builder.append("<table border=\"1\">");
            builder.append("<tr>");
            builder.append("<td>用户名</td>");
            builder.append("<td>密码</td>");
            builder.append("<td>年龄</td>");
            builder.append("<td>电话</td>");
            builder.append("</tr>");

            for (int i = 0; i < raf.length() / 100; i++) {
                byte[] arr = new byte[32];
                raf.read(arr);
                String username = new String(arr, "UTF-8").trim();

                raf.read(arr);
                String password = new String(arr, "UTF-8").trim();

                int age = raf.readInt();

                raf.read(arr);
                String tel = new String(arr, "UTF-8").trim();

                builder.append("<tr>");
                builder.append("<td>" + username + "</td>");
                builder.append("<td>" + password + "</td>");
                builder.append("<td>" + age + "</td>");
                builder.append("<td>" + tel + "</td>");
                builder.append("</tr>");


            }


            builder.append("</table>");
            builder.append("</center>");
            builder.append("</body>");
            builder.append("</html>");

            byte[] data = builder.toString().getBytes("UTF-8");

            response.setStatusCode(200);
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Content-Length", data.length + "");
            response.setData(data);
            response.flush();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}