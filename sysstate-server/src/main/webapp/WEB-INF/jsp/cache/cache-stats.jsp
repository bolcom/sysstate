<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
$(function(){
	$('#toggle-all').click(function(){
 		$('#toggle-all').toggleClass('toggle-checked');
 		var checkBoxes =$('#cache-form input[type=checkbox]')
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
		return false;
	});
});
</script>

  
<form method="POST" action="clear.html" name="cacheForm" id="cache-form">
	<sc:csrfInput/>
	<table id="cache-stats-table">
		<tr>
			<th class="table-header-check"><a id="toggle-all" ></a> </th>
			<th class="table-header-repeat line-left"><a href="">name</a></th>
			<th class="table-header-repeat line-left"><a href="">Av Get</a></th>
			<th class="table-header-repeat line-left"><a href="">Av. Search</a></th>
	
			<th class="table-header-repeat line-left"><a href="">#Mem</a></th>
			<th class="table-header-repeat line-left"><a href="">#Mem Hits</a></th>
			<th class="table-header-repeat line-left"><a href="">#Mem Misses</a></th>
			<th class="table-header-repeat line-left"><a href="">#Mem Ratio</a></th>
	
			<th class="table-header-repeat line-left"><a href="">#Disk</a></th>
			<th class="table-header-repeat line-left"><a href="">#Disk Hits</a></th>
			<th class="table-header-repeat line-left"><a href="">#Disk Misses</a></th>
			<th class="table-header-repeat line-left"><a href="">#Disk Ratio</a></th>
	
			<th class="table-header-repeat line-left"><a href="">#</a></th>
			<th class="table-header-repeat line-left"><a href="">#Hits</a></th>
			<th class="table-header-repeat line-left"><a href="">#Misses</a></th>
			<th class="table-header-repeat line-left"><a href="">#Ratio</a></th>
			
		</tr>
		<c:forEach var="cacheStat" items="${cacheStats }">
			<c:set var="cache" value="${cacheStat.value}"/>
			<c:set var="statistics" value="${cache.statistics}"/>
			<tr>
			
				<td><input name="cacheNames" type="checkbox" value="${cache.name }"/></td>			
				<td>${cache.name}</td>
	
				<td><fmt:formatNumber value="${statistics.averageGetTime}" maxFractionDigits="4"/>ms</td>
				<td><fmt:formatNumber value="${statistics.averageSearchTime}" maxFractionDigits="4"/>ms</td>
	
				<c:set var="total" value="${statistics.memoryStoreObjectCount}"/>
				<c:set var="hits" value="${statistics.inMemoryHits}"/>
				<c:set var="misses" value="${statistics.inMemoryMisses}"/>
				<td style="border-left: 2px Solid Black;background-color: #EEE;">${total}</td>
				<td style="background-color: #EEE;">${hits}</td>
				<td style="background-color: #EEE;">${misses}</td>
				<td style="background-color: #EEE;"><fmt:formatNumber pattern="#.#" value="${(hits / (misses + hits))*100}"/>%</td>
	
				<c:set var="total" value="${statistics.diskStoreObjectCount}"/>
				<c:set var="hits" value="${statistics.onDiskHits}"/>
				<c:set var="misses" value="${statistics.onDiskMisses}"/>
				<td style="border-left: 2px Solid Black;background-color: #DDD;">${total}</td>
				<td style="background-color: #DDD;">${hits}</td>
				<td style="background-color: #DDD;">${misses}</td>
				<td style="background-color: #DDD;"><fmt:formatNumber pattern="#.#" value="${(hits / (misses + hits))*100}"/>%</td>
	
				<c:set var="total" value="${statistics.objectCount}"/>
				<c:set var="hits" value="${statistics.cacheHits}"/>
				<c:set var="misses" value="${statistics.cacheMisses}"/>
				<td style="border-left: 2px Solid Black;background-color: #CCC;">${total}</td>
				<td style="background-color: #CCC;">${hits}</td>
				<td style="background-color: #CCC;">${misses}</td>
				<td style="background-color: #CCC;"><fmt:formatNumber pattern="#.#" value="${(hits / (misses + hits))*100}"/>%</td>
			</tr>
		</c:forEach>
	
	</table>
	<div id="actions-box">
		<a href="" class="action-slider"></a>
		<div id="actions-box-slider">
			<a href="javascript: document.cacheForm.submit();" class="action-edit">Clear</a>
		</div>
		<div class="clear"></div>
	</div>
</form>
<br/>
<a href="../index.html">Back</a>
 


