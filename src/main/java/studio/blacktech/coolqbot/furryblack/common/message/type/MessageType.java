package studio.blacktech.coolqbot.furryblack.common.message.type;


public enum MessageType {

	Command("命令", 0), // FurryBlack认为这是命令

	Normal("普通消息", 1), // 一般消息
	PureCode("纯码消息", 2), // 纯码消息

	Scrawls("涂鸦", 11), // 涂鸦 垃圾消息
	Present("礼物", 12), // 礼物 垃圾消息
	Envelope("红包", 13), // 红包 垃圾消息
	TapVideo("视频", 14), // 视频 垃圾消息
	SnapShot("闪照", 15), // 闪照 垃圾消息
	SyncMusic("听歌", 16); // 一起听 垃圾消息

	private int id;
	private String name;

	private MessageType(int id) {
		this.id = id;

		switch (id) {
		case 0:
			name = "命令";
			break;
		}
	}

	private MessageType(String name, int id) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}


}
