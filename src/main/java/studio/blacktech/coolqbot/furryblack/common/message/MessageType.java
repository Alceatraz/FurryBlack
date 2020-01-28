package studio.blacktech.coolqbot.furryblack.common.message;


public enum MessageType {

	Normal("普通消息"), // 一般消息
	PureCode("纯码消息"), // 纯码消息

	Command("命令"), // FurryBlack认为这是命令

	Scrawls("涂鸦"), // 涂鸦 垃圾消息
	Present("礼物"), // 礼物 垃圾消息
	Envelope("红包"), // 红包 垃圾消息
	TapVideo("视频"), // 视频 垃圾消息
	SnapShot("闪照"), // 闪照 垃圾消息
	SyncMusic("听歌"); // 一起听 垃圾消息

	private String name;

	private MessageType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


}
