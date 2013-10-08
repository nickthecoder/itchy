Util = Class({

    Class : {
        // Creates a string representation of an object, showing all of its attribtes.
        // object       (Object)  : The object to dump
        // maxLevel     (int)     : The number of levels of recursion
        // includeProto (boolean) : If false the object's attributes available via its __proto__ object are ignored.
        // level        (int)     : Internal use only - the current recursion depth.
        dump: function(object, maxLevel, includeProto, level)
        {
            var result = "";
            if(!maxLevel) maxLevel = 0;
            if(!level) level = 0;

            var indent = '    ';
            var padding = "";
            for(var j=1;j<level+1;j++) padding += indent;

            if ((typeof(object) == 'object') || (typeof(object) == 'function')) {

                var names = new Array();
                for (var item in object) {
                    names[names.length] = item;
                }
                names.sort();

                for (var i = 0; i < names.length; i ++ ) {
                    var item = names[i];
                    var val = object[item];
                    var isProto = (object.__proto__) && (object.__proto__[item] === val);
                    
                    if (includeProto || !isProto) {
                        if ( val && (val.__noDump__) ) continue;
                        
                        result += indent + padding + item;

                        if ( isProto ) {
                            result += '\\proto\\';
                        }
                        result += ' = ';

                        if ((typeof(val) == 'object') || (typeof(val) == 'function')) {
                            if ( level >= maxLevel ) {
                                result += '... (' + typeof(val) + ')\n';
                            } else {
                                var str = Util.dump( val, maxLevel, includeProto, level+1 );
                                result += str;
                            }
                        } else {
                            result += val;
                            result += '\n';
                        }
                    }
                }

                if (typeof(object) == 'function') {
                    if (result == '') {
                        result += Util.compactFunction("" + object) + '\n';
                    } else {
                        result = '{\n' + padding + indent + Util.compactFunction("" + object) + '\n' + result + padding + '}\n';
                    }
                } else {

                    result = '{\n' + result + padding + '}\n';
                }

            } else {
                result += '' + object;
            }

            return result;
        },
        
        // Formats a function onto one line, with whitespace compressed. For human consuption only.
        compactFunction: function(str) {
            return str.replace(/\n/g,"").replace(/  */g, " ");
        },

        truncateLines: function(str, columns)
        {
            var result = "";
            
            var lines = str.split('\n' );
            for (var i = 0; i < lines.length; i ++) {
                line = lines[i];
                if ( line.length > columns ) {
                    result += line.substring(0, columns - 3) + '...\n'
                } else {
                    result += line + '\n';
                }
            }
            return result;
        }
    }

});

                    
