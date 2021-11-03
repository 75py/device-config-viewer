import 'package:deviceconfigviewer/entity.dart';
import 'package:deviceconfigviewer/platform.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

void main() => runApp(ProviderScope(child: MaterialApp(home: RootWidget2())));

class RootWidget2 extends ConsumerWidget {
  @override
  Widget build(BuildContext context,
      T Function<T>(ProviderBase<Object, T> provider) watch) {
    AsyncValue<List<DeviceConfigCategory>> categories =
    watch(MyPlatform.deviceConfigsState);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Device Config Viewer'),
        actions: [IconButton(icon: Icon(Icons.search), onPressed: () {})],
      ),
      body: categories.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, stackTrace) =>
              Center(child: Text(error.toString())),
          data: (value) {
            return RefreshIndicator(
              onRefresh: () async {
                await context.refresh(MyPlatform.deviceConfigsState);
              },

              child: TabPage2(categories: value,),
            );
          }),
    );
  }
}

class TabPage2 extends StatelessWidget {
  final List<DeviceConfigCategory> categories;

  const TabPage2({Key? key, required this.categories}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: categories.length,
      child: Scaffold(
        appBar: AppBar(
          toolbarHeight: 48,
          flexibleSpace: SafeArea(
            child: TabBar(
              isScrollable: true,
              tabs: categories.map((e) => Tab(text: e.name,)).toList(),
            ),
          ),
        ),
        body: TabBarView(
          children: categories.map((e) => ConfigListWidget2(e.configs))
              .toList(),
        ),
      ),
    );
  }
}

class ConfigListWidget2 extends StatelessWidget {

  final List<DeviceConfig> configs;

  const ConfigListWidget2(this.configs, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Scrollbar(
        child:  ListView.builder(
        itemBuilder: (BuildContext context, int index) {
          return Container(
              decoration: const BoxDecoration(
                border: Border(
                  bottom: BorderSide(color: Colors.black38),
                ),
              ),
              child: ListTile(
                title: Text(configs[index].name),
                subtitle: Text(configs[index].value),
                onTap: () {
                  /* react to the tile being tapped */
                },
              ));
        },
        itemCount: configs.length,
      ),
      )
    );
  }
}
