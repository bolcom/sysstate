package nl.unionsoft.sysstate.logic.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.logic.TextLogic;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.dao.impl.TextDao;
import nl.unionsoft.sysstate.domain.Text;

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

    public Optional<TextDto> getText(String name) {
        return OptionalConverter.convert(textDao.getText(name), textConverter);
    }

    public void createOrUpdateText(TextDto dto) {
        final Text text = new Text();
        text.setTags(dto.getTags());
        text.setName(dto.getName());
        text.setText(dto.getText());
        text.setLastUpdated(new Date());
        textDao.createOrUpdateText(text);
    }

    public void delete(String name) {
        textDao.delete(name);
    }

    @Override
    public List<TextDto> getTexts(String... tags) {
        return ListConverter.convert(textConverter, textDao.getTexts(tags));
    }

}
