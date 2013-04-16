<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<h2>${plugin.pluginClass.name}</h2>
			<form method="POST" action="" name="cacheForm">
				<table id="id-form">
					<tr>
						<th valign="top">Configuration:</th>
						<td>
						<textarea rows="10" cols="80" name="configuration" class="form-textarea"><c:out value="${configuration}"/></textarea>
						</td>
					</tr>
					<tr>
						<th>&nbsp;</th>
						<td valign="top">
							<input type="submit" value="" class="form-submit" /> 
							<input type="reset" value="" class="form-reset" />
						</td>
						<td></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
				
</table>