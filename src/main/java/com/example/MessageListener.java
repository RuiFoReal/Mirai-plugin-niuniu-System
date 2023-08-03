package com.example;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import static com.example.DatabaseUtils.*;
import static com.example.QQrobot.*;
import static com.example.TaskType.搞对象;

public class MessageListener extends SimpleListenerHost {
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        exception.printStackTrace();
        // 处理事件处理时抛出的异常
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) throws Exception {// 可以抛出任何异常, 将在 handleException 处理
        String message = event.getMessage().contentToString();
        Member sender = event.getSender();
        Group subject = event.getSubject();
        if (message.equals("L")) {
            subject.sendMessage("厉害");
            return;
        }

        if (message.equals("牛子系统")) {
            subject.sendMessage("牛子养成系统(未开发完成)\n" +
                    "    命令：改牛子名 [要改的名字]  改你的牛子的名字,支持空格,最长10个字\n" +
                    "    命令：搞对象 [@对方]  和别人搞对象\n" +
                    "    命令：贴贴！   和对象贴贴！\n" +
                    "    命令：我的对象   查看你的对象的牛子信息\n" +
                    "    命令：变女性   转变为女性,扣除50厘米\n" +
                    "    命令：处理请求 [搞对象/分手] [同意/不同意]  管理你的请求\n" +
                    "    命令：我的牛子   查看你的牛子\n" +
                    "    命令：我要分手   和你的对象分手\n" +
                    "    命令：比划比划 [@对方]  比划一下，赢加长度输减长度，断掉双方都减长度\n" +
                    "    命令：群牛子排行   查看牛子排行榜(此命令意义不明 使用牛子榜替代)\n" +
                    "    命令：领养牛子   领养一只牛子\n" +
                    "    命令：牛子榜   查看牛子排行榜");
            return;
        }

        if (message.equals("牛子榜")) {
            ResultSet resultSet1 = QueryAllData();
            ForwardMessageBuilder messageBuilder = new ForwardMessageBuilder(event.getGroup());
            while (resultSet1.next()) {
                Member member = getMember(event.getGroup(), Long.parseLong(resultSet1.getString("qq")));
                if (member == null) continue;
                String name = member.getNameCard();
                if(name.isEmpty()){
                    messageBuilder.add(3076716686L, "mckLLL", new PlainText(String.format("%s[主人:(%s)]:%f厘米", resultSet1.getString("name"), member.getNick(), resultSet1.getFloat("long"))));
                    continue;
                }
                messageBuilder.add(3076716686L, "mckLLL", new PlainText(String.format("%s[主人:(%s)]:%f厘米", resultSet1.getString("name"), name, resultSet1.getFloat("long"))));
            }
            if (messageBuilder.size()==0){
                return;
            }
            subject.sendMessage(messageBuilder.build());
            resultSet1.close();
            return;
        }
        if (message.equals("我的牛子")) {
            ResultSet resultSet1 = QueryOneUserData(sender.getId());
            if (!resultSet1.next()) {
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "没有牛子你查什么查滚");
                return;
            }
            if (sender.getNameCard().isEmpty()){
                subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                        "主人:%s\n" +
                        "名称:%s\n" +
                        "性别:%s\n" +
                        "长度:%f厘米", sender.getNick(), resultSet1.getString("name"), resultSet1.getString("sex"), resultSet1.getFloat("long")));
                return;
            }

            subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                    "主人:%s\n" +
                    "名称:%s\n" +
                    "性别:%s\n" +
                    "长度:%f厘米", sender.getNameCard(), resultSet1.getString("name"), resultSet1.getString("sex"), resultSet1.getFloat("long")));
            resultSet1.close();
            return;
        }
        if (message.equals("领养牛子")) {
            if (CheckUserExists(sender.getId())) {
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "？你有了你还领，有病");
                return;
            }
            if (sender.getNameCard().isEmpty()){
                InsertUser(sender.getId(), sender.getNick());
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "行了行了行了");
                return;
            }
            InsertUser(sender.getId(), sender.getNameCard());
            subject.sendMessage("---== 牛子系统 ==---\n" +
                    "行了行了行了");
            return;
        }
        if (message.contains("改牛子名")) {
            String[] s = message.split(" ");
            if (s.length != 2) {
                return;
            }
            String name = s[1];
            if (name.isEmpty() || name.length() > 10) {
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你牛子名字太长了,最多只支持10个字");
                return;
            }
            if (!CheckUserExists(sender.getId())) {
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你没有牛子你在这你想干什么啊");
                return;
            }
            UpdateUserName(sender.getId(),name);
            subject.sendMessage("---== 牛子系统 ==---\n" +
                    "行了行了行了");
            return;
        }

        if (message.contains("比划比划")&&message.contains("@")) {
            long atUserQQ = getAtUserQQ(message);
            if (atUserQQ==-1){
                return;
            }
            if (atUserQQ==sender.getId()){
                return;
            }

            if (!CheckUserExists(sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你没有牛子你在这你想干什么啊");
                return;
            }

            if (!CheckUserExists(atUserQQ)){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "真可惜！你选的比划对象人家没有牛子");
                return;
            }
            Member member = getMember(event.getGroup(), atUserQQ);
            if (member==null){
                return;
            }
            if (CheckUserCoolDownTimeByBiHua(sender.getId())) {
                subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                        "你牛子红肿了，等 %d 时。",QueryUserBiHuaCoolDownTime(sender.getId())));
                return;
            }

            if (CheckUserCoolDownTimeByBiHua(atUserQQ)) {
                subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                        "对方牛子红肿了，等 %d 时。",QueryUserBiHuaCoolDownTime(atUserQQ)));
                return;
            }



            Random random = new Random();
            float damage = random.nextFloat()*1000;
            if (random.nextInt(1000)>500){
                UpdateLong(sender.getId(),damage,"+");
                UpdateLong(atUserQQ,damage,"-");
                subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                        "%s和%s开始比划牛子，赢到了 %f 厘米。",sender.getNameCard(), member.getNameCard(),damage));
            }else {
                UpdateLong(sender.getId(),damage,"-");
                UpdateLong(atUserQQ,damage,"+");
                subject.sendMessage(String.format("---== 牛子系统 ==---\n" +
                        "%s和%s开始比划牛子，输了 %f 厘米。",sender.getNameCard(), member.getNameCard(),damage));
            }
            int hour = random.nextInt(6);
            bihua_colddown_and_user.put(atUserQQ,hour);
            bihua_colddown_and_user.put(sender.getId(),hour);
            return;
        }

        if (message.equals("变女性")) {
            if (!CheckUserExists(sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你没有牛子你在这你想干什么啊");
                return;
            }
            if (CheckUserIsFemale(sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你已经是女的了，怎么？");
                return;
            }
            UpdateUserFemale(sender.getId());
            subject.sendMessage("---== 牛子系统 ==---\n" +
                    "行了行了行了");
            return;
        }

        if (message.contains("搞对象")&&message.contains("@")) {
            long atUserQQ = getAtUserQQ(message);
            if (atUserQQ==-1){
                return;
            }
            if (atUserQQ==sender.getId()){
                return;
            }

            if (!CheckUserExists(sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你没有牛子你在这你想干什么啊");
                return;
            }

            if (!CheckUserExists(atUserQQ)){
                subject.sendMessage("---== 对象系统 ===--\n" +
                        "真可惜!Ta没有牛子");
                return;
            }

            if (CheckUserHasMate(atUserQQ)) {
                subject.sendMessage("---== 对象系统 ===--\n" +
                        "真可惜！人家有对象了");
                return;
            }

            if (CheckUserHasMate(sender.getId())) {
                subject.sendMessage("---== 对象系统 ===--\n" +
                        "你有对象了你还找对象？");
                return;
            }
            for (Task task : tasks) {
                if (task.getTaskType().equals(搞对象)&&task.getTarget_qq()==atUserQQ){
                    subject.sendMessage("---== 对象系统 ===--\n" +
                            "已存在请求，可能是别人发的");
                    return;
                }
            }
            Member member = getMember(event.getGroup(), atUserQQ);
            if (member==null){
                return;
            }

            tasks.add(new Task(sender.getId(),搞对象,30,atUserQQ));
            subject.sendMessage("---== 对象系统 ===--\n" +
                    new At(atUserQQ).contentToString()+" 你好，"+new At(sender.getId()).contentToString()+"想跟你搞对象\n" +
                    "输入命令「处理请求 搞对象 同意/不同意」");
        }

        if (message.contains("处理请求")) {
            String[] strings = message.split(" ");
            if (strings.length!=3) {
                return;
            }
            TaskType taskType = TaskType.valueOf(strings[1]);
            if (!CheckTaskExists(taskType,sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "没有待处理的请求");
                return;
            }
            String decision=strings[2];
            if (taskType.equals(搞对象)) {
                long target = getQQbyUser(搞对象, sender.getId());
                switch (decision){
                    case "同意":
                        UpdateUserMate(sender.getId(),target);
                        UpdateUserMate(target,sender.getId());
                        RemoveByTargetQQ(taskType,sender.getId());
                        subject.sendMessage("---== 对象系统 ===--\n" +
                                new At(target)+" 恭喜！！！！对方同意了你的请求");
                        return;
                    case "不同意":
                        subject.sendMessage("---== 对象系统 ===--\n" +
                                new At(target)+" 对方没有同意你的请求");
                        return;
                    default:
                        return;
                }
            }

        }

        if (message.equals("我的对象")) {
            if (!CheckUserExists(sender.getId())) {
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "没有牛子你查什么查滚");
                return;
            }
            if (!CheckUserHasMate(sender.getId())) {
                subject.sendMessage("---== 对象系统 ==---\n" +
                        "你没有对象你在这叭叭什么？");
                return;
            }
            long mate = QueryUserMate(sender.getId());
            ResultSet resultSet1 = QueryOneUserData(mate);
            if (sender.getNameCard().isEmpty()){
                subject.sendMessage(String.format("---== 对象系统 ==---\n" +
                        "你的对象：%s\n" +
                        "Ta的牛子：%s\n" +
                        "牛子性别：%s\n" +
                        "牛子长度：%f厘米", Objects.requireNonNull(getMember(event.getGroup(), mate)).getNameCard(), resultSet1.getString("name"), resultSet1.getString("sex"), resultSet1.getFloat("long")));
                return;
            }

            subject.sendMessage(String.format("---== 对象系统 ==---\n" +
                    "你的对象：%s\n" +
                    "Ta的牛子：%s\n" +
                    "牛子性别：%s\n" +
                    "牛子长度：%f厘米", Objects.requireNonNull(getMember(event.getGroup(), mate)).getNameCard(), resultSet1.getString("name"), resultSet1.getString("sex"), resultSet1.getFloat("long")));
            resultSet1.close();
            return;
        }

        if (message.equals("贴贴！")) {
            if (!CheckUserExists(sender.getId())){
                subject.sendMessage("---== 牛子系统 ==---\n" +
                        "你没有牛子你在这你想干什么啊");
                return;
            }
            if (!CheckUserHasMate(sender.getId())) {
                subject.sendMessage("---== 对象系统 ==---\n" +
                        "你没有对象你在这叭叭什么？");
                return;
            }
            long mate = QueryUserMate(sender.getId());
            if (CheckUserCoolDownTimeByTieTie(mate)||CheckUserCoolDownTimeByTieTie(sender.getId())) {
                subject.sendMessage(String.format("---== 对象系统 ===--\n" +
                        "你俩能不能消停会儿 都粘掉皮了 等 %d 时 再贴",QueryUserTieTieCoolDownTime(mate)));
                return;
            }

            Random random = new Random();
            float i = random.nextFloat()*1000;
            int hour = random.nextInt(6);
            UpdateLong(sender.getId(),i,"+");
            UpdateLong(mate,i,"+");
            subject.sendMessage(String.format("---== 对象系统 ==---\n" +
                    "行行行 贴贴贴 一会儿粘上了加了 %f 厘米，但你俩已经虚了，所以你们得等 %d 小时 后才可以再次贴贴",i,hour));
            tietie_colddown_and_user.put(mate,hour);
            tietie_colddown_and_user.put(sender.getId(),hour);
            return;
        }


        System.out.println("-----------------");
        // 无返回值, 表示一直监听事件
    }

    public static long getAtUserQQ(String message) {
        if (message.contains("@")) {
            int begin = message.indexOf("@") + 1;
            for (int i = begin; i < message.length(); i++) {
                if (!Character.isDigit(message.charAt(i))) return Long.parseLong(message.substring(begin, i));
            }
            return Long.parseLong(message.substring(begin));
        }
        return -1;
    }

    public static Member getMember(Group group, long qq) {
        for (NormalMember member : group.getMembers()) {
            if (member.getId() == qq) {
                return member;
            }
        }
        return null;
    }

    public static boolean CheckTaskExists(TaskType taskType,long qq){
        for (Task task : tasks) {
            if (task.getTaskType().equals(taskType)&&qq==task.getTarget_qq()){
                return true;
            }
        }
        return false;
    }

    public static long getQQbyUser(TaskType taskType,long qq){
        for (Task task : tasks) {
            if (task.getTaskType().equals(taskType)&&qq==task.getTarget_qq()){
                return task.getUser_qq();
            }
        }
        return -1;
    }

    public static void RemoveByTargetQQ(TaskType taskType,long qq) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getTarget_qq()==qq&&task.getTaskType().equals(taskType)){
                iterator.remove();
                return;
            }
        }
    }
    public static boolean CheckUserCoolDownTimeByTieTie(long qq){
        return tietie_colddown_and_user.containsKey(qq);
    }

    public static int QueryUserTieTieCoolDownTime(long qq){
        return tietie_colddown_and_user.get(qq);
    }

    public static boolean CheckUserCoolDownTimeByBiHua(long qq){
        return bihua_colddown_and_user.containsKey(qq);
    }

    public static int QueryUserBiHuaCoolDownTime(long qq){
        return bihua_colddown_and_user.get(qq);
    }
}