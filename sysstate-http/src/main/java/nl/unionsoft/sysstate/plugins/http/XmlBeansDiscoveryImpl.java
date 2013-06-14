package nl.unionsoft.sysstate.plugins.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import nl.unionsoft.sysstate.common.dto.InstanceDto;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

public class XmlBeansDiscoveryImpl extends HttpDiscoveryImpl {

    private static final Logger LOG = LoggerFactory.getLogger(XmlBeansDiscoveryImpl.class);

    @Autowired
    private XmlBeansMarshaller xmlBeansMarshaller;

    @Override
    public Collection<? extends InstanceDto> handleEntity(HttpEntity httpEntity, Properties configuration) throws IOException {
        Collection<? extends InstanceDto> result = null;
        InputStream contentStream = null;
        try {
            if (httpEntity != null) {
                contentStream = httpEntity.getContent();
                // Unmarshal - Convert to Node
                final XmlObject xmlObject = (XmlObject) xmlBeansMarshaller.unmarshal(new StreamSource(contentStream));
                result = handleXmlObject(xmlObject, configuration);
            }
        } catch(final IllegalStateException e) {
            LOG.error("Caught IllegalStateException", e);

        } catch(final IOException e) {
            LOG.error("Caught IOException", e);
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
        return result;
    }

    public Collection<? extends InstanceDto> handleXmlObject(XmlObject xmlObject, Properties configuration) {
        return null;
    }

    public XmlBeansMarshaller getXmlBeansMarshaller() {
        return xmlBeansMarshaller;
    }

    public void setXmlBeansMarshaller(XmlBeansMarshaller xmlBeansMarshaller) {
        this.xmlBeansMarshaller = xmlBeansMarshaller;
    }

}
