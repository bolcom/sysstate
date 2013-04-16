package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.TextDto;

public interface TextLogic {
    public List<TextDto> getTexts();

    public TextDto getText(final Long textId);

    public void createOrUpdateText(final TextDto text);

    public void delete(final Long textId);
}
