package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.logic.TextLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Text;
import nl.unionsoft.sysstate.sysstate_1_0.TextList;
import nl.unionsoft.sysstate.web.rest.converter.TextConverter;

@Controller()
public class TextRestController {

    @Inject
    private TextLogic textLogic;

    @Inject
    private TextConverter textConverter;

    @RequestMapping(value = "/text", method = RequestMethod.GET)
    public TextList getList() {
        List<Text> texts = ListConverter.convert(textConverter, textLogic.getTexts());
        TextList list = new TextList();
        list.getTexts().addAll(texts);
        return list;
    }

    @RequestMapping(value = "/text/{name}", method = RequestMethod.GET)
    public Text get(@PathVariable("name") final String name) {
        Optional<TextDto> text = textLogic.getText(name);
        if (!text.isPresent()){
            throw new IllegalStateException("Text with name [" + name + "] cannot be found");  
        }
        return textConverter.convert(text.get());
        

    }
    
    @RequestMapping(value = "/text", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Text text) {
        TextDto dto = new TextDto();
        dto.setName(text.getName());
        dto.setTags(StringUtils.join(text.getTags()," "));
        dto.setText(text.getText());
        textLogic.createOrUpdateText(dto);
    }

    @RequestMapping(value = "/text/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("name") final String name) {
        textLogic.delete(name);
    }
}
