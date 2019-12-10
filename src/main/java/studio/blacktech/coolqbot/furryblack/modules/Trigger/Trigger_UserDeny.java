package studio.blacktech.coolqbot.furryblack.modules.Trigger;

import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.entry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.TreeMap;

public class Trigger_UserDeny extends ModuleTrigger {

    private static final long serialVersionUID = 1L;

    // ==========================================================================================================================================================
    //
    // 模块基本配置
    //
    // ==========================================================================================================================================================

    private static String MODULE_PACKAGENAME = "Trigger_UserDeny";
    private static String MODULE_COMMANDNAME = "userdeny";
    private static String MODULE_DISPLAYNAME = "过滤器";
    private static String MODULE_DESCRIPTION = "用户过滤器";
    private static String MODULE_VERSION = "2.0";
    private static String[] MODULE_USAGE = new String[]{};
    private static String[] MODULE_PRIVACY_STORED = new String[]{
            "按照\"群-成员\"的层级关系手动配置被阻止的用户"
    };
    private static String[] MODULE_PRIVACY_CACHED = new String[]{};
    private static String[] MODULE_PRIVACY_OBTAIN = new String[]{};

    // ==========================================================================================================================================================
    //
    // 成员变量
    //
    // ==========================================================================================================================================================

    private HashSet<Long> USER_IGNORE;
    private HashSet<Long> DISZ_IGNORE;
    private HashSet<Long> GROP_IGNORE;
    private TreeMap<Long, HashSet<Long>> DISZ_IGNORE_ONE;
    private TreeMap<Long, HashSet<Long>> GROP_IGNORE_ONE;

    private TreeMap<Long, Integer> DENY_USER_COUNT;
    private TreeMap<Long, TreeMap<Long, Integer>> DENY_DISZ_COUNT;
    private TreeMap<Long, TreeMap<Long, Integer>> DENY_GROP_COUNT;

    private File FILE_USERIGNORE;
    private File FILE_DISZIGNORE;
    private File FILE_GROPIGNORE;

    private File FILE_DENY_USER;
    private File FILE_DENY_DISZ;
    private File FILE_DENY_GROP;

    // ==========================================================================================================================================================
    //
    // 生命周期函数
    //
    // ==========================================================================================================================================================

    public Trigger_UserDeny() throws Exception {

        // @formatter:off

        super(
                MODULE_PACKAGENAME,
                MODULE_COMMANDNAME,
                MODULE_DISPLAYNAME,
                MODULE_DESCRIPTION,
                MODULE_VERSION,
                MODULE_USAGE,
                MODULE_PRIVACY_STORED,
                MODULE_PRIVACY_CACHED,
                MODULE_PRIVACY_OBTAIN
        );

        // @formatter:on

    }

