package nl.unionsoft.sysstate.web.rest.converter;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.sysstate_1_0.Text;

@Service("restTextConverter")
public class TextConverter implements Converter<Text, TextDto> {

    @Override
    public Text convert(TextDto dto) {
        if (dto == null) {
            return null;
        }

        Text resource = new Text();
        resource.setName(dto.getName());
        resource.setText(dto.getText());
        resource.getTags().addAll(Arrays.asList(StringUtils.split(dto.getTags()," ")));
        return resource;
    }

}
