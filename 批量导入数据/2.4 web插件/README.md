## web插件说明

json文件使用 ```// ``` 开头注释，行尾也可以用 ```// ``` 注释

模板可以参考demo.json或已有插件

ElasticsearchUnauth插件为get方法

Confluence RCE(CVE-2021-26084)插件为post方法

```
// 这里是注释，// 开头的那行会被忽略
{
    //插件名称，不能为空
    "name": null,
    //风险级别：信息, 低危, 中危, 高危, 严重, 致命，不能为空
    "risk": null,
    //请求方法：不能为空，可以是get、post、put、delete、patch、head、options、trace
    "method": null,
    //请求url，不能为空，以/开头，不需要完整的url，完整的url由系统根据端口、服务、版本获取ip、域名端口后，与协议、方法及当前填写的url拼接而成，get带参数写在url里
    "url": null,
    //请求头，json格式，如果不指定User-Agent头的值，则会从 [扫描配置-爬虫ua] 中随机一个头
    "header": null,
    //请求体，可以是json格式，或者常规参数格式
    // 1.没有参数
    "body": null,
    // 2.json格式参数
    // "body": {
    //     "username": "test",
    //     "password": "passwd"
    // },
    // 3.常规参数
    // "body": "id=1&user=test"
    //插件对应的端口，可以多个value，英文,逗号分割，资产/目标端口与此相同，才会调用该插件
    "port": null,
    //插件对应端口的服务，可以多个value，英文,逗号分割，资产/目标端口的服务包含此服务，则调用该插件
    "service": null,
    //插件对应端口服务的版本，可以多个value，英文,逗号分割，资产/目标端口的版本包含此版本，则调用该插件
    "version": null,
    //结果中包含该关键词，则存在漏洞；结果包含header和response，可以多个value，英文,逗号分割
    "keyword": null,
    //插件对应漏洞wiki中的漏洞，没有则填默认，单个漏洞
    "vulname": "默认"
}
```

### 其他

* 如何判断（http or https）协议

    根据ip端口的服务进行协议判断
    ```
    if (service.contains("https") || service.contains("ssl/http")) {
        pluginconfig.setProtocol("https")
    } else {
        pluginconfig.setProtocol("http");
    }
    ```

* 如何确定url拼接的ip或域名

    ip没有域名，插件仅执行一次，仅ip，即 协议://ip
    
    ip有域名，插件会执行两次，一次是 协议://ip，一次是 协议://域名
