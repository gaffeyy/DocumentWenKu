# 资料文库

## 需求分析

* 用户部分

1. 注册/登录/注销 ： 使用Redis 利用 `Session` + `Cookie`实现分布式登录
2. 用户管理（管理员/普通用户）
3. 点赞资料、收藏资料/取消点赞、取消收藏
4. 增加、获取浏览记录

* 文件资料部分

1. 文件上传

2. 给文件增加标签

3. 文件删除

4. 文件查询
   1. 通过文件名模糊查询
   2. 通过文件ID查询
   3. 通过文件标签查询
   
   



## 技术栈

1. Java编程语言
2. SpringBoot框架
3. SpringMVC + Mybatis/MybatisPlus
4. Junit单元测试库
5. Mysql数据库、Redis数据库
6. Swagger + Knife4j接口文档



## 数据库建表

1. 用户表user

> ```sql
> create table if not exists wenku_user.user
> (
>     userName     varchar(256)                       null comment '用户名称',
>     id           bigint auto_increment comment 'id(主键)'
>         primary key,
>     userAccount  varchar(256)                       null comment '账号',
>     gender       tinyint                            null comment '性别',
>     userPassword varchar(512)                       not null comment '密码',
>     email        varchar(512)                       null comment '邮箱',
>     createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     userRole     int      default 0                 not null comment '用户身份 0 - 普通用户; 1 - 管理员',
>     userTags     varchar(1024)                      null comment '标签 json 列表'
> )
>     comment '用户';
> ```

2. 资料表document

> ```sql
> create table if not exists wenku_user.document
> (
>     documentName varchar(256)                       not null comment '资料名称',
>     documentId   bigint auto_increment comment '资料id(主键)'
>         primary key,
>     category     varchar(256)                       not null comment '资料类型',
>     uploadUserId bigint                             not null comment '上传用户ID',
>     uploadTime   datetime default CURRENT_TIMESTAMP null comment '上传时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     ducomentUrl  varchar(256)                       not null comment '文档URL',
>     tags         varchar(1024)                      null comment '文档标签（JSON列表）',
>     constraint document_user_id_fk
>         foreign key (uploadUserId) references wenku_user.user (id)
> );
> ```



## 用户基本服务接口开发

1. 注册
2. 登录
3. 注销

### Cookie和Session

> 1. cookie是存储再浏览器端的一小段文本。
> 2. seesion是存储在服务器端的一组数据
>
> - session存储于服务器，可以理解为一个状态列表，拥有一个唯一识别符号sessionId，通常存放于cookie中。服务器收到cookie后解析出sessionId，再去session列表中查找，才能找到相应session。依赖cookie
> - cookie类似一个令牌，装有sessionId，存储在客户端，浏览器通常会自动添加。

> 1. 服务器接收到请求，开启一个Session会话，同时生成一个sessionid，并告诉客户端存储一个Cookie
> 2. 客户端收到响应后，就存储该Cookie信息，在下次访问服务器时在请求头中携带该Cookie。



### 实现单点登录

> Session + Cookie + Redis

> 单点登录是什么？

​	单点登录（Single Sign On，SSO），为一个系统开发登录功能，通常的流程是：

* 登录：登录成功后，在服务器端存储一个SessionId，并通知客户端保存一个Cookie，下次访问时，可以由请求中携带的Cookie和服务器存储的SessionID判断用户身份。
* 注销：将服务器端存储的SessionId删除。

​	这种流程对于只有一个系统是有效的。但是对于多系统就不行了，因为服务器端的SessionID只会存储在本地，而其他服务器没有SessionID信息，这样就会导致用户登录一个系统后，进入另一个关联系统就需要再次登录；

​	对此我们可以用一个Redis数据库来存储用户登录信息，这样每个系统就都可以根据Cookie取Redis中验证用户登录状态了。

### Q&A



## 整合Redis

1. 添加依赖

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
```

2. 配置redis连接信息

```yaml
#  Redis
  redis:
    host: 
    port: 6379
    password: 
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 0
    timeout: 3000
    database: 0
```

3. 配置RedisTemplate，自定义序列化器，否则会存入乱码

```java
/**
 * Redis配置类
 *
 * @author gaffey
 *
 */
@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}
```



## 文档基本服务接口开发

1. 上传文档：Ftp与服务器通信
2. 删除文档
3. 查询文档
   1. 根据名称模糊查询
   2. 根据Tags模糊查询
   3. 根据文档Id（主键）查询



## Linux服务器搭建FTP服务

> 利用`vsftpd`搭建FTP服务















# 导入批量测试数据

> 并发批量地导入数据

```java
//自定义线程池
	private ExecutorService executorService =
			new ThreadPoolExecutor(
        			20,
					100,
					10000,
					TimeUnit.MINUTES,
					new ArrayBlockingQueue<>(1000));
```

```java
		//插入十万条测试数据
		int batchSize = 10000;
		int j = 0;
		List<CompletableFuture<Void>> futureList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			List<Document> documentList = new ArrayList<>();
			while (true) {
				j++;
				Document document = new Document();
				document.setDocumentName("i-批量插入测试文档");
				document.setCategory("图书");
				document.setUploadUserId(1L);
				document.setDucomentUrl("http://document.com");
				document.setTags("[Tags]");
				document.setLikes(10L);
				documentList.add(document);
				if (j % batchSize == 0) {
					break;
				}
			}
			// 异步执行
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				System.out.println("threadName: " + Thread.currentThread().getName());
				documentService.saveBatch(documentList, batchSize);
			}, executorService);
			futureList.add(future);
		}
```











# Plan

1. Redis缓存：缓存最热门的文档（点赞最多或浏览最多）；缓存用户的浏览记录（可以缓存最近的二十条记录，采用LRU策略淘汰）
   1. 使用定时任务进行缓存预热
2. Redis分布式锁：只有一个服务获得锁来进行缓存预热
3. SQL查询效率优化：EXPLAN分析查询计划；采用索引优化