var context=document.getElementById("canvas");
var ctx=context.getContext("2d");
ctx.font = "10pt Verdana";
ctx.strokeStyle="#444";
ctx.lineWidth=2;

var CP = window.CanvasRenderingContext2D && CanvasRenderingContext2D.prototype;
if (CP.lineTo) {
    CP.dashedLine = function(x, y, x2, y2, da) {
        if (!da) da = [10,5];
        this.save();
        var dx = (x2-x), dy = (y2-y);
        var len = Math.sqrt(dx*dx + dy*dy);
        var rot = Math.atan2(dy, dx);
        this.translate(x, y);
        this.moveTo(0, 0);
        this.rotate(rot);       
        var dc = da.length;
        var di = 0, draw = true;
        x = 0;
        while (len > x) {
            x += da[di++ % dc];
            if (x > len) x = len;
            draw ? this.lineTo(x, 0): this.moveTo(x, 0);
            draw = !draw;
        }       
        this.restore();
    }
}

function clear()
{
    canvas.width = canvas.width;
}

noEnd = function() {
}
noEnd.margin = 0;

arrow = function() {
    ctx.beginPath();
    ctx.moveTo( 10, -5 );
    ctx.lineTo( 0,0 );
    ctx.lineTo( 10, 5 );
    ctx.stroke();
};
arrow.margin = 8;

implementsEnd = function() {
    ctx.beginPath();
    ctx.moveTo( 16, -8 );
    ctx.lineTo( 0,0 );
    ctx.lineTo( 16, 8 );
    ctx.lineTo( 16, -8 );
    ctx.fillStyle="#fff";
    ctx.fill();
    ctx.stroke();
};
implementsEnd.margin = 1;
implementsEnd.dashed = [6,6];


extendsEnd = function() {
    ctx.beginPath();
    ctx.moveTo( 16, -8 );
    ctx.lineTo( 0,0 );
    ctx.lineTo( 16, 8 );
    ctx.lineTo( 16, -8 );
    ctx.fillStyle="#fff";
    ctx.fill();
    ctx.stroke();
};
extendsEnd.margin = 1;

aggregationEnd = function() {
    ctx.beginPath();
    ctx.lineTo( 0,0 );
    ctx.lineTo( 12, 8 );
    ctx.lineTo( 24, 0 );
    ctx.lineTo( 12, -8 );
    ctx.lineTo( 0, 0 );
    ctx.fillStyle="#fff";
    ctx.fill();
    ctx.stroke();
};
aggregationEnd.margin = 1;

compositionEnd = function() {
    ctx.beginPath();
    ctx.lineTo( 0,0 );
    ctx.lineTo( 12, 8 );
    ctx.lineTo( 24, 0 );
    ctx.lineTo( 12, -8 );
    ctx.lineTo( 0, 0 );
    ctx.fillStyle="#000";
    ctx.fill();
};
compositionEnd.margin = 1;

function connect2( aId, bId, label, reverseLabel, arrowFunc1, arrowFunc2 )
{
    if (!arrowFunc1) {
        arrowFunc1 = arrow;
    }
    if (!arrowFunc2) {
        arrowFunc2 = arrow;
    }
    connect( aId, bId, label, arrowFunc1, arrowFunc1.margin );
    connect( bId, aId, reverseLabel, arrowFunc2, arrowFunc2.margin );
}

function connect( aId, bId, label, arrowFunc, margin )
{
    if (!arrowFunc) {
        arrowFunc = arrow;
    }
    if (!margin) {
        margin = 0;
    }

    var aPoint = findEdge( "#" + aId, "#" + bId, margin );
    var bPoint = findEdge( "#" + bId, "#" + aId, arrowFunc.margin );

    var dx = aPoint.x - bPoint.x;
    var dy = aPoint.y - bPoint.y;
    var mag = Math.sqrt( dx * dx + dy * dy );
    var angle = Math.atan2( dy, dx );
    
    
    ctx.save();
    ctx.translate( bPoint.x, bPoint.y );
    ctx.beginPath();
    ctx.rotate( angle );
    if (arrowFunc.dashed) {
        ctx.dashedLine( 0,0, mag, 0, arrowFunc.dashed );
    } else {
        ctx.moveTo( 0,0 );
        ctx.lineTo( mag, 0 );
    }
    ctx.stroke();
    
    ctx.save();
    arrowFunc();
    ctx.restore();
    
    if ( Math.abs(angle) > Math.PI / 4 ) {
        ctx.rotate( Math.PI );
        ctx.fillText( label, -ctx.measureText(label).width - 20, 15 );
    } else {
        ctx.fillText( label, 20, -8 );
    }
    ctx.restore();
}

function findEdge( a, b, margin )
{
    if (!margin) {
        margin = 0;
    }
    
    var rectA = { left: $(a).position().left, right: $(a).position().left + $(a).width(), top: $(a).position().top, bottom: $(a).position().top + $(a).height() };
    var rectB = { left: $(b).position().left, right: $(b).position().left + $(b).width(), top: $(b).position().top, bottom: $(b).position().top + $(b).height() };
    
    var ax = (rectA.left + rectA.right) / 2;
    var bx = (rectB.left + rectB.right) / 2;
    var ay = (rectA.top + rectA.bottom) / 2;
    var by = (rectB.top + rectB.bottom) / 2;
    
    var rise = by - ay;
    var tread = bx - ax
    var m = rise / tread;
    var c = by - (m * bx);

    var x = ax < bx ? rectA.right + margin : rectA.left - margin;
    var y = m * x + c;
    if ( y < rectA.top || y > rectA.bottom ) {
        y = ay > by ? rectA.top - margin : rectA.bottom + margin;
        x = (y - c) / m;
    }

    return { x: x, y : y };
}

function dragged( box )
{
    var info = box.find( ".info" );
    
    if (info.size() == 0) {
        box.prepend( '<div class="info">Hi</div>' );
        info = box.find(".info" );
    }
    info.text( box.position().left + "," + box.position().top );
    
    drawConnections();
}

