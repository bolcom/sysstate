<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/"%>

<div id="mynetwork"></div>

<script type="text/javascript">
	var edges = new vis.DataSet();

	var nodes = new vis.DataSet();

	(function worker() {
		$.ajax({
			url : "${contextPath}/services/view/${view.id}/ecosystem",
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
		var validIds = [];
		$.each(data.ecoSystem.projects, function() {
			var projectId = "P" + this.id;
			nodes.update([ {
				id : projectId,
				label : this.name,
				group : "PROJECT",
				level : 2
			} ]);
			validIds.push(projectId)
		});
		
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

		$.each(data.ecoSystem.instances, function() {

			var instanceId = "I" + this.id;
			nodes.update([ {
				id : instanceId,
				label : this.projectEnvironment.project.name + "\n" + this.projectEnvironment.environment.name,
				group : this.state.state,
				level : 1
			} ]);
			validIds.push(instanceId);
			$.each(this.instanceLinks, function() {
				var instanceLinkId = "I" + this.instanceId;
				addEdgeIfNotExists(instanceId, instanceLinkId)
			});
			addEdgeIfNotExists(instanceId, "P" + this.projectEnvironment.project.id)
			addEdgeIfNotExists(instanceId, "E" + this.projectEnvironment.environment.id)
		});
		var actualIds = nodes.getIds();

		difference = $.grep(actualIds, function(el) {
			return $.inArray(el, validIds) == -1;
		})
		$.each(difference, function() {
			nodes.remove(this);
		});
		
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
		 
		 hierarchicalLayout: {enabled:true},
		 smoothCurves: false,

		groups : {

			STABLE : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'green',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			UNSTABLE : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'orange',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			ERROR : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'red',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			DISABLED : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'grey',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			PENDING : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'grey',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			PROJECT : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'blue',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			},
			ENVIRONMENT : {
				shape : 'circle',
				color : {
					border : 'black',
					background : 'purple',
					highlight : {
						border : 'yellow',
						background : 'orange'
					}
				},
				fontColor : 'white',
				fontSize : 18
			}			
		}
	};

	var container = document.getElementById('mynetwork');
	var network = new vis.Network(container, data, options);
</script>