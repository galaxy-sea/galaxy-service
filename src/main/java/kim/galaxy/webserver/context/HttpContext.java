package kim.galaxy.webserver.context;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpContext用来定义HTTP协议中相关定义内容
 *
 * @author adminitartor
 */
public class HttpContext {
    /**
     * 回车符
     */
    public static final int CR = 13;
    /**
     * 换行符
     */
    public static final int LF = 10;

    /**
     * 介质类型映射
     * key:资源类型名
     * value:Context-Type对应值
     * 例如:
     * key:html
     * value:text/html
     */
    private static final Map<String, String> MIME_TYPE_MAPPING = new HashMap<String, String>();

    /**
     * 状态代码与描述映射
     * key:状态代码
     * value:状态描述
     */
    private static final Map<Integer, String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>();


    static {
        initMimeTypeMapping();
        initStatusMapping();
    }

    /**
     * 初始化状态代码与描述映射信息
     */
    private static void initStatusMapping() {
        STATUS_CODE_REASON_MAPPING.put(200, "OK");
        STATUS_CODE_REASON_MAPPING.put(302, "Move Temporarily");
        STATUS_CODE_REASON_MAPPING.put(404, "Not Found");
        STATUS_CODE_REASON_MAPPING.put(500, "Internal Server Error");
    }

    /**
     * 初始化介质类型映射
     * <p>
     * 读取conf/web.xml文件,将该文件中根标签
     * <web-app>中所有的子标签<mime-mapping>
     * 解析出来,然后将其中的子标签<extension>
     * 中间的文本做为key,将子标签<mime-type>
     * 中间的文本作为value存入到MIME_TYPE_MAPPING
     * 这个Map中完成初始化
     */
    private static void initMimeTypeMapping() {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File("conf/web.xml"));
            Element root = doc.getRootElement();
            List<Element> mimeList = root.elements("mime-mapping");
            for (Element mime : mimeList) {
                String key = mime.elementText("extension");
                String value = mime.elementText("mime-type");
                MIME_TYPE_MAPPING.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        String str = getMimeType("js");
        System.out.println(str);
    }

    /**
     * 根据给定的资源后缀名获取对应的介质类型
     *
     * @param ex
     * @return
     */
    public static String getMimeType(String ex) {
        return MIME_TYPE_MAPPING.get(ex);
    }

    /**
     * 根据给定的状态代码获取对应的状态描述
     *
     * @param code
     * @return
     */
    public static String getStatusReasonByCode(int code) {
        return STATUS_CODE_REASON_MAPPING.get(code);
    }
}