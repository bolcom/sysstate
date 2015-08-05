
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
			var projectEnvironmentId ='pe' + this.id;
			console.log("Checking if cardBox already has an element with id [#" + projectEnvironmentId + "]")
			var card = $('#' + projectEnvironmentId);   
			if (!card.length) {
				console.log("No card defined, adding new card...")			
				card = addCard(cardBox, projectEnvironmentId, this);
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
				<span class="state"><a href="'+ instance.homepageUrl +'">' + instance.name + '</a></span>\
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
	
	
	function addCard(parent, cardId, projectEnvironment){
		var row = findRow(parent);
		return $('\
       		<div class="col s12 m1" id="' + cardId + '">\
		 		<div class="card">\
			        <div class="waves-effect waves-block waves-light header-small grey">\
			        	<span class="environment">[<a href="${contextPath}/filter/project/' + projectEnvironment.project.id + '/index.html" >' + projectEnvironment.project.name + '</a>] [<a href="${contextPath}/filter/environment/' + projectEnvironment.environment.id + '/index.html" >' + projectEnvironment.environment.name + '</a>]</span>\
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