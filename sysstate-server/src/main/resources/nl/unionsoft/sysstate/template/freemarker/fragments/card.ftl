
 <div id="cardbox"></div>
<script type="text/javascript">
	
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

	function handleData(data){
		var cardBox =$('#cardbox') 
		$.each(data.ecoSystem.projectEnvironments, function() {
			console.log("Checking if cardBox already has an element with id [#pe" + this.id + "]")
			var projectEnvironmentId ='pe' + this.id;   
			if (!$('#' + projectEnvironmentId).length) {
				console.log("No card defined, adding new card for projectEnvironmentId [" + projectEnvironmentId + "]")			
				addCard(cardBox);
			}
		});
	}
	
	function addCard(parent, projectEnvironmentId){
		var row = findRow()
		row.append('\
		<div class="row">\
	      <div class="col s12 m1">\
	        <div class="card-panel teal" id="' + projectEnvironmentId + '">\
	          <span class="white-text">I am a very simple card. I am good at containing small bits of information.</span>\
	        </div>\
	      </div>\
	    </div>\
	    ');		
	}
	
	function findRow(parent){
		console.log("Finding row to append to...");
		parent. 
	}
	
	
</script>	