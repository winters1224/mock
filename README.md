# MOCK项目
尝试mock一个系统的所有对外请求。使得一个系统（服务）可以独立运行、自测/测试。
- http
- dubbo/dubbox
- 数据库？
- redis？
- 其它？

## 基本思路
总的来说，一个mock请求应当有三个处理步骤。这三个处理步骤可以整合成一个模板服务。
- mock

    当请求发送到mock服务上时，检查是否有对应的mock配置。如果有，则根据mock配置来返回响应结果。

- 转发

    如果没有mock配置，那么检查是否有转发配置。mock服务可以把请求转发给实际处理这次请求的服务、并按照真实的响应来返回结果。

- 记录

    如果有转发配置，那么检查是否有记录的需要。如果需要记录，那么把本次请求和响应的信息记录下来，以便于后续创建和修改mock配置使用。

## http 

### mock
从http请求中解析出必要的入参（uri、params、body、header等），根据这些条件从配置文件（数据库）中匹配出对应的返回结果，并发送回去。

- 入参

    目前可以确定的入参包括：
   
    - remote ip
    - url
    - method
    - query string
    - body
    - header
    - cookie
    - 还要考虑文件上传的情况

- 条件

    条件检查计划用JEXL表达式来处理，这样可以有较大的灵活性，便于扩展各种各样的mock条件。
    
    出现多个mock配置的情况下，按顺序从第一个配置开始匹配，而不考虑“最佳”匹配。因为按顺序配置比较简单，“最佳”匹配不仅实现起来麻烦，而且配置起来也麻烦。

- 返回结果

    返回结果跟入参类似，也需要考虑：
    - header
    - body
    - cookie
    - 文件下载的情况
