<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@taglib prefix="sse" uri="http://www.unionsoft.nl/sse/"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
$(function(){
	$('#toggle-all').click(function(){
 		$('#toggle-all').toggleClass('toggle-checked');
 		var checkBoxes =$('#instancesForm input[type=checkbox]')
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
		return false;
	});
});

$(function(){
	$('#action-disable').click(function(){
		var instancesForm = $('#instancesForm');
		instancesForm.attr("action", "${contextPath}/instance/multi/disable.html");
		instancesForm.submit();
		return false;
	});
});

$(function(){
	$('#action-enable').click(function(){
		var instancesForm = $('#instancesForm');
		instancesForm.attr("action", "${contextPath}/instance/multi/enable.html");
		instancesForm.submit();
		return false;
	});
});

$(function(){
	$('#action-delete').click(function(){
		var instancesForm = $('#instancesForm');
		instancesForm.attr("action", "${contextPath}/instance/multi/delete.html");
		instancesForm.submit();
		return false;
	});
});

$(function(){
	$('#action-refresh').click(function(){
		var instancesForm = $('#instancesForm');
		instancesForm.attr("action", "${contextPath}/instance/multi/refresh.html");
		instancesForm.submit();
		return false;
	});
});


</script>



<div id="filter" style="float:left;width:220px;" >
	<div style="width:200px;padding-bottom:20px;">
		<h3>Presets</h3>
		<a href="${contextPath}/filter/index.html">Clear Filter</a> |
		<a href="${contextPath}/filter/index.html?states=UNSTABLE&states=ERROR">Alerts</a> | 
		<a href="${contextPath}/filter/index.html?states=STABLE">Stable</a>
	</div>
	<div style="border-top:1px Solid Gray;width:200px;padding-top:10px;">
		<h3>Search</h3>
	
		
		<sf:form commandName="filter" method="GET" action="">
			<sf:hidden path="id"/>
			<table border="0" cellpadding="0" cellspacing="0"  id="id-form">
				<c:if test="${filter.id != null}">
					<tr>
						<th valign="top">Selected Filter:<br/>
							<h2><c:out value="${filter.name}"/></h2>
						</th>
					</tr>
				</c:if>
			
				<tr>
					<th valign="top">SearchTerm:<br/>
						<sf:input path="search"  cssClass="inp-form"/>
					</th>
				</tr>
				<tr>
					<th valign="top">Tags:<br/>
						<sf:input path="tags"  cssClass="inp-form"/>
					</th>
				</tr>
				<tr>
					<th valign="top">Project:<br/>
						<!-- styledselect_form_1 -->
						<sf:select path="projects" cssClass="" multiple="true" items="${projects}" itemValue="id" itemLabel="name" size="13" cssStyle="width:100px;"/>
					</th>
				</tr>
				<tr>
					<th valign="top">Environments:<br/>
						<sf:select path="environments" cssClass="" multiple="true" items="${environments}" itemValue="id" itemLabel="name" size="7" cssStyle="width:100px;"/>
					</th>
				</tr>
				<tr>
					<th valign="top">Type:<br/>
						<sf:select  path="stateResolvers" cssClass="" multiple="true" items="${stateResolvers}" size="5" cssStyle="width:100px;"/>
					</th>
				</tr>
				<tr>
					<th valign="top">State:<br/>
						<sf:select path="states" cssClass="" multiple="true" items="${states}" size="5" cssStyle="width:100px;"/>
					</th>
				</tr>				
				<tr>
					<td valign="top">
						<input type="submit" name="action" value="search" class="form-submit" />
					</td>
				</tr>
				<sc:authorize access="hasAnyRole('ADMIN','EDITOR')">	
				<tr>
					<td><h3>${filter.id != null ? 'Update' : 'Save' } Filter ${filter.name}</h3></td>
				</tr>
				<tr>
					<th valign="top">FilterName:<br/>
						<sf:input path="name" cssClass="inp-form"/>
					</th>
				</tr>
				<tr>
					<td valign="top">
						<input type="submit" name="action" value="save" class="form-submit" />
					</td>
				</tr>
				</sc:authorize>
				
			</table>
		</sf:form>
	</div>
</div>
<div style="float:left; width:900px">
	<div id="instances">
		<h2>
			Instances
			<sc:authorize url="/instance/create">			
				<a href="${contextPath}/instance/create.html?projectId=${project.id}&environmentId=${environment.id}">
					<img src="${contextPath}/images/create.png" />
				</a>
			</sc:authorize>
		</h2>
		<c:choose>
			<c:when test="${fn:length(instanceStates) == 0 }">
				No results found for Search. Use the search to define your filter<sc:authorize url="/instance/create">, create a <a href="${contextPath}/instance/create.html?projectId=${project.id}&environmentId=${environment.id}">new instance</a></sc:authorize>
				 or start with a <a href="${contextPath}/filter/index.html">blank</a> filter.
			</c:when>
			<c:otherwise>
				<form name="instancesForm" action="${contextPath}/instance/multi.html" id="instancesForm" method="POST">
					<sc:csrfInput/>
					<table id="instances-table">
						<tr>
							<th class="table-header-check"><a id="toggle-all" ></a> </th>
							<th class="table-header-repeat line-left">&nbsp;</th>
							<th class="table-header-repeat line-left"><a href="">Env</a></th>
							<th class="table-header-repeat line-left"><a href="">Config</a></th>
							<th class="table-header-repeat line-left"><a href="">State</a></th>
							<th class="table-header-repeat line-left"><a href="">Scheduling</a></th>
							<th class="table-header-options line-left "><a href="">Options</a></th>
						</tr>
						<c:set var="index" value="0"/>
							<c:forEach var="instanceState" items="${instanceStates}" varStatus="varStatInstance" >
								<c:set var="instance" value="${instanceState.instance }" scope="request"/>
								<c:set var="state" value="${instanceState.state }" scope="request"/>
								<jsp:include page="/WEB-INF/jsp/common/instance.jsp">
									<jsp:param name="alternateRow" value="${(varStatInstance.index)%2 eq 0 }"/>
									<jsp:param name="id" value="checkBox"/>
									<jsp:param name="options" value="true"/>
									<jsp:param name="state" value="true"/>
								</jsp:include>
							</c:forEach>
							
						<%--</c:forEach> --%>				
					</table>
					<div id="actions-box">
						<a href="" class="action-slider"></a>
						<div id="actions-box-slider">
							<a href="#" id="action-disable"  class="action-edit">Disable</a>
							<a href="#" id="action-enable"  class="action-edit">Enable</a>
							<a href="#" id="action-delete"  class="action-delete">Delete</a>
							<a href="#" id="action-refresh"  class="action-delete">Refresh</a>
						</div>
						<div class="clear"></div>
					</div>			
				</form>
			</c:otherwise>
		</c:choose>
				
	
	</div>
</div>

