package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Template;

public interface TemplateDao {

    public Template getTemplate(String templateId);

    public void createOrUpdate(Template template);

    public List<Template> getTemplates();

    public void delete(String templateId);

}
