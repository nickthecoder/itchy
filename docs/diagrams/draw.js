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

function connect( aId, bId, label, arrowFunc )
{
    var aEle = document.getElementById( aId );
    var bEle = document.getElementById( bId );

    var aPoint = findEdge( aEle.getBoundingClientRect(), bEle.getBoundingClientRect() );
    var bPoint = findEdge( bEle.getBoundingClientRect(), aEle.getBoundingClientRect() );
    
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

function findEdge( rectA, rectB )
{
    var ax = (rectA.left + rectA.right) / 2;
    var bx = (rectB.left + rectB.right) / 2;
    var ay = (rectA.top + rectA.bottom) / 2;
    var by = (rectB.top + rectB.bottom) / 2;
    
    var rise = by - ay;
    var tread = bx - ax
    var m = rise / tread;
    var c = by - (m * bx);

    var x = ax < bx ? rectA.right + 3 : rectA.left - 3;
    var y = m * x + c;
    if ( y < rectA.top || y > rectA.bottom ) {
        y = ay > by ? rectA.top - 3 : rectA.bottom + 3;
        x = (y - c) / m;
    }

    return { x: x, y : y };
}

