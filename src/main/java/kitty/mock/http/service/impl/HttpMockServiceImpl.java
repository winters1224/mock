package kitty.mock.http.service.impl;

import com.google.gson.reflect.TypeToken;
import kitty.mock.common.util.JexlUtils;
import kitty.mock.common.util.JsonUtils;
import kitty.mock.http.bean.HttpMockConfig;
import kitty.mock.http.bean.MockInput4Http;
import kitty.mock.http.service.HttpMockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Http Mock的配置服务
 */
@Service("httpMockService")
@Slf4j
class HttpMockServiceImpl implements HttpMockService {

    /**
     * 参数配置列表
     * <p>
     * 暂存在jvm内存中
     */
    private List<HttpMockConfig> configList;

    /**
     * 配置列表的类型（带泛型）
     */
    private static final Type CONFIG_LIST_TYPE = new TypeToken<List<HttpMockConfig>>() {
    }.getType();

    /**
     * 配置文件的路径
     */
    @Value("${mock.http.config.location}")
    private String location;

    /**
     * 遍历{@link #location}下所有的文件和文件夹，把里面的配置解析出来，放到一个大list里
     * <p>
     * TODO 放到缓存或者内存里。
     *
     * @return 返回配置文件列表
     */
    @PostConstruct
    public void initConfigList() {
        // 注意顺序
        List<HttpMockConfig> configList = new LinkedList<>();

        // TODO 考虑classpath的问题
        File start = new File(location);

        parseConfig(start, configList);


        if (CollectionUtils.isEmpty(configList)) {
            throw new RuntimeException(location + " 路径下没有HttpMock的¬配置！");
        }
        this.configList = configList;
    }

    /**
     * 查找配置列表中是否有与入参匹配的配置项
     *
     * @param param the param
     * @return the http mock config
     */
    @Override
    public Optional<HttpMockConfig> queryConfig(MockInput4Http param) {
        return configList.stream().filter(config -> isConfigMatched(config, param)).findFirst();
    }


    /**
     * 判断当前请求是否能匹配对应的配置项
     *
     * @param config http请求/响应的配置
     * @param param  http请求
     * @return 本次http请求是否符合指定config的配置。符合，则返回true；否则返回false
     */
    private boolean isConfigMatched(HttpMockConfig config, MockInput4Http param) {
        // 首先，uri和method要相同
        // 其次，检查表达式是否匹配

        boolean isMatched = StringUtils.equals(config.getUri(), param.getUri()) && config.getMethod() == param
                .getMethod() && JexlUtils.isMatched(config.getExpression(), param);

        log.info("config:{}, param:{}, isMatched:{}", config, param, isMatched);

        return isMatched;
    }

    /**
     * Parse config.
     *
     * @param file       the file
     * @param configList the config list
     */
    private void parseConfig(File file, List<HttpMockConfig> configList) {

        if (file.isDirectory() && file.listFiles() != null) {
            // 如果是文件夹，且文件夹非空，则迭代文件夹内容
            Stream.of(file.listFiles()).filter(Objects::nonNull).forEach(f -> parseConfig(f, configList));
        } else if (file.isFile()) {
            // 如果是文件，则解析文件内容
            configList.addAll(JsonUtils.fromFile(file, CONFIG_LIST_TYPE));
        }
        // 如果不满足以上条件（如文件不存在、空文件夹），则之额吉返回不做处理
    }
}
