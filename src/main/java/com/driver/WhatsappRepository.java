package com.driver;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class WhatsappRepository {

    private HashMap<Group, List<User>> groupUserMap = new HashMap<>();
    private HashMap<Group, List<Message>> groupMessageMap = new HashMap<>();
    private HashMap<Message, User> senderMap = new HashMap<>();
    private HashMap<Group, User> adminMap = new HashMap<>();
    private HashSet<String> userMobile = new HashSet<>();
    private int customGroupCount = 0;
    private int messageId = 0;

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile)) throw new Exception("User already exists");
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        Group group = new Group();
        if(users.size() == 2) {
            group.name = users.get(1).name;
            group.numberOfParticipants = 2;
            adminMap.put(group, users.get(0));
        } else {
            customGroupCount++;
            group.name = "Group " + customGroupCount;
            group.numberOfParticipants = users.size();
            adminMap.put(group, users.get(0));
        }
        groupUserMap.put(group, users);
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        Message message = new Message();
        message.id = messageId;
        message.content = content;
        message.timestamp = new Date();
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!groupUserMap.get(group).contains(sender)) throw new Exception("You are not allowed to send message");

        senderMap.put(message, sender);
        groupMessageMap.putIfAbsent(group, new ArrayList<>());
        groupMessageMap.get(group).add(message);

        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
        if(!groupUserMap.get(group).contains(user)) throw new Exception("User is not a participant");

        adminMap.put(group, user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        Group targetGroup = null;

        for(Group group : groupUserMap.keySet()) {
            if(groupUserMap.get(group).contains(user)) {
                if(adminMap.get(group).equals(user)) throw new Exception("Cannot remove admin");
                targetGroup = group;
                break;
            }
        }

        if(targetGroup == null) throw new Exception("User not found");

        groupUserMap.get(targetGroup).remove(user);

        List<Message> toRemove = new ArrayList<>();
        for(Message msg : senderMap.keySet()) {
            if(senderMap.get(msg).equals(user)) toRemove.add(msg);
        }

        for(Message msg : toRemove) {
            senderMap.remove(msg);
            groupMessageMap.get(targetGroup).remove(msg);
        }

        return groupUserMap.get(targetGroup).size() +
               groupMessageMap.get(targetGroup).size() +
               senderMap.size();
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messages = new ArrayList<>();
        for(List<Message> list : groupMessageMap.values()) {
            for(Message msg : list) {
                if(msg.timestamp.after(start) && msg.timestamp.before(end)) messages.add(msg);
            }
        }

        messages.sort((a, b) -> b.timestamp.compareTo(a.timestamp));

        if(messages.size() < K) throw new Exception("K is greater than the number of messages");
        return messages.get(K-1).content;
    }
}

