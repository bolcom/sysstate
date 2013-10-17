package nl.unionsoft.sysstate.plugins.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;
import org.springframework.stereotype.Service;

@Service("xmlBeanStateResolver")
public abstract class XMLBeanStateResolverImpl extends HttpStateResolverImpl {
    private static final Logger LOG = LoggerFactory.getLogger(XMLBeanStateResolverImpl.class);

    @Autowired
    private XmlBeansMarshaller xmlBeansMarshaller;

    @Override
    public void handleEntity(final HttpEntity httpEntity, final  Map<String, String> configuration, final StateDto state) throws IOException {

        InputStream contentStream = null;
        try {
            if (httpEntity != null) {
                contentStream = httpEntity.getContent();
                // Unmarshal - Convert to Node
                final XmlObject xmlObject = (XmlObject) xmlBeansMarshaller.unmarshal(new StreamSource(contentStream));
                // FIXME
                handleXmlObject(xmlObject, state, configuration);
            }
        } catch (final IllegalStateException e) {
            LOG.error("Caught IllegalStateException", e);
            state.setState(StateType.ERROR);
            state.setDescription(e.getMessage());

        } catch (final IOException e) {
            LOG.error("Caught IOException", e);
            state.setState(StateType.ERROR);
            state.setDescription(e.getMessage());
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    protected abstract void handleXmlObject(final XmlObject xmlObject, final StateDto state,  Map<String, String> configuration);

    public XmlBeansMarshaller getXmlBeansMarshaller() {
        return xmlBeansMarshaller;
    }

    public void setXmlBeansMarshaller(final XmlBeansMarshaller xmlBeansMarshaller) {
        this.xmlBeansMarshaller = xmlBeansMarshaller;
    }

}
