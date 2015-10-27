

<script type="text/javascript">
	
	$(document).ready(function() {
    $('#example').DataTable();
} );
	
		
	(function worker() {
		$.ajax({
			url : "${contextPath}/services/view/${view.id!'0'}/ecosystem",
			success : function(data) {
				handleData(data);
			},
			complete : function() {
				// Schedule the next request when the current one's complete
				setTimeout(worker, 5000);
			}
		});
	})();
	
	
	
</script>	
            