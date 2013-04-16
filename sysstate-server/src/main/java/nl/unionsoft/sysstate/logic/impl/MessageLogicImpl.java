package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.springframework.stereotype.Service;

@Service("messageLogic")
public class MessageLogicImpl implements MessageLogic {

    @Inject
    @Named("userLogic")
    private UserLogic userLogic;

    private final Map<Long, List<MessageDto>> userMessages;

    public MessageLogicImpl () {
        userMessages = new HashMap<Long, List<MessageDto>>();
    }

    public List<MessageDto> getMessages() {
        final List<MessageDto> results = new ArrayList<MessageDto>();
        final UserDto currentUser = userLogic.getCurrentUser();
        if (currentUser != null) {
            final List<MessageDto> messages = userMessages.get(currentUser.getId());
            if (messages != null) {
                results.addAll(messages);
                messages.clear();
            }
        }
        return results;
    }

    public void addUserMessage(MessageDto messageDto) {
        final UserDto currentUser = userLogic.getCurrentUser();
        if (currentUser != null) {
            List<MessageDto> messages = userMessages.get(currentUser.getId());
            if (messages == null) {
                messages = new ArrayList<MessageDto>();
                userMessages.put(currentUser.getId(), messages);
            }
            messages.add(messageDto);
        }
    }

}
