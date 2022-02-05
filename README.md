# “伊卡洛斯”论坛系统

+ 涉及到的主要技术
  + Java
  + Vue
  + Linux
  + Mysql
  + Redis集群
  + ElasticSearch集群
  + RabbitMQ
  + Nginx
  + Shiro
  + Jwt
  + Docker



+ 主要实现功能
  + 登录/注册
  + 文章发表
  + 评论
  + 点赞/踩
  + 关注某人/文章
  + 站内私信
  + 动态内容获取





+ 项目效果展示

  ![登录页面](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.png)

  ![注册页面1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%B3%A8%E5%86%8C%E9%A1%B5%E9%9D%A2.png)

  ![注册页面2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%B3%A8%E5%86%8C%E9%A1%B5%E9%9D%A22.png)

  ![主站](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E4%B8%BB%E7%AB%99.png)

  ![个人主页](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E4%B8%AA%E4%BA%BA%E4%B8%BB%E9%A1%B5.png)

  ![动态](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E5%8A%A8%E6%80%811.png)

  ![发帖](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E5%8F%91%E5%B8%96.png)

  ![发送私信](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E5%8F%91%E9%80%81%E7%A7%81%E4%BF%A1.png)

  ![帖子详情](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E5%B8%96%E5%AD%90%E8%AF%A6%E6%83%85.png)

  ![用户搜索](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%90%9C%E7%B4%A22.png)

  ![内容搜索](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%90%9C%E7%B4%A2%E5%8A%9F%E8%83%BD.png)

  ![私信列表](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E7%A7%81%E4%BF%A1%E5%88%97%E8%A1%A8.png)

  ![评论与点赞点踩](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E8%AF%84%E8%AE%BA%E4%B8%8E%E7%82%B9%E8%B5%9E%E7%82%B9%E8%B8%A9.png)

  

#### 项目搭建时的一些随手笔记

+ Lombok

  + 编译的时候就生成了代码，所以打包的时候不再需要该jar包。故pom的scope属性使用provide即（https://www.bilibili.com/video/BV1gg4y1v7Mi?p=2&spm_id_from=pageDriver 24分）
  + mvn clean package打包后，查看target文件即可看到.class字节码文件的结果。Idea中需安装lombok插件且引入依赖后才可使用，缺一不可。（https://www.bilibili.com/video/BV1gg4y1v7Mi?p=3&spm_id_from=pageDriver 12分）

  ![lombok1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/typoraImg/lombok1.png)

  　　　或者看structure也可

  ![lombok2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/typoraImg/lombok2.png)

  + 一般使用@Data注解足够。但@Data只包含了无参构造方法，想要全参构造方法，需要额外注解@AllArgsConstructor，但是这个注解会覆盖掉原无参构造方法，因此需配合@NoArgsConstructor共同使用。

  + @Accessors(chain = true)  开启set方法的链式调用

    user.setId(“21”).setName(“zk”).setAge(20).setBir(new Date());

  + @EqualsAndHashCode

    1. 此注解会生成equals(Object other) 和 hashCode()方法；
    2. 它默认使用非静态，非瞬态的属性；
    3. 可通过参数exclude排除一些属性；
    4. 可通过参数of指定仅使用哪些属性；
    5. 它默认仅使用该类中定义的属性且不调用父类的方法；
    6. 可通过callSuper = true解决上一点问题。让其生成的方法中调用父类的方法。

  + 当使用lombok时，部分get、set方法需要自定义，lombok不会再生成对应的方法。所以自己写get、set方法和lombok生成的方法不冲突。（https://blog.csdn.net/qq_35794202/article/details/81166469）





+ JDBC

  + SUN为了简化开发人员对各种数据库的统一操作，提供了一个Java操作数据库的规范（JDBC），这些规范的实现由各个数据库的厂商去实现。

  + Pom中mysql-connector-java为mysql的驱动。

  + 连接成功，数据库对象 Connection 代表数据库

    + 设置数据库自动提交  connection.setAutoCommit();
    + 事务提交    connection.commit();
    + 事务回滚    connection.rollback();

  + 执行SQL的对象Statement(PrepareStatement 防sql注入)

    + statement.executeQuery();  //查询操作，返回ResultSet

    + statement.execute();   //执行任何SQL   效率低

    + statement.executeUpdate(); //更新、插入、删除，都用这个，返回受影响的行数

    + int num = statement.executeUpdate(sql);

      if(num > 0) {插入成功，删除成功，更新成功}

    Statement statement = connection.createStatement();

  + 执行SQL，查看返回结果集

    String sql = “SELECT * FROM USERS;”

    ResultSet resultSet = statement.executeQuery(sql);

    While (resultSet.next()) {

    　System.out.println(resultSet.getObject(“id”));

    }

  + 释放连接资源

    resultSet.close();

    statement.close();

    connection.close();

  + PrepareStatement

    String sql = 带有?占位符的sql

    st = conn.prepareStatement(sql);    //预编译sql，先写sql，不执行

    int i = 0;

    st.setInt(++i, 4);

    st.setString(++i, “abc”);

    Int i = st.executeUpdate();  //执行   亦返回受影响的行数

  + PreparedStatement防止sql注入的本质，把传递进来的参数当做字符

    假设其中存在转义字符，就直接忽略，例如引号 ‘ 就会被忽略

  + Sql中关闭事务需要先setAutoCommit(false)，再start transaction, end transaction, commit;  Jdbc中直接conn.setAutoCommit(false)即为开启事务，无需start transaction.

  ![JDBC1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/JDBC1.png)

  ![JDBC2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/JDBC2.png)

  



+ mysql数据库连接池
  + 数据库连接创建与释放过于消耗资源，因此使用池化技术（准备一些预先的资源，代码业务层面，我们不再需要手动创建数据库连接。但是释放连接正常保留。池子是持久的）
  + 开源数据源：DBCP   C3P0   Druid





