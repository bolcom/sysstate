package nl.unionsoft.sysstate.converter;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.domain.Text;

import org.springframework.stereotype.Service;

@Service("textConverter")
public class TextConverter implements Converter<TextDto, Text> {

    public TextDto convert(Text text) {
        TextDto result = null;
        if (text != null) {
            result = new TextDto();
            result.setId(text.getId());
            result.setTags(text.getTags());
            result.setText(text.getText());
            result.setName(text.getName());
        }
        return result;
    }
}
