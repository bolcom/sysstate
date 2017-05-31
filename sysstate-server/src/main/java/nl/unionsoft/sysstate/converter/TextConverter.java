package nl.unionsoft.sysstate.converter;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.domain.Text;

import org.springframework.stereotype.Service;

@Service("textConverter")
public class TextConverter implements Converter<TextDto, Text> {

    public TextDto convert(Text text) {
        TextDto result = null;
        if (text != null) {
            result = new TextDto();
            result.setTags(text.getTags());
            result.setText(text.getText());
            result.setName(text.getName());
            result.setLastUpdated(text.getLastUpdated());
        }
        return result;
    }
}
