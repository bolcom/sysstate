<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript">
$(function(){
	$('#toggle-all').click(function(){
 		$('#toggle-all').toggleClass('toggle-checked');
 		var checkBoxes =$('#discovery-form input[type=checkbox]')
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
		return false;
	});
});
</script>
<table>
	<tr valign="top">
		<td>
			<form method="POST" action="index.html" >
				<table id="id-form">
					<tr>
						<th valign="top">Plugin:</th>
						<td>
						
							<select name="plugin" class="styledselect_form_1">
								<c:forEach var="discoveryPlugin" items="${discoveryPlugins}">
									<option value="${discoveryPlugin.pluginClass.name}">${discoveryPlugin.pluginClass.simpleName}</option>
								</c:forEach>
							
							</select>
						</td>
					</tr>
					<tr>
						<th valign="top">Properties:</th>
						<td>
							<textarea rows="10" cols="80" class="form-textarea" name="properties"><c:out value="${properties}"/></textarea>
						</td>
					</tr>
					<tr>
						<th>&nbsp;</th>
						<td valign="top">
							<input type="submit" value="" class="form-submit" /> 
							<input type="reset" value="" class="form-reset" />
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>		

<table>
	<tr valign="top">
		<td>
			<sf:form method="post" action="add.html" modelAttribute="multiForm" id="discovery-form">
				<table id="discovery-table">
					<tr>
						<th class="table-header-check"><a id="toggle-all" ></a> </th>
						<th class="table-header-repeat line-left"><a href="">Instance</a></th>
						<th class="table-header-repeat line-left" colspan="2"><a href="">Configuration</a></th>
						<th class="table-header-repeat line-left" colspan="1"><a href="">Project</a></th>
						<th class="table-header-repeat line-left" colspan="1"><a href="">Environment</a></th>
					</tr>
					<c:forEach items="${multiForm.instanceListSelectors}" var="item" varStatus="status">
						<tr>
							<td><sf:checkbox path="instanceListSelectors[${status.index}].selected" /></td>
							<td>
								Name: <sf:input path="instanceListSelectors[${status.index}].instance.name" /><br/>
								HomepageUrl: <sf:input path="instanceListSelectors[${status.index}].instance.homepageUrl" /><br/>
								Tags: <sf:input path="instanceListSelectors[${status.index}].instance.tags" />
								
							</td>
							<td>
								RefreshTimeout: <sf:input path="instanceListSelectors[${status.index}].instance.refreshTimeout" /><br/>
								PluginClass:<sf:select path="instanceListSelectors[${status.index}].instance.pluginClass">
									<sf:options items="${stateResolverNames}" itemLabel="simpleName" itemValue="name"/>
								</sf:select><br/>
								
							</td>
							<td>Configuration: <sf:textarea path="instanceListSelectors[${status.index}].instance.configuration"  cols="40" rows="4"/></td>
							<td>
								<sf:select path="instanceListSelectors[${status.index}].instance.projectEnvironment.environment.id">
									<sf:option value=""/>
									<sf:options items="${environments}" itemLabel="name" itemValue="id"/>
								</sf:select>
							</td>
							<td>
								<sf:select path="instanceListSelectors[${status.index}].instance.projectEnvironment.project.id">
									<sf:option value=""/>
									<sf:options items="${projects}" itemLabel="name" itemValue="id"/>
								</sf:select>
							</td>

						</tr>
					
					</c:forEach>
					<tr>
						<th>&nbsp;</th>
						<td valign="top">
							<input type="submit" value="" class="form-submit" /> 
							<input type="reset" value="" class="form-reset" />
						</td>
					</tr>
				</table>
			</sf:form>
		</td>
	</tr>
</table>	