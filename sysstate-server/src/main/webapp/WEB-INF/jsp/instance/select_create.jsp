<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<table>
	<tr valign="top">
		<td>
				<!--  start step-holder -->
				<div id="step-holder">
					<div class="step-no">1</div>
					<div class="step-dark-left">Choose Type</div>
					<div class="step-dark-right">&nbsp;</div>
					<div class="step-no-off">2</div>
					<div class="step-light-left">Edit Details</div>
					<div class="step-light-round">&nbsp;</div>
					<div class="clear"></div>
				</div>
				<!--  end step-holder -->

			<table id="id-form">

			</table>
		</td>
	</tr>
</table>

<h2>Choose the type of StateResolver you would like to create:</h2>
<table id="instances-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Type</a></th>
		
	</tr>
	<c:forEach var="stateResolverName" items="${stateResolverNames}" varStatus="varStatus">
		<c:set var="class" value="${descriptorMapping.theClass}"/>
		<tr class="${(varStatus.index)%2 eq 0  ? '' : 'alternate-row' }">
			<td>
			<a href="${stateResolverName}/create.html" title="Create Instance using ${stateResolverName}" class="icon-5 info-tooltip"></a>
			</td>
			<td>
				${stateResolverName}
			</td>
			
			
		</tr>
	</c:forEach>
</table>


