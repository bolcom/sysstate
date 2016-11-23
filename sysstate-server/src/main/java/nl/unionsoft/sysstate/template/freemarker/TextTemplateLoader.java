package nl.unionsoft.sysstate.template.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import freemarker.cache.TemplateLoader;
import nl.unionsoft.sysstate.common.dto.TextDto;
import nl.unionsoft.sysstate.common.logic.TextLogic;

@Named("textTemplateLoader")
public class TextTemplateLoader implements TemplateLoader {

    private TextLogic textLogic;

    @Inject
    public TextTemplateLoader(TextLogic textLogic) {
        this.textLogic = textLogic;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {

        Optional<TextDto> text = textLogic.getText(name);
        if (text.isPresent()) {
            return text.get();
        }
        return null;

    }

    @Override
    public long getLastModified(Object templateSource) {
        if (!TextDto.class.isAssignableFrom(templateSource.getClass())) {
            throw new IllegalArgumentException("templateSource is not assignable from TextDto.");
        }
        TextDto textDto = (TextDto) templateSource;
        Date lastUpdated = textDto.getLastUpdated();
        if (lastUpdated == null) {
            return 0;
        }
        return lastUpdated.getTime();
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if (!TextDto.class.isAssignableFrom(templateSource.getClass())) {
            throw new IllegalArgumentException("templateSource is not assignable from TextDto.");
        }
        TextDto textDto = (TextDto) templateSource;
        return new StringReader(textDto.getText());
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // Nothing to do here, move along..
    }

}
