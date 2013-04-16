<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="left"><a href=""><img src="${contextPath}/images/forms/icon_plus.gif" width="21" height="21" alt="" /></a></div>
<div class="right">
	<h5>Create New</h5>
	Manage Instances
	<ul class="greyarrow">
		<li><a href="${contextPath}/instance/create.html">New Instance</a></li>
		<c:if test="${instance.id != null }">
			<li><a href="${contextPath}/instance/${instance.id }/copy.html">Copy this instance</a> </li>
		</c:if>
	</ul>
</div>

<div class="clear"></div>
<c:if test="${instance.id != null }">

	<div class="lines-dotted-short"></div>
	
	<div class="left"><a href=""><img src="${contextPath}/images/forms/icon_minus.gif" width="21" height="21" alt="" /></a></div>
	<div class="right">
		<h5>Delete </h5>
		<ul class="greyarrow">
			<li><a href="${contextPath}/instance/${instance.id }/delete.html">Delete this instance</a> </li>
		</ul>
	</div>
	
	<div class="clear"></div>
	<div class="lines-dotted-short"></div>
	
	<div class="left"><a href=""><img src="${contextPath}/images/forms/icon_edit.gif" width="21" height="21" alt="" /></a></div>
	<div class="right">
		<h5>Edit Related</h5>
		<ul class="greyarrow">
			<li><a href="${contextPath}/project/${instance.projectEnvironment.project.id}/update.html?rUrl=${requestUrl}">Edit Project</a></li> 
			<li><a href="${contextPath}/environment/${instance.projectEnvironment.environment.id}/update.html?rUrl=${requestUrl}">Edit Environment</a></li>
		</ul>
	</div>
</c:if>