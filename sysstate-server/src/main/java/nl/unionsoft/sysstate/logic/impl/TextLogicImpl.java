package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.logic.TextLogic;
import nl.unionsoft.sysstate.dao.impl.TextDao;
import nl.unionsoft.sysstate.domain.Text;

import org.springframework.stereotype.Service;

@Service("textLogic")
public class TextLogicImpl implements TextLogic {

    @Inject
    @Named("textDao")
    private TextDao textDao;

    @Inject
    @Named("textConverter")
    private Converter<TextDto, Text> textConverter;

    public List<TextDto> getTexts() {
        return ListConverter.convert(textConverter, textDao.getTexts());
    }

    public TextDto getText(Long textId) {
        return textConverter.convert(textDao.getText(textId));
    }

    public void createOrUpdateText(TextDto dto) {
        final Text text = new Text();
        text.setId(dto.getId());
        text.setTags(dto.getTags());
        text.setName(dto.getName());
        text.setText(dto.getText());
        textDao.createOrUpdateText(text);
    }

    public void delete(Long textId) {
        textDao.delete(textId);
    }

    @Override
    public List<TextDto> getTexts(String... tags) {
        return ListConverter.convert(textConverter, textDao.getTexts(tags));
    }

}
