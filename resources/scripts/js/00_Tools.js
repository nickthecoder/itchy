stdout=java.lang.System.out;
sdterr=java.lang.System.err;
random=new java.util.Random();

function dump(object,maxLevel,level)
{
    var result = "";
    if(!level) level = 0;
    if(!maxLevel) maxLevel = 0;

    var indent = '    ';
    var padding = "";
    for(var j=1;j<level+1;j++) padding += indent;

    if ((typeof(object) == 'object') || (typeof(object) == 'function')) {

        result += '{\n';

        if (typeof(object) == 'function') {
            result += indent;
            result += (padding + object).replace(/\n/g,"");
            result += '\n';
        }

        for (var item in object) {
            var val = object[item];
            result += indent + padding + item + ' = ';
            if ((typeof(val) == 'object') || (typeof(val) == 'function')) {
                if ( level >= maxLevel ) {
                    result += '... (' + typeof(val) + ')\n';
                } else {
                    var str = dump( val, maxLevel, level+1 );
                    result += str;
                }
            } else {
                result += val;
                result += '\n';
            }
        }
                
        result += padding + '}\n';

    } else {
        result += '' + object;
    }
    
    return result;
}



