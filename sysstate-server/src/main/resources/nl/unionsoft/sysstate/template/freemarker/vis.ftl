<div id="mynetwork"></div>
<script type="text/javascript">
	var edges = new vis.DataSet([], {queue:true});

	var nodes = new vis.DataSet([], {queue:true});
	
	(function worker() {
		$.ajax({
			url : "${contextPath}/services/view/${view.id!'0'}/ecosystem",
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
		
		width : '100%',
		height : '100%',
		physics: {barnesHut: {springLength: 140,centralGravity: 0.4,damping: 0.10}},
		edges: {
				arrowScaleFactor : 0.5,
			    color: 'white',
			    width: 1,
			    style : "arrow"
		},	
		groups : {

			STABLE : {
				shape : 'box',
				color : {
					border : 'white',
					background : 'green',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 10
			},
			UNSTABLE : {
				shape : 'box',
				color : {
					border : 'white',
					background : 'orange',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 10
			},
			ERROR : {
				shape : 'box',
				color : {
					border : 'white',
					background : 'red',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 10
			},
			DISABLED : {
				shape : 'box',
				color : {
					border : 'white',
					background : 'grey',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 10
			},
			PENDING : {
				shape : 'box',
				color : {
					border : 'white',
					background : 'grey',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 10
			},
			PROJECT : {
				shape : 'circle',
				color : {
					border : 'white',
					background : 'blue',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 14
			},
			ENVIRONMENT : {
				shape : 'circle',
				color : {
					border : 'white',
					background : 'purple',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 14
			}			
		}
	};

	var container = document.getElementById('mynetwork');
	var network = new vis.Network(container, data, options);
</script>