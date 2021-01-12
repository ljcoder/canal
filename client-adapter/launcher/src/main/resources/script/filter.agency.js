/**
 * 基于机构过滤
 * @type {{insert: AgencyFilter.insert, update: AgencyFilter.update, execute: (function(*): *)}}
 */
var AgencyFilter = {
    filter: function (messages){
        var result = new ArrayList();
        if(messages != null && messages.length > 0){
            for(var i = 0, length = messages.length; i < length ; i ++){
                var row = messages.get(i);
                var type = row['type'];
                var filterRow = null;
                switch (type) {
                    case "INSERT":
                        filterRow = AgencyFilter.insert(row);
                        break;
                    case "UPDATE":
                        filterRow = AgencyFilter.update(row);
                        break;
                    case "DELETE":
                        filterRow = row;
                        break;
                    default:
                        filterRow = row;
                        break;
                }
                if(filterRow != null){
                    result.add(filterRow);
                }
            }
        }
        return result;
    },
    insert: function (row){
        var currentData = listMapToMap(row.getData());
        var agencyCode = currentData[AGENCY_FIELD];
        if(agencyCode != null && agencyCode.startsWith(QINGQI_AGENCY)){
            return row;
        }
        return null;
    },
    update: function (row){
        var currentData = listMapToMap(row.getData());
        var oldData = listMapToMap(row.getOld());
        if(currentData[AGENCY_FIELD] != null){
            if(currentData[AGENCY_FIELD].startsWith(QINGQI_AGENCY)){
                if(oldData[AGENCY_FIELD] != null){
                    if(oldData[AGENCY_FIELD].startsWith(QINGQI_AGENCY)){
                        return row;
                    }else{
                        row['type'] = "INSERT";
                        row['old'] = null;
                        return row;
                    }
                }else{
                    return row;
                }
            }else{
                if(oldData[AGENCY_FIELD] != null && oldData[AGENCY_FIELD].startsWith(QINGQI_AGENCY)){
                    return row;
                }else{
                    return null;
                }
            }
        }else{
            return row;
        }
    }
}