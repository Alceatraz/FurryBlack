# FurryBlack - 一个小动物形象的人工智障  


# 奠

## TX推行自己的开放平台，鉴于这家公司的气味和国内不管什么G13玩意都要实名认证，BOT将不可能迁移到开放平台。各位永别。

## 注意  

"2019-09-20" 联通毫无预兆的屏蔽了443端口，非标准端口的网站怎么看都像是钓鱼网站，所以以添加了GitEE。  
"2019-10-21" 你敢信吗 Gitee被阿里云取消解析了 国内真不是搞技术的地方 再添加一个GitHub的remote。  
"2020-02-15" 钓鱼网站实在是太烂了，取消。

`https://gitee.com/BlackTechStudio/FurryBlackBot`  
`https://github.com/Alceatraz/FurryBlack`

GitEE的组织名字居然会过长不能写，蛋疼，请记住这个群组名应该叫做**BlackTechStudio - Offical**  

### 警告  

**由于极度强迫症对不齐很难受 而且这个项目也没人给我钱 也没有合作者 所以很多代码的名命完全放飞自我 导致很怪**  

比如随意乱缩写  
> user → user  
> disz → discuz  
> grop → group  
> 私聊 → 私聊  
> 组聊 → 讨论组  
> 群聊 → 群聊  

比如混乱的词缀  
> TRIGER → TRIGGER → 触发  
> LISTEN → LISTENER → 监听  
> STORED → STORE → 存储  
> CACHED → CACHE → 缓存  
> OBTAIN → OBTAIN → 获取  

### 警告×2  

即使在文档中$也是环境变量的意思  
例如 `$NAME` 意思是这里是名字而不是照抄"`$NAME`"  

**以上都是我编的，我实在编不下去了**  

## 部署

shui水群统计模块使用了PostgreSQL，共创建了13个表，分别记录群信息、用户信息、群昵称、按照时间线保存的聊天记录和8种CQCode，以及WEB模块的注册用户。    

```
CREATE USERR furryblack WITH PASSWORD 'furryblack';
CREATE DATABASE furryblack OWNER furryblack;
GRANT ALL PRIVILEGES ON DATABASE furryblack TO furryblack;

CREATE TABLE "public"."grop_info" (
  "grop_id" int8 NOT NULL,
  "grop_name" varchar(255) COLLATE "pg_catalog"."default"
);

CREATE TABLE "public"."user_card" (
  "grop_id" int8,
  "user_id" int8,
  "user_nick" varchar(255) COLLATE "pg_catalog"."default"
);

CREATE TABLE "public"."user_nick" (
  "user_id" int8,
  "nickname" varchar(255) COLLATE "pg_catalog"."default"
);

CREATE TABLE "public"."web_user" (
  "username" varchar COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "user_id" int8
);
 
CREATE TABLE "public"."chat_record" (
  "message_id" int8 NOT NULL,
  "message_font" int8 NOT NULL,
  "message_time" timestamp(6) NOT NULL,
  "grop_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "type_id" int4 NOT NULL,
  "message" text COLLATE "pg_catalog"."default" NOT NULL,
  "content" text COLLATE "pg_catalog"."default"
);

CREATE TABLE "public"."record_at" (
  "mesage_id" int8 NOT NULL,
  "receive_id" int8 NOT NULL
);

CREATE TABLE "public"."record_face" (
  "message_id" int8 NOT NULL,
  "face_id" int8 NOT NULL
);

CREATE TABLE "public"."record_sface" (
  "message_id" int8,
  "face_id" varchar(64) COLLATE "pg_catalog"."default"
);

CREATE TABLE "public"."record_bface" (
  "message_id" int8 NOT NULL,
  "face_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL
);

CREATE TABLE "public"."record_emoji" (
  "message_id" int8 NOT NULL,
  "face_id" int8 NOT NULL
);

CREATE TABLE "public"."record_dice" (
  "message_id" int8 NOT NULL,
  "dice_id" int8 NOT NULL
);

CREATE TABLE "public"."record_rps" (
  "message_id" int8 NOT NULL,
  "rps_id" int8
);

CREATE TABLE "public"."record_image" (
  "message_id" int8 NOT NULL,
  "picture_code" varchar(53) COLLATE "pg_catalog"."default" NOT NULL,
  "image_url" varchar(4096) COLLATE "pg_catalog"."default" NOT NULL,
  "image_path" varchar(4096) COLLATE "pg_catalog"."default"
);

```

