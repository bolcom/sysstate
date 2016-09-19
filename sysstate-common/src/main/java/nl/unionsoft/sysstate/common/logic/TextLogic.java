package nl.unionsoft.sysstate.common.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.common.dto.TextDto;

public interface TextLogic {
    public List<TextDto> getTexts();
    
    public List<TextDto> getTexts(String... tags);

    public Optional<TextDto> getText(String name);

    public void createOrUpdateText(final TextDto text);

    public void delete(final String name);
}
