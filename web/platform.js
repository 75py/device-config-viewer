function getDeviceConfigs() {
    return JSON.stringify([
        toConfigObj("window", window),
        toConfigObj("location", location),
        {
            "name": "navigator",
            "configs": sortArray(Object.keys(Object.getPrototypeOf(navigator)).map(key => {
                return {
                    "name": key,
                    "value": String(navigator[key])
                }
            }))
        },
    ]);
}

function toConfigObj(name, obj) {
    return {
        "name": name,
        "configs": sortArray(Object.keys(obj).map(key => {
            return {
                "name": key,
                "value": String(obj[key])
            }
        }))
    }
}

function sortArray(arr) {
    arr.sort((a, b) => {
        return a.name > b.name ? 1 : -1
    })
    return arr
}
