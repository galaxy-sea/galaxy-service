package kim.galaxy.webserver.context;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务端相关信息定义
 *
 * @author adminitartor
 */
public class ServerContext {
    /**
     * Servlet与请求地址之间的映射关系
     */
    private static Map<String, String> servletMapping = new HashMap<String, String>();

    static {
        initServletMapping();
    }

    /**
     * 初始化Servlet映射
     */
    private static void initServletMapping() {
        /*
         * 加载conf/servlets.xml文件
         * 将每个<servlet>标签中的属性url的值作为
         * key,将class属性的值作为value存入到
         * servletMapping这个Map中
         */
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File("conf/servlets.xml"));
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element servletEle : list) {
                String key = servletEle.attributeValue("url");
                String value = servletEle.attributeValue("class");
                servletMapping.put(key, value);
            }
            //			System.out.println(servletMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据给定的url获取对应的Servlet名字
     *
     * @param url
     * @return
     */
    public static String getServletNameByUrl(String url) {
        return servletMapping.get(url);
    }
}