    @Override
    public boolean init() throws Exception {

        initAppFolder();
        initConfFolder();
        initLogsFolder();

        USER_IGNORE = new HashSet<>(100);
        DISZ_IGNORE = new HashSet<>();
        GROP_IGNORE = new HashSet<>();
        DISZ_IGNORE_ONE = new TreeMap<>();
        GROP_IGNORE_ONE = new TreeMap<>();
        DENY_USER_COUNT = new TreeMap<>();
        DENY_DISZ_COUNT = new TreeMap<>();
        DENY_GROP_COUNT = new TreeMap<>();

        if (NEW_CONFIG) {
            CONFIG.setProperty("enable_user", "false");
            CONFIG.setProperty("enable_disz", "false");
            CONFIG.setProperty("enable_grop", "false");
            saveConfig();
        } else {
            loadConfig();
        }

        FILE_USERIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();
        FILE_DISZIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_disz.txt").toFile();
        FILE_GROPIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_grop.txt").toFile();

        FILE_DENY_USER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_user_log.txt").toFile();
        FILE_DENY_DISZ = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_disz_log.txt").toFile();
        FILE_DENY_GROP = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_grop_log.txt").toFile();

        if (!FILE_USERIGNORE.exists()) {
            FILE_USERIGNORE.createNewFile();
        }
        if (!FILE_DISZIGNORE.exists()) {
            FILE_DISZIGNORE.createNewFile();
        }
        if (!FILE_GROPIGNORE.exists()) {
            FILE_GROPIGNORE.createNewFile();
        }

        if (!FILE_DENY_USER.exists()) {
            FILE_DENY_USER.createNewFile();
        }
        if (!FILE_DENY_DISZ.exists()) {
            FILE_DENY_DISZ.createNewFile();
        }
        if (!FILE_DENY_GROP.exists()) {
            FILE_DENY_GROP.createNewFile();
        }

        ENABLE_USER = Boolean.parseBoolean(CONFIG.getProperty("enable_user", "false"));
        ENABLE_DISZ = Boolean.parseBoolean(CONFIG.getProperty("enable_disz", "false"));
        ENABLE_GROP = Boolean.parseBoolean(CONFIG.getProperty("enable_grop", "false"));

        BufferedReader readerUser = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_USERIGNORE), StandardCharsets.UTF_8));
        BufferedReader readerDisz = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_DISZIGNORE), StandardCharsets.UTF_8));
        BufferedReader readerGrop = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_GROPIGNORE), StandardCharsets.UTF_8));

        long userid;
        long diszid;
        long gropid;
        String line;
        String[] temp;

        while ((line = readerUser.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#")).trim();
            }
            USER_IGNORE.add(Long.parseLong(line));
            logger.seek("禁止私聊用户", line);
        }

        while ((line = readerDisz.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (!line.contains(":")) {
                continue;
            }
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#")).trim();
            }
            temp = line.split(":");
            diszid = Long.parseLong(temp[0]);
            userid = Long.parseLong(temp[1]);
            if (userid == 0) {
                DISZ_IGNORE.add(diszid);
            } else {
                if (!DISZ_IGNORE_ONE.containsKey(diszid)) {
                    HashSet<Long> tempSet = new HashSet<>();
                    DISZ_IGNORE_ONE.put(diszid, tempSet);
                }
                DISZ_IGNORE_ONE.get(diszid).add(userid);
            }
            logger.seek("禁止组聊用户", line);
        }

        while ((line = readerGrop.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (!line.contains(":")) {
                continue;
            }
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#")).trim();
            }
            temp = line.split(":");
            temp = line.split(":");
            gropid = Long.parseLong(temp[0]);
            userid = Long.parseLong(temp[1]);
            if (userid == 0) {
                GROP_IGNORE.add(gropid);
            } else {
                if (!GROP_IGNORE_ONE.containsKey(gropid)) {
                    HashSet<Long> tempSet = new HashSet<>();
                    GROP_IGNORE_ONE.put(gropid, tempSet);
                }
                GROP_IGNORE_ONE.get(gropid).add(userid);
            }
            logger.seek("禁止群聊用户", line);
        }

        readerUser.close();
        readerDisz.close();
        readerGrop.close();

        ENABLE_USER = ENABLE_USER && (USER_IGNORE.size() > 0);
        ENABLE_DISZ = ENABLE_DISZ && ((DISZ_IGNORE.size() + DISZ_IGNORE_ONE.size()) > 0);
        ENABLE_GROP = ENABLE_GROP && ((GROP_IGNORE.size() + GROP_IGNORE_ONE.size()) > 0);

        for (Long tempuserid : USER_IGNORE) {
            DENY_USER_COUNT.put(tempuserid, 0);
        }

        for (Long tempdiszid : DISZ_IGNORE_ONE.keySet()) {
            TreeMap<Long, Integer> tempcount = new TreeMap<>();
            HashSet<Long> tempdisz = DISZ_IGNORE_ONE.get(tempdiszid);
            for (Long tempuserid : tempdisz) {
                tempcount.put(tempuserid, 0);
            }
            DENY_DISZ_COUNT.put(tempdiszid, tempcount);
        }

        for (Long tempgropid : GROP_IGNORE_ONE.keySet()) {
            TreeMap<Long, Integer> tempcount = new TreeMap<>();
            HashSet<Long> tempgrop = GROP_IGNORE_ONE.get(tempgropid);
            for (Long tempuserid : tempgrop) {
                tempcount.put(tempuserid, 0);
            }
            DENY_GROP_COUNT.put(tempgropid, tempcount);
        }

        return true;

    }

    @Override
    public boolean boot() throws Exception {
        return true;
    }

    @Override
    public boolean save() throws Exception {
        return true;
    }

    @Override
    public boolean shut() throws Exception {
        return true;
    }

    @Override
    public String[] exec(Message message) throws Exception {
        return new String[]{
                "此模块无可用命令"
        };
    }

    @Override
    public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
    }

    @Override
    public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
    }

    @Override
    public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {

        if (USER_IGNORE.contains(userid)) {

            DENY_USER_COUNT.put(userid, DENY_USER_COUNT.get(userid) + 1);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_DENY_USER, true), StandardCharsets.UTF_8));
            writer.write(message.toString());
            writer.write("\r\n\r\n\r\n\r\n");
            writer.flush();
            writer.close();

            return true;

        } else {

            return false;

        }
    }

    @Override
    public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

        if (USER_IGNORE.contains(userid)) {

            DENY_USER_COUNT.put(userid, DENY_USER_COUNT.get(userid) + 1);

        } else if (DISZ_IGNORE.contains(diszid) && DISZ_IGNORE_ONE.get(diszid).contains(userid)) {

            TreeMap<Long, Integer> temp = DENY_DISZ_COUNT.get(diszid);
            temp.put(userid, temp.get(userid) + 1);

        } else {

            return false;

        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_DENY_DISZ, true), StandardCharsets.UTF_8));
        writer.write(message.toString());
        writer.write("\r\n\r\n\r\n\r\n");
        writer.flush();
        writer.close();

        return true;
    }

    @Override
    public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

        if (USER_IGNORE.contains(userid)) {

            DENY_USER_COUNT.put(userid, DENY_USER_COUNT.get(userid) + 1);

        } else if (GROP_IGNORE.contains(gropid) && GROP_IGNORE_ONE.get(gropid).contains(userid)) {

            TreeMap<Long, Integer> temp = DENY_GROP_COUNT.get(gropid);
            temp.put(userid, temp.get(userid) + 1);

        } else {

            return false;

        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_DENY_GROP, true), StandardCharsets.UTF_8));
        writer.write(message.toString());
        writer.write("\r\n\r\n\r\n\r\n");
        writer.flush();
        writer.close();

        return true;
    }

    @Override
    public String[] generateReport(int mode, Message message, Object... parameters) {

        COUNT_USER = 0;
        COUNT_DISZ = 0;
        COUNT_GROP = 0;

        for (long userid : DENY_USER_COUNT.keySet()) {
            COUNT_USER = COUNT_USER + DENY_USER_COUNT.get(userid);
        }
        for (long diszid : DENY_DISZ_COUNT.keySet()) {
            TreeMap<Long, Integer> disz = DENY_DISZ_COUNT.get(diszid);
            for (long userid : disz.keySet()) {
                COUNT_DISZ = COUNT_DISZ + disz.get(userid);
            }
        }
        for (long gropid : DENY_GROP_COUNT.keySet()) {
            TreeMap<Long, Integer> grop = DENY_GROP_COUNT.get(gropid);
            for (long userid : grop.keySet()) {
                COUNT_GROP = COUNT_GROP + grop.get(userid);
            }
        }

        if ((COUNT_USER == 0) && (COUNT_DISZ == 0) && (COUNT_GROP == 0)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        if (COUNT_USER == 0) {
            builder.append("拦截私聊：0");
        } else {
            builder.append("拦截私聊：");
            builder.append(COUNT_USER);
            for (long userid : DENY_USER_COUNT.keySet()) {
                builder.append("\r\n");
                builder.append(entry.getNickname(userid));
                builder.append(" (");
                builder.append(userid);
                builder.append(") ");
                builder.append(DENY_USER_COUNT.get(userid));
            }
        }

        if (COUNT_DISZ == 0) {
            builder.append("\r\n拦截组聊：0");
        } else {
            builder.append("\r\n拦截组聊：");
            builder.append(COUNT_DISZ);
            builder.append("\r\n");
            for (long diszid : DENY_DISZ_COUNT.keySet()) {
                TreeMap<Long, Integer> disz = DENY_DISZ_COUNT.get(diszid);
                builder.append("组号：");
                builder.append(diszid);
                for (long userid : disz.keySet()) {
                    builder.append("\r\n");
                    builder.append(entry.getNickname(userid));
                    builder.append(" (");
                    builder.append(userid);
                    builder.append(") ");
                    builder.append(disz.get(userid));
                }
            }
        }

        if (COUNT_GROP == 0) {
            builder.append("\r\n拦截群聊：0");
        } else {
            builder.append("\r\n拦截群聊：");
            builder.append(COUNT_GROP);
            builder.append("\r\n");
            for (long gropid : DENY_GROP_COUNT.keySet()) {
                TreeMap<Long, Integer> grop = DENY_GROP_COUNT.get(gropid);
                builder.append(" 群号：");
                builder.append(gropid);
                for (long userid : grop.keySet()) {
                    builder.append("\r\n");
                    builder.append(entry.getNickname(userid));
                    builder.append(" (");
                    builder.append(userid);
                    builder.append(") ");
                    builder.append(grop.get(userid));
                }
            }
        }
        String[] res = new String[]{
                builder.toString()
        };
        return res;
    }

}