+ Mybatis

  + 文档说明    https://mybatis.org/mybatis-3/zh/getting-started.html

  + namespace中包名要和Dao/mapper接口的包名一致

    + select标签          你想返回什么，就返回什么

      id             就是对应的namespace中的方法名

      resultType       sql语句执行的返回值

      parameterType   输入参数类型

    + insert标签          插入n条记录，返回影响行数n。（n>=1,n为0表插入失败）

      parameterType

    + update标签         更新n条记录，返回影响行数n。（n>=0）

    + delete标签         删除n条记录，返回影响行数n。（n>=0）

  + 对于insert，若你想利用返回值获取主键，可使用useGeneratedKeys = "true" keyProperty = "id"。

  + DAO接口传参使用：

    + 多个参数 @Param(int userId)      对应xml文档中的 #{userId}
    + 多个参数也可使用Map做参数 parameterType=map     P5 05:00
    + 如果只有一个参数，可以直接使用int usrId，但不推荐
    + 参数也可以直接传一个User user类对象，在xml文档中，直接使用#{id}, #{name}等等即可，即对象中的属性可以直接取出来。

  + 若数据库字段与java的model类字段不一致，可以使用resultMap进行字段间的映射。

  + Mybatis记录日志：

    + Mybatis-config.xml新增   \<setting name="logImpl" value="LOG4J"/>

    + pom导包   log4j

    + 新增log4j.properties配置

    Log4j可控制日志信息输送的目的地是控制台、文件、GUI组件。

    可控制每一条日志的输出格式。

    可定义每一条日志信息的级别。

    通过配置文件灵活配置，无需修改代码。

    除了自动打印sql日志外，若想手动新增各种日志内容，新增如下代码即可

    private static Logger logger = Logger.getLogger(MybatisTest.class);

    与之对应的是lombok的@Slf4j注解日志，和log4j是两种不同的日志。可在不良人lombok看相关内容。这里我们选用log4j进行日志管理。

  + 多对一，一对多，有需要时自取。

  + 动态SQL    if、choose、foreach

    + If   \<if test>\</if>    如果不使用动态SQL，也可考虑重载
    + choose  when otherwise   用于实现SQL中switch case的逻辑
    + foreach  index的那个下标，可用不写，不用

    动态SQL很容易出现类似第一个条件不匹配，直接匹配第二个条件的情况。可能出现… where and …，有两种方法，要么where后写死跟一个1=1，即… where 1=1 and …，要么使用Mybatis的标签，可用的标签工具有\<trim>、\<where>、\<set>。具体使用参考官方文档。

  + \<sql id="">与\<include>   可提取公共内容

  + **缓存（一级、二级缓存、缓存内容存入redis中）**

    清除策略：LRU（最近最少使用原则，默认）、FIFO（先进先出）、SOFT（软引用）、WEAK（弱引用）

    + 一级缓存：

      又称本地缓存：SqlSession。

      与数据库同一次会话期间查询到的数据会放在本地缓存中。

      以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库。

      缓存失效的情况：

      1. 查询不同的内容。
      2. 任意增删改操作，可能会改变到原来的数据，所以必定会刷新缓存。
      3. 查询不同的Mapper.xml
      4. 手动清理缓存（sqlSession.clearCache();）

      小节：一级缓存默认是开启的，只在一次SqlSession中有效。也就是拿到连接到关闭连接这个时间段。

    + 二级缓存：

      + 默认mybatis仅开启了本地会话缓存，仅仅对一个会话中的数据进行缓存。要启用全局的二级缓存，需在SQL映射文件中添加一行\<cache/>，且config.xml文件中需cacheEnabled设为true（默认也是true，但一般都显式的写出来）。

      + 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存。

      + 基于namespace级别的缓存，一个名称空间，对应一个二级缓存。

      + 工作机制：
        1. 一个会话查询一条数据，这个会话数据就会被放在当前会话的一级缓存中。
        2. 如果当前会话关闭了，这个会话对应的一级缓存就没了。但是我们想要的是，会话关闭，数据被保存到二级缓存中。（关闭了才会保存到二级！）
        3. 新的会话查询信息，就可以从二级缓存中获取内容。

      + 不同的mapper查出的数据会放在自己对应的缓存（map）中。

      + **注意：\<cache type="com.kai.webby.cache.RedisCache">，我们选择使用redis做缓存！**

      + flushInterval（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。

      + size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。

      + readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。

      + 提示 二级缓存是事务性的。这意味着，当 SqlSession 完成并提交时，或是完成并回滚，但没有执行 flushCache=true 的 insert/delete/update 语句时，缓存会获得更新。

      ![Mybatis1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis1.png)

      + readOnly默认是false，pojo类需要序列化。为true只读时，不需要序列化。

      + 小结：

        1. 只要开启了二级缓存，在同一个Mapper下就有效；
        2. 所有的数据都会先放在一级缓存中；
        3. 只有当会话提交，或者关闭的时候，才会提交到二级缓存中。

        select才会使用或不使用缓存；update等只会刷新或不刷新缓存。

  + Mybatis的spring-boot-starter版本，其源码已实现了对配置文件的读取（数据源等），以及如何获取数据库连接等等。

    https://www.cnblogs.com/nullifier/p/11967659.html

    https://www.cnblogs.com/leondryu/p/13345898.html

  + Mybatis整合druid：

    https://www.cnblogs.com/jhxxb/p/11284031.html

    https://www.cnblogs.com/jtlgb/p/11459379.html

    https://www.cnblogs.com/chy18883701161/p/12594889.html

  + **关于Mybatis的.xml文件配置问题，这里集中说明一下**

    1. 网上，或者视频中，各种配置方法的都有，比如写在src的dao文件下，然后配置mybatis-config.xml文件加\<mappers>配置。然后在pom文件修改过滤内容。例如狂神的mybatis教程，可能是对的，但我们配了不少次，基本都用不了，也发现不了问题。
    2. 我们目前保证好用的方法是：首先在application.yml文件做配置

    ![Mybatis2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis2.png)

    　　然后在resource下新建文件路径，不用管层结构，就是单层！mapper.xml统一放这里。

    ![Mybatis3](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis3.png)

    　　　Mybatis-config.xml无需\<mappers>的内容！

    ![Mybatis4](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis4.png)

    　　　pom文件也不需要对资源过滤做什么操作！

    <img src="https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis5.png" alt="Mybatis5" style="zoom:80%;" />

    　　　可能需要注意的是，配置完毕之后，执行maven clean，然后编译，生成新的target文件！

    ![Mybatis6](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis6.png)

    　　　保证最新编译生成的target文件下，包含.xml文件，才是成功的！这时候就可以正常使用了。

    ![Mybatis7](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/Mybatis7.png)

  　　　　

  

