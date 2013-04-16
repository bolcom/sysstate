package nl.unionsoft.sysstate.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.converter.EnvironmentConverter;
import nl.unionsoft.sysstate.domain.Environment;

public class EnvironmentConverterTest {
    EnvironmentConverter environmentConverter;

    @Before
    public void before() {
        environmentConverter = new EnvironmentConverter();
    }

    @Test
    public void testConverter() {
        Environment environment = new Environment();
        environment.setDefaultInstanceTimeout(1000);
        environment.setId(1L);
        environment.setName("Harry");
        environment.setOrder(10);
        EnvironmentDto result = environmentConverter.convert(environment);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), environment.getName());
        Assert.assertEquals(result.getId(), environment.getId());
    }
}
