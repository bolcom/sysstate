<table class="state_table" >
	<colgroup id="cg_prj"/>
	<#list ecoSystem.environments as environment>
		<colgroup id="cg_env_${environment.id}" class="cg_env"/>
	</#list>
	<thead class="head">
		<tr class="row_env">
			<th id="col_prj" class="top_left"><a href="${contextPath}/manager/index.html" >PRJ/ENV</a> </th>
			<#list ecoSystem.environments as environment>
				<th id="col_env_${environment.id}" class="col_env ${environment_hasnext ? '' : 'top_right'}"><a href="${contextPath}/filter/environment/${environment.id}/index.html" >${environment.name}</a></th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list ecoSystem.projects as project>
			<tr id="row_prj_${project.id}" class="row_prj ${(project_index)%2 eq 0 ? 'row_prj_odd' : 'row_prj_even' }">
				<th class="row_prj_name">
					<a href="${contextPath}/filter/project/${project.id}/index.html" >
						${project.name}
					</a>
				</th>
				<#list ecoSystem.environments as environment>
				</#list>	
			</tr>
		</#list>	
		
	</tbody>
	<tfoot class="foot">
		<tr>
			<td class="bottom_left" colspan="${fn:length(viewResults.environments)}">
			<td class="bottom_right">&nbsp;</td>
		</tr>
	</tfoot>		
</table>
