var context=document.getElementById("canvas");
var ctx=context.getContext("2d");
ctx.font = "10pt Verdana";
ctx.strokeStyle="#444";
ctx.lineWidth=2;

arrow = function() {
    ctx.beginPath();
    ctx.moveTo( 10, -5 );
    ctx.lineTo( 0,0 );
    ctx.lineTo( 10, 5 );
    ctx.stroke();
};

function connect2( aId, bId, label, reverseLabel, arrowFunc1, arrowFunc2 )
{
    connect( aId, bId, label, 8, arrowFunc1 );
    connect( bId, aId, reverseLabel, 8, arrowFunc2 );
}

function connect( aId, bId, label, margin, arrowFunc )
{
    if (!arrowFunc) {
        arrowFunc = arrow;
    }
    if (!margin) {
        margin = 0;
    }

    var aEle = document.getElementById( aId );
    var bEle = document.getElementById( bId );

    var aPoint = findEdge( aEle.getBoundingClientRect(), bEle.getBoundingClientRect(), margin );
    var bPoint = findEdge( bEle.getBoundingClientRect(), aEle.getBoundingClientRect(), 8 );
    
    var dx = aPoint.x - bPoint.x;
    var dy = aPoint.y - bPoint.y;
    var mag = Math.sqrt( dx * dx + dy * dy );
    var angle = Math.atan2( dy, dx );
    
    ctx.save();
    ctx.translate( bPoint.x, bPoint.y );
    ctx.beginPath();
    ctx.rotate( angle );
    ctx.moveTo( 0,0 );
    ctx.lineTo( mag, 0 );
    ctx.stroke();
    arrowFunc();
    if ( Math.abs(angle) > Math.PI / 4 ) {
        ctx.rotate( Math.PI );
        ctx.fillText( label, -ctx.measureText(label).width - 20, 15 );
    } else {
        ctx.fillText( label, 20, -8 );
    }
    ctx.restore();
}

function findEdge( rectA, rectB, margin )
{
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

