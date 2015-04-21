<table class="state_table" >
	<colgroup id="cg_prj"/>
	<#list viewResult.environments as environment>
		<colgroup id="cg_env_${environment.id}" class="cg_env"/>
	</#list>
	<thead class="head">
		<tr class="row_env">
			<th id="col_prj" class="top_left"><a href="${contextPath}/manager/index.html" >PRJ/ENV</a> </th>
			<#list viewResult.environments as environment>
				<th id="col_env_${environment.id}" class="col_env <#if !environment_has_next>top_right</#if>"><a href="${contextPath}/filter/environment/${environment.id}/index.html" >${environment.name}</a></th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list viewResult.projects as project>
			<tr id="row_prj_${project.id}" class="row_prj <#if project_index % 2 == 0>row_prj_odd<#else>row_prj_even</#if>">
				<th class="row_prj_name">
					<a href="${contextPath}/filter/project/${project.id}/index.html" >
						${project.name}
					</a>
				</th>
				<#list viewResult.environments as environment>
					
					<#list viewResult.projectEnvironments as projectEnvironment>
						<#if projectEnvironment.project.id == project.id && projectEnvironment.environment.id == environment.id>
							<td id="row_prj_${project.id}_env_${environment.id}" class="row_prj_env state_${projectEnvironment.state!'PENDING'}">
								<div class="row_prj_env" style="position: relative;" >
									<div">
										<a class="inline" href="${contextPath}/projectEnvironment/project/${project.id}/environment/${environment.id}/details.html" title="Configuration">
											<span class="row_prj_env_int_state state_${projectEnvironment.state}"><img src="${contextPath}/images/transparant.gif"/></span>
											<c:set var="styleRating" value=""/>
											<c:if test="${properties['no_weather'] != 'true'}">
												<c:choose>
													<c:when test="${fn:length(instances) == 0 || projectEnvironment.rating  < 0 }">
														<c:set var="styleRating" value=""/>
													</c:when>
													<c:when test="${projectEnvironment.rating < 25 }">
														<c:set var="styleRating" value="rating_0"/>	
													</c:when>
													<c:when test="${projectEnvironment.rating < 50 }">
														<c:set var="styleRating" value="rating_25"/>	
													</c:when>
													<c:when test="${projectEnvironment.rating < 75 }">
														<c:set var="styleRating" value="rating_50"/>	
													</c:when>
													<c:when test="${projectEnvironment.rating < 100 }">
														<c:set var="styleRating" value="rating_75"/>	
													</c:when>
													<c:when test="${projectEnvironment.rating == 100 }">
														<c:set var="styleRating" value="rating_100"/>	
													</c:when>
												</c:choose>
												<span class="row_prj_env_int_rating ${styleRating}"><img src="${contextPath}/images/transparant.gif"/></span>
											</c:if>
											
											<span class="env_prj_name">${project.name}</span>
											
											<c:choose>
												<c:when test="${fn:length(instances) == 0 }">
													None
												</c:when>
												<c:when test="${not empty projectEnvironment.description}">
													${projectEnvironment.description}
												</c:when>
												<c:otherwise>
													(<font class="state_STABLE">${projectEnvironment.count.stable}</font>/<font class="state_UNSTABLE">${projectEnvironment.count.unstable}</font>/<font class="state_ERROR">${projectEnvironment.count.error}</font>/<font class="state_DISABLED">${projectEnvironment.count.disabled}</font>/<font class="state_PENDING">${projectEnvironment.count.pending}</font>)
												</c:otherwise>
											</c:choose>
										</a>
									</div>
								</div>
							</td>
						</#if>
					</#list>
				</#list>	
			</tr>
		</#list>			
	</tbody>
	<tfoot class="foot">
		<tr>
			<td class="bottom_left" colspan="${viewResult.environments?size}">
			<td class="bottom_right">&nbsp;</td>
		</tr>
	</tfoot>		
	
</table>
