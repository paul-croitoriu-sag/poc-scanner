import 'dart:async';
import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter_datawedge/flutter_datawedge.dart';
import 'package:honeywell_scanner/honeywell_scanner.dart';
import 'package:logging/logging.dart';

enum ScannerType { zebra, honeywell, unknown }

class ScannerService {
  static final _logger = Logger('ScannerService');
  final _controller = StreamController<String>.broadcast();

  Stream<String> get scannedBarcodes => _controller.stream;

  StreamSubscription? _zebraSubscription;

  HoneywellScanner? _honeywell;
  ScannerType _type = ScannerType.unknown;

  Future<void> init() async {
    if (!Platform.isAndroid) {
      _logger.warning('ScannerService supports only Android.');
      return;
    }

    final manufacturer = await _getManufacturer();
    _logger.info('Detected manufacturer: $manufacturer');

    if (manufacturer.contains('zebra')) {
      _type = ScannerType.zebra;
      await _initializeZebra();
    } else if (manufacturer.contains('honeywell')) {
      _type = ScannerType.honeywell;
      await _initializeHoneywell();
    } else {
      _logger.warning('Unsupported device manufacturer: $manufacturer');
    }
  }

  Future<void> _initializeZebra() async {
    FlutterDataWedge dw = FlutterDataWedge();

    await dw.initialize();

    await dw.createDefaultProfile(profileName: 'Lodi');

    // Listen to scan results
    _zebraSubscription = dw.onScanResult.listen((event) {
      final data = event.data;
      _logger.info('Zebra scanned: $data');
      _controller.add(data);
    });
  }

  Future<void> _initializeHoneywell() async {
    _honeywell = HoneywellScanner(
      onScannerDecodeCallback: (ScannedData? data) {
        if (data != null && data.code?.isNotEmpty == true) {
          _logger.info('Honeywell scanned: ${data.code}');
          _controller.add(data.code!);
        }
      },
      onScannerErrorCallback: (error) {
        _logger.warning('Honeywell scanner error: $error');
      },
    );

    final supported = await _honeywell!.isSupported();

    if (!supported) {
      _logger.warning('Honeywell AIDC not supported on this device.');
      return;
    }

    List<CodeFormat> codeFormats = CodeFormatUtils.ALL_1D_FORMATS;
    Map<String, dynamic> properties = {...CodeFormatUtils.getAsPropertiesComplement(codeFormats), 'DEC_CODABAR_START_STOP_TRANSMIT': true};
    _honeywell?.setProperties(properties);

    await _honeywell!.startScanner();
  }

  Future<String> _getManufacturer() async {
    final deviceInfo = DeviceInfoPlugin();
    final androidInfo = await deviceInfo.androidInfo;
    return androidInfo.manufacturer.toLowerCase();
  }

  Future<void> dispose() async {
    await _controller.close();

    if (_type == ScannerType.honeywell) {
      await _honeywell?.disposeScanner();
    }

    await _zebraSubscription?.cancel();
  }
}
