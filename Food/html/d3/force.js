var width = 1280,
r = 6;
height = 800;


var color = d3.scale.category20();

var svg = d3.select("#chart").append("svg")
.attr("width", width)
.attr("height", height);

var force = d3.layout.force()
.charge(-120)
.linkDistance(300)
.size([width, height]);

d3.json("1994-3.json", function(json) {
        force
        .nodes(json.nodes)
        .links(json.links)
        .start();
        
        var link = svg.selectAll(".link")
        .data(json.links)
        .enter().append("line")
        .attr("class", "link")
        .style("stroke-width", function(d) { return Math.sqrt(d.value); });
        
        var node = svg.selectAll(".node")
        .data(json.nodes)
        .enter().append("g")
        .attr("class", "node")
        .call(force.drag);
        
        node.append("circle")
        .attr("r", function(d) {return Math.sqrt(d.degree)+5; })
        .style("fill", function(d) { return color(d.group); })
        
        node.append("text")
        .attr("dx", 12)
        .attr("dy", ".35em")
        .text(function(d) { return d.name });
        
        force.on("tick", function() {
                 link.attr("x1", function(d) { return d.source.x; })
                 .attr("y1", function(d) { return d.source.y; })
                 .attr("x2", function(d) { return d.target.x; })
                 .attr("y2", function(d) { return d.target.y; });
                 
                 
                 node.attr("transform", function(d) { return "translate(" + Math.max(r, Math.min(width - r, d.x)) + "," + Math.max(r, Math.min(height - r, d.y)) + ")"; });
                 });
        });