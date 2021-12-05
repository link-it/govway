function fn(defaults, others) {
    var ret = others.slice();           // Clono l'array
    for (const el of defaults) {
        var found = false
        for(const cur of ret) {
            if(el.name === cur.name) {
                found = true;
                break;
            }
        }
        if (!found) {
            ret.push(el)
        }
    }
    return ret
}