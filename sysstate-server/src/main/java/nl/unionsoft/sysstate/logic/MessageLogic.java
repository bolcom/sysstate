package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.dto.MessageDto;

public interface MessageLogic {

    public List<MessageDto> getMessages();

    public void addUserMessage(MessageDto messageDto);
}