+ redis

  + 缓存：使用缓存一定是数据库中数据极少发生修改，更多用于查询这种情况。我们结合mybatis有了实现。

    使用分布式缓存，不使用Mybatis的本地缓存。

    对象放入缓存，对象需实现序列化。

    通过Mybatis默认cache源码可知，可以使用自定义Cache类implements Cache接口，并对里面的方法进行实现。

    **放入Redis中存储，和Redis缓存，是两码事。**

    **存入redis的数据比如点赞点踩，redis挂了确实可能丢失1s内的数据，我们需保障redis集群不挂。**

    **如果只做缓存，数据仅在服务器运行的时候存在，也可暂不使用任何持久化。**

  + **哨兵机制**（可解决故障转移的问题），使用一组哨兵投票，防止脑裂出现。代码连的是哨兵，哨兵会自动告诉你master的ip和端口。

    但无论**主从架构**（仅解决主从备份问题，不可故障转移）还是哨兵架构，都存在主服务器物理上限问题和并发压力问题。

  + **redis集群**支持节点的自动发现（新加入一个节点可自动融入该集群），支持slave-master选举和容错（master挂掉后有slave顶上来），支持在线分片（重新分配hash槽）等特性。redis集群可直接连接；Tomcat集群的连接，中间需通过nginx做中间件；mysql集群中间需要mycat。
  
  + Springoot2.0以后，更推荐使用lettuce，而不是jedis
  
    + jedis：采用直连，多个线程操作的话是不安全的，如果想要避免不安全，使用jedis pool连接池，更像BIO
    + lettuce：采用netty，实例可以再多个线程中进行共享，不存在线程不安全的情况。可以减少线程数据，更像NIO模式
  
    ![redis1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/typoraImg/redis1.png)
  
  + 与redis有关的内容：like，follow
  
  + jedis是纯java操作redis，引入的依赖是
  
    ```xml
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.8.0</version>
    </dependency>
    ```
  
    而现在使用的redisTemplate和stringRedisTemplate是springBoot整合redis，引入的依赖是spring-boot-starter-data-redis
  
    redisTemplate（object，object）会自动执行序列化和反序列化
  
    redis应用场景：p16
  
  + **远程连接需要修改redis的配置文件，bind 0.0.0.0 参考编程不良人p12  20.30**
  
  + 缓存穿透、缓存击穿、缓存雪崩：
  
    + 缓存穿透：
  
      缓存穿透是指缓存和数据库中都没有的数据，而用户不断发起请求，如发起id为-1的数据或id为特别大的不存在的数据。这时用户很可能是攻击者，攻击导致数据库压力过大。
  
      解决方案：
  
      1. 接口层增加校验，如用户鉴权校验，id做基础校验，id <= 0的直接拦截
      2. 从缓存取不到，且在数据库也取不到的数据，可将key-value对写成key-null，缓存有效时间可以设置短一点如30s。这样可防止攻击用户反复用同一个id暴力攻击。（mybatis中cache已经做了这点）
      3. 布隆过滤器
  
    + 缓存击穿：
  
      缓存击穿是指缓存中没有数据但数据库中有数据（一般指缓存时间到期），这时由于并发用户特别多，同时读缓存没读到数据，又同时去数据库去取数据，引起数据库压力瞬间增大，造成过大压力。
  
      解决方案：
  
      1. 设置热点数据永不过期
      2. 加互斥锁
  
      ![redis2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/redis2.png)
  
      说明：
  
      1. 缓存中有数据，直接走上述代码13行后就返回结果了
      2. 缓存中没有数据，第1个进入的线程，获取锁并从数据库去取数据，没释放锁之前，其他并行进入的线程会等待100ms，再重新去缓存取数据。这样就防止都去数据库重复取数据，重复往缓存中更新数据情况出现。
      3. 当然这是简化处理，理论上如果能根据key值加锁就更好了，就是线程A从数据库取key1的数据并不妨碍线程B取key2的数据，上面代码明显做不到这点。
  
    + 缓存雪崩：
  
      缓存雪崩是指缓存中数据大批量到过期时间，而查询数据量巨大，引起数据库压力过大甚至宕机。与缓存击穿不同，缓存击穿是指并发查同一条数据，缓存雪崩是不同数据都过期了，很多数据都查不到从而查询数据库。
  
      解决方案：
  
      1. 缓存数据的过期时间设置随机，防止同一时间大量数据过期现象发生。
      2. 设置热点数据永不过期。
  
  + **docker上部署redis集群**
  
    1. 如果光是用作缓存，暂无需持久化，也无需挂载数据卷。redis不单用作缓存，还想用做数据存储。
    2. 桥接模式，各个容器都有自己的ip，但是外网访问不到，例如虚拟机ip是192.168.1.7，一个docker容器的映射端口为-p 6381:6379，该容器的ip经查为172.17.0.2；另一个docker容器的映射端口为-p6382:6379，该容器的ip经查为172.17.0.3；若桥接模式搭建集群，外部服务器在访问192.168.1.7:6381映射到docker1时，可能crc需转换到docker2的172.17.0.3，但外部服务器无法直接访问到该docker的ip。故无法桥接模式进行集群搭建。
    3. 只好选用host模式进行集群搭建，容器共享宿主机的ip与端口
  
    首次镜像run容器    后续直接start容器，只要你没改
  
    docker run --net host -v /home/ykls22/myredis_conf/master6381:/usr/local/etc/redis --name myredis_master6381 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    docker run --net host -v /home/ykls22/myredis_conf/master6382:/usr/local/etc/redis --name myredis_master6382 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    docker run --net host -v /home/ykls22/myredis_conf/master6383:/usr/local/etc/redis --name myredis_master6383 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    docker run --net host -v /home/ykls22/myredis_conf/slave6384:/usr/local/etc/redis --name myredis_slave6384 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    docker run --net host -v /home/ykls22/myredis_conf/slave6385:/usr/local/etc/redis --name myredis_slave6385 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    docker run --net host -v /home/ykls22/myredis_conf/slave6386:/usr/local/etc/redis --name myredis_slave6386 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf
  
    其中，各配置文件的修改内容如下：
  
    + bind 0.0.0.0
    + port 6381至6386
    + daemonize no/yes 是否已守护进程方式开启redis，即是否单独占用一个窗口
    + cluster-enabled yes
    + cluster-node-timeout 5000
  
    4. **各redis容器启动之后，进入任意一个容器，开始建立集群**
  
       **docker exec -it myredis_master6381 /bin/bash**
  
       **redis-cli --cluster create 192.168.1.7:6381 192.168.1.7:6382 192.168.1.7:6383 192.168.1.7:6384 192.168.1.7:6385 192.168.1.7:6386 --cluster-replicas 1**
  
       **这里是在容器内做的配置，指定的ip是各个容器实际的ip，不是映射到虚拟机的ip+映射端口，这种各容器之间是访问不到的**
  
       （有用到什么ruby脚本么…不确定）（每个主节点有一个从节点；前三个为主节点）
  
    5. 集群建立成功后，来到windows下的客户端，-c为集群启动，否则仅单机启动
  
       redis-cli.exe -h 192.168.1.7 -p 6381 -c
  
       在里面正常操作，即可操作集群
  
       执行cluster nodes，即可见集群状态。
  
    6. springBoot连接redis集群时，改为：
  
       spring.redis.cluster.nodes=192.168.1.7:6381,192.168.1.7:6382,192.168.1.7:6383, 192.168.1.7:6384, 192.168.1.7:6385, 192.168.1.7:6386
  
       虽然连接一个就好，但是若连的那个正好宕机就废了，所以都写上。
  
       整体可借鉴：https://www.cnblogs.com/niceyoo/p/13011626.html
  
  
  
  

