<div id="mynetwork"></div>
<script type="text/javascript">
	var edges = new vis.DataSet([], {queue:true});

	var nodes = new vis.DataSet([], {queue:true});
	
	(function worker() {
		$.ajax({
			url : "${baseUrl}/services/view/${view.id!'0'}/ecosystem",
			success : function(data) {
				handleData(data);
			},
			complete : function() {
				// Schedule the next request when the current one's complete
				setTimeout(worker, 10000);
			}
		});
	})();


	function handleData(data){
		var validIds = [];
		if (data.ecoSystem.environments.length > 1) {
			$.each(data.ecoSystem.environments, function() {
				var environmentId = "E" + this.id;
				nodes.update([ {
					id : environmentId,
					label : this.name,
					group : "ENVIRONMENT",
					level : 0
				} ]);
				validIds.push(environmentId)
			});				
		}
		$.each(data.ecoSystem.instances, function() {

			var instanceId = "I" + this.id;
			nodes.update([ {
				id : instanceId,
				label : "[" + this.projectEnvironment.project.name + "]\n" + (this.state.description ? this.state.description.replace(" ", "\n") : this.state.state),
				group : this.state.state,
				level : 1
			} ]);
			validIds.push(instanceId);
			$.each(this.instanceLinks, function() {
				if (this.direction == 'OUTGOING'){
					var instanceLinkId = "I" + this.instanceId;
					addEdgeIfNotExists(instanceId, instanceLinkId)
				}
			});
			addEdgeIfNotExists(instanceId, "P" + this.projectEnvironment.project.id)
			addEdgeIfNotExists("E" + this.projectEnvironment.environment.id, instanceId)
		});
		var actualIds = nodes.getIds();

		difference = $.grep(actualIds, function(el) {
			return $.inArray(el, validIds) == -1;
		})
		$.each(difference, function() {
			nodes.remove(this);
		});
		nodes.flush();
		edges.flush();
		
	}
	
	function addEdgeIfNotExists(from, to, label){
		var instanceEdges = edges.get({
			filter : function(item) {
				return item.from == from
						&& item.to == to;
			}
		})
		if (instanceEdges.length == 0) {
			edges.update([ {
				from : from,
				to : to,
				label : label
			} ])
		}		
	}
	
	
	
	var data = {
		nodes : nodes,
		edges : edges,
	};
	var options = {
		<#include vis_opts!'vis_network_default_opts.ftl'>
	};

	var container = document.getElementById('mynetwork');
	var network = new vis.Network(container, data, options);
</script>