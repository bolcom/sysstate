package nl.unionsoft.sysstate.logic.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.extending.StateResolver;
import nl.unionsoft.sysstate.common.util.StateUtil;
import nl.unionsoft.sysstate.common.util.SysStateStringUtils;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.converter.StateConverter;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

@Service("stateLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class StateLogicImpl implements StateLogic {

    private static final Logger LOG = LoggerFactory.getLogger(StateLogicImpl.class);

    @Inject
    @Named("stateDao")
    private StateDao stateDao;

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @Inject
    @Named("stateConverter")
    private StateConverter stateConverter;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;
    
    @Scheduled(cron="0 */5 * * * ?")
    public void clean() {
        LOG.info("Cleaning States...");
        Properties sysstateProperties = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);
        String maxDaysToKeepStatesString = sysstateProperties.getProperty("maxDaysToKeepStates");
        int maxDaysToKeepStates = Constants.MAX_DAYS_TO_KEEP_STATES_VALUE;
        if (StringUtils.isNumeric(maxDaysToKeepStatesString)) {
            maxDaysToKeepStates = Integer.valueOf(maxDaysToKeepStatesString);
        }

        if (maxDaysToKeepStates > 0) {
            LOG.info("Max days to keep states is set to: {}", maxDaysToKeepStates);
            stateDao.cleanStatesOlderThanDays(maxDaysToKeepStates);
        } else {
            LOG.info("Max days to keep states <= 0, skipping cleaning...");
        }

    }

    public ListResponse<State> getStates(final ListRequest listRequest) {
        return stateDao.getStates(listRequest);
    }

    public StateDto createOrUpdate(final StateDto dto) {
        if (dto.getInstance() == null) {
            throw new IllegalStateException("State must have an instance!");
        }

        final Long instanceId = dto.getInstance().getId();
        Optional<Instance> optionalInstance = instanceDao.getInstance(instanceId);
        if (!optionalInstance.isPresent()) {
            throw new IllegalStateException("No instance for instanceId [" + instanceId + "] could not be found.");
        }
        Instance instance = optionalInstance.get();
        Optional<State> optionalState = stateDao.getLastStateForInstance(instanceId);
        State state = null;
        if (optionalState.isPresent()) {
            state = optionalState.get();
        }
        final Date stateDate = dto.getCreationDate().toDate();
        if (!match(dto, state)) {
            // @formatter:off
            LOG.debug("State '{}' for instance '{}' changed! old='{}', new='{}'", new Object[] { state, instanceId, dto, state });
            // @formatter:on
            state = new State();
            state.setDescription(dto.getDescription());

            state.setInstance(instance);
            state.setCreationDate(stateDate);
            state.setState(dto.getState());
        } else {
            LOG.debug("State '{}' for instanceId '{}' hasn't changed, updating timestamps, rating & messages only...", state, instanceId);
        }
        state.setMessage(SysStateStringUtils.stripHtml(dto.getMessage()));
        state.setResponseTime(dto.getResponseTime());
        state.setLastUpdate(stateDate);
        state.setRating(dto.getRating());
        stateDao.createOrUpdate(state);
        dto.setId(state.getId());
        return dto;
    }

    public StateDto requestStateForInstance(final InstanceDto instance) {

        final StateDto state = new StateDto();
        state.setInstance(instance);
        ProjectEnvironmentDto projectEnvironment = instance.getProjectEnvironment();
        ProjectDto project = projectEnvironment.getProject();
        EnvironmentDto environment = projectEnvironment.getEnvironment();
        if (!instance.isEnabled()) {
            setDisabled(state, "Disabled by instance");
        } else if (!project.isEnabled()) {
            setDisabled(state, "Disabled by project");
        } else if (!environment.isEnabled()) {
            setDisabled(state, "Disabled by environment");
        } else {
            final String pluginClass = instance.getPluginClass();
            updateState(state, pluginClass, instance);
        }

        return state;
    }

    private void setDisabled(StateDto state, String message) {
        state.setState(StateType.DISABLED);
        state.setDescription("DISABLED");
        state.appendMessage(message);
    }

    private void updateState(StateDto state, String pluginClass, InstanceDto instanceDto) {
        final Long now = System.currentTimeMillis();
        try {
            final StateResolver stateResolver = stateResolverLogic.getStateResolver(pluginClass);
            if (stateResolver == null) {
                throw new IllegalStateException("No stateResolver found for type '" + pluginClass + "'");
            }

            stateResolver.setState(instanceDto, state);
            if (state.getState() == null) {
                throw new IllegalStateException("Result has no state!");
            }
        } catch (final NoSuchBeanDefinitionException e) {
            state.setState(StateType.ERROR);
            state.setDescription("MISSING PLUGIN");
            state.setResponseTime(0L);
            state.appendMessage(StateUtil.exceptionAsMessage(e));
        } catch (final Exception e) {
            LOG.warn("Unable to determine state, caught Exception!", e);
            state.setState(StateType.ERROR);
            state.setDescription(e.getMessage());
            state.setResponseTime(0L);
            state.appendMessage(StateUtil.exceptionAsMessage(e));
        } finally {
            final Long responseTime = System.currentTimeMillis() - now;
            if (state.getResponseTime() < responseTime) {
                state.setResponseTime(responseTime);
            }
            state.setCreationDate(new DateTime());
        }
    }

    public StateDto requestState(final String pluginClass, final Map<String, String> configuration) {
        final StateDto state = new StateDto();
        final InstanceDto instanceDto = new InstanceDto();
        instanceDto.setConfiguration(configuration);
        updateState(state, pluginClass, instanceDto);
        return state;
    }

    public static boolean match(final StateDto dto, final State state) {

        boolean result = false;
        if (state != null) {
            result = StringUtils.equals(dto.getDescription(), state.getDescription()) && dto.getState() == state.getState();

        }
        return result;

    }

    public static boolean match(final StateDto thisState, final StateDto otherState) {

        boolean result = false;
        if (otherState != null) {
            result = StringUtils.equals(thisState.getDescription(), otherState.getDescription()) && thisState.getState() == otherState.getState();

        }
        return result;

    }

    public StateDto getLastStateForInstance(InstanceDto instance) {
        return getLastStateForInstance(instance, StateBehaviour.CACHED);
    }

    @Override
    public StateDto getLastStateForInstance(InstanceDto instance, StateBehaviour stateBehaviour) {
        switch (stateBehaviour) {
            case DIRECT:
                return requestStateForInstance(instance);
            case CACHED:
                Optional<State> optStateDto = stateDao.getLastStateForInstance(instance.getId());
                if (!optStateDto.isPresent()) {
                    return StateDto.PENDING;
                }
                return OptionalConverter.fromOptional(optStateDto, stateConverter, false, StateDto.PENDING);
            default:
                throw new IllegalStateException("Unsupported StateBehaviour [" + stateBehaviour + "]");
        }
    }

    @Override
    public Optional<StateDto> getLastStateForInstance(InstanceDto instance, StateType stateType) {
        return OptionalConverter.convert(stateDao.getLastStateForInstance(instance.getId(), stateType), stateConverter);
    }

    @Override
    public List<StateDto> getLastStateForInstanceForEachType(InstanceDto instance) {
        return Arrays.stream(StateType.values()).parallel()
                .map(st -> getLastStateForInstance(instance, st))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
