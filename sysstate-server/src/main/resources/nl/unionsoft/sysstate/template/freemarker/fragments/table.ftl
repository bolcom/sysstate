<table class="state_table" >
	<colgroup id="cg_prj"/>
	<#list viewResult.environments as environment>
		<colgroup id="cg_env_${environment.id}" class="cg_env"/>
	</#list>
	<thead class="head">
		<tr class="row_env">
			<#if showProjectColumn!true>
				<th id="col_prj" class="top_left"><a href="${baseUrl}/dashboard/index.html" >PRJ/ENV</a> </th>
			</#if>
			
			<#list viewResult.environments as environment>
				<th id="col_env_${environment.id}" class="col_env <#if !environment_has_next>top_right</#if>"><a href="${baseUrl}/filter/environment/${environment.id}/index.html" >${environment.name}</a></th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list viewResult.projects as project>
			<tr id="row_prj_${project.id}" class="row_prj <#if project_index % 2 == 0>row_prj_odd<#else>row_prj_even</#if>">
				<#if showProjectColumn!true>
					<th class="row_prj_name">
						<a href="${baseUrl}/filter/project/${project.id}/index.html" >
							${project.name}
						</a>
					</th>
				</#if>
				<#list viewResult.environments as environment>
					<#assign found=false>
					<#list viewResult.projectEnvironments as projectEnvironment>
						<#if projectEnvironment.project.id == project.id && projectEnvironment.environment.id == environment.id>
							<#assign found=true>
							<td id="row_prj_${project.id}_env_${environment.id}" class="row_prj_env state_${projectEnvironment.state!'PENDING'}">
								<div class="row_prj_env" style="position: relative;" >
									<div">
										<a href="${baseUrl}/projectEnvironment/project/${project.id}/environment/${environment.id}/details.html" title="Configuration">
											<span class="row_prj_env_int_state state_${projectEnvironment.state!'UNKNOWN'}">
											<img src="${baseUrl}/images/transparant.gif"/></span>
											
											<span class="env_prj_name">${project.name}</span>
											<#if projectEnvironment.instances?has_content>
												<#if projectEnvironment.description??>
													${projectEnvironment.description}												
												<#else>
													(<font class="state_STABLE">${projectEnvironment.count.stable}</font>/<font class="state_UNSTABLE">${projectEnvironment.count.unstable}</font>/<font class="state_ERROR">${projectEnvironment.count.error}</font>/<font class="state_DISABLED">${projectEnvironment.count.disabled}</font>/<font class="state_PENDING">${projectEnvironment.count.pending}</font>)
												</#if>
											<#else>
												None
											</#if>
										</a>
									</div>
								</div>
							</td>
						</#if>
					</#list>
					<#if !found>
						<td id="row_prj_${project.id}_env_${environment.id}" class="row_prj_env">&nbsp;</td>
					</#if>
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
