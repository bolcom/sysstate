
 <div id="cardbox"><div class="row"></div></div>
 
<script type="text/javascript">
	
	(function worker() {
		$.ajax({
			url : "${contextPath}/services/view/${view.name}/ecosystem",
			success : function(data) {
				handleData(data);
			},
			complete : function() {
				// Schedule the next request when the current one's complete
				setTimeout(worker, 5000);
			}
		});
	})();

	var stringToColour = function(str) {
	    // str to hash
	    for (var i = 0, hash = 0; i < str.length; hash = str.charCodeAt(i++) + ((hash << 5) - hash));
	    // int/hash to hex
	    for (var i = 0, colour = "#"; i < 3; colour += ("00" + ((hash >> i++ * 8) & 0xFF).toString(16)).slice(-2));
	    return colour;
	}

	function handleData(data){
		var cardBox =$('#cardbox') 
		var validIds = [];
		$.each(data.projectEnvironments, function() {
			var projectEnvironmentId ='pe' + this.id;
			console.log("Checking if cardBox already has an element with id [#" + projectEnvironmentId + "]")
			var card = $('#' + projectEnvironmentId);   
			if (!card.length) {
				console.log("No card defined, adding new card...")			
				card = addCard(cardBox, projectEnvironmentId, this);
			}
			setCardContent(card, this, data.instances);
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
	
	function getCousin(cousinSelectionMethod, row, projectEnvironment){
		switch(cousinSelectionMethod) {
			case "project":
				return row.find('.prj-' + projectEnvironment.project.id + ':first');
				break;
			case "environment":
			default:			
				return row.find('.env-' + projectEnvironment.environment.id + ':first');
				break;
		}
	}
	
	function addCard(parent, cardId, projectEnvironment){
		
		var row = parent.find('.row:first')
		var color = stringToColour(projectEnvironment.environment.name);
		var cousin = getCousin(getUrlParameter('csm'), row, projectEnvironment)

		var environmentName = projectEnvironment.environment.name;
		var newCard = $('\
       		<div class="col s12 m1 env-' + projectEnvironment.environment.id + ' prj-' + projectEnvironment.project.id + '" id="' + cardId + '">\
		 		<div class="card">\
			        <div class="waves-effect waves-block waves-light header-small grey">\
			        	<span class="environment">[<a href="${contextPath}/filter/environment/' + projectEnvironment.environment.id + '/index.html" >' +environmentName + '</a>]</span>\
			        </div>\
				    <div>\
				    	<div class="background">' + projectEnvironment.project.name + '</div>\
				        <div class="instances card-content"></div>\
	              	</div>\
				</div>\
			</div>\
	    ');
		if (cousin[0]){
			console.log("Found cousin [" + cousin[0] + "] for environmentName [" + environmentName + "]");
			return newCard.insertAfter(cousin[0]);
		} else {
			console.log("No cousin found for environmentName [" + environmentName + "]");
			return newCard.appendTo(row);		
		}
		
	}
	
	var getUrlParameter = function getUrlParameter(sParam) {
	    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
	        sURLVariables = sPageURL.split('&'),
	        sParameterName,
	        i;
	
	    for (i = 0; i < sURLVariables.length; i++) {
	        sParameterName = sURLVariables[i].split('=');
	
	        if (sParameterName[0] === sParam) {
	            return sParameterName[1] === undefined ? true : sParameterName[1];
	        }
	    }
	};	
	
	
	
	
</script>	