# FurryBlack - 一个小动物形象的人工智障  

## 注意  

"2019-09-20" 联通毫无预兆的屏蔽了443端口，非标准端口的网站怎么看都像是钓鱼网站，所以以添加了GitEE。  
"2019-10-21" 你敢信吗 Gitee被阿里云取消解析了 国内真不是搞技术的地方 再添加一个GitHub的remote。  

`https://git.blacktech.studio:8888/blacktechstudio/furryblack`  
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

## 开发

### 约定大于规范  

**保持礼貌**  

模块作者应当正确完整的书写所需权限，对用户发送消息时要保持礼貌——不应主动发送消息，除非极其重要不能错过的消息，而且必须可以取消订阅。  

### 基本

关于CoolQ：CQ的JSON解析器支持//注释  
关于Jcq：JCQ的JSON解析器支持//注释  

- 插件不应执行`entry`中的任何生命周期方法。  
- 插件不应执行`Systemd`中的任何生命周期方法。  
- 插件不应覆盖任何变量。  
- 框架并没有针对恶意模块进行任何防御，使用者应自行负责。 

### 文件  

JcqSDK会为jar（即插件）生成同名的文件夹  

比如插件名为`studio.blacktech.coolqbot.furryblack.entry.jar`  
则文件夹字为`studio.blacktech.coolqbot.furryblack.entry`  

此目录为FurryBlack Framework的所有数据主目录，现在请脑子里执行一下chroot  

- `conf/`：所有模块的配置文件，其下按照模块名称生成目录  
- `data/`：所有模块的数据文件，其下按照模块名称生成目录  

模块名即每个模块的`PACKAGE_NAME`  

只有执行了`initConfFolder()`才会生成`/conf/$PACKAGE_NAME`目录  
只有执行了`initDataFolder()`才会生成`/data/$PACKAGE_NAME`目录  
只有执行了`initConfigurtion()`才会生成`/conf/$PACKAGE_NAME/config.properties`文件  

### 模块  

除去主类和其他构造类，一切功能类皆为模块，继承自`Module`。模块提供了一些基础工具，如初始化配置及数据目录，提供一个Properties对象，用于快速配置。模块有四种既定功能的子类分为：  

- 触发器 `extends ModuleTrigger`  
- 监听器 `extends ModuleListener`  
- 执行器 `extends ModuleExecutor`  
- 定时器 `extends ModuleScheduler`  

当收到消息时，将会按照 触发器 → 监听器 → 执行器 的顺序执行  

- 触发器：触发器能收到所有消息，有权力阻止某条消息，用于对消息过滤。  
- 监听器：监听器能收到所有消息，但与触发器不同的是，他没有权力阻止消息。  
- 执行器：执行器只能收到/开头，且命令名相符的消息，这是机器人最重要的功能实现——即交互。  
- 定时器：定时器不参与CoolQ的工作，更多的是为了某些计划任务提供载体。  

### 开始

`Executor_NULL`为模板，复制重命名后修改其内容  
`Executor_DEMO`含完整注释，应按照注释说明的方法开发模块  

为了缩减体积，项目不包含doc文件，可使用使用javadoc自行编译文档  

### 工具

- `entry`  是整个机器人的本体，包含一系列重要方法，开发者不应该调用Systemd中的原始方法，而是使用entry中包装的方法。  
- `Systemd` 是整个系统的核心，负责了包括消息路由、消息发送、模块管理、模块生命周期管理等所有框架功能。  
- `LoggerX` 一个假的精简版LoggerX，本质是加了格式的StringBuffer而且不带任何保存和特殊功能。  
- `Message` `MessageUser` `MessageDisz` `MessageGrop` 所有传入消息都会被包装成Message对象，并根据消息来源包装成对应的子类。  
- `Executor_admin` 这是一个特殊的命令模块，所有管理员命令均因在此处执行（执行时会判断isAdmin，只有管理员可执行）。  
- `Listener_TopSpeak` 是一个特殊的监听模块，所有消息都将在此保存，但是本质是为了统计，所以不支持查询某一条具体消息。  