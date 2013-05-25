package nl.unionsoft.sysstate.logic;

import nl.unionsoft.sysstate.common.extending.StateResolver;

public interface StateResolverLogic {
    public <T extends StateResolver> T getStateResolver(String name);

    public String[] getStateResolverNames();
}
