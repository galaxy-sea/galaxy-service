package kim.galaxy.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 *
 * @author adminitartor
 */
public class WebServer {
    private ServerSocket server;
    private ExecutorService threadPool;

    /**
     * 构造方法,用来初始化服务端应用程序
     */
    public WebServer() {
        try {
            server = new ServerSocket(8080);
            threadPool = Executors.newFixedThreadPool(40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端开始工作的方法
     */
    public void start() {
        try {
            /*
             * 先将while循环注释掉,客户端在收不到响应时 可能会试图尝试多次连接服务端,这会导致服务端 重复打桩,不利于开发查看程序执行流程.所以
             * 暂时只接受一次客户端连接.
             */
            while (true) {
                // System.out.println("等待客户端连接...");
                Socket socket = server.accept();
                // 启动一个线程来处理该客户端
                ClientHandler handler = new ClientHandler(socket);
                // Thread t = new Thread(handler);
                // t.start();

                threadPool.execute(handler);
                // System.out.println("一个客户端连接了!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }

}
