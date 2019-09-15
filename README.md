# FurryBlack - 一个小动物形象的人工智障

## 警告

**由于极度强迫症对不齐很难受 所以很多代码的名命很怪**

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

![很怪.jpg](https://git.blacktech.studio/blacktechstudio/furryblack/raw/master/images/very_strange.jpg)

## 警告×2

$是环境变量的意思 例如 `$NAME` 意思是这里是名字而不是照抄"`$NAME`"

## 文件结构

**PACKAGENAME即名称**


`conf/`：所有模块的配置文件，其下按照模块名称生成目录  
`data/`：所有模块的数据文件，其下按照模块名称生成目录  
只有执行了`initConfFolder()`才会生成`/conf/$PACKAGE_NAME`目录  
只有执行了`initDataFolder()`才会生成`/data/$PACKAGE_NAME`目录  
只有执行了`initConfigurtion()`才会生成`/data/config.properties`目录  

## 框架

**一切皆模块**

除去主类和其他构造类，一切功能类皆为模块。

模块提供了一些基础工具，如初始化配置及数据目录，提供一个Properties对象，用于快速配置

模块有五种个既定功能的子类分为

- 定时器 `extends ModuleScheduler`
- 触发器 `extends ModuleTrigger`
- 监听器 `extends ModuleListener`
- 执行器 `extends ModuleExecutor`

定时器不参与CoolQ的工作，更多的是为了某些计划任务提供载体，比如“使用QQ控制DDNS”、“使用QQ收取系统信息”。  
entry为入口文件，目前共有两个核心模块（`extends Module`）：

- `Systemd`：整个框架的核心，负责启停，事件处理，特殊模块，虽然继承但是不执行继承来的生命周期函数
- `Message`：发送消息的模块，管理员的ID将在此配置
- `Nickmap`：昵称管理模块，昵称的获取

核心模块由于安全问题，仅在entry中有其实例，使用`getSystemd()`可返回其代理子类，子类只对需要公开的方法进行调用转发


**如何开发**

`executor_DEMO`内含完整注释  
`executor_NULL`为复制模板  