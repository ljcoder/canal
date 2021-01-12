var ArrayList = Java.type('java.util.ArrayList');
var HashMap = Java.type('java.util.HashMap');

var QINGQI_AGENCY = "JG01";
var AGENCY_FIELD = "AGENCY_CODE";

var main = function (messages){
    var inNumber = messages == null ? 0 : messages.length;
    messages = AgencyFilter.filter(messages);
    var outNumber = messages == null ? 0 : messages.length;
    print("本次接收数据 "+inNumber+" 条, 过滤后返回数据 "+outNumber+" 条");
}

function listMapToMap(listMap) {
    var ret = new HashMap();
    if(listMap != null && listMap.length > 0) {
        for (var i = 0, length = listMap.length; i < length; i++) {
            var m = listMap.get(i);
            for (var k in m) {
                ret.put(k.toUpperCase(), m[k]);
            }
        }
    }
    return ret;
}