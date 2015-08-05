width : '100%',
height : '100%',
physics: {barnesHut: {springLength: 110,centralGravity: 0.6,damping: 0.08}},
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
		fontSize : 12
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
		fontSize : 12
	}			
}