一些常用的SQL语句

```
提取所有图片

SELECT match[1] AS result
FROM ( SELECT message FROM chat_record ) AS temp_1
CROSS JOIN LATERAL regexp_matches(message, '\[CQ:image,file=\w{32}\.\w{3,4}\]','g') AS match


统计某个群最常说的20句话
 
SELECT "count", "content" FROM (
	SELECT COUNT( "content" ) AS "count", "content" FROM ( 
		SELECT COALESCE ( "content", message ) AS "content" FROM chat_record 
		WHERE grop_id = #{groupID} AND type_id = 10
	) AS temp1
	GROUP BY temp1."content" 
) AS temp2 
ORDER BY temp2."count" DESC LIMIT 20

统计某个群最能说的20个人
SELECT user_id AS user, COUNT ( user_id ) AS "count" 
FROM chat_record 
WHERE grop_id = #{groupID} 
GROUP BY user_id 
ORDER BY "count" DESC 
LIMIT 20
```

## 开发

### 约定大于规范  

**保持礼貌**  

模块作者应当正确完整的书写所需权限，对用户发送消息时要保持礼貌——不应主动发送消息，除非极其重要不能错过的消息，而且必须可以取消订阅。  

### 基本

- CoolQ CQ的JSON解析器支持//注释  
- Jcq JCQ的JSON解析器支持//注释  
- 插件不应执行`entry`中的任何生命周期方法。  
- 插件不应执行`Systemd`中的任何生命周期方法。  
- 插件不应覆盖任何变量。  
- 框架并没有针对恶意模块进行任何防御，使用者应自行负责。 

### 文件  

JcqSDK会为jar（即插件）生成同名的文件夹  

比如插件名为`studio.blacktech.coolqbot.furryblack.entry.jar`  
则文件夹字为`studio.blacktech.coolqbot.furryblack.entry`  

此目录为FurryBlack Framework的所有数据主目录  

- `initAppFolder()` 此方法创建模块的数据目录，即`PACKAGE_NAME`  
- `initConfFolder()` 此方法创建数据目录下的`conf`目录  
- `initDataFolder()` 此方法创建数据目录下的`data`目录  
- `initLogsFolder()` 此方法创建数据目录下的`logs`目录  
- `initPropertiesConfigurtion` 此方法创建`config.properties`文件  
- `loadConfig()` 使用`Properties`读取`config.properties`的内容到`CONFIG`对象  
- `saveConfig()` 使用`Properties`保存`CONFIG`对象的内容到`config.properties`  

### 模块  

除去主类和其他构造类，一切功能类皆为模块，继承自`Module`。模块提供了一些基础工具，如初始化配置及数据目录，提供一个`Properties`对象，用于快速配置。模块有四种既定功能的子类分为：  

- 触发器 `extends ModuleTrigger` 需要标记 `@ModuleTriggerComponent` 注解  
- 监听器 `extends ModuleListener` 需要标记 `@ModuleListenerComponent` 注解  
- 执行器 `extends ModuleExecutor` 需要标记 `@ModuleExecutorComponent` 注解  
- 定时器 `extends ModuleScheduler` 需要标记 `@ModuleSchedulerComponent` 注解  

当收到消息时，将会按照 触发器 → 监听器 → 执行器 的顺序执行  

- 触发器：触发器能收到所有消息，有权力阻止某条消息，用于对消息过滤。  
- 监听器：监听器能收到所有消息，但与触发器不同的是，他没有权力阻止消息。  
- 执行器：执行器只能收到/开头，且命令名相符的消息，这是机器人最重要的功能实现——即交互。  
- 定时器：定时器不参与CoolQ的工作，更多的是为了某些计划任务提供载体。  

### 开始

`Executor_NULL`为模板，复制重命名后修改其内容  
`Executor_DEMO`含完整注释，应按照注释说明的方法开发模块  

### 工具

- `entry`  是整个机器人的本体，包含一系列重要方法，开发者不应该调用Systemd中的原始方法，而是使用entry中包装的方法。  
- `Systemd` 是整个系统的核心，负责了包括消息路由、消息发送、模块管理、模块生命周期管理等所有框架功能。  
- `LoggerX` 一个为了性能进行取舍的日志工具类，包含了大量的实用方法。  
- `Message` `MessageUser` `MessageDisz` `MessageGrop` 所有传入消息都会被包装成Message对象，并根据消息来源包装成对应的子类。  
