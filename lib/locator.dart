import 'package:get_it/get_it.dart';
import 'package:scanner_poc/scanner_service.dart';

final GetIt locator = GetIt.instance;

Future<void> setupLocator() async {
  locator.registerSingletonAsync<ScannerService>(() async {
    final service = ScannerService();
    await service.init();
    return service;
  }, dispose: (service) async => await service.dispose());

  await locator.allReady();
}
