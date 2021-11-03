class DeviceConfigCategory {
  final String name;
  final List<DeviceConfig> configs;

  DeviceConfigCategory(this.name, this.configs);
  DeviceConfigCategory.fromJson(Map<String, dynamic> json)
      : name = json["name"],
        configs = (json["configs"] as Iterable).map((e) => DeviceConfig.fromJson(e)).toList();
}

class DeviceConfig {
  final String name;
  final String value;

  DeviceConfig(this.name, this.value);

  DeviceConfig.fromJson(Map<String, dynamic> json)
      : name = json["name"],
        value = json["value"];
}
/*
[
{
  "name": "category A",
  "configs": [
    {"name": "setting A-1", "value": "a"},
    {"name": "setting A-2", "value": "b"}
  ]
},
{
  "name": "category B",
  "configs": [
    {"name": "setting B-1", "value": "a"},
    {"name": "setting B-2", "value": "b"}
  ]
}
]
* */
