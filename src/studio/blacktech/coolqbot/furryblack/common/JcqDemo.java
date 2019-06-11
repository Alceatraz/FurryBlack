package studio.blacktech.coolqbot.furryblack.common;

import javax.swing.JOptionPane;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.GroupFile;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

/*
 * ===============================================
 *
 *
 * Comment by Alceatraz : ���ļ���JcqSDK���Դ�Demo
 *
 *
 * ===============================================
 */
/**
 * ���ļ���JCQ���������<br>
 * <br>
 *
 * ע���޸�json�е�class���������࣬�粻����������appid���أ����һ�������Զ���д����<br>
 * ����appid(com.example.demo) ������� com.example.Demo<br>
 * �ĵ���ַ�� https://gitee.com/Sobte/JCQ-CoolQ <br>
 * ���ӣ�https://cqp.cc/t/37318 <br>
 * ������������: {@link JcqAppAbstract#CQ CQ}({@link com.sobte.cqp.jcq.entity.CoolQ
 * ��Q���Ĳ�����}), {@link JcqAppAbstract#CC
 * CC}({@link com.sobte.cqp.jcq.entity.CQCode ��Q�������}), ���幦�ܿ��Բ鿴�ĵ�
 */
public class JcqDemo extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	/**
	 * ��main�������Կ�����󻯵ļӿ쿪��Ч�ʣ����Ͷ�λ����λ��<br/>
	 * ���¾���ʹ��Main�������в��Ե�һ�����װ���
	 *
	 * @param args ϵͳ����
	 */
	public static void main(final String[] args) {
// CQ�˱���Ϊ�����������JCQ����ʱʵ������ֵ��ÿ����������ڲ����п�����CQDebug����������
		JcqApp.CQ = new CQDebug();// new CQDebug("Ӧ��Ŀ¼","Ӧ������") �����ô˹�������ʼ��Ӧ�õ�Ŀ¼
		JcqApp.CQ.logInfo("[JCQ] TEST Demo", "��������");// ���ھͿ�����CQ������ִ���κ���Ҫ�Ĳ�����
// Ҫ�����������ʵ����һ���������
		final JcqDemo demo = new JcqDemo();
// �����������и���������,����JCQ���й��̣�ģ��ʵ�����
		demo.startup();// �������п�ʼ ����Ӧ�ó�ʼ������
		demo.enable();// �����ʼ����ɺ�����Ӧ�ã���Ӧ����������
// ��ʼģ�ⷢ����Ϣ
// ģ��˽����Ϣ
// ��ʼģ��QQ�û�������Ϣ������QQȫ�����죬�������
		demo.privateMsg(0, 10001, 2234567819L, "С���Լ��", 0);
		demo.privateMsg(0, 10002, 2222222224L, "������������", 0);
		demo.privateMsg(0, 10003, 2111111334L, "���Ը������΢����", 0);
		demo.privateMsg(0, 10004, 3111111114L, "�����������", 0);
		demo.privateMsg(0, 10005, 3333333334L, "��û�����������QAQ", 0);
// ģ��Ⱥ����Ϣ
// ��ʼģ��Ⱥ����Ϣ
		demo.groupMsg(0, 10006, 3456789012L, 3333333334L, "", "�˵�", 0);
		demo.groupMsg(0, 10008, 3456789012L, 11111111114L, "", "С���أ���������ѽ", 0);
		demo.groupMsg(0, 10009, 427984429L, 3333333334L, "", "[CQ:at,qq=2222222224] ��һ������Ϸ����������", 0);
		demo.groupMsg(0, 10010, 427984429L, 3333333334L, "", "�þò����� [CQ:at,qq=11111111114]", 0);
		demo.groupMsg(0, 10011, 427984429L, 11111111114L, "", "qwq ��û��һ�𿪵�\n[CQ:at,qq=3333333334]������", 0);
// ......
// �������ƣ����Ը���ʵ������޸Ĳ������ͷ�������Ч��
// ��������β��������
// demo.disable();// ʵ�ʹ����г���������ᴥ��disable��ֻ���û��ر��˴˲���Żᴥ��
		demo.exit();// ���������н���������exit����
	}

	/**
	 * ����󽫲������ �벻Ҫ�ڴ��¼���д��������
	 *
	 * @return ����Ӧ�õ�ApiVer��Appid
	 */
	@Override
	public String appInfo() {
// Ӧ��AppID,����� http://d.cqp.me/Pro/����/������Ϣ#appid
		final String AppID = "com.example.demo";// ��ס�������ļ���jsonҲҪʹ��appid���ļ���
		/**
		 * ����������ֹ�����������κδ��룬���ⷢ���쳣����� ����ִ�г�ʼ���������� startup �¼���ִ�У�Type=1001����
		 */
		return ICQVer.CQAPIVER + "," + AppID;
	}

	/**
	 * ��Q���� (Type=1001)<br>
	 * ���������ڿ�Q�����̡߳��б����á�<br>
	 * ��������ִ�в����ʼ�����롣<br>
	 * ����ؾ��췵�ر��ӳ��򣬷���Ῠס��������Լ�������ļ��ء�
	 *
	 * @return ��̶�����0
	 */
	@Override
	@SuppressWarnings("unused")
	public int startup() {
		JcqApp.CQ.getAppDirectory();
// �����磺D:\CoolQ\app\com.sobte.cqp.jcq\app\com.example.demo\
// Ӧ�õ��������ݡ����á����롿����ڴ�Ŀ¼��������û��������š�
		return 0;
	}

	/**
	 * ��Q�˳� (Type=1002)<br>
	 * ���������ڿ�Q�����̡߳��б����á�<br>
	 * ���۱�Ӧ���Ƿ����ã������������ڿ�Q�˳�ǰִ��һ�Σ���������ִ�в���رմ��롣
	 *
	 * @return ��̶�����0�����غ��Q���ܿ�رգ��벻Ҫ��ͨ���̵߳ȷ�ʽִ���������롣
	 */
	@Override
	public int exit() {
		return 0;
	}

	/**
	 * Ӧ���ѱ����� (Type=1003)<br>
	 * ��Ӧ�ñ����ú󣬽��յ����¼���<br>
	 * �����Q����ʱӦ���ѱ����ã����� {@link #startup startup}(Type=1001,��Q����)
	 * �����ú󣬱�����Ҳ��������һ�Ρ�<br>
	 * ��Ǳ�Ҫ����������������ش��ڡ�
	 *
	 * @return ��̶�����0��
	 */
	@Override
	public int enable() {
		JcqAppAbstract.enable = true;
		return 0;
	}

	/**
	 * Ӧ�ý���ͣ�� (Type=1004)<br>
	 * ��Ӧ�ñ�ͣ��ǰ�����յ����¼���<br>
	 * �����Q����ʱӦ���ѱ�ͣ�ã��򱾺��������᡿�����á�<br>
	 * ���۱�Ӧ���Ƿ����ã���Q�ر�ǰ�������������᡿�����á�
	 *
	 * @return ��̶�����0��
	 */
	@Override
	public int disable() {
		JcqAppAbstract.enable = false;
		return 0;
	}

	/**
	 * ˽����Ϣ (Type=21)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subType �����ͣ�11/���Ժ��� 1/��������״̬ 2/����Ⱥ 3/����������
	 * @param msgId   ��ϢID
	 * @param fromQQ  ��ԴQQ
	 * @param msg     ��Ϣ����
	 * @param font    ����
	 * @return ����ֵ*����*ֱ�ӷ����ı� ���Ҫ�ظ���Ϣ�������api����<br>
	 *         ���� ���� {@link IMsg#MSG_INTERCEPT MSG_INTERCEPT} - �ضϱ�����Ϣ�����ټ�������<br>
	 *         ע�⣺Ӧ�����ȼ�����Ϊ"���"(10000)ʱ������ʹ�ñ�����ֵ<br>
	 *         ������ظ���Ϣ������֮���Ӧ��/�������������� ���� {@link IMsg#MSG_IGNORE MSG_IGNORE} -
	 *         ���Ա�����Ϣ
	 */
	@Override
	public int privateMsg(final int subType, final int msgId, final long fromQQ, final String msg, final int font) {
// ���ﴦ����Ϣ
		JcqApp.CQ.sendPrivateMsg(fromQQ, "�㷢������������Ϣ��" + msg + "\n����Java���");
		return IMsg.MSG_IGNORE;
	}

	/**
	 * Ⱥ��Ϣ (Type=2)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subType       �����ͣ�Ŀǰ�̶�Ϊ1
	 * @param msgId         ��ϢID
	 * @param fromGroup     ��ԴȺ��
	 * @param fromQQ        ��ԴQQ��
	 * @param fromAnonymous ��Դ������
	 * @param msg           ��Ϣ����
	 * @param font          ����
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@SuppressWarnings("unused")
	@Override
	public int groupMsg(final int subType, final int msgId, final long fromGroup, final long fromQQ, final String fromAnonymous, final String msg, final int font) {
		// �����Ϣ����������
		if ((fromQQ == 80000000L) && !fromAnonymous.equals("")) { JcqApp.CQ.getAnonymous(fromAnonymous); }
		// ����CQ�밸�� �磺[CQ:at,qq=100000]
		// ����CQ�� ���ñ���Ϊ CC(CQCode) �˱���רΪCQ�������ض���ʽ���˽����ͷ�װ
		// CC.analysis();
		// �˷�����CQ�����Ϊ��ֱ�Ӷ�ȡ�Ķ���
		// ������Ϣ�е�QQID
		// long qqId = CC.getAt(msg);
		// �˷���Ϊ��㷽������ȡ��һ��CQ:at���QQ�ţ�����ʱΪ��-1000
		// List<Long> qqIds = CC.getAts(msg);
		// �˷���Ϊ��ȡ��Ϣ�����е�CQ����󣬴���ʱ���� �ѽ���������
		// ������Ϣ�е�ͼƬ
		// CQImage image = CC.getCQImage(msg);
		// �˷���Ϊ��㷽������ȡ��һ��CQ:image���ͼƬ���ݣ�����ʱ��ӡ�쳣������̨������ null
		// List<CQImage> images = CC.getCQImages(msg);
		// �˷���Ϊ��ȡ��Ϣ�����е�CQͼƬ���ݣ�����ʱ��ӡ�쳣������̨������ �ѽ���������
		// ���ﴦ����Ϣ
		JcqApp.CQ.sendGroupMsg(fromGroup, JcqApp.CC.at(fromQQ) + "�㷢������������Ϣ��" + msg + "\n����Java���");
		return IMsg.MSG_IGNORE;
	}

	/**
	 * ��������Ϣ (Type=4)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype     �����ͣ�Ŀǰ�̶�Ϊ1
	 * @param msgId       ��ϢID
	 * @param fromDiscuss ��Դ������
	 * @param fromQQ      ��ԴQQ��
	 * @param msg         ��Ϣ����
	 * @param font        ����
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int discussMsg(final int subtype, final int msgId, final long fromDiscuss, final long fromQQ, final String msg, final int font) {
// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * Ⱥ�ļ��ϴ��¼� (Type=11)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subType   �����ͣ�Ŀǰ�̶�Ϊ1
	 * @param sendTime  ����ʱ��(ʱ���)// 10λʱ���
	 * @param fromGroup ��ԴȺ��
	 * @param fromQQ    ��ԴQQ��
	 * @param file      �ϴ��ļ���Ϣ
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int groupUpload(final int subType, final int sendTime, final long fromGroup, final long fromQQ, final String file) {
		final GroupFile groupFile = JcqApp.CQ.getGroupFile(file);
		if (groupFile == null) { return IMsg.MSG_IGNORE; }
// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * Ⱥ�¼�-����Ա�䶯 (Type=101)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype        �����ͣ�1/��ȡ������Ա 2/�����ù���Ա
	 * @param sendTime       ����ʱ��(ʱ���)
	 * @param fromGroup      ��ԴȺ��
	 * @param beingOperateQQ ������QQ
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int groupAdmin(final int subtype, final int sendTime, final long fromGroup, final long beingOperateQQ) {
		// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * Ⱥ�¼�-Ⱥ��Ա���� (Type=102)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype        �����ͣ�1/ȺԱ�뿪 2/ȺԱ����
	 * @param sendTime       ����ʱ��(ʱ���)
	 * @param fromGroup      ��ԴȺ��
	 * @param fromQQ         ������QQ(��������Ϊ2ʱ����)
	 * @param beingOperateQQ ������QQ
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int groupMemberDecrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * Ⱥ�¼�-Ⱥ��Ա���� (Type=103)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype        �����ͣ�1/����Ա��ͬ�� 2/����Ա����
	 * @param sendTime       ����ʱ��(ʱ���)
	 * @param fromGroup      ��ԴȺ��
	 * @param fromQQ         ������QQ(������ԱQQ)
	 * @param beingOperateQQ ������QQ(����Ⱥ��QQ)
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int groupMemberIncrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
		// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * �����¼�-��������� (Type=201)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype  �����ͣ�Ŀǰ�̶�Ϊ1
	 * @param sendTime ����ʱ��(ʱ���)
	 * @param fromQQ   ��ԴQQ
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int friendAdd(final int subtype, final int sendTime, final long fromQQ) {
		// ���ﴦ����Ϣ
		return IMsg.MSG_IGNORE;
	}

	/**
	 * ����-������� (Type=301)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype      �����ͣ�Ŀǰ�̶�Ϊ1
	 * @param sendTime     ����ʱ��(ʱ���)
	 * @param fromQQ       ��ԴQQ
	 * @param msg          ����
	 * @param responseFlag ������ʶ(����������)
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int requestAddFriend(final int subtype, final int sendTime, final long fromQQ, final String msg, final String responseFlag) {
		// REQUEST_ADOPT ͨ�� REQUEST_REFUSE �ܾ�
		JcqApp.CQ.setFriendAddRequest(responseFlag, IRequest.REQUEST_ADOPT, null); // ͬ������������
		return IMsg.MSG_IGNORE;
	}

	/**
	 * ����-Ⱥ��� (Type=302)<br>
	 * ���������ڿ�Q���̡߳��б����á�<br>
	 *
	 * @param subtype      �����ͣ�1/����������Ⱥ 2/�Լ�(����¼��)������Ⱥ
	 * @param sendTime     ����ʱ��(ʱ���)
	 * @param fromGroup    ��ԴȺ��
	 * @param fromQQ       ��ԴQQ
	 * @param msg          ����
	 * @param responseFlag ������ʶ(����������)
	 * @return ���ڷ���ֵ˵��, �� {@link #privateMsg ˽����Ϣ} �ķ���
	 */
	@Override
	public int requestAddGroup(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final String msg, final String responseFlag) {
		// ���ﴦ����Ϣ
		// REQUEST_ADOPT ͨ��
		// REQUEST_REFUSE �ܾ�
		// REQUEST_GROUP_ADD Ⱥ���
		// REQUEST_GROUP_INVITE Ⱥ����
		if (subtype == 1) { JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_ADD, IRequest.REQUEST_ADOPT, null); }
		if (subtype == 2) { JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null); }
		return IMsg.MSG_IGNORE;
	}

	/**
	 * ����������JCQ���̡߳��б����á�
	 *
	 * @return �̶�����0
	 */
	public int menuA() {
		JOptionPane.showMessageDialog(null, "���ǲ��Բ˵�A��������������ش���");
		return 0;
	}

	/**
	 * ���������ڿ�Q���̡߳��б����á�
	 *
	 * @return �̶�����0
	 */
	public int menuB() {
		JOptionPane.showMessageDialog(null, "���ǲ��Բ˵�B��������������ش���");
		return 0;
	}
}
