<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/" %>
<div id="mynetwork"></div>

<script type="text/javascript">


	var edges =  new vis.DataSet();

	var nodes = new vis.DataSet();

	(function worker() {
		  $.ajax({
		    url: "http://localhost:8680/services/view/1/ecosystem", 
		    success: function(data) {
		    	var validIds = [];
		    	$.each(data.ecoSystem.instances, function() {

		    		var instanceId = "I" + this.id;
		    		
		    		nodes.update([{id: instanceId, label: this.name,  group: this.state.state}]);
		    		validIds.push(instanceId);
		    		  $.each(this.instanceLinks, function() {
		    			  
		    			  var instanceLinkId = "I" + this.instanceId;
		    			  
		    		   		var instanceEdges = edges.get({
				    			filter: function (item) {
				    				return item.from == instanceId && item.to == instanceLinkId;
				    			}
				    		})
				    		if (instanceEdges.length == 0){
		    			  		edges.update([{from :  instanceId , to : "I" + this.instanceId, label : this.name}])
				    		}
		    		  });
		    	});
		    	var actualIds = nodes.getIds();
		    	
		    	difference = $.grep(actualIds, function(el) { return $.inArray( el, validIds ) == -1; })
		    	$.each(difference, function() {
		    		nodes.remove(this);
		    	});
		    	
		    	
		    },
		    complete: function() {
		      // Schedule the next request when the current one's complete
		      setTimeout(worker, 5000);
		    }
		  });
		})();
	
	
	$(document).ready(function() {
	    $.ajax({
	        url: "http://localhost:8680/services/view/1/ecosystem"
	    }).then(function(data) {
	    	
	    });
	});

  	var data= {
		    nodes: nodes,
		    edges: edges,
		  };
	  var options = {
	    width: '100%',
	    height: '100%',

	    
	    physics: {physics: {barnesHut: {centralGravity: 0}}, smoothCurves: false},
	    
	   	groups: {
	   		
	   		STABLE: {
	  		    shape: 'circle',
	  		    color: {
	  		      border: 'black',
	  		      background: 'green',
	  		      highlight: {
	  		        border: 'yellow',
	  		        background: 'orange'
	  		      }
	  		    },
	  		    fontColor: 'white',
	  		    fontSize: 18
	  		  },
	  		UNSTABLE: {
	  		    shape: 'circle',
	  		    color: {
	  		      border: 'black',
	  		      background: 'orange',
	  		      highlight: {
	  		        border: 'yellow',
	  		        background: 'orange'
	  		      }
	  		    },
	  		    fontColor: 'white',
	  		    fontSize: 18
	  		  },
	  		ERROR: {
	  		    shape: 'circle',
	  		    color: {
	  		      border: 'black',
	  		      background: 'red',
	  		      highlight: {
	  		        border: 'yellow',
	  		        background: 'orange'
	  		      }
	  		    },
	  		    fontColor: 'white',
	  		    fontSize: 18
	  		  },
	  		DISABLED: {
	  		    shape: 'circle',
	  		    color: {
	  		      border: 'black',
	  		      background: 'grey',
	  		      highlight: {
	  		        border: 'yellow',
	  		        background: 'orange'
	  		      }
	  		    },
	  		    fontColor: 'white',
	  		    fontSize: 18
	  		  },
	  		PENDING: {
	  		    shape: 'circle',
	  		    color: {
	  		      border: 'black',
	  		      background: 'grey',
	  		      highlight: {
	  		        border: 'yellow',
	  		        background: 'orange'
	  		      }
	  		    },
	  		    fontColor: 'white',
	  		    fontSize: 18
	  		  }	  	 	  		
	    }
	  };

	var container = document.getElementById('mynetwork');
	var network = new vis.Network(container, data, options); 
	
  
  
</script>