+ docker/linux

  + 容器技术，容器里面运行软件环境。

  + 阿里云，搜索“容器镜像服务”（容器镜像服务ACR），管理控制台，找到加速码。

  + 数据卷实现容器与宿主机共享目录，其意义在于，若容器启动的是mysql，则数据全部都在容器当中，若容器误删除，则数据将清空。因此应将数据保留到宿主机内，容器通过共享来获取mysql数据。这种方式，容器删除了也不会删除掉共享的数据卷。

  + docker run centos:7   由于操作系统没有具体任务，会自动关闭

    Docker run -it centos:7    运行时直接进入容器内部，阻塞其自动关闭。想关闭时运行exit即可    P20

  + container 的 -v 关闭或删除容器后，执行相同的命令重启容器，可自动在新容器恢复原数据。因绑定了数据卷。

  + docker引擎与各个容器之间通过各个网桥连接（bridge）；docker引擎与宿主机之间通过另一个网桥连接。   参考编程不良人P12  29.09

  + \# 3.启动容器指定使用网桥

    \- docker run -d -p 8890:80 --name nginx001 --network info nginx

    \- docker run -d -p 8891:80 --name nginx002 --network info nginx

    ”注意:一旦指定网桥后--name指定名字就是主机名,多个容器指定在同一个网桥时,可以在任意一个容器中使用主机名与容器进行互通“，即容器不同次重启时，ip可能是随机给的，但主机名nginx001，nginx002是写死的，互通时可用主机名进行通信，而非ip  参考编程不良人P12   38.24

  + 修改后的容器，可通过commit再次生成一个镜像 参考编程不良人P14

    ![docker1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/docker1.png)

  + docker安装redis

    ![docker2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/docker2.png)

  + Idea的tool->deployment->configuration可添加远程主机连接，SSH configuration中，user要设置为root，设置成ykls会被告知没权限。密码ykls22.之后在deployment->browser Remote Host中可见远端主机目录

  + Docker Compose 最新1.29.2 稳定版       p29

  + **docker常用命令**

    + 启动docker容器

      docker run -d（后台运行） -p 5672（系统上外部端口）：5672（容器内服务监听端口）-p 15672:15672（-p可以写多个） --name myRabbitMQ（容器名称，跟容器id一样，是容器的唯一标识） tomcat：8.0-jre8（镜像名与版本号）

    + docker exec -it 容器id或者容器名 bash（命令行，一般都用这个）  进入容器，并与容器内命令终端进行交互

    + exit     退出容器

    + 删除所有的容器  docker rm -f $(docker ps -qa)

  + **常用命令**

    + pwd                             查看当前工作路径
    + ls                               列出文件内的内容
    + find / -name aa                    搜索文件所在路径
    + docker inspect 容器id|容器名称       查看容器内部细节





+ nginx

  + Nginx:   默认80端口     访问url时若不加端口，默认就是80端口

    查看pcre依赖的版本：pcre-config –version 目前8.32

    根据codesheep的方式安装了nginx和部分依赖。

    启动nginx:  于/usr/local/nginx/sbin   ./nginx

    停止nginx: /usr/local/nginx/sbin/nginx -s stop

    重启nginx：  /usr/local/nginx/sbin/nginx -s reload

    其配置文件的位置： /usr/local/nginx/conf/nginx.conf

    最初的欢迎页面：http://192.168.1.7/#/index

    我们直接关闭了linux的防火墙，80端口必通

    https://blog.csdn.net/baidu_36124158/article/details/90603496

    **查看防火状态**    **systemctl status firewalld**

    **关闭防火墙**      **systemctl disable firewalld**

    **tomcat按codesheep安装。192.168.1.7:8080       默认8080端口**

    **启动：/usr/local/tomcat/apache-tomcat-8.5.70/bin目录下 ./startup.sh**

    **关闭：……   ./shutdown.sh**

  + **浏览器解析域名并访问的过程：**
    1. **首先查找本地的host文件，查找对应的域名ip映射。**
    2. **若上一步没找到，则通过DNS向网络中进行查找。C:\Windows\System32\drivers\etc\host**





+ shiro与jwt
  + 注册registerWebby不走shiro与jwt
  + 登录loginWebby
    + 获取输入参数后，首先判断该用户名的user对象是否存在，这步只走sql。不存在就报错；存在则获取user对象。
    + 获取到user对象后，拿到数据库中该user的salt，对当前输入的password进行md5与1024hash处理最后生成jwtToken；
    + 获取subject对象，进行登录认证。
  + 登录后，对其它功能所发起的请求将会被shiro拦截，请求所携带token会被jwt验签。进而实现一次登录后，免登陆的功能。





### 服务器环境部署

