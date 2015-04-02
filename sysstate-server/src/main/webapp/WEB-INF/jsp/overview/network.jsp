<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/" %>
<div id="mynetwork"></div>

<script type="text/javascript">


	var edges = [];

	var nodes = new vis.DataSet();

	(function worker() {
		  $.ajax({
		    url: "http://localhost:8680/services/view/1/ecosystem", 
		    success: function(data) {
		    	
		    	
		    	nodes.update([{id: '1', label: 'Node asd',  group: 'stable'}]);	

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
	    width: '400px',
	    height: '400px',
	   	groups: {
	   		
	   		stable: {
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
	  		  }
	    }
	  };

	var container = document.getElementById('mynetwork');
	var network = new vis.Network(container, data, options); 
	
  
  
</script>