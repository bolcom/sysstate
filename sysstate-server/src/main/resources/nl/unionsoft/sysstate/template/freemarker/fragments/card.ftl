
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
		var validIds = [];
		$.each(data.ecoSystem.projectEnvironments, function() {
			console.log("Checking if cardBox already has an element with id [#pe" + this.id + "]")
			var projectEnvironmentId ='pe' + this.id;
			var card = $('#' + projectEnvironmentId);   
			if (!card.length) {
				console.log("No card defined, adding new card for projectEnvironmentId [" + projectEnvironmentId + "]")			
				card = addCard(cardBox, projectEnvironmentId);
			}
			setCardContent(card, this, data.ecoSystem.instances);
			validIds.push(projectEnvironmentId)
		});
		var actualIds = [];
		 
		$("div[id^='pe']").each(function() {
    		actualIds.push(this.id);
		});
		
		var difference = $.grep(actualIds, function(el) {
			return $.inArray(el, validIds) == -1;
		})
		
		$.each(difference, function() {
			console.log("Removing card with id [" + this + "].");
			$('#' + this).remove();
		});
	}
	
	function setCardContent(card, projectEnvironment, instances){
		var panel = card.find(".card");
		panel.find(".environment").text("[" + projectEnvironment.project.name + "] [" + projectEnvironment.environment.name + "]");
		var stateClass = getClassForState(projectEnvironment.state);
		panel.find(".header-small").attr('class', "waves-effect waves-block waves-light header-small " + stateClass );
		
		var cardContent = panel.find(".card-content");
		cardContent.empty();
		$.each(instances, function() {
			if (this.projectEnvironment.id == projectEnvironment.id){
				cardContent.append(createInstanceDetails(this));
			}
		});
	
	}
	function createInstanceDetails(instance){
		return $('\
			<div id="i' + instance.id + '" class="instance-details">\
				<span class="statebox ' + instance.state.state + '">&nbsp;</span>\
				<span class="state">' + instance.name + '</span>\
			</div>\
			');
	}
	
	
	
	function getClassForState(state){
		switch(state){
			case "STABLE":
				return 'green'
			case "UNSTABLE":
				return 'yellow'
			case "ERROR":
				return 'red'
			case "DISABLED":
			case "PENDING":
			default:
				return 'grey'			
		}
	}
	
	
	function addCard(parent, projectEnvironmentId){
		var row = findRow(parent);
		return $('\
       		<div class="col s12 m1" id="' + projectEnvironmentId + '">\
		 		<div class="card">\
			        <div class="waves-effect waves-block waves-light header-small grey">\
			        	<span class="environment">[]</span>\
			        </div>\
			        <div class="instances card-content">\
	              	</div>\
				</div>\
			</div>\
	    ').appendTo(row);		
	}
	
	function findRow(parent){
		var row = null;
		$(".row").each(function() {
			row = $(this);
		});
		
		if (row == null){
			row = $('<div class="row"></div>').appendTo(parent);
		}
		return row;
	}
	
	
	
	
</script>	