+ Centos操作系统部署

  + 虚拟机环境配置

    1. 新建一台虚拟机，自定义安装，稍后安装操作系统（避免VM自动安装）

    2. 虚拟机命名  CentOS_7 _64_prod_1 

       安装位置    E:\CentOS_7 _64_prod_1

    3. 处理器数量2    每个处理器核数1    因此共2核

       内存4G

    4. 网络模式    桥接

    5. 磁盘容量30G 其他选项默认

    6. 编辑虚拟机，选择镜像    E:\CentOS7.4_64Bit\CentOS-7-x86_64-DVD-1708.iso

    7. 安装语言可选用简体中文

    8. 软件选择（无具体要求）：基本环境选择“开发及生成工作站”，附加选项选择“附加开发、DNS名称服务器、开发工具、Emacs、FTP服务器、文件及存储服务器、图形生成工具、大系统性能、主框架访问、MariaDB数据库服务器、平台开发、Python”

    9. 开始安装       root密码设置ykls22  安装完成重启    同意许可

    10. 选择汉语、输入默认、位置关闭、时区北京、在线账号跳过、名字ykls22、密码ykls22

    11. 进行网络配置：

        **首先要注意，C8DH有两块网卡，两个宽带插口**

        ![1](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B21.png)

        **https://bbs.nga.cn/read.php?tid=25932347&page=e&rand=591**

        ![2](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B22.png)

        **我端午装机的时候插的2.5GbE这块网卡的插口；买超龙装机之后，我插到了下面Gigabit那个网卡插口。**

        **对于VM而言，编辑->虚拟网络编辑器->更改设置。要桥接你正在用的网卡！你桥接一个被拔出的网卡当然连不上网。配置当然异常。**

        ![3](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B23.png)

        **另外，宿主机ip是DHCP的，你来回插拔主机网线，或者其他某些时刻，宿主机的ip会变的！ping宿主机之前先看看当前宿主机的ip！**

        排除上述问题后，设置虚拟机的ip。**（注意设置的虚拟机ip不要和宿主机ip相同！）**

        1. ifconfig查看虚拟机网络情况    其使用网卡ens33，最初是没有ip的，ping www.baidu.com不通

        2. dhclient 随机获取一个可用的ip    之后ifconfig 发现ens33有了一个动态ip，记下来

        3. vim /etc/sysconfig/network-scripts/ifcfg-ens33    配置文件  配置成静态ip。

           

           修改BOOTPROTO:static

           修改ONBOOT=yes

           新增IPADDR=192.168.1.5   记下来的ip

           新增NETMASK=255.255.255.0

           新增GATEWAY=192.168.1.1

           新增DNS1=119.29.29.29

           

           i 进入vim输入

           ecs  退出输入

           :wq  保存并退出

        4. 重启网络：systemctl restart network.service   此时ping百度或ping宿主机或ping其他虚拟机就都通了。

        5. 若宿主机能ping通虚拟机，反过来不行，参考如下，亲测有效https://www.cnblogs.com/yyee/p/13511874.html

        6. 当前宿主机ip    192.168.1.6

           CentOS_7_prod_1     192.168.1.10





+ Docker环境部署

  参考编程不良人的docker笔记4.2

  1. 卸载原始docker

     sudo yum remove docker \

     　　　　docker-client \

     　　　　docker-client-latest \

     　　　　docker-common \

     　　　　docker-latest \

     　　　　docker-latest-logrotate \

     　　　　docker-logrotate \

     　　　　docker-engine

  2. bash安装docker

     curl -fsSL get.docker.com -o get-docker.sh

     sudo sh get-docker.sh --mirror Aliyun

  3. 设置docker开机自启动

     sudo systemctl enable docker

  4. 启动docker

     sudo systemctl start docker

  5. 创建docker用户组

     sudo groupadd docker

  6. 将当前用户加入docker组

     sudo usermod -aG docker $USER

  7. 测试docker安装是否正确

     docker run hello-world

  8. 查看docker版本，当前20.10.11

     docker version

  9. 配置阿里云镜像加速，申请流程参考 https://www.bilibili.com/video/BV1ZT4y1K75K?p=5

     sudo mkdir -p /etc/docker

     sudo tee /etc/docker/daemon.json <<-'EOF'

     {

     　　"registry-mirrors": ["https://tpdso6hx.mirror.aliyuncs.com"]

     }

     EOF

     

     sudo systemctl daemon-reload

     sudo systemctl restart docker

     docker info  查看是否设置成功





+ redis集群环境部署

   整体可借鉴：https://www.cnblogs.com/niceyoo/p/13011626.html

  1. 查阅dockerhub上版本，拉取镜像

     docker pull redis:6.2.5

  2. 创建配置文件/home/ykls22/myredis_conf（我记得配置文件镜像里没有，要自己去redis的github上下载到本地然后自行修改）

     其中，各配置文件的修改内容如下：

     bind 0.0.0.0

     port 6381至6386

     daemonize no/yes 是否已守护进程方式开启redis，即是否单独占用一个窗口

     cluster-enabled yes

     cluster-node-timeout 5000

  3. 首次镜像run容器    后续直接start容器，只要你没改

     docker run --net host -v /home/ykls22/myredis_conf/master6381:/usr/local/etc/redis --name myredis_master6381 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

     docker run --net host -v /home/ykls22/myredis_conf/master6382:/usr/local/etc/redis --name myredis_master6382 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

     docker run --net host -v /home/ykls22/myredis_conf/master6383:/usr/local/etc/redis --name myredis_master6383 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

     docker run --net host -v /home/ykls22/myredis_conf/slave6384:/usr/local/etc/redis --name myredis_slave6384 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

     docker run --net host -v /home/ykls22/myredis_conf/slave6385:/usr/local/etc/redis --name myredis_slave6385 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

     docker run --net host -v /home/ykls22/myredis_conf/slave6386:/usr/local/etc/redis --name myredis_slave6386 redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf

  4. 各redis容器启动之后，进入任意一个容器，开始建立集群

     docker exec -it myredis_master6381 /bin/bash

     redis-cli --cluster create 192.168.1.10:6381 192.168.1.10:6382 192.168.1.10:6383 192.168.1.10:6384 192.168.1.10:6385 192.168.1.10:6386 --cluster-replicas 1

     这里是在容器内做的配置，指定的ip是各个容器实际的ip，不是映射到虚拟机的ip+映射端口，这种各容器之间是访问不到的（有用到什么ruby脚本么…不确定）（每个主节点有一个从节点；前三个为主节点）

  5. 集群建立成功后，来到windows下的客户端，-c为集群启动，否则仅单机启动

     d->redis->anzhuang

     redis-server.exe  redis.windows.conf

     redis-cli.exe -h 192.168.1.10 -p 6381 -c

     在里面正常操作，即可操作集群；

     执行cluster nodes，即可见集群状态。

  6. 如果宿主机连接不上，可能是linux防火墙开着   active是开着的状态   也有可能会开启自动重启防火墙，关闭命令参考：

     https://blog.csdn.net/baidu_36124158/article/details/90603496

     查看防火墙状态：systemctl status firewalld

     禁用防火墙：systemctl stop firewalld

     停止并禁用开机启动：sytemctl disable firewalld

  7. 集群配置：

     <img src="https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B24.png" alt="4" style="zoom: 67%;" />

     **我只绑定了配置文件，没绑定数据文件。缓存应该没问题，但估计会导致服务停止后的点赞等数据丢失，参考编程不良人docker的安装笔记有数据挂载相关**





