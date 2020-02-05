package studio.blacktech.coolqbot.furryblack.common.message.type;


public enum MessageType {

	Normal(10), // 普通消息
	PureCode(11), // 纯码消息
	Command(20), // FurryBlack认可的命令
	Scrawls(21), // 涂鸦 垃圾消息
	Present(22), // 礼物 垃圾消息
	Envelope(23), // 红包 垃圾消息
	TapVideo(24), // 视频 垃圾消息
	SnapShot(25), // 闪照 垃圾消息
	SyncMusic(26); // 一起听 垃圾消息

	private int id;
	private String name;

	private MessageType(int id) {

		this.id = id;

		switch (id) {

		case 10:
			name = "普通";
			break;

		case 11:
			name = "纯码";
			break;

		case 20:
			name = "命令";
			break;

		case 21:
			name = "涂鸦";
			break;

		case 22:
			name = "礼物";
			break;

		case 23:
			name = "红包";
			break;

		case 24:
			name = "视频";
			break;

		case 25:
			name = "闪照";
			break;

		case 26:
			name = "听歌";
			break;
		}
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}
}
