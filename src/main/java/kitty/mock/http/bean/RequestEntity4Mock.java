package kitty.mock.http.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

/**
 * 为Mock定制的一个RequestEntity
 * <p>
 * DONE toString()方法可以手动定制一个
 *
 * DONE 没有必要在这里保存header和param。这两个数据只在HttpMockConfig相关的地方使用，在必要时解析出来即可，没有必要一直占用着jvm内存
 * 想了想，还是封装到这里
 *
 * TODO 有可能还是要放到这个里面来处理。mockConfig 里面有ClientIp的配置，父类拿不到这个东西
 *
 * @param <T> the type parameter
 * @author Pluto
 * @date 2019 -12-10
 */
@Getter
@Setter
public class RequestEntity4Mock<T> extends RequestEntity<T> {

    /** 调用方Ip地址 */
    private String clientIp;

    /** 把父类的headers解析出来 */
    private Map<String, String> header;
    /** 把query中的参数解析出来 */
    private Map<String, String> param;

    /**
     * 匹配父类的构造方法，设定一些初始值
     *
     * @param body      the body
     * @param headers   the headers
     * @param method    the method
     * @param url       the url
     * @param paramType the param type
     */
    public RequestEntity4Mock(T body, MultiValueMap<String, String> headers, HttpMethod method, URI url,
                              Type paramType) {
        super(body, headers, method, url, paramType);
    }

    /**RequestEntity4Mock
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "RequestEntity4Mock = {URI = [" + getUrl()
                .getPath() + "]; " + "METHOD = [" + getMethod() + "]; PARAM = [" + getParam() + "]; HEADER = [" + getHeader() + "]}";
    }
}