+ ElasticSearch集群环境部署

  1. 从dockerHub获取docker镜像

     docker pull elasticsearch:7.6.1

  2. 启动容器，挂载配置卷、挂载数据卷、挂载ik分词器插件

     docker run --net host --name ES01 -p 9201:9201 -p 9301:9301 -v es01_config:/usr/share/elasticsearch/config -v es01_plugins:/usr/share/elasticsearch/plugins -v es01_data:/usr/share/elasticsearch/data elasticsearch:7.6.1

     

     docker run --net host --name ES02 -p 9202:9202 -p 9302:9302 -v es02_config:/usr/share/elasticsearch/config -v es02_plugins:/usr/share/elasticsearch/plugins -v es02_data:/usr/share/elasticsearch/data elasticsearch:7.6.1

     

     docker run --net host --name ES03 -p 9203:9203 -p 9303:9303 -v es03_config:/usr/share/elasticsearch/config -v es03_plugins:/usr/share/elasticsearch/plugins -v es03_data:/usr/share/elasticsearch/data elasticsearch:7.6.1

  3. 首次跑不动，因为我们尚未进行文件配置。但是容器已创建好了，且镜像的配置文件也已复制到了指定数据卷。下面修改配置卷。

     修改/var/lib/docker/volumes/es01_config ~es03_config/_data/jvm.options

     　　jvm.options　-Xms250m  -Xmx250m

     修改/var/lib/docker/volumes/es01_config ~es03_config/_data/elasticsearch.yml

     　　cluster.name: "my-docker-es"

     　　node.name: node-1

     　　network.host: 0.0.0.0

     　　http.port: 9201

     　　discovery.seed_hosts: ["192.168.1.10:9301", "192.168.1.10:9302","192.168.1.10:9303"]

     　　cluster.initial_master_nodes: node-1

     　　gateway.recover_after_nodes: 3

     　　transport.tcp.port: 9301

     　　http.cors.enabled: true

     　　http.cors.allow-origin: "*"

     

     cluster.name:集群名称（集群名称必须一致）

     node.name: node-1   node.name: node-2   node.name: node-3　　节点名称（节点名称不能相同）

     network.host: 0.0.0.0　　监听地址（必须开启远程权限，必须关闭防火墙）

     http.port: 9201　　http.port: 9202　　http.port: 9203　　监听端口（集群在一台服务器上时必须不同）

     discovery.seed_hosts:["192.168.1.10:9301","192.168.1.10:9302","192.168.1.10:9303"]　　自动发现其他节点，详见　　https://blog.csdn.net/asty9000/article/details/97028658

     cluster.initial_master_nodes: node-1　　三个配置文件全写这个，指定初始主节点，必须，否则集群连不上

     gateway.recover_after_nodes: 3　　集群可做master的最小节点数

     transport.tcp.port: 9301　　transport.tcp.port: 9302　　transport.tcp.port: 9303　　集群Tcp端口（集群在一台服务器上时必须不同）

     http.cors.enabled: true　　用于跨域

     http.cors.allow-origin: "*"　　用于跨域

  4. 修改ik分词器插件

     安装unzip工具　　https://www.cnblogs.com/ourlang/p/12230972.html

     将/home/ykls22/elasticsearch-analysis-ik-7.6.1.zip，解压到/var/lib/docker/volumes/es01_plugins~es03_plugins/_data之下，解压后的文件夹命名为ik。

  5. 安装kibana

     将/home/ykls22/kibaba-7.6.1-linux-x86_64.tar.gz解压到当前文件夹

     　　　　 tar -zxvf /home/ykls22/kibaba-7.6.1-linux-x86_64.tar.gz

     修改配置/home/ykls22/kibana-7.6.1-linux-x86_64/config/kibana.yml　　

     　　　　新增i18n.locale: "zh-CN"，实现kibana的控制面板汉化

     　　　　server.host: "192.168.1.10"

     　　　　elasticsearch.hosts: ["http://192.168.1.10:9201"]

     如有其他异常问题，可参考：https://blog.csdn.net/Bobdragery/article/details/106842984

     启动在其./bin下 ./kibana    默认端口5601

     Kibana我们就没有必要再用镜像了。

  6. 启动elasticSearch-head

     可视化插件，使用原狂神的windows平台下的该插件

     位置    D:\myProject\Webby\ES\kuangshen\elasticsearch-head-master\elasticsearch-head-master

     启动：　　npm run start　　访问127.0.0.1:9100　　连接192.168.1.10:9201即可

  7. 新增扩展字典

     在./ik/config/IKAnalyzer.cfg.xml内配置\<entry key="ext_dict">ykls22.dic\</entry>

     新建ykls22.dic文件　　注意，该文件必须是UTF-8编码才能生效！！

     新增扩展字典，仅对新增后的内容使用新增字典；历史数据已创建好了索引，仍无法使用新增字典

     ik分词器词库固定，只能自己更新网络新词

  8. 测试集群环境是否正常

     终端 curl 192.168.1.10:9201

     终端 curl 192.168.1.10:9202

     终端 curl 192.168.1.10:9203    判断是否三台ES服务均启动成功

     启动kibana、elasticSearch-head判断是否ES集群服务正常

  9. 建立索引

     代码中无建立索引的代码，首次需手动在kibana中建立索引testquestion与testComment

     此时idea的ES可正常使用了

     ```json
     # 在kibana中，testquestion与testComment的索引建立
     
     GET _search
     {
       "query": {
         "match_all": {}
       }
     }
     
     
     PUT /testquestion
     {
       "mappings": {
         "properties": {
           "title": {
             "type": "text",
             "analyzer": "ik_max_word",
             "search_analyzer": "ik_max_word"
           },
           "description": {
             "type": "text",
             "analyzer": "ik_max_word",
             "search_analyzer": "ik_max_word"
           },
           "user_id": {
             "type": "integer"
           },
           "created_date": {
             "type": "date",
             "format": "yyyy-MM-dd HH:mm:ss"
           },
           "comment_count": {
             "type": "integer"
           },
           "status": {
             "type": "integer"
           },
           "content": {
             "type": "text",
             "analyzer": "ik_max_word",
             "search_analyzer": "ik_max_word"
           }
         }
       },
       "settings": {
         "index": {
           "number_of_shards": "5",
           "number_of_replicas": "1"
         }
       }
     }
     
     
     PUT /testcomment
     {
       "mappings": {
         "properties": {
           "content": {
             "type": "text",
             "analyzer": "ik_max_word",
             "search_analyzer": "ik_max_word"
           },
           "user_id": {
             "type": "integer"
           },
           "entity_id": {
             "type": "integer"
           },
           "entity_type": {
             "type": "text"
           },
           "created_date": {
             "type": "date",
             "format": "yyyy-MM-dd HH:mm:ss"
           },
           "status": {
             "type": "integer"
           }
         }
       },
       "settings": {
         "index": {
           "number_of_shards": "5",
           "number_of_replicas": "1"
         }
       }
     }
     
     
     GET /testquestion
     
     
     GET /testcomment
     
     
     GET /testquestion/_doc/_search
     {
       "query":{
         "multi_match":{
           "query": "摘要",
           "fields": ["title","description","content"]
         }
       }
     }
     ```

  10. 可能遇到的情况

      远程连接问题

      /etc/security/limits.conf配置完成后 -sn测试还是1024　　这个需要 exit　　su ykls22　　重新登一下才会生效。

      https://blog.csdn.net/weixin_43570089/article/details/103630954

      ERROR: [1] bootstrap checks failed

      [1]: the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured

      https://blog.csdn.net/happyzxs/article/details/89156068

  

  

