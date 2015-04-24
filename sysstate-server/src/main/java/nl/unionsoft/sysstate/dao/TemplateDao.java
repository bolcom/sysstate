package nl.unionsoft.sysstate.dao;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.domain.Template;

public interface TemplateDao {

    public Optional<Template> getTemplate(String templateName);

    public void createOrUpdate(Template template);

    public List<Template> getTemplates();

    public void delete(String templateName);

}