+ rabbitmq环境配置

  整体参考编程不良人

  1. 安装rabbitmq的依赖环境，位于/home/ykls22/rabbitmq_packages

     rpm -ivh erlang-22.0.7-1.el7.x86_64.rpm

     rpm -ivh socat-1.7.3.2-2.el7.x86_64.rpm

  2. 安装rabbitmq，文件位于/home/ykls22/rabbitmq_packages

     rpm -ivh rabbitmq-server-3.7.18-1.el7.noarch.rpm

  3. 修改rabbitmq配置。配置文件模板此时位于/usr/share/doc/rabbitmq-server-3.7.18/rabbitmq.config.example，将其复制到/etc/rabbitmq文件夹下(没有该文件夹就自己新建)，并将文件重命名为rabbitmq.config

  4. 修改rabbitmq配置文件rabbitmq.config，将%% {loopback_users, []},修改为（去掉%和，）{loopback_users, []}

  5. 启动rabbitmq中插件管理，即web管理页面

     rabbitmq-plugins enable rabbitmq_management

  6. 启动/重启/停止rabbitmq服务

     systemctl start rabbitmq-server

     systemctl restart rabbitmq-server

     systemctl stop rabbitmq-server

  7. 查看服务状态

     systemctl status rabbitmq-server

  8. 登录192.168.1.10:15672    首次登录用户名和密码都是guest

  9. web页面Admin Virtual Host新建虚拟主机/ykls

     ![5](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B25.png)

  10. web页面Admin Users新建用户ykls，密码ykls，权限Admin最大，confirm是确认密码的意思

      ![6](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B26.png)

  11. 用户ykls绑定虚拟主机/ykls。虚拟主机是为了让各个用户可以互不干扰的工作，其实就是一个独立的访问路径，不同用户使用不同路径，各自有各自的队列、交换机，互不影响。

      ![7](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B27.png)

  12. 新建一个交换机，ykls登录账号下新建，guest没有权限新建

      ![8](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B28.png)

  13. 项目中我们使用临时队列；指定topic为ykls_topic；不同的功能对应不同的routingKey，路由到不同的临时队列。发送和接收消息分别如下：

      <img src="https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B29.png" alt="9" style="zoom:67%;" />

      ![10](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B210.png)

      



+ mysql环境配置

  可参考编程不良人的docker笔记

  1. 拉取镜像

     docker pull mysql:8.0.26

  2. 启动容器，后台运行，指定root用户密码，指定容器名字，挂载数据卷（挂载的位置：/var/lib/docker/volumes/mysqldata）

     docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=ykls --name mysql -v mysqldata:/var/lib/mysql mysql:8.0.26

  3. messageService.getConversationList如果报错，是mysql5.7.5版本与后续版本的差异问题，详见https://blog.csdn.net/qq_34707744/article/details/78031413

     **配置执行完成后，可能重启一次mysql才会生效。**

  



+ 服务部署

  + Nginx环境配置

    1. E:\linux安装包\nginx-1.17.10.tar.gz解压到服务器/home/ykls22/nginx

       tar -zxvf ./nginx-1.17.10.tar.gz -C ./nginx

    2. 安装依赖

       yum -y install pcre-devel

       yum -y install openssl openssl-devel

    3. 编译安装nginx

       cd nginx-1.17.10

       ./configure

       make && make install

       安装完成后，Nginx的可执行文件在/usr/local/nginx/sbin/nginx

    4. 启动nginx

       /usr/local/nginx/sbin/nginx

       停止nginx

       /usr/local/nginx/sbin/nginx -s stop

       修改配置后重启

       /usr/local/nginx/sbin/nginx -s reload

       　　配置文件位置    /usr/local/nginx/conf/nginx.conf

       访问192.168.1.10，可看到nginx运行是否成功

  + JDK环境配置

    1. E:\linux安装包\openjdk-14.0.1_linux-x64.tar.gz解压到服务器/home/ykls22/jdk

       tar -zxvf ../openjdk-14.0.1_linux-x64_bin.tar.gz -C ./

    2. 卸载已有的jdk（注意要以java开头，具体见下图）

       ![11](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B211.png)

    3. 配置环境变量

       编辑/etc/profile文件，尾部添加

       　　JAVA_HOME=/home/ykls22/jdk/jdk-14.0.1

       　　CLASSPATH=$JAVA_HOME/lib/

       　　PATH=$PATH:$JAVA_HOME/bin

       　　export PATH JAVA_HOME CLASSPATH

       刷新生效

       　　source /etc/profile

    4. 检查是否安装成功

       java -version

       javac

  + Node环境配置

    1. E:\linux安装包\node-v14.15.3-linux-x64.tar.xz解压到服务器/home/ykls22/node

       tar -xJvf ../node-v14.15.3-linux-x64.tar.xz -C ./

    2. vim编辑~/.bash_profile文件，在文件末尾追加信息

       \# Nodejs

       export PATH=/home/ykls22/node/node-v14.15.3-linux-x64/bin:$PATH

       刷新 source ~/.bash_profile

    3. 查看配置是否成功

       node -v

       npm version

       npx -v

  + Maven环境部署

    1. E:\linux安装包\apache-maven-3.8.1-bin.tar.gz解压服务器/home/ykls22/maven

       tar -zxvf apache-maven-3.8.1-bin.tar.gz -C ./maven

    2. 编辑配置阿里云镜像

       /home/ykls22/maven/apache-maven-3.8.1/conf/setting.xml

       ![12](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B212.png)

    3. 编辑/etc/profile文件，配置环境变量

       export MAVEN_HOME=/home/ykls22/maven/apache-maven-3.8.1

       export PATH=$MAVEN_HOME/bin:$PATH

       刷新 source /etc/profile

    4. 测试

       mvn -v

  + **后端部署**

    1. pom.xml下添加配置跳过测试

       ![13](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B213.png)

    2. 清理、打包，生成target文件

       <img src="https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B214.png" alt="14" style="zoom:67%;" />

    3. target下的webby-0.0.1-SNAPSHOT.jar文件发送到/home/ykls22/back目录
    4. 启动服务  nohup java -jar webby-0.0.1-SNAPSHOT.jar &
    5. 查看nohup.out日志可看到运行的情况如何
    6. 私信列表失败为mysql那个配置问题，详见mysql的步骤3

  + **前端部署**

    1. 生成打包文件dist

       npm run build

    2. 压缩dist为zip文件，复制到/home/ykls22

    3. 解压到/home/ykls22/front

       unzip -d ./front dist.zip

    4. 修改niginx配置

       /usr/local/nginx/conf/nginx.conf配置修改

       1）80端口下

       ![15](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B215.png)

       2）最上面的用户权限改root

       ![16](https://icarustypora.oss-cn-shenzhen.aliyuncs.com/project_ykls/%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%8E%AF%E5%A2%83%E9%83%A8%E7%BD%B216.png)

    5. 启动nginx，访问地址

       http://192.168.1.10/#/loginwebby




+ 虚拟机对应服务的启动

  docker start mysql

  docker start myredis_master6381

  docker start myredis_master6382

  docker start myredis_master6383

  docker start myredis_slave6384

  docker start myredis_slave6385

  docker start myredis_slave6386

  docker start ES01

  docker start ES02

  docker start ES03

  systemctl start rabbitmq-server



+ 虚拟机服务应用的停止

  docker stop mysql

  docker stop myredis_master6381

  docker stop myredis_master6382

  docker stop myredis_master6383

  docker stop myredis_slave6384

  docker stop myredis_slave6385

  docker stop myredis_slave6386

  docker stop ES01

  docker stop ES02

  docker stop ES03

  systemctl stop rabbitmq-server





+ Mysql数据库表

  ```mysql
  CREATE TABLE `comment` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
    `user_id` int NOT NULL COMMENT '评论人，所属用户',
    `entity_id` int NOT NULL COMMENT '所评论问题的自增id',
    `entity_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '对问题进行评论，固定QUESTION',
    `created_date` datetime NOT NULL COMMENT '最近更新时间',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态',
    PRIMARY KEY (`id`),
    KEY `entity_index` (`entity_id`,`entity_type`) USING BTREE
  ) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3
  
  
  CREATE TABLE `feed` (
    `id` int NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT NULL,
    `user_id` int DEFAULT NULL,
    `data` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci,
    `type` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `user_index` (`user_id`) USING BTREE
  ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3
  
  
  CREATE TABLE `message` (
    `id` int NOT NULL AUTO_INCREMENT,
    `from_id` int DEFAULT NULL,
    `to_id` int DEFAULT NULL,
    `content` text CHARACTER SET utf8 COLLATE utf8_general_ci,
    `created_date` datetime DEFAULT NULL,
    `has_read` int DEFAULT NULL,
    `conversation_id` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    PRIMARY KEY (`id`),
    KEY `conversation_index` (`conversation_id`) USING BTREE,
    KEY `created_date` (`created_date`) USING BTREE
  ) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb3
  
  
  CREATE TABLE `question` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
    `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '摘要',
    `user_id` int NOT NULL COMMENT '提出人，所属用户',
    `created_date` datetime NOT NULL COMMENT '最新修改时间',
    `comment_count` int NOT NULL COMMENT '评论数',
    `status` int NOT NULL COMMENT '0-正常，1-已删除',
    `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'md内容',
    PRIMARY KEY (`id`),
    KEY `date_index` (`created_date`) USING BTREE
  ) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3
  
  
  CREATE TABLE `user` (
    `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '昵称',
    `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码',
    `salt` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '盐',
    `head_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户头像',
    `qq` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '个人qq，用于发送登陆通知等',
    `role` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'admin-管理员\nvip-会员\nnormal-普通用户\nlimited-受限用户',
    `permission` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权限',
    `birth` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户生日',
    `sex` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '性别，F-男，M-女',
    `type` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '可能喜欢',
    `signed` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '个性签名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
  ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3
  ```

***

#### 附加说明

1. 项目可部署在本地虚拟机环境或阿里云服务器环境。我在两个环境都部署过，但个人买的的阿里云服务器性能较低，没办法支持ES等集群环境的部署，最终上线的服务卡顿较为严重。因此若想部署到线上的云服务器，需考虑服务器的性能。
2. 由于个人技术水平有限，另外现在自己的空闲时间也越来越少，项目中有些地方存在不足，有些地方也可能存在一些bug，还望大家见谅！本项目代码完全开源，大家可任意使用。后续若有大佬愿意调试、迭代出更加完善的版本，在下感激不尽！届时希望大佬们能告诉我一下，让我也有更多学习的机会。
3. 项目的前端使用Vue搭建，很惭愧我的前端基础比较渣，前端部分的代码望大家见谅。UI界面很多都是使用饿了么的element-ui。我的显示器是31.5寸，分辨率2560*1440，在这个分辨率下前端效果可正常展示，若想适配其他分辨率，还需各位手动调节css样式代码。
4. 项目中涉及到的一些技术都可以在网上或B站获取学习。这里要感谢codeSheep大佬、编程不良人大佬、狂神大佬等Up主。在此表示真挚谢意！！
5. 欢迎大家访问我的博客http://www.icarus.wiki/，上面有些杂七杂八的技术整理与分享。欢迎大家指出错误或不足！另外大家若发现有哪些内容我没有标明出处，欢迎大家补充！
6. 我的github：https://github.com/YADIANNADETIANFA







































